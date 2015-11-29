/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db.sql;

import com.botbox.util.ArrayUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.db.DBField;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBObject;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.db.EmptyDBResult;
import se.sics.isl.db.sql.SQLDBField;
import se.sics.isl.db.sql.SQLDBResult;
import se.sics.isl.db.sql.SQLDatabase;

public class SQLDBTable
extends DBTable {
    private static final Logger log = Logger.getLogger(SQLDBTable.class.getName());
    protected final SQLDatabase database;
    private boolean isDropped = false;
    private SQLDBField[] fields;
    private int fieldNumber = 0;
    private int dirtyStartField = -1;
    private boolean dirtyFields = false;

    public SQLDBTable(SQLDatabase database, String name, DatabaseMetaData metaData) {
        super(name);
        this.database = database;
        try {
            ResultSet rs = metaData.getColumns(database.getDatabaseName(), null, name, null);
            while (rs.next()) {
                String columnName = rs.getString(4);
                int sqlType = rs.getInt(5);
                int size = rs.getInt(7);
                int type = this.getDBType(sqlType);
                String defaultValue = rs.getString(13);
                if (type >= 0) {
                    this.addField(columnName, type, size, 0, defaultValue);
                    continue;
                }
                log.warning("ignore column " + columnName + " of unsupported type " + rs.getString(6));
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not read meta info for column " + this.name, e);
            log.log(Level.SEVERE, e.getMessage());
            database.handleError(e);
        }
    }

    public SQLDBTable(SQLDatabase database, String name) {
        super(name);
        this.database = database;
    }

    @Override
    public boolean hasField(String name) {
        if (DBField.indexOf(this.fields, 0, this.fieldNumber, name) >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public DBField createField(String name, int type, int size, int flags, Object defaultValue) {
        if (this.isDropped) {
            throw new IllegalStateException("table " + this.name + " has been dropped");
        }
        if (this.database.isClosed()) {
            throw new IllegalStateException("database with table " + this.name + " has been closed");
        }
        if (DBField.indexOf(this.fields, 0, this.fieldNumber, name) >= 0) {
            throw new IllegalArgumentException("field already exists");
        }
        this.database.validateName(name);
        if (this.dirtyStartField < 0) {
            this.dirtyStartField = this.fieldNumber;
            this.dirtyFields = true;
        }
        return this.addField(name, type, size, flags, defaultValue);
    }

    private SQLDBField addField(String name, int type, int size, int flags, Object defaultValue) {
        SQLDBField field = new SQLDBField(name, type, size, flags, defaultValue, this.database.isSQLite());
        if (this.fields == null) {
            this.fields = new SQLDBField[5];
        } else if (this.fields.length == this.fieldNumber) {
            this.fields = (SQLDBField[])ArrayUtils.setSize(this.fields, this.fieldNumber + 5);
        }
        this.fields[this.fieldNumber++] = field;
        return field;
    }

    @Override
    public void drop() {
        if (this.dropTable()) {
            log.finest(String.valueOf(this.name) + ": table dropped");
            this.database.tableDropped(this);
            this.executeStatement("DROP TABLE `" + this.name + '`');
        }
    }

    protected boolean dropTable() {
        if (this.isDropped) {
            return false;
        }
        this.isDropped = true;
        this.fieldNumber = 0;
        this.fields = null;
        this.dirtyFields = false;
        return true;
    }

    @Override
    public int getFieldCount() {
        return this.fieldNumber;
    }

    @Override
    public DBField getField(int index) {
        if (index >= this.fieldNumber) {
            throw new IndexOutOfBoundsException("index=" + index + ",size=" + this.fieldNumber);
        }
        return this.fields[index];
    }

    @Override
    public int getObjectCount() {
        int count = 0;
        if (this.dirtyFields) {
            this.flush();
        }
        try {
            Statement stm = this.database.getConnection().createStatement();
            ResultSet rs = stm.executeQuery("SELECT count(*) FROM `" + this.name + '`');
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stm.close();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not get table size from " + this.name, e);
            this.database.handleError(e);
        }
        return count;
    }

    @Override
    public void insert(DBObject object) throws NumberFormatException {
        if (this.isDropped) {
            throw new IllegalStateException("table " + this.name + " has been dropped");
        }
        if (object.getFieldCount() == 0) {
            return;
        }
        if (this.dirtyFields) {
            this.flush();
        }
        this.validate(object);
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO `").append(this.name).append("` (");
        int i = 0;
        int n = object.getFieldCount();
        while (i < n) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('`').append(object.getFieldName(i)).append('`');
            ++i;
        }
        sb.append(") VALUES (");
        i = 0;
        n = object.getFieldCount();
        while (i < n) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('?');
            ++i;
        }
        sb.append(')');
        String sqlQuery = sb.toString();
        try {
            PreparedStatement pstmt = this.database.getConnection().prepareStatement(sqlQuery);
            int i2 = 0;
            int n2 = object.getFieldCount();
            while (i2 < n2) {
                Object value = object.getObject(object.getFieldName(i2));
                pstmt.setObject(i2 + 1, value);
                ++i2;
            }
            if (pstmt.executeUpdate() == 0) {
                log.warning("could not insert data " + object);
            }
            pstmt.close();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not insert data " + object + ": " + sqlQuery, e);
            this.database.handleError(e);
            throw (IllegalArgumentException)new IllegalArgumentException("could not insert data").initCause(e);
        }
    }

    @Override
    public int update(DBMatcher matcher, DBObject object) {
        if (this.isDropped) {
            throw new IllegalStateException("table " + this.name + " has been dropped");
        }
        if (object.getFieldCount() == 0) {
            return 0;
        }
        if (this.dirtyFields) {
            this.flush();
        }
        this.validate(object);
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE `").append(this.name).append("` SET ");
        int i = 0;
        int n = object.getFieldCount();
        while (i < n) {
            String fieldName = object.getFieldName(i);
            if (i > 0) {
                sb.append(',');
            }
            sb.append('`').append(fieldName).append("`=?");
            ++i;
        }
        this.addWhereClausePrefix(sb, matcher);
        String sqlQuery = sb.toString();
        try {
            PreparedStatement pstmt = this.database.getConnection().prepareStatement(sqlQuery);
            int i2 = 0;
            int n2 = object.getFieldCount();
            while (i2 < n2) {
                String fieldName = object.getFieldName(i2);
                Object value = object.getObject(fieldName);
                pstmt.setObject(i2 + 1, value);
                ++i2;
            }
            this.addWhereClausePostfix(pstmt, object.getFieldCount(), matcher);
            int result = pstmt.executeUpdate();
            pstmt.close();
            return result;
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not execute update: " + sqlQuery, e);
            this.database.handleError(e);
            throw (IllegalArgumentException)new IllegalArgumentException("could not update data").initCause(e);
        }
    }

    @Override
    public int remove(DBMatcher matcher) {
        if (this.isDropped) {
            throw new IllegalStateException("table " + this.name + " has been dropped");
        }
        if (this.dirtyFields) {
            this.flush();
        }
        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM `").append(this.name).append('`');
        this.addWhereClausePrefix(sb, matcher);
        String sqlQuery = sb.toString();
        try {
            PreparedStatement pstmt = this.database.getConnection().prepareStatement(sqlQuery);
            this.addWhereClausePostfix(pstmt, 0, matcher);
            int result = pstmt.executeUpdate();
            pstmt.close();
            return result;
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not remove " + matcher + ": " + sqlQuery, e);
            this.database.handleError(e);
            throw (IllegalArgumentException)new IllegalArgumentException("could not remove data").initCause(e);
        }
    }

    @Override
    public DBResult select() {
        if (this.dirtyFields) {
            this.flush();
        }
        try {
            Statement stm = this.database.getConnection().createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM `" + this.name + '`');
            return new SQLDBResult(this, stm, rs);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not select " + this.name, e);
            this.database.handleError(e);
            return new EmptyDBResult();
        }
    }

    @Override
    public DBResult select(DBMatcher matcher) {
        if (matcher == null) {
            return this.select();
        }
        if (this.dirtyFields) {
            this.flush();
        }
        StringBuffer sb = new StringBuffer().append("SELECT * FROM `").append(this.name).append('`');
        this.addWhereClausePrefix(sb, matcher);
        try {
            PreparedStatement pstmt = this.database.getConnection().prepareStatement(sb.toString());
            this.addWhereClausePostfix(pstmt, 0, matcher);
            ResultSet rs = pstmt.executeQuery();
            return new SQLDBResult(this, pstmt, rs);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not select " + this.name, e);
            this.database.handleError(e);
            return new EmptyDBResult();
        }
    }

    @Override
    public void flush() {
        if (this.dirtyFields) {
            int i;
            String prefix = "ALTER TABLE `" + this.name + "` ";
            StringBuffer sb = new StringBuffer();
            if (this.dirtyStartField == 0) {
                sb.append("CREATE TABLE `").append(this.name).append("` (");
                i = this.dirtyStartField;
                while (i < this.fieldNumber) {
                    if (i > this.dirtyStartField) {
                        sb.append(", ");
                    }
                    this.fields[i].addBasicType(sb);
                    ++i;
                }
                i = this.dirtyStartField;
                while (i < this.fieldNumber) {
                    this.fields[i].addExtraTypeInfo(sb);
                    ++i;
                }
                sb.append(')');
            } else {
                sb.append(prefix).append(" ADD ");
                i = this.dirtyStartField;
                while (i < this.fieldNumber) {
                    if (i > this.dirtyStartField) {
                        sb.append(", ADD ");
                    }
                    this.fields[i].addBasicType(sb);
                    ++i;
                }
                i = this.dirtyStartField;
                while (i < this.fieldNumber) {
                    this.fields[i].addExtraTypeInfoChange(sb);
                    ++i;
                }
            }
            String sql = sb.toString();
            try {
                Statement stm = this.database.getConnection().createStatement();
                stm.execute(sql);
                stm.close();
                this.dirtyStartField = -1;
                this.dirtyFields = false;
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not alter table fields in " + this.name + ": \"" + sql + '\"', e);
                this.database.handleError(e);
            }
        }
    }

    private void validate(DBObject object) {
        int i = 0;
        int n = object.getFieldCount();
        while (i < n) {
            if (DBField.indexOf(this.fields, 0, this.fieldNumber, object.getFieldName(i)) < 0) {
                throw new IllegalArgumentException("unknown field '" + object.getFieldName(i) + '\'');
            }
            ++i;
        }
    }

    private void addWhereClausePrefix(StringBuffer sb, DBMatcher matcher) {
        if (matcher.getFieldCount() > 0) {
            sb.append(" WHERE ");
            int i = 0;
            int n = matcher.getFieldCount();
            while (i < n) {
                String fieldName = matcher.getFieldName(i);
                if (i > 0) {
                    sb.append(" AND ");
                }
                sb.append('`').append(fieldName).append("`=?");
                ++i;
            }
        }
        if (!this.database.isSQLite()) {
            int skip = matcher.getSkip();
            int limit = matcher.getLimit();
            if (limit > 0) {
                sb.append(" LIMIT ");
                if (skip > 0) {
                    sb.append(skip).append(',');
                }
                sb.append(limit);
            } else if (skip > 0) {
                sb.append(" OFFSET ").append(skip);
            }
        }
    }

    private void addWhereClausePostfix(PreparedStatement pstmt, int index, DBMatcher matcher) throws SQLException {
        if (matcher.getFieldCount() > 0) {
            int i = 0;
            int n = matcher.getFieldCount();
            while (i < n) {
                String fieldName = matcher.getFieldName(i);
                Object value = matcher.getObject(fieldName);
                pstmt.setObject(index + i + 1, value);
                ++i;
            }
        }
    }

    private void executeStatement(String sql) {
        try {
            Statement stm = this.database.getConnection().createStatement();
            stm.execute(sql);
            stm.close();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not execute \"" + sql + '\"', e);
            this.database.handleError(e);
        }
    }

    private int getDBType(int sqlType) {
        switch (sqlType) {
            case -7: 
            case -6: 
            case -5: 
            case 4: 
            case 5: {
                return 0;
            }
            case 6: 
            case 7: 
            case 8: {
                return 3;
            }
            case 2: {
                return -1;
            }
            case 3: {
                return -1;
            }
            case 1: {
                return 5;
            }
            case 12: {
                return 4;
            }
            case -1: {
                return -1;
            }
            case 91: 
            case 92: 
            case 93: {
                return 2;
            }
            case -2: {
                return -1;
            }
            case -3: {
                return -1;
            }
            case -4: {
                return -1;
            }
            case 0: {
                return -1;
            }
            case 1111: {
                return -1;
            }
            case 2000: {
                return -1;
            }
            case 2001: {
                return -1;
            }
            case 2002: {
                return -1;
            }
            case 2003: {
                return -1;
            }
            case 2004: {
                return -1;
            }
            case 2005: {
                return -1;
            }
            case 2006: {
                return -1;
            }
            case 70: {
                return -1;
            }
            case 16: {
                return 5;
            }
        }
        return -1;
    }
}

