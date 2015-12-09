/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.Node;
import lombok.ast.Statement;

public class Block
extends Statement<Block> {
    private final List<Statement<?>> statements = new ArrayList();

    public Block withStatement(Statement<?> statement) {
        this.statements.add(this.child(statement));
        return this;
    }

    public Block withStatements(List<Statement<?>> statements) {
        for (Statement statement : statements) {
            this.withStatement(statement);
        }
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitBlock(this, p);
    }

    public List<Statement<?>> getStatements() {
        return this.statements;
    }
}

