/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCCatch
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCThrow
 *  com.sun.tools.javac.tree.JCTree$JCTry
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import lombok.SneakyThrows;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleSneakyThrows
extends JavacAnnotationHandler<SneakyThrows> {
    @Override
    public void handle(AnnotationValues<SneakyThrows> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, SneakyThrows.class);
        Collection exceptionNames = annotation.getRawExpressions("value");
        if (exceptionNames.isEmpty()) {
            exceptionNames = Collections.singleton("java.lang.Throwable");
        }
        ArrayList<String> exceptions = new ArrayList<String>();
        Iterator i$ = exceptionNames.iterator();
        while (i$.hasNext()) {
            String exception = (String)i$.next();
            if (exception.endsWith(".class")) {
                exception = exception.substring(0, exception.length() - 6);
            }
            exceptions.add(exception);
        }
        JavacNode owner = (JavacNode)annotationNode.up();
        switch (owner.getKind()) {
            case METHOD: {
                this.handleMethod(annotationNode, (JCTree.JCMethodDecl)owner.get(), exceptions);
                break;
            }
            default: {
                annotationNode.addError("@SneakyThrows is legal only on methods and constructors.");
            }
        }
    }

    private void handleMethod(JavacNode annotation, JCTree.JCMethodDecl method, Collection<String> exceptions) {
        JavacNode methodNode = (JavacNode)annotation.up();
        if ((method.mods.flags & 1024) != 0) {
            annotation.addError("@SneakyThrows can only be used on concrete methods.");
            return;
        }
        if (method.body == null) {
            return;
        }
        if (method.body.stats.isEmpty()) {
            return;
        }
        JCTree.JCStatement constructorCall = (JCTree.JCStatement)method.body.stats.get(0);
        boolean isConstructorCall = this.isConstructorCall(constructorCall);
        List contents = isConstructorCall ? method.body.stats.tail : method.body.stats;
        for (String exception : exceptions) {
            contents = List.of((Object)this.buildTryCatchBlock(methodNode, contents, exception, (JCTree)annotation.get()));
        }
        method.body.stats = isConstructorCall ? List.of((Object)constructorCall).appendList(contents) : contents;
        methodNode.rebuild();
    }

    private boolean isConstructorCall(JCTree.JCStatement supect) {
        if (!(supect instanceof JCTree.JCExpressionStatement)) {
            return false;
        }
        JCTree.JCExpression supectExpression = ((JCTree.JCExpressionStatement)supect).expr;
        if (!(supectExpression instanceof JCTree.JCMethodInvocation)) {
            return false;
        }
        String methodName = ((JCTree.JCMethodInvocation)supectExpression).meth.toString();
        return "super".equals(methodName) || "this".equals(methodName);
    }

    private JCTree.JCStatement buildTryCatchBlock(JavacNode node, List<JCTree.JCStatement> contents, String exception, JCTree source) {
        TreeMaker maker = node.getTreeMaker();
        JCTree.JCBlock tryBlock = JavacHandlerUtil.setGeneratedBy(maker.Block(0, contents), source);
        JCTree.JCExpression varType = JavacHandlerUtil.chainDots(node, exception.split("\\."));
        JCTree.JCVariableDecl catchParam = maker.VarDef(maker.Modifiers(16), node.toName("$ex"), varType, null);
        JCTree.JCExpression lombokLombokSneakyThrowNameRef = JavacHandlerUtil.chainDots(node, "lombok", "Lombok", "sneakyThrow");
        JCTree.JCBlock catchBody = maker.Block(0, List.of((Object)maker.Throw((JCTree)maker.Apply(List.nil(), lombokLombokSneakyThrowNameRef, List.of((Object)maker.Ident(node.toName("$ex")))))));
        return (JCTree.JCStatement)JavacHandlerUtil.setGeneratedBy(maker.Try(tryBlock, List.of((Object)JavacHandlerUtil.recursiveSetGeneratedBy(maker.Catch(catchParam, catchBody), source)), null), source);
    }

}

