/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 */
package lombok.eclipse.handlers.replace;

import lombok.ast.Statement;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.replace.ExpressionReplaceVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

public class ThisReferenceReplaceVisitor
extends ExpressionReplaceVisitor {
    public ThisReferenceReplaceVisitor(EclipseMethod method, Statement<?> replacement) {
        super(method, replacement);
    }

    @Override
    protected boolean needsReplacing(Expression node) {
        return node instanceof ThisReference && !((ThisReference)node).isImplicitThis();
    }
}

