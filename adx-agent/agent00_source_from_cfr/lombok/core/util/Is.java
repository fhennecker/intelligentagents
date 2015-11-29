/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.util;

import java.util.Collection;
import lombok.core.util.Each;

public final class Is {
    public static boolean empty(String s) {
        if (s == null) {
            return true;
        }
        return s.isEmpty();
    }

    public static boolean empty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean empty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean notEmpty(String s) {
        return !Is.empty(s);
    }

    public static boolean notEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean notEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    public static /* varargs */ boolean oneOf(String s, String ... candidates) {
        for (String candidate : Each.elementIn(candidates)) {
            if (!candidate.equals(s)) continue;
            return true;
        }
        return false;
    }

    public static /* varargs */ boolean oneOf(Object o, Class<?> ... clazzes) {
        for (Class clazz : Each.elementIn(clazzes)) {
            if (!clazz.isInstance(o)) continue;
            return true;
        }
        return false;
    }

    public static /* varargs */ boolean noneOf(Object o, Class<?> ... clazzes) {
        for (Class clazz : Each.elementIn(clazzes)) {
            if (!clazz.isInstance(o)) continue;
            return false;
        }
        return true;
    }

    private Is() {
    }
}

