/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db;

import com.botbox.util.ArrayUtils;
import se.sics.isl.db.Database;

public class DBMatcher {
    private String[] names;
    private Object[] values;
    private int fieldNumber = 0;
    private int skip;
    private int limit;

    public void clear() {
        int count = this.fieldNumber;
        this.fieldNumber = 0;
        this.limit = 0;
        this.skip = 0;
        while (--count >= 0) {
            this.names[count] = null;
            this.values[count] = null;
        }
    }

    public int getSkip() {
        return this.skip;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.setLimit(0, limit);
    }

    public void setLimit(int skip, int limit) {
        this.skip = skip;
        this.limit = limit;
    }

    public int getFieldCount() {
        return this.fieldNumber;
    }

    public String getFieldName(int index) {
        if (index >= this.fieldNumber) {
            throw new IndexOutOfBoundsException("index=" + index + ",size=" + this.fieldNumber);
        }
        return this.names[index];
    }

    public int getInt(String name) {
        return Database.parseInt(this.getObject(name), 0);
    }

    public void setInt(String name, int value) {
        this.setObject(name, new Integer(value));
    }

    public long getLong(String name) {
        return Database.parseLong(this.getObject(name), 0);
    }

    public void setLong(String name, long value) {
        this.setObject(name, new Long(value));
    }

    public double getDouble(String name) {
        return Database.parseDouble(this.getObject(name), 0.0);
    }

    public void setDouble(String name, double value) {
        this.setObject(name, new Double(value));
    }

    public String getString(String name) {
        Object value = this.getObject(name);
        return value == null ? null : value.toString();
    }

    public void setString(String name, String value) {
        this.setObject(name, value);
    }

    public long getTimestamp(String name) {
        return this.getLong(name);
    }

    public void setTimestamp(String name, long value) {
        this.setObject(name, new Long(value));
    }

    public Object getObject(String name) {
        int index = ArrayUtils.indexOf(this.names, name);
        if (index < 0) {
            return null;
        }
        return this.values[index];
    }

    public void setObject(String name, Object value) {
        int index = ArrayUtils.indexOf(this.names, 0, this.fieldNumber, name);
        if (index < 0) {
            index = this.fieldNumber++;
            this.ensureCapacity(this.fieldNumber);
            this.names[index] = name;
        }
        this.values[index] = value;
    }

    private void ensureCapacity(int size) {
        if (this.names == null) {
            this.names = new String[size + 10];
            this.values = new Object[size + 10];
        } else if (this.names.length <= size) {
            this.names = (String[])ArrayUtils.setSize(this.names, size + 10);
            this.values = ArrayUtils.setSize(this.values, size + 10);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer().append(DBMatcher.class.getName()).append('[').append(this.skip).append(',').append(this.limit);
        int i = 0;
        int n = this.fieldNumber;
        while (i < n) {
            sb.append(',').append(this.names[i]).append('=').append(this.values[i]);
            ++i;
        }
        return sb.append(']').toString();
    }
}

