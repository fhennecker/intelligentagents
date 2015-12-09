/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public final class This
extends Expression<This> {
    private TypeRef type;
    private boolean implicit;

    This(TypeRef type) {
        this.type = this.child(type);
    }

    public This implicit() {
        this.implicit = true;
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitThis(this, p);
    }

    public This() {
    }

    public TypeRef getType() {
        return this.type;
    }

    public boolean isImplicit() {
        return this.implicit;
    }
}

