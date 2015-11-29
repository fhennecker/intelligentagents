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
public @interface AllArgsConstructor {
    public String staticName() default "";

    public boolean callSuper() default 0;

    public AccessLevel access() default AccessLevel.PUBLIC;

    @Deprecated
    public boolean suppressConstructorProperties() default 0;
}

