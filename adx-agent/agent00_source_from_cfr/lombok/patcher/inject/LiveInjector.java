/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher.inject;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LiveInjector {
    public void injectSelf() throws IllegalStateException {
        this.inject(LiveInjector.findPathJar(null));
    }

    public void inject(String jarFile) throws IllegalStateException {
        if (System.getProperty("lombok.patcher.safeInject", null) != null) {
            this.slowInject(jarFile);
        } else {
            this.fastInject(jarFile);
        }
    }

    private void fastInject(String jarFile) throws IllegalStateException {
        try {
            Class.forName("sun.instrument.InstrumentationImpl");
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("agent injection only works on a sun-derived 1.6 or higher VM");
        }
        LibJVM libjvm = (LibJVM)Native.loadLibrary(LibJVM.class);
        PointerByReference vms = new PointerByReference();
        IntByReference found = new IntByReference();
        libjvm.JNI_GetCreatedJavaVMs(vms, 1, found);
        LibInstrument libinstrument = (LibInstrument)Native.loadLibrary(LibInstrument.class);
        Pointer vm = vms.getValue();
        libinstrument.Agent_OnAttach(vm, jarFile, null);
    }

    private void slowInject(String jarFile) throws IllegalStateException {
        String ownPidS = ManagementFactory.getRuntimeMXBean().getName();
        ownPidS = ownPidS.substring(0, ownPidS.indexOf(64));
        int ownPid = Integer.parseInt(ownPidS);
        boolean unsupportedEnvironment = false;
        Throwable exception = null;
        try {
            Class vmClass = Class.forName("com.sun.tools.attach.VirtualMachine");
            Object vm = vmClass.getMethod("attach", String.class).invoke(null, String.valueOf(ownPid));
            vmClass.getMethod("loadAgent", String.class).invoke(vm, jarFile);
        }
        catch (ClassNotFoundException e) {
            unsupportedEnvironment = true;
        }
        catch (NoSuchMethodException e) {
            unsupportedEnvironment = true;
        }
        catch (InvocationTargetException e) {
            exception = e.getCause();
            if (exception == null) {
                exception = e;
            }
        }
        catch (Throwable t) {
            exception = t;
        }
        if (unsupportedEnvironment) {
            throw new IllegalStateException("agent injection only works on a sun-derived 1.6 or higher VM");
        }
        if (exception != null) {
            throw new IllegalStateException("agent injection not supported on this platform due to unknown reason", exception);
        }
    }

    public static String findPathJar(Class<?> context) throws IllegalStateException {
        String rawName;
        String classFileName;
        String uri;
        int idx;
        if (context == null) {
            context = LiveInjector.class;
        }
        if ((uri = context.getResource(classFileName = ((idx = (rawName = context.getName()).lastIndexOf(46)) == -1 ? rawName : rawName.substring(idx + 1)) + ".class").toString()).startsWith("file:")) {
            throw new IllegalStateException("This class has been loaded from a directory and not from a jar file.");
        }
        if (!uri.startsWith("jar:file:")) {
            int idx2 = uri.indexOf(58);
            String protocol = idx2 == -1 ? "(unknown)" : uri.substring(0, idx2);
            throw new IllegalStateException("This class has been loaded remotely via the " + protocol + " protocol. Only loading from a jar on the local file system is supported.");
        }
        int idx3 = uri.indexOf(33);
        if (idx3 == -1) {
            throw new IllegalStateException("You appear to have loaded this class from a local jar file, but I can't make sense of the URL!");
        }
        try {
            String fileName = URLDecoder.decode(uri.substring("jar:file:".length(), idx3), Charset.defaultCharset().name());
            return new File(fileName).getAbsolutePath();
        }
        catch (UnsupportedEncodingException e) {
            throw new InternalError("default charset doesn't exist. Your VM is borked.");
        }
    }

    public static interface LibJVM
    extends Library {
        public int JNI_GetCreatedJavaVMs(PointerByReference var1, int var2, IntByReference var3);
    }

    public static interface LibInstrument
    extends Library {
        public void Agent_OnAttach(Pointer var1, String var2, Pointer var3);
    }

}

