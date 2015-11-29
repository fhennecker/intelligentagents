/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.Collection;
import lombok.Application;
import lombok.JvmAgent;
import lombok.core.handlers.EntrypointHandler;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacType;

public class HandleEntrypoint {

    public static abstract class AbstractHandleEntrypoint
    extends JavacASTAdapter {
        private final Class<?> interfaze;

        @Override
        public void visitType(JavacNode typeNode, JCTree.JCClassDecl type) {
            boolean implementsInterface = false;
            boolean isAnImport = typeNode.getImportStatements().contains(this.interfaze.getName());
            if (type.getImplementsClause() != null) {
                for (JCTree.JCExpression exp : type.getImplementsClause()) {
                    if (!exp.toString().equals(this.interfaze.getName()) && (!isAnImport || !exp.toString().equals(this.interfaze.getSimpleName()))) continue;
                    implementsInterface = true;
                    break;
                }
            }
            if (implementsInterface) {
                this.handle(JavacType.typeOf(typeNode, (JCTree)type));
            }
        }

        @Override
        public void endVisitCompilationUnit(JavacNode top, JCTree.JCCompilationUnit unit) {
            JavacHandlerUtil.deleteImportFromCompilationUnit(top, this.interfaze.getName());
        }

        protected abstract void handle(JavacType var1);

        public AbstractHandleEntrypoint(Class<?> interfaze) {
            this.interfaze = interfaze;
        }
    }

    public static class HandleJvmAgent
    extends AbstractHandleEntrypoint {
        public HandleJvmAgent() {
            super(JvmAgent.class);
        }

        @Override
        protected void handle(JavacType type) {
            Javac.markInterfaceAsProcessed(type.node(), JvmAgent.class);
            new EntrypointHandler().createEntrypoint(type, "agentmain", "runAgent", EntrypointHandler.Parameters.JVM_AGENT, EntrypointHandler.Arguments.JVM_AGENT);
            new EntrypointHandler().createEntrypoint(type, "premain", "runAgent", EntrypointHandler.Parameters.JVM_AGENT, EntrypointHandler.Arguments.JVM_AGENT);
        }
    }

    public static class HandleApplication
    extends AbstractHandleEntrypoint {
        public HandleApplication() {
            super(Application.class);
        }

        @Override
        protected void handle(JavacType type) {
            Javac.markInterfaceAsProcessed(type.node(), Application.class);
            new EntrypointHandler().createEntrypoint(type, "main", "runApp", EntrypointHandler.Parameters.APPLICATION, EntrypointHandler.Arguments.APPLICATION);
        }
    }

}

