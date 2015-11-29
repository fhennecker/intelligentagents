/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna.ptr;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class ShortByReference
extends ByReference {
    public ShortByReference() {
        this(0);
    }

    public ShortByReference(short value) {
        super(2);
        this.setValue(value);
    }

    public void setValue(short value) {
        this.getPointer().setShort(0, value);
    }

    public short getValue() {
        return this.getPointer().getShort(0);
    }
}

