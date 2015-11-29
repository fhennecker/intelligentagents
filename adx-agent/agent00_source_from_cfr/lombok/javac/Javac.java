/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.Tree
 *  com.sun.source.tree.Tree$Kind
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 */
package lombok.javac;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Javac {
    private static final Pattern PRIMITIVE_TYPE_NAME_PATTERN = Pattern.compile("^(boolean|byte|short|int|long|float|double|char)$");

    private Javac() {
    }

    public static boolean isPrimitive(JCTree.JCExpression ref) {
        String typeName = ref.toString();
        return PRIMITIVE_TYPE_NAME_PATTERN.matcher(typeName).matches();
    }

    public static Object calculateGuess(JCTree.JCExpression expr) {
        if (expr instanceof JCTree.JCLiteral) {
            JCTree.JCLiteral lit = (JCTree.JCLiteral)expr;
            if (lit.getKind() == Tree.Kind.BOOLEAN_LITERAL) {
                return ((Number)lit.value).intValue() != 0;
            }
            return lit.value;
        }
        if (expr instanceof JCTree.JCIdent || expr instanceof JCTree.JCFieldAccess) {
            String x = expr.toString();
            if (x.endsWith(".class")) {
                x = x.substring(0, x.length() - 6);
            } else {
                int idx = x.lastIndexOf(46);
                if (idx > -1) {
                    x = x.substring(idx + 1);
                }
            }
            return x;
        }
        return null;
    }

    public static int getCtcInt(Class<?> ctcLocation, String identifier) {
        try {
            return (Integer)ctcLocation.getField(identifier).get(null);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

