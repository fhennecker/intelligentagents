/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.Statement;

public final class While
extends Statement<While> {
    private final Expression<?> condition;
    private Statement<?> action;

    public While(Expression<?> condition) {
        this.condition = this.child(condition);
    }

    public While Do(Statement<?> action) {
        this.action = this.child(action);
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitWhile(this, p);
    }

    public Expression<?> getCondition() {
        return this.condition;
    }

    public Statement<?> getAction() {
        return this.action;
    }
}

