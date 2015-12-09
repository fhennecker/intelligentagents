/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.util.config;

import edu.umich.eecs.tac.util.config.ConfigProxy;

public class ConfigProxyUtils {
    private ConfigProxyUtils() {
    }

    public static <T> T createObjectFromProperty(ConfigProxy proxy, String name, String defaultValue) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (T)Class.forName(proxy.getProperty(name, defaultValue)).newInstance();
    }
}

