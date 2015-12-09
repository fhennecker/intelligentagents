/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.SwingInvokeAndWait;
import lombok.SwingInvokeLater;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.handlers.SwingInvokeHandler;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseParameterSanitizer;
import lombok.eclipse.handlers.EclipseParameterValidator;
import lombok.eclipse.handlers.ast.EclipseMethod;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleSwingInvoke {

    @DeferUntilPostDiet
    public static class HandleSwingInvokeAndWait
    extends EclipseAnnotationHandler<SwingInvokeAndWait> {
        @Override
        public void handle(AnnotationValues<SwingInvokeAndWait> annotation, Annotation source, EclipseNode annotationNode) {
            new SwingInvokeHandler<EclipseMethod>(EclipseMethod.methodOf(annotationNode, (ASTNode)source), annotationNode).handle("invokeAndWait", SwingInvokeAndWait.class, new EclipseParameterValidator(), new EclipseParameterSanitizer());
        }
    }

    @DeferUntilPostDiet
    public static class HandleSwingInvokeLater
    extends EclipseAnnotationHandler<SwingInvokeLater> {
        @Override
        public void handle(AnnotationValues<SwingInvokeLater> annotation, Annotation source, EclipseNode annotationNode) {
            new SwingInvokeHandler<EclipseMethod>(EclipseMethod.methodOf(annotationNode, (ASTNode)source), annotationNode).handle("invokeLater", SwingInvokeLater.class, new EclipseParameterValidator(), new EclipseParameterSanitizer());
        }
    }

}

