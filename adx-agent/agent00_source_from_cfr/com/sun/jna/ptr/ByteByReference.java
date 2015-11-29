/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna.ptr;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class ByteByReference
extends ByReference {
    public ByteByReference() {
        this(0);
    }

    public ByteByReference(byte value) {
        super(1);
        this.setValue(value);
    }

    public void setValue(byte value) {
        this.getPointer().setByte(0, value);
    }

    public byte getValue() {
        return this.getPointer().getByte(0);
    }
}

