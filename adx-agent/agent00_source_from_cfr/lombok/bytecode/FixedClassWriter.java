/*
 * Decompiled with CFR 0_110.
 */
package lombok.bytecode;

import lombok.libs.org.objectweb.asm.ClassReader;
import lombok.libs.org.objectweb.asm.ClassWriter;

class FixedClassWriter
extends ClassWriter {
    FixedClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags);
    }

    protected String getCommonSuperClass(String type1, String type2) {
        try {
            return super.getCommonSuperClass(type1, type2);
        }
        catch (OutOfMemoryError e) {
            throw e;
        }
        catch (Throwable t) {
            return "java/lang/Object";
        }
    }
}

