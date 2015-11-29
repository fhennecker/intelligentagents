/*
 * Decompiled with CFR 0_110.
 */
package lombok.delombok;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import lombok.core.LombokApp;
import lombok.delombok.Delombok;

public class DelombokApp
extends LombokApp {
    @Override
    public int runApp(List<String> args) throws Exception {
        try {
            Class.forName("com.sun.tools.javac.main.JavaCompiler");
            this.runDirectly(args);
            return 0;
        }
        catch (ClassNotFoundException e) {
            Class delombokClass = DelombokApp.loadDelombok(args);
            if (delombokClass == null) {
                return 1;
            }
            try {
                DelombokApp.loadDelombok(args).getMethod("main", String[].class).invoke(null, args.toArray(new String[0]));
            }
            catch (InvocationTargetException e1) {
                Throwable t = e1.getCause();
                if (t instanceof Error) {
                    throw (Error)t;
                }
                if (t instanceof Exception) {
                    throw (Exception)t;
                }
                throw e1;
            }
            return 0;
        }
    }

    public static Class<?> loadDelombok(List<String> args) throws Exception {
        final File toolsJar = DelombokApp.findToolsJar();
        if (toolsJar == null) {
            String examplePath = "/path/to/tools.jar";
            if (File.separator.equals("\\")) {
                examplePath = "C:\\path\\to\\tools.jar";
            }
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                if (arg.contains(" ")) {
                    sb.append('\"').append(arg).append('\"');
                    continue;
                }
                sb.append(arg);
            }
            System.err.printf("Can't find tools.jar. Rerun delombok as: java -cp lombok.jar%1$s%2$s lombok.core.Main delombok %3$s\n", File.pathSeparator, examplePath, sb.toString());
            return null;
        }
        final JarFile toolsJarFile = new JarFile(toolsJar);
        ClassLoader loader = new ClassLoader(){

            private Class<?> loadStreamAsClass(String name, boolean resolve, InputStream in) throws ClassNotFoundException {
                Class class_;
                try {
                    int r;
                    byte[] b = new byte[65536];
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((r = in.read(b)) != -1) {
                        out.write(b, 0, r);
                    }
                    in.close();
                    byte[] data = out.toByteArray();
                    Class c = this.defineClass(name, data, 0, data.length);
                    if (resolve) {
                        this.resolveClass(c);
                    }
                    class_ = c;
                }
                catch (Throwable var9_11) {
                    try {
                        in.close();
                        throw var9_11;
                    }
                    catch (IOException e2) {
                        throw new ClassNotFoundException(name, e2);
                    }
                }
                in.close();
                return class_;
            }

            @Override
            protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                String rawName = name.replace(".", "/") + ".class";
                JarEntry entry = toolsJarFile.getJarEntry(rawName);
                if (entry == null) {
                    if (name.startsWith("lombok.")) {
                        return this.loadStreamAsClass(name, resolve, super.getResourceAsStream(rawName));
                    }
                    return super.loadClass(name, resolve);
                }
                try {
                    return this.loadStreamAsClass(name, resolve, toolsJarFile.getInputStream(entry));
                }
                catch (IOException e2) {
                    throw new ClassNotFoundException(name, e2);
                }
            }

            @Override
            public URL getResource(String name) {
                JarEntry entry = toolsJarFile.getJarEntry(name);
                if (entry == null) {
                    return super.getResource(name);
                }
                try {
                    return new URL("jar:file:" + toolsJar.getAbsolutePath() + "!" + name);
                }
                catch (MalformedURLException ignore) {
                    return null;
                }
            }

            @Override
            public Enumeration<URL> getResources(final String name) throws IOException {
                JarEntry entry = toolsJarFile.getJarEntry(name);
                final Enumeration<URL> parent = super.getResources(name);
                if (entry == null) {
                    return super.getResources(name);
                }
                return new Enumeration<URL>(){
                    private boolean first;

                    @Override
                    public boolean hasMoreElements() {
                        return !this.first || parent.hasMoreElements();
                    }

                    @Override
                    public URL nextElement() {
                        if (!this.first) {
                            this.first = true;
                            try {
                                return new URL("jar:file:" + toolsJar.getAbsolutePath() + "!" + name);
                            }
                            catch (MalformedURLException ignore) {
                                return (URL)parent.nextElement();
                            }
                        }
                        return (URL)parent.nextElement();
                    }
                };
            }

        };
        return loader.loadClass("lombok.delombok.Delombok");
    }

    private void runDirectly(List<String> args) {
        Delombok.main(args.toArray(new String[0]));
    }

    private static File findToolsJar() {
        File toolsJar;
        try {
            toolsJar = DelombokApp.findToolsJarViaRT();
            if (toolsJar != null) {
                return toolsJar;
            }
        }
        catch (Throwable ignore) {
            // empty catch block
        }
        try {
            toolsJar = DelombokApp.findToolsJarViaProperties();
            if (toolsJar != null) {
                return toolsJar;
            }
        }
        catch (Throwable ignore) {
            // empty catch block
        }
        try {
            toolsJar = DelombokApp.findToolsJarViaEnvironment();
            return toolsJar;
        }
        catch (Throwable ignore) {
            return null;
        }
    }

    private static File findToolsJarViaEnvironment() {
        for (Map.Entry<String, String> s : System.getenv().entrySet()) {
            if (!"JAVA_HOME".equalsIgnoreCase(s.getKey())) continue;
            return DelombokApp.extensiveCheckToolsJar(new File(s.getValue()));
        }
        return null;
    }

    private static File findToolsJarViaProperties() {
        File home = new File(System.getProperty("java.home", "."));
        return DelombokApp.extensiveCheckToolsJar(home);
    }

    private static File extensiveCheckToolsJar(File base) {
        File toolsJar = DelombokApp.checkToolsJar(base);
        if (toolsJar != null) {
            return toolsJar;
        }
        toolsJar = DelombokApp.checkToolsJar(new File(base, "lib"));
        if (toolsJar != null) {
            return toolsJar;
        }
        toolsJar = DelombokApp.checkToolsJar(new File(base.getParentFile(), "lib"));
        if (toolsJar != null) {
            return toolsJar;
        }
        toolsJar = DelombokApp.checkToolsJar(new File(new File(base, "jdk"), "lib"));
        if (toolsJar != null) {
            return toolsJar;
        }
        return null;
    }

    private static File findToolsJarViaRT() {
        String url = ClassLoader.getSystemClassLoader().getResource("java/lang/String.class").toString();
        if (!url.startsWith("jar:file:")) {
            return null;
        }
        int idx = (url = url.substring("jar:file:".length())).indexOf(33);
        if (idx == -1) {
            return null;
        }
        File toolsJar = DelombokApp.checkToolsJar(new File(url = url.substring(0, idx)).getParentFile());
        if (toolsJar != null) {
            return toolsJar;
        }
        toolsJar = DelombokApp.checkToolsJar(new File(new File(url).getParentFile().getParentFile().getParentFile(), "lib"));
        if (toolsJar != null) {
            return toolsJar;
        }
        return null;
    }

    private static File checkToolsJar(File d) {
        if (d.getName().equals("tools.jar") && d.isFile() && d.canRead()) {
            return d;
        }
        if ((d = new File(d, "tools.jar")).getName().equals("tools.jar") && d.isFile() && d.canRead()) {
            return d;
        }
        return null;
    }

    @Override
    public String getAppName() {
        return "delombok";
    }

    @Override
    public List<String> getAppAliases() {
        return Arrays.asList("unlombok");
    }

    @Override
    public String getAppDescription() {
        return "Applies lombok transformations without compiling your\njava code (so, 'unpacks' lombok annotations and such).";
    }

}

