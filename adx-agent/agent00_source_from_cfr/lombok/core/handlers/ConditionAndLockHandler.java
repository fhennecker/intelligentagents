/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Position;
import lombok.ast.AST;
import lombok.ast.Argument;
import lombok.ast.Block;
import lombok.ast.Call;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.IMethod;
import lombok.ast.IMethodEditor;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.ast.Try;
import lombok.ast.While;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Names;

public final class ConditionAndLockHandler<TYPE_TYPE extends IType<METHOD_TYPE, ?, ?, ?, ?, ?>, METHOD_TYPE extends IMethod<TYPE_TYPE, ?, ?, ?>> {
    private final TYPE_TYPE type;
    private final METHOD_TYPE method;
    private final DiagnosticsReceiver diagnosticsReceiver;
    private AwaitData await;
    private SignalData signal;
    private String lockMethod;

    public ConditionAndLockHandler<TYPE_TYPE, METHOD_TYPE> withAwait(AwaitData await) {
        this.await = await;
        return this;
    }

    public ConditionAndLockHandler<TYPE_TYPE, METHOD_TYPE> withSignal(SignalData signal) {
        this.signal = signal;
        return this;
    }

    public ConditionAndLockHandler<TYPE_TYPE, METHOD_TYPE> withLockMethod(String lockMethod) {
        this.lockMethod = lockMethod;
        return this;
    }

