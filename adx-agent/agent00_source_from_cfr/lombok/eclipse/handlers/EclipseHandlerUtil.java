/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.ILog
 *  org.eclipse.core.runtime.IStatus
 *  org.eclipse.core.runtime.Platform
 *  org.eclipse.core.runtime.Status
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 *  org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.CastExpression
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.EqualExpression
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldReference
 *  org.eclipse.jdt.internal.compiler.ast.IfStatement
 *  org.eclipse.jdt.internal.compiler.ast.IntLiteral
 *  org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.MemberValuePair
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.NameReference
 *  org.eclipse.jdt.internal.compiler.ast.NormalAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.NullLiteral
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.StringLiteral
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.ThrowStatement
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Wildcard
 *  org.eclipse.jdt.internal.compiler.lookup.CaptureBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeConstants
 *  org.eclipse.jdt.internal.compiler.lookup.WildcardBinding
 *  org.osgi.framework.Bundle
 */
package lombok.eclipse.handlers;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Lombok;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.TransformationsUtil;
import lombok.core.TypeResolver;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.HandleGetter;
import lombok.experimental.Accessors;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.osgi.framework.Bundle;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EclipseHandlerUtil {
    private static final String DEFAULT_BUNDLE = "org.eclipse.jdt.core";
    private static Field generatedByField;
    private static Map<ASTNode, ASTNode> generatedNodes;
    private static final Map<FieldDeclaration, Object> generatedLazyGettersWithPrimitiveBoolean;
    private static final Object MARKER;
    private static final char[] ALL;
    private static final Constructor<CastExpression> castExpressionConstructor;
    private static final boolean castExpressionConstructorIsTypeRefBased;
    private static final Constructor<IntLiteral> intLiteralConstructor;
    private static final Method intLiteralFactoryMethod;
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY;

    private EclipseHandlerUtil() {
    }

    public static void error(CompilationUnitDeclaration cud, String message, Throwable error) {
        EclipseHandlerUtil.error(cud, message, null, error);
    }

    public static void error(CompilationUnitDeclaration cud, String message, String bundleName, Throwable error) {
        if (bundleName == null) {
            bundleName = "org.eclipse.jdt.core";
        }
        try {
            new EclipseWorkspaceLogger().error(message, bundleName, error);
        }
        catch (NoClassDefFoundError e) {
            new TerminalLogger().error(message, bundleName, error);
        }
        if (cud != null) {
            EclipseAST.addProblemToCompilationResult(cud, false, message + " - See error log.", 0, 0);
        }
    }

    public static void warning(String message, Throwable error) {
        EclipseHandlerUtil.warning(message, null, error);
    }

    public static void warning(String message, String bundleName, Throwable error) {
        if (bundleName == null) {
            bundleName = "org.eclipse.jdt.core";
        }
        try {
            new EclipseWorkspaceLogger().warning(message, bundleName, error);
        }
        catch (NoClassDefFoundError e) {
            new TerminalLogger().warning(message, bundleName, error);
        }
    }

    public static ASTNode getGeneratedBy(ASTNode node) {
        if (generatedByField != null) {
            try {
                return (ASTNode)generatedByField.get((Object)node);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        Map<ASTNode, ASTNode> e = generatedNodes;
        synchronized (e) {
            return generatedNodes.get((Object)node);
        }
    }

    public static boolean isGenerated(ASTNode node) {
        return EclipseHandlerUtil.getGeneratedBy(node) != null;
    }

    public static ASTNode setGeneratedBy(ASTNode node, ASTNode source) {
        if (generatedByField != null) {
            try {
                generatedByField.set((Object)node, (Object)source);
                return node;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        Map<ASTNode, ASTNode> e = generatedNodes;
        synchronized (e) {
            generatedNodes.put(node, source);
        }
        return node;
    }

    public static MarkerAnnotation generateDeprecatedAnnotation(ASTNode source) {
        QualifiedTypeReference qtr = new QualifiedTypeReference((char[][])new char[][]{{'j', 'a', 'v', 'a'}, {'l', 'a', 'n', 'g'}, {'D', 'e', 'p', 'r', 'e', 'c', 'a', 't', 'e', 'd'}}, Eclipse.poss(source, 3));
        EclipseHandlerUtil.setGeneratedBy((ASTNode)qtr, source);
        return new MarkerAnnotation((TypeReference)qtr, source.sourceStart);
    }

    public static boolean isFieldDeprecated(EclipseNode fieldNode) {
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        if ((field.modifiers & 1048576) != 0) {
            return true;
        }
        if (field.annotations == null) {
            return false;
        }
        for (Annotation annotation : field.annotations) {
            if (!EclipseHandlerUtil.typeMatches(Deprecated.class, fieldNode, annotation.type)) continue;
            return true;
        }
        return false;
    }

    public static boolean typeMatches(Class<?> type, EclipseNode node, TypeReference typeRef) {
        String lastPartB;
        if (typeRef == null || typeRef.getTypeName() == null || typeRef.getTypeName().length == 0) {
            return false;
        }
        String lastPartA = new String(typeRef.getTypeName()[typeRef.getTypeName().length - 1]);
        if (!lastPartA.equals(lastPartB = type.getSimpleName())) {
            return false;
        }
        String typeName = Eclipse.toQualifiedName(typeRef.getTypeName());
        TypeResolver resolver = new TypeResolver(node.getPackageDeclaration(), node.getImportStatements());
        return resolver.typeMatches(node, type.getName(), typeName);
    }

    public static Annotation copyAnnotation(Annotation annotation, ASTNode source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        if (annotation instanceof MarkerAnnotation) {
            MarkerAnnotation ann = new MarkerAnnotation(EclipseHandlerUtil.copyType(annotation.type, source), pS);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ann, source);
            ann.sourceEnd = ann.statementEnd = pE;
            ann.declarationSourceEnd = ann.statementEnd;
            return ann;
        }
        if (annotation instanceof SingleMemberAnnotation) {
            SingleMemberAnnotation ann = new SingleMemberAnnotation(EclipseHandlerUtil.copyType(annotation.type, source), pS);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ann, source);
            ann.sourceEnd = ann.statementEnd = pE;
            ann.declarationSourceEnd = ann.statementEnd;
            ann.memberValue = ((SingleMemberAnnotation)annotation).memberValue;
            return ann;
        }
        if (annotation instanceof NormalAnnotation) {
            NormalAnnotation ann = new NormalAnnotation(EclipseHandlerUtil.copyType(annotation.type, source), pS);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ann, source);
            ann.statementEnd = ann.sourceEnd = pE;
            ann.declarationSourceEnd = ann.sourceEnd;
            ann.memberValuePairs = ((NormalAnnotation)annotation).memberValuePairs;
            return ann;
        }
        return annotation;
    }

    public static TypeParameter[] copyTypeParams(TypeParameter[] params, ASTNode source) {
        if (params == null) {
            return null;
        }
        TypeParameter[] out = new TypeParameter[params.length];
        int idx = 0;
        for (TypeParameter param : params) {
            TypeParameter o = new TypeParameter();
            EclipseHandlerUtil.setGeneratedBy((ASTNode)o, source);
            o.annotations = param.annotations;
            o.bits = param.bits;
            o.modifiers = param.modifiers;
            o.name = param.name;
            o.type = EclipseHandlerUtil.copyType(param.type, source);
            o.sourceStart = param.sourceStart;
            o.sourceEnd = param.sourceEnd;
            o.declarationEnd = param.declarationEnd;
            o.declarationSourceStart = param.declarationSourceStart;
            o.declarationSourceEnd = param.declarationSourceEnd;
            if (param.bounds != null) {
                TypeReference[] b = new TypeReference[param.bounds.length];
                int idx2 = 0;
                for (TypeReference ref : param.bounds) {
                    b[idx2++] = EclipseHandlerUtil.copyType(ref, source);
                }
                o.bounds = b;
            }
            out[idx++] = o;
        }
        return out;
    }

    public static TypeReference[] copyTypes(TypeReference[] refs, ASTNode source) {
        if (refs == null) {
            return null;
        }
        TypeReference[] outs = new TypeReference[refs.length];
        int idx = 0;
        for (TypeReference ref : refs) {
            outs[idx++] = EclipseHandlerUtil.copyType(ref, source);
        }
        return outs;
    }

    public static TypeReference copyType(TypeReference ref, ASTNode source) {
        if (ref instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference iRef = (ParameterizedQualifiedTypeReference)ref;
            TypeReference[][] args = null;
            if (iRef.typeArguments != null) {
                args = new TypeReference[iRef.typeArguments.length][];
                int idx = 0;
                for (TypeReference[] inRefArray : iRef.typeArguments) {
                    if (inRefArray == null) {
                        args[idx++] = null;
                        continue;
                    }
                    TypeReference[] outRefArray = new TypeReference[inRefArray.length];
                    int idx2 = 0;
                    for (TypeReference inRef : inRefArray) {
                        outRefArray[idx2++] = EclipseHandlerUtil.copyType(inRef, source);
                    }
                    args[idx++] = outRefArray;
                }
            }
            ParameterizedQualifiedTypeReference typeRef = new ParameterizedQualifiedTypeReference(iRef.tokens, args, iRef.dimensions(), iRef.sourcePositions);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)typeRef, source);
            return typeRef;
        }
        if (ref instanceof ArrayQualifiedTypeReference) {
            ArrayQualifiedTypeReference iRef = (ArrayQualifiedTypeReference)ref;
            ArrayQualifiedTypeReference typeRef = new ArrayQualifiedTypeReference(iRef.tokens, iRef.dimensions(), iRef.sourcePositions);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)typeRef, source);
            return typeRef;
        }
        if (ref instanceof QualifiedTypeReference) {
            QualifiedTypeReference iRef = (QualifiedTypeReference)ref;
            QualifiedTypeReference typeRef = new QualifiedTypeReference(iRef.tokens, iRef.sourcePositions);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)typeRef, source);
            return typeRef;
        }
        if (ref instanceof ParameterizedSingleTypeReference) {
            ParameterizedSingleTypeReference iRef = (ParameterizedSingleTypeReference)ref;
            TypeReference[] args = null;
            if (iRef.typeArguments != null) {
                args = new TypeReference[iRef.typeArguments.length];
                int idx = 0;
                for (TypeReference inRef : iRef.typeArguments) {
                    args[idx++] = inRef == null ? null : EclipseHandlerUtil.copyType(inRef, source);
                }
            }
            ParameterizedSingleTypeReference typeRef = new ParameterizedSingleTypeReference(iRef.token, args, iRef.dimensions(), (long)iRef.sourceStart << 32 | (long)iRef.sourceEnd);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)typeRef, source);
            return typeRef;
        }
        if (ref instanceof ArrayTypeReference) {
            ArrayTypeReference iRef = (ArrayTypeReference)ref;
            ArrayTypeReference typeRef = new ArrayTypeReference(iRef.token, iRef.dimensions(), (long)iRef.sourceStart << 32 | (long)iRef.sourceEnd);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)typeRef, source);
            return typeRef;
        }
        if (ref instanceof Wildcard) {
            Wildcard original = (Wildcard)ref;
            Wildcard wildcard = new Wildcard(original.kind);
            wildcard.sourceStart = original.sourceStart;
            wildcard.sourceEnd = original.sourceEnd;
            if (original.bound != null) {
                wildcard.bound = EclipseHandlerUtil.copyType(original.bound, source);
            }
            EclipseHandlerUtil.setGeneratedBy((ASTNode)wildcard, source);
            return wildcard;
        }
        if (ref instanceof SingleTypeReference) {
            SingleTypeReference iRef = (SingleTypeReference)ref;
            SingleTypeReference typeRef = new SingleTypeReference(iRef.token, (long)iRef.sourceStart << 32 | (long)iRef.sourceEnd);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)typeRef, source);
            return typeRef;
        }
        return ref;
    }

    public static /* varargs */ Annotation[] copyAnnotations(ASTNode source, Annotation[] ... allAnnotations) {
        boolean allNull = true;
        ArrayList<Annotation> result = new ArrayList<Annotation>();
        for (Annotation[] annotations : allAnnotations) {
            if (annotations == null) continue;
            allNull = false;
            for (Annotation annotation : annotations) {
                result.add(EclipseHandlerUtil.copyAnnotation(annotation, source));
            }
        }
        if (allNull) {
            return null;
        }
        return result.toArray((T[])EMPTY_ANNOTATION_ARRAY);
    }

    public static boolean annotationTypeMatches(Class<? extends java.lang.annotation.Annotation> type, EclipseNode node) {
        if (node.getKind() != AST.Kind.ANNOTATION) {
            return false;
        }
        return EclipseHandlerUtil.typeMatches(type, node, ((Annotation)node.get()).type);
    }

    public static TypeReference makeType(TypeBinding binding, ASTNode pos, boolean allowCompound) {
        QualifiedTypeReference result;
        char[][] parts;
        int dims = binding.dimensions();
        binding = binding.leafComponentType();
        Object base = null;
        switch (binding.id) {
            case 10: {
                base = TypeConstants.INT;
                break;
            }
            case 7: {
                base = TypeConstants.LONG;
                break;
            }
            case 4: {
                base = TypeConstants.SHORT;
                break;
            }
            case 3: {
                base = TypeConstants.BYTE;
                break;
            }
            case 8: {
                base = TypeConstants.DOUBLE;
                break;
            }
            case 9: {
                base = TypeConstants.FLOAT;
                break;
            }
            case 5: {
                base = TypeConstants.BOOLEAN;
                break;
            }
            case 2: {
                base = TypeConstants.CHAR;
                break;
            }
            case 6: {
                base = TypeConstants.VOID;
                break;
            }
            case 12: {
                return null;
            }
        }
        if (base != null) {
            if (dims > 0) {
                ArrayTypeReference result2 = new ArrayTypeReference((char[])base, dims, Eclipse.pos(pos));
                EclipseHandlerUtil.setGeneratedBy((ASTNode)result2, pos);
                return result2;
            }
            SingleTypeReference result3 = new SingleTypeReference((char[])base, Eclipse.pos(pos));
            EclipseHandlerUtil.setGeneratedBy((ASTNode)result3, pos);
            return result3;
        }
        if (binding.isAnonymousType()) {
            ReferenceBinding ref = (ReferenceBinding)binding;
            ReferenceBinding[] supers = ref.superInterfaces();
            if (supers == null || supers.length == 0) {
                supers = new ReferenceBinding[]{ref.superclass()};
            }
            if (supers[0] == null) {
                QualifiedTypeReference result4 = new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, Eclipse.poss(pos, 3));
                EclipseHandlerUtil.setGeneratedBy((ASTNode)result4, pos);
                return result4;
            }
            return EclipseHandlerUtil.makeType((TypeBinding)supers[0], pos, false);
        }
        if (binding instanceof CaptureBinding) {
            return EclipseHandlerUtil.makeType((TypeBinding)((CaptureBinding)binding).wildcard, pos, allowCompound);
        }
        if (binding.isUnboundWildcard()) {
            if (!allowCompound) {
                QualifiedTypeReference result5 = new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, Eclipse.poss(pos, 3));
                EclipseHandlerUtil.setGeneratedBy((ASTNode)result5, pos);
                return result5;
            }
            Wildcard out = new Wildcard(0);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)out, pos);
            out.sourceStart = pos.sourceStart;
            out.sourceEnd = pos.sourceEnd;
            return out;
        }
        if (binding.isWildcard()) {
            WildcardBinding wildcard = (WildcardBinding)binding;
            if (wildcard.boundKind == 1) {
                if (!allowCompound) {
                    return EclipseHandlerUtil.makeType(wildcard.bound, pos, false);
                }
                Wildcard out = new Wildcard(1);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)out, pos);
                out.bound = EclipseHandlerUtil.makeType(wildcard.bound, pos, false);
                out.sourceStart = pos.sourceStart;
                out.sourceEnd = pos.sourceEnd;
                return out;
            }
            if (allowCompound && wildcard.boundKind == 2) {
                Wildcard out = new Wildcard(2);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)out, pos);
                out.bound = EclipseHandlerUtil.makeType(wildcard.bound, pos, false);
                out.sourceStart = pos.sourceStart;
                out.sourceEnd = pos.sourceEnd;
                return out;
            }
            QualifiedTypeReference result6 = new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, Eclipse.poss(pos, 3));
            EclipseHandlerUtil.setGeneratedBy((ASTNode)result6, pos);
            return result6;
        }
        ArrayList<Object> params = new ArrayList<Object>();
        TypeBinding b = binding;
        do {
            boolean isFinalStop = b.isLocalType() || !b.isMemberType() || b.enclosingType() == null;
            TypeReference[] tyParams = null;
            if (b instanceof ParameterizedTypeBinding) {
                ParameterizedTypeBinding paramized = (ParameterizedTypeBinding)b;
                if (paramized.arguments != null) {
                    tyParams = new TypeReference[paramized.arguments.length];
                    for (int i = 0; i < tyParams.length; ++i) {
                        tyParams[i] = EclipseHandlerUtil.makeType(paramized.arguments[i], pos, true);
                    }
                }
            }
            params.add(tyParams);
            if (isFinalStop) break;
            b = b.enclosingType();
        } while (true);
        if (binding.isTypeVariable()) {
            parts = new char[][]{binding.shortReadableName()};
        } else if (binding.isLocalType()) {
            parts = new char[][]{binding.sourceName()};
        } else {
            String[] pkg = new String(binding.qualifiedPackageName()).split("\\.");
            String[] name = new String(binding.qualifiedSourceName()).split("\\.");
            if (pkg.length == 1 && pkg[0].isEmpty()) {
                pkg = new String[]{};
            }
            parts = new char[pkg.length + name.length][];
            boolean ptr = false;
            while (++ptr < pkg.length) {
                parts[ptr] = pkg[ptr].toCharArray();
            }
            while (++ptr < pkg.length + name.length) {
                parts[ptr] = name[ptr - pkg.length].toCharArray();
            }
        }
        while (params.size() < parts.length) {
            params.add(null);
        }
        Collections.reverse(params);
        boolean isParamized = false;
        for (TypeReference[] tyParams : params) {
            if (tyParams == null) continue;
            isParamized = true;
            break;
        }
        if (isParamized) {
            if (parts.length > 1) {
                TypeReference[][] typeArguments = (TypeReference[][])params.toArray((T[])new TypeReference[0][]);
                ParameterizedQualifiedTypeReference result7 = new ParameterizedQualifiedTypeReference((char[][])parts, typeArguments, dims, Eclipse.poss(pos, parts.length));
                EclipseHandlerUtil.setGeneratedBy((ASTNode)result7, pos);
                return result7;
            }
            result = new ParameterizedSingleTypeReference(parts[0], (TypeReference[])params.get(0), dims, Eclipse.pos(pos));
            EclipseHandlerUtil.setGeneratedBy((ASTNode)result, pos);
            return result;
        }
        if (dims > 0) {
            if (parts.length > 1) {
                result = new ArrayQualifiedTypeReference((char[][])parts, dims, Eclipse.poss(pos, parts.length));
                EclipseHandlerUtil.setGeneratedBy((ASTNode)result, pos);
                return result;
            }
            result = new ArrayTypeReference(parts[0], dims, Eclipse.pos(pos));
            EclipseHandlerUtil.setGeneratedBy((ASTNode)result, pos);
            return result;
        }
        if (parts.length > 1) {
            result = new QualifiedTypeReference((char[][])parts, Eclipse.poss(pos, parts.length));
            EclipseHandlerUtil.setGeneratedBy((ASTNode)result, pos);
            return result;
        }
        result = new SingleTypeReference(parts[0], Eclipse.pos(pos));
        EclipseHandlerUtil.setGeneratedBy((ASTNode)result, pos);
        return result;
    }

    public static <A extends java.lang.annotation.Annotation> AnnotationValues<A> createAnnotation(Class<A> type, final EclipseNode annotationNode) {
        final Annotation annotation = (Annotation)annotationNode.get();
        HashMap<String, AnnotationValues.AnnotationValue> values = new HashMap<String, AnnotationValues.AnnotationValue>();
        MemberValuePair[] pairs = annotation.memberValuePairs();
        for (Method m : type.getDeclaredMethods()) {
            boolean isExplicit;
            if (!Modifier.isPublic(m.getModifiers())) continue;
            String name = m.getName();
            ArrayList<String> raws = new ArrayList<String>();
            ArrayList<Expression> expressionValues = new ArrayList<Expression>();
            ArrayList<Object> guesses = new ArrayList<Object>();
            Expression fullExpression = null;
            Expression[] expressions = null;
            if (pairs != null) {
                for (MemberValuePair pair : pairs) {
                    String mName;
                    char[] n = pair.name;
                    String string = mName = n == null ? "value" : new String(pair.name);
                    if (!mName.equals(name)) continue;
                    fullExpression = pair.value;
                }
            }
            boolean bl = isExplicit = fullExpression != null;
            if (isExplicit) {
                expressions = fullExpression instanceof ArrayInitializer ? ((ArrayInitializer)fullExpression).expressions : new Expression[]{fullExpression};
                if (expressions != null) {
                    for (Expression ex : expressions) {
                        StringBuffer sb = new StringBuffer();
                        ex.print(0, sb);
                        raws.add(sb.toString());
                        expressionValues.add(ex);
                        guesses.add(Eclipse.calculateValue(ex));
                    }
                }
            }
            final Expression fullExpr = fullExpression;
            final Expression[] exprs = expressions;
            values.put(name, ()new AnnotationValues.AnnotationValue(annotationNode, raws, expressionValues, guesses, isExplicit){

                public void setError(String message, int valueIdx) {
                    Expression ex;
                    if (valueIdx == -1) {
                        ex = fullExpr;
                    } else {
                        Expression expression = ex = exprs != null ? exprs[valueIdx] : null;
                    }
                    if (ex == null) {
                        ex = annotation;
                    }
                    int sourceStart = ex.sourceStart;
                    int sourceEnd = ex.sourceEnd;
                    annotationNode.addError(message, sourceStart, sourceEnd);
                }

                public void setWarning(String message, int valueIdx) {
                    Expression ex;
                    if (valueIdx == -1) {
                        ex = fullExpr;
                    } else {
                        Expression expression = ex = exprs != null ? exprs[valueIdx] : null;
                    }
                    if (ex == null) {
                        ex = annotation;
                    }
                    int sourceStart = ex.sourceStart;
                    int sourceEnd = ex.sourceEnd;
                    annotationNode.addWarning(message, sourceStart, sourceEnd);
                }
            });
        }
        return new AnnotationValues<A>(type, values, annotationNode);
    }

    public static int toEclipseModifier(AccessLevel value) {
        switch (value) {
            case MODULE: 
            case PACKAGE: {
                return 0;
            }
            default: {
                return 1;
            }
            case PROTECTED: {
                return 4;
            }
            case NONE: 
            case PRIVATE: 
        }
        return 2;
    }

    static void registerCreatedLazyGetter(FieldDeclaration field, char[] methodName, TypeReference returnType) {
        if (!Eclipse.nameEquals(returnType.getTypeName(), "boolean") || returnType.dimensions() > 0) {
            return;
        }
        generatedLazyGettersWithPrimitiveBoolean.put(field, MARKER);
    }

    private static GetterMethod findGetter(EclipseNode field) {
        EclipseNode containingType;
        FieldDeclaration fieldDeclaration = (FieldDeclaration)field.get();
        boolean forceBool = generatedLazyGettersWithPrimitiveBoolean.containsKey((Object)fieldDeclaration);
        TypeReference fieldType = fieldDeclaration.type;
        boolean isBoolean = forceBool || Eclipse.nameEquals(fieldType.getTypeName(), "boolean") && fieldType.dimensions() == 0;
        EclipseNode typeNode = (EclipseNode)field.up();
        for (String potentialGetterName : EclipseHandlerUtil.toAllGetterNames(field, isBoolean)) {
            for (EclipseNode potentialGetter : typeNode.down()) {
                if (potentialGetter.getKind() != AST.Kind.METHOD || !(potentialGetter.get() instanceof MethodDeclaration)) continue;
                MethodDeclaration method = (MethodDeclaration)potentialGetter.get();
                if (method.selector == null || !potentialGetterName.equalsIgnoreCase(new String(method.selector)) || (method.modifiers & 8) != 0 || method.arguments != null && method.arguments.length > 0) continue;
                return new GetterMethod(method.selector, method.returnType);
            }
        }
        boolean hasGetterAnnotation = false;
        for (EclipseNode child : field.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(Getter.class, child)) continue;
            AnnotationValues<A> ann = EclipseHandlerUtil.createAnnotation(Getter.class, child);
            if (((Getter)ann.getInstance()).value() == AccessLevel.NONE) {
                return null;
            }
            hasGetterAnnotation = true;
        }
        if (!hasGetterAnnotation && new HandleGetter().fieldQualifiesForGetterGeneration(field) && (containingType = (EclipseNode)field.up()) != null) {
            for (EclipseNode child2 : containingType.down()) {
                if (child2.getKind() == AST.Kind.ANNOTATION && EclipseHandlerUtil.annotationTypeMatches(Data.class, child2)) {
                    hasGetterAnnotation = true;
                }
                if (child2.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(Getter.class, child2)) continue;
                AnnotationValues<A> ann = EclipseHandlerUtil.createAnnotation(Getter.class, child2);
                if (((Getter)ann.getInstance()).value() == AccessLevel.NONE) {
                    return null;
                }
                hasGetterAnnotation = true;
            }
        }
        if (hasGetterAnnotation) {
            String getterName = EclipseHandlerUtil.toGetterName(field, isBoolean);
            if (getterName == null) {
                return null;
            }
            return new GetterMethod(getterName.toCharArray(), fieldType);
        }
        return null;
    }

    static boolean lookForGetter(EclipseNode field, FieldAccess fieldAccess) {
        if (fieldAccess == FieldAccess.GETTER) {
            return true;
        }
        if (fieldAccess == FieldAccess.ALWAYS_FIELD) {
            return false;
        }
        for (EclipseNode child : field.down()) {
            AnnotationValues<A> ann;
            if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(Getter.class, child) || !((Getter)(ann = EclipseHandlerUtil.createAnnotation(Getter.class, child)).getInstance()).lazy()) continue;
            return true;
        }
        return false;
    }

    static TypeReference getFieldType(EclipseNode field, FieldAccess fieldAccess) {
        GetterMethod getter;
        boolean lookForGetter = EclipseHandlerUtil.lookForGetter(field, fieldAccess);
        GetterMethod getterMethod = getter = lookForGetter ? EclipseHandlerUtil.findGetter(field) : null;
        if (getter == null) {
            return ((FieldDeclaration)field.get()).type;
        }
        return getter.type;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    static Expression createFieldAccessor(EclipseNode field, FieldAccess fieldAccess, ASTNode source) {
        pS = source.sourceStart;
        pE = source.sourceEnd;
        p = (long)pS << 32 | (long)pE;
        lookForGetter = EclipseHandlerUtil.lookForGetter(field, fieldAccess);
        v0 = getter = lookForGetter != false ? EclipseHandlerUtil.findGetter(field) : null;
        if (getter != null) {
            call = new MessageSend();
            EclipseHandlerUtil.setGeneratedBy((ASTNode)call, source);
            call.sourceStart = pS;
            call.statementEnd = call.sourceEnd = pE;
            call.receiver = new ThisReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)call.receiver, source);
            call.selector = GetterMethod.access$300(getter);
            return call;
        }
        fieldDecl = (FieldDeclaration)field.get();
        ref = new FieldReference(fieldDecl.name, p);
        if ((fieldDecl.modifiers & 8) == 0) ** GOTO lbl25
        containerNode = (EclipseNode)field.up();
        if (containerNode != null && containerNode.get() instanceof TypeDeclaration) {
            ref.receiver = new SingleNameReference(((TypeDeclaration)containerNode.get()).name, p);
        } else {
            smallRef = new FieldReference(field.getName().toCharArray(), p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)smallRef, source);
            return smallRef;
