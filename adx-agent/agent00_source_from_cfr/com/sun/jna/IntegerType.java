/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;

public abstract class IntegerType
extends Number
implements NativeMapped {
    private int size;
    private long value;
    private Number number;

    public IntegerType(int size) {
        this(size, 0);
    }

    public IntegerType(int size, long value) {
        this.size = size;
        this.setValue(value);
    }

    public void setValue(long value) {
        long truncated = value;
        this.value = value;
        switch (this.size) {
            case 1: {
                truncated = (byte)value;
                this.number = new Byte((byte)value);
                break;
            }
            case 2: {
                truncated = (short)value;
                this.number = new Short((short)value);
                break;
            }
            case 4: {
                truncated = (int)value;
                this.number = new Integer((int)value);
                break;
            }
            case 8: {
                this.number = new Long(value);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported size: " + this.size);
            }
        }
        if (this.size < 8) {
            long mask = (1 << this.size * 8) - 1 ^ -1;
            if (value < 0 && truncated != value || value >= 0 && (mask & value) != 0) {
                throw new IllegalArgumentException("Argument value 0x" + Long.toHexString(value) + " exceeds native capacity (" + this.size + " bytes) mask=0x" + Long.toHexString(mask));
            }
        }
    }

    public Object toNative() {
        return this.number;
    }

    public Object fromNative(Object nativeValue, FromNativeContext context) {
        long value = nativeValue == null ? 0 : ((Number)nativeValue).longValue();
        try {
            IntegerType number = (IntegerType)this.getClass().newInstance();
            number.setValue(value);
            return number;
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Can't instantiate " + this.getClass());
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Not allowed to instantiate " + this.getClass());
        }
    }

    public Class nativeType() {
        return this.number.getClass();
    }

    public int intValue() {
        return this.number.intValue();
    }

    public long longValue() {
        return this.number.longValue();
    }

    public float floatValue() {
        return this.number.floatValue();
    }

    public double doubleValue() {
        return this.number.doubleValue();
    }

    public boolean equals(Object rhs) {
        return rhs instanceof IntegerType && this.number.equals(((IntegerType)rhs).number);
    }

    public String toString() {
        return this.number.toString();
    }

    public int hashCode() {
        return this.number.hashCode();
    }
}

