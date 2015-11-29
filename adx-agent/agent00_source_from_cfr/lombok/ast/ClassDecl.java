/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.AbstractMethodDecl;
import lombok.ast.Annotation;
import lombok.ast.FieldDecl;
import lombok.ast.Modifier;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;

public class ClassDecl
extends Statement<ClassDecl> {
    protected final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
    private final List<Annotation> annotations = new ArrayList<Annotation>();
    private final List<TypeParam> typeParameters = new ArrayList<TypeParam>();
    private final List<FieldDecl> fields = new ArrayList<FieldDecl>();
    private final List<AbstractMethodDecl<?>> methods = new ArrayList();
    private final List<ClassDecl> memberTypes = new ArrayList<ClassDecl>();
    private final List<TypeRef> superInterfaces = new ArrayList<TypeRef>();
    private final String name;
    private TypeRef superclass;
    private boolean local;
    private boolean anonymous;
    private boolean isInterface;

    public ClassDecl(String name) {
        this.name = name;
    }

    public ClassDecl extending(TypeRef type) {
        this.superclass = type;
        return this;
    }

    public ClassDecl implementing(TypeRef type) {
        this.superInterfaces.add(this.child(type));
        return this;
    }

    public ClassDecl implementing(List<TypeRef> types) {
        for (TypeRef type : types) {
            this.implementing(type);
        }
        return this;
    }

    public ClassDecl makeLocal() {
        this.local = true;
        return this;
    }

    public ClassDecl makeAnonymous() {
        this.anonymous = true;
        return this;
    }

    public ClassDecl makeInterface() {
        this.isInterface = true;
        return this;
    }

    public ClassDecl makePrivate() {
        return this.withModifier(Modifier.PRIVATE);
    }

    public ClassDecl makeProtected() {
        return this.withModifier(Modifier.PROTECTED);
    }

    public ClassDecl makePublic() {
        return this.withModifier(Modifier.PUBLIC);
    }

    public ClassDecl makeStatic() {
        return this.withModifier(Modifier.STATIC);
    }

    public ClassDecl makeFinal() {
        return this.withModifier(Modifier.FINAL);
    }

    public ClassDecl withModifier(Modifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public ClassDecl withMethod(AbstractMethodDecl<?> method) {
        this.methods.add(this.child(method));
        return this;
    }

    public ClassDecl withMethods(List<AbstractMethodDecl<?>> methods) {
        for (AbstractMethodDecl method : methods) {
            this.withMethod(method);
        }
        return this;
    }

    public ClassDecl withField(FieldDecl field) {
        this.fields.add(this.child(field));
        return this;
    }

    public ClassDecl withFields(List<FieldDecl> fields) {
        for (FieldDecl field : fields) {
            this.withField(field);
        }
        return this;
    }

    public ClassDecl withType(ClassDecl type) {
        this.memberTypes.add(this.child(type));
        return this;
    }

    public ClassDecl withTypeParameter(TypeParam typeParameter) {
        this.typeParameters.add(this.child(typeParameter));
        return this;
    }

    public ClassDecl withTypeParameters(List<TypeParam> typeParameters) {
        for (TypeParam typeParameter : typeParameters) {
            this.withTypeParameter(typeParameter);
        }
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitClassDecl(this, p);
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

    public List<FieldDecl> getFields() {
        return this.fields;
    }

    public List<AbstractMethodDecl<?>> getMethods() {
        return this.methods;
    }

    public List<ClassDecl> getMemberTypes() {
        return this.memberTypes;
    }

    public List<TypeRef> getSuperInterfaces() {
        return this.superInterfaces;
    }

    public String getName() {
        return this.name;
    }

    public TypeRef getSuperclass() {
        return this.superclass;
    }

    public boolean isLocal() {
        return this.local;
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public boolean isInterface() {
        return this.isInterface;
    }
}

