/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.Compiler
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.impl.ITypeRequestor
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope
 *  org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 *  org.eclipse.jdt.internal.compiler.parser.Parser
 */
package lombok.eclipse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.Lombok;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.PrintAST;
import lombok.core.SpiLoadUtil;
import lombok.core.TypeLibrary;
import lombok.core.TypeResolver;
import lombok.eclipse.DeferUntilBuildFieldsAndMethods;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;

public class HandlerLibrary {
    private TypeLibrary typeLibrary = new TypeLibrary();
    private Map<String, AnnotationHandlerContainer<?>> annotationHandlers = new HashMap();
    private Collection<EclipseASTVisitor> visitorHandlers = new ArrayList<EclipseASTVisitor>();
    private static final Map<ASTNode, Object> handledMap = new WeakHashMap<ASTNode, Object>();
    private static final Object MARKER = new Object();

    public static HandlerLibrary load() {
        HandlerLibrary lib = new HandlerLibrary();
        HandlerLibrary.loadAnnotationHandlers(lib);
        HandlerLibrary.loadVisitorHandlers(lib);
        return lib;
    }

    private static void loadAnnotationHandlers(HandlerLibrary lib) {
        try {
            for (EclipseAnnotationHandler handler : SpiLoadUtil.findServices(EclipseAnnotationHandler.class, EclipseAnnotationHandler.class.getClassLoader())) {
                try {
                    Class<? extends java.lang.annotation.Annotation> annotationClass = SpiLoadUtil.findAnnotationClass(handler.getClass(), EclipseAnnotationHandler.class);
                    AnnotationHandlerContainer<? extends java.lang.annotation.Annotation> container = new AnnotationHandlerContainer<java.lang.annotation.Annotation>(handler, annotationClass);
                    String annotationClassName = container.annotationClass.getName().replace("$", ".");
                    if (lib.annotationHandlers.put(annotationClassName, container) != null) {
                        EclipseHandlerUtil.error(null, "Duplicate handlers for annotation type: " + annotationClassName, null);
                    }
                    lib.typeLibrary.addType(container.annotationClass.getName());
                }
                catch (Throwable t) {
                    EclipseHandlerUtil.error(null, "Can't load Lombok annotation handler for Eclipse: ", t);
                }
            }
        }
        catch (IOException e) {
            Lombok.sneakyThrow(e);
        }
    }

    private static void loadVisitorHandlers(HandlerLibrary lib) {
        try {
            for (EclipseASTVisitor visitor : SpiLoadUtil.findServices(EclipseASTVisitor.class, EclipseASTVisitor.class.getClassLoader())) {
                lib.visitorHandlers.add(visitor);
            }
        }
        catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }

    private boolean checkAndSetHandled(ASTNode node) {
        Map<ASTNode, Object> map = handledMap;
        synchronized (map) {
            return handledMap.put(node, MARKER) != MARKER;
        }
    }

    private boolean needsHandling(ASTNode node) {
        Map<ASTNode, Object> map = handledMap;
        synchronized (map) {
            return handledMap.get((Object)node) != MARKER;
        }
    }

    public void handleAnnotation(CompilationUnitDeclaration ast, EclipseNode annotationNode, Annotation annotation, boolean skipPrintAst) {
        String pkgName = annotationNode.getPackageDeclaration();
        Collection<String> imports = annotationNode.getImportStatements();
        TypeResolver resolver = new TypeResolver(pkgName, imports);
        TypeReference rawType = annotation.type;
        if (rawType == null) {
            return;
        }
        for (String fqn : resolver.findTypeMatches(annotationNode, this.typeLibrary, Eclipse.toQualifiedName(annotation.type.getTypeName()))) {
            AnnotationHandlerContainer container;
            boolean isPrintAST = fqn.equals(PrintAST.class.getName());
            if (isPrintAST == skipPrintAst || (container = this.annotationHandlers.get(fqn)) == null || container.deferUntilBuildFieldsAndMethods()) continue;
            if (!annotationNode.isCompleteParse() && container.deferUntilPostDiet()) {
                if (!this.needsHandling((ASTNode)annotation)) continue;
                container.preHandle(annotation, annotationNode);
                continue;
            }
            try {
                if (!this.checkAndSetHandled((ASTNode)annotation)) continue;
                container.handle(annotation, annotationNode);
            }
            catch (AnnotationValues.AnnotationValueDecodeFail fail) {
                fail.owner.setError(fail.getMessage(), fail.idx);
            }
            catch (Throwable t) {
                EclipseHandlerUtil.error(ast, String.format("Lombok annotation handler %s failed", container.handler.getClass()), t);
            }
        }
    }

