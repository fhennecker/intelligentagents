/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 */
package lombok.eclipse.handlers.replace;

import lombok.ast.Statement;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.replace.StatementReplaceVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;

public class ReturnStatementReplaceVisitor
extends StatementReplaceVisitor {
    public ReturnStatementReplaceVisitor(EclipseMethod method, Statement<?> replacement) {
        super(method, replacement);
    }

    @Override
    protected boolean needsReplacing(org.eclipse.jdt.internal.compiler.ast.Statement node) {
        return node instanceof ReturnStatement;
    }
}

