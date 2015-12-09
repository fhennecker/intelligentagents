/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.ast.ASTVisitor;
import lombok.ast.Node;
import lombok.ast.TypeRef;

public class JavaDoc
extends Node<JavaDoc> {
    private final Map<String, String> argumentReferences = new HashMap<String, String>();
    private final Map<String, String> paramTypeReferences = new HashMap<String, String>();
    private final Map<TypeRef, String> exceptionReferences = new HashMap<TypeRef, String>();
    private final String message;
    private String returnMessage;

    public JavaDoc() {
        this(null);
    }

    public JavaDoc withTypeParameter(String typeParameter) {
        return this.withTypeParameter(typeParameter, "");
    }

    public JavaDoc withTypeParameter(String typeParameter, String message) {
        this.paramTypeReferences.put(typeParameter, message);
        return this;
    }

    public JavaDoc withArgument(String argument) {
        return this.withArgument(argument, "");
    }

    public JavaDoc withArgument(String argument, String message) {
        this.argumentReferences.put(argument, message);
        return this;
    }

    public JavaDoc withException(TypeRef exceptionRef) {
        return this.withException(exceptionRef, "");
    }

    public JavaDoc withException(TypeRef exceptionRef, String message) {
        this.exceptionReferences.put(this.child(exceptionRef), message);
        return this;
    }

    public JavaDoc withReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitJavaDoc(this, p);
    }

    @ConstructorProperties(value={"message"})
    public JavaDoc(String message) {
        this.message = message;
    }

    public Map<String, String> getArgumentReferences() {
        return this.argumentReferences;
    }

    public Map<String, String> getParamTypeReferences() {
        return this.paramTypeReferences;
    }

    public Map<TypeRef, String> getExceptionReferences() {
        return this.exceptionReferences;
    }

    public String getMessage() {
        return this.message;
    }

    public String getReturnMessage() {
        return this.returnMessage;
    }
}

