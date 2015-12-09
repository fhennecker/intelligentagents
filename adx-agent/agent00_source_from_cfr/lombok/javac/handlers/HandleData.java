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
import lombok.Data;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.HandleConstructor;
import lombok.javac.handlers.HandleEqualsAndHashCode;
import lombok.javac.handlers.HandleGetter;
import lombok.javac.handlers.HandleSetter;
import lombok.javac.handlers.HandleToString;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleData
extends JavacAnnotationHandler<Data> {
    @Override
    public void handle(AnnotationValues<Data> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        boolean notAClass;
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Data.class);
        JavacNode typeNode = (JavacNode)annotationNode.up();
        boolean bl = notAClass = !JavacHandlerUtil.isClass(typeNode);
        if (notAClass) {
            annotationNode.addError("@Data is only supported on a class.");
            return;
        }
        Data data = annotation.getInstance();
        String staticConstructorName = data.staticConstructor();
        boolean callSuper = data.callSuper();
        HandleConstructor.ConstructorData cData = new HandleConstructor.ConstructorData().fieldProvider(HandleConstructor.FieldProvider.REQUIRED).accessLevel(AccessLevel.PUBLIC).staticName(staticConstructorName).callSuper(callSuper);
        if (!HandleConstructor.constructorOrConstructorAnnotationExists(typeNode)) {
            new HandleConstructor().generateConstructor(typeNode, (JCTree)ast, cData);
        } else if (cData.staticConstructorRequired()) {
            annotationNode.addWarning("Ignoring static constructor name: explicit @XxxArgsConstructor annotation present; its `staticName` parameter will be used.");
        }
        new HandleGetter().generateGetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true);
        new HandleSetter().generateSetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true);
        new HandleEqualsAndHashCode().generateEqualsAndHashCodeForType(typeNode, annotationNode, callSuper);
        new HandleToString().generateToStringForType(typeNode, annotationNode, callSuper);
    }
}

