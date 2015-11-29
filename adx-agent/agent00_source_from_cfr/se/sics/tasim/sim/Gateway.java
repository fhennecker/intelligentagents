/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import java.io.IOException;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.sim.Admin;
import se.sics.tasim.sim.AgentChannel;

public abstract class Gateway {
    private Admin admin;
    private String name;

    protected Gateway() {
    }

    final void init(Admin admin, String name) throws IllegalConfigurationException {
        if (admin == null) {
            throw new NullPointerException();
        }
        this.admin = admin;
        this.name = name;
        this.initGateway();
    }

    public String getName() {
        return this.name;
    }

    public String getServerVersion() {
        return "0.8.19";
    }

    final void start() throws IOException {
        this.startGateway();
    }

    final void stop() {
        this.stopGateway();
    }

    protected abstract void initGateway() throws IllegalConfigurationException;

    protected abstract void startGateway() throws IOException;

    protected abstract void stopGateway();

    protected ConfigManager getConfig() {
        return this.admin.getConfig();
    }

    protected void loginAgentChannel(AgentChannel channel, String name, String password) {
        channel.init(this.admin, name, password);
    }
}

