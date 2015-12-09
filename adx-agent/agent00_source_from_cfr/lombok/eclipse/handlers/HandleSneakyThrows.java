/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.MemberValuePair
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.ThrowStatement
 *  org.eclipse.jdt.internal.compiler.ast.TryStatement
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.SneakyThrows;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@DeferUntilPostDiet
public class HandleSneakyThrows
extends EclipseAnnotationHandler<SneakyThrows> {
    @Override
    public void handle(AnnotationValues<SneakyThrows> annotation, Annotation source, EclipseNode annotationNode) {
        List<String> exceptionNames = annotation.getRawExpressions("value");
        ArrayList<DeclaredException> exceptions = new ArrayList<DeclaredException>();
        MemberValuePair[] memberValuePairs = source.memberValuePairs();
        if (memberValuePairs == null || memberValuePairs.length == 0) {
            exceptions.add(new DeclaredException("java.lang.Throwable", (ASTNode)source));
        } else {
            Expression arrayOrSingle = memberValuePairs[0].value;
            Expression[] exceptionNameNodes = arrayOrSingle instanceof ArrayInitializer ? ((ArrayInitializer)arrayOrSingle).expressions : new Expression[]{arrayOrSingle};
            if (exceptionNames.size() != exceptionNameNodes.length) {
                annotationNode.addError("LOMBOK BUG: The number of exception classes in the annotation isn't the same pre- and post- guessing.");
            }
            int idx = 0;
            Iterator<String> i$ = exceptionNames.iterator();
            while (i$.hasNext()) {
                String exceptionName = i$.next();
                if (exceptionName.endsWith(".class")) {
                    exceptionName = exceptionName.substring(0, exceptionName.length() - 6);
                }
                exceptions.add(new DeclaredException(exceptionName, (ASTNode)exceptionNameNodes[idx++]));
            }
        }
        EclipseNode owner = (EclipseNode)annotationNode.up();
        switch (owner.getKind()) {
            case METHOD: {
                this.handleMethod(annotationNode, (AbstractMethodDeclaration)owner.get(), exceptions);
                break;
            }
            default: {
                annotationNode.addError("@SneakyThrows is legal only on methods and constructors.");
            }
        }
    }

    private void handleMethod(EclipseNode annotation, AbstractMethodDeclaration method, List<DeclaredException> exceptions) {
        if (method.isAbstract()) {
            annotation.addError("@SneakyThrows can only be used on concrete methods.");
            return;
        }
        if (method.statements == null) {
            return;
        }
        Statement[] contents = method.statements;
        for (DeclaredException exception : exceptions) {
            contents = new Statement[]{this.buildTryCatchBlock(contents, exception, exception.node, method)};
        }
        method.statements = contents;
        ((EclipseNode)annotation.up()).rebuild();
    }

    private Statement buildTryCatchBlock(Statement[] contents, DeclaredException exception, ASTNode source, AbstractMethodDeclaration method) {
        SingleTypeReference typeReference;
        int methodStart = method.bodyStart;
        int methodEnd = method.bodyEnd;
        long methodPosEnd = (long)(methodEnd << 32) | (long)methodEnd & 0xFFFFFFFFL;
        TryStatement tryStatement = new TryStatement();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)tryStatement, source);
        tryStatement.tryBlock = new Block(0);
        tryStatement.tryBlock.sourceStart = methodStart;
        tryStatement.tryBlock.sourceEnd = methodEnd;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)tryStatement.tryBlock, source);
        tryStatement.tryBlock.statements = contents;
        if (exception.exceptionName.indexOf(46) == -1) {
            typeReference = new SingleTypeReference(exception.exceptionName.toCharArray(), methodPosEnd);
            typeReference.statementEnd = methodEnd;
        } else {
            String[] x = exception.exceptionName.split("\\.");
            char[][] elems = new char[x.length][];
            long[] poss = new long[x.length];
            Arrays.fill(poss, methodPosEnd);
            for (int i = 0; i < x.length; ++i) {
                elems[i] = x[i].trim().toCharArray();
            }
            typeReference = new QualifiedTypeReference((char[][])elems, poss);
        }
        EclipseHandlerUtil.setGeneratedBy((ASTNode)typeReference, source);
        Argument catchArg = new Argument("$ex".toCharArray(), methodPosEnd, (TypeReference)typeReference, 16);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)catchArg, source);
        catchArg.declarationEnd = catchArg.sourceEnd = methodEnd;
        catchArg.declarationSourceEnd = catchArg.sourceEnd;
        catchArg.modifiersSourceStart = catchArg.sourceStart = methodEnd;
        catchArg.declarationSourceStart = catchArg.sourceStart;
        tryStatement.catchArguments = new Argument[]{catchArg};
        MessageSend sneakyThrowStatement = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)sneakyThrowStatement, source);
        sneakyThrowStatement.receiver = new QualifiedNameReference((char[][])new char[][]{"lombok".toCharArray(), "Lombok".toCharArray()}, new long[2], methodEnd, methodEnd);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)sneakyThrowStatement.receiver, source);
        sneakyThrowStatement.receiver.statementEnd = methodEnd;
        sneakyThrowStatement.selector = "sneakyThrow".toCharArray();
        SingleNameReference exRef = new SingleNameReference("$ex".toCharArray(), methodPosEnd);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)exRef, source);
        exRef.statementEnd = methodEnd;
        sneakyThrowStatement.arguments = new Expression[]{exRef};
        sneakyThrowStatement.nameSourcePosition = -2;
        sneakyThrowStatement.sourceStart = methodEnd;
        sneakyThrowStatement.sourceEnd = sneakyThrowStatement.statementEnd = methodEnd;
        ThrowStatement rethrowStatement = new ThrowStatement((Expression)sneakyThrowStatement, methodEnd, methodEnd);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)rethrowStatement, source);
        Block block = new Block(0);
        block.sourceStart = methodEnd;
        block.sourceEnd = methodEnd;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)block, source);
        block.statements = new Statement[]{rethrowStatement};
        tryStatement.catchBlocks = new Block[]{block};
        tryStatement.sourceStart = method.bodyStart;
        tryStatement.sourceEnd = method.bodyEnd;
        return tryStatement;
    }

    private static class DeclaredException {
        final String exceptionName;
        final ASTNode node;

        DeclaredException(String exceptionName, ASTNode node) {
            this.exceptionName = exceptionName;
            this.node = node;
        }
    }

}

