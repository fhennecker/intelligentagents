/*
 * Decompiled with CFR 0_110.
 */
package lombok.eclipse.agent;

import java.lang.instrument.Instrumentation;
import lombok.core.Agent;
import lombok.eclipse.agent.PatchAutoGenMethodStub;
import lombok.eclipse.agent.PatchVisibleForTesting;
import lombok.eclipse.agent.PatchYield;
import lombok.patcher.ScriptManager;
import lombok.patcher.equinox.EquinoxClassLoader;

public final class LombokPGEclipsePatcher
extends Agent {
    @Override
    public void runAgent(String agentArgs, Instrumentation instrumentation, boolean injected) throws Exception {
        String[] args = agentArgs == null ? new String[]{} : agentArgs.split(":");
        boolean forceEcj = false;
        boolean forceEclipse = false;
        for (String arg : args) {
            if (arg.trim().equalsIgnoreCase("ECJ")) {
                forceEcj = true;
            }
            if (!arg.trim().equalsIgnoreCase("ECLIPSE")) continue;
            forceEclipse = true;
        }
        if (forceEcj && forceEclipse) {
            forceEcj = false;
            forceEclipse = false;
        }
        boolean ecj = forceEcj ? true : (forceEclipse ? false : injected);
        this.registerPatchScripts(instrumentation, injected, ecj);
    }

    private void registerPatchScripts(Instrumentation instrumentation, boolean reloadExistingClasses, boolean ecjOnly) {
        ScriptManager sm = new ScriptManager();
        sm.registerTransformer(instrumentation);
        if (!ecjOnly) {
            EquinoxClassLoader.addPrefix("lombok.");
            EquinoxClassLoader.registerScripts(sm);
        }
        this.patchEcjTransformers(sm, ecjOnly);
        if (reloadExistingClasses) {
            sm.reloadClasses(instrumentation);
        }
    }

    private void patchEcjTransformers(ScriptManager sm, boolean ecj) {
        PatchAutoGenMethodStub.addPatches(sm, ecj);
        PatchVisibleForTesting.addPatches(sm, ecj);
        PatchYield.addPatches(sm, ecj);
    }
}

