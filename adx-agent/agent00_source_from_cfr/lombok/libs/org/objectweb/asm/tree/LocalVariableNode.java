/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.LabelNode;

public class LocalVariableNode {
    public String name;
    public String desc;
    public String signature;
    public LabelNode start;
    public LabelNode end;
    public int index;

    public LocalVariableNode(String string, String string2, String string3, LabelNode labelNode, LabelNode labelNode2, int n) {
        this.name = string;
        this.desc = string2;
        this.signature = string3;
        this.start = labelNode;
        this.end = labelNode2;
        this.index = n;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitLocalVariable(this.name, this.desc, this.signature, this.start.getLabel(), this.end.getLabel(), this.index);
    }
}

