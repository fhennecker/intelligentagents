/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 */
package lombok.eclipse.handlers.replace;

import lombok.ast.AST;
import lombok.ast.Statement;
import lombok.core.util.As;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.replace.ExpressionReplaceVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;

public class VariableNameReplaceVisitor
extends ExpressionReplaceVisitor {
    private final String oldName;

    public VariableNameReplaceVisitor(EclipseMethod method, String oldName, String newName) {
        super(method, AST.Name(newName));
        this.oldName = oldName;
    }

    @Override
    protected boolean needsReplacing(Expression node) {
        return node instanceof SingleNameReference && this.oldName.equals(As.string(((SingleNameReference)node).token));
    }
}

