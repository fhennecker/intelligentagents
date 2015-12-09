/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.TypeTags
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCBinary
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCCase
 *  com.sun.tools.javac.tree.JCTree$JCCatch
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCIf
 *  com.sun.tools.javac.tree.JCTree$JCInstanceOf
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCTry
 *  com.sun.tools.javac.tree.JCTree$JCTypeCast
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import lombok.Cleanup;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.ResolutionBased;
import lombok.javac.handlers.JavacHandlerUtil;

@ResolutionBased
public class HandleCleanup
extends JavacAnnotationHandler<Cleanup> {
    @Override
    public void handle(AnnotationValues<Cleanup> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        List statements;
        JCTree.JCBlock finalizer;
        if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
            return;
        }
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Cleanup.class);
        Cleanup cleanup = annotation.getInstance();
        String cleanupName = cleanup.value();
        boolean quietly = cleanup.quietly();
        if (cleanupName.length() == 0) {
            annotationNode.addError("cleanupName cannot be the empty string.");
            return;
        }
        boolean isLocalDeclaration = false;
        switch (((JavacNode)annotationNode.up()).getKind()) {
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
        JCTree.JCVariableDecl decl = (JCTree.JCVariableDecl)((JavacNode)annotationNode.up()).get();
        if (isLocalDeclaration && decl.init == null) {
            annotationNode.addError("@Cleanup variable declarations need to be initialized.");
            return;
        }
        JavacNode ancestor = (JavacNode)((JavacNode)annotationNode.up()).directUp();
        JCTree blockNode = (JCTree)ancestor.get();
        if (blockNode instanceof JCTree.JCBlock) {
            statements = ((JCTree.JCBlock)blockNode).stats;
        } else if (blockNode instanceof JCTree.JCCase) {
            statements = ((JCTree.JCCase)blockNode).stats;
        } else if (blockNode instanceof JCTree.JCMethodDecl) {
            statements = ((JCTree.JCMethodDecl)blockNode).body.stats;
        } else {
            annotationNode.addError("@Cleanup is legal only on a local variable declaration inside a block.");
            return;
        }
        boolean seenDeclaration = false;
        ListBuffer newStatements = ListBuffer.lb();
        ListBuffer tryBlock = ListBuffer.lb();
        for (JCTree.JCStatement statement : statements) {
            if (isLocalDeclaration && !seenDeclaration) {
                if (statement == decl) {
                    seenDeclaration = true;
                }
                newStatements.append((Object)statement);
                continue;
            }
            tryBlock.append((Object)statement);
        }
        if (isLocalDeclaration && !seenDeclaration) {
            annotationNode.addError("LOMBOK BUG: Can't find this local variable declaration inside its parent.");
            return;
        }
        this.doAssignmentCheck(annotationNode, tryBlock.toList(), decl.name);
        TreeMaker maker = annotationNode.getTreeMaker();
        if ("close".equals(cleanupName) && !annotation.isExplicit("value")) {
            JCTree.JCFieldAccess cleanupMethod = maker.Select((JCTree.JCExpression)maker.TypeCast((JCTree)JavacHandlerUtil.chainDotsString(annotationNode, "java.io.Closeable"), (JCTree.JCExpression)maker.Ident(decl.name)), annotationNode.toName(cleanupName));
            List<JCTree.JCStatement> cleanupCall = List.of((Object)maker.Exec((JCTree.JCExpression)maker.Apply(List.nil(), (JCTree.JCExpression)cleanupMethod, List.nil())));
            if (quietly) {
                cleanupCall = this.cleanupQuietly(maker, annotationNode, cleanupCall);
            }
            JCTree.JCInstanceOf isClosable = maker.TypeTest((JCTree.JCExpression)maker.Ident(decl.name), (JCTree)JavacHandlerUtil.chainDotsString(annotationNode, "java.io.Closeable"));
            JCTree.JCIf ifIsClosableCleanup = maker.If((JCTree.JCExpression)isClosable, (JCTree.JCStatement)maker.Block(0, cleanupCall), null);
            finalizer = JavacHandlerUtil.recursiveSetGeneratedBy(maker.Block(0, List.of((Object)ifIsClosableCleanup)), (JCTree)ast);
        } else {
            JCTree.JCFieldAccess cleanupMethod = maker.Select((JCTree.JCExpression)maker.Ident(decl.name), annotationNode.toName(cleanupName));
            List<JCTree.JCStatement> cleanupCall = List.of((Object)maker.Exec((JCTree.JCExpression)maker.Apply(List.nil(), (JCTree.JCExpression)cleanupMethod, List.nil())));
            JCTree.JCMethodInvocation preventNullAnalysis = this.preventNullAnalysis(maker, annotationNode, (JCTree.JCExpression)maker.Ident(decl.name));
            JCTree.JCBinary isNull = maker.Binary(Javac.getCtcInt(JCTree.class, "NE"), (JCTree.JCExpression)preventNullAnalysis, (JCTree.JCExpression)maker.Literal(Javac.getCtcInt(TypeTags.class, "BOT"), (Object)null));
            if (quietly) {
                cleanupCall = this.cleanupQuietly(maker, annotationNode, cleanupCall);
            }
            JCTree.JCIf ifNotNullCleanup = maker.If((JCTree.JCExpression)isNull, (JCTree.JCStatement)maker.Block(0, cleanupCall), null);
            finalizer = JavacHandlerUtil.recursiveSetGeneratedBy(maker.Block(0, List.of((Object)ifNotNullCleanup)), (JCTree)ast);
        }
        newStatements.append((Object)JavacHandlerUtil.setGeneratedBy(maker.Try(JavacHandlerUtil.setGeneratedBy(maker.Block(0, tryBlock.toList()), (JCTree)ast), List.nil(), finalizer), (JCTree)ast));
        if (blockNode instanceof JCTree.JCBlock) {
            ((JCTree.JCBlock)blockNode).stats = newStatements.toList();
        } else if (blockNode instanceof JCTree.JCCase) {
            ((JCTree.JCCase)blockNode).stats = newStatements.toList();
        } else if (blockNode instanceof JCTree.JCMethodDecl) {
            ((JCTree.JCMethodDecl)blockNode).body.stats = newStatements.toList();
        } else {
            throw new AssertionError((Object)"Should not get here");
        }
        ancestor.rebuild();
    }

    private List<JCTree.JCStatement> cleanupQuietly(TreeMaker maker, JavacNode node, List<JCTree.JCStatement> cleanupCall) {
        JCTree.JCVariableDecl catchParam = maker.VarDef(maker.Modifiers(16), node.toName("$ex"), JavacHandlerUtil.chainDotsString(node, "java.io.IOException"), null);
        JCTree.JCBlock catchBody = maker.at(0).Block(0, List.nil());
        return List.of((Object)maker.Try(maker.Block(0, cleanupCall), List.of((Object)maker.Catch(catchParam, catchBody)), null));
    }

    private JCTree.JCMethodInvocation preventNullAnalysis(TreeMaker maker, JavacNode node, JCTree.JCExpression expression) {
        JCTree.JCMethodInvocation singletonList = maker.Apply(List.nil(), JavacHandlerUtil.chainDotsString(node, "java.util.Collections.singletonList"), List.of((Object)expression));
        JCTree.JCMethodInvocation cleanedExpr = maker.Apply(List.nil(), (JCTree.JCExpression)maker.Select((JCTree.JCExpression)singletonList, node.toName("get")), List.of((Object)maker.Literal(4, (Object)0)));
        return cleanedExpr;
    }

    private void doAssignmentCheck(JavacNode node, List<JCTree.JCStatement> statements, Name name) {
        for (JCTree.JCStatement statement : statements) {
            this.doAssignmentCheck0(node, (JCTree)statement, name);
        }
    }

    private void doAssignmentCheck0(JavacNode node, JCTree statement, Name name) {
        JavacNode problemNode;
        if (statement instanceof JCTree.JCAssign) {
            this.doAssignmentCheck0(node, (JCTree)((JCTree.JCAssign)statement).rhs, name);
        }
        if (statement instanceof JCTree.JCExpressionStatement) {
            this.doAssignmentCheck0(node, (JCTree)((JCTree.JCExpressionStatement)statement).expr, name);
        }
        if (statement instanceof JCTree.JCVariableDecl) {
            this.doAssignmentCheck0(node, (JCTree)((JCTree.JCVariableDecl)statement).init, name);
        }
        if (statement instanceof JCTree.JCTypeCast) {
            this.doAssignmentCheck0(node, (JCTree)((JCTree.JCTypeCast)statement).expr, name);
        }
        if (statement instanceof JCTree.JCIdent && ((JCTree.JCIdent)statement).name.contentEquals((CharSequence)name) && (problemNode = (JavacNode)node.getNodeFor(statement)) != null) {
            problemNode.addWarning("You're assigning an auto-cleanup variable to something else. This is a bad idea.");
        }
    }

}

