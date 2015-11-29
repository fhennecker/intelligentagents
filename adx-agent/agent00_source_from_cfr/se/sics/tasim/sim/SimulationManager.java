/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import java.io.File;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.sim.Admin;
import se.sics.tasim.sim.Simulation;

public abstract class SimulationManager {
    private String name;
    private Admin admin;

    protected SimulationManager() {
    }

    final void init(Admin admin, String name) {
        if (name == null || admin == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.admin = admin;
        this.init();
    }

    protected String getName() {
        return this.name;
    }

    protected ConfigManager getConfig() {
        return this.admin.getConfig();
    }

    protected ConfigManager loadSimulationConfig(String simulationType) {
        this.checkSimulationType(simulationType);
        ConfigManager config = this.getConfig();
        String configFile = config.getProperty("manager." + this.getName() + '.' + simulationType + ".config", String.valueOf(simulationType) + "_sim.conf");
        config = new ConfigManager(config);
        config.loadConfiguration(new File(this.admin.getConfigDirectory(), configFile).getAbsolutePath());
        return config;
    }

    protected void checkSimulationType(String simulationType) {
        int i = 0;
        int n = simulationType.length();
        while (i < n) {
            char c = simulationType.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                throw new IllegalArgumentException("simulation type may only include letters and digits: " + simulationType);
            }
            ++i;
        }
    }

    protected void registerType(String type) {
        this.checkSimulationType(type);
        this.admin.addSimulationManager(type, this);
    }

    protected SimulationInfo createSimulationInfo(String type, String params, int length) {
        return new SimulationInfo(this.admin.getNextUniqueSimulationID(), type, params, length);
    }

    protected abstract void init();

    public Admin getAdmin() {
        return this.admin;
    }

    public abstract SimulationInfo createSimulationInfo(String var1, String var2);

    public abstract boolean join(int var1, int var2, SimulationInfo var3);

    public abstract String getSimulationRoleName(String var1, int var2);

    public abstract int getSimulationRoleID(String var1, String var2);

    public abstract int getSimulationLength(String var1, String var2);

    public abstract Simulation createSimulation(SimulationInfo var1);
}

