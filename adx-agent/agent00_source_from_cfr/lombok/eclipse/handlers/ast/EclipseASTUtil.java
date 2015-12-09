/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse.handlers.ast;

import lombok.ast.AST;
import lombok.ast.TypeRef;
import lombok.core.util.As;
import lombok.core.util.Is;
import lombok.core.util.Names;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public final class EclipseASTUtil {
    public static TypeRef boxedType(TypeReference type) {
        if (type == null) {
            return null;
        }
        TypeRef boxedType = AST.Type((Object)type);
        if (Is.oneOf((Object)type, SingleTypeReference.class) && Is.noneOf((Object)type, ArrayTypeReference.class)) {
            String name = As.string(type.getLastToken());
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

    private EclipseASTUtil() {
    }
}

