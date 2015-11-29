/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

import java.io.IOException;
import java.io.InputStream;
import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.Attribute;
import lombok.libs.org.objectweb.asm.ByteVector;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.libs.org.objectweb.asm.Handle;
import lombok.libs.org.objectweb.asm.Item;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.MethodWriter;
import lombok.libs.org.objectweb.asm.Opcodes;
import lombok.libs.org.objectweb.asm.Type;

public class ClassReader {
    public static final int SKIP_CODE = 1;
    public static final int SKIP_DEBUG = 2;
    public static final int SKIP_FRAMES = 4;
    public static final int EXPAND_FRAMES = 8;
    public final byte[] b;
    private final int[] a;
    private final String[] c;
    private final int d;
    public final int header;

    public ClassReader(byte[] arrby) {
        this(arrby, 0, arrby.length);
    }

    public ClassReader(byte[] arrby, int n, int n2) {
        this.b = arrby;
        if (this.readShort(6) > 51) {
            throw new IllegalArgumentException();
        }
        this.a = new int[this.readUnsignedShort(n + 8)];
        int n3 = this.a.length;
        this.c = new String[n3];
        int n4 = 0;
        int n5 = n + 10;
        for (int i = 1; i < n3; ++i) {
            int n6;
            this.a[i] = n5 + 1;
            switch (arrby[n5]) {
                case 3: 
                case 4: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 18: {
                    n6 = 5;
                    break;
                }
                case 5: 
                case 6: {
                    n6 = 9;
                    ++i;
                    break;
                }
                case 1: {
                    n6 = 3 + this.readUnsignedShort(n5 + 1);
                    if (n6 <= n4) break;
                    n4 = n6;
                    break;
                }
                case 15: {
                    n6 = 4;
                    break;
                }
                default: {
                    n6 = 3;
                }
            }
            n5 += n6;
        }
        this.d = n4;
        this.header = n5;
    }

    public int getAccess() {
        return this.readUnsignedShort(this.header);
    }

    public String getClassName() {
        return this.readClass(this.header + 2, new char[this.d]);
    }

    public String getSuperName() {
        int n = this.a[this.readUnsignedShort(this.header + 4)];
        return n == 0 ? null : this.readUTF8(n, new char[this.d]);
    }

    public String[] getInterfaces() {
        int n = this.header + 6;
        int n2 = this.readUnsignedShort(n);
        String[] arrstring = new String[n2];
        if (n2 > 0) {
            char[] arrc = new char[this.d];
            for (int i = 0; i < n2; ++i) {
                arrstring[i] = this.readClass(n += 2, arrc);
            }
        }
        return arrstring;
    }

    void a(ClassWriter classWriter) {
        int n;
        char[] arrc = new char[this.d];
        int n2 = this.a.length;
        Item[] arritem = new Item[n2];
        for (n = 1; n < n2; ++n) {
            int n3;
            int n4 = this.a[n];
            byte by = this.b[n4 - 1];
            Item item = new Item(n);
            switch (by) {
                int n5;
                case 9: 
                case 10: 
                case 11: {
                    n5 = this.a[this.readUnsignedShort(n4 + 2)];
                    item.a(by, this.readClass(n4, arrc), this.readUTF8(n5, arrc), this.readUTF8(n5 + 2, arrc));
                    break;
                }
                case 3: {
                    item.a(this.readInt(n4));
                    break;
                }
                case 4: {
                    item.a(Float.intBitsToFloat(this.readInt(n4)));
                    break;
                }
                case 12: {
                    item.a(by, this.readUTF8(n4, arrc), this.readUTF8(n4 + 2, arrc), null);
                    break;
                }
                case 5: {
                    item.a(this.readLong(n4));
                    ++n;
                    break;
                }
                case 6: {
                    item.a(Double.longBitsToDouble(this.readLong(n4)));
                    ++n;
                    break;
                }
                case 1: {
                    String string = this.c[n];
                    if (string == null) {
                        n4 = this.a[n];
                        string = this.c[n] = this.a(n4 + 2, this.readUnsignedShort(n4), arrc);
                    }
                    item.a(by, string, null, null);
                    break;
                }
                case 15: {
                    n3 = this.a[this.readUnsignedShort(n4 + 1)];
                    n5 = this.a[this.readUnsignedShort(n3 + 2)];
                    item.a(20 + this.readByte(n4), this.readClass(n3, arrc), this.readUTF8(n5, arrc), this.readUTF8(n5 + 2, arrc));
                    break;
                }
                case 18: {
                    if (classWriter.A == null) {
                        this.a(classWriter, arritem, arrc);
                    }
                    n5 = this.a[this.readUnsignedShort(n4 + 2)];
                    item.a(this.readUTF8(n5, arrc), this.readUTF8(n5 + 2, arrc), this.readUnsignedShort(n4));
                    break;
                }
                default: {
                    item.a(by, this.readUTF8(n4, arrc), null, null);
                }
            }
            n3 = item.j % arritem.length;
            item.k = arritem[n3];
            arritem[n3] = item;
        }
        n = this.a[1] - 1;
        classWriter.d.putByteArray(this.b, n, this.header - n);
        classWriter.e = arritem;
        classWriter.f = (int)(0.75 * (double)n2);
        classWriter.c = n2;
    }

    private void a(ClassWriter classWriter, Item[] arritem, char[] arrc) {
        int n;
        int n2 = this.header;
        n2 += 8 + (this.readUnsignedShort(n2 + 6) << 1);
        int n3 = this.readUnsignedShort(n2);
        n2 += 2;
        while (n3 > 0) {
            n = this.readUnsignedShort(n2 + 6);
            n2 += 8;
            while (n > 0) {
                n2 += 6 + this.readInt(n2 + 2);
                --n;
            }
            --n3;
        }
        n3 = this.readUnsignedShort(n2);
        n2 += 2;
        while (n3 > 0) {
            n = this.readUnsignedShort(n2 + 6);
            n2 += 8;
            while (n > 0) {
                n2 += 6 + this.readInt(n2 + 2);
                --n;
            }
            --n3;
        }
        n3 = this.readUnsignedShort(n2);
        n2 += 2;
        while (n3 > 0) {
            String string = this.readUTF8(n2, arrc);
            int n4 = this.readInt(n2 + 2);
            if ("BootstrapMethods".equals(string)) {
                int n5 = this.readUnsignedShort(n2 + 6);
                int n6 = n2 + 8;
                for (n = 0; n < n5; ++n) {
                    int n7 = this.readConst(this.readUnsignedShort(n6), arrc).hashCode();
                    int n8 = n6 + 4;
                    for (int i = this.readUnsignedShort((int)(n6 + 2)); i > 0; --i) {
                        n7 ^= this.readConst(this.readUnsignedShort(n8), arrc).hashCode();
                        n8 += 2;
                    }
                    Item item = new Item(n);
                    item.a(n6 - n2 - 8, n7 & Integer.MAX_VALUE);
                    int n9 = item.j % arritem.length;
                    item.k = arritem[n9];
                    arritem[n9] = item;
                    n6 = n8;
                }
                classWriter.z = n5;
                ByteVector byteVector = new ByteVector(n4 + 62);
                byteVector.putByteArray(this.b, n2 + 8, n4 - 2);
                classWriter.A = byteVector;
                return;
            }
            n2 += 6 + n4;
            --n3;
        }
    }

