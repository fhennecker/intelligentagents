/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.demand;

import java.util.Iterator;
import tau.tac.adx.demand.Accumulator;

public class AccumulatorImpl {
    public static <A> A accumulate(Accumulator<A> accumulator, Iterable<? extends A> i, A init) {
        A result = init;
        Iterator<A> iter = i.iterator();
        while (iter.hasNext()) {
            result = accumulator.accumulate(result, iter.next());
        }
        return result;
    }
}

