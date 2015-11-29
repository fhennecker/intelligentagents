/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.Handle;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;

public class InvokeDynamicInsnNode
extends AbstractInsnNode {
    public String name;
    public String desc;
    public Handle bsm;
    public Object[] bsmArgs;

    public /* varargs */ InvokeDynamicInsnNode(String string, String string2, Handle handle, Object ... arrobject) {
        super(186);
        this.name = string;
        this.desc = string2;
        this.bsm = handle;
        this.bsmArgs = arrobject;
    }

    public int getType() {
        return 6;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitInvokeDynamicInsn(this.name, this.desc, this.bsm, this.bsmArgs);
    }

    public AbstractInsnNode clone(Map map) {
        return new InvokeDynamicInsnNode(this.name, this.desc, this.bsm, this.bsmArgs);
    }
}

