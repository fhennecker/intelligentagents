/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCPrimitiveTypeTree
 */
package lombok.javac.handlers.ast;

import com.sun.tools.javac.tree.JCTree;
import lombok.ast.AST;
import lombok.ast.TypeRef;
import lombok.core.util.Is;
import lombok.core.util.Names;

public final class JavacASTUtil {
    public static TypeRef boxedType(JCTree.JCExpression type) {
        if (type == null) {
            return null;
        }
        TypeRef boxedType = AST.Type((Object)type);
        if (type instanceof JCTree.JCPrimitiveTypeTree) {
            String name = type.toString();
            if ("int".equals(name)) {
                boxedType = AST.Type(Integer.class);
            } else if ("char".equals(name)) {
                boxedType = AST.Type(Character.class);
            } else if (Is.oneOf(name, "void", "boolean", "float", "double", "byte", "short", "long")) {
                boxedType = AST.Type("java.lang." + Names.capitalize(name));
            }
        }
        return boxedType;
    }

    private JavacASTUtil() {
    }
}

