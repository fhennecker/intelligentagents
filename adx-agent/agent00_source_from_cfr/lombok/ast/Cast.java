/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public class Cast
extends Expression<Cast> {
    private final TypeRef type;
    private final Expression<?> expression;

    public Cast(TypeRef type, Expression<?> expression) {
        this.type = this.child(type);
        this.expression = this.child(expression);
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitCast(this, p);
    }

    public TypeRef getType() {
        return this.type;
    }

    public Expression<?> getExpression() {
        return this.expression;
    }
}

