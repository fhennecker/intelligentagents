/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.beans.ConstructorProperties;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.BoundSetter;
import lombok.ast.Annotation;
import lombok.ast.Argument;
import lombok.ast.Block;
import lombok.ast.Call;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.IField;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.If;
import lombok.ast.LocalDecl;
import lombok.ast.MethodDecl;
import lombok.ast.New;
import lombok.ast.NewArray;
import lombok.ast.Statement;
import lombok.ast.Synchronized;
import lombok.ast.Try;
import lombok.ast.TypeRef;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.TransformationsUtil;
import lombok.core.util.As;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Names;
import lombok.experimental.Accessors;

public abstract class BoundSetterHandler<TYPE_TYPE extends IType<?, FIELD_TYPE, ?, ?, ?, ?>, FIELD_TYPE extends IField<?, ?, ?>, LOMBOK_NODE_TYPE extends LombokNode<?, LOMBOK_NODE_TYPE, ?>, SOURCE_TYPE> {
    private static final String PROPERTY_CHANGE_SUPPORT_FIELD_NAME = "$propertyChangeSupport";
    private static final String VETOABLE_CHANGE_SUPPORT_FIELD_NAME = "$vetoableChangeSupport";
    private static final String PROPERTY_CHANGE_SUPPORT_METHOD_NAME = "getPropertyChangeSupport";
    private static final String VETOABLE_CHANGE_SUPPORT_METHOD_NAME = "getVetoableChangeSupport";
    private static final String LISTENER_ARG_NAME = "listener";
    private static final String PROPERTY_NAME_ARG_NAME = "propertyName";
    private static final String OLD_VALUE_ARG_NAME = "oldValue";
    private static final String NEW_VALUE_ARG_NAME = "newValue";
    private static final String[] PROPERTY_CHANGE_METHOD_NAMES = As.array("addPropertyChangeListener", "removePropertyChangeListener");
    private static final String[] VETOABLE_CHANGE_METHOD_NAMES = As.array("addVetoableChangeListener", "removeVetoableChangeListener");
    private static final String FIRE_PROPERTY_CHANGE_METHOD_NAME = "firePropertyChange";
    private static final String FIRE_VETOABLE_CHANGE_METHOD_NAME = "fireVetoableChange";
    private static final String OLD_VALUE_VARIABLE_NAME = "$old";
    private static final String E_VALUE_VARIABLE_NAME = "$e";
    private static final Pattern SETTER_PATTERN = Pattern.compile("^(?:setter|fluentsetter|boundsetter)$", 2);
    private final LOMBOK_NODE_TYPE annotationNode;
    private final SOURCE_TYPE ast;

    public void handle(AccessLevel level, boolean vetoable, boolean throwVetoException) {
        Object mayBeField = this.annotationNode.up();
        if (mayBeField == null) {
            return;
        }
        TYPE_TYPE type = this.typeOf(this.annotationNode, this.ast);
        ArrayList<Object> fields = new ArrayList<Object>();
        if (mayBeField.getKind() == AST.Kind.FIELD) {
            for (LombokNode node : this.annotationNode.upFromAnnotationToFields()) {
                fields.add(this.fieldOf(node, this.ast));
            }
        } else if (mayBeField.getKind() == AST.Kind.TYPE) {
            for (IField field : type.fields()) {
                if (!field.annotations(SETTER_PATTERN).isEmpty() || field.name().startsWith("$") || field.isFinal() || field.isStatic()) continue;
                fields.add(field);
            }
        } else {
            this.annotationNode.addError(ErrorMessages.canBeUsedOnClassAndFieldOnly(BoundSetter.class));
            return;
        }
        this.generateSetter(type, fields, level, vetoable | throwVetoException, throwVetoException);
    }

    protected abstract TYPE_TYPE typeOf(LOMBOK_NODE_TYPE var1, SOURCE_TYPE var2);

    protected abstract FIELD_TYPE fieldOf(LOMBOK_NODE_TYPE var1, SOURCE_TYPE var2);

    private void generateSetter(TYPE_TYPE type, List<FIELD_TYPE> fields, AccessLevel level, boolean vetoable, boolean throwVetoException) {
        if (!fields.isEmpty()) {
            if (!this.hasAllPropertyChangeMethods(type)) {
                this.generatePropertyChangeSupportFields(type);
                this.generateGetPropertySupportMethod(type);
                this.generatePropertyChangeListenerMethods(type);
                this.generateFirePropertyChangeMethod(type);
            }
            if (vetoable && !this.hasAllVetoableChangeMethods(type)) {
                this.generateVetoableChangeSupportFields(type);
                this.generateGetVetoableSupportMethod(type);
                this.generateVetoableChangeListenerMethods(type);
                this.generateFireVetoableChangeMethod(type);
            }
        }
        for (IField field : fields) {
            String propertyNameFieldName = "PROP_" + Names.camelCaseToConstant(field.name());
            this.generatePropertyNameConstant(type, field, propertyNameFieldName);
            this.generateSetter(type, field, level, vetoable, throwVetoException, propertyNameFieldName);
        }
    }

