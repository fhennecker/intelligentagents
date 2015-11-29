/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 */
package lombok.eclipse.agent;

import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.TransformEclipseAST;
import lombok.eclipse.handlers.Eclipse;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

final class Patches {
    public static final String AST_PACKAGE = "org.eclipse.jdt.internal.compiler.ast";
    public static final String LOOKUP_PACKAGE = "org.eclipse.jdt.internal.compiler.lookup";
    public static final String PROBLEM_PACKAGE = "org.eclipse.jdt.internal.compiler.problem";
    public static final String TEXT_JAVA_PACKAGE = "org.eclipse.jdt.ui.text.java";
    public static final String ABSTRACTMETHODDECLARATION = "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration";
    public static final String BINDING = "org.eclipse.jdt.internal.compiler.lookup.Binding";
    public static final String BINDINGS = "org.eclipse.jdt.internal.compiler.lookup.Binding[]";
    public static final String BLOCKSCOPE = "org.eclipse.jdt.internal.compiler.lookup.BlockScope";
    public static final String CLASSSCOPE = "org.eclipse.jdt.internal.compiler.lookup.ClassScope";
    public static final String COMPILATIONUNITSCOPE = "org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope";
    public static final String COMPLETIONPROPOSALCOLLECTOR = "org.eclipse.jdt.ui.text.java.CompletionProposalCollector";
    public static final String IJAVACOMPLETIONPROPOSALS = "org.eclipse.jdt.ui.text.java.IJavaCompletionProposal[]";
    public static final String INVOCATIONSITE = "org.eclipse.jdt.internal.compiler.lookup.InvocationSite";
    public static final String MESSAGESEND = "org.eclipse.jdt.internal.compiler.ast.MessageSend";
    public static final String METHODBINDING = "org.eclipse.jdt.internal.compiler.lookup.MethodBinding";
    public static final String METHODBINDINGS = "org.eclipse.jdt.internal.compiler.lookup.MethodBinding[]";
    public static final String METHODDECLARATION = "org.eclipse.jdt.internal.compiler.ast.MethodDeclaration";
    public static final String METHODVERIFIER = "org.eclipse.jdt.internal.compiler.lookup.MethodVerifier";
    public static final String PACKAGEBINDING = "org.eclipse.jdt.internal.compiler.lookup.PackageBinding";
    public static final String PROBLEMREPORTER = "org.eclipse.jdt.internal.compiler.problem.ProblemReporter";
    public static final String REFERENCEBINDING = "org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding";
    public static final String SCOPE = "org.eclipse.jdt.internal.compiler.lookup.Scope";
    public static final String SOURCETYPEBINDING = "org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding";
    public static final String TYPEBINDING = "org.eclipse.jdt.internal.compiler.lookup.TypeBinding";
    public static final String TYPEBINDINGS = "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]";
    public static final String TYPEDECLARATION = "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration";

    public static Annotation getAnnotation(Class<? extends java.lang.annotation.Annotation> expectedType, TypeDeclaration decl) {
        if (Eclipse.hasAnnotations(decl)) {
            for (Annotation ann : decl.annotations) {
                if (!Patches.matchesType(ann, expectedType, decl)) continue;
                return ann;
            }
        }
        return null;
    }

    private static boolean matchesType(Annotation ann, Class<?> expectedType, TypeDeclaration decl) {
        if (ann.type == null) {
            return false;
        }
        TypeBinding tb = ann.resolvedType;
        if (tb == null && ann.type != null) {
            try {
                tb = ann.type.resolveType((BlockScope)decl.initializerScope);
            }
            catch (Exception ignore) {
                // empty catch block
            }
        }
        if (tb == null) {
            return false;
        }
        return new String(tb.readableName()).equals(expectedType.getName());
    }

    public static EclipseNode getMethodNode(AbstractMethodDeclaration decl) {
        CompilationUnitDeclaration cud = decl.scope.compilationUnitScope().referenceContext;
        EclipseAST astNode = TransformEclipseAST.getAST(cud, false);
        EclipseNode node = (EclipseNode)astNode.get(decl);
        if (node == null) {
            astNode = TransformEclipseAST.getAST(cud, true);
            node = (EclipseNode)astNode.get(decl);
        }
        return node;
    }

    public static EclipseNode getTypeNode(TypeDeclaration decl) {
        CompilationUnitDeclaration cud = decl.scope.compilationUnitScope().referenceContext;
        EclipseAST astNode = TransformEclipseAST.getAST(cud, false);
        EclipseNode node = (EclipseNode)astNode.get(decl);
        if (node == null) {
            astNode = TransformEclipseAST.getAST(cud, true);
            node = (EclipseNode)astNode.get(decl);
        }
        return node;
    }

    private Patches() {
    }
}

