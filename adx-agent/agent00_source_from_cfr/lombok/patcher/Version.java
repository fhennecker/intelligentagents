/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher;

import java.io.PrintStream;

public class Version {
    private static final String VERSION = "0.6";

    private Version() {
    }

    public static void main(String[] args) {
        System.out.println("0.6");
    }

    public static String getVersion() {
        return "0.6";
    }
}