    public ClassReader(InputStream inputStream) throws IOException {
        this(ClassReader.a(inputStream, false));
    }

    public ClassReader(String string) throws IOException {
        this(ClassReader.a(ClassLoader.getSystemResourceAsStream(string.replace('.', '/') + ".class"), true));
    }

    private static byte[] a(InputStream inputStream, boolean bl) throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        try {
            byte[] arrby = new byte[inputStream.available()];
            int n = 0;
            do {
                int n2;
                byte[] arrby2;
                if ((n2 = inputStream.read(arrby, n, arrby.length - n)) == -1) {
                    byte[] arrby3;
                    if (n < arrby.length) {
                        arrby3 = new byte[n];
                        System.arraycopy(arrby, 0, arrby3, 0, n);
                        arrby = arrby3;
                    }
                    arrby3 = arrby;
                    return arrby3;
                }
                if ((n += n2) != arrby.length) continue;
                int n3 = inputStream.read();
                if (n3 < 0) {
                    arrby2 = arrby;
                    return arrby2;
                }
                arrby2 = new byte[arrby.length + 1000];
                System.arraycopy(arrby, 0, arrby2, 0, n);
                arrby2[n++] = (byte)n3;
                arrby = arrby2;
            } while (true);
        }
        finally {
            if (bl) {
                inputStream.close();
            }
        }
    }

    public void accept(ClassVisitor classVisitor, int n) {
        this.accept(classVisitor, new Attribute[0], n);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public void accept(ClassVisitor var1_1, Attribute[] var2_2, int var3_3) {
        block210 : {
            var4_4 = this.b;
            var5_5 = new char[this.d];
            var6_6 = 0;
            var7_7 = 0;
            var8_8 = null;
            var9_9 = this.header;
            var10_10 = this.readUnsignedShort(var9_9);
            var11_11 = this.readClass(var9_9 + 2, var5_5);
            var12_12 = this.a[this.readUnsignedShort(var9_9 + 4)];
            var13_13 = var12_12 == 0 ? null : this.readUTF8(var12_12, var5_5);
            var14_14 = new String[this.readUnsignedShort(var9_9 + 6)];
            var15_15 = 0;
            var9_9 += 8;
            for (var16_16 = 0; var16_16 < var14_14.length; var9_9 += 2, ++var16_16) {
                var14_14[var16_16] = this.readClass(var9_9, var5_5);
            }
            var17_17 = (var3_3 & 1) != 0;
            var18_18 = (var3_3 & 2) != 0;
            var19_19 = (var3_3 & 8) != 0;
            var12_12 = var9_9;
            var16_16 = this.readUnsignedShort(var12_12);
            var12_12 += 2;
            while (var16_16 > 0) {
                var20_20 = this.readUnsignedShort(var12_12 + 6);
                var12_12 += 8;
                while (var20_20 > 0) {
                    var12_12 += 6 + this.readInt(var12_12 + 2);
                    --var20_20;
                }
                --var16_16;
            }
            var16_16 = this.readUnsignedShort(var12_12);
            var12_12 += 2;
            while (var16_16 > 0) {
                var20_20 = this.readUnsignedShort(var12_12 + 6);
                var12_12 += 8;
                while (var20_20 > 0) {
                    var12_12 += 6 + this.readInt(var12_12 + 2);
                    --var20_20;
                }
                --var16_16;
            }
            var21_21 = null;
            var22_22 = null;
            var23_23 = null;
            var24_24 = null;
            var25_25 = null;
            var26_26 = null;
            var27_27 = null;
            var16_16 = this.readUnsignedShort(var12_12);
            var12_12 += 2;
            while (var16_16 > 0) {
                var28_28 = this.readUTF8(var12_12, var5_5);
                if ("SourceFile".equals(var28_28)) {
                    var22_22 = this.readUTF8(var12_12 + 6, var5_5);
                } else if ("InnerClasses".equals(var28_28)) {
                    var15_15 = var12_12 + 6;
                } else if ("EnclosingMethod".equals(var28_28)) {
                    var24_24 = this.readClass(var12_12 + 6, var5_5);
                    var29_29 = this.readUnsignedShort(var12_12 + 8);
                    if (var29_29 != 0) {
                        var25_25 = this.readUTF8(this.a[var29_29], var5_5);
                        var26_26 = this.readUTF8(this.a[var29_29] + 2, var5_5);
                    }
                } else if ("Signature".equals(var28_28)) {
                    var21_21 = this.readUTF8(var12_12 + 6, var5_5);
                } else if ("RuntimeVisibleAnnotations".equals(var28_28)) {
                    var6_6 = var12_12 + 6;
                } else if ("Deprecated".equals(var28_28)) {
                    var10_10 |= 131072;
                } else if ("Synthetic".equals(var28_28)) {
                    var10_10 |= 266240;
                } else if ("SourceDebugExtension".equals(var28_28)) {
                    var29_29 = this.readInt(var12_12 + 2);
                    var23_23 = this.a(var12_12 + 6, var29_29, new char[var29_29]);
                } else if ("RuntimeInvisibleAnnotations".equals(var28_28)) {
                    var7_7 = var12_12 + 6;
                } else if ("BootstrapMethods".equals(var28_28)) {
                    var29_29 = this.readUnsignedShort(var12_12 + 6);
                    var27_27 = new int[var29_29];
                    var30_30 = var12_12 + 8;
                    for (var20_20 = 0; var20_20 < var29_29; var30_30 += 2 + this.readUnsignedShort((int)(var30_30 + 2)) << 1, ++var20_20) {
                        var27_27[var20_20] = var30_30;
                    }
                } else {
                    var31_33 = this.a(var2_2, var28_28, var12_12 + 6, this.readInt(var12_12 + 2), var5_5, -1, null);
                    if (var31_33 != null) {
                        var31_33.a = var8_8;
                        var8_8 = var31_33;
                    }
                }
                var12_12 += 6 + this.readInt(var12_12 + 2);
                --var16_16;
            }
            var1_1.visit(this.readInt(4), var10_10, var11_11, var21_21, var13_13, var14_14);
            if (!(var18_18 || var22_22 == null && var23_23 == null)) {
                var1_1.visitSource(var22_22, var23_23);
            }
            if (var24_24 != null) {
                var1_1.visitOuterClass(var24_24, var25_25, var26_26);
            }
            var16_16 = 1;
            do {
                if (var16_16 < 0) ** GOTO lbl114
                v0 = var12_12 = var16_16 == 0 ? var7_7 : var6_6;
                if (var12_12 == 0) ** GOTO lbl129
                var20_20 = this.readUnsignedShort(var12_12);
                var12_12 += 2;
                ** GOTO lbl125
lbl114: // 2 sources:
                while (var8_8 != null) {
                    var31_33 = var8_8.a;
                    var8_8.a = null;
                    var1_1.visitAttribute(var8_8);
                    var8_8 = var31_33;
                }
                if (var15_15 != 0) {
                    var16_16 = this.readUnsignedShort((int)var15_15);
                    var15_15 += 2;
                    break;
                }
                break block210;
lbl125: // 2 sources:
                while (var20_20 > 0) {
                    var12_12 = this.a(var12_12 + 2, var5_5, true, var1_1.visitAnnotation(this.readUTF8(var12_12, var5_5), var16_16 != 0));
                    --var20_20;
                }
lbl129: // 2 sources:
                --var16_16;
            } while (true);
            while (var16_16 > 0) {
                var1_1.visitInnerClass(this.readUnsignedShort((int)var15_15) == 0 ? null : this.readClass((int)var15_15, var5_5), this.readUnsignedShort(var15_15 + 2) == 0 ? null : this.readClass(var15_15 + 2, var5_5), this.readUnsignedShort(var15_15 + 4) == 0 ? null : this.readUTF8(var15_15 + 4, var5_5), this.readUnsignedShort(var15_15 + 6));
                var15_15 += 8;
                --var16_16;
            }
        }
        var16_16 = this.readUnsignedShort(var9_9);
        var9_9 += 2;
        block48 : do {
            if (var16_16 > 0) {
                var10_10 = this.readUnsignedShort(var9_9);
                var11_11 = this.readUTF8(var9_9 + 2, var5_5);
                var32_34 = this.readUTF8(var9_9 + 4, var5_5);
                var29_29 = 0;
                var21_21 = null;
                var6_6 = 0;
                var7_7 = 0;
                var8_8 = null;
                var20_20 = this.readUnsignedShort(var9_9 + 6);
                var9_9 += 8;
            } else {
                var16_16 = this.readUnsignedShort(var9_9);
                var9_9 += 2;
                break;
            }
            while (var20_20 > 0) {
                var28_28 = this.readUTF8(var9_9, var5_5);
                if ("ConstantValue".equals(var28_28)) {
                    var29_29 = this.readUnsignedShort(var9_9 + 6);
                } else if ("Signature".equals(var28_28)) {
                    var21_21 = this.readUTF8(var9_9 + 6, var5_5);
                } else if ("Deprecated".equals(var28_28)) {
                    var10_10 |= 131072;
                } else if ("Synthetic".equals(var28_28)) {
                    var10_10 |= 266240;
                } else if ("RuntimeVisibleAnnotations".equals(var28_28)) {
                    var6_6 = var9_9 + 6;
                } else if ("RuntimeInvisibleAnnotations".equals(var28_28)) {
                    var7_7 = var9_9 + 6;
                } else {
                    var31_33 = this.a(var2_2, var28_28, var9_9 + 6, this.readInt(var9_9 + 2), var5_5, -1, null);
                    if (var31_33 != null) {
                        var31_33.a = var8_8;
                        var8_8 = var31_33;
                    }
                }
                var9_9 += 6 + this.readInt(var9_9 + 2);
                --var20_20;
            }
            var30_32 = var1_1.visitField(var10_10, var11_11, var32_34, var21_21, var29_29 == 0 ? null : this.readConst(var29_29, var5_5));
            if (var30_32 == null) ** GOTO lbl199
            var20_20 = 1;
            do {
                if (var20_20 < 0) ** GOTO lbl192
                v1 = var12_12 = var20_20 == 0 ? var7_7 : var6_6;
                if (var12_12 == 0) ** GOTO lbl205
                var33_35 = this.readUnsignedShort(var12_12);
                var12_12 += 2;
                ** GOTO lbl201
lbl192: // 2 sources:
                while (var8_8 != null) {
                    var31_33 = var8_8.a;
                    var8_8.a = null;
                    var30_32.visitAttribute(var8_8);
                    var8_8 = var31_33;
                }
                var30_32.visitEnd();
lbl199: // 2 sources:
                --var16_16;
                continue block48;
lbl201: // 2 sources:
                while (var33_35 > 0) {
                    var12_12 = this.a(var12_12 + 2, var5_5, true, var30_32.visitAnnotation(this.readUTF8(var12_12, var5_5), var20_20 != 0));
                    --var33_35;
                }
lbl205: // 2 sources:
                --var20_20;
            } while (true);
            break;
        } while (true);
        do {
            block211 : {
                block212 : {
                    if (var16_16 <= 0) {
                        var1_1.visitEnd();
                        return;
                    }
                    var29_29 = var9_9 + 6;
                    var10_10 = this.readUnsignedShort(var9_9);
                    var11_11 = this.readUTF8(var9_9 + 2, var5_5);
                    var32_34 = this.readUTF8(var9_9 + 4, var5_5);
                    var21_21 = null;
                    var6_6 = 0;
                    var7_7 = 0;
                    var30_31 = 0;
                    var34_36 = 0;
                    var35_37 = 0;
                    var8_8 = null;
                    var12_12 = 0;
                    var15_15 = 0;
                    var20_20 = this.readUnsignedShort(var9_9 + 6);
                    var9_9 += 8;
                    while (var20_20 > 0) {
                        var28_28 = this.readUTF8(var9_9, var5_5);
                        var36_38 = this.readInt(var9_9 + 2);
                        var9_9 += 6;
                        if ("Code".equals(var28_28)) {
                            if (!var17_17) {
                                var12_12 = var9_9;
                            }
                        } else if ("Exceptions".equals(var28_28)) {
                            var15_15 = var9_9;
                        } else if ("Signature".equals(var28_28)) {
                            var21_21 = this.readUTF8(var9_9, var5_5);
                        } else if ("Deprecated".equals(var28_28)) {
                            var10_10 |= 131072;
                        } else if ("RuntimeVisibleAnnotations".equals(var28_28)) {
                            var6_6 = var9_9;
                        } else if ("AnnotationDefault".equals(var28_28)) {
                            var30_31 = var9_9;
                        } else if ("Synthetic".equals(var28_28)) {
                            var10_10 |= 266240;
                        } else if ("RuntimeInvisibleAnnotations".equals(var28_28)) {
                            var7_7 = var9_9;
                        } else if ("RuntimeVisibleParameterAnnotations".equals(var28_28)) {
                            var34_36 = var9_9;
                        } else if ("RuntimeInvisibleParameterAnnotations".equals(var28_28)) {
                            var35_37 = var9_9;
                        } else {
                            var31_33 = this.a(var2_2, var28_28, var9_9, (int)var36_38, var5_5, -1, null);
                            if (var31_33 != null) {
                                var31_33.a = var8_8;
                                var8_8 = var31_33;
                            }
                        }
                        var9_9 += var36_38;
                        --var20_20;
                    }
                    if (var15_15 == 0) {
                        var36_38 = null;
                    } else {
                        var36_38 = new String[this.readUnsignedShort((int)var15_15)];
                        var15_15 += 2;
                        for (var20_20 = 0; var20_20 < var36_38.length; var15_15 += 2, ++var20_20) {
                            var36_38[var20_20] = this.readClass((int)var15_15, var5_5);
                        }
                    }
                    var37_39 = var1_1.visitMethod(var10_10, var11_11, var32_34, var21_21, (String[])var36_38);
                    if (var37_39 == null) ** GOTO lbl319
                    if (!(var37_39 instanceof MethodWriter)) ** GOTO lbl295
                    var38_40 = (MethodWriter)var37_39;
                    if (var38_40.b.M != this || var21_21 != var38_40.g) ** GOTO lbl295
                    var39_42 = 0;
                    if (var36_38 == null) {
                        var39_42 = var38_40.j == 0 ? 1 : 0;
                    } else if (var36_38.length == var38_40.j) {
                        var39_42 = 1;
                        for (var20_20 = var36_38.length - 1; var20_20 >= 0; --var20_20) {
                            if (var38_40.k[var20_20] == this.readUnsignedShort((int)(var15_15 -= 2))) continue;
                            var39_42 = 0;
                            break;
                        }
                    }
                    if (var39_42 == 0) ** GOTO lbl295
                    var38_40.h = var29_29;
                    var38_40.i = var9_9 - var29_29;
                    ** GOTO lbl760
lbl295: // 3 sources:
                    if (var30_31 != 0) {
                        var38_40 = var37_39.visitAnnotationDefault();
                        this.a(var30_31, var5_5, null, (AnnotationVisitor)var38_40);
                        if (var38_40 != null) {
                            var38_40.visitEnd();
                        }
                    }
                    var20_20 = 1;
                    do {
                        if (var20_20 < 0) ** GOTO lbl309
                        var15_15 = var20_20 == 0 ? var7_7 : var6_6;
                        v2 = var15_15;
                        if (var15_15 == 0) ** GOTO lbl334
                        var33_35 = this.readUnsignedShort((int)var15_15);
                        var15_15 += 2;
                        ** GOTO lbl330
lbl309: // 1 sources:
                        if (var34_36 != 0) {
                            this.a(var34_36, var32_34, var5_5, true, var37_39);
                        }
                        if (var35_37 != 0) {
                            this.a(var35_37, var32_34, var5_5, false, var37_39);
                        }
                        while (var8_8 != null) {
                            var31_33 = var8_8.a;
                            var8_8.a = null;
                            var37_39.visitAttribute(var8_8);
                            var8_8 = var31_33;
                        }
lbl319: // 2 sources:
                        if (var37_39 != null && var12_12 != 0) {
                            var38_41 = this.readUnsignedShort(var12_12);
                            var39_42 = this.readUnsignedShort(var12_12 + 2);
                            var40_43 = this.readInt(var12_12 + 4);
                            var41_44 = var12_12 += 8;
                            var42_45 = var12_12 + var40_43;
                            var37_39.visitCode();
                            var43_46 = new Label[var40_43 + 2];
                            this.readLabel(var40_43 + 1, var43_46);
                            break;
                        }
                        break block211;
lbl330: // 2 sources:
                        while (var33_35 > 0) {
                            var15_15 = this.a(var15_15 + 2, var5_5, true, var37_39.visitAnnotation(this.readUTF8((int)var15_15, var5_5), var20_20 != 0));
                            --var33_35;
                        }
lbl334: // 2 sources:
                        --var20_20;
                    } while (true);
                    block60 : while (var12_12 < var42_45) {
                        var15_15 = var12_12 - var41_44;
                        var44_47 = var4_4[var12_12] & 255;
                        switch (ClassWriter.a[var44_47]) {
                            case 0: 
                            case 4: {
                                ++var12_12;
                                continue block60;
                            }
                            case 9: {
                                this.readLabel(var15_15 + this.readShort(var12_12 + 1), var43_46);
                                var12_12 += 3;
                                continue block60;
                            }
                            case 10: {
                                this.readLabel(var15_15 + this.readInt(var12_12 + 1), var43_46);
                                var12_12 += 5;
                                continue block60;
                            }
                            case 17: {
                                var44_47 = var4_4[var12_12 + 1] & 255;
                                if (var44_47 == 132) {
                                    var12_12 += 6;
                                    continue block60;
                                }
                                var12_12 += 4;
                                continue block60;
                            }
                            case 14: {
                                var12_12 = var12_12 + 4 - (var15_15 & 3);
                                this.readLabel(var15_15 + this.readInt(var12_12), var43_46);
                                var20_20 = this.readInt(var12_12 + 8) - this.readInt(var12_12 + 4) + 1;
                                var12_12 += 12;
                                while (var20_20 > 0) {
                                    this.readLabel(var15_15 + this.readInt(var12_12), var43_46);
                                    var12_12 += 4;
                                    --var20_20;
                                }
                                continue block60;
                            }
                            case 15: {
                                var12_12 = var12_12 + 4 - (var15_15 & 3);
                                this.readLabel(var15_15 + this.readInt(var12_12), var43_46);
                                var20_20 = this.readInt(var12_12 + 4);
                                var12_12 += 8;
                                while (var20_20 > 0) {
                                    this.readLabel(var15_15 + this.readInt(var12_12 + 4), var43_46);
                                    var12_12 += 8;
                                    --var20_20;
                                }
                                continue block60;
                            }
                            case 1: 
                            case 3: 
                            case 11: {
                                var12_12 += 2;
                                continue block60;
                            }
                            case 2: 
                            case 5: 
                            case 6: 
                            case 12: 
                            case 13: {
                                var12_12 += 3;
                                continue block60;
                            }
                            case 7: 
                            case 8: {
                                var12_12 += 5;
                                continue block60;
                            }
                        }
                        var12_12 += 4;
                    }
                    var20_20 = this.readUnsignedShort(var12_12);
                    var12_12 += 2;
                    while (var20_20 > 0) {
                        var44_48 = this.readLabel(this.readUnsignedShort(var12_12), var43_46);
                        var45_49 = this.readLabel(this.readUnsignedShort(var12_12 + 2), var43_46);
                        var46_50 = this.readLabel(this.readUnsignedShort(var12_12 + 4), var43_46);
                        var47_51 = this.readUnsignedShort(var12_12 + 6);
                        if (var47_51 == 0) {
                            var37_39.visitTryCatchBlock(var44_48, (Label)var45_49, (Label)var46_50, null);
                        } else {
                            var37_39.visitTryCatchBlock(var44_48, (Label)var45_49, (Label)var46_50, this.readUTF8(this.a[var47_51], var5_5));
                        }
                        var12_12 += 8;
                        --var20_20;
                    }
                    var44_47 = 0;
                    var45_49 = false;
                    var46_50 = false;
                    var47_51 = 0;
                    var48_52 = 0;
                    var49_53 = 0;
                    var50_54 = 0;
                    var51_55 = 0;
                    var52_56 = 0;
                    var53_57 = 0;
                    var54_58 = null;
                    var55_59 = null;
                    var56_60 = true;
                    var8_8 = null;
                    var20_20 = this.readUnsignedShort(var12_12);
                    var12_12 += 2;
                    while (var20_20 > 0) {
                        var28_28 = this.readUTF8(var12_12, var5_5);
                        if ("LocalVariableTable".equals(var28_28)) {
                            if (!var18_18) {
                                var44_47 = var12_12 + 6;
                                var15_15 = var12_12 + 8;
                                for (var33_35 = (reference)this.readUnsignedShort((int)(var12_12 + 6)); var33_35 > 0; var15_15 += 10, --var33_35) {
                                    var57_61 = this.readUnsignedShort((int)var15_15);
                                    if (var43_46[var57_61] == null) {
                                        this.readLabel((int)var57_61, (Label[])var43_46).a |= 1;
                                    }
                                    if (var43_46[var57_61 += this.readUnsignedShort((int)(var15_15 + 2))] != null) continue;
                                    this.readLabel((int)var57_61, (Label[])var43_46).a |= 1;
                                }
                            }
                        } else if ("LocalVariableTypeTable".equals(var28_28)) {
                            var45_49 = var12_12 + 6;
                        } else if ("LineNumberTable".equals(var28_28)) {
                            if (!var18_18) {
                                var15_15 = var12_12 + 8;
                                for (var33_35 = (reference)this.readUnsignedShort((int)(var12_12 + 6)); var33_35 > 0; var15_15 += 4, --var33_35) {
                                    var57_61 = this.readUnsignedShort((int)var15_15);
                                    if (var43_46[var57_61] == null) {
                                        this.readLabel((int)var57_61, (Label[])var43_46).a |= 1;
                                    }
                                    var43_46[var57_61].b = this.readUnsignedShort((int)(var15_15 + 2));
                                }
                            }
                        } else if ("StackMapTable".equals(var28_28)) {
                            if ((var3_3 & 4) == 0) {
                                var46_50 = var12_12 + 8;
                                var47_51 = this.readInt(var12_12 + 2);
                                var48_52 = this.readUnsignedShort(var12_12 + 6);
                            }
                        } else if ("StackMap".equals(var28_28)) {
                            if ((var3_3 & 4) == 0) {
                                var46_50 = var12_12 + 8;
                                var47_51 = this.readInt(var12_12 + 2);
                                var48_52 = this.readUnsignedShort(var12_12 + 6);
                                var56_60 = false;
                            }
                        } else {
                            for (var33_35 = (reference)false ? 1 : 0; var33_35 < var2_2.length; ++var33_35) {
                                if (!var2_2[var33_35].type.equals(var28_28) || (var31_33 = var2_2[var33_35].read(this, var12_12 + 6, this.readInt(var12_12 + 2), var5_5, var41_44 - 8, var43_46)) == null) continue;
                                var31_33.a = var8_8;
                                var8_8 = var31_33;
                            }
                        }
                        var12_12 += 6 + this.readInt(var12_12 + 2);
                        --var20_20;
                    }
                    if (var46_50 != false) {
                        var54_58 = new Object[var39_42];
                        var55_59 = new Object[var38_41];
                        if (var19_19) {
                            var58_63 = 0;
                            if ((var10_10 & 8) == 0) {
                                var54_58[var58_63++] = "<init>".equals(var11_11) != false ? Opcodes.UNINITIALIZED_THIS : this.readClass(this.header + 2, var5_5);
                            }
                            var20_20 = (reference)true ? 1 : 0;
                            block68 : do {
                                var33_35 = var20_20;
                                switch (var32_34.charAt((int)var20_20++)) {
                                    case 'B': 
                                    case 'C': 
                                    case 'I': 
                                    case 'S': 
                                    case 'Z': {
                                        var54_58[var58_63++] = Opcodes.INTEGER;
                                        continue block68;
                                    }
                                    case 'F': {
                                        var54_58[var58_63++] = Opcodes.FLOAT;
                                        continue block68;
                                    }
                                    case 'J': {
                                        var54_58[var58_63++] = Opcodes.LONG;
                                        continue block68;
                                    }
                                    case 'D': {
                                        var54_58[var58_63++] = Opcodes.DOUBLE;
                                        continue block68;
                                    }
                                    case '[': {
                                        while (var32_34.charAt((int)var20_20) == '[') {
                                            ++var20_20;
                                        }
                                        if (var32_34.charAt((int)var20_20) == 'L') {
                                            ++var20_20;
                                            while (var32_34.charAt((int)var20_20) != ';') {
                                                ++var20_20;
                                            }
                                        }
                                        var54_58[var58_63++] = var32_34.substring((int)var33_35, (int)(++var20_20));
                                        continue block68;
                                    }
                                    case 'L': {
                                        while (var32_34.charAt((int)var20_20) != ';') {
                                            ++var20_20;
                                        }
                                        var54_58[var58_63++] = var32_34.substring((int)(var33_35 + true), (int)var20_20++);
                                        continue block68;
                                    }
                                }
                                break;
                            } while (true);
                            var51_55 = var58_63;
                        }
                        var50_54 = -1;
                        for (var20_20 = (reference)var46_50; var20_20 < var46_50 + var47_51 - 2; ++var20_20) {
                            if (var4_4[var20_20] != 8 || (var33_35 = (reference)this.readUnsignedShort((int)(var20_20 + true))) < 0 || var33_35 >= var40_43 || (var4_4[var41_44 + var33_35] & 255) != 187) continue;
                            this.readLabel((int)var33_35, var43_46);
                        }
                    }
                    var12_12 = var41_44;
                    block73 : do {
                        if (var12_12 >= var42_45) ** GOTO lbl529
                        var15_15 = var12_12 - var41_44;
                        var58_64 = var43_46[var15_15];
                        if (var58_64 == null) ** GOTO lbl547
                        var37_39.visitLabel(var58_64);
                        if (var18_18 || var58_64.b <= 0) ** GOTO lbl547
                        var37_39.visitLineNumber(var58_64.b, var58_64);
                        ** GOTO lbl547
lbl529: // 1 sources:
                        var58_62 = var43_46[var42_45 - var41_44];
                        if (var58_62 != null) {
                            var37_39.visitLabel(var58_62);
                        }
                        if (!var18_18 && var44_47 != 0) {
                            var59_65 = null;
                            if (var45_49 != false) {
                                var33_35 = (reference)(this.readUnsignedShort((int)var45_49) * 3);
                                var15_15 = var45_49 + 2;
                                var59_65 = new int[var33_35];
                                while (var33_35 > 0) {
                                    var59_65[--var33_35] = var15_15 + 6;
                                    var59_65[--var33_35] = this.readUnsignedShort((int)(var15_15 + 8));
                                    var59_65[--var33_35] = this.readUnsignedShort((int)var15_15);
                                    var15_15 += 10;
                                }
                            }
                            var15_15 = var44_47 + 2;
                            break;
                        }
                        break block212;
lbl547: // 5 sources:
                        while (var54_58 != null && (var50_54 == var15_15 || var50_54 == -1)) {
                            if (!var56_60 || var19_19) {
                                var37_39.visitFrame(-1, var51_55, var54_58, var53_57, var55_59);
                            } else if (var50_54 != -1) {
                                var37_39.visitFrame(var49_53, (int)var52_56, var54_58, var53_57, var55_59);
                            }
                            if (var48_52 > 0) {
                                if (var56_60) {
                                    var59_65 = var4_4[var46_50++] & 255;
                                } else {
                                    var59_65 = 255;
                                    var50_54 = -1;
                                }
                                var52_56 = 0;
                                if (var59_65 < 64) {
                                    var60_67 = var59_65;
                                    var49_53 = 3;
                                    var53_57 = 0;
                                } else if (var59_65 < 128) {
                                    var60_67 = var59_65 - 64;
                                    var46_50 = this.a(var55_59, 0, (int)var46_50, var5_5, var43_46);
                                    var49_53 = 4;
                                    var53_57 = 1;
                                } else {
                                    var60_67 = this.readUnsignedShort((int)var46_50);
                                    var46_50 += 2;
                                    if (var59_65 == 247) {
                                        var46_50 = this.a(var55_59, 0, (int)var46_50, var5_5, var43_46);
                                        var49_53 = 4;
                                        var53_57 = 1;
                                    } else if (var59_65 >= 248 && var59_65 < 251) {
                                        var49_53 = 2;
                                        var52_56 = 251 - var59_65;
                                        var51_55 -= var52_56;
                                        var53_57 = 0;
                                    } else if (var59_65 == 251) {
                                        var49_53 = 3;
                                        var53_57 = 0;
                                    } else if (var59_65 < 255) {
                                        var20_20 = (reference)(var19_19 != false ? var51_55 : 0);
                                        for (var33_35 = var59_65 - 251; var33_35 > 0; --var33_35) {
                                            var46_50 = this.a(var54_58, (int)var20_20++, (int)var46_50, var5_5, var43_46);
                                        }
                                        var49_53 = 1;
                                        var52_56 = var59_65 - 251;
                                        var51_55 += var52_56;
                                        var53_57 = 0;
                                    } else {
                                        var49_53 = 0;
                                        var51_55 = this.readUnsignedShort((int)var46_50);
                                        var52_56 = var51_55;
                                        var46_50 += 2;
                                        var20_20 = (reference)false ? 1 : 0;
                                        for (var61_68 = var51_55; var61_68 > 0; --var61_68) {
                                            var46_50 = this.a(var54_58, (int)var20_20++, (int)var46_50, var5_5, var43_46);
                                        }
                                        var61_68 = var53_57 = this.readUnsignedShort((int)var46_50);
                                        var46_50 += 2;
                                        var20_20 = (reference)false ? 1 : 0;
                                        while (var61_68 > 0) {
                                            var46_50 = this.a(var55_59, (int)var20_20++, (int)var46_50, var5_5, var43_46);
                                            --var61_68;
                                        }
                                    }
                                }
                                this.readLabel(var50_54 += var60_67 + true, var43_46);
                                --var48_52;
                                continue;
                            }
                            var54_58 = null;
                        }
                        var59_66 = var4_4[var12_12] & 255;
                        switch (ClassWriter.a[var59_66]) {
                            case 0: {
                                var37_39.visitInsn(var59_66);
                                ++var12_12;
                                continue block73;
                            }
                            case 4: {
                                if (var59_66 > 54) {
                                    var37_39.visitVarInsn(54 + (var59_66 >> 2), (var59_66 -= 59) & 3);
                                } else {
                                    var37_39.visitVarInsn(21 + (var59_66 >> 2), (var59_66 -= 26) & 3);
                                }
                                ++var12_12;
                                continue block73;
                            }
                            case 9: {
                                var37_39.visitJumpInsn(var59_66, var43_46[var15_15 + this.readShort(var12_12 + 1)]);
                                var12_12 += 3;
                                continue block73;
                            }
                            case 10: {
                                var37_39.visitJumpInsn(var59_66 - 33, var43_46[var15_15 + this.readInt(var12_12 + 1)]);
                                var12_12 += 5;
                                continue block73;
                            }
                            case 17: {
                                var59_66 = var4_4[var12_12 + 1] & 255;
                                if (var59_66 == 132) {
                                    var37_39.visitIincInsn(this.readUnsignedShort(var12_12 + 2), this.readShort(var12_12 + 4));
                                    var12_12 += 6;
                                    continue block73;
                                }
                                var37_39.visitVarInsn(var59_66, this.readUnsignedShort(var12_12 + 2));
                                var12_12 += 4;
                                continue block73;
                            }
                            case 14: {
                                var12_12 = var12_12 + 4 - (var15_15 & 3);
                                var57_61 = var15_15 + this.readInt(var12_12);
                                var60_67 = this.readInt(var12_12 + 4);
                                var61_68 = this.readInt(var12_12 + 8);
                                var12_12 += 12;
                                var62_69 = new Label[var61_68 - var60_67 + 1];
                                for (var20_20 = (reference)false ? 1 : 0; var20_20 < var62_69.length; var12_12 += 4, ++var20_20) {
                                    var62_69[var20_20] = var43_46[var15_15 + this.readInt(var12_12)];
                                }
                                var37_39.visitTableSwitchInsn((int)var60_67, var61_68, var43_46[var57_61], var62_69);
                                continue block73;
                            }
                            case 15: {
                                var12_12 = var12_12 + 4 - (var15_15 & 3);
                                var57_61 = var15_15 + this.readInt(var12_12);
                                var20_20 = (reference)this.readInt(var12_12 + 4);
                                var12_12 += 8;
                                var63_71 = new int[var20_20];
                                var64_72 = new Label[var20_20];
                                for (var20_20 = (reference)false ? 1 : 0; var20_20 < var63_71.length; var12_12 += 8, ++var20_20) {
                                    var63_71[var20_20] = this.readInt(var12_12);
                                    var64_72[var20_20] = var43_46[var15_15 + this.readInt(var12_12 + 4)];
                                }
                                var37_39.visitLookupSwitchInsn(var43_46[var57_61], (int[])var63_71, var64_72);
                                continue block73;
                            }
                            case 3: {
                                var37_39.visitVarInsn(var59_66, var4_4[var12_12 + 1] & 255);
                                var12_12 += 2;
                                continue block73;
                            }
                            case 1: {
                                var37_39.visitIntInsn(var59_66, var4_4[var12_12 + 1]);
                                var12_12 += 2;
                                continue block73;
                            }
                            case 2: {
                                var37_39.visitIntInsn(var59_66, this.readShort(var12_12 + 1));
                                var12_12 += 3;
                                continue block73;
                            }
                            case 11: {
                                var37_39.visitLdcInsn(this.readConst(var4_4[var12_12 + 1] & 255, var5_5));
                                var12_12 += 2;
                                continue block73;
                            }
                            case 12: {
                                var37_39.visitLdcInsn(this.readConst(this.readUnsignedShort(var12_12 + 1), var5_5));
                                var12_12 += 3;
                                continue block73;
                            }
                            case 6: 
                            case 7: {
                                var65_74 = this.a[this.readUnsignedShort(var12_12 + 1)];
                                var66_75 = this.readClass(var65_74, var5_5);
                                var65_74 = this.a[this.readUnsignedShort(var65_74 + 2)];
                                var67_79 = this.readUTF8(var65_74, var5_5);
                                var68_80 = this.readUTF8(var65_74 + 2, var5_5);
                                if (var59_66 < 182) {
                                    var37_39.visitFieldInsn(var59_66, var66_75, var67_79, var68_80);
                                } else {
                                    var37_39.visitMethodInsn(var59_66, var66_75, var67_79, var68_80);
                                }
                                if (var59_66 == 185) {
                                    var12_12 += 5;
                                    continue block73;
                                }
                                var12_12 += 3;
                                continue block73;
                            }
                            case 8: {
                                var65_74 = this.a[this.readUnsignedShort(var12_12 + 1)];
                                var66_76 = var27_27[this.readUnsignedShort(var65_74)];
                                var65_74 = this.a[this.readUnsignedShort(var65_74 + 2)];
                                var67_79 = this.readUTF8(var65_74, var5_5);
                                var68_80 = this.readUTF8(var65_74 + 2, var5_5);
                                var69_81 = this.readUnsignedShort((int)var66_76);
                                var70_82 = (Handle)this.readConst(var69_81, var5_5);
                                var71_83 = this.readUnsignedShort((int)(var66_76 + 2));
                                var72_84 = new Object[var71_83];
                                var66_77 += 4;
                                for (var73_85 = 0; var73_85 < var71_83; var66_78 += 2, ++var73_85) {
                                    var74_86 = this.readUnsignedShort((int)var66_78);
                                    var72_84[var73_85] = this.readConst(var74_86, var5_5);
                                }
                                var37_39.visitInvokeDynamicInsn(var67_79, var68_80, var70_82, var72_84);
                                var12_12 += 5;
                                continue block73;
                            }
                            case 5: {
                                var37_39.visitTypeInsn(var59_66, this.readClass(var12_12 + 1, var5_5));
                                var12_12 += 3;
                                continue block73;
                            }
                            case 13: {
                                var37_39.visitIincInsn(var4_4[var12_12 + 1] & 255, var4_4[var12_12 + 2]);
                                var12_12 += 3;
                                continue block73;
                            }
                        }
                        var37_39.visitMultiANewArrayInsn(this.readClass(var12_12 + 1, var5_5), var4_4[var12_12 + 3] & 255);
                        var12_12 += 4;
                    } while (true);
                    for (var33_35 = (reference)this.readUnsignedShort((int)var44_47); var33_35 > 0; var15_15 += 10, --var33_35) {
                        var60_67 = this.readUnsignedShort((int)var15_15);
                        var61_68 = this.readUnsignedShort((int)(var15_15 + 2));
                        var62_70 = this.readUnsignedShort((int)(var15_15 + 8));
                        var63_71 = null;
                        if (var59_65 != null) {
                            for (var64_73 = 0; var64_73 < var59_65.length; var64_73 += 3) {
                                if (var59_65[var64_73] != var60_67 || var59_65[var64_73 + 1] != var62_70) continue;
                                var63_71 = this.readUTF8((int)var59_65[var64_73 + 2], var5_5);
                                break;
                            }
                        }
                        var37_39.visitLocalVariable(this.readUTF8((int)(var15_15 + 4), var5_5), this.readUTF8((int)(var15_15 + 6), var5_5), (String)var63_71, var43_46[var60_67], var43_46[var60_67 + var61_68], var62_70);
                    }
                }
                while (var8_8 != null) {
                    var31_33 = var8_8.a;
                    var8_8.a = null;
                    var37_39.visitAttribute(var8_8);
                    var8_8 = var31_33;
                }
                var37_39.visitMaxs(var38_41, var39_42);
            }
            if (var37_39 != null) {
                var37_39.visitEnd();
            }
lbl760: // 4 sources:
            --var16_16;
        } while (true);
    }

    private void a(int n, String string, char[] arrc, boolean bl, MethodVisitor methodVisitor) {
        AnnotationVisitor annotationVisitor;
        int n2;
        int n3 = this.b[n++] & 255;
        int n4 = Type.getArgumentTypes(string).length - n3;
        for (n2 = 0; n2 < n4; ++n2) {
            annotationVisitor = methodVisitor.visitParameterAnnotation(n2, "Ljava/lang/Synthetic;", false);
            if (annotationVisitor == null) continue;
            annotationVisitor.visitEnd();
        }
        while (n2 < n3 + n4) {
            int n5 = this.readUnsignedShort(n);
            n += 2;
            while (n5 > 0) {
                annotationVisitor = methodVisitor.visitParameterAnnotation(n2, this.readUTF8(n, arrc), bl);
                n = this.a(n + 2, arrc, true, annotationVisitor);
                --n5;
            }
            ++n2;
        }
    }

    private int a(int n, char[] arrc, boolean bl, AnnotationVisitor annotationVisitor) {
        int n2 = this.readUnsignedShort(n);
        n += 2;
        if (bl) {
            while (n2 > 0) {
                n = this.a(n + 2, arrc, this.readUTF8(n, arrc), annotationVisitor);
                --n2;
            }
        } else {
            while (n2 > 0) {
                n = this.a(n, arrc, null, annotationVisitor);
                --n2;
            }
        }
        if (annotationVisitor != null) {
            annotationVisitor.visitEnd();
        }
        return n;
    }

    private int a(int n, char[] arrc, String string, AnnotationVisitor annotationVisitor) {
        if (annotationVisitor == null) {
            switch (this.b[n] & 255) {
                case 101: {
                    return n + 5;
                }
                case 64: {
                    return this.a(n + 3, arrc, true, null);
                }
                case 91: {
                    return this.a(n + 1, arrc, false, null);
                }
            }
            return n + 3;
        }
        block5 : switch (this.b[n++] & 255) {
            case 68: 
            case 70: 
            case 73: 
            case 74: {
                annotationVisitor.visit(string, this.readConst(this.readUnsignedShort(n), arrc));
                n += 2;
                break;
            }
            case 66: {
                annotationVisitor.visit(string, new Byte((byte)this.readInt(this.a[this.readUnsignedShort(n)])));
                n += 2;
                break;
            }
            case 90: {
                annotationVisitor.visit(string, this.readInt(this.a[this.readUnsignedShort(n)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
                n += 2;
                break;
            }
            case 83: {
                annotationVisitor.visit(string, new Short((short)this.readInt(this.a[this.readUnsignedShort(n)])));
                n += 2;
                break;
            }
            case 67: {
                annotationVisitor.visit(string, new Character((char)this.readInt(this.a[this.readUnsignedShort(n)])));
                n += 2;
                break;
            }
            case 115: {
                annotationVisitor.visit(string, this.readUTF8(n, arrc));
                n += 2;
                break;
            }
            case 101: {
                annotationVisitor.visitEnum(string, this.readUTF8(n, arrc), this.readUTF8(n + 2, arrc));
                n += 4;
                break;
            }
            case 99: {
                annotationVisitor.visit(string, Type.getType(this.readUTF8(n, arrc)));
                n += 2;
                break;
            }
            case 64: {
                n = this.a(n + 2, arrc, true, annotationVisitor.visitAnnotation(string, this.readUTF8(n, arrc)));
                break;
            }
            case 91: {
                int n2 = this.readUnsignedShort(n);
                n += 2;
                if (n2 == 0) {
                    return this.a(n - 2, arrc, false, annotationVisitor.visitArray(string));
                }
                switch (this.b[n++] & 255) {
                    case 66: {
                        byte[] arrby = new byte[n2];
                        for (int i = 0; i < n2; ++i) {
                            arrby[i] = (byte)this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, arrby);
                        --n;
                        break block5;
                    }
                    case 90: {
                        boolean[] arrbl = new boolean[n2];
                        for (int i = 0; i < n2; ++i) {
                            arrbl[i] = this.readInt(this.a[this.readUnsignedShort(n)]) != 0;
                            n += 3;
                        }
                        annotationVisitor.visit(string, arrbl);
                        --n;
                        break block5;
                    }
                    case 83: {
                        short[] arrs = new short[n2];
                        for (int i = 0; i < n2; ++i) {
                            arrs[i] = (short)this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, arrs);
                        --n;
                        break block5;
                    }
                    case 67: {
                        char[] arrc2 = new char[n2];
                        for (int i = 0; i < n2; ++i) {
                            arrc2[i] = (char)this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, arrc2);
                        --n;
                        break block5;
                    }
                    case 73: {
                        int[] arrn = new int[n2];
                        for (int i = 0; i < n2; ++i) {
                            arrn[i] = this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, arrn);
                        --n;
                        break block5;
                    }
                    case 74: {
                        long[] arrl = new long[n2];
                        for (int i = 0; i < n2; ++i) {
                            arrl[i] = this.readLong(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, arrl);
                        --n;
                        break block5;
                    }
                    case 70: {
                        float[] arrf = new float[n2];
                        for (int i = 0; i < n2; ++i) {
                            arrf[i] = Float.intBitsToFloat(this.readInt(this.a[this.readUnsignedShort(n)]));
                            n += 3;
                        }
                        annotationVisitor.visit(string, arrf);
                        --n;
                        break block5;
                    }
                    case 68: {
                        double[] arrd = new double[n2];
                        for (int i = 0; i < n2; ++i) {
                            arrd[i] = Double.longBitsToDouble(this.readLong(this.a[this.readUnsignedShort(n)]));
                            n += 3;
                        }
                        annotationVisitor.visit(string, arrd);
                        --n;
                        break block5;
                    }
                }
                n = this.a(n - 3, arrc, false, annotationVisitor.visitArray(string));
            }
        }
        return n;
    }

    private int a(Object[] arrobject, int n, int n2, char[] arrc, Label[] arrlabel) {
        int n3 = this.b[n2++] & 255;
        switch (n3) {
            case 0: {
                arrobject[n] = Opcodes.TOP;
                break;
            }
            case 1: {
                arrobject[n] = Opcodes.INTEGER;
                break;
            }
            case 2: {
                arrobject[n] = Opcodes.FLOAT;
                break;
            }
            case 3: {
                arrobject[n] = Opcodes.DOUBLE;
                break;
            }
            case 4: {
                arrobject[n] = Opcodes.LONG;
                break;
            }
            case 5: {
                arrobject[n] = Opcodes.NULL;
                break;
            }
            case 6: {
                arrobject[n] = Opcodes.UNINITIALIZED_THIS;
                break;
            }
            case 7: {
                arrobject[n] = this.readClass(n2, arrc);
                n2 += 2;
                break;
            }
            default: {
                arrobject[n] = this.readLabel(this.readUnsignedShort(n2), arrlabel);
                n2 += 2;
            }
        }
        return n2;
    }

    protected Label readLabel(int n, Label[] arrlabel) {
        if (arrlabel[n] == null) {
            arrlabel[n] = new Label();
        }
        return arrlabel[n];
    }

    private Attribute a(Attribute[] arrattribute, String string, int n, int n2, char[] arrc, int n3, Label[] arrlabel) {
        for (int i = 0; i < arrattribute.length; ++i) {
            if (!arrattribute[i].type.equals(string)) continue;
            return arrattribute[i].read(this, n, n2, arrc, n3, arrlabel);
        }
        return new Attribute(string).read(this, n, n2, null, -1, null);
    }

    public int getItemCount() {
        return this.a.length;
    }

    public int getItem(int n) {
        return this.a[n];
    }

    public int getMaxStringLength() {
        return this.d;
    }

    public int readByte(int n) {
        return this.b[n] & 255;
    }

    public int readUnsignedShort(int n) {
        byte[] arrby = this.b;
        return (arrby[n] & 255) << 8 | arrby[n + 1] & 255;
    }

    public short readShort(int n) {
        byte[] arrby = this.b;
        return (short)((arrby[n] & 255) << 8 | arrby[n + 1] & 255);
    }

    public int readInt(int n) {
        byte[] arrby = this.b;
        return (arrby[n] & 255) << 24 | (arrby[n + 1] & 255) << 16 | (arrby[n + 2] & 255) << 8 | arrby[n + 3] & 255;
    }

    public long readLong(int n) {
        long l = this.readInt(n);
        long l2 = (long)this.readInt(n + 4) & 0xFFFFFFFFL;
        return l << 32 | l2;
    }

    public String readUTF8(int n, char[] arrc) {
        int n2 = this.readUnsignedShort(n);
        String string = this.c[n2];
        if (string != null) {
            return string;
        }
        n = this.a[n2];
        this.c[n2] = this.a(n + 2, this.readUnsignedShort(n), arrc);
        return this.c[n2];
    }

    private String a(int n, int n2, char[] arrc) {
        int n3 = n + n2;
        byte[] arrby = this.b;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        while (n < n3) {
            int n7 = arrby[n++];
            switch (n5) {
                case 0: {
                    if ((n7 &= 255) < 128) {
                        arrc[n4++] = (char)n7;
                        break;
                    }
                    if (n7 < 224 && n7 > 191) {
                        n6 = (char)(n7 & 31);
                        n5 = 1;
                        break;
                    }
                    n6 = (char)(n7 & 15);
                    n5 = 2;
                    break;
                }
                case 1: {
                    arrc[n4++] = (char)(n6 << 6 | n7 & 63);
                    n5 = 0;
                    break;
                }
                case 2: {
                    n6 = (char)(n6 << 6 | n7 & 63);
                    n5 = 1;
                }
            }
        }
        return new String(arrc, 0, n4);
    }

    public String readClass(int n, char[] arrc) {
        return this.readUTF8(this.a[this.readUnsignedShort(n)], arrc);
    }

    public Object readConst(int n, char[] arrc) {
        int n2 = this.a[n];
        switch (this.b[n2 - 1]) {
            case 3: {
                return new Integer(this.readInt(n2));
            }
            case 4: {
                return new Float(Float.intBitsToFloat(this.readInt(n2)));
            }
            case 5: {
                return new Long(this.readLong(n2));
            }
            case 6: {
                return new Double(Double.longBitsToDouble(this.readLong(n2)));
            }
            case 7: {
                return Type.getObjectType(this.readUTF8(n2, arrc));
            }
            case 8: {
                return this.readUTF8(n2, arrc);
            }
            case 16: {
                return Type.getMethodType(this.readUTF8(n2, arrc));
            }
        }
        int n3 = this.readByte(n2);
        int[] arrn = this.a;
        int n4 = arrn[this.readUnsignedShort(n2 + 1)];
        String string = this.readClass(n4, arrc);
        n4 = arrn[this.readUnsignedShort(n4 + 2)];
        String string2 = this.readUTF8(n4, arrc);
        String string3 = this.readUTF8(n4 + 2, arrc);
        return new Handle(n3, string, string2, string3);
    }
}

