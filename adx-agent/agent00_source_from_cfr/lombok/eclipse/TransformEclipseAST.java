/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope
 *  org.eclipse.jdt.internal.compiler.parser.Parser
 */
package lombok.eclipse;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Collection;
import lombok.core.AST;
import lombok.core.debug.DebugSnapshotStore;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.HandlerLibrary;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.patcher.Symbols;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.parser.Parser;

public class TransformEclipseAST {
    private final EclipseAST ast;
    private static final Field astCacheField;
    private static final HandlerLibrary handlers;
    public static boolean disableLombok;

    public static void transform_swapped(CompilationUnitDeclaration ast, Parser parser) {
        TransformEclipseAST.transform(parser, ast);
    }

    public static EclipseAST getAST(CompilationUnitDeclaration ast, boolean forceRebuild) {
        EclipseAST existing = null;
        if (astCacheField != null) {
            try {
                existing = (EclipseAST)astCacheField.get((Object)ast);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (existing == null) {
            existing = new EclipseAST(ast);
            if (astCacheField != null) {
                try {
                    astCacheField.set((Object)ast, existing);
                }
                catch (Exception ignore) {}
            }
        } else {
            existing.rebuild(forceRebuild);
        }
        return existing;
    }

    public static void transform(Parser parser, CompilationUnitDeclaration ast) {
        if (disableLombok) {
            return;
        }
        if (Symbols.hasSymbol("lombok.disable")) {
            return;
        }
        try {
            DebugSnapshotStore.INSTANCE.snapshot(ast, "transform entry", new Object[0]);
            EclipseAST existing = TransformEclipseAST.getAST(ast, false);
            new TransformEclipseAST(existing).go();
            DebugSnapshotStore.INSTANCE.snapshot(ast, "transform exit", new Object[0]);
        }
        catch (Throwable t) {
            DebugSnapshotStore.INSTANCE.snapshot(ast, "transform error: %s", t.getClass().getSimpleName());
            try {
                String message = "Lombok can't parse this source: " + t.toString();
                EclipseAST.addProblemToCompilationResult(ast, false, message, 0, 0);
                t.printStackTrace();
            }
            catch (Throwable t2) {
                try {
                    EclipseHandlerUtil.error(ast, "Can't create an error in the problems dialog while adding: " + t.toString(), t2);
                }
                catch (Throwable t3) {
                    disableLombok = true;
                }
            }
        }
    }

    public TransformEclipseAST(EclipseAST ast) {
        this.ast = ast;
    }

    public void go() {
        handlers.callASTVisitors(this.ast);
        this.ast.traverse(new AnnotationVisitor(true));
        this.ast.traverse(new AnnotationVisitor(false));
    }

    public static boolean handleAnnotationOnBuildFieldsAndMethods(ClassScope scope) {
        if (disableLombok) {
            return false;
        }
        if (Symbols.hasSymbol("lombok.disable")) {
            return false;
        }
        TypeDeclaration decl = scope.referenceContext;
        if (decl == null) {
            return false;
        }
        CompilationUnitDeclaration cud = decl.scope.compilationUnitScope().referenceContext;
        EclipseAST ast = TransformEclipseAST.getAST(cud, false);
        EclipseNode typeNode = (EclipseNode)ast.get(decl);
        if (typeNode == null) {
            ast = TransformEclipseAST.getAST(cud, true);
            typeNode = (EclipseNode)ast.get(decl);
        }
        if (typeNode == null) {
            return false;
        }
        if (decl.annotations != null) {
            for (Annotation ann : decl.annotations) {
                handlers.handleAnnotationOnBuildFieldsAndMethods(typeNode, ann);
            }
        }
        for (EclipseNode child : typeNode.down()) {
            Annotation[] annotations = null;
            if (child.getKind() == AST.Kind.METHOD) {
                annotations = ((AbstractMethodDeclaration)child.get()).annotations;
            } else if (child.getKind() == AST.Kind.FIELD) {
                annotations = ((AbstractVariableDeclaration)child.get()).annotations;
            }
            if (annotations == null) continue;
            for (Annotation ann : annotations) {
                handlers.handleAnnotationOnBuildFieldsAndMethods(typeNode, ann);
            }
        }
        return false;
    }

    static {
        disableLombok = false;
        Field f = null;
        HandlerLibrary h = null;
        try {
            h = HandlerLibrary.load();
        }
        catch (Throwable t) {
            try {
                EclipseHandlerUtil.error(null, "Problem initializing lombok", t);
            }
            catch (Throwable t2) {
                System.err.println("Problem initializing lombok");
                t.printStackTrace();
            }
            disableLombok = true;
        }
        try {
            f = CompilationUnitDeclaration.class.getDeclaredField("$lombokAST");
        }
        catch (Throwable t) {
            // empty catch block
        }
        astCacheField = f;
        handlers = h;
    }

    private static class AnnotationVisitor
    extends EclipseASTAdapter {
        private final boolean skipPrintAst;

        public AnnotationVisitor(boolean skipAllButPrintAST) {
            this.skipPrintAst = skipAllButPrintAST;
        }

        public void visitAnnotationOnField(FieldDeclaration field, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            handlers.handleAnnotation(top, annotationNode, annotation, this.skipPrintAst);
        }

        public void visitAnnotationOnMethodArgument(Argument arg, AbstractMethodDeclaration method, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            handlers.handleAnnotation(top, annotationNode, annotation, this.skipPrintAst);
        }

        public void visitAnnotationOnLocal(LocalDeclaration local, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            handlers.handleAnnotation(top, annotationNode, annotation, this.skipPrintAst);
        }

        public void visitAnnotationOnMethod(AbstractMethodDeclaration method, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            handlers.handleAnnotation(top, annotationNode, annotation, this.skipPrintAst);
        }

        public void visitAnnotationOnType(TypeDeclaration type, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            handlers.handleAnnotation(top, annotationNode, annotation, this.skipPrintAst);
        }
    }

}

