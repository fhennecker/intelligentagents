/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.List;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.commons.TryCatchBlockSorter$1;
import lombok.libs.org.objectweb.asm.tree.MethodNode;

public class TryCatchBlockSorter
extends MethodNode {
    public TryCatchBlockSorter(MethodVisitor methodVisitor, int n, String string, String string2, String string3, String[] arrstring) {
        this(262144, methodVisitor, n, string, string2, string3, arrstring);
    }

    protected TryCatchBlockSorter(int n, MethodVisitor methodVisitor, int n2, String string, String string2, String string3, String[] arrstring) {
        super(n, n2, string, string2, string3, arrstring);
        this.mv = methodVisitor;
    }

    public void visitEnd() {
        TryCatchBlockSorter$1 tryCatchBlockSorter$1 = new TryCatchBlockSorter$1(this);
        Collections.sort(this.tryCatchBlocks, tryCatchBlockSorter$1);
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }
}

