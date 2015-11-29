/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.Attribute;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.libs.org.objectweb.asm.tree.AnnotationNode;

public class FieldNode
extends FieldVisitor {
    public int access;
    public String name;
    public String desc;
    public String signature;
    public Object value;
    public List visibleAnnotations;
    public List invisibleAnnotations;
    public List attrs;

    public FieldNode(int n, String string, String string2, String string3, Object object) {
        this(262144, n, string, string2, string3, object);
    }

    public FieldNode(int n, int n2, String string, String string2, String string3, Object object) {
        super(n);
        this.access = n2;
        this.name = string;
        this.desc = string2;
        this.signature = string3;
        this.value = object;
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        AnnotationNode annotationNode = new AnnotationNode(string);
        if (bl) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = new ArrayList(1);
            }
            this.visibleAnnotations.add(annotationNode);
        } else {
            if (this.invisibleAnnotations == null) {
                this.invisibleAnnotations = new ArrayList(1);
            }
            this.invisibleAnnotations.add(annotationNode);
        }
        return annotationNode;
    }

    public void visitAttribute(Attribute attribute) {
        if (this.attrs == null) {
            this.attrs = new ArrayList(1);
        }
        this.attrs.add(attribute);
    }

    public void visitEnd() {
    }

    public void check(int n) {
    }

    public void accept(ClassVisitor classVisitor) {
        AnnotationNode annotationNode;
        int n;
        FieldVisitor fieldVisitor = classVisitor.visitField(this.access, this.name, this.desc, this.signature, this.value);
        if (fieldVisitor == null) {
            return;
        }
        int n2 = this.visibleAnnotations == null ? 0 : this.visibleAnnotations.size();
        for (n = 0; n < n2; ++n) {
            annotationNode = (AnnotationNode)this.visibleAnnotations.get(n);
            annotationNode.accept(fieldVisitor.visitAnnotation(annotationNode.desc, true));
        }
        n2 = this.invisibleAnnotations == null ? 0 : this.invisibleAnnotations.size();
        for (n = 0; n < n2; ++n) {
            annotationNode = (AnnotationNode)this.invisibleAnnotations.get(n);
            annotationNode.accept(fieldVisitor.visitAnnotation(annotationNode.desc, false));
        }
        n2 = this.attrs == null ? 0 : this.attrs.size();
        for (n = 0; n < n2; ++n) {
            fieldVisitor.visitAttribute((Attribute)this.attrs.get(n));
        }
        fieldVisitor.visitEnd();
    }
}

