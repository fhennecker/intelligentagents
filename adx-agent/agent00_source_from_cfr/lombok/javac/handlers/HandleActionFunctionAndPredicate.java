/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Attribute
 *  com.sun.tools.javac.code.Attribute$Class
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$MethodSymbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Action;
import lombok.Function;
import lombok.Predicate;
import lombok.core.AnnotationValues;
import lombok.core.handlers.ActionFunctionAndPredicateHandler;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.util.As;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.ResolutionBased;
import lombok.javac.handlers.HandleVal;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacParameterSanitizer;
import lombok.javac.handlers.JavacParameterValidator;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacResolver;

public class HandleActionFunctionAndPredicate {
    public void handle(AnnotationValues<? extends Annotation> annotation, JCTree.JCAnnotation source, JavacNode annotationNode, String forcedReturnType) {
        JCTree annotationType = source.annotationType;
        JavacMethod method = JavacMethod.methodOf(annotationNode, (JCTree)source);
        if (method.isAbstract()) {
            annotationNode.addError(String.format("@%s can be used on concrete methods only", new Object[]{annotationType}));
            return;
        }
        if (forcedReturnType != null && !method.returns(forcedReturnType)) {
            annotationNode.addError(String.format("@%s can only be used on methods with '%s' as return type", new Object[]{annotationType, forcedReturnType}));
            return;
        }
        Object templates = annotation.getActualExpression("value");
        Symbol.TypeSymbol resolvedTemplates = this.resolveTemplates(method.node(), source, templates);
        if (resolvedTemplates == null) {
            annotationNode.addError(String.format("@%s unable to resolve template type", new Object[]{annotationType}));
            return;
        }
        java.util.List<ActionFunctionAndPredicateHandler.TemplateData> matchingTemplates = this.findTemplatesFor(method.get(), resolvedTemplates, forcedReturnType);
        if (matchingTemplates.isEmpty()) {
            annotationNode.addError(String.format("@%s no template found that matches the given method signature", new Object[]{annotationType}));
            return;
        }
        if (matchingTemplates.size() > 1) {
            annotationNode.addError(String.format("@%s more than one template found that matches the given method signature", new Object[]{annotationType}));
            return;
        }
        method.node().traverse(new HandleVal());
        new ActionFunctionAndPredicateHandler().rebuildMethod(method, matchingTemplates.get(0), new JavacParameterValidator(), new JavacParameterSanitizer());
    }

    private Symbol.TypeSymbol resolveTemplates(JavacNode node, JCTree.JCAnnotation annotation, Object templatesDef) {
        if (templatesDef instanceof JCTree.JCFieldAccess) {
            JCTree.JCFieldAccess templates = (JCTree.JCFieldAccess)templatesDef;
            if (!"class".equals(As.string((Object)templates.name))) {
                return null;
            }
            Type templatesType = JavacResolver.CLASS.resolveMember(node, templates.selected);
            return templatesType == null ? null : templatesType.asElement();
        }
        Type annotationType = JavacResolver.CLASS.resolveMember(node, (JCTree.JCExpression)annotation.annotationType);
        if (annotationType == null) {
            return null;
        }
        java.util.List<Symbol.MethodSymbol> enclosedMethods = this.enclosedMethodsOf(annotationType.asElement());
        if (enclosedMethods.size() != 1) {
            return null;
        }
        Attribute.Class defaultValue = (Attribute.Class)enclosedMethods.get(0).getDefaultValue();
        return defaultValue.getValue().asElement();
    }

    private java.util.List<ActionFunctionAndPredicateHandler.TemplateData> findTemplatesFor(JCTree.JCMethodDecl methodDecl, Symbol.TypeSymbol template, String forcedReturnType) {
        ArrayList<ActionFunctionAndPredicateHandler.TemplateData> foundTemplates = new ArrayList<ActionFunctionAndPredicateHandler.TemplateData>();
        ActionFunctionAndPredicateHandler.TemplateData templateData = this.templateDataFor(methodDecl, template, forcedReturnType);
        if (templateData != null) {
            foundTemplates.add(templateData);
        }
        for (Symbol enclosedElement : template.getEnclosedElements()) {
            Symbol.TypeSymbol enclosedType;
            if (!(enclosedElement instanceof Symbol.TypeSymbol) || !(enclosedType = (Symbol.TypeSymbol)enclosedElement).isInterface() && !enclosedType.isStatic()) continue;
            foundTemplates.addAll(this.findTemplatesFor(methodDecl, enclosedType, forcedReturnType));
        }
        return foundTemplates;
    }

