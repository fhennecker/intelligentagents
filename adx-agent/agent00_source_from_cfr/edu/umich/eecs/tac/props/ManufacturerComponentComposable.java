/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public class ManufacturerComponentComposable
extends AbstractTransportable {
    private static final String MANUFACTURER_KEY = "manufacturer";
    private static final String COMPONENT_KEY = "component";
    private int hashCode;
    private String manufacturer;
    private String component;

    public ManufacturerComponentComposable() {
        this.calculateHashCode();
    }

    public final String getManufacturer() {
        return this.manufacturer;
    }

    public final void setManufacturer(String manufacturer) {
        this.lockCheck();
        this.manufacturer = manufacturer;
        this.calculateHashCode();
    }

    public final String getComponent() {
        return this.component;
    }

    public final void setComponent(String component) throws IllegalStateException {
        this.lockCheck();
        this.component = component;
        this.calculateHashCode();
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        this.setManufacturer(reader.getAttribute("manufacturer", null));
        this.setComponent(reader.getAttribute("component", null));
        this.calculateHashCode();
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        if (this.getManufacturer() != null) {
            writer.attr("manufacturer", this.getManufacturer());
        }
        if (this.getComponent() != null) {
            writer.attr("component", this.getComponent());
        }
    }

    public final int hashCode() {
        return this.hashCode;
    }

    protected final void calculateHashCode() {
        int result = this.manufacturer != null ? this.manufacturer.hashCode() : 0;
        this.hashCode = result = 31 * result + (this.component != null ? this.component.hashCode() : 0);
    }

    public final String toString() {
        return String.format("(%s (%s,%s))", this.getClass().getSimpleName(), this.getManufacturer(), this.getComponent());
    }

    protected final boolean composableEquals(ManufacturerComponentComposable o) {
        if (o == null) {
            return false;
        }
        if (this.isLocked() != o.isLocked()) {
            return false;
        }
        if (this.component != null ? !this.component.equals(o.component) : o.component != null) {
            return false;
        }
        return !(this.manufacturer != null ? !this.manufacturer.equals(o.manufacturer) : o.manufacturer != null);
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.hashCode() != o.hashCode() || this.getClass() != o.getClass()) {
            return false;
        }
        return this.composableEquals((ManufacturerComponentComposable)o);
    }
}

