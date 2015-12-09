/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarFile;
import lombok.patcher.PatchScript;

public class ScriptManager {
    private final List<PatchScript> scripts = new ArrayList<PatchScript>();
    private static final String DEBUG_PATCHING = System.getProperty("lombok.patcher.patchDebugDir", null);
    private final ClassFileTransformer transformer;

    public ScriptManager() {
        this.transformer = new ClassFileTransformer(){

            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                Object byteCode = classfileBuffer;
                boolean patched = false;
                if (className == null) {
                    return null;
                }
                for (PatchScript script : ScriptManager.this.scripts) {
                    Object transformed = null;
                    try {
                        transformed = script.patch(className, (byte[])byteCode);
                    }
                    catch (Throwable t) {
                        System.err.printf("Transformer %s failed on %s. Trace:\n", script.getPatchScriptName(), className);
                        t.printStackTrace();
                        transformed = null;
                    }
                    if (transformed == null) continue;
                    patched = true;
                    byteCode = transformed;
                }
                if (patched && DEBUG_PATCHING != null) {
                    try {
                        File f = new File(DEBUG_PATCHING, className + ".class");
                        f.getParentFile().mkdirs();
                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write((byte[])byteCode);
                        fos.close();
                    }
                    catch (IOException e) {
                        System.err.println("Can't log patch result.");
                        e.printStackTrace();
                    }
                }
                return patched ? byteCode : (Object)null;
            }
        };
    }

    public void addScript(PatchScript script) {
        this.scripts.add(script);
    }

    public void registerTransformer(Instrumentation instrumentation) {
        try {
            Method m = Instrumentation.class.getMethod("addTransformer", ClassFileTransformer.class, Boolean.TYPE);
            m.invoke(instrumentation, this.transformer, true);
        }
        catch (Throwable t) {
            instrumentation.addTransformer(this.transformer);
        }
    }

    public void reloadClasses(Instrumentation instrumentation) {
        HashSet<String> toReload = new HashSet<String>();
        for (PatchScript s : this.scripts) {
            toReload.addAll(s.getClassesToReload());
        }
        for (Class c : instrumentation.getAllLoadedClasses()) {
            if (!toReload.contains(c.getName())) continue;
            try {
                Instrumentation.class.getMethod("retransformClasses", Class[].class).invoke(instrumentation, {c});
                continue;
            }
            catch (InvocationTargetException e) {
                throw new UnsupportedOperationException("The " + c.getName() + " class is already loaded and cannot be modified. " + "You'll have to restart the application to patch it. Reason: " + e.getCause());
            }
            catch (Throwable t) {
                throw new UnsupportedOperationException("This appears to be a JVM v1.5, which cannot reload already loaded classes. You'll have to restart the application to patch it.");
            }
        }
    }

    private static boolean classpathContains(String property, String path) {
        String pathCanonical = new File(path).getAbsolutePath();
        try {
            pathCanonical = new File(path).getCanonicalPath();
        }
        catch (Exception ignore) {
            // empty catch block
        }
        for (String existingPath : System.getProperty(property, "").split(File.pathSeparator)) {
            String p = new File(existingPath).getAbsolutePath();
            try {
                p = new File(existingPath).getCanonicalPath();
            }
            catch (Throwable ignore) {
                // empty catch block
            }
            if (!p.equals(pathCanonical)) continue;
            return true;
        }
        return false;
    }

    public void addToSystemClasspath(Instrumentation instrumentation, String pathToJar) {
        if (pathToJar == null) {
            throw new NullPointerException("pathToJar");
        }
        if (ScriptManager.classpathContains("sun.boot.class.path", pathToJar)) {
            return;
        }
        if (ScriptManager.classpathContains("java.class.path", pathToJar)) {
            return;
        }
        try {
            Method m = instrumentation.getClass().getMethod("appendToSystemClassLoaderSearch", JarFile.class);
            m.invoke(instrumentation, new JarFile(pathToJar));
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException("Adding to the classloader path is not possible on a v1.5 JVM");
        }
        catch (IOException e) {
            throw new IllegalArgumentException("not found or not a jar file: " + pathToJar, e);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("appendToSystemClassLoaderSearch isn't public? This isn't a JVM...");
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new IllegalArgumentException("Unknown issue: " + cause, cause);
        }
    }

    public void addToBootClasspath(Instrumentation instrumentation, String pathToJar) {
        if (pathToJar == null) {
            throw new NullPointerException("pathToJar");
        }
        if (ScriptManager.classpathContains("sun.boot.class.path", pathToJar)) {
            return;
        }
        try {
            Method m = instrumentation.getClass().getMethod("appendToBootstrapClassLoaderSearch", JarFile.class);
            m.invoke(instrumentation, new JarFile(pathToJar));
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException("Adding to the classloader path is not possible on a v1.5 JVM");
        }
        catch (IOException e) {
            throw new IllegalArgumentException("not found or not a jar file: " + pathToJar, e);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("appendToSystemClassLoaderSearch isn't public? This isn't a JVM...");
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new IllegalArgumentException("Unknown issue: " + cause, cause);
        }
    }

}

