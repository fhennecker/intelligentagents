/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class TypeArguments {
    public static Class<?> getClassFor(Class<?> clazz, int typeArgumentIndex) {
        Type type = TypeArguments.getTypeFor(clazz, typeArgumentIndex);
        Class result = TypeArguments.getClassFor(type);
        return result == null ? Object.class : result;
    }

    private static Type getTypeFor(Class<?> clazz, int index) {
        Type superClass = clazz.getGenericSuperclass();
        if (!(superClass instanceof ParameterizedType)) {
            return null;
        }
        Type[] typeArguments = ((ParameterizedType)superClass).getActualTypeArguments();
        return index >= typeArguments.length ? null : typeArguments[index];
    }

    private static Class<?> getClassFor(Type type) {
        Type componentType;
        Class componentClass;
        Class clazz = null;
        if (type instanceof Class) {
            clazz = (Class)type;
        } else if (type instanceof ParameterizedType) {
            clazz = TypeArguments.getClassFor(((ParameterizedType)type).getRawType());
        } else if (type instanceof GenericArrayType && (componentClass = TypeArguments.getClassFor(componentType = ((GenericArrayType)type).getGenericComponentType())) != null) {
            clazz = Array.newInstance(componentClass, 0).getClass();
        }
        return clazz;
    }

    private TypeArguments() {
    }
}

