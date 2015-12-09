/*
 * Decompiled with CFR 0_110.
 */
package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EnumUtils {
    public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> enumClass) {
        LinkedHashMap<String, Enum> map = new LinkedHashMap<String, Enum>();
        for (Enum e : (Enum[])enumClass.getEnumConstants()) {
            map.put(e.name(), e);
        }
        return map;
    }

    public static <E extends Enum<E>> List<E> getEnumList(Class<E> enumClass) {
        return new ArrayList<E>(Arrays.asList(enumClass.getEnumConstants()));
    }

    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
        if (enumName == null) {
            return false;
        }
        try {
            Enum.valueOf(enumClass, enumName);
            return true;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String enumName) {
        if (enumName == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, enumName);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static <E extends Enum<E>> long generateBitVector(Class<E> enumClass, Iterable<E> values) {
        EnumUtils.checkBitVectorable(enumClass);
        Validate.notNull(values);
        long total = 0;
        for (Enum constant : values) {
            total |= (long)(1 << constant.ordinal());
        }
        return total;
    }

    public static /* varargs */ <E extends Enum<E>> long generateBitVector(Class<E> enumClass, E ... values) {
        Validate.noNullElements(values);
        return EnumUtils.generateBitVector(enumClass, Arrays.asList(values));
    }

    public static <E extends Enum<E>> EnumSet<E> processBitVector(Class<E> enumClass, long value) {
        Enum[] constants = (Enum[])EnumUtils.checkBitVectorable(enumClass).getEnumConstants();
        EnumSet<Enum> results = EnumSet.noneOf(enumClass);
        for (Enum constant : constants) {
            if ((value & (long)(1 << constant.ordinal())) == 0) continue;
            results.add(constant);
        }
        return results;
    }

    private static <E extends Enum<E>> Class<E> checkBitVectorable(Class<E> enumClass) {
        Validate.notNull(enumClass, "EnumClass must be defined.", new Object[0]);
        Enum[] constants = (Enum[])enumClass.getEnumConstants();
        Validate.isTrue(constants != null, "%s does not seem to be an Enum type", enumClass);
        Validate.isTrue(constants.length <= 64, "Cannot store %s %s values in %s bits", constants.length, enumClass.getSimpleName(), 64);
        return enumClass;
    }
}

