/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.FromNativeContext;

public interface FromNativeConverter {
    public Object fromNative(Object var1, FromNativeContext var2);

    public Class nativeType();
}

