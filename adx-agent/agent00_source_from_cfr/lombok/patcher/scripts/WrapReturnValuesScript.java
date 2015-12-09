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
public final class WrapReturnValuesScript
extends MethodLevelPatchScript {
    @NonNull
    private final Hook wrapper;
    private final Set<StackRequest> requests;
    private final boolean hijackReturnValue;
    private final boolean transplant;
    private final boolean insert;

    WrapReturnValuesScript(List<TargetMatcher> matchers, Hook wrapper, boolean transplant, boolean insert, Set<StackRequest> requests) {
        super(matchers);
        if (wrapper == null) {
            throw new NullPointerException("wrapper");
        }
        this.wrapper = wrapper;
        this.hijackReturnValue = !wrapper.getMethodDescriptor().endsWith(")V");
        this.requests = requests;
        this.transplant = transplant;
        this.insert = insert;
        assert (!insert || !transplant);
    }

    @Override
    protected PatchScript.MethodPatcher createPatcher(ClassWriter writer, final String classSpec) {
        PatchScript.MethodPatcher patcher = new PatchScript.MethodPatcher(writer, new PatchScript.MethodPatcherFactory(){

            public MethodVisitor createMethodVisitor(String name, String desc, MethodVisitor parent, MethodLogistics logistics) {
                return new WrapReturnValues(parent, logistics, classSpec);
            }
        });
        if (this.transplant) {
            patcher.addTransplant(this.wrapper);
        }
        return patcher;
    }

    public String toString() {
        return "WrapReturnValuesScript(wrapper=" + this.wrapper + ", requests=" + this.requests + ", hijackReturnValue=" + this.hijackReturnValue + ", transplant=" + this.transplant + ", insert=" + this.insert + ")";
    }

    private class WrapReturnValues
    extends MethodVisitor {
        private final MethodLogistics logistics;
        private final String ownClassSpec;

        public WrapReturnValues(MethodVisitor mv, MethodLogistics logistics, String ownClassSpec) {
            super(262144, mv);
            this.logistics = logistics;
            this.ownClassSpec = ownClassSpec;
        }

        public void visitInsn(int opcode) {
            if (opcode != this.logistics.getReturnOpcode()) {
                super.visitInsn(opcode);
                return;
            }
            if (WrapReturnValuesScript.this.requests.contains((Object)StackRequest.RETURN_VALUE)) {
                if (!WrapReturnValuesScript.this.hijackReturnValue) {
                    this.logistics.generateDupForReturn(this.mv);
                }
            } else if (WrapReturnValuesScript.this.hijackReturnValue) {
                this.logistics.generatePopForReturn(this.mv);
            }
            if (WrapReturnValuesScript.this.requests.contains((Object)StackRequest.THIS)) {
                this.logistics.generateLoadOpcodeForThis(this.mv);
            }
            for (StackRequest param : StackRequest.PARAMS_IN_ORDER) {
                if (!WrapReturnValuesScript.this.requests.contains((Object)param)) continue;
                this.logistics.generateLoadOpcodeForParam(param.getParamPos(), this.mv);
            }
            if (WrapReturnValuesScript.this.insert) {
                WrapReturnValuesScript.insertMethod(WrapReturnValuesScript.this.wrapper, this.mv);
            } else {
                super.visitMethodInsn(184, WrapReturnValuesScript.this.transplant ? this.ownClassSpec : WrapReturnValuesScript.this.wrapper.getClassSpec(), WrapReturnValuesScript.this.wrapper.getMethodName(), WrapReturnValuesScript.this.wrapper.getMethodDescriptor());
            }
            super.visitInsn(opcode);
        }
    }

}

