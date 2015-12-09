/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

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
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.InitializableEclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.EclipseParameterSanitizer;
import lombok.eclipse.handlers.EclipseParameterValidator;
import lombok.eclipse.handlers.ast.EclipseMethod;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleRethrowAndRethrows {

    @DeferUntilPostDiet
    public static class HandleRethrows
    extends EclipseAnnotationHandler<Rethrows> {
        @Override
        public void handle(AnnotationValues<Rethrows> annotation, Annotation source, EclipseNode annotationNode) {
            RethrowAndRethrowsHandler<EclipseMethod> handler = new RethrowAndRethrowsHandler<EclipseMethod>(EclipseMethod.methodOf(annotationNode, (ASTNode)source), annotationNode);
            for (Object rethrow : annotation.getActualExpressions("value")) {
                InitializableEclipseNode rethrowNode = new InitializableEclipseNode((EclipseAST)annotationNode.getAst(), (ASTNode)rethrow, new ArrayList<EclipseNode>(), AST.Kind.ANNOTATION);
                Rethrow ann = (Rethrow)EclipseHandlerUtil.createAnnotation(Rethrow.class, rethrowNode).getInstance();
                handler.withRethrow(new RethrowAndRethrowsHandler.RethrowData(RethrowAndRethrowsHandler.classNames(ann.value()), ann.as(), ann.message()));
            }
            handler.handle(Rethrows.class, new EclipseParameterValidator(), new EclipseParameterSanitizer());
        }
    }

    @DeferUntilPostDiet
    public static class HandleRethrow
    extends EclipseAnnotationHandler<Rethrow> {
        @Override
        public void handle(AnnotationValues<Rethrow> annotation, Annotation source, EclipseNode annotationNode) {
            Rethrow ann = annotation.getInstance();
            new RethrowAndRethrowsHandler<EclipseMethod>(EclipseMethod.methodOf(annotationNode, (ASTNode)source), annotationNode).withRethrow(new RethrowAndRethrowsHandler.RethrowData(RethrowAndRethrowsHandler.classNames(ann.value()), ann.as(), ann.message())).handle(Rethrow.class, new EclipseParameterValidator(), new EclipseParameterSanitizer());
        }
    }

}

