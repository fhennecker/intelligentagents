/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import lombok.ast.ASTVisitor;
import lombok.ast.Statement;

public class Break
extends Statement<Break> {
    private String label;

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitBreak(this, p);
    }

    public Break() {
    }

    @ConstructorProperties(value={"label"})
    public Break(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}

