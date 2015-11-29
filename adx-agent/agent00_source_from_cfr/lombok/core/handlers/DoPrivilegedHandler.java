/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.beans.ConstructorProperties;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import lombok.DoPrivileged;
import lombok.ast.AST;
import lombok.ast.AbstractMethodDecl;
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

public class DoPrivilegedHandler<METHOD_TYPE extends IMethod<?, ?, ?, ?>> {
    private final METHOD_TYPE method;
    private final DiagnosticsReceiver diagnosticsReceiver;

    public void handle(IParameterValidator<METHOD_TYPE> validation, IParameterSanitizer<METHOD_TYPE> sanitizer) {
        if (this.method == null) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnMethodOnly(DoPrivileged.class));
            return;
        }
        if (this.method.isAbstract() || this.method.isEmpty()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnConcreteMethodOnly(DoPrivileged.class));
            return;
        }
        this.method.editor().forceQualifiedThis();
        TypeRef innerReturnType = this.method.boxedReturns();
        if (this.method.returns("void")) {
            this.method.editor().replaceReturns((Statement)AST.Return(AST.Null()).posHint(this.method.get()));
            this.method.editor().replaceBody(((Block)AST.Block().posHint(this.method.get())).withStatements(validation.validateParameterOf(this.method)).withStatements(sanitizer.sanitizeParameterOf(this.method)).withStatement(AST.Try(AST.Block().withStatement(AST.Call(AST.Name(AccessController.class), "doPrivileged").withArgument(AST.New(AST.Type(PrivilegedExceptionAction.class).withTypeArgument(innerReturnType)).withTypeDeclaration(AST.ClassDecl("").makeAnonymous().makeLocal().withMethod(((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(innerReturnType, "run").makePublic()).withThrownExceptions(this.method.thrownExceptions())).withStatements(this.method.statements())).withStatement(AST.Return(AST.Null()))))))).Catch(AST.Arg(AST.Type(PrivilegedActionException.class), "$ex"), AST.Block().withStatement(((LocalDecl)AST.LocalDecl(AST.Type(Throwable.class), "$cause").makeFinal()).withInitialization(AST.Call(AST.Name("$ex"), "getCause"))).withStatements(this.rethrowStatements(this.method)).withStatement(AST.Throw(AST.New(AST.Type(RuntimeException.class)).withArgument(AST.Name("$cause")))))));
        } else {
            this.method.editor().replaceBody(((Block)AST.Block().posHint(this.method.get())).withStatements(validation.validateParameterOf(this.method)).withStatements(sanitizer.sanitizeParameterOf(this.method)).withStatement(AST.Try(AST.Block().withStatement(AST.Return(AST.Call(AST.Name(AccessController.class), "doPrivileged").withArgument(AST.New(AST.Type(PrivilegedExceptionAction.class).withTypeArgument(innerReturnType)).withTypeDeclaration(AST.ClassDecl("").makeAnonymous().makeLocal().withMethod(((MethodDecl)((MethodDecl)AST.MethodDecl(innerReturnType, "run").makePublic()).withThrownExceptions(this.method.thrownExceptions())).withStatements(this.method.statements()))))))).Catch(AST.Arg(AST.Type(PrivilegedActionException.class), "$ex"), AST.Block().withStatement(((LocalDecl)AST.LocalDecl(AST.Type(Throwable.class), "$cause").makeFinal()).withInitialization(AST.Call(AST.Name("$ex"), "getCause"))).withStatements(this.rethrowStatements(this.method)).withStatement(AST.Throw(AST.New(AST.Type(RuntimeException.class)).withArgument(AST.Name("$cause")))))));
        }
        this.method.editor().rebuild();
    }

    private List<Statement<?>> rethrowStatements(METHOD_TYPE method) {
        ArrayList rethrowStatements = new ArrayList();
        for (TypeRef thrownException : method.thrownExceptions()) {
            rethrowStatements.add(AST.If(AST.InstanceOf(AST.Name("$cause"), thrownException)).Then(AST.Throw(AST.Cast(thrownException, AST.Name("$cause")))));
        }
        return rethrowStatements;
    }

    @ConstructorProperties(value={"method", "diagnosticsReceiver"})
    public DoPrivilegedHandler(METHOD_TYPE method, DiagnosticsReceiver diagnosticsReceiver) {
        this.method = method;
        this.diagnosticsReceiver = diagnosticsReceiver;
    }
}

