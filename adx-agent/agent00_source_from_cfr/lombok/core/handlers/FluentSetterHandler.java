/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.FluentSetter;
import lombok.ast.Annotation;
import lombok.ast.Argument;
import lombok.ast.Expression;
import lombok.ast.IField;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.If;
import lombok.ast.MethodDecl;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.ast.TypeRef;
import lombok.core.AST;
import lombok.core.LombokNode;
import lombok.core.TransformationsUtil;
import lombok.core.util.ErrorMessages;

public abstract class FluentSetterHandler<TYPE_TYPE extends IType<?, FIELD_TYPE, ?, ?, ?, ?>, FIELD_TYPE extends IField<?, ?, ?>, LOMBOK_NODE_TYPE extends LombokNode<?, LOMBOK_NODE_TYPE, ?>, SOURCE_TYPE> {
    private static final Pattern SETTER_PATTERN = Pattern.compile("^(?:setter|fluentsetter|boundsetter)$", 2);
    private final LOMBOK_NODE_TYPE annotationNode;
    private final SOURCE_TYPE ast;

    public void handle(AccessLevel level) {
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
            this.annotationNode.addError(ErrorMessages.canBeUsedOnClassAndFieldOnly(FluentSetter.class));
            return;
        }
        this.generateSetter(type, (FIELD_TYPE)fields, level);
    }

    protected abstract TYPE_TYPE typeOf(LOMBOK_NODE_TYPE var1, SOURCE_TYPE var2);

    protected abstract FIELD_TYPE fieldOf(LOMBOK_NODE_TYPE var1, SOURCE_TYPE var2);

    private void generateSetter(TYPE_TYPE type, List<FIELD_TYPE> fields, AccessLevel level) {
        for (IField field : fields) {
            this.generateSetter(type, field, level);
        }
    }

    private void generateSetter(TYPE_TYPE type, FIELD_TYPE field, AccessLevel level) {
        TypeRef fieldType;
        String fieldName = field.name();
        if (type.hasMethod(fieldName, fieldType = field.type())) {
            return;
        }
        List<Annotation> nonNulls = field.annotations(TransformationsUtil.NON_NULL_PATTERN);
        List<Annotation> nullables = field.annotations(TransformationsUtil.NULLABLE_PATTERN);
        MethodDecl methodDecl = (MethodDecl)((MethodDecl)lombok.ast.AST.MethodDecl(lombok.ast.AST.Type(type.name()).withTypeArguments(type.typeArguments()), fieldName).withAccessLevel(level)).withArgument((Argument)((Argument)lombok.ast.AST.Arg(fieldType, fieldName).withAnnotations(nonNulls)).withAnnotations(nullables));
        if (!nonNulls.isEmpty() && !field.isPrimitive()) {
            methodDecl.withStatement(lombok.ast.AST.If(lombok.ast.AST.Equal(lombok.ast.AST.Name(fieldName), lombok.ast.AST.Null())).Then(lombok.ast.AST.Throw(lombok.ast.AST.New(lombok.ast.AST.Type(NullPointerException.class)).withArgument(lombok.ast.AST.String(fieldName)))));
        }
        ((MethodDecl)methodDecl.withStatement(lombok.ast.AST.Assign(lombok.ast.AST.Field(fieldName), lombok.ast.AST.Name(fieldName)))).withStatement(lombok.ast.AST.Return(lombok.ast.AST.This()));
        type.editor().injectMethod(methodDecl);
    }

    @ConstructorProperties(value={"annotationNode", "ast"})
    public FluentSetterHandler(LOMBOK_NODE_TYPE annotationNode, SOURCE_TYPE ast) {
        this.annotationNode = annotationNode;
        this.ast = ast;
    }
}

