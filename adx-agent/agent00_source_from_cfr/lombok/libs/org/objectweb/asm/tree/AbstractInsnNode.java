/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.List;
import java.util.Map;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.LabelNode;

public abstract class AbstractInsnNode {
    public static final int INSN = 0;
    public static final int INT_INSN = 1;
    public static final int VAR_INSN = 2;
    public static final int TYPE_INSN = 3;
    public static final int FIELD_INSN = 4;
    public static final int METHOD_INSN = 5;
    public static final int INVOKE_DYNAMIC_INSN = 6;
    public static final int JUMP_INSN = 7;
    public static final int LABEL = 8;
    public static final int LDC_INSN = 9;
    public static final int IINC_INSN = 10;
    public static final int TABLESWITCH_INSN = 11;
    public static final int LOOKUPSWITCH_INSN = 12;
    public static final int MULTIANEWARRAY_INSN = 13;
    public static final int FRAME = 14;
    public static final int LINE = 15;
    protected int opcode;
    AbstractInsnNode prev;
    AbstractInsnNode next;
    int index;

    protected AbstractInsnNode(int n) {
        this.opcode = n;
        this.index = -1;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public abstract int getType();

    public AbstractInsnNode getPrevious() {
        return this.prev;
    }

    public AbstractInsnNode getNext() {
        return this.next;
    }

    public abstract void accept(MethodVisitor var1);

    public abstract AbstractInsnNode clone(Map var1);

    static LabelNode clone(LabelNode labelNode, Map map) {
        return (LabelNode)map.get(labelNode);
    }

    static LabelNode[] clone(List list, Map map) {
        LabelNode[] arrlabelNode = new LabelNode[list.size()];
        for (int i = 0; i < arrlabelNode.length; ++i) {
            arrlabelNode[i] = (LabelNode)map.get(list.get(i));
        }
        return arrlabelNode;
    }
}

