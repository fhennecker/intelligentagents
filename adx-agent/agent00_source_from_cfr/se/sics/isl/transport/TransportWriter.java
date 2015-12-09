/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.transport;

import se.sics.isl.transport.Transportable;

public abstract class TransportWriter {
    public void addConstant(String constant) {
    }

    public abstract int getNodeLevel();

    public abstract TransportWriter node(String var1);

    public abstract TransportWriter endNode(String var1);

    public TransportWriter attr(String name, int value) {
        return this.attr(name, Integer.toString(value));
    }

    public TransportWriter attr(String name, long value) {
        return this.attr(name, Long.toString(value));
    }

    public TransportWriter attr(String name, float value) {
        return this.attr(name, Float.toString(value));
    }

    public TransportWriter attr(String name, double value) {
        return this.attr(name, Double.toString(value));
    }

    public TransportWriter attr(String name, int[] value) {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        int j = 0;
        int m = value.length;
        while (j < m) {
            if (j > 0) {
                sb.append(',');
            }
            sb.append("" + value[j]);
            ++j;
        }
        sb.append(']');
        return this.attr(name, sb.toString());
    }

    public abstract TransportWriter attr(String var1, String var2);

    public TransportWriter write(Transportable object) {
        String nodeName = object.getTransportName();
        this.node(nodeName);
        int nodeLevel = this.getNodeLevel();
        object.write(this);
        if (nodeLevel != this.getNodeLevel()) {
            throw new IllegalStateException("wrong node level " + this.getNodeLevel() + " (expected " + nodeLevel + ") for transportable " + object.getClass().getName());
        }
        this.endNode(nodeName);
        return this;
    }
}

