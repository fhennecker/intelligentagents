/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.TypeRef;

public class DefaultValue
extends Expression<DefaultValue> {
    private final TypeRef type;

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitDefaultValue(this, p);
    }

    @ConstructorProperties(value={"type"})
    public DefaultValue(TypeRef type) {
        this.type = type;
    }

    public TypeRef getType() {
        return this.type;
    }
}

