/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;
import lombok.libs.org.objectweb.asm.tree.LabelNode;

public class TableSwitchInsnNode
extends AbstractInsnNode {
    public int min;
    public int max;
    public LabelNode dflt;
    public List labels;

    public /* varargs */ TableSwitchInsnNode(int n, int n2, LabelNode labelNode, LabelNode ... arrlabelNode) {
        super(170);
        this.min = n;
        this.max = n2;
        this.dflt = labelNode;
        this.labels = new ArrayList();
        if (arrlabelNode != null) {
            this.labels.addAll(Arrays.asList(arrlabelNode));
        }
    }

    public int getType() {
        return 11;
    }

    public void accept(MethodVisitor methodVisitor) {
        Label[] arrlabel = new Label[this.labels.size()];
        for (int i = 0; i < arrlabel.length; ++i) {
            arrlabel[i] = ((LabelNode)this.labels.get(i)).getLabel();
        }
        methodVisitor.visitTableSwitchInsn(this.min, this.max, this.dflt.getLabel(), arrlabel);
    }

    public AbstractInsnNode clone(Map map) {
        return new TableSwitchInsnNode(this.min, this.max, TableSwitchInsnNode.clone(this.dflt, map), TableSwitchInsnNode.clone(this.labels, map));
    }
}

