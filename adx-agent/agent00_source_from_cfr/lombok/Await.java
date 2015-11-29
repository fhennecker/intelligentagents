/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.Position;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Await {
    public String conditionName();

    public Position pos() default Position.BEFORE;

    public String lockName() default "";

    public String conditionMethod();
}

