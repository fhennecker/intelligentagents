/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.AbstractMethodDecl;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public class MethodDecl
extends AbstractMethodDecl<MethodDecl> {
    private TypeRef returnType;
    private boolean implementing;
    private boolean noBody;

    public MethodDecl(TypeRef returnType, String name) {
        super(name);
        this.returnType = this.child(returnType);
    }

    public MethodDecl withReturnType(TypeRef returnType) {
        this.returnType = this.child(returnType);
        return (MethodDecl)this.self();
    }

    public MethodDecl withNoBody() {
        this.noBody = true;
        return (MethodDecl)this.self();
    }

    public boolean noBody() {
        return this.noBody;
    }

    public MethodDecl implementing() {
        this.implementing = true;
        return (MethodDecl)this.self();
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitMethodDecl(this, p);
    }

    public TypeRef getReturnType() {
        return this.returnType;
    }

    public boolean isImplementing() {
        return this.implementing;
    }
}

