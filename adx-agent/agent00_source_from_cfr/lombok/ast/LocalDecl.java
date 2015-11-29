/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.AbstractVariableDecl;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public final class LocalDecl
extends AbstractVariableDecl<LocalDecl> {
    private Expression<?> initialization;

    public LocalDecl(TypeRef type, String name) {
        super(type, name);
    }

    public TypeRef getType() {
        return this.type;
    }

    public LocalDecl withInitialization(Expression<?> initialization) {
        this.initialization = this.child(initialization);
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitLocalDecl(this, p);
    }

    public Expression<?> getInitialization() {
        return this.initialization;
    }
}

