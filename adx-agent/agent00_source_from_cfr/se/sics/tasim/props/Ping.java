/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.props;

import java.io.Serializable;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class Ping
implements Transportable,
Serializable {
    private static final long serialVersionUID = 5214670517699777053L;
    public static final int PONG = 1;
    public static final int PING = 2;
    private int flags;

    public Ping() {
        this.flags = 2;
    }

    public Ping(int flags) {
        this.flags = flags;
    }

    public boolean isPing() {
        if ((this.flags & 2) != 0) {
            return true;
        }
        return false;
    }

    public boolean isPong() {
        if ((this.flags & 1) != 0) {
            return true;
        }
        return false;
    }

    public Ping createPong() {
        return new Ping(1);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer().append(this.getTransportName()).append('[').append(this.flags);
        return buf.append(']').toString();
    }

    @Override
    public String getTransportName() {
        return "ping";
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        this.flags = reader.getAttributeAsInt("flags");
    }

    @Override
    public void write(TransportWriter writer) {
        writer.attr("flags", this.flags);
    }
}

