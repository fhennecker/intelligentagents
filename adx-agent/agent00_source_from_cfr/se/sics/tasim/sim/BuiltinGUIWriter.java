/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.viewer.ViewerConnection;

public class BuiltinGUIWriter
extends ViewerConnection {
    private ViewerConnection viewer;
    private EventWriter writer;

    public BuiltinGUIWriter(ViewerConnection viewer, EventWriter writer) {
        this.viewer = viewer;
        this.writer = writer;
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
        this.viewer.nextTimeUnit(timeUnit);
        this.writer.nextTimeUnit(timeUnit);
    }

    @Override
    public void participant(int index, int role, String name, int participantID) {
        this.viewer.participant(index, role, name, participantID);
        this.writer.participant(index, role, name, participantID);
    }

    @Override
    public void dataUpdated(int agent, int type, int value) {
        this.viewer.dataUpdated(agent, type, value);
        this.writer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, long value) {
        this.viewer.dataUpdated(agent, type, value);
        this.writer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, float value) {
        this.viewer.dataUpdated(agent, type, value);
        this.writer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, double value) {
        this.viewer.dataUpdated(agent, type, value);
        this.writer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, String value) {
        this.viewer.dataUpdated(agent, type, value);
        this.writer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, Transportable value) {
        this.viewer.dataUpdated(agent, type, value);
        this.writer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int type, Transportable value) {
        this.viewer.dataUpdated(type, value);
        this.writer.dataUpdated(type, value);
    }

    @Override
    public void interaction(int fromAgent, int toAgent, int type) {
        this.viewer.interaction(fromAgent, toAgent, type);
        this.writer.interaction(fromAgent, toAgent, type);
    }

    @Override
    public void interactionWithRole(int fromAgent, int role, int type) {
        this.viewer.interactionWithRole(fromAgent, role, type);
        this.writer.interactionWithRole(fromAgent, role, type);
    }

    @Override
    public void setServerTime(long serverTime) {
        this.viewer.setServerTime(serverTime);
    }

    @Override
    public void simulationStarted(int realSimID, String type, long startTime, long endTime, String timeUnitName, int timeUnitCount) {
        this.viewer.simulationStarted(realSimID, type, startTime, endTime, timeUnitName, timeUnitCount);
    }

    @Override
    public void simulationStopped(int realSimID) {
        this.viewer.simulationStopped(realSimID);
    }

    @Override
    public void nextSimulation(int realSimID, long startTime) {
        this.viewer.nextSimulation(realSimID, startTime);
    }

    @Override
    public void intCache(int agent, int type, int[] cache) {
        this.viewer.intCache(agent, type, cache);
    }
}

