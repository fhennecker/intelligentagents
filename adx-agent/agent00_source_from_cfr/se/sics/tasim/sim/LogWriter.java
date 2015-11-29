/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.sim;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.is.EventWriter;

public class LogWriter
extends EventWriter {
    private static final Logger log = Logger.getLogger(LogWriter.class.getName());
    private static final byte[] TACT_HEADER;
    private EventWriter parentWriter;
    private DataOutputStream out;
    private BinaryTransportWriter writer;
    private boolean isClosed = false;

    static {
        byte[] arrby = new byte[8];
        arrby[0] = 84;
        arrby[1] = 65;
        arrby[2] = 67;
        arrby[3] = 84;
        TACT_HEADER = arrby;
    }

    public LogWriter(EventWriter parentWriter) {
        this(parentWriter, null);
    }

    public LogWriter(EventWriter parentWriter, OutputStream out) {
        if (parentWriter == null) {
            throw new NullPointerException();
        }
        this.parentWriter = parentWriter;
        if (out == null) {
            this.isClosed = true;
        } else {
            this.out = new DataOutputStream(out);
            this.writer = new BinaryTransportWriter();
            try {
                this.out.write(TACT_HEADER);
            }
            catch (Exception e) {
                this.isClosed = true;
                log.log(Level.SEVERE, "could not initialize log", e);
                try {
                    this.out.close();
                }
                catch (Exception var4_4) {
                    // empty catch block
                }
                this.out = null;
            }
        }
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    public synchronized void close() {
        if (!this.isClosed) {
            try {
                this.commit();
                if (!this.isClosed) {
                    try {
                        this.out.writeInt(0);
                    }
                    catch (Exception e) {
                        log.log(Level.SEVERE, "could not write end of log", e);
                    }
                }
            }
            finally {
                this.isClosed = true;
                try {
                    this.out.close();
                }
                catch (Exception var3_4) {}
                this.out = null;
                this.writer.clear();
            }
        }
    }

    public synchronized void commit() {
        if (!this.isClosed) {
            this.writer.finish();
            int size = this.writer.size();
            if (size > 0) {
                try {
                    this.out.writeInt(this.writer.size());
                    this.writer.write(this.out);
                }
                catch (IOException e) {
                    this.isClosed = true;
                    log.log(Level.SEVERE, "could not write to log", e);
                    try {
                        this.out.close();
                    }
                    catch (Exception var3_3) {
                        // empty catch block
                    }
                    this.out = null;
                }
            }
            this.writer.clear();
        }
    }

    public synchronized void nextTimeUnit(int timeUnit, long time) {
        this.parentWriter.nextTimeUnit(timeUnit);
        if (!this.isClosed) {
            this.writer.node("nextTimeUnit").attr("unit", timeUnit).attr("time", time).endNode("nextTimeUnit");
        }
    }

    @Override
    public void participant(int id, int role, String name, int participantID) {
        this.parentWriter.participant(id, role, name, participantID);
    }

    @Override
    public synchronized void nextTimeUnit(int timeUnit) {
        this.parentWriter.nextTimeUnit(timeUnit);
        if (!this.isClosed) {
            this.writer.node("nextTimeUnit").attr("unit", timeUnit).endNode("nextTimeUnit");
        }
    }

    @Override
    public synchronized void dataUpdated(int agent, int type, int value) {
        this.parentWriter.dataUpdated(agent, type, value);
        if (!this.isClosed) {
            this.writer.node("intUpdated").attr("agent", agent);
            if (type != 0) {
                this.writer.attr("type", type);
            }
            this.writer.attr("value", value).endNode("intUpdated");
        }
    }

    @Override
    public synchronized void dataUpdated(int agent, int type, long value) {
        this.parentWriter.dataUpdated(agent, type, value);
        if (!this.isClosed) {
            this.writer.node("longUpdated").attr("agent", agent);
            if (type != 0) {
                this.writer.attr("type", type);
            }
            this.writer.attr("value", value).endNode("longUpdated");
        }
    }

    @Override
    public synchronized void dataUpdated(int agent, int type, float value) {
        this.parentWriter.dataUpdated(agent, type, value);
        if (!this.isClosed) {
            this.writer.node("floatUpdated").attr("agent", agent);
            if (type != 0) {
                this.writer.attr("type", type);
            }
            this.writer.attr("value", value).endNode("floatUpdated");
        }
    }

    @Override
    public synchronized void dataUpdated(int agent, int type, double value) {
        this.parentWriter.dataUpdated(agent, type, value);
        if (!this.isClosed) {
            this.writer.node("doubleUpdated").attr("agent", agent);
            if (type != 0) {
                this.writer.attr("type", type);
            }
            this.writer.attr("value", value).endNode("doubleUpdated");
        }
    }

    @Override
    public synchronized void dataUpdated(int agent, int type, String value) {
        this.parentWriter.dataUpdated(agent, type, value);
        if (!this.isClosed) {
            this.writer.node("stringUpdated").attr("agent", agent);
            if (type != 0) {
                this.writer.attr("type", type);
            }
            this.writer.attr("value", value).endNode("stringUpdated");
        }
    }

    @Override
    public synchronized void dataUpdated(int agent, int type, Transportable value) {
        this.parentWriter.dataUpdated(agent, type, value);
        if (!this.isClosed) {
            this.writer.node("objectUpdated").attr("agent", agent);
            if (type != 0) {
                this.writer.attr("type", type);
            }
            this.writer.write(value);
            this.writer.endNode("objectUpdated");
        }
    }

    @Override
    public synchronized void dataUpdated(int type, Transportable value) {
        this.parentWriter.dataUpdated(type, value);
        if (!this.isClosed) {
            this.writer.node("objectUpdated");
            if (type != 0) {
                this.writer.attr("type", type);
            }
            this.writer.write(value);
            this.writer.endNode("objectUpdated");
        }
    }

    @Override
    public void interaction(int fromAgent, int toAgent, int type) {
        this.parentWriter.interaction(fromAgent, toAgent, type);
    }

    @Override
    public void interactionWithRole(int fromAgent, int role, int type) {
        this.parentWriter.interactionWithRole(fromAgent, role, type);
    }

    public synchronized void message(int sender, int receiver, Transportable content, long time) {
        if (!this.isClosed) {
            this.writer.node("message").attr("sender", sender).attr("receiver", receiver).attr("time", time);
            this.writer.write(content);
            this.writer.endNode("message");
        }
    }

    public synchronized void messageToRole(int sender, int role, Transportable content, long time) {
        if (!this.isClosed) {
            this.writer.node("messageToRole").attr("sender", sender).attr("role", role).attr("time", time);
            this.writer.write(content);
            this.writer.endNode("messageToRole");
        }
    }

    @Override
    public void intCache(int agent, int type, int[] cache) {
    }

    public synchronized LogWriter write(Transportable content) {
        if (!this.isClosed) {
            this.writer.write(content);
        }
        return this;
    }

    public LogWriter node(String name) {
        if (!this.isClosed) {
            this.writer.node(name);
        }
        return this;
    }

    public LogWriter endNode(String name) {
        if (!this.isClosed) {
            this.writer.endNode(name);
        }
        return this;
    }

    public LogWriter attr(String name, int value) {
        if (!this.isClosed) {
            this.writer.attr(name, value);
        }
        return this;
    }

    public LogWriter attr(String name, long value) {
        if (!this.isClosed) {
            this.writer.attr(name, value);
        }
        return this;
    }

    public LogWriter attr(String name, float value) {
        if (!this.isClosed) {
            this.writer.attr(name, value);
        }
        return this;
    }

    public LogWriter attr(String name, double value) {
        if (!this.isClosed) {
            this.writer.attr(name, value);
        }
        return this;
    }

    public LogWriter attr(String name, String value) {
        if (!this.isClosed) {
            this.writer.attr(name, value);
        }
        return this;
    }
}

