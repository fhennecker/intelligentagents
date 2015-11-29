/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 */
package lombok.eclipse.agent;

import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.agent.Patches;
import lombok.eclipse.handlers.HandleYield;
import lombok.patcher.Hook;
import lombok.patcher.MethodTarget;
import lombok.patcher.PatchScript;
import lombok.patcher.ScriptManager;
import lombok.patcher.StackRequest;
import lombok.patcher.TargetMatcher;
import lombok.patcher.scripts.ExitFromMethodEarlyScript;
import lombok.patcher.scripts.ScriptBuilder;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;

public final class PatchYield {
    static void addPatches(ScriptManager sm, boolean ecj) {
        String HOOK_NAME = PatchYield.class.getName();
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "resolveStatements", "void", new String[0])).request(StackRequest.THIS).decisionMethod(new Hook(HOOK_NAME, "onAbstractMethodDeclaration_resolveStatements", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).build());
    }

    public static boolean onAbstractMethodDeclaration_resolveStatements(AbstractMethodDeclaration decl) {
        if (decl.statements != null) {
            EclipseNode methodNode = Patches.getMethodNode(decl);
            methodNode.traverse(new HandleYield());
        }
        return false;
    }

    private PatchYield() {
    }
}

