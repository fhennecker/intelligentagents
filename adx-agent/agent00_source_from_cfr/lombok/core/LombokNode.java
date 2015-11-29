/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import lombok.core.AST;
import lombok.core.DiagnosticsReceiver;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class LombokNode<A extends AST<A, L, N>, L extends LombokNode<A, L, N>, N>
implements DiagnosticsReceiver {
    protected final A ast;
    protected final AST.Kind kind;
    protected final N node;
    protected final List<L> children;
    protected L parent;
    protected boolean isStructurallySignificant;

    protected LombokNode(A ast, N node, List<L> children, AST.Kind kind) {
        this.ast = ast;
        this.kind = kind;
        this.node = node;
        this.children = children == null ? new ArrayList() : children;
        for (LombokNode child : this.children) {
            child.parent = this;
            if (child.isStructurallySignificant) continue;
            child.isStructurallySignificant = this.calculateIsStructurallySignificant(node);
        }
        this.isStructurallySignificant = this.calculateIsStructurallySignificant(null);
    }

    public A getAst() {
        return this.ast;
    }

    public String toString() {
        Object[] arrobject = new Object[3];
        arrobject[0] = this.kind;
        arrobject[1] = this.node == null ? "(NULL)" : this.node.getClass();
        arrobject[2] = this.node == null ? "" : this.node;
        return String.format("NODE %s (%s) %s", arrobject);
    }

    public String getPackageDeclaration() {
        return this.ast.getPackageDeclaration();
    }

    public Collection<String> getImportStatements() {
        return this.ast.getImportStatements();
    }

    protected abstract boolean calculateIsStructurallySignificant(N var1);

    public L getNodeFor(N obj) {
        return this.ast.get(obj);
    }

    public N get() {
        return this.node;
    }

    public L replaceWith(N newN, AST.Kind newNodeKind) {
        this.ast.setChanged();
        L newNode = this.ast.buildTree(newN, newNodeKind);
        newNode.parent = this.parent;
        for (int i = 0; i < this.parent.children.size(); ++i) {
            if (this.parent.children.get(i) != this) continue;
            this.parent.children.set(i, newNode);
        }
        this.parent.replaceChildNode(this.get(), newN);
        return newNode;
    }

    public void replaceChildNode(N oldN, N newN) {
        this.ast.setChanged();
        this.ast.replaceStatementInNode(this.get(), oldN, newN);
    }

    public AST.Kind getKind() {
        return this.kind;
    }

    public abstract String getName();

    public L up() {
        L result = this.parent;
        while (result != null && !result.isStructurallySignificant) {
            result = result.parent;
        }
        return result;
    }

    public Collection<L> upFromAnnotationToFields() {
        if (this.getKind() != AST.Kind.ANNOTATION) {
            return Collections.emptyList();
        }
        L field = this.up();
        if (field == null || field.getKind() != AST.Kind.FIELD) {
            return Collections.emptyList();
        }
        L type = field.up();
        if (type == null || type.getKind() != AST.Kind.TYPE) {
            return Collections.emptyList();
        }
        ArrayList<LombokNode> fields = new ArrayList<LombokNode>();
        for (LombokNode potentialField : type.down()) {
            if (potentialField.getKind() != AST.Kind.FIELD || !this.fieldContainsAnnotation(potentialField.get(), this.get())) continue;
            fields.add(potentialField);
        }
        return fields;
    }

    protected abstract boolean fieldContainsAnnotation(N var1, N var2);

    public L directUp() {
        return this.parent;
    }

    public Collection<L> down() {
        return new ArrayList<L>(this.children);
    }

    public L top() {
        return this.ast.top();
    }

    public String getFileName() {
        return this.ast.getFileName();
    }

    public L add(N newChild, AST.Kind newChildKind) {
        this.ast.setChanged();
        L n = this.ast.buildTree(newChild, newChildKind);
        if (n == null) {
            return null;
        }
        n.parent = this;
        this.children.add(n);
        return n;
    }

    public void rebuild() {
        IdentityHashMap<K, V> oldNodes = new IdentityHashMap<K, V>();
        this.gatherAndRemoveChildren(oldNodes);
        L newNode = this.ast.buildTree(this.get(), this.kind);
        this.ast.setChanged();
        this.ast.replaceNewWithExistingOld(oldNodes, newNode);
    }

    private void gatherAndRemoveChildren(Map<N, L> map) {
        for (LombokNode child : this.children) {
            child.gatherAndRemoveChildren(map);
        }
        this.ast.identityDetector.remove(this.get());
        map.put(this.get(), (LombokNode)this);
        this.children.clear();
        this.ast.getNodeMap().remove(this.get());
    }

    public void removeChild(L child) {
        this.ast.setChanged();
        this.children.remove(child);
    }

    public boolean isStructurallySignificant() {
        return this.isStructurallySignificant;
    }
}

