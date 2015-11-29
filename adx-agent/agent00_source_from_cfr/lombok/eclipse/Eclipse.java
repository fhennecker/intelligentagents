/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess
 *  org.eclipse.jdt.internal.compiler.ast.Clinit
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Literal
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.impl.Constant
 */
package lombok.eclipse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public class Eclipse {
    private static final Annotation[] EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];
    public static final int ECLIPSE_DO_NOT_TOUCH_FLAG = 8388608;
    private static final Pattern PRIMITIVE_TYPE_NAME_PATTERN = Pattern.compile("^(boolean|byte|short|int|long|float|double|char)$");

    private Eclipse() {
    }

    public static String toQualifiedName(char[][] typeName) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (char[] c : typeName) {
            sb.append(first ? "" : ".").append(c);
            first = false;
        }
        return sb.toString();
    }

    public static char[][] fromQualifiedName(String typeName) {
        String[] split = typeName.split("\\.");
        char[][] result = new char[split.length][];
        for (int i = 0; i < split.length; ++i) {
            result[i] = split[i].toCharArray();
        }
        return result;
    }

    public static long pos(ASTNode node) {
        return (long)node.sourceStart << 32 | (long)node.sourceEnd & 0xFFFFFFFFL;
    }

    public static long[] poss(ASTNode node, int repeat) {
        long p = (long)node.sourceStart << 32 | (long)node.sourceEnd & 0xFFFFFFFFL;
        long[] out = new long[repeat];
        Arrays.fill(out, p);
        return out;
    }

    public static boolean nameEquals(char[][] typeName, String string) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (char[] elem : typeName) {
            if (first) {
                first = false;
            } else {
                sb.append('.');
            }
            sb.append(elem);
        }
        return string.contentEquals(sb);
    }

    public static boolean hasClinit(TypeDeclaration parent) {
        if (parent.methods == null) {
            return false;
        }
        for (AbstractMethodDeclaration method : parent.methods) {
            if (!(method instanceof Clinit)) continue;
            return true;
        }
        return false;
    }

    public static Annotation[] findAnnotations(FieldDeclaration field, Pattern namePattern) {
        ArrayList<Annotation> result = new ArrayList<Annotation>();
        if (field.annotations == null) {
            return EMPTY_ANNOTATIONS_ARRAY;
        }
        for (Annotation annotation : field.annotations) {
            String suspect;
            char[][] typeName;
            TypeReference typeRef = annotation.type;
            if (typeRef == null || typeRef.getTypeName() == null || !namePattern.matcher(suspect = new String((typeName = typeRef.getTypeName())[typeName.length - 1])).matches()) continue;
            result.add(annotation);
        }
        return result.toArray((T[])EMPTY_ANNOTATIONS_ARRAY);
    }

    public static boolean isPrimitive(TypeReference ref) {
        if (ref.dimensions() > 0) {
            return false;
        }
        return PRIMITIVE_TYPE_NAME_PATTERN.matcher(Eclipse.toQualifiedName(ref.getTypeName())).matches();
    }

    public static Object calculateValue(Expression e) {
        if (e instanceof Literal) {
            ((Literal)e).computeConstant();
            switch (e.constant.typeID()) {
                case 10: {
                    return e.constant.intValue();
                }
                case 3: {
                    return Byte.valueOf(e.constant.byteValue());
                }
                case 4: {
                    return e.constant.shortValue();
                }
                case 2: {
                    return Character.valueOf(e.constant.charValue());
                }
                case 9: {
                    return Float.valueOf(e.constant.floatValue());
                }
                case 8: {
                    return e.constant.doubleValue();
                }
                case 5: {
                    return e.constant.booleanValue();
                }
                case 7: {
                    return e.constant.longValue();
                }
                case 11: {
                    return e.constant.stringValue();
                }
            }
            return null;
        }
        if (e instanceof ClassLiteralAccess) {
            return Eclipse.toQualifiedName(((ClassLiteralAccess)e).type.getTypeName());
        }
        if (e instanceof SingleNameReference) {
            return new String(((SingleNameReference)e).token);
        }
        if (e instanceof QualifiedNameReference) {
            String qName = Eclipse.toQualifiedName(((QualifiedNameReference)e).tokens);
            int idx = qName.lastIndexOf(46);
            return idx == -1 ? qName : qName.substring(idx + 1);
        }
        return null;
    }
}

