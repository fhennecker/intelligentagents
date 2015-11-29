/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public abstract class Agent {
    private static final List<AgentInfo> AGENTS = Collections.unmodifiableList(Arrays.asList(new NetbeansPatcherInfo(), new EclipsePatcherInfo()));

    protected abstract void runAgent(String var1, Instrumentation var2, boolean var3) throws Exception;

    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Throwable {
        Agent.runAgents(agentArgs, instrumentation, true);
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) throws Throwable {
        Agent.runAgents(agentArgs, instrumentation, false);
    }

    private static void runAgents(String agentArgs, Instrumentation instrumentation, boolean injected) throws Throwable {
        for (AgentInfo info : AGENTS) {
            try {
                Class<?> agentClass = Class.forName(info.className());
                Agent agent = (Agent)agentClass.newInstance();
                agent.runAgent(agentArgs, instrumentation, injected);
            }
            catch (Throwable t) {
                info.problem(t, instrumentation);
            }
        }
    }

    private static class EclipsePatcherInfo
    extends AgentInfo {
        private EclipsePatcherInfo() {
            super();
        }

        String className() {
            return "lombok.eclipse.agent.EclipsePatcher";
        }
    }

    private static class NetbeansPatcherInfo
    extends AgentInfo {
        private NetbeansPatcherInfo() {
            super();
        }

        String className() {
            return "lombok.netbeans.agent.NetbeansPatcher";
        }

        void problem(Throwable in, Instrumentation instrumentation) throws Throwable {
            try {
                super.problem(in, instrumentation);
            }
            catch (InternalError ie) {
                throw ie;
            }
            catch (Throwable t) {
                final String error = t instanceof UnsupportedClassVersionError ? "Lombok only works on netbeans if you start netbeans using a 1.6 or higher JVM.\nChange your platform's default JVM, or edit etc/netbeans.conf\nand explicitly tell netbeans your 1.6 JVM's location." : "Lombok disabled due to error: " + t;
                instrumentation.addTransformer(new ClassFileTransformer(){

                    @Override
                    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                        if ("org/netbeans/modules/java/source/parsing/JavacParser".equals(className)) {
                            SwingUtilities.invokeLater(new Runnable(){

                                public void run() {
                                    JOptionPane.showMessageDialog(null, error, "Lombok Disabled", 0);
                                }
                            });
                        }
                        return null;
                    }

                });
            }
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
                throw new InternalError("Lombok bug. Class: " + this.className() + " is not an implementation of lombok.core.Agent");
            }
            if (t instanceof IllegalAccessError) {
                throw new InternalError("Lombok bug. Class: " + this.className() + " is not public");
            }
            if (t instanceof InstantiationException) {
                throw new InternalError("Lombok bug. Class: " + this.className() + " is not concrete or has no public no-args constructor");
            }
            throw t;
        }
    }

}

