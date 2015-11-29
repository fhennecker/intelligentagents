/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.ASTVisitor;
import lombok.ast.AbstractVariableDecl;
import lombok.ast.Expression;
import lombok.ast.JavaDoc;
import lombok.ast.Modifier;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public final class FieldDecl
extends AbstractVariableDecl<FieldDecl> {
    private Expression<?> initialization;

    public FieldDecl(TypeRef type, String name) {
        super(type, name);
    }

    public TypeRef getType() {
        return this.type;
    }

    public JavaDoc getJavaDoc() {
        return this.javaDoc;
    }

    public FieldDecl makePublic() {
        return (FieldDecl)this.withModifier(Modifier.PUBLIC);
    }

    public FieldDecl makePrivate() {
        return (FieldDecl)this.withModifier(Modifier.PRIVATE);
    }

    public FieldDecl makeStatic() {
        return (FieldDecl)this.withModifier(Modifier.STATIC);
    }

    public FieldDecl makeVolatile() {
        return (FieldDecl)this.withModifier(Modifier.VOLATILE);
    }

    public FieldDecl makeTransient() {
        return (FieldDecl)this.withModifier(Modifier.TRANSIENT);
    }

    public FieldDecl withInitialization(Expression<?> initialization) {
        this.initialization = this.child(initialization);
        return this;
    }

    public FieldDecl withJavaDoc(JavaDoc javaDoc) {
        this.javaDoc = this.child(javaDoc);
        return (FieldDecl)this.self();
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitFieldDecl(this, p);
    }

    public Expression<?> getInitialization() {
        return this.initialization;
    }
}

