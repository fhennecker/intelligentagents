/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.ForStatement
 *  org.eclipse.jdt.internal.compiler.ast.ForeachStatement
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse.handlers;

import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.val;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

@DeferUntilPostDiet
public class HandleVal
extends EclipseASTAdapter {
    public void visitLocal(EclipseNode localNode, LocalDeclaration local) {
        if (!EclipseHandlerUtil.typeMatches(val.class, localNode, local.type)) {
            return;
        }
        boolean variableOfForEach = false;
        if (((EclipseNode)localNode.directUp()).get() instanceof ForeachStatement) {
            ForeachStatement fs = (ForeachStatement)((EclipseNode)localNode.directUp()).get();
            boolean bl = variableOfForEach = fs.elementVariable == local;
        }
        if (local.initialization == null && !variableOfForEach) {
            localNode.addError("'val' on a local variable requires an initializer expression");
            return;
        }
        if (local.initialization instanceof ArrayInitializer) {
            localNode.addError("'val' is not compatible with array initializer expressions. Use the full form (new int[] { ... } instead of just { ... })");
            return;
        }
        if (((EclipseNode)localNode.directUp()).get() instanceof ForStatement) {
            localNode.addError("'val' is not allowed in old-style for loops");
            return;
        }
    }
}

