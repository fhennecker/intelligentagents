/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.List;
import lombok.AccessLevel;
import lombok.ast.Annotation;
import lombok.ast.Argument;
import lombok.ast.IMethodEditor;
import lombok.ast.IType;
import lombok.ast.Statement;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;
import lombok.core.LombokNode;

public interface IMethod<TYPE_TYPE extends IType<?, ?, ?, ?, ?, ?>, LOMBOK_NODE_TYPE extends LombokNode<?, ?, ?>, AST_BASE_TYPE, AST_METHOD_DECL_TYPE> {
    public IMethodEditor<AST_BASE_TYPE> editor();

    public TypeRef returns();

    public TypeRef boxedReturns();

    public boolean returns(Class<?> var1);

    public boolean returns(String var1);

    public AccessLevel accessLevel();

    public boolean isSynchronized();

    public boolean isStatic();

    public boolean isConstructor();

    public boolean isAbstract();

    public boolean isEmpty();

    public AST_METHOD_DECL_TYPE get();

    public LOMBOK_NODE_TYPE node();

    public LOMBOK_NODE_TYPE getAnnotation(Class<? extends java.lang.annotation.Annotation> var1);

    public LOMBOK_NODE_TYPE getAnnotation(String var1);

    public boolean hasNonFinalArgument();

    public boolean hasArguments();

    public String name();

    public TYPE_TYPE surroundingType();

    public List<Statement<?>> statements();

    public List<Annotation> annotations();

    public /* varargs */ List<Argument> arguments(ArgumentStyle ... var1);

    public List<TypeParam> typeParameters();

    public List<TypeRef> thrownExceptions();

    public static enum ArgumentStyle {
        INCLUDE_ANNOTATIONS,
        BOXED_TYPES;
        

        private ArgumentStyle() {
        }
    }

}

