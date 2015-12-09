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

@Target(value={ElementType.FIELD, ElementType.TYPE})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Getter {
    public AccessLevel value() default AccessLevel.PUBLIC;

    public boolean lazy() default 0;
}

