/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher;

import java.util.Collection;

public interface TargetMatcher {
    public Collection<String> getAffectedClasses();

    public boolean matches(String var1, String var2, String var3);
}

