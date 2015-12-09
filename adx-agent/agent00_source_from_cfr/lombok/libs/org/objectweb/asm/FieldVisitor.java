/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.Attribute;

public abstract class FieldVisitor {
    protected final int api;
    protected FieldVisitor fv;

    public FieldVisitor(int n) {
        this(n, null);
    }

    public FieldVisitor(int n, FieldVisitor fieldVisitor) {
        this.api = n;
        this.fv = fieldVisitor;
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        if (this.fv != null) {
            return this.fv.visitAnnotation(string, bl);
        }
        return null;
    }

    public void visitAttribute(Attribute attribute) {
        if (this.fv != null) {
            this.fv.visitAttribute(attribute);
        }
    }

    public void visitEnd() {
        if (this.fv != null) {
            this.fv.visitEnd();
        }
    }
}

