/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.util;

import java.lang.annotation.Annotation;

public final class ErrorMessages {
    public static String canBeUsedOnConcreteClassOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on concrete classes only", annotationType);
    }

    public static String canBeUsedOnClassOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on classes only", annotationType);
    }

    public static String canBeUsedOnClassAndEnumOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on classes and enums only", annotationType);
    }

    public static String canBeUsedOnClassAndFieldOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on classes and fields only", annotationType);
    }

    public static String canBeUsedOnClassAndMethodOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on classes and methods only", annotationType);
    }

    public static String canBeUsedOnFieldOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on fields only", annotationType);
    }

    public static String canBeUsedOnPrivateFinalFieldOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on private final fields only", annotationType);
    }

    public static String canBeUsedOnInitializedFieldOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on initialized fields only", annotationType);
    }

    public static String canBeUsedOnMethodOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on methods only", annotationType);
    }

    public static String canBeUsedOnConcreteMethodOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on concrete methods only", annotationType);
    }

    public static String canBeUsedOnEnumFieldsOnly(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s can be used on enum fields only", annotationType);
    }

    public static String requiresDefaultOrNoArgumentConstructor(Class<? extends Annotation> annotationType) {
        return ErrorMessages.errorMessage("@%s requires a default or no-argument constructor", annotationType);
    }

    public static String unsupportedExpressionIn(String where, Object expr) {
        return String.format("Unsupported Expression in '%s': %s", where, expr);
    }

    public static String isNotAllowedHere(String what) {
        return String.format("'%s' is not allowed here.", what);
    }

    public static String firstArgumentCanBeVariableNameOrNewClassStatementOnly(String what) {
        return String.format("The first argument of '%s' can be variable name or new-class statement  only", what);
    }

    public static String canBeUsedInBodyOfMethodsOnly(String what) {
        return String.format("'%s' can be used in the body of methods only", what);
    }

    private static String errorMessage(String format, Class<? extends Annotation> annotationType) {
        return String.format(format, annotationType.getName());
    }

    private ErrorMessages() {
    }
}

