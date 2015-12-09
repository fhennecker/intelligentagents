/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.LabeledStatementTree
 *  com.sun.source.tree.StatementTree
 *  com.sun.source.tree.TreeVisitor
 *  com.sun.source.tree.VariableTree
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$VarSymbol
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeCopier
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.List
 */
package lombok.javac;

import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeCopier;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

public class TreeMirrorMaker
extends TreeCopier<Void> {
    private final IdentityHashMap<JCTree, JCTree> originalToCopy = new IdentityHashMap();

    public TreeMirrorMaker(TreeMaker maker) {
        super(maker);
    }

    public <T extends JCTree> T copy(T original) {
        JCTree copy = super.copy(original);
        this.originalToCopy.put((JCTree)original, copy);
        return (T)copy;
    }

    public <T extends JCTree> T copy(T original, Void p) {
        JCTree copy = super.copy(original, (Object)p);
        this.originalToCopy.put((JCTree)original, copy);
        return (T)copy;
    }

    public <T extends JCTree> List<T> copy(List<T> originals) {
        List copies = super.copy(originals);
        if (originals != null) {
            Iterator it1 = originals.iterator();
            Iterator it2 = copies.iterator();
            while (it1.hasNext()) {
                this.originalToCopy.put((JCTree)it1.next(), (JCTree)it2.next());
            }
        }
        return copies;
    }

    public <T extends JCTree> List<T> copy(List<T> originals, Void p) {
        List copies = super.copy(originals, (Object)p);
        if (originals != null) {
            Iterator it1 = originals.iterator();
            Iterator it2 = copies.iterator();
            while (it1.hasNext()) {
                this.originalToCopy.put((JCTree)it1.next(), (JCTree)it2.next());
            }
        }
        return copies;
    }

    public Map<JCTree, JCTree> getOriginalToCopyMap() {
        return Collections.unmodifiableMap(this.originalToCopy);
    }

    public JCTree visitVariable(VariableTree node, Void p) {
        JCTree.JCVariableDecl copy = (JCTree.JCVariableDecl)super.visitVariable(node, (Object)p);
        copy.sym = ((JCTree.JCVariableDecl)node).sym;
        return copy;
    }

    public JCTree visitLabeledStatement(LabeledStatementTree node, Void p) {
        return (JCTree)node.getStatement().accept((TreeVisitor)this, (Object)p);
    }
}

