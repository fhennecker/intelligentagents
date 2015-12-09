/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import edu.umich.eecs.tac.props.QueryType;
import java.text.ParseException;
import java.util.Arrays;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public class AdvertiserInfo
extends AbstractTransportable {
    private String manufacturerSpecialty;
    private String componentSpecialty;
    private double manufacturerBonus;
    private double componentBonus;
    private double distributionCapacityDiscounter;
    private String publisherId;
    private int distributionCapacity;
    private String advertiserId;
    private int distributionWindow;
    private double targetEffect;
    private double[] focusEffects = new double[QueryType.values().length];

    public final double getFocusEffects(QueryType queryType) {
        return this.focusEffects[queryType.ordinal()];
    }

    public final void setFocusEffects(QueryType queryType, double focusEffect) {
        this.lockCheck();
        this.focusEffects[queryType.ordinal()] = focusEffect;
    }

    public final double getTargetEffect() {
        return this.targetEffect;
    }

    public final void setTargetEffect(double targetEffect) {
        this.lockCheck();
        this.targetEffect = targetEffect;
    }

    public final int getDistributionWindow() {
        return this.distributionWindow;
    }

    public final void setDistributionWindow(int distributionWindow) {
        this.lockCheck();
        this.distributionWindow = distributionWindow;
    }

    public final String getAdvertiserId() {
        return this.advertiserId;
    }

    public final void setAdvertiserId(String advertiserId) {
        this.lockCheck();
        this.advertiserId = advertiserId;
    }

    public final String getManufacturerSpecialty() {
        return this.manufacturerSpecialty;
    }

    public final void setManufacturerSpecialty(String manufacturerSpecialty) {
        this.lockCheck();
        this.manufacturerSpecialty = manufacturerSpecialty;
    }

    public final String getComponentSpecialty() {
        return this.componentSpecialty;
    }

    public final void setComponentSpecialty(String componentSpecialty) {
        this.lockCheck();
        this.componentSpecialty = componentSpecialty;
    }

    public final double getManufacturerBonus() {
        return this.manufacturerBonus;
    }

    public final void setManufacturerBonus(double manufacturerBonus) {
        this.lockCheck();
        this.manufacturerBonus = manufacturerBonus;
    }

    public final double getComponentBonus() {
        return this.componentBonus;
    }

    public final void setComponentBonus(double componentBonus) {
        this.lockCheck();
        this.componentBonus = componentBonus;
    }

    public final String getPublisherId() {
        return this.publisherId;
    }

    public final void setPublisherId(String publisherId) {
        this.lockCheck();
        this.publisherId = publisherId;
    }

    public final int getDistributionCapacity() {
        return this.distributionCapacity;
    }

    public final void setDistributionCapacity(int distributionCapacity) {
        this.lockCheck();
        this.distributionCapacity = distributionCapacity;
    }

    public final double getDistributionCapacityDiscounter() {
        return this.distributionCapacityDiscounter;
    }

    public final void setDistributionCapacityDiscounter(double distributionCapacityDiscounter) {
        this.lockCheck();
        this.distributionCapacityDiscounter = distributionCapacityDiscounter;
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        this.manufacturerSpecialty = reader.getAttribute("manufacturerSpecialty", null);
        this.manufacturerBonus = reader.getAttributeAsDouble("manufacturerBonus", 0.0);
        this.componentSpecialty = reader.getAttribute("componentSpecialty", null);
        this.componentBonus = reader.getAttributeAsDouble("componentBonus", 0.0);
        this.distributionCapacityDiscounter = reader.getAttributeAsDouble("distributionCapacityDiscounter", 1.0);
        this.publisherId = reader.getAttribute("publisherId", null);
        this.distributionCapacity = reader.getAttributeAsInt("distributionCapacity");
        this.advertiserId = reader.getAttribute("advertiserId", null);
        this.distributionWindow = reader.getAttributeAsInt("distributionWindow");
        this.targetEffect = reader.getAttributeAsDouble("targetEffect", 0.0);
        QueryType[] arrqueryType = QueryType.values();
        int n = arrqueryType.length;
        int n2 = 0;
        while (n2 < n) {
            QueryType type = arrqueryType[n2];
            this.focusEffects[type.ordinal()] = reader.getAttributeAsDouble(String.format("focusEffect[%s]", type.name()), 1.0);
            ++n2;
        }
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        if (this.manufacturerSpecialty != null) {
            writer.attr("manufacturerSpecialty", this.manufacturerSpecialty);
        }
        writer.attr("manufacturerBonus", this.manufacturerBonus);
        if (this.componentSpecialty != null) {
            writer.attr("componentSpecialty", this.componentSpecialty);
        }
        writer.attr("componentBonus", this.componentBonus);
        writer.attr("distributionCapacityDiscounter", this.distributionCapacityDiscounter);
        if (this.publisherId != null) {
            writer.attr("publisherId", this.publisherId);
        }
        writer.attr("distributionCapacity", this.distributionCapacity);
        if (this.advertiserId != null) {
            writer.attr("advertiserId", this.advertiserId);
        }
        writer.attr("distributionWindow", this.distributionWindow);
        writer.attr("targetEffect", this.targetEffect);
        QueryType[] arrqueryType = QueryType.values();
        int n = arrqueryType.length;
        int n2 = 0;
        while (n2 < n) {
            QueryType type = arrqueryType[n2];
            writer.attr(String.format("focusEffect[%s]", type.name()), this.focusEffects[type.ordinal()]);
            ++n2;
        }
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AdvertiserInfo that = (AdvertiserInfo)o;
        if (Double.compare(that.componentBonus, this.componentBonus) != 0) {
            return false;
        }
        if (Double.compare(that.distributionCapacityDiscounter, this.distributionCapacityDiscounter) != 0) {
            return false;
        }
        if (this.distributionCapacity != that.distributionCapacity) {
            return false;
        }
        if (this.distributionWindow != that.distributionWindow) {
            return false;
        }
        if (Double.compare(that.manufacturerBonus, this.manufacturerBonus) != 0) {
            return false;
        }
        if (Double.compare(that.targetEffect, this.targetEffect) != 0) {
            return false;
        }
        if (this.advertiserId != null ? !this.advertiserId.equals(that.advertiserId) : that.advertiserId != null) {
            return false;
        }
        if (this.componentSpecialty != null ? !this.componentSpecialty.equals(that.componentSpecialty) : that.componentSpecialty != null) {
            return false;
        }
        if (!Arrays.equals(this.focusEffects, that.focusEffects)) {
            return false;
        }
        if (this.manufacturerSpecialty != null ? !this.manufacturerSpecialty.equals(that.manufacturerSpecialty) : that.manufacturerSpecialty != null) {
            return false;
        }
        if (this.publisherId != null ? !this.publisherId.equals(that.publisherId) : that.publisherId != null) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        int result = this.manufacturerSpecialty != null ? this.manufacturerSpecialty.hashCode() : 0;
        result = 31 * result + (this.componentSpecialty != null ? this.componentSpecialty.hashCode() : 0);
        long temp = this.manufacturerBonus != 0.0 ? Double.doubleToLongBits(this.manufacturerBonus) : 0;
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = this.componentBonus != 0.0 ? Double.doubleToLongBits(this.componentBonus) : 0;
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = this.distributionCapacityDiscounter != 0.0 ? Double.doubleToLongBits(this.distributionCapacityDiscounter) : 0;
        result = 31 * result + (int)(temp ^ temp >>> 32);
        result = 31 * result + (this.publisherId != null ? this.publisherId.hashCode() : 0);
        result = 31 * result + this.distributionCapacity;
        result = 31 * result + (this.advertiserId != null ? this.advertiserId.hashCode() : 0);
        result = 31 * result + this.distributionWindow;
        temp = this.targetEffect != 0.0 ? Double.doubleToLongBits(this.targetEffect) : 0;
        result = 31 * result + (int)(temp ^ temp >>> 32);
        result = 31 * result + Arrays.hashCode(this.focusEffects);
        return result;
    }
}

