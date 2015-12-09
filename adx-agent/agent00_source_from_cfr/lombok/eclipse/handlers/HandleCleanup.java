/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.Assignment
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.CaseStatement
 *  org.eclipse.jdt.internal.compiler.ast.CastExpression
 *  org.eclipse.jdt.internal.compiler.ast.EqualExpression
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.IfStatement
 *  org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MemberValuePair
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.NullLiteral
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.SwitchStatement
 *  org.eclipse.jdt.internal.compiler.ast.TryStatement
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse.handlers;

import java.util.Arrays;
import lombok.Cleanup;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@DeferUntilPostDiet
public class HandleCleanup
extends EclipseAnnotationHandler<Cleanup> {
    @Override
    public void handle(AnnotationValues<Cleanup> annotation, Annotation ast, EclipseNode annotationNode) {
        Statement[] statements;
        boolean isSwitch;
        int start;
        int ss;
        int end;
        Cleanup cleanup = annotation.getInstance();
        String cleanupName = cleanup.value();
        boolean quietly = cleanup.quietly();
        if (cleanupName.length() == 0) {
            annotationNode.addError("cleanupName cannot be the empty string.");
            return;
        }
        boolean isLocalDeclaration = false;
        switch (((EclipseNode)annotationNode.up()).getKind()) {
            case ARGUMENT: {
                isLocalDeclaration = false;
                break;
            }
            case LOCAL: {
                isLocalDeclaration = true;
                break;
            }
            default: {
                annotationNode.addError("@Cleanup is legal only on local variable declarations.");
                return;
            }
        }
        LocalDeclaration decl = (LocalDeclaration)((EclipseNode)annotationNode.up()).get();
        if (isLocalDeclaration && decl.initialization == null) {
            annotationNode.addError("@Cleanup variable declarations need to be initialized.");
            return;
        }
        EclipseNode ancestor = (EclipseNode)((EclipseNode)annotationNode.up()).directUp();
        ASTNode blockNode = (ASTNode)ancestor.get();
        if (blockNode instanceof AbstractMethodDeclaration) {
            isSwitch = false;
            statements = ((AbstractMethodDeclaration)blockNode).statements;
        } else if (blockNode instanceof Block) {
            isSwitch = false;
            statements = ((Block)blockNode).statements;
        } else if (blockNode instanceof SwitchStatement) {
            isSwitch = true;
            statements = ((SwitchStatement)blockNode).statements;
        } else {
            annotationNode.addError("@Cleanup is legal only on a local variable declaration inside a block.");
            return;
        }
        if (statements == null) {
            annotationNode.addError("LOMBOK BUG: Parent block does not contain any statements.");
            return;
        }
        if (isLocalDeclaration) {
            for (start = 0; start < statements.length && statements[start] != decl; ++start) {
            }
            if (start == statements.length) {
                annotationNode.addError("LOMBOK BUG: Can't find this local variable declaration inside its parent.");
                return;
            }
            ++start;
        }
        if (isSwitch) {
            for (end = start + 1; end < statements.length && !(statements[end] instanceof CaseStatement); ++end) {
            }
        } else {
            end = statements.length;
        }
        int pS = ast.sourceStart;
        int pE = ast.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        Statement[] tryBlock = new Statement[end - start];
        System.arraycopy(statements, start, tryBlock, 0, end - start);
        int newStatementsLength = statements.length - (end - start);
        Statement[] newStatements = new Statement[++newStatementsLength];
        System.arraycopy(statements, 0, newStatements, 0, start);
        System.arraycopy(statements, end, newStatements, start + 1, statements.length - end);
        this.doAssignmentCheck(annotationNode, tryBlock, decl.name);
        TryStatement tryStatement = new TryStatement();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)tryStatement, (ASTNode)ast);
        tryStatement.tryBlock = new Block(0);
        tryStatement.tryBlock.statements = tryBlock;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)tryStatement.tryBlock, (ASTNode)ast);
        int se = ss = decl.declarationSourceEnd + 1;
        if (tryBlock.length > 0) {
            se = tryBlock[tryBlock.length - 1].sourceEnd + 1;
            tryStatement.sourceStart = ss;
            tryStatement.sourceEnd = se;
            tryStatement.tryBlock.sourceStart = ss;
            tryStatement.tryBlock.sourceEnd = se;
        }
        newStatements[start] = tryStatement;
        Statement[] finallyBlock = new Statement[1];
        if ("close".equals(cleanupName) && !annotation.isExplicit("value")) {
            SingleNameReference varName = new SingleNameReference(decl.name, p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)varName, (ASTNode)ast);
            CastExpression castExpression = EclipseHandlerUtil.makeCastExpression((Expression)varName, this.generateQualifiedTypeRef((ASTNode)ast, "java".toCharArray(), "io".toCharArray(), "Closeable".toCharArray()), (ASTNode)ast);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)castExpression, (ASTNode)ast);
            MessageSend safeClose = new MessageSend();
            EclipseHandlerUtil.setGeneratedBy((ASTNode)safeClose, (ASTNode)ast);
            safeClose.sourceStart = ast.sourceStart;
            safeClose.sourceEnd = ast.sourceEnd;
            safeClose.statementEnd = ast.sourceEnd;
            safeClose.receiver = castExpression;
            long nameSourcePosition = (long)ast.sourceStart << 32 | (long)ast.sourceEnd;
            if (ast.memberValuePairs() != null) {
                for (MemberValuePair pair : ast.memberValuePairs()) {
                    if (pair.name == null || !new String(pair.name).equals("value")) continue;
                    nameSourcePosition = (long)pair.value.sourceStart << 32 | (long)pair.value.sourceEnd;
                    break;
                }
            }
            safeClose.nameSourcePosition = nameSourcePosition;
            safeClose.selector = cleanupName.toCharArray();
            MessageSend cleanupCall = safeClose;
            if (quietly) {
                cleanupCall = this.cleanupQuietly(ast, (Statement)cleanupCall);
            }
            varName = new SingleNameReference(decl.name, p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)varName, (ASTNode)ast);
            InstanceOfExpression isClosable = new InstanceOfExpression((Expression)varName, this.generateQualifiedTypeRef((ASTNode)ast, "java".toCharArray(), "io".toCharArray(), "Closeable".toCharArray()));
            EclipseHandlerUtil.setGeneratedBy((ASTNode)isClosable, (ASTNode)ast);
            Block closeBlock = new Block(0);
            closeBlock.statements = new Statement[1];
            closeBlock.statements[0] = cleanupCall;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)closeBlock, (ASTNode)ast);
            IfStatement ifStatement = new IfStatement((Expression)isClosable, (Statement)closeBlock, 0, 0);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ifStatement, (ASTNode)ast);
            finallyBlock[0] = ifStatement;
        } else {
            MessageSend unsafeClose = new MessageSend();
            EclipseHandlerUtil.setGeneratedBy((ASTNode)unsafeClose, (ASTNode)ast);
            unsafeClose.sourceStart = ast.sourceStart;
            unsafeClose.sourceEnd = ast.sourceEnd;
            SingleNameReference receiver = new SingleNameReference(decl.name, 0);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)receiver, (ASTNode)ast);
            unsafeClose.receiver = receiver;
            long nameSourcePosition = (long)ast.sourceStart << 32 | (long)ast.sourceEnd;
            if (ast.memberValuePairs() != null) {
                for (MemberValuePair pair : ast.memberValuePairs()) {
                    if (pair.name == null || !new String(pair.name).equals("value")) continue;
                    nameSourcePosition = (long)pair.value.sourceStart << 32 | (long)pair.value.sourceEnd;
                    break;
                }
            }
            unsafeClose.nameSourcePosition = nameSourcePosition;
            unsafeClose.selector = cleanupName.toCharArray();
            MessageSend cleanupCall = unsafeClose;
            if (quietly) {
                cleanupCall = this.cleanupQuietly(ast, (Statement)cleanupCall);
            }
            SingleNameReference varName = new SingleNameReference(decl.name, p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)varName, (ASTNode)ast);
            NullLiteral nullLiteral = new NullLiteral(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)nullLiteral, (ASTNode)ast);
            MessageSend preventNullAnalysis = this.preventNullAnalysis(ast, (Expression)varName);
            EqualExpression equalExpression = new EqualExpression((Expression)preventNullAnalysis, (Expression)nullLiteral, 29);
            equalExpression.sourceStart = pS;
            equalExpression.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)equalExpression, (ASTNode)ast);
            Block closeBlock = new Block(0);
            closeBlock.statements = new Statement[1];
            closeBlock.statements[0] = cleanupCall;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)closeBlock, (ASTNode)ast);
            IfStatement ifStatement = new IfStatement((Expression)equalExpression, (Statement)closeBlock, 0, 0);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ifStatement, (ASTNode)ast);
            finallyBlock[0] = ifStatement;
        }
        tryStatement.finallyBlock = new Block(0);
        if (!isSwitch) {
            tryStatement.finallyBlock.sourceStart = blockNode.sourceEnd;
            tryStatement.finallyBlock.sourceEnd = blockNode.sourceEnd;
        }
        EclipseHandlerUtil.setGeneratedBy((ASTNode)tryStatement.finallyBlock, (ASTNode)ast);
        tryStatement.finallyBlock.statements = finallyBlock;
        tryStatement.catchArguments = null;
        tryStatement.catchBlocks = null;
        if (blockNode instanceof AbstractMethodDeclaration) {
            ((AbstractMethodDeclaration)blockNode).statements = newStatements;
        } else if (blockNode instanceof Block) {
            ((Block)blockNode).statements = newStatements;
        } else if (blockNode instanceof SwitchStatement) {
            ((SwitchStatement)blockNode).statements = newStatements;
        }
        ancestor.rebuild();
    }

    private Statement cleanupQuietly(Annotation ast, Statement cleanupCall) {
        int pS = ast.sourceStart;
        int pE = ast.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        TryStatement tryStatement = new TryStatement();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)tryStatement, (ASTNode)ast);
        Block tryBlock = new Block(0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)tryBlock, (ASTNode)ast);
        tryBlock.statements = new Statement[]{cleanupCall};
        tryBlock.sourceStart = pS;
        tryBlock.sourceEnd = pE;
        tryStatement.tryBlock = tryBlock;
        String[] x = new String[]{"java", "io", "IOException"};
        char[][] elems = new char[x.length][];
        long[] poss = new long[x.length];
        Arrays.fill(poss, p);
        for (int i = 0; i < x.length; ++i) {
            elems[i] = x[i].trim().toCharArray();
        }
        QualifiedTypeReference typeReference = new QualifiedTypeReference((char[][])elems, poss);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)typeReference, (ASTNode)ast);
        Argument catchArg = new Argument("$ex".toCharArray(), 0, (TypeReference)typeReference, 16);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)catchArg, (ASTNode)ast);
        catchArg.sourceStart = 0;
        catchArg.sourceEnd = 0;
        catchArg.declarationEnd = -1;
        catchArg.declarationSourceEnd = -1;
        Block catchBlock = new Block(0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)catchBlock, (ASTNode)ast);
        catchBlock.statements = new Statement[0];
        catchBlock.sourceStart = pS;
        catchBlock.sourceEnd = pE;
        tryStatement.catchArguments = new Argument[]{catchArg};
        tryStatement.catchBlocks = new Block[]{catchBlock};
        return tryStatement;
    }

    private MessageSend preventNullAnalysis(Annotation ast, Expression expr) {
        MessageSend singletonList = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)singletonList, (ASTNode)ast);
        int pS = ast.sourceStart;
        int pE = ast.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        singletonList.receiver = EclipseHandlerUtil.createNameReference("java.util.Collections", ast);
        singletonList.selector = "singletonList".toCharArray();
        singletonList.arguments = new Expression[]{expr};
        singletonList.nameSourcePosition = p;
        singletonList.sourceStart = pS;
        singletonList.sourceEnd = singletonList.statementEnd = pE;
        MessageSend preventNullAnalysis = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)preventNullAnalysis, (ASTNode)ast);
        preventNullAnalysis.receiver = singletonList;
        preventNullAnalysis.selector = "get".toCharArray();
        preventNullAnalysis.arguments = new Expression[]{EclipseHandlerUtil.makeIntLiteral("0".toCharArray(), (ASTNode)ast)};
        preventNullAnalysis.nameSourcePosition = p;
        preventNullAnalysis.sourceStart = pS;
        preventNullAnalysis.sourceEnd = singletonList.statementEnd = pE;
        return preventNullAnalysis;
    }

    private /* varargs */ TypeReference generateQualifiedTypeRef(ASTNode source, char[] ... varNames) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        long[] poss = Eclipse.poss(source, varNames.length);
        QualifiedTypeReference ref = varNames.length > 1 ? new QualifiedTypeReference(varNames, poss) : new SingleTypeReference(varNames[0], p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ref, source);
        return ref;
    }

    private void doAssignmentCheck(EclipseNode node, Statement[] tryBlock, char[] varName) {
        for (Statement statement : tryBlock) {
            this.doAssignmentCheck0(node, statement, varName);
        }
    }

    private void doAssignmentCheck0(EclipseNode node, Statement statement, char[] varName) {
        EclipseNode problemNode;
        if (statement instanceof Assignment) {
            this.doAssignmentCheck0(node, (Statement)((Assignment)statement).expression, varName);
        } else if (statement instanceof LocalDeclaration) {
            this.doAssignmentCheck0(node, (Statement)((LocalDeclaration)statement).initialization, varName);
        } else if (statement instanceof CastExpression) {
            this.doAssignmentCheck0(node, (Statement)((CastExpression)statement).expression, varName);
        } else if (statement instanceof SingleNameReference && Arrays.equals(((SingleNameReference)statement).token, varName) && (problemNode = (EclipseNode)node.getNodeFor(statement)) != null) {
            problemNode.addWarning("You're assigning an auto-cleanup variable to something else. This is a bad idea.");
        }
    }

}

