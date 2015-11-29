/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim.server;

import com.botbox.util.ArrayUtils;
import com.botbox.util.ThreadPool;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;
import se.sics.isl.inet.InetServer;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.ContextFactory;
import se.sics.isl.util.AMonitor;
import se.sics.isl.util.AdminMonitor;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.props.AdminContent;
import se.sics.tasim.sim.AgentChannel;
import se.sics.tasim.sim.Gateway;
import se.sics.tasim.sim.server.TACTChannel;

public class TACTGateway
extends Gateway
implements AMonitor {
    private static final String CONF = "sim.gateway.";
    private static final Logger log = Logger.getLogger(TACTGateway.class.getName());
    private static final String STATUS_NAME = "TACT";
    private Context transportContext;
    private ThreadPool threadPool;
    private TACTServer server;
    private TACTChannel[] agentConnections;
    private boolean isRunning = true;

    @Override
    protected void initGateway() {
        this.transportContext = AdminContent.createContext();
        ConfigManager config = this.getConfig();
        String name = this.getName();
        String contextFactoryClassName = config.getProperty("sim.gateway." + name + ".contextFactory", config.getProperty("server.contextFactory"));
        try {
            ContextFactory contextFactory = (ContextFactory)Class.forName(contextFactoryClassName).newInstance();
            this.transportContext = contextFactory.createContext(this.transportContext);
        }
        catch (ClassNotFoundException e) {
            log.severe("server " + this.getName() + " unable to load context factory: Class not found");
        }
        catch (InstantiationException e) {
            log.severe("server " + this.getName() + " unable to load context factory: Class cannot be instantiated");
        }
        catch (IllegalAccessException e) {
            log.severe("server " + this.getName() + " unable to load context factory: Illegal access exception");
        }
    }

    @Override
    protected void startGateway() throws IOException {
        if (!this.isRunning || this.server != null) {
            return;
        }
        ConfigManager config = this.getConfig();
        String name = this.getName();
        String host = config.getProperty("sim.gateway." + name + ".host", config.getProperty("server.host"));
        int port = config.getPropertyAsInt("sim.gateway." + name + ".port", 6502);
        int minThreads = config.getPropertyAsInt("sim.gateway." + name + ".minThreads", 5);
        int maxThreads = config.getPropertyAsInt("sim.gateway." + name + ".maxThreads", 50);
        int maxIdleThreads = config.getPropertyAsInt("sim.gateway." + name + ".maxIdleThreads", 25);
        this.threadPool = ThreadPool.getThreadPool("viewer");
        this.threadPool.setMinThreads(minThreads);
        this.threadPool.setMaxThreads(maxThreads);
        this.threadPool.setMaxIdleThreads(maxIdleThreads);
        this.threadPool.setInterruptThreadsAfter(120000);
        this.server = new TACTServer(this, "tact", host, port);
        this.server.start();
        log.info("TACT Server started at " + this.server.getBindAddress());
        AdminMonitor adminMonitor = AdminMonitor.getDefault();
        if (adminMonitor != null) {
            adminMonitor.addMonitor("TACT", this);
        }
    }

    @Override
    protected void stopGateway() {
        TACTChannel[] connections;
        if (!this.isRunning) {
            return;
        }
        this.isRunning = false;
        TACTServer server = this.server;
        if (server != null) {
            this.server = null;
            server.stop();
        }
        if ((connections = this.agentConnections) != null) {
            int i = 0;
            int n = connections.length;
            while (i < n) {
                connections[i].close();
                ++i;
            }
        }
    }

    @Override
    public String getStatus(String propertyName) {
        if (propertyName != "TACT") {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("--- TACT Gateway ---");
        TACTChannel[] connections = this.agentConnections;
        if (connections != null) {
            int i = 0;
            int n = connections.length;
            while (i < n) {
                TACTChannel channel = connections[i];
                sb.append('\n').append(i + 1).append(": ").append(channel.getName()).append(" (").append(channel.getAddress()).append(',').append(channel.getRemoteHost()).append(':').append(channel.getRemotePort()).append(')');
                ++i;
            }
        } else {
            sb.append("\n<no connections>");
        }
        return sb.toString();
    }

    final ThreadPool getThreadPool() {
        return this.threadPool;
    }

    final Context getContext() {
        return this.transportContext;
    }

    final void loginAgentChannel(TACTChannel channel, String name, String password) {
        super.loginAgentChannel(channel, name, password);
    }

    public synchronized void addAgentConnection(TACTChannel connection) {
        this.agentConnections = (TACTChannel[])ArrayUtils.add(TACTChannel.class, this.agentConnections, connection);
    }

    public synchronized void removeAgentConnection(TACTChannel connection) {
        this.agentConnections = (TACTChannel[])ArrayUtils.remove(this.agentConnections, connection);
    }

    private static class TACTServer
    extends InetServer {
        private TACTGateway gateway;

        public TACTServer(TACTGateway gateway, String name, String host, int port) {
            super(name, host, port);
            this.gateway = gateway;
        }

        @Override
        protected void serverStarted() {
        }

        @Override
        protected void serverShutdown() {
            if (this.gateway.isRunning) {
                log.severe("server " + this.getName() + " died!!!");
            }
        }

        @Override
        protected void newConnection(Socket socket) throws IOException {
            new se.sics.tasim.sim.server.TACTChannel(this.gateway, socket);
        }
    }

}

