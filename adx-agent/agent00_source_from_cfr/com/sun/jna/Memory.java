/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;

public class Memory
extends Pointer {
    private static Map buffers = new WeakHashMap<K, V>();
    protected long size;

    public static void purge() {
        buffers.size();
    }

    public Memory(long size) {
        this.size = size;
        if (size <= 0) {
            throw new IllegalArgumentException("Allocation size must be greater than zero");
        }
        this.peer = Memory.malloc(size);
        if (this.peer == 0) {
            throw new OutOfMemoryError("Cannot allocate " + size + " bytes");
        }
    }

    protected Memory() {
    }

    public Pointer share(long offset) {
        return this.share(offset, this.getSize() - offset);
    }

    public Pointer share(long offset, long sz) {
        if (offset == 0 && sz == this.getSize()) {
            return this;
        }
        this.boundsCheck(offset, sz);
        return new SharedMemory(offset);
    }

    public Memory align(int byteBoundary) {
        if (byteBoundary <= 0) {
            throw new IllegalArgumentException("Byte boundary must be positive: " + byteBoundary);
        }
        for (int i = 0; i < 32; ++i) {
            if (byteBoundary != 1 << i) continue;
            long mask = (long)byteBoundary - 1 ^ -1;
            if ((this.peer & mask) != this.peer) {
                long newPeer = this.peer + (long)byteBoundary - 1 & mask;
                long newSize = this.peer + this.size - newPeer;
                if (newSize <= 0) {
                    throw new IllegalArgumentException("Insufficient memory to align to the requested boundary");
                }
                return (Memory)this.share(newPeer - this.peer, newSize);
            }
            return this;
        }
        throw new IllegalArgumentException("Byte boundary must be a power of two");
    }

    protected void finalize() {
        Memory.free(this.peer);
        this.peer = 0;
    }

    public void clear() {
        this.clear(this.size);
    }

    public boolean isValid() {
        return this.peer != 0;
    }

    public long getSize() {
        return this.size;
    }

    protected void boundsCheck(long off, long sz) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("Invalid offset: " + off);
        }
        if (off + sz > this.size) {
            String msg = "Bounds exceeds available space : size=" + this.size + ", offset=" + (off + sz);
            throw new IndexOutOfBoundsException(msg);
        }
    }

    public void read(long bOff, byte[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 1);
        super.read(bOff, buf, index, length);
    }

    public void read(long bOff, short[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 2);
        super.read(bOff, buf, index, length);
    }

    public void read(long bOff, char[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 2);
        super.read(bOff, buf, index, length);
    }

    public void read(long bOff, int[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 4);
        super.read(bOff, buf, index, length);
    }

    public void read(long bOff, long[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 8);
        super.read(bOff, buf, index, length);
    }

    public void read(long bOff, float[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 4);
        super.read(bOff, buf, index, length);
    }

    public void read(long bOff, double[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 8);
        super.read(bOff, buf, index, length);
    }

    public void write(long bOff, byte[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 1);
        super.write(bOff, buf, index, length);
    }

    public void write(long bOff, short[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 2);
        super.write(bOff, buf, index, length);
    }

    public void write(long bOff, char[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 2);
        super.write(bOff, buf, index, length);
    }

    public void write(long bOff, int[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 4);
        super.write(bOff, buf, index, length);
    }

    public void write(long bOff, long[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 8);
        super.write(bOff, buf, index, length);
    }

    public void write(long bOff, float[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 4);
        super.write(bOff, buf, index, length);
    }

    public void write(long bOff, double[] buf, int index, int length) {
        this.boundsCheck(bOff, length * 8);
        super.write(bOff, buf, index, length);
    }

    public byte getByte(long offset) {
        this.boundsCheck(offset, 1);
        return super.getByte(offset);
    }

    public char getChar(long offset) {
        this.boundsCheck(offset, 1);
        return super.getChar(offset);
    }

    public short getShort(long offset) {
        this.boundsCheck(offset, 2);
        return super.getShort(offset);
    }

    public int getInt(long offset) {
        this.boundsCheck(offset, 4);
        return super.getInt(offset);
    }

    public long getLong(long offset) {
        this.boundsCheck(offset, 8);
        return super.getLong(offset);
    }

    public float getFloat(long offset) {
        this.boundsCheck(offset, 4);
        return super.getFloat(offset);
    }

    public double getDouble(long offset) {
        this.boundsCheck(offset, 8);
        return super.getDouble(offset);
    }

    public Pointer getPointer(long offset) {
        this.boundsCheck(offset, Pointer.SIZE);
        return super.getPointer(offset);
    }

    public ByteBuffer getByteBuffer(long offset, long length) {
        this.boundsCheck(offset, length);
        ByteBuffer b = super.getByteBuffer(offset, length);
        buffers.put(b, this);
        return b;
    }

    public String getString(long offset, boolean wide) {
        this.boundsCheck(offset, 0);
        return super.getString(offset, wide);
    }

    public void setByte(long offset, byte value) {
        this.boundsCheck(offset, 1);
        super.setByte(offset, value);
    }

    public void setChar(long offset, char value) {
        this.boundsCheck(offset, Native.WCHAR_SIZE);
        super.setChar(offset, value);
    }

    public void setShort(long offset, short value) {
        this.boundsCheck(offset, 2);
        super.setShort(offset, value);
    }

    public void setInt(long offset, int value) {
        this.boundsCheck(offset, 4);
        super.setInt(offset, value);
    }

    public void setLong(long offset, long value) {
        this.boundsCheck(offset, 8);
        super.setLong(offset, value);
    }

    public void setFloat(long offset, float value) {
        this.boundsCheck(offset, 4);
        super.setFloat(offset, value);
    }

    public void setDouble(long offset, double value) {
        this.boundsCheck(offset, 8);
        super.setDouble(offset, value);
    }

    public void setPointer(long offset, Pointer value) {
        this.boundsCheck(offset, Pointer.SIZE);
        super.setPointer(offset, value);
    }

    public void setString(long offset, String value, boolean wide) {
        if (wide) {
            this.boundsCheck(offset, (value.length() + 1) * Native.WCHAR_SIZE);
        } else {
            this.boundsCheck(offset, value.getBytes().length + 1);
        }
        super.setString(offset, value, wide);
    }

    protected static native long malloc(long var0);

    protected static native void free(long var0);

    public String toString() {
        return "allocated@0x" + Long.toHexString(this.peer) + " (" + this.size + " bytes)";
    }

    private class SharedMemory
    extends Memory {
        public SharedMemory(long offset) {
            this.size = Memory.this.size - offset;
            this.peer = Memory.this.peer + offset;
        }

        protected void finalize() {
        }

        protected void boundsCheck(long off, long sz) {
            Memory.this.boundsCheck(this.peer - Memory.this.peer + off, sz);
        }

        public String toString() {
            return super.toString() + " (shared from " + Memory.this.toString() + ")";
        }
    }

}

