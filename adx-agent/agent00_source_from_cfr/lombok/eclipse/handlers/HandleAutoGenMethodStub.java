/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 */
package lombok.eclipse.handlers;

import lombok.AutoGenMethodStub;
import lombok.ast.AST;
import lombok.ast.Expression;
import lombok.ast.MethodDecl;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.core.AnnotationValues;
import lombok.core.util.ErrorMessages;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseType;
import lombok.eclipse.handlers.ast.EclipseTypeEditor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class HandleAutoGenMethodStub
extends EclipseAnnotationHandler<AutoGenMethodStub> {
    @Override
    public void handle(AnnotationValues<AutoGenMethodStub> annotation, Annotation source, EclipseNode annotationNode) {
        EclipseType type = EclipseType.typeOf(annotationNode, (ASTNode)source);
        if (type.isInterface() || type.isAnnotation()) {
            annotationNode.addError(ErrorMessages.canBeUsedOnClassAndEnumOnly(AutoGenMethodStub.class));
        }
    }

    public MethodDeclaration handle(MethodBinding abstractMethod, AnnotationValues<AutoGenMethodStub> annotation, Annotation source, EclipseNode annotationNode) {
        EclipseType type = EclipseType.typeOf(annotationNode, (ASTNode)source);
        Statement statement = annotation.getInstance().throwException() ? AST.Throw(AST.New(AST.Type(UnsupportedOperationException.class)).withArgument(AST.String("This method is not implemented yet."))) : AST.ReturnDefault();
        MethodDeclaration method = (MethodDeclaration)type.editor().injectMethod((MethodDecl)AST.MethodDecl((Object)abstractMethod).implementing().withStatement(statement));
        type.editor().rebuild();
        return method;
    }
}

