/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.transport;

import java.util.Hashtable;
import se.sics.isl.transport.Transportable;

public class Context {
    private Context parent;
    private String name;
    private Hashtable registry = new Hashtable();

    public Context(String name) {
        this(name, null);
    }

    public Context(String name, Context parent) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return this.name;
    }

    public Context getParent() {
        return this.parent;
    }

    public String lookupClass(String transportName) {
        String className = (String)this.registry.get(transportName);
        return className == null && this.parent != null ? this.parent.lookupClass(transportName) : className;
    }

    public void addTransportable(String transportName, String className) {
        this.registry.put(transportName, className);
    }

    public void addTransportable(Transportable instance) {
        this.registry.put(instance.getTransportName(), instance.getClass().getName());
    }
}

