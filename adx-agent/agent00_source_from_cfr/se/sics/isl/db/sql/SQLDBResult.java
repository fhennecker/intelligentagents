/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db.sql;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.db.DBField;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.sql.SQLDBTable;

public class SQLDBResult
extends DBResult {
    private static final Logger log = Logger.getLogger(SQLDBResult.class.getName());
    private SQLDBTable table;
    private Statement stm;
    private ResultSet rs;

    public SQLDBResult(SQLDBTable table, Statement stm, ResultSet rs) {
        this.table = table;
        this.stm = stm;
        this.rs = rs;
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
        try {
            return this.rs.getInt(name);
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not getInt " + name, e);
            return 0;
        }
    }

    @Override
    public long getLong(String name) {
        try {
            return this.rs.getLong(name);
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not getLong " + name, e);
            return 0;
        }
    }

    @Override
    public double getDouble(String name) {
        try {
            return this.rs.getDouble(name);
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not getDouble " + name, e);
            return 0.0;
        }
    }

    @Override
    public String getString(String name) {
        try {
            return this.rs.getString(name);
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not getString " + name, e);
            return null;
        }
    }

    @Override
    public Object getObject(String name) {
        try {
            return this.rs.getObject(name);
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not getObject " + name, e);
            return null;
        }
    }

    @Override
    public long getTimestamp(String name) {
        try {
            Timestamp ts = this.rs.getTimestamp(name);
            return ts != null ? ts.getTime() : 0;
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not getTimestamp " + name, e);
            return 0;
        }
    }

    @Override
    public boolean next() {
        boolean hasNext = false;
        try {
            hasNext = this.rs.next();
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not next", e);
        }
        return hasNext;
    }

    @Override
    public void close() {
        try {
            this.rs.close();
            this.stm.close();
        }
        catch (Exception var1_1) {
            // empty catch block
        }
    }
}