    public boolean preHandle(String lockName, Class<? extends Annotation> annotationType) {
        boolean isReadWriteLock;
        if (this.method == null) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnMethodOnly(annotationType));
            return false;
        }
        if (this.method.isAbstract()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnConcreteMethodOnly(annotationType));
            return false;
        }
        boolean bl = isReadWriteLock = this.lockMethod != null;
        if (!isReadWriteLock && this.await == null && this.signal == null) {
            return false;
        }
        String annotationTypeName = annotationType.getSimpleName();
        String completeLockName = this.createCompleteLockName(lockName, isReadWriteLock);
        if (!this.tryToAddLockField(completeLockName, isReadWriteLock, annotationTypeName)) {
            return false;
        }
        if (!isReadWriteLock) {
            if (!this.tryToAddConditionField(this.await, completeLockName, annotationTypeName)) {
                return false;
            }
            if (!this.tryToAddConditionField(this.signal, completeLockName, annotationTypeName)) {
                return false;
            }
        }
        return true;
    }

    public void handle(String lockName, Class<? extends Annotation> annotationType, IParameterValidator<METHOD_TYPE> validation, IParameterSanitizer<METHOD_TYPE> sanitizer) {
        Call unLockCall;
        Call lockCall;
        if (!this.preHandle(lockName, annotationType)) {
            return;
        }
        boolean isReadWriteLock = this.lockMethod != null;
        String annotationTypeName = annotationType.getSimpleName();
        String completeLockName = this.createCompleteLockName(lockName, isReadWriteLock);
        ArrayList beforeMethodBlock = new ArrayList();
        ArrayList afterMethodBlock = new ArrayList();
        if (!isReadWriteLock) {
            if (!this.getConditionStatements(this.await, completeLockName, annotationTypeName, beforeMethodBlock, afterMethodBlock)) {
                return;
            }
            if (!this.getConditionStatements(this.signal, completeLockName, annotationTypeName, beforeMethodBlock, afterMethodBlock)) {
                return;
            }
        }
        if (isReadWriteLock) {
            lockCall = AST.Call(AST.Call(AST.Field(completeLockName), this.lockMethod), "lock");
            unLockCall = AST.Call(AST.Call(AST.Field(completeLockName), this.lockMethod), "unlock");
        } else {
            lockCall = AST.Call(AST.Field(completeLockName), "lock");
            unLockCall = AST.Call(AST.Field(completeLockName), "unlock");
        }
        this.method.editor().replaceBody(((Block)AST.Block().posHint(this.method.get())).withStatements(validation.validateParameterOf(this.method)).withStatements(sanitizer.sanitizeParameterOf(this.method)).withStatement(lockCall).withStatement(AST.Try(AST.Block().withStatements(beforeMethodBlock).withStatements(this.method.statements()).withStatements(afterMethodBlock)).Finally(AST.Block().withStatement(unLockCall))));
        this.method.editor().rebuild();
    }

    private String createCompleteLockName(String lockName, boolean isReadWriteLock) {
        String completeLockName = lockName;
        if (!isReadWriteLock && Names.trim(lockName).isEmpty()) {
            String awaitCondition = Names.trim(this.await == null ? "" : this.await.condition);
            String signalCondition = Names.trim(this.signal == null ? "" : this.signal.condition);
            completeLockName = "$" + Names.camelCase(awaitCondition, signalCondition, "lock");
        }
        return completeLockName;
    }

    private boolean getConditionStatements(ConditionData condition, String lockName, String annotationTypeName, List<Statement<?>> before, List<Statement<?>> after) {
        if (condition == null) {
            return true;
        }
        if (this.tryToAddConditionField(condition, lockName, annotationTypeName)) {
            switch (condition.pos) {
                case BEFORE: {
                    before.add(condition.toStatement());
                    break;
                }
                default: {
                    after.add(condition.toStatement());
                }
            }
            return true;
        }
        return false;
    }

    private boolean tryToAddLockField(String lockName, boolean isReadWriteLock, String annotationTypeName) {
        String trimmedLockName = Names.trim(lockName);
        if (trimmedLockName.isEmpty()) {
            this.diagnosticsReceiver.addError(String.format("@%s 'lockName' may not be empty or null.", annotationTypeName));
            return false;
        }
        if (!this.type.hasField(trimmedLockName)) {
            if (isReadWriteLock) {
                this.type.editor().injectField(((FieldDecl)AST.FieldDecl(AST.Type(ReadWriteLock.class), trimmedLockName).makePrivate().makeFinal()).withInitialization(AST.New(AST.Type(ReentrantReadWriteLock.class))));
            } else {
                this.type.editor().injectField(((FieldDecl)AST.FieldDecl(AST.Type(Lock.class), trimmedLockName).makePrivate().makeFinal()).withInitialization(AST.New(AST.Type(ReentrantLock.class))));
            }
        }
        return true;
    }

    private boolean tryToAddConditionField(ConditionData condition, String lockName, String annotationTypeName) {
        if (condition == null) {
            return true;
        }
        String conditionName = Names.trim(condition.condition);
        if (conditionName.isEmpty()) {
            this.diagnosticsReceiver.addError(String.format("@%s 'conditionName' may not be empty or null.", annotationTypeName));
            return false;
        }
        if (!this.type.hasField(conditionName)) {
            this.type.editor().injectField(((FieldDecl)AST.FieldDecl(AST.Type(Condition.class), conditionName).makePrivate().makeFinal()).withInitialization(AST.Call(AST.Name(lockName), "newCondition")));
        }
        return true;
    }

    @ConstructorProperties(value={"type", "method", "diagnosticsReceiver"})
    public ConditionAndLockHandler(TYPE_TYPE type, METHOD_TYPE method, DiagnosticsReceiver diagnosticsReceiver) {
        this.type = type;
        this.method = method;
        this.diagnosticsReceiver = diagnosticsReceiver;
    }

    public static abstract class ConditionData {
        protected final String condition;
        protected final Position pos;

        public abstract Statement<?> toStatement();

        public ConditionData(String condition, Position pos) {
            this.condition = condition;
            this.pos = pos;
        }
    }

    public static class SignalData
    extends ConditionData {
        public SignalData(String condition, Position pos) {
            super(condition, pos);
        }

        @Override
        public Statement<?> toStatement() {
            return AST.Call(AST.Field(this.condition), "signal");
        }
    }

    public static class AwaitData
    extends ConditionData {
        protected final String conditionMethod;

        public AwaitData(String condition, String conditionMethod, Position pos) {
            super(condition, pos);
            this.conditionMethod = conditionMethod;
        }

        @Override
        public Statement<?> toStatement() {
            return AST.Try(AST.Block().withStatement(AST.While(AST.Call(AST.This(), this.conditionMethod)).Do(AST.Call(AST.Field(this.condition), "await")))).Catch(AST.Arg(AST.Type(InterruptedException.class), "e"), AST.Block().withStatement(AST.Throw(AST.New(AST.Type(RuntimeException.class)).withArgument(AST.Name("e")))));
        }
    }

}

