/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 */
package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.ast.IMethod;
import lombok.ast.Statement;
import lombok.core.handlers.IParameterValidator;
import lombok.core.util.Each;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.Eclipse;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.ast.EclipseMethod;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;

public class EclipseParameterValidator
implements IParameterValidator<EclipseMethod> {
    @Override
    public List<Statement<?>> validateParameterOf(EclipseMethod method) {
        ArrayList validateStatements = new ArrayList();
        int argumentIndex = 0;
        block0 : for (Argument argument : Each.elementIn(method.get().arguments)) {
            String argumentName = new String(argument.name);
            ++argumentIndex;
            for (IParameterValidator.ValidationStrategy validationStrategy : IParameterValidator.ValidationStrategy.IN_ORDER) {
                Annotation ann = Eclipse.getAnnotation(validationStrategy.getType(), argument.annotations);
                if (ann == null || EclipseHandlerUtil.isGenerated((ASTNode)ann)) continue;
                EclipseNode annotationNode = (EclipseNode)method.node().getNodeFor(ann);
                java.lang.annotation.Annotation annotation = EclipseHandlerUtil.createAnnotation(validationStrategy.getType(), annotationNode).getInstance();
                validateStatements.addAll(validationStrategy.getStatementsFor(argumentName, argumentIndex, annotation));
                EclipseHandlerUtil.setGeneratedBy((ASTNode)ann, (ASTNode)ann);
                argument.bits |= 8388608;
                continue block0;
            }
        }
        for (Statement validateStatement : validateStatements) {
            validateStatement.posHint((Object)method.get());
        }
        return validateStatements;
    }
}

