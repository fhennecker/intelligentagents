/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher.scripts;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.Label;
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
public class ExitFromMethodEarlyScript
extends MethodLevelPatchScript {
    @NonNull
    private final Hook decisionWrapper;
    @NonNull
    private final Hook valueWrapper;
    private final Set<StackRequest> requests;
    private final boolean transplant;
    private final boolean insert;
    private final boolean insertCallOnly;

    ExitFromMethodEarlyScript(List<TargetMatcher> matchers, Hook decisionWrapper, Hook valueWrapper, boolean transplant, boolean insert, Set<StackRequest> requests) {
        super(matchers);
        this.decisionWrapper = decisionWrapper;
        this.valueWrapper = valueWrapper;
        this.requests = requests;
        this.transplant = transplant;
        this.insert = insert;
        boolean bl = this.insertCallOnly = decisionWrapper != null && decisionWrapper.getMethodDescriptor().endsWith(")V");
        if (!this.insertCallOnly && decisionWrapper != null && !decisionWrapper.getMethodDescriptor().endsWith(")Z")) {
            throw new IllegalArgumentException("The decisionWrapper method must either return 'boolean' or return 'void'.");
        }
        assert (!insert || !transplant);
    }

    @Override
    protected PatchScript.MethodPatcher createPatcher(ClassWriter writer, final String classSpec) {
        PatchScript.MethodPatcher patcher = new PatchScript.MethodPatcher(writer, new PatchScript.MethodPatcherFactory(){

            public MethodVisitor createMethodVisitor(String name, String desc, MethodVisitor parent, MethodLogistics logistics) {
                if (ExitFromMethodEarlyScript.this.valueWrapper == null && !ExitFromMethodEarlyScript.this.insertCallOnly && logistics.getReturnOpcode() != 177) {
                    throw new IllegalStateException("method " + name + desc + " must return something, but " + "you did not provide a value hook method.");
                }
                return new ExitEarly(parent, logistics, classSpec);
            }
        });
        if (this.transplant) {
            patcher.addTransplant(this.decisionWrapper);
            if (this.valueWrapper != null) {
                patcher.addTransplant(this.valueWrapper);
            }
        }
        return patcher;
    }

    private class ExitEarly
    extends MethodVisitor {
        private final MethodLogistics logistics;
        private final String ownClassSpec;

        public ExitEarly(MethodVisitor mv, MethodLogistics logistics, String ownClassSpec) {
            super(262144, mv);
            this.logistics = logistics;
            this.ownClassSpec = ownClassSpec;
        }

        public void visitCode() {
            if (ExitFromMethodEarlyScript.this.decisionWrapper == null) {
                if (this.logistics.getReturnOpcode() == 177) {
                    this.mv.visitInsn(177);
                    return;
                }
                this.insertValueWrapperCall();
                return;
            }
            if (ExitFromMethodEarlyScript.this.requests.contains((Object)StackRequest.THIS)) {
                this.logistics.generateLoadOpcodeForThis(this.mv);
            }
            for (StackRequest param : StackRequest.PARAMS_IN_ORDER) {
                if (!ExitFromMethodEarlyScript.this.requests.contains((Object)param)) continue;
                this.logistics.generateLoadOpcodeForParam(param.getParamPos(), this.mv);
            }
            if (ExitFromMethodEarlyScript.this.insert) {
                ExitFromMethodEarlyScript.insertMethod(ExitFromMethodEarlyScript.this.decisionWrapper, this.mv);
            } else {
                super.visitMethodInsn(184, ExitFromMethodEarlyScript.this.transplant ? this.ownClassSpec : ExitFromMethodEarlyScript.this.decisionWrapper.getClassSpec(), ExitFromMethodEarlyScript.this.decisionWrapper.getMethodName(), ExitFromMethodEarlyScript.this.decisionWrapper.getMethodDescriptor());
            }
            if (ExitFromMethodEarlyScript.this.insertCallOnly) {
                super.visitCode();
                return;
            }
            Label l0 = new Label();
            this.mv.visitJumpInsn(153, l0);
            if (this.logistics.getReturnOpcode() == 177) {
                this.mv.visitInsn(177);
            } else {
                if (ExitFromMethodEarlyScript.this.requests.contains((Object)StackRequest.THIS)) {
                    this.logistics.generateLoadOpcodeForThis(this.mv);
                }
                for (StackRequest param2 : StackRequest.PARAMS_IN_ORDER) {
                    if (!ExitFromMethodEarlyScript.this.requests.contains((Object)param2)) continue;
                    this.logistics.generateLoadOpcodeForParam(param2.getParamPos(), this.mv);
                }
                if (ExitFromMethodEarlyScript.this.insert) {
                    ExitFromMethodEarlyScript.insertMethod(ExitFromMethodEarlyScript.this.valueWrapper, this.mv);
                } else {
                    super.visitMethodInsn(184, ExitFromMethodEarlyScript.this.transplant ? this.ownClassSpec : ExitFromMethodEarlyScript.this.valueWrapper.getClassSpec(), ExitFromMethodEarlyScript.this.valueWrapper.getMethodName(), ExitFromMethodEarlyScript.this.valueWrapper.getMethodDescriptor());
                }
                this.logistics.generateReturnOpcode(this.mv);
            }
            this.mv.visitLabel(l0);
            this.mv.visitFrame(3, 0, null, 0, null);
            super.visitCode();
        }

        private void insertValueWrapperCall() {
            if (ExitFromMethodEarlyScript.this.requests.contains((Object)StackRequest.THIS)) {
                this.logistics.generateLoadOpcodeForThis(this.mv);
            }
            for (StackRequest param : StackRequest.PARAMS_IN_ORDER) {
                if (!ExitFromMethodEarlyScript.this.requests.contains((Object)param)) continue;
                this.logistics.generateLoadOpcodeForParam(param.getParamPos(), this.mv);
            }
            if (ExitFromMethodEarlyScript.this.insert) {
                ExitFromMethodEarlyScript.insertMethod(ExitFromMethodEarlyScript.this.valueWrapper, this.mv);
            } else {
                super.visitMethodInsn(184, ExitFromMethodEarlyScript.this.transplant ? this.ownClassSpec : ExitFromMethodEarlyScript.this.valueWrapper.getClassSpec(), ExitFromMethodEarlyScript.this.valueWrapper.getMethodName(), ExitFromMethodEarlyScript.this.valueWrapper.getMethodDescriptor());
            }
            this.logistics.generateReturnOpcode(this.mv);
        }
    }

}

