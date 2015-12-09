/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.IntegerType;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.NativeString;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.StructureReadContext;
import com.sun.jna.StructureWriteContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.Union;
import com.sun.jna.WString;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class Structure {
    private static final boolean REVERSE_FIELDS;
    static boolean REQUIRES_FIELD_ORDER;
    static final boolean isPPC;
    static final boolean isSPARC;
    public static final int ALIGN_DEFAULT = 0;
    public static final int ALIGN_NONE = 1;
    public static final int ALIGN_GNUC = 2;
    public static final int ALIGN_MSVC = 3;
    private static final int MAX_GNUC_ALIGNMENT;
    protected static final int CALCULATE_SIZE = -1;
    private Pointer memory;
    private int size = -1;
    private int alignType;
    private int structAlignment;
    private final Map structFields = new LinkedHashMap();
    private final Map nativeStrings = new HashMap();
    private TypeMapper typeMapper;
    private long typeInfo;
    private List fieldOrder;
    private boolean autoRead = true;
    private boolean autoWrite = true;
    private Structure[] array;
    private static final ThreadLocal busy;
    static /* synthetic */ Class class$java$lang$Void;

    protected Structure() {
        this((Pointer)null);
    }

    protected Structure(TypeMapper mapper) {
        this(null, 0, mapper);
    }

    protected Structure(Pointer p) {
        this(p, 0);
    }

    protected Structure(Pointer p, int alignment) {
        this(p, alignment, null);
    }

    protected Structure(Pointer p, int alignment, TypeMapper mapper) {
        this.setAlignType(alignment);
        this.setTypeMapper(mapper);
        if (p != null) {
            this.useMemory(p);
        } else {
            this.allocateMemory(-1);
        }
    }

    Map fields() {
        return this.structFields;
    }

    protected void setTypeMapper(TypeMapper mapper) {
        Class declaring;
        if (mapper == null && (declaring = this.getClass().getDeclaringClass()) != null) {
            mapper = Native.getTypeMapper(declaring);
        }
        this.typeMapper = mapper;
        this.size = -1;
        if (this.memory instanceof AutoAllocated) {
            this.memory = null;
        }
    }

    protected void setAlignType(int alignType) {
        if (alignType == 0) {
            Class declaring = this.getClass().getDeclaringClass();
            if (declaring != null) {
                alignType = Native.getStructureAlignment(declaring);
            }
            if (alignType == 0) {
                alignType = Platform.isWindows() ? 3 : 2;
            }
        }
        this.alignType = alignType;
        this.size = -1;
        if (this.memory instanceof AutoAllocated) {
            this.memory = null;
        }
    }

    protected void useMemory(Pointer m) {
        this.useMemory(m, 0);
    }

    protected void useMemory(Pointer m, int offset) {
        try {
            this.memory = m.share(offset, this.size());
            this.array = null;
        }
        catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Structure exceeds provided memory bounds");
        }
    }

    protected void ensureAllocated() {
        if (this.size == -1) {
            this.allocateMemory();
        }
    }

    protected void allocateMemory() {
        this.allocateMemory(this.calculateSize(true));
    }

    protected void allocateMemory(int size) {
        if (size == -1) {
            size = this.calculateSize(false);
        } else if (size <= 0) {
            throw new IllegalArgumentException("Structure size must be greater than zero: " + size);
        }
        if (size != -1) {
            if (this.memory == null || this.memory instanceof AutoAllocated) {
                this.memory = new AutoAllocated(size);
                this.memory.clear(size);
            }
            this.size = size;
        }
    }

    public int size() {
        this.ensureAllocated();
        return this.size;
    }

    public void clear() {
        this.memory.clear(this.size());
    }

    public Pointer getPointer() {
        this.ensureAllocated();
        return this.memory;
    }

    Set busy() {
        return (Set)busy.get();
    }

    public void read() {
        this.ensureAllocated();
        if (this.busy().contains(this)) {
            return;
        }
        this.busy().add(this);
        try {
            Iterator i = this.structFields.values().iterator();
            while (i.hasNext()) {
                this.readField((StructField)i.next());
            }
        }
        finally {
            this.busy().remove(this);
        }
    }

    public Object readField(String name) {
        this.ensureAllocated();
        StructField f = (StructField)this.structFields.get(name);
        if (f == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        return this.readField(f);
    }

    Object getField(StructField structField) {
        try {
            return structField.field.get(this);
        }
        catch (Exception e) {
            throw new Error("Exception reading field '" + structField.name + "' in " + this.getClass() + ": " + e);
        }
    }

    void setField(StructField structField, Object value) {
        try {
            structField.field.set(this, value);
        }
        catch (IllegalAccessException e) {
            throw new Error("Unexpectedly unable to write to field '" + structField.name + "' within " + this.getClass() + ": " + e);
        }
    }

    static Structure updateStructureByReference(Class type, Structure s, Pointer address) {
        if (address == null) {
            s = null;
        } else {
            if (s == null || !address.equals(s.getPointer())) {
                s = Structure.newInstance(type);
                s.useMemory(address);
            }
            s.autoRead();
        }
        return s;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    Object readField(StructField structField) {
        offset = structField.offset;
        fieldType = structField.type;
        readConverter = structField.readConverter;
        if (readConverter != null) {
            fieldType = readConverter.nativeType();
        }
        v0 = Structure.class;
        if (v0.isAssignableFrom(fieldType)) ** GOTO lbl-1000
        v1 = Callback.class;
        if (v1.isAssignableFrom(fieldType)) ** GOTO lbl-1000
        v2 = Buffer.class;
        if (v2.isAssignableFrom(fieldType)) ** GOTO lbl-1000
        v3 = Pointer.class;
        if (v3.isAssignableFrom(fieldType) || fieldType.isArray()) lbl-1000: // 4 sources:
        {
            v4 = this.getField(structField);
        } else {
            v4 = null;
        }
        currentValue = v4;
        result = this.memory.getValue(offset, fieldType, currentValue);
        if (readConverter != null) {
            result = readConverter.fromNative(result, structField.context);
        }
        this.setField(structField, result);
        return result;
    }

    public void write() {
        this.ensureAllocated();
        if (this instanceof ByValue) {
            this.getTypeInfo();
        }
        if (this.busy().contains(this)) {
            return;
        }
        this.busy().add(this);
        try {
            Iterator i = this.structFields.values().iterator();
            while (i.hasNext()) {
                StructField sf = (StructField)i.next();
                if (sf.isVolatile) continue;
                this.writeField(sf);
            }
        }
        finally {
            this.busy().remove(this);
        }
    }

    public void writeField(String name) {
        this.ensureAllocated();
        StructField f = (StructField)this.structFields.get(name);
        if (f == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        this.writeField(f);
    }

    public void writeField(String name, Object value) {
        this.ensureAllocated();
        StructField f = (StructField)this.structFields.get(name);
        if (f == null) {
            throw new IllegalArgumentException("No such field: " + name);
        }
        this.setField(f, value);
        this.writeField(f);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    void writeField(StructField structField) {
        offset = structField.offset;
        value = this.getField(structField);
        fieldType = structField.type;
        converter = structField.writeConverter;
        if (converter != null) {
            value = converter.toNative(value, new StructureWriteContext(this, structField.field));
            fieldType = converter.nativeType();
        }
        v0 = String.class;
        if (v0 == fieldType) ** GOTO lbl-1000
        v1 = WString.class;
        if (v1 == fieldType) lbl-1000: // 2 sources:
        {
            v2 = wide = fieldType == WString.class;
            if (value != null) {
                nativeString = new NativeString(value.toString(), wide);
                this.nativeStrings.put(structField.name, nativeString);
                value = nativeString.getPointer();
            } else {
                value = null;
                this.nativeStrings.remove(structField.name);
            }
        }
        try {
            this.memory.setValue(offset, value, fieldType);
            return;
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            msg = "Structure field \"" + structField.name + "\" was declared as " + structField.type + (structField.type == fieldType ? "" : new StringBuffer().append(" (native type ").append(fieldType).append(")").toString()) + ", which is not supported within a Structure";
            throw new IllegalArgumentException(msg);
        }
    }

    protected List getFieldOrder() {
        Structure structure = this;
        synchronized (structure) {
            if (this.fieldOrder == null) {
                this.fieldOrder = new ArrayList();
            }
            return this.fieldOrder;
        }
    }

    protected void setFieldOrder(String[] fields) {
        this.getFieldOrder().addAll(Arrays.asList(fields));
    }

    protected void sortFields(Field[] fields, String[] names) {
        block0 : for (int i = 0; i < names.length; ++i) {
            for (int f = i; f < fields.length; ++f) {
                if (!names[i].equals(fields[f].getName())) continue;
                Field tmp = fields[f];
                fields[f] = fields[i];
                fields[i] = tmp;
                continue block0;
            }
        }
    }

    int calculateSize(boolean force) {
        int i;
        this.structAlignment = 1;
        int calculatedSize = 0;
        Field[] fields = this.getClass().getFields();
        ArrayList<Field> flist = new ArrayList<Field>();
        for (i = 0; i < fields.length; ++i) {
            int modifiers = fields[i].getModifiers();
            if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) continue;
            flist.add(fields[i]);
        }
        fields = flist.toArray(new Field[flist.size()]);
        if (REVERSE_FIELDS) {
            for (i = 0; i < fields.length / 2; ++i) {
                int idx = fields.length - 1 - i;
                Field tmp = fields[i];
                fields[i] = fields[idx];
                fields[idx] = tmp;
            }
        } else if (REQUIRES_FIELD_ORDER) {
            List fieldOrder = this.getFieldOrder();
            if (fieldOrder.size() < fields.length) {
                if (force) {
                    throw new Error("This VM does not store fields in a predictable order; you must use setFieldOrder: " + System.getProperty("java.vendor") + ", " + System.getProperty("java.version"));
                }
                return -1;
            }
            this.sortFields(fields, fieldOrder.toArray(new String[fieldOrder.size()]));
        }
        for (i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            int modifiers = field.getModifiers();
            Class type = field.getType();
            StructField structField = new StructField();
            structField.isVolatile = Modifier.isVolatile(modifiers);
            structField.field = field;
            if (Modifier.isFinal(modifiers)) {
                field.setAccessible(true);
            }
            structField.name = field.getName();
            structField.type = type;
            if ((class$com$sun$jna$Callback == null ? Structure.class$("com.sun.jna.Callback") : class$com$sun$jna$Callback).isAssignableFrom(type) && !type.isInterface()) {
                throw new IllegalArgumentException("Structure Callback field '" + field.getName() + "' must be an interface");
            }
            if (type.isArray() && (class$com$sun$jna$Structure == null ? Structure.class$("com.sun.jna.Structure") : class$com$sun$jna$Structure).equals(type.getComponentType())) {
                String msg = "Nested Structure arrays must use a derived Structure type so that the size of the elements can be determined";
                throw new IllegalArgumentException(msg);
            }
            int fieldAlignment = 1;
            if (!Modifier.isPublic(field.getModifiers())) continue;
            Object value = this.getField(structField);
            if (value == null) {
                if ((class$com$sun$jna$Structure == null ? Structure.class$("com.sun.jna.Structure") : class$com$sun$jna$Structure).isAssignableFrom(type) && !(class$com$sun$jna$Structure$ByReference == null ? Structure.class$("com.sun.jna.Structure$ByReference") : class$com$sun$jna$Structure$ByReference).isAssignableFrom(type)) {
                    try {
                        value = Structure.newInstance(type);
                        this.setField(structField, value);
                    }
                    catch (IllegalArgumentException e) {
                        String msg = "Can't determine size of nested structure: " + e.getMessage();
                        throw new IllegalArgumentException(msg);
                    }
                } else if (type.isArray()) {
                    if (force) {
                        throw new IllegalStateException("Array fields must be initialized");
                    }
                    return -1;
                }
            }
            Class nativeType = type;
            if ((class$com$sun$jna$NativeMapped == null ? Structure.class$("com.sun.jna.NativeMapped") : class$com$sun$jna$NativeMapped).isAssignableFrom(type)) {
                NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
                if (value == null) {
                    value = tc.defaultValue();
                    this.setField(structField, value);
                }
                nativeType = tc.nativeType();
                structField.writeConverter = tc;
                structField.readConverter = tc;
                structField.context = new StructureReadContext(this, field);
            } else if (this.typeMapper != null) {
                ToNativeConverter writeConverter = this.typeMapper.getToNativeConverter(type);
                FromNativeConverter readConverter = this.typeMapper.getFromNativeConverter(type);
                if (writeConverter != null && readConverter != null) {
                    nativeType = (value = writeConverter.toNative(value, new StructureWriteContext(this, structField.field))) != null ? value.getClass() : (class$com$sun$jna$Pointer == null ? Structure.class$("com.sun.jna.Pointer") : class$com$sun$jna$Pointer);
                    structField.writeConverter = writeConverter;
                    structField.readConverter = readConverter;
                    structField.context = new StructureReadContext(this, field);
                } else if (writeConverter != null || readConverter != null) {
                    String msg = "Structures require bidirectional type conversion for " + type;
                    throw new IllegalArgumentException(msg);
                }
            }
            try {
                structField.size = Native.getNativeSize(nativeType, value);
                fieldAlignment = this.getNativeAlignment(nativeType, value, i == 0);
            }
            catch (IllegalArgumentException e) {
                if (!force && this.typeMapper == null) {
                    return -1;
                }
                String msg = "Invalid Structure field in " + this.getClass() + ", field name '" + structField.name + "', " + structField.type + ": " + e.getMessage();
                throw new IllegalArgumentException(msg);
            }
            this.structAlignment = Math.max(this.structAlignment, fieldAlignment);
            if (calculatedSize % fieldAlignment != 0) {
                calculatedSize += fieldAlignment - calculatedSize % fieldAlignment;
            }
            structField.offset = calculatedSize;
            calculatedSize += structField.size;
            this.structFields.put(structField.name, structField);
        }
        if (calculatedSize > 0) {
            int size = this.calculateAlignedSize(calculatedSize);
            if (this instanceof ByValue) {
                this.getTypeInfo();
            }
            return size;
        }
        throw new IllegalArgumentException("Structure " + this.getClass() + " has unknown size (ensure " + "all fields are public)");
    }

    int calculateAlignedSize(int calculatedSize) {
        if (this.alignType != 1 && calculatedSize % this.structAlignment != 0) {
            calculatedSize += this.structAlignment - calculatedSize % this.structAlignment;
        }
        return calculatedSize;
    }

    protected int getStructAlignment() {
        if (this.size == -1) {
            this.calculateSize(true);
        }
        return this.structAlignment;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    protected int getNativeAlignment(Class type, Object value, boolean isFirstElement) {
        alignment = 1;
        v0 = NativeMapped.class;
        if (v0.isAssignableFrom(type)) {
            tc = NativeMappedConverter.getInstance(type);
            type = tc.nativeType();
            value = tc.toNative(value, new ToNativeContext());
        }
        size = Native.getNativeSize(type, value);
        if (type.isPrimitive()) ** GOTO lbl25
        v1 = Long.class;
        if (v1 == type) ** GOTO lbl25
        v2 = Integer.class;
        if (v2 == type) ** GOTO lbl25
        v3 = Short.class;
        if (v3 == type) ** GOTO lbl25
        v4 = Character.class;
        if (v4 == type) ** GOTO lbl25
        v5 = Byte.class;
        if (v5 == type) ** GOTO lbl25
        v6 = Boolean.class;
        if (v6 == type) ** GOTO lbl25
        v7 = Float.class;
        if (v7 == type) ** GOTO lbl25
        v8 = Double.class;
        if (v8 != type) ** GOTO lbl27
lbl25: // 9 sources:
        alignment = size;
        ** GOTO lbl51
lbl27: // 1 sources:
        v9 = Pointer.class;
        if (v9 == type) ** GOTO lbl-1000
        v10 = Buffer.class;
        if (v10.isAssignableFrom(type)) ** GOTO lbl-1000
        v11 = Callback.class;
        if (v11.isAssignableFrom(type)) ** GOTO lbl-1000
        v12 = WString.class;
        if (v12 == type) ** GOTO lbl-1000
        v13 = String.class;
        if (v13 == type) lbl-1000: // 5 sources:
        {
            alignment = Pointer.SIZE;
        } else {
            v14 = Structure.class;
            if (v14.isAssignableFrom(type)) {
                v15 = ByReference.class;
                if (v15.isAssignableFrom(type)) {
                    alignment = Pointer.SIZE;
                } else {
                    if (value == null) {
                        value = Structure.newInstance(type);
                    }
                    alignment = ((Structure)value).getStructAlignment();
                }
            } else {
                if (type.isArray() == false) throw new IllegalArgumentException("Type " + type + " has unknown " + "native alignment");
                alignment = this.getNativeAlignment(type.getComponentType(), null, isFirstElement);
            }
        }
lbl51: // 5 sources:
        if (this.alignType == 1) {
            return 1;
        }
        if (this.alignType == 3) {
            return Math.min(8, alignment);
        }
        if (this.alignType != 2) return alignment;
        if (isFirstElement == false) return Math.min(Structure.MAX_GNUC_ALIGNMENT, alignment);
        if (Platform.isMac() == false) return Math.min(Structure.MAX_GNUC_ALIGNMENT, alignment);
        if (Structure.isPPC != false) return alignment;
        return Math.min(Structure.MAX_GNUC_ALIGNMENT, alignment);
    }

    public String toString() {
        return this.toString(0, true);
    }

    private String format(Class type) {
        String s = type.getName();
        int dot = s.lastIndexOf(".");
        return s.substring(dot + 1);
    }

    private String toString(int indent, boolean showContents) {
        String LS = System.getProperty("line.separator");
        String name = this.format(this.getClass()) + "(" + this.getPointer() + ")";
        if (!(this.getPointer() instanceof Memory)) {
            name = name + " (" + this.size() + " bytes)";
        }
        String prefix = "";
        for (int idx = 0; idx < indent; ++idx) {
            prefix = prefix + "  ";
        }
        String contents = LS;
        if (!showContents) {
            contents = "...}";
        } else {
            Iterator i = this.structFields.values().iterator();
            while (i.hasNext()) {
                StructField sf = (StructField)i.next();
                Object value = this.getField(sf);
                String type = this.format(sf.type);
                String index = "";
                contents = contents + prefix;
                if (sf.type.isArray() && value != null) {
                    type = this.format(sf.type.getComponentType());
                    index = "[" + Array.getLength(value) + "]";
                }
                contents = contents + "  " + type + " " + sf.name + index + "@" + Integer.toHexString(sf.offset);
                if (value instanceof Structure) {
                    value = ((Structure)value).toString(indent + 1, !(value instanceof ByReference));
                }
                contents = contents + "=";
                contents = value instanceof Long ? contents + Long.toHexString((Long)value) : (value instanceof Integer ? contents + Integer.toHexString((Integer)value) : (value instanceof Short ? contents + Integer.toHexString(((Short)value).shortValue()) : (value instanceof Byte ? contents + Integer.toHexString(((Byte)value).byteValue()) : contents + String.valueOf(value).trim())));
                contents = contents + LS;
                if (i.hasNext()) continue;
                contents = contents + prefix + "}";
            }
        }
        if (indent == 0 && Boolean.getBoolean("jna.dump_memory")) {
            byte[] buf = this.getPointer().getByteArray(0, this.size());
            int BYTES_PER_ROW = 4;
            contents = contents + LS + "memory dump" + LS;
            for (int i = 0; i < buf.length; ++i) {
                if (i % 4 == 0) {
                    contents = contents + "[";
                }
                if (buf[i] >= 0 && buf[i] < 16) {
                    contents = contents + "0";
                }
                contents = contents + Integer.toHexString(buf[i] & 255);
                if (i % 4 != 3 || i >= buf.length - 1) continue;
                contents = contents + "]" + LS;
            }
            contents = contents + "]";
        }
        return name + " {" + contents;
    }

    public Structure[] toArray(Structure[] array) {
        this.ensureAllocated();
        if (this.memory instanceof AutoAllocated) {
            Memory m = (Memory)this.memory;
            int requiredSize = array.length * this.size();
            if (m.getSize() < (long)requiredSize) {
                m = new AutoAllocated(requiredSize);
                m.clear();
                this.useMemory(m);
            }
        }
        array[0] = this;
        int size = this.size();
        for (int i = 1; i < array.length; ++i) {
            array[i] = Structure.newInstance(this.getClass());
            array[i].useMemory(this.memory.share(i * size, size));
            array[i].read();
        }
        if (!(this instanceof ByValue)) {
            this.array = array;
        }
        return array;
    }

    public Structure[] toArray(int size) {
        return this.toArray((Structure[])Array.newInstance(this.getClass(), size));
    }

    private Class baseClass() {
        if (this instanceof ByReference || this instanceof ByValue) {
            Class class_ = Structure.class;
            if (class_.isAssignableFrom(this.getClass().getSuperclass())) {
                return this.getClass().getSuperclass();
            }
        }
        return this.getClass();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass() && ((Structure)o).baseClass() != this.baseClass()) {
            return false;
        }
        Structure s = (Structure)o;
        if (s.size() == this.size()) {
            this.clear();
            this.write();
            byte[] buf = this.getPointer().getByteArray(0, this.size());
            s.clear();
            s.write();
            byte[] sbuf = s.getPointer().getByteArray(0, s.size());
            return Arrays.equals(buf, sbuf);
        }
        return false;
    }

    public int hashCode() {
        this.clear();
        this.write();
        return Arrays.hashCode(this.getPointer().getByteArray(0, this.size()));
    }

    protected void cacheTypeInfo(Pointer p) {
        this.typeInfo = p.peer;
    }

    Pointer getTypeInfo() {
        Pointer p = Structure.getTypeInfo(this);
        this.cacheTypeInfo(p);
        return p;
    }

    public void setAutoSynch(boolean auto) {
        this.setAutoRead(auto);
        this.setAutoWrite(auto);
    }

    public void setAutoRead(boolean auto) {
        this.autoRead = auto;
    }

    public boolean getAutoRead() {
        return this.autoRead;
    }

    public void setAutoWrite(boolean auto) {
        this.autoWrite = auto;
    }

    public boolean getAutoWrite() {
        return this.autoWrite;
    }

    static Pointer getTypeInfo(Object obj) {
        return FFIType.get(obj);
    }

    public static Structure newInstance(Class type) throws IllegalArgumentException {
        try {
            Structure s = (Structure)type.newInstance();
            if (s instanceof ByValue) {
                s.allocateMemory();
            }
            return s;
        }
        catch (InstantiationException e) {
            String msg = "Can't instantiate " + type + " (" + e + ")";
            throw new IllegalArgumentException(msg);
        }
        catch (IllegalAccessException e) {
            String msg = "Instantiation of " + type + " not allowed, is it public? (" + e + ")";
            throw new IllegalArgumentException(msg);
        }
    }

    private static void structureArrayCheck(Structure[] ss) {
        Pointer base = ss[0].getPointer();
        int size = ss[0].size();
        for (int si = 1; si < ss.length; ++si) {
            if (ss[si].getPointer().peer == base.peer + (long)(size * si)) continue;
            String msg = "Structure array elements must use contiguous memory (bad backing address at Structure array index " + si + ")";
            throw new IllegalArgumentException(msg);
        }
    }

    public static void autoRead(Structure[] ss) {
        Structure.structureArrayCheck(ss);
        if (ss[0].array == ss) {
            ss[0].autoRead();
        } else {
            for (int si = 0; si < ss.length; ++si) {
                ss[si].autoRead();
            }
        }
    }

    public void autoRead() {
        if (this.getAutoRead()) {
            this.read();
            if (this.array != null) {
                for (int i = 1; i < this.array.length; ++i) {
                    this.array[i].autoRead();
                }
            }
        }
    }

    public static void autoWrite(Structure[] ss) {
        Structure.structureArrayCheck(ss);
        if (ss[0].array == ss) {
            ss[0].autoWrite();
        } else {
            for (int si = 0; si < ss.length; ++si) {
                ss[si].autoWrite();
            }
        }
    }

    public void autoWrite() {
        if (this.getAutoWrite()) {
            this.write();
            if (this.array != null) {
                for (int i = 1; i < this.array.length; ++i) {
                    this.array[i].autoWrite();
                }
            }
        }
    }

    static {
        Class class_ = MemberOrder.class;
        Field[] fields = class_.getFields();
        REVERSE_FIELDS = "last".equals(fields[0].getName());
        REQUIRES_FIELD_ORDER = !"middle".equals(fields[1].getName());
        String arch = System.getProperty("os.arch").toLowerCase();
        isPPC = "ppc".equals(arch) || "powerpc".equals(arch);
        isSPARC = "sparc".equals(arch);
        MAX_GNUC_ALIGNMENT = isSPARC ? 8 : Native.LONG_SIZE;
        busy = new ThreadLocal(){

            protected synchronized Object initialValue() {
                return new StructureSet();
            }

            class StructureSet
            extends AbstractCollection
            implements Set {
                private Structure[] elements;
                private int count;

                StructureSet() {
                }

                private void ensureCapacity(int size) {
                    if (this.elements == null) {
                        this.elements = new Structure[size * 3 / 2];
                    } else if (this.elements.length < size) {
                        Structure[] e = new Structure[size * 3 / 2];
                        System.arraycopy(this.elements, 0, e, 0, this.elements.length);
                        this.elements = e;
                    }
                }

                public int size() {
                    return this.count;
                }

                public boolean contains(Object o) {
                    return this.indexOf(o) != -1;
                }

                public boolean add(Object o) {
                    if (!this.contains(o)) {
                        this.ensureCapacity(this.count + 1);
                        this.elements[this.count++] = (Structure)o;
                    }
                    return true;
                }

                private int indexOf(Object o) {
                    Structure s1 = (Structure)o;
                    for (int i = 0; i < this.count; ++i) {
                        Structure s2 = this.elements[i];
                        if (s1 != s2 && (s1.baseClass() != s2.baseClass() || s1.size() != s2.size() || !s1.getPointer().equals(s2.getPointer()))) continue;
                        return i;
                    }
                    return -1;
                }

                public boolean remove(Object o) {
                    int idx = this.indexOf(o);
                    if (idx != -1) {
                        if (--this.count > 0) {
                            this.elements[idx] = this.elements[this.count];
                            this.elements[this.count] = null;
                        }
                        return true;
                    }
                    return false;
                }

                public Iterator iterator() {
                    return null;
                }
            }

        };
    }

    private class AutoAllocated
    extends Memory {
        public AutoAllocated(int size) {
            super(size);
        }
    }

    static class FFIType
    extends Structure {
        private static Map typeInfoMap = new WeakHashMap();
        private static final int FFI_TYPE_STRUCT = 13;
        public size_t size;
        public short alignment;
        public short type = 13;
        public Pointer elements;

        private FFIType(Structure ref) {
            Pointer[] els;
            if (ref instanceof Union) {
                StructField sf = ((Union)ref).biggestField;
                els = new Pointer[]{FFIType.get(ref.getField(sf), sf.type), null};
            } else {
                els = new Pointer[ref.fields().size() + 1];
                int idx = 0;
                Iterator i = ref.fields().values().iterator();
                while (i.hasNext()) {
                    StructField sf = (StructField)i.next();
                    els[idx++] = FFIType.get(ref.getField(sf), sf.type);
                }
            }
            this.init(els);
        }

        private FFIType(Object array, Class type) {
            int length = Array.getLength(array);
            Pointer[] els = new Pointer[length + 1];
            Pointer p = FFIType.get(null, type.getComponentType());
            for (int i = 0; i < length; ++i) {
                els[i] = p;
            }
            this.init(els);
        }

        private void init(Pointer[] els) {
            this.elements = new Memory(Pointer.SIZE * els.length);
            this.elements.write(0, els, 0, els.length);
            this.write();
        }

        static Pointer get(Object obj) {
            if (obj == null) {
                return ffi_type_pointer;
            }
            if (obj instanceof Class) {
                return FFIType.get(null, (Class)obj);
            }
            return FFIType.get(obj, obj.getClass());
        }

        /*
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Converted monitor instructions to comments
         * Lifted jumps to return sites
         */
        private static Pointer get(Object obj, Class cls) {
            var2_2 = FFIType.typeInfoMap;
            // MONITORENTER : var2_2
            o = FFIType.typeInfoMap.get(cls);
            if (o instanceof Pointer) {
                // MONITOREXIT : var2_2
                return (Pointer)o;
            }
            if (o instanceof FFIType) {
                // MONITOREXIT : var2_2
                return ((FFIType)o).getPointer();
            }
            v0 = Structure.class$java$nio$Buffer == null ? (Structure.class$java$nio$Buffer = Structure.class$("java.nio.Buffer")) : Structure.class$java$nio$Buffer;
            if (v0.isAssignableFrom(cls)) ** GOTO lbl-1000
            v1 = Structure.class$com$sun$jna$Callback == null ? (Structure.class$com$sun$jna$Callback = Structure.class$("com.sun.jna.Callback")) : Structure.class$com$sun$jna$Callback;
            if (v1.isAssignableFrom(cls)) lbl-1000: // 2 sources:
            {
                FFIType.typeInfoMap.put(cls, FFITypes.access$1000());
                // MONITOREXIT : var2_2
                return FFITypes.access$1000();
            }
            v2 = Structure.class$com$sun$jna$Structure == null ? (Structure.class$com$sun$jna$Structure = Structure.class$("com.sun.jna.Structure")) : Structure.class$com$sun$jna$Structure;
            if (v2.isAssignableFrom(cls)) {
                if (obj == null) {
                    obj = FFIType.newInstance(cls);
                }
                v3 = Structure.class$com$sun$jna$Structure$ByReference == null ? (Structure.class$com$sun$jna$Structure$ByReference = Structure.class$("com.sun.jna.Structure$ByReference")) : Structure.class$com$sun$jna$Structure$ByReference;
                if (v3.isAssignableFrom(cls)) {
                    FFIType.typeInfoMap.put(cls, FFITypes.access$1000());
                    // MONITOREXIT : var2_2
                    return FFITypes.access$1000();
                }
                type = new FFIType((Structure)obj);
                FFIType.typeInfoMap.put(cls, type);
                // MONITOREXIT : var2_2
                return type.getPointer();
            }
            v4 = Structure.class$com$sun$jna$NativeMapped == null ? (Structure.class$com$sun$jna$NativeMapped = Structure.class$("com.sun.jna.NativeMapped")) : Structure.class$com$sun$jna$NativeMapped;
            if (v4.isAssignableFrom(cls)) {
                c = NativeMappedConverter.getInstance(cls);
                // MONITOREXIT : var2_2
                return FFIType.get(c.toNative(obj, new ToNativeContext()), c.nativeType());
            }
            if (cls.isArray() == false) throw new IllegalArgumentException("Unsupported type " + cls);
            type = new FFIType(obj, cls);
            FFIType.typeInfoMap.put(obj, type);
            // MONITOREXIT : var2_2
            return type.getPointer();
        }

        static {
            if (Native.POINTER_SIZE == 0) {
                throw new Error("Native library not initialized");
            }
            if (ffi_type_void == null) {
                throw new Error("FFI types not initialized");
            }
            typeInfoMap.put(Void.TYPE, ffi_type_void);
            Class class_ = Structure.class$java$lang$Void == null ? (Structure.class$java$lang$Void = Structure.class$("java.lang.Void")) : Structure.class$java$lang$Void;
            typeInfoMap.put(class_, ffi_type_void);
            typeInfoMap.put(Float.TYPE, ffi_type_float);
            Class class_2 = Structure.class$java$lang$Float == null ? (Structure.class$java$lang$Float = Structure.class$("java.lang.Float")) : Structure.class$java$lang$Float;
            typeInfoMap.put(class_2, ffi_type_float);
            typeInfoMap.put(Double.TYPE, ffi_type_double);
            Class class_3 = Structure.class$java$lang$Double == null ? (Structure.class$java$lang$Double = Structure.class$("java.lang.Double")) : Structure.class$java$lang$Double;
            typeInfoMap.put(class_3, ffi_type_double);
            typeInfoMap.put(Long.TYPE, ffi_type_sint64);
            Class class_4 = Structure.class$java$lang$Long == null ? (Structure.class$java$lang$Long = Structure.class$("java.lang.Long")) : Structure.class$java$lang$Long;
            typeInfoMap.put(class_4, ffi_type_sint64);
            typeInfoMap.put(Integer.TYPE, ffi_type_sint32);
            Class class_5 = Structure.class$java$lang$Integer == null ? (Structure.class$java$lang$Integer = Structure.class$("java.lang.Integer")) : Structure.class$java$lang$Integer;
            typeInfoMap.put(class_5, ffi_type_sint32);
            typeInfoMap.put(Short.TYPE, ffi_type_sint16);
            Class class_6 = Structure.class$java$lang$Short == null ? (Structure.class$java$lang$Short = Structure.class$("java.lang.Short")) : Structure.class$java$lang$Short;
            typeInfoMap.put(class_6, ffi_type_sint16);
            Pointer ctype = Native.WCHAR_SIZE == 2 ? ffi_type_uint16 : ffi_type_uint32;
            typeInfoMap.put(Character.TYPE, ctype);
            Class class_7 = Structure.class$java$lang$Character == null ? (Structure.class$java$lang$Character = Structure.class$("java.lang.Character")) : Structure.class$java$lang$Character;
            typeInfoMap.put(class_7, ctype);
            typeInfoMap.put(Byte.TYPE, ffi_type_sint8);
            Class class_8 = Structure.class$java$lang$Byte == null ? (Structure.class$java$lang$Byte = Structure.class$("java.lang.Byte")) : Structure.class$java$lang$Byte;
            typeInfoMap.put(class_8, ffi_type_sint8);
            typeInfoMap.put(Boolean.TYPE, ffi_type_uint32);
            Class class_9 = Structure.class$java$lang$Boolean == null ? (Structure.class$java$lang$Boolean = Structure.class$("java.lang.Boolean")) : Structure.class$java$lang$Boolean;
            typeInfoMap.put(class_9, ffi_type_uint32);
            Class class_10 = Structure.class$com$sun$jna$Pointer == null ? (Structure.class$com$sun$jna$Pointer = Structure.class$("com.sun.jna.Pointer")) : Structure.class$com$sun$jna$Pointer;
            typeInfoMap.put(class_10, ffi_type_pointer);
            Class class_11 = Structure.class$java$lang$String == null ? (Structure.class$java$lang$String = Structure.class$("java.lang.String")) : Structure.class$java$lang$String;
            typeInfoMap.put(class_11, ffi_type_pointer);
            Class class_12 = Structure.class$com$sun$jna$WString == null ? (Structure.class$com$sun$jna$WString = Structure.class$("com.sun.jna.WString")) : Structure.class$com$sun$jna$WString;
            typeInfoMap.put(class_12, ffi_type_pointer);
        }

        private static class FFITypes {
            private static Pointer ffi_type_void;
            private static Pointer ffi_type_float;
            private static Pointer ffi_type_double;
            private static Pointer ffi_type_longdouble;
            private static Pointer ffi_type_uint8;
            private static Pointer ffi_type_sint8;
            private static Pointer ffi_type_uint16;
            private static Pointer ffi_type_sint16;
            private static Pointer ffi_type_uint32;
            private static Pointer ffi_type_sint32;
            private static Pointer ffi_type_uint64;
            private static Pointer ffi_type_sint64;
            private static Pointer ffi_type_pointer;

            private FFITypes() {
            }
        }

        public static class size_t
        extends IntegerType {
            public size_t() {
                this(0);
            }

            public size_t(long value) {
                super(Native.POINTER_SIZE, value);
            }
        }

    }

    class StructField {
        public String name;
        public Class type;
        public Field field;
        public int size;
        public int offset;
        public boolean isVolatile;
        public FromNativeConverter readConverter;
        public ToNativeConverter writeConverter;
        public FromNativeContext context;

        StructField() {
            this.size = -1;
            this.offset = -1;
        }
    }

    private static class MemberOrder {
        public int first;
        public int middle;
        public int last;

        private MemberOrder() {
        }
    }

    public static interface ByReference {
    }

    public static interface ByValue {
    }

}

