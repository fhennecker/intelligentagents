/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;

public class IntInsnNode
extends AbstractInsnNode {
    public int operand;

    public IntInsnNode(int n, int n2) {
        super(n);
        this.operand = n2;
    }

    public void setOpcode(int n) {
        this.opcode = n;
    }

    public int getType() {
        return 1;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitIntInsn(this.opcode, this.operand);
    }

    public AbstractInsnNode clone(Map map) {
        return new IntInsnNode(this.opcode, this.operand);
    }
}

