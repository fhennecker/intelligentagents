/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.Function;
import com.sun.jna.FunctionMapper;
import com.sun.jna.IntegerType;
import com.sun.jna.Library;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.Buffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class Native {
    private static String nativeLibraryPath = null;
    private static boolean unpacked;
    private static Map typeMappers;
    private static Map alignments;
    private static Map options;
    private static Map libraries;
    private static final Callback.UncaughtExceptionHandler DEFAULT_HANDLER;
    private static Callback.UncaughtExceptionHandler callbackExceptionHandler;
    public static final int POINTER_SIZE;
    public static final int LONG_SIZE;
    public static final int WCHAR_SIZE;
    public static final int SIZE_T_SIZE;
    private static final int TYPE_VOIDP = 0;
    private static final int TYPE_LONG = 1;
    private static final int TYPE_WCHAR_T = 2;
    private static final int TYPE_SIZE_T = 3;
    private static final Object finalizer;
    private static final ThreadLocal lastError;
    private static Map registeredClasses;
    private static Map registeredLibraries;
    private static Object unloader;
    private static final int CVT_UNSUPPORTED = -1;
    private static final int CVT_DEFAULT = 0;
    private static final int CVT_POINTER = 1;
    private static final int CVT_STRING = 2;
    private static final int CVT_STRUCTURE = 3;
    private static final int CVT_STRUCTURE_BYVAL = 4;
    private static final int CVT_BUFFER = 5;
    private static final int CVT_ARRAY_BYTE = 6;
    private static final int CVT_ARRAY_SHORT = 7;
    private static final int CVT_ARRAY_CHAR = 8;
    private static final int CVT_ARRAY_INT = 9;
    private static final int CVT_ARRAY_LONG = 10;
    private static final int CVT_ARRAY_FLOAT = 11;
    private static final int CVT_ARRAY_DOUBLE = 12;
    private static final int CVT_ARRAY_BOOLEAN = 13;
    private static final int CVT_BOOLEAN = 14;
    private static final int CVT_CALLBACK = 15;
    private static final int CVT_FLOAT = 16;
    private static final int CVT_NATIVE_MAPPED = 17;
    private static final int CVT_WSTRING = 18;
    private static final int CVT_INTEGER_TYPE = 19;
    private static final int CVT_POINTER_TYPE = 20;
    private static final int CVT_TYPE_MAPPER = 21;
    static /* synthetic */ Class class$com$sun$jna$LastErrorException;

    private static boolean deleteNativeLibrary() {
        String path = nativeLibraryPath;
        if (path == null || !unpacked) {
            return true;
        }
        File flib = new File(path);
        if (flib.delete()) {
            nativeLibraryPath = null;
            unpacked = false;
            return true;
        }
        try {
            Class class_ = Native.class;
            ClassLoader cl = class_.getClassLoader();
            Class class_2 = ClassLoader.class;
            Field f = class_2.getDeclaredField("nativeLibraries");
            f.setAccessible(true);
            List libs = (List)f.get(cl);
            Iterator i = libs.iterator();
            while (i.hasNext()) {
                Object lib = i.next();
                f = lib.getClass().getDeclaredField("name");
                f.setAccessible(true);
                String name = (String)f.get(lib);
                if (!name.equals(path) && name.indexOf(path) == -1) continue;
                Method m = lib.getClass().getDeclaredMethod("finalize", new Class[0]);
                m.setAccessible(true);
                m.invoke(lib, new Object[0]);
                nativeLibraryPath = null;
                if (unpacked && flib.exists()) {
                    if (flib.delete()) {
                        unpacked = false;
                        return true;
                    }
                    return false;
                }
                return true;
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        return false;
    }

    private Native() {
    }

    private static native void initIDs();

    public static synchronized native void setProtected(boolean var0);

    public static synchronized native boolean isProtected();

    public static synchronized native void setPreserveLastError(boolean var0);

    public static synchronized native boolean getPreserveLastError();

    public static long getWindowID(Window w) throws HeadlessException {
        return Native.getComponentID(w);
    }

    public static long getComponentID(Component c) throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException("No native windows when headless");
        }
        if (c.isLightweight()) {
            throw new IllegalArgumentException("Component must be heavyweight");
        }
        if (!c.isDisplayable()) {
            throw new IllegalStateException("Component must be displayable");
        }
        if (Platform.isX11() && System.getProperty("java.version").startsWith("1.4") && !c.isVisible()) {
            throw new IllegalStateException("Component must be visible");
        }
        return Native.getWindowHandle0(c);
    }

    public static Pointer getWindowPointer(Window w) throws HeadlessException {
        return Native.getComponentPointer(w);
    }

    public static Pointer getComponentPointer(Component c) throws HeadlessException {
        return new Pointer(Native.getComponentID(c));
    }

    private static native long getWindowHandle0(Component var0);

    public static native Pointer getDirectBufferPointer(Buffer var0);

    public static String toString(byte[] buf) {
        return Native.toString(buf, System.getProperty("jna.encoding"));
    }

    public static String toString(byte[] buf, String encoding) {
        int term;
        String s = null;
        if (encoding != null) {
            try {
                s = new String(buf, encoding);
            }
            catch (UnsupportedEncodingException e) {
                // empty catch block
            }
        }
        if (s == null) {
            s = new String(buf);
        }
        if ((term = s.indexOf(0)) != -1) {
            s = s.substring(0, term);
        }
        return s;
    }

    public static String toString(char[] buf) {
        String s = new String(buf);
        int term = s.indexOf(0);
        if (term != -1) {
            s = s.substring(0, term);
        }
        return s;
    }

    public static Object loadLibrary(Class interfaceClass) {
        return Native.loadLibrary(null, interfaceClass);
    }

    public static Object loadLibrary(Class interfaceClass, Map options) {
        return Native.loadLibrary(null, interfaceClass, options);
    }

    public static Object loadLibrary(String name, Class interfaceClass) {
        return Native.loadLibrary(name, interfaceClass, Collections.EMPTY_MAP);
    }

    public static Object loadLibrary(String name, Class interfaceClass, Map options) {
        Library.Handler handler = new Library.Handler(name, interfaceClass, options);
        ClassLoader loader = interfaceClass.getClassLoader();
        Library proxy = (Library)Proxy.newProxyInstance(loader, new Class[]{interfaceClass}, handler);
        Native.cacheOptions(interfaceClass, options, proxy);
        return proxy;
    }

    private static void loadLibraryInstance(Class cls) {
        if (cls != null && !libraries.containsKey(cls)) {
            try {
                Field[] fields = cls.getFields();
                for (int i = 0; i < fields.length; ++i) {
                    Field field = fields[i];
                    if (field.getType() != cls || !Modifier.isStatic(field.getModifiers())) continue;
                    libraries.put(cls, new WeakReference<Object>(field.get(null)));
                    break;
                }
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Could not access instance of " + cls + " (" + e + ")");
            }
        }
    }

    static Class findEnclosingLibraryClass(Class cls) {
        Class fromDeclaring;
        Class<?> declaring;
        if (cls == null) {
            return null;
        }
        Map map = libraries;
        synchronized (map) {
            if (options.containsKey(cls)) {
                return cls;
            }
        }
        Class class_ = Library.class;
        if (class_.isAssignableFrom(cls)) {
            return cls;
        }
        Class class_2 = Callback.class;
        if (class_2.isAssignableFrom(cls)) {
            cls = CallbackReference.findCallbackClass(cls);
        }
        if ((fromDeclaring = Native.findEnclosingLibraryClass(declaring = cls.getDeclaringClass())) != null) {
            return fromDeclaring;
        }
        return Native.findEnclosingLibraryClass(cls.getSuperclass());
    }

    public static Map getLibraryOptions(Class type) {
        Map map = libraries;
        synchronized (map) {
            Class interfaceClass = Native.findEnclosingLibraryClass(type);
            if (interfaceClass != null) {
                Native.loadLibraryInstance(interfaceClass);
            } else {
                interfaceClass = type;
            }
            if (!options.containsKey(interfaceClass)) {
                try {
                    Field field = interfaceClass.getField("OPTIONS");
                    field.setAccessible(true);
                    options.put(interfaceClass, field.get(null));
                }
                catch (NoSuchFieldException e) {
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("OPTIONS must be a public field of type java.util.Map (" + e + "): " + interfaceClass);
                }
            }
            return (Map)options.get(interfaceClass);
        }
    }

    public static TypeMapper getTypeMapper(Class cls) {
        Map map = libraries;
        synchronized (map) {
            Class interfaceClass = Native.findEnclosingLibraryClass(cls);
            if (interfaceClass != null) {
                Native.loadLibraryInstance(interfaceClass);
            } else {
                interfaceClass = cls;
            }
            if (!typeMappers.containsKey(interfaceClass)) {
                try {
                    Field field = interfaceClass.getField("TYPE_MAPPER");
                    field.setAccessible(true);
                    typeMappers.put(interfaceClass, field.get(null));
                }
                catch (NoSuchFieldException e) {
                    Map options = Native.getLibraryOptions(cls);
                    if (options != null && options.containsKey("type-mapper")) {
                        typeMappers.put(interfaceClass, options.get("type-mapper"));
                    }
                }
                catch (Exception e) {
                    Class class_ = TypeMapper.class;
                    throw new IllegalArgumentException("TYPE_MAPPER must be a public field of type " + class_.getName() + " (" + e + "): " + interfaceClass);
                }
            }
            return (TypeMapper)typeMappers.get(interfaceClass);
        }
    }

    public static int getStructureAlignment(Class cls) {
        Map map = libraries;
        synchronized (map) {
            Class interfaceClass = Native.findEnclosingLibraryClass(cls);
            if (interfaceClass != null) {
                Native.loadLibraryInstance(interfaceClass);
            } else {
                interfaceClass = cls;
            }
            if (!alignments.containsKey(interfaceClass)) {
                try {
                    Field field = interfaceClass.getField("STRUCTURE_ALIGNMENT");
                    field.setAccessible(true);
                    alignments.put(interfaceClass, field.get(null));
                }
                catch (NoSuchFieldException e) {
                    Map options = Native.getLibraryOptions(interfaceClass);
                    if (options != null && options.containsKey("structure-alignment")) {
                        alignments.put(interfaceClass, options.get("structure-alignment"));
                    }
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("STRUCTURE_ALIGNMENT must be a public field of type int (" + e + "): " + interfaceClass);
                }
            }
            // MONITOREXIT [0, 2] lbl23 : MonitorExitStatement: MONITOREXIT : var1_1
            Integer value = (Integer)alignments.get(interfaceClass);
            return value != null ? value : 0;
        }
    }

    static byte[] getBytes(String s) {
        try {
            return Native.getBytes(s, System.getProperty("jna.encoding"));
        }
        catch (UnsupportedEncodingException e) {
            return s.getBytes();
        }
    }

    static byte[] getBytes(String s, String encoding) throws UnsupportedEncodingException {
        if (encoding != null) {
            return s.getBytes(encoding);
        }
        return s.getBytes();
    }

    public static byte[] toByteArray(String s) {
        byte[] bytes = Native.getBytes(s);
        byte[] buf = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, buf, 0, bytes.length);
        return buf;
    }

    public static byte[] toByteArray(String s, String encoding) throws UnsupportedEncodingException {
        byte[] bytes = Native.getBytes(s, encoding);
        byte[] buf = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, buf, 0, bytes.length);
        return buf;
    }

    public static char[] toCharArray(String s) {
        char[] chars = s.toCharArray();
        char[] buf = new char[chars.length + 1];
        System.arraycopy(chars, 0, buf, 0, chars.length);
        return buf;
    }

    static String getNativeLibraryResourcePath(int osType, String arch, String name) {
        String osPrefix;
        arch = arch.toLowerCase();
        switch (osType) {
            case 2: {
                if ("i386".equals(arch)) {
                    arch = "x86";
                }
                osPrefix = "win32-" + arch;
                break;
            }
            case 0: {
                osPrefix = "darwin";
                break;
            }
            case 1: {
                if ("x86".equals(arch)) {
                    arch = "i386";
                } else if ("x86_64".equals(arch)) {
                    arch = "amd64";
                }
                osPrefix = "linux-" + arch;
                break;
            }
            case 3: {
                osPrefix = "sunos-" + arch;
                break;
            }
            default: {
                int space;
                osPrefix = name.toLowerCase();
                if ("x86".equals(arch)) {
                    arch = "i386";
                }
                if ("x86_64".equals(arch)) {
                    arch = "amd64";
                }
                if ("powerpc".equals(arch)) {
                    arch = "ppc";
                }
                if ((space = osPrefix.indexOf(" ")) != -1) {
                    osPrefix = osPrefix.substring(0, space);
                }
                osPrefix = osPrefix + "-" + arch;
            }
        }
        return "/com/sun/jna/" + osPrefix;
    }

    private static void loadNativeLibrary() {
        String libName = "jnidispatch";
        String bootPath = System.getProperty("jna.boot.library.path");
        if (bootPath != null) {
            String[] dirs = bootPath.split(File.pathSeparator);
            for (int i = 0; i < dirs.length; ++i) {
                String path = new File(new File(dirs[i]), System.mapLibraryName(libName)).getAbsolutePath();
                try {
                    System.load(path);
                    nativeLibraryPath = path;
                    return;
                }
                catch (UnsatisfiedLinkError ex) {
                    String ext;
                    String orig;
                    if (!Platform.isMac()) continue;
                    if (path.endsWith("dylib")) {
                        orig = "dylib";
                        ext = "jnilib";
                    } else {
                        orig = "jnilib";
                        ext = "dylib";
                    }
                    try {
                        path = path.substring(0, path.lastIndexOf(orig)) + ext;
                        System.load(path);
                        nativeLibraryPath = path;
                        return;
                    }
                    catch (UnsatisfiedLinkError ex) {
                        // empty catch block
                    }
                    continue;
                }
            }
        }
        try {
            System.loadLibrary(libName);
            nativeLibraryPath = libName;
        }
        catch (UnsatisfiedLinkError e) {
            Native.loadNativeLibraryFromJar();
        }
    }

    private static void loadNativeLibraryFromJar() {
        String libname = System.mapLibraryName("jnidispatch");
        String arch = System.getProperty("os.arch");
        String name = System.getProperty("os.name");
        String resourceName = Native.getNativeLibraryResourcePath(Platform.getOSType(), arch, name) + "/" + libname;
        Class class_ = Native.class;
        URL url = class_.getResource(resourceName);
        if (url == null && Platform.isMac() && resourceName.endsWith(".dylib")) {
            resourceName = resourceName.substring(0, resourceName.lastIndexOf(".dylib")) + ".jnilib";
            url = Native.class.getResource(resourceName);
        }
        if (url == null) {
            throw new UnsatisfiedLinkError("jnidispatch (" + resourceName + ") not found in resource path");
        }
        File lib = null;
        if (url.getProtocol().toLowerCase().equals("file")) {
            lib = new File(URLDecoder.decode(url.getPath()));
        } else {
            InputStream is = Native.class.getResourceAsStream(resourceName);
            if (is == null) {
                throw new Error("Can't obtain jnidispatch InputStream");
            }
            FileOutputStream fos = null;
            try {
                int count;
                lib = File.createTempFile("jna", Platform.isWindows() ? ".dll" : null);
                lib.deleteOnExit();
                ClassLoader cl = Native.class.getClassLoader();
                if (Platform.deleteNativeLibraryAfterVMExit() && (cl == null || cl.equals(ClassLoader.getSystemClassLoader()))) {
                    Runtime.getRuntime().addShutdownHook(new DeleteNativeLibrary(lib));
                }
                fos = new FileOutputStream(lib);
                byte[] buf = new byte[1024];
                while ((count = is.read(buf, 0, buf.length)) > 0) {
                    fos.write(buf, 0, count);
                }
            }
            catch (IOException e) {
                throw new Error("Failed to create temporary file for jnidispatch library: " + e);
            }
            finally {
                try {
                    is.close();
                }
                catch (IOException e) {}
                if (fos != null) {
                    try {
                        fos.close();
                    }
                    catch (IOException e) {}
                }
            }
            unpacked = true;
        }
        System.load(lib.getAbsolutePath());
        nativeLibraryPath = lib.getAbsolutePath();
    }

    private static native int sizeof(int var0);

    private static native String getNativeVersion();

    private static native String getAPIChecksum();

    public static int getLastError() {
        return (Integer)lastError.get();
    }

    public static native void setLastError(int var0);

    static void updateLastError(int e) {
        lastError.set(new Integer(e));
    }

    public static Library synchronizedLibrary(final Library library) {
        Class<?> cls = library.getClass();
        if (!Proxy.isProxyClass(cls)) {
            throw new IllegalArgumentException("Library must be a proxy class");
        }
        InvocationHandler ih = Proxy.getInvocationHandler(library);
        if (!(ih instanceof Library.Handler)) {
            throw new IllegalArgumentException("Unrecognized proxy handler: " + ih);
        }
        final Library.Handler handler = (Library.Handler)ih;
        InvocationHandler newHandler = new InvocationHandler(){

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                NativeLibrary nativeLibrary = handler.getNativeLibrary();
                synchronized (nativeLibrary) {
                    return handler.invoke(library, method, args);
                }
            }
        };
        return (Library)Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), newHandler);
    }

    public static String getWebStartLibraryPath(String libName) {
        if (System.getProperty("javawebstart.version") == null) {
            return null;
        }
        try {
            Class class_ = Native.class;
            ClassLoader cl = class_.getClassLoader();
            Method m = (Method)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    try {
                        Class class_ = Native.class$java$lang$ClassLoader == null ? (Native.class$java$lang$ClassLoader = Native.class$("java.lang.ClassLoader")) : Native.class$java$lang$ClassLoader;
                        Class[] arrclass = new Class[1];
                        Class class_2 = Native.class$java$lang$String == null ? (Native.class$java$lang$String = Native.class$("java.lang.String")) : Native.class$java$lang$String;
                        arrclass[0] = class_2;
                        Method m = class_.getDeclaredMethod("findLibrary", arrclass);
                        m.setAccessible(true);
                        return m;
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
            });
            String libpath = (String)m.invoke(cl, libName);
            if (libpath != null) {
                return new File(libpath).getParent();
            }
            String msg = "Library '" + libName + "' was not found by class loader " + cl;
            throw new UnsatisfiedLinkError(msg);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static int getNativeSize(Class type, Object value) {
        if (type.isArray()) {
            int len = Array.getLength(value);
            if (len > 0) {
                Object o = Array.get(value, 0);
                return len * Native.getNativeSize(type.getComponentType(), o);
            }
            throw new IllegalArgumentException("Arrays of length zero not allowed: " + type);
        }
        Class class_ = Structure.class;
        if (class_.isAssignableFrom(type)) {
            Class class_2 = Structure.ByReference.class;
            if (!class_2.isAssignableFrom(type)) {
                if (value == null) {
                    value = Structure.newInstance(type);
                }
                return ((Structure)value).size();
            }
        }
        try {
            return Native.getNativeSize(type);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The type \"" + type.getName() + "\" is not supported: " + e.getMessage());
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static int getNativeSize(Class cls) {
        Class class_ = NativeMapped.class;
        if (class_.isAssignableFrom(cls)) {
            cls = NativeMappedConverter.getInstance(cls).nativeType();
        }
        if (cls == Boolean.TYPE) return 4;
        Class class_2 = Boolean.class;
        if (cls == class_2) {
            return 4;
        }
        if (cls == Byte.TYPE) return 1;
        Class class_3 = Byte.class;
        if (cls == class_3) {
            return 1;
        }
        if (cls == Short.TYPE) return 2;
        Class class_4 = Short.class;
        if (cls == class_4) {
            return 2;
        }
        if (cls == Character.TYPE) return WCHAR_SIZE;
        Class class_5 = Character.class;
        if (cls == class_5) {
            return WCHAR_SIZE;
        }
        if (cls == Integer.TYPE) return 4;
        Class class_6 = Integer.class;
        if (cls == class_6) {
            return 4;
        }
        if (cls == Long.TYPE) return 8;
        Class class_7 = Long.class;
        if (cls == class_7) {
            return 8;
        }
        if (cls == Float.TYPE) return 4;
        Class class_8 = Float.class;
        if (cls == class_8) {
            return 4;
        }
        if (cls == Double.TYPE) return 8;
        Class class_9 = Double.class;
        if (cls == class_9) {
            return 8;
        }
        Class class_10 = Structure.class;
        if (class_10.isAssignableFrom(cls)) {
            Class class_11 = Structure.ByValue.class;
            if (!class_11.isAssignableFrom(cls)) return POINTER_SIZE;
            return Structure.newInstance(cls).size();
        }
        Class class_12 = Pointer.class;
        if (class_12.isAssignableFrom(cls)) return POINTER_SIZE;
        Class class_13 = Buffer.class;
        if (class_13.isAssignableFrom(cls)) return POINTER_SIZE;
        Class class_14 = Callback.class;
        if (class_14.isAssignableFrom(cls)) return POINTER_SIZE;
        Class class_15 = String.class;
        if (class_15 == cls) return POINTER_SIZE;
        Class class_16 = WString.class;
        if (class_16 != cls) throw new IllegalArgumentException("Native size for type \"" + cls.getName() + "\" is unknown");
        return POINTER_SIZE;
    }

    public static boolean isSupportedNativeType(Class cls) {
        Class class_ = Structure.class;
        if (class_.isAssignableFrom(cls)) {
            return true;
        }
        try {
            return Native.getNativeSize(cls) != 0;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void setCallbackExceptionHandler(Callback.UncaughtExceptionHandler eh) {
        callbackExceptionHandler = eh == null ? DEFAULT_HANDLER : eh;
    }

    public static Callback.UncaughtExceptionHandler getCallbackExceptionHandler() {
        return callbackExceptionHandler;
    }

    public static void register(String libName) {
        Native.register(Native.getNativeClass(Native.getCallingClass()), NativeLibrary.getInstance(libName));
    }

    public static void register(NativeLibrary lib) {
        Native.register(Native.getNativeClass(Native.getCallingClass()), lib);
    }

    static Class getNativeClass(Class cls) {
        Method[] methods = cls.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            if ((methods[i].getModifiers() & 256) == 0) continue;
            return cls;
        }
        int idx = cls.getName().lastIndexOf("$");
        if (idx != -1) {
            String name = cls.getName().substring(0, idx);
            try {
                return Native.getNativeClass(Class.forName(name, true, cls.getClassLoader()));
            }
            catch (ClassNotFoundException e) {
                // empty catch block
            }
        }
        throw new IllegalArgumentException("Can't determine class with native methods from the current context (" + cls + ")");
    }

    static Class getCallingClass() {
        Class[] context = new SecurityManager(){

            public Class[] getClassContext() {
                return super.getClassContext();
            }
        }.getClassContext();
        if (context.length < 4) {
            throw new IllegalStateException("This method must be called from the static initializer of a class");
        }
        return context[3];
    }

    public static void unregister() {
        Native.unregister(Native.getNativeClass(Native.getCallingClass()));
    }

    public static void unregister(Class cls) {
        Map map = registeredClasses;
        synchronized (map) {
            if (registeredClasses.containsKey(cls)) {
                Native.unregister(cls, (long[])registeredClasses.get(cls));
                registeredClasses.remove(cls);
                registeredLibraries.remove(cls);
            }
        }
    }

    private static native void unregister(Class var0, long[] var1);

    private static String getSignature(Class cls) {
        if (cls.isArray()) {
            return "[" + Native.getSignature(cls.getComponentType());
        }
        if (cls.isPrimitive()) {
            if (cls == Void.TYPE) {
                return "V";
            }
            if (cls == Boolean.TYPE) {
                return "Z";
            }
            if (cls == Byte.TYPE) {
                return "B";
            }
            if (cls == Short.TYPE) {
                return "S";
            }
            if (cls == Character.TYPE) {
                return "C";
            }
            if (cls == Integer.TYPE) {
                return "I";
            }
            if (cls == Long.TYPE) {
                return "J";
            }
            if (cls == Float.TYPE) {
                return "F";
            }
            if (cls == Double.TYPE) {
                return "D";
            }
        }
        return "L" + cls.getName().replace(".", "/") + ";";
    }

    private static int getConversion(Class type, TypeMapper mapper) {
        Class class_ = Boolean.class;
        if (type == class_) {
            type = Boolean.TYPE;
        } else {
            Class class_2 = Byte.class;
            if (type == class_2) {
                type = Byte.TYPE;
            } else {
                Class class_3 = Short.class;
                if (type == class_3) {
                    type = Short.TYPE;
                } else {
                    Class class_4 = Character.class;
                    if (type == class_4) {
                        type = Character.TYPE;
                    } else {
                        Class class_5 = Integer.class;
                        if (type == class_5) {
                            type = Integer.TYPE;
                        } else {
                            Class class_6 = Long.class;
                            if (type == class_6) {
                                type = Long.TYPE;
                            } else {
                                Class class_7 = Float.class;
                                if (type == class_7) {
                                    type = Float.TYPE;
                                } else {
                                    Class class_8 = Double.class;
                                    if (type == class_8) {
                                        type = Double.TYPE;
                                    } else {
                                        Class class_9 = Void.class;
                                        if (type == class_9) {
                                            type = Void.TYPE;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (mapper != null && (mapper.getFromNativeConverter(type) != null || mapper.getToNativeConverter(type) != null)) {
            return 21;
        }
        Class class_10 = Pointer.class;
        if (class_10.isAssignableFrom(type)) {
            return 1;
        }
        Class class_11 = String.class;
        if (class_11 == type) {
            return 2;
        }
        Class class_12 = WString.class;
        if (class_12.isAssignableFrom(type)) {
            return 18;
        }
        Class class_13 = Buffer.class;
        if (class_13.isAssignableFrom(type)) {
            return 5;
        }
        Class class_14 = Structure.class;
        if (class_14.isAssignableFrom(type)) {
            Class class_15 = Structure.ByValue.class;
            if (class_15.isAssignableFrom(type)) {
                return 4;
            }
            return 3;
        }
        if (type.isArray()) {
            switch (type.getName().charAt(1)) {
                case 'Z': {
                    return 13;
                }
                case 'B': {
                    return 6;
                }
                case 'S': {
                    return 7;
                }
                case 'C': {
                    return 8;
                }
                case 'I': {
                    return 9;
                }
                case 'J': {
                    return 10;
                }
                case 'F': {
                    return 11;
                }
                case 'D': {
                    return 12;
                }
            }
        }
        if (type.isPrimitive()) {
            return type == Boolean.TYPE ? 14 : 0;
        }
        Class class_16 = Callback.class;
        if (class_16.isAssignableFrom(type)) {
            return 15;
        }
        Class class_17 = IntegerType.class;
        if (class_17.isAssignableFrom(type)) {
            return 19;
        }
        Class class_18 = PointerType.class;
        if (class_18.isAssignableFrom(type)) {
            return 20;
        }
        Class class_19 = NativeMapped.class;
        if (class_19.isAssignableFrom(type)) {
            return 17;
        }
        return -1;
    }

    public static void register(Class cls, NativeLibrary lib) {
        Method[] methods = cls.getDeclaredMethods();
        ArrayList<Method> mlist = new ArrayList<Method>();
        TypeMapper mapper = (TypeMapper)lib.getOptions().get("type-mapper");
        for (int i = 0; i < methods.length; ++i) {
            if ((methods[i].getModifiers() & 256) == 0) continue;
            mlist.add(methods[i]);
        }
        long[] handles = new long[mlist.size()];
        for (int i22 = 0; i22 < handles.length; ++i22) {
            long closure_rtype;
            long rtype;
            Method method = (Method)mlist.get(i22);
            String sig = "(";
            Class<?> rclass = method.getReturnType();
            Class<?>[] ptypes = method.getParameterTypes();
            long[] atypes = new long[ptypes.length];
            long[] closure_atypes = new long[ptypes.length];
            int[] cvt = new int[ptypes.length];
            ToNativeConverter[] toNative = new ToNativeConverter[ptypes.length];
            FromNativeConverter fromNative = null;
            int rcvt = Native.getConversion(rclass, mapper);
            boolean throwLastError = false;
            switch (rcvt) {
                case -1: {
                    throw new IllegalArgumentException(rclass + " is not a supported return type (in method " + method.getName() + " in " + cls + ")");
                }
                case 21: {
                    fromNative = mapper.getFromNativeConverter(rclass);
                    closure_rtype = Structure.FFIType.get(rclass).peer;
                    rtype = Structure.FFIType.get((Object)fromNative.nativeType()).peer;
                    break;
                }
                case 17: 
                case 19: 
                case 20: {
                    closure_rtype = Structure.FFIType.get((Object)(Native.class$com$sun$jna$Pointer == null ? Native.class$((String)"com.sun.jna.Pointer") : Native.class$com$sun$jna$Pointer)).peer;
                    rtype = Structure.FFIType.get((Object)NativeMappedConverter.getInstance(rclass).nativeType()).peer;
                    break;
                }
                case 3: {
                    closure_rtype = rtype = Structure.FFIType.get((Object)(Native.class$com$sun$jna$Pointer == null ? Native.class$((String)"com.sun.jna.Pointer") : Native.class$com$sun$jna$Pointer)).peer;
                    break;
                }
                case 4: {
                    closure_rtype = Structure.FFIType.get((Object)(Native.class$com$sun$jna$Pointer == null ? Native.class$((String)"com.sun.jna.Pointer") : Native.class$com$sun$jna$Pointer)).peer;
                    rtype = Structure.FFIType.get(rclass).peer;
                    break;
                }
                default: {
                    closure_rtype = rtype = Structure.FFIType.get(rclass).peer;
                }
            }
            block19 : for (int t = 0; t < ptypes.length; ++t) {
                Class type = ptypes[t];
                sig = sig + Native.getSignature(type);
                cvt[t] = Native.getConversion(type, mapper);
                if (cvt[t] == -1) {
                    throw new IllegalArgumentException(type + " is not a supported argument type (in method " + method.getName() + " in " + cls + ")");
                }
                if (cvt[t] == 17 || cvt[t] == 19) {
                    type = NativeMappedConverter.getInstance(type).nativeType();
                } else if (cvt[t] == 21) {
                    toNative[t] = mapper.getToNativeConverter(type);
                }
                switch (cvt[t]) {
                    case 4: 
                    case 17: 
                    case 19: 
                    case 20: {
                        atypes[t] = Structure.FFIType.get((Object)type).peer;
                        closure_atypes[t] = Structure.FFIType.get((Object)(Native.class$com$sun$jna$Pointer == null ? Native.class$((String)"com.sun.jna.Pointer") : Native.class$com$sun$jna$Pointer)).peer;
                        continue block19;
                    }
                    case 21: {
                        closure_atypes[t] = type.isPrimitive() ? Structure.FFIType.get((Object)type).peer : Structure.FFIType.get((Object)(Native.class$com$sun$jna$Pointer == null ? Native.class$((String)"com.sun.jna.Pointer") : Native.class$com$sun$jna$Pointer)).peer;
                        atypes[t] = Structure.FFIType.get((Object)toNative[t].nativeType()).peer;
                        continue block19;
                    }
                    case 0: {
                        closure_atypes[t] = atypes[t] = Structure.FFIType.get((Object)type).peer;
                        continue block19;
                    }
                    default: {
                        closure_atypes[t] = atypes[t] = Structure.FFIType.get((Object)(Native.class$com$sun$jna$Pointer == null ? Native.class$((String)"com.sun.jna.Pointer") : Native.class$com$sun$jna$Pointer)).peer;
                    }
                }
            }
            sig = sig + ")";
            sig = sig + Native.getSignature(rclass);
            Class<?>[] etypes = method.getExceptionTypes();
            for (int e = 0; e < etypes.length; ++e) {
                if (!(class$com$sun$jna$LastErrorException == null ? Native.class$("com.sun.jna.LastErrorException") : class$com$sun$jna$LastErrorException).isAssignableFrom(etypes[e])) continue;
                throwLastError = true;
                break;
            }
            String name = method.getName();
            FunctionMapper fmapper = (FunctionMapper)lib.getOptions().get("function-mapper");
            if (fmapper != null) {
                name = fmapper.getFunctionName(lib, method);
            }
            Function f = lib.getFunction(name, method);
            try {
                handles[i22] = Native.registerMethod(cls, method.getName(), sig, cvt, closure_atypes, atypes, rcvt, closure_rtype, rtype, rclass, f.peer, f.getCallingConvention(), throwLastError, toNative, fromNative);
                continue;
            }
            catch (NoSuchMethodError e) {
                throw new UnsatisfiedLinkError("No method " + method.getName() + " with signature " + sig + " in " + cls);
            }
        }
        Map i22 = registeredClasses;
        synchronized (i22) {
            registeredClasses.put(cls, handles);
            registeredLibraries.put(cls, lib);
        }
        Native.cacheOptions(cls, lib.getOptions(), null);
    }

    private static void cacheOptions(Class cls, Map libOptions, Object proxy) {
        Map map = libraries;
        synchronized (map) {
            if (!libOptions.isEmpty()) {
                options.put(cls, libOptions);
            }
            if (libOptions.containsKey("type-mapper")) {
                typeMappers.put(cls, libOptions.get("type-mapper"));
            }
            if (libOptions.containsKey("structure-alignment")) {
                alignments.put(cls, libOptions.get("structure-alignment"));
            }
            if (proxy != null) {
                libraries.put(cls, new WeakReference<Object>(proxy));
            }
            if (!cls.isInterface()) {
                Class class_ = Library.class;
                if (class_.isAssignableFrom(cls)) {
                    Class<?>[] ifaces = cls.getInterfaces();
                    for (int i = 0; i < ifaces.length; ++i) {
                        if (!(class$com$sun$jna$Library == null ? Native.class$("com.sun.jna.Library") : class$com$sun$jna$Library).isAssignableFrom(ifaces[i])) continue;
                        Native.cacheOptions(ifaces[i], libOptions, proxy);
                        break;
                    }
                }
            }
        }
    }

    private static native long registerMethod(Class var0, String var1, String var2, int[] var3, long[] var4, long[] var5, int var6, long var7, long var9, Class var11, long var12, int var14, boolean var15, ToNativeConverter[] var16, FromNativeConverter var17);

    private static NativeMapped fromNative(Class cls, Object value) {
        return (NativeMapped)NativeMappedConverter.getInstance(cls).fromNative(value, new FromNativeContext(cls));
    }

    private static Class nativeType(Class cls) {
        return NativeMappedConverter.getInstance(cls).nativeType();
    }

    private static Object toNative(ToNativeConverter cvt, Object o) {
        return cvt.toNative(o, new ToNativeContext());
    }

    private static Object fromNative(FromNativeConverter cvt, Object o, Class cls) {
        return cvt.fromNative(o, new FromNativeContext(cls));
    }

    public static native long ffi_prep_cif(int var0, int var1, long var2, long var4);

    public static native void ffi_call(long var0, long var2, long var4, long var6);

    public static native long ffi_prep_closure(long var0, ffi_callback var2);

    public static native void ffi_free_closure(long var0);

    static native int initialize_ffi_type(long var0);

    public static void main(String[] args) {
        String version;
        String DEFAULT_TITLE = "Java Native Access (JNA)";
        String UNKNOWN_VERSION = "unknown - package information missing";
        Class class_ = Native.class;
        Package pkg = class_.getPackage();
        String title = pkg.getSpecificationTitle();
        if (title == null) {
            title = "Java Native Access (JNA)";
        }
        if ((version = pkg.getSpecificationVersion()) == null) {
            version = "unknown - package information missing";
        }
        title = title + " API Version " + version;
        System.out.println(title);
        version = pkg.getImplementationVersion();
        if (version == null) {
            version = "unknown - package information missing";
        }
        System.out.println("Version: " + version);
        System.out.println(" Native: " + Native.getNativeVersion() + " (" + Native.getAPIChecksum() + ")");
        System.exit(0);
    }

    static {
        typeMappers = new WeakHashMap<K, V>();
        alignments = new WeakHashMap<K, V>();
        options = new WeakHashMap<K, V>();
        libraries = new WeakHashMap<K, V>();
        callbackExceptionHandler = Native.DEFAULT_HANDLER = new Callback.UncaughtExceptionHandler(){

            public void uncaughtException(Callback c, Throwable e) {
                System.err.println("JNA: Callback " + c + " threw the following exception:");
                e.printStackTrace();
            }
        };
        Native.loadNativeLibrary();
        POINTER_SIZE = Native.sizeof(0);
        LONG_SIZE = Native.sizeof(1);
        WCHAR_SIZE = Native.sizeof(2);
        SIZE_T_SIZE = Native.sizeof(3);
        Native.initIDs();
        if (Boolean.getBoolean("jna.protected")) {
            Native.setProtected(true);
        }
        finalizer = new Object(){

            protected void finalize() {
                Native.deleteNativeLibrary();
            }
        };
        lastError = new ThreadLocal(){

            protected synchronized Object initialValue() {
                return new Integer(0);
            }
        };
        registeredClasses = new HashMap<K, V>();
        registeredLibraries = new HashMap<K, V>();
        unloader = new Object(){

            protected void finalize() {
                Map map = registeredClasses;
                synchronized (map) {
                    Iterator i = registeredClasses.entrySet().iterator();
                    while (i.hasNext()) {
                        Map.Entry e = i.next();
                        Native.unregister((Class)e.getKey(), (long[])e.getValue());
                        i.remove();
                    }
                }
            }
        };
    }

    public static interface ffi_callback {
        public void invoke(long var1, long var3, long var5);
    }

    public static class DeleteNativeLibrary
    extends Thread {
        private final File file;

        public DeleteNativeLibrary(File file) {
            this.file = file;
        }

        public void run() {
            if (!Native.deleteNativeLibrary()) {
                try {
                    Runtime.getRuntime().exec(new String[]{System.getProperty("java.home") + "/bin/java", "-cp", System.getProperty("java.class.path"), this.getClass().getName(), this.file.getAbsolutePath()});
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void main(String[] args) {
            File file;
            if (args.length == 1 && (file = new File(args[0])).exists()) {
                long start = System.currentTimeMillis();
                while (!file.delete() && file.exists()) {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {
                        // empty catch block
                    }
                    if (System.currentTimeMillis() - start <= 5000) continue;
                    System.err.println("Could not remove temp file: " + file.getAbsolutePath());
                    break;
                }
            }
            System.exit(0);
        }
    }

}

