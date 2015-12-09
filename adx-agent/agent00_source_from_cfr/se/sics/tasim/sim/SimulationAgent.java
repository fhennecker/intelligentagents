/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import com.botbox.util.ArrayUtils;
import com.botbox.util.ThreadPool;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.AgentService;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.sim.AgentChannel;
import se.sics.tasim.sim.MessageListener;
import se.sics.tasim.sim.Simulation;

public class SimulationAgent
extends AgentService {
    private Simulation simulation;
    private boolean isRunning = false;
    private boolean hasAgentBeenActive = false;
    private boolean isBlocked = false;
    private int index;
    private int role;
    private int participantID = -1;
    private boolean isProxy = false;
    private AgentChannel channel;
    private MessageListener[] messageListeners;

    public SimulationAgent(Agent agent, String name) {
        super(agent, name);
    }

    final void setup(Simulation simulation, int index, String address, int role, int participantID) {
        if (simulation == null || address == null) {
            throw new NullPointerException();
        }
        this.index = index;
        this.simulation = simulation;
        this.role = role;
        this.participantID = participantID;
        this.isRunning = true;
        this.isBlocked = false;
        this.initializeAgent();
        this.simulationSetup(address);
    }

    final void stop() {
        try {
            this.simulationStopped();
        }
        finally {
            this.isRunning = false;
        }
    }

    final void shutdown() {
        try {
            this.simulationFinished();
        }
        finally {
            AgentChannel channel = this.channel;
            this.channel = null;
            if (channel != null) {
                channel.removeProxyAgent(this);
            }
            this.simulation = null;
        }
    }

    int getParticipantID() {
        return this.participantID;
    }

    boolean isProxy() {
        return this.isProxy;
    }

    void setProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    boolean isBlocked() {
        return this.isBlocked;
    }

    void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    boolean hasAgentChannel() {
        if (this.channel != null) {
            return true;
        }
        return false;
    }

    AgentChannel getAgentChannel() {
        return this.channel;
    }

    synchronized void setAgentChannel(AgentChannel channel, boolean recover) {
        if (channel != this.channel) {
            if (!this.isProxy) {
                throw new IllegalStateException("proxy mode not supported in " + this.getName());
            }
            if (channel == null) {
                throw new NullPointerException();
            }
            if (this.channel != null) {
                this.channel.removeProxyAgent(this);
            }
            this.channel = channel;
            channel.setProxyAgent(this);
            channel.setSimulationThreadPool(this.simulation.getSimulationThreadPool());
            if (this.simulation != null && recover) {
                this.simulation.requestAgentRecovery(this);
            }
        }
    }

    synchronized void removeAgentChannel(AgentChannel channel) {
        if (channel == this.channel) {
            this.channel = null;
        }
    }

    public boolean isSupported(String name) {
        AgentChannel channel = this.channel;
        return channel != null ? channel.isSupported(name) : false;
    }

    public void requestPing() {
        AgentChannel channel = this.channel;
        if (channel != null) {
            channel.requestPing();
        }
    }

    public int getPingCount() {
        AgentChannel channel = this.channel;
        return channel != null ? channel.getPingCount() : 0;
    }

    public long getLastResponseTime() {
        AgentChannel channel = this.channel;
        return channel != null ? channel.getLastResponseTime() : 0;
    }

    public long getAverageResponseTime() {
        AgentChannel channel = this.channel;
        return channel != null ? channel.getAverageResponseTime() : 0;
    }

    public boolean hasAgentBeenActive() {
        return this.hasAgentBeenActive;
    }

    public int getIndex() {
        return this.index;
    }

    public int getRole() {
        return this.role;
    }

    @Override
    protected long getServerTime() {
        return this.simulation.getServerTime();
    }

    @Override
    protected void addTimeListener(TimeListener listener) {
        this.simulation.addTimeListener(listener);
    }

    @Override
    protected void removeTimeListener(TimeListener listener) {
        this.simulation.removeTimeListener(listener);
    }

    protected void deliverFromAgent(Message message) {
        super.sendMessage(message);
    }

    @Override
    protected void deliverToServer(Message message) {
        if (this.isRunning) {
            this.hasAgentBeenActive = true;
            this.fireMessageSent(message);
            this.simulation.deliverMessage(message);
        }
    }

    @Override
    protected void deliverToServer(int role, Transportable content) {
        if (this.isRunning) {
            this.hasAgentBeenActive = true;
            this.fireMessageSent(role, content);
            this.simulation.deliverMessageToRole(this, role, content);
        }
    }

    final void messageReceived(Simulation simulation, Message message) {
        if (simulation != this.simulation) {
            throw new SecurityException("message from wrong simulation");
        }
        this.fireMessageReceived(message);
        if (!this.isBlocked) {
            if (this.isProxy) {
                AgentChannel channel = this.channel;
                if (channel != null) {
                    channel.deliverToAgent(message);
                }
            } else {
                this.deliverToAgent(message);
            }
        }
    }

    public synchronized void addMessageListener(MessageListener listener) {
        this.messageListeners = (MessageListener[])ArrayUtils.add(MessageListener.class, this.messageListeners, listener);
    }

    public synchronized void removeMessageListener(MessageListener listener) {
        this.messageListeners = (MessageListener[])ArrayUtils.remove(this.messageListeners, listener);
    }

    private void fireMessageReceived(Message message) {
        MessageListener[] listeners = this.messageListeners;
        if (listeners != null) {
            String sender = message.getSender();
            Transportable content = message.getContent();
            int i = 0;
            int n = listeners.length;
            while (i < n) {
                listeners[i].messageReceived(this, sender, content);
                ++i;
            }
        }
    }

    private void fireMessageSent(Message message) {
        MessageListener[] listeners = this.messageListeners;
        if (listeners != null) {
            String receiver = message.getReceiver();
            Transportable content = message.getContent();
            int i = 0;
            int n = listeners.length;
            while (i < n) {
                listeners[i].messageSent(this, receiver, content);
                ++i;
            }
        }
    }

    private void fireMessageSent(int role, Transportable content) {
        MessageListener[] listeners = this.messageListeners;
        if (listeners != null) {
            int i = 0;
            int n = listeners.length;
            while (i < n) {
                listeners[i].messageSent(this, role, content);
                ++i;
            }
        }
    }

    static int indexOf(SimulationAgent[] array, int participantID) {
        if (array != null) {
            int i = 0;
            int n = array.length;
            while (i < n) {
                SimulationAgent a = array[i];
                if (a.participantID == participantID) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    protected void finalize() throws Throwable {
        Logger.global.info("SIMULATIONAGENT " + this.getName() + " (" + this.getAddress() + ',' + this.participantID + ") IS BEING GARBAGED");
        super.finalize();
    }
}

