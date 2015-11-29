/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.AccessLevel;
import lombok.LazyGetter;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.LazyGetterHandler;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseField;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleLazyGetter
extends EclipseAnnotationHandler<LazyGetter> {
    @Override
    public void handle(AnnotationValues<LazyGetter> annotation, Annotation ast, EclipseNode annotationNode) {
        EclipseType type = EclipseType.typeOf(annotationNode, (ASTNode)ast);
        EclipseField field = EclipseField.fieldOf(annotationNode, (ASTNode)ast);
        LazyGetter annotationInstance = annotation.getInstance();
        new LazyGetterHandler<EclipseType, EclipseField>(type, field, annotationNode).handle(annotationInstance.value());
    }
}

