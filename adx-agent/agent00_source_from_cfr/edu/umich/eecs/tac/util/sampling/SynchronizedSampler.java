/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.util.sampling;

import edu.umich.eecs.tac.util.sampling.Sampler;

public class SynchronizedSampler<T>
implements Sampler<T> {
    private Sampler<T> sampler;
    private final Object lock;

    public SynchronizedSampler(Sampler<T> sampler) {
        this.sampler = sampler;
        this.lock = new Object();
    }

    @Override
    public T getSample() {
        Object object = this.lock;
        synchronized (object) {
            return this.sampler.getSample();
        }
    }
}

