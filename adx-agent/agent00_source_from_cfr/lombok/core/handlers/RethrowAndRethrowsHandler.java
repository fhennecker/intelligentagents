/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.ast.AST;
import lombok.ast.Argument;
import lombok.ast.Block;
import lombok.ast.Call;
import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.IMethodEditor;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.ast.Try;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.handlers.IParameterValidator;
import lombok.core.util.As;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Is;

public final class RethrowAndRethrowsHandler<METHOD_TYPE extends IMethod<?, ?, ?, ?>> {
    private final List<RethrowData> rethrows = new ArrayList<RethrowData>();
    private final METHOD_TYPE method;
    private final DiagnosticsReceiver diagnosticsReceiver;

    public RethrowAndRethrowsHandler<METHOD_TYPE> withRethrow(RethrowData rethrowData) {
        this.rethrows.add(rethrowData);
        return this;
    }

    public void handle(Class<? extends Annotation> annotationType, IParameterValidator<METHOD_TYPE> validation, IParameterSanitizer<METHOD_TYPE> sanitizer) {
        if (this.rethrows.isEmpty()) {
            return;
        }
        if (this.method == null) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnMethodOnly(annotationType));
            return;
        }
        if (this.method.isAbstract() || this.method.isEmpty()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnConcreteMethodOnly(annotationType));
            return;
        }
        Try tryBuilder = AST.Try(AST.Block().withStatements(validation.validateParameterOf(this.method)).withStatements(sanitizer.sanitizeParameterOf(this.method)).withStatements(this.method.statements()));
        int counter = 1;
        for (RethrowData rethrow : this.rethrows) {
            for (Class thrown : rethrow.thrown) {
                String varname = "$e" + counter++;
                String message = rethrow.message;
                if (RethrowData.class == thrown) {
                    tryBuilder.Catch(AST.Arg(AST.Type(RuntimeException.class), varname), AST.Block().withStatement(AST.Throw(AST.Name(varname))));
                    continue;
                }
                if (message.isEmpty()) {
                    tryBuilder.Catch(AST.Arg(AST.Type(thrown.getName()), varname), AST.Block().withStatement(AST.Throw(AST.New(AST.Type(rethrow.as.getName())).withArgument(AST.Name(varname)))));
                    continue;
                }
                ArrayList arguments = new ArrayList();
                message = this.manipulateMessage(message, arguments);
                tryBuilder.Catch(AST.Arg(AST.Type(thrown.getName()), varname), AST.Block().withStatement(AST.Throw(AST.New(AST.Type(rethrow.as.getName())).withArgument(AST.Call(AST.Name(String.class), "format").withArgument(AST.String(message)).withArguments(arguments)).withArgument(AST.Name(varname)))));
            }
        }
        this.method.editor().replaceBody((Block)AST.Block().withStatement(tryBuilder).posHint(this.method.get()));
        this.method.editor().rebuild();
    }

    private String manipulateMessage(String message, List<Expression<?>> arguments) {
        Matcher matcher = Pattern.compile("\\$([a-zA-Z0-9_]+)").matcher(message);
        StringBuilder manipulatedMessage = new StringBuilder();
        int start = 0;
        while (matcher.find()) {
            manipulatedMessage.append(message.substring(start, matcher.start())).append("%s");
            arguments.add(AST.Name(message.substring(matcher.start(1), matcher.end(1))));
            start = matcher.end();
        }
        manipulatedMessage.append(message.substring(start, message.length()));
        return manipulatedMessage.toString();
    }

    public static List<Class<?>> classNames(Class<?>[] classes) {
        if (Is.empty(classes)) {
            return As.list(RethrowData.class, Exception.class);
        }
        return As.list(classes);
    }

    @ConstructorProperties(value={"method", "diagnosticsReceiver"})
    public RethrowAndRethrowsHandler(METHOD_TYPE method, DiagnosticsReceiver diagnosticsReceiver) {
        this.method = method;
        this.diagnosticsReceiver = diagnosticsReceiver;
    }

    public static class RethrowData {
        public final List<Class<?>> thrown;
        public final Class<?> as;
        public final String message;

        public RethrowData(List<Class<?>> thrown, Class<?> as, String message) {
            this.thrown = thrown;
            this.as = as;
            this.message = message;
        }
    }

}

