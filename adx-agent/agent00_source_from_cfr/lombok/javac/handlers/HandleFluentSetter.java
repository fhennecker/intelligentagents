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
import lombok.FluentSetter;
import lombok.ast.IField;
import lombok.ast.IType;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.handlers.FluentSetterHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacField;
import lombok.javac.handlers.ast.JavacType;

public class HandleFluentSetter
extends JavacAnnotationHandler<FluentSetter> {
    @Override
    public void handle(AnnotationValues<FluentSetter> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        FluentSetter annotationInstance = annotation.getInstance();
        new FluentSetterHandler<JavacType, JavacField, JavacNode, JCTree>(annotationNode, (JCTree)ast){

            @Override
            protected JavacType typeOf(JavacNode node, JCTree ast) {
                return JavacType.typeOf(node, ast);
            }

            @Override
            protected JavacField fieldOf(JavacNode node, JCTree ast) {
                return JavacField.fieldOf(node, ast);
            }
        }.handle(annotationInstance.value());
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, FluentSetter.class);
        Javac.deleteImport(annotationNode, AccessLevel.class);
    }

}

