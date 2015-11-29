/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCBreak
 *  com.sun.tools.javac.tree.JCTree$JCCase
 *  com.sun.tools.javac.tree.JCTree$JCCatch
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCContinue
 *  com.sun.tools.javac.tree.JCTree$JCDoWhileLoop
 *  com.sun.tools.javac.tree.JCTree$JCEnhancedForLoop
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCForLoop
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCIf
 *  com.sun.tools.javac.tree.JCTree$JCLabeledStatement
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCNewClass
 *  com.sun.tools.javac.tree.JCTree$JCParens
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCSwitch
 *  com.sun.tools.javac.tree.JCTree$JCTry
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.JCTree$JCWhileLoop
 *  com.sun.tools.javac.tree.TreeScanner
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import lombok.Yield;
import lombok.ast.AST;
import lombok.ast.Annotation;
import lombok.ast.Block;
import lombok.ast.Case;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.IMethod;
import lombok.ast.If;
import lombok.ast.Node;
import lombok.ast.Statement;
import lombok.ast.Switch;
import lombok.ast.TypeRef;
import lombok.core.handlers.YieldHandler;
import lombok.core.util.As;
import lombok.core.util.Each;
import lombok.core.util.ErrorMessages;
import lombok.core.util.Is;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacNode;
import lombok.javac.handlers.HandleVal;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacMethodEditor;

