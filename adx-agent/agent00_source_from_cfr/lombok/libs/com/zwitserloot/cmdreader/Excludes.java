/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.com.zwitserloot.cmdreader;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
@Documented
public @interface Excludes {
    public String[] value();
}

