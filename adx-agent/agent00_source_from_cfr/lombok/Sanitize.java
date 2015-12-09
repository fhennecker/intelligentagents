/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.Normalizer;

@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Sanitize {

    @Target(value={ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.SOURCE)
    public static @interface Normalize {
        public Normalizer.Form value() default Normalizer.Form.NFKC;
    }

    @Target(value={ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.SOURCE)
    public static @interface With {
        public String value();
    }

}

