/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 */
package lombok.eclipse.handlers;

import java.util.List;
import lombok.ExtensionMethod;
import lombok.core.AnnotationValues;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HandleExtensionMethod
extends EclipseAnnotationHandler<ExtensionMethod> {
    @Override
    public void handle(AnnotationValues<ExtensionMethod> annotation, Annotation ast, EclipseNode annotationNode) {
        boolean notAClass;
        TypeDeclaration typeDecl = null;
        EclipseNode owner = (EclipseNode)annotationNode.up();
        if (owner.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)owner.get();
        }
        int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
        boolean bl = notAClass = (modifiers & 8704) != 0;
        if (typeDecl == null || notAClass) {
            annotationNode.addError("@ExtensionMethod is legal only on classes and enums.");
            return;
        }
        List<Object> listenerInterfaces = annotation.getActualExpressions("value");
        if (listenerInterfaces.isEmpty()) {
            annotationNode.addWarning(String.format("@ExtensionMethod has no effect since no extension types were specified.", new Object[0]));
            return;
        }
    }
}

