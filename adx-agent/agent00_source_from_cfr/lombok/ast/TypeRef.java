/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;

public class TypeRef
extends Expression<TypeRef> {
    private final List<TypeRef> typeArgs = new ArrayList<TypeRef>();
    private final String typeName;
    private boolean superType;
    private int dims;

    public TypeRef(Class<?> clazz) {
        this(clazz.getName());
    }

    public TypeRef makeSuperType() {
        this.superType = true;
        return this;
    }

    public TypeRef withDimensions(int dims) {
        this.dims = dims;
        return this;
    }

    public TypeRef withTypeArgument(TypeRef typeArg) {
        this.typeArgs.add(this.child(typeArg));
        return this;
    }

    public TypeRef withTypeArguments(List<TypeRef> typeArgs) {
        for (TypeRef typeArg : typeArgs) {
            this.withTypeArgument(typeArg);
        }
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitTypeRef(this, p);
    }

    @ConstructorProperties(value={"typeName"})
    public TypeRef(String typeName) {
        this.typeName = typeName;
    }

    public List<TypeRef> getTypeArgs() {
        return this.typeArgs;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public boolean isSuperType() {
        return this.superType;
    }

    public int getDims() {
        return this.dims;
    }
}

