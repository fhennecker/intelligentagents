/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.ui.text.java.IJavaCompletionProposal
 */
package lombok.eclipse.agent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Lombok;
import lombok.eclipse.agent.PatchExtensionMethodCompletionProposal;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;

public class PatchExtensionMethodCompletionProposalPortal {
    private static final String COMPLETION_PROPOSAL_COLLECTOR = "org.eclipse.jdt.ui.text.java.CompletionProposalCollector";
    private static final String I_JAVA_COMPLETION_PROPOSAL_ARRAY = "[Lorg.eclipse.jdt.ui.text.java.IJavaCompletionProposal;";

    public static IJavaCompletionProposal[] getJavaCompletionProposals(Object[] javaCompletionProposals, Object completionProposalCollector) {
        try {
            return (IJavaCompletionProposal[])ReflectionForUi.getJavaCompletionProposals.invoke(null, javaCompletionProposals, completionProposalCollector);
        }
        catch (NoClassDefFoundError e) {
            return (IJavaCompletionProposal[])javaCompletionProposals;
        }
        catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
        catch (InvocationTargetException e) {
            throw Lombok.sneakyThrow(e.getCause());
        }
        catch (NullPointerException e) {
            if (!"false".equals(System.getProperty("lombok.debug.reflection", "false"))) {
                e.initCause(ReflectionForUi.problem);
                throw e;
            }
            return (IJavaCompletionProposal[])javaCompletionProposals;
        }
    }

    private static final class ReflectionForUi {
        public static final Method getJavaCompletionProposals;
        public static final Throwable problem;

        private ReflectionForUi() {
        }

        static {
            Method p = null;
            Throwable problem_ = null;
            try {
                p = PatchExtensionMethodCompletionProposal.class.getMethod("getJavaCompletionProposals", Class.forName("[Lorg.eclipse.jdt.ui.text.java.IJavaCompletionProposal;"), Class.forName("org.eclipse.jdt.ui.text.java.CompletionProposalCollector"));
            }
            catch (Throwable t) {
                problem_ = t;
            }
            getJavaCompletionProposals = p;
            problem = problem_;
        }
    }

}
