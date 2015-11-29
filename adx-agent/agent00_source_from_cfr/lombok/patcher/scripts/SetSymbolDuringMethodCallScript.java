/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher.scripts;

import java.util.Collection;
import java.util.List;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.patcher.Hook;
import lombok.patcher.MethodLogistics;
import lombok.patcher.PatchScript;
import lombok.patcher.TargetMatcher;
import lombok.patcher.scripts.MethodLevelPatchScript;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SetSymbolDuringMethodCallScript
extends MethodLevelPatchScript {
    private final Hook callToWrap;
    private final String symbol;

    SetSymbolDuringMethodCallScript(List<TargetMatcher> matchers, Hook callToWrap, String symbol) {
        super(matchers);
        if (callToWrap == null) {
            throw new NullPointerException("callToWrap");
        }
        if (symbol == null) {
            throw new NullPointerException("symbol");
        }
        this.callToWrap = callToWrap;
        this.symbol = symbol;
    }

    @Override
    protected PatchScript.MethodPatcher createPatcher(ClassWriter writer, String classSpec) {
        PatchScript.MethodPatcher patcher = new PatchScript.MethodPatcher(writer, new PatchScript.MethodPatcherFactory(){

            public MethodVisitor createMethodVisitor(String name, String desc, MethodVisitor parent, MethodLogistics logistics) {
                return new WrapWithSymbol(parent);
            }
        });
        return patcher;
    }

    private class WrapWithSymbol
    extends MethodVisitor {
        public WrapWithSymbol(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (SetSymbolDuringMethodCallScript.this.callToWrap.getClassSpec().equals(owner) && SetSymbolDuringMethodCallScript.this.callToWrap.getMethodName().equals(name) && SetSymbolDuringMethodCallScript.this.callToWrap.getMethodDescriptor().equals(desc)) {
                this.createTryFinally(opcode, owner, name, desc);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc);
            }
        }

        private void createTryFinally(int opcode, String owner, String name, String desc) {
            Label start = new Label();
            Label end = new Label();
            Label handler = new Label();
            Label restOfMethod = new Label();
            this.mv.visitTryCatchBlock(start, end, handler, null);
            this.mv.visitLabel(start);
            this.mv.visitLdcInsn(SetSymbolDuringMethodCallScript.this.symbol);
            this.mv.visitMethodInsn(184, "lombok/patcher/Symbols", "push", "(Ljava/lang/String;)V");
            this.mv.visitMethodInsn(opcode, owner, name, desc);
            this.mv.visitLabel(end);
            this.mv.visitMethodInsn(184, "lombok/patcher/Symbols", "pop", "()V");
            this.mv.visitJumpInsn(167, restOfMethod);
            this.mv.visitLabel(handler);
            this.mv.visitMethodInsn(184, "lombok/patcher/Symbols", "pop", "()V");
            this.mv.visitInsn(191);
            this.mv.visitLabel(restOfMethod);
        }
    }

}

