/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding
 */
package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Action;
import lombok.Function;
import lombok.Predicate;
import lombok.core.AnnotationValues;
import lombok.core.handlers.ActionFunctionAndPredicateHandler;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.util.As;
import lombok.core.util.Each;
import lombok.eclipse.DeferUntilBuildFieldsAndMethods;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseParameterSanitizer;
import lombok.eclipse.handlers.EclipseParameterValidator;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class HandleActionFunctionAndPredicate {
    public void handle(Class<?> templates, Annotation source, EclipseNode annotationNode, String forcedReturnType) {
        TypeReference annotationType = source.type;
        EclipseMethod method = EclipseMethod.methodOf(annotationNode, (ASTNode)source);
        if (method.isAbstract()) {
            annotationNode.addError(String.format("@%s can be used on concrete methods only", new Object[]{annotationType}));
            return;
        }
        if (forcedReturnType != null && !method.returns(forcedReturnType)) {
            annotationNode.addError(String.format("@%s can only be used on methods with '%s' as return type", new Object[]{annotationType, forcedReturnType}));
            return;
        }
        ReferenceBinding resolvedTemplates = this.resolveTemplates(method.node(), source, templates);
        if (resolvedTemplates == null) {
            annotationNode.addError(String.format("@%s unable to resolve template type", new Object[]{annotationType}));
            return;
        }
        List<ActionFunctionAndPredicateHandler.TemplateData> matchingTemplates = this.findTemplatesFor(method.get(), resolvedTemplates, forcedReturnType);
        if (matchingTemplates.isEmpty()) {
            annotationNode.addError(String.format("@%s no template found that matches the given method signature", new Object[]{annotationType}));
            return;
        }
        if (matchingTemplates.size() > 1) {
            annotationNode.addError(String.format("@%s more than one template found that matches the given method signature", new Object[]{annotationType}));
            return;
        }
        new ActionFunctionAndPredicateHandler().rebuildMethod(method, matchingTemplates.get(0), new EclipseParameterValidator(), new EclipseParameterSanitizer());
    }

    private ReferenceBinding resolveTemplates(EclipseNode node, Annotation annotation, Class<?> templatesDef) {
        EclipseType type = EclipseType.typeOf(node, (ASTNode)annotation);
        MethodScope blockScope = type.get().initializerScope;
        char[][] typeNameTokens = Eclipse.fromQualifiedName(templatesDef.getName());
        QualifiedTypeReference typeRef = new QualifiedTypeReference(typeNameTokens, Eclipse.poss((ASTNode)annotation, typeNameTokens.length));
        return (ReferenceBinding)typeRef.resolveType((BlockScope)blockScope);
    }

    private List<ActionFunctionAndPredicateHandler.TemplateData> findTemplatesFor(AbstractMethodDeclaration methodDecl, ReferenceBinding template, String forcedReturnType) {
        ArrayList<ActionFunctionAndPredicateHandler.TemplateData> foundTemplates = new ArrayList<ActionFunctionAndPredicateHandler.TemplateData>();
        ActionFunctionAndPredicateHandler.TemplateData templateData = this.templateDataFor(methodDecl, template, forcedReturnType);
        if (templateData != null) {
            foundTemplates.add(templateData);
        }
        for (ReferenceBinding memberType : Each.elementIn(template.memberTypes())) {
            if (!template.isInterface() && !memberType.isStatic()) continue;
            foundTemplates.addAll(this.findTemplatesFor(methodDecl, memberType, forcedReturnType));
        }
        return foundTemplates;
    }

    private ActionFunctionAndPredicateHandler.TemplateData templateDataFor(AbstractMethodDeclaration methodDecl, ReferenceBinding template, String forcedReturnType) {
        if (!template.isPublic()) {
            return null;
        }
        if (!template.isInterface() && !template.isAbstract()) {
            return null;
        }
        List<TypeVariableBinding> templateTypeArguments = As.list(template.typeVariables());
        List<MethodBinding> enclosedMethods = this.enclosedMethodsOf((TypeBinding)template);
        if (enclosedMethods.size() != 1) {
            return null;
        }
        MethodBinding enclosedMethod = enclosedMethods.get(0);
        if (!this.matchesReturnType(enclosedMethod, forcedReturnType)) {
            return null;
        }
        List<TypeBinding> methodTypeArguments = As.list(enclosedMethod.parameters);
        if (forcedReturnType == null) {
            methodTypeArguments.add(enclosedMethod.returnType);
        }
        if (!templateTypeArguments.equals(methodTypeArguments)) {
            return null;
        }
        if (forcedReturnType == null ? this.numberOfParameters(methodDecl) + 1 != templateTypeArguments.size() : this.numberOfParameters(methodDecl) != templateTypeArguments.size()) {
            return null;
        }
        return new ActionFunctionAndPredicateHandler.TemplateData(this.qualifiedName((TypeBinding)template), As.string(enclosedMethod.selector), forcedReturnType);
    }

    private boolean matchesReturnType(MethodBinding method, String forcedReturnType) {
        if (forcedReturnType == null) {
            return true;
        }
        if ("void".equals(forcedReturnType)) {
            return method.returnType.id == 6;
        }
        if ("boolean".equals(forcedReturnType)) {
            return method.returnType.id == 5;
        }
        return false;
    }

    private int numberOfParameters(AbstractMethodDeclaration methodDecl) {
        int numberOfParameters = 0;
        for (Argument param : Each.elementIn(methodDecl.arguments)) {
            if (As.string(param.name).startsWith("_")) continue;
            ++numberOfParameters;
        }
        return numberOfParameters;
    }

    private String qualifiedName(TypeBinding typeBinding) {
        String qualifiedName = As.string(typeBinding.qualifiedPackageName());
        if (!qualifiedName.isEmpty()) {
            qualifiedName = qualifiedName + ".";
        }
        qualifiedName = qualifiedName + As.string(typeBinding.qualifiedSourceName());
        return qualifiedName;
    }

    private List<MethodBinding> enclosedMethodsOf(TypeBinding type) {
        ArrayList<MethodBinding> enclosedMethods = new ArrayList<MethodBinding>();
        if (type instanceof ReferenceBinding) {
            ReferenceBinding rb = (ReferenceBinding)type;
            for (MethodBinding enclosedElement : Each.elementIn(rb.availableMethods())) {
                if (!enclosedElement.isAbstract()) continue;
                enclosedMethods.add(enclosedElement);
            }
        }
        return enclosedMethods;
    }

    @DeferUntilBuildFieldsAndMethods
    public static class HandlePredicate
    extends EclipseAnnotationHandler<Predicate> {
        @Override
        public void handle(AnnotationValues<Predicate> annotation, Annotation source, EclipseNode annotationNode) {
            new HandleActionFunctionAndPredicate().handle(annotation.getInstance().value(), source, annotationNode, "boolean");
        }
    }

    @DeferUntilBuildFieldsAndMethods
    public static class HandleFunction
    extends EclipseAnnotationHandler<Function> {
        @Override
        public void handle(AnnotationValues<Function> annotation, Annotation source, EclipseNode annotationNode) {
            new HandleActionFunctionAndPredicate().handle(annotation.getInstance().value(), source, annotationNode, null);
        }
    }

    @DeferUntilBuildFieldsAndMethods
    public static class HandleAction
    extends EclipseAnnotationHandler<Action> {
        @Override
        public void handle(AnnotationValues<Action> annotation, Annotation source, EclipseNode annotationNode) {
            new HandleActionFunctionAndPredicate().handle(annotation.getInstance().value(), source, annotationNode, "void");
        }
    }

}

