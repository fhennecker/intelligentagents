/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.List;
import lombok.ast.ClassDecl;
import lombok.ast.ConstructorDecl;
import lombok.ast.EnumConstant;
import lombok.ast.FieldDecl;
import lombok.ast.IMethod;
import lombok.ast.Initializer;
import lombok.ast.MethodDecl;
import lombok.ast.Node;

public interface ITypeEditor<METHOD_TYPE extends IMethod<?, ?, ?, ?>, AST_BASE_TYPE, AST_TYPE_DECL_TYPE, AST_METHOD_DECL_TYPE> {
    public <T extends AST_BASE_TYPE> T build(Node<?> var1);

    public <T extends AST_BASE_TYPE> T build(Node<?> var1, Class<T> var2);

    public <T extends AST_BASE_TYPE> List<T> build(List<? extends Node<?>> var1);

    public <T extends AST_BASE_TYPE> List<T> build(List<? extends Node<?>> var1, Class<T> var2);

    public void injectInitializer(Initializer var1);

    public void injectField(FieldDecl var1);

    public void injectField(EnumConstant var1);

    public AST_METHOD_DECL_TYPE injectMethod(MethodDecl var1);

    public AST_METHOD_DECL_TYPE injectConstructor(ConstructorDecl var1);

    public void injectType(ClassDecl var1);

    public void removeMethod(METHOD_TYPE var1);

    public void makeEnum();

    public void makePrivate();

    public void makePackagePrivate();

    public void makeProtected();

    public void makePublic();

    public void makeStatic();

    public void rebuild();
}

