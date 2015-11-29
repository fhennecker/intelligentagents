/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.Statement;

public class Case
extends Statement<Case> {
    private final List<Statement<?>> statements = new ArrayList();
    private Expression<?> pattern;

    public Case(Expression<?> pattern) {
        this.pattern = this.child(pattern);
    }

    public Case withPattern(Expression<?> pattern) {
        this.pattern = this.child(pattern);
        return this;
    }

    public Case withStatement(Statement<?> statement) {
        this.statements.add(this.child(statement));
        return this;
    }

    public Case withStatements(List<Statement<?>> statements) {
        for (Statement statement : statements) {
            this.withStatement(statement);
        }
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitCase(this, p);
    }

    public Case() {
    }

    public List<Statement<?>> getStatements() {
        return this.statements;
    }

    public Expression<?> getPattern() {
        return this.pattern;
    }
}

