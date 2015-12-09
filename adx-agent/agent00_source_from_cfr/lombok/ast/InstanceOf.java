/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public final class InstanceOf
extends Expression<InstanceOf> {
    private final Expression<?> expression;
    private final TypeRef type;

    public InstanceOf(Expression<?> expression, TypeRef type) {
        this.expression = this.child(expression);
        this.type = this.child(type);
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitInstanceOf(this, p);
    }

    public Expression<?> getExpression() {
        return this.expression;
    }

    public TypeRef getType() {
        return this.type;
    }
}

