/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer;

import se.sics.isl.transport.Transportable;

public interface ViewListener {
    public void dataUpdated(int var1, int var2, int var3);

    public void dataUpdated(int var1, int var2, long var3);

    public void dataUpdated(int var1, int var2, float var3);

    public void dataUpdated(int var1, int var2, double var3);

    public void dataUpdated(int var1, int var2, String var3);

    public void dataUpdated(int var1, int var2, Transportable var3);

    public void dataUpdated(int var1, Transportable var2);

    public void participant(int var1, int var2, String var3, int var4);
}

