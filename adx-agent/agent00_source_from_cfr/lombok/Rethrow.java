/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Rethrow {
    public Class<? extends Exception>[] value() default {};

    public Class<? extends Exception> as() default RuntimeException.class;

    public String message() default "";
}

