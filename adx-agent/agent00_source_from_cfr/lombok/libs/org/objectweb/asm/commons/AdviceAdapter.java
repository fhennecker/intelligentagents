/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.libs.org.objectweb.asm.Handle;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.Opcodes;
import lombok.libs.org.objectweb.asm.Type;
import lombok.libs.org.objectweb.asm.commons.GeneratorAdapter;

public abstract class AdviceAdapter
extends GeneratorAdapter
implements Opcodes {
    private static final Object THIS = new Object();
    private static final Object OTHER = new Object();
    protected int methodAccess;
    protected String methodDesc;
    private boolean constructor;
    private boolean superInitialized;
    private List stackFrame;
    private Map branches;

    protected AdviceAdapter(int n, MethodVisitor methodVisitor, int n2, String string, String string2) {
        super(n, methodVisitor, n2, string, string2);
        this.methodAccess = n2;
        this.methodDesc = string2;
        this.constructor = "<init>".equals(string);
    }

    public void visitCode() {
        this.mv.visitCode();
        if (this.constructor) {
            this.stackFrame = new ArrayList();
            this.branches = new HashMap();
        } else {
            this.superInitialized = true;
            this.onMethodEnter();
        }
    }

    public void visitLabel(Label label) {
        List list;
        this.mv.visitLabel(label);
        if (this.constructor && this.branches != null && (list = (List)this.branches.get(label)) != null) {
            this.stackFrame = list;
            this.branches.remove(label);
        }
    }

    public void visitInsn(int n) {
        if (this.constructor) {
            switch (n) {
                case 177: {
                    this.onMethodExit(n);
                    break;
                }
                case 172: 
                case 174: 
                case 176: 
                case 191: {
                    this.popValue();
                    this.onMethodExit(n);
                    break;
                }
                case 173: 
                case 175: {
                    this.popValue();
                    this.popValue();
                    this.onMethodExit(n);
                    break;
                }
                case 0: 
                case 47: 
                case 49: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 134: 
                case 138: 
                case 139: 
                case 143: 
                case 145: 
                case 146: 
                case 147: 
                case 190: {
                    break;
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 11: 
                case 12: 
                case 13: 
                case 133: 
                case 135: 
                case 140: 
                case 141: {
                    this.pushValue(OTHER);
                    break;
                }
                case 9: 
                case 10: 
                case 14: 
                case 15: {
                    this.pushValue(OTHER);
                    this.pushValue(OTHER);
                    break;
                }
                case 46: 
                case 48: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 87: 
                case 96: 
                case 98: 
                case 100: 
                case 102: 
                case 104: 
                case 106: 
                case 108: 
                case 110: 
                case 112: 
                case 114: 
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 128: 
                case 130: 
                case 136: 
                case 137: 
                case 142: 
                case 144: 
                case 149: 
                case 150: 
                case 194: 
                case 195: {
                    this.popValue();
                    break;
                }
                case 88: 
                case 97: 
                case 99: 
                case 101: 
                case 103: 
                case 105: 
                case 107: 
                case 109: 
                case 111: 
                case 113: 
                case 115: 
                case 127: 
                case 129: 
                case 131: {
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 79: 
                case 81: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 148: 
                case 151: 
                case 152: {
                    this.popValue();
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 80: 
                case 82: {
                    this.popValue();
                    this.popValue();
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 89: {
                    this.pushValue(this.peekValue());
                    break;
                }
                case 90: {
                    int n2 = this.stackFrame.size();
                    this.stackFrame.add(n2 - 2, this.stackFrame.get(n2 - 1));
                    break;
                }
                case 91: {
                    int n3 = this.stackFrame.size();
                    this.stackFrame.add(n3 - 3, this.stackFrame.get(n3 - 1));
                    break;
                }
                case 92: {
                    int n4 = this.stackFrame.size();
                    this.stackFrame.add(n4 - 2, this.stackFrame.get(n4 - 1));
                    this.stackFrame.add(n4 - 2, this.stackFrame.get(n4 - 1));
                    break;
                }
                case 93: {
                    int n5 = this.stackFrame.size();
                    this.stackFrame.add(n5 - 3, this.stackFrame.get(n5 - 1));
                    this.stackFrame.add(n5 - 3, this.stackFrame.get(n5 - 1));
                    break;
                }
                case 94: {
                    int n6 = this.stackFrame.size();
                    this.stackFrame.add(n6 - 4, this.stackFrame.get(n6 - 1));
                    this.stackFrame.add(n6 - 4, this.stackFrame.get(n6 - 1));
                    break;
                }
                case 95: {
                    int n7 = this.stackFrame.size();
                    this.stackFrame.add(n7 - 2, this.stackFrame.get(n7 - 1));
                    this.stackFrame.remove(n7);
                }
            }
        } else {
            switch (n) {
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 191: {
                    this.onMethodExit(n);
                }
            }
        }
        this.mv.visitInsn(n);
    }

    public void visitVarInsn(int n, int n2) {
        super.visitVarInsn(n, n2);
        if (this.constructor) {
            switch (n) {
                case 21: 
                case 23: {
                    this.pushValue(OTHER);
                    break;
                }
                case 22: 
                case 24: {
                    this.pushValue(OTHER);
                    this.pushValue(OTHER);
                    break;
                }
                case 25: {
                    this.pushValue(n2 == 0 ? THIS : OTHER);
                    break;
                }
                case 54: 
                case 56: 
                case 58: {
                    this.popValue();
                    break;
                }
                case 55: 
                case 57: {
                    this.popValue();
                    this.popValue();
                }
            }
        }
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        this.mv.visitFieldInsn(n, string, string2, string3);
        if (this.constructor) {
            char c = string3.charAt(0);
            boolean bl = c == 'J' || c == 'D';
            switch (n) {
                case 178: {
                    this.pushValue(OTHER);
                    if (!bl) break;
                    this.pushValue(OTHER);
                    break;
                }
                case 179: {
                    this.popValue();
                    if (!bl) break;
                    this.popValue();
                    break;
                }
                case 181: {
                    this.popValue();
                    if (!bl) break;
                    this.popValue();
                    this.popValue();
                    break;
                }
                default: {
                    if (!bl) break;
                    this.pushValue(OTHER);
                }
            }
        }
    }

    public void visitIntInsn(int n, int n2) {
        this.mv.visitIntInsn(n, n2);
        if (this.constructor && n != 188) {
            this.pushValue(OTHER);
        }
    }

    public void visitLdcInsn(Object object) {
        this.mv.visitLdcInsn(object);
        if (this.constructor) {
            this.pushValue(OTHER);
            if (object instanceof Double || object instanceof Long) {
                this.pushValue(OTHER);
            }
        }
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        this.mv.visitMultiANewArrayInsn(string, n);
        if (this.constructor) {
            for (int i = 0; i < n; ++i) {
                this.popValue();
            }
            this.pushValue(OTHER);
        }
    }

    public void visitTypeInsn(int n, String string) {
        this.mv.visitTypeInsn(n, string);
        if (this.constructor && n == 187) {
            this.pushValue(OTHER);
        }
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        this.mv.visitMethodInsn(n, string, string2, string3);
        if (this.constructor) {
            Type[] arrtype = Type.getArgumentTypes(string3);
            for (int i = 0; i < arrtype.length; ++i) {
                this.popValue();
                if (arrtype[i].getSize() != 2) continue;
                this.popValue();
            }
            switch (n) {
                case 182: 
                case 185: {
                    this.popValue();
                    break;
                }
                case 183: {
                    Object object = this.popValue();
                    if (object != THIS || this.superInitialized) break;
                    this.onMethodEnter();
                    this.superInitialized = true;
                    this.constructor = false;
                }
            }
            Type type = Type.getReturnType(string3);
            if (type != Type.VOID_TYPE) {
                this.pushValue(OTHER);
                if (type.getSize() == 2) {
                    this.pushValue(OTHER);
                }
            }
        }
    }

    public /* varargs */ void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... arrobject) {
        this.mv.visitInvokeDynamicInsn(string, string2, handle, arrobject);
        if (this.constructor) {
            Type[] arrtype = Type.getArgumentTypes(string2);
            for (int i = 0; i < arrtype.length; ++i) {
                this.popValue();
                if (arrtype[i].getSize() != 2) continue;
                this.popValue();
            }
            Type type = Type.getReturnType(string2);
            if (type != Type.VOID_TYPE) {
                this.pushValue(OTHER);
                if (type.getSize() == 2) {
                    this.pushValue(OTHER);
                }
            }
        }
    }

    public void visitJumpInsn(int n, Label label) {
        this.mv.visitJumpInsn(n, label);
        if (this.constructor) {
            switch (n) {
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 198: 
                case 199: {
                    this.popValue();
                    break;
                }
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: {
                    this.popValue();
                    this.popValue();
                    break;
                }
                case 168: {
                    this.pushValue(OTHER);
                }
            }
            this.addBranch(label);
        }
    }

    public void visitLookupSwitchInsn(Label label, int[] arrn, Label[] arrlabel) {
        this.mv.visitLookupSwitchInsn(label, arrn, arrlabel);
        if (this.constructor) {
            this.popValue();
            this.addBranches(label, arrlabel);
        }
    }

    public /* varargs */ void visitTableSwitchInsn(int n, int n2, Label label, Label ... arrlabel) {
        this.mv.visitTableSwitchInsn(n, n2, label, arrlabel);
        if (this.constructor) {
            this.popValue();
            this.addBranches(label, arrlabel);
        }
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        super.visitTryCatchBlock(label, label2, label3, string);
        if (this.constructor && !this.branches.containsKey(label3)) {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add(OTHER);
            this.branches.put(label3, arrayList);
        }
    }

    private void addBranches(Label label, Label[] arrlabel) {
        this.addBranch(label);
        for (int i = 0; i < arrlabel.length; ++i) {
            this.addBranch(arrlabel[i]);
        }
    }

    private void addBranch(Label label) {
        if (this.branches.containsKey(label)) {
            return;
        }
        this.branches.put(label, new ArrayList(this.stackFrame));
    }

    private Object popValue() {
        return this.stackFrame.remove(this.stackFrame.size() - 1);
    }

    private Object peekValue() {
        return this.stackFrame.get(this.stackFrame.size() - 1);
    }

    private void pushValue(Object object) {
        this.stackFrame.add(object);
    }

    protected void onMethodEnter() {
    }

    protected void onMethodExit(int n) {
    }
}

