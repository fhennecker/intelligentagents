/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;

public class VarInsnNode
extends AbstractInsnNode {
    public int var;

    public VarInsnNode(int n, int n2) {
        super(n);
        this.var = n2;
    }

    public void setOpcode(int n) {
        this.opcode = n;
    }

    public int getType() {
        return 2;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitVarInsn(this.opcode, this.var);
    }

    public AbstractInsnNode clone(Map map) {
        return new VarInsnNode(this.opcode, this.var);
    }
}

