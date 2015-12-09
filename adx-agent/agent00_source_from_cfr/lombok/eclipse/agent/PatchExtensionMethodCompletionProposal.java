/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.core.CompletionProposal
 *  org.eclipse.jdt.internal.codeassist.InternalCompletionContext
 *  org.eclipse.jdt.internal.codeassist.InternalCompletionProposal
 *  org.eclipse.jdt.internal.codeassist.InternalExtendedCompletionContext
 *  org.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberAccess
 *  org.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedNameReference
 *  org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.FieldReference
 *  org.eclipse.jdt.internal.compiler.ast.NameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.env.INameEnvironment
 *  org.eclipse.jdt.internal.compiler.impl.ITypeRequestor
 *  org.eclipse.jdt.internal.compiler.lookup.Binding
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.Scope
 *  org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.VariableBinding
 *  org.eclipse.jdt.internal.core.NameLookup
 *  org.eclipse.jdt.internal.core.SearchableEnvironment
 *  org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal
 *  org.eclipse.jdt.ui.text.java.CompletionProposalCollector
 *  org.eclipse.jdt.ui.text.java.IJavaCompletionProposal
 */
package lombok.eclipse.agent;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.ExtensionMethod;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.agent.ExtensionMethodCompletionProposal;
import lombok.eclipse.agent.PatchExtensionMethod;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.codeassist.InternalCompletionProposal;
import org.eclipse.jdt.internal.codeassist.InternalExtendedCompletionContext;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberAccess;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedNameReference;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.core.NameLookup;
import org.eclipse.jdt.internal.core.SearchableEnvironment;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PatchExtensionMethodCompletionProposal {
    public static IJavaCompletionProposal[] getJavaCompletionProposals(IJavaCompletionProposal[] javaCompletionProposals, CompletionProposalCollector completionProposalCollector) {
        ArrayList<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>(Arrays.asList(javaCompletionProposals));
        if (PatchExtensionMethodCompletionProposal.canExtendCodeAssist(proposals)) {
            IJavaCompletionProposal firstProposal = proposals.get(0);
            int replacementOffset = PatchExtensionMethodCompletionProposal.getReplacementOffset(firstProposal);
            for (PatchExtensionMethod.Extension extension : PatchExtensionMethodCompletionProposal.getExtensionMethods(completionProposalCollector)) {
                for (MethodBinding method : extension.extensionMethods) {
                    ExtensionMethodCompletionProposal newProposal = new ExtensionMethodCompletionProposal(replacementOffset);
                    PatchExtensionMethodCompletionProposal.copyNameLookupAndCompletionEngine(completionProposalCollector, firstProposal, newProposal);
                    ASTNode node = PatchExtensionMethodCompletionProposal.getAssistNode(completionProposalCollector);
                    newProposal.setMethodBinding(method, node);
                    PatchExtensionMethodCompletionProposal.createAndAddJavaCompletionProposal(completionProposalCollector, (CompletionProposal)newProposal, proposals);
                }
            }
        }
        return proposals.toArray((T[])new IJavaCompletionProposal[proposals.size()]);
    }

    private static List<PatchExtensionMethod.Extension> getExtensionMethods(CompletionProposalCollector completionProposalCollector) {
        ArrayList<PatchExtensionMethod.Extension> extensions = new ArrayList<PatchExtensionMethod.Extension>();
        ClassScope classScope = PatchExtensionMethodCompletionProposal.getClassScope(completionProposalCollector);
        if (classScope != null) {
            TypeDeclaration decl = classScope.referenceContext;
            TypeBinding firstParameterType = PatchExtensionMethodCompletionProposal.getFirstParameterType(decl, completionProposalCollector);
            EclipseNode typeNode = PatchExtensionMethod.getTypeNode(decl);
            while (typeNode != null) {
                Annotation ann = PatchExtensionMethod.getAnnotation(ExtensionMethod.class, typeNode);
                extensions.addAll(0, PatchExtensionMethod.getApplicableExtensionMethods(typeNode, ann, firstParameterType));
                typeNode = PatchExtensionMethod.upToType(typeNode);
            }
        }
        return extensions;
    }

    static TypeBinding getFirstParameterType(TypeDeclaration decl, CompletionProposalCollector completionProposalCollector) {
        TypeBinding firstParameterType = null;
        ASTNode node = PatchExtensionMethodCompletionProposal.getAssistNode(completionProposalCollector);
        if (node == null) {
            return null;
        }
        if (!(node instanceof CompletionOnQualifiedNameReference || node instanceof CompletionOnSingleNameReference || node instanceof CompletionOnMemberAccess)) {
            return null;
        }
        if (node instanceof NameReference) {
            Binding binding = ((NameReference)node).binding;
            if (node instanceof SingleNameReference && ((SingleNameReference)node).token.length == 0) {
                firstParameterType = decl.binding;
            } else if (binding instanceof VariableBinding) {
                firstParameterType = ((VariableBinding)binding).type;
            } else if (binding instanceof TypeBinding) {
                firstParameterType = (TypeBinding)binding;
            }
        } else if (node instanceof FieldReference) {
            firstParameterType = ((FieldReference)node).actualReceiverType;
        }
        return firstParameterType;
    }

    private static ASTNode getAssistNode(CompletionProposalCollector completionProposalCollector) {
        try {
            InternalCompletionContext context = (InternalCompletionContext)Reflection.contextField.get((Object)completionProposalCollector);
            InternalExtendedCompletionContext extendedContext = (InternalExtendedCompletionContext)Reflection.extendedContextField.get((Object)context);
            if (extendedContext == null) {
                return null;
            }
            return (ASTNode)Reflection.assistNodeField.get((Object)extendedContext);
        }
        catch (Exception ignore) {
            return null;
        }
    }

    private static ClassScope getClassScope(CompletionProposalCollector completionProposalCollector) {
        ClassScope scope = null;
        try {
            Scope assistScope;
            InternalCompletionContext context = (InternalCompletionContext)Reflection.contextField.get((Object)completionProposalCollector);
            InternalExtendedCompletionContext extendedContext = (InternalExtendedCompletionContext)Reflection.extendedContextField.get((Object)context);
            if (extendedContext != null && (assistScope = (Scope)Reflection.assistScopeField.get((Object)extendedContext)) != null) {
                scope = assistScope.classScope();
            }
        }
        catch (IllegalAccessException ignore) {
            // empty catch block
        }
        return scope;
    }

    private static void copyNameLookupAndCompletionEngine(CompletionProposalCollector completionProposalCollector, IJavaCompletionProposal proposal, InternalCompletionProposal newProposal) {
        try {
            InternalCompletionContext context = (InternalCompletionContext)Reflection.contextField.get((Object)completionProposalCollector);
            InternalExtendedCompletionContext extendedContext = (InternalExtendedCompletionContext)Reflection.extendedContextField.get((Object)context);
            LookupEnvironment lookupEnvironment = (LookupEnvironment)Reflection.lookupEnvironmentField.get((Object)extendedContext);
            Reflection.nameLookupField.set((Object)newProposal, (Object)((SearchableEnvironment)lookupEnvironment.nameEnvironment).nameLookup);
            Reflection.completionEngineField.set((Object)newProposal, (Object)lookupEnvironment.typeRequestor);
        }
        catch (IllegalAccessException ignore) {
            // empty catch block
        }
    }

    private static void createAndAddJavaCompletionProposal(CompletionProposalCollector completionProposalCollector, CompletionProposal newProposal, List<IJavaCompletionProposal> proposals) {
        try {
            proposals.add((IJavaCompletionProposal)Reflection.createJavaCompletionProposalMethod.invoke((Object)completionProposalCollector, new Object[]{newProposal}));
        }
        catch (Exception ignore) {
            // empty catch block
        }
    }

    private static boolean canExtendCodeAssist(List<IJavaCompletionProposal> proposals) {
        return !proposals.isEmpty() && Reflection.isComplete();
    }

    private static int getReplacementOffset(IJavaCompletionProposal proposal) {
        try {
            return Reflection.replacementOffsetField.getInt((Object)proposal);
        }
        catch (Exception ignore) {
            return 0;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class Reflection {
        public static final Field replacementOffsetField = Reflection.accessField(AbstractJavaCompletionProposal.class, "fReplacementOffset");
        public static final Field contextField = Reflection.accessField(CompletionProposalCollector.class, "fContext");
        public static final Field extendedContextField = Reflection.accessField(InternalCompletionContext.class, "extendedContext");
        public static final Field assistNodeField = Reflection.accessField(InternalExtendedCompletionContext.class, "assistNode");
        public static final Field assistScopeField = Reflection.accessField(InternalExtendedCompletionContext.class, "assistScope");
        public static final Field lookupEnvironmentField = Reflection.accessField(InternalExtendedCompletionContext.class, "lookupEnvironment");
        public static final Field completionEngineField = Reflection.accessField(InternalCompletionProposal.class, "completionEngine");
        public static final Field nameLookupField = Reflection.accessField(InternalCompletionProposal.class, "nameLookup");
        public static final Method createJavaCompletionProposalMethod = Reflection.accessMethod(CompletionProposalCollector.class, "createJavaCompletionProposal", CompletionProposal.class);

        Reflection() {
        }

        static boolean isComplete() {
            Object[] requiredFieldsAndMethods;
            for (Object o : requiredFieldsAndMethods = new Object[]{replacementOffsetField, contextField, extendedContextField, assistNodeField, assistScopeField, lookupEnvironmentField, completionEngineField, nameLookupField, createJavaCompletionProposalMethod}) {
                if (o != null) continue;
                return false;
            }
            return true;
        }

        private static Field accessField(Class<?> clazz, String fieldName) {
            try {
                return Reflection.makeAccessible(clazz.getDeclaredField(fieldName));
            }
            catch (Exception e) {
                return null;
            }
        }

        private static Method accessMethod(Class<?> clazz, String methodName, Class<?> parameter) {
            try {
                return Reflection.makeAccessible(clazz.getDeclaredMethod(methodName, parameter));
            }
            catch (Exception e) {
                return null;
            }
        }

        private static <T extends AccessibleObject> T makeAccessible(T object) {
            object.setAccessible(true);
            return object;
        }
    }

}

