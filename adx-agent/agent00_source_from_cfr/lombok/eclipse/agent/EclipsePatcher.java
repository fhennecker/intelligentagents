/*
 * Decompiled with CFR 0_110.
 */
package lombok.eclipse.agent;

import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.core.Agent;
import lombok.patcher.Hook;
import lombok.patcher.MethodTarget;
import lombok.patcher.PatchScript;
import lombok.patcher.ScriptManager;
import lombok.patcher.StackRequest;
import lombok.patcher.TargetMatcher;
import lombok.patcher.equinox.EquinoxClassLoader;
import lombok.patcher.scripts.AddFieldScript;
import lombok.patcher.scripts.ExitFromMethodEarlyScript;
import lombok.patcher.scripts.ReplaceMethodCallScript;
import lombok.patcher.scripts.ScriptBuilder;
import lombok.patcher.scripts.SetSymbolDuringMethodCallScript;
import lombok.patcher.scripts.WrapMethodCallScript;
import lombok.patcher.scripts.WrapReturnValuesScript;

public class EclipsePatcher
extends Agent {
    private static final String ECLIPSE_SIGNATURE_CLASS = "org/eclipse/core/runtime/adaptor/EclipseStarter";

    public void runAgent(String agentArgs, Instrumentation instrumentation, boolean injected) throws Exception {
        String[] args = agentArgs == null ? new String[]{} : agentArgs.split(":");
        boolean forceEcj = false;
        boolean forceEclipse = false;
        for (String arg : args) {
            if (arg.trim().equalsIgnoreCase("ECJ")) {
                forceEcj = true;
            }
            if (!arg.trim().equalsIgnoreCase("ECLIPSE")) continue;
            forceEclipse = true;
        }
        if (forceEcj && forceEclipse) {
            forceEcj = false;
            forceEclipse = false;
        }
        boolean ecj = forceEcj ? true : (forceEclipse ? false : injected);
        EclipsePatcher.registerPatchScripts(instrumentation, injected, ecj);
    }

    private static void registerPatchScripts(Instrumentation instrumentation, boolean reloadExistingClasses, boolean ecjOnly) {
        ScriptManager sm = new ScriptManager();
        sm.registerTransformer(instrumentation);
        if (!ecjOnly) {
            EquinoxClassLoader.addPrefix("lombok.");
            EquinoxClassLoader.registerScripts(sm);
        }
        if (!ecjOnly) {
            EclipsePatcher.patchCatchReparse(sm);
            EclipsePatcher.patchIdentifierEndReparse(sm);
            EclipsePatcher.patchRetrieveEllipsisStartPosition(sm);
            EclipsePatcher.patchRetrieveRightBraceOrSemiColonPosition(sm);
            EclipsePatcher.patchSetGeneratedFlag(sm);
            EclipsePatcher.patchDomAstReparseIssues(sm);
            EclipsePatcher.patchHideGeneratedNodes(sm);
            EclipsePatcher.patchPostCompileHookEclipse(sm);
            EclipsePatcher.patchFixSourceTypeConverter(sm);
            EclipsePatcher.patchDisableLombokForCodeFormatterAndCleanup(sm);
            EclipsePatcher.patchListRewriteHandleGeneratedMethods(sm);
            EclipsePatcher.patchSyntaxAndOccurrencesHighlighting(sm);
            EclipsePatcher.patchSortMembersOperation(sm);
            EclipsePatcher.patchExtractInterface(sm);
            EclipsePatcher.patchAboutDialog(sm);
        } else {
            EclipsePatcher.patchPostCompileHookEcj(sm);
        }
        EclipsePatcher.patchAvoidReparsingGeneratedCode(sm);
        EclipsePatcher.patchLombokizeAST(sm);
        EclipsePatcher.patchEcjTransformers(sm, ecjOnly);
        EclipsePatcher.patchExtensionMethod(sm, ecjOnly);
        if (reloadExistingClasses) {
            sm.reloadClasses(instrumentation);
        }
    }

    private static void patchExtractInterface(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.SourceElementNotifier", "notifySourceElementRequestor", "void", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "org.eclipse.jdt.internal.compiler.ast.ImportReference")).methodToWrap(new Hook("org.eclipse.jdt.internal.compiler.util.HashtableOfObjectToInt", "get", "int", "java.lang.Object")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "getSourceEndFixed", "int", "int", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).requestExtra(StackRequest.PARAM1).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.ExtractInterfaceProcessor", "createMethodDeclaration", "void", "org.eclipse.jdt.internal.corext.refactoring.structure.CompilationUnitRewrite", "org.eclipse.jdt.core.dom.rewrite.ASTRewrite", "org.eclipse.jdt.core.dom.AbstractTypeDeclaration", "org.eclipse.jdt.core.dom.MethodDeclaration")).methodToWrap(new Hook("org.eclipse.jface.text.IDocument", "get", "java.lang.String", "int", "int")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "getRealMethodDeclarationSource", "java.lang.String", "java.lang.String", "java.lang.Object", "org.eclipse.jdt.core.dom.MethodDeclaration")).requestExtra(StackRequest.THIS, StackRequest.PARAM4).transplant().build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.ExtractInterfaceProcessor", "createMemberDeclarations")).target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.ExtractInterfaceProcessor", "createMethodComments")).methodToReplace(new Hook("org.eclipse.jdt.internal.corext.refactoring.structure.ASTNodeSearchUtil", "getMethodDeclarationNode", "org.eclipse.jdt.core.dom.MethodDeclaration", "org.eclipse.jdt.core.IMethod", "org.eclipse.jdt.core.dom.CompilationUnit")).replacementMethod(new Hook("lombok.eclipse.agent.PatchFixes", "getRealMethodDeclarationNode", "org.eclipse.jdt.core.dom.MethodDeclaration", "org.eclipse.jdt.core.IMethod", "org.eclipse.jdt.core.dom.CompilationUnit")).transplant().build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.core.dom.rewrite.ListRewrite", "insertFirst")).decisionMethod(new Hook("lombok.eclipse.agent.PatchFixes", "isListRewriteOnGeneratedNode", "boolean", "org.eclipse.jdt.core.dom.rewrite.ListRewrite")).request(StackRequest.THIS).transplant().build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.ExtractInterfaceProcessor", "createMethodComment")).decisionMethod(new Hook("lombok.eclipse.agent.PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).request(StackRequest.PARAM2).transplant().build());
    }

    private static void patchAboutDialog(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.core.internal.runtime.Product", "getProperty", "java.lang.String", "java.lang.String")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "addLombokNotesToEclipseAboutDialog", "java.lang.String", "java.lang.String", "java.lang.String")).request(StackRequest.RETURN_VALUE, StackRequest.PARAM1).transplant().build());
    }

    private static void patchSyntaxAndOccurrencesHighlighting(ScriptManager sm) {
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.ui.search.OccurrencesFinder", "addUsage")).target(new MethodTarget("org.eclipse.jdt.internal.ui.search.OccurrencesFinder", "addWrite")).target(new MethodTarget("org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightingReconciler$PositionCollector", "visit", "boolean", "org.eclipse.jdt.core.dom.SimpleName")).decisionMethod(new Hook("lombok.eclipse.agent.PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).valueMethod(new Hook("lombok.eclipse.agent.PatchFixes", "returnFalse", "boolean", "java.lang.Object")).request(StackRequest.PARAM1).build());
    }

    private static void patchDisableLombokForCodeFormatterAndCleanup(ScriptManager sm) {
        sm.addScript(ScriptBuilder.setSymbolDuringMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.formatter.DefaultCodeFormatter", "formatCompilationUnit")).callToWrap(new Hook("org.eclipse.jdt.internal.core.util.CodeSnippetParsingUtil", "parseCompilationUnit", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration", "char[]", "java.util.Map", "boolean")).symbol("lombok.disable").build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.ControlStatementsFix$ControlStatementFinder", "visit", "boolean", "org.eclipse.jdt.core.dom.DoStatement")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.ControlStatementsFix$ControlStatementFinder", "visit", "boolean", "org.eclipse.jdt.core.dom.EnhancedForStatement")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.ControlStatementsFix$ControlStatementFinder", "visit", "boolean", "org.eclipse.jdt.core.dom.ForStatement")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.ControlStatementsFix$ControlStatementFinder", "visit", "boolean", "org.eclipse.jdt.core.dom.IfStatement")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.ControlStatementsFix$ControlStatementFinder", "visit", "boolean", "org.eclipse.jdt.core.dom.WhileStatement")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.CodeStyleFix$ThisQualifierVisitor", "visit", "boolean", "org.eclipse.jdt.core.dom.MethodInvocation")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.CodeStyleFix$ThisQualifierVisitor", "visit", "boolean", "org.eclipse.jdt.core.dom.FieldAccess")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.CodeStyleFix$CodeStyleVisitor", "visit", "boolean", "org.eclipse.jdt.core.dom.MethodInvocation")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.CodeStyleFix$CodeStyleVisitor", "visit", "boolean", "org.eclipse.jdt.core.dom.TypeDeclaration")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.CodeStyleFix$CodeStyleVisitor", "visit", "boolean", "org.eclipse.jdt.core.dom.QualifiedName")).target(new MethodTarget("org.eclipse.jdt.internal.corext.fix.CodeStyleFix$CodeStyleVisitor", "visit", "boolean", "org.eclipse.jdt.core.dom.SimpleName")).decisionMethod(new Hook("lombok.eclipse.agent.PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).request(StackRequest.PARAM1).valueMethod(new Hook("lombok.eclipse.agent.PatchFixes", "returnFalse", "boolean", "java.lang.Object")).build());
    }

    private static void patchListRewriteHandleGeneratedMethods(ScriptManager sm) {
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.dom.rewrite.ASTRewriteAnalyzer$ListRewriter", "rewriteList")).methodToReplace(new Hook("org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent", "getChildren", "org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent[]", new String[0])).replacementMethod(new Hook("lombok.eclipse.agent.PatchFixes", "listRewriteHandleGeneratedMethods", "org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent[]", "org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent")).build());
    }

    private static void patchSortMembersOperation(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.CompilationUnit")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.CompilationUnit", "types", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.AnnotationTypeDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.AnnotationTypeDeclaration", "bodyDeclarations", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.AnonymousClassDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.AnonymousClassDeclaration", "bodyDeclarations", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.TypeDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.TypeDeclaration", "bodyDeclarations", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.EnumDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.EnumDeclaration", "bodyDeclarations", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.EnumDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.EnumDeclaration", "enumConstants", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
    }

    private static void patchDomAstReparseIssues(ScriptManager sm) {
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.dom.rewrite.ASTRewriteAnalyzer", "visit")).methodToReplace(new Hook("org.eclipse.jdt.internal.core.dom.rewrite.TokenScanner", "getTokenEndOffset", "int", "int", "int")).replacementMethod(new Hook("lombok.eclipse.agent.PatchFixes", "getTokenEndOffsetFixed", "int", "org.eclipse.jdt.internal.core.dom.rewrite.TokenScanner", "int", "int", "java.lang.Object")).requestExtra(StackRequest.PARAM1).transplant().build());
    }

    private static void patchPostCompileHookEclipse(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.builder.IncrementalImageBuilder", "writeClassFileContents")).target(new MethodTarget("org.eclipse.jdt.internal.core.builder.AbstractImageBuilder", "writeClassFileContents")).methodToWrap(new Hook("org.eclipse.jdt.internal.compiler.ClassFile", "getBytes", "byte[]", new String[0])).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "runPostCompiler", "byte[]", "byte[]", "java.lang.String")).requestExtra(StackRequest.PARAM3).transplant().build());
    }

    private static void patchPostCompileHookEcj(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl", "outputClassFiles")).methodToWrap(new Hook("javax.tools.JavaFileObject", "openOutputStream", "java.io.OutputStream", new String[0])).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "runPostCompiler", "java.io.OutputStream", "java.io.OutputStream")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.util.Util", "writeToDisk")).methodToWrap(new Hook("java.io.BufferedOutputStream", "<init>", "void", "java.io.OutputStream", "int")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "runPostCompiler", "java.io.BufferedOutputStream", "java.io.BufferedOutputStream", "java.lang.String", "java.lang.String")).requestExtra(StackRequest.PARAM2, StackRequest.PARAM3).transplant().build());
    }

    private static void patchHideGeneratedNodes(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder", "findByNode")).target(new MethodTarget("org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder", "findByBinding")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "removeGeneratedSimpleNames", "org.eclipse.jdt.core.dom.SimpleName[]", "org.eclipse.jdt.core.dom.SimpleName[]")).request(StackRequest.RETURN_VALUE).build());
        EclipsePatcher.patchRefactorScripts(sm);
        EclipsePatcher.patchFormatters(sm);
    }

    private static void patchFormatters(ScriptManager sm) {
        sm.addScript(ScriptBuilder.setSymbolDuringMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.ui.text.java.JavaFormattingStrategy", "format", "void", new String[0])).callToWrap(new Hook("org.eclipse.jdt.internal.corext.util.CodeFormatterUtil", "reformat", "org.eclipse.text.edits.TextEdit", "int", "java.lang.String", "int", "int", "int", "java.lang.String", "java.util.Map")).symbol("lombok.disable").build());
    }

    private static void patchRefactorScripts(ScriptManager sm) {
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.core.dom.rewrite.ASTRewrite", "replace")).target(new MethodTarget("org.eclipse.jdt.core.dom.rewrite.ASTRewrite", "remove")).decisionMethod(new Hook("lombok.eclipse.agent.PatchFixes", "skipRewritingGeneratedNodes", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).transplant().request(StackRequest.PARAM1).build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.rename.RenameTypeProcessor", "addConstructorRenames")).methodToWrap(new Hook("org.eclipse.jdt.core.IType", "getMethods", "org.eclipse.jdt.core.IMethod[]", new String[0])).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "removeGeneratedMethods", "org.eclipse.jdt.core.IMethod[]", "org.eclipse.jdt.core.IMethod[]")).transplant().build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.rename.TempOccurrenceAnalyzer", "visit", "boolean", "org.eclipse.jdt.core.dom.SimpleName")).target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.rename.RenameAnalyzeUtil$ProblemNodeFinder$NameNodeVisitor", "visit", "boolean", "org.eclipse.jdt.core.dom.SimpleName")).decisionMethod(new Hook("lombok.eclipse.agent.PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).valueMethod(new Hook("lombok.eclipse.agent.PatchFixes", "returnTrue", "boolean", "java.lang.Object")).request(StackRequest.PARAM1).transplant().build());
    }

    private static void patchCatchReparse(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "retrieveStartingCatchPosition")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "fixRetrieveStartingCatchPosition", "int", "int", "int")).transplant().request(StackRequest.RETURN_VALUE, StackRequest.PARAM1).build());
    }

    private static void patchIdentifierEndReparse(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "retrieveIdentifierEndPosition")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "fixRetrieveIdentifierEndPosition", "int", "int", "int")).transplant().request(StackRequest.RETURN_VALUE, StackRequest.PARAM2).build());
    }

    private static void patchRetrieveEllipsisStartPosition(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "retrieveEllipsisStartPosition")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "fixRetrieveEllipsisStartPosition", "int", "int", "int")).transplant().request(StackRequest.RETURN_VALUE, StackRequest.PARAM2).build());
    }

    private static void patchRetrieveRightBraceOrSemiColonPosition(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "retrieveRightBraceOrSemiColonPosition")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "retrieveRightBrace")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "fixRetrieveRightBraceOrSemiColonPosition", "int", "int", "int")).transplant().request(StackRequest.RETURN_VALUE, StackRequest.PARAM2).build());
    }

    private static void patchSetGeneratedFlag(ScriptManager sm) {
        sm.addScript(ScriptBuilder.addField().targetClass("org.eclipse.jdt.internal.compiler.ast.ASTNode").fieldName("$generatedBy").fieldType("Lorg/eclipse/jdt/internal/compiler/ast/ASTNode;").setPublic().setTransient().build());
        sm.addScript(ScriptBuilder.addField().targetClass("org.eclipse.jdt.core.dom.ASTNode").fieldName("$isGenerated").fieldType("Z").setPublic().setTransient().build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new TargetMatcher(){

            @Override
            public boolean matches(String classSpec, String methodName, String descriptor) {
                if (!"convert".equals(methodName)) {
                    return false;
                }
                List<String> fullDesc = MethodTarget.decomposeFullDesc(descriptor);
                if ("V".equals(fullDesc.get(0))) {
                    return false;
                }
                if (fullDesc.size() < 2) {
                    return false;
                }
                if (!fullDesc.get(1).startsWith("Lorg/eclipse/jdt/internal/compiler/ast/")) {
                    return false;
                }
                return true;
            }

            @Override
            public Collection<String> getAffectedClasses() {
                return Collections.singleton("org.eclipse.jdt.core.dom.ASTConverter");
            }
        }).request(StackRequest.PARAM1, StackRequest.RETURN_VALUE).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.ASTNode", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).request(StackRequest.PARAM2, StackRequest.RETURN_VALUE).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToFieldDeclaration", "org.eclipse.jdt.core.dom.FieldDeclaration", "org.eclipse.jdt.internal.compiler.ast.FieldDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.internal.compiler.ast.NameReference")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.internal.compiler.ast.TypeReference")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToVariableDeclarationExpression", "org.eclipse.jdt.core.dom.VariableDeclarationExpression", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToSingleVariableDeclaration", "org.eclipse.jdt.core.dom.SingleVariableDeclaration", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToVariableDeclarationFragment", "org.eclipse.jdt.core.dom.VariableDeclarationFragment", "org.eclipse.jdt.internal.compiler.ast.FieldDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToVariableDeclarationFragment", "org.eclipse.jdt.core.dom.VariableDeclarationFragment", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToVariableDeclarationStatement", "org.eclipse.jdt.core.dom.VariableDeclarationStatement", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).request(StackRequest.PARAM1, StackRequest.RETURN_VALUE).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new TargetMatcher(){

            @Override
            public boolean matches(String classSpec, String methodName, String descriptor) {
                if (!methodName.startsWith("convert")) {
                    return false;
                }
                List<String> fullDesc = MethodTarget.decomposeFullDesc(descriptor);
                if (fullDesc.size() < 2) {
                    return false;
                }
                if (!fullDesc.get(1).startsWith("Lorg/eclipse/jdt/internal/compiler/ast/")) {
                    return false;
                }
                return true;
            }

            @Override
            public Collection<String> getAffectedClasses() {
                return Collections.singleton("org.eclipse.jdt.core.dom.ASTConverter");
            }
        }).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM1).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.ASTNode", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM2).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "int", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM4).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "int", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.QualifiedName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM4).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM3).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.QualifiedName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM3).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
    }

    private static void patchAvoidReparsingGeneratedCode(ScriptManager sm) {
        String PARSER_SIG = "org.eclipse.jdt.internal.compiler.parser.Parser";
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "parse", "void", "org.eclipse.jdt.internal.compiler.ast.MethodDeclaration", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration")).decisionMethod(new Hook("lombok.eclipse.agent.PatchFixes", "checkBit24", "boolean", "java.lang.Object")).transplant().request(StackRequest.PARAM1).build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "parse", "void", "org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration", "boolean")).decisionMethod(new Hook("lombok.eclipse.agent.PatchFixes", "checkBit24", "boolean", "java.lang.Object")).transplant().request(StackRequest.PARAM1).build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "parse", "void", "org.eclipse.jdt.internal.compiler.ast.Initializer", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration")).decisionMethod(new Hook("lombok.eclipse.agent.PatchFixes", "checkBit24", "boolean", "java.lang.Object")).transplant().request(StackRequest.PARAM1).build());
    }

    private static void patchLombokizeAST(ScriptManager sm) {
        sm.addScript(ScriptBuilder.addField().targetClass("org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration").fieldName("$lombokAST").fieldType("Ljava/lang/Object;").setPublic().setTransient().build());
        String PARSER_SIG = "org.eclipse.jdt.internal.compiler.parser.Parser";
        String CUD_SIG = "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration";
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "getMethodBodies", "void", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration")).wrapMethod(new Hook("lombok.eclipse.TransformEclipseAST", "transform", "void", "org.eclipse.jdt.internal.compiler.parser.Parser", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration")).request(StackRequest.THIS, StackRequest.PARAM1).build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "endParse", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration", "int")).wrapMethod(new Hook("lombok.eclipse.TransformEclipseAST", "transform_swapped", "void", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration", "org.eclipse.jdt.internal.compiler.parser.Parser")).request(StackRequest.THIS, StackRequest.RETURN_VALUE).build());
        String CLASSSCOPE = "org.eclipse.jdt.internal.compiler.lookup.ClassScope";
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.ClassScope", "buildFieldsAndMethods", "void", new String[0])).request(StackRequest.THIS).decisionMethod(new Hook("lombok.eclipse.TransformEclipseAST", "handleAnnotationOnBuildFieldsAndMethods", "boolean", "org.eclipse.jdt.internal.compiler.lookup.ClassScope")).build());
    }

    private static void patchEcjTransformers(ScriptManager sm, boolean ecj) {
        EclipsePatcher.addPatchesForDelegate(sm, ecj);
        EclipsePatcher.addPatchesForVal(sm);
        if (!ecj) {
            EclipsePatcher.addPatchesForValEclipse(sm);
        }
        if (!ecj) {
            EclipsePatcher.addPatchesForConstructorAndDataEclipse(sm);
        }
    }

    private static void addPatchesForDelegate(ScriptManager sm, boolean ecj) {
        String CLASSSCOPE_SIG = "org.eclipse.jdt.internal.compiler.lookup.ClassScope";
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.ClassScope", "buildFieldsAndMethods", "void", new String[0])).request(StackRequest.THIS).decisionMethod(new Hook("lombok.eclipse.agent.PatchDelegatePortal", "handleDelegateForType", "boolean", "java.lang.Object")).build());
    }

    private static void addPatchesForValEclipse(ScriptManager sm) {
        String LOCALDECLARATION_SIG = "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration";
        String PARSER_SIG = "org.eclipse.jdt.internal.compiler.parser.Parser";
        String VARIABLEDECLARATIONSTATEMENT_SIG = "org.eclipse.jdt.core.dom.VariableDeclarationStatement";
        String SINGLEVARIABLEDECLARATION_SIG = "org.eclipse.jdt.core.dom.SingleVariableDeclaration";
        String ASTCONVERTER_SIG = "org.eclipse.jdt.core.dom.ASTConverter";
        sm.addScript(ScriptBuilder.addField().fieldName("$initCopy").fieldType("Lorg/eclipse/jdt/internal/compiler/ast/ASTNode;").setPublic().setTransient().targetClass("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration").build());
        sm.addScript(ScriptBuilder.addField().fieldName("$iterableCopy").fieldType("Lorg/eclipse/jdt/internal/compiler/ast/ASTNode;").setPublic().setTransient().targetClass("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration").build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "consumeExitVariableWithInitialization", "void", new String[0])).request(StackRequest.THIS).wrapMethod(new Hook("lombok.eclipse.agent.PatchValEclipsePortal", "copyInitializationOfLocalDeclaration", "void", "java.lang.Object")).build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "consumeEnhancedForStatementHeader", "void", new String[0])).request(StackRequest.THIS).wrapMethod(new Hook("lombok.eclipse.agent.PatchValEclipsePortal", "copyInitializationOfForEachIterable", "void", "java.lang.Object")).build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setModifiers", "void", "org.eclipse.jdt.core.dom.VariableDeclarationStatement", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).wrapMethod(new Hook("lombok.eclipse.agent.PatchValEclipsePortal", "addFinalAndValAnnotationToVariableDeclarationStatement", "void", "java.lang.Object", "java.lang.Object", "java.lang.Object")).request(StackRequest.THIS, StackRequest.PARAM1, StackRequest.PARAM2).build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setModifiers", "void", "org.eclipse.jdt.core.dom.SingleVariableDeclaration", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).wrapMethod(new Hook("lombok.eclipse.agent.PatchValEclipsePortal", "addFinalAndValAnnotationToSingleVariableDeclaration", "void", "java.lang.Object", "java.lang.Object", "java.lang.Object")).request(StackRequest.THIS, StackRequest.PARAM1, StackRequest.PARAM2).build());
    }

    private static void addPatchesForVal(ScriptManager sm) {
        String LOCALDECLARATION_SIG = "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration";
        String FOREACHSTATEMENT_SIG = "org.eclipse.jdt.internal.compiler.ast.ForeachStatement";
        String EXPRESSION_SIG = "org.eclipse.jdt.internal.compiler.ast.Expression";
        String BLOCKSCOPE_SIG = "org.eclipse.jdt.internal.compiler.lookup.BlockScope";
        String TYPEBINDING_SIG = "org.eclipse.jdt.internal.compiler.lookup.TypeBinding";
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration", "resolve", "void", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).request(StackRequest.THIS, StackRequest.PARAM1).decisionMethod(new Hook("lombok.eclipse.agent.PatchVal", "handleValForLocalDeclaration", "boolean", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration", "resolve", "void", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.ast.Expression", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).requestExtra(StackRequest.THIS).replacementMethod(new Hook("lombok.eclipse.agent.PatchVal", "skipResolveInitializerIfAlreadyCalled2", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.ast.Expression", "org.eclipse.jdt.internal.compiler.lookup.BlockScope", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.ForeachStatement", "resolve", "void", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.ast.Expression", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).replacementMethod(new Hook("lombok.eclipse.agent.PatchVal", "skipResolveInitializerIfAlreadyCalled", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.ast.Expression", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.ForeachStatement", "resolve", "void", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).request(StackRequest.THIS, StackRequest.PARAM1).decisionMethod(new Hook("lombok.eclipse.agent.PatchVal", "handleValForForEach", "boolean", "org.eclipse.jdt.internal.compiler.ast.ForeachStatement", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).build());
    }

    private static void addPatchesForConstructorAndDataEclipse(ScriptManager sm) {
        String SOURCEELEMENTNOTIFIER = "org.eclipse.jdt.internal.compiler.SourceElementNotifier";
        String FIELDDECLARATION = "org.eclipse.jdt.internal.compiler.ast.FieldDeclaration";
        String TYPEDECLARATION = "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration";
        String ISOURCEELEMENTREQUESTOR = "org.eclipse.jdt.internal.compiler.ISourceElementRequestor";
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.SourceElementNotifier", "notifySourceElementRequestor", "void", "org.eclipse.jdt.internal.compiler.ast.FieldDeclaration", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.ISourceElementRequestor", "exitField", "void", "int", "int", "int")).requestExtra(StackRequest.PARAM1, StackRequest.PARAM2).replacementMethod(new Hook("lombok.eclipse.agent.PatchConstructorAndDataEclipsePortal", "onSourceElementRequestor_exitField", "void", "java.lang.Object", "int", "int", "int", "org.eclipse.jdt.internal.compiler.ast.FieldDeclaration", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration")).build());
    }

    private static void patchFixSourceTypeConverter(ScriptManager sm) {
        String SOURCE_TYPE_CONVERTER_SIG = "org.eclipse.jdt.internal.compiler.parser.SourceTypeConverter";
        String I_ANNOTATABLE_SIG = "org.eclipse.jdt.core.IAnnotatable";
        String ANNOTATION_SIG = "org.eclipse.jdt.internal.compiler.ast.Annotation";
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.SourceTypeConverter", "convertAnnotations", "org.eclipse.jdt.internal.compiler.ast.Annotation[]", "org.eclipse.jdt.core.IAnnotatable")).wrapMethod(new Hook("lombok.eclipse.agent.PatchFixes", "convertAnnotations", "org.eclipse.jdt.internal.compiler.ast.Annotation[]", "org.eclipse.jdt.internal.compiler.ast.Annotation[]", "org.eclipse.jdt.core.IAnnotatable")).request(StackRequest.PARAM1, StackRequest.RETURN_VALUE).build());
    }

    private static void patchExtensionMethod(ScriptManager sm, boolean ecj) {
        String PATCH_EXTENSIONMETHOD = "lombok.eclipse.agent.PatchExtensionMethod";
        String PATCH_EXTENSIONMETHOD_COMPLETIONPROPOSAL_PORTAL = "lombok.eclipse.agent.PatchExtensionMethodCompletionProposalPortal";
        String MESSAGE_SEND_SIG = "org.eclipse.jdt.internal.compiler.ast.MessageSend";
        String TYPE_BINDING_SIG = "org.eclipse.jdt.internal.compiler.lookup.TypeBinding";
        String BLOCK_SCOPE_SIG = "org.eclipse.jdt.internal.compiler.lookup.BlockScope";
        String TYPE_BINDINGS_SIG = "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]";
        String PROBLEM_REPORTER_SIG = "org.eclipse.jdt.internal.compiler.problem.ProblemReporter";
        String METHOD_BINDING_SIG = "org.eclipse.jdt.internal.compiler.lookup.MethodBinding";
        String COMPLETION_PROPOSAL_COLLECTOR_SIG = "org.eclipse.jdt.ui.text.java.CompletionProposalCollector";
        String I_JAVA_COMPLETION_PROPOSAL_SIG = "org.eclipse.jdt.ui.text.java.IJavaCompletionProposal[]";
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.MessageSend", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).request(StackRequest.RETURN_VALUE).request(StackRequest.THIS).request(StackRequest.PARAM1).wrapMethod(new Hook("lombok.eclipse.agent.PatchExtensionMethod", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.MessageSend", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "errorNoMethodFor", "void", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]")).replacementMethod(new Hook("lombok.eclipse.agent.PatchExtensionMethod", "errorNoMethodFor", "void", "org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.MessageSend", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "invalidMethod", "void", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).replacementMethod(new Hook("lombok.eclipse.agent.PatchExtensionMethod", "invalidMethod", "void", "org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).build());
        if (!ecj) {
            sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.ui.text.java.CompletionProposalCollector", "getJavaCompletionProposals", "org.eclipse.jdt.ui.text.java.IJavaCompletionProposal[]", new String[0])).request(StackRequest.RETURN_VALUE).request(StackRequest.THIS).wrapMethod(new Hook("lombok.eclipse.agent.PatchExtensionMethodCompletionProposalPortal", "getJavaCompletionProposals", "org.eclipse.jdt.ui.text.java.IJavaCompletionProposal[]", "java.lang.Object[]", "java.lang.Object")).build());
        }
    }

}

