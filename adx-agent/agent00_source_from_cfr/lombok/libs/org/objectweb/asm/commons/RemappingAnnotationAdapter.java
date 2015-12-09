/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.commons.Remapper;

public class RemappingAnnotationAdapter
extends AnnotationVisitor {
    protected final Remapper remapper;

    public RemappingAnnotationAdapter(AnnotationVisitor annotationVisitor, Remapper remapper) {
        this(262144, annotationVisitor, remapper);
    }

    protected RemappingAnnotationAdapter(int n, AnnotationVisitor annotationVisitor, Remapper remapper) {
        super(n, annotationVisitor);
        this.remapper = remapper;
    }

    public void visit(String string, Object object) {
        this.av.visit(string, this.remapper.mapValue(object));
    }

    public void visitEnum(String string, String string2, String string3) {
        this.av.visitEnum(string, this.remapper.mapDesc(string2), string3);
    }

    public AnnotationVisitor visitAnnotation(String string, String string2) {
        AnnotationVisitor annotationVisitor = this.av.visitAnnotation(string, this.remapper.mapDesc(string2));
        return annotationVisitor == null ? null : (annotationVisitor == this.av ? this : new RemappingAnnotationAdapter(annotationVisitor, this.remapper));
    }

    public AnnotationVisitor visitArray(String string) {
        AnnotationVisitor annotationVisitor = this.av.visitArray(string);
        return annotationVisitor == null ? null : (annotationVisitor == this.av ? this : new RemappingAnnotationAdapter(annotationVisitor, this.remapper));
    }
}

