/*
 * Decompiled with CFR 0_110.
 */
package lombok;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Lombok {
    public static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) {
            throw new NullPointerException("t");
        }
        Lombok.sneakyThrow0(t);
        return null;
    }

    private static <T extends Throwable> void sneakyThrow0(Throwable t) throws Throwable {
        throw t;
    }

    public static <T> T preventNullAnalysis(T value) {
        return value;
    }
}

