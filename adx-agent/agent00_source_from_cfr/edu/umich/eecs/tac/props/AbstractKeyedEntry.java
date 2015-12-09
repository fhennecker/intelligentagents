/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.KeyedEntry;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public abstract class AbstractKeyedEntry<T>
implements KeyedEntry<T> {
    private T key;

    @Override
    public final T getKey() {
        return this.key;
    }

    public final void setKey(T key) {
        this.key = key;
    }

    @Override
    public final String getTransportName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public final void read(TransportReader reader) throws ParseException {
        this.readEntry(reader);
        this.readKey(reader);
    }

    @Override
    public final void write(TransportWriter writer) {
        this.writeEntry(writer);
        this.writeKey(writer);
    }

    protected abstract void readEntry(TransportReader var1) throws ParseException;

    protected abstract void readKey(TransportReader var1) throws ParseException;

    protected abstract void writeEntry(TransportWriter var1);

    protected abstract void writeKey(TransportWriter var1);
}

