/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db;

import se.sics.isl.db.DBTable;
import se.sics.isl.db.Database;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;

public class PrefixDatabase
extends Database {
    private final String prefix;
    private final Database database;

    public PrefixDatabase(String prefix, Database database, ConfigManager config, String configPrefix) {
        this.prefix = prefix;
        this.database = database;
        try {
            this.init(database.getName(), config, configPrefix);
        }
        catch (IllegalConfigurationException var5_5) {
            // empty catch block
        }
    }

    @Override
    protected void init(ConfigManager config, String prefix) {
    }

    @Override
    public DBTable createTable(String name) {
        return this.database.createTable(String.valueOf(this.prefix) + name);
    }

    @Override
    public DBTable getTable(String name) {
        return this.database.getTable(String.valueOf(this.prefix) + name);
    }

    @Override
    public void flush() {
        this.database.flush();
    }

    @Override
    public void drop() {
    }

    @Override
    public boolean isClosed() {
        return this.database.isClosed();
    }

    @Override
    public void close() {
    }
}

