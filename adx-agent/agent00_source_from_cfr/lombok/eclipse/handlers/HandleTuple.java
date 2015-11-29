/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ASTVisitor
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.Assignment
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 */
package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import lombok.Tuple;
import lombok.ast.AST;
import lombok.ast.Expression;
import lombok.ast.LocalDecl;
import lombok.ast.Node;
import lombok.ast.TypeRef;
import lombok.core.util.Arrays;
import lombok.core.util.Each;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Is;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.Eclipse;
import lombok.eclipse.handlers.ast.EclipseASTMaker;
import lombok.eclipse.handlers.ast.EclipseMethod;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;

public class HandleTuple
extends EclipseASTAdapter {
    private final Set<String> methodNames = new HashSet<String>();
    private int withVarCounter;

    @Override
    public void visitCompilationUnit(EclipseNode top, CompilationUnitDeclaration unit) {
        this.methodNames.clear();
        this.withVarCounter = 0;
    }

    @Override
    public void visitLocal(EclipseNode localNode, LocalDeclaration local) {
        MessageSend initTupleCall = this.getTupelCall(localNode, local.initialization);
        if (initTupleCall != null) {
            EclipseMethod method = EclipseMethod.methodOf(localNode, (ASTNode)local);
            if (method == null) {
                localNode.addError(ErrorMessages.canBeUsedInBodyOfMethodsOnly("tuple"));
            } else if (this.handle(localNode, initTupleCall)) {
                this.methodNames.add(Eclipse.getMethodName(initTupleCall));
            }
        }
    }

    @Override
    public void visitStatement(EclipseNode statementNode, Statement statement) {
        if (statement instanceof Assignment) {
            Assignment assignment = (Assignment)statement;
            MessageSend leftTupleCall = this.getTupelCall(statementNode, assignment.lhs);
            MessageSend rightTupleCall = this.getTupelCall(statementNode, assignment.expression);
            if (leftTupleCall != null && rightTupleCall != null) {
                EclipseMethod method = EclipseMethod.methodOf(statementNode, (ASTNode)statement);
                if (method == null) {
                    statementNode.addError(ErrorMessages.canBeUsedInBodyOfMethodsOnly("tuple"));
                } else if (this.handle(statementNode, leftTupleCall, rightTupleCall)) {
                    this.methodNames.add(Eclipse.getMethodName(leftTupleCall));
                    this.methodNames.add(Eclipse.getMethodName(rightTupleCall));
                }
            }
        }
    }

    private MessageSend getTupelCall(EclipseNode node, org.eclipse.jdt.internal.compiler.ast.Expression expression) {
        MessageSend tupleCall;
        String methodName;
        if (expression instanceof MessageSend && Eclipse.isMethodCallValid(node, methodName = Eclipse.getMethodName(tupleCall = (MessageSend)expression), Tuple.class, "tuple")) {
            return tupleCall;
        }
        return null;
    }

    @Override
    public void endVisitCompilationUnit(EclipseNode top, CompilationUnitDeclaration unit) {
        for (String methodName : this.methodNames) {
            Eclipse.deleteMethodCallImports(top, methodName, Tuple.class, "tuple");
        }
    }

    public boolean handle(EclipseNode tupleInitNode, MessageSend initTupleCall) {
        if (Is.empty(initTupleCall.arguments)) {
            return true;
        }
        int numberOfArguments = initTupleCall.arguments.length;
        ArrayList<LocalDeclaration> localDecls = new ArrayList<LocalDeclaration>();
        String type = ((LocalDeclaration)tupleInitNode.get()).type.toString();
        for (EclipseNode node : ((EclipseNode)tupleInitNode.directUp()).down()) {
            if (!(node.get() instanceof LocalDeclaration)) continue;
            LocalDeclaration localDecl = (LocalDeclaration)node.get();
            if (!type.equals(localDecl.type.toString())) continue;
            localDecls.add(localDecl);
            if (localDecls.size() > numberOfArguments) {
                localDecls.remove(0);
            }
            if (!node.equals(tupleInitNode)) continue;
            break;
        }
        if (numberOfArguments != localDecls.size()) {
            tupleInitNode.addError(String.format("Argument mismatch on the right side. (required: %s found: %s)", localDecls.size(), numberOfArguments));
            return false;
        }
        int index = 0;
        for (LocalDeclaration localDecl : localDecls) {
            localDecl.initialization = initTupleCall.arguments[index++];
        }
        return true;
    }

    public boolean handle(EclipseNode tupleAssignNode, MessageSend leftTupleCall, MessageSend rightTupleCall) {
        if (!this.validateTupel(tupleAssignNode, leftTupleCall, rightTupleCall)) {
            return false;
        }
        ArrayList<Statement> tempVarAssignments = new ArrayList<Statement>();
        ArrayList assignments = new ArrayList();
        List<String> varnames = this.collectVarnames(leftTupleCall.arguments);
        EclipseASTMaker builder = new EclipseASTMaker(tupleAssignNode, (ASTNode)leftTupleCall);
        if (Arrays.sameSize(leftTupleCall.arguments, rightTupleCall.arguments)) {
            ListIterator<String> varnameIter = varnames.listIterator();
            HashSet<String> blacklistedNames = new HashSet<String>();
            for (org.eclipse.jdt.internal.compiler.ast.Expression arg : Each.elementIn(rightTupleCall.arguments)) {
                String varname = varnameIter.next();
                boolean canUseSimpleAssignment = new SimpleAssignmentAnalyser(blacklistedNames).scan((ASTNode)arg);
                blacklistedNames.add(varname);
                if (!canUseSimpleAssignment) {
                    TypeReference vartype = new VarTypeFinder(varname, (ASTNode)tupleAssignNode.get()).scan((ASTNode)((EclipseNode)tupleAssignNode.top()).get());
                    if (vartype != null) {
                        String tempVarname = "$tuple" + this.withVarCounter++;
                        tempVarAssignments.add((Statement)builder.build(((LocalDecl)AST.LocalDecl(AST.Type((Object)vartype), tempVarname).makeFinal()).withInitialization(AST.Expr((Object)arg)), Statement.class));
                        assignments.add(builder.build(AST.Assign(AST.Name(varname), AST.Name(tempVarname)), Statement.class));
                        continue;
                    }
                    tupleAssignNode.addError("Lombok-pg Bug. Unable to find vartype.");
                    return false;
                }
                assignments.add(builder.build(AST.Assign(AST.Name(varname), AST.Expr((Object)arg)), Statement.class));
            }
        } else {
            TypeReference vartype = new VarTypeFinder(varnames.get(0), (ASTNode)tupleAssignNode.get()).scan((ASTNode)((EclipseNode)tupleAssignNode.top()).get());
            if (vartype != null) {
                String tempVarname = "$tuple" + this.withVarCounter++;
                tempVarAssignments.add((Statement)builder.build(((LocalDecl)AST.LocalDecl(AST.Type((Object)vartype).withDimensions(1), tempVarname).makeFinal()).withInitialization(AST.Expr((Object)rightTupleCall.arguments[0])), Statement.class));
                int arrayIndex = 0;
                for (String varname : varnames) {
                    assignments.add(builder.build(AST.Assign(AST.Name(varname), AST.ArrayRef(AST.Name(tempVarname), AST.Number(arrayIndex++))), Statement.class));
                }
            }
        }
        tempVarAssignments.addAll(assignments);
        this.tryToInjectStatements(tupleAssignNode, (ASTNode)tupleAssignNode.get(), tempVarAssignments);
        return true;
    }

    private boolean validateTupel(EclipseNode tupleAssignNode, MessageSend leftTupleCall, MessageSend rightTupleCall) {
        if (!Arrays.sameSize(leftTupleCall.arguments, rightTupleCall.arguments) && rightTupleCall.arguments.length != 1) {
            tupleAssignNode.addError("The left and right hand side of the assignment must have the same amount of arguments or must have one array-type argument for the tuple assignment to work.");
            return false;
        }
        if (!this.containsOnlyNames(leftTupleCall.arguments)) {
            tupleAssignNode.addError("Only variable names are allowed as arguments of the left hand side in a tuple assignment.");
            return false;
        }
        return true;
    }

    private void tryToInjectStatements(EclipseNode node, ASTNode nodeThatUsesTupel, List<Statement> statementsToInject) {
        EclipseNode parent = node;
        ASTNode statementThatUsesTupel = nodeThatUsesTupel;
        while (!(((EclipseNode)parent.directUp()).get() instanceof AbstractMethodDeclaration) && !(((EclipseNode)parent.directUp()).get() instanceof Block)) {
            parent = (EclipseNode)parent.directUp();
            statementThatUsesTupel = (ASTNode)parent.get();
        }
        Statement statement = (Statement)statementThatUsesTupel;
        EclipseNode grandParent = (EclipseNode)parent.directUp();
        ASTNode block = (ASTNode)grandParent.get();
        if (block instanceof Block) {
            ((Block)block).statements = HandleTuple.injectStatements(((Block)block).statements, statement, statementsToInject);
        } else if (block instanceof AbstractMethodDeclaration) {
            ((AbstractMethodDeclaration)block).statements = HandleTuple.injectStatements(((AbstractMethodDeclaration)block).statements, statement, statementsToInject);
        } else {
            return;
        }
        grandParent.rebuild();
    }

    private static Statement[] injectStatements(Statement[] statements, Statement statement, List<Statement> withCallStatements) {
        ArrayList<Statement> newStatements = new ArrayList<Statement>();
        for (Statement stat : statements) {
            if (stat == statement) {
                newStatements.addAll(withCallStatements);
                continue;
            }
            newStatements.add(stat);
        }
        return newStatements.toArray((T[])new Statement[newStatements.size()]);
    }

    private List<String> collectVarnames(org.eclipse.jdt.internal.compiler.ast.Expression[] expressions) {
        ArrayList<String> varnames = new ArrayList<String>();
        if (expressions != null) {
            for (org.eclipse.jdt.internal.compiler.ast.Expression expression : expressions) {
                varnames.add(new String(((SingleNameReference)expression).token));
            }
        }
        return varnames;
    }

    private boolean containsOnlyNames(org.eclipse.jdt.internal.compiler.ast.Expression[] expressions) {
        if (expressions != null) {
            for (org.eclipse.jdt.internal.compiler.ast.Expression expression : expressions) {
                if (expression instanceof SingleNameReference) continue;
                return false;
            }
        }
        return true;
    }

    private static class SimpleAssignmentAnalyser
    extends ASTVisitor {
        private final Set<String> blacklistedVarnames;
        private boolean canUseSimpleAssignment;

        public boolean scan(ASTNode astNode) {
            this.canUseSimpleAssignment = true;
            if (astNode instanceof CompilationUnitDeclaration) {
                ((CompilationUnitDeclaration)astNode).traverse((ASTVisitor)this, (CompilationUnitScope)null);
            } else if (astNode instanceof MethodDeclaration) {
                ((MethodDeclaration)astNode).traverse((ASTVisitor)this, (ClassScope)null);
            } else {
                astNode.traverse((ASTVisitor)this, null);
            }
            return this.canUseSimpleAssignment;
        }

        public boolean visit(SingleNameReference singleNameReference, BlockScope scope) {
            if (this.blacklistedVarnames.contains(new String(singleNameReference.token))) {
                this.canUseSimpleAssignment = false;
                return false;
            }
            return true;
        }

        public SimpleAssignmentAnalyser(Set<String> blacklistedVarnames) {
            this.blacklistedVarnames = blacklistedVarnames;
        }
    }

    private static class VarTypeFinder
    extends ASTVisitor {
        private final String varname;
        private final ASTNode expr;
        private boolean lockVarname;
        private TypeReference vartype;

        public TypeReference scan(ASTNode astNode) {
            if (astNode instanceof CompilationUnitDeclaration) {
                ((CompilationUnitDeclaration)astNode).traverse((ASTVisitor)this, (CompilationUnitScope)null);
            } else if (astNode instanceof MethodDeclaration) {
                ((MethodDeclaration)astNode).traverse((ASTVisitor)this, (ClassScope)null);
            } else {
                astNode.traverse((ASTVisitor)this, null);
            }
            return this.vartype;
        }

        public boolean visit(LocalDeclaration localDeclaration, BlockScope scope) {
            return this.visit((AbstractVariableDeclaration)localDeclaration);
        }

        public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
            return this.visit((AbstractVariableDeclaration)fieldDeclaration);
        }

        public boolean visit(Argument argument, BlockScope scope) {
            return this.visit((AbstractVariableDeclaration)argument);
        }

        public boolean visit(Argument argument, ClassScope scope) {
            return this.visit((AbstractVariableDeclaration)argument);
        }

        public boolean visit(Assignment assignment, BlockScope scope) {
            if (this.expr != null && this.expr.equals((Object)assignment)) {
                this.lockVarname = true;
            }
            return true;
        }

        public boolean visit(AbstractVariableDeclaration variableDeclaration) {
            if (!this.lockVarname && this.varname.equals(new String(variableDeclaration.name))) {
                this.vartype = variableDeclaration.type;
            }
            return true;
        }

        public VarTypeFinder(String varname, ASTNode expr) {
            this.varname = varname;
            this.expr = expr;
        }
    }

}

