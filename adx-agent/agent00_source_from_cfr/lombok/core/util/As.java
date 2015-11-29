/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class As {
    public static /* varargs */ <T> T[] array(T ... elements) {
        return elements;
    }

    public static /* varargs */ <T> List<T> list(T ... elements) {
        ArrayList results = new ArrayList();
        if (elements != null) {
            Collections.addAll(results, elements);
        }
        return results;
    }

    public static /* varargs */ <T> List<T> unmodifiableList(T ... a) {
        return Collections.unmodifiableList(As.list(a));
    }

    public static String string(char[] s) {
        return new String(s);
    }

    public static String string(Object s) {
        return s.toString();
    }

    private As() {
    }
}

