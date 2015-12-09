/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Statement;

public class WrappedStatement
extends Statement<WrappedStatement> {
    private final Object wrappedObject;

    public WrappedStatement(Object wrappedObject) {
        this.wrappedObject = wrappedObject;
        this.posHint(wrappedObject);
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitWrappedStatement(this, p);
    }

    public Object getWrappedObject() {
        return this.wrappedObject;
    }
}

