/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.util;

import java.util.ArrayList;
import lombok.core.util.Each;
import lombok.core.util.Is;

public class Names {
    public static String interfaceName(String s) {
        if (Is.empty(s) || s.length() <= 2) {
            return s;
        }
        return s.charAt(0) == 'I' && Character.isUpperCase(s.charAt(1)) && Character.isLowerCase(s.charAt(2)) ? s.substring(1) : s;
    }

    public static String decapitalize(String s) {
        if (s == null) {
            return "";
        }
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static String capitalize(String s) {
        if (s == null) {
            return "";
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String trim(String s) {
        if (s == null) {
            return "";
        }
        return s.trim();
    }

    public static String singular(String s) {
        return s.endsWith("s") ? s.substring(0, s.length() - 1) : s;
    }

    public static String camelCaseToConstant(String fieldName) {
        if (Is.empty(fieldName)) {
            return "";
        }
        char[] chars = fieldName.toCharArray();
        StringBuilder b = new StringBuilder();
        b.append(Character.toUpperCase(chars[0]));
        int iend = chars.length;
        for (int i = 1; i < iend; ++i) {
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                b.append('_');
            } else {
                c = Character.toUpperCase(c);
            }
            b.append(c);
        }
        return b.toString();
    }

    public static /* varargs */ String camelCase(String first, String ... rest) {
        ArrayList<String> nonEmptyStrings = new ArrayList<String>();
        if (Is.notEmpty(first)) {
            nonEmptyStrings.add(first);
        }
        for (String s : Each.elementIn(rest)) {
            if (!Is.notEmpty(s)) continue;
            nonEmptyStrings.add(s);
        }
        return Names.camelCase0(nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]));
    }

    private static String camelCase0(String[] s) {
        if (Is.empty(s)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(s[0]);
        int iend = s.length;
        for (int i = 1; i < iend; ++i) {
            builder.append(Names.capitalize(s[i]));
        }
        return builder.toString();
    }

    private Names() {
    }
}

