/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import java.lang.annotation.Annotation;
import lombok.SwingInvokeAndWait;
import lombok.SwingInvokeLater;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.handlers.SwingInvokeHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacParameterSanitizer;
import lombok.javac.handlers.JavacParameterValidator;
import lombok.javac.handlers.ast.JavacMethod;

public class HandleSwingInvoke {

    public static class HandleSwingInvokeAndWait
    extends JavacAnnotationHandler<SwingInvokeAndWait> {
        @Override
        public void handle(AnnotationValues<SwingInvokeAndWait> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
            if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
                return;
            }
            JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, SwingInvokeAndWait.class);
            new SwingInvokeHandler<JavacMethod>(JavacMethod.methodOf(annotationNode, (JCTree)source), annotationNode).handle("invokeAndWait", SwingInvokeAndWait.class, new JavacParameterValidator(), new JavacParameterSanitizer());
        }
    }

    public static class HandleSwingInvokeLater
    extends JavacAnnotationHandler<SwingInvokeLater> {
        @Override
        public void handle(AnnotationValues<SwingInvokeLater> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
            if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
                return;
            }
            JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, SwingInvokeLater.class);
            new SwingInvokeHandler<JavacMethod>(JavacMethod.methodOf(annotationNode, (JCTree)source), annotationNode).handle("invokeLater", SwingInvokeLater.class, new JavacParameterValidator(), new JavacParameterSanitizer());
        }
    }

}

