/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db;

import se.sics.isl.db.DBTable;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;

public abstract class Database {
    private String name;

    protected Database() {
    }

    public final void init(String name, ConfigManager config, String prefix) throws IllegalConfigurationException {
        if (this.name != null) {
            throw new IllegalStateException("already initialized");
        }
        this.validateName(name);
        this.name = name;
        this.init(config, prefix);
    }

    protected abstract void init(ConfigManager var1, String var2) throws IllegalConfigurationException;

    public void validateName(String name) {
        if (name == null || name.length() < 1) {
            throw new IllegalArgumentException("too short name '" + name + '\'');
        }
        char c = name.charAt(0);
        if (c < 'a' || c > 'z') {
            throw new IllegalArgumentException("illegal prefix in name '" + name + '\'');
        }
        int i = 1;
        int n = name.length();
        while (i < n) {
            c = name.charAt(i);
            if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_')) {
                throw new IllegalArgumentException("illegal character '" + c + "' in '" + name + '\'');
            }
            ++i;
        }
    }

    public final String getName() {
        return this.name;
    }

    public abstract DBTable createTable(String var1);

    public abstract DBTable getTable(String var1);

    public abstract void flush();

    public abstract void drop();

    public abstract boolean isClosed();

    public abstract void close();

    public static int parseInt(Object value, int defaultValue) {
        if (value instanceof Integer) {
            return (Integer)value;
        }
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Long) {
                return (int)((Long)value).longValue();
            }
            return Integer.parseInt(value.toString());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static long parseLong(Object value, long defaultValue) {
        if (value instanceof Long) {
            return (Long)value;
        }
        if (value instanceof Integer) {
            return ((Integer)value).intValue();
        }
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.toString());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static double parseDouble(Object value, double defaultValue) {
        if (value instanceof Double) {
            return (Double)value;
        }
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Integer) {
                return ((Integer)value).intValue();
            }
            if (value instanceof Long) {
                return ((Long)value).longValue();
            }
            return Double.parseDouble(value.toString());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
}

