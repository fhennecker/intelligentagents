/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.EnumId;
import lombok.ast.AST;
import lombok.ast.Argument;
import lombok.ast.Block;
import lombok.ast.Call;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.Foreach;
import lombok.ast.IField;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.If;
import lombok.ast.Initializer;
import lombok.ast.MethodDecl;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.ast.TypeRef;
import lombok.core.DiagnosticsReceiver;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Names;

public class EnumIdHandler<TYPE_TYPE extends IType<?, ?, ?, ?, ?, ?>, FIELD_TYPE extends IField<?, ?, ?>> {
    private final TYPE_TYPE type;
    private final FIELD_TYPE field;
    private final DiagnosticsReceiver diagnosticsReceiver;

    public void handle() {
        if (!this.type.isEnum()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnEnumFieldsOnly(EnumId.class));
            return;
        }
        String fieldName = this.field.name();
        String lookupFieldName = "$" + Names.camelCaseToConstant(Names.camelCase(fieldName, "lookup"));
        String foreachVarName = Names.decapitalize(this.type.name());
        String exceptionText = "Enumeration '" + this.type.name() + "' has no value for '" + fieldName + " = %s'";
        this.type.editor().injectField(((FieldDecl)AST.FieldDecl(AST.Type(Map.class).withTypeArgument(this.field.boxedType()).withTypeArgument(AST.Type(this.type.name())), lookupFieldName).makePrivate().makeStatic().makeFinal()).withInitialization(AST.New(AST.Type(HashMap.class).withTypeArgument(this.field.boxedType()).withTypeArgument(AST.Type(this.type.name())))));
        this.type.editor().injectInitializer(AST.Initializer().makeStatic().withStatement(AST.Foreach(AST.LocalDecl(AST.Type(this.type.name()), foreachVarName)).In(AST.Call(AST.Name(this.type.name()), "values")).Do(AST.Block().withStatement(AST.Call(AST.Name(lookupFieldName), "put").withArgument(AST.Field(AST.Name(foreachVarName), fieldName)).withArgument(AST.Name(foreachVarName))))));
        this.type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type(this.type.name()), Names.camelCase("find", "by", fieldName)).makePublic()).makeStatic()).withArgument(AST.Arg(this.field.type(), fieldName))).withStatement(AST.If(AST.Call(AST.Name(lookupFieldName), "containsKey").withArgument(AST.Name(fieldName))).Then(AST.Block().withStatement(AST.Return(AST.Call(AST.Name(lookupFieldName), "get").withArgument(AST.Name(fieldName))))))).withStatement(AST.Throw(AST.New(AST.Type(IllegalArgumentException.class)).withArgument(AST.Call(AST.Name(String.class), "format").withArgument(AST.String(exceptionText)).withArgument(AST.Name(fieldName))))));
    }

    @ConstructorProperties(value={"type", "field", "diagnosticsReceiver"})
    public EnumIdHandler(TYPE_TYPE type, FIELD_TYPE field, DiagnosticsReceiver diagnosticsReceiver) {
        this.type = type;
        this.field = field;
        this.diagnosticsReceiver = diagnosticsReceiver;
    }
}

