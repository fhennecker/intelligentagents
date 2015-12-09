/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 */
package lombok.eclipse.agent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Lombok;
import lombok.eclipse.agent.PatchExtensionMethod;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class PatchExtensionMethodPortal {
    private static final String TYPE_BINDING = "org.eclipse.jdt.internal.compiler.lookup.TypeBinding";
    private static final String TYPE_BINDING_ARRAY = "[Lorg.eclipse.jdt.internal.compiler.lookup.TypeBinding;";
    private static final String MESSAGE_SEND = "org.eclipse.jdt.internal.compiler.ast.MessageSend";
    private static final String BLOCK_SCOPE = "org.eclipse.jdt.internal.compiler.lookup.BlockScope";
    private static final String METHOD_BINDING = "org.eclipse.jdt.internal.compiler.lookup.MethodBinding";
    private static final String PROBLEM_REPORTER = "org.eclipse.jdt.internal.compiler.problem.ProblemReporter";

    public static TypeBinding resolveType(Object resolvedType, Object methodCall, Object scope) {
        try {
            return (TypeBinding)Reflection.resolveType.invoke(null, resolvedType, methodCall, scope);
        }
        catch (NoClassDefFoundError e) {
            return (TypeBinding)resolvedType;
        }
        catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
        catch (InvocationTargetException e) {
            throw Lombok.sneakyThrow(e);
        }
        catch (NullPointerException e) {
            if (!"false".equals(System.getProperty("lombok.debug.reflection", "false"))) {
                e.initCause(Reflection.problem);
                throw e;
            }
            return (TypeBinding)resolvedType;
        }
    }

    public static void errorNoMethodFor(Object problemReporter, Object messageSend, Object recType, Object params) {
        block5 : {
            try {
                Reflection.errorNoMethodFor.invoke(null, problemReporter, messageSend, recType, params);
            }
            catch (NoClassDefFoundError e) {
            }
            catch (IllegalAccessException e) {
                throw Lombok.sneakyThrow(e);
            }
            catch (InvocationTargetException e) {
                throw Lombok.sneakyThrow(e.getCause());
            }
            catch (NullPointerException e) {
                if ("false".equals(System.getProperty("lombok.debug.reflection", "false"))) break block5;
                e.initCause(Reflection.problem);
                throw e;
            }
        }
    }

    public static void invalidMethod(Object problemReporter, Object messageSend, Object method) {
        block5 : {
            try {
                Reflection.invalidMethod.invoke(null, problemReporter, messageSend, method);
            }
            catch (NoClassDefFoundError e) {
            }
            catch (IllegalAccessException e) {
                Lombok.sneakyThrow(e);
            }
            catch (InvocationTargetException e) {
                throw Lombok.sneakyThrow(e.getCause());
            }
            catch (NullPointerException e) {
                if ("false".equals(System.getProperty("lombok.debug.reflection", "false"))) break block5;
                e.initCause(Reflection.problem);
                throw e;
            }
        }
    }

    private static final class Reflection {
        public static final Method resolveType;
        public static final Method errorNoMethodFor;
        public static final Method invalidMethod;
        public static final Throwable problem;

        private Reflection() {
        }

        static {
            Method m = null;
            Method n = null;
            Method o = null;
            Throwable problem_ = null;
            try {
                m = PatchExtensionMethod.class.getMethod("resolveType", Class.forName("org.eclipse.jdt.internal.compiler.lookup.TypeBinding"), Class.forName("org.eclipse.jdt.internal.compiler.ast.MessageSend"), Class.forName("org.eclipse.jdt.internal.compiler.lookup.BlockScope"));
                n = PatchExtensionMethod.class.getMethod("errorNoMethodFor", Class.forName("org.eclipse.jdt.internal.compiler.problem.ProblemReporter"), Class.forName("org.eclipse.jdt.internal.compiler.ast.MessageSend"), Class.forName("org.eclipse.jdt.internal.compiler.lookup.TypeBinding"), Class.forName("[Lorg.eclipse.jdt.internal.compiler.lookup.TypeBinding;"));
                o = PatchExtensionMethod.class.getMethod("invalidMethod", Class.forName("org.eclipse.jdt.internal.compiler.problem.ProblemReporter"), Class.forName("org.eclipse.jdt.internal.compiler.ast.MessageSend"), Class.forName("org.eclipse.jdt.internal.compiler.lookup.MethodBinding"));
            }
            catch (Throwable t) {
                problem_ = t;
            }
            resolveType = m;
            errorNoMethodFor = n;
            invalidMethod = o;
            problem = problem_;
        }
    }

}

