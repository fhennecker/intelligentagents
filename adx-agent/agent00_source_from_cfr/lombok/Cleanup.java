/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Cleanup {
    public String value() default "close";

    public boolean quietly() default 0;
}

