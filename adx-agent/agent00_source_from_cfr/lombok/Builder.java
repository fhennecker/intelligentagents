/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.AccessLevel;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Builder {
    public AccessLevel value() default AccessLevel.PUBLIC;

    public String prefix() default "";

    public String[] exclude() default {};

    public boolean convenientMethods() default 1;

    public String[] callMethods() default {};

    public boolean allowReset() default 0;

    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.SOURCE)
    public static @interface Extension {
        public String[] fields() default {};
    }

}

