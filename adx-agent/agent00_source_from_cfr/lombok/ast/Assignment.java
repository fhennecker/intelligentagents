/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;

public final class Assignment
extends Expression<Assignment> {
    private final Expression<?> left;
    private final Expression<?> right;

    public Assignment(Expression<?> left, Expression<?> right) {
        this.left = this.child(left);
        this.right = this.child(right);
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitAssignment(this, p);
    }

    public Expression<?> getLeft() {
        return this.left;
    }

    public Expression<?> getRight() {
        return this.right;
    }
}

