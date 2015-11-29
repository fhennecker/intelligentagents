/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.TypeTags
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCNewArray
 *  com.sun.tools.javac.tree.JCTree$JCSynchronized
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import lombok.Synchronized;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleSynchronized
extends JavacAnnotationHandler<Synchronized> {
    private static final String INSTANCE_LOCK_NAME = "$lock";
    private static final String STATIC_LOCK_NAME = "$LOCK";

    @Override
    public void handle(AnnotationValues<Synchronized> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
            return;
        }
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Synchronized.class);
        JavacNode methodNode = (JavacNode)annotationNode.up();
        if (methodNode == null || methodNode.getKind() != AST.Kind.METHOD || !(methodNode.get() instanceof JCTree.JCMethodDecl)) {
            annotationNode.addError("@Synchronized is legal only on methods.");
            return;
        }
        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl)methodNode.get();
        if ((method.mods.flags & 1024) != 0) {
            annotationNode.addError("@Synchronized is legal only on concrete methods.");
            return;
        }
        boolean isStatic = (method.mods.flags & 8) != 0;
        String lockName = annotation.getInstance().value();
        boolean autoMake = false;
        if (lockName.length() == 0) {
            autoMake = true;
            lockName = isStatic ? "$LOCK" : "$lock";
        }
        TreeMaker maker = methodNode.getTreeMaker().at(ast.pos);
        if (JavacHandlerUtil.fieldExists(lockName, methodNode) == JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            if (!autoMake) {
                annotationNode.addError("The field " + lockName + " does not exist.");
                return;
            }
            JCTree.JCExpression objectType = JavacHandlerUtil.chainDots(methodNode, ast.pos, "java", "lang", "Object");
            JCTree.JCNewArray newObjectArray = maker.NewArray(JavacHandlerUtil.chainDots(methodNode, ast.pos, "java", "lang", "Object"), List.of((Object)maker.Literal(Javac.getCtcInt(TypeTags.class, "INT"), (Object)0)), null);
            JCTree.JCVariableDecl fieldDecl = JavacHandlerUtil.recursiveSetGeneratedBy(maker.VarDef(maker.Modifiers((long)(18 | (isStatic ? 8 : 0))), methodNode.toName(lockName), objectType, (JCTree.JCExpression)newObjectArray), (JCTree)ast);
            JavacHandlerUtil.injectFieldSuppressWarnings((JavacNode)methodNode.up(), fieldDecl);
        }
        if (method.body == null) {
            return;
        }
        JCTree.JCFieldAccess lockNode = isStatic ? JavacHandlerUtil.chainDots(methodNode, ast.pos, ((JavacNode)methodNode.up()).getName(), lockName) : maker.Select((JCTree.JCExpression)maker.Ident(methodNode.toName("this")), methodNode.toName(lockName));
        JavacHandlerUtil.recursiveSetGeneratedBy(lockNode, (JCTree)ast);
        method.body = JavacHandlerUtil.setGeneratedBy(maker.Block(0, List.of((Object)JavacHandlerUtil.setGeneratedBy(maker.Synchronized((JCTree.JCExpression)lockNode, method.body), (JCTree)ast))), (JCTree)ast);
        methodNode.rebuild();
    }
}

