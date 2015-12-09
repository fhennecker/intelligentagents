/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.AnnotationWriter;
import lombok.libs.org.objectweb.asm.Attribute;
import lombok.libs.org.objectweb.asm.ByteVector;
import lombok.libs.org.objectweb.asm.ClassReader;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.libs.org.objectweb.asm.FieldWriter;
import lombok.libs.org.objectweb.asm.Handle;
import lombok.libs.org.objectweb.asm.Item;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.MethodWriter;
import lombok.libs.org.objectweb.asm.Type;

/*
 * Duplicate member names - consider using --renamedupmembers true
 */
public class ClassWriter
extends ClassVisitor {
    public static final int COMPUTE_MAXS = 1;
    public static final int COMPUTE_FRAMES = 2;
    static final byte[] a;
    ClassReader M;
    int b;
    int c = 1;
    final ByteVector d = new ByteVector();
    Item[] e = new Item[256];
    int f = (int)(0.75 * (double)this.e.length);
    final Item g = new Item();
    final Item h = new Item();
    final Item i = new Item();
    final Item j = new Item();
    Item[] H;
    private short G;
    private int k;
    private int l;
    String I;
    private int m;
    private int n;
    private int o;
    private int[] p;
    private int q;
    private ByteVector r;
    private int s;
    private int t;
    private AnnotationWriter u;
    private AnnotationWriter v;
    private Attribute w;
    private int x;
    private ByteVector y;
    int z;
    ByteVector A;
    FieldWriter B;
    FieldWriter C;
    MethodWriter D;
    MethodWriter E;
    private final boolean K;
    private final boolean J;
    boolean L;

    public ClassWriter(int n) {
        super(262144);
        this.K = (n & 1) != 0;
        this.J = (n & 2) != 0;
    }

    public ClassWriter(ClassReader classReader, int n) {
        this(n);
        classReader.a(this);
        this.M = classReader;
    }

    public final void visit(int n, int n2, String string, String string2, String string3, String[] arrstring) {
        this.b = n;
        this.k = n2;
        this.l = this.newClass(string);
        this.I = string;
        if (string2 != null) {
            this.m = this.newUTF8(string2);
        }
        int n3 = this.n = string3 == null ? 0 : this.newClass(string3);
        if (arrstring != null && arrstring.length > 0) {
            this.o = arrstring.length;
            this.p = new int[this.o];
            for (int i = 0; i < this.o; ++i) {
                this.p[i] = this.newClass(arrstring[i]);
            }
        }
    }

    public final void visitSource(String string, String string2) {
        if (string != null) {
            this.q = this.newUTF8(string);
        }
        if (string2 != null) {
            this.r = new ByteVector().putUTF8(string2);
        }
    }

    public final void visitOuterClass(String string, String string2, String string3) {
        this.s = this.newClass(string);
        if (string2 != null && string3 != null) {
            this.t = this.newNameType(string2, string3);
        }
    }

    public final AnnotationVisitor visitAnnotation(String string, boolean bl) {
        ByteVector byteVector = new ByteVector();
        byteVector.putShort(this.newUTF8(string)).putShort(0);
        AnnotationWriter annotationWriter = new AnnotationWriter(this, true, byteVector, byteVector, 2);
        if (bl) {
            annotationWriter.g = this.u;
            this.u = annotationWriter;
        } else {
            annotationWriter.g = this.v;
            this.v = annotationWriter;
        }
        return annotationWriter;
    }

    public final void visitAttribute(Attribute attribute) {
        attribute.a = this.w;
        this.w = attribute;
    }

    public final void visitInnerClass(String string, String string2, String string3, int n) {
        if (this.y == null) {
            this.y = new ByteVector();
        }
        ++this.x;
        this.y.putShort(string == null ? 0 : this.newClass(string));
        this.y.putShort(string2 == null ? 0 : this.newClass(string2));
        this.y.putShort(string3 == null ? 0 : this.newUTF8(string3));
        this.y.putShort(n);
    }

    public final FieldVisitor visitField(int n, String string, String string2, String string3, Object object) {
        return new FieldWriter(this, n, string, string2, string3, object);
    }

    public final MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] arrstring) {
        return new MethodWriter(this, n, string, string2, string3, arrstring, this.K, this.J);
    }

    public final void visitEnd() {
    }

    public byte[] toByteArray() {
        int n;
        if (this.c > 32767) {
            throw new RuntimeException("Class file too large!");
        }
        int n2 = 24 + 2 * this.o;
        int n3 = 0;
        FieldWriter fieldWriter = this.B;
        while (fieldWriter != null) {
            ++n3;
            n2 += fieldWriter.a();
            fieldWriter = (FieldWriter)fieldWriter.fv;
        }
        int n4 = 0;
        MethodWriter methodWriter = this.D;
        while (methodWriter != null) {
            ++n4;
            n2 += methodWriter.a();
            methodWriter = (MethodWriter)methodWriter.mv;
        }
        int n5 = 0;
        if (this.A != null) {
            ++n5;
            n2 += 8 + this.A.b;
            this.newUTF8("BootstrapMethods");
        }
        if (this.m != 0) {
            ++n5;
            n2 += 8;
            this.newUTF8("Signature");
        }
        if (this.q != 0) {
            ++n5;
            n2 += 8;
            this.newUTF8("SourceFile");
        }
        if (this.r != null) {
            ++n5;
            n2 += this.r.b + 4;
            this.newUTF8("SourceDebugExtension");
        }
        if (this.s != 0) {
            ++n5;
            n2 += 10;
            this.newUTF8("EnclosingMethod");
        }
        if ((this.k & 131072) != 0) {
            ++n5;
            n2 += 6;
            this.newUTF8("Deprecated");
        }
        if ((this.k & 4096) != 0 && ((this.b & 65535) < 49 || (this.k & 262144) != 0)) {
            ++n5;
            n2 += 6;
            this.newUTF8("Synthetic");
        }
        if (this.y != null) {
            ++n5;
            n2 += 8 + this.y.b;
            this.newUTF8("InnerClasses");
        }
        if (this.u != null) {
            ++n5;
            n2 += 8 + this.u.a();
            this.newUTF8("RuntimeVisibleAnnotations");
        }
        if (this.v != null) {
            ++n5;
            n2 += 8 + this.v.a();
            this.newUTF8("RuntimeInvisibleAnnotations");
        }
        if (this.w != null) {
            n5 += this.w.a();
            n2 += this.w.a(this, null, 0, -1, -1);
        }
        ByteVector byteVector = new ByteVector(n2 += this.d.b);
        byteVector.putInt(-889275714).putInt(this.b);
        byteVector.putShort(this.c).putByteArray(this.d.a, 0, this.d.b);
        int n6 = 393216 | (this.k & 262144) / 64;
        byteVector.putShort(this.k & ~ n6).putShort(this.l).putShort(this.n);
        byteVector.putShort(this.o);
        for (n = 0; n < this.o; ++n) {
            byteVector.putShort(this.p[n]);
        }
        byteVector.putShort(n3);
        fieldWriter = this.B;
        while (fieldWriter != null) {
            fieldWriter.a(byteVector);
            fieldWriter = (FieldWriter)fieldWriter.fv;
        }
        byteVector.putShort(n4);
        methodWriter = this.D;
        while (methodWriter != null) {
            methodWriter.a(byteVector);
            methodWriter = (MethodWriter)methodWriter.mv;
        }
        byteVector.putShort(n5);
        if (this.A != null) {
            byteVector.putShort(this.newUTF8("BootstrapMethods"));
            byteVector.putInt(this.A.b + 2).putShort(this.z);
            byteVector.putByteArray(this.A.a, 0, this.A.b);
        }
        if (this.m != 0) {
            byteVector.putShort(this.newUTF8("Signature")).putInt(2).putShort(this.m);
        }
        if (this.q != 0) {
            byteVector.putShort(this.newUTF8("SourceFile")).putInt(2).putShort(this.q);
        }
        if (this.r != null) {
            n = this.r.b - 2;
            byteVector.putShort(this.newUTF8("SourceDebugExtension")).putInt(n);
            byteVector.putByteArray(this.r.a, 2, n);
        }
        if (this.s != 0) {
            byteVector.putShort(this.newUTF8("EnclosingMethod")).putInt(4);
            byteVector.putShort(this.s).putShort(this.t);
        }
        if ((this.k & 131072) != 0) {
            byteVector.putShort(this.newUTF8("Deprecated")).putInt(0);
        }
        if ((this.k & 4096) != 0 && ((this.b & 65535) < 49 || (this.k & 262144) != 0)) {
            byteVector.putShort(this.newUTF8("Synthetic")).putInt(0);
        }
        if (this.y != null) {
            byteVector.putShort(this.newUTF8("InnerClasses"));
            byteVector.putInt(this.y.b + 2).putShort(this.x);
            byteVector.putByteArray(this.y.a, 0, this.y.b);
        }
        if (this.u != null) {
            byteVector.putShort(this.newUTF8("RuntimeVisibleAnnotations"));
            this.u.a(byteVector);
        }
        if (this.v != null) {
            byteVector.putShort(this.newUTF8("RuntimeInvisibleAnnotations"));
            this.v.a(byteVector);
        }
        if (this.w != null) {
            this.w.a(this, null, 0, -1, -1, byteVector);
        }
        if (this.L) {
            ClassWriter classWriter = new ClassWriter(2);
            new ClassReader(byteVector.a).accept(classWriter, 4);
            return classWriter.toByteArray();
        }
        return byteVector.a;
    }

    Item a(Object object) {
        if (object instanceof Integer) {
            int n = (Integer)object;
            return this.a(n);
        }
        if (object instanceof Byte) {
            int n = ((Byte)object).intValue();
            return this.a(n);
        }
        if (object instanceof Character) {
            char c = ((Character)object).charValue();
            return this.a(c);
        }
        if (object instanceof Short) {
            int n = ((Short)object).intValue();
            return this.a(n);
        }
        if (object instanceof Boolean) {
            int n = (Boolean)object != false ? 1 : 0;
            return this.a(n);
        }
        if (object instanceof Float) {
            float f = ((Float)object).floatValue();
            return this.a(f);
        }
        if (object instanceof Long) {
            long l = (Long)object;
            return this.a(l);
        }
        if (object instanceof Double) {
            double d = (Double)object;
            return this.a(d);
        }
        if (object instanceof String) {
            return this.b((String)object);
        }
        if (object instanceof Type) {
            Type type = (Type)object;
            int n = type.getSort();
            if (n == 9) {
                return this.a(type.getDescriptor());
            }
            if (n == 10) {
                return this.a(type.getInternalName());
            }
            return this.c(type.getDescriptor());
        }
        if (object instanceof Handle) {
            Handle handle = (Handle)object;
            return this.a(handle.a, handle.b, handle.c, handle.d);
        }
        throw new IllegalArgumentException("value " + object);
    }

    public int newConst(Object object) {
        return this.a((Object)object).a;
    }

    public int newUTF8(String string) {
        this.g.a(1, string, null, null);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(1).putUTF8(string);
            item = new Item(this.c++, this.g);
            this.b(item);
        }
        return item.a;
    }

    Item a(String string) {
        this.h.a(7, string, null, null);
        Item item = this.a(this.h);
        if (item == null) {
            this.d.b(7, this.newUTF8(string));
            item = new Item(this.c++, this.h);
            this.b(item);
        }
        return item;
    }

    public int newClass(String string) {
        return this.a((String)string).a;
    }

    Item c(String string) {
        this.h.a(16, string, null, null);
        Item item = this.a(this.h);
        if (item == null) {
            this.d.b(16, this.newUTF8(string));
            item = new Item(this.c++, this.h);
            this.b(item);
        }
        return item;
    }

    public int newMethodType(String string) {
        return this.c((String)string).a;
    }

    Item a(int n, String string, String string2, String string3) {
        this.j.a(20 + n, string, string2, string3);
        Item item = this.a(this.j);
        if (item == null) {
            if (n <= 4) {
                this.b(15, n, this.newField(string, string2, string3));
            } else {
                this.b(15, n, this.newMethod(string, string2, string3, n == 9));
            }
            item = new Item(this.c++, this.j);
            this.b(item);
        }
        return item;
    }

    public int newHandle(int n, String string, String string2, String string3) {
        return this.a((int)n, (String)string, (String)string2, (String)string3).a;
    }

    /* varargs */ Item a(String string, String string2, Handle handle, Object ... arrobject) {
        int n;
        ByteVector byteVector = this.A;
        if (byteVector == null) {
            byteVector = this.A = new ByteVector();
        }
        int n2 = byteVector.b;
        int n3 = handle.hashCode();
        byteVector.putShort(this.newHandle(handle.a, handle.b, handle.c, handle.d));
        int n4 = arrobject.length;
        byteVector.putShort(n4);
        for (int i = 0; i < n4; ++i) {
            Object object = arrobject[i];
            n3 ^= object.hashCode();
            byteVector.putShort(this.newConst(object));
        }
        byte[] arrby = byteVector.a;
        int n5 = 2 + n4 << 1;
        Item item = this.e[(n3 &= Integer.MAX_VALUE) % this.e.length];
        block1 : while (item != null) {
            if (item.b != 33 || item.j != n3) {
                item = item.k;
                continue;
            }
            n = item.c;
            for (int j = 0; j < n5; ++j) {
                if (arrby[n2 + j] == arrby[n + j]) continue;
                item = item.k;
                continue block1;
            }
        }
        if (item != null) {
            n = item.a;
            byteVector.b = n2;
        } else {
            n = this.z++;
            item = new Item(n);
            item.a(n2, n3);
            this.b(item);
        }
        this.i.a(string, string2, n);
        item = this.a(this.i);
        if (item == null) {
            this.a(18, n, this.newNameType(string, string2));
            item = new Item(this.c++, this.i);
            this.b(item);
        }
        return item;
    }

    public /* varargs */ int newInvokeDynamic(String string, String string2, Handle handle, Object ... arrobject) {
        return this.a((String)string, (String)string2, (Handle)handle, (Object[])arrobject).a;
    }

    Item a(String string, String string2, String string3) {
        this.i.a(9, string, string2, string3);
        Item item = this.a(this.i);
        if (item == null) {
            this.a(9, this.newClass(string), this.newNameType(string2, string3));
            item = new Item(this.c++, this.i);
            this.b(item);
        }
        return item;
    }

    public int newField(String string, String string2, String string3) {
        return this.a((String)string, (String)string2, (String)string3).a;
    }

    Item a(String string, String string2, String string3, boolean bl) {
        int n = bl ? 11 : 10;
        this.i.a(n, string, string2, string3);
        Item item = this.a(this.i);
        if (item == null) {
            this.a(n, this.newClass(string), this.newNameType(string2, string3));
            item = new Item(this.c++, this.i);
            this.b(item);
        }
        return item;
    }

    public int newMethod(String string, String string2, String string3, boolean bl) {
        return this.a((String)string, (String)string2, (String)string3, (boolean)bl).a;
    }

    Item a(int n) {
        this.g.a(n);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(3).putInt(n);
            item = new Item(this.c++, this.g);
            this.b(item);
        }
        return item;
    }

    Item a(float f) {
        this.g.a(f);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(4).putInt(this.g.c);
            item = new Item(this.c++, this.g);
            this.b(item);
        }
        return item;
    }

    Item a(long l) {
        this.g.a(l);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(5).putLong(l);
            item = new Item(this.c, this.g);
            this.c += 2;
            this.b(item);
        }
        return item;
    }

    Item a(double d) {
        this.g.a(d);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(6).putLong(this.g.d);
            item = new Item(this.c, this.g);
            this.c += 2;
            this.b(item);
        }
        return item;
    }

    private Item b(String string) {
        this.h.a(8, string, null, null);
        Item item = this.a(this.h);
        if (item == null) {
            this.d.b(8, this.newUTF8(string));
            item = new Item(this.c++, this.h);
            this.b(item);
        }
        return item;
    }

    public int newNameType(String string, String string2) {
        return this.a((String)string, (String)string2).a;
    }

    Item a(String string, String string2) {
        this.h.a(12, string, string2, null);
        Item item = this.a(this.h);
        if (item == null) {
            this.a(12, this.newUTF8(string), this.newUTF8(string2));
            item = new Item(this.c++, this.h);
            this.b(item);
        }
        return item;
    }

    int c(String string) {
        this.g.a(30, string, null, null);
        Item item = this.a(this.g);
        if (item == null) {
            item = this.c(this.g);
        }
        return item.a;
    }

    int a(String string, int n) {
        this.g.b = 31;
        this.g.c = n;
        this.g.g = string;
        this.g.j = Integer.MAX_VALUE & 31 + string.hashCode() + n;
        Item item = this.a(this.g);
        if (item == null) {
            item = this.c(this.g);
        }
        return item.a;
    }

    private Item c(Item item) {
        this.G = (short)(this.G + 1);
        Item item2 = new Item(this.G, this.g);
        this.b(item2);
        if (this.H == null) {
            this.H = new Item[16];
        }
        if (this.G == this.H.length) {
            Item[] arritem = new Item[2 * this.H.length];
            System.arraycopy(this.H, 0, arritem, 0, this.H.length);
            this.H = arritem;
        }
        this.H[this.G] = item2;
        return item2;
    }

    int a(int n, int n2) {
        this.h.b = 32;
        this.h.d = (long)n | (long)n2 << 32;
        this.h.j = Integer.MAX_VALUE & 32 + n + n2;
        Item item = this.a(this.h);
        if (item == null) {
            String string = this.H[n].g;
            String string2 = this.H[n2].g;
            this.h.c = this.c(this.getCommonSuperClass(string, string2));
            item = new Item(0, this.h);
            this.b(item);
        }
        return item.c;
    }

    protected String getCommonSuperClass(String string, String string2) {
        Class class_;
        Class class_2;
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            class_2 = Class.forName(string.replace('/', '.'), false, classLoader);
            class_ = Class.forName(string2.replace('/', '.'), false, classLoader);
        }
        catch (Exception var6_6) {
            throw new RuntimeException(var6_6.toString());
        }
        if (class_2.isAssignableFrom(class_)) {
            return string;
        }
        if (class_.isAssignableFrom(class_2)) {
            return string2;
        }
        if (class_2.isInterface() || class_.isInterface()) {
            return "java/lang/Object";
        }
        while (!(class_2 = class_2.getSuperclass()).isAssignableFrom(class_)) {
        }
        return class_2.getName().replace('.', '/');
    }

    private Item a(Item item) {
        Item item2 = this.e[item.j % this.e.length];
        while (!(item2 == null || item2.b == item.b && item.a(item2))) {
            item2 = item2.k;
        }
        return item2;
    }

    private void b(Item item) {
        int n;
        if (this.c + this.G > this.f) {
            n = this.e.length;
            int n2 = n * 2 + 1;
            Item[] arritem = new Item[n2];
            for (int i = n - 1; i >= 0; --i) {
                Item item2 = this.e[i];
                while (item2 != null) {
                    int n3 = item2.j % arritem.length;
                    Item item3 = item2.k;
                    item2.k = arritem[n3];
                    arritem[n3] = item2;
                    item2 = item3;
                }
            }
            this.e = arritem;
            this.f = (int)((double)n2 * 0.75);
        }
        n = item.j % this.e.length;
        item.k = this.e[n];
        this.e[n] = item;
    }

    private void a(int n, int n2, int n3) {
        this.d.b(n, n2).putShort(n3);
    }

    private void b(int n, int n2, int n3) {
        this.d.a(n, n2).putShort(n3);
    }

    static {
        byte[] arrby = new byte[220];
        String string = "AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAAAAAAGGGGGGGHIFBFAAFFAARQJJKKJJJJJJJJJJJJJJJJJJ";
        for (int i = 0; i < arrby.length; ++i) {
            arrby[i] = (byte)(string.charAt(i) - 65);
        }
        a = arrby;
    }
}