    private ActionFunctionAndPredicateHandler.TemplateData templateDataFor(JCTree.JCMethodDecl methodDecl, Symbol.TypeSymbol template, String forcedReturnType) {
        if ((template.flags() & 1) == 0) {
            return null;
        }
        if (!template.isInterface() && (template.flags() & 1024) == 0) {
            return null;
        }
        ArrayList templateTypeArguments = new ArrayList(template.type.getTypeArguments());
        java.util.List<Symbol.MethodSymbol> enclosedMethods = this.enclosedMethodsOf(template);
        if (enclosedMethods.size() != 1) {
            return null;
        }
        Symbol.MethodSymbol enclosedMethod = enclosedMethods.get(0);
        Type enclosedMethodType = enclosedMethod.type;
        if (!this.matchesReturnType(enclosedMethodType, forcedReturnType)) {
            return null;
        }
        ArrayList<Type> methodTypeArguments = new ArrayList<Type>((Collection<Type>)enclosedMethodType.getParameterTypes());
        if (forcedReturnType == null) {
            methodTypeArguments.add(enclosedMethodType.getReturnType());
        }
        if (!templateTypeArguments.equals(methodTypeArguments)) {
            return null;
        }
        if (forcedReturnType == null ? this.numberOfParameters(methodDecl) + 1 != templateTypeArguments.size() : this.numberOfParameters(methodDecl) != templateTypeArguments.size()) {
            return null;
        }
        return new ActionFunctionAndPredicateHandler.TemplateData(As.string((Object)template.getQualifiedName()), As.string((Object)enclosedMethod.name), forcedReturnType);
    }

    private boolean matchesReturnType(Type methodType, String forcedReturnType) {
        if (forcedReturnType == null) {
            return true;
        }
        return forcedReturnType.equals(methodType.getReturnType().toString());
    }

    private int numberOfParameters(JCTree.JCMethodDecl methodDecl) {
        int numberOfParameters = 0;
        for (JCTree.JCVariableDecl param : methodDecl.params) {
            if (As.string((Object)param.name).startsWith("_")) continue;
            ++numberOfParameters;
        }
        return numberOfParameters;
    }

    private java.util.List<Symbol.MethodSymbol> enclosedMethodsOf(Symbol.TypeSymbol type) {
        ArrayList<Symbol.MethodSymbol> enclosedMethods = new ArrayList<Symbol.MethodSymbol>();
        for (Symbol enclosedElement : type.getEnclosedElements()) {
            if (!(enclosedElement instanceof Symbol.MethodSymbol) || (enclosedElement.flags() & 1024) == 0) continue;
            enclosedMethods.add((Symbol.MethodSymbol)enclosedElement);
        }
        return enclosedMethods;
    }

    @ResolutionBased
    public static class HandlePredicate
    extends JavacAnnotationHandler<Predicate> {
        @Override
        public void handle(AnnotationValues<Predicate> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
            JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Predicate.class);
            new HandleActionFunctionAndPredicate().handle(annotation, source, annotationNode, "boolean");
        }
    }

    @ResolutionBased
    public static class HandleFunction
    extends JavacAnnotationHandler<Function> {
        @Override
        public void handle(AnnotationValues<Function> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
            JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Function.class);
            new HandleActionFunctionAndPredicate().handle(annotation, source, annotationNode, null);
        }
    }

    @ResolutionBased
    public static class HandleAction
    extends JavacAnnotationHandler<Action> {
        @Override
        public void handle(AnnotationValues<Action> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
            JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Action.class);
            new HandleActionFunctionAndPredicate().handle(annotation, source, annotationNode, "void");
        }
    }

}