    private boolean hasAllPropertyChangeMethods(TYPE_TYPE type) {
        for (String methodName : PROPERTY_CHANGE_METHOD_NAMES) {
            if (type.hasMethodIncludingSupertypes(methodName, lombok.ast.AST.Type(PropertyChangeListener.class))) continue;
            return false;
        }
        return type.hasMethodIncludingSupertypes("firePropertyChange", lombok.ast.AST.Type(String.class), lombok.ast.AST.Type(Object.class), lombok.ast.AST.Type(Object.class));
    }

    private boolean hasAllVetoableChangeMethods(TYPE_TYPE type) {
        for (String methodName : VETOABLE_CHANGE_METHOD_NAMES) {
            if (type.hasMethodIncludingSupertypes(methodName, lombok.ast.AST.Type(VetoableChangeListener.class))) continue;
            return false;
        }
        return type.hasMethodIncludingSupertypes("fireVetoableChange", lombok.ast.AST.Type(String.class), lombok.ast.AST.Type(Object.class), lombok.ast.AST.Type(Object.class));
    }

    private void generatePropertyNameConstant(TYPE_TYPE type, FIELD_TYPE field, String propertyNameFieldName) {
        String propertyName = field.name();
        if (type.hasField(propertyNameFieldName)) {
            return;
        }
        type.editor().injectField(((FieldDecl)lombok.ast.AST.FieldDecl(lombok.ast.AST.Type(String.class), propertyNameFieldName).makePublic().makeStatic().makeFinal()).withInitialization(lombok.ast.AST.String(propertyName)));
    }

    private void generateSetter(TYPE_TYPE type, FIELD_TYPE field, AccessLevel level, boolean vetoable, boolean throwVetoException, String propertyNameFieldName) {
        String fieldName = field.name();
        boolean isBoolean = field.isOfType("boolean");
        AnnotationValues<Accessors> accessors = AnnotationValues.of(Accessors.class, field.node());
        String setterName = TransformationsUtil.toSetterName(accessors, fieldName, isBoolean);
        if (type.hasMethod(setterName, field.type())) {
            return;
        }
        String oldValueName = "$old";
        List<Annotation> nonNulls = field.annotations(TransformationsUtil.NON_NULL_PATTERN);
        MethodDecl methodDecl = (MethodDecl)((MethodDecl)lombok.ast.AST.MethodDecl(lombok.ast.AST.Type("void"), setterName).withAccessLevel(level)).withArgument((Argument)lombok.ast.AST.Arg(field.type(), fieldName).withAnnotations(nonNulls));
        if (!nonNulls.isEmpty() && !field.isPrimitive()) {
            methodDecl.withStatement(lombok.ast.AST.If(lombok.ast.AST.Equal(lombok.ast.AST.Name(fieldName), lombok.ast.AST.Null())).Then(lombok.ast.AST.Throw(lombok.ast.AST.New(lombok.ast.AST.Type(NullPointerException.class)).withArgument(lombok.ast.AST.String(fieldName)))));
        }
        methodDecl.withStatement(((LocalDecl)lombok.ast.AST.LocalDecl(field.type(), oldValueName).makeFinal()).withInitialization(lombok.ast.AST.Field(fieldName)));
        if (vetoable) {
            if (throwVetoException) {
                methodDecl.withThrownException(lombok.ast.AST.Type(PropertyVetoException.class));
                methodDecl.withStatement(lombok.ast.AST.Call("fireVetoableChange").withArgument(lombok.ast.AST.Name(propertyNameFieldName)).withArgument(lombok.ast.AST.Name(oldValueName)).withArgument(lombok.ast.AST.Name(fieldName)));
            } else {
                methodDecl.withStatement(lombok.ast.AST.Try(lombok.ast.AST.Block().withStatement(lombok.ast.AST.Call("fireVetoableChange").withArgument(lombok.ast.AST.Name(propertyNameFieldName)).withArgument(lombok.ast.AST.Name(oldValueName)).withArgument(lombok.ast.AST.Name(fieldName)))).Catch(lombok.ast.AST.Arg(lombok.ast.AST.Type(PropertyVetoException.class), "$e"), lombok.ast.AST.Block().withStatement(lombok.ast.AST.Return())));
            }
        }
        ((MethodDecl)methodDecl.withStatement(lombok.ast.AST.Assign(lombok.ast.AST.Field(fieldName), lombok.ast.AST.Name(fieldName)))).withStatement(lombok.ast.AST.Call("firePropertyChange").withArgument(lombok.ast.AST.Name(propertyNameFieldName)).withArgument(lombok.ast.AST.Name(oldValueName)).withArgument(lombok.ast.AST.Name(fieldName)));
        type.editor().injectMethod(methodDecl);
    }

