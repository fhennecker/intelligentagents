/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public class SlotInfo
extends AbstractTransportable {
    private int promotedSlots;
    private int regularSlots;
    private double promotedSlotBonus;

    public final int getPromotedSlots() {
        return this.promotedSlots;
    }

    public final void setPromotedSlots(int promotedSlots) {
        this.lockCheck();
        this.promotedSlots = promotedSlots;
    }

    public final int getRegularSlots() {
        return this.regularSlots;
    }

    public final void setRegularSlots(int regularSlots) {
        this.lockCheck();
        this.regularSlots = regularSlots;
    }

    public final double getPromotedSlotBonus() {
        return this.promotedSlotBonus;
    }

    public final void setPromotedSlotBonus(double promotedSlotBonus) {
        this.lockCheck();
        this.promotedSlotBonus = promotedSlotBonus;
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        this.promotedSlots = reader.getAttributeAsInt("promotedSlots", 0);
        this.regularSlots = reader.getAttributeAsInt("regularSlots", 0);
        this.promotedSlotBonus = reader.getAttributeAsDouble("promotedSlotBonus", 0.0);
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        writer.attr("promotedSlots", this.promotedSlots);
        writer.attr("regularSlots", this.regularSlots);
        writer.attr("promotedSlotBonus", this.promotedSlotBonus);
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SlotInfo slotInfo = (SlotInfo)o;
        if (Double.compare(slotInfo.promotedSlotBonus, this.promotedSlotBonus) != 0) {
            return false;
        }
        if (this.promotedSlots != slotInfo.promotedSlots) {
            return false;
        }
        if (this.regularSlots != slotInfo.regularSlots) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        int result = this.promotedSlots;
        result = 31 * result + this.regularSlots;
        long temp = this.promotedSlotBonus != 0.0 ? Double.doubleToLongBits(this.promotedSlotBonus) : 0;
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

