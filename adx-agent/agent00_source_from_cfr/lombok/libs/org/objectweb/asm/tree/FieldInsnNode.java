/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;

public class FieldInsnNode
extends AbstractInsnNode {
    public String owner;
    public String name;
    public String desc;

    public FieldInsnNode(int n, String string, String string2, String string3) {
        super(n);
        this.owner = string;
        this.name = string2;
        this.desc = string3;
    }

    public void setOpcode(int n) {
        this.opcode = n;
    }

    public int getType() {
        return 4;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitFieldInsn(this.opcode, this.owner, this.name, this.desc);
    }

    public AbstractInsnNode clone(Map map) {
        return new FieldInsnNode(this.opcode, this.owner, this.name, this.desc);
    }
}

