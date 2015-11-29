/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.ast.Annotation;
import lombok.ast.Argument;
import lombok.ast.JavaDoc;
import lombok.ast.Modifier;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;

public abstract class AbstractMethodDecl<SELF_TYPE extends AbstractMethodDecl<SELF_TYPE>>
extends Node<SELF_TYPE> {
    protected final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
    protected final List<Annotation> annotations = new ArrayList<Annotation>();
    protected final List<TypeParam> typeParameters = new ArrayList<TypeParam>();
    protected final List<Argument> arguments = new ArrayList<Argument>();
    protected final List<TypeRef> thrownExceptions = new ArrayList<TypeRef>();
    protected final List<Statement<?>> statements = new ArrayList();
    protected final String name;
    protected JavaDoc javaDoc;

    public SELF_TYPE makePrivate() {
        return this.withModifier(Modifier.PRIVATE);
    }

    public SELF_TYPE makeProtected() {
        return this.withModifier(Modifier.PROTECTED);
    }

    public SELF_TYPE makePublic() {
        return this.withModifier(Modifier.PUBLIC);
    }

    public SELF_TYPE makeStatic() {
        return this.withModifier(Modifier.STATIC);
    }

    public SELF_TYPE withAccessLevel(AccessLevel level) {
        switch (level) {
            case PUBLIC: {
                return this.makePublic();
            }
            case PROTECTED: {
                return this.makeProtected();
            }
            case PRIVATE: {
                return this.makePrivate();
            }
        }
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withModifier(Modifier modifier) {
        this.modifiers.add(modifier);
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withAnnotation(Annotation annotation) {
        this.annotations.add(this.child(annotation));
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withAnnotations(List<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            this.withAnnotation(annotation);
        }
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withArgument(Argument argument) {
        this.arguments.add(this.child(argument));
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withArguments(List<Argument> arguments) {
        for (Argument argument : arguments) {
            this.withArgument(argument);
        }
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withStatement(Statement<?> statement) {
        this.statements.add(this.child(statement));
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withStatements(List<Statement<?>> statements) {
        for (Statement statement : statements) {
            this.withStatement(statement);
        }
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withThrownException(TypeRef thrownException) {
        this.thrownExceptions.add(this.child(thrownException));
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withThrownExceptions(List<TypeRef> thrownExceptions) {
        for (TypeRef thrownException : thrownExceptions) {
            this.withThrownException(thrownException);
        }
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withTypeParameter(TypeParam typeParameter) {
        this.typeParameters.add(this.child(typeParameter));
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withTypeParameters(List<TypeParam> typeParameters) {
        for (TypeParam typeParameter : typeParameters) {
            this.withTypeParameter(typeParameter);
        }
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    public SELF_TYPE withJavaDoc(JavaDoc javaDoc) {
        this.javaDoc = this.child(javaDoc);
        return (SELF_TYPE)((AbstractMethodDecl)this.self());
    }

    @ConstructorProperties(value={"name"})
    public AbstractMethodDecl(String name) {
        this.name = name;
    }

    public EnumSet<Modifier> getModifiers() {
        return this.modifiers;
    }

    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    public List<TypeParam> getTypeParameters() {
        return this.typeParameters;
    }

    public List<Argument> getArguments() {
        return this.arguments;
    }

    public List<TypeRef> getThrownExceptions() {
        return this.thrownExceptions;
    }

    public List<Statement<?>> getStatements() {
        return this.statements;
    }

    public String getName() {
        return this.name;
    }

    public JavaDoc getJavaDoc() {
        return this.javaDoc;
    }

}

