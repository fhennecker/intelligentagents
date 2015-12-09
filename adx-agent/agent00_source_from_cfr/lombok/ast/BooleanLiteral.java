/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;

public final class BooleanLiteral
extends Expression<BooleanLiteral> {
    private final boolean isTrue;

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitBooleanLiteral(this, p);
    }

    @ConstructorProperties(value={"isTrue"})
    public BooleanLiteral(boolean isTrue) {
        this.isTrue = isTrue;
    }

    public boolean isTrue() {
        return this.isTrue;
    }
}

