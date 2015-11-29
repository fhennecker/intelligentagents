/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;

public class Binary
extends Expression<Binary> {
    private final Expression<?> left;
    private final String operator;
    private final Expression<?> right;

    public Binary(Expression<?> left, String operator, Expression<?> right) {
        this.left = this.child(left);
        this.operator = operator;
        this.right = this.child(right);
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitBinary(this, p);
    }

    public Expression<?> getLeft() {
        return this.left;
    }

    public String getOperator() {
        return this.operator;
    }

    public Expression<?> getRight() {
        return this.right;
    }
}

