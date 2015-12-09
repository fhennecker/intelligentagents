/*
 * Decompiled with CFR 0_110.
 */
package com.botbox.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;

public class ArrayQueue
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 5791745982858131414L;
    private transient Object[] queueData;
    private transient int first = 0;
    private transient int last = 0;
    private int size = 0;

    public ArrayQueue() {
        this(10);
    }

    public ArrayQueue(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("illegal capacity: " + initialCapacity);
        }
        this.queueData = new Object[initialCapacity];
    }

    public void ensureCapacity(int minCapacity) {
        int capacity = this.queueData.length;
        if (capacity < minCapacity) {
            int newCapacity = capacity * 3 / 2 + 1;
            this.set(newCapacity < minCapacity ? minCapacity : newCapacity);
        }
    }

    private void set(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        this.copy(newData);
        this.first = 0;
        this.last = this.size;
        this.queueData = newData;
    }

    private void copy(Object[] newData) {
        if (this.first < this.last) {
            System.arraycopy(this.queueData, this.first, newData, 0, this.size);
        } else if (this.size > 0) {
            int capacity = this.queueData.length;
            System.arraycopy(this.queueData, this.first, newData, 0, capacity - this.first);
            if (this.last > 0) {
                System.arraycopy(this.queueData, 0, newData, capacity - this.first, this.last);
            }
        }
    }

    public void trimToSize() {
        if (this.size < this.queueData.length) {
            this.set(this.size);
        }
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        if (this.size == 0) {
            return true;
        }
        return false;
    }

    public boolean contains(Object element) {
        if (this.indexOf(element) >= 0) {
            return true;
        }
        return false;
    }

    public int indexOf(Object element) {
        return this.indexOf(element, 0);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public int indexOf(Object element, int index) {
        if (index < 0) return -1;
        if (index >= this.size) {
            return -1;
        }
        capacity = this.queueData.length;
        n = (this.first + index) % capacity;
        if (element != null) ** GOTO lbl18
        while (index < this.size) {
            if (this.queueData[n] == null) {
                return index;
            }
            n = (n + 1) % capacity;
            ++index;
        }
        return -1;
lbl-1000: // 1 sources:
        {
            if (element.equals(this.queueData[n])) {
                return index;
            }
            n = (n + 1) % capacity;
            ++index;
lbl18: // 2 sources:
            ** while (index < this.size)
        }
lbl19: // 1 sources:
        return -1;
    }

    private int lastIndexOf(Object element) {
        if (this.size == 0) {
            return -1;
        }
        int n = this.last;
        int index = this.size;
        if (element == null) {
            do {
                n = n > 0 ? --n : this.queueData.length - 1;
                --index;
                if (this.queueData[n] != null) continue;
                return index;
            } while (index > 0);
        } else {
            do {
                n = n > 0 ? --n : this.queueData.length - 1;
                --index;
                if (!element.equals(this.queueData[n])) continue;
                return index;
            } while (index > 0);
        }
        return -1;
    }

    public Object get(int index) {
        return this.queueData[this.getIndex(index)];
    }

    public Object set(int index, Object element) {
        index = this.getIndex(index);
        Object oldValue = this.queueData[index];
        this.queueData[index] = element;
        return oldValue;
    }

    public boolean add(Object element) {
        this.ensureCapacity(this.size + 1);
        this.queueData[this.last] = element;
        this.last = (this.last + 1) % this.queueData.length;
        ++this.size;
        return true;
    }

    public void add(int index, Object element) {
        if (index == this.size) {
            this.add(element);
        } else {
            this.ensureCapacity(this.size + 1);
            index = this.getIndex(index);
            if (index == this.first) {
                if (this.first > 0) {
                    --this.first;
                    index = this.first;
                } else {
                    index = this.first = this.queueData.length - 1;
                }
            } else if (index < this.last) {
                System.arraycopy(this.queueData, index, this.queueData, index + 1, this.last - index);
                this.last = (this.last + 1) % this.queueData.length;
            } else {
                System.arraycopy(this.queueData, this.first, this.queueData, this.first - 1, index - this.first);
                --index;
                --this.first;
            }
            this.queueData[index] = element;
            ++this.size;
        }
    }

    public Object remove(int index) {
        index = this.getIndex(index);
        Object value = this.queueData[index];
        if (index == this.first) {
            this.queueData[this.first] = null;
            this.first = (this.first + 1) % this.queueData.length;
        } else if (index < this.last) {
            --this.last;
            if (index < this.last) {
                System.arraycopy(this.queueData, index + 1, this.queueData, index, this.last - index);
            }
            this.queueData[this.last] = null;
        } else if (this.last == 0 && index == this.queueData.length - 1) {
            this.queueData[index] = null;
            this.last = this.queueData.length - 1;
        } else {
            System.arraycopy(this.queueData, this.first, this.queueData, this.first + 1, index - this.first);
            this.queueData[this.first++] = null;
        }
        --this.size;
        return value;
    }

    public void clear() {
        int queueLen = this.queueData.length;
        int i = 0;
        int index = this.first;
        while (i < this.size) {
            this.queueData[index] = null;
            index = (index + 1) % queueLen;
            ++i;
        }
        this.size = 0;
        this.last = 0;
        this.first = 0;
    }

    public Object clone() {
        try {
            ArrayQueue q = (ArrayQueue)super.clone();
            q.set(q.size);
            return q;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public Object[] toArray() {
        Object[] array = new Object[this.size];
        this.copy(array);
        return array;
    }

    public Object[] toArray(Object[] array) {
        if (array.length < this.size) {
            array = (Object[])Array.newInstance(array.getClass().getComponentType(), this.size);
        }
        this.copy(array);
        if (array.length > this.size) {
            array[this.size] = null;
        }
        return array;
    }

    private int getIndex(int index) {
        if (index >= 0 && index < this.size) {
            return (this.first + index) % this.queueData.length;
        }
        throw new IndexOutOfBoundsException("index=" + index + " size=" + this.size);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        int queueLen = this.queueData.length;
        out.defaultWriteObject();
        out.writeInt(queueLen);
        int i = 0;
        int index = this.first;
        while (i < this.size) {
            out.writeObject(this.queueData[index]);
            index = (index + 1) % queueLen;
            ++i;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.queueData = new Object[in.readInt()];
        int i = 0;
        while (i < this.size) {
            this.queueData[i] = in.readObject();
            ++i;
        }
        this.first = 0;
        this.last = this.size;
    }
}

