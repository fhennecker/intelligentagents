/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.Map;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;
import lombok.libs.org.objectweb.asm.tree.LabelNode;

public class LineNumberNode
extends AbstractInsnNode {
    public int line;
    public LabelNode start;

    public LineNumberNode(int n, LabelNode labelNode) {
        super(-1);
        this.line = n;
        this.start = labelNode;
    }

    public int getType() {
        return 15;
    }

    public void accept(MethodVisitor methodVisitor) {
        methodVisitor.visitLineNumber(this.line, this.start.getLabel());
    }

    public AbstractInsnNode clone(Map map) {
        return new LineNumberNode(this.line, LineNumberNode.clone(this.start, map));
    }
}

