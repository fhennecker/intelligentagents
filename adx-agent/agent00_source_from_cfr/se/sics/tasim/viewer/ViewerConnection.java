/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import se.sics.tasim.is.EventWriter;

public abstract class ViewerConnection
extends EventWriter {
    public abstract void setServerTime(long var1);

    public abstract void simulationStarted(int var1, String var2, long var3, long var5, String var7, int var8);

    public abstract void simulationStopped(int var1);

    public abstract void nextSimulation(int var1, long var2);

    @Override
    public abstract void intCache(int var1, int var2, int[] var3);
}

