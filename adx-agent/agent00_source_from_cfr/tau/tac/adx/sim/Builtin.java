/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.sim;

import java.util.Random;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.is.EventWriter;
import tau.tac.adx.sim.TACAdxSimulation;

public abstract class Builtin
extends Agent
implements TimeListener {
    private final String baseConfigName;
    private ConfigManager config;
    private String configName;
    private TACAdxSimulation simulation;
    private int index;

    protected Builtin(String baseConfigName) {
        this.baseConfigName = baseConfigName;
    }

    protected TACAdxSimulation getSimulation() {
        return this.simulation;
    }

    protected void setConfig(ConfigManager config, String configName) {
        if (this.config != null) {
            throw new IllegalStateException("config already set");
        }
        this.config = config;
        this.configName = configName;
    }

    protected int getIndex() {
        return this.index;
    }

    @Override
    protected final void simulationSetup() {
    }

    public final void simulationSetup(TACAdxSimulation simulation, int index) {
        this.index = index;
        this.simulation = simulation;
        this.config = simulation.getConfig();
        this.configName = this.getName();
        this.setup();
    }

    @Override
    protected final void simulationStopped() {
        this.stopped();
    }

    @Override
    protected final void simulationFinished() {
        try {
            this.shutdown();
        }
        finally {
            this.simulation = null;
        }
    }

    protected String getProperty(String name) {
        return this.getProperty(name, null);
    }

    protected String getProperty(String name, String defaultValue) {
        String value = this.config.getProperty(String.valueOf(this.baseConfigName) + this.configName + '.' + name);
        if (value == null) {
            value = this.config.getProperty(String.valueOf(this.baseConfigName) + name, defaultValue);
        }
        return value;
    }

    protected String[] getPropertyAsArray(String name) {
        return this.getPropertyAsArray(name, null);
    }

    protected String[] getPropertyAsArray(String name, String defaultValue) {
        String[] value = this.config.getPropertyAsArray(String.valueOf(this.baseConfigName) + this.configName + '.' + name);
        if (value == null) {
            value = this.config.getPropertyAsArray(String.valueOf(this.baseConfigName) + name, defaultValue);
        }
        return value;
    }

    protected int getPropertyAsInt(String name, int defaultValue) {
        String property = String.valueOf(this.baseConfigName) + name;
        int value = this.config.getPropertyAsInt(property, defaultValue);
        property = String.valueOf(this.baseConfigName) + this.configName + '.' + name;
        value = this.config.getPropertyAsInt(property, value);
        return value;
    }

    protected int[] getPropertyAsIntArray(String name) {
        return this.getPropertyAsIntArray(name, null);
    }

    protected int[] getPropertyAsIntArray(String name, String defaultValue) {
        String[] value = this.getPropertyAsArray(name, defaultValue);
        if (value != null) {
            int[] intValue = new int[value.length];
            int i = 0;
            int n = value.length;
            while (i < n) {
                intValue[i] = Integer.parseInt(value[i]);
                ++i;
            }
            return intValue;
        }
        return null;
    }

    protected long getPropertyAsLong(String name, long defaultValue) {
        String property = String.valueOf(this.baseConfigName) + name;
        long value = this.config.getPropertyAsLong(property, defaultValue);
        property = String.valueOf(this.baseConfigName) + this.configName + '.' + name;
        value = this.config.getPropertyAsLong(property, value);
        return value;
    }

    protected float getPropertyAsFloat(String name, float defaultValue) {
        String property = String.valueOf(this.baseConfigName) + name;
        float value = this.config.getPropertyAsFloat(property, defaultValue);
        property = String.valueOf(this.baseConfigName) + this.configName + '.' + name;
        value = this.config.getPropertyAsFloat(property, value);
        return value;
    }

    protected double getPropertyAsDouble(String name, double defaultValue) {
        String property = String.valueOf(this.baseConfigName) + name;
        double value = this.config.getPropertyAsDouble(property, defaultValue);
        property = String.valueOf(this.baseConfigName) + this.configName + '.' + name;
        value = this.config.getPropertyAsDouble(property, value);
        return value;
    }

    protected int getNumberOfAdvertisers() {
        return this.simulation.getNumberOfAdvertisers();
    }

    protected Random getRandom() {
        return this.simulation.getRandom();
    }

    protected String getAgentName(String agentAddress) {
        return this.simulation.getAgentName(agentAddress);
    }

    protected EventWriter getEventWriter() {
        return this.simulation.getEventWriter();
    }

    protected void sendEvent(String message) {
        this.simulation.getEventWriter().dataUpdated(this.index, 1, message);
    }

    protected void sendWarningEvent(String message) {
        this.simulation.getEventWriter().dataUpdated(this.index, 2, message);
    }

    protected String[] getAdvertiserAddresses() {
        return this.simulation.getAdvertiserAddresses();
    }

    protected String[] getAdxAdvertiserAddresses() {
        return this.simulation.getAdxAdvertiserAddresses();
    }

    final void sendMessage(String sender, String receiver, Transportable content) {
    }

    protected abstract void setup();

    protected abstract void stopped();

    protected abstract void shutdown();
}

