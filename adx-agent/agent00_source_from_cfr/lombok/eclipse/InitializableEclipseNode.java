/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 */
package lombok.eclipse;

import java.util.List;
import lombok.core.AST;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseNode;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

public class InitializableEclipseNode
extends EclipseNode {
    public InitializableEclipseNode(EclipseAST ast, ASTNode node, List<EclipseNode> children, AST.Kind kind) {
        super(ast, node, children, kind);
    }
}

