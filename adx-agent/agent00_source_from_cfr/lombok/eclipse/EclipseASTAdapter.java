/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Initializer
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 */
package lombok.eclipse;

import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

public abstract class EclipseASTAdapter
implements EclipseASTVisitor {
    public void visitCompilationUnit(EclipseNode top, CompilationUnitDeclaration unit) {
    }

    public void endVisitCompilationUnit(EclipseNode top, CompilationUnitDeclaration unit) {
    }

    public void visitType(EclipseNode typeNode, TypeDeclaration type) {
    }

    public void visitAnnotationOnType(TypeDeclaration type, EclipseNode annotationNode, Annotation annotation) {
    }

    public void endVisitType(EclipseNode typeNode, TypeDeclaration type) {
    }

    public void visitInitializer(EclipseNode initializerNode, Initializer initializer) {
    }

    public void endVisitInitializer(EclipseNode initializerNode, Initializer initializer) {
    }

    public void visitField(EclipseNode fieldNode, FieldDeclaration field) {
    }

    public void visitAnnotationOnField(FieldDeclaration field, EclipseNode annotationNode, Annotation annotation) {
    }

    public void endVisitField(EclipseNode fieldNode, FieldDeclaration field) {
    }

    public void visitMethod(EclipseNode methodNode, AbstractMethodDeclaration method) {
    }

    public void visitAnnotationOnMethod(AbstractMethodDeclaration method, EclipseNode annotationNode, Annotation annotation) {
    }

    public void endVisitMethod(EclipseNode methodNode, AbstractMethodDeclaration method) {
    }

    public void visitMethodArgument(EclipseNode argNode, Argument arg, AbstractMethodDeclaration method) {
    }

    public void visitAnnotationOnMethodArgument(Argument arg, AbstractMethodDeclaration method, EclipseNode annotationNode, Annotation annotation) {
    }

    public void endVisitMethodArgument(EclipseNode argNode, Argument arg, AbstractMethodDeclaration method) {
    }

    public void visitLocal(EclipseNode localNode, LocalDeclaration local) {
    }

    public void visitAnnotationOnLocal(LocalDeclaration local, EclipseNode annotationNode, Annotation annotation) {
    }

    public void endVisitLocal(EclipseNode localNode, LocalDeclaration local) {
    }

    public void visitStatement(EclipseNode statementNode, Statement statement) {
    }

    public void endVisitStatement(EclipseNode statementNode, Statement statement) {
    }
}

