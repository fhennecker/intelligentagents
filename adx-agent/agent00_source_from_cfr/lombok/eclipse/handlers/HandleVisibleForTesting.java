/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.VisibleForTesting;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.util.ErrorMessages;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseMethod;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleVisibleForTesting
extends EclipseAnnotationHandler<VisibleForTesting> {
    @Override
    public void handle(AnnotationValues<VisibleForTesting> annotation, Annotation source, EclipseNode annotationNode) {
        EclipseNode mayBeMethod = (EclipseNode)annotationNode.up();
        if (mayBeMethod.getKind() == AST.Kind.METHOD) {
            EclipseMethod method = EclipseMethod.methodOf(annotationNode, (ASTNode)source);
            if (method.isAbstract()) {
                annotationNode.addError(ErrorMessages.canBeUsedOnConcreteMethodOnly(VisibleForTesting.class));
                return;
            }
        } else if (mayBeMethod.getKind() != AST.Kind.TYPE) {
            annotationNode.addError(ErrorMessages.canBeUsedOnClassAndMethodOnly(VisibleForTesting.class));
        }
    }
}

