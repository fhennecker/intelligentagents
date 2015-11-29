/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import lombok.TypeArguments;

public final class Functions {
    private Functions() {
    }

    public static abstract class Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
        public abstract R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6, T7 var7, T8 var8);

        public final Class<?> getReturnType() {
            return TypeArguments.getClassFor(this.getClass(), 8);
        }

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }

        public final Class<?> getParameterType2() {
            return TypeArguments.getClassFor(this.getClass(), 1);
        }

        public final Class<?> getParameterType3() {
            return TypeArguments.getClassFor(this.getClass(), 2);
        }

        public final Class<?> getParameterType4() {
            return TypeArguments.getClassFor(this.getClass(), 3);
        }

        public final Class<?> getParameterType5() {
            return TypeArguments.getClassFor(this.getClass(), 4);
        }

        public final Class<?> getParameterType6() {
            return TypeArguments.getClassFor(this.getClass(), 5);
        }

        public final Class<?> getParameterType7() {
            return TypeArguments.getClassFor(this.getClass(), 6);
        }

        public Class<?> getParameterType8() {
            return TypeArguments.getClassFor(this.getClass(), 7);
        }
    }

    public static abstract class Function7<T1, T2, T3, T4, T5, T6, T7, R> {
        public abstract R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6, T7 var7);

        public final Class<?> getReturnType() {
            return TypeArguments.getClassFor(this.getClass(), 7);
        }

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }

        public final Class<?> getParameterType2() {
            return TypeArguments.getClassFor(this.getClass(), 1);
        }

        public final Class<?> getParameterType3() {
            return TypeArguments.getClassFor(this.getClass(), 2);
        }

        public final Class<?> getParameterType4() {
            return TypeArguments.getClassFor(this.getClass(), 3);
        }

        public final Class<?> getParameterType5() {
            return TypeArguments.getClassFor(this.getClass(), 4);
        }

        public final Class<?> getParameterType6() {
            return TypeArguments.getClassFor(this.getClass(), 5);
        }

        public final Class<?> getParameterType7() {
            return TypeArguments.getClassFor(this.getClass(), 6);
        }
    }

    public static abstract class Function6<T1, T2, T3, T4, T5, T6, R> {
        public abstract R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6);

        public final Class<?> getReturnType() {
            return TypeArguments.getClassFor(this.getClass(), 6);
        }

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }

        public final Class<?> getParameterType2() {
            return TypeArguments.getClassFor(this.getClass(), 1);
        }

        public final Class<?> getParameterType3() {
            return TypeArguments.getClassFor(this.getClass(), 2);
        }

        public final Class<?> getParameterType4() {
            return TypeArguments.getClassFor(this.getClass(), 3);
        }

        public final Class<?> getParameterType5() {
            return TypeArguments.getClassFor(this.getClass(), 4);
        }

        public final Class<?> getParameterType6() {
            return TypeArguments.getClassFor(this.getClass(), 5);
        }
    }

    public static abstract class Function5<T1, T2, T3, T4, T5, R> {
        public abstract R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5);

        public final Class<?> getReturnType() {
            return TypeArguments.getClassFor(this.getClass(), 5);
        }

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }

        public final Class<?> getParameterType2() {
            return TypeArguments.getClassFor(this.getClass(), 1);
        }

        public final Class<?> getParameterType3() {
            return TypeArguments.getClassFor(this.getClass(), 2);
        }

        public final Class<?> getParameterType4() {
            return TypeArguments.getClassFor(this.getClass(), 3);
        }

        public final Class<?> getParameterType5() {
            return TypeArguments.getClassFor(this.getClass(), 4);
        }
    }

    public static abstract class Function4<T1, T2, T3, T4, R> {
        public abstract R apply(T1 var1, T2 var2, T3 var3, T4 var4);

        public final Class<?> getReturnType() {
            return TypeArguments.getClassFor(this.getClass(), 4);
        }

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }

        public final Class<?> getParameterType2() {
            return TypeArguments.getClassFor(this.getClass(), 1);
        }

        public final Class<?> getParameterType3() {
            return TypeArguments.getClassFor(this.getClass(), 2);
        }

        public final Class<?> getParameterType4() {
            return TypeArguments.getClassFor(this.getClass(), 3);
        }
    }

    public static abstract class Function3<T1, T2, T3, R> {
        public abstract R apply(T1 var1, T2 var2, T3 var3);

        public final Class<?> getReturnType() {
            return TypeArguments.getClassFor(this.getClass(), 3);
        }

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }

        public final Class<?> getParameterType2() {
            return TypeArguments.getClassFor(this.getClass(), 1);
        }

        public final Class<?> getParameterType3() {
            return TypeArguments.getClassFor(this.getClass(), 2);
        }
    }

    public static abstract class Function2<T1, T2, R> {
        public abstract R apply(T1 var1, T2 var2);

        public final Class<?> getReturnType() {
            return TypeArguments.getClassFor(this.getClass(), 2);
        }

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }

        public final Class<?> getParameterType2() {
            return TypeArguments.getClassFor(this.getClass(), 1);
        }
    }

    public static abstract class Function1<T1, R> {
        public abstract R apply(T1 var1);

        public final Class<?> getReturnType() {
            return TypeArguments.getClassFor(this.getClass(), 1);
        }

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }
    }

    public static abstract class Function0<R> {
        public abstract R apply();

        public final Class<?> getReturnType() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }
    }

}

