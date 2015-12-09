/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher.scripts;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.patcher.Hook;
import lombok.patcher.MethodLogistics;
import lombok.patcher.PatchScript;
import lombok.patcher.StackRequest;
import lombok.patcher.TargetMatcher;
import lombok.patcher.scripts.MethodLevelPatchScript;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ReplaceMethodCallScript
extends MethodLevelPatchScript {
    private final Hook wrapper;
    private final Hook methodToReplace;
    private final boolean transplant;
    private final boolean insert;
    private final Set<StackRequest> extraRequests;

    ReplaceMethodCallScript(List<TargetMatcher> matchers, Hook callToReplace, Hook wrapper, boolean transplant, boolean insert, Set<StackRequest> extraRequests) {
        super(matchers);
        if (callToReplace == null) {
            throw new NullPointerException("callToReplace");
        }
        if (wrapper == null) {
            throw new NullPointerException("wrapper");
        }
        this.methodToReplace = callToReplace;
        this.wrapper = wrapper;
        this.transplant = transplant;
        this.insert = insert;
        assert (!insert || !transplant);
        this.extraRequests = extraRequests;
    }

    @Override
    protected PatchScript.MethodPatcher createPatcher(ClassWriter writer, final String classSpec) {
        PatchScript.MethodPatcher patcher = new PatchScript.MethodPatcher(writer, new PatchScript.MethodPatcherFactory(){

            public MethodVisitor createMethodVisitor(String name, String desc, MethodVisitor parent, MethodLogistics logistics) {
                return new ReplaceMethodCall(parent, classSpec, logistics);
            }
        });
        if (this.transplant) {
            patcher.addTransplant(this.wrapper);
        }
        return patcher;
    }

    private class ReplaceMethodCall
    extends MethodVisitor {
        private final String ownClassSpec;
        private final MethodLogistics logistics;

        public ReplaceMethodCall(MethodVisitor mv, String ownClassSpec, MethodLogistics logistics) {
            super(262144, mv);
            this.ownClassSpec = ownClassSpec;
            this.logistics = logistics;
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (ReplaceMethodCallScript.this.methodToReplace.getClassSpec().equals(owner) && ReplaceMethodCallScript.this.methodToReplace.getMethodName().equals(name) && ReplaceMethodCallScript.this.methodToReplace.getMethodDescriptor().equals(desc)) {
                if (ReplaceMethodCallScript.this.extraRequests.contains((Object)StackRequest.THIS)) {
                    this.logistics.generateLoadOpcodeForThis(this.mv);
                }
                for (StackRequest param : StackRequest.PARAMS_IN_ORDER) {
                    if (!ReplaceMethodCallScript.this.extraRequests.contains((Object)param)) continue;
                    this.logistics.generateLoadOpcodeForParam(param.getParamPos(), this.mv);
                }
                if (ReplaceMethodCallScript.this.insert) {
                    ReplaceMethodCallScript.insertMethod(ReplaceMethodCallScript.this.wrapper, this.mv);
                } else {
                    super.visitMethodInsn(184, ReplaceMethodCallScript.this.transplant ? this.ownClassSpec : ReplaceMethodCallScript.this.wrapper.getClassSpec(), ReplaceMethodCallScript.this.wrapper.getMethodName(), ReplaceMethodCallScript.this.wrapper.getMethodDescriptor());
                }
            } else {
                super.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }

}

