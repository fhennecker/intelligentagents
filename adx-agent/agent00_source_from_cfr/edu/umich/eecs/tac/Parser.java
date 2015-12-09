/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac;

import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.ServerConfig;
import tau.tac.adx.props.AdxInfoContextFactory;

public abstract class Parser {
    private static final Logger log = Logger.getLogger(Parser.class.getName());
    private static final String CONFIG_NAME = new ServerConfig().getTransportName();
    private final LogReader logReader;

    protected Parser(LogReader logReader) {
        this.logReader = logReader;
        AdxInfoContextFactory aaInfo = new AdxInfoContextFactory();
        this.logReader.setContext(aaInfo.createContext());
    }

    protected LogReader getReader() {
        return this.logReader;
    }

    public final void start() throws IOException, ParseException {
        try {
            this.parseStarted();
            while (this.logReader.hasMoreChunks()) {
                TransportReader reader = this.logReader.nextChunk();
                this.handleNodes(reader);
            }
        }
        finally {
            this.stop();
        }
    }

    private void handleNodes(TransportReader reader) throws ParseException {
        while (reader.nextNode(false)) {
            int type;
            int agentIndex;
            if (reader.isNode("intUpdated")) {
                type = reader.getAttributeAsInt("type", 0);
                agentIndex = reader.getAttributeAsInt("agent", -1);
                int value = reader.getAttributeAsInt("value");
                if (agentIndex < 0) continue;
                this.dataUpdated(agentIndex, type, value);
                continue;
            }
            if (reader.isNode("longUpdated")) {
                type = reader.getAttributeAsInt("type", 0);
                agentIndex = reader.getAttributeAsInt("agent", -1);
                long value = reader.getAttributeAsLong("value");
                if (agentIndex < 0) continue;
                this.dataUpdated(agentIndex, type, value);
                continue;
            }
            if (reader.isNode("floatUpdated")) {
                type = reader.getAttributeAsInt("type", 0);
                agentIndex = reader.getAttributeAsInt("agent", -1);
                float value = reader.getAttributeAsFloat("value");
                if (agentIndex < 0) continue;
                this.dataUpdated(agentIndex, type, value);
                continue;
            }
            if (reader.isNode("doubleUpdated")) {
                type = reader.getAttributeAsInt("type", 0);
                agentIndex = reader.getAttributeAsInt("agent", -1);
                double value = reader.getAttributeAsDouble("value");
                if (agentIndex < 0) continue;
                this.dataUpdated(agentIndex, type, value);
                continue;
            }
            if (reader.isNode("stringUpdated")) {
                type = reader.getAttributeAsInt("type", 0);
                agentIndex = reader.getAttributeAsInt("agent", -1);
                String value = reader.getAttribute("value");
                if (agentIndex < 0) continue;
                this.dataUpdated(agentIndex, type, value);
                continue;
            }
            if (reader.isNode("messageToRole")) {
                int sender = reader.getAttributeAsInt("sender");
                int role = reader.getAttributeAsInt("role");
                reader.enterNode();
                reader.nextNode(true);
                Transportable content = reader.readTransportable();
                reader.exitNode();
                this.messageToRole(sender, role, content);
                continue;
            }
            if (reader.isNode("message")) {
                int receiver = reader.getAttributeAsInt("receiver");
                if (receiver == 0) continue;
                int sender = reader.getAttributeAsInt("sender");
                reader.enterNode();
                reader.nextNode(true);
                Transportable content = reader.readTransportable();
                reader.exitNode();
                this.message(sender, receiver, content);
                continue;
            }
            if (reader.isNode("objectUpdated")) {
                int agentIndex2 = reader.getAttributeAsInt("agent", -1);
                int type2 = reader.getAttributeAsInt("type", 0);
                reader.enterNode();
                reader.nextNode(true);
                Transportable content = reader.readTransportable();
                reader.exitNode();
                if (agentIndex2 >= 0) {
                    this.dataUpdated(agentIndex2, type2, content);
                    continue;
                }
                this.dataUpdated(type2, content);
                continue;
            }
            if (reader.isNode("transaction")) {
                int source = reader.getAttributeAsInt("source");
                int recipient = reader.getAttributeAsInt("recipient");
                double amount = reader.getAttributeAsDouble("amount");
                this.transaction(source, recipient, amount);
                continue;
            }
            if (reader.isNode("nextTimeUnit")) {
                int date = reader.getAttributeAsInt("unit");
                long time = reader.getAttributeAsLong("time", 0);
                this.nextDay(date, time);
                continue;
            }
            if (reader.isNode(CONFIG_NAME)) {
                Transportable content = reader.readTransportable();
                this.data(content);
                continue;
            }
            this.unhandledNode(reader.getNodeName());
        }
    }

    public final void stop() {
        this.logReader.close();
        this.parseStopped();
    }

    protected void parseStarted() {
    }

    protected void parseStopped() {
    }

    protected void messageToRole(int sender, int role, Transportable content) {
        ParticipantInfo[] infos = this.logReader.getParticipants();
        if (infos != null) {
            int i = 0;
            int n = infos.length;
            while (i < n) {
                if (infos[i].getRole() == role) {
                    this.message(sender, infos[i].getIndex(), content);
                }
                ++i;
            }
        }
    }

    protected abstract void message(int var1, int var2, Transportable var3);

    protected void data(Transportable object) {
    }

    protected void dataUpdated(int agent, int type, int value) {
    }

    protected void dataUpdated(int agent, int type, long value) {
    }

    protected void dataUpdated(int agent, int type, float value) {
    }

    protected void dataUpdated(int agent, int type, double value) {
    }

    protected void dataUpdated(int agent, int type, String value) {
    }

    protected void dataUpdated(int agent, int type, Transportable content) {
    }

    protected void dataUpdated(int type, Transportable content) {
    }

    protected void transaction(int source, int recipient, double amount) {
    }

    protected void nextDay(int date, long serverTime) {
    }

    protected void unhandledNode(String nodeName) {
        log.warning("ignoring unhandled node '" + nodeName + '\'');
    }
}

