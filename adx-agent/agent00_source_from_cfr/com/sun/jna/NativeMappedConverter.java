/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;
import com.sun.jna.Pointer;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;
import java.util.Map;
import java.util.WeakHashMap;

public class NativeMappedConverter
implements TypeConverter {
    private static Map converters = new WeakHashMap();
    private final Class type;
    private final Class nativeType;
    private final NativeMapped instance;

    public static NativeMappedConverter getInstance(Class cls) {
        Map map = converters;
        synchronized (map) {
            NativeMappedConverter nmc = (NativeMappedConverter)converters.get(cls);
            if (nmc == null) {
                nmc = new NativeMappedConverter(cls);
                converters.put(cls, nmc);
            }
            return nmc;
        }
    }

    public NativeMappedConverter(Class type) {
        Class class_ = NativeMapped.class;
        if (!class_.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Type must derive from " + NativeMapped.class);
        }
        this.type = type;
        this.instance = this.defaultValue();
        this.nativeType = this.instance.nativeType();
    }

    public NativeMapped defaultValue() {
        try {
            return (NativeMapped)this.type.newInstance();
        }
        catch (InstantiationException e) {
            String msg = "Can't create an instance of " + this.type + ", requires a no-arg constructor: " + e;
            throw new IllegalArgumentException(msg);
        }
        catch (IllegalAccessException e) {
            String msg = "Not allowed to create an instance of " + this.type + ", requires a public, no-arg constructor: " + e;
            throw new IllegalArgumentException(msg);
        }
    }

    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return this.instance.fromNative(nativeValue, context);
    }

    public Class nativeType() {
        return this.nativeType;
    }

    public Object toNative(Object value, ToNativeContext context) {
        if (value == null) {
            Class class_ = Pointer.class;
            if (class_.isAssignableFrom(this.nativeType)) {
                return null;
            }
            value = this.defaultValue();
        }
        return ((NativeMapped)value).toNative();
    }
}

