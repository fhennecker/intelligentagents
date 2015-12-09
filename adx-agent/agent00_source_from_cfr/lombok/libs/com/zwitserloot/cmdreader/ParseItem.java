/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.com.zwitserloot.cmdreader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import lombok.libs.com.zwitserloot.cmdreader.Description;
import lombok.libs.com.zwitserloot.cmdreader.Excludes;
import lombok.libs.com.zwitserloot.cmdreader.FullName;
import lombok.libs.com.zwitserloot.cmdreader.Mandatory;
import lombok.libs.com.zwitserloot.cmdreader.Requires;
import lombok.libs.com.zwitserloot.cmdreader.Sequential;
import lombok.libs.com.zwitserloot.cmdreader.Shorthand;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ParseItem {
    private final List<Class<?>> LEGAL_CLASSES = Collections.unmodifiableList(Arrays.asList(Integer.class, Long.class, Short.class, Byte.class, Float.class, Double.class, Boolean.class, Character.class, String.class, Enum.class));
    private final Field field;
    private final boolean isCollection;
    private final Class<?> type;
    private final String fullName;
    private final boolean isSeq;
    private final boolean isParameterized;
    private final boolean isMandatory;
    private final String shorthand;
    private final String description;
    private final List<String> excludes;
    private final List<String> mandatoryIf;
    private final List<String> mandatoryIfNot;
    private final List<String> requires;
    private static final List<Class<?>> ARRAY_LIST_COMPATIBLES = Collections.unmodifiableList(Arrays.asList(Collection.class, AbstractCollection.class, List.class, AbstractList.class, ArrayList.class));
    private static final List<Class<?>> HASH_SET_COMPATIBLES = Collections.unmodifiableList(Arrays.asList(Set.class, AbstractSet.class, HashSet.class));
    private static final List<Class<?>> LINKED_LIST_COMPATIBLES = Collections.unmodifiableList(Arrays.asList(AbstractSequentialList.class, Queue.class, LinkedList.class));
    private List<String> TRUE_VALS = Collections.unmodifiableList(Arrays.asList("1", "true", "t", "y", "yes", "on"));
    private List<String> FALSE_VALS = Collections.unmodifiableList(Arrays.asList("0", "false", "f", "n", "no", "off"));

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    ParseItem(Field field) {
        reference rawType3;
        this.field = field;
        field.setAccessible(true);
        if (Collection.class.isAssignableFrom(field.getType())) {
            this.isCollection = true;
            Type genericType = field.getGenericType();
            Type[] typeArgs = null;
            if (!(genericType instanceof ParameterizedType) || (typeArgs = ((ParameterizedType)genericType).getActualTypeArguments()) == null || typeArgs.length != 1 || !(typeArgs[0] instanceof Class)) throw new IllegalArgumentException(String.format("Only primitives, Strings, Enums, and Collections of those are allowed (for type: %s)", field.getGenericType()));
            Class rawType2 = (Class)typeArgs[0];
        } else {
            this.isCollection = false;
            Class rawType3 = field.getType();
        }
        this.type = rawType3 == Integer.TYPE ? Integer.class : (rawType3 == Long.TYPE ? Long.class : (rawType3 == Short.TYPE ? Short.class : (rawType3 == Byte.TYPE ? Byte.class : (rawType3 == Double.TYPE ? Double.class : (rawType3 == Float.TYPE ? Float.class : (rawType3 == Character.TYPE ? Character.class : (rawType3 == Boolean.TYPE ? Boolean.class : rawType3)))))));
        if (!this.LEGAL_CLASSES.contains(this.type)) {
            throw new IllegalArgumentException("Not a valid class for command line parsing: " + field.getGenericType());
        }
        this.fullName = this.setupFullName(field);
        this.isSeq = field.getAnnotation(Sequential.class) != null;
        this.isParameterized = field.getType() != Boolean.TYPE && field.getType() != Boolean.class;
        this.shorthand = this.setupShorthand(field);
        this.description = this.setupDescription(field);
        this.isMandatory = this.setupMandatory(field);
        this.mandatoryIf = this.setupMandatoryIf(field);
        this.mandatoryIfNot = this.setupMandatoryIfNot(field);
        this.requires = this.setupRequires(field);
        this.excludes = this.setupExcludes(field);
        try {
            this.sanityChecks();
            return;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("%s (at %s)", e.getMessage(), this.fullName));
        }
    }

    private void sanityChecks() {
        if (!this.isParameterized && Boolean.class != this.type) {
            throw new IllegalArgumentException("Non-parameterized parameters must have type boolean. - it's there (true), or not (false).");
        }
        if (!this.isParameterized && this.isMandatory) {
            throw new IllegalArgumentException("Non-parameterized parameters must not be mandatory - what's the point of having it?");
        }
        if (this.isSeq && !"".equals(this.shorthand)) {
            throw new IllegalArgumentException("sequential parameters must not have any shorthands.");
        }
        if (this.isSeq && !this.isParameterized) {
            throw new IllegalArgumentException("sequential parameters must always be parameterized.");
        }
    }

    static void multiSanityChecks(List<ParseItem> items) {
        int len = items.size();
        for (int i = 0; i < len; ++i) {
            for (int j = i + 1; j < len; ++j) {
                if (!items.get((int)i).fullName.equalsIgnoreCase(items.get((int)j).fullName)) continue;
                throw new IllegalArgumentException(String.format("Duplicate full names for fields %s and %s.", items.get((int)i).field.getName(), items.get((int)j).field.getName()));
            }
        }
        ParseItem isCollectionIsSeq = null;
        for (ParseItem item : items) {
            if (item.isSeq && isCollectionIsSeq != null) {
                throw new IllegalArgumentException(String.format("After the sequential, collection item %s no more sequential items allowed (at %s)", isCollectionIsSeq.fullName, item.fullName));
            }
            if (!item.isSeq || !item.isCollection) continue;
            isCollectionIsSeq = item;
        }
        ParseItem firstNonMandatoryIsSeq = null;
        for (ParseItem item2 : items) {
            if (!item2.isSeq) continue;
            if (firstNonMandatoryIsSeq == null && !item2.isMandatory) {
                firstNonMandatoryIsSeq = item2;
            }
            if (!item2.isMandatory || firstNonMandatoryIsSeq == null) continue;
            throw new IllegalArgumentException(String.format("Sequential item %s is non-mandatory, so %s which is a later sequential item must also be non-mandatory", firstNonMandatoryIsSeq.fullName, item2.fullName));
        }
    }

    static Map<Character, ParseItem> makeShortHandMap(List<ParseItem> items) {
        HashMap<Character, ParseItem> out = new HashMap<Character, ParseItem>();
        for (ParseItem item : items) {
            for (char c : item.shorthand.toCharArray()) {
                if (out.containsKey(Character.valueOf(c))) {
                    throw new IllegalArgumentException(String.format("Both %s and %s contain the shorthand %s", out.get((Object)Character.valueOf((char)c)).fullName, item.fullName, Character.valueOf(c)));
                }
                out.put(Character.valueOf(c), item);
            }
        }
        return out;
    }

    String getFullName() {
        return this.fullName;
    }

    boolean isSeq() {
        return this.isSeq;
    }

    boolean isMandatory() {
        return this.isMandatory;
    }

    List<String> getMandatoryIf() {
        return this.mandatoryIf;
    }

    List<String> getMandatoryIfNot() {
        return this.mandatoryIfNot;
    }

    List<String> getRequires() {
        return this.requires;
    }

    List<String> getExcludes() {
        return this.excludes;
    }

    boolean isParameterized() {
        return this.isParameterized;
    }

    boolean isCollection() {
        return this.isCollection;
    }

    String getFullDescription() {
        return this.description;
    }

    void set(Object o, String value) {
        Object v = this.stringToObject(value);
        try {
            if (this.isCollection) {
                AbstractCollection l = (ArrayList<Object>)this.field.get(o);
                if (l == null) {
                    if (ARRAY_LIST_COMPATIBLES.contains(this.field.getType())) {
                        l = new ArrayList<Object>();
                    } else if (LINKED_LIST_COMPATIBLES.contains(this.field.getType())) {
                        l = new LinkedList();
                    } else if (HASH_SET_COMPATIBLES.contains(this.field.getType())) {
                        l = new HashSet();
                    } else {
                        throw new IllegalArgumentException("Cannot construct a collection of type " + this.field.getType() + " -- try List, Set, Collection, or Queue.");
                    }
                    this.field.set(o, l);
                }
                l.add(v);
            } else {
                this.field.set(o, v);
            }
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Huh?");
        }
    }

    private Object stringToObject(String raw) {
        if (String.class == this.type) {
            return raw;
        }
        if (Integer.class == this.type) {
            return Integer.parseInt(raw);
        }
        if (Long.class == this.type) {
            return Long.parseLong(raw);
        }
        if (Short.class == this.type) {
            return Short.parseShort(raw);
        }
        if (Byte.class == this.type) {
            return Byte.valueOf(Byte.parseByte(raw));
        }
        if (Float.class == this.type) {
            return Float.valueOf(Float.parseFloat(raw));
        }
        if (Double.class == this.type) {
            return Double.parseDouble(raw);
        }
        if (Boolean.class == this.type) {
            return raw == null ? true : this.parseBoolean(raw);
        }
        if (Character.class == this.type) {
            return Character.valueOf(raw.length() == 0 ? '\u0000' : raw.charAt(0));
        }
        if (Enum.class == this.type) {
            return Enum.valueOf(this.type, raw);
        }
        throw new IllegalArgumentException("Huh?");
    }

    private String setupFullName(Field field) {
        FullName ann = (FullName)field.getAnnotation(FullName.class);
        if (ann == null) {
            return field.getName();
        }
        if (ann.value().trim().equals("")) {
            throw new IllegalArgumentException("Missing name for field: " + field.getName());
        }
        return ann.value();
    }

    private String setupShorthand(Field field) {
        Shorthand ann = (Shorthand)field.getAnnotation(Shorthand.class);
        if (ann == null) {
            return "";
        }
        String[] value = ann.value();
        StringBuilder sb = new StringBuilder();
        for (String v : value) {
            char[] c = v.toCharArray();
            if (c.length != 1) {
                throw new IllegalArgumentException(String.format("Shorthands must be strings of 1 character long. (%s at %s)", v, this.fullName));
            }
            if (c[0] == '-') {
                throw new IllegalArgumentException(String.format("The dash (-) is not a legal shorthand character. (at %s)", this.fullName));
            }
            if (sb.indexOf(v) > -1) {
                throw new IllegalArgumentException(String.format("Duplicate shorthand: %s (at %s)", v, this.fullName));
            }
            sb.append(v);
        }
        return sb.toString();
    }

    private String setupDescription(Field field) {
        StringBuilder out = new StringBuilder();
        Description ann = (Description)field.getAnnotation(Description.class);
        if (ann != null) {
            out.append(ann.value());
        }
        if (this.isCollection) {
            out.append(out.length() > 0 ? "  " : "").append("This option may be used multiple times.");
        }
        if (this.isParameterized && this.type != String.class) {
            if (out.length() > 0) {
                out.append("  ");
            }
            if (this.type == Float.class || this.type == Double.class) {
                out.append("value is a floating point number.");
            }
            if (this.type == Integer.class || this.type == Long.class || this.type == Short.class || this.type == Byte.class) {
                out.append("value is an integer.");
            }
            if (this.type == Boolean.class) {
                out.append("value is 'true' or 'false'.");
            }
            if (this.type == Character.class) {
                out.append("Value is a single character.");
            }
            if (this.type == Enum.class) {
                Enum[] enumConstants;
                out.append("value is one of: ");
                boolean first = true;
                for (Enum e : enumConstants = (Enum[])this.type.getEnumConstants()) {
                    if (first) {
                        first = false;
                    } else {
                        out.append(", ");
                    }
                    out.append(e.name());
                }
                out.append(".");
            }
        }
        return out.toString();
    }

    private boolean setupMandatory(Field field) {
        Mandatory mandatory = (Mandatory)field.getAnnotation(Mandatory.class);
        return mandatory != null && mandatory.onlyIf().length == 0 && mandatory.onlyIfNot().length == 0;
    }

    private List<String> setupMandatoryIf(Field field) {
        Mandatory mandatory = (Mandatory)field.getAnnotation(Mandatory.class);
        if (mandatory == null || mandatory.onlyIf().length == 0) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(mandatory.onlyIf()));
    }

    private List<String> setupMandatoryIfNot(Field field) {
        Mandatory mandatory = (Mandatory)field.getAnnotation(Mandatory.class);
        if (mandatory == null || mandatory.onlyIfNot().length == 0) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(mandatory.onlyIfNot()));
    }

    private List<String> setupRequires(Field feild) {
        Requires requires = (Requires)this.field.getAnnotation(Requires.class);
        if (requires == null || requires.value().length == 0) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(requires.value()));
    }

    private List<String> setupExcludes(Field field) {
        Excludes excludes = (Excludes)field.getAnnotation(Excludes.class);
        if (excludes == null || excludes.value().length == 0) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(excludes.value()));
    }

    private boolean parseBoolean(String raw) {
        for (String x2 : this.TRUE_VALS) {
            if (!x2.equalsIgnoreCase(raw)) continue;
            return true;
        }
        for (String x2 : this.FALSE_VALS) {
            if (!x2.equalsIgnoreCase(raw)) continue;
            return false;
        }
        throw new IllegalArgumentException("Not a boolean: " + raw);
    }

    String getShorthand() {
        return this.shorthand;
    }
}
