/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;

public final class NameRef
extends Expression<NameRef> {
    private final String name;

    public NameRef(Class<?> clazz) {
        this(clazz.getName());
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitNameRef(this, p);
    }

    @ConstructorProperties(value={"name"})
    public NameRef(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

