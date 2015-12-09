/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.LabelNode;

public class TryCatchBlockNode {
    public LabelNode start;
    public LabelNode end;
    public LabelNode handler;
    public String type;

    public TryCatchBlockNode(LabelNode labelNode, LabelNode labelNode2, LabelNode labelNode3, String string) {
        this.start = labelNode;
        this.end = labelNode2;
        this.handler = labelNode3;
        this.type = string;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitTryCatchBlock(this.start.getLabel(), this.end.getLabel(), this.handler == null ? null : this.handler.getLabel(), this.type);
    }
}

