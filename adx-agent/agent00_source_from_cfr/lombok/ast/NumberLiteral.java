/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;

public final class NumberLiteral
extends Expression<NumberLiteral> {
    private Number number;

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitNumberLiteral(this, p);
    }

    @ConstructorProperties(value={"number"})
    public NumberLiteral(Number number) {
        this.number = number;
    }

    public Number getNumber() {
        return this.number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }
}

