/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.KeyedEntry;
import java.util.Iterator;

public class KeyIterator<T>
implements Iterator<T> {
    private Iterator<? extends KeyedEntry<? extends T>> delegateIterator;

    public KeyIterator(Iterator<? extends KeyedEntry<? extends T>> delegateIterator) {
        if (delegateIterator == null) {
            throw new NullPointerException("delegate iterator cannot be null");
        }
        this.delegateIterator = delegateIterator;
    }

    @Override
    public final boolean hasNext() {
        return this.delegateIterator.hasNext();
    }

    @Override
    public final T next() {
        return this.delegateIterator.next().getKey();
    }

    @Override
    public final void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("remove is not supported in this iterator");
    }
}

