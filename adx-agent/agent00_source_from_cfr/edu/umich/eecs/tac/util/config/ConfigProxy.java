/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.util.config;

public interface ConfigProxy {
    public String getProperty(String var1);

    public String getProperty(String var1, String var2);

    public String[] getPropertyAsArray(String var1);

    public String[] getPropertyAsArray(String var1, String var2);

    public int getPropertyAsInt(String var1, int var2);

    public int[] getPropertyAsIntArray(String var1);

    public int[] getPropertyAsIntArray(String var1, String var2);

    public long getPropertyAsLong(String var1, long var2);

    public float getPropertyAsFloat(String var1, float var2);

    public double getPropertyAsDouble(String var1, double var2);
}

