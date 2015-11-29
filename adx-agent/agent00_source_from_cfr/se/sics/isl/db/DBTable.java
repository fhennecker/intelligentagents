/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db;

import se.sics.isl.db.DBField;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBObject;
import se.sics.isl.db.DBResult;

public abstract class DBTable {
    protected final String name;

    public DBTable(String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract boolean hasField(String var1);

    public DBField createField(String name, int type, int size, int flags) {
        return this.createField(name, type, size, flags, null);
    }

    public abstract DBField createField(String var1, int var2, int var3, int var4, Object var5);

    public abstract void drop();

    public abstract int getFieldCount();

    public abstract DBField getField(int var1);

    public abstract int getObjectCount();

    public abstract void insert(DBObject var1);

    public abstract int update(DBMatcher var1, DBObject var2);

    public abstract int remove(DBMatcher var1);

    public abstract DBResult select();

    public abstract DBResult select(DBMatcher var1);

    public void flush() {
    }

    public static int indexOf(DBTable[] tables, int start, int end, String name) {
        int i = start;
        while (i < end) {
            if (name.equalsIgnoreCase(tables[i].name)) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}

