/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.beans.ConstructorProperties;
import java.util.List;
import lombok.Sanitize;
import lombok.ast.AST;
import lombok.ast.Block;
import lombok.ast.IMethod;
import lombok.ast.IMethodEditor;
import lombok.ast.Statement;
import lombok.core.DiagnosticsReceiver;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.util.ErrorMessages;

public class SanitizeHandler<METHOD_TYPE extends IMethod<?, ?, ?, ?>> {
    private final METHOD_TYPE method;
    private final DiagnosticsReceiver diagnosticsReceiver;

    public void handle(IParameterSanitizer<METHOD_TYPE> sanitizer) {
        if (this.method == null) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnMethodOnly(Sanitize.class));
            return;
        }
        if (this.method.isAbstract() || this.method.isEmpty()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnConcreteMethodOnly(Sanitize.class));
            return;
        }
        this.method.editor().replaceBody(((Block)AST.Block().posHint(this.method.get())).withStatements(sanitizer.sanitizeParameterOf(this.method)).withStatements(this.method.statements()));
        this.method.editor().rebuild();
    }

    @ConstructorProperties(value={"method", "diagnosticsReceiver"})
    public SanitizeHandler(METHOD_TYPE method, DiagnosticsReceiver diagnosticsReceiver) {
        this.method = method;
        this.diagnosticsReceiver = diagnosticsReceiver;
    }
}

