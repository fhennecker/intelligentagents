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
import tau.tac.adx.props.AdLink;

public class Ranking
extends AbstractTransportable {
    private final List<Slot> slots = new ArrayList<Slot>();

    public final void add(AdLink adLink, boolean promoted) throws IllegalStateException {
        this.add(new Slot(adLink, promoted));
    }

    public final void add(AdLink adLink) throws IllegalStateException {
        this.add(adLink, false);
    }

    protected final void add(Slot slot) throws IllegalStateException {
        this.lockCheck();
        this.slots.add(slot);
    }

    public final void set(int position, AdLink adLink, boolean promoted) throws IllegalStateException {
        this.lockCheck();
        this.slots.set(position, new Slot(adLink, promoted));
    }

    public final AdLink get(int position) {
        return this.slots.get(position).getAdLink();
    }

    public final boolean isPromoted(int position) {
        return this.slots.get(position).isPromoted();
    }

    public final int positionForAd(AdLink adLink) {
        int i = 0;
        while (i < this.size()) {
            if (this.get(i).equals(adLink)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public final int size() {
        return this.slots.size();
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer().append('[');
        int i = 0;
        int n = this.size();
        while (i < n) {
            sb.append('[').append(i).append(": ").append(this.get(i)).append(']');
            ++i;
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        while (reader.nextNode(Slot.class.getSimpleName(), false)) {
            this.add((Slot)reader.readTransportable());
        }
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        for (Slot slot : this.slots) {
            writer.write(slot);
        }
    }

    public static class Slot
    extends AbstractTransportable {
        private static final long serialVersionUID = -2489798409612047493L;
        private AdLink adLink;
        private boolean promoted;

        public Slot() {
        }

        public Slot(AdLink adLink, boolean promoted) {
            this.adLink = adLink;
            this.promoted = promoted;
        }

        public final AdLink getAdLink() {
            return this.adLink;
        }

        public final void setAdLink(AdLink adLink) {
            this.adLink = adLink;
        }

        public final boolean isPromoted() {
            return this.promoted;
        }

        public final void setPromoted(boolean promoted) {
            this.promoted = promoted;
        }

        @Override
        protected final void readWithLock(TransportReader reader) throws ParseException {
            boolean bl = this.promoted = reader.getAttributeAsInt("promoted", 0) > 0;
            if (reader.nextNode(AdLink.class.getSimpleName(), false)) {
                this.adLink = (AdLink)reader.readTransportable();
            }
        }

        @Override
        protected final void writeWithLock(TransportWriter writer) {
            if (this.promoted) {
                writer.attr("promoted", 1);
            }
            if (this.adLink != null) {
                writer.write(this.adLink);
            }
        }
    }

}

