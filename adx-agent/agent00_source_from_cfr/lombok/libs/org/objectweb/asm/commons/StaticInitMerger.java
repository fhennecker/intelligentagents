/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.MethodVisitor;

public class StaticInitMerger
extends ClassVisitor {
    private String name;
    private MethodVisitor clinit;
    private final String prefix;
    private int counter;

    public StaticInitMerger(String string, ClassVisitor classVisitor) {
        this(262144, string, classVisitor);
    }

    protected StaticInitMerger(int n, String string, ClassVisitor classVisitor) {
        super(n, classVisitor);
        this.prefix = string;
    }

    public void visit(int n, int n2, String string, String string2, String string3, String[] arrstring) {
        this.cv.visit(n, n2, string, string2, string3, arrstring);
        this.name = string;
    }

    public MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] arrstring) {
        MethodVisitor methodVisitor;
        if ("<clinit>".equals(string)) {
            int n2 = 10;
            String string4 = this.prefix + this.counter++;
            methodVisitor = this.cv.visitMethod(n2, string4, string2, string3, arrstring);
            if (this.clinit == null) {
                this.clinit = this.cv.visitMethod(n2, string, string2, null, null);
            }
            this.clinit.visitMethodInsn(184, this.name, string4, string2);
        } else {
            methodVisitor = this.cv.visitMethod(n, string, string2, string3, arrstring);
        }
        return methodVisitor;
    }

    public void visitEnd() {
        if (this.clinit != null) {
            this.clinit.visitInsn(177);
            this.clinit.visitMaxs(0, 0);
        }
        this.cv.visitEnd();
    }
}

