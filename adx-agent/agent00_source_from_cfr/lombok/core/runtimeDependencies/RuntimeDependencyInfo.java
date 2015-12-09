/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.runtimeDependencies;

import java.util.List;

public interface RuntimeDependencyInfo {
    public List<String> getRuntimeDependentsDescriptions();

    public List<String> getRuntimeDependencies();
}

