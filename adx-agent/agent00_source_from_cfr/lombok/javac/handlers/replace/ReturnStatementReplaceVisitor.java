/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 */
package lombok.javac.handlers.replace;

import com.sun.tools.javac.tree.JCTree;
import lombok.ast.Statement;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.replace.StatementReplaceVisitor;

public class ReturnStatementReplaceVisitor
extends StatementReplaceVisitor {
    public ReturnStatementReplaceVisitor(JavacMethod method, Statement<?> replacement) {
        super(method, replacement);
    }

    @Override
    protected boolean needsReplacing(JCTree.JCStatement node) {
        return node instanceof JCTree.JCReturn;
    }
}