lbl25: // 1 sources:
            ref.receiver = new ThisReference(pS, pE);
        }
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ref, source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ref.receiver, source);
        return ref;
    }

    static Expression createFieldAccessor(EclipseNode field, FieldAccess fieldAccess, ASTNode source, char[] receiver) {
        GetterMethod getter;
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        boolean lookForGetter = EclipseHandlerUtil.lookForGetter(field, fieldAccess);
        GetterMethod getterMethod = getter = lookForGetter ? EclipseHandlerUtil.findGetter(field) : null;
        if (getter == null) {
            char[][] tokens = new char[][]{receiver, field.getName().toCharArray()};
            long[] poss = new long[]{p, p};
            QualifiedNameReference ref = new QualifiedNameReference((char[][])tokens, poss, pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ref, source);
            return ref;
        }
        MessageSend call = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)call, source);
        call.sourceStart = pS;
        call.statementEnd = call.sourceEnd = pE;
        call.receiver = new SingleNameReference(receiver, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)call.receiver, source);
        call.selector = getter.name;
        return call;
    }

    public static List<String> toAllGetterNames(EclipseNode field, boolean isBoolean) {
        String fieldName = field.getName();
        AnnotationValues<Accessors> accessors = EclipseHandlerUtil.getAccessorsForField(field);
        return TransformationsUtil.toAllGetterNames(accessors, fieldName, isBoolean);
    }

    public static String toGetterName(EclipseNode field, boolean isBoolean) {
        String fieldName = field.getName();
        AnnotationValues<Accessors> accessors = EclipseHandlerUtil.getAccessorsForField(field);
        return TransformationsUtil.toGetterName(accessors, fieldName, isBoolean);
    }

    public static List<String> toAllSetterNames(EclipseNode field, boolean isBoolean) {
        String fieldName = field.getName();
        AnnotationValues<Accessors> accessors = EclipseHandlerUtil.getAccessorsForField(field);
        return TransformationsUtil.toAllSetterNames(accessors, fieldName, isBoolean);
    }

    public static String toSetterName(EclipseNode field, boolean isBoolean) {
        String fieldName = field.getName();
        AnnotationValues<Accessors> accessors = EclipseHandlerUtil.getAccessorsForField(field);
        return TransformationsUtil.toSetterName(accessors, fieldName, isBoolean);
    }

    public static boolean shouldReturnThis(EclipseNode field) {
        if ((((FieldDeclaration)field.get()).modifiers & 8) != 0) {
            return false;
        }
        AnnotationValues<Accessors> accessors = EclipseHandlerUtil.getAccessorsForField(field);
        boolean forced = accessors.getActualExpression("chain") != null;
        Accessors instance = accessors.getInstance();
        return instance.chain() || instance.fluent() && !forced;
    }

    public static boolean filterField(FieldDeclaration declaration) {
        if (declaration.initialization instanceof AllocationExpression && ((AllocationExpression)declaration.initialization).enumConstant != null) {
            return false;
        }
        if (declaration.type == null) {
            return false;
        }
        if (declaration.name.length > 0 && declaration.name[0] == '$') {
            return false;
        }
        if ((declaration.modifiers & 8) != 0) {
            return false;
        }
        return true;
    }

    public static AnnotationValues<Accessors> getAccessorsForField(EclipseNode field) {
        for (EclipseNode node : field.down()) {
            if (!EclipseHandlerUtil.annotationTypeMatches(Accessors.class, node)) continue;
            return EclipseHandlerUtil.createAnnotation(Accessors.class, node);
        }
        for (EclipseNode current = (EclipseNode)field.up(); current != null; current = (EclipseNode)current.up()) {
            for (EclipseNode node2 : current.down()) {
                if (!EclipseHandlerUtil.annotationTypeMatches(Accessors.class, node2)) continue;
                return EclipseHandlerUtil.createAnnotation(Accessors.class, node2);
            }
        }
        return AnnotationValues.of(Accessors.class, field);
    }

    public static MemberExistsResult fieldExists(String fieldName, EclipseNode node) {
        while (node != null && !(node.get() instanceof TypeDeclaration)) {
            node = (EclipseNode)node.up();
        }
        if (node != null && node.get() instanceof TypeDeclaration) {
            TypeDeclaration typeDecl = (TypeDeclaration)node.get();
            if (typeDecl.fields != null) {
                for (FieldDeclaration def : typeDecl.fields) {
                    char[] fName = def.name;
                    if (fName == null || !fieldName.equals(new String(fName))) continue;
                    return EclipseHandlerUtil.getGeneratedBy((ASTNode)def) == null ? MemberExistsResult.EXISTS_BY_USER : MemberExistsResult.EXISTS_BY_LOMBOK;
                }
            }
        }
        return MemberExistsResult.NOT_EXISTS;
    }

    public static MemberExistsResult methodExists(String methodName, EclipseNode node, int params) {
        return EclipseHandlerUtil.methodExists(methodName, node, true, params);
    }

    public static MemberExistsResult methodExists(String methodName, EclipseNode node, boolean caseSensitive, int params) {
        while (node != null && !(node.get() instanceof TypeDeclaration)) {
            node = (EclipseNode)node.up();
        }
        if (node != null && node.get() instanceof TypeDeclaration) {
            TypeDeclaration typeDecl = (TypeDeclaration)node.get();
            if (typeDecl.methods != null) {
                for (AbstractMethodDeclaration def : typeDecl.methods) {
                    char[] mName;
                    boolean nameEquals;
                    if (!(def instanceof MethodDeclaration) || (mName = def.selector) == null) continue;
                    boolean bl = nameEquals = caseSensitive ? methodName.equals(new String(mName)) : methodName.equalsIgnoreCase(new String(mName));
                    if (!nameEquals) continue;
                    if (params > -1) {
                        int minArgs = 0;
                        int maxArgs = 0;
                        if (def.arguments != null && def.arguments.length > 0) {
                            minArgs = def.arguments.length;
                            if ((def.arguments[def.arguments.length - 1].type.bits & 16384) != 0) {
                                --minArgs;
                                maxArgs = Integer.MAX_VALUE;
                            } else {
                                maxArgs = minArgs;
                            }
                        }
                        if (params < minArgs || params > maxArgs) continue;
                    }
                    return EclipseHandlerUtil.getGeneratedBy((ASTNode)def) == null ? MemberExistsResult.EXISTS_BY_USER : MemberExistsResult.EXISTS_BY_LOMBOK;
                }
            }
        }
        return MemberExistsResult.NOT_EXISTS;
    }

    public static MemberExistsResult constructorExists(EclipseNode node) {
        while (node != null && !(node.get() instanceof TypeDeclaration)) {
            node = (EclipseNode)node.up();
        }
        if (node != null && node.get() instanceof TypeDeclaration) {
            TypeDeclaration typeDecl = (TypeDeclaration)node.get();
            if (typeDecl.methods != null) {
                for (AbstractMethodDeclaration def : typeDecl.methods) {
                    if (!(def instanceof ConstructorDeclaration) || (def.bits & 128) != 0 || EclipseHandlerUtil.isGeneratedByBuilder((ConstructorDeclaration)def)) continue;
                    return EclipseHandlerUtil.getGeneratedBy((ASTNode)def) == null ? MemberExistsResult.EXISTS_BY_USER : MemberExistsResult.EXISTS_BY_LOMBOK;
                }
            }
        }
        return MemberExistsResult.NOT_EXISTS;
    }

    private static boolean isGeneratedByBuilder(ConstructorDeclaration def) {
        return def.arguments != null && def.arguments.length == 1 && "$Builder".equals(new String(def.arguments[0].type.getLastToken()));
    }

    public static void injectFieldSuppressWarnings(EclipseNode type, FieldDeclaration field) {
        field.annotations = EclipseHandlerUtil.createSuppressWarningsAll((ASTNode)field, field.annotations);
        EclipseHandlerUtil.injectField(type, field);
    }

    public static void injectField(EclipseNode type, FieldDeclaration field) {
        TypeDeclaration parent = (TypeDeclaration)type.get();
        if (parent.fields == null) {
            parent.fields = new FieldDeclaration[1];
            parent.fields[0] = field;
        } else {
            FieldDeclaration f;
            int index;
            int size = parent.fields.length;
            FieldDeclaration[] newArray = new FieldDeclaration[size + 1];
            System.arraycopy(parent.fields, 0, newArray, 0, size);
            for (index = 0; index < size && (EclipseHandlerUtil.isEnumConstant(f = newArray[index]) || EclipseHandlerUtil.isGenerated((ASTNode)f)); ++index) {
            }
            System.arraycopy(newArray, index, newArray, index + 1, size - index);
            newArray[index] = field;
            parent.fields = newArray;
        }
        if ((EclipseHandlerUtil.isEnumConstant(field) || (field.modifiers & 8) != 0) && !Eclipse.hasClinit(parent)) {
            parent.addClinit();
        }
        type.add(field, AST.Kind.FIELD);
    }

    private static boolean isEnumConstant(FieldDeclaration field) {
        return field.initialization instanceof AllocationExpression && ((AllocationExpression)field.initialization).enumConstant == field;
    }

    public static void injectMethod(EclipseNode type, AbstractMethodDeclaration method) {
        method.annotations = EclipseHandlerUtil.createSuppressWarningsAll((ASTNode)method, method.annotations);
        TypeDeclaration parent = (TypeDeclaration)type.get();
        if (parent.methods == null) {
            parent.methods = new AbstractMethodDeclaration[1];
            parent.methods[0] = method;
        } else {
            if (method instanceof ConstructorDeclaration) {
                for (int i = 0; i < parent.methods.length; ++i) {
                    if (!(parent.methods[i] instanceof ConstructorDeclaration) || (parent.methods[i].bits & 128) == 0) continue;
                    EclipseNode tossMe = (EclipseNode)type.getNodeFor(parent.methods[i]);
                    AbstractMethodDeclaration[] withoutGeneratedConstructor = new AbstractMethodDeclaration[parent.methods.length - 1];
                    System.arraycopy(parent.methods, 0, withoutGeneratedConstructor, 0, i);
                    System.arraycopy(parent.methods, i + 1, withoutGeneratedConstructor, i, parent.methods.length - i - 1);
                    parent.methods = withoutGeneratedConstructor;
                    if (tossMe == null) break;
                    ((EclipseNode)tossMe.up()).removeChild(tossMe);
                    break;
                }
            }
            AbstractMethodDeclaration[] newArray = new AbstractMethodDeclaration[parent.methods.length + 1];
            System.arraycopy(parent.methods, 0, newArray, 0, parent.methods.length);
            newArray[parent.methods.length] = method;
            parent.methods = newArray;
        }
        type.add(method, AST.Kind.METHOD);
    }

    public static Annotation[] createSuppressWarningsAll(ASTNode source, Annotation[] originalAnnotationArray) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        long[] poss = new long[3];
        Arrays.fill(poss, p);
        QualifiedTypeReference suppressWarningsType = new QualifiedTypeReference(TypeConstants.JAVA_LANG_SUPPRESSWARNINGS, poss);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)suppressWarningsType, source);
        SingleMemberAnnotation ann = new SingleMemberAnnotation((TypeReference)suppressWarningsType, pS);
        ann.declarationSourceEnd = pE;
        ann.memberValue = new StringLiteral(ALL, pS, pE, 0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ann, source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ann.memberValue, source);
        if (originalAnnotationArray == null) {
            return new Annotation[]{ann};
        }
        Annotation[] newAnnotationArray = new Annotation[originalAnnotationArray.length + 1];
        System.arraycopy(originalAnnotationArray, 0, newAnnotationArray, 0, originalAnnotationArray.length);
        newAnnotationArray[originalAnnotationArray.length] = ann;
        return newAnnotationArray;
    }

    public static Statement generateNullCheck(AbstractVariableDeclaration variable, ASTNode source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        if (Eclipse.isPrimitive(variable.type)) {
            return null;
        }
        AllocationExpression exception = new AllocationExpression();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)exception, source);
        exception.type = new QualifiedTypeReference(Eclipse.fromQualifiedName("java.lang.NullPointerException"), new long[]{p, p, p});
        EclipseHandlerUtil.setGeneratedBy((ASTNode)exception.type, source);
        exception.arguments = new Expression[]{new StringLiteral(variable.name, pS, pE, 0)};
        EclipseHandlerUtil.setGeneratedBy((ASTNode)exception.arguments[0], source);
        ThrowStatement throwStatement = new ThrowStatement((Expression)exception, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)throwStatement, source);
        SingleNameReference varName = new SingleNameReference(variable.name, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)varName, source);
        NullLiteral nullLiteral = new NullLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)nullLiteral, source);
        EqualExpression equalExpression = new EqualExpression((Expression)varName, (Expression)nullLiteral, 18);
        equalExpression.sourceStart = pS;
        equalExpression.statementEnd = equalExpression.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)equalExpression, source);
        IfStatement ifStatement = new IfStatement((Expression)equalExpression, (Statement)throwStatement, 0, 0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ifStatement, source);
        return ifStatement;
    }

    public static MarkerAnnotation makeMarkerAnnotation(char[][] name, ASTNode source) {
        long pos = (long)source.sourceStart << 32 | (long)source.sourceEnd;
        QualifiedTypeReference typeRef = new QualifiedTypeReference(name, new long[]{pos, pos, pos});
        EclipseHandlerUtil.setGeneratedBy((ASTNode)typeRef, source);
        MarkerAnnotation ann = new MarkerAnnotation((TypeReference)typeRef, (int)(pos >> 32));
        ann.sourceEnd = ann.statementEnd = (int)pos;
        ann.declarationSourceEnd = ann.statementEnd;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ann, source);
        return ann;
    }

    public static List<Integer> createListOfNonExistentFields(List<String> list, EclipseNode type, boolean excludeStandard, boolean excludeTransient) {
        boolean[] matched = new boolean[list.size()];
        for (EclipseNode child : type.down()) {
            int idx;
            if (list.isEmpty()) break;
            if (child.getKind() != AST.Kind.FIELD || excludeStandard && ((((FieldDeclaration)child.get()).modifiers & 8) != 0 || child.getName().startsWith("$")) || excludeTransient && (((FieldDeclaration)child.get()).modifiers & 128) != 0 || (idx = list.indexOf(child.getName())) <= -1) continue;
            matched[idx] = true;
        }
        ArrayList<Integer> problematic = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); ++i) {
            if (matched[i]) continue;
            problematic.add(i);
        }
        return problematic;
    }

    public static CastExpression makeCastExpression(Expression ref, TypeReference castTo, ASTNode source) {
        CastExpression result;
        try {
            if (castExpressionConstructorIsTypeRefBased) {
                result = castExpressionConstructor.newInstance(new Object[]{ref, castTo});
            } else {
                TypeReference castToConverted = castTo;
                if (castTo.getClass() == SingleTypeReference.class && !Eclipse.isPrimitive(castTo)) {
                    SingleTypeReference str = (SingleTypeReference)castTo;
                    castToConverted = new SingleNameReference(str.token, 0);
                    castToConverted.bits = castToConverted.bits & -4 | 4;
                    castToConverted.sourceStart = str.sourceStart;
                    castToConverted.sourceEnd = str.sourceEnd;
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)castToConverted, source);
                } else if (castTo.getClass() == QualifiedTypeReference.class) {
                    QualifiedTypeReference qtr = (QualifiedTypeReference)castTo;
                    castToConverted = new QualifiedNameReference(qtr.tokens, qtr.sourcePositions, qtr.sourceStart, qtr.sourceEnd);
                    castToConverted.bits = castToConverted.bits & -4 | 4;
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)castToConverted, source);
                }
                result = castExpressionConstructor.newInstance(new Object[]{ref, castToConverted});
            }
        }
        catch (InvocationTargetException e) {
            throw Lombok.sneakyThrow(e.getCause());
        }
        catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
        catch (InstantiationException e) {
            throw Lombok.sneakyThrow(e);
        }
        result.sourceStart = source.sourceStart;
        result.sourceEnd = source.sourceEnd;
        result.statementEnd = source.sourceEnd;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)result, source);
        return result;
    }

    public static IntLiteral makeIntLiteral(char[] token, ASTNode source) {
        IntLiteral result;
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        try {
            result = intLiteralConstructor != null ? intLiteralConstructor.newInstance(token, pS, pE) : (IntLiteral)intLiteralFactoryMethod.invoke(null, token, pS, pE);
        }
        catch (InvocationTargetException e) {
            throw Lombok.sneakyThrow(e.getCause());
        }
        catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
        catch (InstantiationException e) {
            throw Lombok.sneakyThrow(e);
        }
        EclipseHandlerUtil.setGeneratedBy((ASTNode)result, source);
        return result;
    }

    static Annotation[] getAndRemoveAnnotationParameter(Annotation annotation, String annotationName) {
        ArrayList<Annotation> result = new ArrayList<Annotation>();
        if (annotation instanceof NormalAnnotation) {
            NormalAnnotation normalAnnotation = (NormalAnnotation)annotation;
            MemberValuePair[] memberValuePairs = normalAnnotation.memberValuePairs;
            ArrayList<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
            if (memberValuePairs != null) {
                for (MemberValuePair memberValuePair : memberValuePairs) {
                    if (annotationName.equals(new String(memberValuePair.name))) {
                        Expression value = memberValuePair.value;
                        if (value instanceof ArrayInitializer) {
                            ArrayInitializer array = (ArrayInitializer)value;
                            for (Expression expression : array.expressions) {
                                if (!(expression instanceof Annotation)) continue;
                                result.add((Annotation)expression);
                            }
                            continue;
                        }
                        if (!(value instanceof Annotation)) continue;
                        result.add((Annotation)value);
                        continue;
                    }
                    pairs.add(memberValuePair);
                }
            }
            if (!result.isEmpty()) {
                normalAnnotation.memberValuePairs = pairs.isEmpty() ? null : pairs.toArray((T[])new MemberValuePair[0]);
                return result.toArray((T[])EMPTY_ANNOTATION_ARRAY);
            }
        }
        return EMPTY_ANNOTATION_ARRAY;
    }

    static NameReference createNameReference(String name, Annotation source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        char[][] nameTokens = Eclipse.fromQualifiedName(name);
        long[] pos = new long[nameTokens.length];
        Arrays.fill(pos, p);
        QualifiedNameReference nameReference = new QualifiedNameReference(nameTokens, pos, pS, pE);
        nameReference.statementEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)nameReference, (ASTNode)source);
        return nameReference;
    }

    static {
        try {
            generatedByField = ASTNode.class.getDeclaredField("$generatedBy");
        }
        catch (Throwable t) {
            // empty catch block
        }
        generatedNodes = new WeakHashMap<ASTNode, ASTNode>();
        generatedLazyGettersWithPrimitiveBoolean = new WeakHashMap<FieldDeclaration, Object>();
        MARKER = new Object();
        ALL = "all".toCharArray();
        Constructor constructor = null;
        for (Constructor ctor : CastExpression.class.getConstructors()) {
            if (ctor.getParameterTypes().length != 2) continue;
            constructor = ctor;
        }
        Constructor castExpressionConstructor_ = constructor;
        castExpressionConstructor = castExpressionConstructor_;
        castExpressionConstructorIsTypeRefBased = castExpressionConstructor.getParameterTypes()[1] == TypeReference.class;
        Class[] parameterTypes = new Class[]{char[].class, Integer.TYPE, Integer.TYPE};
        Constructor intLiteralConstructor_ = null;
        Method intLiteralFactoryMethod_ = null;
        try {
            intLiteralConstructor_ = IntLiteral.class.getConstructor(parameterTypes);
        }
        catch (Throwable ignore) {
            // empty catch block
        }
        try {
            intLiteralFactoryMethod_ = IntLiteral.class.getMethod("buildIntLiteral", parameterTypes);
        }
        catch (Throwable ignore) {
            // empty catch block
        }
        intLiteralConstructor = intLiteralConstructor_;
        intLiteralFactoryMethod = intLiteralFactoryMethod_;
        EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    }

    public static enum MemberExistsResult {
        NOT_EXISTS,
        EXISTS_BY_LOMBOK,
        EXISTS_BY_USER;
        

        private MemberExistsResult() {
        }
    }

    public static enum FieldAccess {
        GETTER,
        PREFER_FIELD,
        ALWAYS_FIELD;
        

        private FieldAccess() {
        }
    }

    private static class GetterMethod {
        private final char[] name;
        private final TypeReference type;

        GetterMethod(char[] name, TypeReference type) {
            this.name = name;
            this.type = type;
        }
    }

    private static class EclipseWorkspaceLogger {
        private EclipseWorkspaceLogger() {
        }

        void error(String message, String bundleName, Throwable error) {
            this.msg(4, message, bundleName, error);
        }

        void warning(String message, String bundleName, Throwable error) {
            this.msg(2, message, bundleName, error);
        }

        private void msg(int msgType, String message, String bundleName, Throwable error) {
            Bundle bundle = Platform.getBundle((String)bundleName);
            if (bundle == null) {
                System.err.printf("Can't find bundle %s while trying to report error:\n%s\n", bundleName, message);
                return;
            }
            ILog log = Platform.getLog((Bundle)bundle);
            log.log((IStatus)new Status(msgType, bundleName, message, error));
        }
    }

    private static class TerminalLogger {
        private TerminalLogger() {
        }

        void error(String message, String bundleName, Throwable error) {
            System.err.println(message);
            if (error != null) {
                error.printStackTrace();
            }
        }

        void warning(String message, String bundleName, Throwable error) {
            System.err.println(message);
            if (error != null) {
                error.printStackTrace();
            }
        }
    }

}

