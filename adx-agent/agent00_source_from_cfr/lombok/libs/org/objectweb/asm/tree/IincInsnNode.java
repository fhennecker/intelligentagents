/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;

public class IincInsnNode
extends AbstractInsnNode {
    public int var;
    public int incr;

    public IincInsnNode(int n, int n2) {
        super(132);
        this.var = n;
        this.incr = n2;
    }

    public int getType() {
        return 10;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitIincInsn(this.var, this.incr);
    }

    public AbstractInsnNode clone(Map map) {
        return new IincInsnNode(this.var, this.incr);
    }
}

