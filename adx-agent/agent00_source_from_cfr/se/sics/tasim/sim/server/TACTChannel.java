/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim.server;

import com.botbox.util.ThreadPool;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.tact.TACTConnection;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.AdminContent;
import se.sics.tasim.props.Alert;
import se.sics.tasim.props.Ping;
import se.sics.tasim.sim.AgentChannel;
import se.sics.tasim.sim.server.TACTGateway;

final class TACTChannel
extends AgentChannel {
    private static final Logger log = Logger.getLogger(TACTChannel.class.getName());
    private static final String MESSAGE_NAME = new Message().getTransportName();
    private static int channelCounter = 0;
    private BinaryTransportReader reader = new BinaryTransportReader();
    private BinaryTransportWriter writer = new BinaryTransportWriter();
    private final TACTGateway gateway;
    private final TACTConnection connection;
    private boolean isActiveOrdersSupported = false;
    private boolean isPingSupported = false;

    TACTChannel(TACTGateway gateway, Socket socket) throws IOException {
        this.gateway = gateway;
        this.reader.setContext(gateway.getContext());
        this.connection = new TACTConnection(String.valueOf(gateway.getName()) + '-' + ++channelCounter, socket){

            @Override
            protected void connectionOpened() {
            }

            @Override
            protected void connectionClosed() {
                TACTChannel.this.gateway.removeAgentConnection(TACTChannel.this);
                TACTChannel.this.close();
            }

            @Override
            protected void dataRead(byte[] buffer, int offset, int length) {
                TACTChannel.this.dataRead(buffer, offset, length);
            }
        };
        this.connection.setThreadPool(gateway.getThreadPool());
        this.connection.start();
        gateway.addAgentConnection(this);
    }

    @Override
    protected void setSimulationThreadPool(ThreadPool threadPool) {
        if (threadPool == null) {
            this.connection.setThreadPool(this.gateway.getThreadPool());
        } else {
            this.connection.setThreadPool(threadPool);
        }
    }

    @Override
    public boolean isSupported(String name) {
        if ("activeOrders".equals(name)) {
            return this.isActiveOrdersSupported;
        }
        if ("ping".equals(name)) {
            return this.isPingSupported;
        }
        return false;
    }

    @Override
    public synchronized void addTransportConstant(String name) {
        this.writer.addConstant(name);
    }

    protected String getAddress() {
        return this.connection.getName();
    }

    @Override
    public String getRemoteHost() {
        return this.connection.getRemoteHost();
    }

    public int getRemotePort() {
        return this.connection.getRemotePort();
    }

    @Override
    protected boolean sendPingRequest() {
        if (this.isPingSupported && !super.isClosed()) {
            this.deliverToAgent(new Message("admin", this.getName(), new Ping()));
            return true;
        }
        return false;
    }

    @Override
    protected void closeChannel() {
        this.connection.close();
    }

    @Override
    public void deliverToAgent(Message message) {
        if (!this.connection.isClosed()) {
            byte[] messageData = this.getBytes(message);
            this.connection.write(messageData);
        }
    }

    private synchronized byte[] getBytes(Message message) {
        String node = message.getTransportName();
        this.writer.node(node);
        message.write(this.writer);
        this.writer.endNode(node);
        this.writer.finish();
        byte[] data = this.writer.getBytes();
        this.writer.clear();
        return data;
    }

    private void dataRead(byte[] buffer, int offset, int length) {
        Message message;
        this.reader.setMessage(buffer, offset, length);
        while ((message = this.parseMessage(this.reader)) != null) {
            try {
                this.deliverFromAgent(message);
                continue;
            }
            catch (Exception e) {
                log.log(Level.SEVERE, String.valueOf(this.connection.getName()) + " could not deliver message from agent: " + message, e);
            }
        }
    }

    private Message parseMessage(BinaryTransportReader reader) {
        block5 : {
            try {
                if (reader.nextNode(MESSAGE_NAME, false)) break block5;
                return null;
            }
            catch (Exception e) {
                log.log(Level.WARNING, "could not parse message", e);
                try {
                    reader.printMessage();
                }
                catch (ParseException var3_4) {
                    // empty catch block
                }
                return null;
            }
        }
        Message message = new Message();
        reader.enterNode();
        message.read(reader);
        reader.exitNode();
        return message;
    }

    @Override
    protected void deliverFromAgent(Message message) {
        Transportable content = message.getContent();
        if (super.isClosed()) {
            AdminContent reply = new AdminContent(4, 2);
            if (content.getClass() == AdminContent.class) {
                AdminContent admin = (AdminContent)content;
                int type = admin.getType();
                if (type == 4) {
                    String clientVersion = admin.getAttribute("client.version");
                    String serverVersion = this.gateway.getServerVersion();
                    if (ConfigManager.compareVersion("0.7", clientVersion) > 0) {
                        String messageText = "You seem to use an incompatible version of AgentWare (" + clientVersion + ").\n" + "During alpha testing the system might change between each\n" + "release and you need an AgentWare compatible with \n" + "the current server version (" + serverVersion + ").";
                        Alert alert = new Alert("Wrong Version", messageText);
                        this.deliverToAgent(message.createReply(alert));
                        reply.setError(1, "incompatible client version");
                    } else {
                        if (ConfigManager.compareVersion(clientVersion, "0.9.6") >= 0) {
                            this.isActiveOrdersSupported = true;
                            this.writer.setSupported("constants", true);
                            this.writer.setSupported("tables", true);
                        } else {
                            this.isActiveOrdersSupported = false;
                        }
                        this.isPingSupported = ConfigManager.compareVersion(clientVersion, "0.9.7") >= 0;
                        String name = admin.getAttribute("name");
                        String password = admin.getAttribute("password");
                        try {
                            this.gateway.loginAgentChannel(this, name, password);
                            reply.setError(0);
                            this.connection.setUserName(name);
                            log.info("user " + name + " logged in as " + this.connection.getName() + " with version " + clientVersion + " from " + this.connection.getRemoteHost());
                            reply.setAttribute("server.version", serverVersion);
                        }
                        catch (Exception e) {
                            log.log(Level.WARNING, "could not login user " + name, e);
                            reply.setError(2, e.getMessage());
                        }
                    }
                } else if (type == 8) {
                    reply = new AdminContent(8);
                }
            }
            this.deliverToAgent(message.createReply(reply));
            if (reply.getType() == 8) {
                this.close();
            }
        } else if (content instanceof Ping) {
            Ping ping = (Ping)content;
            if (ping.isPong()) {
                this.pongReceived();
            } else if (ping.isPing()) {
                this.deliverToAgent(message.createReply(ping.createPong()));
            }
        } else {
            super.deliverFromAgent(message);
        }
    }

}

