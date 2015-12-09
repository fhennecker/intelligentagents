/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.SOURCE)
public @interface PrintAST {
    public String outfile() default "";

    public boolean printContent() default 0;

    public boolean printPositions() default 0;
}

