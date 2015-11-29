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

public class ObjectField
extends FileDBField {
    private Object[] values;
    private int lastIndex = -1;
    private Object lastValue;

    protected ObjectField(FileDBTable table, String name, int type, int size, int flags, Object defaultValue) {
        super(table, name, type, size, flags, defaultValue);
        if (type == 5) {
            throw new IllegalArgumentException("byte format not yet supported!!!");
        }
    }

    @Override
    protected String getString(int index) {
        Object value = this.getObject(index);
        return value == null ? null : value.toString();
    }

    @Override
    protected Object getObject(int index) {
        if (index >= this.table.getObjectCount()) {
            throw new IllegalArgumentException("no such Object: " + index + ",size=" + this.table.getObjectCount());
        }
        return this.values[index];
    }

    @Override
    protected int indexOf(Object val2, int start, int end) {
        if (val2 == null) {
            int j = start;
            while (j < end) {
                if (this.values[j] == null) {
                    return j;
                }
                ++j;
            }
        } else {
            int j = start;
            while (j < end) {
                if (val2.equals(this.values[j])) {
                    return j;
                }
                ++j;
            }
        }
        return -1;
    }

    @Override
    protected boolean match(int index, Object value) {
        if (value != null && !value.equals(this.getObject(index))) {
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
        this.lastValue = value != null ? value.toString() : (index >= this.table.getObjectCount() && this.defaultValue != null ? this.defaultValue : null);
        if (this.isUnique() && (this.indexOf(this.lastValue, 0, index) >= 0 || this.indexOf(this.lastValue, index + 1, this.table.getObjectCount()) >= 0)) {
            throw new IllegalArgumentException("An Object with " + this.name + " = '" + this.lastValue + "' already exists");
        }
        this.lastIndex = index;
    }

    @Override
    protected void set() {
        if (this.lastIndex >= 0) {
            if (this.values == null) {
                this.values = new Object[this.lastIndex + 10];
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
            this.values = new Object[index + 10];
            startIndex = 0;
        } else if (this.values.length <= index) {
            startIndex = this.values.length;
            this.values = ArrayUtils.setSize(this.values, index + 10);
        }
        if (startIndex >= 0 && this.defaultValue != null) {
            int i = startIndex;
            while (i < index) {
                this.values[i] = this.defaultValue;
                ++i;
            }
        }
    }

    @Override
    protected void loadState(ObjectInputStream oin, int number) throws IOException, ClassNotFoundException {
        Object[] values = new Object[number];
        int i = 0;
        while (i < number) {
            values[i] = oin.readObject();
            ++i;
        }
        this.values = values;
    }

    @Override
    protected void saveState(ObjectOutputStream oout) throws IOException {
        int len = this.table.getObjectCount();
        int i = 0;
        while (i < len) {
            Object v = this.values[i];
            oout.writeObject(v == null ? null : v.toString());
            ++i;
        }
    }
}

