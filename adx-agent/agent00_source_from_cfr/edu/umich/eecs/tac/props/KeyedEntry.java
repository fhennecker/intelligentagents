/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import java.io.Serializable;
import se.sics.isl.transport.Transportable;

public interface KeyedEntry<T>
extends Serializable,
Transportable {
    public T getKey();
}

