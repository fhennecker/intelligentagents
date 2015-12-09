/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse.handlers;

import java.util.Collection;
import lombok.Application;
import lombok.JvmAgent;
import lombok.core.handlers.EntrypointHandler;
import lombok.core.util.Each;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class HandleEntrypoint {

    public static abstract class EclipseEntrypointHandler
    extends EclipseASTAdapter {
        private final Class<?> interfaze;

        @Override
        public void visitType(EclipseNode typeNode, TypeDeclaration type) {
            boolean implementsInterface = false;
            boolean isAnImport = typeNode.getImportStatements().contains(this.interfaze.getName());
            for (TypeReference ref : Each.elementIn(type.superInterfaces)) {
                if (!ref.toString().equals(this.interfaze.getName()) && (!isAnImport || !ref.toString().equals(this.interfaze.getSimpleName()))) continue;
                implementsInterface = true;
                break;
            }
            if (implementsInterface) {
                this.handle(EclipseType.typeOf(typeNode, (ASTNode)type));
            }
        }

        protected abstract void handle(EclipseType var1);

        public EclipseEntrypointHandler(Class<?> interfaze) {
            this.interfaze = interfaze;
        }
    }

    public static class HandleJvmAgent
    extends EclipseEntrypointHandler {
        public HandleJvmAgent() {
            super(JvmAgent.class);
        }

        @Override
        protected void handle(EclipseType type) {
            new EntrypointHandler().createEntrypoint(type, "agentmain", "runAgent", EntrypointHandler.Parameters.JVM_AGENT, EntrypointHandler.Arguments.JVM_AGENT);
            new EntrypointHandler().createEntrypoint(type, "premain", "runAgent", EntrypointHandler.Parameters.JVM_AGENT, EntrypointHandler.Arguments.JVM_AGENT);
        }
    }

    public static class HandleApplication
    extends EclipseEntrypointHandler {
        public HandleApplication() {
            super(Application.class);
        }

        @Override
        protected void handle(EclipseType type) {
            new EntrypointHandler().createEntrypoint(type, "main", "runApp", EntrypointHandler.Parameters.APPLICATION, EntrypointHandler.Arguments.APPLICATION);
        }
    }

}

