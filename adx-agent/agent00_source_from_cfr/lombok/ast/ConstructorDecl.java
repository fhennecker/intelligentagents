/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.AbstractMethodDecl;

public class ConstructorDecl
extends AbstractMethodDecl<ConstructorDecl> {
    private boolean implicitSuper;

    public ConstructorDecl(String name) {
        super(name);
    }

    public ConstructorDecl withImplicitSuper() {
        this.implicitSuper = true;
        return this;
    }

    public boolean implicitSuper() {
        return this.implicitSuper;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitConstructorDecl(this, p);
    }
}

