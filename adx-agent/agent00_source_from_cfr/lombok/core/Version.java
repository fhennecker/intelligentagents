/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.io.PrintStream;

public class Version {
    private static final String VERSION = "0.11.3";
    private static final String RELEASE_NAME = "Dashing Kakapo";

    private Version() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            System.out.printf("Lombok %s\n", Version.getFullVersion());
        } else {
            System.out.println("0.11.3");
        }
    }

    public static String getVersion() {
        return "0.11.3";
    }

    public static String getReleaseName() {
        return "Dashing Kakapo";
    }

    public static String getFullVersion() {
        return String.format("v%s \"%s\"", "0.11.3", "Dashing Kakapo");
    }
}

