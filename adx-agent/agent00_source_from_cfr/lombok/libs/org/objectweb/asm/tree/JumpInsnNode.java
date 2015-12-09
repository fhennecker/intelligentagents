/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;
import lombok.libs.org.objectweb.asm.tree.LabelNode;

public class JumpInsnNode
extends AbstractInsnNode {
    public LabelNode label;

    public JumpInsnNode(int n, LabelNode labelNode) {
        super(n);
        this.label = labelNode;
    }

    public void setOpcode(int n) {
        this.opcode = n;
    }

    public int getType() {
        return 7;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitJumpInsn(this.opcode, this.label.getLabel());
    }

    public AbstractInsnNode clone(Map map) {
        return new JumpInsnNode(this.opcode, JumpInsnNode.clone(this.label, map));
    }
}

