/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.Statement;

public final class Return
extends Statement<Return> {
    private Expression<?> expression;

    public Return(Expression<?> expression) {
        this.expression = this.child(expression);
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitReturn(this, p);
    }

    public Return() {
    }

    public Expression<?> getExpression() {
        return this.expression;
    }
}

