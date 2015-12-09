/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import lombok.libs.org.objectweb.asm.commons.Remapper;
import lombok.libs.org.objectweb.asm.signature.SignatureVisitor;

public class RemappingSignatureAdapter
extends SignatureVisitor {
    private final SignatureVisitor v;
    private final Remapper remapper;
    private String className;

    public RemappingSignatureAdapter(SignatureVisitor signatureVisitor, Remapper remapper) {
        this(262144, signatureVisitor, remapper);
    }

    protected RemappingSignatureAdapter(int n, SignatureVisitor signatureVisitor, Remapper remapper) {
        super(n);
        this.v = signatureVisitor;
        this.remapper = remapper;
    }

    public void visitClassType(String string) {
        this.className = string;
        this.v.visitClassType(this.remapper.mapType(string));
    }

    public void visitInnerClassType(String string) {
        this.className = this.className + '$' + string;
        String string2 = this.remapper.mapType(this.className);
        this.v.visitInnerClassType(string2.substring(string2.lastIndexOf(36) + 1));
    }

    public void visitFormalTypeParameter(String string) {
        this.v.visitFormalTypeParameter(string);
    }

    public void visitTypeVariable(String string) {
        this.v.visitTypeVariable(string);
    }

    public SignatureVisitor visitArrayType() {
        this.v.visitArrayType();
        return this;
    }

    public void visitBaseType(char c) {
        this.v.visitBaseType(c);
    }

    public SignatureVisitor visitClassBound() {
        this.v.visitClassBound();
        return this;
    }

    public SignatureVisitor visitExceptionType() {
        this.v.visitExceptionType();
        return this;
    }

    public SignatureVisitor visitInterface() {
        this.v.visitInterface();
        return this;
    }

    public SignatureVisitor visitInterfaceBound() {
        this.v.visitInterfaceBound();
        return this;
    }

    public SignatureVisitor visitParameterType() {
        this.v.visitParameterType();
        return this;
    }

    public SignatureVisitor visitReturnType() {
        this.v.visitReturnType();
        return this;
    }

    public SignatureVisitor visitSuperclass() {
        this.v.visitSuperclass();
        return this;
    }

    public void visitTypeArgument() {
        this.v.visitTypeArgument();
    }

    public SignatureVisitor visitTypeArgument(char c) {
        this.v.visitTypeArgument(c);
        return this;
    }

    public void visitEnd() {
        this.v.visitEnd();
    }
}

