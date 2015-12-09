/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.AssignmentTree
 *  com.sun.source.tree.IdentifierTree
 *  com.sun.source.tree.MemberSelectTree
 *  com.sun.source.tree.Tree
 *  com.sun.source.tree.VariableTree
 *  com.sun.source.util.TreeScanner
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCCase
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 */
package lombok.javac.handlers;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;
import javax.lang.model.element.Name;
import lombok.Tuple;
import lombok.ast.AST;
import lombok.ast.Expression;
import lombok.ast.LocalDecl;
import lombok.ast.Node;
import lombok.ast.TypeRef;
import lombok.core.util.ErrorMessages;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.ast.JavacASTMaker;
import lombok.javac.handlers.ast.JavacMethod;

public class HandleTuple
extends JavacASTAdapter {
    private final Set<String> methodNames = new HashSet<String>();
    private int withVarCounter;

    @Override
    public void visitCompilationUnit(JavacNode top, JCTree.JCCompilationUnit unit) {
        this.methodNames.clear();
        this.withVarCounter = 0;
    }

    @Override
    public void visitLocal(JavacNode localNode, JCTree.JCVariableDecl local) {
        JCTree.JCMethodInvocation initTupleCall = this.getTupelCall(localNode, local.init);
        if (initTupleCall != null) {
            JavacMethod method = JavacMethod.methodOf(localNode, (JCTree)local);
            if (method == null) {
                localNode.addError(ErrorMessages.canBeUsedInBodyOfMethodsOnly("tuple"));
            } else if (this.handle(localNode, initTupleCall)) {
                this.methodNames.add(initTupleCall.meth.toString());
            }
        }
    }

    @Override
    public void visitStatement(JavacNode statementNode, JCTree statement) {
        if (statement instanceof JCTree.JCAssign) {
            JCTree.JCAssign assignment = (JCTree.JCAssign)statement;
            JCTree.JCMethodInvocation leftTupleCall = this.getTupelCall(statementNode, assignment.lhs);
            JCTree.JCMethodInvocation rightTupleCall = this.getTupelCall(statementNode, assignment.rhs);
            if (leftTupleCall != null && rightTupleCall != null) {
                JavacMethod method = JavacMethod.methodOf(statementNode, statement);
                if (method == null) {
                    statementNode.addError(ErrorMessages.canBeUsedInBodyOfMethodsOnly("tuple"));
                } else if (this.handle(statementNode, leftTupleCall, rightTupleCall)) {
                    this.methodNames.add(leftTupleCall.meth.toString());
                    this.methodNames.add(rightTupleCall.meth.toString());
                }
            }
        }
    }

    private JCTree.JCMethodInvocation getTupelCall(JavacNode node, JCTree.JCExpression expression) {
        if (expression instanceof JCTree.JCMethodInvocation) {
            JCTree.JCMethodInvocation tupleCall = (JCTree.JCMethodInvocation)expression;
            String methodName = tupleCall.meth.toString();
            if (Javac.isMethodCallValid(node, methodName, Tuple.class, "tuple")) {
                return tupleCall;
            }
        }
        return null;
    }

    @Override
    public void endVisitCompilationUnit(JavacNode top, JCTree.JCCompilationUnit unit) {
        for (String methodName : this.methodNames) {
            Javac.deleteMethodCallImports(top, methodName, Tuple.class, "tuple");
        }
    }

    public boolean handle(JavacNode tupleInitNode, JCTree.JCMethodInvocation initTupleCall) {
        if (initTupleCall.args.isEmpty()) {
            return true;
        }
        int numberOfArguments = initTupleCall.args.size();
        List localDecls = List.nil();
        String type = ((JCTree.JCVariableDecl)tupleInitNode.get()).vartype.toString();
        for (JavacNode node : ((JavacNode)tupleInitNode.directUp()).down()) {
            if (!(node.get() instanceof JCTree.JCVariableDecl)) continue;
            JCTree.JCVariableDecl localDecl = (JCTree.JCVariableDecl)node.get();
            if (!type.equals(localDecl.vartype.toString())) continue;
            if ((localDecls = localDecls.append((Object)localDecl)).size() > numberOfArguments) {
                localDecls.head = localDecls.tail.head;
            }
            if (!node.equals(tupleInitNode)) continue;
            break;
        }
        if (numberOfArguments != localDecls.length()) {
            tupleInitNode.addError(String.format("Argument mismatch on the right side. (required: %s found: %s)", localDecls.length(), numberOfArguments));
            return false;
        }
        int index = 0;
        for (JCTree.JCVariableDecl localDecl : localDecls) {
            localDecl.init = (JCTree.JCExpression)initTupleCall.args.get(index++);
        }
        return true;
    }

    public boolean handle(JavacNode tupleAssignNode, JCTree.JCMethodInvocation leftTupleCall, JCTree.JCMethodInvocation rightTupleCall) {
        if (!this.validateTupel(tupleAssignNode, leftTupleCall, rightTupleCall)) {
            return false;
        }
        ListBuffer tempVarAssignments = ListBuffer.lb();
        ListBuffer assignments = ListBuffer.lb();
        List<String> varnames = this.collectVarnames(leftTupleCall.args);
        JavacASTMaker builder = new JavacASTMaker(tupleAssignNode, (JCTree)leftTupleCall);
        if (leftTupleCall.args.length() == rightTupleCall.args.length()) {
            ListIterator varnameIter = varnames.listIterator();
            HashSet<String> blacklistedNames = new HashSet<String>();
            for (JCTree.JCExpression arg : rightTupleCall.args) {
                String varname = (String)varnameIter.next();
                Boolean canUseSimpleAssignment = (Boolean)new SimpleAssignmentAnalyser(blacklistedNames).scan((Tree)arg, (Object)null);
                blacklistedNames.add(varname);
                if (canUseSimpleAssignment != null && !canUseSimpleAssignment.booleanValue()) {
                    JCTree.JCExpression vartype = (JCTree.JCExpression)new VarTypeFinder(varname, (JCTree)tupleAssignNode.get()).scan((Tree)((JavacNode)tupleAssignNode.top()).get(), (Object)null);
                    if (vartype != null) {
                        String tempVarname = "$tuple" + this.withVarCounter++;
                        tempVarAssignments.append(builder.build(((LocalDecl)AST.LocalDecl(AST.Type((Object)vartype), tempVarname).makeFinal()).withInitialization(AST.Expr((Object)arg)), JCTree.JCStatement.class));
                        assignments.append(builder.build(AST.Assign(AST.Name(varname), AST.Name(tempVarname)), JCTree.JCStatement.class));
                        continue;
                    }
                    tupleAssignNode.addError("Lombok-pg Bug. Unable to find vartype.");
                    return false;
                }
                assignments.append(builder.build(AST.Assign(AST.Name(varname), AST.Expr((Object)arg)), JCTree.JCStatement.class));
            }
        } else {
            JCTree.JCExpression vartype = (JCTree.JCExpression)new VarTypeFinder((String)varnames.get(0), (JCTree)tupleAssignNode.get()).scan((Tree)((JavacNode)tupleAssignNode.top()).get(), (Object)null);
            if (vartype != null) {
                String tempVarname = "$tuple" + this.withVarCounter++;
                tempVarAssignments.append(builder.build(((LocalDecl)AST.LocalDecl(AST.Type((Object)vartype).withDimensions(1), tempVarname).makeFinal()).withInitialization(AST.Expr(rightTupleCall.args.head)), JCTree.JCStatement.class));
                int arrayIndex = 0;
                for (String varname : varnames) {
                    assignments.append(builder.build(AST.Assign(AST.Name(varname), AST.ArrayRef(AST.Name(tempVarname), AST.Number(arrayIndex++))), JCTree.JCStatement.class));
                }
            }
        }
        tempVarAssignments.appendList(assignments);
        this.tryToInjectStatements(tupleAssignNode, (JCTree)tupleAssignNode.get(), tempVarAssignments.toList());
        return true;
    }

    private boolean validateTupel(JavacNode tupleAssignNode, JCTree.JCMethodInvocation leftTupleCall, JCTree.JCMethodInvocation rightTupleCall) {
        if (leftTupleCall.args.length() != rightTupleCall.args.length() && rightTupleCall.args.length() != 1) {
            tupleAssignNode.addError("The left and right hand side of the assignment must have the same amount of arguments or must have one array-type argument for the tuple assignment to work.");
            return false;
        }
        if (!this.containsOnlyNames(leftTupleCall.args)) {
            tupleAssignNode.addError("Only variable names are allowed as arguments of the left hand side in a tuple assignment.");
            return false;
        }
        return true;
    }

    private void tryToInjectStatements(JavacNode node, JCTree nodeThatUsesTupel, List<JCTree.JCStatement> statementsToInject) {
        JavacNode parent = node;
        JCTree statementThatUsesTupel = nodeThatUsesTupel;
        while (!(statementThatUsesTupel instanceof JCTree.JCStatement)) {
            parent = (JavacNode)parent.directUp();
            statementThatUsesTupel = (JCTree)parent.get();
        }
        JCTree.JCStatement statement = (JCTree.JCStatement)statementThatUsesTupel;
        JavacNode grandParent = (JavacNode)parent.directUp();
        JCTree block = (JCTree)grandParent.get();
        if (block instanceof JCTree.JCBlock) {
            ((JCTree.JCBlock)block).stats = this.injectStatements(((JCTree.JCBlock)block).stats, statement, statementsToInject);
        } else if (block instanceof JCTree.JCCase) {
            ((JCTree.JCCase)block).stats = this.injectStatements(((JCTree.JCCase)block).stats, statement, statementsToInject);
        } else if (block instanceof JCTree.JCMethodDecl) {
            ((JCTree.JCMethodDecl)block).body.stats = this.injectStatements(((JCTree.JCMethodDecl)block).body.stats, statement, statementsToInject);
        } else {
            return;
        }
        grandParent.rebuild();
    }

    private List<JCTree.JCStatement> injectStatements(List<JCTree.JCStatement> statements, JCTree.JCStatement statement, List<JCTree.JCStatement> statementsToInject) {
        ListBuffer newStatements = ListBuffer.lb();
        for (JCTree.JCStatement stat : statements) {
            if (stat == statement) {
                newStatements.appendList(statementsToInject);
                continue;
            }
            newStatements.append((Object)stat);
        }
        return newStatements.toList();
    }

    private List<String> collectVarnames(List<JCTree.JCExpression> expressions) {
        ListBuffer varnames = ListBuffer.lb();
        for (JCTree.JCExpression expression : expressions) {
            varnames.append((Object)expression.toString());
        }
        return varnames.toList();
    }

    private boolean containsOnlyNames(List<JCTree.JCExpression> expressions) {
        for (JCTree.JCExpression expression : expressions) {
            if (expression instanceof JCTree.JCIdent) continue;
            return false;
        }
        return true;
    }

    private static class SimpleAssignmentAnalyser
    extends TreeScanner<Boolean, Void> {
        private final Set<String> blacklistedVarnames;

        public Boolean visitMemberSelect(MemberSelectTree node, Void p) {
            return true;
        }

        public Boolean visitIdentifier(IdentifierTree node, Void p) {
            return !this.blacklistedVarnames.contains(node.getName().toString());
        }

        public Boolean reduce(Boolean r1, Boolean r2) {
            return r1 == false && r2 != false;
        }

        public SimpleAssignmentAnalyser(Set<String> blacklistedVarnames) {
            this.blacklistedVarnames = blacklistedVarnames;
        }
    }

    private static class VarTypeFinder
    extends TreeScanner<JCTree.JCExpression, Void> {
        private final String varname;
        private final JCTree expr;
        private boolean lockVarname;

        public JCTree.JCExpression visitVariable(VariableTree node, Void p) {
            if (!this.lockVarname && this.varname.equals(node.getName().toString())) {
                return (JCTree.JCExpression)node.getType();
            }
            return null;
        }

        public JCTree.JCExpression visitAssignment(AssignmentTree node, Void p) {
            if (this.expr != null && this.expr.equals((Object)node)) {
                this.lockVarname = true;
            }
            return (JCTree.JCExpression)super.visitAssignment(node, (Object)p);
        }

        public JCTree.JCExpression reduce(JCTree.JCExpression r1, JCTree.JCExpression r2) {
            return r1 != null ? r1 : r2;
        }

        public VarTypeFinder(String varname, JCTree expr) {
            this.varname = varname;
            this.expr = expr;
        }
    }

}

