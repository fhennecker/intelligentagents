/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db.file;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import se.sics.isl.db.DBField;
import se.sics.isl.db.file.FileDBTable;

public abstract class FileDBField
extends DBField {
    protected final FileDBTable table;

    protected FileDBField(FileDBTable table, String name, int type, int size, int flags, Object defaultValue) {
        super(name, type, size, flags, defaultValue);
        this.table = table;
    }

    protected abstract String getString(int var1);

    protected abstract Object getObject(int var1);

    protected abstract int indexOf(Object var1, int var2, int var3);

    protected abstract boolean match(int var1, Object var2);

    protected abstract void remove(int var1);

    protected abstract void prepareSet(int var1, Object var2);

    protected abstract void set();

    protected abstract void ensureCapacity(int var1);

    protected abstract void loadState(ObjectInputStream var1, int var2) throws IOException, ClassNotFoundException;

    protected abstract void saveState(ObjectOutputStream var1) throws IOException;
}

