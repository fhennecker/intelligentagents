/*
 * Decompiled with CFR 0_110.
 */
package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Validate {

    @Target(value={ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.SOURCE)
    public static @interface NotEmpty {
    }

    @Target(value={ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.SOURCE)
    public static @interface NotNull {
    }

    @Target(value={ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.SOURCE)
    public static @interface With {
        public String value();
    }

}

