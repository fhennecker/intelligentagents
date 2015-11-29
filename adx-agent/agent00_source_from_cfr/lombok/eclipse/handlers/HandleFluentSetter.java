/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.AccessLevel;
import lombok.FluentSetter;
import lombok.ast.IField;
import lombok.ast.IType;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.handlers.FluentSetterHandler;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseField;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleFluentSetter
extends EclipseAnnotationHandler<FluentSetter> {
    @Override
    public void handle(AnnotationValues<FluentSetter> annotation, Annotation ast, EclipseNode annotationNode) {
        FluentSetter annotationInstance = annotation.getInstance();
        new FluentSetterHandler<EclipseType, EclipseField, EclipseNode, ASTNode>(annotationNode, (ASTNode)ast){

            @Override
            protected EclipseType typeOf(EclipseNode node, ASTNode ast) {
                return EclipseType.typeOf(node, ast);
            }

            @Override
            protected EclipseField fieldOf(EclipseNode node, ASTNode ast) {
                return EclipseField.fieldOf(node, ast);
            }
        }.handle(annotationInstance.value());
    }

}

