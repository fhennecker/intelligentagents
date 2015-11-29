/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 */
package lombok.eclipse.handlers;

import lombok.AccessLevel;
import lombok.Data;
import lombok.core.AnnotationValues;
import lombok.eclipse.DeferUntilBuildFieldsAndMethods;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.HandleConstructor;
import lombok.eclipse.handlers.HandleEqualsAndHashCode;
import lombok.eclipse.handlers.HandleGetter;
import lombok.eclipse.handlers.HandleSetter;
import lombok.eclipse.handlers.HandleToString;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@DeferUntilBuildFieldsAndMethods
public class HandleData
extends EclipseAnnotationHandler<Data> {
    @Override
    public void handle(AnnotationValues<Data> annotation, Annotation ast, EclipseNode annotationNode) {
        boolean notAClass;
        EclipseNode typeNode = (EclipseNode)annotationNode.up();
        TypeDeclaration typeDecl = null;
        if (typeNode.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)typeNode.get();
        }
        int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
        boolean bl = notAClass = (modifiers & 25088) != 0;
        if (typeDecl == null || notAClass) {
            annotationNode.addError("@Data is only supported on a class.");
            return;
        }
        Data data = annotation.getInstance();
        String staticConstructorName = data.staticConstructor();
        boolean callSuper = data.callSuper();
        new HandleGetter().generateGetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true);
        new HandleSetter().generateSetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true);
        new HandleEqualsAndHashCode().generateEqualsAndHashCodeForType(typeNode, annotationNode, callSuper);
        new HandleToString().generateToStringForType(typeNode, annotationNode, callSuper);
        HandleConstructor.ConstructorData cData = new HandleConstructor.ConstructorData().fieldProvider(HandleConstructor.FieldProvider.REQUIRED).accessLevel(AccessLevel.PUBLIC).staticName(staticConstructorName).callSuper(callSuper);
        if (!HandleConstructor.constructorOrConstructorAnnotationExists(typeNode)) {
            new HandleConstructor().generateConstructor(typeNode, (ASTNode)ast, cData);
        } else if (cData.staticConstructorRequired()) {
            annotationNode.addWarning("Ignoring static constructor name: explicit @XxxArgsConstructor annotation present; its `staticName` parameter will be used.");
        }
    }
}

