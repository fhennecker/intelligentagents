/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db;

import com.botbox.util.ArrayUtils;
import se.sics.isl.db.Database;

public class DBObject {
    private String[] names;
    private Object[] values;
    private int fieldNumber = 0;

    public void clear() {
        int count = this.fieldNumber;
        this.fieldNumber = 0;
        while (--count >= 0) {
            this.names[count] = null;
            this.values[count] = null;
        }
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
        int index = ArrayUtils.indexOf(this.names, 0, this.fieldNumber, name);
        return index < 0 ? null : this.values[index];
    }

    public void setObject(String name, Object value) {
        int index = ArrayUtils.indexOf(this.names, 0, this.fieldNumber, name);
        if (index < 0) {
            if (this.names == null) {
                this.names = new String[10];
                this.values = new Object[10];
            } else if (this.names.length <= this.fieldNumber) {
                this.names = (String[])ArrayUtils.setSize(this.names, this.fieldNumber + 10);
                this.values = ArrayUtils.setSize(this.values, this.fieldNumber + 10);
            }
            index = this.fieldNumber++;
            this.names[index] = name;
        }
        this.values[index] = value;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer().append(DBObject.class.getName()).append('[');
        int i = 0;
        int n = this.fieldNumber;
        while (i < n) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(this.names[i]).append('=').append(this.values[i]);
            ++i;
        }
        return sb.append(']').toString();
    }
}

