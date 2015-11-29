/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ASTVisitor
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 */
package lombok.eclipse.handlers.replace;

import java.beans.ConstructorProperties;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.core.util.Is;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.ast.EclipseMethodEditor;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;

public abstract class ReplaceVisitor<NODE_TYPE extends ASTNode>
extends ASTVisitor {
    private final EclipseMethod method;
    private final Statement<?> replacement;

    public void visit(ASTNode astNode) {
        if (astNode instanceof MethodDeclaration) {
            ((MethodDeclaration)astNode).traverse((ASTVisitor)this, (ClassScope)null);
        } else {
            astNode.traverse((ASTVisitor)this, null);
        }
    }

    protected final void replace(NODE_TYPE[] nodes) {
        if (Is.notEmpty(nodes)) {
            int iend = nodes.length;
            for (int i = 0; i < iend; ++i) {
                if (!this.needsReplacing(nodes[i])) continue;
                nodes[i] = this.method.editor().build(this.replacement);
            }
        }
    }

    protected final NODE_TYPE replace(NODE_TYPE node) {
        if (node != null && this.needsReplacing(node)) {
            return (NODE_TYPE)this.method.editor().build(this.replacement);
        }
        return node;
    }

    protected abstract boolean needsReplacing(NODE_TYPE var1);

    @ConstructorProperties(value={"method", "replacement"})
    protected ReplaceVisitor(EclipseMethod method, Statement<?> replacement) {
        this.method = method;
        this.replacement = replacement;
    }
}

