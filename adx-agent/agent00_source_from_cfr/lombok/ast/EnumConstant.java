/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.AbstractVariableDecl;
import lombok.ast.Expression;
import lombok.ast.JavaDoc;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public final class EnumConstant
extends AbstractVariableDecl<EnumConstant> {
    private final List<Expression<?>> args = new ArrayList();

    public EnumConstant(String name) {
        super(null, name);
    }

    public JavaDoc getJavaDoc() {
        return this.javaDoc;
    }

    public EnumConstant withArgument(Expression<?> arg) {
        this.args.add(this.child(arg));
        return this;
    }

    public EnumConstant withJavaDoc(JavaDoc javaDoc) {
        this.javaDoc = this.child(javaDoc);
        return (EnumConstant)this.self();
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitEnumConstant(this, p);
    }

    public List<Expression<?>> getArgs() {
        return this.args;
    }
}

