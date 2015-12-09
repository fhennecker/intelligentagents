/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.handlers.ast;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.ast.AST;
import lombok.ast.Argument;
import lombok.ast.Block;
import lombok.ast.IMethodEditor;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.ast.TypeRef;
import lombok.core.util.As;
import lombok.core.util.Is;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.ast.JavacASTMaker;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacType;
import lombok.javac.handlers.replace.ReturnStatementReplaceVisitor;
import lombok.javac.handlers.replace.ThisReferenceReplaceVisitor;
import lombok.javac.handlers.replace.VariableNameReplaceVisitor;

public final class JavacMethodEditor
implements IMethodEditor<JCTree> {
    private final JavacMethod method;
    private final JavacASTMaker builder;

    JavacMethodEditor(JavacMethod method, JCTree source) {
        this.method = method;
        this.builder = new JavacASTMaker(method.node(), source);
    }

    public JCTree.JCMethodDecl get() {
        return this.method.get();
    }

    public JavacNode node() {
        return this.method.node();
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
    public void replaceReturnType(TypeRef returnType) {
        if (this.method.isConstructor()) {
            return;
        }
        this.get().restype = (JCTree.JCExpression)this.build(returnType);
    }

    @Override
    public void replaceReturns(Statement<?> replacement) {
        new ReturnStatementReplaceVisitor(this.method, replacement).visit((JCTree)this.get());
    }

    @Override
    public void replaceVariableName(String oldName, String newName) {
        new VariableNameReplaceVisitor(this.method, oldName, newName).visit((JCTree)this.get());
    }

    @Override
    public void forceQualifiedThis() {
        new ThisReferenceReplaceVisitor(this.method, AST.This(AST.Type(this.method.surroundingType().name()))).visit((JCTree)this.get());
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
    public /* varargs */ void replaceArguments(Argument ... arguments) {
        this.replaceArguments(As.list(arguments));
    }

    @Override
    public void replaceArguments(java.util.List<Argument> arguments) {
        this.get().params = (List)this.build(arguments, JCTree.JCVariableDecl.class);
    }

    @Override
    public /* varargs */ void replaceBody(Statement<?> ... statements) {
        this.replaceBody(As.list(statements));
    }

    @Override
    public void replaceBody(java.util.List<Statement<?>> statements) {
        this.replaceBody(AST.Block().withStatements(statements));
    }

    @Override
    public void replaceBody(Block body) {
        JCTree.JCStatement suspect;
        Block bodyWithConstructorCall = new Block();
        if (!this.method.isEmpty() && this.isConstructorCall(suspect = (JCTree.JCStatement)this.get().body.stats.get(0))) {
            bodyWithConstructorCall.withStatement(AST.Stat((Object)suspect));
        }
        bodyWithConstructorCall.withStatements(body.getStatements());
        this.get().body = (JCTree.JCBlock)this.builder.build(bodyWithConstructorCall);
        Javac.addSuppressWarningsAll(this.get().mods, this.node(), this.get().pos);
    }

    private boolean isConstructorCall(JCTree.JCStatement supect) {
        if (!(supect instanceof JCTree.JCExpressionStatement)) {
            return false;
        }
        JCTree.JCExpression supectExpression = ((JCTree.JCExpressionStatement)supect).expr;
        if (!(supectExpression instanceof JCTree.JCMethodInvocation)) {
            return false;
        }
        return Is.oneOf(((JCTree.JCMethodInvocation)supectExpression).meth.toString(), "super", "this");
    }

    @Override
    public void rebuild() {
        this.node().rebuild();
    }

    public String toString() {
        return this.get().toString();
    }
}

