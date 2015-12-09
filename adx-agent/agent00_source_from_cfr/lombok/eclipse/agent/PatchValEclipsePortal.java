/*
 * Decompiled with CFR 0_110.
 */
package lombok.eclipse.agent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Lombok;
import lombok.eclipse.agent.PatchValEclipse;

public class PatchValEclipsePortal {
    static final String LOCALDECLARATION_SIG = "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration";
    static final String PARSER_SIG = "org.eclipse.jdt.internal.compiler.parser.Parser";
    static final String VARIABLEDECLARATIONSTATEMENT_SIG = "org.eclipse.jdt.core.dom.VariableDeclarationStatement";
    static final String SINGLEVARIABLEDECLARATION_SIG = "org.eclipse.jdt.core.dom.SingleVariableDeclaration";
    static final String ASTCONVERTER_SIG = "org.eclipse.jdt.core.dom.ASTConverter";

    public static void copyInitializationOfForEachIterable(Object parser) {
        block5 : {
            try {
                Reflection.copyInitializationOfForEachIterable.invoke(null, parser);
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

    public static void copyInitializationOfLocalDeclaration(Object parser) {
        block5 : {
            try {
                Reflection.copyInitializationOfLocalDeclaration.invoke(null, parser);
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

    public static void addFinalAndValAnnotationToVariableDeclarationStatement(Object converter, Object out, Object in) {
        block5 : {
            try {
                Reflection.addFinalAndValAnnotationToVariableDeclarationStatement.invoke(null, converter, out, in);
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

    public static void addFinalAndValAnnotationToSingleVariableDeclaration(Object converter, Object out, Object in) {
        block5 : {
            try {
                Reflection.addFinalAndValAnnotationToSingleVariableDeclaration.invoke(null, converter, out, in);
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

    private static final class Reflection {
        public static final Method copyInitializationOfForEachIterable;
        public static final Method copyInitializationOfLocalDeclaration;
        public static final Method addFinalAndValAnnotationToVariableDeclarationStatement;
        public static final Method addFinalAndValAnnotationToSingleVariableDeclaration;
        public static final Throwable problem;

        private Reflection() {
        }

        static {
            Method m = null;
            Method n = null;
            Method o = null;
            Method p = null;
            Throwable problem_ = null;
            try {
                m = PatchValEclipse.class.getMethod("copyInitializationOfForEachIterable", Class.forName("org.eclipse.jdt.internal.compiler.parser.Parser"));
                n = PatchValEclipse.class.getMethod("copyInitializationOfLocalDeclaration", Class.forName("org.eclipse.jdt.internal.compiler.parser.Parser"));
                o = PatchValEclipse.class.getMethod("addFinalAndValAnnotationToVariableDeclarationStatement", Object.class, Class.forName("org.eclipse.jdt.core.dom.VariableDeclarationStatement"), Class.forName("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration"));
                p = PatchValEclipse.class.getMethod("addFinalAndValAnnotationToSingleVariableDeclaration", Object.class, Class.forName("org.eclipse.jdt.core.dom.SingleVariableDeclaration"), Class.forName("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration"));
            }
            catch (Throwable t) {
                problem_ = t;
            }
            copyInitializationOfForEachIterable = m;
            copyInitializationOfLocalDeclaration = n;
            addFinalAndValAnnotationToVariableDeclarationStatement = o;
            addFinalAndValAnnotationToSingleVariableDeclaration = p;
            problem = problem_;
        }
    }

}

