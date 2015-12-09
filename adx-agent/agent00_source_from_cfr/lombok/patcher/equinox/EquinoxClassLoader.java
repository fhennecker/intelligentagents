/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher.equinox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import lombok.patcher.Hook;
import lombok.patcher.MethodTarget;
import lombok.patcher.PatchScript;
import lombok.patcher.ScriptManager;
import lombok.patcher.StackRequest;
import lombok.patcher.TargetMatcher;
import lombok.patcher.inject.LiveInjector;
import lombok.patcher.scripts.ExitFromMethodEarlyScript;
import lombok.patcher.scripts.ScriptBuilder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EquinoxClassLoader
extends ClassLoader {
    private static Map<ClassLoader, WeakReference<EquinoxClassLoader>> hostLoaders = new WeakHashMap<ClassLoader, WeakReference<EquinoxClassLoader>>();
    private static EquinoxClassLoader coreLoader = new EquinoxClassLoader();
    private static Method resolveMethod;
    private static final List<String> prefixes;
    private static final List<String> corePrefixes;
    private final List<File> classpath = new ArrayList<File>();
    private final List<ClassLoader> subLoaders = new ArrayList<ClassLoader>();
    private final Set<String> cantFind = new HashSet<String>();
    private static final String SELF_NAME = "lombok.patcher.equinox.EquinoxClassLoader";
    private final Map<String, WeakReference<Class<?>>> defineCache = new HashMap();

    private EquinoxClassLoader() {
        this.classpath.add(new File(LiveInjector.findPathJar(EquinoxClassLoader.class)));
    }

    public static /* varargs */ void addPrefix(String ... additionalPrefixes) {
        prefixes.addAll(Arrays.asList(additionalPrefixes));
    }

    public static /* varargs */ void addCorePrefix(String ... additionalPrefixes) {
        corePrefixes.addAll(Arrays.asList(additionalPrefixes));
    }

    public void addClasspath(String file) {
        this.classpath.add(new File(file));
    }

    public void addSubLoader(ClassLoader loader) {
        if (!this.subLoaders.contains(loader)) {
            this.subLoaders.add(loader);
        }
    }

    public static void registerScripts(ScriptManager manager) {
        manager.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader", "loadClass", "java.lang.Class", "java.lang.String", "boolean")).target(new MethodTarget("org.eclipse.osgi.framework.adapter.core.AbstractClassLoader", "loadClass", "java.lang.Class", "java.lang.String", "boolean")).decisionMethod(new Hook("lombok.patcher.equinox.EquinoxClassLoader", "overrideLoadDecide", "boolean", "java.lang.ClassLoader", "java.lang.String", "boolean")).valueMethod(new Hook("lombok.patcher.equinox.EquinoxClassLoader", "overrideLoadResult", "java.lang.Class", "java.lang.ClassLoader", "java.lang.String", "boolean")).request(StackRequest.THIS, StackRequest.PARAM1, StackRequest.PARAM2).build());
    }

    private void logLoadError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        boolean controlLoad = false;
        WeakReference ref = this.defineCache.get(name);
        if (ref != null) {
            Class result = ref.get();
            if (result != null) {
                return result;
            }
            this.defineCache.remove(name);
        }
        for (String corePrefix : corePrefixes) {
            if (!name.startsWith(corePrefix)) continue;
            if (this != coreLoader) {
                return coreLoader.loadClass(name, resolve);
            }
            controlLoad = true;
            break;
        }
        for (String prefix : prefixes) {
            if (!name.startsWith(prefix)) continue;
            controlLoad = true;
            break;
        }
        Class c = null;
        for (File file : this.classpath) {
            if (file.isFile()) {
                try {
                    JarFile jf = new JarFile(file);
                    ZipEntry entry = jf.getEntry(name);
                    if (entry == null) continue;
                    byte[] classData = EquinoxClassLoader.readStream(jf.getInputStream(entry));
                    c = this.defineClass(name, classData, 0, classData.length);
                    this.defineCache.put(name, new WeakReference(c));
                    break;
                }
                catch (IOException e) {
                    this.logLoadError(e);
                    continue;
                }
            }
            File target = new File(file, name);
            if (!target.exists()) continue;
            try {
                byte[] classData = EquinoxClassLoader.readStream(new FileInputStream(target));
                c = this.defineClass(name, classData, 0, classData.length);
                this.defineCache.put(name, new WeakReference(c));
                break;
            }
            catch (IOException e) {
                this.logLoadError(e);
                continue;
            }
        }
        if (c != null) {
            if (resolve) {
                this.resolveClass(c);
            }
            return c;
        }
        if (controlLoad) {
            try {
                byte[] classData = EquinoxClassLoader.readStream(super.getResourceAsStream(name.replace(".", "/") + ".class"));
                c = this.defineClass(name, classData, 0, classData.length);
                this.defineCache.put(name, new WeakReference(c));
                if (resolve) {
                    this.resolveClass(c);
                }
                return c;
            }
            catch (Exception ignore) {
            }
            catch (UnsupportedClassVersionError e) {
                System.err.println("BAD CLASS VERSION TRYING TO LOAD: " + name);
                throw e;
            }
        } else {
            try {
                return super.loadClass(name, resolve);
            }
            catch (ClassNotFoundException ignore) {
                // empty catch block
            }
        }
        this.cantFind.add(name);
        for (ClassLoader subLoader : this.subLoaders) {
            try {
                c = subLoader.loadClass(name);
                if (resolve) {
                    if (resolveMethod == null) {
                        try {
                            Method m = ClassLoader.class.getDeclaredMethod("resolveClass", Class.class);
                            m.setAccessible(true);
                            resolveMethod = m;
                        }
                        catch (Exception ignore) {
                            // empty catch block
                        }
                    }
                    if (resolveMethod != null) {
                        try {
                            resolveMethod.invoke(subLoader, c);
                        }
                        catch (Exception ignore) {
                            // empty catch block
                        }
                    }
                }
                return c;
            }
            catch (ClassNotFoundException ignore) {
                continue;
            }
        }
        throw new ClassNotFoundException(name);
    }

    private static byte[] readStream(InputStream in) throws IOException {
        try {
            int r22;
            byte[] b = new byte[4096];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((r22 = in.read(b)) != -1) {
                baos.write(b, 0, r22);
            }
            byte[] r22 = baos.toByteArray();
            return r22;
        }
        finally {
            in.close();
        }
    }

    private static EquinoxClassLoader getHostLoader(ClassLoader original) {
        Map<ClassLoader, WeakReference<EquinoxClassLoader>> map = hostLoaders;
        synchronized (map) {
            EquinoxClassLoader ecl;
            WeakReference<EquinoxClassLoader> ref = hostLoaders.get(original);
            EquinoxClassLoader equinoxClassLoader = ecl = ref == null ? null : ref.get();
            if (ecl != null) {
                return ecl;
            }
            ecl = new EquinoxClassLoader();
            ecl.addSubLoader(original);
            hostLoaders.put(original, new WeakReference<EquinoxClassLoader>(ecl));
            return ecl;
        }
    }

    public static boolean overrideLoadDecide(ClassLoader original, String name, boolean resolve) {
        EquinoxClassLoader hostLoader = EquinoxClassLoader.getHostLoader(original);
        if (hostLoader.cantFind.contains(name)) {
            return false;
        }
        for (String prefix : prefixes) {
            if (!name.startsWith(prefix)) continue;
            return true;
        }
        return false;
    }

    public static Class<?> overrideLoadResult(ClassLoader original, String name, boolean resolve) throws ClassNotFoundException {
        EquinoxClassLoader hostLoader = EquinoxClassLoader.getHostLoader(original);
        hostLoader.addSubLoader(original);
        return hostLoader.loadClass(name, resolve);
    }

    static {
        prefixes = new ArrayList<String>();
        corePrefixes = new ArrayList<String>();
        corePrefixes.add("lombok.patcher.");
    }
}

