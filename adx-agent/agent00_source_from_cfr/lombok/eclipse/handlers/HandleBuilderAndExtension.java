/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.Builder;
import lombok.ast.TypeRef;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.handlers.BuilderAndExtensionHandler;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Names;
import lombok.eclipse.DeferUntilBuildFieldsAndMethods;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.EclipseParameterSanitizer;
import lombok.eclipse.handlers.EclipseParameterValidator;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleBuilderAndExtension {

    @DeferUntilBuildFieldsAndMethods
    public static class HandleBuilderExtension
    extends EclipseAnnotationHandler<Builder.Extension> {
        @Override
        public void handle(AnnotationValues<Builder.Extension> annotation, Annotation source, EclipseNode annotationNode) {
            EclipseMethod method = EclipseMethod.methodOf(annotationNode, (ASTNode)source);
            if (method == null) {
                annotationNode.addError(ErrorMessages.canBeUsedOnMethodOnly(Builder.Extension.class));
                return;
            }
            if (method.isAbstract()) {
                annotationNode.addError(ErrorMessages.canBeUsedOnConcreteMethodOnly(Builder.Extension.class));
                return;
            }
            EclipseType type = EclipseType.typeOf(annotationNode, (ASTNode)source);
            LombokNode builderNode = type.getAnnotation((Class)Builder.class);
            if (builderNode == null) {
                annotationNode.addError("@Builder.Extension is only allowed in types annotated with @Builder");
                return;
            }
            AnnotationValues<Builder> builderAnnotation = EclipseHandlerUtil.createAnnotation(Builder.class, (EclipseNode)builderNode);
            if (!type.hasMethod(Names.decapitalize(type.name()), new TypeRef[0])) {
                new HandleBuilder().handle(builderAnnotation, (Annotation)builderNode.get(), (EclipseNode)builderNode);
            }
            new BuilderAndExtensionHandler().handleExtension(type, method, new EclipseParameterValidator(), new EclipseParameterSanitizer(), builderAnnotation.getInstance(), annotation.getInstance());
        }
    }

    public static class HandleBuilder
    extends EclipseAnnotationHandler<Builder> {
        @Override
        public void handle(AnnotationValues<Builder> annotation, Annotation source, EclipseNode annotationNode) {
            EclipseType type = EclipseType.typeOf(annotationNode, (ASTNode)source);
            if (type.isInterface() || type.isEnum() || type.isAnnotation()) {
                annotationNode.addError(ErrorMessages.canBeUsedOnClassOnly(Builder.class));
                return;
            }
            switch (EclipseHandlerUtil.methodExists(Names.decapitalize(type.name()), type.node(), false, 0)) {
                case EXISTS_BY_LOMBOK: {
                    return;
                }
                case EXISTS_BY_USER: {
                    String message = "Not generating 'public static %s %s()' A method with that name already exists";
                    annotationNode.addWarning(String.format("Not generating 'public static %s %s()' A method with that name already exists", "$Builder", Names.decapitalize(type.name())));
                    return;
                }
            }
            new BuilderAndExtensionHandler().handleBuilder(type, annotation.getInstance());
        }
    }

}

