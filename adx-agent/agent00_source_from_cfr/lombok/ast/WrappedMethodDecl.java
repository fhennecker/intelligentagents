/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.MethodDecl;
import lombok.ast.TypeRef;

public class WrappedMethodDecl
extends MethodDecl {
    private final Object wrappedObject;

    public WrappedMethodDecl(Object wrappedObject) {
        super(null, null);
        this.wrappedObject = wrappedObject;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitWrappedMethodDecl(this, p);
    }

    public Object getWrappedObject() {
        return this.wrappedObject;
    }
}

