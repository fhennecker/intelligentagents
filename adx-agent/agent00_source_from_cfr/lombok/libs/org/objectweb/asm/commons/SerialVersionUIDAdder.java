/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.commons.SerialVersionUIDAdder$Item;

public class SerialVersionUIDAdder
extends ClassVisitor {
    private boolean computeSVUID;
    private boolean hasSVUID;
    private int access;
    private String name;
    private String[] interfaces;
    private Collection svuidFields = new ArrayList();
    private boolean hasStaticInitializer;
    private Collection svuidConstructors = new ArrayList();
    private Collection svuidMethods = new ArrayList();

    public SerialVersionUIDAdder(ClassVisitor classVisitor) {
        this(262144, classVisitor);
    }

    protected SerialVersionUIDAdder(int n, ClassVisitor classVisitor) {
        super(n, classVisitor);
    }

    public void visit(int n, int n2, String string, String string2, String string3, String[] arrstring) {
        boolean bl = this.computeSVUID = (n2 & 512) == 0;
        if (this.computeSVUID) {
            this.name = string;
            this.access = n2;
            this.interfaces = arrstring;
        }
        super.visit(n, n2, string, string2, string3, arrstring);
    }

    public MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] arrstring) {
        if (this.computeSVUID) {
            if ("<clinit>".equals(string)) {
                this.hasStaticInitializer = true;
            }
            int n2 = n & 3391;
            if ((n & 2) == 0) {
                if ("<init>".equals(string)) {
                    this.svuidConstructors.add(new SerialVersionUIDAdder$Item(string, n2, string2));
                } else if (!"<clinit>".equals(string)) {
                    this.svuidMethods.add(new SerialVersionUIDAdder$Item(string, n2, string2));
                }
            }
        }
        return super.visitMethod(n, string, string2, string3, arrstring);
    }

    public FieldVisitor visitField(int n, String string, String string2, String string3, Object object) {
        if (this.computeSVUID) {
            if ("serialVersionUID".equals(string)) {
                this.computeSVUID = false;
                this.hasSVUID = true;
            }
            if ((n & 2) == 0 || (n & 136) == 0) {
                int n2 = n & 223;
                this.svuidFields.add(new SerialVersionUIDAdder$Item(string, n2, string2));
            }
        }
        return super.visitField(n, string, string2, string3, object);
    }

    public void visitInnerClass(String string, String string2, String string3, int n) {
        if (this.name != null && this.name.equals(string)) {
            this.access = n;
        }
        super.visitInnerClass(string, string2, string3, n);
    }

    public void visitEnd() {
        if (this.computeSVUID && !this.hasSVUID) {
            try {
                super.visitField(24, "serialVersionUID", "J", null, new Long(this.computeSVUID()));
            }
            catch (Throwable var1_1) {
                throw new RuntimeException("Error while computing SVUID for " + this.name, var1_1);
            }
        }
        super.visitEnd();
    }

    protected long computeSVUID() throws IOException {
        long l;
        FilterOutputStream filterOutputStream = null;
        l = 0;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            filterOutputStream = new DataOutputStream(byteArrayOutputStream);
            filterOutputStream.writeUTF(this.name.replace('/', '.'));
            filterOutputStream.writeInt(this.access & 1553);
            Arrays.sort(this.interfaces);
            for (int i = 0; i < this.interfaces.length; ++i) {
                filterOutputStream.writeUTF(this.interfaces[i].replace('/', '.'));
            }
            SerialVersionUIDAdder.writeItems(this.svuidFields, (DataOutput)((Object)filterOutputStream), false);
            if (this.hasStaticInitializer) {
                filterOutputStream.writeUTF("<clinit>");
                filterOutputStream.writeInt(8);
                filterOutputStream.writeUTF("()V");
            }
            SerialVersionUIDAdder.writeItems(this.svuidConstructors, (DataOutput)((Object)filterOutputStream), true);
            SerialVersionUIDAdder.writeItems(this.svuidMethods, (DataOutput)((Object)filterOutputStream), true);
            filterOutputStream.flush();
            byte[] arrby = this.computeSHAdigest(byteArrayOutputStream.toByteArray());
            for (int j = java.lang.Math.min((int)arrby.length, (int)8) - 1; j >= 0; --j) {
                l = l << 8 | (long)(arrby[j] & 255);
            }
        }
        finally {
            if (filterOutputStream != null) {
                filterOutputStream.close();
            }
        }
        return l;
    }

    protected byte[] computeSHAdigest(byte[] arrby) {
        try {
            return MessageDigest.getInstance("SHA").digest(arrby);
        }
        catch (Exception var2_2) {
            throw new UnsupportedOperationException(var2_2.toString());
        }
    }

    private static void writeItems(Collection collection, DataOutput dataOutput, boolean bl) throws IOException {
        int n = collection.size();
        Object[] arrobject = collection.toArray(new SerialVersionUIDAdder$Item[n]);
        Arrays.sort(arrobject);
        for (int i = 0; i < n; ++i) {
            dataOutput.writeUTF(arrobject[i].name);
            dataOutput.writeInt(arrobject[i].access);
            dataOutput.writeUTF(bl ? arrobject[i].desc.replace('/', '.') : arrobject[i].desc);
        }
    }
}

