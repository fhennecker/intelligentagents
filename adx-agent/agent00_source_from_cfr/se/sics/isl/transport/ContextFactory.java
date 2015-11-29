/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.transport;

import se.sics.isl.transport.Context;

public interface ContextFactory {
    public Context createContext();

    public Context createContext(Context var1);
}

