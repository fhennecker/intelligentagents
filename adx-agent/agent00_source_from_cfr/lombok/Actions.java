/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import lombok.TypeArguments;

public final class Actions {
    private Actions() {
    }

    public static abstract class Action8<T1, T2, T3, T4, T5, T6, T7, T8> {
        public abstract void apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6, T7 var7, T8 var8);

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

    public static abstract class Action7<T1, T2, T3, T4, T5, T6, T7> {
        public abstract void apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6, T7 var7);

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

    public static abstract class Action6<T1, T2, T3, T4, T5, T6> {
        public abstract void apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6);

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

    public static abstract class Action5<T1, T2, T3, T4, T5> {
        public abstract void apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5);

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

    public static abstract class Action4<T1, T2, T3, T4> {
        public abstract void apply(T1 var1, T2 var2, T3 var3, T4 var4);

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

    public static abstract class Action3<T1, T2, T3> {
        public abstract void apply(T1 var1, T2 var2, T3 var3);

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

    public static abstract class Action2<T1, T2> {
        public abstract void apply(T1 var1, T2 var2);

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }

        public final Class<?> getParameterType2() {
            return TypeArguments.getClassFor(this.getClass(), 1);
        }
    }

    public static abstract class Action1<T1> {
        public abstract void apply(T1 var1);

        public final Class<?> getParameterType1() {
            return TypeArguments.getClassFor(this.getClass(), 0);
        }
    }

    public static abstract class Action0 {
        public abstract void apply();
    }

}

