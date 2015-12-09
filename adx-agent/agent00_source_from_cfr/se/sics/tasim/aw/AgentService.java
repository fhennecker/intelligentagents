/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.aw;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;

public abstract class AgentService {
    private final Agent agent;
    private String name;
    private String address;

    protected AgentService(Agent agent, String name) {
        if (agent == null || name == null) {
            throw new NullPointerException();
        }
        this.agent = agent;
        this.address = this.name = name;
    }

    protected void initializeAgent() {
        this.agent.init(this);
    }

    protected void simulationSetup(String address) {
        if (address != null) {
            this.address = address;
        }
        this.agent.simulationSetup();
    }

    protected void simulationStopped() {
        this.agent.simulationStopped();
    }

    protected void simulationFinished() {
        this.agent.simulationFinished();
    }

    protected abstract void addTimeListener(TimeListener var1);

    protected abstract void removeTimeListener(TimeListener var1);

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public Agent getAgent() {
        return this.agent;
    }

    protected abstract long getServerTime();

    protected void sendMessage(Message message) {
        if (this.address == null) {
            throw new IllegalStateException("not initialized");
        }
        String sender = message.getSender();
        if (sender == null) {
            message.setSender(this.address);
        } else if (!sender.equals(this.address)) {
            throw new SecurityException("Can not send message from other than self: Self=" + this.address + ", Sender=" + sender);
        }
        this.deliverToServer(message);
    }

    protected abstract void deliverToServer(Message var1);

    protected void sendToRole(int role, Transportable content) {
        if (this.address == null) {
            throw new IllegalStateException("not initialized");
        }
        this.deliverToServer(role, content);
    }

    protected abstract void deliverToServer(int var1, Transportable var2);

    protected void deliverToAgent(Message message) {
        this.agent.messageReceived(message);
    }
}

