/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Hook {
    @NonNull
    private final String className;
    @NonNull
    private final String methodName;
    @NonNull
    private final String returnType;
    @NonNull
    private final List<String> parameterTypes;
    private static final Map<String, String> PRIMITIVES;

    public boolean isConstructor() {
        return "<init>".equals(this.methodName);
    }

    public /* varargs */ Hook(String className, String methodName, String returnType, String ... parameterTypes) {
        if (className == null) {
            throw new NullPointerException("classSpec");
        }
        if (methodName == null) {
            throw new NullPointerException("methodName");
        }
        if (returnType == null) {
            throw new NullPointerException("returnType");
        }
        if (parameterTypes == null) {
            throw new NullPointerException("parameterTypes");
        }
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        ArrayList<String> params = new ArrayList<String>();
        for (String param : parameterTypes) {
            params.add(param);
        }
        this.parameterTypes = Collections.unmodifiableList(params);
    }

    public String getClassSpec() {
        return Hook.convertType(this.className);
    }

    public String getMethodDescriptor() {
        StringBuilder out = new StringBuilder();
        out.append("(");
        for (String p : this.parameterTypes) {
            out.append(Hook.toSpec(p));
        }
        out.append(")");
        out.append(Hook.toSpec(this.returnType));
        return out.toString();
    }

    public static String toSpec(String type) {
        StringBuilder out = new StringBuilder();
        while (type.endsWith("[]")) {
            type = type.substring(0, type.length() - 2);
            out.append("[");
        }
        String p = PRIMITIVES.get(type);
        if (p != null) {
            out.append(p);
            return out.toString();
        }
        out.append("L");
        out.append(Hook.convertType(type));
        out.append(';');
        return out.toString();
    }

    public static String convertType(String type) {
        StringBuilder out = new StringBuilder();
        for (String part : type.split("\\.")) {
            if (out.length() > 0) {
                out.append('/');
            }
            out.append(part);
        }
        return out.toString();
    }

    public String toString() {
        return "Hook(className=" + this.getClassName() + ", methodName=" + this.getMethodName() + ", returnType=" + this.getReturnType() + ", parameterTypes=" + this.getParameterTypes() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Hook)) {
            return false;
        }
        Hook other = (Hook)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getClassName() == null ? other.getClassName() != null : !this.getClassName().equals(other.getClassName())) {
            return false;
        }
        if (this.getMethodName() == null ? other.getMethodName() != null : !this.getMethodName().equals(other.getMethodName())) {
            return false;
        }
        if (this.getReturnType() == null ? other.getReturnType() != null : !this.getReturnType().equals(other.getReturnType())) {
            return false;
        }
        if (this.getParameterTypes() == null ? other.getParameterTypes() != null : !this.getParameterTypes().equals(other.getParameterTypes())) {
            return false;
        }
        return true;
    }

    public boolean canEqual(Object other) {
        return other instanceof Hook;
    }

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        result = result * 31 + (this.getClassName() == null ? 0 : this.getClassName().hashCode());
        result = result * 31 + (this.getMethodName() == null ? 0 : this.getMethodName().hashCode());
        result = result * 31 + (this.getReturnType() == null ? 0 : this.getReturnType().hashCode());
        result = result * 31 + (this.getParameterTypes() == null ? 0 : this.getParameterTypes().hashCode());
        return result;
    }

    @NonNull
    public String getClassName() {
        return this.className;
    }

    @NonNull
    public String getMethodName() {
        return this.methodName;
    }

    @NonNull
    public String getReturnType() {
        return this.returnType;
    }

    @NonNull
    public List<String> getParameterTypes() {
        return this.parameterTypes;
    }

    static {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("int", "I");
        m.put("long", "J");
        m.put("short", "S");
        m.put("byte", "B");
        m.put("char", "C");
        m.put("double", "D");
        m.put("float", "F");
        m.put("void", "V");
        m.put("boolean", "Z");
        PRIMITIVES = Collections.unmodifiableMap(m);
    }
}

