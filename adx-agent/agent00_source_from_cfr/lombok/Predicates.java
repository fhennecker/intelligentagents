/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import lombok.Functions;

public final class Predicates {
    private Predicates() {
    }

    public static abstract class Predicate1<T1>
    extends Functions.Function1<T1, Boolean> {
        @Override
        public final Boolean apply(T1 t1) {
            return this.evaluate(t1);
        }

        public abstract boolean evaluate(T1 var1);
    }

}

