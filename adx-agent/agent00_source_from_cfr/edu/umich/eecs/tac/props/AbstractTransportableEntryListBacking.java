/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public abstract class AbstractTransportableEntryListBacking<S extends Transportable>
extends AbstractTransportable {
    private final List<S> entries = new ArrayList<S>();

    protected AbstractTransportableEntryListBacking() {
    }

    protected final List<S> getEntries() {
        return this.entries;
    }

    public final int size() {
        return this.entries.size();
    }

    protected void toStringBeforeEntries(StringBuilder builder) {
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder("(");
        builder.append(this.getClass().getSimpleName());
        this.toStringBeforeEntries(builder);
        for (Transportable entry : this.entries) {
            builder.append(' ').append(entry);
        }
        this.toStringAfterEntries(builder);
        builder.append(')');
        return builder.toString();
    }

    protected void toStringAfterEntries(StringBuilder builder) {
    }

    protected void readBeforeEntries(TransportReader reader) throws ParseException {
    }

    protected void readAfterEntries(TransportReader reader) throws ParseException {
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        this.readBeforeEntries(reader);
        while (reader.nextNode(this.entryClass().getSimpleName(), false)) {
            this.addEntry(reader.readTransportable());
        }
        this.readAfterEntries(reader);
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        this.writeBeforeEntries(writer);
        for (Transportable reportEntry : this.entries) {
            writer.write(reportEntry);
        }
        this.writeAfterEntries(writer);
    }

    protected void writeAfterEntries(TransportWriter writer) {
    }

    protected void writeBeforeEntries(TransportWriter writer) {
    }

    public final S getEntry(int index) throws IndexOutOfBoundsException {
        return (S)((Transportable)this.entries.get(index));
    }

    public final void removeEntry(int index) throws IllegalStateException {
        this.beforeRemoveEntry(index);
        this.lockCheck();
        this.entries.remove(index);
        this.afterRemoveEntry(index);
    }

    protected void beforeRemoveEntry(int index) {
    }

    protected void afterRemoveEntry(int index) {
    }

    protected final int addEntry(S entry) throws IllegalStateException {
        this.beforeAddEntry(entry);
        this.lockCheck();
        this.entries.add(entry);
        this.afterAddEntry(entry);
        return this.size() - 1;
    }

    protected void beforeAddEntry(S entry) throws IllegalStateException {
    }

    protected void afterAddEntry(S entry) throws IllegalStateException {
    }

    protected abstract Class entryClass();
}

