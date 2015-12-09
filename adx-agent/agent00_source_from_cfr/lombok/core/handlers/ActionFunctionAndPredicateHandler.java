/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.ast.AST;
import lombok.ast.AbstractMethodDecl;
import lombok.ast.Annotation;
import lombok.ast.Argument;
import lombok.ast.ClassDecl;
import lombok.ast.IMethod;
import lombok.ast.IMethodEditor;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.MethodDecl;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;

public final class ActionFunctionAndPredicateHandler<TYPE_TYPE extends IType<METHOD_TYPE, ?, ?, ?, ?, ?>, METHOD_TYPE extends IMethod<TYPE_TYPE, ?, ?, ?>> {
    public void rebuildMethod(METHOD_TYPE method, TemplateData template, IParameterValidator<METHOD_TYPE> validation, IParameterSanitizer<METHOD_TYPE> sanitizer) {
        Object type = method.surroundingType();
        TypeRef returnType = template.forcedReturnType == null ? method.boxedReturns() : AST.Type(template.forcedReturnType);
        ArrayList<TypeRef> boxedArgumentTypes = new ArrayList<TypeRef>();
        List<Argument> arguments = this.withUnderscoreName(method.arguments(IMethod.ArgumentStyle.INCLUDE_ANNOTATIONS));
        List<Argument> boxedArguments = method.arguments(IMethod.ArgumentStyle.BOXED_TYPES, IMethod.ArgumentStyle.INCLUDE_ANNOTATIONS);
        boxedArguments.removeAll(this.withUnderscoreName(boxedArguments));
        for (Argument argument : boxedArguments) {
            boxedArgumentTypes.add(argument.getType());
        }
        if (template.forcedReturnType == null && method.returns("void")) {
            method.editor().replaceReturns(AST.Return(AST.Null()));
        }
        TypeRef interfaceType = AST.Type(template.typeName).withTypeArguments(boxedArgumentTypes);
        if (template.forcedReturnType == null) {
            interfaceType.withTypeArgument(returnType);
        }
        MethodDecl innerMethod = (MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(returnType, template.methodName).posHint(method.get())).withArguments(boxedArguments)).makePublic()).implementing().withStatements(validation.validateParameterOf(method))).withStatements(sanitizer.sanitizeParameterOf(method))).withStatements(method.statements());
        if (template.forcedReturnType == null && method.returns("void")) {
            innerMethod.withStatement(AST.Return(AST.Null()));
        }
        MethodDecl methodReplacement = (MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(interfaceType, method.name()).posHint(method.get())).withArguments(arguments)).withTypeParameters(method.typeParameters())).withAnnotations(method.annotations())).withStatement(AST.Return(AST.New(interfaceType).withTypeDeclaration(AST.ClassDecl("").makeAnonymous().makeLocal().withMethod(innerMethod))));
        if (method.isStatic()) {
            methodReplacement.makeStatic();
        }
        methodReplacement.withAccessLevel(method.accessLevel());
        type.editor().injectMethod(methodReplacement);
        type.editor().removeMethod(method);
        type.editor().rebuild();
    }

    private List<Argument> withUnderscoreName(List<Argument> arguments) {
        ArrayList<Argument> filtedList = new ArrayList<Argument>();
        for (Argument argument : arguments) {
            if (!argument.getName().startsWith("_")) continue;
            filtedList.add(argument);
        }
        return filtedList;
    }

    public static class TemplateData {
        private final String typeName;
        private final String methodName;
        private final String forcedReturnType;

        public TemplateData(String typeName, String methodName, String forcedReturnType) {
            this.typeName = typeName;
            this.methodName = methodName;
            this.forcedReturnType = forcedReturnType;
        }

        public String getTypeName() {
            return this.typeName;
        }

        public String getMethodName() {
            return this.methodName;
        }

        public String getForcedReturnType() {
            return this.forcedReturnType;
        }

        public String toString() {
            return "ActionFunctionAndPredicateHandler.TemplateData(typeName=" + this.getTypeName() + ", methodName=" + this.getMethodName() + ", forcedReturnType=" + this.getForcedReturnType() + ")";
        }
    }

}

