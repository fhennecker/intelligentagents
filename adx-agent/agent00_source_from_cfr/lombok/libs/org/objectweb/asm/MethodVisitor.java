/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.Attribute;
import lombok.libs.org.objectweb.asm.Handle;
import lombok.libs.org.objectweb.asm.Label;

public abstract class MethodVisitor {
    protected final int api;
    protected MethodVisitor mv;

    public MethodVisitor(int n) {
        this(n, null);
    }

    public MethodVisitor(int n, MethodVisitor methodVisitor) {
        this.api = n;
        this.mv = methodVisitor;
    }

    public AnnotationVisitor visitAnnotationDefault() {
        if (this.mv != null) {
            return this.mv.visitAnnotationDefault();
        }
        return null;
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        if (this.mv != null) {
            return this.mv.visitAnnotation(string, bl);
        }
        return null;
    }

    public AnnotationVisitor visitParameterAnnotation(int n, String string, boolean bl) {
        if (this.mv != null) {
            return this.mv.visitParameterAnnotation(n, string, bl);
        }
        return null;
    }

    public void visitAttribute(Attribute attribute) {
        if (this.mv != null) {
            this.mv.visitAttribute(attribute);
        }
    }

    public void visitCode() {
        if (this.mv != null) {
            this.mv.visitCode();
        }
    }

    public void visitFrame(int n, int n2, Object[] arrobject, int n3, Object[] arrobject2) {
        if (this.mv != null) {
            this.mv.visitFrame(n, n2, arrobject, n3, arrobject2);
        }
    }

    public void visitInsn(int n) {
        if (this.mv != null) {
            this.mv.visitInsn(n);
        }
    }

    public void visitIntInsn(int n, int n2) {
        if (this.mv != null) {
            this.mv.visitIntInsn(n, n2);
        }
    }

    public void visitVarInsn(int n, int n2) {
        if (this.mv != null) {
            this.mv.visitVarInsn(n, n2);
        }
    }

    public void visitTypeInsn(int n, String string) {
        if (this.mv != null) {
            this.mv.visitTypeInsn(n, string);
        }
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        if (this.mv != null) {
            this.mv.visitFieldInsn(n, string, string2, string3);
        }
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        if (this.mv != null) {
            this.mv.visitMethodInsn(n, string, string2, string3);
        }
    }

    public /* varargs */ void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... arrobject) {
        if (this.mv != null) {
            this.mv.visitInvokeDynamicInsn(string, string2, handle, arrobject);
        }
    }

    public void visitJumpInsn(int n, Label label) {
        if (this.mv != null) {
            this.mv.visitJumpInsn(n, label);
        }
    }

    public void visitLabel(Label label) {
        if (this.mv != null) {
            this.mv.visitLabel(label);
        }
    }

    public void visitLdcInsn(Object object) {
        if (this.mv != null) {
            this.mv.visitLdcInsn(object);
        }
    }

    public void visitIincInsn(int n, int n2) {
        if (this.mv != null) {
            this.mv.visitIincInsn(n, n2);
        }
    }

    public /* varargs */ void visitTableSwitchInsn(int n, int n2, Label label, Label ... arrlabel) {
        if (this.mv != null) {
            this.mv.visitTableSwitchInsn(n, n2, label, arrlabel);
        }
    }

    public void visitLookupSwitchInsn(Label label, int[] arrn, Label[] arrlabel) {
        if (this.mv != null) {
            this.mv.visitLookupSwitchInsn(label, arrn, arrlabel);
        }
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        if (this.mv != null) {
            this.mv.visitMultiANewArrayInsn(string, n);
        }
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        if (this.mv != null) {
            this.mv.visitTryCatchBlock(label, label2, label3, string);
        }
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n) {
        if (this.mv != null) {
            this.mv.visitLocalVariable(string, string2, string3, label, label2, n);
        }
    }

    public void visitLineNumber(int n, Label label) {
        if (this.mv != null) {
            this.mv.visitLineNumber(n, label);
        }
    }

    public void visitMaxs(int n, int n2) {
        if (this.mv != null) {
            this.mv.visitMaxs(n, n2);
        }
    }

    public void visitEnd() {
        if (this.mv != null) {
            this.mv.visitEnd();
        }
    }
}

