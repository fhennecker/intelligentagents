/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.props;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.tasim.props.SimpleContent;

public class StartInfo
extends SimpleContent {
    private static final long serialVersionUID = 2985725711302603057L;
    private int simulationID;
    private long startTime;
    private int simulationLength;
    private int secondsPerDay;

    public StartInfo() {
    }

    public StartInfo(int simulationID, long startTime, int simulationLength, int secondsPerDay) {
        this.simulationID = simulationID;
        this.startTime = startTime;
        this.simulationLength = simulationLength;
        this.secondsPerDay = secondsPerDay;
        if (secondsPerDay < 1) {
            throw new IllegalArgumentException("secondsPerDay must be positive: " + secondsPerDay);
        }
    }

    public int getSimulationID() {
        return this.simulationID;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.startTime + (long)this.simulationLength;
    }

    public int getSimulationLength() {
        return this.simulationLength;
    }

    public int getSecondsPerDay() {
        return this.secondsPerDay;
    }

    public int getNumberOfDays() {
        return this.simulationLength / (this.secondsPerDay * 1000);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer().append(this.getTransportName()).append('[').append(this.simulationID).append(',').append(this.startTime).append(',').append(this.simulationLength).append(',').append(this.secondsPerDay).append(',');
        return this.params(buf).append(']').toString();
    }

    @Override
    public String getTransportName() {
        return "startInfo";
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        if (this.isLocked()) {
            throw new IllegalStateException("locked");
        }
        this.simulationID = reader.getAttributeAsInt("id");
        this.startTime = reader.getAttributeAsLong("startTime");
        this.simulationLength = reader.getAttributeAsInt("length") * 1000;
        this.secondsPerDay = reader.getAttributeAsInt("secondsPerDay");
        super.read(reader);
    }

    @Override
    public void write(TransportWriter writer) {
        writer.attr("id", this.simulationID).attr("startTime", this.startTime).attr("length", this.simulationLength / 1000).attr("secondsPerDay", this.secondsPerDay);
        super.write(writer);
    }
}

