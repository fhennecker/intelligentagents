/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import lombok.Sanitize;
import lombok.ast.IMethod;
import lombok.ast.Statement;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.util.Names;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacMethodEditor;

public class JavacParameterSanitizer
implements IParameterSanitizer<JavacMethod> {
    @Override
    public java.util.List<Statement<?>> sanitizeParameterOf(JavacMethod method) {
        Javac.deleteImport(method.node(), Sanitize.class);
        for (IParameterSanitizer.SanitizerStrategy sanitizerStrategy : IParameterSanitizer.SanitizerStrategy.IN_ORDER) {
            Javac.deleteImport(method.node(), sanitizerStrategy.getType());
        }
        ArrayList sanitizeStatements = new ArrayList();
        block1 : for (JCTree.JCVariableDecl argument : method.get().params) {
            String argumentName = argument.name.toString();
            String newArgumentName = Names.camelCase("sanitized", argumentName);
            for (IParameterSanitizer.SanitizerStrategy sanitizerStrategy2 : IParameterSanitizer.SanitizerStrategy.IN_ORDER) {
                JCTree.JCAnnotation ann = Javac.getAnnotation(sanitizerStrategy2.getType(), argument.mods);
                if (ann == null) continue;
                JavacNode annotationNode = (JavacNode)method.node().getNodeFor(ann);
                Annotation annotation = JavacHandlerUtil.createAnnotation(sanitizerStrategy2.getType(), annotationNode).getInstance();
                sanitizeStatements.add(sanitizerStrategy2.getStatementFor((Object)argument.vartype, argumentName, newArgumentName, annotation));
                method.editor().replaceVariableName(argumentName, newArgumentName);
                argument.mods.flags |= 16;
                argument.mods.annotations = Javac.remove(argument.mods.annotations, ann);
                continue block1;
            }
        }
        for (Statement sanitizeStatement : sanitizeStatements) {
            sanitizeStatement.posHint((Object)method.get());
        }
        return sanitizeStatements;
    }
}

