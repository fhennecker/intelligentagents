/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.Sanitize;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.SanitizeHandler;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseParameterSanitizer;
import lombok.eclipse.handlers.ast.EclipseMethod;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

@DeferUntilPostDiet
public class HandleSanitize
extends EclipseAnnotationHandler<Sanitize> {
    @Override
    public void handle(AnnotationValues<Sanitize> annotation, Annotation source, EclipseNode annotationNode) {
        new SanitizeHandler<EclipseMethod>(EclipseMethod.methodOf(annotationNode, (ASTNode)source), annotationNode).handle(new EclipseParameterSanitizer());
    }
}

