/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.TypeRef;

public class Wildcard
extends TypeRef {
    private final TypeRef type;
    private final Bound bound;

    public Wildcard() {
        this(null, null);
    }

    public Wildcard(Bound bound, TypeRef type) {
        super((String)null);
        this.type = type;
        this.bound = bound;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitWildcard(this, p);
    }

    public TypeRef getType() {
        return this.type;
    }

    public Bound getBound() {
        return this.bound;
    }

    public static enum Bound {
        EXTENDS,
        SUPER;
        

        private Bound() {
        }
    }

}

