/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.Statement;

public final class If
extends Statement<If> {
    private final Expression<?> condition;
    private Statement<?> thenStatement;
    private Statement<?> elseStatement;

    public If(Expression<?> condition) {
        this.condition = this.child(condition);
    }

    public If Then(Statement<?> thenStatement) {
        this.thenStatement = this.child(thenStatement);
        return this;
    }

    public If Else(Statement<?> elseStatement) {
        this.elseStatement = this.child(elseStatement);
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitIf(this, p);
    }

    public Expression<?> getCondition() {
        return this.condition;
    }

    public Statement<?> getThenStatement() {
        return this.thenStatement;
    }

    public Statement<?> getElseStatement() {
        return this.elseStatement;
    }
}

