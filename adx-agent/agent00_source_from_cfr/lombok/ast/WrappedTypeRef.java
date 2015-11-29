/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.TypeRef;

public class WrappedTypeRef
extends TypeRef {
    private final Object wrappedObject;

    public WrappedTypeRef(Object wrappedObject) {
        super((String)null);
        this.wrappedObject = wrappedObject;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitWrappedTypeRef(this, p);
    }

    public Object getWrappedObject() {
        return this.wrappedObject;
    }
}

