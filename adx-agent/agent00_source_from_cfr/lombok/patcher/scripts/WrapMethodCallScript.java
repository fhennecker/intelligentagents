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
import lombok.patcher.MethodTarget;
import lombok.patcher.PatchScript;
import lombok.patcher.StackRequest;
import lombok.patcher.TargetMatcher;
import lombok.patcher.scripts.MethodLevelPatchScript;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WrapMethodCallScript
extends MethodLevelPatchScript {
    private final Hook wrapper;
    private final Hook callToWrap;
    private final boolean transplant;
    private final boolean insert;
    private final boolean leaveReturnValueIntact;
    private final Set<StackRequest> extraRequests;

    WrapMethodCallScript(List<TargetMatcher> matchers, Hook callToWrap, Hook wrapper, boolean transplant, boolean insert, Set<StackRequest> extraRequests) {
        super(matchers);
        if (callToWrap == null) {
            throw new NullPointerException("callToWrap");
        }
        if (wrapper == null) {
            throw new NullPointerException("wrapper");
        }
        this.leaveReturnValueIntact = wrapper.getMethodDescriptor().endsWith(")V") && (!callToWrap.getMethodDescriptor().endsWith(")V") || callToWrap.isConstructor());
        this.callToWrap = callToWrap;
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
            super.visitMethodInsn(opcode, owner, name, desc);
            if (WrapMethodCallScript.this.callToWrap.getClassSpec().equals(owner) && WrapMethodCallScript.this.callToWrap.getMethodName().equals(name) && WrapMethodCallScript.this.callToWrap.getMethodDescriptor().equals(desc)) {
                if (WrapMethodCallScript.this.leaveReturnValueIntact) {
                    if (WrapMethodCallScript.this.callToWrap.isConstructor()) {
                        this.mv.visitInsn(89);
                    } else {
                        MethodLogistics.generateDupForType(MethodTarget.decomposeFullDesc(WrapMethodCallScript.this.callToWrap.getMethodDescriptor()).get(0), this.mv);
                    }
                }
                if (WrapMethodCallScript.this.extraRequests.contains((Object)StackRequest.THIS)) {
                    this.logistics.generateLoadOpcodeForThis(this.mv);
                }
                for (StackRequest param : StackRequest.PARAMS_IN_ORDER) {
                    if (!WrapMethodCallScript.this.extraRequests.contains((Object)param)) continue;
                    this.logistics.generateLoadOpcodeForParam(param.getParamPos(), this.mv);
                }
                if (WrapMethodCallScript.this.insert) {
                    WrapMethodCallScript.insertMethod(WrapMethodCallScript.this.wrapper, this.mv);
                } else {
                    super.visitMethodInsn(184, WrapMethodCallScript.this.transplant ? this.ownClassSpec : WrapMethodCallScript.this.wrapper.getClassSpec(), WrapMethodCallScript.this.wrapper.getMethodName(), WrapMethodCallScript.this.wrapper.getMethodDescriptor());
                }
            }
        }
    }

}

