/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.This;

public final class FieldRef
extends Expression<FieldRef> {
    private Expression<?> receiver;
    private final String name;

    public FieldRef(Expression<?> receiver, String name) {
        this.receiver = this.child(receiver);
        this.name = name;
    }

    public FieldRef(String name) {
        this(new This(), name);
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitFieldRef(this, p);
    }

    public Expression<?> getReceiver() {
        return this.receiver;
    }

    public String getName() {
        return this.name;
    }
}

