/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.EnumId;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.EnumIdHandler;
import lombok.core.util.ErrorMessages;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacField;
import lombok.javac.handlers.ast.JavacType;

public class HandleEnumId
extends JavacAnnotationHandler<EnumId> {
    @Override
    public void handle(AnnotationValues<EnumId> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, EnumId.class);
        JavacType type = JavacType.typeOf(annotationNode, (JCTree)source);
        JavacField field = JavacField.fieldOf(annotationNode, (JCTree)source);
        if (field == null) {
            annotationNode.addError(ErrorMessages.canBeUsedOnFieldOnly(EnumId.class));
            return;
        }
        new EnumIdHandler<JavacType, JavacField>(type, field, annotationNode).handle();
    }
}

