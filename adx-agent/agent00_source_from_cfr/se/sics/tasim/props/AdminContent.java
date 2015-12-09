/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.props;

import java.text.ParseException;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.props.SimpleContent;

public class AdminContent
extends SimpleContent {
    private static final long serialVersionUID = -6827270443299040674L;
    public static final int NONE = 0;
    public static final int ERROR = 1;
    public static final int PING = 2;
    public static final int PONG = 3;
    public static final int AUTH = 4;
    public static final int SERVER_TIME = 5;
    public static final int NEXT_SIMULATION = 6;
    public static final int JOIN_SIMULATION = 7;
    public static final int QUIT = 8;
    private static final String[] TYPE_NAMES = new String[]{"<none>", "error", "ping", "pong", "auth", "server time", "next simulation", "join simulation", "quit"};
    public static final int NO_ERROR = 0;
    public static final int NOT_SUPPORTED = 1;
    public static final int NOT_AUTH = 2;
    public static final int NO_SIMULATION_CREATED = 3;
    private static final String[] ERROR_NAMES = new String[]{"no error", "not supported", "not authenticated", "no simulation created"};
    private int type = 0;
    private int error = 0;
    private String errorReason = null;

    public static Context createContext() {
        Context context = new Context("admincontext");
        context.addTransportable(new AdminContent());
        return context;
    }

    public static String getTypeAsString(int type) {
        return type >= 0 && type < TYPE_NAMES.length ? TYPE_NAMES[type] : Integer.toString(type);
    }

    public static String getErrorAsString(int errorType) {
        return errorType >= 0 && errorType < ERROR_NAMES.length ? ERROR_NAMES[errorType] : Integer.toString(errorType);
    }

    public AdminContent() {
    }

    public AdminContent(int type) {
        this(type, 0, null);
    }

    public AdminContent(int type, int error) {
        this(type, error, null);
    }

    public AdminContent(int type, int error, String errorReason) {
        this.type = type;
        this.error = error;
        this.errorReason = errorReason;
    }

    public int getType() {
        return this.type;
    }

    public boolean isError() {
        if (this.error != 0) {
            return true;
        }
        return false;
    }

    public int getError() {
        return this.error;
    }

    public String getErrorReason() {
        return this.errorReason;
    }

    public void setError(int error) {
        this.setError(error, null);
    }

    public void setError(int error, String errorReason) {
        if (this.isLocked()) {
            throw new IllegalStateException("locked");
        }
        this.error = error;
        this.errorReason = errorReason;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer().append(this.getTransportName()).append('[').append(AdminContent.getTypeAsString(this.type));
        if (this.isError()) {
            buf.append(',').append(AdminContent.getErrorAsString(this.error));
            if (this.errorReason != null) {
                buf.append(',').append(this.errorReason);
            }
        }
        buf.append(',');
        return this.params(buf).append(']').toString();
    }

    @Override
    public String getTransportName() {
        return "adminContent";
    }

    @Override
    public void read(TransportReader reader) throws ParseException {
        if (this.isLocked()) {
            throw new IllegalStateException("locked");
        }
        this.type = reader.getAttributeAsInt("type");
        this.error = reader.getAttributeAsInt("error", 0);
        if (this.error != 0) {
            this.errorReason = reader.getAttribute("reason", null);
        }
        super.read(reader);
    }

    @Override
    public void write(TransportWriter writer) {
        writer.attr("type", this.type);
        if (this.error != 0) {
            writer.attr("error", this.error);
            if (this.errorReason != null) {
                writer.attr("reason", this.errorReason);
            }
        }
        super.write(writer);
    }
}

