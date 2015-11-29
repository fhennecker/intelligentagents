/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.Handle;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.commons.LocalVariablesSorter;
import lombok.libs.org.objectweb.asm.commons.Remapper;
import lombok.libs.org.objectweb.asm.commons.RemappingAnnotationAdapter;

public class RemappingMethodAdapter
extends LocalVariablesSorter {
    protected final Remapper remapper;

    public RemappingMethodAdapter(int n, String string, MethodVisitor methodVisitor, Remapper remapper) {
        this(262144, n, string, methodVisitor, remapper);
    }

    protected RemappingMethodAdapter(int n, int n2, String string, MethodVisitor methodVisitor, Remapper remapper) {
        super(n, n2, string, methodVisitor);
        this.remapper = remapper;
    }

    public AnnotationVisitor visitAnnotationDefault() {
        AnnotationVisitor annotationVisitor = this.mv.visitAnnotationDefault();
        return annotationVisitor == null ? annotationVisitor : new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        AnnotationVisitor annotationVisitor = this.mv.visitAnnotation(this.remapper.mapDesc(string), bl);
        return annotationVisitor == null ? annotationVisitor : new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
    }

    public AnnotationVisitor visitParameterAnnotation(int n, String string, boolean bl) {
        AnnotationVisitor annotationVisitor = this.mv.visitParameterAnnotation(n, this.remapper.mapDesc(string), bl);
        return annotationVisitor == null ? annotationVisitor : new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
    }

    public void visitFrame(int n, int n2, Object[] arrobject, int n3, Object[] arrobject2) {
        super.visitFrame(n, n2, this.remapEntries(n2, arrobject), n3, this.remapEntries(n3, arrobject2));
    }

    private Object[] remapEntries(int n, Object[] arrobject) {
        for (int i = 0; i < n; ++i) {
            if (!(arrobject[i] instanceof String)) continue;
            Object[] arrobject2 = new Object[n];
            if (i > 0) {
                System.arraycopy(arrobject, 0, arrobject2, 0, i);
            }
            do {
                Object object = arrobject[i];
                Object object2 = arrobject2[i++] = object instanceof String ? this.remapper.mapType((String)object) : object;
            } while (i < n);
            return arrobject2;
        }
        return arrobject;
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        super.visitFieldInsn(n, this.remapper.mapType(string), this.remapper.mapFieldName(string, string2, string3), this.remapper.mapDesc(string3));
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        super.visitMethodInsn(n, this.remapper.mapType(string), this.remapper.mapMethodName(string, string2, string3), this.remapper.mapMethodDesc(string3));
    }

    public /* varargs */ void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... arrobject) {
        for (int i = 0; i < arrobject.length; ++i) {
            arrobject[i] = this.remapper.mapValue(arrobject[i]);
        }
        super.visitInvokeDynamicInsn(this.remapper.mapInvokeDynamicMethodName(string, string2), this.remapper.mapMethodDesc(string2), (Handle)this.remapper.mapValue(handle), arrobject);
    }

    public void visitTypeInsn(int n, String string) {
        super.visitTypeInsn(n, this.remapper.mapType(string));
    }

    public void visitLdcInsn(Object object) {
        super.visitLdcInsn(this.remapper.mapValue(object));
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        super.visitMultiANewArrayInsn(this.remapper.mapDesc(string), n);
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        super.visitTryCatchBlock(label, label2, label3, string == null ? null : this.remapper.mapType(string));
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n) {
        super.visitLocalVariable(string, this.remapper.mapDesc(string2), this.remapper.mapSignature(string3, true), label, label2, n);
    }
}

