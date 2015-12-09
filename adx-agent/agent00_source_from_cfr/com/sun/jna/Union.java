/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class Union
extends Structure {
    private Structure.StructField activeField;
    Structure.StructField biggestField;

    protected Union() {
    }

    protected Union(Pointer p) {
        super(p);
    }

    protected Union(Pointer p, int alignType) {
        super(p, alignType);
    }

    protected Union(TypeMapper mapper) {
        super(mapper);
    }

    protected Union(Pointer p, int alignType, TypeMapper mapper) {
        super(p, alignType, mapper);
    }

    public void setType(Class type) {
        this.ensureAllocated();
        Iterator i = this.fields().values().iterator();
        while (i.hasNext()) {
            Structure.StructField f = (Structure.StructField)i.next();
            if (f.type != type) continue;
            this.activeField = f;
            return;
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }

    public Object readField(String name) {
        this.ensureAllocated();
        Structure.StructField f = (Structure.StructField)this.fields().get(name);
        if (f != null) {
            this.setType(f.type);
        }
        return super.readField(name);
    }

    public void writeField(String name) {
        this.ensureAllocated();
        Structure.StructField f = (Structure.StructField)this.fields().get(name);
        if (f != null) {
            this.setType(f.type);
        }
        super.writeField(name);
    }

    public void writeField(String name, Object value) {
        this.ensureAllocated();
        Structure.StructField f = (Structure.StructField)this.fields().get(name);
        if (f != null) {
            this.setType(f.type);
        }
        super.writeField(name, value);
    }

    public Object getTypedValue(Class type) {
        this.ensureAllocated();
        Iterator i = this.fields().values().iterator();
        while (i.hasNext()) {
            Structure.StructField f = (Structure.StructField)i.next();
            if (f.type != type) continue;
            this.activeField = f;
            this.read();
            return this.getField(this.activeField);
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }

    public Object setTypedValue(Object object) {
        this.ensureAllocated();
        Structure.StructField f = this.findField(object.getClass());
        if (f != null) {
            this.activeField = f;
            this.setField(f, object);
            return this;
        }
        throw new IllegalArgumentException("No field of type " + object.getClass() + " in " + this);
    }

    private Structure.StructField findField(Class type) {
        Iterator i = this.fields().values().iterator();
        while (i.hasNext()) {
            Structure.StructField f = (Structure.StructField)i.next();
            if (!f.type.isAssignableFrom(type)) continue;
            return f;
        }
        return null;
    }

    void writeField(Structure.StructField field) {
        if (field == this.activeField) {
            super.writeField(field);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Object readField(Structure.StructField field) {
        if (field == this.activeField) return super.readField(field);
        Class class_ = Structure.class;
        if (class_.isAssignableFrom(field.type)) return null;
        Class class_2 = String.class;
        if (class_2.isAssignableFrom(field.type)) return null;
        Class class_3 = WString.class;
        if (class_3.isAssignableFrom(field.type)) return null;
        return super.readField(field);
    }

    int calculateSize(boolean force) {
        int size = super.calculateSize(force);
        if (size != -1) {
            int fsize = 0;
            Iterator i = this.fields().values().iterator();
            while (i.hasNext()) {
                Structure.StructField f = (Structure.StructField)i.next();
                f.offset = 0;
                if (f.size <= fsize && (f.size != fsize || !(class$com$sun$jna$Structure == null ? Union.class$("com.sun.jna.Structure") : class$com$sun$jna$Structure).isAssignableFrom(f.type))) continue;
                fsize = f.size;
                this.biggestField = f;
            }
            size = this.calculateAlignedSize(fsize);
            if (size > 0 && this instanceof Structure.ByValue) {
                this.getTypeInfo();
            }
        }
        return size;
    }

    protected int getNativeAlignment(Class type, Object value, boolean isFirstElement) {
        return super.getNativeAlignment(type, value, true);
    }

    Pointer getTypeInfo() {
        if (this.biggestField == null) {
            return null;
        }
        return super.getTypeInfo();
    }
}

