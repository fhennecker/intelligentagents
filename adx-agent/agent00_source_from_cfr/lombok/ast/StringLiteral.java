/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;

public final class StringLiteral
extends Expression<StringLiteral> {
    private final String string;

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitStringLiteral(this, p);
    }

    @ConstructorProperties(value={"string"})
    public StringLiteral(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }
}

