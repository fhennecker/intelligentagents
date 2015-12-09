/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Javadoc
 *  org.eclipse.jdt.internal.compiler.ast.MemberValuePair
 *  org.eclipse.jdt.internal.compiler.ast.NormalAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse.handlers.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.ast.AST;
import lombok.ast.Annotation;
import lombok.ast.Expression;
import lombok.ast.IField;
import lombok.ast.IFieldEditor;
import lombok.ast.TypeRef;
import lombok.core.LombokNode;
import lombok.core.util.As;
import lombok.core.util.Each;
import lombok.core.util.Is;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseASTUtil;
import lombok.eclipse.handlers.ast.EclipseFieldEditor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public final class EclipseField
implements IField<EclipseNode, ASTNode, FieldDeclaration> {
    private final EclipseNode fieldNode;
    private final EclipseFieldEditor editor;

    private EclipseField(EclipseNode fieldNode, ASTNode source) {
        if (!(fieldNode.get() instanceof FieldDeclaration)) {
            throw new IllegalArgumentException();
        }
        this.fieldNode = fieldNode;
        this.editor = new EclipseFieldEditor(this, source);
    }

    public EclipseFieldEditor editor() {
        return this.editor;
    }

    @Override
    public boolean isPrivate() {
        return (this.get().modifiers & 2) != 0;
    }

    @Override
    public boolean isFinal() {
        return (this.get().modifiers & 16) != 0;
    }

    @Override
    public boolean isStatic() {
        return (this.get().modifiers & 8) != 0;
    }

    @Override
    public boolean isInitialized() {
        return this.get().initialization != null;
    }

    @Override
    public boolean isPrimitive() {
        return Eclipse.isPrimitive(this.get().type);
    }

    @Override
    public boolean hasJavaDoc() {
        return this.get().javadoc != null;
    }

    @Override
    public FieldDeclaration get() {
        return (FieldDeclaration)this.fieldNode.get();
    }

    @Override
    public EclipseNode node() {
        return this.fieldNode;
    }

    @Override
    public TypeRef type() {
        return AST.Type((Object)this.get().type);
    }

    @Override
    public TypeRef boxedType() {
        return EclipseASTUtil.boxedType(this.get().type);
    }

    @Override
    public boolean isOfType(String typeName) {
        TypeReference variableType = this.get().type;
        if (variableType == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (char[] elem : variableType.getTypeName()) {
            if (first) {
                first = false;
            } else {
                sb.append('.');
            }
            sb.append(elem);
        }
        String type = sb.toString();
        return type.endsWith(typeName);
    }

    @Override
    public String name() {
        return this.node().getName();
    }

    @Override
    public Expression<?> initialization() {
        return this.get().initialization == null ? null : AST.Expr((Object)this.get().initialization);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public List<TypeRef> typeArguments() {
        ArrayList<TypeRef> typeArguments = new ArrayList<TypeRef>();
        TypeReference type = this.get().type;
        if (type instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference typeRef = (ParameterizedQualifiedTypeReference)type;
            if (!Is.notEmpty((Object[])typeRef.typeArguments)) return typeArguments;
            for (TypeReference typeArgument : Each.elementIn(typeRef.typeArguments[typeRef.typeArguments.length - 1])) {
                typeArguments.add(AST.Type((Object)typeArgument));
            }
            return typeArguments;
        } else {
            if (!(type instanceof ParameterizedSingleTypeReference)) return typeArguments;
            ParameterizedSingleTypeReference typeRef = (ParameterizedSingleTypeReference)type;
            for (TypeReference typeArgument : Each.elementIn(typeRef.typeArguments)) {
                typeArguments.add(AST.Type((Object)typeArgument));
            }
        }
        return typeArguments;
    }

    @Override
    public List<Annotation> annotations() {
        return this.annotations(null);
    }

    @Override
    public List<Annotation> annotations(Pattern namePattern) {
        ArrayList<Annotation> result = new ArrayList<Annotation>();
        for (org.eclipse.jdt.internal.compiler.ast.Annotation annotation : Each.elementIn(this.get().annotations)) {
            TypeReference typeRef = annotation.type;
            char[][] typeName = typeRef.getTypeName();
            String suspect = As.string(typeName[typeName.length - 1]);
            if (namePattern != null && !namePattern.matcher(suspect).matches()) continue;
            Annotation ann = (Annotation)AST.Annotation(AST.Type((Object)annotation.type)).posHint((Object)annotation);
            if (annotation instanceof SingleMemberAnnotation) {
                ann.withValue(AST.Expr((Object)((SingleMemberAnnotation)annotation).memberValue));
            } else if (annotation instanceof NormalAnnotation) {
                for (MemberValuePair pair : Each.elementIn(((NormalAnnotation)annotation).memberValuePairs)) {
                    ann.withValue(As.string(pair.name), AST.Expr((Object)pair.value)).posHint((Object)pair);
                }
            }
            result.add(ann);
        }
        return result;
    }

    public String toString() {
        return this.get().toString();
    }

    public static EclipseField fieldOf(EclipseNode node, ASTNode source) {
        EclipseNode fieldNode;
        for (fieldNode = node; fieldNode != null && !(fieldNode.get() instanceof FieldDeclaration); fieldNode = (EclipseNode)fieldNode.up()) {
        }
        return fieldNode == null ? null : new EclipseField(fieldNode, source);
    }
}

