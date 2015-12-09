/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.util.sampling;

import edu.umich.eecs.tac.util.sampling.MutableSampler;

public class SynchronizedMutableSampler<T>
implements MutableSampler<T> {
    private MutableSampler<T> mutableSampler;
    private final Object lock;

    public SynchronizedMutableSampler(MutableSampler<T> mutableSampler) {
        this.mutableSampler = mutableSampler;
        this.lock = new Object();
    }

    @Override
    public void addState(double weight, T state) {
        Object object = this.lock;
        synchronized (object) {
            this.mutableSampler.addState(weight, state);
        }
    }

    @Override
    public T getSample() {
        Object object = this.lock;
        synchronized (object) {
            return this.mutableSampler.getSample();
        }
    }
}

