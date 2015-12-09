/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.DoPrivileged;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.DoPrivilegedHandler;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacParameterSanitizer;
import lombok.javac.handlers.JavacParameterValidator;
import lombok.javac.handlers.ast.JavacMethod;

public class HandleDoPrivileged
extends JavacAnnotationHandler<DoPrivileged> {
    @Override
    public void handle(AnnotationValues<DoPrivileged> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
        if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
            return;
        }
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, DoPrivileged.class);
        new DoPrivilegedHandler<JavacMethod>(JavacMethod.methodOf(annotationNode, (JCTree)source), annotationNode).handle(new JavacParameterValidator(), new JavacParameterSanitizer());
    }
}

