/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db;

public abstract class DBField {
    public static final int INTEGER = 0;
    public static final int LONG = 1;
    public static final int TIMESTAMP = 2;
    public static final int DOUBLE = 3;
    public static final int STRING = 4;
    public static final int BYTE = 5;
    public static final int UNIQUE = 1;
    public static final int AUTOINCREMENT = 2;
    public static final int INDEX = 4;
    public static final int MAY_BE_NULL = 8;
    public static final int PRIMARY = 16;
    protected final String name;
    protected final int type;
    protected final int size;
    protected final int flags;
    protected final Object defaultValue;

    protected DBField(String name, int type, int size, int flags, Object defaultValue) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.type = type;
        this.size = size;
        this.flags = flags;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public int getType() {
        return this.type;
    }

    public int getSize() {
        return this.size;
    }

    public int getFlags() {
        return this.flags;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isUnique() {
        if ((this.flags & 1) != 0) {
            return true;
        }
        return false;
    }

    public static int indexOf(DBField[] fields, int start, int end, String name) {
        int i = start;
        while (i < end) {
            if (name.equals(fields[i].name)) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}

