/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.awt.EventQueue;
import java.beans.ConstructorProperties;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import lombok.ast.AST;
import lombok.ast.AbstractMethodDecl;
import lombok.ast.Annotation;
import lombok.ast.Argument;
import lombok.ast.Block;
import lombok.ast.Call;
import lombok.ast.ClassDecl;
import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.IMethodEditor;
import lombok.ast.If;
import lombok.ast.LocalDecl;
import lombok.ast.MethodDecl;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.ast.Try;
import lombok.ast.TypeRef;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Names;

public final class SwingInvokeHandler<METHOD_TYPE extends IMethod<?, ?, ?, ?>> {
    private final METHOD_TYPE method;
    private final DiagnosticsReceiver diagnosticsReceiver;

    public void handle(String methodName, Class<? extends java.lang.annotation.Annotation> annotationType, IParameterValidator<METHOD_TYPE> validation, IParameterSanitizer<METHOD_TYPE> sanitizer) {
        if (this.method == null) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnMethodOnly(annotationType));
            return;
        }
        if (this.method.isAbstract() || this.method.isEmpty()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnConcreteMethodOnly(annotationType));
            return;
        }
        this.method.editor().forceQualifiedThis();
        String field = "$" + Names.camelCase(this.method.name(), "runnable");
        Call elseStatementRun = AST.Call(AST.Name(EventQueue.class), methodName).withArgument(AST.Name(field));
        Block elseStatement = "invokeAndWait".equals(methodName) ? AST.Block().withStatement(this.generateTryCatchBlock(elseStatementRun, this.method)) : AST.Block().withStatement(elseStatementRun);
        this.method.editor().replaceBody(((Block)AST.Block().posHint(this.method.get())).withStatements(validation.validateParameterOf(this.method)).withStatements(sanitizer.sanitizeParameterOf(this.method)).withStatement(((LocalDecl)AST.LocalDecl(AST.Type(Runnable.class), field).makeFinal()).withInitialization(AST.New(AST.Type(Runnable.class)).withTypeDeclaration(AST.ClassDecl("").makeAnonymous().makeLocal().withMethod(((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("void"), "run").makePublic()).withAnnotation(AST.Annotation(AST.Type(Override.class)))).withStatements(this.method.statements()))))).withStatement(AST.If(AST.Call(AST.Name(EventQueue.class), "isDispatchThread")).Then(AST.Block().withStatement(AST.Call(AST.Name(field), "run"))).Else(elseStatement)));
        this.method.editor().rebuild();
    }

    private Try generateTryCatchBlock(Call elseStatementRun, METHOD_TYPE method) {
        return AST.Try(AST.Block().withStatement(elseStatementRun)).Catch(AST.Arg(AST.Type(InterruptedException.class), "$ex1"), AST.Block()).Catch(AST.Arg(AST.Type(InvocationTargetException.class), "$ex2"), AST.Block().withStatement(((LocalDecl)AST.LocalDecl(AST.Type(Throwable.class), "$cause").makeFinal()).withInitialization(AST.Call(AST.Name("$ex2"), "getCause"))).withStatements(this.rethrowStatements(method)).withStatement(AST.Throw(AST.New(AST.Type(RuntimeException.class)).withArgument(AST.Name("$cause")))));
    }

    private List<Statement<?>> rethrowStatements(METHOD_TYPE method) {
        ArrayList rethrowStatements = new ArrayList();
        for (TypeRef thrownException : method.thrownExceptions()) {
            rethrowStatements.add(AST.If(AST.InstanceOf(AST.Name("$cause"), thrownException)).Then(AST.Throw(AST.Cast(thrownException, AST.Name("$cause")))));
        }
        return rethrowStatements;
    }

    @ConstructorProperties(value={"method", "diagnosticsReceiver"})
    public SwingInvokeHandler(METHOD_TYPE method, DiagnosticsReceiver diagnosticsReceiver) {
        this.method = method;
        this.diagnosticsReceiver = diagnosticsReceiver;
    }
}

