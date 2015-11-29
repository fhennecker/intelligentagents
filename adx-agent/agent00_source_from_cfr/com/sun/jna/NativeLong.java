/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

public class NativeLong
extends IntegerType {
    public static final int SIZE = Native.LONG_SIZE;

    public NativeLong() {
        this(0);
    }

    public NativeLong(long value) {
        super(SIZE, value);
    }
}

