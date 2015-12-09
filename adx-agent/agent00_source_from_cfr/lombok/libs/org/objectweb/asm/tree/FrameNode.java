/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;
import lombok.libs.org.objectweb.asm.tree.LabelNode;

public class FrameNode
extends AbstractInsnNode {
    public int type;
    public List local;
    public List stack;

    private FrameNode() {
        super(-1);
    }

    public FrameNode(int n, int n2, Object[] arrobject, int n3, Object[] arrobject2) {
        super(-1);
        this.type = n;
        switch (n) {
            case -1: 
            case 0: {
                this.local = FrameNode.asList(n2, arrobject);
                this.stack = FrameNode.asList(n3, arrobject2);
                break;
            }
            case 1: {
                this.local = FrameNode.asList(n2, arrobject);
                break;
            }
            case 2: {
                this.local = Arrays.asList(new Object[n2]);
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                this.stack = FrameNode.asList(1, arrobject2);
            }
        }
    }

    public int getType() {
        return 14;
    }

    public void accept(MethodVisitor methodVisitor) {
        switch (this.type) {
            case -1: 
            case 0: {
                methodVisitor.visitFrame(this.type, this.local.size(), FrameNode.asArray(this.local), this.stack.size(), FrameNode.asArray(this.stack));
                break;
            }
            case 1: {
                methodVisitor.visitFrame(this.type, this.local.size(), FrameNode.asArray(this.local), 0, null);
                break;
            }
            case 2: {
                methodVisitor.visitFrame(this.type, this.local.size(), null, 0, null);
                break;
            }
            case 3: {
                methodVisitor.visitFrame(this.type, 0, null, 0, null);
                break;
            }
            case 4: {
                methodVisitor.visitFrame(this.type, 0, null, 1, FrameNode.asArray(this.stack));
            }
        }
    }

    public AbstractInsnNode clone(Map map) {
        Object object;
        int n;
        FrameNode frameNode = new FrameNode();
        frameNode.type = this.type;
        if (this.local != null) {
            frameNode.local = new ArrayList();
            for (n = 0; n < this.local.size(); ++n) {
                object = this.local.get(n);
                if (object instanceof LabelNode) {
                    object = map.get(object);
                }
                frameNode.local.add(object);
            }
        }
        if (this.stack != null) {
            frameNode.stack = new ArrayList();
            for (n = 0; n < this.stack.size(); ++n) {
                object = this.stack.get(n);
                if (object instanceof LabelNode) {
                    object = map.get(object);
                }
                frameNode.stack.add(object);
            }
        }
        return frameNode;
    }

    private static List asList(int n, Object[] arrobject) {
        return Arrays.asList(arrobject).subList(0, n);
    }

    private static Object[] asArray(List list) {
        Object[] arrobject = new Object[list.size()];
        for (int i = 0; i < arrobject.length; ++i) {
            Object object = list.get(i);
            if (object instanceof LabelNode) {
                object = ((LabelNode)object).getLabel();
            }
            arrobject[i] = object;
        }
        return arrobject;
    }
}

