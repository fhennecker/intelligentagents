/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 */
package lombok.javac.handlers.replace;

import com.sun.tools.javac.tree.JCTree;
import lombok.ast.Statement;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.replace.ExpressionReplaceVisitor;

public class ThisReferenceReplaceVisitor
extends ExpressionReplaceVisitor {
    public ThisReferenceReplaceVisitor(JavacMethod method, Statement<?> replacement) {
        super(method, replacement);
    }

    @Override
    protected boolean needsReplacing(JCTree.JCExpression node) {
        return node instanceof JCTree.JCIdent && "this".equals(node.toString());
    }
}

