/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db.file;

import com.botbox.util.ArrayUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import se.sics.isl.db.file.FileDBField;
import se.sics.isl.db.file.FileDBTable;

public class LongField
extends FileDBField {
    private long[] values;
    private int lastIndex = -1;
    private long lastValue;
    private long defValue;

    protected LongField(FileDBTable table, String name, int type, int size, int flags, Object defaultValue) {
        super(table, name, type, size, flags, defaultValue);
        this.defValue = defaultValue != null ? this.getValue(defaultValue) : 0;
    }

    @Override
    protected String getString(int index) {
        long value = this.getLong(index);
        return Long.toString(value);
    }

    @Override
    protected Object getObject(int index) {
        return new Long(this.getLong(index));
    }

    long getLong(int index) {
        if (index >= this.table.getObjectCount()) {
            throw new IllegalArgumentException("no such object: " + index + ",size=" + this.table.getObjectCount());
        }
        return this.values[index];
    }

    private long getValue(Object value) {
        if (value instanceof Long) {
            return (Long)value;
        }
        try {
            return Long.parseLong(value.toString());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("long expected: " + value);
        }
    }

    @Override
    protected int indexOf(Object value, int start, int end) {
        return this.indexOf(this.getValue(value), start, end);
    }

    protected int indexOf(long val2, int start, int end) {
        int j = start;
        while (j < end) {
            if (this.values[j] == val2) {
                return j;
            }
            ++j;
        }
        return -1;
    }

    @Override
    protected boolean match(int index, Object value) {
        if (value != null && this.getValue(value) != this.getLong(index)) {
            return false;
        }
        return true;
    }

    @Override
    protected void remove(int index) {
        System.arraycopy(this.values, index + 1, this.values, index, this.table.getObjectCount() - index - 1);
    }

    @Override
    protected void prepareSet(int index, Object value) {
        this.lastValue = value != null ? this.getValue(value) : (index >= this.table.getObjectCount() && this.defaultValue != null ? this.defValue : 0);
        if (this.isUnique() && (this.indexOf(this.lastValue, 0, index) >= 0 || this.indexOf(this.lastValue, index + 1, this.table.getObjectCount()) >= 0)) {
            throw new IllegalArgumentException("An object with " + this.name + " = '" + this.lastValue + "' already exists");
        }
        this.lastIndex = index;
    }

    @Override
    protected void set() {
        if (this.lastIndex >= 0) {
            if (this.values == null) {
                this.values = new long[this.lastIndex + 10];
            } else if (this.values.length <= this.lastIndex) {
                this.values = ArrayUtils.setSize(this.values, this.lastIndex + 10);
            }
            this.values[this.lastIndex] = this.lastValue;
            this.lastIndex = -1;
        }
    }

    @Override
    protected void ensureCapacity(int index) {
        int startIndex = -1;
        if (this.values == null) {
            this.values = new long[index + 10];
            startIndex = 0;
        } else if (this.values.length <= index) {
            startIndex = this.values.length;
            this.values = ArrayUtils.setSize(this.values, index + 10);
        }
        if (startIndex >= 0) {
            int i = startIndex;
            while (i < index) {
                this.values[i] = this.defValue;
                ++i;
            }
        }
    }

    @Override
    protected void loadState(ObjectInputStream oin, int number) throws IOException {
        long[] values = new long[number];
        int i = 0;
        while (i < number) {
            values[i] = oin.readLong();
            ++i;
        }
        this.values = values;
    }

    @Override
    protected void saveState(ObjectOutputStream oout) throws IOException {
        int len = this.table.getObjectCount();
        int i = 0;
        while (i < len) {
            oout.writeLong(this.values[i]);
            ++i;
        }
    }
}

