/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Validate;
import lombok.ast.AST;
import lombok.ast.Block;
import lombok.ast.Call;
import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.If;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.core.util.As;

public interface IParameterValidator<METHOD_TYPE extends IMethod<?, ?, ?, ?>> {
    public List<Statement<?>> validateParameterOf(METHOD_TYPE var1);

    public static enum ValidationStrategy {
        WITH((Class)Validate.With.class){

            @Override
            public List<? extends Statement<?>> getStatementsFor(String argumentName, int argumentIndex, Annotation annotation) {
                ArrayList<Statement> statements = new ArrayList<Statement>();
                statements.addAll(NOT_NULL.getStatementsFor(argumentName, argumentIndex, annotation));
                statements.add(AST.If(AST.Not(AST.Call(((Validate.With)annotation).value()).withArgument(AST.Name(argumentName)))).Then(AST.Block().withStatement(AST.Throw(AST.New(AST.Type(IllegalArgumentException.class)).withArgument(ValidationStrategy.formattedMessage("The object '%s' (argument #%s) is invalid", argumentName, argumentIndex))))));
                return statements;
            }
        }
        ,
        NOT_NULL((Class)Validate.NotNull.class){

            @Override
            public List<? extends Statement<?>> getStatementsFor(String argumentName, int argumentIndex, Annotation annotation) {
                return Collections.singletonList(AST.If(AST.Equal(AST.Name(argumentName), AST.Null())).Then(AST.Block().withStatement(AST.Throw(AST.New(AST.Type(NullPointerException.class)).withArgument(ValidationStrategy.formattedMessage("The validated object '%s' (argument #%s) is null", argumentName, argumentIndex))))));
            }
        }
        ,
        NOT_EMPTY((Class)Validate.NotEmpty.class){

            @Override
            public List<? extends Statement<?>> getStatementsFor(String argumentName, int argumentIndex, Annotation annotation) {
                ArrayList<Statement> statements = new ArrayList<Statement>();
                statements.addAll(NOT_NULL.getStatementsFor(argumentName, argumentIndex, annotation));
                statements.add(AST.If(AST.Call(AST.Name(argumentName), "isEmpty")).Then(AST.Block().withStatement(AST.Throw(AST.New(AST.Type(IllegalArgumentException.class)).withArgument(ValidationStrategy.formattedMessage("The validated object '%s' (argument #%s) is empty", argumentName, argumentIndex))))));
                return statements;
            }
        };
        
        public static final Iterable<ValidationStrategy> IN_ORDER;
        private final Class<? extends Annotation> type;

        public abstract List<? extends Statement<?>> getStatementsFor(String var1, int var2, Annotation var3);

        private static final Expression<?> formattedMessage(String message, String argumentName, int argumentIndex) {
            return AST.Call(AST.Name(String.class), "format").withArgument(AST.String(message)).withArgument(AST.String(argumentName)).withArgument(AST.Number(argumentIndex));
        }

        private ValidationStrategy(Class<? extends Annotation> type) {
            this.type = type;
        }

        public Class<? extends Annotation> getType() {
            return this.type;
        }

        static {
            IN_ORDER = As.unmodifiableList(new ValidationStrategy[]{WITH, NOT_NULL, NOT_EMPTY});
        }

    }

}

