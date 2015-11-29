/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
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
import java.util.Collection;
import lombok.Validate;
import lombok.ast.IMethod;
import lombok.ast.Statement;
import lombok.core.handlers.IParameterValidator;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacMethod;

public class JavacParameterValidator
implements IParameterValidator<JavacMethod> {
    @Override
    public java.util.List<Statement<?>> validateParameterOf(JavacMethod method) {
        Javac.deleteImport(method.node(), Validate.class);
        for (IParameterValidator.ValidationStrategy validationStrategy : IParameterValidator.ValidationStrategy.IN_ORDER) {
            Javac.deleteImport(method.node(), validationStrategy.getType());
        }
        ArrayList validateStatements = new ArrayList();
        int argumentIndex = 0;
        block1 : for (JCTree.JCVariableDecl argument : method.get().params) {
            String argumentName = argument.name.toString();
            ++argumentIndex;
            for (IParameterValidator.ValidationStrategy validationStrategy2 : IParameterValidator.ValidationStrategy.IN_ORDER) {
                JCTree.JCAnnotation ann = Javac.getAnnotation(validationStrategy2.getType(), argument.mods);
                if (ann == null) continue;
                JavacNode annotationNode = (JavacNode)method.node().getNodeFor(ann);
                Annotation annotation = JavacHandlerUtil.createAnnotation(validationStrategy2.getType(), annotationNode).getInstance();
                validateStatements.addAll(validationStrategy2.getStatementsFor(argumentName, argumentIndex, annotation));
                argument.mods.annotations = Javac.remove(argument.mods.annotations, ann);
                continue block1;
            }
        }
        for (Statement validateStatement : validateStatements) {
            validateStatement.posHint((Object)method.get());
        }
        return validateStatements;
    }
}

