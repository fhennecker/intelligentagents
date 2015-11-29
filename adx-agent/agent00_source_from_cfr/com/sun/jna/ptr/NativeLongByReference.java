/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna.ptr;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class NativeLongByReference
extends ByReference {
    public NativeLongByReference() {
        this(new NativeLong(0));
    }

    public NativeLongByReference(NativeLong value) {
        super(NativeLong.SIZE);
        this.setValue(value);
    }

    public void setValue(NativeLong value) {
        this.getPointer().setNativeLong(0, value);
    }

    public NativeLong getValue() {
        return this.getPointer().getNativeLong(0);
    }
}

