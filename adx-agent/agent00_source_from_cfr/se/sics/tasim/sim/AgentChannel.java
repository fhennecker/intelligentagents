/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import com.botbox.util.ThreadPool;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.AdminContent;
import se.sics.tasim.sim.Admin;
import se.sics.tasim.sim.SimulationAgent;

public abstract class AgentChannel {
    public static final String ACTIVE_ORDERS = "activeOrders";
    public static final String PING = "ping";
    private Admin admin;
    private int userID;
    private String name;
    private SimulationAgent proxy;
    private boolean isClosed = true;
    private long pingRequested = 0;
    private long lastResponeTime = 0;
    private long totalResponseTime = 0;
    private int pingCount = 0;

    protected AgentChannel() {
    }

    final void init(Admin admin, String name, String password) {
        if (this.admin != null) {
            throw new IllegalStateException("already initialized");
        }
        this.admin = admin;
        this.name = name;
        this.userID = admin.loginAgentChannel(this, password);
        this.isClosed = false;
        admin.agentChannelAvailable(this);
    }

    final void setProxyAgent(SimulationAgent proxy) {
        this.proxy = proxy;
    }

    final void removeProxyAgent(SimulationAgent proxy) {
        if (this.proxy == proxy) {
            this.proxy = null;
            this.setSimulationThreadPool(null);
        }
    }

    public final int getUserID() {
        return this.userID;
    }

    public final String getName() {
        return this.name;
    }

    public abstract boolean isSupported(String var1);

    public void addTransportConstant(String name) {
    }

    public void requestPing() {
        if (this.pingRequested <= 0) {
            this.pingRequested = System.currentTimeMillis();
            this.sendPingRequest();
        }
    }

    protected abstract boolean sendPingRequest();

    protected void pongReceived() {
        long requested = this.pingRequested;
        this.pingRequested = 0;
        if (requested > 0) {
            long responseTime;
            this.lastResponeTime = responseTime = System.currentTimeMillis() - requested;
            this.totalResponseTime += responseTime;
            ++this.pingCount;
        }
    }

    public int getPingCount() {
        return this.pingCount;
    }

    public long getLastResponseTime() {
        return this.lastResponeTime;
    }

    public long getAverageResponseTime() {
        return this.pingCount == 0 ? 0 : this.totalResponseTime / (long)this.pingCount;
    }

    public abstract String getRemoteHost();

    protected abstract void setSimulationThreadPool(ThreadPool var1);

    protected void deliverFromAgent(Message message) {
        String receiver = message.getReceiver();
        if (!this.isClosed) {
            if ("admin".equals(receiver)) {
                Transportable content = message.getContent();
                if (message.getSender() == null) {
                    message.setSender(this.name);
                }
                if (content instanceof AdminContent && ((AdminContent)content).getType() == 8) {
                    this.deliverToAgent(message.createReply(new AdminContent(8)));
                    this.close();
                } else {
                    this.admin.deliverMessageFromAgent(this, message);
                }
            } else {
                SimulationAgent proxy = this.proxy;
                if (proxy != null) {
                    proxy.deliverFromAgent(message);
                }
            }
        }
    }

    protected abstract void deliverToAgent(Message var1);

    public final boolean isClosed() {
        return this.isClosed;
    }

    public final void close() {
        if (!this.isClosed) {
            this.isClosed = true;
            this.admin.logoutAgentChannel(this);
            SimulationAgent proxy = this.proxy;
            if (proxy != null) {
                this.proxy = null;
                proxy.removeAgentChannel(this);
            }
        }
        this.closeChannel();
    }

    protected abstract void closeChannel();

    static int indexOf(AgentChannel[] array, int start, int end, int userID) {
        int i = start;
        while (i < end) {
            if (array[i].userID == userID) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}

