/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.AnnotationWriter;
import lombok.libs.org.objectweb.asm.Attribute;
import lombok.libs.org.objectweb.asm.ByteVector;
import lombok.libs.org.objectweb.asm.ClassReader;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.Edge;
import lombok.libs.org.objectweb.asm.Frame;
import lombok.libs.org.objectweb.asm.Handle;
import lombok.libs.org.objectweb.asm.Handler;
import lombok.libs.org.objectweb.asm.Item;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.Type;

class MethodWriter
extends MethodVisitor {
    final ClassWriter b;
    private int c;
    private final int d;
    private final int e;
    private final String f;
    String g;
    int h;
    int i;
    int j;
    int[] k;
    private ByteVector l;
    private AnnotationWriter m;
    private AnnotationWriter n;
    private AnnotationWriter[] o;
    private AnnotationWriter[] p;
    private int S;
    private Attribute q;
    private ByteVector r = new ByteVector();
    private int s;
    private int t;
    private int T;
    private int u;
    private ByteVector v;
    private int w;
    private int[] x;
    private int y;
    private int[] z;
    private int A;
    private Handler B;
    private Handler C;
    private int D;
    private ByteVector E;
    private int F;
    private ByteVector G;
    private int H;
    private ByteVector I;
    private Attribute J;
    private boolean K;
    private int L;
    private final int M;
    private Label N;
    private Label O;
    private Label P;
    private int Q;
    private int R;

    MethodWriter(ClassWriter classWriter, int n, String string, String string2, String string3, String[] arrstring, boolean bl, boolean bl2) {
        int n2;
        super(262144);
        if (classWriter.D == null) {
            classWriter.D = this;
        } else {
            classWriter.E.mv = this;
        }
        classWriter.E = this;
        this.b = classWriter;
        this.c = n;
        this.d = classWriter.newUTF8(string);
        this.e = classWriter.newUTF8(string2);
        this.f = string2;
        this.g = string3;
        if (arrstring != null && arrstring.length > 0) {
            this.j = arrstring.length;
            this.k = new int[this.j];
            for (n2 = 0; n2 < this.j; ++n2) {
                this.k[n2] = classWriter.newClass(arrstring[n2]);
            }
        }
        int n3 = bl2 ? 0 : (this.M = bl ? 1 : 2);
        if (bl || bl2) {
            if (bl2 && "<init>".equals(string)) {
                this.c |= 262144;
            }
            n2 = Type.getArgumentsAndReturnSizes(this.f) >> 2;
            if ((n & 8) != 0) {
                --n2;
            }
            this.t = n2;
            this.T = n2;
            this.N = new Label();
            this.N.a |= 8;
            this.visitLabel(this.N);
        }
    }

    public AnnotationVisitor visitAnnotationDefault() {
        this.l = new ByteVector();
        return new AnnotationWriter(this.b, false, this.l, null, 0);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        ByteVector byteVector = new ByteVector();
        byteVector.putShort(this.b.newUTF8(string)).putShort(0);
        AnnotationWriter annotationWriter = new AnnotationWriter(this.b, true, byteVector, byteVector, 2);
        if (bl) {
            annotationWriter.g = this.m;
            this.m = annotationWriter;
        } else {
            annotationWriter.g = this.n;
            this.n = annotationWriter;
        }
        return annotationWriter;
    }

    public AnnotationVisitor visitParameterAnnotation(int n, String string, boolean bl) {
        ByteVector byteVector = new ByteVector();
        if ("Ljava/lang/Synthetic;".equals(string)) {
            this.S = Math.max(this.S, n + 1);
            return new AnnotationWriter(this.b, false, byteVector, null, 0);
        }
        byteVector.putShort(this.b.newUTF8(string)).putShort(0);
        AnnotationWriter annotationWriter = new AnnotationWriter(this.b, true, byteVector, byteVector, 2);
        if (bl) {
            if (this.o == null) {
                this.o = new AnnotationWriter[Type.getArgumentTypes(this.f).length];
            }
            annotationWriter.g = this.o[n];
            this.o[n] = annotationWriter;
        } else {
            if (this.p == null) {
                this.p = new AnnotationWriter[Type.getArgumentTypes(this.f).length];
            }
            annotationWriter.g = this.p[n];
            this.p[n] = annotationWriter;
        }
        return annotationWriter;
    }

    public void visitAttribute(Attribute attribute) {
        if (attribute.isCodeAttribute()) {
            attribute.a = this.J;
            this.J = attribute;
        } else {
            attribute.a = this.q;
            this.q = attribute;
        }
    }

    public void visitCode() {
    }

    public void visitFrame(int n, int n2, Object[] arrobject, int n3, Object[] arrobject2) {
        if (this.M == 0) {
            return;
        }
        if (n == -1) {
            int n4;
            this.T = n2;
            this.a(this.r.b, n2, n3);
            for (n4 = 0; n4 < n2; ++n4) {
                this.z[this.y++] = arrobject[n4] instanceof String ? 24117248 | this.b.c((String)arrobject[n4]) : (arrobject[n4] instanceof Integer ? (Integer)arrobject[n4] : 25165824 | this.b.a("", ((Label)arrobject[n4]).c));
            }
            for (n4 = 0; n4 < n3; ++n4) {
                this.z[this.y++] = arrobject2[n4] instanceof String ? 24117248 | this.b.c((String)arrobject2[n4]) : (arrobject2[n4] instanceof Integer ? (Integer)arrobject2[n4] : 25165824 | this.b.a("", ((Label)arrobject2[n4]).c));
            }
            this.b();
        } else {
            int n5;
            if (this.v == null) {
                this.v = new ByteVector();
                n5 = this.r.b;
            } else {
                n5 = this.r.b - this.w - 1;
                if (n5 < 0) {
                    if (n == 3) {
                        return;
                    }
                    throw new IllegalStateException();
                }
            }
            switch (n) {
                case 0: {
                    int n6;
                    this.T = n2;
                    this.v.putByte(255).putShort(n5).putShort(n2);
                    for (n6 = 0; n6 < n2; ++n6) {
                        this.a(arrobject[n6]);
                    }
                    this.v.putShort(n3);
                    for (n6 = 0; n6 < n3; ++n6) {
                        this.a(arrobject2[n6]);
                    }
                    break;
                }
                case 1: {
                    this.T += n2;
                    this.v.putByte(251 + n2).putShort(n5);
                    for (int i = 0; i < n2; ++i) {
                        this.a(arrobject[i]);
                    }
                    break;
                }
                case 2: {
                    this.T -= n2;
                    this.v.putByte(251 - n2).putShort(n5);
                    break;
                }
                case 3: {
                    if (n5 < 64) {
                        this.v.putByte(n5);
                        break;
                    }
                    this.v.putByte(251).putShort(n5);
                    break;
                }
                case 4: {
                    if (n5 < 64) {
                        this.v.putByte(64 + n5);
                    } else {
                        this.v.putByte(247).putShort(n5);
                    }
                    this.a(arrobject2[0]);
                }
            }
            this.w = this.r.b;
            ++this.u;
        }
        this.s = Math.max(this.s, n3);
        this.t = Math.max(this.t, this.T);
    }

    public void visitInsn(int n) {
        this.r.putByte(n);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, null, null);
            } else {
                int n2 = this.Q + Frame.a[n];
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
            if (n >= 172 && n <= 177 || n == 191) {
                this.e();
            }
        }
    }

    public void visitIntInsn(int n, int n2) {
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, n2, null, null);
            } else if (n != 188) {
                int n3 = this.Q + 1;
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (n == 17) {
            this.r.b(n, n2);
        } else {
            this.r.a(n, n2);
        }
    }

    public void visitVarInsn(int n, int n2) {
        int n3;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, n2, null, null);
            } else if (n == 169) {
                this.P.a |= 256;
                this.P.f = this.Q;
                this.e();
            } else {
                n3 = this.Q + Frame.a[n];
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (this.M != 2) {
            n3 = n == 22 || n == 24 || n == 55 || n == 57 ? n2 + 2 : n2 + 1;
            if (n3 > this.t) {
                this.t = n3;
            }
        }
        if (n2 < 4 && n != 169) {
            n3 = n < 54 ? 26 + (n - 21 << 2) + n2 : 59 + (n - 54 << 2) + n2;
            this.r.putByte(n3);
        } else if (n2 >= 256) {
            this.r.putByte(196).b(n, n2);
        } else {
            this.r.a(n, n2);
        }
        if (n >= 54 && this.M == 0 && this.A > 0) {
            this.visitLabel(new Label());
        }
    }

    public void visitTypeInsn(int n, String string) {
        Item item = this.b.a(string);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, this.r.b, this.b, item);
            } else if (n == 187) {
                int n2 = this.Q + 1;
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(n, item.a);
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        Item item = this.b.a(string, string2, string3);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, this.b, item);
            } else {
                int n2;
                char c = string3.charAt(0);
                switch (n) {
                    case 178: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? 2 : 1);
                        break;
                    }
                    case 179: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? -2 : -1);
                        break;
                    }
                    case 180: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? 1 : 0);
                        break;
                    }
                    default: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? -3 : -2);
                    }
                }
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(n, item.a);
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        boolean bl = n == 185;
        Item item = this.b.a(string, string2, string3, bl);
        int n2 = item.c;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, this.b, item);
            } else {
                if (n2 == 0) {
                    item.c = n2 = Type.getArgumentsAndReturnSizes(string3);
                }
                int n3 = n == 184 ? this.Q - (n2 >> 2) + (n2 & 3) + 1 : this.Q - (n2 >> 2) + (n2 & 3);
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (bl) {
            if (n2 == 0) {
                item.c = n2 = Type.getArgumentsAndReturnSizes(string3);
            }
            this.r.b(185, item.a).a(n2 >> 2, 0);
        } else {
            this.r.b(n, item.a);
        }
    }

    public /* varargs */ void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... arrobject) {
        Item item = this.b.a(string, string2, handle, arrobject);
        int n = item.c;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(186, 0, this.b, item);
            } else {
                int n2;
                if (n == 0) {
                    item.c = n = Type.getArgumentsAndReturnSizes(string2);
                }
                if ((n2 = this.Q - (n >> 2) + (n & 3) + 1) > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(186, item.a);
        this.r.putShort(0);
    }

    public void visitJumpInsn(int n, Label label) {
        Label label2 = null;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, null, null);
                label.a().a |= 16;
                this.a(0, label);
                if (n != 167) {
                    label2 = new Label();
                }
            } else if (n == 168) {
                if ((label.a & 512) == 0) {
                    label.a |= 512;
                    ++this.L;
                }
                this.P.a |= 128;
                this.a(this.Q + 1, label);
                label2 = new Label();
            } else {
                this.Q += Frame.a[n];
                this.a(this.Q, label);
            }
        }
        if ((label.a & 2) != 0 && label.c - this.r.b < -32768) {
            if (n == 167) {
                this.r.putByte(200);
            } else if (n == 168) {
                this.r.putByte(201);
            } else {
                if (label2 != null) {
                    label2.a |= 16;
                }
                this.r.putByte(n <= 166 ? (n + 1 ^ 1) - 1 : n ^ 1);
                this.r.putShort(8);
                this.r.putByte(200);
            }
            label.a(this, this.r, this.r.b - 1, true);
        } else {
            this.r.putByte(n);
            label.a(this, this.r, this.r.b - 1, false);
        }
        if (this.P != null) {
            if (label2 != null) {
                this.visitLabel(label2);
            }
            if (n == 167) {
                this.e();
            }
        }
    }

    public void visitLabel(Label label) {
        this.K |= label.a(this, this.r.b, this.r.a);
        if ((label.a & 1) != 0) {
            return;
        }
        if (this.M == 0) {
            if (this.P != null) {
                if (label.c == this.P.c) {
                    this.P.a |= label.a & 16;
                    label.h = this.P.h;
                    return;
                }
                this.a(0, label);
            }
            this.P = label;
            if (label.h == null) {
                label.h = new Frame();
                label.h.b = label;
            }
            if (this.O != null) {
                if (label.c == this.O.c) {
                    this.O.a |= label.a & 16;
                    label.h = this.O.h;
                    this.P = this.O;
                    return;
                }
                this.O.i = label;
            }
            this.O = label;
        } else if (this.M == 1) {
            if (this.P != null) {
                this.P.g = this.R;
                this.a(this.Q, label);
            }
            this.P = label;
            this.Q = 0;
            this.R = 0;
            if (this.O != null) {
                this.O.i = label;
            }
            this.O = label;
        }
    }

    public void visitLdcInsn(Object object) {
        int n;
        Item item = this.b.a(object);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(18, 0, this.b, item);
            } else {
                n = item.b == 5 || item.b == 6 ? this.Q + 2 : this.Q + 1;
                if (n > this.R) {
                    this.R = n;
                }
                this.Q = n;
            }
        }
        n = item.a;
        if (item.b == 5 || item.b == 6) {
            this.r.b(20, n);
        } else if (n >= 256) {
            this.r.b(19, n);
        } else {
            this.r.a(18, n);
        }
    }

    public void visitIincInsn(int n, int n2) {
        int n3;
        if (this.P != null && this.M == 0) {
            this.P.h.a(132, n, null, null);
        }
        if (this.M != 2 && (n3 = n + 1) > this.t) {
            this.t = n3;
        }
        if (n > 255 || n2 > 127 || n2 < -128) {
            this.r.putByte(196).b(132, n).putShort(n2);
        } else {
            this.r.putByte(132).a(n, n2);
        }
    }

    public /* varargs */ void visitTableSwitchInsn(int n, int n2, Label label, Label ... arrlabel) {
        int n3 = this.r.b;
        this.r.putByte(170);
        this.r.putByteArray(null, 0, (4 - this.r.b % 4) % 4);
        label.a(this, this.r, n3, true);
        this.r.putInt(n).putInt(n2);
        for (int i = 0; i < arrlabel.length; ++i) {
            arrlabel[i].a(this, this.r, n3, true);
        }
        this.a(label, arrlabel);
    }

    public void visitLookupSwitchInsn(Label label, int[] arrn, Label[] arrlabel) {
        int n = this.r.b;
        this.r.putByte(171);
        this.r.putByteArray(null, 0, (4 - this.r.b % 4) % 4);
        label.a(this, this.r, n, true);
        this.r.putInt(arrlabel.length);
        for (int i = 0; i < arrlabel.length; ++i) {
            this.r.putInt(arrn[i]);
            arrlabel[i].a(this, this.r, n, true);
        }
        this.a(label, arrlabel);
    }

    private void a(Label label, Label[] arrlabel) {
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(171, 0, null, null);
                this.a(0, label);
                label.a().a |= 16;
                for (int i = 0; i < arrlabel.length; ++i) {
                    this.a(0, arrlabel[i]);
                    arrlabel[i].a().a |= 16;
                }
            } else {
                --this.Q;
                this.a(this.Q, label);
                for (int i = 0; i < arrlabel.length; ++i) {
                    this.a(this.Q, arrlabel[i]);
                }
            }
            this.e();
        }
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        Item item = this.b.a(string);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(197, n, this.b, item);
            } else {
                this.Q += 1 - n;
            }
        }
        this.r.b(197, item.a).putByte(n);
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        ++this.A;
        Handler handler = new Handler();
        handler.a = label;
        handler.b = label2;
        handler.c = label3;
        handler.d = string;
        int n = handler.e = string != null ? this.b.newClass(string) : 0;
        if (this.C == null) {
            this.B = handler;
        } else {
            this.C.f = handler;
        }
        this.C = handler;
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n) {
        int n2;
        char c;
        if (string3 != null) {
            if (this.G == null) {
                this.G = new ByteVector();
            }
            ++this.F;
            this.G.putShort(label.c).putShort(label2.c - label.c).putShort(this.b.newUTF8(string)).putShort(this.b.newUTF8(string3)).putShort(n);
        }
        if (this.E == null) {
            this.E = new ByteVector();
        }
        ++this.D;
        this.E.putShort(label.c).putShort(label2.c - label.c).putShort(this.b.newUTF8(string)).putShort(this.b.newUTF8(string2)).putShort(n);
        if (this.M != 2 && (n2 = n + ((c = string2.charAt(0)) == 'J' || c == 'D' ? 2 : 1)) > this.t) {
            this.t = n2;
        }
    }

    public void visitLineNumber(int n, Label label) {
        if (this.I == null) {
            this.I = new ByteVector();
        }
        ++this.H;
        this.I.putShort(label.c);
        this.I.putShort(n);
    }

    public void visitMaxs(int n, int n2) {
        if (this.M == 0) {
            Object object;
            Object object3;
            Type[] arrtype;
            Edge edge /* !! */ ;
            Object object2;
            int n3;
            Handler handler = this.B;
            while (handler != null) {
                object3 = handler.a.a();
                arrtype = handler.c.a();
                Label label = handler.b.a();
                object2 = handler.d == null ? "java/lang/Throwable" : handler.d;
                int n4 = 24117248 | this.b.c((String)object2);
                arrtype.a |= 16;
                while (object3 != label) {
                    edge /* !! */  = new Edge();
                    edge /* !! */ .a = n4;
                    edge /* !! */ .b = arrtype;
                    edge /* !! */ .c = object3.j;
                    object3.j = edge /* !! */ ;
                    object3 = object3.i;
                }
                handler = handler.f;
            }
            object3 = this.N.h;
            arrtype = Type.getArgumentTypes(this.f);
            object3.a(this.b, this.c, arrtype, this.t);
            this.b((Frame)object3);
            Object object4 = 0;
            object2 = this.N;
            while (object2 != null) {
                Object object5 = object2;
                object2 = object2.k;
                object5.k = null;
                object3 = object5.h;
                if ((object5.a & 16) != 0) {
                    object5.a |= 32;
                }
                object5.a |= 64;
                edge /* !! */  = (Edge)(object3.d.length + object5.g);
                if (edge /* !! */  > object4) {
                    object4 = edge /* !! */ ;
                }
                Edge edge2 = object5.j;
                while (edge2 != null) {
                    object = edge2.b.a();
                    n3 = (int)object3.a(this.b, object.h, edge2.a) ? 1 : 0;
                    if (n3 != 0 && object.k == null) {
                        object.k = object2;
                        object2 = object;
                    }
                    edge2 = edge2.c;
                }
            }
            Label label = this.N;
            while (label != null) {
                Label label2;
                int n4;
                object3 = label.h;
                if ((label.a & 32) != 0) {
                    this.b((Frame)object3);
                }
                if ((label.a & 64) == 0 && (object = ((label2 = label.i) == null ? this.r.b : label2.c) - 1) >= (n4 = label.c)) {
                    object4 = Math.max((int)object4, 1);
                    for (n3 = n4; n3 < object; ++n3) {
                        this.r.a[n3] = 0;
                    }
                    this.r.a[object] = -65;
                    this.a(n4, 0, 1);
                    this.z[this.y++] = 24117248 | this.b.c("java/lang/Throwable");
                    this.b();
                    this.B = Handler.a(this.B, label, label2);
                }
                label = label.i;
            }
            handler = this.B;
            this.A = 0;
            while (handler != null) {
                ++this.A;
                handler = handler.f;
            }
            this.s = object4;
        } else if (this.M == 1) {
            Object object;
            Label label;
            Label label3;
            Handler handler = this.B;
            while (handler != null) {
                Label label4 = handler.a;
                label3 = handler.c;
                label = handler.b;
                while (label4 != label) {
                    object = new Edge();
                    object.a = Integer.MAX_VALUE;
                    object.b = label3;
                    if ((label4.a & 128) == 0) {
                        object.c = label4.j;
                        label4.j = object;
                    } else {
                        object.c = label4.j.c.c;
                        label4.j.c.c = object;
                    }
                    label4 = label4.i;
                }
                handler = handler.f;
            }
            if (this.L > 0) {
                int n6 = 0;
                this.N.b(null, 1, this.L);
                label3 = this.N;
                while (label3 != null) {
                    if ((label3.a & 128) != 0) {
                        label = label3.j.c.b;
                        if ((label.a & 1024) == 0) {
                            label.b(null, (long)n6 / 32 << 32 | 1 << ++n6 % 32, this.L);
                        }
                    }
                    label3 = label3.i;
                }
                label3 = this.N;
                while (label3 != null) {
                    if ((label3.a & 128) != 0) {
                        label = this.N;
                        while (label != null) {
                            label.a &= -2049;
                            label = label.i;
                        }
                        object = label3.j.c.b;
                        object.b(label3, 0, this.L);
                    }
                    label3 = label3.i;
                }
            }
            int n7 = 0;
            label3 = this.N;
            while (label3 != null) {
                void edge;
                label = label3;
                label3 = label3.k;
                int n8 = label.f;
                int n9 = n8 + label.g;
                if (n9 > n7) {
                    n7 = n9;
                }
                Edge edge3 = label.j;
                if ((label.a & 128) != 0) {
                    Edge edge4 = edge3.c;
                }
                while (edge != null) {
                    label = edge.b;
                    if ((label.a & 8) == 0) {
                        label.f = edge.a == Integer.MAX_VALUE ? 1 : n8 + edge.a;
                        label.a |= 8;
                        label.k = label3;
                        label3 = label;
                    }
                    Edge edge5 = edge.c;
                }
            }
            this.s = Math.max(n, n7);
        } else {
            this.s = n;
            this.t = n2;
        }
    }

    public void visitEnd() {
    }

    private void a(int n, Label label) {
        Edge edge = new Edge();
        edge.a = n;
        edge.b = label;
        edge.c = this.P.j;
        this.P.j = edge;
    }

    private void e() {
        if (this.M == 0) {
            Label label = new Label();
            label.h = new Frame();
            label.h.b = label;
            label.a(this, this.r.b, this.r.a);
            this.O.i = label;
            this.O = label;
        } else {
            this.P.g = this.R;
        }
        this.P = null;
    }

    private void b(Frame frame) {
        int n;
        int n2;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int[] arrn = frame.c;
        int[] arrn2 = frame.d;
        for (n = 0; n < arrn.length; ++n) {
            n2 = arrn[n];
            if (n2 == 16777216) {
                ++n3;
            } else {
                n4 += n3 + 1;
                n3 = 0;
            }
            if (n2 != 16777220 && n2 != 16777219) continue;
            ++n;
        }
        for (n = 0; n < arrn2.length; ++n) {
            n2 = arrn2[n];
            ++n5;
            if (n2 != 16777220 && n2 != 16777219) continue;
            ++n;
        }
        this.a(frame.b.c, n4, n5);
        n = 0;
        while (n4 > 0) {
            n2 = arrn[n];
            this.z[this.y++] = n2;
            if (n2 == 16777220 || n2 == 16777219) {
                ++n;
            }
            ++n;
            --n4;
        }
        for (n = 0; n < arrn2.length; ++n) {
            n2 = arrn2[n];
            this.z[this.y++] = n2;
            if (n2 != 16777220 && n2 != 16777219) continue;
            ++n;
        }
        this.b();
    }

    private void a(int n, int n2, int n3) {
        int n4 = 3 + n2 + n3;
        if (this.z == null || this.z.length < n4) {
            this.z = new int[n4];
        }
        this.z[0] = n;
        this.z[1] = n2;
        this.z[2] = n3;
        this.y = 3;
    }

    private void b() {
        if (this.x != null) {
            if (this.v == null) {
                this.v = new ByteVector();
            }
            this.c();
            ++this.u;
        }
        this.x = this.z;
        this.z = null;
    }

    private void c() {
        int n = this.z[1];
        int n2 = this.z[2];
        if ((this.b.b & 65535) < 50) {
            this.v.putShort(this.z[0]).putShort(n);
            this.a(3, 3 + n);
            this.v.putShort(n2);
            this.a(3 + n, 3 + n + n2);
            return;
        }
        int n3 = this.x[1];
        int n4 = 255;
        int n5 = 0;
        int n6 = this.u == 0 ? this.z[0] : this.z[0] - this.x[0] - 1;
        if (n2 == 0) {
            n5 = n - n3;
            switch (n5) {
                case -3: 
                case -2: 
                case -1: {
                    n4 = 248;
                    n3 = n;
                    break;
                }
                case 0: {
                    n4 = n6 < 64 ? 0 : 251;
                    break;
                }
                case 1: 
                case 2: 
                case 3: {
                    n4 = 252;
                }
            }
        } else if (n == n3 && n2 == 1) {
            int n7 = n4 = n6 < 63 ? 64 : 247;
        }
        if (n4 != 255) {
            int n8 = 3;
            for (int i = 0; i < n3; ++i) {
                if (this.z[n8] != this.x[n8]) {
                    n4 = 255;
                    break;
                }
                ++n8;
            }
        }
        switch (n4) {
            case 0: {
                this.v.putByte(n6);
                break;
            }
            case 64: {
                this.v.putByte(64 + n6);
                this.a(3 + n, 4 + n);
                break;
            }
            case 247: {
                this.v.putByte(247).putShort(n6);
                this.a(3 + n, 4 + n);
                break;
            }
            case 251: {
                this.v.putByte(251).putShort(n6);
                break;
            }
            case 248: {
                this.v.putByte(251 + n5).putShort(n6);
                break;
            }
            case 252: {
                this.v.putByte(251 + n5).putShort(n6);
                this.a(3 + n3, 3 + n);
                break;
            }
            default: {
                this.v.putByte(255).putShort(n6).putShort(n);
                this.a(3, 3 + n);
                this.v.putShort(n2);
                this.a(3 + n, 3 + n + n2);
            }
        }
    }

    private void a(int n, int n2) {
        block13 : for (int i = n; i < n2; ++i) {
            int n3 = this.z[i];
            int n4 = n3 & -268435456;
            if (n4 == 0) {
                int n5 = n3 & 1048575;
                switch (n3 & 267386880) {
                    case 24117248: {
                        this.v.putByte(7).putShort(this.b.newClass(this.b.H[n5].g));
                        continue block13;
                    }
                    case 25165824: {
                        this.v.putByte(8).putShort(this.b.H[n5].c);
                        continue block13;
                    }
                }
                this.v.putByte(n5);
                continue;
            }
            StringBuffer stringBuffer = new StringBuffer();
            n4 >>= 28;
            while (n4-- > 0) {
                stringBuffer.append('[');
            }
            if ((n3 & 267386880) == 24117248) {
                stringBuffer.append('L');
                stringBuffer.append(this.b.H[n3 & 1048575].g);
                stringBuffer.append(';');
            } else {
                switch (n3 & 15) {
                    case 1: {
                        stringBuffer.append('I');
                        break;
                    }
                    case 2: {
                        stringBuffer.append('F');
                        break;
                    }
                    case 3: {
                        stringBuffer.append('D');
                        break;
                    }
                    case 9: {
                        stringBuffer.append('Z');
                        break;
                    }
                    case 10: {
                        stringBuffer.append('B');
                        break;
                    }
                    case 11: {
                        stringBuffer.append('C');
                        break;
                    }
                    case 12: {
                        stringBuffer.append('S');
                        break;
                    }
                    default: {
                        stringBuffer.append('J');
                    }
                }
            }
            this.v.putByte(7).putShort(this.b.newClass(stringBuffer.toString()));
        }
    }

    private void a(Object object) {
        if (object instanceof String) {
            this.v.putByte(7).putShort(this.b.newClass((String)object));
        } else if (object instanceof Integer) {
            this.v.putByte((Integer)object);
        } else {
            this.v.putByte(8).putShort(((Label)object).c);
        }
    }

    final int a() {
        int n;
        if (this.h != 0) {
            return 6 + this.i;
        }
        if (this.K) {
            this.d();
        }
        int n2 = 8;
        if (this.r.b > 0) {
            if (this.r.b > 65536) {
                throw new RuntimeException("Method code too large!");
            }
            this.b.newUTF8("Code");
            n2 += 18 + this.r.b + 8 * this.A;
            if (this.E != null) {
                this.b.newUTF8("LocalVariableTable");
                n2 += 8 + this.E.b;
            }
            if (this.G != null) {
                this.b.newUTF8("LocalVariableTypeTable");
                n2 += 8 + this.G.b;
            }
            if (this.I != null) {
                this.b.newUTF8("LineNumberTable");
                n2 += 8 + this.I.b;
            }
            if (this.v != null) {
                n = (this.b.b & 65535) >= 50 ? 1 : 0;
                this.b.newUTF8(n != 0 ? "StackMapTable" : "StackMap");
                n2 += 8 + this.v.b;
            }
            if (this.J != null) {
                n2 += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
            }
        }
        if (this.j > 0) {
            this.b.newUTF8("Exceptions");
            n2 += 8 + 2 * this.j;
        }
        if ((this.c & 4096) != 0 && ((this.b.b & 65535) < 49 || (this.c & 262144) != 0)) {
            this.b.newUTF8("Synthetic");
            n2 += 6;
        }
        if ((this.c & 131072) != 0) {
            this.b.newUTF8("Deprecated");
            n2 += 6;
        }
        if (this.g != null) {
            this.b.newUTF8("Signature");
            this.b.newUTF8(this.g);
            n2 += 8;
        }
        if (this.l != null) {
            this.b.newUTF8("AnnotationDefault");
            n2 += 6 + this.l.b;
        }
        if (this.m != null) {
            this.b.newUTF8("RuntimeVisibleAnnotations");
            n2 += 8 + this.m.a();
        }
        if (this.n != null) {
            this.b.newUTF8("RuntimeInvisibleAnnotations");
            n2 += 8 + this.n.a();
        }
        if (this.o != null) {
            this.b.newUTF8("RuntimeVisibleParameterAnnotations");
            n2 += 7 + 2 * (this.o.length - this.S);
            for (n = this.o.length - 1; n >= this.S; --n) {
                n2 += this.o[n] == null ? 0 : this.o[n].a();
            }
        }
        if (this.p != null) {
            this.b.newUTF8("RuntimeInvisibleParameterAnnotations");
            n2 += 7 + 2 * (this.p.length - this.S);
            for (n = this.p.length - 1; n >= this.S; --n) {
                n2 += this.p[n] == null ? 0 : this.p[n].a();
            }
        }
        if (this.q != null) {
            n2 += this.q.a(this.b, null, 0, -1, -1);
        }
        return n2;
    }

    final void a(ByteVector byteVector) {
        int n;
        int n2 = 393216 | (this.c & 262144) / 64;
        byteVector.putShort(this.c & ~ n2).putShort(this.d).putShort(this.e);
        if (this.h != 0) {
            byteVector.putByteArray(this.b.M.b, this.h, this.i);
            return;
        }
        int n3 = 0;
        if (this.r.b > 0) {
            ++n3;
        }
        if (this.j > 0) {
            ++n3;
        }
        if ((this.c & 4096) != 0 && ((this.b.b & 65535) < 49 || (this.c & 262144) != 0)) {
            ++n3;
        }
        if ((this.c & 131072) != 0) {
            ++n3;
        }
        if (this.g != null) {
            ++n3;
        }
        if (this.l != null) {
            ++n3;
        }
        if (this.m != null) {
            ++n3;
        }
        if (this.n != null) {
            ++n3;
        }
        if (this.o != null) {
            ++n3;
        }
        if (this.p != null) {
            ++n3;
        }
        if (this.q != null) {
            n3 += this.q.a();
        }
        byteVector.putShort(n3);
        if (this.r.b > 0) {
            n = 12 + this.r.b + 8 * this.A;
            if (this.E != null) {
                n += 8 + this.E.b;
            }
            if (this.G != null) {
                n += 8 + this.G.b;
            }
            if (this.I != null) {
                n += 8 + this.I.b;
            }
            if (this.v != null) {
                n += 8 + this.v.b;
            }
            if (this.J != null) {
                n += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
            }
            byteVector.putShort(this.b.newUTF8("Code")).putInt(n);
            byteVector.putShort(this.s).putShort(this.t);
            byteVector.putInt(this.r.b).putByteArray(this.r.a, 0, this.r.b);
            byteVector.putShort(this.A);
            if (this.A > 0) {
                Handler handler = this.B;
                while (handler != null) {
                    byteVector.putShort(handler.a.c).putShort(handler.b.c).putShort(handler.c.c).putShort(handler.e);
                    handler = handler.f;
                }
            }
            n3 = 0;
            if (this.E != null) {
                ++n3;
            }
            if (this.G != null) {
                ++n3;
            }
            if (this.I != null) {
                ++n3;
            }
            if (this.v != null) {
                ++n3;
            }
            if (this.J != null) {
                n3 += this.J.a();
            }
            byteVector.putShort(n3);
            if (this.E != null) {
                byteVector.putShort(this.b.newUTF8("LocalVariableTable"));
                byteVector.putInt(this.E.b + 2).putShort(this.D);
                byteVector.putByteArray(this.E.a, 0, this.E.b);
            }
            if (this.G != null) {
                byteVector.putShort(this.b.newUTF8("LocalVariableTypeTable"));
                byteVector.putInt(this.G.b + 2).putShort(this.F);
                byteVector.putByteArray(this.G.a, 0, this.G.b);
            }
            if (this.I != null) {
                byteVector.putShort(this.b.newUTF8("LineNumberTable"));
                byteVector.putInt(this.I.b + 2).putShort(this.H);
                byteVector.putByteArray(this.I.a, 0, this.I.b);
            }
            if (this.v != null) {
                boolean bl = (this.b.b & 65535) >= 50;
                byteVector.putShort(this.b.newUTF8(bl ? "StackMapTable" : "StackMap"));
                byteVector.putInt(this.v.b + 2).putShort(this.u);
                byteVector.putByteArray(this.v.a, 0, this.v.b);
            }
            if (this.J != null) {
                this.J.a(this.b, this.r.a, this.r.b, this.t, this.s, byteVector);
            }
        }
        if (this.j > 0) {
            byteVector.putShort(this.b.newUTF8("Exceptions")).putInt(2 * this.j + 2);
            byteVector.putShort(this.j);
            for (n = 0; n < this.j; ++n) {
                byteVector.putShort(this.k[n]);
            }
        }
        if ((this.c & 4096) != 0 && ((this.b.b & 65535) < 49 || (this.c & 262144) != 0)) {
            byteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
        }
        if ((this.c & 131072) != 0) {
            byteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
        }
        if (this.g != null) {
            byteVector.putShort(this.b.newUTF8("Signature")).putInt(2).putShort(this.b.newUTF8(this.g));
        }
        if (this.l != null) {
            byteVector.putShort(this.b.newUTF8("AnnotationDefault"));
            byteVector.putInt(this.l.b);
            byteVector.putByteArray(this.l.a, 0, this.l.b);
        }
        if (this.m != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
            this.m.a(byteVector);
        }
        if (this.n != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
            this.n.a(byteVector);
        }
        if (this.o != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeVisibleParameterAnnotations"));
            AnnotationWriter.a(this.o, this.S, byteVector);
        }
        if (this.p != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeInvisibleParameterAnnotations"));
            AnnotationWriter.a(this.p, this.S, byteVector);
        }
        if (this.q != null) {
            this.q.a(this.b, null, 0, -1, -1, byteVector);
        }
    }

    private void d() {
        Object object;
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        Type[] arrtype;
        byte[] arrby = this.r.a;
        Object object2 = new int[]{};
        Object object3 = new int[]{};
        boolean[] arrbl = new boolean[this.r.b];
        int n6 = 3;
        do {
            if (n6 == 3) {
                n6 = 2;
            }
            n3 = 0;
            while (n3 < arrby.length) {
                int n7 = arrby[n3] & 255;
                n5 = 0;
                switch (ClassWriter.a[n7]) {
                    case 0: 
                    case 4: {
                        ++n3;
                        break;
                    }
                    case 9: {
                        if (n7 > 201) {
                            n7 = n7 < 218 ? n7 - 49 : n7 - 20;
                            n = n3 + MethodWriter.c(arrby, n3 + 1);
                        } else {
                            n = n3 + MethodWriter.b(arrby, n3 + 1);
                        }
                        n2 = MethodWriter.a((int[])object2, (int[])object3, n3, n);
                        if (!(n2 >= -32768 && n2 <= 32767 || arrbl[n3])) {
                            n5 = n7 == 167 || n7 == 168 ? 2 : 5;
                            arrbl[n3] = true;
                        }
                        n3 += 3;
                        break;
                    }
                    case 10: {
                        n3 += 5;
                        break;
                    }
                    case 14: {
                        if (n6 == 1) {
                            n2 = MethodWriter.a((int[])object2, (int[])object3, 0, n3);
                            n5 = - (n2 & 3);
                        } else if (!arrbl[n3]) {
                            n5 = n3 & 3;
                            arrbl[n3] = true;
                        }
                        n3 = n3 + 4 - (n3 & 3);
                        n3 += 4 * (MethodWriter.a(arrby, n3 + 8) - MethodWriter.a(arrby, n3 + 4) + 1) + 12;
                        break;
                    }
                    case 15: {
                        if (n6 == 1) {
                            n2 = MethodWriter.a((int[])object2, (int[])object3, 0, n3);
                            n5 = - (n2 & 3);
                        } else if (!arrbl[n3]) {
                            n5 = n3 & 3;
                            arrbl[n3] = true;
                        }
                        n3 = n3 + 4 - (n3 & 3);
                        n3 += 8 * MethodWriter.a(arrby, n3 + 4) + 8;
                        break;
                    }
                    case 17: {
                        n7 = arrby[n3 + 1] & 255;
                        if (n7 == 132) {
                            n3 += 6;
                            break;
                        }
                        n3 += 4;
                        break;
                    }
                    case 1: 
                    case 3: 
                    case 11: {
                        n3 += 2;
                        break;
                    }
                    case 2: 
                    case 5: 
                    case 6: 
                    case 12: 
                    case 13: {
                        n3 += 3;
                        break;
                    }
                    case 7: 
                    case 8: {
                        n3 += 5;
                        break;
                    }
                    default: {
                        n3 += 4;
                    }
                }
                if (n5 == 0) continue;
                arrtype = new int[object2.length + 1];
                object = new int[object3.length + 1];
                System.arraycopy(object2, 0, arrtype, 0, object2.length);
                System.arraycopy(object3, 0, object, 0, object3.length);
                arrtype[object2.length] = n3;
                object[object3.length] = n5;
                object2 = arrtype;
                object3 = object;
                if (n5 <= 0) continue;
                n6 = 3;
            }
            if (n6 >= 3) continue;
            --n6;
        } while (n6 != 0);
        ByteVector byteVector = new ByteVector(this.r.b);
        n3 = 0;
        block24 : while (n3 < this.r.b) {
            n5 = arrby[n3] & 255;
            switch (ClassWriter.a[n5]) {
                int n8;
                int n9;
                case 0: 
                case 4: {
                    byteVector.putByte(n5);
                    ++n3;
                    continue block24;
                }
                case 9: {
                    if (n5 > 201) {
                        n5 = n5 < 218 ? n5 - 49 : n5 - 20;
                        n = n3 + MethodWriter.c(arrby, n3 + 1);
                    } else {
                        n = n3 + MethodWriter.b(arrby, n3 + 1);
                    }
                    n2 = MethodWriter.a((int[])object2, (int[])object3, n3, n);
                    if (arrbl[n3]) {
                        if (n5 == 167) {
                            byteVector.putByte(200);
                        } else if (n5 == 168) {
                            byteVector.putByte(201);
                        } else {
                            byteVector.putByte(n5 <= 166 ? (n5 + 1 ^ 1) - 1 : n5 ^ 1);
                            byteVector.putShort(8);
                            byteVector.putByte(200);
                            n2 -= 3;
                        }
                        byteVector.putInt(n2);
                    } else {
                        byteVector.putByte(n5);
                        byteVector.putShort(n2);
                    }
                    n3 += 3;
                    continue block24;
                }
                case 10: {
                    n = n3 + MethodWriter.a(arrby, n3 + 1);
                    n2 = MethodWriter.a((int[])object2, (int[])object3, n3, n);
                    byteVector.putByte(n5);
                    byteVector.putInt(n2);
                    n3 += 5;
                    continue block24;
                }
                case 14: {
                    n9 = n3;
                    n3 = n3 + 4 - (n9 & 3);
                    byteVector.putByte(170);
                    byteVector.putByteArray(null, 0, (4 - byteVector.b % 4) % 4);
                    n = n9 + MethodWriter.a(arrby, n3);
                    n2 = MethodWriter.a((int[])object2, (int[])object3, n9, n);
                    byteVector.putInt(n2);
                    n8 = MethodWriter.a(arrby, n3 += 4);
                    byteVector.putInt(n8);
                    byteVector.putInt(MethodWriter.a(arrby, (n3 += 4) - 4));
                    for (n8 = MethodWriter.a((byte[])arrby, (int)(n3 += 4)) - n8 + 1; n8 > 0; --n8) {
                        n = n9 + MethodWriter.a(arrby, n3);
                        n3 += 4;
                        n2 = MethodWriter.a((int[])object2, (int[])object3, n9, n);
                        byteVector.putInt(n2);
                    }
                    continue block24;
                }
                case 15: {
                    n9 = n3;
                    n3 = n3 + 4 - (n9 & 3);
                    byteVector.putByte(171);
                    byteVector.putByteArray(null, 0, (4 - byteVector.b % 4) % 4);
                    n = n9 + MethodWriter.a(arrby, n3);
                    n2 = MethodWriter.a((int[])object2, (int[])object3, n9, n);
                    byteVector.putInt(n2);
                    n3 += 4;
                    byteVector.putInt(n8);
                    for (n8 = MethodWriter.a((byte[])arrby, (int)(n3 += 4)); n8 > 0; --n8) {
                        byteVector.putInt(MethodWriter.a(arrby, n3));
                        n = n9 + MethodWriter.a(arrby, n3 += 4);
                        n3 += 4;
                        n2 = MethodWriter.a((int[])object2, (int[])object3, n9, n);
                        byteVector.putInt(n2);
                    }
                    continue block24;
                }
                case 17: {
                    n5 = arrby[n3 + 1] & 255;
                    if (n5 == 132) {
                        byteVector.putByteArray(arrby, n3, 6);
                        n3 += 6;
                        continue block24;
                    }
                    byteVector.putByteArray(arrby, n3, 4);
                    n3 += 4;
                    continue block24;
                }
                case 1: 
                case 3: 
                case 11: {
                    byteVector.putByteArray(arrby, n3, 2);
                    n3 += 2;
                    continue block24;
                }
                case 2: 
                case 5: 
                case 6: 
                case 12: 
                case 13: {
                    byteVector.putByteArray(arrby, n3, 3);
                    n3 += 3;
                    continue block24;
                }
                case 7: 
                case 8: {
                    byteVector.putByteArray(arrby, n3, 5);
                    n3 += 5;
                    continue block24;
                }
            }
            byteVector.putByteArray(arrby, n3, 4);
            n3 += 4;
        }
        if (this.u > 0) {
            if (this.M == 0) {
                this.u = 0;
                this.v = null;
                this.x = null;
                this.z = null;
                Frame frame = new Frame();
                frame.b = this.N;
                arrtype = Type.getArgumentTypes(this.f);
                frame.a(this.b, this.c, arrtype, this.t);
                this.b(frame);
                object = this.N;
                while (object != null) {
                    n3 = object.c - 3;
                    if ((object.a & 32) != 0 || n3 >= 0 && arrbl[n3]) {
                        MethodWriter.a((int[])object2, (int[])object3, (Label)object);
                        this.b(object.h);
                    }
                    object = object.i;
                }
            } else {
                this.b.L = true;
            }
        }
        Handler handler = this.B;
        while (handler != null) {
            MethodWriter.a((int[])object2, (int[])object3, handler.a);
            MethodWriter.a((int[])object2, (int[])object3, handler.b);
            MethodWriter.a((int[])object2, (int[])object3, handler.c);
            handler = handler.f;
        }
        for (n4 = 0; n4 < 2; ++n4) {
            arrtype = (Type[])(n4 == 0 ? this.E : this.G);
            if (arrtype == null) continue;
            arrby = arrtype.a;
            for (n3 = 0; n3 < arrtype.b; n3 += 10) {
                n = MethodWriter.c(arrby, n3);
                n2 = MethodWriter.a((int[])object2, (int[])object3, 0, n);
                MethodWriter.a(arrby, n3, n2);
                n2 = MethodWriter.a((int[])object2, (int[])object3, 0, n += MethodWriter.c(arrby, n3 + 2)) - n2;
                MethodWriter.a(arrby, n3 + 2, n2);
            }
        }
        if (this.I != null) {
            arrby = this.I.a;
            for (n3 = 0; n3 < this.I.b; n3 += 4) {
                MethodWriter.a(arrby, n3, MethodWriter.a((int[])object2, (int[])object3, 0, MethodWriter.c(arrby, n3)));
            }
        }
        arrtype = this.J;
        while (arrtype != null) {
            object = arrtype.getLabels();
            if (object != null) {
                for (n4 = object.length - 1; n4 >= 0; --n4) {
                    MethodWriter.a((int[])object2, (int[])object3, (Label)object[n4]);
                }
            }
            arrtype = arrtype.a;
        }
        this.r = byteVector;
    }

    static int c(byte[] arrby, int n) {
        return (arrby[n] & 255) << 8 | arrby[n + 1] & 255;
    }

    static short b(byte[] arrby, int n) {
        return (short)((arrby[n] & 255) << 8 | arrby[n + 1] & 255);
    }

    static int a(byte[] arrby, int n) {
        return (arrby[n] & 255) << 24 | (arrby[n + 1] & 255) << 16 | (arrby[n + 2] & 255) << 8 | arrby[n + 3] & 255;
    }

    static void a(byte[] arrby, int n, int n2) {
        arrby[n] = (byte)(n2 >>> 8);
        arrby[n + 1] = (byte)n2;
    }

    static int a(int[] arrn, int[] arrn2, int n, int n2) {
        int n3 = n2 - n;
        for (int i = 0; i < arrn.length; ++i) {
            if (n < arrn[i] && arrn[i] <= n2) {
                n3 += arrn2[i];
                continue;
            }
            if (n2 >= arrn[i] || arrn[i] > n) continue;
            n3 -= arrn2[i];
        }
        return n3;
    }

    static void a(int[] arrn, int[] arrn2, Label label) {
        if ((label.a & 4) == 0) {
            label.c = MethodWriter.a(arrn, arrn2, 0, label.c);
            label.a |= 4;
        }
    }
}

