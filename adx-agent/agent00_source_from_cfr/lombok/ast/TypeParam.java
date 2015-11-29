/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.ast.TypeRef;

public final class TypeParam
extends Statement<TypeParam> {
    protected final List<TypeRef> bounds = new ArrayList<TypeRef>();
    protected final String name;

    public TypeParam withBound(TypeRef bound) {
        this.bounds.add(this.child(bound));
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitTypeParam(this, p);
    }

    @ConstructorProperties(value={"name"})
    public TypeParam(String name) {
        this.name = name;
    }

    public List<TypeRef> getBounds() {
        return this.bounds;
    }

    public String getName() {
        return this.name;
    }
}

