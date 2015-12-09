/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.AbstractVariableDecl;
import lombok.ast.TypeRef;

public class Argument
extends AbstractVariableDecl<Argument> {
    public Argument(TypeRef type, String name) {
        super(type, name);
    }

    public TypeRef getType() {
        return this.type;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitArgument(this, p);
    }
}

