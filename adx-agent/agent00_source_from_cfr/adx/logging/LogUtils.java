/*
 * Decompiled with CFR 0_110.
 */
package adx.logging;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import tau.tac.adx.users.properties.Age;

public class LogUtils {
    public static Map<Age, String> age2string = new HashMap<Age, String>(){};

    public static String formatDouble(double d, int lhs, int rhs) {
        String dec = new DecimalFormat("0." + new String(new char[Math.max(1, rhs - 1)]).replace("\u0000", "#")).format(d);
        int dec_int = dec.replaceFirst("\\.\\d*", "").length();
        int dec_frac = dec.replaceFirst("\\d*", "").length();
        return String.valueOf(new String(new char[lhs - dec_int]).replace("\u0000", " ")) + dec + new String(new char[rhs - dec_frac]).replace("\u0000", " ");
    }

}

