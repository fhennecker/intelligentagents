/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.lang.annotation.Annotation;
import java.text.Normalizer;
import java.util.List;
import lombok.Sanitize;
import lombok.ast.AST;
import lombok.ast.Call;
import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.LocalDecl;
import lombok.ast.Statement;
import lombok.core.util.As;

public interface IParameterSanitizer<METHOD_TYPE extends IMethod<?, ?, ?, ?>> {
    public List<Statement<?>> sanitizeParameterOf(METHOD_TYPE var1);

    public static enum SanitizerStrategy {
        WITH((Class)Sanitize.With.class){

            @Override
            public Statement<?> getStatementFor(Object argumentType, String argumentName, String newArgumentName, Annotation annotation) {
                return ((LocalDecl)AST.LocalDecl(AST.Type(argumentType), newArgumentName).makeFinal()).withInitialization(AST.Call(((Sanitize.With)annotation).value()).withArgument(AST.Name(argumentName)));
            }
        }
        ,
        NORMALIZE((Class)Sanitize.Normalize.class){

            @Override
            public Statement<?> getStatementFor(Object argumentType, String argumentName, String newArgumentName, Annotation annotation) {
                Normalizer.Form normalizerForm = ((Sanitize.Normalize)annotation).value();
                return ((LocalDecl)AST.LocalDecl(AST.Type(argumentType), newArgumentName).makeFinal()).withInitialization(AST.Call(AST.Name("java.text.Normalizer"), "normalize").withArgument(AST.Name(argumentName)).withArgument(AST.Name(String.format("java.text.Normalizer.Form.%s", normalizerForm.name()))));
            }
        };
        
        public static final Iterable<SanitizerStrategy> IN_ORDER;
        private final Class<? extends Annotation> type;

        public abstract Statement<?> getStatementFor(Object var1, String var2, String var3, Annotation var4);

        private SanitizerStrategy(Class<? extends Annotation> type) {
            this.type = type;
        }

        public Class<? extends Annotation> getType() {
            return this.type;
        }

        static {
            IN_ORDER = As.unmodifiableList(new SanitizerStrategy[]{WITH, NORMALIZE});
        }

    }

}

