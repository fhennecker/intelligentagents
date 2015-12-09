/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.TreeVisitor
 *  com.sun.source.util.TreeScanner
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 */
package lombok.javac.handlers.replace;

import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.beans.ConstructorProperties;
import java.util.Iterator;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacMethodEditor;

public abstract class ReplaceVisitor<NODE_TYPE extends JCTree>
extends TreeScanner<Void, Void> {
    private final JavacMethod method;
    private final Statement<?> replacement;

    public void visit(JCTree node) {
        node.accept((TreeVisitor)this, (Object)null);
    }

    protected final List<NODE_TYPE> replace(List<NODE_TYPE> nodes) {
        ListBuffer newNodes = ListBuffer.lb();
        Iterator i$ = nodes.iterator();
        while (i$.hasNext()) {
            Object node = (JCTree)i$.next();
            if (this.needsReplacing(node)) {
                node = this.method.editor().build(this.replacement);
            }
            newNodes.append(node);
        }
        return newNodes.toList();
    }

    protected final NODE_TYPE replace(NODE_TYPE node) {
        if (node != null && this.needsReplacing(node)) {
            return (NODE_TYPE)this.method.editor().build(this.replacement);
        }
        return node;
    }

    protected abstract boolean needsReplacing(NODE_TYPE var1);

    @ConstructorProperties(value={"method", "replacement"})
    protected ReplaceVisitor(JavacMethod method, Statement<?> replacement) {
        this.method = method;
        this.replacement = replacement;
    }
}

