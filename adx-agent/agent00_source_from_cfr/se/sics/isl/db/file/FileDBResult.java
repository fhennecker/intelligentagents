/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db.file;

import java.util.ConcurrentModificationException;
import se.sics.isl.db.DBField;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.Database;
import se.sics.isl.db.file.DoubleField;
import se.sics.isl.db.file.FileDBField;
import se.sics.isl.db.file.FileDBTable;
import se.sics.isl.db.file.IntField;
import se.sics.isl.db.file.LongField;

public class FileDBResult
extends DBResult {
    private final FileDBTable table;
    private int changeID;
    private int skip;
    private int limit;
    private int lastIndex = -1;
    private int matchesCounter = 0;
    private int[] selectIndex;
    private Object[] selectValues;

    FileDBResult(DBMatcher matcher, FileDBTable table, int[] selectIndex, Object[] selectValues) {
        this.table = table;
        this.changeID = table.getChangeCount();
        this.selectIndex = selectIndex;
        this.selectValues = selectValues;
        if (matcher != null) {
            this.skip = matcher.getSkip();
            this.limit = matcher.getLimit();
        }
    }

    @Override
    public int getFieldCount() {
        return this.table.getFieldCount();
    }

    @Override
    public DBField getField(int index) {
        return this.table.getField(index);
    }

    @Override
    public int getInt(String name) {
        FileDBField field = this.table.getField(name);
        if (this.lastIndex < 0) {
            throw new IllegalStateException("no more results");
        }
        if (field instanceof IntField) {
            return ((IntField)field).getInt(this.lastIndex);
        }
        return Database.parseInt(field.getObject(this.lastIndex), 0);
    }

    @Override
    public long getLong(String name) {
        FileDBField field = this.table.getField(name);
        if (this.lastIndex < 0) {
            throw new IllegalStateException("no more results");
        }
        if (field instanceof LongField) {
            return ((LongField)field).getLong(this.lastIndex);
        }
        return Database.parseLong(field.getObject(this.lastIndex), 0);
    }

    @Override
    public long getTimestamp(String name) {
        return this.getLong(name);
    }

    @Override
    public double getDouble(String name) {
        FileDBField field = this.table.getField(name);
        if (this.lastIndex < 0) {
            throw new IllegalStateException("no more results");
        }
        if (field instanceof DoubleField) {
            return ((DoubleField)field).getDouble(this.lastIndex);
        }
        return Database.parseDouble(field.getObject(this.lastIndex), 0.0);
    }

    @Override
    public String getString(String name) {
        FileDBField field = this.table.getField(name);
        if (this.lastIndex < 0) {
            throw new IllegalStateException("no more results");
        }
        return field.getString(this.lastIndex);
    }

    @Override
    public Object getObject(String name) {
        FileDBField field = this.table.getField(name);
        if (this.lastIndex < 0) {
            throw new IllegalStateException("no more results");
        }
        return field.getObject(this.lastIndex);
    }

    @Override
    public boolean next() {
        if (this.changeID != this.table.getChangeCount()) {
            throw new ConcurrentModificationException();
        }
        if (this.skip > 0) {
            int objectCount = this.table.getObjectCount();
            do {
                this.lastIndex = this.table.next(this.selectIndex, this.selectValues, this.lastIndex);
            } while (this.lastIndex < objectCount && --this.skip > 0);
            if (this.lastIndex >= objectCount) {
                return false;
            }
        }
        if (this.limit > 0 && this.matchesCounter >= this.limit) {
            return false;
        }
        this.lastIndex = this.table.next(this.selectIndex, this.selectValues, this.lastIndex);
        if (this.lastIndex < this.table.getObjectCount()) {
            ++this.matchesCounter;
            return true;
        }
        return false;
    }

    @Override
    public void close() {
    }

    int getLastIndex() {
        return this.lastIndex;
    }

    void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    void setChangeID(int changeID) {
        this.changeID = changeID;
    }
}

