/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.ast.AST;
import lombok.ast.AbstractMethodDecl;
import lombok.ast.Annotation;
import lombok.ast.Argument;
import lombok.ast.Call;
import lombok.ast.ClassDecl;
import lombok.ast.ConstructorDecl;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.IField;
import lombok.ast.IFieldEditor;
import lombok.ast.IMethod;
import lombok.ast.IMethodEditor;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.MethodDecl;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;
import lombok.ast.Wildcard;
import lombok.core.TransformationsUtil;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.util.Is;
import lombok.core.util.Names;

public class BuilderAndExtensionHandler<TYPE_TYPE extends IType<METHOD_TYPE, FIELD_TYPE, ?, ?, ?, ?>, METHOD_TYPE extends IMethod<TYPE_TYPE, ?, ?, ?>, FIELD_TYPE extends IField<?, ?, ?>> {
    public static final String OPTIONAL_DEF = "OptionalDef";
    public static final String BUILDER = "$Builder";

    public void handleBuilder(TYPE_TYPE type, Builder builder) {
        BuilderData builderData = new BuilderData((IType)type, builder, null).collect();
        ArrayList<TypeRef> interfaceTypes = new ArrayList<TypeRef>(builderData.getRequiredFieldDefTypes());
        interfaceTypes.add(AST.Type("OptionalDef"));
        for (TypeRef interfaceType : interfaceTypes) {
            interfaceType.withTypeArguments(type.typeArguments());
        }
        ArrayList builderMethods = new ArrayList();
        this.createConstructor(builderData);
        this.createInitializeBuilderMethod(builderData);
        this.createRequiredFieldInterfaces(builderData, builderMethods);
        this.createOptionalFieldInterface(builderData, builderMethods);
        this.createBuilder(builderData, interfaceTypes, builderMethods);
    }

