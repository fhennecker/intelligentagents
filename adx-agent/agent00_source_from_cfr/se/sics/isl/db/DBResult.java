/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db;

import se.sics.isl.db.DBField;

public abstract class DBResult {
    protected DBResult() {
    }

    public abstract int getFieldCount();

    public abstract DBField getField(int var1);

    public abstract int getInt(String var1);

    public abstract long getLong(String var1);

    public abstract double getDouble(String var1);

    public abstract String getString(String var1);

    public abstract Object getObject(String var1);

    public abstract long getTimestamp(String var1);

    public abstract boolean next();

    public abstract void close();
}

