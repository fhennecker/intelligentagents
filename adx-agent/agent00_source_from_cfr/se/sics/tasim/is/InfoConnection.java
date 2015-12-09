/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is;

import java.io.IOException;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.is.SimConnection;
import se.sics.tasim.is.SimulationInfo;

public abstract class InfoConnection
extends EventWriter {
    public static final int STATUS = 1;
    public static final int UNIQUE_SIM_ID = 2;
    public static final int SIM_ID = 3;
    public static final int STATUS_READY = 1;
    public static final int CREATE_SIMULATION = 1;
    public static final int REMOVE_SIMULATION = 2;
    public static final int JOIN_SIMULATION = 3;
    public static final int RESERVE_TIME = 4;
    public static final int SCHEDULE_COMPETITION = 10;
    private SimConnection sim;
    private String serverName;
    private String serverPassword;
    private String serverVersion;

    public void setSimConnection(SimConnection sim) {
        if (this.sim != null) {
            throw new IllegalStateException("Connection already set");
        }
        this.sim = sim;
    }

    public SimConnection getSimConnection() {
        return this.sim;
    }

    public abstract void init(ConfigManager var1) throws IllegalConfigurationException, IOException;

    public abstract void close();

    public String getServerName() {
        return this.serverName;
    }

    public String getServerPassword() {
        return this.serverPassword;
    }

    public String getServerVersion() {
        return this.serverVersion;
    }

    public void auth(String serverName, String serverPassword, String serverVersion) {
        if (serverName == null || serverVersion == null) {
            throw new NullPointerException();
        }
        if (serverName.length() < 1) {
            throw new IllegalArgumentException("too short server name");
        }
        this.serverName = serverName;
        this.serverPassword = serverPassword;
        this.serverVersion = serverVersion;
    }

    public abstract void requestSuccessful(int var1, int var2);

    public abstract void requestFailed(int var1, int var2, String var3);

    public abstract void checkUser(String var1);

    public abstract int addUser(String var1, String var2, String var3);

    public abstract void dataUpdated(int var1, int var2);

    public abstract void simulationCreated(SimulationInfo var1);

    public abstract void simulationCreated(SimulationInfo var1, int var2);

    public abstract void simulationRemoved(int var1, String var2);

    public abstract void simulationJoined(int var1, int var2, int var3);

    public abstract void simulationLocked(int var1, int var2);

    public abstract void simulationStarted(int var1, String var2, int var3);

    public abstract void simulationStopped(int var1, int var2, boolean var3);

    public abstract void sendChatMessage(long var1, String var3);
}

