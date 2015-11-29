/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna.ptr;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class FloatByReference
extends ByReference {
    public FloatByReference() {
        this(0.0f);
    }

    public FloatByReference(float value) {
        super(4);
        this.setValue(value);
    }

    public void setValue(float value) {
        this.getPointer().setFloat(0, value);
    }

    public float getValue() {
        return this.getPointer().getFloat(0);
    }
}

