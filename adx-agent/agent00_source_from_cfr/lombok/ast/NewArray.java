/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public final class NewArray
extends Expression<NewArray> {
    private final List<Expression<?>> dimensionExpressions = new ArrayList();
    private final List<Expression<?>> initializerExpressions = new ArrayList();
    private final TypeRef type;
    private final int dimensions;

    public NewArray(TypeRef type, int dimensions) {
        this.type = this.child(type);
        this.dimensions = dimensions;
    }

    public NewArray(TypeRef type) {
        this(type, 1);
    }

    public NewArray withDimensionExpression(Expression<?> dimensionExpression) {
        this.dimensionExpressions.add(this.child(dimensionExpression));
        return this;
    }

    public NewArray withInitializerExpression(Expression<?> initializerExpression) {
        this.initializerExpressions.add(this.child(initializerExpression));
        return this;
    }

    public NewArray withInitializerExpressions(List<Expression<?>> initializerExpressions) {
        for (Expression initializerExpression : initializerExpressions) {
            this.withInitializerExpression(initializerExpression);
        }
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitNewArray(this, p);
    }

    public List<Expression<?>> getDimensionExpressions() {
        return this.dimensionExpressions;
    }

    public List<Expression<?>> getInitializerExpressions() {
        return this.initializerExpressions;
    }

    public TypeRef getType() {
        return this.type;
    }

    public int getDimensions() {
        return this.dimensions;
    }
}

