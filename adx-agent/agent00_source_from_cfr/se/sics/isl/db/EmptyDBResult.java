/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db;

import se.sics.isl.db.DBField;
import se.sics.isl.db.DBResult;

public class EmptyDBResult
extends DBResult {
    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public DBField getField(int index) {
        throw new IndexOutOfBoundsException("index=" + index + ",size=" + 0);
    }

    @Override
    public int getInt(String name) {
        throw new IllegalStateException("no more results");
    }

    @Override
    public long getLong(String name) {
        throw new IllegalStateException("no more results");
    }

    @Override
    public double getDouble(String name) {
        throw new IllegalStateException("no more results");
    }

    @Override
    public String getString(String name) {
        throw new IllegalStateException("no more results");
    }

    @Override
    public Object getObject(String name) {
        throw new IllegalStateException("no more results");
    }

    @Override
    public long getTimestamp(String name) {
        throw new IllegalStateException("no more results");
    }

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public void close() {
    }
}

