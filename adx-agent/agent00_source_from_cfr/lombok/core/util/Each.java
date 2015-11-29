/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Each {
    public static <T> Collection<T> elementIn(Collection<T> elements) {
        return elements == null ? Collections.emptyList() : elements;
    }

    public static /* varargs */ <T> Collection<T> elementIn(T ... elements) {
        return elements == null ? Collections.emptyList() : Arrays.asList(elements);
    }

    private Each() {
    }
}

