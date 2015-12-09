/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.List;
import lombok.ast.Expression;
import lombok.ast.Node;

public interface IFieldEditor<AST_BASE_TYPE> {
    public <T extends AST_BASE_TYPE> T build(Node<?> var1);

    public <T extends AST_BASE_TYPE> T build(Node<?> var1, Class<T> var2);

    public <T extends AST_BASE_TYPE> List<T> build(List<? extends Node<?>> var1);

    public <T extends AST_BASE_TYPE> List<T> build(List<? extends Node<?>> var1, Class<T> var2);

    public void replaceInitialization(Expression<?> var1);

    public void makePrivate();

    public void makePackagePrivate();

    public void makeProtected();

    public void makePublic();

    public void makeNonFinal();
}

