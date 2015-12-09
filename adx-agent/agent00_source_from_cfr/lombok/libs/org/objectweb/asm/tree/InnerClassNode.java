/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import lombok.libs.org.objectweb.asm.ClassVisitor;

public class InnerClassNode {
    public String name;
    public String outerName;
    public String innerName;
    public int access;

    public InnerClassNode(String string, String string2, String string3, int n) {
        this.name = string;
        this.outerName = string2;
        this.innerName = string3;
        this.access = n;
    }

    public void accept(ClassVisitor classVisitor) {
        classVisitor.visitInnerClass(this.name, this.outerName, this.innerName, this.access);
    }
}

