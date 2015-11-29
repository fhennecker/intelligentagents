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

public class LookupSwitchInsnNode
extends AbstractInsnNode {
    public LabelNode dflt;
    public List keys;
    public List labels;

    public LookupSwitchInsnNode(LabelNode labelNode, int[] arrn, LabelNode[] arrlabelNode) {
        super(171);
        this.dflt = labelNode;
        this.keys = new ArrayList(arrn == null ? 0 : arrn.length);
        this.labels = new ArrayList(arrlabelNode == null ? 0 : arrlabelNode.length);
        if (arrn != null) {
            for (int i = 0; i < arrn.length; ++i) {
                this.keys.add(new Integer(arrn[i]));
            }
        }
        if (arrlabelNode != null) {
            this.labels.addAll(Arrays.asList(arrlabelNode));
        }
    }

    public int getType() {
        return 12;
    }

    public void accept(MethodVisitor methodVisitor) {
        int[] arrn = new int[this.keys.size()];
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = (Integer)this.keys.get(i);
        }
        Label[] arrlabel = new Label[this.labels.size()];
        for (int j = 0; j < arrlabel.length; ++j) {
            arrlabel[j] = ((LabelNode)this.labels.get(j)).getLabel();
        }
        methodVisitor.visitLookupSwitchInsn(this.dflt.getLabel(), arrn, arrlabel);
    }

    public AbstractInsnNode clone(Map map) {
        LookupSwitchInsnNode lookupSwitchInsnNode = new LookupSwitchInsnNode(LookupSwitchInsnNode.clone(this.dflt, map), null, LookupSwitchInsnNode.clone(this.labels, map));
        lookupSwitchInsnNode.keys.addAll(this.keys);
        return lookupSwitchInsnNode;
    }
}

