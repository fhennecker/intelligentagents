/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.Sanitize;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.SanitizeHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacParameterSanitizer;
import lombok.javac.handlers.ast.JavacMethod;

public class HandleSanitize
extends JavacAnnotationHandler<Sanitize> {
    @Override
    public void handle(AnnotationValues<Sanitize> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
        if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
            return;
        }
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Sanitize.class);
        new SanitizeHandler<JavacMethod>(JavacMethod.methodOf(annotationNode, (JCTree)source), annotationNode).handle(new JavacParameterSanitizer());
    }
}

