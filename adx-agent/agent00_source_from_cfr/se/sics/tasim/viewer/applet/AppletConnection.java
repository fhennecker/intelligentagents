/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer.applet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.ContextFactory;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.ViewerConnection;
import se.sics.tasim.viewer.applet.ViewerApplet;

public class AppletConnection
implements Runnable {
    private static final byte[] TACT_HEADER;
    private int MAX_BUFFER = 512000;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private BinaryTransportWriter transportWriter;
    private BinaryTransportReader transportReader;
    private ViewerApplet applet;
    private ViewerConnection viewer;
    private Thread connectionThread;
    private boolean finish = false;
    private String serverHost;
    private static final Logger log;

    static {
        byte[] arrby = new byte[8];
        arrby[0] = 84;
        arrby[1] = 65;
        arrby[2] = 67;
        arrby[3] = 84;
        TACT_HEADER = arrby;
        log = Logger.getLogger(AppletConnection.class.getName());
    }

    public AppletConnection(ViewerApplet applet, ViewerConnection viewer) {
        this.applet = applet;
        this.viewer = viewer;
    }

    public boolean connect() {
        try {
            this.disconnect();
            URL url = this.applet.getCodeBase();
            String serverName = this.applet.getServerName();
            String userName = this.applet.getUserName();
            int serverPort = this.applet.getServerPort();
            this.serverHost = url.getHost();
            String statusMessage = "Connecting to server " + serverName + " at " + this.serverHost + ':' + serverPort;
            log.fine(statusMessage);
            this.applet.setStatusMessage(statusMessage);
            this.transportWriter = new BinaryTransportWriter();
            this.transportReader = new BinaryTransportReader();
            this.transportReader.setContext(this.applet.getContextFactory().createContext());
            this.socket = new Socket(this.serverHost, serverPort);
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out = new DataOutputStream(this.socket.getOutputStream());
            this.out.write(TACT_HEADER);
            this.transportWriter.node("auth").attr("serverName", serverName).attr("userName", userName).attr("version", "0.8.19").endNode("auth");
            this.sendData(this.transportWriter);
            this.applet.setStatusMessage("Connected to server " + serverName);
            return true;
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "Connection to server failed", e);
            this.socket = null;
            return false;
        }
    }

    public void start() {
        this.connectionThread = new Thread(this);
        this.connectionThread.start();
    }

    public void stop() {
        if (!this.finish) {
            this.finish = true;
            this.disconnect();
            Thread t = this.connectionThread;
            if (t != null) {
                this.connectionThread = null;
                t.interrupt();
            }
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    public void run() {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:371)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:449)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2859)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:805)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:220)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:165)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:91)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:354)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:751)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:683)
        // org.benf.cfr.reader.Main.doJar(Main.java:128)
        // org.benf.cfr.reader.Main.main(Main.java:178)
        throw new IllegalStateException("Decompilation failed");
    }

    private void sendData(BinaryTransportWriter writer) throws IOException {
        writer.finish();
        this.out.writeInt(writer.size());
        writer.write(this.out);
        this.out.flush();
        writer.clear();
    }

    public synchronized void sendChatMessage(String message) {
        this.transportWriter.clear();
        this.transportWriter.node("chat").attr("message", message);
        this.transportWriter.endNode("chat");
        try {
            this.sendData(this.transportWriter);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "Can not send chat message to server", e);
        }
    }

    private void parseMessage(byte[] buffer, int offset, int size) {
        try {
            BinaryTransportReader reader = this.transportReader;
            reader.setMessage(buffer, offset, size);
            while (reader.nextNode(false)) {
                if (reader.isNode("intUpdated")) {
                    this.viewer.dataUpdated(reader.getAttributeAsInt("agent"), reader.getAttributeAsInt("type", 0), reader.getAttributeAsInt("value"));
                    continue;
                }
                if (reader.isNode("longUpdated")) {
                    this.viewer.dataUpdated(reader.getAttributeAsInt("agent"), reader.getAttributeAsInt("type", 0), reader.getAttributeAsLong("value"));
                    continue;
                }
                if (reader.isNode("floatUpdated")) {
                    this.viewer.dataUpdated(reader.getAttributeAsInt("agent"), reader.getAttributeAsInt("type", 0), reader.getAttributeAsFloat("value"));
                    continue;
                }
                if (reader.isNode("doubleUpdated")) {
                    this.viewer.dataUpdated(reader.getAttributeAsInt("agent"), reader.getAttributeAsInt("type", 0), reader.getAttributeAsDouble("value"));
                    continue;
                }
                if (reader.isNode("stringUpdated")) {
                    this.viewer.dataUpdated(reader.getAttributeAsInt("agent"), reader.getAttributeAsInt("type", 0), reader.getAttribute("value"));
                    continue;
                }
                if (reader.isNode("objectUpdated")) {
                    int agent = reader.getAttributeAsInt("agent", -1);
                    int type = reader.getAttributeAsInt("type", 0);
                    reader.enterNode();
                    if (reader.nextNode(false)) {
                        Transportable content = reader.readTransportable();
                        if (agent < 0) {
                            this.viewer.dataUpdated(type, content);
                        } else {
                            this.viewer.dataUpdated(agent, type, content);
                        }
                    } else {
                        log.warning("no content for objectUpdated");
                    }
                    reader.exitNode();
                    continue;
                }
                if (reader.isNode("interaction")) {
                    this.viewer.interaction(reader.getAttributeAsInt("fromAgent"), reader.getAttributeAsInt("toAgent"), reader.getAttributeAsInt("type", 0));
                    continue;
                }
                if (reader.isNode("interactionWithRole")) {
                    this.viewer.interactionWithRole(reader.getAttributeAsInt("fromAgent"), reader.getAttributeAsInt("role"), reader.getAttributeAsInt("type", 0));
                    continue;
                }
                if (reader.isNode("nextTimeUnit")) {
                    this.viewer.nextTimeUnit(reader.getAttributeAsInt("unit"));
                    continue;
                }
                if (reader.isNode("nextSimulation")) {
                    this.viewer.nextSimulation(reader.getAttributeAsInt("id", -1), reader.getAttributeAsLong("startTime", 0));
                    continue;
                }
                if (reader.isNode("simulationStarted")) {
                    this.viewer.simulationStarted(reader.getAttributeAsInt("id"), reader.getAttribute("type"), reader.getAttributeAsLong("startTime"), reader.getAttributeAsLong("endTime"), reader.getAttribute("timeUnitName", null), reader.getAttributeAsInt("timeUnitCount", 0));
                    continue;
                }
                if (reader.isNode("participant")) {
                    this.viewer.participant(reader.getAttributeAsInt("id"), reader.getAttributeAsInt("role"), reader.getAttribute("name"), reader.getAttributeAsInt("participantID"));
                    continue;
                }
                if (reader.isNode("simulationStopped")) {
                    this.viewer.simulationStopped(reader.getAttributeAsInt("id"));
                    continue;
                }
                if (reader.isNode("chat")) {
                    this.applet.addChatMessage(reader.getAttributeAsLong("time"), reader.getAttribute("server"), reader.getAttribute("user"), reader.getAttribute("message"));
                    continue;
                }
                if (reader.isNode("serverTime")) {
                    this.viewer.setServerTime(reader.getAttributeAsLong("time"));
                    continue;
                }
                if (reader.isNode("intCache")) {
                    this.viewer.intCache(reader.getAttributeAsInt("agent"), reader.getAttributeAsInt("type", 0), reader.getAttributeAsIntArray("cache"));
                    continue;
                }
                log.warning("ignoring message " + reader.getNodeName());
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "Error while parsing message", e);
        }
    }

    public void disconnect() {
        Socket socket = this.socket;
        this.socket = null;
        if (socket != null) {
            try {
                DataInputStream in = this.in;
                DataOutputStream out = this.out;
                this.in = null;
                this.out = null;
                this.transportWriter = null;
                this.transportReader = null;
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                socket.close();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "Error while disconnecting from server", e);
            }
            if (!this.finish) {
                this.applet.setStatusMessage("Disconnected from server... ");
            }
        }
    }
}

