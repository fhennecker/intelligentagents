/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.tact.TACTConnection;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.Context;
import se.sics.tasim.is.common.BlockingViewerServer;
import se.sics.tasim.is.common.SimServer;

public class BlockingViewerChannel
extends TACTConnection {
    private static final Logger log = Logger.getLogger(BlockingViewerChannel.class.getName());
    private static int channelCounter = 0;
    private final BlockingViewerServer viewerServer;
    private final BinaryTransportReader reader = new BinaryTransportReader();
    private SimServer simServer;

    public BlockingViewerChannel(BlockingViewerServer server, Socket socket, Context context) throws IOException {
        super(String.valueOf(server.getName()) + '-' + ++channelCounter, socket);
        this.viewerServer = server;
        this.reader.setContext(context);
        this.viewerServer.addViewerConnection(this);
    }

    @Override
    protected void connectionOpened() {
    }

    @Override
    protected void connectionClosed() {
        this.viewerServer.removeViewerConnection(this);
        SimServer server = this.simServer;
        if (server != null) {
            this.simServer = null;
            server.removeViewerConnection(this);
        }
    }

    @Override
    protected void dataRead(byte[] buffer, int offset, int length) {
        block5 : {
            this.reader.setMessage(buffer, offset, length);
            SimServer server = this.simServer;
            if (server == null) {
                try {
                    if (!this.reader.nextNode("auth", false)) break block5;
                    String serverName = this.reader.getAttribute("serverName");
                    String userName = this.reader.getAttribute("userName");
                    server = this.viewerServer.getSimServer(this, serverName);
                    if (server != null) {
                        this.setUserName(userName);
                        this.simServer = server;
                        log.finer("logged in " + userName + " as " + this.getName() + " from " + this.getRemoteHost());
                        server.addViewerConnection(this);
                        break block5;
                    }
                    log.severe(String.valueOf(this.getName()) + " could not login " + userName + " (unknown server " + serverName + ')');
                    this.close();
                }
                catch (Exception e) {
                    log.log(Level.SEVERE, String.valueOf(this.getName()) + " could not handle authentication", e);
                    this.close();
                }
            } else {
                server.viewerDataReceived(this, this.reader);
            }
        }
    }
}

