/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.transport;

import java.io.PrintStream;
import java.text.ParseException;
import se.sics.isl.transport.Context;
import se.sics.isl.transport.Transportable;

public abstract class TransportReader {
    private Context currentContext;

    protected TransportReader() {
    }

    public void setContext(Context context) {
        this.currentContext = context;
    }

    public abstract void reset();

    protected abstract int getPosition();

    public Transportable readTransportable() throws ParseException {
        String name = this.getNodeName();
        String className = this.getTransportableClass(name);
        Transportable object = this.createTransportable(className);
        this.readTransportable(object);
        return object;
    }

    protected String getTransportableClass(String nodeName) throws ParseException {
        Context context = this.currentContext;
        if (context == null) {
            throw new ParseException("no context for node " + nodeName, this.getPosition());
        }
        String className = context.lookupClass(nodeName);
        if (className != null) {
            return className;
        }
        throw new ParseException("no node named " + nodeName + " in context " + context.getName(), this.getPosition());
    }

    protected Transportable createTransportable(String className) throws ParseException {
        try {
            return (Transportable)Class.forName(className).newInstance();
        }
        catch (Exception e) {
            throw (ParseException)new ParseException("could not create transportable of type '" + className + '\'', this.getPosition()).initCause(e);
        }
    }

    protected void readTransportable(Transportable object) throws ParseException {
        this.enterNode();
        object.read(this);
        this.exitNode();
    }

    public abstract boolean hasMoreNodes() throws ParseException;

    public abstract boolean nextNode(boolean var1) throws ParseException;

    public abstract boolean nextNode(String var1, boolean var2) throws ParseException;

    public abstract String getNodeName() throws ParseException;

    public abstract boolean isNode() throws ParseException;

    public abstract boolean isNode(String var1) throws ParseException;

    public abstract boolean enterNode() throws ParseException;

    public abstract boolean exitNode() throws ParseException;

    public abstract int getAttributeCount();

    public abstract String getAttributeName(int var1) throws ParseException;

    public String getAttribute(int index) throws ParseException {
        return this.getAttribute(this.getAttributeName(index), null);
    }

    public String getAttribute(String name) throws ParseException {
        return this.getAttribute(name, null, true);
    }

    public String getAttribute(String name, String defaultValue) throws ParseException {
        return this.getAttribute(name, defaultValue, false);
    }

    protected abstract String getAttribute(String var1, String var2, boolean var3) throws ParseException;

    public int getAttributeAsInt(String name) throws ParseException {
        return this.getAttributeAsInt(name, 0, true);
    }

    public int getAttributeAsInt(String name, int defaultValue) throws ParseException {
        return this.getAttributeAsInt(name, defaultValue, false);
    }

    protected int getAttributeAsInt(String name, int defaultValue, boolean isRequired) throws ParseException {
        String value = this.getAttribute(name, null, isRequired);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            }
            catch (Exception var5_5) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    public long getAttributeAsLong(String name) throws ParseException {
        return this.getAttributeAsLong(name, 0, true);
    }

    public long getAttributeAsLong(String name, long defaultValue) throws ParseException {
        return this.getAttributeAsLong(name, defaultValue, false);
    }

    protected long getAttributeAsLong(String name, long defaultValue, boolean isRequired) throws ParseException {
        String value = this.getAttribute(name, null, isRequired);
        if (value != null) {
            try {
                return Long.parseLong(value);
            }
            catch (Exception var6_5) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    public float getAttributeAsFloat(String name) throws ParseException {
        return this.getAttributeAsFloat(name, 0.0f, true);
    }

    public float getAttributeAsFloat(String name, float defaultValue) throws ParseException {
        return this.getAttributeAsFloat(name, defaultValue, false);
    }

    protected float getAttributeAsFloat(String name, float defaultValue, boolean isRequired) throws ParseException {
        String value = this.getAttribute(name, null, isRequired);
        if (value != null) {
            try {
                return Float.parseFloat(value);
            }
            catch (Exception var5_5) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    public double getAttributeAsDouble(String name) throws ParseException {
        return this.getAttributeAsDouble(name, 0.0, true);
    }

    public double getAttributeAsDouble(String name, double defaultValue) throws ParseException {
        return this.getAttributeAsDouble(name, defaultValue, false);
    }

    public double getAttributeAsDouble(String name, double defaultValue, boolean isRequired) throws ParseException {
        String value = this.getAttribute(name, null, isRequired);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            }
            catch (Exception var6_5) {
                // empty catch block
            }
        }
        return defaultValue;
    }

    public int[] getAttributeAsIntArray(String name) throws ParseException {
        return this.getAttributeAsIntArray(name, false);
    }

    protected int[] getAttributeAsIntArray(String name, boolean isRequired) throws ParseException {
        System.out.println("Conversion of String value to int[] not *yet* supported");
        return null;
    }
}

