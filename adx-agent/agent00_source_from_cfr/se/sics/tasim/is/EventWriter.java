/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is;

import se.sics.isl.transport.Transportable;

public abstract class EventWriter {
    protected EventWriter() {
    }

    public abstract void participant(int var1, int var2, String var3, int var4);

    public abstract void nextTimeUnit(int var1);

    public abstract void dataUpdated(int var1, int var2, int var3);

    public abstract void dataUpdated(int var1, int var2, long var3);

    public abstract void dataUpdated(int var1, int var2, float var3);

    public abstract void dataUpdated(int var1, int var2, double var3);

    public abstract void dataUpdated(int var1, int var2, String var3);

    public abstract void dataUpdated(int var1, int var2, Transportable var3);

    public abstract void dataUpdated(int var1, Transportable var2);

    public abstract void interaction(int var1, int var2, int var3);

    public abstract void interactionWithRole(int var1, int var2, int var3);

    public abstract void intCache(int var1, int var2, int[] var3);
}

