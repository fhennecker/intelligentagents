/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.HashMap;
import java.util.Map;
import lombok.ast.ASTVisitor;
import lombok.ast.Expression;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public class Annotation
extends Expression<Annotation> {
    private final Map<String, Expression<?>> values = new HashMap();
    private final TypeRef type;

    public Annotation(TypeRef type) {
        this.type = this.child(type);
    }

    public Annotation withValue(Expression<?> value) {
        return this.withValue("value", value);
    }

    public Annotation withValue(String name, Expression<?> value) {
        this.values.put(name, this.child(value));
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitAnnotation(this, p);
    }

    public Map<String, Expression<?>> getValues() {
        return this.values;
    }

    public TypeRef getType() {
        return this.type;
    }
}