    public void handleAnnotationOnBuildFieldsAndMethods(EclipseNode typeNode, Annotation annotation) {
        TypeDeclaration decl = (TypeDeclaration)typeNode.get();
        TypeBinding tb = this.resolveAnnotation(decl, annotation);
        if (tb == null) {
            return;
        }
        AnnotationHandlerContainer container = this.annotationHandlers.get(new String(tb.readableName()));
        if (container == null) {
            return;
        }
        if (!container.deferUntilBuildFieldsAndMethods()) {
            return;
        }
        EclipseNode annotationNode = (EclipseNode)((EclipseAST)typeNode.getAst()).get(annotation);
        if (this.isMethodAnnotation(annotationNode) && !typeNode.isCompleteParse() && decl.scope != null) {
            CompilationUnitScope cus = decl.scope.compilationUnitScope();
            ITypeRequestor typeRequestor = cus.environment().typeRequestor;
            if (typeRequestor instanceof Compiler) {
                Compiler c = (Compiler)typeRequestor;
                try {
                    c.parser.getMethodBodies(cus.referenceContext);
                    typeNode.rebuild();
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
        }
        try {
            if (this.checkAndSetHandled((ASTNode)annotation)) {
                container.handle(annotation, annotationNode);
            }
        }
        catch (AnnotationValues.AnnotationValueDecodeFail fail) {
            fail.owner.setError(fail.getMessage(), fail.idx);
        }
    }

    private boolean isMethodAnnotation(EclipseNode annotationNode) {
        EclipseNode parent = (EclipseNode)annotationNode.up();
        if (parent == null) {
            return false;
        }
        return parent.getKind() == AST.Kind.METHOD;
    }

    private TypeBinding resolveAnnotation(TypeDeclaration decl, Annotation ann) {
        TypeBinding tb = ann.resolvedType;
        if (tb == null && ann.type != null) {
            try {
                tb = ann.type.resolveType((BlockScope)decl.initializerScope);
            }
            catch (Exception ignore) {
                // empty catch block
            }
        }
        return tb;
    }

    public void callASTVisitors(EclipseAST ast) {
        for (EclipseASTVisitor visitor : this.visitorHandlers) {
            try {
                ast.traverse(visitor);
            }
            catch (Throwable t) {
                EclipseHandlerUtil.error((CompilationUnitDeclaration)((EclipseNode)ast.top()).get(), String.format("Lombok visitor handler %s failed", visitor.getClass()), t);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class AnnotationHandlerContainer<T extends java.lang.annotation.Annotation> {
        private EclipseAnnotationHandler<T> handler;
        private Class<T> annotationClass;

        AnnotationHandlerContainer(EclipseAnnotationHandler<T> handler, Class<T> annotationClass) {
            this.handler = handler;
            this.annotationClass = annotationClass;
        }

        public void handle(Annotation annotation, EclipseNode annotationNode) {
            AnnotationValues<T> annValues = EclipseHandlerUtil.createAnnotation(this.annotationClass, annotationNode);
            this.handler.handle(annValues, annotation, annotationNode);
        }

        public void preHandle(Annotation annotation, EclipseNode annotationNode) {
            AnnotationValues<T> annValues = EclipseHandlerUtil.createAnnotation(this.annotationClass, annotationNode);
            this.handler.preHandle(annValues, annotation, annotationNode);
        }

        public boolean deferUntilPostDiet() {
            return this.handler.getClass().isAnnotationPresent(DeferUntilPostDiet.class);
        }

        public boolean deferUntilBuildFieldsAndMethods() {
            return this.handler.getClass().isAnnotationPresent(DeferUntilBuildFieldsAndMethods.class);
        }
    }

}

