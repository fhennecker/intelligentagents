/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.AccessLevel;
import lombok.LazyGetter;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.LazyGetterHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacField;
import lombok.javac.handlers.ast.JavacType;

public class HandleLazyGetter
extends JavacAnnotationHandler<LazyGetter> {
    @Override
    public void handle(AnnotationValues<LazyGetter> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JavacType type = JavacType.typeOf(annotationNode, (JCTree)ast);
        JavacField field = JavacField.fieldOf(annotationNode, (JCTree)ast);
        LazyGetter annotationInstance = annotation.getInstance();
        new LazyGetterHandler<JavacType, JavacField>(type, field, annotationNode).handle(annotationInstance.value());
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, LazyGetter.class);
        Javac.deleteImport(annotationNode, AccessLevel.class);
    }
}

