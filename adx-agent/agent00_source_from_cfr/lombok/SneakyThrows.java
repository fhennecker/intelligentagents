/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(value=RetentionPolicy.SOURCE)
public @interface SneakyThrows {
    public Class<? extends Throwable>[] value() default {Throwable.class};
}

