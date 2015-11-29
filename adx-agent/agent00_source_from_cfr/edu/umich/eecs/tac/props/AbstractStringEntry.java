/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractKeyedEntry;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public abstract class AbstractStringEntry
extends AbstractKeyedEntry<String> {
    private static final String KEY_NODE = "AbstractStringEntryKeyNode";
    private static final String KEY_ATTRIBUTE = "AbstractStringEntryKey";

    @Override
    protected final void readKey(TransportReader reader) throws ParseException {
        reader.nextNode("AbstractStringEntryKeyNode", true);
        this.setKey(reader.getAttribute("AbstractStringEntryKey", null));
    }

    @Override
    protected final void writeKey(TransportWriter writer) {
        writer.node("AbstractStringEntryKeyNode");
        if (this.getKey() != null) {
            writer.attr("AbstractStringEntryKey", (String)this.getKey());
        }
        writer.endNode("AbstractStringEntryKeyNode");
    }
}

