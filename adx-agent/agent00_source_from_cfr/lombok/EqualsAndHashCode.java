/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.SOURCE)
public @interface EqualsAndHashCode {
    public String[] exclude() default {};

    public String[] of() default {};

    public boolean callSuper() default 0;

    public boolean doNotUseGetters() default 0;
}
