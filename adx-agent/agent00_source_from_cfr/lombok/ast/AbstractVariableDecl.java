/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import lombok.ast.Annotation;
import lombok.ast.JavaDoc;
import lombok.ast.Modifier;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.ast.TypeRef;

public abstract class AbstractVariableDecl<SELF_TYPE extends AbstractVariableDecl<SELF_TYPE>>
extends Statement<SELF_TYPE> {
    protected final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
    protected final List<Annotation> annotations = new ArrayList<Annotation>();
    protected final TypeRef type;
    protected final String name;
    protected JavaDoc javaDoc;

    public AbstractVariableDecl(TypeRef type, String name) {
        this.type = this.child(type);
        this.name = name;
    }

    public SELF_TYPE makeFinal() {
        return this.withModifier(Modifier.FINAL);
    }

    public SELF_TYPE withModifier(Modifier modifier) {
        this.modifiers.add(modifier);
        return (SELF_TYPE)((AbstractVariableDecl)this.self());
    }

    public SELF_TYPE withAnnotation(Annotation annotation) {
        this.annotations.add(this.child(annotation));
        return (SELF_TYPE)((AbstractVariableDecl)this.self());
    }

    public SELF_TYPE withAnnotations(List<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            this.withAnnotation(annotation);
        }
        return (SELF_TYPE)((AbstractVariableDecl)this.self());
    }

    public EnumSet<Modifier> getModifiers() {
        return this.modifiers;
    }

    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    public String getName() {
        return this.name;
    }
}

