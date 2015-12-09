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
import lombok.BoundSetter;
import lombok.ast.IField;
import lombok.ast.IType;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.handlers.BoundSetterHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.ResolutionBased;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacField;
import lombok.javac.handlers.ast.JavacType;

@ResolutionBased
public class HandleBoundSetter
extends JavacAnnotationHandler<BoundSetter> {
    @Override
    public void handle(AnnotationValues<BoundSetter> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        BoundSetter annotationInstance = annotation.getInstance();
        new BoundSetterHandler<JavacType, JavacField, JavacNode, JCTree>(annotationNode, (JCTree)ast){

            @Override
            protected JavacType typeOf(JavacNode node, JCTree ast) {
                return JavacType.typeOf(node, ast);
            }

            @Override
            protected JavacField fieldOf(JavacNode node, JCTree ast) {
                return JavacField.fieldOf(node, ast);
            }
        }.handle(annotationInstance.value(), annotationInstance.vetoable(), annotationInstance.throwVetoException());
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, BoundSetter.class);
        Javac.deleteImport(annotationNode, AccessLevel.class);
    }

}

