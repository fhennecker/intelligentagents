/*
 * Decompiled with CFR 0_110.
 */
package lombok.bytecode;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.bytecode.AsmUtil;
import lombok.bytecode.ClassFileMetaData;
import lombok.bytecode.FixedClassWriter;
import lombok.bytecode.SneakyThrowsRemover;
import lombok.core.DiagnosticsReceiver;
import lombok.core.PostCompilerTransformation;
import lombok.libs.org.objectweb.asm.ClassReader;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.MethodVisitor;

public class SneakyThrowsRemover
implements PostCompilerTransformation {
    public byte[] applyTransformations(byte[] original, String fileName, DiagnosticsReceiver diagnostics) {
        if (!new ClassFileMetaData(original).usesMethod("lombok/Lombok", "sneakyThrow")) {
            return null;
        }
        byte[] fixedByteCode = AsmUtil.fixJSRInlining(original);
        ClassReader reader = new ClassReader(fixedByteCode);
        FixedClassWriter writer = new FixedClassWriter(reader, 3);
        final AtomicBoolean changesMade = new AtomicBoolean();
        reader.accept(new ClassVisitor(262144, writer){

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                class SneakyThrowsRemoverVisitor
                extends MethodVisitor {
                    final /* synthetic */ AtomicBoolean val$changesMade;
                    final /* synthetic */ SneakyThrowsRemover this$0;

                    SneakyThrowsRemoverVisitor(MethodVisitor var1_1) {
                        this.this$0 = var1_1;
                        this.val$changesMade = methodVisitor;
                        super(262144, mv);
                    }

                    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                        if (opcode == 184 && "sneakyThrow".equals(name) && "lombok/Lombok".equals(owner) && "(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;".equals(desc)) {
                            this.val$changesMade.set(true);
                            super.visitInsn(191);
                        } else {
                            super.visitMethodInsn(opcode, owner, name, desc);
                        }
                    }
                }
                return new SneakyThrowsRemoverVisitor(SneakyThrowsRemover.this, super.visitMethod(access, name, desc, signature, exceptions), changesMade);
            }
        }, 0);
        return changesMade.get() ? (Object)writer.toByteArray() : null;
    }

}

