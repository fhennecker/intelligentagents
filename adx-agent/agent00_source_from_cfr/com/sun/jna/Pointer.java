/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeContext;
import com.sun.jna.WString;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Pointer {
    public static final int SIZE = Native.POINTER_SIZE;
    public static final Pointer NULL;
    protected long peer;

    public static final Pointer createConstant(long peer) {
        return new Opaque(peer);
    }

    Pointer() {
    }

    Pointer(long peer) {
        this.peer = peer;
    }

    public Pointer share(long offset) {
        return this.share(offset, 0);
    }

    public Pointer share(long offset, long sz) {
        if (offset == 0) {
            return this;
        }
        return new Pointer(this.peer + offset);
    }

    public void clear(long size) {
        this.setMemory(0, size, 0);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        return o instanceof Pointer && ((Pointer)o).peer == this.peer;
    }

    public int hashCode() {
        return (int)((this.peer >>> 32) + (this.peer & -1));
    }

    public long indexOf(long offset, byte value) {
        return Pointer._indexOf(this.peer + offset, value);
    }

    private static native long _indexOf(long var0, byte var2);

    public void read(long offset, byte[] buf, int index, int length) {
        Pointer._read(this.peer + offset, buf, index, length);
    }

    private static native void _read(long var0, byte[] var2, int var3, int var4);

    public void read(long offset, short[] buf, int index, int length) {
        Pointer._read(this.peer + offset, buf, index, length);
    }

    private static native void _read(long var0, short[] var2, int var3, int var4);

    public void read(long offset, char[] buf, int index, int length) {
        Pointer._read(this.peer + offset, buf, index, length);
    }

    private static native void _read(long var0, char[] var2, int var3, int var4);

    public void read(long offset, int[] buf, int index, int length) {
        Pointer._read(this.peer + offset, buf, index, length);
    }

    private static native void _read(long var0, int[] var2, int var3, int var4);

    public void read(long offset, long[] buf, int index, int length) {
        Pointer._read(this.peer + offset, buf, index, length);
    }

    private static native void _read(long var0, long[] var2, int var3, int var4);

    public void read(long offset, float[] buf, int index, int length) {
        Pointer._read(this.peer + offset, buf, index, length);
    }

    private static native void _read(long var0, float[] var2, int var3, int var4);

    public void read(long offset, double[] buf, int index, int length) {
        Pointer._read(this.peer + offset, buf, index, length);
    }

    private static native void _read(long var0, double[] var2, int var3, int var4);

    public void read(long offset, Pointer[] buf, int index, int length) {
        for (int i = 0; i < length; ++i) {
            buf[i + index] = this.getPointer(offset + (long)(i * SIZE));
        }
    }

    public void write(long offset, byte[] buf, int index, int length) {
        Pointer._write(this.peer + offset, buf, index, length);
    }

    private static native void _write(long var0, byte[] var2, int var3, int var4);

    public void write(long offset, short[] buf, int index, int length) {
        Pointer._write(this.peer + offset, buf, index, length);
    }

    private static native void _write(long var0, short[] var2, int var3, int var4);

    public void write(long offset, char[] buf, int index, int length) {
        Pointer._write(this.peer + offset, buf, index, length);
    }

    private static native void _write(long var0, char[] var2, int var3, int var4);

    public void write(long offset, int[] buf, int index, int length) {
        Pointer._write(this.peer + offset, buf, index, length);
    }

    private static native void _write(long var0, int[] var2, int var3, int var4);

    public void write(long offset, long[] buf, int index, int length) {
        Pointer._write(this.peer + offset, buf, index, length);
    }

    private static native void _write(long var0, long[] var2, int var3, int var4);

    public void write(long offset, float[] buf, int index, int length) {
        Pointer._write(this.peer + offset, buf, index, length);
    }

    private static native void _write(long var0, float[] var2, int var3, int var4);

    public void write(long offset, double[] buf, int index, int length) {
        Pointer._write(this.peer + offset, buf, index, length);
    }

    private static native void _write(long var0, double[] var2, int var3, int var4);

    public void write(long bOff, Pointer[] buf, int index, int length) {
        for (int i = 0; i < length; ++i) {
            this.setPointer(bOff + (long)(i * SIZE), buf[index + i]);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Object getValue(long offset, Class type, Object currentValue) {
        Object result = null;
        Class class_ = Structure.class;
        if (class_.isAssignableFrom(type)) {
            Structure s = (Structure)currentValue;
            Class class_2 = Structure.ByReference.class;
            if (class_2.isAssignableFrom(type)) {
                s = Structure.updateStructureByReference(type, s, this.getPointer(offset));
                return s;
            } else {
                s.useMemory(this, (int)offset);
                s.read();
            }
            return s;
        }
        if (type == Boolean.TYPE) return Function.valueOf(this.getInt(offset) != 0);
        Class class_3 = Boolean.class;
        if (type == class_3) {
            return Function.valueOf(this.getInt(offset) != 0);
        }
        if (type == Byte.TYPE) return new Byte(this.getByte(offset));
        Class class_4 = Byte.class;
        if (type == class_4) {
            return new Byte(this.getByte(offset));
        }
        if (type == Short.TYPE) return new Short(this.getShort(offset));
        Class class_5 = Short.class;
        if (type == class_5) {
            return new Short(this.getShort(offset));
        }
        if (type == Character.TYPE) return new Character(this.getChar(offset));
        Class class_6 = Character.class;
        if (type == class_6) {
            return new Character(this.getChar(offset));
        }
        if (type == Integer.TYPE) return new Integer(this.getInt(offset));
        Class class_7 = Integer.class;
        if (type == class_7) {
            return new Integer(this.getInt(offset));
        }
        if (type == Long.TYPE) return new Long(this.getLong(offset));
        Class class_8 = Long.class;
        if (type == class_8) {
            return new Long(this.getLong(offset));
        }
        if (type == Float.TYPE) return new Float(this.getFloat(offset));
        Class class_9 = Float.class;
        if (type == class_9) {
            return new Float(this.getFloat(offset));
        }
        if (type == Double.TYPE) return new Double(this.getDouble(offset));
        Class class_10 = Double.class;
        if (type == class_10) {
            return new Double(this.getDouble(offset));
        }
        Class class_11 = Pointer.class;
        if (class_11.isAssignableFrom(type)) {
            Pointer p = this.getPointer(offset);
            if (p == null) return result;
            Pointer oldp = currentValue instanceof Pointer ? (Pointer)currentValue : null;
            if (oldp == null) return p;
            if (p.peer == oldp.peer) return oldp;
            return p;
        }
        Class class_12 = String.class;
        if (type == class_12) {
            Pointer p = this.getPointer(offset);
            if (p == null) return null;
            String string = p.getString(0);
            return string;
        }
        Class class_13 = WString.class;
        if (type == class_13) {
            Pointer p = this.getPointer(offset);
            if (p == null) return null;
            WString wString = new WString(p.getString(0, true));
            return wString;
        }
        Class class_14 = Callback.class;
        if (class_14.isAssignableFrom(type)) {
            Pointer fp = this.getPointer(offset);
            if (fp == null) {
                return null;
            }
            Callback cb = (Callback)currentValue;
            Pointer oldfp = CallbackReference.getFunctionPointer(cb);
            if (fp.equals(oldfp)) return cb;
            cb = CallbackReference.getCallback(type, fp);
            return cb;
        }
        Class class_15 = Buffer.class;
        if (class_15.isAssignableFrom(type)) {
            Pointer bp = this.getPointer(offset);
            if (bp == null) {
                return null;
            }
            Pointer oldbp = currentValue == null ? null : Native.getDirectBufferPointer((Buffer)currentValue);
            if (oldbp == null) throw new IllegalStateException("Can't autogenerate a direct buffer on memory read");
            if (oldbp.equals(bp)) return result;
            throw new IllegalStateException("Can't autogenerate a direct buffer on memory read");
        }
        Class class_16 = NativeMapped.class;
        if (class_16.isAssignableFrom(type)) {
            NativeMapped nm = (NativeMapped)currentValue;
            if (nm != null) {
                Object value = this.getValue(offset, nm.nativeType(), null);
                return nm.fromNative(value, new FromNativeContext(type));
            }
            NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
            Object value = this.getValue(offset, tc.nativeType(), null);
            return tc.fromNative(value, new FromNativeContext(type));
        }
        if (!type.isArray()) throw new IllegalArgumentException("Reading \"" + type + "\" from memory is not supported");
        result = currentValue;
        if (result == null) {
            throw new IllegalStateException("Need an initialized array");
        }
        this.getArrayValue(offset, result, type.getComponentType());
        return result;
    }

    private void getArrayValue(long offset, Object o, Class cls) {
        int length = 0;
        length = Array.getLength(o);
        Object result = o;
        if (cls == Byte.TYPE) {
            this.read(offset, (byte[])result, 0, length);
        } else if (cls == Short.TYPE) {
            this.read(offset, (short[])result, 0, length);
        } else if (cls == Character.TYPE) {
            this.read(offset, (char[])result, 0, length);
        } else if (cls == Integer.TYPE) {
            this.read(offset, (int[])result, 0, length);
        } else if (cls == Long.TYPE) {
            this.read(offset, (long[])result, 0, length);
        } else if (cls == Float.TYPE) {
            this.read(offset, (float[])result, 0, length);
        } else if (cls == Double.TYPE) {
            this.read(offset, (double[])result, 0, length);
        } else {
            Class class_ = Pointer.class;
            if (class_.isAssignableFrom(cls)) {
                this.read(offset, (Pointer[])result, 0, length);
            } else {
                Class class_2 = Structure.class;
                if (class_2.isAssignableFrom(cls)) {
                    Structure[] sarray = (Structure[])result;
                    Class class_3 = Structure.ByReference.class;
                    if (class_3.isAssignableFrom(cls)) {
                        Pointer[] parray = this.getPointerArray(offset, sarray.length);
                        for (int i = 0; i < sarray.length; ++i) {
                            sarray[i] = Structure.updateStructureByReference(cls, sarray[i], parray[i]);
                        }
                    } else {
                        for (int i = 0; i < sarray.length; ++i) {
                            if (sarray[i] == null) {
                                sarray[i] = Structure.newInstance(cls);
                            }
                            sarray[i].useMemory(this, (int)(offset + (long)(i * sarray[i].size())));
                            sarray[i].read();
                        }
                    }
                } else {
                    Class class_4 = NativeMapped.class;
                    if (class_4.isAssignableFrom(cls)) {
                        NativeMapped[] array = (NativeMapped[])result;
                        NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
                        int size = Native.getNativeSize(result.getClass(), result) / array.length;
                        for (int i = 0; i < array.length; ++i) {
                            Object value = this.getValue(offset + (long)(size * i), tc.nativeType(), array[i]);
                            array[i] = (NativeMapped)tc.fromNative(value, new FromNativeContext(cls));
                        }
                    } else {
                        throw new IllegalArgumentException("Reading array of " + cls + " from memory not supported");
                    }
                }
            }
        }
    }

    public byte getByte(long offset) {
        return Pointer._getByte(this.peer + offset);
    }

    private static native byte _getByte(long var0);

    public char getChar(long offset) {
        return Pointer._getChar(this.peer + offset);
    }

    private static native char _getChar(long var0);

    public short getShort(long offset) {
        return Pointer._getShort(this.peer + offset);
    }

    private static native short _getShort(long var0);

    public int getInt(long offset) {
        return Pointer._getInt(this.peer + offset);
    }

    private static native int _getInt(long var0);

    public long getLong(long offset) {
        return Pointer._getLong(this.peer + offset);
    }

    private static native long _getLong(long var0);

    public NativeLong getNativeLong(long offset) {
        return new NativeLong(NativeLong.SIZE == 8 ? this.getLong(offset) : (long)this.getInt(offset));
    }

    public float getFloat(long offset) {
        return this._getFloat(this.peer + offset);
    }

    private native float _getFloat(long var1);

    public double getDouble(long offset) {
        return Pointer._getDouble(this.peer + offset);
    }

    private static native double _getDouble(long var0);

    public Pointer getPointer(long offset) {
        return Pointer._getPointer(this.peer + offset);
    }

    private static native Pointer _getPointer(long var0);

    public ByteBuffer getByteBuffer(long offset, long length) {
        return this._getDirectByteBuffer(this.peer + offset, length).order(ByteOrder.nativeOrder());
    }

    private native ByteBuffer _getDirectByteBuffer(long var1, long var3);

    public String getString(long offset, boolean wide) {
        return Pointer._getString(this.peer + offset, wide);
    }

    private static native String _getString(long var0, boolean var2);

    public String getString(long offset) {
        long len;
        String encoding = System.getProperty("jna.encoding");
        if (encoding != null && (len = this.indexOf(offset, 0)) != -1) {
            if (len > Integer.MAX_VALUE) {
                throw new OutOfMemoryError("String exceeds maximum length: " + len);
            }
            byte[] data = this.getByteArray(offset, (int)len);
            try {
                return new String(data, encoding);
            }
            catch (UnsupportedEncodingException e) {
                // empty catch block
            }
        }
        return this.getString(offset, false);
    }

    public byte[] getByteArray(long offset, int arraySize) {
        byte[] buf = new byte[arraySize];
        this.read(offset, buf, 0, arraySize);
        return buf;
    }

    public char[] getCharArray(long offset, int arraySize) {
        char[] buf = new char[arraySize];
        this.read(offset, buf, 0, arraySize);
        return buf;
    }

    public short[] getShortArray(long offset, int arraySize) {
        short[] buf = new short[arraySize];
        this.read(offset, buf, 0, arraySize);
        return buf;
    }

    public int[] getIntArray(long offset, int arraySize) {
        int[] buf = new int[arraySize];
        this.read(offset, buf, 0, arraySize);
        return buf;
    }

    public long[] getLongArray(long offset, int arraySize) {
        long[] buf = new long[arraySize];
        this.read(offset, buf, 0, arraySize);
        return buf;
    }

    public float[] getFloatArray(long offset, int arraySize) {
        float[] buf = new float[arraySize];
        this.read(offset, buf, 0, arraySize);
        return buf;
    }

    public double[] getDoubleArray(long offset, int arraySize) {
        double[] buf = new double[arraySize];
        this.read(offset, buf, 0, arraySize);
        return buf;
    }

    public Pointer[] getPointerArray(long base) {
        ArrayList<Pointer> array = new ArrayList<Pointer>();
        int offset = 0;
        Pointer p = this.getPointer(base);
        while (p != null) {
            array.add(p);
            p = this.getPointer(base + (long)(offset += SIZE));
        }
        return array.toArray(new Pointer[array.size()]);
    }

    public Pointer[] getPointerArray(long offset, int arraySize) {
        Pointer[] buf = new Pointer[arraySize];
        this.read(offset, buf, 0, arraySize);
        return buf;
    }

    public String[] getStringArray(long base) {
        return this.getStringArray(base, -1, false);
    }

    public String[] getStringArray(long base, int length) {
        return this.getStringArray(base, length, false);
    }

    public String[] getStringArray(long base, boolean wide) {
        return this.getStringArray(base, -1, wide);
    }

    public String[] getStringArray(long base, int length, boolean wide) {
        ArrayList<String> strings = new ArrayList<String>();
        int offset = 0;
        Pointer p = this.getPointer(base);
        if (length != -1) {
            int count = 0;
            while (count++ < length) {
                strings.add(p.getString(0, wide));
                p = this.getPointer(base + (long)(offset += SIZE));
            }
        } else {
            while (p != null) {
                strings.add(p.getString(0, wide));
                p = this.getPointer(base + (long)(offset += SIZE));
            }
        }
        return strings.toArray(new String[strings.size()]);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    void setValue(long offset, Object value, Class type) {
        if (type == Boolean.TYPE) ** GOTO lbl-1000
        v0 = Boolean.class;
        if (type == v0) lbl-1000: // 2 sources:
        {
            this.setInt(offset, Boolean.TRUE.equals(value) != false ? -1 : 0);
            return;
        }
        if (type == Byte.TYPE) ** GOTO lbl-1000
        v1 = Byte.class;
        if (type == v1) lbl-1000: // 2 sources:
        {
            this.setByte(offset, value == null ? 0 : ((Byte)value).byteValue());
            return;
        }
        if (type == Short.TYPE) ** GOTO lbl-1000
        v2 = Short.class;
        if (type == v2) lbl-1000: // 2 sources:
        {
            this.setShort(offset, value == null ? 0 : (Short)value);
            return;
        }
        if (type == Character.TYPE) ** GOTO lbl-1000
        v3 = Character.class;
        if (type == v3) lbl-1000: // 2 sources:
        {
            this.setChar(offset, value == null ? '\u0000' : ((Character)value).charValue());
            return;
        }
        if (type == Integer.TYPE) ** GOTO lbl-1000
        v4 = Integer.class;
        if (type == v4) lbl-1000: // 2 sources:
        {
            this.setInt(offset, value == null ? 0 : (Integer)value);
            return;
        }
        if (type == Long.TYPE) ** GOTO lbl-1000
        v5 = Long.class;
        if (type == v5) lbl-1000: // 2 sources:
        {
            this.setLong(offset, value == null ? 0 : (Long)value);
            return;
        }
        if (type == Float.TYPE) ** GOTO lbl-1000
        v6 = Float.class;
        if (type == v6) lbl-1000: // 2 sources:
        {
            this.setFloat(offset, value == null ? 0.0f : ((Float)value).floatValue());
            return;
        }
        if (type == Double.TYPE) ** GOTO lbl-1000
        v7 = Double.class;
        if (type == v7) lbl-1000: // 2 sources:
        {
            this.setDouble(offset, value == null ? 0.0 : (Double)value);
            return;
        }
        v8 = Pointer.class;
        if (type == v8) {
            this.setPointer(offset, (Pointer)value);
            return;
        }
        v9 = String.class;
        if (type == v9) {
            this.setPointer(offset, (Pointer)value);
            return;
        }
        v10 = WString.class;
        if (type == v10) {
            this.setPointer(offset, (Pointer)value);
            return;
        }
        v11 = Structure.class;
        if (v11.isAssignableFrom(type)) {
            s = (Structure)value;
            v12 = Structure.ByReference.class;
            if (!v12.isAssignableFrom(type)) {
                s.useMemory(this, (int)offset);
                s.write();
                return;
            }
            this.setPointer(offset, s == null ? null : s.getPointer());
            if (s == null) return;
            s.autoWrite();
            return;
        }
        v13 = Callback.class;
        if (v13.isAssignableFrom(type)) {
            this.setPointer(offset, CallbackReference.getFunctionPointer((Callback)value));
            return;
        }
        v14 = Buffer.class;
        if (v14.isAssignableFrom(type)) {
            p = value == null ? null : Native.getDirectBufferPointer((Buffer)value);
            this.setPointer(offset, p);
            return;
        }
        v15 = NativeMapped.class;
        if (v15.isAssignableFrom(type)) {
            tc = NativeMappedConverter.getInstance(type);
            nativeType = tc.nativeType();
            this.setValue(offset, tc.toNative(value, new ToNativeContext()), nativeType);
            return;
        }
        if (type.isArray() == false) throw new IllegalArgumentException("Writing " + type + " to memory is not supported");
        this.setArrayValue(offset, value, type.getComponentType());
    }

    private void setArrayValue(long offset, Object value, Class cls) {
        if (cls == Byte.TYPE) {
            byte[] buf = (byte[])value;
            this.write(offset, buf, 0, buf.length);
        } else if (cls == Short.TYPE) {
            short[] buf = (short[])value;
            this.write(offset, buf, 0, buf.length);
        } else if (cls == Character.TYPE) {
            char[] buf = (char[])value;
            this.write(offset, buf, 0, buf.length);
        } else if (cls == Integer.TYPE) {
            int[] buf = (int[])value;
            this.write(offset, buf, 0, buf.length);
        } else if (cls == Long.TYPE) {
            long[] buf = (long[])value;
            this.write(offset, buf, 0, buf.length);
        } else if (cls == Float.TYPE) {
            float[] buf = (float[])value;
            this.write(offset, buf, 0, buf.length);
        } else if (cls == Double.TYPE) {
            double[] buf = (double[])value;
            this.write(offset, buf, 0, buf.length);
        } else {
            Class class_ = Pointer.class;
            if (class_.isAssignableFrom(cls)) {
                Pointer[] buf = (Pointer[])value;
                this.write(offset, buf, 0, buf.length);
            } else {
                Class class_2 = Structure.class;
                if (class_2.isAssignableFrom(cls)) {
                    Structure[] sbuf = (Structure[])value;
                    Class class_3 = Structure.ByReference.class;
                    if (class_3.isAssignableFrom(cls)) {
                        Pointer[] buf = new Pointer[sbuf.length];
                        for (int i = 0; i < sbuf.length; ++i) {
                            buf[i] = sbuf[i] == null ? null : sbuf[i].getPointer();
                            sbuf[i].write();
                        }
                        this.write(offset, buf, 0, buf.length);
                    } else {
                        for (int i = 0; i < sbuf.length; ++i) {
                            if (sbuf[i] == null) {
                                sbuf[i] = Structure.newInstance(cls);
                            }
                            sbuf[i].useMemory(this, (int)(offset + (long)(i * sbuf[i].size())));
                            sbuf[i].write();
                        }
                    }
                } else {
                    Class class_4 = NativeMapped.class;
                    if (class_4.isAssignableFrom(cls)) {
                        NativeMapped[] buf = (NativeMapped[])value;
                        NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
                        Class nativeType = tc.nativeType();
                        int size = Native.getNativeSize(value.getClass(), value) / buf.length;
                        for (int i = 0; i < buf.length; ++i) {
                            Object element = tc.toNative(buf[i], new ToNativeContext());
                            this.setValue(offset + (long)(i * size), element, nativeType);
                        }
                    } else {
                        throw new IllegalArgumentException("Writing array of " + cls + " to memory not supported");
                    }
                }
            }
        }
    }

    public void setMemory(long offset, long length, byte value) {
        Pointer._setMemory(this.peer + offset, length, value);
    }

    static native void _setMemory(long var0, long var2, byte var4);

    public void setByte(long offset, byte value) {
        Pointer._setByte(this.peer + offset, value);
    }

    private static native void _setByte(long var0, byte var2);

    public void setShort(long offset, short value) {
        Pointer._setShort(this.peer + offset, value);
    }

    private static native void _setShort(long var0, short var2);

    public void setChar(long offset, char value) {
        Pointer._setChar(this.peer + offset, value);
    }

    private static native void _setChar(long var0, char var2);

    public void setInt(long offset, int value) {
        Pointer._setInt(this.peer + offset, value);
    }

    private static native void _setInt(long var0, int var2);

    public void setLong(long offset, long value) {
        Pointer._setLong(this.peer + offset, value);
    }

    private static native void _setLong(long var0, long var2);

    public void setNativeLong(long offset, NativeLong value) {
        if (NativeLong.SIZE == 8) {
            this.setLong(offset, value.longValue());
        } else {
            this.setInt(offset, value.intValue());
        }
    }

    public void setFloat(long offset, float value) {
        Pointer._setFloat(this.peer + offset, value);
    }

    private static native void _setFloat(long var0, float var2);

    public void setDouble(long offset, double value) {
        Pointer._setDouble(this.peer + offset, value);
    }

    private static native void _setDouble(long var0, double var2);

    public void setPointer(long offset, Pointer value) {
        Pointer._setPointer(this.peer + offset, value != null ? value.peer : 0);
    }

    private static native void _setPointer(long var0, long var2);

    public void setString(long offset, String value, boolean wide) {
        Pointer._setString(this.peer + offset, value, wide);
    }

    private static native void _setString(long var0, String var2, boolean var3);

    public void setString(long offset, String value) {
        byte[] data = Native.getBytes(value);
        this.write(offset, data, 0, data.length);
        this.setByte(offset + (long)data.length, 0);
    }

    public String toString() {
        return "native@0x" + Long.toHexString(this.peer);
    }

    static {
        if (SIZE == 0) {
            throw new Error("Native library not initialized");
        }
        NULL = null;
    }

    private static class Opaque
    extends Pointer {
        private final String MSG;

        private Opaque(long peer) {
            super(peer);
            this.MSG = "This pointer is opaque: " + this;
        }

        public long indexOf(long offset, byte value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void read(long bOff, byte[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void read(long bOff, char[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void read(long bOff, short[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void read(long bOff, int[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void read(long bOff, long[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void read(long bOff, float[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void read(long bOff, double[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void write(long bOff, byte[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void write(long bOff, char[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void write(long bOff, short[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void write(long bOff, int[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void write(long bOff, long[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void write(long bOff, float[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void write(long bOff, double[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public byte getByte(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public char getChar(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public short getShort(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public int getInt(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public long getLong(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public float getFloat(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public double getDouble(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public Pointer getPointer(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public String getString(long bOff, boolean wide) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void setByte(long bOff, byte value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void setChar(long bOff, char value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void setShort(long bOff, short value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void setInt(long bOff, int value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void setLong(long bOff, long value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void setFloat(long bOff, float value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void setDouble(long bOff, double value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void setPointer(long offset, Pointer value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public void setString(long offset, String value, boolean wide) {
            throw new UnsupportedOperationException(this.MSG);
        }

        public String toString() {
            return "opaque@0x" + Long.toHexString(this.peer);
        }
    }

}

