/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db.file;

import com.botbox.util.ArrayUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.db.DBField;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBObject;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.db.file.DoubleField;
import se.sics.isl.db.file.FileDBField;
import se.sics.isl.db.file.FileDBResult;
import se.sics.isl.db.file.FileDatabase;
import se.sics.isl.db.file.IntField;
import se.sics.isl.db.file.LongField;
import se.sics.isl.db.file.NameFilter;
import se.sics.isl.db.file.ObjectField;

public class FileDBTable
extends DBTable {
    private static final String BAK_EXT = ".~1~";
    private static final Logger log = Logger.getLogger(FileDBTable.class.getName());
    protected final FileDatabase database;
    private final String fileFields;
    private final String fileObjects;
    private FileDBField[] fields;
    private int fieldNumber = 0;
    private int objectNumber = 0;
    private boolean dirtyFields = false;
    private boolean dirtyObjects = false;
    private boolean isDropped = false;
    private boolean objectsLoaded = false;
    private boolean exists = false;
    private int changeCount = 0;

    protected FileDBTable(FileDatabase database, String name, boolean create) {
        super(name);
        this.database = database;
        File root = database.getDatabaseRoot();
        this.fileFields = new File(root, String.valueOf(name) + ".db").getAbsolutePath();
        this.fileObjects = new File(root, String.valueOf(name) + ".dat").getAbsolutePath();
        if (!create) {
            this.loadFields();
        } else {
            this.objectsLoaded = true;
        }
    }

    boolean exists() {
        return this.exists;
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
        if (DBField.indexOf(this.fields, 0, this.fieldNumber, name) >= 0) {
            throw new IllegalArgumentException("field already exists");
        }
        this.database.validateName(name);
        if (!this.objectsLoaded) {
            this.loadObjects();
        }
        FileDBField field = this.newField(name, type, size, flags, defaultValue);
        if (this.objectNumber > 0) {
            field.ensureCapacity(this.objectNumber);
        }
        if (this.fields == null) {
            this.fields = new FileDBField[5];
        } else if (this.fields.length == this.fieldNumber) {
            this.fields = (FileDBField[])ArrayUtils.setSize(this.fields, this.fieldNumber + 5);
        }
        this.fields[this.fieldNumber++] = field;
        this.dirtyFields = true;
        ++this.changeCount;
        return field;
    }

    private FileDBField newField(String name, int type, int size, int flags, Object defaultValue) {
        switch (type) {
            case 0: {
                return new IntField(this, name, type, size, flags, defaultValue);
            }
            case 1: 
            case 2: {
                return new LongField(this, name, type, size, flags, defaultValue);
            }
            case 3: {
                return new DoubleField(this, name, type, size, flags, defaultValue);
            }
            case 4: 
            case 5: {
                return new ObjectField(this, name, type, size, flags, defaultValue);
            }
        }
        throw new IllegalArgumentException("unknown type " + type);
    }

    @Override
    public void drop() {
        if (this.dropTable()) {
            log.finest(String.valueOf(this.name) + ": table dropped");
            this.database.tableDropped(this);
            File[] files = this.database.getDatabaseRoot().listFiles(new NameFilter(this.name));
            int i = 0;
            int n = files.length;
            while (i < n) {
                files[i].delete();
                ++i;
            }
        }
    }

    protected boolean dropTable() {
        if (this.isDropped) {
            return false;
        }
        this.isDropped = true;
        this.objectNumber = 0;
        this.fieldNumber = 0;
        this.dirtyObjects = false;
        this.dirtyFields = false;
        this.exists = false;
        this.fields = null;
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
        if (!this.objectsLoaded) {
            this.loadObjects();
        }
        return this.objectNumber;
    }

    @Override
    public void insert(DBObject object) throws NumberFormatException {
        if (this.isDropped) {
            throw new IllegalStateException("table " + this.name + " has been dropped");
        }
        this.validate(object);
        if (!this.objectsLoaded) {
            this.loadObjects();
        }
        int i = 0;
        int n = this.fieldNumber;
        while (i < n) {
            FileDBField field = this.fields[i];
            field.prepareSet(this.objectNumber, object.getObject(field.getName()));
            ++i;
        }
        i = 0;
        n = this.fieldNumber;
        while (i < n) {
            this.fields[i].set();
            ++i;
        }
        ++this.objectNumber;
        this.dirtyObjects = true;
        ++this.changeCount;
    }

    @Override
    public int update(DBMatcher matcher, DBObject object) {
        if (this.isDropped) {
            throw new IllegalStateException("table " + this.name + " has been dropped");
        }
        this.validate(object);
        if (!this.objectsLoaded) {
            this.loadObjects();
        }
        FileDBResult result = (FileDBResult)this.select(matcher);
        int objectsChanged = 0;
        while (result.next()) {
            int lastIndex = result.getLastIndex();
            int i = 0;
            int n = this.fieldNumber;
            while (i < n) {
                FileDBField field = this.fields[i];
                Object v = object.getObject(field.getName());
                if (v != null) {
                    field.prepareSet(lastIndex, v);
                }
                ++i;
            }
            i = 0;
            n = this.fieldNumber;
            while (i < n) {
                this.fields[i].set();
                ++i;
            }
            this.dirtyObjects = true;
            ++this.changeCount;
            ++objectsChanged;
            result.setChangeID(this.changeCount);
        }
        return objectsChanged;
    }

    @Override
    public int remove(DBMatcher matcher) {
        if (this.isDropped) {
            throw new IllegalStateException("table " + this.name + " has been dropped");
        }
        if (!this.objectsLoaded) {
            this.loadObjects();
        }
        FileDBResult result = (FileDBResult)this.select(matcher);
        int objectsRemoved = 0;
        while (result.next()) {
            int lastIndex = result.getLastIndex();
            if (lastIndex + 1 < this.objectNumber) {
                int i = 0;
                int n = this.fieldNumber;
                while (i < n) {
                    this.fields[i].remove(lastIndex);
                    ++i;
                }
                result.setLastIndex(lastIndex - 1);
            }
            this.dirtyObjects = true;
            ++this.changeCount;
            --this.objectNumber;
            ++objectsRemoved;
            result.setChangeID(this.changeCount);
        }
        return objectsRemoved;
    }

    @Override
    public DBResult select() {
        if (!this.objectsLoaded) {
            this.loadObjects();
        }
        return new FileDBResult(null, this, null, null);
    }

    @Override
    public DBResult select(DBMatcher matcher) {
        int matchNumber;
        Object[] matchValues;
        Object fieldIndex;
        if (matcher == null) {
            return this.select();
        }
        if (!this.objectsLoaded) {
            this.loadObjects();
        }
        if ((matchNumber = matcher.getFieldCount()) == 0) {
            fieldIndex = null;
            matchValues = null;
        } else {
            fieldIndex = new int[matchNumber];
            matchValues = new Object[matchNumber];
            int i = 0;
            while (i < matchNumber) {
                String fname = matcher.getFieldName(i);
                int n = DBField.indexOf(this.fields, 0, this.fieldNumber, fname);
                fieldIndex[i] = n;
                if (n < 0) {
                    throw new IllegalArgumentException("unknown field '" + fname + '\'');
                }
                matchValues[i] = matcher.getObject(fname);
                ++i;
            }
        }
        return new FileDBResult(matcher, this, (int[])fieldIndex, matchValues);
    }

    @Override
    public void flush() {
        if (this.dirtyFields) {
            this.saveFields();
            if (this.objectNumber > 0) {
                this.saveObjects();
            }
        }
        if (this.dirtyObjects) {
            this.saveObjects();
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

    int getChangeCount() {
        return this.changeCount;
    }

    FileDBField getField(String name) {
        int index = DBField.indexOf(this.fields, 0, this.fieldNumber, name);
        if (index >= 0) {
            return this.fields[index];
        }
        throw new IllegalArgumentException("unknown field '" + name + '\'');
    }

    int next(int[] fieldIndex, Object[] matchValues, int lastIndex) {
        int startIndex;
        lastIndex = lastIndex < 0 ? 0 : ++lastIndex;
        if (lastIndex >= this.objectNumber) {
            return this.objectNumber;
        }
        if (fieldIndex == null) {
            return lastIndex;
        }
        if (this.fieldNumber == 0) {
            return this.objectNumber;
        }
        int endIndex = lastIndex;
        int matchNumber = fieldIndex.length;
        do {
            if ((endIndex = (startIndex = this.fields[fieldIndex[0]].indexOf(matchValues[0], endIndex, this.objectNumber))) < 0) {
                return this.objectNumber;
            }
            int i = 1;
            while (i < matchNumber) {
                if ((endIndex = this.fields[fieldIndex[i]].indexOf(matchValues[i], endIndex, this.objectNumber)) < 0) {
                    return this.objectNumber;
                }
                ++i;
            }
        } while (endIndex > startIndex);
        return endIndex;
    }

    protected void loadFields() {
        this.loadState(this.fileFields, true);
    }

    protected void saveFields() {
        this.saveState(this.fileFields);
    }

    protected void loadObjects() {
        this.objectsLoaded = true;
        this.loadState(this.fileObjects, true);
    }

    protected void saveObjects() {
        this.saveState(this.fileObjects);
    }

    private void loadState(String name, boolean revert) {
        block3 : {
            try {
                InputStream in = this.getInputStream(name);
                this.loadState(name, in);
                this.exists = true;
            }
            catch (FileNotFoundException in) {
            }
            catch (Exception e) {
                log.log(Level.SEVERE, String.valueOf(this.name) + ": could not load data from " + name, e);
                this.exists = true;
                if (!revert || !this.revertFile(name)) break block3;
                this.loadState(name, false);
            }
        }
    }

    private void loadState(String name, InputStream in) throws ClassNotFoundException, IOException {
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(in);
            if (name == this.fileFields) {
                int number = oin.readInt();
                FileDBField[] fields = new FileDBField[number];
                int i = 0;
                while (i < number) {
                    int type = oin.readInt();
                    int size = oin.readInt();
                    int flags = oin.readInt();
                    String fieldName = (String)oin.readObject();
                    Object defaultValue = oin.readObject();
                    fields[i] = this.newField(fieldName, type, size, flags, defaultValue);
                    ++i;
                }
                this.fields = fields;
                this.fieldNumber = number;
            } else {
                int number = oin.readInt();
                int i = 0;
                while (i < this.fieldNumber) {
                    this.fields[i].loadState(oin, number);
                    ++i;
                }
                this.objectNumber = number;
            }
        }
        finally {
            if (oin != null) {
                oin.close();
            } else {
                in.close();
            }
        }
    }

    private void saveState(String name) {
        OutputStream out = null;
        ObjectOutputStream oout = null;
        try {
            try {
                out = this.getOutputStream(name);
                oout = new ObjectOutputStream(out);
                if (name == this.fileObjects) {
                    this.dirtyObjects = false;
                    oout.writeInt(this.objectNumber);
                    int i = 0;
                    while (i < this.fieldNumber) {
                        this.fields[i].saveState(oout);
                        ++i;
                    }
                } else {
                    this.dirtyFields = false;
                    oout.writeInt(this.fieldNumber);
                    int i = 0;
                    while (i < this.fieldNumber) {
                        FileDBField field = this.fields[i];
                        Object defaultValue = field.getDefaultValue();
                        oout.writeInt(field.getType());
                        oout.writeInt(field.getSize());
                        oout.writeInt(field.getFlags());
                        oout.writeObject(field.getName());
                        oout.writeObject(defaultValue == null ? null : defaultValue.toString());
                        ++i;
                    }
                    this.exists = true;
                }
            }
            catch (Exception e) {
                log.log(Level.SEVERE, String.valueOf(this.name) + ": could not save data to " + name, e);
                if (name == this.fileObjects) {
                    this.dirtyObjects = true;
                } else {
                    this.dirtyFields = true;
                }
                try {
                    if (oout != null) {
                        oout.close();
                    } else if (out != null) {
                        out.close();
                    }
                }
                catch (IOException var8_9) {}
            }
        }
        finally {
            try {
                if (oout != null) {
                    oout.close();
                } else if (out != null) {
                    out.close();
                }
            }
            catch (IOException var8_11) {}
        }
    }

    private InputStream getInputStream(String filename) throws IOException {
        try {
            return new FileInputStream(filename);
        }
        catch (FileNotFoundException e) {
            String bakName = String.valueOf(filename) + ".~1~";
            if (new File(bakName).renameTo(new File(filename))) {
                return new FileInputStream(filename);
            }
            throw e;
        }
    }

    private OutputStream getOutputStream(String filename) throws IOException {
        String bakName = String.valueOf(filename) + ".~1~";
        File fp = new File(filename);
        if (fp.exists()) {
            File bakFp = new File(bakName);
            bakFp.delete();
            fp.renameTo(bakFp);
        }
        return new FileOutputStream(filename);
    }

    private boolean revertFile(String name) {
        File bakFp = new File(String.valueOf(name) + ".~1~");
        File fp = new File(name);
        if (fp.exists() && !fp.delete()) {
            log.severe(String.valueOf(this.name) + ": could not remove old file " + fp + " when reverting (will retry)" + bakFp);
            Runtime.getRuntime().gc();
            try {
                Thread.sleep(2000);
            }
            catch (Exception var4_4) {
                // empty catch block
            }
            Runtime.getRuntime().gc();
            if (!fp.delete()) {
                log.severe(String.valueOf(this.name) + ": could not remove old file " + fp + " when reverting " + bakFp);
                int nr = 1;
                File rFp = new File(String.valueOf(name) + ".~" + ++nr + '~');
                while (rFp.exists() && !rFp.delete() && ++nr < 10) {
                    log.severe(String.valueOf(this.name) + ": could not remove old removed file " + rFp + " when reverting attribute");
                    rFp = new File(String.valueOf(name) + ".~" + nr + '~');
                }
                if (!fp.renameTo(rFp)) {
                    log.severe(String.valueOf(this.name) + ": could not rename old file " + fp + " to " + rFp + " when reverting");
                }
            }
        }
        return bakFp.renameTo(fp);
    }
}

