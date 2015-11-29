/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;

public final class Unary
extends Expression<Unary> {
    private final String operator;
    private final Expression<?> expression;

    public Unary(String operator, Expression<?> expression) {
        this.operator = operator;
        this.expression = this.child(expression);
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitUnary(this, p);
    }

    public String getOperator() {
        return this.operator;
    }

    public Expression<?> getExpression() {
        return this.expression;
    }
}

