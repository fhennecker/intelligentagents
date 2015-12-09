/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.Validate;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterValidator;
import lombok.core.handlers.ValidateHandler;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseParameterValidator;
import lombok.eclipse.handlers.ast.EclipseMethod;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

@DeferUntilPostDiet
public class HandleValidate
extends EclipseAnnotationHandler<Validate> {
    @Override
    public void handle(AnnotationValues<Validate> annotation, Annotation source, EclipseNode annotationNode) {
        new ValidateHandler<EclipseMethod>(EclipseMethod.methodOf(annotationNode, (ASTNode)source), annotationNode).handle(new EclipseParameterValidator());
    }
}

