/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db.sql;

import com.botbox.util.ArrayUtils;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.db.DBField;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBObject;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.db.Database;
import se.sics.isl.db.sql.SQLDBTable;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;

public class SQLDatabase
extends Database {
    private static final Logger log = Logger.getLogger(SQLDatabase.class.getName());
    private String databaseURL = "jdbc:mysql://localhost:3306/mysql";
    private String driverName = "org.gjt.mm.mysql.Driver";
    private String databaseUser = null;
    private String databasePassword = null;
    private String database;
    private Connection databaseConnection;
    private SQLDBTable[] tables;
    private int tableNumber = 0;
    private boolean isDropped = false;
    private boolean isClosed = false;
    private boolean isSQLite = false;

    @Override
    protected void init(ConfigManager config, String prefix) throws IllegalConfigurationException {
        String name = this.getName();
        this.database = config.getProperty(String.valueOf(prefix) + "sql.database", name);
        this.validateName(this.database);
        this.driverName = config.getProperty(String.valueOf(prefix) + "sql.driver", this.driverName);
        this.databaseURL = config.getProperty(String.valueOf(prefix) + "sql.url", this.databaseURL);
        this.databaseUser = config.getProperty(String.valueOf(prefix) + "sql.user");
        this.databasePassword = config.getProperty(String.valueOf(prefix) + "sql.password");
        try {
            Driver driver = (Driver)Class.forName(this.driverName).newInstance();
            Connection cdb = this.getConnection(false);
            this.isSQLite = config.getProperty(String.valueOf(prefix) + "sql.driver").equals("org.sqlite.JDBC");
            if (!this.isSQLite) {
                Statement stm = cdb.createStatement();
                stm.execute("CREATE DATABASE IF NOT EXISTS " + this.database);
                stm.close();
            }
            cdb.setCatalog(this.database);
            DatabaseMetaData meta = cdb.getMetaData();
            log.info(String.valueOf(this.getName()) + ": using database " + this.database + " (" + meta.getDatabaseProductName() + ' ' + meta.getDatabaseProductVersion() + ')');
            ResultSet rs = meta.getTables(this.database, null, null, null);
            ArrayList<SQLDBTable> tableList = null;
            while (rs.next()) {
                if (tableList == null) {
                    tableList = new ArrayList<SQLDBTable>();
                }
                if (rs.getString(3).indexOf("SQLITE") != -1) continue;
                tableList.add(new SQLDBTable(this, rs.getString(3), meta));
            }
            rs.close();
            if (tableList != null) {
                this.tableNumber = tableList.size();
                this.tables = tableList.toArray(new SQLDBTable[this.tableNumber]);
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, String.valueOf(this.getName()) + ": could not open database " + this.database, e);
            throw new IllegalConfigurationException("could not open SQL database " + this.database);
        }
    }

    @Override
    public DBTable createTable(String name) {
        if (this.isDropped) {
            throw new IllegalStateException("database " + this.getName() + " has been dropped");
        }
        if (this.isClosed) {
            throw new IllegalStateException("database " + this.getName() + " has been closed");
        }
        if (this.getTable(name) != null) {
            throw new IllegalArgumentException("table '" + name + "' already exists");
        }
        this.validateName(name);
        SQLDBTable table = new SQLDBTable(this, name);
        log.fine(String.valueOf(this.getName()) + ": added table " + name);
        return this.addTable(table);
    }

    @Override
    public DBTable getTable(String name) {
        int index = DBTable.indexOf(this.tables, 0, this.tableNumber, name);
        return index >= 0 ? this.tables[index] : null;
    }

    @Override
    public void flush() {
        int i = 0;
        while (i < this.tableNumber) {
            this.tables[i].flush();
            ++i;
        }
    }

    @Override
    public void drop() {
        if (!this.isDropped) {
            this.isDropped = true;
            this.isClosed = true;
            log.fine(String.valueOf(this.getName()) + ": database dropped");
            int len = this.tableNumber;
            this.tableNumber = 0;
            int i = 0;
            while (i < this.tableNumber) {
                this.tables[i].dropTable();
                ++i;
            }
            this.tables = null;
            try {
                Connection cdb = this.getConnection(true);
                Statement stm = cdb.createStatement();
                stm.execute("DROP DATABASE " + this.database);
                stm.close();
                cdb.close();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, String.valueOf(this.getName()) + ": could not drop database " + this.database, e);
            }
        }
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override
    public void close() {
        if (!this.isClosed) {
            this.isClosed = true;
            int i2 = 0;
            while (i2 < this.tableNumber) {
                this.tables[i2].flush();
                ++i2;
            }
            try {
                if (this.databaseConnection != null && !this.databaseConnection.isClosed()) {
                    this.databaseConnection.close();
                }
            }
            catch (Exception i2) {
                // empty catch block
            }
        }
    }

    private SQLDBTable addTable(SQLDBTable table) {
        if (this.tables == null) {
            this.tables = new SQLDBTable[5];
        } else if (this.tables.length <= this.tableNumber) {
            this.tables = (SQLDBTable[])ArrayUtils.setSize(this.tables, this.tableNumber + 5);
        }
        this.tables[this.tableNumber++] = table;
        return table;
    }

    Connection getConnection() throws SQLException {
        return this.getConnection(true);
    }

    private Connection getConnection(boolean useDatabase) throws SQLException {
        if (this.isDropped) {
            throw new SQLException("database " + this.getName() + " has been dropped");
        }
        if (this.isClosed) {
            throw new SQLException("database " + this.getName() + " has been closed");
        }
        Connection connection = this.databaseConnection;
        if (connection == null || connection.isClosed()) {
            log.finest(String.valueOf(this.getName()) + ": connecting to database " + this.databaseURL + " (" + this.database + ')');
            connection = this.databaseUser != null ? (this.databaseConnection = DriverManager.getConnection(this.databaseURL, this.databaseUser, this.databasePassword == null ? "" : this.databasePassword)) : (this.databaseConnection = DriverManager.getConnection(this.databaseURL, null));
        }
        if (useDatabase) {
            connection.setCatalog(this.database);
        }
        return connection;
    }

    void handleError(Exception e) {
    }

    String getDatabaseName() {
        return this.database;
    }

    boolean isSQLite() {
        return this.isSQLite;
    }

    void tableDropped(SQLDBTable table) {
        int index = ArrayUtils.indexOf(this.tables, 0, this.tableNumber, table);
        if (index >= 0) {
            --this.tableNumber;
            this.tables[index] = this.tables[this.tableNumber];
            this.tables[this.tableNumber] = null;
            log.fine(String.valueOf(this.getName()) + ": dropped table " + table.getName());
        }
    }

    public static void main(String[] args) throws IllegalConfigurationException {
        Logger.getLogger("").setLevel(Level.FINEST);
        ConfigManager config = new ConfigManager();
        config.setProperty("sql.database", "mytest");
        SQLDatabase db = new SQLDatabase();
        db.init("test", config, "");
        DBTable table = db.getTable("test");
        if (table == null) {
            System.out.println("Creating table test");
            table = db.createTable("test");
            DBField field = table.createField("id", 0, 32, 17, null);
            field = table.createField("score", 0, 32, 0, null);
            DBObject o = new DBObject();
            int i = 0;
            int n = 20;
            while (i < n) {
                o.setInt("id", i);
                o.setInt("score", i * 32);
                table.insert(o);
                ++i;
            }
            table.flush();
        } else {
            System.out.println("Database test already existed");
            if (!table.hasField("hacke")) {
                System.out.println("creating field hacke");
                table.createField("hacke", 0, 32, 7, null);
                table.flush();
            } else {
                System.out.println("field 'hacke' aready exists");
            }
        }
        DBMatcher matcher = new DBMatcher();
        matcher.setLimit(5, 5);
        DBResult result = table.select(matcher);
        while (result.next()) {
            System.out.println("RESULT: id=" + result.getInt("id") + " score=" + result.getInt("score"));
        }
        matcher = new DBMatcher();
        matcher.setInt("id", 17);
        result = table.select(matcher);
        while (result.next()) {
            System.out.println("MATCHED RESULT: id=" + result.getInt("id") + " score=" + result.getInt("score"));
        }
        System.out.println("Number of rows: " + table.getObjectCount());
        matcher.clear();
        matcher.setInt("id", 2);
        System.out.println("REMOVED " + table.remove(matcher) + " objects with id=2");
        System.out.println("Number of rows: " + table.getObjectCount());
        System.out.println("Adding item with id 2 and 2343");
        DBObject o = new DBObject();
        o.setInt("id", 2);
        o.setInt("score", 2343);
        table.insert(o);
        table.flush();
        System.out.println("Number of rows: " + table.getObjectCount());
        matcher.clear();
        matcher.setInt("id", 2);
        result = table.select(matcher);
        while (result.next()) {
            System.out.println("MATCHED RESULT: id=" + result.getInt("id") + " score=" + result.getInt("score"));
        }
        System.out.println("Updating item 2 to score 64");
        o.clear();
        o.setInt("score", 64);
        table.update(matcher, o);
        System.out.println("Number of rows: " + table.getObjectCount());
        matcher.clear();
        matcher.setInt("id", 2);
        result = table.select(matcher);
        while (result.next()) {
            System.out.println("MATCHED RESULT: id=" + result.getInt("id") + " score=" + result.getInt("score"));
        }
    }
}

