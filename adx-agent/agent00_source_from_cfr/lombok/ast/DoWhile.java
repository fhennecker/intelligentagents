/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.BooleanLiteral;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.Statement;

public final class DoWhile
extends Statement<DoWhile> {
    private final Statement<?> action;
    private Expression<?> condition;

    public DoWhile(Statement<?> action) {
        this.action = this.child(action);
        this.condition = this.child(new BooleanLiteral(true));
    }

    public DoWhile While(Expression<?> condition) {
        this.condition = this.child(condition);
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitDoWhile(this, p);
    }

    public Statement<?> getAction() {
        return this.action;
    }

    public Expression<?> getCondition() {
        return this.condition;
    }
}

