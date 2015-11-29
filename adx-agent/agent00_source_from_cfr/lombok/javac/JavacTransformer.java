/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.List
 */
package lombok.javac;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import java.util.ArrayList;
import javax.annotation.processing.Messager;
import lombok.javac.HandlerLibrary;
import lombok.javac.JavacAST;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacNode;
import lombok.javac.LombokOptions;

public class JavacTransformer {
    private final HandlerLibrary handlers;
    private final Messager messager;

    public JavacTransformer(Messager messager) {
        this.messager = messager;
        this.handlers = HandlerLibrary.load(messager);
    }

    public void transform(boolean postResolution, Context context, java.util.List<JCTree.JCCompilationUnit> compilationUnitsRaw) {
        List compilationUnits;
        if (compilationUnitsRaw instanceof List) {
            compilationUnits = (List)compilationUnitsRaw;
        } else {
            compilationUnits = List.nil();
            for (int i = compilationUnitsRaw.size() - 1; i >= 0; --i) {
                compilationUnits = compilationUnits.prepend((Object)compilationUnitsRaw.get(i));
            }
        }
        ArrayList<JavacAST> asts = new ArrayList<JavacAST>();
        for (JCTree.JCCompilationUnit unit : compilationUnits) {
            asts.add(new JavacAST(this.messager, context, unit));
        }
        if (!postResolution) {
            this.handlers.setPreResolutionPhase();
            for (JavacAST ast2 : asts) {
                this.handlers.callASTVisitors(ast2);
                ast2.traverse(new AnnotationVisitor());
            }
        }
        if (postResolution) {
            this.handlers.setPostResolutionPhase();
            for (JavacAST ast2 : asts) {
                this.handlers.callASTVisitors(ast2);
                ast2.traverse(new AnnotationVisitor());
            }
            this.handlers.setPrintASTPhase();
            for (JavacAST ast2 : asts) {
                ast2.traverse(new AnnotationVisitor());
            }
        }
        for (JavacAST ast2 : asts) {
            if (!ast2.isChanged()) continue;
            LombokOptions.markChanged(context, (JCTree.JCCompilationUnit)((JavacNode)ast2.top()).get());
        }
    }

    private class AnnotationVisitor
    extends JavacASTAdapter {
        private AnnotationVisitor() {
        }

        @Override
        public void visitAnnotationOnType(JCTree.JCClassDecl type, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation);
        }

        @Override
        public void visitAnnotationOnField(JCTree.JCVariableDecl field, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation);
        }

        @Override
        public void visitAnnotationOnMethod(JCTree.JCMethodDecl method, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation);
        }

        @Override
        public void visitAnnotationOnMethodArgument(JCTree.JCVariableDecl argument, JCTree.JCMethodDecl method, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation);
        }

        @Override
        public void visitAnnotationOnLocal(JCTree.JCVariableDecl local, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation);
        }
    }

}

