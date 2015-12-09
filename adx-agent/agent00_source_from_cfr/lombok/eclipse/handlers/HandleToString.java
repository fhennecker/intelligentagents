/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.CompilationResult
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.BinaryExpression
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.NameReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.StringLiteral
 *  org.eclipse.jdt.internal.compiler.ast.SuperReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.TypeConstants
 */
package lombok.eclipse.handlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.ToString;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HandleToString
extends EclipseAnnotationHandler<ToString> {
    private static final Set<String> BUILT_IN_TYPES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("byte", "short", "int", "long", "char", "boolean", "double", "float")));

    private void checkForBogusFieldNames(EclipseNode type, AnnotationValues<ToString> annotation) {
        Iterator<Integer> i$;
        int i;
        if (annotation.isExplicit("exclude")) {
            i$ = EclipseHandlerUtil.createListOfNonExistentFields(Arrays.asList(annotation.getInstance().exclude()), type, true, false).iterator();
            while (i$.hasNext()) {
                i = i$.next();
                annotation.setWarning("exclude", "This field does not exist, or would have been excluded anyway.", i);
            }
        }
        if (annotation.isExplicit("of")) {
            i$ = EclipseHandlerUtil.createListOfNonExistentFields(Arrays.asList(annotation.getInstance().of()), type, false, false).iterator();
            while (i$.hasNext()) {
                i = i$.next();
                annotation.setWarning("of", "This field does not exist.", i);
            }
        }
    }

    public void generateToStringForType(EclipseNode typeNode, EclipseNode errorNode, Boolean callSuper) {
        for (EclipseNode child : typeNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(ToString.class, child)) continue;
            return;
        }
        boolean includeFieldNames = true;
        try {
            includeFieldNames = (Boolean)ToString.class.getMethod("includeFieldNames", new Class[0]).getDefaultValue();
        }
        catch (Exception ignore) {
            // empty catch block
        }
        this.generateToString(typeNode, errorNode, null, null, includeFieldNames, callSuper, false, EclipseHandlerUtil.FieldAccess.GETTER);
    }

    @Override
    public void handle(AnnotationValues<ToString> annotation, Annotation ast, EclipseNode annotationNode) {
        ToString ann = annotation.getInstance();
        List<String> excludes = Arrays.asList(ann.exclude());
        List<String> includes = Arrays.asList(ann.of());
        EclipseNode typeNode = (EclipseNode)annotationNode.up();
        Boolean callSuper = ann.callSuper();
        if (!annotation.isExplicit("callSuper")) {
            callSuper = null;
        }
        if (!annotation.isExplicit("exclude")) {
            excludes = null;
        }
        if (!annotation.isExplicit("of")) {
            includes = null;
        }
        if (excludes != null && includes != null) {
            excludes = null;
            annotation.setWarning("exclude", "exclude and of are mutually exclusive; the 'exclude' parameter will be ignored.");
        }
        this.checkForBogusFieldNames(typeNode, annotation);
        EclipseHandlerUtil.FieldAccess fieldAccess = ann.doNotUseGetters() ? EclipseHandlerUtil.FieldAccess.PREFER_FIELD : EclipseHandlerUtil.FieldAccess.GETTER;
        this.generateToString(typeNode, annotationNode, excludes, includes, ann.includeFieldNames(), callSuper, true, fieldAccess);
    }

    public void generateToString(EclipseNode typeNode, EclipseNode errorNode, List<String> excludes, List<String> includes, boolean includeFieldNames, Boolean callSuper, boolean whineIfExists, EclipseHandlerUtil.FieldAccess fieldAccess) {
        boolean notAClass;
        TypeDeclaration typeDecl = null;
        if (typeNode.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)typeNode.get();
        }
        int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
        boolean bl = notAClass = (modifiers & 8704) != 0;
        if (typeDecl == null || notAClass) {
            errorNode.addError("@ToString is only supported on a class or enum.");
        }
        if (callSuper == null) {
            try {
                callSuper = (boolean)((Boolean)ToString.class.getMethod("callSuper", new Class[0]).getDefaultValue());
            }
            catch (Exception ignore) {
                // empty catch block
            }
        }
        ArrayList<EclipseNode> nodesForToString = new ArrayList<EclipseNode>();
        if (includes != null) {
            for (EclipseNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.FIELD) continue;
                FieldDeclaration fieldDecl = (FieldDeclaration)child.get();
                if (!includes.contains(new String(fieldDecl.name))) continue;
                nodesForToString.add(child);
            }
        } else {
            for (EclipseNode child : typeNode.down()) {
                FieldDeclaration fieldDecl;
                if (child.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)child.get()) || excludes != null && excludes.contains(new String(fieldDecl.name))) continue;
                nodesForToString.add(child);
            }
        }
        switch (EclipseHandlerUtil.methodExists("toString", typeNode, 0)) {
            case NOT_EXISTS: {
                MethodDeclaration toString = this.createToString(typeNode, nodesForToString, includeFieldNames, callSuper, (ASTNode)errorNode.get(), fieldAccess);
                EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)toString);
                break;
            }
            case EXISTS_BY_LOMBOK: {
                break;
            }
            default: {
                if (!whineIfExists) break;
                errorNode.addWarning("Not generating toString(): A method with that name already exists");
            }
        }
    }

    private MethodDeclaration createToString(EclipseNode type, Collection<EclipseNode> fields, boolean includeFieldNames, boolean callSuper, ASTNode source, EclipseHandlerUtil.FieldAccess fieldAccess) {
        String typeName = this.getTypeName(type);
        char[] suffix = ")".toCharArray();
        String infixS = ", ";
        char[] infix = infixS.toCharArray();
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        int PLUS = 14;
        char[] prefix = callSuper ? (typeName + "(super=").toCharArray() : (fields.isEmpty() ? (typeName + "()").toCharArray() : (includeFieldNames ? (typeName + "(" + new String(((FieldDeclaration)fields.iterator().next().get()).name) + "=").toCharArray() : (typeName + "(").toCharArray()));
        boolean first = true;
        StringLiteral current = new StringLiteral(prefix, pS, pE, 0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)current, source);
        if (callSuper) {
            MessageSend callToSuper = new MessageSend();
            callToSuper.sourceStart = pS;
            callToSuper.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)callToSuper, source);
            callToSuper.receiver = new SuperReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)callToSuper, source);
            callToSuper.selector = "toString".toCharArray();
            current = new BinaryExpression((Expression)current, (Expression)callToSuper, 14);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)current, source);
            first = false;
        }
        for (EclipseNode field : fields) {
            StringLiteral fieldNameLiteral;
            Expression ex;
            TypeReference fType = EclipseHandlerUtil.getFieldType(field, fieldAccess);
            Expression fieldAccessor = EclipseHandlerUtil.createFieldAccessor(field, fieldAccess, source);
            if (fType.dimensions() > 0) {
                MessageSend arrayToString = new MessageSend();
                arrayToString.sourceStart = pS;
                arrayToString.sourceEnd = pE;
                arrayToString.receiver = this.generateQualifiedNameRef(source, TypeConstants.JAVA, TypeConstants.UTIL, "Arrays".toCharArray());
                arrayToString.arguments = new Expression[]{fieldAccessor};
                EclipseHandlerUtil.setGeneratedBy((ASTNode)arrayToString.arguments[0], source);
                arrayToString.selector = fType.dimensions() > 1 || !BUILT_IN_TYPES.contains(new String(fType.getLastToken())) ? "deepToString".toCharArray() : "toString".toCharArray();
                ex = arrayToString;
            } else {
                ex = fieldAccessor;
            }
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ex, source);
            if (first) {
                current = new BinaryExpression((Expression)current, ex, 14);
                current.sourceStart = pS;
                current.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy((ASTNode)current, source);
                first = false;
                continue;
            }
            if (includeFieldNames) {
                char[] namePlusEqualsSign = (infixS + field.getName() + "=").toCharArray();
                fieldNameLiteral = new StringLiteral(namePlusEqualsSign, pS, pE, 0);
            } else {
                fieldNameLiteral = new StringLiteral(infix, pS, pE, 0);
            }
            EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldNameLiteral, source);
            current = new BinaryExpression((Expression)current, (Expression)fieldNameLiteral, 14);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)current, source);
            current = new BinaryExpression((Expression)current, ex, 14);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)current, source);
        }
        if (!first) {
            StringLiteral suffixLiteral = new StringLiteral(suffix, pS, pE, 0);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)suffixLiteral, source);
            current = new BinaryExpression((Expression)current, (Expression)suffixLiteral, 14);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)current, source);
        }
        ReturnStatement returnStatement = new ReturnStatement((Expression)current, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)returnStatement, source);
        MethodDeclaration method = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method, source);
        method.modifiers = EclipseHandlerUtil.toEclipseModifier(AccessLevel.PUBLIC);
        method.returnType = new QualifiedTypeReference(TypeConstants.JAVA_LANG_STRING, new long[]{p, p, p});
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method.returnType, source);
        method.annotations = new Annotation[]{EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, source)};
        method.arguments = null;
        method.selector = "toString".toCharArray();
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 8388608;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        method.statements = new Statement[]{returnStatement};
        return method;
    }

    private String getTypeName(EclipseNode type) {
        String typeName = this.getSingleTypeName(type);
        for (EclipseNode upType = (EclipseNode)type.up(); upType != null && upType.getKind() == AST.Kind.TYPE; upType = (EclipseNode)upType.up()) {
            typeName = this.getSingleTypeName(upType) + "." + typeName;
        }
        return typeName;
    }

    private String getSingleTypeName(EclipseNode type) {
        TypeDeclaration typeDeclaration = (TypeDeclaration)type.get();
        char[] rawTypeName = typeDeclaration.name;
        return rawTypeName == null ? "" : new String(rawTypeName);
    }

    private /* varargs */ NameReference generateQualifiedNameRef(ASTNode source, char[] ... varNames) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        SingleNameReference ref = varNames.length > 1 ? new QualifiedNameReference(varNames, new long[varNames.length], pS, pE) : new SingleNameReference(varNames[0], p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ref, source);
        return ref;
    }

}

