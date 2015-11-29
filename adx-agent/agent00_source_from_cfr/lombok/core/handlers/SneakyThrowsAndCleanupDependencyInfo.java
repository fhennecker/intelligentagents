/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.util.Arrays;
import java.util.List;
import lombok.core.runtimeDependencies.RuntimeDependencyInfo;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SneakyThrowsAndCleanupDependencyInfo
implements RuntimeDependencyInfo {
    @Override
    public List<String> getRuntimeDependencies() {
        return Arrays.asList("lombok/Lombok.class");
    }

    @Override
    public List<String> getRuntimeDependentsDescriptions() {
        return Arrays.asList("@SneakyThrows (only when delomboking - using @SneakyThrows in code that is compiled with lombok on the classpath does not create the dependency)", "@Cleanup (only when delomboking - using @Cleanup in code that is compiled with lombok on the classpath does not create the dependency)");
    }
}

