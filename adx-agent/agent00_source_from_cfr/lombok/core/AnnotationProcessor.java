/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import lombok.patcher.inject.LiveInjector;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@SupportedAnnotationTypes(value={"*"})
public class AnnotationProcessor
extends AbstractProcessor {
    private final List<ProcessorDescriptor> registered = Arrays.asList(new JavacDescriptor(), new EcjDescriptor());
    private final List<ProcessorDescriptor> active = new ArrayList<ProcessorDescriptor>();
    private final List<String> delayedWarnings = new ArrayList<String>();
    private static final Map<ClassLoader, Boolean> lombokAlreadyAddedTo = new WeakHashMap<ClassLoader, Boolean>();

    private static String trace(Throwable t) {
        StringWriter w = new StringWriter();
        t.printStackTrace(new PrintWriter(w, true));
        return w.toString();
    }

    @Override
    public void init(ProcessingEnvironment procEnv) {
        super.init(procEnv);
        for (ProcessorDescriptor proc : this.registered) {
            if (!proc.want(procEnv, this.delayedWarnings)) continue;
            this.active.add(proc);
        }
        if (this.active.isEmpty() && this.delayedWarnings.isEmpty()) {
            StringBuilder supported = new StringBuilder();
            for (ProcessorDescriptor proc2 : this.registered) {
                if (supported.length() > 0) {
                    supported.append(", ");
                }
                supported.append(proc2.getName());
            }
            procEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format("You aren't using a compiler supported by lombok, so lombok will not work and has been disabled.\nYour processor is: %s\nLombok supports: %s", procEnv.getClass().getName(), supported));
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> rootElements;
        if (!this.delayedWarnings.isEmpty() && !(rootElements = roundEnv.getRootElements()).isEmpty()) {
            Element firstRoot = rootElements.iterator().next();
            for (String warning : this.delayedWarnings) {
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, warning, firstRoot);
            }
            this.delayedWarnings.clear();
        }
        boolean handled = false;
        for (ProcessorDescriptor proc : this.active) {
            handled |= proc.process(annotations, roundEnv);
        }
        return handled;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.values()[SourceVersion.values().length - 1];
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class EcjDescriptor
    extends ProcessorDescriptor {
        EcjDescriptor() {
        }

        @Override
        String getName() {
            return "ECJ";
        }

        @Override
        boolean want(ProcessingEnvironment procEnv, List<String> delayedWarnings) {
            if (!procEnv.getClass().getName().startsWith("org.eclipse.jdt.")) {
                return false;
            }
            return true;
        }

        @Override
        boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class JavacDescriptor
    extends ProcessorDescriptor {
        private Processor processor;

        JavacDescriptor() {
        }

        @Override
        String getName() {
            return "sun/apple javac 1.6";
        }

        @Override
        boolean want(ProcessingEnvironment procEnv, List<String> delayedWarnings) {
            if (!procEnv.getClass().getName().equals("com.sun.tools.javac.processing.JavacProcessingEnvironment")) {
                return false;
            }
            try {
                ClassLoader classLoader = this.findAndPatchClassLoader(procEnv);
                this.processor = (Processor)Class.forName("lombok.javac.apt.Processor", false, classLoader).newInstance();
            }
            catch (Exception e) {
                delayedWarnings.add("You found a bug in lombok; lombok.javac.apt.Processor is not available. Lombok will not run during this compilation: " + AnnotationProcessor.trace(e));
                return false;
            }
            catch (NoClassDefFoundError e) {
                delayedWarnings.add("Can't load javac processor due to (most likely) a class loader problem: " + AnnotationProcessor.trace(e));
                return false;
            }
            try {
                this.processor.init(procEnv);
            }
            catch (Exception e) {
                delayedWarnings.add("lombok.javac.apt.Processor could not be initialized. Lombok will not run during this compilation: " + AnnotationProcessor.trace(e));
                return false;
            }
            catch (NoClassDefFoundError e) {
                delayedWarnings.add("Can't initialize javac processor due to (most likely) a class loader problem: " + AnnotationProcessor.trace(e));
                return false;
            }
            return true;
        }

        private ClassLoader findAndPatchClassLoader(ProcessingEnvironment procEnv) throws Exception {
            ClassLoader environmentClassLoader = procEnv.getClass().getClassLoader();
            if (environmentClassLoader != null && environmentClassLoader.getClass().getCanonicalName().equals("org.codehaus.plexus.compiler.javac.IsolatedClassLoader")) {
                if (lombokAlreadyAddedTo.put(environmentClassLoader, true) == null) {
                    Method m = environmentClassLoader.getClass().getDeclaredMethod("addURL", URL.class);
                    URL selfUrl = new File(LiveInjector.findPathJar(AnnotationProcessor.class)).toURI().toURL();
                    m.invoke(environmentClassLoader, selfUrl);
                }
                return environmentClassLoader;
            }
            ClassLoader ourClassLoader = JavacDescriptor.class.getClassLoader();
            if (ourClassLoader == null) {
                return ClassLoader.getSystemClassLoader();
            }
            return ourClassLoader;
        }

        @Override
        boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return this.processor.process(annotations, roundEnv);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static abstract class ProcessorDescriptor {
        ProcessorDescriptor() {
        }

        abstract boolean want(ProcessingEnvironment var1, List<String> var2);

        abstract String getName();

        abstract boolean process(Set<? extends TypeElement> var1, RoundEnvironment var2);
    }

}

