/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.LocalDecl;
import lombok.ast.Node;
import lombok.ast.Statement;

public final class Foreach
extends Statement<Foreach> {
    private final LocalDecl elementVariable;
    private Expression<?> collection;
    private Statement<?> action;

    public Foreach(LocalDecl elementVariable) {
        this.elementVariable = this.child(elementVariable);
    }

    public Foreach In(Expression<?> collection) {
        this.collection = this.child(collection);
        return this;
    }

    public Foreach Do(Statement<?> action) {
        this.action = this.child(action);
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitForeach(this, p);
    }

    public LocalDecl getElementVariable() {
        return this.elementVariable;
    }

    public Expression<?> getCollection() {
        return this.collection;
    }

    public Statement<?> getAction() {
        return this.action;
    }
}

