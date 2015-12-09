/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

import lombok.libs.org.objectweb.asm.ByteVector;
import lombok.libs.org.objectweb.asm.ClassReader;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.Label;

public class Attribute {
    public final String type;
    byte[] b;
    Attribute a;

    protected Attribute(String string) {
        this.type = string;
    }

    public boolean isUnknown() {
        return true;
    }

    public boolean isCodeAttribute() {
        return false;
    }

    protected Label[] getLabels() {
        return null;
    }

    protected Attribute read(ClassReader classReader, int n, int n2, char[] arrc, int n3, Label[] arrlabel) {
        Attribute attribute = new Attribute(this.type);
        attribute.b = new byte[n2];
        System.arraycopy(classReader.b, n, attribute.b, 0, n2);
        return attribute;
    }

    protected ByteVector write(ClassWriter classWriter, byte[] arrby, int n, int n2, int n3) {
        ByteVector byteVector = new ByteVector();
        byteVector.a = this.b;
        byteVector.b = this.b.length;
        return byteVector;
    }

    final int a() {
        int n = 0;
        Attribute attribute = this;
        while (attribute != null) {
            ++n;
            attribute = attribute.a;
        }
        return n;
    }

    final int a(ClassWriter classWriter, byte[] arrby, int n, int n2, int n3) {
        Attribute attribute = this;
        int n4 = 0;
        while (attribute != null) {
            classWriter.newUTF8(attribute.type);
            n4 += attribute.write((ClassWriter)classWriter, (byte[])arrby, (int)n, (int)n2, (int)n3).b + 6;
            attribute = attribute.a;
        }
        return n4;
    }

    final void a(ClassWriter classWriter, byte[] arrby, int n, int n2, int n3, ByteVector byteVector) {
        Attribute attribute = this;
        while (attribute != null) {
            ByteVector byteVector2 = attribute.write(classWriter, arrby, n, n2, n3);
            byteVector.putShort(classWriter.newUTF8(attribute.type)).putInt(byteVector2.b);
            byteVector.putByteArray(byteVector2.a, 0, byteVector2.b);
            attribute = attribute.a;
        }
    }
}

