/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.util;

public final class Arrays {
    public static <T> T[] copy(T[] array) {
        return java.util.Arrays.copyOf(array, array.length);
    }

    public static boolean sameSize(Object[] array1, Object[] array2) {
        if (array1 == null || array2 == null) {
            return false;
        }
        return array1.length == array2.length;
    }

    public static <T> T[] resize(T[] array, int newSize) {
        return java.util.Arrays.copyOf(array, newSize);
    }

    private Arrays() {
    }
}

