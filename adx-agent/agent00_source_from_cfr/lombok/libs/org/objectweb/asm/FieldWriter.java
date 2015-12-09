/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.AnnotationWriter;
import lombok.libs.org.objectweb.asm.Attribute;
import lombok.libs.org.objectweb.asm.ByteVector;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.libs.org.objectweb.asm.Item;

final class FieldWriter
extends FieldVisitor {
    private final ClassWriter b;
    private final int c;
    private final int d;
    private final int e;
    private int f;
    private int g;
    private AnnotationWriter h;
    private AnnotationWriter i;
    private Attribute j;

    FieldWriter(ClassWriter classWriter, int n, String string, String string2, String string3, Object object) {
        super(262144);
        if (classWriter.B == null) {
            classWriter.B = this;
        } else {
            classWriter.C.fv = this;
        }
        classWriter.C = this;
        this.b = classWriter;
        this.c = n;
        this.d = classWriter.newUTF8(string);
        this.e = classWriter.newUTF8(string2);
        if (string3 != null) {
            this.f = classWriter.newUTF8(string3);
        }
        if (object != null) {
            this.g = classWriter.a((Object)object).a;
        }
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        ByteVector byteVector = new ByteVector();
        byteVector.putShort(this.b.newUTF8(string)).putShort(0);
        AnnotationWriter annotationWriter = new AnnotationWriter(this.b, true, byteVector, byteVector, 2);
        if (bl) {
            annotationWriter.g = this.h;
            this.h = annotationWriter;
        } else {
            annotationWriter.g = this.i;
            this.i = annotationWriter;
        }
        return annotationWriter;
    }

    public void visitAttribute(Attribute attribute) {
        attribute.a = this.j;
        this.j = attribute;
    }

    public void visitEnd() {
    }

    int a() {
        int n = 8;
        if (this.g != 0) {
            this.b.newUTF8("ConstantValue");
            n += 8;
        }
        if ((this.c & 4096) != 0 && ((this.b.b & 65535) < 49 || (this.c & 262144) != 0)) {
            this.b.newUTF8("Synthetic");
            n += 6;
        }
        if ((this.c & 131072) != 0) {
            this.b.newUTF8("Deprecated");
            n += 6;
        }
        if (this.f != 0) {
            this.b.newUTF8("Signature");
            n += 8;
        }
        if (this.h != null) {
            this.b.newUTF8("RuntimeVisibleAnnotations");
            n += 8 + this.h.a();
        }
        if (this.i != null) {
            this.b.newUTF8("RuntimeInvisibleAnnotations");
            n += 8 + this.i.a();
        }
        if (this.j != null) {
            n += this.j.a(this.b, null, 0, -1, -1);
        }
        return n;
    }

    void a(ByteVector byteVector) {
        int n = 393216 | (this.c & 262144) / 64;
        byteVector.putShort(this.c & ~ n).putShort(this.d).putShort(this.e);
        int n2 = 0;
        if (this.g != 0) {
            ++n2;
        }
        if ((this.c & 4096) != 0 && ((this.b.b & 65535) < 49 || (this.c & 262144) != 0)) {
            ++n2;
        }
        if ((this.c & 131072) != 0) {
            ++n2;
        }
        if (this.f != 0) {
            ++n2;
        }
        if (this.h != null) {
            ++n2;
        }
        if (this.i != null) {
            ++n2;
        }
        if (this.j != null) {
            n2 += this.j.a();
        }
        byteVector.putShort(n2);
        if (this.g != 0) {
            byteVector.putShort(this.b.newUTF8("ConstantValue"));
            byteVector.putInt(2).putShort(this.g);
        }
        if ((this.c & 4096) != 0 && ((this.b.b & 65535) < 49 || (this.c & 262144) != 0)) {
            byteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
        }
        if ((this.c & 131072) != 0) {
            byteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
        }
        if (this.f != 0) {
            byteVector.putShort(this.b.newUTF8("Signature"));
            byteVector.putInt(2).putShort(this.f);
        }
        if (this.h != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
            this.h.a(byteVector);
        }
        if (this.i != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
            this.i.a(byteVector);
        }
        if (this.j != null) {
            this.j.a(this.b, null, 0, -1, -1, byteVector);
        }
    }
}

