/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is;

import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.is.EventWriter;

public class TransportEventWriter
extends EventWriter {
    private TransportWriter writer;

    public TransportEventWriter(TransportWriter writer) {
        this.writer = writer;
    }

    @Override
    public void participant(int agent, int role, String name, int participantID) {
        this.writer.node("participant").attr("id", agent).attr("role", role).attr("name", name).attr("participantID", participantID).endNode("participant");
    }

    @Override
    public void nextTimeUnit(int timeUnit) {
        this.writer.node("nextTimeUnit").attr("unit", timeUnit).endNode("nextTimeUnit");
    }

    @Override
    public void dataUpdated(int agent, int type, int value) {
        this.writer.node("intUpdated").attr("agent", agent);
        if (type != 0) {
            this.writer.attr("type", type);
        }
        this.writer.attr("value", value).endNode("intUpdated");
    }

    @Override
    public void dataUpdated(int agent, int type, long value) {
        this.writer.node("longUpdated").attr("agent", agent);
        if (type != 0) {
            this.writer.attr("type", type);
        }
        this.writer.attr("value", value).endNode("longUpdated");
    }

    @Override
    public void dataUpdated(int agent, int type, float value) {
        this.writer.node("floatUpdated").attr("agent", agent);
        if (type != 0) {
            this.writer.attr("type", type);
        }
        this.writer.attr("value", value).endNode("floatUpdated");
    }

    @Override
    public void dataUpdated(int agent, int type, double value) {
        this.writer.node("doubleUpdated").attr("agent", agent);
        if (type != 0) {
            this.writer.attr("type", type);
        }
        this.writer.attr("value", value).endNode("doubleUpdated");
    }

    @Override
    public void dataUpdated(int agent, int type, String value) {
        this.writer.node("stringUpdated").attr("agent", agent);
        if (type != 0) {
            this.writer.attr("type", type);
        }
        this.writer.attr("value", value).endNode("stringUpdated");
    }

    @Override
    public void dataUpdated(int agent, int type, Transportable content) {
        this.writer.node("objectUpdated").attr("agent", agent);
        if (type != 0) {
            this.writer.attr("type", type);
        }
        this.writer.write(content);
        this.writer.endNode("objectUpdated");
    }

    @Override
    public void dataUpdated(int type, Transportable content) {
        this.writer.node("objectUpdated");
        if (type != 0) {
            this.writer.attr("type", type);
        }
        this.writer.write(content);
        this.writer.endNode("objectUpdated");
    }

    @Override
    public void interaction(int fromAgent, int toAgent, int type) {
        this.writer.node("interaction").attr("fromAgent", fromAgent).attr("toAgent", toAgent);
        if (type != 0) {
            this.writer.attr("type", type);
        }
        this.writer.endNode("interaction");
    }

    @Override
    public void interactionWithRole(int fromAgent, int role, int type) {
        this.writer.node("interactionWithRole").attr("fromAgent", fromAgent).attr("role", role);
        if (type != 0) {
            this.writer.attr("type", type);
        }
        this.writer.endNode("interactionWithRole");
    }

    @Override
    public void intCache(int agent, int type, int[] cache) {
        this.writer.node("intCache").attr("agent", agent).attr("type", type).attr("cache", cache);
        this.writer.endNode("intCache");
    }
}

