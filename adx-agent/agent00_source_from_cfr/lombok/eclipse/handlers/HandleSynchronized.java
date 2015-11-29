/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldReference
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.TypeConstants
 */
package lombok.eclipse.handlers;

import lombok.Synchronized;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@DeferUntilPostDiet
public class HandleSynchronized
extends EclipseAnnotationHandler<Synchronized> {
    private static final char[] INSTANCE_LOCK_NAME = "$lock".toCharArray();
    private static final char[] STATIC_LOCK_NAME = "$LOCK".toCharArray();

    @Override
    public void preHandle(AnnotationValues<Synchronized> annotation, Annotation source, EclipseNode annotationNode) {
        EclipseNode methodNode = (EclipseNode)annotationNode.up();
        if (methodNode == null || methodNode.getKind() != AST.Kind.METHOD || !(methodNode.get() instanceof MethodDeclaration)) {
            return;
        }
        MethodDeclaration method = (MethodDeclaration)methodNode.get();
        if (method.isAbstract()) {
            return;
        }
        this.createLockField(annotation, annotationNode, method.isStatic(), false);
    }

    private char[] createLockField(AnnotationValues<Synchronized> annotation, EclipseNode annotationNode, boolean isStatic, boolean reportErrors) {
        char[] lockName = annotation.getInstance().value().toCharArray();
        Annotation source = (Annotation)annotationNode.get();
        boolean autoMake = false;
        if (lockName.length == 0) {
            autoMake = true;
            char[] arrc = lockName = isStatic ? STATIC_LOCK_NAME : INSTANCE_LOCK_NAME;
        }
        if (EclipseHandlerUtil.fieldExists(new String(lockName), annotationNode) == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            if (!autoMake) {
                if (reportErrors) {
                    annotationNode.addError(String.format("The field %s does not exist.", new String(lockName)));
                }
                return null;
            }
            FieldDeclaration fieldDecl = new FieldDeclaration(lockName, 0, -1);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldDecl, (ASTNode)source);
            fieldDecl.declarationSourceEnd = -1;
            fieldDecl.modifiers = (isStatic ? 8 : 0) | 16 | 2;
            ArrayAllocationExpression arrayAlloc = new ArrayAllocationExpression();
            EclipseHandlerUtil.setGeneratedBy((ASTNode)arrayAlloc, (ASTNode)source);
            arrayAlloc.dimensions = new Expression[]{EclipseHandlerUtil.makeIntLiteral("0".toCharArray(), (ASTNode)source)};
            arrayAlloc.type = new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, new long[]{0, 0, 0});
            EclipseHandlerUtil.setGeneratedBy((ASTNode)arrayAlloc.type, (ASTNode)source);
            fieldDecl.type = new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, new long[]{0, 0, 0});
            EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldDecl.type, (ASTNode)source);
            fieldDecl.initialization = arrayAlloc;
            EclipseHandlerUtil.injectField((EclipseNode)((EclipseNode)annotationNode.up()).up(), fieldDecl);
        }
        return lockName;
    }

    @Override
    public void handle(AnnotationValues<Synchronized> annotation, Annotation source, EclipseNode annotationNode) {
        FieldReference lockVariable;
        int p1 = source.sourceStart - 1;
        int p2 = source.sourceStart - 2;
        long pos = (long)p1 << 32 | (long)p2;
        EclipseNode methodNode = (EclipseNode)annotationNode.up();
        if (methodNode == null || methodNode.getKind() != AST.Kind.METHOD || !(methodNode.get() instanceof MethodDeclaration)) {
            annotationNode.addError("@Synchronized is legal only on methods.");
            return;
        }
        MethodDeclaration method = (MethodDeclaration)methodNode.get();
        if (method.isAbstract()) {
            annotationNode.addError("@Synchronized is legal only on concrete methods.");
            return;
        }
        char[] lockName = this.createLockField(annotation, annotationNode, method.isStatic(), true);
        if (lockName == null) {
            return;
        }
        if (method.statements == null) {
            return;
        }
        Block block = new Block(0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)block, (ASTNode)source);
        block.statements = method.statements;
        block.sourceEnd = method.bodyEnd;
        block.sourceStart = method.bodyStart;
        if (method.isStatic()) {
            lockVariable = new QualifiedNameReference((char[][])new char[][]{((EclipseNode)methodNode.up()).getName().toCharArray(), lockName}, new long[]{pos, pos}, p1, p2);
        } else {
            lockVariable = new FieldReference(lockName, pos);
            ThisReference thisReference = new ThisReference(p1, p2);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)thisReference, (ASTNode)source);
            lockVariable.receiver = thisReference;
        }
        EclipseHandlerUtil.setGeneratedBy((ASTNode)lockVariable, (ASTNode)source);
        method.statements = new Statement[]{new SynchronizedStatement((Expression)lockVariable, block, 0, 0)};
        method.statements[0].sourceEnd = method.bodyEnd;
        method.statements[0].sourceStart = method.bodyStart;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method.statements[0], (ASTNode)source);
        methodNode.rebuild();
    }
}

