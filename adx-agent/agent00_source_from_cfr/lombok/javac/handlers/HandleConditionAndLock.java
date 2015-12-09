/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import java.lang.annotation.Annotation;
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
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacParameterSanitizer;
import lombok.javac.handlers.JavacParameterValidator;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacType;

public class HandleConditionAndLock {
    private static ConditionAndLockHandler<JavacType, JavacMethod> prepareConditionAndLockHandler(JavacNode node, JCTree.JCAnnotation source, Class<? extends Annotation> annotationType) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(node, annotationType);
        Javac.deleteImport(node, Position.class);
        return new ConditionAndLockHandler<JavacType, JavacMethod>(JavacType.typeOf(node, (JCTree)source), JavacMethod.methodOf(node, (JCTree)source), node);
    }

    public static class HandleAwaitBeforeAndSignalAfter
    extends JavacAnnotationHandler<AwaitBeforeAndSignalAfter> {
        @Override
        public void handle(AnnotationValues<AwaitBeforeAndSignalAfter> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            AwaitBeforeAndSignalAfter ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast, AwaitBeforeAndSignalAfter.class).withAwait(new ConditionAndLockHandler.AwaitData(ann.awaitConditionName(), ann.awaitConditionMethod(), Position.BEFORE)).withSignal(new ConditionAndLockHandler.SignalData(ann.signalConditionName(), Position.AFTER)).handle(ann.lockName(), AwaitBeforeAndSignalAfter.class, new JavacParameterValidator(), new JavacParameterSanitizer());
        }
    }

    public static class HandleAwait
    extends JavacAnnotationHandler<Await> {
        @Override
        public void handle(AnnotationValues<Await> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            Await ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast, Await.class).withAwait(new ConditionAndLockHandler.AwaitData(ann.conditionName(), ann.conditionMethod(), ann.pos())).handle(ann.lockName(), Await.class, new JavacParameterValidator(), new JavacParameterSanitizer());
        }
    }

    public static class HandleSignal
    extends JavacAnnotationHandler<Signal> {
        @Override
        public void handle(AnnotationValues<Signal> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            Signal ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast, Signal.class).withSignal(new ConditionAndLockHandler.SignalData(ann.value(), ann.pos())).handle(ann.lockName(), Signal.class, new JavacParameterValidator(), new JavacParameterSanitizer());
        }
    }

    public static class HandleWriteLock
    extends JavacAnnotationHandler<WriteLock> {
        @Override
        public void handle(AnnotationValues<WriteLock> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            WriteLock ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast, WriteLock.class).withLockMethod("writeLock").handle(ann.value(), WriteLock.class, new JavacParameterValidator(), new JavacParameterSanitizer());
        }
    }

    public static class HandleReadLock
    extends JavacAnnotationHandler<ReadLock> {
        @Override
        public void handle(AnnotationValues<ReadLock> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            ReadLock ann = annotation.getInstance();
            HandleConditionAndLock.prepareConditionAndLockHandler(annotationNode, ast, ReadLock.class).withLockMethod("readLock").handle(ann.value(), ReadLock.class, new JavacParameterValidator(), new JavacParameterSanitizer());
        }
    }

}

