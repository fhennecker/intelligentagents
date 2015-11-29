/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class LombokApp {
    public abstract int runApp(List<String> var1) throws Exception;

    public abstract String getAppName();

    public abstract String getAppDescription();

    public List<String> getAppAliases() {
        return Collections.emptyList();
    }

    public boolean isDebugTool() {
        return false;
    }
}

