/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.util;

public final class Cast {
    public static <T> T uncheckedCast(Object o) {
        return (T)o;
    }

    private Cast() {
    }
}

