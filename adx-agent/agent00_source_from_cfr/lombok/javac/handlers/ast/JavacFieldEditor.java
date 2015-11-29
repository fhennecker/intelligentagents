/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.handlers.ast;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.ast.Expression;
import lombok.ast.IFieldEditor;
import lombok.ast.Node;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacASTMaker;
import lombok.javac.handlers.ast.JavacField;

public final class JavacFieldEditor
implements IFieldEditor<JCTree> {
    private final JavacField field;
    private final JavacASTMaker builder;

    JavacFieldEditor(JavacField field, JCTree source) {
        this.field = field;
        this.builder = new JavacASTMaker(field.node(), source);
    }

    JCTree.JCVariableDecl get() {
        return this.field.get();
    }

    @Override
    public <T extends JCTree> T build(Node<?> node) {
        return this.builder.build(node);
    }

    @Override
    public <T extends JCTree> T build(Node<?> node, Class<T> extectedType) {
        return this.builder.build(node, extectedType);
    }

    @Override
    public <T extends JCTree> java.util.List<T> build(java.util.List<? extends Node<?>> nodes) {
        return this.builder.build(nodes);
    }

    @Override
    public <T extends JCTree> java.util.List<T> build(java.util.List<? extends Node<?>> nodes, Class<T> extectedType) {
        return this.builder.build(nodes, extectedType);
    }

    @Override
    public void replaceInitialization(Expression<?> initialization) {
        this.get().init = initialization == null ? null : (JCTree.JCExpression)this.build(initialization, JCTree.JCExpression.class);
    }

    @Override
    public void makePrivate() {
        this.makePackagePrivate();
        this.get().mods.flags |= 2;
    }

    @Override
    public void makePackagePrivate() {
        this.get().mods.flags &= -8;
    }

    @Override
    public void makeProtected() {
        this.makePackagePrivate();
        this.get().mods.flags |= 4;
    }

    @Override
    public void makePublic() {
        this.makePackagePrivate();
        this.get().mods.flags |= 1;
    }

    @Override
    public void makeNonFinal() {
        this.get().mods.flags &= -17;
    }

    public String toString() {
        return this.get().toString();
    }
}

