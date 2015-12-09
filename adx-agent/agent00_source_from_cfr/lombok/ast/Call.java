/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public class Call
extends Expression<Call> {
    private final List<Expression<?>> args = new ArrayList();
    private final List<TypeRef> typeArgs = new ArrayList<TypeRef>();
    private final Expression<?> receiver;
    private final String name;

    public Call(Expression<?> receiver, String name) {
        this.receiver = this.child(receiver);
        this.name = name;
    }

    public Call(String name) {
        this(null, name);
    }

    public Call withArgument(Expression<?> argument) {
        this.args.add(this.child(argument));
        return this;
    }

    public Call withArguments(List<Expression<?>> arguments) {
        for (Expression argument : arguments) {
            this.withArgument(argument);
        }
        return this;
    }

    public Call withTypeArgument(TypeRef typeArg) {
        this.typeArgs.add(this.child(typeArg));
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitCall(this, p);
    }

    public List<Expression<?>> getArgs() {
        return this.args;
    }

    public List<TypeRef> getTypeArgs() {
        return this.typeArgs;
    }

    public Expression<?> getReceiver() {
        return this.receiver;
    }

    public String getName() {
        return this.name;
    }
}

