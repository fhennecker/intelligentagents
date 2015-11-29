/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse.handlers.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.AST;
import lombok.ast.Annotation;
import lombok.ast.Block;
import lombok.ast.Expression;
import lombok.ast.IMethodEditor;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.ast.TypeRef;
import lombok.core.util.As;
import lombok.core.util.Each;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseASTMaker;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.ast.EclipseType;
import lombok.eclipse.handlers.replace.ReturnStatementReplaceVisitor;
import lombok.eclipse.handlers.replace.ThisReferenceReplaceVisitor;
import lombok.eclipse.handlers.replace.VariableNameReplaceVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public final class EclipseMethodEditor
implements IMethodEditor<ASTNode> {
    private final EclipseMethod method;
    private final EclipseASTMaker builder;

    EclipseMethodEditor(EclipseMethod method, ASTNode source) {
        this.method = method;
        this.builder = new EclipseASTMaker(method.node(), source);
    }

    AbstractMethodDeclaration get() {
        return this.method.get();
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
    public void replaceReturnType(TypeRef returnType) {
        if (this.method.isConstructor()) {
            return;
        }
        MethodDeclaration methodDecl = (MethodDeclaration)this.get();
        methodDecl.returnType = (TypeReference)this.build(returnType);
    }

    @Override
    public void replaceReturns(Statement<?> replacement) {
        new ReturnStatementReplaceVisitor(this.method, replacement).visit((ASTNode)this.get());
    }

    @Override
    public void replaceVariableName(String oldName, String newName) {
        new VariableNameReplaceVisitor(this.method, oldName, newName).visit((ASTNode)this.get());
    }

    @Override
    public void forceQualifiedThis() {
        new ThisReferenceReplaceVisitor(this.method, AST.This(AST.Type(this.method.surroundingType().name()))).visit((ASTNode)this.get());
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
    public /* varargs */ void replaceArguments(lombok.ast.Argument ... arguments) {
        this.replaceArguments(As.list(arguments));
    }

    @Override
    public void replaceArguments(List<lombok.ast.Argument> arguments) {
        this.get().arguments = this.build(arguments).toArray((T[])new Argument[0]);
    }

    @Override
    public /* varargs */ void replaceBody(Statement<?> ... statements) {
        this.replaceBody(As.list(statements));
    }

    @Override
    public void replaceBody(List<Statement<?>> statements) {
        this.get().bits |= 8388608;
        this.get().statements = this.build(statements).toArray((T[])new org.eclipse.jdt.internal.compiler.ast.Statement[0]);
        ArrayList<Object> annotations = new ArrayList<Object>();
        org.eclipse.jdt.internal.compiler.ast.Annotation[] originalAnnotations = this.get().annotations;
        for (org.eclipse.jdt.internal.compiler.ast.Annotation originalAnnotation : Each.elementIn(originalAnnotations)) {
            if (originalAnnotation.type.toString().endsWith("SuppressWarnings")) continue;
            annotations.add((Object)originalAnnotation);
        }
        annotations.add(this.build(AST.Annotation(AST.Type("java.lang.SuppressWarnings")).withValue(AST.String("all")), org.eclipse.jdt.internal.compiler.ast.Annotation.class));
        this.get().annotations = annotations.toArray((T[])new org.eclipse.jdt.internal.compiler.ast.Annotation[0]);
    }

    @Override
    public void replaceBody(Block body) {
        this.replaceBody(body.getStatements());
    }

    @Override
    public void rebuild() {
        this.method.node().rebuild();
    }

    public String toString() {
        return this.get().toString();
    }
}

