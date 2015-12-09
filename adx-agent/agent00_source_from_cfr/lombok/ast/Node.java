/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import lombok.ast.ASTPrinter;
import lombok.ast.ASTVisitor;
import lombok.core.util.Cast;

public abstract class Node<SELF_TYPE extends Node<SELF_TYPE>> {
    private Node<?> parent;
    private Object posHint;

    public final <T extends Node<?>> T child(T node) {
        if (node != null) {
            node.parent = this;
        }
        return node;
    }

    public final Node<?> up() {
        return this.parent;
    }

    public final <T extends Node<?>> T upTo(Class<T> type) {
        Node node;
        for (node = this; node != null && !type.isInstance(node); node = node.up()) {
        }
        return (T)((Node)type.cast(node));
    }

    protected final SELF_TYPE self() {
        return (SELF_TYPE)((Node)Cast.uncheckedCast(this));
    }

    public final SELF_TYPE posHint(Object posHint) {
        this.posHint = posHint;
        return this.self();
    }

    public final <T> T posHint() {
        Node node;
        for (node = this; node != null && node.posHint == null; node = node.up()) {
        }
        return node == null ? null : (T)Cast.uncheckedCast(node.posHint);
    }

    public String toString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        this.accept(new ASTPrinter(), new ASTPrinter.State(ps));
        return baos.toString();
    }

    public abstract <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> var1, PARAMETER_TYPE var2);
}

