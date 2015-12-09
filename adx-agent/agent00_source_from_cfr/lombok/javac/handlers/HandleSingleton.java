/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.Singleton;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.SingletonHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacType;

public class HandleSingleton
extends JavacAnnotationHandler<Singleton> {
    @Override
    public void handle(AnnotationValues<Singleton> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Singleton.class);
        new SingletonHandler(JavacType.typeOf(annotationNode, (JCTree)source), annotationNode).handle(annotation.getInstance().style());
    }
}

