/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.props;

import java.io.Serializable;
import java.text.ParseException;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class Alert
implements Transportable,
Serializable {
    private static final long serialVersionUID = 5611022906104201693L;
    private int priority;
    private String title;
    private String message;

    public Alert() {
    }

    public Alert(String title, String message) {
        this(0, title, message);
    }

    public Alert(int priority, String title, String message) {
        this.priority = priority;
        this.title = title;
        this.message = message;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return String.valueOf(this.getTransportName()) + '[' + this.priority + ',' + this.title + ',' + this.message + ']';
    }

    @Override
    public String getTransportName() {
        return "alert";
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        this.priority = reader.getAttributeAsInt("priority", 0);
        this.title = reader.getAttribute("title");
        this.message = reader.getAttribute("message");
    }

    @Override
    public void write(TransportWriter writer) {
        if (this.priority != 0) {
            writer.attr("priority", this.priority);
        }
        writer.attr("title", this.title);
        writer.attr("message", this.message);
    }
}

