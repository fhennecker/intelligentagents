/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractKeyedEntry;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public abstract class AbstractTransportableEntry<T extends Transportable>
extends AbstractKeyedEntry<T> {
    @Override
    protected final void readKey(TransportReader reader) throws ParseException {
        if (reader.nextNode(this.keyNodeName(), false)) {
            this.setKey(reader.readTransportable());
        }
    }

    @Override
    protected final void writeKey(TransportWriter writer) {
        if (this.getKey() != null) {
            writer.write((Transportable)this.getKey());
        }
    }

    protected abstract String keyNodeName();
}

