/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.EnumId;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.EnumIdHandler;
import lombok.core.util.ErrorMessages;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseField;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleEnumId
extends EclipseAnnotationHandler<EnumId> {
    @Override
    public void handle(AnnotationValues<EnumId> annotation, Annotation source, EclipseNode annotationNode) {
        EclipseType type = EclipseType.typeOf(annotationNode, (ASTNode)source);
        EclipseField field = EclipseField.fieldOf(annotationNode, (ASTNode)source);
        if (field == null) {
            annotationNode.addError(ErrorMessages.canBeUsedOnFieldOnly(EnumId.class));
            return;
        }
        new EnumIdHandler<EclipseType, EclipseField>(type, field, annotationNode).handle();
    }
}

