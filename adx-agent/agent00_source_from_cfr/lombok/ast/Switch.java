/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.Case;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.Statement;

public final class Switch
extends Statement<Switch> {
    private final List<Case> cases = new ArrayList<Case>();
    private final Expression<?> expression;

    public Switch(Expression<?> expression) {
        this.expression = this.child(expression);
    }

    public Switch withCase(Case caze) {
        this.cases.add(this.child(caze));
        return this;
    }

    public Switch withCases(List<Case> cases) {
        for (Case caze : cases) {
            this.withCase(caze);
        }
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitSwitch(this, p);
    }

    public List<Case> getCases() {
        return this.cases;
    }

    public Expression<?> getExpression() {
        return this.expression;
    }
}

