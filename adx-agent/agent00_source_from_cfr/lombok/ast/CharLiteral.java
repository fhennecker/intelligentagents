/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;

public final class CharLiteral
extends Expression<CharLiteral> {
    private final String character;

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitCharLiteral(this, p);
    }

    @ConstructorProperties(value={"character"})
    public CharLiteral(String character) {
        this.character = character;
    }

    public String getCharacter() {
        return this.character;
    }
}

