/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;

public class TypeInsnNode
extends AbstractInsnNode {
    public String desc;

    public TypeInsnNode(int n, String string) {
        super(n);
        this.desc = string;
    }

    public void setOpcode(int n) {
        this.opcode = n;
    }

    public int getType() {
        return 3;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitTypeInsn(this.opcode, this.desc);
    }

    public AbstractInsnNode clone(Map map) {
        return new TypeInsnNode(this.opcode, this.desc);
    }
}

