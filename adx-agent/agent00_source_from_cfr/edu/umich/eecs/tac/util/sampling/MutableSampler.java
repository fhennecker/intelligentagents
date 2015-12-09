/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.util.sampling;

import edu.umich.eecs.tac.util.sampling.Sampler;

public interface MutableSampler<T>
extends Sampler<T> {
    public void addState(double var1, T var3);
}

