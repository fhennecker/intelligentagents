/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import se.sics.isl.util.IllegalConfigurationException;

public class ConfigManager {
    private static Logger logCache;
    protected final ConfigManager parent;
    protected final Properties properties = new Properties();

    public ConfigManager() {
        this(null);
    }

    public ConfigManager(ConfigManager parent) {
        this.parent = parent;
    }

    public boolean loadConfiguration(String configFile) {
        try {
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(configFile));
            try {
                this.loadConfiguration(input);
            }
            finally {
                input.close();
            }
        }
        catch (FileNotFoundException e) {
            return false;
        }
        catch (IOException e) {
            throw (IllegalArgumentException)new IllegalArgumentException("could not read config file '" + configFile + '\'').initCause(e);
        }
    }

    public boolean loadConfiguration(URL configURL) {
        try {
            BufferedInputStream input = new BufferedInputStream(configURL.openStream());
            try {
                this.loadConfiguration(input);
            }
            finally {
                input.close();
            }
        }
        catch (FileNotFoundException e) {
            return false;
        }
        catch (IOException e) {
            throw (IllegalArgumentException)new IllegalArgumentException("could not read config file '" + configURL + '\'').initCause(e);
        }
    }

    public void loadConfiguration(InputStream input) throws IOException {
        Properties properties = this.properties;
        synchronized (properties) {
            this.properties.clear();
            this.properties.load(input);
        }
    }

    public Enumeration names() {
        return this.properties.keys();
    }

    public String getProperty(String name) {
        return this.getProperty(name, null);
    }

    public String getProperty(String name, String defaultValue) {
        String value = this.properties.getProperty(name);
        if (value == null || value.length() == 0) {
            value = this.parent != null ? this.parent.getProperty(name, defaultValue) : defaultValue;
        }
        return value;
    }

    public void setProperty(String name, String value) {
        this.properties.setProperty(name, value);
    }

    public String[] getPropertyAsArray(String name) {
        return this.getPropertyAsArray(name, null);
    }

    public String[] getPropertyAsArray(String name, String defaultValue) {
        StringTokenizer tok;
        int len;
        String valueList = this.getProperty(name, defaultValue);
        if (valueList != null && (len = (tok = new StringTokenizer(valueList, ", \t")).countTokens()) > 0) {
            String[] names = new String[len];
            int i = 0;
            while (i < len) {
                names[i] = tok.nextToken();
                ++i;
            }
            return names;
        }
        return null;
    }

    public int getPropertyAsInt(String name, int defaultValue) {
        String value = this.getProperty(name, null);
        return value != null ? this.parseInt(name, value, defaultValue) : defaultValue;
    }

    public long getPropertyAsLong(String name, long defaultValue) {
        String value = this.getProperty(name, null);
        return value != null ? this.parseLong(name, value, defaultValue) : defaultValue;
    }

    public float getPropertyAsFloat(String name, float defaultValue) {
        String value = this.getProperty(name, null);
        return value != null ? this.parseFloat(name, value, defaultValue) : defaultValue;
    }

    public double getPropertyAsDouble(String name, double defaultValue) {
        String value = this.getProperty(name, null);
        return value != null ? this.parseDouble(name, value, defaultValue) : defaultValue;
    }

    public boolean getPropertyAsBoolean(String name, boolean defaultValue) {
        String value = this.getProperty(name, null);
        return value != null ? this.parseBoolean(name, value, defaultValue) : defaultValue;
    }

    protected int parseInt(String name, String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        }
        catch (Exception e) {
            if (logCache == null) {
                logCache = Logger.getLogger(ConfigManager.class.getName());
            }
            logCache.warning("config '" + name + "' has a non-integer value '" + value + '\'');
            return defaultValue;
        }
    }

    protected long parseLong(String name, String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        }
        catch (Exception e) {
            if (logCache == null) {
                logCache = Logger.getLogger(ConfigManager.class.getName());
            }
            logCache.warning("config '" + name + "' has a non-long value '" + value + '\'');
            return defaultValue;
        }
    }

    protected float parseFloat(String name, String value, float defaultValue) {
        try {
            return Float.parseFloat(value);
        }
        catch (Exception e) {
            if (logCache == null) {
                logCache = Logger.getLogger(ConfigManager.class.getName());
            }
            logCache.warning("config '" + name + "' has a non-float value '" + value + '\'');
            return defaultValue;
        }
    }

    protected double parseDouble(String name, String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        }
        catch (Exception e) {
            if (logCache == null) {
                logCache = Logger.getLogger(ConfigManager.class.getName());
            }
            logCache.warning("config '" + name + "' has a non-double value '" + value + '\'');
            return defaultValue;
        }
    }

    protected boolean parseBoolean(String name, String value, boolean defaultValue) {
        if (!("true".equals(value) || "yes".equals(value) || "1".equals(value))) {
            return false;
        }
        return true;
    }

    public Object[] createInstances(String configName, Class type) throws IllegalConfigurationException {
        return this.createInstances(configName, type, this.getPropertyAsArray(String.valueOf(configName) + ".names"));
    }

    public Object[] createInstances(String configName, Class type, String[] names) throws IllegalConfigurationException {
        if (names == null || names.length == 0) {
            return null;
        }
        String className = null;
        String iName = null;
        String defaultClassName = this.getProperty(String.valueOf(configName) + ".class");
        try {
            Object[] vector = (Object[])Array.newInstance(type, names.length);
            int i = 0;
            int n = names.length;
            while (i < n) {
                iName = names[i];
                className = this.getProperty(String.valueOf(configName) + '.' + iName + ".class", defaultClassName);
                if (className == null) {
                    throw new IllegalConfigurationException("no class definition for " + configName + ' ' + iName);
                }
                vector[i] = Class.forName(className).newInstance();
                ++i;
            }
            return vector;
        }
        catch (IllegalConfigurationException e) {
            throw e;
        }
        catch (Exception e) {
            throw (IllegalConfigurationException)new IllegalConfigurationException("could not create " + configName + ' ' + iName + " '" + className + '\'').initCause(e);
        }
    }

    public static int compareVersion(String version1, String version2) {
        if (version1 == null) {
            return version2 == null ? 0 : -1;
        }
        if (version2 == null) {
            return 1;
        }
        int s1 = 0;
        int s2 = 0;
        int l1 = version1.length();
        int l2 = version2.length();
        do {
            int c;
            int i1 = version1.indexOf(46, s1);
            int i2 = version2.indexOf(46, s2);
            if (i1 < 0) {
                i1 = l1;
            }
            if (i2 < 0) {
                i2 = l2;
            }
            if ((c = ConfigManager.compareVersion(version1, s1, i1, version2, s2, i2)) != 0) {
                return c;
            }
            s1 = i1 + 1;
            s2 = i2 + 1;
        } while (s1 < l1 || s2 < l2);
        return 0;
    }

    private static int compareVersion(String version1, int s1, int e1, String version2, int s2, int e2) {
        int e1len = e1 - s1;
        int e2len = e2 - s2;
        int len = e1len > e2len ? e1len : e2len;
        int i = 0;
        int pos1 = e1 - len;
        int pos2 = e2 - len;
        while (i < len) {
            int c2;
            int c1 = pos1 < s1 || pos1 >= e1 ? 48 : (int)version1.charAt(pos1);
            int n = c2 = pos2 < s2 || pos2 >= e2 ? 48 : (int)version2.charAt(pos2);
            if (c1 < c2) {
                return -1;
            }
            if (c1 > c2) {
                return 1;
            }
            ++i;
            ++pos1;
            ++pos2;
        }
        return 0;
    }
}

