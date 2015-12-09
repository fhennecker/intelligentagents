/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import java.util.AbstractMap;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.libs.org.objectweb.asm.commons.JSRInlinerAdapter;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;
import lombok.libs.org.objectweb.asm.tree.InsnList;
import lombok.libs.org.objectweb.asm.tree.LabelNode;

class JSRInlinerAdapter$Instantiation
extends AbstractMap {
    final JSRInlinerAdapter$Instantiation previous;
    public final BitSet subroutine;
    public final Map rangeTable;
    public final LabelNode returnLabel;
    final /* synthetic */ JSRInlinerAdapter this$0;

    JSRInlinerAdapter$Instantiation(JSRInlinerAdapter jSRInlinerAdapter, JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation, BitSet bitSet) {
        this.this$0 = jSRInlinerAdapter;
        this.rangeTable = new HashMap();
        this.previous = jSRInlinerAdapter$Instantiation;
        this.subroutine = bitSet;
        Object object = jSRInlinerAdapter$Instantiation;
        while (object != null) {
            if (object.subroutine == bitSet) {
                throw new RuntimeException("Recursive invocation of " + bitSet);
            }
            object = object.previous;
        }
        this.returnLabel = jSRInlinerAdapter$Instantiation != null ? new LabelNode() : null;
        object = null;
        int n = jSRInlinerAdapter.instructions.size();
        for (int i = 0; i < n; ++i) {
            AbstractInsnNode abstractInsnNode = jSRInlinerAdapter.instructions.get(i);
            if (abstractInsnNode.getType() == 8) {
                LabelNode labelNode = (LabelNode)abstractInsnNode;
                if (object == null) {
                    object = new LabelNode();
                }
                this.rangeTable.put(labelNode, object);
                continue;
            }
            if (this.findOwner(i) != this) continue;
            object = null;
        }
    }

    public JSRInlinerAdapter$Instantiation findOwner(int n) {
        if (!this.subroutine.get(n)) {
            return null;
        }
        if (!this.this$0.dualCitizens.get(n)) {
            return this;
        }
        JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation = this;
        JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation2 = this.previous;
        while (jSRInlinerAdapter$Instantiation2 != null) {
            if (jSRInlinerAdapter$Instantiation2.subroutine.get(n)) {
                jSRInlinerAdapter$Instantiation = jSRInlinerAdapter$Instantiation2;
            }
            jSRInlinerAdapter$Instantiation2 = jSRInlinerAdapter$Instantiation2.previous;
        }
        return jSRInlinerAdapter$Instantiation;
    }

    public LabelNode gotoLabel(LabelNode labelNode) {
        JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation = this.findOwner(this.this$0.instructions.indexOf(labelNode));
        return (LabelNode)jSRInlinerAdapter$Instantiation.rangeTable.get(labelNode);
    }

    public LabelNode rangeLabel(LabelNode labelNode) {
        return (LabelNode)this.rangeTable.get(labelNode);
    }

    public Set entrySet() {
        return null;
    }

    public LabelNode get(Object object) {
        return this.gotoLabel((LabelNode)object);
    }
}

