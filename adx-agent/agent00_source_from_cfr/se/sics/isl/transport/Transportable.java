/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.transport;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public interface Transportable {
    public String getTransportName();

    public void read(TransportReader var1) throws ParseException;

    public void write(TransportWriter var1);
}

