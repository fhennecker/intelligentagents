/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Statement;

public class Continue
extends Statement<Continue> {
    private String label;

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitContinue(this, p);
    }

    public Continue() {
    }

    @ConstructorProperties(value={"label"})
    public Continue(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}

