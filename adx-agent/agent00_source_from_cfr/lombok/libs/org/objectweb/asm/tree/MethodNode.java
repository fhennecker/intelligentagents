/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.Attribute;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.Handle;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.Type;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;
import lombok.libs.org.objectweb.asm.tree.AnnotationNode;
import lombok.libs.org.objectweb.asm.tree.FieldInsnNode;
import lombok.libs.org.objectweb.asm.tree.FrameNode;
import lombok.libs.org.objectweb.asm.tree.IincInsnNode;
import lombok.libs.org.objectweb.asm.tree.InsnList;
import lombok.libs.org.objectweb.asm.tree.InsnNode;
import lombok.libs.org.objectweb.asm.tree.IntInsnNode;
import lombok.libs.org.objectweb.asm.tree.InvokeDynamicInsnNode;
import lombok.libs.org.objectweb.asm.tree.JumpInsnNode;
import lombok.libs.org.objectweb.asm.tree.LabelNode;
import lombok.libs.org.objectweb.asm.tree.LdcInsnNode;
import lombok.libs.org.objectweb.asm.tree.LineNumberNode;
import lombok.libs.org.objectweb.asm.tree.LocalVariableNode;
import lombok.libs.org.objectweb.asm.tree.LookupSwitchInsnNode;
import lombok.libs.org.objectweb.asm.tree.MethodInsnNode;
import lombok.libs.org.objectweb.asm.tree.MethodNode$1;
import lombok.libs.org.objectweb.asm.tree.MultiANewArrayInsnNode;
import lombok.libs.org.objectweb.asm.tree.TableSwitchInsnNode;
import lombok.libs.org.objectweb.asm.tree.TryCatchBlockNode;
import lombok.libs.org.objectweb.asm.tree.TypeInsnNode;
import lombok.libs.org.objectweb.asm.tree.VarInsnNode;

