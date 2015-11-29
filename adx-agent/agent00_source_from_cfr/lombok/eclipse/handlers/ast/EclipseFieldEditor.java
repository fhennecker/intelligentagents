/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 */
package lombok.eclipse.handlers.ast;

import java.util.List;
import lombok.ast.Expression;
import lombok.ast.IFieldEditor;
import lombok.ast.Node;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseASTMaker;
import lombok.eclipse.handlers.ast.EclipseField;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;

public final class EclipseFieldEditor
implements IFieldEditor<ASTNode> {
    private final EclipseField field;
    private final EclipseASTMaker builder;

    EclipseFieldEditor(EclipseField field, ASTNode source) {
        this.field = field;
        this.builder = new EclipseASTMaker(field.node(), source);
    }

    FieldDeclaration get() {
        return this.field.get();
    }

    @Override
    public <T extends ASTNode> T build(Node<?> node) {
        return this.builder.build(node);
    }

    @Override
    public <T extends ASTNode> T build(Node<?> node, Class<T> extectedType) {
        return this.builder.build(node, extectedType);
    }

    @Override
    public <T extends ASTNode> List<T> build(List<? extends Node<?>> nodes) {
        return this.builder.build(nodes);
    }

    @Override
    public <T extends ASTNode> List<T> build(List<? extends Node<?>> nodes, Class<T> extectedType) {
        return this.builder.build(nodes, extectedType);
    }

    @Override
    public void replaceInitialization(Expression<?> initialization) {
        this.get().initialization = initialization == null ? null : (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(initialization.posHint((Object)this.get().initialization), (Class<T>)org.eclipse.jdt.internal.compiler.ast.Expression.class);
    }

    @Override
    public void makePrivate() {
        this.makePackagePrivate();
        this.get().modifiers |= 2;
    }

    @Override
    public void makePackagePrivate() {
        this.get().modifiers &= -8;
    }

    @Override
    public void makeProtected() {
        this.makePackagePrivate();
        this.get().modifiers |= 4;
    }

    @Override
    public void makePublic() {
        this.makePackagePrivate();
        this.get().modifiers |= 1;
    }

    @Override
    public void makeNonFinal() {
        this.get().modifiers &= -17;
    }

    public String toString() {
        return this.get().toString();
    }
}

