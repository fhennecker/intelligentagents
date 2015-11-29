/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.InvocationSite
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.PackageBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.Scope
 *  org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 */
package lombok.eclipse.agent;

import lombok.core.util.As;
import lombok.core.util.Each;
import lombok.patcher.Hook;
import lombok.patcher.MethodTarget;
import lombok.patcher.PatchScript;
import lombok.patcher.ScriptManager;
import lombok.patcher.TargetMatcher;
import lombok.patcher.scripts.ReplaceMethodCallScript;
import lombok.patcher.scripts.ScriptBuilder;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public final class PatchVisibleForTesting {
    static void addPatches(ScriptManager sm, boolean ecj) {
        String HOOK_NAME = PatchVisibleForTesting.class.getName();
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.Scope", "getMethod", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "char[]", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]", "org.eclipse.jdt.internal.compiler.lookup.InvocationSite")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.lookup.Scope", "findMethod", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding", "org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding", "char[]", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]", "org.eclipse.jdt.internal.compiler.lookup.InvocationSite")).replacementMethod(new Hook(HOOK_NAME, "onFindMethod", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding", "org.eclipse.jdt.internal.compiler.lookup.Scope", "org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding", "char[]", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]", "org.eclipse.jdt.internal.compiler.lookup.InvocationSite")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.Scope", "getMethod", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "char[]", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]", "org.eclipse.jdt.internal.compiler.lookup.InvocationSite")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.lookup.Scope", "findExactMethod", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding", "org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding", "char[]", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]", "org.eclipse.jdt.internal.compiler.lookup.InvocationSite")).replacementMethod(new Hook(HOOK_NAME, "onFindExactMethod", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding", "org.eclipse.jdt.internal.compiler.lookup.Scope", "org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding", "char[]", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]", "org.eclipse.jdt.internal.compiler.lookup.InvocationSite")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope", "findImport", "org.eclipse.jdt.internal.compiler.lookup.Binding", "char[][]", "int")).target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope", "findSingleImport", "org.eclipse.jdt.internal.compiler.lookup.Binding", "char[][]", "int", "boolean")).target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.Scope", "getTypeOrPackage", "org.eclipse.jdt.internal.compiler.lookup.Binding", "char[]", "int", "boolean")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.lookup.Scope", "findType", "org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding", "char[]", "org.eclipse.jdt.internal.compiler.lookup.PackageBinding", "org.eclipse.jdt.internal.compiler.lookup.PackageBinding")).replacementMethod(new Hook(HOOK_NAME, "onFindType", "org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding", "org.eclipse.jdt.internal.compiler.lookup.Scope", "char[]", "org.eclipse.jdt.internal.compiler.lookup.PackageBinding", "org.eclipse.jdt.internal.compiler.lookup.PackageBinding")).build());
    }

    public static MethodBinding onFindMethod(Scope scope, ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
        return PatchVisibleForTesting.handleVisibleForTestingOnMethod(scope, scope.findMethod(receiverType, selector, argumentTypes, invocationSite));
    }

    public static MethodBinding onFindExactMethod(Scope scope, ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
        return PatchVisibleForTesting.handleVisibleForTestingOnMethod(scope, scope.findExactMethod(receiverType, selector, argumentTypes, invocationSite));
    }

    public static ReferenceBinding onFindType(Scope scope, char[] typeName, PackageBinding declarationPackage, PackageBinding invocationPackage) {
        return PatchVisibleForTesting.handleVisibleForTestingOnType(scope, scope.findType(typeName, declarationPackage, invocationPackage));
    }

    private static MethodBinding handleVisibleForTestingOnMethod(Scope scope, MethodBinding methodBinding) {
        if (methodBinding == null || methodBinding.declaringClass == null) {
            return methodBinding;
        }
        for (AnnotationBinding annotation : Each.elementIn(methodBinding.getAnnotations())) {
            ClassScope classScope;
            if (!As.string((Object)annotation.getAnnotationType()).contains("VisibleForTesting") || (classScope = scope.outerMostClassScope()) == null) continue;
            TypeDeclaration decl = classScope.referenceContext;
            if (methodBinding.declaringClass == decl.binding || As.string(decl.name).contains("Test")) continue;
            return new ProblemMethodBinding(methodBinding, methodBinding.selector, methodBinding.parameters, 2);
        }
        return methodBinding;
    }

    private static ReferenceBinding handleVisibleForTestingOnType(Scope scope, ReferenceBinding typeBinding) {
        if (typeBinding == null) {
            return typeBinding;
        }
        for (AnnotationBinding annotation : Each.elementIn(typeBinding.getAnnotations())) {
            ClassScope classScope;
            if (!As.string((Object)annotation.getAnnotationType()).contains("VisibleForTesting") || (classScope = scope.outerMostClassScope()) == null) continue;
            TypeDeclaration decl = classScope.referenceContext;
            if (As.string(decl.name).contains("Test")) continue;
            return new ProblemReferenceBinding(typeBinding.compoundName, typeBinding, 2);
        }
        return typeBinding;
    }

    private PatchVisibleForTesting() {
    }
}

