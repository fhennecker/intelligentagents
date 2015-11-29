/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ASTVisitor
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.BreakStatement
 *  org.eclipse.jdt.internal.compiler.ast.CaseStatement
 *  org.eclipse.jdt.internal.compiler.ast.ContinueStatement
 *  org.eclipse.jdt.internal.compiler.ast.DoStatement
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.ForStatement
 *  org.eclipse.jdt.internal.compiler.ast.ForeachStatement
 *  org.eclipse.jdt.internal.compiler.ast.IfStatement
 *  org.eclipse.jdt.internal.compiler.ast.LabeledStatement
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.SuperReference
 *  org.eclipse.jdt.internal.compiler.ast.SwitchStatement
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.TrueLiteral
 *  org.eclipse.jdt.internal.compiler.ast.TryStatement
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.ast.WhileStatement
 *  org.eclipse.jdt.internal.compiler.lookup.Binding
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 */
package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Yield;
import lombok.ast.AST;
import lombok.ast.Annotation;
import lombok.ast.Case;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.IMethod;
import lombok.ast.If;
import lombok.ast.Node;
import lombok.ast.Switch;
import lombok.ast.TypeRef;
import lombok.core.handlers.YieldHandler;
import lombok.core.util.As;
import lombok.core.util.Each;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Is;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.Eclipse;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.ast.EclipseMethodEditor;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class HandleYield
extends EclipseASTAdapter {
    @Override
    public void visitStatement(EclipseNode statementNode, Statement statement) {
        String methodName;
        if (statement instanceof MessageSend && Eclipse.isMethodCallValid(statementNode, methodName = Eclipse.getMethodName((MessageSend)statement), Yield.class, "yield")) {
            EclipseMethod method = EclipseMethod.methodOf(statementNode, (ASTNode)statement);
            if (method == null || method.isConstructor()) {
                statementNode.addError(ErrorMessages.canBeUsedInBodyOfMethodsOnly("yield"));
            } else {
                new YieldHandler().handle(method, new EclipseYieldDataCollector());
            }
        }
    }

    private static class EclipseYieldDataCollector
    extends YieldHandler.AbstractYieldDataCollector<EclipseMethod, ASTNode> {
        private final List<SingleNameReference> singleNameReferences = new ArrayList<SingleNameReference>();

        private EclipseYieldDataCollector() {
        }

        @Override
        public String elementType(EclipseMethod method) {
            MethodDeclaration methodDecl = (MethodDeclaration)method.get();
            TypeReference type = methodDecl.returnType;
            if (type instanceof ParameterizedSingleTypeReference) {
                ParameterizedSingleTypeReference returnType = (ParameterizedSingleTypeReference)type;
                if (returnType.typeArguments != null) {
                    return returnType.typeArguments[0].toString();
                }
            }
            return Object.class.getName();
        }

        @Override
        public boolean scan() {
            try {
                ((EclipseMethod)this.method).get().traverse((ASTVisitor)new YieldQuickScanner(), (ClassScope)null);
                return false;
            }
            catch (IllegalStateException ignore) {
                boolean collected;
                AbstractMethodDeclaration decl = ((EclipseMethod)this.method).get();
                int length = decl.statements.length;
                for (int i = 0; i < length; ++i) {
                    decl.statements[i].resolve((BlockScope)decl.scope);
                }
                ValidationScanner scanner = new ValidationScanner();
                ((EclipseMethod)this.method).get().traverse((ASTVisitor)scanner, (ClassScope)null);
                for (SingleNameReference tree : this.singleNameReferences) {
                    tree.actualReceiverType = null;
                    tree.resolvedType = null;
                    tree.binding = null;
                    tree.bits |= 7;
                }
                Iterator i$ = this.yields.iterator();
                while (i$.hasNext()) {
                    YieldHandler.Scope scope;
                    YieldHandler.Scope yieldScope = scope = (YieldHandler.Scope)i$.next();
                    do {
                        this.allScopes.put(yieldScope.node, yieldScope);
                    } while ((yieldScope = yieldScope.parent) != null);
                }
                boolean bl = collected = !this.breaks.isEmpty();
                while (collected) {
                    collected = false;
                    for (YieldHandler.Scope scope : this.breaks) {
                        YieldHandler.Scope target = scope.target;
                        if (target != null && !this.allScopes.containsKey(target.node) || this.allScopes.containsKey(scope.node)) continue;
                        collected = true;
                        YieldHandler.Scope breakScope = scope;
                        do {
                            this.allScopes.put(breakScope.node, breakScope);
                        } while ((breakScope = breakScope.parent) != null);
                    }
                }
                for (YieldHandler.Scope scope : this.variableDecls) {
                    boolean stateVariable = false;
                    if (this.allScopes.containsKey(scope.parent.node)) {
                        stateVariable = true;
                    } else if (scope.parent.node instanceof TryStatement && this.allScopes.containsKey(scope.parent.parent.node)) {
                        stateVariable = true;
                    }
                    if (!stateVariable) continue;
                    LocalDeclaration variable = (LocalDeclaration)scope.node;
                    this.allScopes.put(scope.node, scope);
                    this.stateVariables.add(AST.FieldDecl(AST.Type((Object)variable.type), As.string(variable.name)).makePrivate());
                }
                return true;
            }
        }

        @Override
        public void prepareRefactor() {
            this.root = (YieldHandler.Scope)this.allScopes.get((Object)((EclipseMethod)this.method).get());
        }

        private org.eclipse.jdt.internal.compiler.ast.Expression getYieldExpression(MessageSend invoke) {
            if ("yield".equals(As.string(invoke.selector)) && invoke.arguments != null && invoke.arguments.length == 1) {
                return invoke.arguments[0];
            }
            return null;
        }

        private boolean isTrueLiteral(org.eclipse.jdt.internal.compiler.ast.Expression expression) {
            return expression instanceof TrueLiteral;
        }

        private YieldHandler.Scope<ASTNode> getFinallyScope(YieldHandler.Scope<ASTNode> scope, YieldHandler.Scope<ASTNode> top) {
            ASTNode previous = null;
            while (scope != null) {
                ASTNode tree = (ASTNode)scope.node;
                if (tree instanceof TryStatement) {
                    TryStatement statement = (TryStatement)tree;
                    if (statement.finallyBlock != null && statement.finallyBlock != previous) {
                        return scope;
                    }
                }
                if (scope == top) break;
                previous = tree;
                scope = scope.parent;
            }
            return null;
        }

        static /* synthetic */ YieldHandler.Scope access$700(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$1100(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$1500(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$2000(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$2500(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$2900(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$3300(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$3700(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$4100(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$6400(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$7000(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$7400(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$8200(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$8600(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$9400(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$9800(EclipseYieldDataCollector x0) {
            return x0.current;
        }

        private class ValidationScanner
        extends ASTVisitor {
            private ValidationScanner() {
            }

            public boolean visit(final MethodDeclaration tree, ClassScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        for (Statement statement : Each.elementIn(tree.statements)) {
                            EclipseYieldDataCollector.this.refactorStatement((Object)statement);
                        }
                        EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getBreakLabel(this));
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(MethodDeclaration tree, ClassScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$700((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final Block tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        for (Statement statement : Each.elementIn(tree.statements)) {
                            EclipseYieldDataCollector.this.refactorStatement((Object)statement);
                        }
                        EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getBreakLabel(this));
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(Block tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$1100((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final LabeledStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        EclipseYieldDataCollector.this.refactorStatement((Object)tree.statement);
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(LabeledStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$1500((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final ForStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        for (Statement statement : Each.elementIn(tree.initializations)) {
                            EclipseYieldDataCollector.this.refactorStatement((Object)statement);
                        }
                        Case label = AST.Case();
                        Case breakLabel = EclipseYieldDataCollector.this.getBreakLabel(this);
                        EclipseYieldDataCollector.this.addLabel(label);
                        if (tree.condition != null && !EclipseYieldDataCollector.this.isTrueLiteral(tree.condition)) {
                            EclipseYieldDataCollector.this.addStatement(AST.If(AST.Not(AST.Expr((Object)tree.condition))).Then(AST.Block().withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(breakLabel))).withStatement(AST.Continue())));
                        }
                        EclipseYieldDataCollector.this.refactorStatement((Object)tree.action);
                        EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getIterationLabel(this));
                        for (Statement statement2 : Each.elementIn(tree.increments)) {
                            EclipseYieldDataCollector.this.refactorStatement((Object)statement2);
                        }
                        EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(label)));
                        EclipseYieldDataCollector.this.addStatement(AST.Continue());
                        EclipseYieldDataCollector.this.addLabel(breakLabel);
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(ForStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$2000((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final ForeachStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        String iteratorVar = "$" + As.string(tree.elementVariable.name) + "Iter";
                        EclipseYieldDataCollector.this.stateVariables.add(AST.FieldDecl(AST.Type("java.util.Iterator").withTypeArgument(AST.Type((Object)tree.elementVariable.type)), iteratorVar).makePrivate().withAnnotation(AST.Annotation(AST.Type(SuppressWarnings.class)).withValue(AST.String("all"))));
                        EclipseYieldDataCollector.this.addStatement(AST.Assign(AST.Name(iteratorVar), AST.Call(AST.Expr((Object)tree.collection), "iterator")));
                        EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getIterationLabel(this));
                        EclipseYieldDataCollector.this.addStatement(AST.If(AST.Not(AST.Call(AST.Name(iteratorVar), "hasNext"))).Then(AST.Block().withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(EclipseYieldDataCollector.this.getBreakLabel(this)))).withStatement(AST.Continue())));
                        EclipseYieldDataCollector.this.addStatement(AST.Assign(AST.Name(As.string(tree.elementVariable.name)), AST.Call(AST.Name(iteratorVar), "next")));
                        EclipseYieldDataCollector.this.refactorStatement((Object)tree.action);
                        EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(EclipseYieldDataCollector.this.getIterationLabel(this))));
                        EclipseYieldDataCollector.this.addStatement(AST.Continue());
                        EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getBreakLabel(this));
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(ForeachStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$2500((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final DoStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getIterationLabel(this));
                        EclipseYieldDataCollector.this.refactorStatement((Object)tree.action);
                        EclipseYieldDataCollector.this.addStatement(AST.If(AST.Expr((Object)tree.condition)).Then(AST.Block().withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(this.breakLabel))).withStatement(AST.Continue())));
                        EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getBreakLabel(this));
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(DoStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$2900((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final WhileStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getIterationLabel(this));
                        if (!EclipseYieldDataCollector.this.isTrueLiteral(tree.condition)) {
                            EclipseYieldDataCollector.this.addStatement(AST.If(AST.Not(AST.Expr((Object)tree.condition))).Then(AST.Block().withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(EclipseYieldDataCollector.this.getBreakLabel(this)))).withStatement(AST.Continue())));
                        }
                        EclipseYieldDataCollector.this.refactorStatement((Object)tree.action);
                        EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(EclipseYieldDataCollector.this.getIterationLabel(this))));
                        EclipseYieldDataCollector.this.addStatement(AST.Continue());
                        EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getBreakLabel(this));
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(WhileStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$3300((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final IfStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        Case label = tree.elseStatement == null ? EclipseYieldDataCollector.this.getBreakLabel(this) : AST.Case();
                        EclipseYieldDataCollector.this.addStatement(AST.If(AST.Not(AST.Expr((Object)tree.condition))).Then(AST.Block().withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(label))).withStatement(AST.Continue())));
                        if (tree.elseStatement != null) {
                            EclipseYieldDataCollector.this.refactorStatement((Object)tree.thenStatement);
                            EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(EclipseYieldDataCollector.this.getBreakLabel(this))));
                            EclipseYieldDataCollector.this.addStatement(AST.Continue());
                            EclipseYieldDataCollector.this.addLabel(label);
                            EclipseYieldDataCollector.this.refactorStatement((Object)tree.elseStatement);
                            EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getBreakLabel(this));
                        } else {
                            EclipseYieldDataCollector.this.refactorStatement((Object)tree.thenStatement);
                            EclipseYieldDataCollector.this.addLabel(EclipseYieldDataCollector.this.getBreakLabel(this));
                        }
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(IfStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$3700((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final SwitchStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        Case breakLabel = EclipseYieldDataCollector.this.getBreakLabel(this);
                        Switch switchStatement = AST.Switch(AST.Expr((Object)tree.expression));
                        EclipseYieldDataCollector.this.addStatement(switchStatement);
                        if (Is.notEmpty(tree.statements)) {
                            boolean hasDefault = false;
                            for (Statement statement : tree.statements) {
                                if (statement instanceof CaseStatement) {
                                    CaseStatement caseStatement = (CaseStatement)statement;
                                    if (caseStatement.constantExpression == null) {
                                        hasDefault = true;
                                    }
                                    Case label = AST.Case();
                                    switchStatement.withCase(AST.Case(AST.Expr((Object)caseStatement.constantExpression)).withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(label))).withStatement(AST.Continue()));
                                    EclipseYieldDataCollector.this.addLabel(label);
                                    continue;
                                }
                                EclipseYieldDataCollector.this.refactorStatement((Object)statement);
                            }
                            if (!hasDefault) {
                                switchStatement.withCase(AST.Case().withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(breakLabel))).withStatement(AST.Continue()));
                            }
                        }
                        EclipseYieldDataCollector.this.addLabel(breakLabel);
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(SwitchStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$4100((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final TryStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        Case finallyLabel;
                        boolean hasFinally = tree.finallyBlock != null;
                        boolean hasCatch = Is.notEmpty(tree.catchArguments);
                        YieldHandler.ErrorHandler catchHandler = null;
                        YieldHandler.ErrorHandler finallyHandler = null;
                        Case tryLabel = AST.Case();
                        Case breakLabel = EclipseYieldDataCollector.this.getBreakLabel(this);
                        String finallyErrorName = null;
                        if (hasFinally) {
                            finallyHandler = new YieldHandler.ErrorHandler();
                            finallyLabel = EclipseYieldDataCollector.this.getFinallyLabel(this);
                            EclipseYieldDataCollector.this.finallyBlocks++;
                            finallyErrorName = EclipseYieldDataCollector.this.errorName + EclipseYieldDataCollector.this.finallyBlocks;
                            this.labelName = "$state" + EclipseYieldDataCollector.this.finallyBlocks;
                            EclipseYieldDataCollector.this.stateVariables.add(AST.FieldDecl(AST.Type(Throwable.class), finallyErrorName).makePrivate());
                            EclipseYieldDataCollector.this.stateVariables.add(AST.FieldDecl(AST.Type("int"), this.labelName).makePrivate());
                            EclipseYieldDataCollector.this.addStatement(AST.Assign(AST.Name(finallyErrorName), AST.Null()));
                            EclipseYieldDataCollector.this.addStatement(AST.Assign(AST.Name(this.labelName), EclipseYieldDataCollector.this.literal(breakLabel)));
                        } else {
                            finallyLabel = breakLabel;
                        }
                        EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(tryLabel)));
                        if (hasCatch) {
                            catchHandler = new YieldHandler.ErrorHandler();
                            catchHandler.begin = EclipseYieldDataCollector.this.cases.size();
                        } else if (hasFinally) {
                            finallyHandler.begin = EclipseYieldDataCollector.this.cases.size();
                        }
                        EclipseYieldDataCollector.this.addLabel(tryLabel);
                        EclipseYieldDataCollector.this.refactorStatement((Object)tree.tryBlock);
                        EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(finallyLabel)));
                        if (hasCatch) {
                            EclipseYieldDataCollector.this.addStatement(AST.Continue());
                            catchHandler.end = EclipseYieldDataCollector.this.cases.size();
                            int numberOfCatchBlocks = tree.catchArguments.length;
                            for (int i = 0; i < numberOfCatchBlocks; ++i) {
                                Argument argument = tree.catchArguments[i];
                                Block block = tree.catchBlocks[i];
                                Case label = AST.Case();
                                EclipseYieldDataCollector.this.usedLabels.add(label);
                                EclipseYieldDataCollector.this.addLabel(label);
                                EclipseYieldDataCollector.this.refactorStatement((Object)block);
                                EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(finallyLabel)));
                                EclipseYieldDataCollector.this.addStatement(AST.Continue());
                                catchHandler.statements.add(AST.If(AST.InstanceOf(AST.Name(EclipseYieldDataCollector.this.errorName), AST.Type((Object)argument.type))).Then(AST.Block().withStatement(AST.Assign(AST.Name(As.string(argument.name)), AST.Cast(AST.Type((Object)argument.type), AST.Name(EclipseYieldDataCollector.this.errorName)))).withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(label))).withStatement(AST.Continue())));
                            }
                            EclipseYieldDataCollector.this.errorHandlers.add(catchHandler);
                            if (hasFinally) {
                                finallyHandler.begin = catchHandler.end;
                            }
                        }
                        if (hasFinally) {
                            finallyHandler.end = EclipseYieldDataCollector.this.cases.size();
                            EclipseYieldDataCollector.this.addLabel(finallyLabel);
                            EclipseYieldDataCollector.this.refactorStatement((Object)tree.finallyBlock);
                            EclipseYieldDataCollector.this.addStatement(AST.If(AST.NotEqual(AST.Name(finallyErrorName), AST.Null())).Then(AST.Block().withStatement(AST.Assign(AST.Name(EclipseYieldDataCollector.this.errorName), AST.Name(finallyErrorName))).withStatement(AST.Break())));
                            YieldHandler.Scope next = EclipseYieldDataCollector.this.getFinallyScope(this.parent, null);
                            if (next != null) {
                                Case label = EclipseYieldDataCollector.this.getFinallyLabel(next);
                                EclipseYieldDataCollector.this.addStatement(AST.If(AST.Binary(AST.Name(this.labelName), ">", EclipseYieldDataCollector.this.literal(label))).Then(AST.Block().withStatement(AST.Assign(AST.Name(next.labelName), AST.Name(this.labelName))).withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(label))).withStatement(EclipseYieldDataCollector.this.setState(AST.Name(this.labelName)))));
                            } else {
                                EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(AST.Name(this.labelName)));
                            }
                            EclipseYieldDataCollector.this.addStatement(AST.Continue());
                            finallyHandler.statements.add(AST.Assign(AST.Name(finallyErrorName), AST.Name(EclipseYieldDataCollector.this.errorName)));
                            finallyHandler.statements.add(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(finallyLabel)));
                            finallyHandler.statements.add(AST.Continue());
                            EclipseYieldDataCollector.this.usedLabels.add(finallyLabel);
                            EclipseYieldDataCollector.this.errorHandlers.add(finallyHandler);
                        }
                        EclipseYieldDataCollector.this.addLabel(breakLabel);
                    }
                };
                return super.visit(tree, scope);
            }

            public void endVisit(TryStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$6400((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(final LocalDeclaration tree, BlockScope scope) {
                EclipseYieldDataCollector.this.variableDecls.add(new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        if (tree.initialization != null) {
                            if (tree.initialization instanceof ArrayInitializer) {
                                ArrayInitializer initializer = (ArrayInitializer)tree.initialization;
                                ArrayAllocationExpression allocation = new ArrayAllocationExpression();
                                allocation.type = (TypeReference)((EclipseMethod)EclipseYieldDataCollector.this.method).editor().build(AST.Type(tree.type.toString()));
                                allocation.initializer = initializer;
                                allocation.dimensions = new org.eclipse.jdt.internal.compiler.ast.Expression[tree.type.dimensions()];
                                EclipseYieldDataCollector.this.addStatement(AST.Assign(AST.Name(As.string(tree.name)), AST.Expr((Object)allocation)));
                            } else {
                                EclipseYieldDataCollector.this.addStatement(AST.Assign(AST.Name(As.string(tree.name)), AST.Expr((Object)tree.initialization)));
                            }
                        }
                    }
                });
                return super.visit(tree, scope);
            }

            public boolean visit(Argument tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                    }
                };
                if (!(EclipseYieldDataCollector.access$7000((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent.node instanceof MethodDeclaration)) {
                    EclipseYieldDataCollector.this.variableDecls.add(EclipseYieldDataCollector.this.current);
                }
                return super.visit(tree, scope);
            }

            public void endVisit(Argument tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$7400((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(ReturnStatement tree, BlockScope scope) {
                ((EclipseMethod)EclipseYieldDataCollector.this.method).node().addError("The 'return' expression is permitted.");
                return false;
            }

            public void endVisit(ReturnStatement tree, BlockScope scope) {
            }

            public boolean visit(BreakStatement tree, BlockScope scope) {
                YieldHandler.Scope target;
                target = null;
                char[] label = tree.label;
                if (label != null) {
                    YieldHandler.Scope labelScope = EclipseYieldDataCollector.this.current;
                    while (labelScope != null) {
                        if (labelScope.node instanceof LabeledStatement) {
                            LabeledStatement labeledStatement = (LabeledStatement)labelScope.node;
                            if (Arrays.equals(label, labeledStatement.label)) {
                                if (target != null) {
                                    ((EclipseMethod)EclipseYieldDataCollector.this.method).node().addError("Invalid label.");
                                }
                                target = labelScope;
                            }
                        }
                        labelScope = labelScope.parent;
                    }
                } else {
                    YieldHandler.Scope labelScope = EclipseYieldDataCollector.this.current;
                    while (labelScope != null) {
                        if (Is.oneOf(labelScope.node, ForStatement.class, ForeachStatement.class, WhileStatement.class, DoStatement.class, SwitchStatement.class)) {
                            target = labelScope;
                            break;
                        }
                        labelScope = labelScope.parent;
                    }
                }
                if (target == null) {
                    ((EclipseMethod)EclipseYieldDataCollector.this.method).node().addError("Invalid break.");
                }
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        YieldHandler.Scope next = EclipseYieldDataCollector.this.getFinallyScope(this.parent, this.target);
                        Case label = EclipseYieldDataCollector.this.getBreakLabel(this.target);
                        if (next == null) {
                            EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(label)));
                            EclipseYieldDataCollector.this.addStatement(AST.Continue());
                        } else {
                            EclipseYieldDataCollector.this.addStatement(AST.Assign(AST.Name(next.labelName), EclipseYieldDataCollector.this.literal(label)));
                            EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(EclipseYieldDataCollector.this.getFinallyLabel(next))));
                            EclipseYieldDataCollector.this.addStatement(AST.Continue());
                        }
                    }
                };
                EclipseYieldDataCollector.access$8200((EclipseYieldDataCollector)EclipseYieldDataCollector.this).target = target;
                EclipseYieldDataCollector.this.breaks.add(EclipseYieldDataCollector.this.current);
                return false;
            }

            public void endVisit(BreakStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$8600((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(ContinueStatement tree, BlockScope scope) {
                YieldHandler.Scope target;
                target = null;
                char[] label = tree.label;
                if (label != null) {
                    YieldHandler.Scope labelScope = EclipseYieldDataCollector.this.current;
                    while (labelScope != null) {
                        if (labelScope.node instanceof LabeledStatement) {
                            LabeledStatement labeledStatement = (LabeledStatement)labelScope.node;
                            if (label == labeledStatement.label) {
                                if (target != null) {
                                    ((EclipseMethod)EclipseYieldDataCollector.this.method).node().addError("Invalid label.");
                                }
                                if (Is.oneOf(labelScope.node, ForStatement.class, ForeachStatement.class, WhileStatement.class, DoStatement.class)) {
                                    target = labelScope;
                                } else {
                                    ((EclipseMethod)EclipseYieldDataCollector.this.method).node().addError("Invalid continue.");
                                }
                            }
                        }
                        labelScope = labelScope.parent;
                    }
                } else {
                    YieldHandler.Scope labelScope = EclipseYieldDataCollector.this.current;
                    while (labelScope != null) {
                        if (Is.oneOf(labelScope.node, ForStatement.class, ForeachStatement.class, WhileStatement.class, DoStatement.class)) {
                            target = labelScope;
                            break;
                        }
                        labelScope = labelScope.parent;
                    }
                }
                if (target == null) {
                    ((EclipseMethod)EclipseYieldDataCollector.this.method).node().addError("Invalid continue.");
                }
                EclipseYieldDataCollector.this.current = new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                    @Override
                    public void refactor() {
                        YieldHandler.Scope next = EclipseYieldDataCollector.this.getFinallyScope(this.parent, this.target);
                        Case label = EclipseYieldDataCollector.this.getIterationLabel(this.target);
                        if (next == null) {
                            EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(label)));
                            EclipseYieldDataCollector.this.addStatement(AST.Continue());
                        } else {
                            EclipseYieldDataCollector.this.addStatement(AST.Assign(AST.Name(next.labelName), EclipseYieldDataCollector.this.literal(label)));
                            EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(EclipseYieldDataCollector.this.getFinallyLabel(next))));
                            EclipseYieldDataCollector.this.addStatement(AST.Continue());
                        }
                    }
                };
                EclipseYieldDataCollector.access$9400((EclipseYieldDataCollector)EclipseYieldDataCollector.this).target = target;
                EclipseYieldDataCollector.this.breaks.add(EclipseYieldDataCollector.this.current);
                return false;
            }

            public void endVisit(ContinueStatement tree, BlockScope scope) {
                EclipseYieldDataCollector.this.current = EclipseYieldDataCollector.access$9800((EclipseYieldDataCollector)EclipseYieldDataCollector.this).parent;
            }

            public boolean visit(ThisReference tree, BlockScope scope) {
                if (!tree.isImplicitThis()) {
                    ((EclipseMethod)EclipseYieldDataCollector.this.method).node().addError("No unqualified 'this' expression is permitted.");
                }
                return false;
            }

            public boolean visit(SuperReference tree, BlockScope scope) {
                ((EclipseMethod)EclipseYieldDataCollector.this.method).node().addError("No unqualified 'super' expression is permitted.");
                return false;
            }

            public boolean visit(SingleNameReference tree, BlockScope scope) {
                EclipseYieldDataCollector.this.singleNameReferences.add(tree);
                return super.visit(tree, scope);
            }

            public boolean visit(MessageSend tree, BlockScope scope) {
                String name;
                final org.eclipse.jdt.internal.compiler.ast.Expression expression = EclipseYieldDataCollector.this.getYieldExpression(tree);
                if (expression != null) {
                    EclipseYieldDataCollector.this.yields.add(new YieldHandler.Scope<ASTNode>(EclipseYieldDataCollector.this.current, (ASTNode)tree){

                        @Override
                        public void refactor() {
                            Case label = EclipseYieldDataCollector.this.getBreakLabel(this);
                            EclipseYieldDataCollector.this.addStatement(AST.Assign(AST.Name(EclipseYieldDataCollector.this.nextName), AST.Expr((Object)expression)));
                            EclipseYieldDataCollector.this.addStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(label)));
                            EclipseYieldDataCollector.this.addStatement(AST.Return(AST.True()));
                            EclipseYieldDataCollector.this.addLabel(label);
                            YieldHandler.Scope next = EclipseYieldDataCollector.this.getFinallyScope(this.parent, null);
                            if (next != null) {
                                EclipseYieldDataCollector.this.breakCases.add(new Case(EclipseYieldDataCollector.this.literal(label)).withStatement(AST.Assign(AST.Name(next.labelName), EclipseYieldDataCollector.this.literal(EclipseYieldDataCollector.this.getBreakLabel(EclipseYieldDataCollector.this.root)))).withStatement(EclipseYieldDataCollector.this.setState(EclipseYieldDataCollector.this.literal(EclipseYieldDataCollector.this.getFinallyLabel(next)))).withStatement(AST.Continue()));
                            }
                        }
                    });
                    expression.traverse((ASTVisitor)this, scope);
                    return false;
                }
                if (tree.receiver.isImplicitThis() && Is.oneOf(name = As.string(tree.selector), "hasNext", "next", "remove", "close")) {
                    ((EclipseMethod)EclipseYieldDataCollector.this.method).node().addError(String.format("Cannot call method %s(), as it is hidden.", name));
                }
                return super.visit(tree, scope);
            }

        }

        private class YieldQuickScanner
        extends ASTVisitor {
            private YieldQuickScanner() {
            }

            public boolean visit(MessageSend tree, BlockScope scope) {
                org.eclipse.jdt.internal.compiler.ast.Expression expression = EclipseYieldDataCollector.this.getYieldExpression(tree);
                if (expression != null) {
                    throw new IllegalStateException();
                }
                return super.visit(tree, scope);
            }
        }

    }

}

