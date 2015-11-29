/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.List;
import lombok.ast.Annotation;
import lombok.ast.IField;
import lombok.ast.IMethod;
import lombok.ast.ITypeEditor;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;

public interface IType<METHOD_TYPE extends IMethod<?, ?, ?, ?>, FIELD_TYPE extends IField<?, ?, ?>, LOMBOK_NODE_TYPE extends LombokNode<?, ?, ?>, AST_BASE_TYPE, AST_TYPE_DECL_TYPE, AST_METHOD_DECL_TYPE> {
    public ITypeEditor<METHOD_TYPE, AST_BASE_TYPE, AST_TYPE_DECL_TYPE, AST_METHOD_DECL_TYPE> editor();

    public boolean isInterface();

    public boolean isEnum();

    public boolean isAnnotation();

    public boolean isClass();

    public boolean hasSuperClass();

    public <T extends IType<?, ?, ?, ?, ?, ?>> T memberType(String var1);

    public <T extends IType<?, ?, ?, ?, ?, ?>> T surroundingType();

    public List<METHOD_TYPE> methods();

    public List<FIELD_TYPE> fields();

    public boolean hasMultiArgumentConstructor();

    public AST_TYPE_DECL_TYPE get();

    public LOMBOK_NODE_TYPE node();

    public <A extends java.lang.annotation.Annotation> AnnotationValues<A> getAnnotationValue(Class<A> var1);

    public LOMBOK_NODE_TYPE getAnnotation(Class<? extends java.lang.annotation.Annotation> var1);

    public LOMBOK_NODE_TYPE getAnnotation(String var1);

    public String name();

    public List<TypeRef> typeArguments();

    public List<TypeParam> typeParameters();

    public List<Annotation> annotations();

    public boolean hasField(String var1);

    public /* varargs */ boolean hasMethod(String var1, TypeRef ... var2);

    public /* varargs */ boolean hasMethodIncludingSupertypes(String var1, TypeRef ... var2);
}

