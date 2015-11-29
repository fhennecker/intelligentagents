/*
 * Decompiled with CFR 0_110.
 */
package lombok.bytecode;

import lombok.bytecode.FixedClassWriter;
import lombok.libs.org.objectweb.asm.ClassReader;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.commons.JSRInlinerAdapter;

class AsmUtil {
    private AsmUtil() {
        throw new UnsupportedOperationException();
    }

    static byte[] fixJSRInlining(byte[] byteCode) {
        ClassReader reader = new ClassReader(byteCode);
        FixedClassWriter writer = new FixedClassWriter(reader, 0);
        ClassVisitor visitor = new ClassVisitor(262144, writer){

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return new JSRInlinerAdapter(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc, signature, exceptions);
            }
        };
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }

}

