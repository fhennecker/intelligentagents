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
import java.util.ArrayList;
import java.util.List;
import lombok.Rethrow;
import lombok.Rethrows;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.handlers.RethrowAndRethrowsHandler;
import lombok.javac.JavacAST;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacParameterSanitizer;
import lombok.javac.handlers.JavacParameterValidator;
import lombok.javac.handlers.ast.JavacMethod;

public class HandleRethrowAndRethrows {
    private static RethrowAndRethrowsHandler<JavacMethod> prepareRethrowAndRethrowsHandler(JavacNode node, JCTree.JCAnnotation source, Class<? extends Annotation> annotationType) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(node, annotationType);
        Javac.deleteImport(node, Rethrow.class);
        return new RethrowAndRethrowsHandler<JavacMethod>(JavacMethod.methodOf(node, (JCTree)source), node);
    }

    public static class HandleRethrows
    extends JavacAnnotationHandler<Rethrows> {
        @Override
        public void handle(AnnotationValues<Rethrows> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            RethrowAndRethrowsHandler handler = HandleRethrowAndRethrows.prepareRethrowAndRethrowsHandler(annotationNode, ast, Rethrow.class);
            for (Object rethrow : annotation.getActualExpressions("value")) {
                JavacNode rethrowNode = new JavacNode((JavacAST)annotationNode.getAst(), (JCTree)rethrow, new ArrayList<JavacNode>(), AST.Kind.ANNOTATION);
                Rethrow ann = (Rethrow)JavacHandlerUtil.createAnnotation(Rethrow.class, rethrowNode).getInstance();
                handler.withRethrow(new RethrowAndRethrowsHandler.RethrowData(RethrowAndRethrowsHandler.classNames(ann.value()), ann.as(), ann.message()));
            }
            handler.handle(Rethrows.class, new JavacParameterValidator(), new JavacParameterSanitizer());
        }
    }

    public static class HandleRethrow
    extends JavacAnnotationHandler<Rethrow> {
        @Override
        public void handle(AnnotationValues<Rethrow> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            Rethrow ann = annotation.getInstance();
            HandleRethrowAndRethrows.prepareRethrowAndRethrowsHandler(annotationNode, ast, Rethrow.class).withRethrow(new RethrowAndRethrowsHandler.RethrowData(RethrowAndRethrowsHandler.classNames(ann.value()), ann.as(), ann.message())).handle(Rethrow.class, new JavacParameterValidator(), new JavacParameterSanitizer());
        }
    }

}

