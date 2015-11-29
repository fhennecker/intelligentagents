/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.Builder;
import lombok.ast.TypeRef;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.handlers.BuilderAndExtensionHandler;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Names;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacParameterSanitizer;
import lombok.javac.handlers.JavacParameterValidator;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacType;

public class HandleBuilderAndExtension {

    public static class HandleBuilderExtension
    extends JavacAnnotationHandler<Builder.Extension> {
        @Override
        public void handle(AnnotationValues<Builder.Extension> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
            if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
                return;
            }
            JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Builder.Extension.class);
            JavacMethod method = JavacMethod.methodOf(annotationNode, (JCTree)source);
            if (method == null) {
                annotationNode.addError(ErrorMessages.canBeUsedOnMethodOnly(Builder.Extension.class));
                return;
            }
            if (method.isAbstract() || method.isEmpty()) {
                annotationNode.addError(ErrorMessages.canBeUsedOnConcreteMethodOnly(Builder.Extension.class));
                return;
            }
            JavacType type = JavacType.typeOf(annotationNode, (JCTree)source);
            LombokNode builderNode = type.getAnnotation((Class)Builder.class);
            if (builderNode == null) {
                annotationNode.addError("@Builder.Extension is only allowed in types annotated with @Builder");
                return;
            }
            AnnotationValues<Builder> builderAnnotation = JavacHandlerUtil.createAnnotation(Builder.class, (JavacNode)builderNode);
            if (!type.hasMethod(Names.decapitalize(type.name()), new TypeRef[0])) {
                new HandleBuilder().handle(builderAnnotation, (JCTree.JCAnnotation)builderNode.get(), (JavacNode)builderNode);
            }
            new BuilderAndExtensionHandler().handleExtension(type, method, new JavacParameterValidator(), new JavacParameterSanitizer(), builderAnnotation.getInstance(), annotation.getInstance());
        }
    }

    public static class HandleBuilder
    extends JavacAnnotationHandler<Builder> {
        @Override
        public void handle(AnnotationValues<Builder> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
            JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Builder.class);
            JavacType type = JavacType.typeOf(annotationNode, (JCTree)source);
            if (type.isInterface() || type.isEnum() || type.isAnnotation()) {
                annotationNode.addError(ErrorMessages.canBeUsedOnClassOnly(Builder.class));
                return;
            }
            switch (JavacHandlerUtil.methodExists(Names.decapitalize(type.name()), type.node(), false, 0)) {
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

