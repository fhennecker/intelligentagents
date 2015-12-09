/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.commons.Remapper;
import lombok.libs.org.objectweb.asm.commons.RemappingAnnotationAdapter;
import lombok.libs.org.objectweb.asm.commons.RemappingFieldAdapter;
import lombok.libs.org.objectweb.asm.commons.RemappingMethodAdapter;

public class RemappingClassAdapter
extends ClassVisitor {
    protected final Remapper remapper;
    protected String className;

    public RemappingClassAdapter(ClassVisitor classVisitor, Remapper remapper) {
        this(262144, classVisitor, remapper);
    }

    protected RemappingClassAdapter(int n, ClassVisitor classVisitor, Remapper remapper) {
        super(n, classVisitor);
        this.remapper = remapper;
    }

    public void visit(int n, int n2, String string, String string2, String string3, String[] arrstring) {
        this.className = string;
        super.visit(n, n2, this.remapper.mapType(string), this.remapper.mapSignature(string2, false), this.remapper.mapType(string3), arrstring == null ? null : this.remapper.mapTypes(arrstring));
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        AnnotationVisitor annotationVisitor = super.visitAnnotation(this.remapper.mapDesc(string), bl);
        return annotationVisitor == null ? null : this.createRemappingAnnotationAdapter(annotationVisitor);
    }

    public FieldVisitor visitField(int n, String string, String string2, String string3, Object object) {
        FieldVisitor fieldVisitor = super.visitField(n, this.remapper.mapFieldName(this.className, string, string2), this.remapper.mapDesc(string2), this.remapper.mapSignature(string3, true), this.remapper.mapValue(object));
        return fieldVisitor == null ? null : this.createRemappingFieldAdapter(fieldVisitor);
    }

    public MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] arrstring) {
        String string4 = this.remapper.mapMethodDesc(string2);
        MethodVisitor methodVisitor = super.visitMethod(n, this.remapper.mapMethodName(this.className, string, string2), string4, this.remapper.mapSignature(string3, false), arrstring == null ? null : this.remapper.mapTypes(arrstring));
        return methodVisitor == null ? null : this.createRemappingMethodAdapter(n, string4, methodVisitor);
    }

    public void visitInnerClass(String string, String string2, String string3, int n) {
        super.visitInnerClass(this.remapper.mapType(string), string2 == null ? null : this.remapper.mapType(string2), string3, n);
    }

    public void visitOuterClass(String string, String string2, String string3) {
        super.visitOuterClass(this.remapper.mapType(string), string2 == null ? null : this.remapper.mapMethodName(string, string2, string3), string3 == null ? null : this.remapper.mapMethodDesc(string3));
    }

    protected FieldVisitor createRemappingFieldAdapter(FieldVisitor fieldVisitor) {
        return new RemappingFieldAdapter(fieldVisitor, this.remapper);
    }

    protected MethodVisitor createRemappingMethodAdapter(int n, String string, MethodVisitor methodVisitor) {
        return new RemappingMethodAdapter(n, string, methodVisitor, this.remapper);
    }

    protected AnnotationVisitor createRemappingAnnotationAdapter(AnnotationVisitor annotationVisitor) {
        return new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
    }
}

