/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.VisibleForTesting;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.util.ErrorMessages;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacMethodEditor;
import lombok.javac.handlers.ast.JavacType;
import lombok.javac.handlers.ast.JavacTypeEditor;

public class HandleVisibleForTesting
extends JavacAnnotationHandler<VisibleForTesting> {
    @Override
    public void handle(AnnotationValues<VisibleForTesting> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, VisibleForTesting.class);
        JavacNode mayBeMethod = (JavacNode)annotationNode.up();
        if (mayBeMethod.getKind() == AST.Kind.METHOD) {
            JavacMethod method = JavacMethod.methodOf(annotationNode, (JCTree)source);
            if (method.isAbstract()) {
                annotationNode.addError(ErrorMessages.canBeUsedOnConcreteMethodOnly(VisibleForTesting.class));
                return;
            }
            method.editor().makePrivate();
            method.editor().rebuild();
        } else if (mayBeMethod.getKind() == AST.Kind.TYPE) {
            JavacType type = JavacType.typeOf(annotationNode, (JCTree)source);
            type.editor().makePrivate();
            type.editor().rebuild();
        } else {
            annotationNode.addError(ErrorMessages.canBeUsedOnClassAndMethodOnly(VisibleForTesting.class));
        }
    }
}

