/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.ToNativeContext;

public interface ToNativeConverter {
    public Object toNative(Object var1, ToNativeContext var2);

    public Class nativeType();
}

