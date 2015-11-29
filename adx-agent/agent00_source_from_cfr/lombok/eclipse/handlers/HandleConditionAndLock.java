/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import lombok.Await;
import lombok.AwaitBeforeAndSignalAfter;
import lombok.Position;
import lombok.ReadLock;
import lombok.Signal;
import lombok.WriteLock;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.ConditionAndLockHandler;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseParameterSanitizer;
import lombok.eclipse.handlers.EclipseParameterValidator;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleConditionAndLock {
    private static ConditionAndLockHandler<EclipseType, EclipseMethod> prepareConditionAndLockHandler(EclipseNode node, Annotation source) {
        return new ConditionAndLockHandler<EclipseType, EclipseMethod>(EclipseType.typeOf(node, (ASTNode)source), EclipseMethod.methodOf(node, (ASTNode)source), node);
    }

    @DeferUntilPostDiet
    public static class HandleAwaitBeforeAndSignalAfter
    extends EclipseAnnotationHandler<AwaitBeforeAndSignalAfter> {
        @Override
        public void preHandle(AnnotationValues<AwaitBeforeAndSignalAfter> annotation, Annotation ast, EclipseNode annotationNode) {
            AwaitBeforeAndSignalAfter ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withAwait(new ConditionAndLockHandler.AwaitData(ann.awaitConditionName(), ann.awaitConditionMethod(), Position.BEFORE)).withSignal(new ConditionAndLockHandler.SignalData(ann.signalConditionName(), Position.AFTER)).preHandle(ann.lockName(), AwaitBeforeAndSignalAfter.class);
        }

        @Override
        public void handle(AnnotationValues<AwaitBeforeAndSignalAfter> annotation, Annotation ast, EclipseNode annotationNode) {
            AwaitBeforeAndSignalAfter ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withAwait(new ConditionAndLockHandler.AwaitData(ann.awaitConditionName(), ann.awaitConditionMethod(), Position.BEFORE)).withSignal(new ConditionAndLockHandler.SignalData(ann.signalConditionName(), Position.AFTER)).handle(ann.lockName(), AwaitBeforeAndSignalAfter.class, new EclipseParameterValidator(), new EclipseParameterSanitizer());
        }
    }

    @DeferUntilPostDiet
    public static class HandleAwait
    extends EclipseAnnotationHandler<Await> {
        @Override
        public void preHandle(AnnotationValues<Await> annotation, Annotation ast, EclipseNode annotationNode) {
            Await ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withAwait(new ConditionAndLockHandler.AwaitData(ann.conditionName(), ann.conditionMethod(), ann.pos())).preHandle(ann.lockName(), Await.class);
        }

        @Override
        public void handle(AnnotationValues<Await> annotation, Annotation ast, EclipseNode annotationNode) {
            Await ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withAwait(new ConditionAndLockHandler.AwaitData(ann.conditionName(), ann.conditionMethod(), ann.pos())).handle(ann.lockName(), Await.class, new EclipseParameterValidator(), new EclipseParameterSanitizer());
        }
    }

    @DeferUntilPostDiet
    public static class HandleSignal
    extends EclipseAnnotationHandler<Signal> {
        @Override
        public void preHandle(AnnotationValues<Signal> annotation, Annotation ast, EclipseNode annotationNode) {
            Signal ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withSignal(new ConditionAndLockHandler.SignalData(ann.value(), ann.pos())).preHandle(ann.lockName(), Signal.class);
        }

        @Override
        public void handle(AnnotationValues<Signal> annotation, Annotation ast, EclipseNode annotationNode) {
            Signal ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withSignal(new ConditionAndLockHandler.SignalData(ann.value(), ann.pos())).handle(ann.lockName(), Signal.class, new EclipseParameterValidator(), new EclipseParameterSanitizer());
        }
    }

    @DeferUntilPostDiet
    public static class HandleWriteLock
    extends EclipseAnnotationHandler<WriteLock> {
        @Override
        public void preHandle(AnnotationValues<WriteLock> annotation, Annotation ast, EclipseNode annotationNode) {
            WriteLock ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withLockMethod("writeLock").preHandle(ann.value(), WriteLock.class);
        }

        @Override
        public void handle(AnnotationValues<WriteLock> annotation, Annotation ast, EclipseNode annotationNode) {
            WriteLock ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withLockMethod("writeLock").handle(ann.value(), WriteLock.class, new EclipseParameterValidator(), new EclipseParameterSanitizer());
        }
    }

    @DeferUntilPostDiet
    public static class HandleReadLock
    extends EclipseAnnotationHandler<ReadLock> {
        @Override
        public void preHandle(AnnotationValues<ReadLock> annotation, Annotation ast, EclipseNode annotationNode) {
            ReadLock ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withLockMethod("readLock").preHandle(ann.value(), ReadLock.class);
        }

        @Override
        public void handle(AnnotationValues<ReadLock> annotation, Annotation ast, EclipseNode annotationNode) {
            ReadLock ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast).withLockMethod("readLock").handle(ann.value(), ReadLock.class, new EclipseParameterValidator(), new EclipseParameterSanitizer());
        }
    }

}

