/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.beans.ConstructorProperties;
import lombok.AccessLevel;
import lombok.LazyGetter;
import lombok.ast.AST;
import lombok.ast.Block;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.IField;
import lombok.ast.IFieldEditor;
import lombok.ast.IMethod;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.If;
import lombok.ast.MethodDecl;
import lombok.ast.NewArray;
import lombok.ast.Statement;
import lombok.ast.Synchronized;
import lombok.ast.TypeRef;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.TransformationsUtil;
import lombok.core.util.ErrorMessages;
import lombok.experimental.Accessors;

public class LazyGetterHandler<TYPE_TYPE extends IType<? extends IMethod<TYPE_TYPE, ?, ?, ?>, ?, ?, ?, ?, ?>, FIELD_TYPE extends IField<?, ?, ?>> {
    private final TYPE_TYPE type;
    private final FIELD_TYPE field;
    private final DiagnosticsReceiver diagnosticsReceiver;

    public void handle(AccessLevel level) {
        if (this.field == null) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnFieldOnly(LazyGetter.class));
            return;
        }
        if (!this.field.isFinal() && !this.field.isPrivate()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnPrivateFinalFieldOnly(LazyGetter.class));
            return;
        }
        if (!this.field.isInitialized()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnInitializedFieldOnly(LazyGetter.class));
            return;
        }
        String fieldName = this.field.name();
        boolean isBoolean = this.field.isOfType("boolean");
        AnnotationValues<Accessors> accessors = AnnotationValues.of(Accessors.class, this.field.node());
        String methodName = TransformationsUtil.toGetterName(accessors, fieldName, isBoolean);
        for (String altName : TransformationsUtil.toAllGetterNames(accessors, fieldName, isBoolean)) {
            if (!this.type.hasMethod(altName, new TypeRef[0])) continue;
            return;
        }
        this.createGetter(this.type, this.field, level, methodName);
    }

    private void createGetter(TYPE_TYPE type, FIELD_TYPE field, AccessLevel level, String methodName) {
        String fieldName = field.name();
        String initializedFieldName = "$" + fieldName + "Initialized";
        String lockFieldName = "$" + fieldName + "Lock";
        type.editor().injectField(AST.FieldDecl(AST.Type("boolean"), initializedFieldName).makePrivate().makeVolatile());
        type.editor().injectField(((FieldDecl)AST.FieldDecl(AST.Type(Object.class).withDimensions(1), lockFieldName).makePrivate().makeFinal()).withInitialization(AST.NewArray(AST.Type(Object.class)).withDimensionExpression(AST.Number(0))));
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(field.type(), methodName).withAccessLevel(level)).withStatement(AST.If(AST.Not(AST.Field(initializedFieldName))).Then(AST.Block().withStatement(AST.Synchronized(AST.Field(lockFieldName)).withStatement(AST.If(AST.Not(AST.Field(initializedFieldName))).Then(AST.Block().withStatement(AST.Assign(AST.Field(fieldName), field.initialization())).withStatement(AST.Assign(AST.Field(initializedFieldName), AST.True())))))))).withStatement(AST.Return(AST.Field(fieldName))));
        field.editor().replaceInitialization(null);
        field.editor().makeNonFinal();
    }

    @ConstructorProperties(value={"type", "field", "diagnosticsReceiver"})
    public LazyGetterHandler(TYPE_TYPE type, FIELD_TYPE field, DiagnosticsReceiver diagnosticsReceiver) {
        this.type = type;
        this.field = field;
        this.diagnosticsReceiver = diagnosticsReceiver;
    }
}

