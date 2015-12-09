/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db.file;

import com.botbox.util.ArrayUtils;
import java.io.File;
import java.io.PrintStream;
import java.util.logging.Logger;
import se.sics.isl.db.DBField;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBObject;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.db.Database;
import se.sics.isl.db.file.FileDBResult;
import se.sics.isl.db.file.FileDBTable;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;

public class FileDatabase
extends Database {
    private static final Logger log = Logger.getLogger(FileDatabase.class.getName());
    private File databaseRoot;
    private FileDBTable[] tables;
    private int tableNumber = 0;
    private boolean isDropped = false;
    private boolean isClosed = false;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void init(ConfigManager config, String prefix) {
        String directory = config.getProperty(String.valueOf(prefix) + "file.path", this.getName());
        File file = new File(directory);
        boolean create = config.getPropertyAsBoolean(String.valueOf(prefix) + "file.create", false);
        if (file.exists()) {
            if (!file.isDirectory()) throw new IllegalArgumentException("File '" + directory + "' is not a directory");
            this.databaseRoot = file.getAbsoluteFile();
        } else {
            if (!create || !file.mkdirs()) throw new IllegalArgumentException(create ? "Database '" + directory + "' could not be created" : "Database '" + directory + "' does not exist");
            this.databaseRoot = file.getAbsoluteFile();
        }
        log.info(String.valueOf(this.getName()) + ": database opened as " + this.databaseRoot.getPath());
    }

    @Override
    public DBTable createTable(String name) {
        if (this.isDropped) {
            throw new IllegalStateException("database " + this.getName() + " has been dropped");
        }
        if (this.getTable(name) != null) {
            throw new IllegalArgumentException("table '" + name + "' already exists");
        }
        this.validateName(name);
        FileDBTable table = new FileDBTable(this, name, true);
        log.info(String.valueOf(this.getName()) + ": added table " + name);
        return this.addTable(table);
    }

    @Override
    public DBTable getTable(String name) {
        int index = DBTable.indexOf(this.tables, 0, this.tableNumber, name);
        if (index < 0 && !this.isDropped) {
            FileDBTable table = new FileDBTable(this, name, false);
            if (table.exists()) {
                log.finest(String.valueOf(this.getName()) + ": loaded table " + name);
                return this.addTable(table);
            }
            return null;
        }
        return this.tables[index];
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
            log.info(String.valueOf(this.getName()) + ": database dropped");
            int len = this.tableNumber;
            this.tableNumber = 0;
            int i = 0;
            while (i < this.tableNumber) {
                this.tables[i].dropTable();
                ++i;
            }
            this.tables = null;
            File[] files = this.databaseRoot.listFiles();
            int i2 = 0;
            int n = files.length;
            while (i2 < n) {
                files[i2].delete();
                ++i2;
            }
            this.databaseRoot.delete();
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
            int i = 0;
            while (i < this.tableNumber) {
                this.tables[i].flush();
                ++i;
            }
        }
    }

    private FileDBTable addTable(FileDBTable table) {
        if (this.tables == null) {
            this.tables = new FileDBTable[5];
        } else if (this.tables.length <= this.tableNumber) {
            this.tables = (FileDBTable[])ArrayUtils.setSize(this.tables, this.tableNumber + 5);
        }
        this.tables[this.tableNumber++] = table;
        return table;
    }

    void tableDropped(FileDBTable table) {
        int index = ArrayUtils.indexOf(this.tables, 0, this.tableNumber, table);
        if (index >= 0) {
            --this.tableNumber;
            this.tables[index] = this.tables[this.tableNumber];
            this.tables[this.tableNumber] = null;
            log.info(String.valueOf(this.getName()) + ": dropped table " + table.getName());
        }
    }

    File getDatabaseRoot() {
        return this.databaseRoot;
    }

    public static void main(String[] args) throws IllegalConfigurationException {
        FileDatabase db = new FileDatabase();
        ConfigManager config = new ConfigManager();
        config.setProperty("file.path", "testdb");
        config.setProperty("file.create", "true");
        db.init("test", config, "");
        DBTable table = db.getTable("test");
        if (table != null) {
            db.drop();
            db = new FileDatabase();
            db.init("test", config, "");
            table = db.getTable("test");
        }
        if (table == null) {
            System.out.println("Creating table test");
            table = db.createTable("test");
            DBField field = table.createField("id", 0, 32, 1, null);
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
        }
        DBResult result = table.select();
        while (result.next()) {
            System.out.println("RESULT: id=" + result.getInt("id") + " score=" + result.getInt("score"));
        }
        DBMatcher matcher = new DBMatcher();
        matcher.setInt("id", 17);
        result = table.select(matcher);
        while (result.next()) {
            System.out.println("MATCHED RESULT: id=" + result.getInt("id") + " score=" + result.getInt("score"));
        }
        System.out.println("No Elements:" + ((FileDBTable)table).getObjectCount());
        matcher.clear();
        matcher.setLimit(1);
        matcher.setInt("id", 2);
        System.out.println("REMOVED " + table.remove(matcher) + " objects");
        matcher.clear();
        matcher.setLimit(5);
        System.out.println("No Elements:" + ((FileDBTable)table).getObjectCount());
        result = table.select(matcher);
        while (result.next()) {
            System.out.println("LIMIT RESULT: id=" + result.getInt("id") + " score=" + result.getInt("score") + " lastIndex=" + ((FileDBResult)result).getLastIndex() + " objectCount=" + ((FileDBTable)table).getObjectCount());
        }
        DBObject o = new DBObject();
        o.setInt("id", 12);
        o.setInt("score", 2343);
        table.insert(o);
        table.flush();
    }
}

