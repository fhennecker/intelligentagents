/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher.scripts;

import java.util.Collection;
import java.util.Collections;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.patcher.MethodTarget;
import lombok.patcher.PatchScript;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AddFieldScript
extends PatchScript {
    private final int accessFlags;
    private final String targetClass;
    private final String fieldName;
    private final String fieldType;
    private final Object value;

    AddFieldScript(String targetClass, int accessFlags, String fieldName, String fieldType, Object value) {
        if (targetClass == null) {
            throw new NullPointerException("targetClass");
        }
        if (fieldName == null) {
            throw new NullPointerException("fieldName");
        }
        if (fieldType == null) {
            throw new NullPointerException("typeSpec");
        }
        this.accessFlags = accessFlags;
        this.targetClass = targetClass;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.value = value;
    }

    @Override
    public byte[] patch(String className, byte[] byteCode) {
        if (!MethodTarget.typeMatches(className, this.targetClass)) {
            return null;
        }
        return this.runASM(byteCode, false);
    }

    @Override
    protected ClassVisitor createClassVisitor(ClassWriter writer, String classSpec) {
        return new ClassVisitor(262144, writer){

            public void visitEnd() {
                FieldVisitor fv = this.cv.visitField(AddFieldScript.this.accessFlags, AddFieldScript.this.fieldName, AddFieldScript.this.fieldType, null, AddFieldScript.this.value);
                fv.visitEnd();
                super.visitEnd();
            }
        };
    }

    @Override
    public Collection<String> getClassesToReload() {
        return Collections.singleton(this.targetClass);
    }

}

