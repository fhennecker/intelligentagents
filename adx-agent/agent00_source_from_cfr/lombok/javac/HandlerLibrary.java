/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 */
package lombok.javac;

import com.sun.tools.javac.tree.JCTree;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.PrintAST;
import lombok.core.SpiLoadUtil;
import lombok.core.TypeLibrary;
import lombok.core.TypeResolver;
import lombok.javac.JavacAST;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.ResolutionBased;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandlerLibrary {
    private final TypeLibrary typeLibrary = new TypeLibrary();
    private final Map<String, AnnotationHandlerContainer<?>> annotationHandlers = new HashMap();
    private final Collection<JavacASTVisitor> visitorHandlers = new ArrayList<JavacASTVisitor>();
    private final Messager messager;
    private int phase = 0;
    private static final Map<JCTree, Object> handledMap = new WeakHashMap<JCTree, Object>();
    private static final Object MARKER = new Object();

    public HandlerLibrary(Messager messager) {
        this.messager = messager;
    }

    public static HandlerLibrary load(Messager messager) {
        HandlerLibrary library = new HandlerLibrary(messager);
        try {
            HandlerLibrary.loadAnnotationHandlers(library);
            HandlerLibrary.loadVisitorHandlers(library);
        }
        catch (IOException e) {
            System.err.println("Lombok isn't running due to misconfigured SPI files: " + e);
        }
        return library;
    }

    private static void loadAnnotationHandlers(HandlerLibrary lib) throws IOException {
        for (JavacAnnotationHandler handler : SpiLoadUtil.findServices(JavacAnnotationHandler.class, JavacAnnotationHandler.class.getClassLoader())) {
            Class<? extends Annotation> annotationClass;
            AnnotationHandlerContainer<? extends Annotation> container = new AnnotationHandlerContainer<Annotation>(handler, annotationClass = SpiLoadUtil.findAnnotationClass(handler.getClass(), JavacAnnotationHandler.class));
            String annotationClassName = container.annotationClass.getName().replace("$", ".");
            if (lib.annotationHandlers.put(annotationClassName, container) != null) {
                lib.javacWarning("Duplicate handlers for annotation type: " + annotationClassName);
            }
            lib.typeLibrary.addType(container.annotationClass.getName());
        }
    }

    private static void loadVisitorHandlers(HandlerLibrary lib) throws IOException {
        for (JavacASTVisitor visitor : SpiLoadUtil.findServices(JavacASTVisitor.class, JavacASTVisitor.class.getClassLoader())) {
            lib.visitorHandlers.add(visitor);
        }
    }

    public void javacWarning(String message) {
        this.javacWarning(message, null);
    }

    public void javacWarning(String message, Throwable t) {
        this.messager.printMessage(Diagnostic.Kind.WARNING, message + (t == null ? "" : new StringBuilder().append(": ").append(t).toString()));
    }

    public void javacError(String message) {
        this.javacError(message, null);
    }

    public void javacError(String message, Throwable t) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, message + (t == null ? "" : new StringBuilder().append(": ").append(t).toString()));
        if (t != null) {
            t.printStackTrace();
        }
    }

    private boolean checkAndSetHandled(JCTree node) {
        Map<JCTree, Object> map = handledMap;
        synchronized (map) {
            return handledMap.put(node, MARKER) != MARKER;
        }
    }

    public void handleAnnotation(JCTree.JCCompilationUnit unit, JavacNode node, JCTree.JCAnnotation annotation) {
        TypeResolver resolver = new TypeResolver(node.getPackageDeclaration(), node.getImportStatements());
        String rawType = annotation.annotationType.toString();
        for (String fqn : resolver.findTypeMatches(node, this.typeLibrary, rawType)) {
            AnnotationHandlerContainer container;
            boolean isPrintAST = fqn.equals(PrintAST.class.getName());
            if (isPrintAST && this.phase != 2 || !isPrintAST && this.phase == 2 || (container = this.annotationHandlers.get(fqn)) == null) continue;
            try {
                if (container.isResolutionBased() && this.phase == 1 && this.checkAndSetHandled((JCTree)annotation)) {
                    container.handle(node);
                }
                if (!container.isResolutionBased() && this.phase == 0 && this.checkAndSetHandled((JCTree)annotation)) {
                    container.handle(node);
                }
                if (container.annotationClass != PrintAST.class || this.phase != 2 || !this.checkAndSetHandled((JCTree)annotation)) continue;
                container.handle(node);
            }
            catch (AnnotationValues.AnnotationValueDecodeFail fail) {
                fail.owner.setError(fail.getMessage(), fail.idx);
            }
            catch (Throwable t) {
                String sourceName = "(unknown).java";
                if (unit != null && unit.sourcefile != null) {
                    sourceName = unit.sourcefile.getName();
                }
                this.javacError(String.format("Lombok annotation handler %s failed on " + sourceName, container.handler.getClass()), t);
            }
        }
    }

    public void callASTVisitors(JavacAST ast) {
        for (JavacASTVisitor visitor : this.visitorHandlers) {
            try {
                boolean isResolutionBased = visitor.getClass().isAnnotationPresent(ResolutionBased.class);
                if (!isResolutionBased && this.phase == 0) {
                    ast.traverse(visitor);
                }
                if (!isResolutionBased || this.phase != 1) continue;
                ast.traverse(visitor);
            }
            catch (Throwable t) {
                this.javacError(String.format("Lombok visitor handler %s failed", visitor.getClass()), t);
            }
        }
    }

    public void setPreResolutionPhase() {
        this.phase = 0;
    }

    public void setPostResolutionPhase() {
        this.phase = 1;
    }

    public void setPrintASTPhase() {
        this.phase = 2;
    }

    private static class AnnotationHandlerContainer<T extends Annotation> {
        private JavacAnnotationHandler<T> handler;
        private Class<T> annotationClass;

        AnnotationHandlerContainer(JavacAnnotationHandler<T> handler, Class<T> annotationClass) {
            this.handler = handler;
            this.annotationClass = annotationClass;
        }

        public boolean isResolutionBased() {
            return this.handler.getClass().isAnnotationPresent(ResolutionBased.class);
        }

        public void handle(JavacNode node) {
            this.handler.handle(JavacHandlerUtil.createAnnotation(this.annotationClass, node), (JCTree.JCAnnotation)node.get(), node);
        }
    }

}