    public void handleExtension(TYPE_TYPE type, METHOD_TYPE method, IParameterValidator<METHOD_TYPE> validation, IParameterSanitizer<METHOD_TYPE> sanitizer, Builder builder, Builder.Extension extension) {
        Object builderType = type.memberType("$Builder");
        BuilderData builderData = new BuilderData((IType)type, builder, null).collect();
        ExtensionType extensionType = this.getExtensionType(method, builderData, extension.fields());
        if (extensionType == ExtensionType.NONE) {
            return;
        }
        Object interfaceType = extensionType == ExtensionType.REQUIRED ? type.memberType(builderData.getRequiredFieldDefTypeNames().get(0)) : type.memberType("OptionalDef");
        builderType.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), method.name()).posHint(method.get())).makePublic()).implementing().withArguments(method.arguments(IMethod.ArgumentStyle.INCLUDE_ANNOTATIONS))).withStatements(validation.validateParameterOf(method))).withStatements(sanitizer.sanitizeParameterOf(method))).withStatements(method.statements())).withStatement(AST.Return(AST.This())));
        interfaceType.editor().injectMethod((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), method.name()).makePublic()).withNoBody().withArguments(method.arguments(IMethod.ArgumentStyle.INCLUDE_ANNOTATIONS)));
        type.editor().removeMethod(method);
    }

    private ExtensionType getExtensionType(METHOD_TYPE method, BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, String[] fields) {
        if (method.isConstructor() || method.accessLevel() != AccessLevel.PRIVATE || !method.returns("void")) {
            method.node().addWarning("@Builder.Extension: The method '" + method.name() + "' is not a valid extension and was ignored.");
            return ExtensionType.NONE;
        }
        String[] extensionFieldNames = Is.notEmpty(fields) ? fields : this.extensionFieldNames(method, builderData);
        List<String> allFieldNames = builderData.getAllFieldNames();
        for (String potentialFieldName : extensionFieldNames) {
            if (allFieldNames.contains(Names.decapitalize(potentialFieldName))) continue;
            method.node().addWarning("@Builder.Extension: The method '" + method.name() + "' is not a valid extension and was ignored.");
            return ExtensionType.NONE;
        }
        List<String> requiredFieldNames = builderData.getRequiredFieldNames();
        HashSet<String> uninitializedRequiredFieldNames = new HashSet<String>();
        for (IField field : builderData.getAllFields()) {
            if (!requiredFieldNames.contains(field.name()) || field.isInitialized()) continue;
            uninitializedRequiredFieldNames.add(field.name());
        }
        boolean containsRequiredFields = false;
        for (String potentialFieldName2 : extensionFieldNames) {
            containsRequiredFields |= uninitializedRequiredFieldNames.remove(Names.decapitalize(potentialFieldName2));
        }
        if (containsRequiredFields) {
            if (uninitializedRequiredFieldNames.isEmpty()) {
                return ExtensionType.REQUIRED;
            }
            method.node().addWarning("@Builder.Extension: The method '" + method.name() + "' is not a valid extension and was ignored.");
            return ExtensionType.NONE;
        }
        return ExtensionType.OPTIONAL;
    }

    private String[] extensionFieldNames(METHOD_TYPE method, BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData) {
        String prefix = builderData.getPrefix();
        String methodName = method.name();
        if (methodName.startsWith(prefix)) {
            methodName = methodName.substring(prefix.length());
        }
        return methodName.split("And");
    }

    private void createConstructor(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData) {
        TYPE_TYPE type = builderData.getType();
        if (this.hasCustomConstructor(type)) {
            return;
        }
        ConstructorDecl constructorDecl = ((ConstructorDecl)((ConstructorDecl)AST.ConstructorDecl(type.name()).makePrivate()).withArgument((Argument)AST.Arg(AST.Type("$Builder").withTypeArguments(type.typeArguments()), "builder").makeFinal())).withImplicitSuper();
        for (IField field : builderData.getAllFields()) {
            if (field.isFinal() && field.isInitialized()) {
                if (BuilderAndExtensionHandler.isCollection(field)) {
                    constructorDecl.withStatement(AST.Call(AST.Field(field.name()), "addAll").withArgument(AST.Field(AST.Name("builder"), field.name())));
                    continue;
                }
                if (!BuilderAndExtensionHandler.isMap(field)) continue;
                constructorDecl.withStatement(AST.Call(AST.Field(field.name()), "putAll").withArgument(AST.Field(AST.Name("builder"), field.name())));
                continue;
            }
            constructorDecl.withStatement(AST.Assign(AST.Field(field.name()), AST.Field(AST.Name("builder"), field.name())));
        }
        type.editor().injectConstructor(constructorDecl);
    }

    private boolean hasCustomConstructor(IType<METHOD_TYPE, ?, ?, ?, ?, ?> type) {
        for (IMethod method : type.methods()) {
            String argumentTypeName;
            Argument argument;
            List<Argument> arguments;
            if (!method.isConstructor() || (arguments = method.arguments(new IMethod.ArgumentStyle[0])).size() != 1 || !(argumentTypeName = (argument = arguments.get(0)).getType().toString()).endsWith("Builder")) continue;
            method.editor().replaceArguments((Argument)AST.Arg(AST.Type("$Builder").withTypeArguments(type.typeArguments()), argument.getName()).makeFinal());
            return true;
        }
        return false;
    }

    private void createInitializeBuilderMethod(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData) {
        TYPE_TYPE type = builderData.getType();
        TypeRef fieldDefType = builderData.getRequiredFields().isEmpty() ? AST.Type("OptionalDef") : builderData.getRequiredFieldDefTypes().get(0);
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(fieldDefType, Names.decapitalize(type.name())).makeStatic()).withAccessLevel(builderData.getLevel())).withTypeParameters(type.typeParameters())).withStatement(AST.Return(AST.New(AST.Type("$Builder").withTypeArguments(type.typeArguments())))));
    }

    private void createRequiredFieldInterfaces(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, List<AbstractMethodDecl<?>> builderMethods) {
        List<FIELD_TYPE> fields = builderData.getRequiredFields();
        if (!fields.isEmpty()) {
            TYPE_TYPE type = builderData.getType();
            List<String> names = builderData.getRequiredFieldDefTypeNames();
            IField field = (IField)fields.get(0);
            String name = names.get(0);
            int iend = fields.size();
            for (int i = 1; i < iend; ++i) {
                ArrayList interfaceMethods = new ArrayList();
                this.createFluentSetter(builderData, names.get(i), field, interfaceMethods, builderMethods);
                type.editor().injectType(AST.InterfaceDecl(name).makePublic().makeStatic().withTypeParameters(type.typeParameters()).withMethods(interfaceMethods));
                field = (IField)fields.get(i);
                name = names.get(i);
            }
            ArrayList interfaceMethods = new ArrayList();
            this.createFluentSetter(builderData, "OptionalDef", field, interfaceMethods, builderMethods);
            type.editor().injectType(AST.InterfaceDecl(name).makePublic().makeStatic().withTypeParameters(type.typeParameters()).withMethods(interfaceMethods));
        }
    }

    private void createOptionalFieldInterface(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, List<AbstractMethodDecl<?>> builderMethods) {
        TYPE_TYPE type = builderData.getType();
        ArrayList interfaceMethods = new ArrayList();
        for (IField field : builderData.getOptionalFields()) {
            if (BuilderAndExtensionHandler.isInitializedMapOrCollection(field)) {
                if (!builderData.isGenerateConvenientMethodsEnabled()) continue;
                if (BuilderAndExtensionHandler.isCollection(field)) {
                    this.createCollectionMethods(builderData, field, interfaceMethods, builderMethods);
                    continue;
                }
                if (!BuilderAndExtensionHandler.isMap(field)) continue;
                this.createMapMethods(builderData, field, interfaceMethods, builderMethods);
                continue;
            }
            this.createFluentSetter(builderData, "OptionalDef", field, interfaceMethods, builderMethods);
        }
        this.createBuildMethod(builderData, type.name(), interfaceMethods, builderMethods);
        if (builderData.isAllowReset()) {
            this.createResetMethod(builderData, interfaceMethods, builderMethods);
        }
        for (String callMethod : builderData.getCallMethods()) {
            this.createMethodCall(builderData, callMethod, interfaceMethods, builderMethods);
        }
        type.editor().injectType(AST.InterfaceDecl("OptionalDef").makePublic().makeStatic().withTypeParameters(type.typeParameters()).withMethods(interfaceMethods));
    }

    private void createFluentSetter(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, String typeName, FIELD_TYPE field, List<AbstractMethodDecl<?>> interfaceMethods, List<AbstractMethodDecl<?>> builderMethods) {
        TYPE_TYPE type = builderData.getType();
        String methodName = Names.camelCase(builderData.getPrefix(), field.name());
        Argument arg0 = (Argument)AST.Arg(field.type(), field.name()).makeFinal();
        builderMethods.add(((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type(typeName).withTypeArguments(type.typeArguments()), methodName).makePublic()).implementing().withArgument(arg0)).withStatement(AST.Assign(AST.Field(field.name()), AST.Name(field.name())))).withStatement(AST.Return(AST.This())));
        interfaceMethods.add(((MethodDecl)AST.MethodDecl(AST.Type(typeName).withTypeArguments(type.typeArguments()), methodName).makePublic()).withNoBody().withArgument(arg0));
    }

    private void createCollectionMethods(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, FIELD_TYPE field, List<AbstractMethodDecl<?>> interfaceMethods, List<AbstractMethodDecl<?>> builderMethods) {
        TYPE_TYPE type = builderData.getType();
        TypeRef elementType = AST.Type(Object.class);
        TypeRef collectionType = AST.Type(Collection.class);
        List<TypeRef> typeArguments = field.typeArguments();
        if (typeArguments.size() == 1) {
            elementType = typeArguments.get(0);
            collectionType.withTypeArgument(AST.Wildcard(Wildcard.Bound.EXTENDS, elementType));
        }
        String addMethodName = Names.singular(Names.camelCase(builderData.getPrefix(), field.name()));
        Argument arg0 = (Argument)AST.Arg(elementType, "arg0").makeFinal();
        builderMethods.add(((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), addMethodName).makePublic()).implementing().withArgument(arg0)).withStatement(AST.Call(AST.Field(field.name()), "add").withArgument(AST.Name("arg0")))).withStatement(AST.Return(AST.This())));
        interfaceMethods.add(((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), addMethodName).makePublic()).withNoBody().withArgument(arg0));
        String addAllMethodName = Names.camelCase(builderData.getPrefix(), field.name());
        arg0 = (Argument)AST.Arg(collectionType, "arg0").makeFinal();
        builderMethods.add(((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), addAllMethodName).makePublic()).implementing().withArgument(arg0)).withStatement(AST.Call(AST.Field(field.name()), "addAll").withArgument(AST.Name("arg0")))).withStatement(AST.Return(AST.This())));
        interfaceMethods.add(((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), addAllMethodName).makePublic()).withNoBody().withArgument(arg0));
    }

    private void createMapMethods(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, FIELD_TYPE field, List<AbstractMethodDecl<?>> interfaceMethods, List<AbstractMethodDecl<?>> builderMethods) {
        TYPE_TYPE type = builderData.getType();
        TypeRef keyType = AST.Type(Object.class);
        TypeRef valueType = AST.Type(Object.class);
        TypeRef mapType = AST.Type(Map.class);
        List<TypeRef> typeArguments = field.typeArguments();
        if (typeArguments.size() == 2) {
            keyType = typeArguments.get(0);
            valueType = typeArguments.get(1);
            mapType.withTypeArgument(AST.Wildcard(Wildcard.Bound.EXTENDS, keyType)).withTypeArgument(AST.Wildcard(Wildcard.Bound.EXTENDS, valueType));
        }
        String putMethodName = Names.singular(Names.camelCase(builderData.getPrefix(), field.name()));
        Argument arg0 = (Argument)AST.Arg(keyType, "arg0").makeFinal();
        Argument arg1 = (Argument)AST.Arg(valueType, "arg1").makeFinal();
        builderMethods.add(((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), putMethodName).makePublic()).implementing().withArgument(arg0)).withArgument(arg1)).withStatement(AST.Call(AST.Field(field.name()), "put").withArgument(AST.Name("arg0")).withArgument(AST.Name("arg1")))).withStatement(AST.Return(AST.This())));
        interfaceMethods.add(((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), putMethodName).makePublic()).withNoBody().withArgument(arg0)).withArgument(arg1));
        String putAllMethodName = Names.camelCase(builderData.getPrefix(), field.name());
        arg0 = (Argument)AST.Arg(mapType, "arg0").makeFinal();
        builderMethods.add(((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), putAllMethodName).makePublic()).implementing().withArgument(arg0)).withStatement(AST.Call(AST.Field(field.name()), "putAll").withArgument(AST.Name("arg0")))).withStatement(AST.Return(AST.This())));
        interfaceMethods.add(((MethodDecl)AST.MethodDecl(AST.Type("OptionalDef").withTypeArguments(type.typeArguments()), putAllMethodName).makePublic()).withNoBody().withArgument(arg0));
    }

    private void createBuildMethod(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, String typeName, List<AbstractMethodDecl<?>> interfaceMethods, List<AbstractMethodDecl<?>> builderMethods) {
        TYPE_TYPE type = builderData.getType();
        builderMethods.add(((MethodDecl)AST.MethodDecl(AST.Type(typeName).withTypeArguments(type.typeArguments()), "build").makePublic()).implementing().withStatement(AST.Return(AST.New(AST.Type(typeName).withTypeArguments(type.typeArguments())).withArgument(AST.This()))));
        interfaceMethods.add(((MethodDecl)AST.MethodDecl(AST.Type(typeName).withTypeArguments(type.typeArguments()), "build").makePublic()).withNoBody());
    }

    private void createResetMethod(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, List<AbstractMethodDecl<?>> interfaceMethods, List<AbstractMethodDecl<?>> builderMethods) {
        TypeRef fieldDefType = builderData.getRequiredFields().isEmpty() ? AST.Type("OptionalDef") : builderData.getRequiredFieldDefTypes().get(0);
        MethodDecl methodDecl = ((MethodDecl)AST.MethodDecl(fieldDefType, "reset").makePublic()).implementing();
        for (IField field : builderData.getAllFields()) {
            if (field.isInitialized()) {
                String fieldDefaultMethodName = "$" + field.name() + "Default";
                methodDecl.withStatement(AST.Assign(AST.Field(field.name()), AST.Call(fieldDefaultMethodName)));
                continue;
            }
            methodDecl.withStatement(AST.Assign(AST.Field(field.name()), AST.DefaultValue(field.type())));
        }
        builderMethods.add(methodDecl.withStatement(AST.Return(AST.This())));
        interfaceMethods.add(((MethodDecl)AST.MethodDecl(fieldDefType, "reset").makePublic()).withNoBody());
    }

    private void createMethodCall(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, String methodName, List<AbstractMethodDecl<?>> interfaceMethods, List<AbstractMethodDecl<?>> builderMethods) {
        TYPE_TYPE type = builderData.getType();
        TypeRef returnType = AST.Type("void");
        boolean returnsVoid = true;
        ArrayList<TypeRef> thrownExceptions = new ArrayList<TypeRef>();
        if ("toString".equals(methodName)) {
            returnType = AST.Type(String.class);
            returnsVoid = false;
        } else {
            for (IMethod method : type.methods()) {
                if (!methodName.equals(method.name()) || method.hasArguments()) continue;
                returnType = method.returns();
                returnsVoid = method.returns("void");
                thrownExceptions.addAll(method.thrownExceptions());
                break;
            }
        }
        Call call = AST.Call(AST.Call("build"), methodName);
        if (returnsVoid) {
            builderMethods.add(((MethodDecl)((MethodDecl)AST.MethodDecl(returnType, methodName).makePublic()).implementing().withThrownExceptions(thrownExceptions)).withStatement(call));
        } else {
            builderMethods.add(((MethodDecl)((MethodDecl)AST.MethodDecl(returnType, methodName).makePublic()).implementing().withThrownExceptions(thrownExceptions)).withStatement(AST.Return(call)));
        }
        interfaceMethods.add(((MethodDecl)AST.MethodDecl(returnType, methodName).makePublic()).withNoBody().withThrownExceptions(thrownExceptions));
    }

    private void createBuilder(BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> builderData, List<TypeRef> interfaceTypes, List<AbstractMethodDecl<?>> builderMethods) {
        TYPE_TYPE type = builderData.getType();
        ArrayList<FieldDecl> builderFields = new ArrayList<FieldDecl>();
        ArrayList builderFieldDefaultMethods = new ArrayList();
        for (IField field : builderData.getAllFields()) {
            FieldDecl builderField = AST.FieldDecl(field.type(), field.name()).makePrivate();
            if (field.isInitialized()) {
                String fieldDefaultMethodName = "$" + field.name() + "Default";
                builderFieldDefaultMethods.add(((MethodDecl)((MethodDecl)AST.MethodDecl(field.type(), fieldDefaultMethodName).makeStatic()).withTypeParameters(type.typeParameters())).withStatement(AST.Return(field.initialization())));
                builderField.withInitialization(AST.Call(fieldDefaultMethodName));
                field.editor().replaceInitialization(AST.Call(AST.Name("$Builder"), fieldDefaultMethodName));
            }
            builderFields.add(builderField);
        }
        type.editor().injectType(AST.ClassDecl("$Builder").withTypeParameters(type.typeParameters()).makePrivate().makeStatic().implementing(interfaceTypes).withFields(builderFields).withMethods(builderFieldDefaultMethods).withMethods(builderMethods).withMethod(((ConstructorDecl)AST.ConstructorDecl("$Builder").makePrivate()).withImplicitSuper()));
    }

    private static <FIELD_TYPE extends IField<?, ?, ?>> boolean isInitializedMapOrCollection(FIELD_TYPE field) {
        return (BuilderAndExtensionHandler.isMap(field) || BuilderAndExtensionHandler.isCollection(field)) && field.isInitialized();
    }

    private static <FIELD_TYPE extends IField<?, ?, ?>> boolean isCollection(FIELD_TYPE field) {
        return field.isOfType("Collection") || field.isOfType("List") || field.isOfType("Set");
    }

    private static <FIELD_TYPE extends IField<?, ?, ?>> boolean isMap(FIELD_TYPE field) {
        return field.isOfType("Map");
    }

    static class 1 {
    }

    private static enum ExtensionType {
        NONE,
        REQUIRED,
        OPTIONAL;
        

        private ExtensionType() {
        }
    }

    private static class BuilderData<TYPE_TYPE extends IType<METHOD_TYPE, FIELD_TYPE, ?, ?, ?, ?>, METHOD_TYPE extends IMethod<TYPE_TYPE, ?, ?, ?>, FIELD_TYPE extends IField<?, ?, ?>> {
        private final List<FIELD_TYPE> requiredFields = new ArrayList<FIELD_TYPE>();
        private final List<FIELD_TYPE> optionalFields = new ArrayList<FIELD_TYPE>();
        private final List<TypeRef> requiredFieldDefTypes = new ArrayList<TypeRef>();
        private final List<String> requiredFieldNames = new ArrayList<String>();
        private final List<String> optionalFieldNames = new ArrayList<String>();
        private final List<String> requiredFieldDefTypeNames = new ArrayList<String>();
        private final TYPE_TYPE type;
        private final String prefix;
        private final List<String> callMethods;
        private final boolean generateConvenientMethodsEnabled;
        private final boolean allowReset;
        private final AccessLevel level;
        private final Set<String> excludes;

        private BuilderData(TYPE_TYPE type, Builder builder) {
            this.type = type;
            this.excludes = new HashSet<String>(Arrays.asList(builder.exclude()));
            this.generateConvenientMethodsEnabled = builder.convenientMethods();
            this.prefix = builder.prefix();
            this.callMethods = Arrays.asList(builder.callMethods());
            this.level = builder.value();
            this.allowReset = builder.allowReset();
        }

        public BuilderData<TYPE_TYPE, METHOD_TYPE, FIELD_TYPE> collect() {
            for (IField field : this.type.fields()) {
                String fieldName;
                if (field.isStatic() || this.excludes.contains(fieldName = field.name())) continue;
                if (!(field.isInitialized() || !field.isFinal() && field.annotations(TransformationsUtil.NON_NULL_PATTERN).isEmpty())) {
                    this.requiredFields.add((IField)field);
                    this.requiredFieldNames.add(fieldName);
                    String typeName = Names.capitalize(Names.camelCase(fieldName, "def"));
                    this.requiredFieldDefTypeNames.add(typeName);
                    this.requiredFieldDefTypes.add(AST.Type(typeName));
                    continue;
                }
                if ((!this.generateConvenientMethodsEnabled || !BuilderAndExtensionHandler.isInitializedMapOrCollection(field)) && field.isFinal()) continue;
                this.optionalFields.add((IField)field);
                this.optionalFieldNames.add(fieldName);
            }
            return this;
        }

        public List<FIELD_TYPE> getAllFields() {
            ArrayList<FIELD_TYPE> allFields = new ArrayList<FIELD_TYPE>(this.getRequiredFields());
            allFields.addAll(this.getOptionalFields());
            return allFields;
        }

        public List<String> getAllFieldNames() {
            ArrayList<String> allFieldNames = new ArrayList<String>(this.getRequiredFieldNames());
            allFieldNames.addAll(this.getOptionalFieldNames());
            return allFieldNames;
        }

        public List<FIELD_TYPE> getRequiredFields() {
            return this.requiredFields;
        }

        public List<FIELD_TYPE> getOptionalFields() {
            return this.optionalFields;
        }

        public List<TypeRef> getRequiredFieldDefTypes() {
            return this.requiredFieldDefTypes;
        }

        public List<String> getRequiredFieldNames() {
            return this.requiredFieldNames;
        }

        public List<String> getOptionalFieldNames() {
            return this.optionalFieldNames;
        }

        public List<String> getRequiredFieldDefTypeNames() {
            return this.requiredFieldDefTypeNames;
        }

        public TYPE_TYPE getType() {
            return this.type;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public List<String> getCallMethods() {
            return this.callMethods;
        }

        public boolean isGenerateConvenientMethodsEnabled() {
            return this.generateConvenientMethodsEnabled;
        }

        public boolean isAllowReset() {
            return this.allowReset;
        }

        public AccessLevel getLevel() {
            return this.level;
        }

        public Set<String> getExcludes() {
            return this.excludes;
        }

        /* synthetic */ BuilderData(IType x0, Builder x1, 1 x2) {
            this(x0, x1);
        }
    }

}

