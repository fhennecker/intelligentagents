/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;

public class MultiANewArrayInsnNode
extends AbstractInsnNode {
    public String desc;
    public int dims;

    public MultiANewArrayInsnNode(String string, int n) {
        super(197);
        this.desc = string;
        this.dims = n;
    }

    public int getType() {
        return 13;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitMultiANewArrayInsn(this.desc, this.dims);
    }

    public AbstractInsnNode clone(Map map) {
        return new MultiANewArrayInsnNode(this.desc, this.dims);
    }
}

