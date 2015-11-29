/*
 * Decompiled with CFR 0_110.
 */
package lombok.experimental;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.FIELD})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Accessors {
    public boolean fluent() default 0;

    public boolean chain() default 0;

    public String[] prefix() default {};
}

