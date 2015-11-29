/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import com.botbox.util.ArrayUtils;
import com.botbox.util.ThreadPool;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;
import se.sics.isl.inet.InetServer;
import se.sics.isl.transport.Context;
import se.sics.isl.util.AMonitor;
import se.sics.isl.util.AdminMonitor;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.common.BlockingViewerChannel;
import se.sics.tasim.is.common.InfoServer;
import se.sics.tasim.is.common.SimServer;

public class BlockingViewerServer
extends InetServer
implements AMonitor {
    private static final Logger log = Logger.getLogger(BlockingViewerServer.class.getName());
    private static final String STATUS_NAME = "Viewer";
    private static final String CONF = "is.viewer.";
    private InfoServer infoServer;
    private Context transportContext;
    private BlockingViewerChannel[] viewerConnections;
    private ThreadPool viewerThreadPool;

    public BlockingViewerServer(InfoServer infoServer) {
        super("viewer", infoServer.getConfig().getProperty("is.viewer.host", infoServer.getConfig().getProperty("server.host")), infoServer.getConfig().getPropertyAsInt("is.viewer.port", 4042));
        ConfigManager config = infoServer.getConfig();
        this.infoServer = infoServer;
        this.transportContext = new Context("viewer");
        int minThreads = config.getPropertyAsInt("is.viewer.minThreads", 5);
        int maxThreads = config.getPropertyAsInt("is.viewer.maxThreads", 50);
        int maxIdleThreads = config.getPropertyAsInt("is.viewer.maxIdleThreads", 25);
        this.viewerThreadPool = ThreadPool.getThreadPool("viewer");
        this.viewerThreadPool.setMinThreads(minThreads);
        this.viewerThreadPool.setMaxThreads(maxThreads);
        this.viewerThreadPool.setMaxIdleThreads(maxIdleThreads);
        this.viewerThreadPool.setInterruptThreadsAfter(120000);
        AdminMonitor adminMonitor = AdminMonitor.getDefault();
        if (adminMonitor != null) {
            adminMonitor.addMonitor("Viewer", this);
        }
    }

    @Override
    protected void serverStarted() {
        log.info("viewer server started at " + this.getBindAddress());
    }

    @Override
    protected void serverShutdown() {
        BlockingViewerChannel[] connections;
        BlockingViewerServer blockingViewerServer = this;
        synchronized (blockingViewerServer) {
            connections = this.viewerConnections;
            this.viewerConnections = null;
        }
        if (connections != null) {
            int i = 0;
            int n = connections.length;
            while (i < n) {
                connections[i].close();
                ++i;
            }
        }
        log.severe("viewer server has closed");
        this.infoServer.serverClosed(this);
    }

    @Override
    protected void newConnection(Socket socket) throws IOException {
        BlockingViewerChannel channel = new BlockingViewerChannel(this, socket, this.transportContext);
        channel.setThreadPool(this.viewerThreadPool);
        channel.start();
    }

    SimServer getSimServer(BlockingViewerChannel connection, String serverName) {
        return this.infoServer.getSimServer(serverName);
    }

    synchronized void addViewerConnection(BlockingViewerChannel connection) {
        this.viewerConnections = (BlockingViewerChannel[])ArrayUtils.add(BlockingViewerChannel.class, this.viewerConnections, connection);
    }

    synchronized void removeViewerConnection(BlockingViewerChannel connection) {
        this.viewerConnections = (BlockingViewerChannel[])ArrayUtils.remove(this.viewerConnections, connection);
    }

    @Override
    public String getStatus(String propertyName) {
        if (propertyName != "Viewer") {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("--- Viewer Connections ---");
        BlockingViewerChannel[] connections = this.viewerConnections;
        if (connections != null) {
            int i = 0;
            int n = connections.length;
            while (i < n) {
                BlockingViewerChannel channel = connections[i];
                sb.append('\n').append(i + 1).append(": ").append(channel.getName()).append(" (").append(channel.getRemoteHost()).append(':').append(channel.getRemotePort()).append(')');
                ++i;
            }
        } else {
            sb.append("\n<no connections>");
        }
        return sb.toString();
    }
}