public class HandleYield
extends JavacASTAdapter {
    private final Set<String> methodNames = new HashSet<String>();

    @Override
    public void visitCompilationUnit(JavacNode top, JCTree.JCCompilationUnit unit) {
        this.methodNames.clear();
    }

    @Override
    public void visitStatement(JavacNode statementNode, JCTree statement) {
        if (statement instanceof JCTree.JCMethodInvocation) {
            JCTree.JCMethodInvocation methodCall = (JCTree.JCMethodInvocation)statement;
            String methodName = methodCall.meth.toString();
            if (Javac.isMethodCallValid(statementNode, methodName, Yield.class, "yield")) {
                JavacMethod method = JavacMethod.methodOf(statementNode, statement);
                if (method == null || method.isConstructor()) {
                    statementNode.addError(ErrorMessages.canBeUsedInBodyOfMethodsOnly("yield"));
                    return;
                }
                if (JavacHandlerUtil.inNetbeansEditor(statementNode)) {
                    if (!(method.get().body.stats.last() instanceof JCTree.JCReturn)) {
                        method.get().body.stats = method.get().body.stats.append(method.editor().build(AST.Return(AST.Null()), JCTree.JCStatement.class));
                    }
                    return;
                }
                if (new YieldHandler().handle(method, new JavacYieldDataCollector())) {
                    this.methodNames.add(methodName);
                }
            }
        }
    }

    @Override
    public void endVisitCompilationUnit(JavacNode top, JCTree.JCCompilationUnit unit) {
        for (String methodName : this.methodNames) {
            Javac.deleteMethodCallImports(top, methodName, Yield.class, "yield");
        }
    }

    private static class JavacYieldDataCollector
    extends YieldHandler.AbstractYieldDataCollector<JavacMethod, JCTree> {
        private JavacYieldDataCollector() {
        }

        @Override
        public String elementType(JavacMethod method) {
            JCTree.JCExpression type = method.get().restype;
            if (type instanceof JCTree.JCTypeApply) {
                JCTree.JCTypeApply returnType = (JCTree.JCTypeApply)type;
                if (!returnType.arguments.isEmpty()) {
                    return ((JCTree.JCExpression)returnType.arguments.head).type.toString();
                }
            }
            return Object.class.getName();
        }

        @Override
        public boolean scan() {
            try {
                new YieldQuickScanner().scan((JCTree)((JavacMethod)this.method).get().body);
                return false;
            }
            catch (IllegalStateException ignore) {
                boolean collected;
                ((JavacMethod)this.method).node().traverse(new HandleVal());
                YieldScanner scanner = new YieldScanner();
                scanner.scan((JCTree)((JavacMethod)this.method).get().body);
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
                    } else if (scope.parent.node instanceof JCTree.JCCatch && this.allScopes.containsKey(scope.parent.parent.node)) {
                        stateVariable = true;
                    }
                    if (!stateVariable) continue;
                    JCTree.JCVariableDecl variable = (JCTree.JCVariableDecl)scope.node;
                    this.allScopes.put(scope.node, scope);
                    this.stateVariables.add(AST.FieldDecl(AST.Type((Object)variable.vartype), As.string((Object)variable.name)).makePrivate());
                }
                return true;
            }
        }

        @Override
        public void prepareRefactor() {
            this.root = (YieldHandler.Scope)this.allScopes.get((Object)((JavacMethod)this.method).get().body);
        }

        private JCTree.JCExpression getYieldExpression(JCTree.JCExpression expr) {
            if (expr instanceof JCTree.JCMethodInvocation) {
                JCTree.JCMethodInvocation methodCall = (JCTree.JCMethodInvocation)expr;
                if (methodCall.meth.toString().endsWith("yield") && methodCall.args.length() == 1) {
                    return (JCTree.JCExpression)methodCall.args.head;
                }
            }
            return null;
        }

        private boolean isTrueLiteral(JCTree.JCExpression expression) {
            if (expression instanceof JCTree.JCLiteral) {
                return "true".equals(expression.toString());
            }
            if (expression instanceof JCTree.JCParens) {
                return this.isTrueLiteral(((JCTree.JCParens)expression).expr);
            }
            return false;
        }

        private YieldHandler.Scope<JCTree> getFinallyScope(YieldHandler.Scope<JCTree> scope, YieldHandler.Scope<JCTree> top) {
            JCTree previous = null;
            while (scope != null) {
                JCTree tree = (JCTree)scope.node;
                if (tree instanceof JCTree.JCTry) {
                    JCTree.JCTry statement = (JCTree.JCTry)tree;
                    if (statement.finalizer != null && statement.finalizer != previous) {
                        return scope;
                    }
                }
                if (scope == top) break;
                previous = tree;
                scope = scope.parent;
            }
            return null;
        }

        static /* synthetic */ YieldHandler.Scope access$700(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$1100(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$1600(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$2100(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$2500(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$2900(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$3300(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$3700(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$6000(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$6600(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$7400(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$7800(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$8600(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$9000(JavacYieldDataCollector x0) {
            return x0.current;
        }

        static /* synthetic */ YieldHandler.Scope access$10000(JavacYieldDataCollector x0) {
            return x0.current;
        }

        private class YieldScanner
        extends TreeScanner {
            private YieldScanner() {
            }

            public void visitBlock(final JCTree.JCBlock tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        for (JCTree.JCStatement statement : Each.elementIn(tree.stats)) {
                            JavacYieldDataCollector.this.refactorStatement((Object)statement);
                        }
                        JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getBreakLabel(this));
                    }
                };
                super.visitBlock(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$700((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitLabelled(final JCTree.JCLabeledStatement tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        JavacYieldDataCollector.this.refactorStatement((Object)tree.body);
                    }
                };
                super.visitLabelled(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$1100((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitForLoop(final JCTree.JCForLoop tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        for (JCTree.JCStatement statement : Each.elementIn(tree.init)) {
                            JavacYieldDataCollector.this.refactorStatement((Object)statement);
                        }
                        Case label = AST.Case();
                        Case breakLabel = JavacYieldDataCollector.this.getBreakLabel(this);
                        JavacYieldDataCollector.this.addLabel(label);
                        if (tree.cond != null && !JavacYieldDataCollector.this.isTrueLiteral(tree.cond)) {
                            JavacYieldDataCollector.this.addStatement(AST.If(AST.Not(AST.Expr((Object)tree.cond))).Then(AST.Block().withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(breakLabel))).withStatement(AST.Continue())));
                        }
                        JavacYieldDataCollector.this.refactorStatement((Object)tree.body);
                        JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getIterationLabel(this));
                        for (JCTree.JCExpressionStatement statement2 : Each.elementIn(tree.step)) {
                            JavacYieldDataCollector.this.refactorStatement((Object)statement2);
                        }
                        JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(label)));
                        JavacYieldDataCollector.this.addStatement(AST.Continue());
                        JavacYieldDataCollector.this.addLabel(breakLabel);
                    }
                };
                super.visitForLoop(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$1600((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitForeachLoop(final JCTree.JCEnhancedForLoop tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        String iteratorVar = "$" + As.string((Object)tree.var.name) + "Iter";
                        JavacYieldDataCollector.this.stateVariables.add(AST.FieldDecl(AST.Type("java.util.Iterator").withTypeArgument(AST.Type((Object)tree.var.vartype)), iteratorVar).makePrivate().withAnnotation(AST.Annotation(AST.Type(SuppressWarnings.class)).withValue(AST.String("all"))));
                        JavacYieldDataCollector.this.addStatement(AST.Assign(AST.Name(iteratorVar), AST.Call(AST.Expr((Object)tree.expr), "iterator")));
                        JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getIterationLabel(this));
                        JavacYieldDataCollector.this.addStatement(AST.If(AST.Not(AST.Call(AST.Name(iteratorVar), "hasNext"))).Then(AST.Block().withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getBreakLabel(this)))).withStatement(AST.Continue())));
                        JavacYieldDataCollector.this.addStatement(AST.Assign(AST.Name(As.string((Object)tree.var.name)), AST.Call(AST.Name(iteratorVar), "next")));
                        JavacYieldDataCollector.this.refactorStatement((Object)tree.body);
                        JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getIterationLabel(this))));
                        JavacYieldDataCollector.this.addStatement(AST.Continue());
                        JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getBreakLabel(this));
                    }
                };
                super.visitForeachLoop(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$2100((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitDoLoop(final JCTree.JCDoWhileLoop tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getIterationLabel(this));
                        JavacYieldDataCollector.this.refactorStatement((Object)tree.body);
                        JavacYieldDataCollector.this.addStatement(AST.If(AST.Expr((Object)tree.cond)).Then(AST.Block().withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getIterationLabel(this)))).withStatement(AST.Continue())));
                        JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getBreakLabel(this));
                    }
                };
                super.visitDoLoop(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$2500((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitWhileLoop(final JCTree.JCWhileLoop tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getIterationLabel(this));
                        if (!JavacYieldDataCollector.this.isTrueLiteral(tree.cond)) {
                            JavacYieldDataCollector.this.addStatement(AST.If(AST.Not(AST.Expr((Object)tree.cond))).Then(AST.Block().withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getBreakLabel(this)))).withStatement(AST.Continue())));
                        }
                        JavacYieldDataCollector.this.refactorStatement((Object)tree.body);
                        JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getIterationLabel(this))));
                        JavacYieldDataCollector.this.addStatement(AST.Continue());
                        JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getBreakLabel(this));
                    }
                };
                super.visitWhileLoop(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$2900((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitIf(final JCTree.JCIf tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        Case label = tree.elsepart == null ? JavacYieldDataCollector.this.getBreakLabel(this) : AST.Case();
                        JavacYieldDataCollector.this.addStatement(AST.If(AST.Not(AST.Expr((Object)tree.cond))).Then(AST.Block().withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(label))).withStatement(AST.Continue())));
                        if (tree.elsepart != null) {
                            JavacYieldDataCollector.this.refactorStatement((Object)tree.thenpart);
                            JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getBreakLabel(this))));
                            JavacYieldDataCollector.this.addStatement(AST.Continue());
                            JavacYieldDataCollector.this.addLabel(label);
                            JavacYieldDataCollector.this.refactorStatement((Object)tree.elsepart);
                            JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getBreakLabel(this));
                        } else {
                            JavacYieldDataCollector.this.refactorStatement((Object)tree.thenpart);
                            JavacYieldDataCollector.this.addLabel(JavacYieldDataCollector.this.getBreakLabel(this));
                        }
                    }
                };
                super.visitIf(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$3300((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitSwitch(final JCTree.JCSwitch tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        Case breakLabel = JavacYieldDataCollector.this.getBreakLabel(this);
                        Switch switchStatement = AST.Switch(AST.Expr((Object)tree.selector));
                        JavacYieldDataCollector.this.addStatement(switchStatement);
                        if (Is.notEmpty(tree.cases)) {
                            boolean hasDefault = false;
                            for (JCTree.JCCase item : tree.cases) {
                                if (item.pat == null) {
                                    hasDefault = true;
                                }
                                Case label = AST.Case();
                                switchStatement.withCase(AST.Case(AST.Expr((Object)item.pat)).withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(label))).withStatement(AST.Continue()));
                                JavacYieldDataCollector.this.addLabel(label);
                                for (JCTree.JCStatement statement : item.stats) {
                                    JavacYieldDataCollector.this.refactorStatement((Object)statement);
                                }
                            }
                            if (!hasDefault) {
                                switchStatement.withCase(AST.Case().withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(breakLabel))).withStatement(AST.Continue()));
                            }
                        }
                        JavacYieldDataCollector.this.addLabel(breakLabel);
                    }
                };
                super.visitSwitch(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$3700((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitTry(final JCTree.JCTry tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        Case finallyLabel;
                        boolean hasFinally = tree.finalizer != null;
                        boolean hasCatch = Is.notEmpty(tree.catchers);
                        YieldHandler.ErrorHandler catchHandler = null;
                        YieldHandler.ErrorHandler finallyHandler = null;
                        Case tryLabel = AST.Case();
                        Case breakLabel = JavacYieldDataCollector.this.getBreakLabel(this);
                        String finallyErrorName = null;
                        if (hasFinally) {
                            finallyHandler = new YieldHandler.ErrorHandler();
                            finallyLabel = JavacYieldDataCollector.this.getFinallyLabel(this);
                            JavacYieldDataCollector.this.finallyBlocks++;
                            finallyErrorName = JavacYieldDataCollector.this.errorName + JavacYieldDataCollector.this.finallyBlocks;
                            this.labelName = "$state" + JavacYieldDataCollector.this.finallyBlocks;
                            JavacYieldDataCollector.this.stateVariables.add(AST.FieldDecl(AST.Type(Throwable.class), finallyErrorName).makePrivate());
                            JavacYieldDataCollector.this.stateVariables.add(AST.FieldDecl(AST.Type("int"), this.labelName).makePrivate());
                            JavacYieldDataCollector.this.addStatement(AST.Assign(AST.Name(finallyErrorName), AST.Null()));
                            JavacYieldDataCollector.this.addStatement(AST.Assign(AST.Name(this.labelName), JavacYieldDataCollector.this.literal(breakLabel)));
                        } else {
                            finallyLabel = breakLabel;
                        }
                        JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(tryLabel)));
                        if (hasCatch) {
                            catchHandler = new YieldHandler.ErrorHandler();
                            catchHandler.begin = JavacYieldDataCollector.this.cases.size();
                        } else if (hasFinally) {
                            finallyHandler.begin = JavacYieldDataCollector.this.cases.size();
                        }
                        JavacYieldDataCollector.this.addLabel(tryLabel);
                        JavacYieldDataCollector.this.refactorStatement((Object)tree.body);
                        JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(finallyLabel)));
                        if (hasCatch) {
                            JavacYieldDataCollector.this.addStatement(AST.Continue());
                            catchHandler.end = JavacYieldDataCollector.this.cases.size();
                            for (JCTree.JCCatch catcher : tree.catchers) {
                                Case label = AST.Case();
                                JavacYieldDataCollector.this.usedLabels.add(label);
                                JavacYieldDataCollector.this.addLabel(label);
                                JavacYieldDataCollector.this.refactorStatement((Object)catcher.body);
                                JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(finallyLabel)));
                                JavacYieldDataCollector.this.addStatement(AST.Continue());
                                catchHandler.statements.add(AST.If(AST.InstanceOf(AST.Name(JavacYieldDataCollector.this.errorName), AST.Type((Object)catcher.param.vartype))).Then(AST.Block().withStatement(AST.Assign(AST.Name(As.string((Object)catcher.param.name)), AST.Cast(AST.Type((Object)catcher.param.vartype), AST.Name(JavacYieldDataCollector.this.errorName)))).withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(label))).withStatement(AST.Continue())));
                            }
                            JavacYieldDataCollector.this.errorHandlers.add(catchHandler);
                            if (hasFinally) {
                                finallyHandler.begin = catchHandler.end;
                            }
                        }
                        if (hasFinally) {
                            finallyHandler.end = JavacYieldDataCollector.this.cases.size();
                            JavacYieldDataCollector.this.addLabel(finallyLabel);
                            JavacYieldDataCollector.this.refactorStatement((Object)tree.finalizer);
                            JavacYieldDataCollector.this.addStatement(AST.If(AST.NotEqual(AST.Name(finallyErrorName), AST.Null())).Then(AST.Block().withStatement(AST.Assign(AST.Name(JavacYieldDataCollector.this.errorName), AST.Name(finallyErrorName))).withStatement(AST.Break())));
                            YieldHandler.Scope next = JavacYieldDataCollector.this.getFinallyScope(this.parent, null);
                            if (next != null) {
                                Case label = JavacYieldDataCollector.this.getFinallyLabel(next);
                                JavacYieldDataCollector.this.addStatement(AST.If(AST.Binary(AST.Name(this.labelName), ">", JavacYieldDataCollector.this.literal(label))).Then(AST.Block().withStatement(AST.Assign(AST.Name(next.labelName), AST.Name(this.labelName))).withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(label))).withStatement(JavacYieldDataCollector.this.setState(AST.Name(this.labelName)))));
                            } else {
                                JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(AST.Name(this.labelName)));
                            }
                            JavacYieldDataCollector.this.addStatement(AST.Continue());
                            finallyHandler.statements.add(AST.Assign(AST.Name(finallyErrorName), AST.Name(JavacYieldDataCollector.this.errorName)));
                            finallyHandler.statements.add(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(finallyLabel)));
                            finallyHandler.statements.add(AST.Continue());
                            JavacYieldDataCollector.this.usedLabels.add(finallyLabel);
                            JavacYieldDataCollector.this.errorHandlers.add(finallyHandler);
                        }
                        JavacYieldDataCollector.this.addLabel(breakLabel);
                    }
                };
                super.visitTry(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$6000((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitVarDef(final JCTree.JCVariableDecl tree) {
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        if (tree.init != null) {
                            JavacYieldDataCollector.this.addStatement(AST.Assign(AST.Name(As.string((Object)tree.name)), AST.Expr((Object)tree.init)));
                        }
                    }
                };
                JavacYieldDataCollector.this.variableDecls.add(JavacYieldDataCollector.this.current);
                super.visitVarDef(tree);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$6600((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitReturn(JCTree.JCReturn tree) {
                ((JavacMethod)JavacYieldDataCollector.this.method).node().addError("The 'return' expression is permitted.");
            }

            public void visitBreak(JCTree.JCBreak tree) {
                YieldHandler.Scope target;
                target = null;
                Name label = tree.label;
                if (label != null) {
                    YieldHandler.Scope labelScope = JavacYieldDataCollector.this.current;
                    while (labelScope != null) {
                        if (labelScope.node instanceof JCTree.JCLabeledStatement) {
                            JCTree.JCLabeledStatement labeledStatement = (JCTree.JCLabeledStatement)labelScope.node;
                            if (label == labeledStatement.label) {
                                if (target != null) {
                                    ((JavacMethod)JavacYieldDataCollector.this.method).node().addError("Invalid label.");
                                }
                                target = labelScope;
                            }
                        }
                        labelScope = labelScope.parent;
                    }
                } else {
                    YieldHandler.Scope labelScope = JavacYieldDataCollector.this.current;
                    while (labelScope != null) {
                        if (Is.oneOf(labelScope.node, JCTree.JCForLoop.class, JCTree.JCEnhancedForLoop.class, JCTree.JCWhileLoop.class, JCTree.JCDoWhileLoop.class, JCTree.JCSwitch.class)) {
                            target = labelScope;
                            break;
                        }
                        labelScope = labelScope.parent;
                    }
                }
                if (target == null) {
                    ((JavacMethod)JavacYieldDataCollector.this.method).node().addError("Invalid break.");
                }
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        YieldHandler.Scope next = JavacYieldDataCollector.this.getFinallyScope(this.parent, this.target);
                        Case label = JavacYieldDataCollector.this.getBreakLabel(this.target);
                        if (next == null) {
                            JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(label)));
                            JavacYieldDataCollector.this.addStatement(AST.Continue());
                        } else {
                            JavacYieldDataCollector.this.addStatement(AST.Assign(AST.Name(next.labelName), JavacYieldDataCollector.this.literal(label)));
                            JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getFinallyLabel(next))));
                            JavacYieldDataCollector.this.addStatement(AST.Continue());
                        }
                    }
                };
                JavacYieldDataCollector.access$7400((JavacYieldDataCollector)JavacYieldDataCollector.this).target = target;
                JavacYieldDataCollector.this.breaks.add(JavacYieldDataCollector.this.current);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$7800((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitContinue(JCTree.JCContinue tree) {
                YieldHandler.Scope target;
                target = null;
                Name label = tree.label;
                if (label != null) {
                    YieldHandler.Scope labelScope = JavacYieldDataCollector.this.current;
                    while (labelScope != null) {
                        if (labelScope.node instanceof JCTree.JCLabeledStatement) {
                            JCTree.JCLabeledStatement labeledStatement = (JCTree.JCLabeledStatement)labelScope.node;
                            if (label == labeledStatement.label) {
                                if (target != null) {
                                    ((JavacMethod)JavacYieldDataCollector.this.method).node().addError("Invalid label.");
                                }
                                if (Is.oneOf(labelScope.node, JCTree.JCForLoop.class, JCTree.JCEnhancedForLoop.class, JCTree.JCWhileLoop.class, JCTree.JCDoWhileLoop.class)) {
                                    target = labelScope;
                                } else {
                                    ((JavacMethod)JavacYieldDataCollector.this.method).node().addError("Invalid continue.");
                                }
                            }
                        }
                        labelScope = labelScope.parent;
                    }
                } else {
                    YieldHandler.Scope labelScope = JavacYieldDataCollector.this.current;
                    while (labelScope != null) {
                        if (Is.oneOf(labelScope.node, JCTree.JCForLoop.class, JCTree.JCEnhancedForLoop.class, JCTree.JCWhileLoop.class, JCTree.JCDoWhileLoop.class)) {
                            target = labelScope;
                            break;
                        }
                        labelScope = labelScope.parent;
                    }
                }
                if (target == null) {
                    ((JavacMethod)JavacYieldDataCollector.this.method).node().addError("Invalid continue.");
                }
                JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                    @Override
                    public void refactor() {
                        YieldHandler.Scope next = JavacYieldDataCollector.this.getFinallyScope(this.parent, this.target);
                        Case label = JavacYieldDataCollector.this.getIterationLabel(this.target);
                        if (next == null) {
                            JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(label)));
                            JavacYieldDataCollector.this.addStatement(AST.Continue());
                        } else {
                            JavacYieldDataCollector.this.addStatement(AST.Assign(AST.Name(next.labelName), JavacYieldDataCollector.this.literal(label)));
                            JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getFinallyLabel(next))));
                            JavacYieldDataCollector.this.addStatement(AST.Continue());
                        }
                    }
                };
                JavacYieldDataCollector.access$8600((JavacYieldDataCollector)JavacYieldDataCollector.this).target = target;
                JavacYieldDataCollector.this.breaks.add(JavacYieldDataCollector.this.current);
                JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$9000((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
            }

            public void visitApply(JCTree.JCMethodInvocation tree) {
                String name;
                if (tree.meth instanceof JCTree.JCIdent && Is.oneOf(name = As.string((Object)tree.meth), "hasNext", "next", "remove", "close")) {
                    ((JavacMethod)JavacYieldDataCollector.this.method).node().addError("Cannot call method " + name + "(), as it is hidden.");
                }
                super.visitApply(tree);
            }

            public void visitExec(JCTree.JCExpressionStatement tree) {
                final JCTree.JCExpression expression = JavacYieldDataCollector.this.getYieldExpression(tree.expr);
                if (expression != null) {
                    JavacYieldDataCollector.this.current = new YieldHandler.Scope<JCTree>(JavacYieldDataCollector.this.current, (JCTree)tree){

                        @Override
                        public void refactor() {
                            Case label = JavacYieldDataCollector.this.getBreakLabel(this);
                            JavacYieldDataCollector.this.addStatement(AST.Assign(AST.Name(JavacYieldDataCollector.this.nextName), AST.Expr((Object)expression)));
                            JavacYieldDataCollector.this.addStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(label)));
                            JavacYieldDataCollector.this.addStatement(AST.Return(AST.True()));
                            JavacYieldDataCollector.this.addLabel(label);
                            YieldHandler.Scope next = JavacYieldDataCollector.this.getFinallyScope(this.parent, null);
                            if (next != null) {
                                JavacYieldDataCollector.this.breakCases.add(new Case(JavacYieldDataCollector.this.literal(label)).withStatement(AST.Assign(AST.Name(next.labelName), JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getBreakLabel(JavacYieldDataCollector.this.root)))).withStatement(JavacYieldDataCollector.this.setState(JavacYieldDataCollector.this.literal(JavacYieldDataCollector.this.getFinallyLabel(next)))).withStatement(AST.Continue()));
                            }
                        }
                    };
                    JavacYieldDataCollector.this.yields.add(JavacYieldDataCollector.this.current);
                    this.scan((JCTree)expression);
                    JavacYieldDataCollector.this.current = JavacYieldDataCollector.access$10000((JavacYieldDataCollector)JavacYieldDataCollector.this).parent;
                } else {
                    super.visitExec(tree);
                }
            }

            public void visitIdent(JCTree.JCIdent tree) {
                if ("this".equals(tree.name.toString())) {
                    ((JavacMethod)JavacYieldDataCollector.this.method).node().addError("No unqualified 'this' expression is permitted.");
                }
                if ("super".equals(tree.name.toString())) {
                    ((JavacMethod)JavacYieldDataCollector.this.method).node().addError("No unqualified 'super' expression is permitted.");
                }
                super.visitIdent(tree);
            }

            public void visitNewClass(JCTree.JCNewClass tree) {
                this.scan((JCTree)tree.encl);
                this.scan((JCTree)tree.clazz);
                this.scan(tree.args);
            }

        }

        private class YieldQuickScanner
        extends TreeScanner {
            private YieldQuickScanner() {
            }

            public void visitExec(JCTree.JCExpressionStatement tree) {
                JCTree.JCExpression expression = JavacYieldDataCollector.this.getYieldExpression(tree.expr);
                if (expression != null) {
                    throw new IllegalStateException();
                }
                super.visitExec(tree);
            }
        }

    }

}

