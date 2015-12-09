/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCEnhancedForLoop
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCForLoop
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCNewArray
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import java.util.Map;
import lombok.javac.JavacAST;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacNode;
import lombok.javac.JavacResolution;
import lombok.javac.ResolutionBased;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.val;

@ResolutionBased
public class HandleVal
extends JavacASTAdapter {
    @Override
    public void visitLocal(JavacNode localNode, JCTree.JCVariableDecl local) {
        if (local.vartype == null || !local.vartype.toString().equals("val") && !local.vartype.toString().equals("lombok.val")) {
            return;
        }
        JCTree.JCExpression source = local.vartype;
        if (!JavacHandlerUtil.typeMatches(val.class, localNode, (JCTree)local.vartype)) {
            return;
        }
        JCTree parentRaw = (JCTree)((JavacNode)localNode.directUp()).get();
        if (parentRaw instanceof JCTree.JCForLoop) {
            localNode.addError("'val' is not allowed in old-style for loops");
            return;
        }
        JCTree.JCExpression rhsOfEnhancedForLoop = null;
        if (local.init == null && parentRaw instanceof JCTree.JCEnhancedForLoop) {
            JCTree.JCEnhancedForLoop efl = (JCTree.JCEnhancedForLoop)parentRaw;
            if (efl.var == local) {
                rhsOfEnhancedForLoop = efl.expr;
            }
        }
        if (rhsOfEnhancedForLoop == null && local.init == null) {
            localNode.addError("'val' on a local variable requires an initializer expression");
            return;
        }
        if (local.init instanceof JCTree.JCNewArray && ((JCTree.JCNewArray)local.init).elemtype == null) {
            localNode.addError("'val' is not compatible with array initializer expressions. Use the full form (new int[] { ... } instead of just { ... })");
            return;
        }
        if (localNode.shouldDeleteLombokAnnotations()) {
            JavacHandlerUtil.deleteImportFromCompilationUnit(localNode, "lombok.val");
        }
        local.mods.flags |= 16;
        if (!localNode.shouldDeleteLombokAnnotations()) {
            JCTree.JCAnnotation valAnnotation = JavacHandlerUtil.recursiveSetGeneratedBy(localNode.getTreeMaker().Annotation((JCTree)local.vartype, List.nil()), (JCTree)source);
            local.mods.annotations = local.mods.annotations == null ? List.of((Object)valAnnotation) : local.mods.annotations.append((Object)valAnnotation);
        }
        local.vartype = JavacResolution.createJavaLangObject((JavacAST)localNode.getAst());
        try {
            JavacResolution resolver;
            Type type;
            if (rhsOfEnhancedForLoop == null) {
                if (local.init.type == null) {
                    resolver = new JavacResolution(localNode.getContext());
                    type = ((JCTree.JCExpression)resolver.resolveMethodMember((JavacNode)localNode).get((Object)local.init)).type;
                } else {
                    type = local.init.type;
                }
            } else if (rhsOfEnhancedForLoop.type == null) {
                resolver = new JavacResolution(localNode.getContext());
                type = ((JCTree.JCExpression)resolver.resolveMethodMember((JavacNode)((JavacNode)localNode.directUp())).get((Object)rhsOfEnhancedForLoop)).type;
            } else {
                type = rhsOfEnhancedForLoop.type;
            }
            try {
                Type componentType;
                JCTree.JCExpression replacement = rhsOfEnhancedForLoop != null ? ((componentType = JavacResolution.ifTypeIsIterableToComponent(type, (JavacAST)localNode.getAst())) == null ? JavacResolution.createJavaLangObject((JavacAST)localNode.getAst()) : JavacResolution.typeToJCTree(componentType, (JavacAST)localNode.getAst(), false)) : JavacResolution.typeToJCTree(type, (JavacAST)localNode.getAst(), false);
                local.vartype = replacement != null ? replacement : JavacResolution.createJavaLangObject((JavacAST)localNode.getAst());
                ((JavacAST)localNode.getAst()).setChanged();
            }
            catch (JavacResolution.TypeNotConvertibleException e) {
                localNode.addError("Cannot use 'val' here because initializer expression does not have a representable type: " + e.getMessage());
                local.vartype = JavacResolution.createJavaLangObject((JavacAST)localNode.getAst());
            }
        }
        catch (RuntimeException e) {
            local.vartype = JavacResolution.createJavaLangObject((JavacAST)localNode.getAst());
            throw e;
        }
        finally {
            JavacHandlerUtil.recursiveSetGeneratedBy(local.vartype, (JCTree)source);
        }
    }
}

