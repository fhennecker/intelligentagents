/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.ForeachStatement
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.ArrayBinding
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeConstants
 *  org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding
 */
package lombok.eclipse.agent;

import java.lang.reflect.Field;
import lombok.eclipse.Eclipse;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class PatchVal {
    public static TypeBinding skipResolveInitializerIfAlreadyCalled(Expression expr, BlockScope scope) {
        if (expr.resolvedType != null) {
            return expr.resolvedType;
        }
        return expr.resolveType(scope);
    }

    public static TypeBinding skipResolveInitializerIfAlreadyCalled2(Expression expr, BlockScope scope, LocalDeclaration decl) {
        if (decl != null && LocalDeclaration.class.equals(decl.getClass()) && expr.resolvedType != null) {
            return expr.resolvedType;
        }
        return expr.resolveType(scope);
    }

    public static boolean matches(String key, char[] array) {
        if (array == null || key.length() != array.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (key.charAt(i) == array[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean couldBeVal(TypeReference ref) {
        if (ref instanceof SingleTypeReference) {
            char[] token = ((SingleTypeReference)ref).token;
            return PatchVal.matches("val", token);
        }
        if (ref instanceof QualifiedTypeReference) {
            char[][] tokens = ((QualifiedTypeReference)ref).tokens;
            if (tokens == null || tokens.length != 2) {
                return false;
            }
            return PatchVal.matches("lombok", tokens[0]) && PatchVal.matches("val", tokens[1]);
        }
        return false;
    }

    private static boolean isVal(TypeReference ref, BlockScope scope) {
        if (!PatchVal.couldBeVal(ref)) {
            return false;
        }
        TypeBinding resolvedType = ref.resolvedType;
        if (resolvedType == null) {
            resolvedType = ref.resolveType(scope, false);
        }
        if (resolvedType == null) {
            return false;
        }
        char[] pkg = resolvedType.qualifiedPackageName();
        char[] nm = resolvedType.qualifiedSourceName();
        return PatchVal.matches("lombok", pkg) && PatchVal.matches("val", nm);
    }

    public static boolean handleValForLocalDeclaration(LocalDeclaration local, BlockScope scope) {
        if (local == null || !LocalDeclaration.class.equals(local.getClass())) {
            return false;
        }
        boolean decomponent = false;
        if (!PatchVal.isVal(local.type, scope)) {
            return false;
        }
        if (new Throwable().getStackTrace()[2].getClassName().contains("ForStatement")) {
            return false;
        }
        Expression init = local.initialization;
        if (init == null && initCopyField != null) {
            try {
                init = (Expression)initCopyField.get((Object)local);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (init == null && iterableCopyField != null) {
            try {
                init = (Expression)iterableCopyField.get((Object)local);
                decomponent = true;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        TypeReference replacement = null;
        if (init != null) {
            TypeBinding resolved;
            TypeBinding typeBinding = resolved = decomponent ? PatchVal.getForEachComponentType(init, scope) : init.resolveType(scope);
            if (resolved != null) {
                replacement = EclipseHandlerUtil.makeType(resolved, (ASTNode)local.type, false);
            }
        }
        local.modifiers |= 16;
        local.annotations = PatchVal.addValAnnotation(local.annotations, local.type, scope);
        local.type = replacement != null ? replacement : new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, Eclipse.poss((ASTNode)local.type, 3));
        return false;
    }

    public static boolean handleValForForEach(ForeachStatement forEach, BlockScope scope) {
        if (forEach.elementVariable == null) {
            return false;
        }
        if (!PatchVal.isVal(forEach.elementVariable.type, scope)) {
            return false;
        }
        TypeBinding component = PatchVal.getForEachComponentType(forEach.collection, scope);
        if (component == null) {
            return false;
        }
        TypeReference replacement = EclipseHandlerUtil.makeType(component, (ASTNode)forEach.elementVariable.type, false);
        forEach.elementVariable.modifiers |= 16;
        forEach.elementVariable.annotations = PatchVal.addValAnnotation(forEach.elementVariable.annotations, forEach.elementVariable.type, scope);
        forEach.elementVariable.type = replacement != null ? replacement : new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, Eclipse.poss((ASTNode)forEach.elementVariable.type, 3));
        return false;
    }

    private static Annotation[] addValAnnotation(Annotation[] originals, TypeReference originalRef, BlockScope scope) {
        Annotation[] newAnn;
        if (originals != null) {
            newAnn = new Annotation[1 + originals.length];
            System.arraycopy(originals, 0, newAnn, 0, originals.length);
        } else {
            newAnn = new Annotation[1];
        }
        newAnn[newAnn.length - 1] = new MarkerAnnotation(originalRef, originalRef.sourceStart);
        return newAnn;
    }

    private static TypeBinding getForEachComponentType(Expression collection, BlockScope scope) {
        if (collection != null) {
            TypeBinding resolved = collection.resolvedType;
            if (resolved == null) {
                resolved = collection.resolveType(scope);
            }
            if (resolved == null) {
                return null;
            }
            if (resolved.isArrayType()) {
                resolved = ((ArrayBinding)resolved).elementsType();
                return resolved;
            }
            if (resolved instanceof ReferenceBinding) {
                ReferenceBinding iterableType = ((ReferenceBinding)resolved).findSuperTypeOriginatingFrom(38, false);
                TypeVariableBinding[] arguments = null;
                if (iterableType != null) {
                    switch (iterableType.kind()) {
                        case 2052: {
                            arguments = iterableType.typeVariables();
                            break;
                        }
                        case 260: {
                            arguments = ((ParameterizedTypeBinding)iterableType).arguments;
                            break;
                        }
                        case 1028: {
                            return null;
                        }
                    }
                }
                if (arguments != null && arguments.length == 1) {
                    return arguments[0];
                }
            }
        }
        return null;
    }

    public static final class Reflection {
        private static final Field initCopyField;
        private static final Field iterableCopyField;

        static {
            Field a = null;
            Field b = null;
            try {
                a = LocalDeclaration.class.getDeclaredField("$initCopy");
                b = LocalDeclaration.class.getDeclaredField("$iterableCopy");
            }
            catch (Throwable t) {
                // empty catch block
            }
            initCopyField = a;
            iterableCopyField = b;
        }
    }

}

