/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportable;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public class PublisherInfo
extends AbstractTransportable {
    private double squashingParameter;

    public final double getSquashingParameter() {
        return this.squashingParameter;
    }

    public final void setSquashingParameter(double squashingParameter) {
        this.lockCheck();
        this.squashingParameter = squashingParameter;
    }

    @Override
    protected final void readWithLock(TransportReader reader) throws ParseException {
        this.squashingParameter = reader.getAttributeAsDouble("squashingParameter", 0.0);
    }

    @Override
    protected final void writeWithLock(TransportWriter writer) {
        writer.attr("squashingParameter", this.squashingParameter);
    }
}

