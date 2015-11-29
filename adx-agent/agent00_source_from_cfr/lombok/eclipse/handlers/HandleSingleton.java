/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.Singleton;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.SingletonHandler;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleSingleton
extends EclipseAnnotationHandler<Singleton> {
    @Override
    public void handle(AnnotationValues<Singleton> annotation, Annotation source, EclipseNode annotationNode) {
        Singleton.Style style = annotation.getInstance().style();
        new SingletonHandler(EclipseType.typeOf(annotationNode, (ASTNode)source), annotationNode).handle(style);
    }
}

