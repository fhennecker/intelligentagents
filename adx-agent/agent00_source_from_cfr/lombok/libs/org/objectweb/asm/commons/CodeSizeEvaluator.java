/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import lombok.libs.org.objectweb.asm.Handle;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.Opcodes;

public class CodeSizeEvaluator
extends MethodVisitor
implements Opcodes {
    private int minSize;
    private int maxSize;

    public CodeSizeEvaluator(MethodVisitor methodVisitor) {
        this(262144, methodVisitor);
    }

    protected CodeSizeEvaluator(int n, MethodVisitor methodVisitor) {
        super(n, methodVisitor);
    }

    public int getMinSize() {
        return this.minSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void visitInsn(int n) {
        ++this.minSize;
        ++this.maxSize;
        if (this.mv != null) {
            this.mv.visitInsn(n);
        }
    }

    public void visitIntInsn(int n, int n2) {
        if (n == 17) {
            this.minSize += 3;
            this.maxSize += 3;
        } else {
            this.minSize += 2;
            this.maxSize += 2;
        }
        if (this.mv != null) {
            this.mv.visitIntInsn(n, n2);
        }
    }

    public void visitVarInsn(int n, int n2) {
        if (n2 < 4 && n != 169) {
            ++this.minSize;
            ++this.maxSize;
        } else if (n2 >= 256) {
            this.minSize += 4;
            this.maxSize += 4;
        } else {
            this.minSize += 2;
            this.maxSize += 2;
        }
        if (this.mv != null) {
            this.mv.visitVarInsn(n, n2);
        }
    }

    public void visitTypeInsn(int n, String string) {
        this.minSize += 3;
        this.maxSize += 3;
        if (this.mv != null) {
            this.mv.visitTypeInsn(n, string);
        }
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        this.minSize += 3;
        this.maxSize += 3;
        if (this.mv != null) {
            this.mv.visitFieldInsn(n, string, string2, string3);
        }
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        if (n == 185) {
            this.minSize += 5;
            this.maxSize += 5;
        } else {
            this.minSize += 3;
            this.maxSize += 3;
        }
        if (this.mv != null) {
            this.mv.visitMethodInsn(n, string, string2, string3);
        }
    }

    public /* varargs */ void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... arrobject) {
        this.minSize += 5;
        this.maxSize += 5;
        if (this.mv != null) {
            this.mv.visitInvokeDynamicInsn(string, string2, handle, arrobject);
        }
    }

    public void visitJumpInsn(int n, Label label) {
        this.minSize += 3;
        this.maxSize = n == 167 || n == 168 ? (this.maxSize += 5) : (this.maxSize += 8);
        if (this.mv != null) {
            this.mv.visitJumpInsn(n, label);
        }
    }

    public void visitLdcInsn(Object object) {
        if (object instanceof Long || object instanceof Double) {
            this.minSize += 3;
            this.maxSize += 3;
        } else {
            this.minSize += 2;
            this.maxSize += 3;
        }
        if (this.mv != null) {
            this.mv.visitLdcInsn(object);
        }
    }

    public void visitIincInsn(int n, int n2) {
        if (n > 255 || n2 > 127 || n2 < -128) {
            this.minSize += 6;
            this.maxSize += 6;
        } else {
            this.minSize += 3;
            this.maxSize += 3;
        }
        if (this.mv != null) {
            this.mv.visitIincInsn(n, n2);
        }
    }

    public /* varargs */ void visitTableSwitchInsn(int n, int n2, Label label, Label ... arrlabel) {
        this.minSize += 13 + arrlabel.length * 4;
        this.maxSize += 16 + arrlabel.length * 4;
        if (this.mv != null) {
            this.mv.visitTableSwitchInsn(n, n2, label, arrlabel);
        }
    }

    public void visitLookupSwitchInsn(Label label, int[] arrn, Label[] arrlabel) {
        this.minSize += 9 + arrn.length * 8;
        this.maxSize += 12 + arrn.length * 8;
        if (this.mv != null) {
            this.mv.visitLookupSwitchInsn(label, arrn, arrlabel);
        }
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        this.minSize += 4;
        this.maxSize += 4;
        if (this.mv != null) {
            this.mv.visitMultiANewArrayInsn(string, n);
        }
    }
}

