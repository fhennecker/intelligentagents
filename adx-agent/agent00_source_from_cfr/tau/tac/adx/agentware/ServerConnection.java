/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.agentware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.AdminContent;
import se.sics.tasim.props.Alert;
import se.sics.tasim.props.Ping;
import tau.tac.adx.agentware.MessageSender;
import tau.tac.adx.agentware.SimClient;

public class ServerConnection
implements Runnable {
    private static final Logger log = Logger.getLogger(ServerConnection.class.getName());
    private static final byte[] TACT_HEADER;
    private static int connectionCounter;
    private final SimClient simClient;
    private int id = -1;
    private long delayInMillis = 0;
    private MessageSender messageSender;
    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;
    private final BinaryTransportWriter transportWriter = new BinaryTransportWriter();
    private final BinaryTransportReader transportReader = new BinaryTransportReader();
    private boolean isAuthenticated = false;

    static {
        byte[] arrby = new byte[8];
        arrby[0] = 84;
        arrby[1] = 65;
        arrby[2] = 67;
        arrby[3] = 84;
        TACT_HEADER = arrby;
        connectionCounter = 0;
    }

    public ServerConnection(SimClient simClient, long delayInMillis) {
        this.delayInMillis = delayInMillis;
        this.simClient = simClient;
        this.transportReader.setContext(simClient.getContext());
    }

    public int getID() {
        return this.id;
    }

    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
        if (isAuthenticated) {
            log.finer("(" + this.id + ") successfully logged in as " + this.simClient.getUserName());
        }
    }

    public void setTransportSupported(String name) {
        this.transportWriter.setSupported(name, true);
    }

    public boolean sendMessage(Message msg) {
        if (this.messageSender == null) {
            return false;
        }
        return this.messageSender.addMessage(msg);
    }

    public void open() {
        if (this.id > 0) {
            throw new IllegalStateException("already opened");
        }
        this.id = ++connectionCounter;
        new Thread((Runnable)this, "Connection." + this.id).start();
    }

    public void close() {
        AdminContent content = new AdminContent(8);
        Message msg = new Message(this.simClient.getUserName(), "admin", content);
        if (!this.sendMessage(msg)) {
            this.disconnect();
            this.simClient.connectionClosed(this);
        }
    }

    private boolean connect() {
        try {
            String host = this.simClient.getServerHost();
            int port = this.simClient.getServerPort();
            log.fine("(" + this.id + ") connecting to server " + host + " at port " + port);
            this.socket = new Socket(host, port);
            this.input = new DataInputStream(this.socket.getInputStream());
            this.output = new DataOutputStream(this.socket.getOutputStream());
            this.output.write(TACT_HEADER);
            log.fine("(" + this.id + ") connected to server " + host);
            this.messageSender = new MessageSender(this, "Sender." + this.id);
            return true;
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "(" + this.id + ") connection to server failed", e);
            this.disconnect();
            return false;
        }
    }

    private boolean disconnect() {
        if (this.socket != null) {
            try {
                try {
                    log.fine("(" + this.id + ") disconnected from server " + this.simClient.getServerHost());
                    if (this.output != null) {
                        this.output.close();
                    }
                    if (this.input != null) {
                        this.input.close();
                    }
                    this.socket.close();
                }
                catch (Exception e) {
                    log.log(Level.SEVERE, "(" + this.id + ") could not close connection", e);
                    this.socket = null;
                    this.output = null;
                    this.input = null;
                    this.isAuthenticated = false;
                    if (this.messageSender != null) {
                        this.messageSender.close();
                        this.messageSender = null;
                    }
                }
            }
            finally {
                this.socket = null;
                this.output = null;
                this.input = null;
                this.isAuthenticated = false;
                if (this.messageSender != null) {
                    this.messageSender.close();
                    this.messageSender = null;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        if (this.delayInMillis > 0) {
            try {
                Thread.sleep(this.delayInMillis);
            }
            catch (Exception var1_1) {
                // empty catch block
            }
        }
        do {
            if (this.connect()) continue;
            this.simClient.showWarning("Connection Failed", "Could not connect to " + this.simClient.getServerHost() + " (will retry in 30 seconds)");
            try {
                Thread.sleep(30000);
                continue;
            }
            catch (Exception var1_3) {
                // empty catch block
            }
        } while (this.messageSender == null);
        this.simClient.connectionOpened(this);
        try {
            try {
                byte[] buffer = new byte[8192];
                while (this.socket != null) {
                    int size = this.input.readInt();
                    if (size > buffer.length) {
                        buffer = new byte[size + 8192];
                    }
                    this.input.readFully(buffer, 0, size);
                    Message msg = this.parseMessage(buffer, 0, size);
                    if (msg == null) continue;
                    Transportable content = msg.getContent();
                    if (content instanceof AdminContent) {
                        AdminContent admin = (AdminContent)content;
                        if (admin.getType() == 8) {
                            if (log.isLoggable(Level.FINEST)) {
                                log.finest("(" + this.id + ") received " + msg);
                            }
                            this.disconnect();
                            this.simClient.connectionClosed(this);
                            continue;
                        }
                        this.simClient.adminFromServer(this, admin);
                        continue;
                    }
                    if (content instanceof Alert) {
                        Alert alert = (Alert)content;
                        this.simClient.alertFromServer(this, alert);
                        continue;
                    }
                    if (content instanceof Ping) {
                        this.sendMessage(msg.createReply(new Ping(1)));
                        continue;
                    }
                    this.simClient.messageFromServer(this, msg);
                }
            }
            catch (Throwable e) {
                log.log(Level.SEVERE, "(" + this.id + ") could not read", e);
                if (this.disconnect()) {
                    this.simClient.connectionClosed(this);
                }
            }
        }
        finally {
            if (this.disconnect()) {
                this.simClient.connectionClosed(this);
            }
        }
    }

    private Message parseMessage(byte[] buffer, int offset, int size) {
        try {
            Message msg = new Message();
            this.transportReader.setMessage(buffer, offset, size);
            if (this.transportReader.nextNode(msg.getTransportName(), false)) {
                this.transportReader.enterNode();
                msg.read(this.transportReader);
                return msg;
            }
            log.warning("(" + this.id + ") no message found in received data");
            return null;
        }
        catch (Exception e) {
            log.log(Level.WARNING, "(" + this.id + ") could not parse message", e);
            return null;
        }
    }

    boolean deliverMessage(Message msg) {
        DataOutputStream output = this.output;
        if (output == null) {
            log.warning("(" + this.id + ") could not send message (closed connection) " + msg);
            return false;
        }
        if (log.isLoggable(Level.FINEST)) {
            log.finest("(" + this.id + ") sending " + msg);
        }
        String node = msg.getTransportName();
        this.transportWriter.clear();
        this.transportWriter.node(node);
        msg.write(this.transportWriter);
        this.transportWriter.endNode(node);
        this.transportWriter.finish();
        try {
            output.writeInt(this.transportWriter.size());
            this.transportWriter.write(output);
            output.flush();
            return true;
        }
        catch (Exception e) {
            try {
                log.log(Level.SEVERE, "(" + this.id + ") could not send message to server", e);
                this.simClient.showWarning("Connection Failed", "could not send message to server");
                if (this.disconnect()) {
                    this.simClient.connectionClosed(this);
                }
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "(" + this.id + ") could not generate message " + msg, e);
            }
        }
        return false;
    }
}

