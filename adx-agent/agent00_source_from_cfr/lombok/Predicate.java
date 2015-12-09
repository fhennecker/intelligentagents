/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.Predicates;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Predicate {
    public Class<?> value() default Predicates.class;
}