public class MethodNode
extends MethodVisitor {
    public int access;
    public String name;
    public String desc;
    public String signature;
    public List exceptions;
    public List visibleAnnotations;
    public List invisibleAnnotations;
    public List attrs;
    public Object annotationDefault;
    public List[] visibleParameterAnnotations;
    public List[] invisibleParameterAnnotations;
    public InsnList instructions;
    public List tryCatchBlocks;
    public int maxStack;
    public int maxLocals;
    public List localVariables;
    private boolean visited;

    public MethodNode() {
        this(262144);
    }

    public MethodNode(int n) {
        super(n);
        this.instructions = new InsnList();
    }

    public MethodNode(int n, String string, String string2, String string3, String[] arrstring) {
        this(262144, n, string, string2, string3, arrstring);
    }

    public MethodNode(int n, int n2, String string, String string2, String string3, String[] arrstring) {
        boolean bl;
        super(n);
        this.access = n2;
        this.name = string;
        this.desc = string2;
        this.signature = string3;
        this.exceptions = new ArrayList(arrstring == null ? 0 : arrstring.length);
        boolean bl2 = bl = (n2 & 1024) != 0;
        if (!bl) {
            this.localVariables = new ArrayList(5);
        }
        this.tryCatchBlocks = new ArrayList();
        if (arrstring != null) {
            this.exceptions.addAll(Arrays.asList(arrstring));
        }
        this.instructions = new InsnList();
    }

    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationNode(new MethodNode$1(this, 0));
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        AnnotationNode annotationNode = new AnnotationNode(string);
        if (bl) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = new ArrayList(1);
            }
            this.visibleAnnotations.add(annotationNode);
        } else {
            if (this.invisibleAnnotations == null) {
                this.invisibleAnnotations = new ArrayList(1);
            }
            this.invisibleAnnotations.add(annotationNode);
        }
        return annotationNode;
    }

    public AnnotationVisitor visitParameterAnnotation(int n, String string, boolean bl) {
        AnnotationNode annotationNode = new AnnotationNode(string);
        if (bl) {
            if (this.visibleParameterAnnotations == null) {
                int n2 = Type.getArgumentTypes(this.desc).length;
                this.visibleParameterAnnotations = new List[n2];
            }
            if (this.visibleParameterAnnotations[n] == null) {
                this.visibleParameterAnnotations[n] = new ArrayList(1);
            }
            this.visibleParameterAnnotations[n].add(annotationNode);
        } else {
            if (this.invisibleParameterAnnotations == null) {
                int n3 = Type.getArgumentTypes(this.desc).length;
                this.invisibleParameterAnnotations = new List[n3];
            }
            if (this.invisibleParameterAnnotations[n] == null) {
                this.invisibleParameterAnnotations[n] = new ArrayList(1);
            }
            this.invisibleParameterAnnotations[n].add(annotationNode);
        }
        return annotationNode;
    }

    public void visitAttribute(Attribute attribute) {
        if (this.attrs == null) {
            this.attrs = new ArrayList(1);
        }
        this.attrs.add(attribute);
    }

    public void visitCode() {
    }

    public void visitFrame(int n, int n2, Object[] arrobject, int n3, Object[] arrobject2) {
        this.instructions.add(new FrameNode(n, n2, arrobject == null ? null : this.getLabelNodes(arrobject), n3, arrobject2 == null ? null : this.getLabelNodes(arrobject2)));
    }

    public void visitInsn(int n) {
        this.instructions.add(new InsnNode(n));
    }

    public void visitIntInsn(int n, int n2) {
        this.instructions.add(new IntInsnNode(n, n2));
    }

    public void visitVarInsn(int n, int n2) {
        this.instructions.add(new VarInsnNode(n, n2));
    }

    public void visitTypeInsn(int n, String string) {
        this.instructions.add(new TypeInsnNode(n, string));
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        this.instructions.add(new FieldInsnNode(n, string, string2, string3));
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        this.instructions.add(new MethodInsnNode(n, string, string2, string3));
    }

    public /* varargs */ void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... arrobject) {
        this.instructions.add(new InvokeDynamicInsnNode(string, string2, handle, arrobject));
    }

    public void visitJumpInsn(int n, Label label) {
        this.instructions.add(new JumpInsnNode(n, this.getLabelNode(label)));
    }

    public void visitLabel(Label label) {
        this.instructions.add(this.getLabelNode(label));
    }

    public void visitLdcInsn(Object object) {
        this.instructions.add(new LdcInsnNode(object));
    }

    public void visitIincInsn(int n, int n2) {
        this.instructions.add(new IincInsnNode(n, n2));
    }

    public /* varargs */ void visitTableSwitchInsn(int n, int n2, Label label, Label ... arrlabel) {
        this.instructions.add(new TableSwitchInsnNode(n, n2, this.getLabelNode(label), this.getLabelNodes(arrlabel)));
    }

    public void visitLookupSwitchInsn(Label label, int[] arrn, Label[] arrlabel) {
        this.instructions.add(new LookupSwitchInsnNode(this.getLabelNode(label), arrn, this.getLabelNodes(arrlabel)));
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        this.instructions.add(new MultiANewArrayInsnNode(string, n));
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        this.tryCatchBlocks.add(new TryCatchBlockNode(this.getLabelNode(label), this.getLabelNode(label2), this.getLabelNode(label3), string));
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n) {
        this.localVariables.add(new LocalVariableNode(string, string2, string3, this.getLabelNode(label), this.getLabelNode(label2), n));
    }

    public void visitLineNumber(int n, Label label) {
        this.instructions.add(new LineNumberNode(n, this.getLabelNode(label)));
    }

    public void visitMaxs(int n, int n2) {
        this.maxStack = n;
        this.maxLocals = n2;
    }

    public void visitEnd() {
    }

    protected LabelNode getLabelNode(Label label) {
        if (!(label.info instanceof LabelNode)) {
            label.info = new LabelNode(label);
        }
        return (LabelNode)label.info;
    }

    private LabelNode[] getLabelNodes(Label[] arrlabel) {
        LabelNode[] arrlabelNode = new LabelNode[arrlabel.length];
        for (int i = 0; i < arrlabel.length; ++i) {
            arrlabelNode[i] = this.getLabelNode(arrlabel[i]);
        }
        return arrlabelNode;
    }

    private Object[] getLabelNodes(Object[] arrobject) {
        Object[] arrobject2 = new Object[arrobject.length];
        for (int i = 0; i < arrobject.length; ++i) {
            Object object = arrobject[i];
            if (object instanceof Label) {
                object = this.getLabelNode((Label)object);
            }
            arrobject2[i] = object;
        }
        return arrobject2;
    }

    public void check(int n) {
    }

    public void accept(ClassVisitor classVisitor) {
        String[] arrstring = new String[this.exceptions.size()];
        this.exceptions.toArray(arrstring);
        MethodVisitor methodVisitor = classVisitor.visitMethod(this.access, this.name, this.desc, this.signature, arrstring);
        if (methodVisitor != null) {
            this.accept(methodVisitor);
        }
    }

    public void accept(MethodVisitor methodVisitor) {
        int n;
        AnnotationNode annotationNode;
        int n2;
        Object object;
        if (this.annotationDefault != null) {
            object = methodVisitor.visitAnnotationDefault();
            AnnotationNode.accept((AnnotationVisitor)object, null, this.annotationDefault);
            if (object != null) {
                object.visitEnd();
            }
        }
        int n3 = this.visibleAnnotations == null ? 0 : this.visibleAnnotations.size();
        for (n2 = 0; n2 < n3; ++n2) {
            object = (AnnotationNode)this.visibleAnnotations.get(n2);
            object.accept(methodVisitor.visitAnnotation(object.desc, true));
        }
        n3 = this.invisibleAnnotations == null ? 0 : this.invisibleAnnotations.size();
        for (n2 = 0; n2 < n3; ++n2) {
            object = (AnnotationNode)this.invisibleAnnotations.get(n2);
            object.accept(methodVisitor.visitAnnotation(object.desc, false));
        }
        n3 = this.visibleParameterAnnotations == null ? 0 : this.visibleParameterAnnotations.length;
        for (n2 = 0; n2 < n3; ++n2) {
            object = this.visibleParameterAnnotations[n2];
            if (object == null) continue;
            for (n = 0; n < object.size(); ++n) {
                annotationNode = (AnnotationNode)object.get(n);
                annotationNode.accept(methodVisitor.visitParameterAnnotation(n2, annotationNode.desc, true));
            }
        }
        n3 = this.invisibleParameterAnnotations == null ? 0 : this.invisibleParameterAnnotations.length;
        for (n2 = 0; n2 < n3; ++n2) {
            object = this.invisibleParameterAnnotations[n2];
            if (object == null) continue;
            for (n = 0; n < object.size(); ++n) {
                annotationNode = (AnnotationNode)object.get(n);
                annotationNode.accept(methodVisitor.visitParameterAnnotation(n2, annotationNode.desc, false));
            }
        }
        if (this.visited) {
            this.instructions.resetLabels();
        }
        n3 = this.attrs == null ? 0 : this.attrs.size();
        for (n2 = 0; n2 < n3; ++n2) {
            methodVisitor.visitAttribute((Attribute)this.attrs.get(n2));
        }
        if (this.instructions.size() > 0) {
            methodVisitor.visitCode();
            n3 = this.tryCatchBlocks == null ? 0 : this.tryCatchBlocks.size();
            for (n2 = 0; n2 < n3; ++n2) {
                ((TryCatchBlockNode)this.tryCatchBlocks.get(n2)).accept(methodVisitor);
            }
            this.instructions.accept(methodVisitor);
            n3 = this.localVariables == null ? 0 : this.localVariables.size();
            for (n2 = 0; n2 < n3; ++n2) {
                ((LocalVariableNode)this.localVariables.get(n2)).accept(methodVisitor);
            }
            methodVisitor.visitMaxs(this.maxStack, this.maxLocals);
            this.visited = true;
        }
        methodVisitor.visitEnd();
    }
}

