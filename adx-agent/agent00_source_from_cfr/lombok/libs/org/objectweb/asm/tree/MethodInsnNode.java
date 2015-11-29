/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;

public class MethodInsnNode
extends AbstractInsnNode {
    public String owner;
    public String name;
    public String desc;

    public MethodInsnNode(int n, String string, String string2, String string3) {
        super(n);
        this.owner = string;
        this.name = string2;
        this.desc = string3;
    }

    public void setOpcode(int n) {
        this.opcode = n;
    }

    public int getType() {
        return 5;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitMethodInsn(this.opcode, this.owner, this.name, this.desc);
    }

    public AbstractInsnNode clone(Map map) {
        return new MethodInsnNode(this.opcode, this.owner, this.name, this.desc);
    }
}

