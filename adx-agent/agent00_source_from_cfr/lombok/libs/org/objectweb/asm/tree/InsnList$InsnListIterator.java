/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import lombok.libs.org.objectweb.asm.tree.AbstractInsnNode;
import lombok.libs.org.objectweb.asm.tree.InsnList;

final class InsnList$InsnListIterator
implements ListIterator {
    AbstractInsnNode next;
    AbstractInsnNode prev;
    final /* synthetic */ InsnList this$0;

    InsnList$InsnListIterator(InsnList insnList, int n) {
        this.this$0 = insnList;
        if (n == insnList.size()) {
            this.next = null;
            this.prev = insnList.getLast();
        } else {
            this.next = insnList.get(n);
            this.prev = this.next.prev;
        }
    }

    public boolean hasNext() {
        return this.next != null;
    }

    public Object next() {
        AbstractInsnNode abstractInsnNode;
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        this.prev = abstractInsnNode = this.next;
        this.next = abstractInsnNode.next;
        return abstractInsnNode;
    }

    public void remove() {
        this.this$0.remove(this.prev);
        this.prev = this.prev.prev;
    }

    public boolean hasPrevious() {
        return this.prev != null;
    }

    public Object previous() {
        AbstractInsnNode abstractInsnNode;
        this.next = abstractInsnNode = this.prev;
        this.prev = abstractInsnNode.prev;
        return abstractInsnNode;
    }

    public int nextIndex() {
        if (this.next == null) {
            return this.this$0.size();
        }
        if (this.this$0.cache == null) {
            this.this$0.cache = this.this$0.toArray();
        }
        return this.next.index;
    }

    public int previousIndex() {
        if (this.prev == null) {
            return -1;
        }
        if (this.this$0.cache == null) {
            this.this$0.cache = this.this$0.toArray();
        }
        return this.prev.index;
    }

    public void add(Object object) {
        this.this$0.insertBefore(this.next, (AbstractInsnNode)object);
        this.prev = (AbstractInsnNode)object;
    }

    public void set(Object object) {
        this.this$0.set(this.next.prev, (AbstractInsnNode)object);
        this.prev = (AbstractInsnNode)object;
    }
}

