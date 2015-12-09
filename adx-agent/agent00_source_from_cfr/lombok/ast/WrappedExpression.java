/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;

public class WrappedExpression
extends Expression<WrappedExpression> {
    private final Object wrappedObject;

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitWrappedExpression(this, p);
    }

    @ConstructorProperties(value={"wrappedObject"})
    public WrappedExpression(Object wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    public Object getWrappedObject() {
        return this.wrappedObject;
    }
}

