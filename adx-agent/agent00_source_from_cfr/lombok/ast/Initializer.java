/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.Modifier;
import lombok.ast.Node;
import lombok.ast.Statement;

public class Initializer
extends Statement<Initializer> {
    private final List<Statement<?>> statements = new ArrayList();
    protected final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

    public Initializer withStatement(Statement<?> statement) {
        this.statements.add(this.child(statement));
        return this;
    }

    public Initializer withStatements(List<Statement<?>> statements) {
        for (Statement statement : statements) {
            this.withStatement(statement);
        }
        return this;
    }

    public Initializer makeStatic() {
        this.modifiers.add(Modifier.STATIC);
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitInitializer(this, p);
    }

    public List<Statement<?>> getStatements() {
        return this.statements;
    }

    public EnumSet<Modifier> getModifiers() {
        return this.modifiers;
    }
}

