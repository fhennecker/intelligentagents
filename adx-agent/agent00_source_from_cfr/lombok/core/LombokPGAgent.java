/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.lang.instrument.Instrumentation;
import lombok.core.Agent;

public abstract class LombokPGAgent
extends Agent {
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Throwable {
        Agent.agentmain(agentArgs, instrumentation);
        LombokPGAgent.runMoreAgents(agentArgs, instrumentation, true);
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) throws Throwable {
        Agent.premain(agentArgs, instrumentation);
        LombokPGAgent.runMoreAgents(agentArgs, instrumentation, false);
    }

    private static void runMoreAgents(String agentArgs, Instrumentation instrumentation, boolean injected) throws Throwable {
        LombokPGEclipsePatcherInfo info = new LombokPGEclipsePatcherInfo();
        try {
            Class agentClass = Class.forName(info.className());
            Agent agent = (Agent)agentClass.newInstance();
            agent.runAgent(agentArgs, instrumentation, injected);
        }
        catch (Throwable t) {
            info.problem(t, instrumentation);
        }
    }

    private static class LombokPGEclipsePatcherInfo
    extends AgentInfo {
        private LombokPGEclipsePatcherInfo() {
            super();
        }

        @Override
        String className() {
            return "lombok.eclipse.agent.LombokPGEclipsePatcher";
        }
    }

    private static abstract class AgentInfo {
        private AgentInfo() {
        }

        abstract String className();

        void problem(Throwable t, Instrumentation instrumentation) throws Throwable {
            if (t instanceof ClassNotFoundException) {
                return;
            }
            if (t instanceof ClassCastException) {
                throw new InternalError("Lombok-PG bug. Class: " + this.className() + " is not an implementation of lombok.core.Agent");
            }
            if (t instanceof IllegalAccessError) {
                throw new InternalError("Lombok-PG bug. Class: " + this.className() + " is not public");
            }
            if (t instanceof InstantiationException) {
                throw new InternalError("Lombok-PG bug. Class: " + this.className() + " is not concrete or has no public no-args constructor");
            }
            throw t;
        }
    }

}

