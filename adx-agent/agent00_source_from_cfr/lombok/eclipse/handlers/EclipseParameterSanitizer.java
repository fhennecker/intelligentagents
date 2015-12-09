/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.IMethod;
import lombok.ast.Statement;
import lombok.core.handlers.IParameterSanitizer;
import lombok.core.util.Each;
import lombok.core.util.Names;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.Eclipse;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.ast.EclipseMethodEditor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class EclipseParameterSanitizer
implements IParameterSanitizer<EclipseMethod> {
    @Override
    public List<Statement<?>> sanitizeParameterOf(EclipseMethod method) {
        ArrayList sanitizeStatements = new ArrayList();
        block0 : for (Argument argument : Each.elementIn(method.get().arguments)) {
            String argumentName = new String(argument.name);
            String newArgumentName = Names.camelCase("sanitized", argumentName);
            for (IParameterSanitizer.SanitizerStrategy sanitizerStrategy : IParameterSanitizer.SanitizerStrategy.IN_ORDER) {
                Annotation ann = Eclipse.getAnnotation(sanitizerStrategy.getType(), argument.annotations);
                if (ann == null || EclipseHandlerUtil.isGenerated((ASTNode)ann)) continue;
                EclipseNode annotationNode = (EclipseNode)method.node().getNodeFor(ann);
                java.lang.annotation.Annotation annotation = EclipseHandlerUtil.createAnnotation(sanitizerStrategy.getType(), annotationNode).getInstance();
                sanitizeStatements.add(sanitizerStrategy.getStatementFor((Object)argument.type, argumentName, newArgumentName, annotation));
                method.editor().replaceVariableName(argumentName, newArgumentName);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)ann, (ASTNode)ann);
                argument.modifiers |= 16;
                argument.bits |= 8388608;
                continue block0;
            }
        }
        for (Statement sanitizeStatement : sanitizeStatements) {
            sanitizeStatement.posHint((Object)method.get());
        }
        return sanitizeStatements;
    }
}

