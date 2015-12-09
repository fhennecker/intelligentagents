/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;

public class ArrayRef
extends Expression<ArrayRef> {
    private final Expression<?> indexed;
    private final Expression<?> index;

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitArrayRef(this, p);
    }

    @ConstructorProperties(value={"indexed", "index"})
    public ArrayRef(Expression<?> indexed, Expression<?> index) {
        this.indexed = indexed;
        this.index = index;
    }

    public Expression<?> getIndexed() {
        return this.indexed;
    }

    public Expression<?> getIndex() {
        return this.index;
    }
}

