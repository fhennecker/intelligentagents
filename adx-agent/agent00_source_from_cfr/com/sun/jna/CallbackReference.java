/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  [Lcom.sun.jna.WString;
 *  [Ljava.lang.Object;
 *  [Ljava.lang.String;
 */
package com.sun.jna;

import [Lcom.sun.jna.WString;;
import [Ljava.lang.Object;;
import [Ljava.lang.String;;
import com.sun.jna.AltCallingConvention;
import com.sun.jna.Callback;
import com.sun.jna.CallbackParameterContext;
import com.sun.jna.CallbackProxy;
import com.sun.jna.CallbackResultContext;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.NativeString;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

class CallbackReference
extends WeakReference {
    static final Map callbackMap = new WeakHashMap<K, V>();
    static final Map directCallbackMap = new WeakHashMap<K, V>();
    static final Map allocations = new WeakHashMap<K, V>();
    private static final Method PROXY_CALLBACK_METHOD;
    Pointer cbstruct;
    CallbackProxy proxy;
    Method method;

    public static Callback getCallback(Class type, Pointer p) {
        return CallbackReference.getCallback(type, p, false);
    }

    private static Callback getCallback(Class type, Pointer p, boolean direct) {
        Map map;
        if (p == null) {
            return null;
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Callback type must be an interface");
        }
        Map map2 = map = direct ? directCallbackMap : callbackMap;
        synchronized (map2) {
            Iterator i = map.keySet().iterator();
            while (i.hasNext()) {
                Pointer cbp;
                CallbackReference cbref;
                Callback cb = (Callback)i.next();
                if (!type.isAssignableFrom(cb.getClass()) || !p.equals(cbp = (cbref = (CallbackReference)map.get(cb)) != null ? cbref.getTrampoline() : CallbackReference.getNativeFunctionPointer(cb))) continue;
                return cb;
            }
            Class class_ = AltCallingConvention.class;
            int ctype = class_.isAssignableFrom(type) ? 1 : 0;
            HashMap<String, Method> foptions = new HashMap<String, Method>();
            Map options = Native.getLibraryOptions(type);
            if (options != null) {
                foptions.putAll(options);
            }
            foptions.put("invoking-method", CallbackReference.getCallbackMethod(type));
            NativeFunctionHandler h = new NativeFunctionHandler(p, ctype, foptions);
            Callback cb = (Callback)Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, h);
            map.put(cb, null);
            return cb;
        }
    }

    private CallbackReference(Callback callback, int callingConvention, boolean direct) {
        boolean ppc;
        super(callback);
        TypeMapper mapper = Native.getTypeMapper(callback.getClass());
        String arch = System.getProperty("os.arch").toLowerCase();
        boolean bl = ppc = "ppc".equals(arch) || "powerpc".equals(arch);
        if (direct) {
            Method m = CallbackReference.getCallbackMethod(callback);
            Class<?>[] ptypes = m.getParameterTypes();
            for (int i = 0; i < ptypes.length; ++i) {
                if (ppc && (ptypes[i] == Float.TYPE || ptypes[i] == Double.TYPE)) {
                    direct = false;
                    break;
                }
                if (mapper == null || mapper.getFromNativeConverter(ptypes[i]) == null) continue;
                direct = false;
                break;
            }
            if (mapper != null && mapper.getToNativeConverter(m.getReturnType()) != null) {
                direct = false;
            }
        }
        if (direct) {
            this.method = CallbackReference.getCallbackMethod(callback);
            Class[] nativeParamTypes = this.method.getParameterTypes();
            Class returnType = this.method.getReturnType();
            this.cbstruct = CallbackReference.createNativeCallback(callback, this.method, nativeParamTypes, returnType, callingConvention, true);
        } else {
            this.proxy = callback instanceof CallbackProxy ? (CallbackProxy)callback : new DefaultCallbackProxy(CallbackReference.getCallbackMethod(callback), mapper);
            Class[] nativeParamTypes = this.proxy.getParameterTypes();
            Class returnType = this.proxy.getReturnType();
            if (mapper != null) {
                for (int i = 0; i < nativeParamTypes.length; ++i) {
                    FromNativeConverter rc = mapper.getFromNativeConverter(nativeParamTypes[i]);
                    if (rc == null) continue;
                    nativeParamTypes[i] = rc.nativeType();
                }
                ToNativeConverter tn = mapper.getToNativeConverter(returnType);
                if (tn != null) {
                    returnType = tn.nativeType();
                }
            }
            for (int i = 0; i < nativeParamTypes.length; ++i) {
                nativeParamTypes[i] = this.getNativeType(nativeParamTypes[i]);
                if (CallbackReference.isAllowableNativeType(nativeParamTypes[i])) continue;
                String msg = "Callback argument " + nativeParamTypes[i] + " requires custom type conversion";
                throw new IllegalArgumentException(msg);
            }
            if (!CallbackReference.isAllowableNativeType(returnType = this.getNativeType(returnType))) {
                String msg = "Callback return type " + returnType + " requires custom type conversion";
                throw new IllegalArgumentException(msg);
            }
            this.cbstruct = CallbackReference.createNativeCallback(this.proxy, PROXY_CALLBACK_METHOD, nativeParamTypes, returnType, callingConvention, false);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Class getNativeType(Class cls) {
        Class class_ = Structure.class;
        if (class_.isAssignableFrom(cls)) {
            Structure.newInstance(cls);
            Class class_2 = Structure.ByValue.class;
            if (class_2.isAssignableFrom(cls)) return cls;
            Class class_3 = Pointer.class;
            return class_3;
        }
        Class class_4 = NativeMapped.class;
        if (class_4.isAssignableFrom(cls)) {
            return NativeMappedConverter.getInstance(cls).nativeType();
        }
        Class class_5 = String.class;
        if (cls != class_5) {
            Class class_6 = WString.class;
            if (cls != class_6) {
                Class class_7 = String;.class;
                if (cls != class_7) {
                    Class class_8 = WString;.class;
                    if (cls != class_8) {
                        Class class_9 = Callback.class;
                        if (!class_9.isAssignableFrom(cls)) return cls;
                    }
                }
            }
        }
        Class class_10 = Pointer.class;
        return class_10;
    }

    private static Method checkMethod(Method m) {
        if (m.getParameterTypes().length > 256) {
            String msg = "Method signature exceeds the maximum parameter count: " + m;
            throw new UnsupportedOperationException(msg);
        }
        return m;
    }

    static Class findCallbackClass(Class type) {
        Class class_ = Callback.class;
        if (!class_.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not derived from com.sun.jna.Callback");
        }
        if (type.isInterface()) {
            return type;
        }
        Class<?>[] ifaces = type.getInterfaces();
        for (int i = 0; i < ifaces.length; ++i) {
            if (!(class$com$sun$jna$Callback == null ? CallbackReference.class$("com.sun.jna.Callback") : class$com$sun$jna$Callback).isAssignableFrom(ifaces[i])) continue;
            try {
                CallbackReference.getCallbackMethod(ifaces[i]);
                return ifaces[i];
            }
            catch (IllegalArgumentException e) {
                break;
            }
        }
        if (Callback.class.isAssignableFrom(type.getSuperclass())) {
            return CallbackReference.findCallbackClass(type.getSuperclass());
        }
        return type;
    }

    private static Method getCallbackMethod(Callback callback) {
        return CallbackReference.getCallbackMethod(CallbackReference.findCallbackClass(callback.getClass()));
    }

    private static Method getCallbackMethod(Class cls) {
        Method[] pubMethods = cls.getDeclaredMethods();
        Method[] classMethods = cls.getMethods();
        HashSet<Method> pmethods = new HashSet<Method>(Arrays.asList(pubMethods));
        pmethods.retainAll(Arrays.asList(classMethods));
        Iterator<Method> i = pmethods.iterator();
        while (i.hasNext()) {
            Method m = i.next();
            if (!Callback.FORBIDDEN_NAMES.contains(m.getName())) continue;
            i.remove();
        }
        Method[] methods = pmethods.toArray(new Method[pmethods.size()]);
        if (methods.length == 1) {
            return CallbackReference.checkMethod(methods[0]);
        }
        for (int i2 = 0; i2 < methods.length; ++i2) {
            Method m = methods[i2];
            if (!"callback".equals(m.getName())) continue;
            return CallbackReference.checkMethod(m);
        }
        String msg = "Callback must implement a single public method, or one public method named 'callback'";
        throw new IllegalArgumentException(msg);
    }

    public Pointer getTrampoline() {
        return this.cbstruct.getPointer(0);
    }

    protected void finalize() {
        CallbackReference.freeNativeCallback(this.cbstruct.peer);
        this.cbstruct.peer = 0;
    }

    private Callback getCallback() {
        return (Callback)this.get();
    }

    private static Pointer getNativeFunctionPointer(Callback cb) {
        InvocationHandler handler;
        if (Proxy.isProxyClass(cb.getClass()) && (handler = Proxy.getInvocationHandler(cb)) instanceof NativeFunctionHandler) {
            return ((NativeFunctionHandler)handler).getPointer();
        }
        return null;
    }

    public static Pointer getFunctionPointer(Callback cb) {
        return CallbackReference.getFunctionPointer(cb, false);
    }

    private static Pointer getFunctionPointer(Callback cb, boolean direct) {
        Map map;
        Pointer fp = null;
        if (cb == null) {
            return null;
        }
        fp = CallbackReference.getNativeFunctionPointer(cb);
        if (fp != null) {
            return fp;
        }
        int callingConvention = cb instanceof AltCallingConvention ? 1 : 0;
        Map map2 = map = direct ? directCallbackMap : callbackMap;
        synchronized (map2) {
            CallbackReference cbref = (CallbackReference)map.get(cb);
            if (cbref == null) {
                cbref = new CallbackReference(cb, callingConvention, direct);
                map.put(cb, cbref);
            }
            return cbref.getTrampoline();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean isAllowableNativeType(Class cls) {
        if (cls == Void.TYPE) return true;
        Class class_ = Void.class;
        if (cls == class_) return true;
        if (cls == Boolean.TYPE) return true;
        Class class_2 = Boolean.class;
        if (cls == class_2) return true;
        if (cls == Byte.TYPE) return true;
        Class class_3 = Byte.class;
        if (cls == class_3) return true;
        if (cls == Short.TYPE) return true;
        Class class_4 = Short.class;
        if (cls == class_4) return true;
        if (cls == Character.TYPE) return true;
        Class class_5 = Character.class;
        if (cls == class_5) return true;
        if (cls == Integer.TYPE) return true;
        Class class_6 = Integer.class;
        if (cls == class_6) return true;
        if (cls == Long.TYPE) return true;
        Class class_7 = Long.class;
        if (cls == class_7) return true;
        if (cls == Float.TYPE) return true;
        Class class_8 = Float.class;
        if (cls == class_8) return true;
        if (cls == Double.TYPE) return true;
        Class class_9 = Double.class;
        if (cls == class_9) return true;
        Class class_10 = Structure.ByValue.class;
        if (class_10.isAssignableFrom(cls)) {
            Class class_11 = Structure.class;
            if (class_11.isAssignableFrom(cls)) return true;
        }
        Class class_12 = Pointer.class;
        if (!class_12.isAssignableFrom(cls)) return false;
        return true;
    }

    private static Pointer getNativeString(Object value, boolean wide) {
        if (value != null) {
            NativeString ns = new NativeString(value.toString(), wide);
            allocations.put(value, ns);
            return ns.getPointer();
        }
        return null;
    }

    private static synchronized native Pointer createNativeCallback(Callback var0, Method var1, Class[] var2, Class var3, int var4, boolean var5);

    private static synchronized native void freeNativeCallback(long var0);

    static {
        try {
            Class class_ = CallbackProxy.class;
            Class[] arrclass = new Class[1];
            Class class_2 = Object;.class;
            arrclass[0] = class_2;
            PROXY_CALLBACK_METHOD = class_.getMethod("callback", arrclass);
        }
        catch (Exception e) {
            throw new Error("Error looking up CallbackProxy.callback() method");
        }
    }

    private static class NativeFunctionHandler
    implements InvocationHandler {
        private Function function;
        private Map options;

        public NativeFunctionHandler(Pointer address, int callingConvention, Map options) {
            this.function = new Function(address, callingConvention);
            this.options = options;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Library.Handler.OBJECT_TOSTRING.equals(method)) {
                String str = "Proxy interface to " + this.function;
                Method m = (Method)this.options.get("invoking-method");
                Class cls = CallbackReference.findCallbackClass(m.getDeclaringClass());
                str = str + " (" + cls.getName() + ")";
                return str;
            }
            if (Library.Handler.OBJECT_HASHCODE.equals(method)) {
                return new Integer(this.hashCode());
            }
            if (Library.Handler.OBJECT_EQUALS.equals(method)) {
                Object o = args[0];
                if (o != null && Proxy.isProxyClass(o.getClass())) {
                    return Function.valueOf(Proxy.getInvocationHandler(o) == this);
                }
                return Boolean.FALSE;
            }
            if (Function.isVarArgs(method)) {
                args = Function.concatenateVarArgs(args);
            }
            return this.function.invoke(method.getReturnType(), args, this.options);
        }

        public Pointer getPointer() {
            return this.function;
        }
    }

    private class DefaultCallbackProxy
    implements CallbackProxy {
        private Method callbackMethod;
        private ToNativeConverter toNative;
        private FromNativeConverter[] fromNative;

        public DefaultCallbackProxy(Method callbackMethod, TypeMapper mapper) {
            this.callbackMethod = callbackMethod;
            Class<?>[] argTypes = callbackMethod.getParameterTypes();
            Class returnType = callbackMethod.getReturnType();
            this.fromNative = new FromNativeConverter[argTypes.length];
            Class class_ = CallbackReference.class$com$sun$jna$NativeMapped == null ? (CallbackReference.class$com$sun$jna$NativeMapped = CallbackReference.class$("com.sun.jna.NativeMapped")) : CallbackReference.class$com$sun$jna$NativeMapped;
            if (class_.isAssignableFrom(returnType)) {
                this.toNative = NativeMappedConverter.getInstance(returnType);
            } else if (mapper != null) {
                this.toNative = mapper.getToNativeConverter(returnType);
            }
            for (int i = 0; i < this.fromNative.length; ++i) {
                if ((CallbackReference.class$com$sun$jna$NativeMapped == null ? CallbackReference.class$("com.sun.jna.NativeMapped") : CallbackReference.class$com$sun$jna$NativeMapped).isAssignableFrom(argTypes[i])) {
                    this.fromNative[i] = new NativeMappedConverter(argTypes[i]);
                    continue;
                }
                if (mapper == null) continue;
                this.fromNative[i] = mapper.getFromNativeConverter(argTypes[i]);
            }
            if (!callbackMethod.isAccessible()) {
                try {
                    callbackMethod.setAccessible(true);
                }
                catch (SecurityException e) {
                    throw new IllegalArgumentException("Callback method is inaccessible, make sure the interface is public: " + callbackMethod);
                }
            }
        }

        private Object invokeCallback(Object[] args) {
            Class<?>[] paramTypes = this.callbackMethod.getParameterTypes();
            Object[] callbackArgs = new Object[args.length];
            for (int i = 0; i < args.length; ++i) {
                Class type = paramTypes[i];
                Object arg = args[i];
                if (this.fromNative[i] != null) {
                    CallbackParameterContext context = new CallbackParameterContext(type, this.callbackMethod, args, i);
                    callbackArgs[i] = this.fromNative[i].fromNative(arg, context);
                    continue;
                }
                callbackArgs[i] = this.convertArgument(arg, type);
            }
            Object result = null;
            Callback cb = CallbackReference.this.getCallback();
            if (cb != null) {
                try {
                    result = this.convertResult(this.callbackMethod.invoke(cb, callbackArgs));
                }
                catch (IllegalArgumentException e) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e);
                }
                catch (IllegalAccessException e) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e);
                }
                catch (InvocationTargetException e) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e.getTargetException());
                }
            }
            for (int i2 = 0; i2 < callbackArgs.length; ++i2) {
                if (!(callbackArgs[i2] instanceof Structure) || callbackArgs[i2] instanceof Structure.ByValue) continue;
                ((Structure)callbackArgs[i2]).autoWrite();
            }
            return result;
        }

        public Object callback(Object[] args) {
            try {
                return this.invokeCallback(args);
            }
            catch (Throwable t) {
                Native.getCallbackExceptionHandler().uncaughtException(CallbackReference.this.getCallback(), t);
                return null;
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private Object convertArgument(Object value, Class dstType) {
            if (value instanceof Pointer) {
                Class class_ = CallbackReference.class$java$lang$String == null ? (CallbackReference.class$java$lang$String = CallbackReference.class$("java.lang.String")) : CallbackReference.class$java$lang$String;
                if (dstType == class_) {
                    return ((Pointer)value).getString(0);
                }
                Class class_2 = CallbackReference.class$com$sun$jna$WString == null ? (CallbackReference.class$com$sun$jna$WString = CallbackReference.class$("com.sun.jna.WString")) : CallbackReference.class$com$sun$jna$WString;
                if (dstType == class_2) {
                    return new WString(((Pointer)value).getString(0, true));
                }
                Class class_3 = CallbackReference.array$Ljava$lang$String == null ? (CallbackReference.array$Ljava$lang$String = CallbackReference.class$("[Ljava.lang.String;")) : CallbackReference.array$Ljava$lang$String;
                if (dstType == class_3) return ((Pointer)value).getStringArray(0, dstType == (CallbackReference.array$Lcom$sun$jna$WString == null ? (CallbackReference.array$Lcom$sun$jna$WString = CallbackReference.class$("[Lcom.sun.jna.WString;")) : CallbackReference.array$Lcom$sun$jna$WString));
                Class class_4 = CallbackReference.array$Lcom$sun$jna$WString == null ? (CallbackReference.array$Lcom$sun$jna$WString = CallbackReference.class$("[Lcom.sun.jna.WString;")) : CallbackReference.array$Lcom$sun$jna$WString;
                if (dstType == class_4) {
                    return ((Pointer)value).getStringArray(0, dstType == (CallbackReference.array$Lcom$sun$jna$WString == null ? (CallbackReference.array$Lcom$sun$jna$WString = CallbackReference.class$("[Lcom.sun.jna.WString;")) : CallbackReference.array$Lcom$sun$jna$WString));
                }
                Class class_5 = CallbackReference.class$com$sun$jna$Callback == null ? (CallbackReference.class$com$sun$jna$Callback = CallbackReference.class$("com.sun.jna.Callback")) : CallbackReference.class$com$sun$jna$Callback;
                if (class_5.isAssignableFrom(dstType)) {
                    return CallbackReference.getCallback(dstType, (Pointer)value);
                }
                Class class_6 = CallbackReference.class$com$sun$jna$Structure == null ? (CallbackReference.class$com$sun$jna$Structure = CallbackReference.class$("com.sun.jna.Structure")) : CallbackReference.class$com$sun$jna$Structure;
                if (!class_6.isAssignableFrom(dstType)) return value;
                Structure s = Structure.newInstance(dstType);
                Class class_7 = CallbackReference.class$com$sun$jna$Structure$ByValue == null ? (CallbackReference.class$com$sun$jna$Structure$ByValue = CallbackReference.class$("com.sun.jna.Structure$ByValue")) : CallbackReference.class$com$sun$jna$Structure$ByValue;
                if (class_7.isAssignableFrom(dstType)) {
                    byte[] buf = new byte[s.size()];
                    ((Pointer)value).read(0, buf, 0, buf.length);
                    s.getPointer().write(0, buf, 0, buf.length);
                } else {
                    s.useMemory((Pointer)value);
                }
                s.read();
                return s;
            }
            if (Boolean.TYPE != dstType) {
                Class class_ = CallbackReference.class$java$lang$Boolean == null ? (CallbackReference.class$java$lang$Boolean = CallbackReference.class$("java.lang.Boolean")) : CallbackReference.class$java$lang$Boolean;
                if (class_ != dstType) return value;
            }
            if (!(value instanceof Number)) return value;
            return Function.valueOf(((Number)value).intValue() != 0);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private Object convertResult(Object value) {
            if (this.toNative != null) {
                value = this.toNative.toNative(value, new CallbackResultContext(this.callbackMethod));
            }
            if (value == null) {
                return null;
            }
            Class cls = value.getClass();
            Class class_ = CallbackReference.class$com$sun$jna$Structure == null ? (CallbackReference.class$com$sun$jna$Structure = CallbackReference.class$("com.sun.jna.Structure")) : CallbackReference.class$com$sun$jna$Structure;
            if (class_.isAssignableFrom(cls)) {
                Class class_2 = CallbackReference.class$com$sun$jna$Structure$ByValue == null ? (CallbackReference.class$com$sun$jna$Structure$ByValue = CallbackReference.class$("com.sun.jna.Structure$ByValue")) : CallbackReference.class$com$sun$jna$Structure$ByValue;
                if (!class_2.isAssignableFrom(cls)) return ((Structure)value).getPointer();
                return value;
            }
            if (cls == Boolean.TYPE) return Boolean.TRUE.equals(value) ? Function.INTEGER_TRUE : Function.INTEGER_FALSE;
            Class class_3 = CallbackReference.class$java$lang$Boolean == null ? (CallbackReference.class$java$lang$Boolean = CallbackReference.class$("java.lang.Boolean")) : CallbackReference.class$java$lang$Boolean;
            if (cls == class_3) {
                return Boolean.TRUE.equals(value) ? Function.INTEGER_TRUE : Function.INTEGER_FALSE;
            }
            Class class_4 = CallbackReference.class$java$lang$String == null ? (CallbackReference.class$java$lang$String = CallbackReference.class$("java.lang.String")) : CallbackReference.class$java$lang$String;
            if (cls == class_4) return CallbackReference.getNativeString(value, cls == (CallbackReference.class$com$sun$jna$WString == null ? (CallbackReference.class$com$sun$jna$WString = CallbackReference.class$("com.sun.jna.WString")) : CallbackReference.class$com$sun$jna$WString));
            Class class_5 = CallbackReference.class$com$sun$jna$WString == null ? (CallbackReference.class$com$sun$jna$WString = CallbackReference.class$("com.sun.jna.WString")) : CallbackReference.class$com$sun$jna$WString;
            if (cls == class_5) {
                return CallbackReference.getNativeString(value, cls == (CallbackReference.class$com$sun$jna$WString == null ? (CallbackReference.class$com$sun$jna$WString = CallbackReference.class$("com.sun.jna.WString")) : CallbackReference.class$com$sun$jna$WString));
            }
            Class class_6 = CallbackReference.array$Ljava$lang$String == null ? (CallbackReference.array$Ljava$lang$String = CallbackReference.class$("[Ljava.lang.String;")) : CallbackReference.array$Ljava$lang$String;
            if (cls == class_6 || cls == (CallbackReference.class$com$sun$jna$WString == null ? (CallbackReference.class$com$sun$jna$WString = CallbackReference.class$("com.sun.jna.WString")) : CallbackReference.class$com$sun$jna$WString)) {
                StringArray sa = cls == (CallbackReference.array$Ljava$lang$String == null ? (CallbackReference.array$Ljava$lang$String = CallbackReference.class$("[Ljava.lang.String;")) : CallbackReference.array$Ljava$lang$String) ? new StringArray((String[])value) : new StringArray((WString[])value);
                CallbackReference.allocations.put(value, sa);
                return sa;
            }
            Class class_7 = CallbackReference.class$com$sun$jna$Callback == null ? (CallbackReference.class$com$sun$jna$Callback = CallbackReference.class$("com.sun.jna.Callback")) : CallbackReference.class$com$sun$jna$Callback;
            if (!class_7.isAssignableFrom(cls)) return value;
            return CallbackReference.getFunctionPointer((Callback)value);
        }

        public Class[] getParameterTypes() {
            return this.callbackMethod.getParameterTypes();
        }

        public Class getReturnType() {
            return this.callbackMethod.getReturnType();
        }
    }

}

