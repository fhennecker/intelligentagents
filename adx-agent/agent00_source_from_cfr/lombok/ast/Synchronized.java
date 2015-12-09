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

public class Synchronized
extends Statement<Synchronized> {
    private final Expression<?> lock;
    private final List<Statement<?>> statements = new ArrayList();

    public Synchronized(Expression<?> lock) {
        this.lock = this.child(lock);
    }

    public Synchronized withStatement(Statement<?> statement) {
        this.statements.add(this.child(statement));
        return this;
    }

    public Synchronized withStatements(List<Statement<?>> statements) {
        for (Statement statement : statements) {
            this.withStatement(statement);
        }
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitSynchronized(this, p);
    }

    public Expression<?> getLock() {
        return this.lock;
    }

    public List<Statement<?>> getStatements() {
        return this.statements;
    }
}

