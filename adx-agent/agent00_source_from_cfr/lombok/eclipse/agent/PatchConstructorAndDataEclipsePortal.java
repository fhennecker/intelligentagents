/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 */
package lombok.eclipse.agent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Lombok;
import lombok.eclipse.agent.PatchConstructorAndDataEclipse;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

public class PatchConstructorAndDataEclipsePortal {
    public static void onSourceElementRequestor_exitField(Object requestor, int initializationStart, int declarationEnd, int declarationSourceEnd, FieldDeclaration fieldDeclaration, TypeDeclaration typeDeclaration) {
        try {
            Reflection.onSourceElementRequestor_exitFieldMethod.invoke(null, new Object[]{requestor, initializationStart, declarationEnd, declarationSourceEnd, fieldDeclaration, typeDeclaration});
        }
        catch (NoClassDefFoundError e) {
        }
        catch (IllegalAccessException e) {
            Lombok.sneakyThrow(e);
        }
        catch (InvocationTargetException e) {
            Lombok.sneakyThrow(e);
        }
        catch (NullPointerException e) {
            e.initCause(Reflection.problem);
            throw e;
        }
    }

    private static final class Reflection {
        public static final Method onSourceElementRequestor_exitFieldMethod;
        public static final Throwable problem;

        private Reflection() {
        }

        static {
            Method m = null;
            Throwable problem_ = null;
            try {
                m = PatchConstructorAndDataEclipse.class.getMethod("onSourceElementRequestor_exitField", Class.forName("org.eclipse.jdt.internal.compiler.ISourceElementRequestor"), Integer.TYPE, Integer.TYPE, Integer.TYPE, FieldDeclaration.class, TypeDeclaration.class);
            }
            catch (Throwable t) {
                problem_ = t;
            }
            onSourceElementRequestor_exitFieldMethod = m;
            problem = problem_;
        }
    }

}

