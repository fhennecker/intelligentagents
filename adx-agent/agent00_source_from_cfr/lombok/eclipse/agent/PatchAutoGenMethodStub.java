/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding
 *  org.eclipse.jdt.internal.compiler.problem.ProblemReporter
 */
package lombok.eclipse.agent;

import lombok.AutoGenMethodStub;
import lombok.core.AnnotationValues;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.agent.Patches;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.HandleAutoGenMethodStub;
import lombok.patcher.Hook;
import lombok.patcher.MethodTarget;
import lombok.patcher.PatchScript;
import lombok.patcher.ScriptManager;
import lombok.patcher.TargetMatcher;
import lombok.patcher.scripts.ReplaceMethodCallScript;
import lombok.patcher.scripts.ScriptBuilder;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public final class PatchAutoGenMethodStub {
    private static final ThreadLocal<Boolean> ISSUE_WAS_FIXED = new ThreadLocal<Boolean>(){

        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    static void addPatches(ScriptManager sm, boolean ecj) {
        String HOOK_NAME = PatchAutoGenMethodStub.class.getName();
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "checkAbstractMethod", "void", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "checkInheritedMethods", "void", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding[]", "int")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "addMissingAbstractMethodFor", "org.eclipse.jdt.internal.compiler.ast.MethodDeclaration", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).replacementMethod(new Hook(HOOK_NAME, "addMissingAbstractMethodFor", "org.eclipse.jdt.internal.compiler.ast.MethodDeclaration", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "checkAbstractMethod", "void", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "checkInheritedMethods", "void", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding[]", "int")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "abstractMethodMustBeImplemented", "void", "org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).replacementMethod(new Hook(HOOK_NAME, "abstractMethodMustBeImplemented", "void", "org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "checkAbstractMethod_aroundBody0", "void", "org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "checkInheritedMethods_aroundBody2", "void", "org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding[]", "int")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "addMissingAbstractMethodFor", "org.eclipse.jdt.internal.compiler.ast.MethodDeclaration", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).replacementMethod(new Hook(HOOK_NAME, "addMissingAbstractMethodFor", "org.eclipse.jdt.internal.compiler.ast.MethodDeclaration", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "checkAbstractMethod_aroundBody0", "void", "org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "checkInheritedMethods_aroundBody2", "void", "org.eclipse.jdt.internal.compiler.lookup.MethodVerifier", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding[]", "int")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "abstractMethodMustBeImplemented", "void", "org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).replacementMethod(new Hook(HOOK_NAME, "abstractMethodMustBeImplemented", "void", "org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).build());
    }

    public static MethodDeclaration addMissingAbstractMethodFor(TypeDeclaration decl, MethodBinding abstractMethod) {
        Annotation ann = Patches.getAnnotation(AutoGenMethodStub.class, decl);
        EclipseNode typeNode = Patches.getTypeNode(decl);
        if (ann != null && typeNode != null) {
            EclipseNode annotationNode = (EclipseNode)typeNode.getNodeFor(ann);
            try {
                MethodDeclaration method = new HandleAutoGenMethodStub().handle(abstractMethod, EclipseHandlerUtil.createAnnotation(AutoGenMethodStub.class, annotationNode), ann, annotationNode);
                ISSUE_WAS_FIXED.set(true);
                return method;
            }
            catch (AnnotationValues.AnnotationValueDecodeFail fail) {
                fail.owner.setError(fail.getMessage(), fail.idx);
            }
        }
        return decl.addMissingAbstractMethodFor(abstractMethod);
    }

    public static void abstractMethodMustBeImplemented(ProblemReporter problemReporter, SourceTypeBinding type, MethodBinding abstractMethod) {
        if (ISSUE_WAS_FIXED.get().booleanValue()) {
            ISSUE_WAS_FIXED.set(false);
        } else {
            problemReporter.abstractMethodMustBeImplemented(type, abstractMethod);
        }
    }

    private PatchAutoGenMethodStub() {
    }

}

