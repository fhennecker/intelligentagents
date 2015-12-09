/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.List;
import java.util.regex.Pattern;
import lombok.ast.Annotation;
import lombok.ast.Expression;
import lombok.ast.IFieldEditor;
import lombok.ast.TypeRef;
import lombok.core.LombokNode;

public interface IField<LOMBOK_NODE_TYPE extends LombokNode<?, ?, ?>, AST_BASE_TYPE, AST_VARIABLE_DECL_TYPE> {
    public IFieldEditor<AST_BASE_TYPE> editor();

    public boolean isPrivate();

    public boolean isFinal();

    public boolean isStatic();

    public boolean isInitialized();

    public boolean isPrimitive();

    public boolean hasJavaDoc();

    public AST_VARIABLE_DECL_TYPE get();

    public LOMBOK_NODE_TYPE node();

    public TypeRef type();

    public TypeRef boxedType();

    public boolean isOfType(String var1);

    public String name();

    public Expression<?> initialization();

    public List<TypeRef> typeArguments();

    public List<Annotation> annotations();

    public List<Annotation> annotations(Pattern var1);
}

