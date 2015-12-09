/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna.ptr;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class IntByReference
extends ByReference {
    public IntByReference() {
        this(0);
    }

    public IntByReference(int value) {
        super(4);
        this.setValue(value);
    }

    public void setValue(int value) {
        this.getPointer().setInt(0, value);
    }

    public int getValue() {
        return this.getPointer().getInt(0);
    }
}