    private void generatePropertyChangeSupportFields(TYPE_TYPE type) {
        if (!type.hasField("$propertyChangeSupport")) {
            type.editor().injectField(lombok.ast.AST.FieldDecl(lombok.ast.AST.Type(PropertyChangeSupport.class), "$propertyChangeSupport").makePrivate().makeTransient().makeVolatile());
        }
        if (!type.hasField("$propertyChangeSupportLock")) {
            type.editor().injectField(((FieldDecl)lombok.ast.AST.FieldDecl(lombok.ast.AST.Type(Object.class).withDimensions(1), "$propertyChangeSupportLock").makePrivate().makeFinal()).withInitialization(lombok.ast.AST.NewArray(lombok.ast.AST.Type(Object.class)).withDimensionExpression(lombok.ast.AST.Number(0))));
        }
    }

    private void generateGetPropertySupportMethod(TYPE_TYPE type) {
        if (type.hasMethod("getPropertyChangeSupport", new TypeRef[0])) {
            return;
        }
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)lombok.ast.AST.MethodDecl(lombok.ast.AST.Type(PropertyChangeSupport.class), "getPropertyChangeSupport").makePrivate()).withStatement(lombok.ast.AST.If(lombok.ast.AST.Equal(lombok.ast.AST.Field("$propertyChangeSupport"), lombok.ast.AST.Null())).Then(lombok.ast.AST.Block().withStatement(lombok.ast.AST.Synchronized(lombok.ast.AST.Field("$propertyChangeSupportLock")).withStatement(lombok.ast.AST.If(lombok.ast.AST.Equal(lombok.ast.AST.Field("$propertyChangeSupport"), lombok.ast.AST.Null())).Then(lombok.ast.AST.Block().withStatement(lombok.ast.AST.Assign(lombok.ast.AST.Field("$propertyChangeSupport"), lombok.ast.AST.New(lombok.ast.AST.Type(PropertyChangeSupport.class)).withArgument(lombok.ast.AST.This()))))))))).withStatement(lombok.ast.AST.Return(lombok.ast.AST.Field("$propertyChangeSupport"))));
    }

    private void generatePropertyChangeListenerMethods(TYPE_TYPE type) {
        for (String methodName : PROPERTY_CHANGE_METHOD_NAMES) {
            this.generatePropertyChangeListenerMethod(methodName, type);
        }
    }

    private void generatePropertyChangeListenerMethod(String methodName, TYPE_TYPE type) {
        if (type.hasMethod(methodName, lombok.ast.AST.Type(PropertyChangeListener.class))) {
            return;
        }
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)lombok.ast.AST.MethodDecl(lombok.ast.AST.Type("void"), methodName).makePublic()).withArgument(lombok.ast.AST.Arg(lombok.ast.AST.Type(PropertyChangeListener.class), "listener"))).withStatement(lombok.ast.AST.Call(lombok.ast.AST.Call("getPropertyChangeSupport"), methodName).withArgument(lombok.ast.AST.Name("listener"))));
    }

    private void generateFirePropertyChangeMethod(TYPE_TYPE type) {
        if (type.hasMethod("firePropertyChange", lombok.ast.AST.Type(String.class), lombok.ast.AST.Type(Object.class), lombok.ast.AST.Type(Object.class))) {
            return;
        }
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)lombok.ast.AST.MethodDecl(lombok.ast.AST.Type("void"), "firePropertyChange").makePublic()).withArgument(lombok.ast.AST.Arg(lombok.ast.AST.Type(String.class), "propertyName"))).withArgument(lombok.ast.AST.Arg(lombok.ast.AST.Type(Object.class), "oldValue"))).withArgument(lombok.ast.AST.Arg(lombok.ast.AST.Type(Object.class), "newValue"))).withStatement(lombok.ast.AST.Call(lombok.ast.AST.Call("getPropertyChangeSupport"), "firePropertyChange").withArgument(lombok.ast.AST.Name("propertyName")).withArgument(lombok.ast.AST.Name("oldValue")).withArgument(lombok.ast.AST.Name("newValue"))));
    }

    private void generateVetoableChangeSupportFields(TYPE_TYPE type) {
        if (!type.hasField("$vetoableChangeSupport")) {
            type.editor().injectField(lombok.ast.AST.FieldDecl(lombok.ast.AST.Type(VetoableChangeSupport.class), "$vetoableChangeSupport").makePrivate().makeTransient().makeVolatile());
        }
        if (!type.hasField("$vetoableChangeSupportLock")) {
            type.editor().injectField(((FieldDecl)lombok.ast.AST.FieldDecl(lombok.ast.AST.Type(Object.class).withDimensions(1), "$vetoableChangeSupportLock").makePrivate().makeFinal()).withInitialization(lombok.ast.AST.NewArray(lombok.ast.AST.Type(Object.class)).withDimensionExpression(lombok.ast.AST.Number(0))));
        }
    }

    private void generateGetVetoableSupportMethod(TYPE_TYPE type) {
        if (type.hasMethod("getVetoableChangeSupport", new TypeRef[0])) {
            return;
        }
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)lombok.ast.AST.MethodDecl(lombok.ast.AST.Type(VetoableChangeSupport.class), "getVetoableChangeSupport").makePrivate()).withStatement(lombok.ast.AST.If(lombok.ast.AST.Equal(lombok.ast.AST.Field("$vetoableChangeSupport"), lombok.ast.AST.Null())).Then(lombok.ast.AST.Block().withStatement(lombok.ast.AST.Synchronized(lombok.ast.AST.Field("$vetoableChangeSupportLock")).withStatement(lombok.ast.AST.If(lombok.ast.AST.Equal(lombok.ast.AST.Field("$vetoableChangeSupport"), lombok.ast.AST.Null())).Then(lombok.ast.AST.Block().withStatement(lombok.ast.AST.Assign(lombok.ast.AST.Field("$vetoableChangeSupport"), lombok.ast.AST.New(lombok.ast.AST.Type(VetoableChangeSupport.class)).withArgument(lombok.ast.AST.This()))))))))).withStatement(lombok.ast.AST.Return(lombok.ast.AST.Field("$vetoableChangeSupport"))));
    }

    private void generateVetoableChangeListenerMethods(TYPE_TYPE type) {
        for (String methodName : VETOABLE_CHANGE_METHOD_NAMES) {
            this.generateVetoableChangeListenerMethod(methodName, type);
        }
    }

    private void generateVetoableChangeListenerMethod(String methodName, TYPE_TYPE type) {
        if (type.hasMethod(methodName, lombok.ast.AST.Type(VetoableChangeListener.class))) {
            return;
        }
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)lombok.ast.AST.MethodDecl(lombok.ast.AST.Type("void"), methodName).makePublic()).withArgument(lombok.ast.AST.Arg(lombok.ast.AST.Type(VetoableChangeListener.class), "listener"))).withStatement(lombok.ast.AST.Call(lombok.ast.AST.Call("getVetoableChangeSupport"), methodName).withArgument(lombok.ast.AST.Name("listener"))));
    }

    private void generateFireVetoableChangeMethod(TYPE_TYPE type) {
        if (type.hasMethod("fireVetoableChange", lombok.ast.AST.Type(String.class), lombok.ast.AST.Type(Object.class), lombok.ast.AST.Type(Object.class))) {
            return;
        }
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)lombok.ast.AST.MethodDecl(lombok.ast.AST.Type("void"), "fireVetoableChange").makePublic()).withThrownException(lombok.ast.AST.Type(PropertyVetoException.class))).withArgument(lombok.ast.AST.Arg(lombok.ast.AST.Type(String.class), "propertyName"))).withArgument(lombok.ast.AST.Arg(lombok.ast.AST.Type(Object.class), "oldValue"))).withArgument(lombok.ast.AST.Arg(lombok.ast.AST.Type(Object.class), "newValue"))).withStatement(lombok.ast.AST.Call(lombok.ast.AST.Call("getVetoableChangeSupport"), "fireVetoableChange").withArgument(lombok.ast.AST.Name("propertyName")).withArgument(lombok.ast.AST.Name("oldValue")).withArgument(lombok.ast.AST.Name("newValue"))));
    }

    @ConstructorProperties(value={"annotationNode", "ast"})
    public BoundSetterHandler(LOMBOK_NODE_TYPE annotationNode, SOURCE_TYPE ast) {
        this.annotationNode = annotationNode;
        this.ast = ast;
    }
}

