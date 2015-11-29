/*
 * Decompiled with CFR 0_110.
 */
package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.ConcurrentInitializer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AtomicInitializer<T>
implements ConcurrentInitializer<T> {
    private final AtomicReference<T> reference = new AtomicReference();

    @Override
    public T get() throws ConcurrentException {
        T result = this.reference.get();
        if (result == null && !this.reference.compareAndSet(null, result = this.initialize())) {
            result = this.reference.get();
        }
        return result;
    }

    protected abstract T initialize() throws ConcurrentException;
}

