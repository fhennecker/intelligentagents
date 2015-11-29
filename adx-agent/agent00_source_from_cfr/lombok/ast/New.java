/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.ClassDecl;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public final class New
extends Expression<New> {
    private final List<Expression<?>> args = new ArrayList();
    private final List<TypeRef> typeArgs = new ArrayList<TypeRef>();
    private final TypeRef type;
    private ClassDecl anonymousType;

    public New(TypeRef type) {
        this.type = this.child(type);
    }

    public New withArgument(Expression<?> arg) {
        this.args.add(this.child(arg));
        return this;
    }

    public New withTypeArgument(TypeRef typeArg) {
        this.typeArgs.add(this.child(typeArg));
        return this;
    }

    public New withTypeArguments(List<TypeRef> typeArgs) {
        for (TypeRef typeArg : typeArgs) {
            this.withTypeArgument(typeArg);
        }
        return this;
    }

    public New withTypeDeclaration(ClassDecl anonymousType) {
        this.anonymousType = this.child(anonymousType);
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitNew(this, p);
    }

    public List<Expression<?>> getArgs() {
        return this.args;
    }

    public List<TypeRef> getTypeArgs() {
        return this.typeArgs;
    }

    public TypeRef getType() {
        return this.type;
    }

    public ClassDecl getAnonymousType() {
        return this.anonymousType;
    }
}

