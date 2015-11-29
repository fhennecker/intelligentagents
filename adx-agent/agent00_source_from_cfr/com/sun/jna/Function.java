/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  [Lcom.sun.jna.NativeMapped;
 *  [Lcom.sun.jna.Pointer;
 *  [Lcom.sun.jna.Structure;
 *  [Lcom.sun.jna.WString;
 *  [Ljava.lang.String;
 */
package com.sun.jna;

import [Lcom.sun.jna.NativeMapped;;
import [Lcom.sun.jna.Pointer;;
import [Lcom.sun.jna.Structure;;
import [Lcom.sun.jna.WString;;
import [Ljava.lang.String;;
import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.FunctionParameterContext;
import com.sun.jna.FunctionResultContext;
import com.sun.jna.Memory;
import com.sun.jna.MethodParameterContext;
import com.sun.jna.MethodResultContext;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class Function
extends Pointer {
    public static final int MAX_NARGS = 256;
    public static final int C_CONVENTION = 0;
    public static final int ALT_CONVENTION = 1;
    private static final int MASK_CC = 3;
    public static final int THROW_LAST_ERROR = 4;
    static final Integer INTEGER_TRUE = new Integer(-1);
    static final Integer INTEGER_FALSE = new Integer(0);
    private NativeLibrary library;
    private final String functionName;
    int callFlags;
    final Map options;
    static final String OPTION_INVOKING_METHOD = "invoking-method";
    static /* synthetic */ Class array$Lcom$sun$jna$Structure$ByReference;

    public static Function getFunction(String libraryName, String functionName) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName);
    }

    public static Function getFunction(String libraryName, String functionName, int callFlags) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName, callFlags);
    }

    Function(NativeLibrary library, String functionName, int callFlags) {
        this.checkCallingConvention(callFlags & 3);
        if (functionName == null) {
            throw new NullPointerException("Function name must not be null");
        }
        this.library = library;
        this.functionName = functionName;
        this.callFlags = callFlags;
        this.options = library.options;
        try {
            this.peer = library.getSymbolAddress(functionName);
        }
        catch (UnsatisfiedLinkError e) {
            throw new UnsatisfiedLinkError("Error looking up function '" + functionName + "': " + e.getMessage());
        }
    }

    Function(Pointer functionAddress, int callFlags) {
        this.checkCallingConvention(callFlags & 3);
        if (functionAddress == null || functionAddress.peer == 0) {
            throw new NullPointerException("Function address may not be null");
        }
        this.functionName = functionAddress.toString();
        this.callFlags = callFlags;
        this.peer = functionAddress.peer;
        this.options = Collections.EMPTY_MAP;
    }

    private void checkCallingConvention(int convention) throws IllegalArgumentException {
        switch (convention) {
            case 0: 
            case 1: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Unrecognized calling convention: " + convention);
            }
        }
    }

    public String getName() {
        return this.functionName;
    }

    public int getCallingConvention() {
        return this.callFlags & 3;
    }

    public Object invoke(Class returnType, Object[] inArgs) {
        return this.invoke(returnType, inArgs, this.options);
    }

    public Object invoke(Class returnType, Object[] inArgs, Map options) {
        Object[] args = new Object[]{};
        if (inArgs != null) {
            if (inArgs.length > 256) {
                throw new UnsupportedOperationException("Maximum argument count is 256");
            }
            args = new Object[inArgs.length];
            System.arraycopy(inArgs, 0, args, 0, args.length);
        }
        TypeMapper mapper = (TypeMapper)options.get("type-mapper");
        Method invokingMethod = (Method)options.get("invoking-method");
        boolean allowObjects = Boolean.TRUE.equals(options.get("allow-objects"));
        for (int i = 0; i < args.length; ++i) {
            args[i] = this.convertArgument(args, i, invokingMethod, mapper, allowObjects);
        }
        Class nativeType = returnType;
        FromNativeConverter resultConverter = null;
        Class class_ = NativeMapped.class;
        if (class_.isAssignableFrom(returnType)) {
            NativeMappedConverter tc;
            resultConverter = tc = NativeMappedConverter.getInstance(returnType);
            nativeType = tc.nativeType();
        } else if (mapper != null && (resultConverter = mapper.getFromNativeConverter(returnType)) != null) {
            nativeType = resultConverter.nativeType();
        }
        Object result = this.invoke(args, nativeType, allowObjects);
        if (resultConverter != null) {
            FunctionResultContext context = invokingMethod != null ? new MethodResultContext(returnType, this, inArgs, invokingMethod) : new FunctionResultContext(returnType, this, inArgs);
            result = resultConverter.fromNative(result, context);
        }
        if (inArgs != null) {
            for (int i2 = 0; i2 < inArgs.length; ++i2) {
                Object inArg = inArgs[i2];
                if (inArg == null) continue;
                if (inArg instanceof Structure) {
                    if (inArg instanceof Structure.ByValue) continue;
                    ((Structure)inArg).autoRead();
                    continue;
                }
                if (args[i2] instanceof PostCallRead) {
                    ((PostCallRead)args[i2]).read();
                    if (!(args[i2] instanceof PointerArray)) continue;
                    PointerArray array = (PointerArray)args[i2];
                    if (!(array$Lcom$sun$jna$Structure$ByReference == null ? Function.class$("[Lcom.sun.jna.Structure$ByReference;") : array$Lcom$sun$jna$Structure$ByReference).isAssignableFrom(inArg.getClass())) continue;
                    Class type = inArg.getClass().getComponentType();
                    Structure[] ss = (Structure[])inArg;
                    for (int si = 0; si < ss.length; ++si) {
                        Pointer p = array.getPointer(Pointer.SIZE * si);
                        ss[si] = Structure.updateStructureByReference(type, ss[si], p);
                    }
                    continue;
                }
                if (!(array$Lcom$sun$jna$Structure == null ? Function.class$("[Lcom.sun.jna.Structure;") : array$Lcom$sun$jna$Structure).isAssignableFrom(inArg.getClass())) continue;
                Structure.autoRead((Structure[])inArg);
            }
        }
        return result;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    Object invoke(Object[] args, Class returnType, boolean allowObjects) {
        result = null;
        if (returnType == null || returnType == Void.TYPE) ** GOTO lbl-1000
        v0 = Void.class;
        if (returnType == v0) lbl-1000: // 2 sources:
        {
            this.invokeVoid(this.callFlags, args);
            return null;
        }
        if (returnType == Boolean.TYPE) return Function.valueOf(this.invokeInt(this.callFlags, args) != 0);
        v1 = Boolean.class;
        if (returnType == v1) {
            return Function.valueOf(this.invokeInt(this.callFlags, args) != 0);
        }
        if (returnType == Byte.TYPE) return new Byte((byte)this.invokeInt(this.callFlags, args));
        v2 = Byte.class;
        if (returnType == v2) {
            return new Byte((byte)this.invokeInt(this.callFlags, args));
        }
        if (returnType == Short.TYPE) return new Short((short)this.invokeInt(this.callFlags, args));
        v3 = Short.class;
        if (returnType == v3) {
            return new Short((short)this.invokeInt(this.callFlags, args));
        }
        if (returnType == Character.TYPE) return new Character((char)this.invokeInt(this.callFlags, args));
        v4 = Character.class;
        if (returnType == v4) {
            return new Character((char)this.invokeInt(this.callFlags, args));
        }
        if (returnType == Integer.TYPE) return new Integer(this.invokeInt(this.callFlags, args));
        v5 = Integer.class;
        if (returnType == v5) {
            return new Integer(this.invokeInt(this.callFlags, args));
        }
        if (returnType == Long.TYPE) return new Long(this.invokeLong(this.callFlags, args));
        v6 = Long.class;
        if (returnType == v6) {
            return new Long(this.invokeLong(this.callFlags, args));
        }
        if (returnType == Float.TYPE) return new Float(this.invokeFloat(this.callFlags, args));
        v7 = Float.class;
        if (returnType == v7) {
            return new Float(this.invokeFloat(this.callFlags, args));
        }
        if (returnType == Double.TYPE) return new Double(this.invokeDouble(this.callFlags, args));
        v8 = Double.class;
        if (returnType == v8) {
            return new Double(this.invokeDouble(this.callFlags, args));
        }
        v9 = String.class;
        if (returnType == v9) {
            return this.invokeString(this.callFlags, args, false);
        }
        v10 = WString.class;
        if (returnType == v10) {
            s = this.invokeString(this.callFlags, args, true);
            if (s == null) return result;
            return new WString(s);
        }
        v11 = Pointer.class;
        if (v11.isAssignableFrom(returnType)) {
            return this.invokePointer(this.callFlags, args);
        }
        v12 = Structure.class;
        if (v12.isAssignableFrom(returnType)) {
            v13 = Structure.ByValue.class;
            if (v13.isAssignableFrom(returnType)) {
                s = this.invokeStructure(this.callFlags, args, Structure.newInstance(returnType));
                s.autoRead();
                return s;
            }
            result = this.invokePointer(this.callFlags, args);
            if (result == null) return result;
            s = Structure.newInstance(returnType);
            s.useMemory((Pointer)result);
            s.autoRead();
            return s;
        }
        v14 = Callback.class;
        if (v14.isAssignableFrom(returnType)) {
            result = this.invokePointer(this.callFlags, args);
            if (result == null) return result;
            return CallbackReference.getCallback(returnType, (Pointer)result);
        }
        v15 = String;.class;
        if (returnType == v15) {
            p = this.invokePointer(this.callFlags, args);
            if (p == null) return result;
            return p.getStringArray(0);
        }
        v16 = WString;.class;
        if (returnType == v16) {
            p = this.invokePointer(this.callFlags, args);
            if (p == null) return result;
            arr = p.getStringArray(0, true);
            warr = new WString[arr.length];
            i = 0;
            do {
                if (i >= arr.length) {
                    return warr;
                }
                warr[i] = new WString(arr[i]);
                ++i;
            } while (true);
        }
        v17 = Pointer;.class;
        if (returnType == v17) {
            p = this.invokePointer(this.callFlags, args);
            if (p == null) return result;
            return p.getPointerArray(0);
        }
        if (allowObjects == false) throw new IllegalArgumentException("Unsupported return type " + returnType + " in function " + this.getName());
        result = this.invokeObject(this.callFlags, args);
        if (result == null) return result;
        if (returnType.isAssignableFrom(result.getClass()) != false) return result;
        throw new ClassCastException("Return type " + returnType + " does not match result " + result.getClass());
    }

    private Object convertArgument(Object[] args, int index, Method invokingMethod, TypeMapper mapper, boolean allowObjects) {
        Object arg = args[index];
        if (arg != null) {
            Class type = arg.getClass();
            ToNativeConverter converter = null;
            Class class_ = NativeMapped.class;
            if (class_.isAssignableFrom(type)) {
                converter = NativeMappedConverter.getInstance(type);
            } else if (mapper != null) {
                converter = mapper.getToNativeConverter(type);
            }
            if (converter != null) {
                FunctionParameterContext context = invokingMethod != null ? new MethodParameterContext(this, args, index, invokingMethod) : new FunctionParameterContext(this, args, index);
                arg = converter.toNative(arg, context);
            }
        }
        if (arg == null || this.isPrimitiveArray(arg.getClass())) {
            return arg;
        }
        Class argClass = arg.getClass();
        if (arg instanceof Structure) {
            Structure struct = (Structure)arg;
            struct.autoWrite();
            if (struct instanceof Structure.ByValue) {
                Class ptype = struct.getClass();
                if (invokingMethod != null) {
                    Class<?>[] ptypes = invokingMethod.getParameterTypes();
                    if (Function.isVarArgs(invokingMethod)) {
                        if (index < ptypes.length - 1) {
                            ptype = ptypes[index];
                        } else {
                            Class etype = ptypes[ptypes.length - 1].getComponentType();
                            Class class_ = Object.class;
                            if (etype != class_) {
                                ptype = etype;
                            }
                        }
                    } else {
                        ptype = ptypes[index];
                    }
                }
                Class class_ = Structure.ByValue.class;
                if (class_.isAssignableFrom(ptype)) {
                    return struct;
                }
            }
            return struct.getPointer();
        }
        if (arg instanceof Callback) {
            return CallbackReference.getFunctionPointer((Callback)arg);
        }
        if (arg instanceof String) {
            return new NativeString((String)arg, false).getPointer();
        }
        if (arg instanceof WString) {
            return new NativeString(arg.toString(), true).getPointer();
        }
        if (arg instanceof Boolean) {
            return Boolean.TRUE.equals(arg) ? INTEGER_TRUE : INTEGER_FALSE;
        }
        Class class_ = String;.class;
        if (class_ == argClass) {
            return new StringArray((String[])arg);
        }
        Class class_2 = WString;.class;
        if (class_2 == argClass) {
            return new StringArray((WString[])arg);
        }
        Class class_3 = Pointer;.class;
        if (class_3 == argClass) {
            return new PointerArray((Pointer[])arg);
        }
        Class class_4 = NativeMapped;.class;
        if (class_4.isAssignableFrom(argClass)) {
            return new NativeMappedArray((NativeMapped[])arg);
        }
        Class class_5 = Structure;.class;
        if (class_5.isAssignableFrom(argClass)) {
            Structure[] ss = (Structure[])arg;
            Class type = argClass.getComponentType();
            Class class_6 = Structure.ByReference.class;
            boolean byRef = class_6.isAssignableFrom(type);
            if (byRef) {
                Pointer[] pointers = new Pointer[ss.length + 1];
                for (int i = 0; i < ss.length; ++i) {
                    pointers[i] = ss[i] != null ? ss[i].getPointer() : null;
                }
                return new PointerArray(pointers);
            }
            if (ss.length == 0) {
                throw new IllegalArgumentException("Structure array must have non-zero length");
            }
            if (ss[0] == null) {
                Structure.newInstance(type).toArray(ss);
                return ss[0].getPointer();
            }
            Structure.autoWrite(ss);
            return ss[0].getPointer();
        }
        if (argClass.isArray()) {
            throw new IllegalArgumentException("Unsupported array argument type: " + argClass.getComponentType());
        }
        if (allowObjects) {
            return arg;
        }
        if (!Native.isSupportedNativeType(arg.getClass())) {
            throw new IllegalArgumentException("Unsupported argument type " + arg.getClass().getName() + " at parameter " + index + " of function " + this.getName());
        }
        return arg;
    }

    private boolean isPrimitiveArray(Class argClass) {
        return argClass.isArray() && argClass.getComponentType().isPrimitive();
    }

    private native int invokeInt(int var1, Object[] var2);

    private native long invokeLong(int var1, Object[] var2);

    public void invoke(Object[] args) {
        Class class_ = Void.class;
        this.invoke(class_, args);
    }

    private native void invokeVoid(int var1, Object[] var2);

    private native float invokeFloat(int var1, Object[] var2);

    private native double invokeDouble(int var1, Object[] var2);

    private String invokeString(int callFlags, Object[] args, boolean wide) {
        Pointer ptr = this.invokePointer(callFlags, args);
        String s = null;
        if (ptr != null) {
            s = wide ? ptr.getString(0, wide) : ptr.getString(0);
        }
        return s;
    }

    private native Pointer invokePointer(int var1, Object[] var2);

    private native Structure invokeStructure(int var1, Object[] var2, Structure var3);

    private native Object invokeObject(int var1, Object[] var2);

    public String toString() {
        if (this.library != null) {
            return "native function " + this.functionName + "(" + this.library.getName() + ")@0x" + Long.toHexString(this.peer);
        }
        return "native function@0x" + Long.toHexString(this.peer);
    }

    public Object invokeObject(Object[] args) {
        Class class_ = Object.class;
        return this.invoke(class_, args);
    }

    public Pointer invokePointer(Object[] args) {
        Class class_ = Pointer.class;
        return (Pointer)this.invoke(class_, args);
    }

    public String invokeString(Object[] args, boolean wide) {
        Class class_ = wide ? WString.class : String.class;
        Object o = this.invoke(class_, args);
        return o != null ? o.toString() : null;
    }

    public int invokeInt(Object[] args) {
        Class class_ = Integer.class;
        return (Integer)this.invoke(class_, args);
    }

    public long invokeLong(Object[] args) {
        Class class_ = Long.class;
        return (Long)this.invoke(class_, args);
    }

    public float invokeFloat(Object[] args) {
        Class class_ = Float.class;
        return ((Float)this.invoke(class_, args)).floatValue();
    }

    public double invokeDouble(Object[] args) {
        Class class_ = Double.class;
        return (Double)this.invoke(class_, args);
    }

    public void invokeVoid(Object[] args) {
        Class class_ = Void.class;
        this.invoke(class_, args);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() == this.getClass()) {
            Function other = (Function)o;
            return other.callFlags == this.callFlags && other.options.equals(this.options) && other.peer == this.peer;
        }
        return false;
    }

    static Object[] concatenateVarArgs(Object[] inArgs) {
        if (inArgs != null && inArgs.length > 0) {
            Class<?> argType;
            Object lastArg = inArgs[inArgs.length - 1];
            Class<?> class_ = argType = lastArg != null ? lastArg.getClass() : null;
            if (argType != null && argType.isArray()) {
                Object[] varArgs = (Object[])lastArg;
                Object[] fullArgs = new Object[inArgs.length + varArgs.length];
                System.arraycopy(inArgs, 0, fullArgs, 0, inArgs.length - 1);
                System.arraycopy(varArgs, 0, fullArgs, inArgs.length - 1, varArgs.length);
                fullArgs[fullArgs.length - 1] = null;
                inArgs = fullArgs;
            }
        }
        return inArgs;
    }

    static boolean isVarArgs(Method m) {
        try {
            Method v = m.getClass().getMethod("isVarArgs", new Class[0]);
            return Boolean.TRUE.equals(v.invoke(m, new Object[0]));
        }
        catch (SecurityException e) {
        }
        catch (NoSuchMethodException e) {
        }
        catch (IllegalArgumentException e) {
        }
        catch (IllegalAccessException e) {
        }
        catch (InvocationTargetException e) {
            // empty catch block
        }
        return false;
    }

    static Boolean valueOf(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }

    private static class PointerArray
    extends Memory
    implements PostCallRead {
        private final Pointer[] original;

        public PointerArray(Pointer[] arg) {
            super(Pointer.SIZE * (arg.length + 1));
            this.original = arg;
            for (int i = 0; i < arg.length; ++i) {
                this.setPointer(i * Pointer.SIZE, arg[i]);
            }
            this.setPointer(Pointer.SIZE * arg.length, null);
        }

        public void read() {
            for (int i = 0; i < this.original.length; ++i) {
                this.original[i] = this.getPointer(i * Pointer.SIZE);
            }
        }
    }

    private static class NativeMappedArray
    extends Memory
    implements PostCallRead {
        private final NativeMapped[] original;

        public NativeMappedArray(NativeMapped[] arg) {
            super(Native.getNativeSize(arg.getClass(), arg));
            this.original = arg;
            Class nativeType = arg.getClass().getComponentType();
            this.setValue(0, this.original, this.original.getClass());
        }

        public void read() {
            this.getValue(0, this.original.getClass(), this.original);
        }
    }

    public static interface PostCallRead {
        public void read();
    }

}

