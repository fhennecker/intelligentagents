/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;

public class InsnNode
extends AbstractInsnNode {
    public InsnNode(int n) {
        super(n);
    }

    public int getType() {
        return 0;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitInsn(this.opcode);
    }

    public AbstractInsnNode clone(Map map) {
        return new InsnNode(this.opcode);
    }
}

