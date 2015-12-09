/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.Statement;

public final class Throw
extends Statement<Throw> {
    private final Expression<?> expression;

    public Throw(Expression<?> expression) {
        this.expression = this.child(expression);
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitThrow(this, p);
    }

    public Expression<?> getExpression() {
        return this.expression;
    }
}

