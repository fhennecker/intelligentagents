/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.sim.SimulationAgent;

public interface MessageListener {
    public void messageReceived(SimulationAgent var1, String var2, Transportable var3);

    public void messageSent(SimulationAgent var1, String var2, Transportable var3);

    public void messageSent(SimulationAgent var1, int var2, Transportable var3);
}

