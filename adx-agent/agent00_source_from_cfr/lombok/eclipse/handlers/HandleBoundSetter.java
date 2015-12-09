/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.AccessLevel;
import lombok.BoundSetter;
import lombok.ast.IField;
import lombok.ast.IType;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.handlers.BoundSetterHandler;
import lombok.eclipse.DeferUntilBuildFieldsAndMethods;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseField;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

@DeferUntilBuildFieldsAndMethods
public class HandleBoundSetter
extends EclipseAnnotationHandler<BoundSetter> {
    @Override
    public void handle(AnnotationValues<BoundSetter> annotation, Annotation ast, EclipseNode annotationNode) {
        BoundSetter annotationInstance = annotation.getInstance();
        new BoundSetterHandler<EclipseType, EclipseField, EclipseNode, ASTNode>(annotationNode, (ASTNode)ast){

            @Override
            protected EclipseType typeOf(EclipseNode node, ASTNode ast) {
                return EclipseType.typeOf(node, ast);
            }

            @Override
            protected EclipseField fieldOf(EclipseNode node, ASTNode ast) {
                return EclipseField.fieldOf(node, ast);
            }
        }.handle(annotationInstance.value(), annotationInstance.vetoable(), annotationInstance.throwVetoException());
    }

}

