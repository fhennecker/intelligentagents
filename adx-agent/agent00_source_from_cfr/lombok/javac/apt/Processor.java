/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.CompilationUnitTree
 *  com.sun.source.util.TreePath
 *  com.sun.source.util.Trees
 *  com.sun.tools.javac.processing.JavacFiler
 *  com.sun.tools.javac.processing.JavacProcessingEnvironment
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.util.Context
 */
package lombok.javac.apt;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacFiler;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import lombok.Lombok;
import lombok.core.DiagnosticsReceiver;
import lombok.javac.JavacTransformer;
import lombok.javac.apt.InterceptingJavaFileManager;
import lombok.javac.apt.MessagerDiagnosticsReceiver;

@SupportedAnnotationTypes(value={"*"})
public class Processor
extends AbstractProcessor {
    private JavacProcessingEnvironment processingEnv;
    private JavacTransformer transformer;
    private Trees trees;
    private final IdentityHashMap<JCTree.JCCompilationUnit, Void> rootsAtPhase0 = new IdentityHashMap();
    private final IdentityHashMap<JCTree.JCCompilationUnit, Void> rootsAtPhase1 = new IdentityHashMap();
    private int dummyCount = 0;

    @Override
    public void init(ProcessingEnvironment procEnv) {
        super.init(procEnv);
        this.processingEnv = (JavacProcessingEnvironment)procEnv;
        this.placePostCompileAndDontMakeForceRoundDummiesHook();
        this.transformer = new JavacTransformer(procEnv.getMessager());
        this.trees = Trees.instance((ProcessingEnvironment)procEnv);
    }

    private void placePostCompileAndDontMakeForceRoundDummiesHook() {
        this.stopJavacProcessingEnvironmentFromClosingOurClassloader();
        this.forceMultipleRoundsInNetBeansEditor();
        Context context = this.processingEnv.getContext();
        this.disablePartialReparseInNetBeansEditor(context);
        try {
            Method keyMethod = Context.class.getDeclaredMethod("key", Class.class);
            keyMethod.setAccessible(true);
            Object key = keyMethod.invoke((Object)context, JavaFileManager.class);
            Field htField = Context.class.getDeclaredField("ht");
            htField.setAccessible(true);
            Map ht = (Map)htField.get((Object)context);
            JavaFileManager originalFiler = (JavaFileManager)ht.get(key);
            if (!(originalFiler instanceof InterceptingJavaFileManager)) {
                Messager messager = this.processingEnv.getMessager();
                MessagerDiagnosticsReceiver receiver = new MessagerDiagnosticsReceiver(messager);
                InterceptingJavaFileManager newFiler = new InterceptingJavaFileManager(originalFiler, receiver);
                ht.put(key, newFiler);
                Field filerFileManagerField = JavacFiler.class.getDeclaredField("fileManager");
                filerFileManagerField.setAccessible(true);
                filerFileManagerField.set(this.processingEnv.getFiler(), newFiler);
            }
        }
        catch (Exception e) {
            throw Lombok.sneakyThrow(e);
        }
    }

    private void forceMultipleRoundsInNetBeansEditor() {
        try {
            Field f = JavacProcessingEnvironment.class.getDeclaredField("isBackgroundCompilation");
            f.setAccessible(true);
            f.set((Object)this.processingEnv, true);
        }
        catch (NoSuchFieldException e) {
        }
        catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }

    private void disablePartialReparseInNetBeansEditor(Context context) {
        try {
            Class cancelServiceClass = Class.forName("com.sun.tools.javac.util.CancelService");
            Method cancelServiceInstace = cancelServiceClass.getDeclaredMethod("instance", Context.class);
            Object cancelService = cancelServiceInstace.invoke(null, new Object[]{context});
            if (cancelService == null) {
                return;
            }
            Field parserField = cancelService.getClass().getDeclaredField("parser");
            parserField.setAccessible(true);
            Object parser = parserField.get(cancelService);
            Field supportsReparseField = parser.getClass().getDeclaredField("supportsReparse");
            supportsReparseField.setAccessible(true);
            supportsReparseField.set(parser, false);
        }
        catch (ClassNotFoundException e) {
        }
        catch (NoSuchFieldException e) {
        }
        catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }

    private void stopJavacProcessingEnvironmentFromClosingOurClassloader() {
        try {
            Field f = JavacProcessingEnvironment.class.getDeclaredField("processorClassLoader");
            f.setAccessible(true);
            ClassLoader unwrapped = (ClassLoader)f.get((Object)this.processingEnv);
            WrappingClassLoader wrapped = new WrappingClassLoader(unwrapped);
            f.set((Object)this.processingEnv, wrapped);
        }
        catch (NoSuchFieldException e) {
        }
        catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ArrayList<JCTree.JCCompilationUnit> cus;
        if (roundEnv.processingOver()) {
            return false;
        }
        if (!this.rootsAtPhase0.isEmpty()) {
            cus = new ArrayList<JCTree.JCCompilationUnit>(this.rootsAtPhase0.keySet());
            this.transformer.transform(true, this.processingEnv.getContext(), cus);
            this.rootsAtPhase1.putAll(this.rootsAtPhase0);
            this.rootsAtPhase0.clear();
        }
        for (Element element : roundEnv.getRootElements()) {
            JCTree.JCCompilationUnit unit = this.toUnit(element);
            if (unit == null || this.rootsAtPhase1.containsKey((Object)unit)) continue;
            this.rootsAtPhase0.put(unit, null);
        }
        if (!this.rootsAtPhase0.isEmpty()) {
            cus = new ArrayList<JCTree.JCCompilationUnit>(this.rootsAtPhase0.keySet());
            this.transformer.transform(false, this.processingEnv.getContext(), cus);
            JavacFiler filer = (JavacFiler)this.processingEnv.getFiler();
            if (!filer.newFiles()) {
                try {
                    JavaFileObject dummy = filer.createSourceFile((CharSequence)("lombok.dummy.ForceNewRound" + this.dummyCount++), new Element[0]);
                    Writer w = dummy.openWriter();
                    w.close();
                }
                catch (Exception e) {
                    this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Can't force a new processing round. Lombok features that require resolution won't work.");
                }
            }
        }
        return false;
    }

    private JCTree.JCCompilationUnit toUnit(Element element) {
        TreePath path;
        TreePath treePath = path = this.trees == null ? null : this.trees.getPath(element);
        if (path == null) {
            return null;
        }
        return (JCTree.JCCompilationUnit)path.getCompilationUnit();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.values()[SourceVersion.values().length - 1];
    }

    private static class WrappingClassLoader
    extends ClassLoader {
        private final ClassLoader parent;

        public WrappingClassLoader(ClassLoader parent) {
            this.parent = parent;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            return this.parent.loadClass(name);
        }

        public String toString() {
            return this.parent.toString();
        }

        @Override
        public URL getResource(String name) {
            return this.parent.getResource(name);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return this.parent.getResources(name);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return this.parent.getResourceAsStream(name);
        }

        @Override
        public void setDefaultAssertionStatus(boolean enabled) {
            this.parent.setDefaultAssertionStatus(enabled);
        }

        @Override
        public void setPackageAssertionStatus(String packageName, boolean enabled) {
            this.parent.setPackageAssertionStatus(packageName, enabled);
        }

        @Override
        public void setClassAssertionStatus(String className, boolean enabled) {
            this.parent.setClassAssertionStatus(className, enabled);
        }

        @Override
        public void clearAssertionStatus() {
            this.parent.clearAssertionStatus();
        }
    }

}

