/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse;

import lombok.core.AnnotationValues;
import lombok.eclipse.EclipseNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class EclipseAnnotationHandler<T extends java.lang.annotation.Annotation> {
    public abstract void handle(AnnotationValues<T> var1, Annotation var2, EclipseNode var3);

    public void preHandle(AnnotationValues<T> annotation, Annotation ast, EclipseNode annotationNode) {
    }
}

