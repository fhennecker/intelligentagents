/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.aw;

import java.util.Enumeration;
import java.util.Hashtable;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.AgentService;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;

public abstract class Agent {
    public static final String COORDINATOR = "coordinator";
    public static final String ADMIN = "admin";
    private static int lastID = 0;
    private AgentService service;

    private synchronized int generateNextID() {
        return lastID++;
    }

    protected Agent() {
    }

    final void init(AgentService service) {
        if (this.service != null) {
            throw new IllegalStateException("already initialized");
        }
        this.service = service;
    }

    public String getName() {
        return this.service.getName();
    }

    public String getAddress() {
        return this.service.getAddress();
    }

    protected long getServerTime() {
        return this.service.getServerTime();
    }

    protected int getNextID() {
        return this.generateNextID();
    }

    protected void addTimeListener(TimeListener listener) {
        this.service.addTimeListener(listener);
    }

    protected void removeTimeListener(TimeListener listener) {
        this.service.removeTimeListener(listener);
    }

    protected void sendMessage(Message message) {
        this.service.sendMessage(message);
    }

    protected void sendMessage(String receiver, Transportable content) {
        this.service.sendMessage(new Message(receiver, content));
    }

    protected void sendMessages(Hashtable messageTable) {
        Enumeration enumeration = messageTable.keys();
        while (enumeration.hasMoreElements()) {
            String receiver = (String)enumeration.nextElement();
            Transportable content = (Transportable)messageTable.get(receiver);
            this.sendMessage(receiver, content);
        }
    }

    protected void sendToRole(int role, Transportable content) {
        this.service.sendToRole(role, content);
    }

    protected abstract void messageReceived(Message var1);

    protected abstract void simulationSetup();

    protected void simulationStopped() {
    }

    protected abstract void simulationFinished();
}

