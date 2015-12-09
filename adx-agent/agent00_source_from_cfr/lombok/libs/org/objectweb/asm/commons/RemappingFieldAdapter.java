/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.libs.org.objectweb.asm.commons.Remapper;
import lombok.libs.org.objectweb.asm.commons.RemappingAnnotationAdapter;

public class RemappingFieldAdapter
extends FieldVisitor {
    private final Remapper remapper;

    public RemappingFieldAdapter(FieldVisitor fieldVisitor, Remapper remapper) {
        this(262144, fieldVisitor, remapper);
    }

    protected RemappingFieldAdapter(int n, FieldVisitor fieldVisitor, Remapper remapper) {
        super(n, fieldVisitor);
        this.remapper = remapper;
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        AnnotationVisitor annotationVisitor = this.fv.visitAnnotation(this.remapper.mapDesc(string), bl);
        return annotationVisitor == null ? null : new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
    }
}

