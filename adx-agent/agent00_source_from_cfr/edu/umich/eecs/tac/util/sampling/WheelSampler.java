/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.util.sampling;

import edu.umich.eecs.tac.util.sampling.MutableSampler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WheelSampler<T>
implements MutableSampler<T> {
    private final Random random;
    private final List<Slot<T>> slots;
    private boolean dirty;
    private double sum;

    public WheelSampler() {
        this(new Random());
    }

    public WheelSampler(Random random) {
        this.random = random;
        this.slots = new ArrayList<Slot<T>>();
        this.dirty = true;
    }

    @Override
    public void addState(double weight, T state) {
        this.slots.add(new Slot<T>(weight, state));
        this.dirty = true;
    }

    @Override
    public T getSample() {
        if (this.slots.isEmpty()) {
            return null;
        }
        if (this.slots.size() == 1) {
            return this.slots.get(0).getState();
        }
        if (this.dirty) {
            this.clean();
        }
        double dart = this.random.nextDouble() * this.sum;
        int index = -1;
        while ((dart -= this.slots.get(++index).getWeight()) > 0.0 && index < this.slots.size() - 1) {
        }
        return this.slots.get(index).getState();
    }

    private void clean() {
        Collections.sort(this.slots, Collections.reverseOrder());
        this.sum = 0.0;
        for (Slot<T> slot : this.slots) {
            this.sum += slot.getWeight();
        }
        this.dirty = false;
    }

    private static final class Slot<T>
    implements Comparable<Slot<T>> {
        protected final double weight;
        protected final T state;

        public Slot(double weight, T state) throws IllegalArgumentException {
            if (weight < 0.0) {
                throw new IllegalArgumentException("weight cannot be null");
            }
            this.weight = weight;
            this.state = state;
        }

        @Override
        public int compareTo(Slot<T> slot) {
            return Double.compare(this.weight, slot.weight);
        }

        public double getWeight() {
            return this.weight;
        }

        public T getState() {
            return this.state;
        }
    }

}

