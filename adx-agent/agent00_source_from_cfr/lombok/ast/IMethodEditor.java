/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.List;
import lombok.ast.Argument;
import lombok.ast.Block;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.ast.TypeRef;

public interface IMethodEditor<AST_BASE_TYPE> {
    public <T extends AST_BASE_TYPE> T build(Node<?> var1);

    public <T extends AST_BASE_TYPE> T build(Node<?> var1, Class<T> var2);

    public <T extends AST_BASE_TYPE> List<T> build(List<? extends Node<?>> var1);

    public <T extends AST_BASE_TYPE> List<T> build(List<? extends Node<?>> var1, Class<T> var2);

    public /* varargs */ void replaceArguments(Argument ... var1);

    public void replaceArguments(List<Argument> var1);

    public void replaceReturnType(TypeRef var1);

    public void replaceReturns(Statement<?> var1);

    public void replaceVariableName(String var1, String var2);

    public /* varargs */ void replaceBody(Statement<?> ... var1);

    public void replaceBody(List<Statement<?>> var1);

    public void replaceBody(Block var1);

    public void forceQualifiedThis();

    public void makePrivate();

    public void makePackagePrivate();

    public void makeProtected();

    public void makePublic();

    public void rebuild();
}

