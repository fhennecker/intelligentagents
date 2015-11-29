/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.util;

public class FormatUtils {
    private FormatUtils() {
    }

    public static String formatAmount(long amount) {
        long aAmount = amount >= 0 ? amount : - amount;
        String str = Long.toString(aAmount);
        boolean meg = str.length() > 6;
        StringBuffer sb = new StringBuffer();
        if (amount < 0) {
            sb.append('-');
        }
        int slen = str.length() - (meg ? 6 : 0);
        int pos = 0;
        int i = slen;
        while (i > 0) {
            sb.append(str.charAt(slen - i));
            if (i > 1 && (2 + slen - pos) % 3 == 0) {
                sb.append(' ');
            }
            ++pos;
            --i;
        }
        if (pos < 4 && meg) {
            sb.append('.');
            while (pos < 4) {
                sb.append(str.charAt(slen++));
                ++pos;
            }
        }
        if (meg) {
            sb.append(" M");
        }
        return sb.toString();
    }

    public static String formatLong(long value) {
        boolean isNegative;
        boolean bl = isNegative = value < 0;
        if (isNegative) {
            value = - value;
        }
        char[] buffer = new char[26];
        int index = buffer.length - 1;
        if (value == 0) {
            buffer[index--] = 48;
        } else {
            int count = 0;
            while (value > 0 && index >= 0) {
                if (count % 3 == 0 && count > 0 && index > 0) {
                    buffer[index--] = 32;
                }
                buffer[index--] = (char)(48 + value % 10);
                value /= 10;
                ++count;
            }
        }
        if (isNegative && index >= 0) {
            buffer[index--] = 45;
        }
        return new String(buffer, index + 1, buffer.length - index - 1);
    }

    public static String formatLong(long value, String separator) {
        boolean isNegative;
        boolean bl = isNegative = value < 0;
        if (isNegative) {
            value = - value;
        }
        int sepLen = separator.length();
        int maxLen = 20 + 6 * sepLen;
        char[] buffer = new char[maxLen];
        int index = maxLen - 1;
        if (value == 0) {
            buffer[index--] = 48;
        } else {
            int count = 0;
            while (value > 0 && index >= 0) {
                if (count % 3 == 0 && count > 0 && index > sepLen) {
                    separator.getChars(0, sepLen, buffer, (index -= sepLen) + 1);
                }
                buffer[index--] = (char)(48 + value % 10);
                value /= 10;
                ++count;
            }
        }
        if (isNegative && index >= 0) {
            buffer[index--] = 45;
        }
        return new String(buffer, index + 1, maxLen - index - 1);
    }

    public static String formatDouble(double value) {
        boolean isNegative;
        boolean bl = isNegative = value < 0.0 || value == 0.0 && 1.0 / value < 0.0;
        if (isNegative) {
            value = - value;
        }
        char[] buffer = new char[29];
        long intValue = (long)value;
        int decValue = (int)((value - (double)intValue) * 100.0 + 0.5);
        int index = buffer.length - 1;
        buffer[index--] = (char)(48 + decValue % 10);
        buffer[index--] = (char)(48 + decValue / 10 % 10);
        buffer[index--] = 46;
        if (intValue == 0) {
            buffer[index--] = 48;
        } else {
            int count = 0;
            while (intValue > 0 && index >= 0) {
                if (count % 3 == 0 && count > 0 && index > 0) {
                    buffer[index--] = 32;
                }
                buffer[index--] = (char)(48 + intValue % 10);
                intValue /= 10;
                ++count;
            }
        }
        if (isNegative && index >= 0) {
            buffer[index--] = 45;
        }
        return new String(buffer, index + 1, buffer.length - index - 1);
    }

    public static String formatDouble(double value, String separator) {
        boolean isNegative;
        boolean bl = isNegative = value < 0.0 || value == 0.0 && 1.0 / value < 0.0;
        if (isNegative) {
            value = - value;
        }
        int sepLen = separator.length();
        int maxLen = 20 + 6 * sepLen + 3;
        char[] buffer = new char[maxLen];
        long intValue = (long)value;
        int decValue = (int)((value - (double)intValue) * 100.0 + 0.5);
        int index = maxLen - 1;
        buffer[index--] = (char)(48 + decValue % 10);
        buffer[index--] = (char)(48 + decValue / 10 % 10);
        buffer[index--] = 46;
        if (intValue == 0) {
            buffer[index--] = 48;
        } else {
            int count = 0;
            while (intValue > 0 && index >= 0) {
                if (count % 3 == 0 && count > 0 && index > sepLen) {
                    separator.getChars(0, sepLen, buffer, (index -= sepLen) + 1);
                }
                buffer[index--] = (char)(48 + intValue % 10);
                intValue /= 10;
                ++count;
            }
        }
        if (isNegative && index >= 0) {
            buffer[index--] = 45;
        }
        return new String(buffer, index + 1, maxLen - index - 1);
    }
}

