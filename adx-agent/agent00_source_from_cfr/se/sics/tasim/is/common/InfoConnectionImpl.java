/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import java.io.IOException;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.is.InfoConnection;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.is.common.SimServer;

public class InfoConnectionImpl
extends InfoConnection {
    private SimServer simServer;

    @Override
    public void init(ConfigManager config) throws IllegalConfigurationException, IOException {
    }

    @Override
    public void close() {
        if (this.simServer != null) {
            this.simServer.close();
        }
    }

    public void setSimServer(SimServer simServer) {
        if (simServer == null) {
            throw new NullPointerException();
        }
        this.simServer = simServer;
    }

    public SimServer getSimServer() {
        return this.simServer;
    }

    @Override
    public void requestSuccessful(int operation, int id) {
        this.simServer.requestSuccessful(operation, id);
    }

    @Override
    public void requestFailed(int operation, int id, String reason) {
        this.simServer.requestFailed(operation, id, reason);
    }

    @Override
    public void checkUser(String userName) {
        this.simServer.checkUser(userName);
    }

    @Override
    public int addUser(String name, String password, String email) {
        return this.simServer.addUser(name, password, email);
    }

    @Override
    public void dataUpdated(int type, int value) {
        this.simServer.dataUpdated(type, value);
    }

    @Override
    public void simulationCreated(SimulationInfo info) {
        this.simServer.simulationCreated(info);
    }

    @Override
    public void simulationCreated(SimulationInfo info, int competitionID) {
        this.simServer.simulationCreated(info, competitionID);
    }

    @Override
    public void simulationRemoved(int simulationUniqID, String msg) {
        this.simServer.simulationRemoved(simulationUniqID, msg);
    }

    @Override
    public void simulationJoined(int simulationUniqID, int agentID, int role) {
        this.simServer.simulationJoined(simulationUniqID, agentID, role);
    }

    @Override
    public void simulationLocked(int simulationUniqID, int simID) {
        this.simServer.simulationLocked(simulationUniqID, simID);
    }

    @Override
    public void simulationStarted(int simulationUniqID, String timeUnitName, int timeUnitCount) {
        this.simServer.simulationStarted(simulationUniqID, timeUnitName, timeUnitCount);
    }

    @Override
    public void simulationStopped(int simulationUniqID, int simulationID, boolean error) {
        this.simServer.simulationStopped(simulationUniqID, simulationID, error);
    }

    @Override
    public void sendChatMessage(long time, String message) {
        this.simServer.sendChatMessage(time, message);
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
        this.simServer.nextTimeUnit(timeUnit);
    }

    @Override
    public void participant(int id, int role, String name, int participantID) {
        this.simServer.participant(id, role, name, participantID);
    }

    @Override
    public void dataUpdated(int agent, int type, int value) {
        this.simServer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, long value) {
        this.simServer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, float value) {
        this.simServer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, double value) {
        this.simServer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, String value) {
        this.simServer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int agent, int type, Transportable value) {
        this.simServer.dataUpdated(agent, type, value);
    }

    @Override
    public void dataUpdated(int type, Transportable value) {
        this.simServer.dataUpdated(type, value);
    }

    @Override
    public void interaction(int fromAgent, int toAgent, int type) {
        this.simServer.interaction(fromAgent, toAgent, type);
    }

    @Override
    public void interactionWithRole(int fromAgent, int role, int type) {
        this.simServer.interactionWithRole(fromAgent, role, type);
    }

    @Override
    public void intCache(int agent, int type, int[] cache) {
    }
}

