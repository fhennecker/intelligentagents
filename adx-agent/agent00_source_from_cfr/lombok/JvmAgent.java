/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.instrument.Instrumentation;

public interface JvmAgent {
    public void runAgent(boolean var1, String var2, Instrumentation var3) throws Throwable;
}

