/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.Opcodes;
import lombok.libs.org.objectweb.asm.Type;

public class LocalVariablesSorter
extends MethodVisitor {
    private static final Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");
    private int[] mapping = new int[40];
    private Object[] newLocals = new Object[20];
    protected final int firstLocal;
    protected int nextLocal;
    private boolean changed;

    public LocalVariablesSorter(int n, String string, MethodVisitor methodVisitor) {
        this(262144, n, string, methodVisitor);
    }

    protected LocalVariablesSorter(int n, int n2, String string, MethodVisitor methodVisitor) {
        super(n, methodVisitor);
        Type[] arrtype = Type.getArgumentTypes(string);
        this.nextLocal = (8 & n2) == 0 ? 1 : 0;
        for (int i = 0; i < arrtype.length; ++i) {
            this.nextLocal += arrtype[i].getSize();
        }
        this.firstLocal = this.nextLocal;
    }

    public void visitVarInsn(int n, int n2) {
        Type type;
        switch (n) {
            case 22: 
            case 55: {
                type = Type.LONG_TYPE;
                break;
            }
            case 24: 
            case 57: {
                type = Type.DOUBLE_TYPE;
                break;
            }
            case 23: 
            case 56: {
                type = Type.FLOAT_TYPE;
                break;
            }
            case 21: 
            case 54: {
                type = Type.INT_TYPE;
                break;
            }
            default: {
                type = OBJECT_TYPE;
            }
        }
        this.mv.visitVarInsn(n, this.remap(n2, type));
    }

    public void visitIincInsn(int n, int n2) {
        this.mv.visitIincInsn(this.remap(n, Type.INT_TYPE), n2);
    }

    public void visitMaxs(int n, int n2) {
        this.mv.visitMaxs(n, this.nextLocal);
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n) {
        int n2 = this.remap(n, Type.getType(string2));
        this.mv.visitLocalVariable(string, string2, string3, label, label2, n2);
    }

    public void visitFrame(int n, int n2, Object[] arrobject, int n3, Object[] arrobject2) {
        int n4;
        if (n != -1) {
            throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
        }
        if (!this.changed) {
            this.mv.visitFrame(n, n2, arrobject, n3, arrobject2);
            return;
        }
        Object[] arrobject3 = new Object[this.newLocals.length];
        System.arraycopy(this.newLocals, 0, arrobject3, 0, arrobject3.length);
        int n5 = 0;
        for (n4 = 0; n4 < n2; ++n4) {
            int n6;
            Object object = arrobject[n4];
            int n7 = n6 = object == Opcodes.LONG || object == Opcodes.DOUBLE ? 2 : 1;
            if (object != Opcodes.TOP) {
                Type type = OBJECT_TYPE;
                if (object == Opcodes.INTEGER) {
                    type = Type.INT_TYPE;
                } else if (object == Opcodes.FLOAT) {
                    type = Type.FLOAT_TYPE;
                } else if (object == Opcodes.LONG) {
                    type = Type.LONG_TYPE;
                } else if (object == Opcodes.DOUBLE) {
                    type = Type.DOUBLE_TYPE;
                } else if (object instanceof String) {
                    type = Type.getObjectType((String)object);
                }
                this.setFrameLocal(this.remap(n5, type), object);
            }
            n5 += n6;
        }
        n5 = 0;
        n4 = 0;
        int n8 = 0;
        while (n5 < this.newLocals.length) {
            Object object;
            if ((object = this.newLocals[n5++]) != null && object != Opcodes.TOP) {
                this.newLocals[n8] = object;
                n4 = n8 + 1;
                if (object == Opcodes.LONG || object == Opcodes.DOUBLE) {
                    ++n5;
                }
            } else {
                this.newLocals[n8] = Opcodes.TOP;
            }
            ++n8;
        }
        this.mv.visitFrame(n, n4, this.newLocals, n3, arrobject2);
        this.newLocals = arrobject3;
    }

    public int newLocal(Type type) {
        Object object;
        switch (type.getSort()) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                object = Opcodes.INTEGER;
                break;
            }
            case 6: {
                object = Opcodes.FLOAT;
                break;
            }
            case 7: {
                object = Opcodes.LONG;
                break;
            }
            case 8: {
                object = Opcodes.DOUBLE;
                break;
            }
            case 9: {
                object = type.getDescriptor();
                break;
            }
            default: {
                object = type.getInternalName();
            }
        }
        int n = this.nextLocal;
        this.nextLocal += type.getSize();
        this.setLocalType(n, type);
        this.setFrameLocal(n, object);
        return n;
    }

    protected void setLocalType(int n, Type type) {
    }

    private void setFrameLocal(int n, Object object) {
        int n2 = this.newLocals.length;
        if (n >= n2) {
            Object[] arrobject = new Object[Math.max(2 * n2, n + 1)];
            System.arraycopy(this.newLocals, 0, arrobject, 0, n2);
            this.newLocals = arrobject;
        }
        this.newLocals[n] = object;
    }

    private int remap(int n, Type type) {
        int n2;
        int n3;
        if (n + type.getSize() <= this.firstLocal) {
            return n;
        }
        int n4 = 2 * n + type.getSize() - 1;
        if (n4 >= (n2 = this.mapping.length)) {
            int[] arrn = new int[Math.max(2 * n2, n4 + 1)];
            System.arraycopy(this.mapping, 0, arrn, 0, n2);
            this.mapping = arrn;
        }
        if ((n3 = this.mapping[n4]) == 0) {
            n3 = this.newLocalMapping(type);
            this.setLocalType(n3, type);
            this.mapping[n4] = n3 + 1;
        } else {
            --n3;
        }
        if (n3 != n) {
            this.changed = true;
        }
        return n3;
    }

    protected int newLocalMapping(Type type) {
        int n = this.nextLocal;
        this.nextLocal += type.getSize();
        return n;
    }
}

