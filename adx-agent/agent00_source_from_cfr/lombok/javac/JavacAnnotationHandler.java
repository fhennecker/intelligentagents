/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac;

import com.sun.tools.javac.tree.JCTree;
import java.lang.annotation.Annotation;
import lombok.core.AnnotationValues;
import lombok.javac.JavacNode;

public abstract class JavacAnnotationHandler<T extends Annotation> {
    public abstract void handle(AnnotationValues<T> var1, JCTree.JCAnnotation var2, JavacNode var3);
}

