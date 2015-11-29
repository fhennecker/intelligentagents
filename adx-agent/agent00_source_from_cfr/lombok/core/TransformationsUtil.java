/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import lombok.core.AnnotationValues;
import lombok.experimental.Accessors;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TransformationsUtil {
    public static final Pattern NON_NULL_PATTERN = Pattern.compile("^(?:nonnull)$", 2);
    public static final Pattern NULLABLE_PATTERN = Pattern.compile("^(?:nullable|checkfornull)$", 2);

    private TransformationsUtil() {
    }

    private static CharSequence removePrefix(CharSequence fieldName, String[] prefixes) {
        if (prefixes == null || prefixes.length == 0) {
            return fieldName;
        }
        block0 : for (String prefix : prefixes) {
            if (prefix.length() == 0) {
                return fieldName;
            }
            if (fieldName.length() <= prefix.length()) continue;
            for (int i = 0; i < prefix.length(); ++i) {
                if (fieldName.charAt(i) != prefix.charAt(i)) continue block0;
            }
            char followupChar = fieldName.charAt(prefix.length());
            if (Character.isLetter(prefix.charAt(prefix.length() - 1)) && Character.isLowerCase(followupChar)) continue;
            return "" + Character.toLowerCase(followupChar) + fieldName.subSequence(prefix.length() + 1, fieldName.length());
        }
        return null;
    }

    public static String toGetterName(AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        if (fieldName.length() == 0) {
            return null;
        }
        Accessors ac = accessors.getInstance();
        if ((fieldName = TransformationsUtil.removePrefix(fieldName, ac.prefix())) == null) {
            return null;
        }
        if (ac.fluent()) {
            return fieldName.toString();
        }
        if (isBoolean && fieldName.toString().startsWith("is") && fieldName.length() > 2 && !Character.isLowerCase(fieldName.charAt(2))) {
            return fieldName.toString();
        }
        return TransformationsUtil.buildName(isBoolean ? "is" : "get", fieldName.toString());
    }

    public static String toSetterName(AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        if (fieldName.length() == 0) {
            return null;
        }
        Accessors ac = accessors.getInstance();
        if ((fieldName = TransformationsUtil.removePrefix(fieldName, ac.prefix())) == null) {
            return null;
        }
        String fName = fieldName.toString();
        if (ac.fluent()) {
            return fName;
        }
        if (isBoolean && fName.startsWith("is") && fieldName.length() > 2 && !Character.isLowerCase(fName.charAt(2))) {
            return "set" + fName.substring(2);
        }
        return TransformationsUtil.buildName("set", fName);
    }

    public static List<String> toAllGetterNames(AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        if (fieldName.length() == 0) {
            return Collections.emptyList();
        }
        if (!isBoolean) {
            String getterName = TransformationsUtil.toGetterName(accessors, fieldName, false);
            return getterName == null ? Collections.emptyList() : Collections.singletonList(getterName);
        }
        Accessors acc = accessors.getInstance();
        if ((fieldName = TransformationsUtil.removePrefix(fieldName, acc.prefix())) == null || fieldName.length() == 0) {
            return Collections.emptyList();
        }
        List<String> baseNames = TransformationsUtil.toBaseNames(fieldName, isBoolean, acc.fluent());
        HashSet<String> names = new HashSet<String>();
        for (String baseName : baseNames) {
            if (acc.fluent()) {
                names.add(baseName);
                continue;
            }
            names.add(TransformationsUtil.buildName("is", baseName));
            names.add(TransformationsUtil.buildName("get", baseName));
        }
        return new ArrayList<String>(names);
    }

    public static List<String> toAllSetterNames(AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        if (!isBoolean) {
            String setterName = TransformationsUtil.toSetterName(accessors, fieldName, false);
            return setterName == null ? Collections.emptyList() : Collections.singletonList(setterName);
        }
        Accessors acc = accessors.getInstance();
        if ((fieldName = TransformationsUtil.removePrefix(fieldName, acc.prefix())) == null) {
            return Collections.emptyList();
        }
        List<String> baseNames = TransformationsUtil.toBaseNames(fieldName, isBoolean, acc.fluent());
        HashSet<String> names = new HashSet<String>();
        for (String baseName : baseNames) {
            if (acc.fluent()) {
                names.add(baseName);
                continue;
            }
            names.add(TransformationsUtil.buildName("set", baseName));
        }
        return new ArrayList<String>(names);
    }

    private static List<String> toBaseNames(CharSequence fieldName, boolean isBoolean, boolean fluent) {
        ArrayList<String> baseNames = new ArrayList<String>();
        baseNames.add(fieldName.toString());
        String fName = fieldName.toString();
        if (fName.startsWith("is") && fName.length() > 2 && !Character.isLowerCase(fName.charAt(2))) {
            String baseName = fName.substring(2);
            if (fluent) {
                baseNames.add("" + Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1));
            } else {
                baseNames.add(baseName);
            }
        }
        return baseNames;
    }

    private static String buildName(String prefix, String suffix) {
        if (suffix.length() == 0) {
            return prefix;
        }
        if (prefix.length() == 0) {
            return suffix;
        }
        char first = suffix.charAt(0);
        if (Character.isLowerCase(first)) {
            boolean useUpperCase = suffix.length() > 2 && (Character.isTitleCase(suffix.charAt(1)) || Character.isUpperCase(suffix.charAt(1)));
            Object[] arrobject = new Object[2];
            arrobject[0] = Character.valueOf(useUpperCase ? Character.toUpperCase(first) : Character.toTitleCase(first));
            arrobject[1] = suffix.subSequence(1, suffix.length());
            suffix = String.format("%s%s", arrobject);
        }
        return String.format("%s%s", prefix, suffix);
    }
}

