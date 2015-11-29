/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import java.io.Serializable;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public abstract class AbstractTransportable
implements Transportable,
Serializable {
    private boolean locked;

    public final void lock() {
        this.locked = true;
    }

    public final boolean isLocked() {
        return this.locked;
    }

    protected final void lockCheck() throws IllegalStateException {
        if (this.isLocked()) {
            throw new IllegalStateException("locked");
        }
    }

    @Override
    public final void read(TransportReader reader) throws ParseException {
        this.lockCheck();
        boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
        this.readWithLock(reader);
        if (lock) {
            this.lock();
        }
    }

    @Override
    public final void write(TransportWriter writer) {
        if (this.isLocked()) {
            writer.attr("lock", 1);
        }
        this.writeWithLock(writer);
    }

    @Override
    public final String getTransportName() {
        return this.getClass().getSimpleName();
    }

    protected abstract void readWithLock(TransportReader var1) throws ParseException;

    protected abstract void writeWithLock(TransportWriter var1);
}

