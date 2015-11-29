/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

class SerialVersionUIDAdder$Item
implements Comparable {
    final String name;
    final int access;
    final String desc;

    SerialVersionUIDAdder$Item(String string, int n, String string2) {
        this.name = string;
        this.access = n;
        this.desc = string2;
    }

    public int compareTo(SerialVersionUIDAdder$Item serialVersionUIDAdder$Item) {
        int n = this.name.compareTo(serialVersionUIDAdder$Item.name);
        if (n == 0) {
            n = this.desc.compareTo(serialVersionUIDAdder$Item.desc);
        }
        return n;
    }

    public boolean equals(Object object) {
        if (object instanceof SerialVersionUIDAdder$Item) {
            return this.compareTo((SerialVersionUIDAdder$Item)object) == 0;
        }
        return false;
    }

    public int hashCode() {
        return (this.name + this.desc).hashCode();
    }
}

