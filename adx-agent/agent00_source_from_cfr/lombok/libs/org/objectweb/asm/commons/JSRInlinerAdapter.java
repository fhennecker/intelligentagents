/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.Opcodes;
import lombok.libs.org.objectweb.asm.commons.JSRInlinerAdapter$Instantiation;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;
import lombok.libs.org.objectweb.asm.tree.InsnList;
import lombok.libs.org.objectweb.asm.tree.InsnNode;
import lombok.libs.org.objectweb.asm.tree.JumpInsnNode;
import lombok.libs.org.objectweb.asm.tree.LabelNode;
import lombok.libs.org.objectweb.asm.tree.LocalVariableNode;
import lombok.libs.org.objectweb.asm.tree.LookupSwitchInsnNode;
import lombok.libs.org.objectweb.asm.tree.MethodNode;
import lombok.libs.org.objectweb.asm.tree.TableSwitchInsnNode;
import lombok.libs.org.objectweb.asm.tree.TryCatchBlockNode;

public class JSRInlinerAdapter
extends MethodNode
implements Opcodes {
    private final Map subroutineHeads = new HashMap();
    private final BitSet mainSubroutine = new BitSet();
    final BitSet dualCitizens = new BitSet();

    public JSRInlinerAdapter(MethodVisitor methodVisitor, int n, String string, String string2, String string3, String[] arrstring) {
        this(262144, methodVisitor, n, string, string2, string3, arrstring);
    }

    protected JSRInlinerAdapter(int n, MethodVisitor methodVisitor, int n2, String string, String string2, String string3, String[] arrstring) {
        super(n, n2, string, string2, string3, arrstring);
        this.mv = methodVisitor;
    }

    public void visitJumpInsn(int n, Label label) {
        super.visitJumpInsn(n, label);
        LabelNode labelNode = ((JumpInsnNode)this.instructions.getLast()).label;
        if (n == 168 && !this.subroutineHeads.containsKey(labelNode)) {
            this.subroutineHeads.put(labelNode, new BitSet());
        }
    }

    public void visitEnd() {
        if (!this.subroutineHeads.isEmpty()) {
            this.markSubroutines();
            this.emitCode();
        }
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }

    private void markSubroutines() {
        BitSet bitSet = new BitSet();
        this.markSubroutineWalk(this.mainSubroutine, 0, bitSet);
        Iterator iterator = this.subroutineHeads.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            LabelNode labelNode = (LabelNode)entry.getKey();
            BitSet bitSet2 = (BitSet)entry.getValue();
            int n = this.instructions.indexOf(labelNode);
            this.markSubroutineWalk(bitSet2, n, bitSet);
        }
    }

    private void markSubroutineWalk(BitSet bitSet, int n, BitSet bitSet2) {
        this.markSubroutineWalkDFS(bitSet, n, bitSet2);
        boolean bl = true;
        while (bl) {
            bl = false;
            Iterator iterator = this.tryCatchBlocks.iterator();
            while (iterator.hasNext()) {
                TryCatchBlockNode tryCatchBlockNode = (TryCatchBlockNode)iterator.next();
                int n2 = this.instructions.indexOf(tryCatchBlockNode.handler);
                if (bitSet.get(n2)) continue;
                int n3 = this.instructions.indexOf(tryCatchBlockNode.start);
                int n4 = this.instructions.indexOf(tryCatchBlockNode.end);
                int n5 = bitSet.nextSetBit(n3);
                if (n5 == -1 || n5 >= n4) continue;
                this.markSubroutineWalkDFS(bitSet, n2, bitSet2);
                bl = true;
            }
        }
    }

    private void markSubroutineWalkDFS(BitSet bitSet, int n, BitSet bitSet2) {
        do {
            int n2;
            LabelNode labelNode;
            int n3;
            AbstractInsnNode abstractInsnNode = this.instructions.get(n);
            if (bitSet.get(n)) {
                return;
            }
            bitSet.set(n);
            if (bitSet2.get(n)) {
                this.dualCitizens.set(n);
            }
            bitSet2.set(n);
            if (abstractInsnNode.getType() == 7 && abstractInsnNode.getOpcode() != 168) {
                JumpInsnNode jumpInsnNode = (JumpInsnNode)abstractInsnNode;
                n2 = this.instructions.indexOf(jumpInsnNode.label);
                this.markSubroutineWalkDFS(bitSet, n2, bitSet2);
            }
            if (abstractInsnNode.getType() == 11) {
                TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode)abstractInsnNode;
                n2 = this.instructions.indexOf(tableSwitchInsnNode.dflt);
                this.markSubroutineWalkDFS(bitSet, n2, bitSet2);
                for (n3 = tableSwitchInsnNode.labels.size() - 1; n3 >= 0; --n3) {
                    labelNode = (LabelNode)tableSwitchInsnNode.labels.get(n3);
                    n2 = this.instructions.indexOf(labelNode);
                    this.markSubroutineWalkDFS(bitSet, n2, bitSet2);
                }
            }
            if (abstractInsnNode.getType() == 12) {
                LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode)abstractInsnNode;
                n2 = this.instructions.indexOf(lookupSwitchInsnNode.dflt);
                this.markSubroutineWalkDFS(bitSet, n2, bitSet2);
                for (n3 = lookupSwitchInsnNode.labels.size() - 1; n3 >= 0; --n3) {
                    labelNode = (LabelNode)lookupSwitchInsnNode.labels.get(n3);
                    n2 = this.instructions.indexOf(labelNode);
                    this.markSubroutineWalkDFS(bitSet, n2, bitSet2);
                }
            }
            switch (this.instructions.get(n).getOpcode()) {
                case 167: 
                case 169: 
                case 170: 
                case 171: 
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 191: {
                    return;
                }
            }
            ++n;
        } while (true);
    }

    private void emitCode() {
        LinkedList<JSRInlinerAdapter$Instantiation> linkedList = new LinkedList<JSRInlinerAdapter$Instantiation>();
        linkedList.add(new JSRInlinerAdapter$Instantiation(this, null, this.mainSubroutine));
        InsnList insnList = new InsnList();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        while (!linkedList.isEmpty()) {
            JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation = (JSRInlinerAdapter$Instantiation)linkedList.removeFirst();
            this.emitSubroutine(jSRInlinerAdapter$Instantiation, linkedList, insnList, arrayList, arrayList2);
        }
        this.instructions = insnList;
        this.tryCatchBlocks = arrayList;
        this.localVariables = arrayList2;
    }

    private void emitSubroutine(JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation, List list, InsnList insnList, List list2, List list3) {
        LabelNode labelNode;
        Object object;
        AbstractInsnNode abstractInsnNode;
        Object object2 = null;
        int n = this.instructions.size();
        for (int i = 0; i < n; ++i) {
            Object object3;
            abstractInsnNode = this.instructions.get(i);
            object = jSRInlinerAdapter$Instantiation.findOwner(i);
            if (abstractInsnNode.getType() == 8) {
                labelNode = (LabelNode)abstractInsnNode;
                object3 = jSRInlinerAdapter$Instantiation.rangeLabel(labelNode);
                if (object3 == object2) continue;
                insnList.add((AbstractInsnNode)object3);
                object2 = object3;
                continue;
            }
            if (object != jSRInlinerAdapter$Instantiation) continue;
            if (abstractInsnNode.getOpcode() == 169) {
                labelNode = null;
                object3 = jSRInlinerAdapter$Instantiation;
                while (object3 != null) {
                    if (object3.subroutine.get(i)) {
                        labelNode = object3.returnLabel;
                    }
                    object3 = object3.previous;
                }
                if (labelNode == null) {
                    throw new RuntimeException("Instruction #" + i + " is a RET not owned by any subroutine");
                }
                insnList.add(new JumpInsnNode(167, labelNode));
                continue;
            }
            if (abstractInsnNode.getOpcode() == 168) {
                labelNode = ((JumpInsnNode)abstractInsnNode).label;
                object3 = (BitSet)this.subroutineHeads.get(labelNode);
                JSRInlinerAdapter$Instantiation jSRInlinerAdapter$Instantiation2 = new JSRInlinerAdapter$Instantiation(this, jSRInlinerAdapter$Instantiation, (BitSet)object3);
                LabelNode labelNode2 = jSRInlinerAdapter$Instantiation2.gotoLabel(labelNode);
                insnList.add(new InsnNode(1));
                insnList.add(new JumpInsnNode(167, labelNode2));
                insnList.add(jSRInlinerAdapter$Instantiation2.returnLabel);
                list.add(jSRInlinerAdapter$Instantiation2);
                continue;
            }
            insnList.add(abstractInsnNode.clone(jSRInlinerAdapter$Instantiation));
        }
        Iterator iterator = this.tryCatchBlocks.iterator();
        while (iterator.hasNext()) {
            TryCatchBlockNode tryCatchBlockNode = (TryCatchBlockNode)iterator.next();
            abstractInsnNode = jSRInlinerAdapter$Instantiation.rangeLabel(tryCatchBlockNode.start);
            if (abstractInsnNode == (object = jSRInlinerAdapter$Instantiation.rangeLabel(tryCatchBlockNode.end))) continue;
            labelNode = jSRInlinerAdapter$Instantiation.gotoLabel(tryCatchBlockNode.handler);
            if (abstractInsnNode == null || object == null || labelNode == null) {
                throw new RuntimeException("Internal error!");
            }
            list2.add(new TryCatchBlockNode((LabelNode)abstractInsnNode, (LabelNode)object, labelNode, tryCatchBlockNode.type));
        }
        iterator = this.localVariables.iterator();
        while (iterator.hasNext()) {
            LocalVariableNode localVariableNode = (LocalVariableNode)iterator.next();
            abstractInsnNode = jSRInlinerAdapter$Instantiation.rangeLabel(localVariableNode.start);
            if (abstractInsnNode == (object = jSRInlinerAdapter$Instantiation.rangeLabel(localVariableNode.end))) continue;
            list3.add(new LocalVariableNode(localVariableNode.name, localVariableNode.desc, localVariableNode.signature, (LabelNode)abstractInsnNode, (LabelNode)object, localVariableNode.index));
        }
    }

    private static void log(String string) {
        System.err.println(string);
    }
}

