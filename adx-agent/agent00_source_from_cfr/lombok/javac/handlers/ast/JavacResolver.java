/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.Context
 */
package lombok.javac.handlers.ast;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import java.util.Map;
import lombok.core.util.Is;
import lombok.javac.JavacNode;
import lombok.javac.JavacResolution;

public enum JavacResolver {
    CLASS{

        @Override
        public Type resolveMember(JavacNode node, JCTree.JCExpression expr) {
            Type type = expr.type;
            if (type == null) {
                try {
                    new JavacResolution(node.getContext()).resolveClassMember(node);
                    type = expr.type;
                }
                catch (Exception ignore) {
                    // empty catch block
                }
            }
            return type;
        }
    }
    ,
    METHOD{

        @Override
        public Type resolveMember(JavacNode node, JCTree.JCExpression expr) {
            Type type = expr.type;
            if (type == null) {
                try {
                    JCTree.JCExpression resolvedExpression = (JCTree.JCExpression)new JavacResolution(node.getContext()).resolveMethodMember(node).get((Object)expr);
                    if (resolvedExpression != null) {
                        type = resolvedExpression.type;
                    }
                }
                catch (Exception ignore) {
                    // empty catch block
                }
            }
            return type;
        }
    }
    ,
    CLASS_AND_METHOD{

        @Override
        public Type resolveMember(JavacNode node, JCTree.JCExpression expr) {
            Type type = METHOD.resolveMember(node, expr);
            if (type == null) {
                JavacNode classNode;
                for (classNode = node; classNode != null && Is.noneOf(classNode.get(), JCTree.JCBlock.class, JCTree.JCMethodDecl.class, JCTree.JCVariableDecl.class); classNode = (JavacNode)classNode.up()) {
                }
                if (classNode != null) {
                    type = CLASS.resolveMember(classNode, expr);
                }
            }
            return type;
        }
    };
    

    private JavacResolver() {
    }

    public abstract Type resolveMember(JavacNode var1, JCTree.JCExpression var2);

}

