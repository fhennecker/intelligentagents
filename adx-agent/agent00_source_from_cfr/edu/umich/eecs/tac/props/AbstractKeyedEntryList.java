/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.props;

import edu.umich.eecs.tac.props.AbstractTransportableEntryListBacking;
import edu.umich.eecs.tac.props.KeyIterator;
import edu.umich.eecs.tac.props.KeyedEntry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class AbstractKeyedEntryList<T, S extends KeyedEntry<T>>
extends AbstractTransportableEntryListBacking<S>
implements Iterable<T> {
    public final int indexForEntry(T key) {
        int i = 0;
        while (i < this.size()) {
            if (((KeyedEntry)this.getEntry(i)).getKey().equals(key)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    @Override
    public final Iterator<T> iterator() {
        return new KeyIterator(this.getEntries().iterator());
    }

    public final boolean containsKey(T key) {
        if (this.indexForEntry(key) > -1) {
            return true;
        }
        return false;
    }

    protected final int addKey(T key) throws NullPointerException {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        return this.addEntry(this.createEntry(key));
    }

    protected abstract S createEntry(T var1);

    public final Set<T> keys() {
        HashSet<T> keys = new HashSet<T>();
        int i = 0;
        while (i < this.size()) {
            keys.add(((KeyedEntry)this.getEntry(i)).getKey());
            ++i;
        }
        return keys;
    }

    protected final T getKey(int index) throws IndexOutOfBoundsException {
        return ((KeyedEntry)this.getEntry(index)).getKey();
    }

    public final S getEntry(T key) {
        int index = this.indexForEntry(key);
        if (index < 0) {
            return null;
        }
        return (S)((KeyedEntry)this.getEntry(index));
    }
}

