/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.props;

import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.tasim.props.SimpleContent;

public class SimulationStatus
extends SimpleContent {
    private static final long serialVersionUID = 7937789505047945874L;
    private int simDate;
    private int consumedMillis;
    private boolean isSimulationEnded;

    public SimulationStatus() {
    }

    public SimulationStatus(int currentDate, int consumedMillis) {
        this(currentDate, consumedMillis, false);
    }

    public SimulationStatus(int currentDate, int consumedMillis, boolean isSimulationEnded) {
        this.simDate = currentDate;
        this.consumedMillis = consumedMillis;
        this.isSimulationEnded = isSimulationEnded;
    }

    public boolean isSimulationEnded() {
        return this.isSimulationEnded;
    }

    public int getCurrentDate() {
        return this.simDate;
    }

    public int getConsumedMillis() {
        return this.consumedMillis;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer().append(this.getTransportName()).append('[').append(this.simDate).append(',').append(this.consumedMillis).append(',').append(this.isSimulationEnded).append(',');
        return this.params(buf).append(']').toString();
    }

    @Override
    public String getTransportName() {
        return "simulationStatus";
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        if (this.isLocked()) {
            throw new IllegalStateException("locked");
        }
        this.simDate = reader.getAttributeAsInt("date");
        this.consumedMillis = reader.getAttributeAsInt("consumedMillis");
        this.isSimulationEnded = reader.getAttributeAsInt("isSimulationEnded", 0) > 0;
        super.read(reader);
    }

    @Override
    public void write(TransportWriter writer) {
        writer.attr("date", this.simDate).attr("consumedMillis", this.consumedMillis);
        if (this.isSimulationEnded) {
            writer.attr("isSimulationEnded", 1);
        }
        super.write(writer);
    }
}

