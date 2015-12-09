/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import lombok.libs.org.objectweb.asm.Type;

public class Method {
    private final String name;
    private final String desc;
    private static final Map DESCRIPTORS = new HashMap();

    public Method(String string, String string2) {
        this.name = string;
        this.desc = string2;
    }

    public Method(String string, Type type, Type[] arrtype) {
        this(string, Type.getMethodDescriptor(type, arrtype));
    }

    public static Method getMethod(java.lang.reflect.Method method) {
        return new Method(method.getName(), Type.getMethodDescriptor(method));
    }

    public static Method getMethod(Constructor constructor) {
        return new Method("<init>", Type.getConstructorDescriptor(constructor));
    }

    public static Method getMethod(String string) throws IllegalArgumentException {
        return Method.getMethod(string, false);
    }

    public static Method getMethod(String string, boolean bl) throws IllegalArgumentException {
        int n;
        int n2 = string.indexOf(32);
        int n3 = string.indexOf(40, n2) + 1;
        int n4 = string.indexOf(41, n3);
        if (n2 == -1 || n3 == -1 || n4 == -1) {
            throw new IllegalArgumentException();
        }
        String string2 = string.substring(0, n2);
        String string3 = string.substring(n2 + 1, n3 - 1).trim();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        do {
            String string4;
            if ((n = string.indexOf(44, n3)) == -1) {
                string4 = Method.map(string.substring(n3, n4).trim(), bl);
            } else {
                string4 = Method.map(string.substring(n3, n).trim(), bl);
                n3 = n + 1;
            }
            stringBuffer.append(string4);
        } while (n != -1);
        stringBuffer.append(')');
        stringBuffer.append(Method.map(string2, bl));
        return new Method(string3, stringBuffer.toString());
    }

    private static String map(String string, boolean bl) {
        if ("".equals(string)) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer();
        int n = 0;
        while ((n = string.indexOf("[]", n) + 1) > 0) {
            stringBuffer.append('[');
        }
        String string2 = string.substring(0, string.length() - stringBuffer.length() * 2);
        String string3 = (String)DESCRIPTORS.get(string2);
        if (string3 != null) {
            stringBuffer.append(string3);
        } else {
            stringBuffer.append('L');
            if (string2.indexOf(46) < 0) {
                if (!bl) {
                    stringBuffer.append("java/lang/");
                }
                stringBuffer.append(string2);
            } else {
                stringBuffer.append(string2.replace('.', '/'));
            }
            stringBuffer.append(';');
        }
        return stringBuffer.toString();
    }

    public String getName() {
        return this.name;
    }

    public String getDescriptor() {
        return this.desc;
    }

    public Type getReturnType() {
        return Type.getReturnType(this.desc);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(this.desc);
    }

    public String toString() {
        return this.name + this.desc;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Method)) {
            return false;
        }
        Method method = (Method)object;
        return this.name.equals(method.name) && this.desc.equals(method.desc);
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.desc.hashCode();
    }

    static {
        DESCRIPTORS.put("void", "V");
        DESCRIPTORS.put("byte", "B");
        DESCRIPTORS.put("char", "C");
        DESCRIPTORS.put("double", "D");
        DESCRIPTORS.put("float", "F");
        DESCRIPTORS.put("int", "I");
        DESCRIPTORS.put("long", "J");
        DESCRIPTORS.put("short", "S");
        DESCRIPTORS.put("boolean", "Z");
    }
}

