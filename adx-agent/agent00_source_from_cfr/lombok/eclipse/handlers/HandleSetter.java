/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.CompilationResult
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.Assignment
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 */
package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.TransformationsUtil;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HandleSetter
extends EclipseAnnotationHandler<Setter> {
    public boolean generateSetterForType(EclipseNode typeNode, EclipseNode pos, AccessLevel level, boolean checkForTypeLevelSetter) {
        boolean notAClass;
        if (checkForTypeLevelSetter && typeNode != null) {
            for (EclipseNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(Setter.class, child)) continue;
                return true;
            }
        }
        TypeDeclaration typeDecl = null;
        if (typeNode.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)typeNode.get();
        }
        int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
        boolean bl = notAClass = (modifiers & 25088) != 0;
        if (typeDecl == null || notAClass) {
            pos.addError("@Setter is only supported on a class or a field.");
            return false;
        }
        for (EclipseNode field : typeNode.down()) {
            FieldDeclaration fieldDecl;
            if (field.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)field.get()) || (fieldDecl.modifiers & 16) != 0) continue;
            this.generateSetterForField(field, (ASTNode)pos.get(), level);
        }
        return true;
    }

    public void generateSetterForField(EclipseNode fieldNode, ASTNode pos, AccessLevel level) {
        for (EclipseNode child : fieldNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(Setter.class, child)) continue;
            return;
        }
        this.createSetterForField(level, fieldNode, fieldNode, pos, false);
    }

    @Override
    public void handle(AnnotationValues<Setter> annotation, Annotation ast, EclipseNode annotationNode) {
        EclipseNode node = (EclipseNode)annotationNode.up();
        AccessLevel level = annotation.getInstance().value();
        if (level == AccessLevel.NONE || node == null) {
            return;
        }
        switch (node.getKind()) {
            case FIELD: {
                this.createSetterForFields(level, annotationNode.upFromAnnotationToFields(), annotationNode, (ASTNode)annotationNode.get(), true);
                break;
            }
            case TYPE: {
                this.generateSetterForType(node, annotationNode, level, false);
            }
        }
    }

    private void createSetterForFields(AccessLevel level, Collection<EclipseNode> fieldNodes, EclipseNode errorNode, ASTNode source, boolean whineIfExists) {
        for (EclipseNode fieldNode : fieldNodes) {
            this.createSetterForField(level, fieldNode, errorNode, source, whineIfExists);
        }
    }

    private void createSetterForField(AccessLevel level, EclipseNode fieldNode, EclipseNode errorNode, ASTNode source, boolean whineIfExists) {
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            errorNode.addError("@Setter is only supported on a class or a field.");
            return;
        }
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        TypeReference fieldType = EclipseHandlerUtil.copyType(field.type, source);
        boolean isBoolean = Eclipse.nameEquals(fieldType.getTypeName(), "boolean") && fieldType.dimensions() == 0;
        String setterName = EclipseHandlerUtil.toSetterName(fieldNode, isBoolean);
        boolean shouldReturnThis = EclipseHandlerUtil.shouldReturnThis(fieldNode);
        if (setterName == null) {
            errorNode.addWarning("Not generating setter for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        int modifier = EclipseHandlerUtil.toEclipseModifier(level) | field.modifiers & 8;
        for (String altName : EclipseHandlerUtil.toAllSetterNames(fieldNode, isBoolean)) {
            switch (EclipseHandlerUtil.methodExists(altName, fieldNode, false, 1)) {
                case EXISTS_BY_LOMBOK: {
                    return;
                }
                case EXISTS_BY_USER: {
                    if (whineIfExists) {
                        String altNameExpl = "";
                        if (!altName.equals(setterName)) {
                            altNameExpl = String.format(" (%s)", altName);
                        }
                        errorNode.addWarning(String.format("Not generating %s(): A method with that name already exists%s", setterName, altNameExpl));
                    }
                    return;
                }
            }
        }
        MethodDeclaration method = this.generateSetter((TypeDeclaration)((EclipseNode)fieldNode.up()).get(), fieldNode, setterName, shouldReturnThis, modifier, source);
        EclipseHandlerUtil.injectMethod((EclipseNode)fieldNode.up(), (AbstractMethodDeclaration)method);
    }

    private MethodDeclaration generateSetter(TypeDeclaration parent, EclipseNode fieldNode, String name, boolean shouldReturnThis, int modifier, ASTNode source) {
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        MethodDeclaration method = new MethodDeclaration(parent.compilationResult);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method, source);
        method.modifiers = modifier;
        if (shouldReturnThis) {
            EclipseNode type;
            for (type = fieldNode; type != null && type.getKind() != AST.Kind.TYPE; type = (EclipseNode)type.up()) {
            }
            if (type != null && type.get() instanceof TypeDeclaration) {
                TypeDeclaration typeDecl = (TypeDeclaration)type.get();
                if (typeDecl.typeParameters != null && typeDecl.typeParameters.length > 0) {
                    TypeReference[] refs = new TypeReference[typeDecl.typeParameters.length];
                    int idx = 0;
                    for (TypeParameter param : typeDecl.typeParameters) {
                        SingleTypeReference typeRef = new SingleTypeReference(param.name, (long)param.sourceStart << 32 | (long)param.sourceEnd);
                        EclipseHandlerUtil.setGeneratedBy((ASTNode)typeRef, source);
                        refs[idx++] = typeRef;
                    }
                    method.returnType = new ParameterizedSingleTypeReference(typeDecl.name, refs, 0, p);
                } else {
                    method.returnType = new SingleTypeReference(((TypeDeclaration)type.get()).name, p);
                }
            }
        }
        if (method.returnType == null) {
            method.returnType = TypeReference.baseTypeReference((int)6, (int)0);
            method.returnType.sourceStart = pS;
            method.returnType.sourceEnd = pE;
            shouldReturnThis = false;
        }
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method.returnType, source);
        if (EclipseHandlerUtil.isFieldDeprecated(fieldNode)) {
            method.annotations = new Annotation[]{EclipseHandlerUtil.generateDeprecatedAnnotation(source)};
        }
        Argument param = new Argument(field.name, p, EclipseHandlerUtil.copyType(field.type, source), 16);
        param.sourceStart = pS;
        param.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)param, source);
        method.arguments = new Argument[]{param};
        method.selector = name.toCharArray();
        method.binding = null;
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 8388608;
        Expression fieldRef = EclipseHandlerUtil.createFieldAccessor(fieldNode, EclipseHandlerUtil.FieldAccess.ALWAYS_FIELD, source);
        SingleNameReference fieldNameRef = new SingleNameReference(field.name, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldNameRef, source);
        Assignment assignment = new Assignment(fieldRef, (Expression)fieldNameRef, (int)p);
        assignment.sourceStart = pS;
        assignment.sourceEnd = assignment.statementEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)assignment, source);
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        Annotation[] nonNulls = Eclipse.findAnnotations(field, TransformationsUtil.NON_NULL_PATTERN);
        Annotation[] nullables = Eclipse.findAnnotations(field, TransformationsUtil.NULLABLE_PATTERN);
        ArrayList<Object> statements = new ArrayList<Object>(5);
        if (nonNulls.length == 0) {
            statements.add((Object)assignment);
        } else {
            Statement nullCheck = EclipseHandlerUtil.generateNullCheck((AbstractVariableDeclaration)field, source);
            if (nullCheck != null) {
                statements.add((Object)nullCheck);
            }
            statements.add((Object)assignment);
        }
        if (shouldReturnThis) {
            ThisReference thisRef = new ThisReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)thisRef, source);
            ReturnStatement returnThis = new ReturnStatement((Expression)thisRef, pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)returnThis, source);
            statements.add((Object)returnThis);
        }
        method.statements = statements.toArray((T[])new Statement[0]);
        Annotation[] copiedAnnotations = EclipseHandlerUtil.copyAnnotations(source, nonNulls, nullables);
        if (copiedAnnotations.length != 0) {
            param.annotations = copiedAnnotations;
        }
        return method;
    }

}

