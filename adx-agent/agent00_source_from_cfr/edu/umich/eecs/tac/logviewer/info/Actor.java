/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.info;

public class Actor {
    private int simulationIndex;
    private String address;
    private String name;

    public Actor(int simulationIndex, String address, String name) {
        this.simulationIndex = simulationIndex;
        this.address = address;
        this.name = name == null ? address : name;
    }

    public int getSimulationIndex() {
        return this.simulationIndex;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }
}

