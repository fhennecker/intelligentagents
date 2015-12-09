/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Scope
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$ClassSymbol
 *  com.sun.tools.javac.code.Symbol$MethodSymbol
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 */
package lombok.javac.handlers.ast;

import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import lombok.ast.AbstractMethodDecl;
import lombok.ast.ClassDecl;
import lombok.ast.ConstructorDecl;
import lombok.ast.EnumConstant;
import lombok.ast.FieldDecl;
import lombok.ast.IMethod;
import lombok.ast.ITypeEditor;
import lombok.ast.Initializer;
import lombok.ast.MethodDecl;
import lombok.ast.Node;
import lombok.ast.WrappedMethodDecl;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacASTMaker;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacType;

public final class JavacTypeEditor
implements ITypeEditor<JavacMethod, JCTree, JCTree.JCClassDecl, JCTree.JCMethodDecl> {
    private final JavacType type;
    private final JavacASTMaker builder;

    JavacTypeEditor(JavacType type, JCTree source) {
        this.type = type;
        this.builder = new JavacASTMaker(type.node(), source);
    }

    JCTree.JCClassDecl get() {
        return this.type.get();
    }

    JavacNode node() {
        return this.type.node();
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
    public void injectInitializer(Initializer initializer) {
        JCTree.JCBlock initializerBlock = (JCTree.JCBlock)this.builder.build(initializer);
        Javac.injectInitializer(this.node(), initializerBlock);
    }

    @Override
    public void injectField(FieldDecl fieldDecl) {
        JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)this.builder.build(fieldDecl);
        JavacHandlerUtil.injectField(this.node(), field);
    }

    @Override
    public void injectField(EnumConstant enumConstant) {
        JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)this.builder.build(enumConstant);
        JavacHandlerUtil.injectField(this.node(), field);
    }

    @Override
    public JCTree.JCMethodDecl injectMethod(MethodDecl methodDecl) {
        return this.injectMethodImpl(methodDecl);
    }

    @Override
    public JCTree.JCMethodDecl injectConstructor(ConstructorDecl constructorDecl) {
        return this.injectMethodImpl(constructorDecl);
    }

    private JCTree.JCMethodDecl injectMethodImpl(AbstractMethodDecl<?> methodDecl) {
        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl)this.builder.build(methodDecl, JCTree.JCMethodDecl.class);
        JavacHandlerUtil.injectMethod(this.node(), method);
        if (methodDecl instanceof WrappedMethodDecl) {
            WrappedMethodDecl node = (WrappedMethodDecl)methodDecl;
            Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol)node.getWrappedObject();
            JCTree.JCClassDecl tree = this.get();
            Symbol.ClassSymbol c = tree.sym;
            c.members_field.enter((Symbol)methodSymbol, c.members_field, methodSymbol.enclClass().members_field);
            method.sym = methodSymbol;
        }
        return method;
    }

    @Override
    public void injectType(ClassDecl typeDecl) {
        JCTree.JCClassDecl type = (JCTree.JCClassDecl)this.builder.build(typeDecl);
        Javac.injectType(this.node(), type);
    }

    @Override
    public void removeMethod(JavacMethod method) {
        JCTree.JCClassDecl type = this.get();
        ListBuffer defs = ListBuffer.lb();
        for (JCTree def : type.defs) {
            if (def.equals((Object)method.get())) continue;
            defs.append((Object)def);
        }
        type.defs = defs.toList();
        this.node().removeChild(method.node());
    }

    @Override
    public void makeEnum() {
        this.get().mods.flags |= 16384;
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
    public void makeStatic() {
        this.get().mods.flags |= 8;
    }

    @Override
    public void rebuild() {
        this.node().rebuild();
    }

    public String toString() {
        return this.get().toString();
    }
}

