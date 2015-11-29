/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.io.PrintStream;

public interface DiagnosticsReceiver {
    public static final DiagnosticsReceiver CONSOLE = new DiagnosticsReceiver(){

        public void addError(String message) {
            System.err.println("Error: " + message);
        }

        public void addWarning(String message) {
            System.out.println("Warning: " + message);
        }
    };

    public void addError(String var1);

    public void addWarning(String var1);

}

