/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.FromNativeConverter;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeConverter;
import com.sun.jna.TypeMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultTypeMapper
implements TypeMapper {
    private List toNativeConverters = new ArrayList();
    private List fromNativeConverters = new ArrayList();

    private Class getAltClass(Class cls) {
        Class class_ = Boolean.class;
        if (cls == class_) {
            return Boolean.TYPE;
        }
        if (cls == Boolean.TYPE) {
            return Boolean.class;
        }
        Class class_2 = Byte.class;
        if (cls == class_2) {
            return Byte.TYPE;
        }
        if (cls == Byte.TYPE) {
            return Byte.class;
        }
        Class class_3 = Character.class;
        if (cls == class_3) {
            return Character.TYPE;
        }
        if (cls == Character.TYPE) {
            return Character.class;
        }
        Class class_4 = Short.class;
        if (cls == class_4) {
            return Short.TYPE;
        }
        if (cls == Short.TYPE) {
            return Short.class;
        }
        Class class_5 = Integer.class;
        if (cls == class_5) {
            return Integer.TYPE;
        }
        if (cls == Integer.TYPE) {
            return Integer.class;
        }
        Class class_6 = Long.class;
        if (cls == class_6) {
            return Long.TYPE;
        }
        if (cls == Long.TYPE) {
            return Long.class;
        }
        Class class_7 = Float.class;
        if (cls == class_7) {
            return Float.TYPE;
        }
        if (cls == Float.TYPE) {
            return Float.class;
        }
        Class class_8 = Double.class;
        if (cls == class_8) {
            return Double.TYPE;
        }
        if (cls == Double.TYPE) {
            return Double.class;
        }
        return null;
    }

    public void addToNativeConverter(Class cls, ToNativeConverter converter) {
        this.toNativeConverters.add(new Entry(cls, converter));
        Class alt = this.getAltClass(cls);
        if (alt != null) {
            this.toNativeConverters.add(new Entry(alt, converter));
        }
    }

    public void addFromNativeConverter(Class cls, FromNativeConverter converter) {
        this.fromNativeConverters.add(new Entry(cls, converter));
        Class alt = this.getAltClass(cls);
        if (alt != null) {
            this.fromNativeConverters.add(new Entry(alt, converter));
        }
    }

    protected void addTypeConverter(Class cls, TypeConverter converter) {
        this.addFromNativeConverter(cls, converter);
        this.addToNativeConverter(cls, converter);
    }

    private Object lookupConverter(Class javaClass, List converters) {
        Iterator i = converters.iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            if (!entry.type.isAssignableFrom(javaClass)) continue;
            return entry.converter;
        }
        return null;
    }

    public FromNativeConverter getFromNativeConverter(Class javaType) {
        return (FromNativeConverter)this.lookupConverter(javaType, this.fromNativeConverters);
    }

    public ToNativeConverter getToNativeConverter(Class javaType) {
        return (ToNativeConverter)this.lookupConverter(javaType, this.toNativeConverters);
    }

    private static class Entry {
        public Class type;
        public Object converter;

        public Entry(Class type, Object converter) {
            this.type = type;
            this.converter = converter;
        }
    }

}

