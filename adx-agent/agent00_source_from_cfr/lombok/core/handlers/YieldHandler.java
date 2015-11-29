/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.ast.AST;
import lombok.ast.AbstractMethodDecl;
import lombok.ast.Argument;
import lombok.ast.Assignment;
import lombok.ast.Block;
import lombok.ast.Call;
import lombok.ast.Case;
import lombok.ast.ClassDecl;
import lombok.ast.ConstructorDecl;
import lombok.ast.Continue;
import lombok.ast.DoWhile;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.IMethod;
import lombok.ast.IMethodEditor;
import lombok.ast.If;
import lombok.ast.LocalDecl;
import lombok.ast.MethodDecl;
import lombok.ast.NameRef;
import lombok.ast.NumberLiteral;
import lombok.ast.Return;
import lombok.ast.Statement;
import lombok.ast.Switch;
import lombok.ast.Try;
import lombok.ast.TypeRef;
import lombok.ast.While;
import lombok.core.util.Is;
import lombok.core.util.Names;

public class YieldHandler<METHOD_TYPE extends IMethod<?, ?, ?, ?>, AST_BASE_TYPE> {
    public boolean handle(METHOD_TYPE method, AbstractYieldDataCollector<METHOD_TYPE, AST_BASE_TYPE> collector) {
        boolean returnsIterable = method.returns(Iterable.class);
        boolean returnsIterator = method.returns(Iterator.class);
        if (!returnsIterable && !returnsIterator) {
            method.node().addError("Method that contain yield() can only return java.util.Iterator or java.lang.Iterable.");
            return true;
        }
        if (method.hasNonFinalArgument()) {
            method.node().addError("Parameters should be final.");
            return true;
        }
        String stateName = "$state";
        String nextName = "$next";
        String errorName = "$yieldException";
        collector.collect(method, "$state", "$next", "$yieldException", returnsIterable);
        if (!collector.hasYields()) {
            return true;
        }
        String yielderName = collector.yielderName(method);
        ClassDecl yielder = collector.getYielder();
        method.editor().replaceBody(yielder, AST.Return(AST.New(AST.Type(yielderName))));
        method.editor().rebuild();
        return true;
    }

    public static abstract class Scope<AST_BASE_TYPE> {
        public final Scope<AST_BASE_TYPE> parent;
        public final AST_BASE_TYPE node;
        public Scope<AST_BASE_TYPE> target;
        public String labelName;
        public Case iterationLabel;
        public Case breakLabel;
        public Case finallyLabel;

        public abstract void refactor();

        public Scope(Scope<AST_BASE_TYPE> parent, AST_BASE_TYPE node) {
            this.parent = parent;
            this.node = node;
        }
    }

    public static class ErrorHandler {
        public int begin;
        public int end;
        public List<Statement<?>> statements = new ArrayList();
    }

    public static abstract class AbstractYieldDataCollector<METHOD_TYPE extends IMethod<?, ?, ?, ?>, AST_BASE_TYPE> {
        protected METHOD_TYPE method;
        protected List<Scope<AST_BASE_TYPE>> yields = new ArrayList<Scope<AST_BASE_TYPE>>();
        protected List<Scope<AST_BASE_TYPE>> breaks = new ArrayList<Scope<AST_BASE_TYPE>>();
        protected List<Scope<AST_BASE_TYPE>> variableDecls = new ArrayList<Scope<AST_BASE_TYPE>>();
        protected List<FieldDecl> stateVariables = new ArrayList<FieldDecl>();
        protected Scope<AST_BASE_TYPE> root;
        protected Scope<AST_BASE_TYPE> current;
        protected Map<AST_BASE_TYPE, Scope<AST_BASE_TYPE>> allScopes = new HashMap<AST_BASE_TYPE, Scope<AST_BASE_TYPE>>();
        protected List<Case> cases = new ArrayList<Case>();
        protected List<Statement<?>> statements = new ArrayList();
        protected List<Case> breakCases = new ArrayList<Case>();
        protected List<ErrorHandler> errorHandlers = new ArrayList<ErrorHandler>();
        protected int finallyBlocks;
        protected Map<NumberLiteral, Case> labelLiterals = new HashMap<NumberLiteral, Case>();
        protected Set<Case> usedLabels = new HashSet<Case>();
        protected String stateName;
        protected String nextName;
        protected String errorName;
        protected boolean returnsIterable;

        public ClassDecl getYielder() {
            String yielderName = this.yielderName(this.method);
            String elementType = this.elementType(this.method);
            List<FieldDecl> variables = this.getStateVariables();
            Switch stateSwitch = this.getStateSwitch();
            Switch errorHandlerSwitch = this.getErrorHandlerSwitch();
            Statement closeStatement = this.getCloseStatement();
            ClassDecl yielder = ((ClassDecl)AST.ClassDecl(yielderName).posHint(this.method.get())).makeLocal().implementing(AST.Type(Iterator.class).withTypeArgument(AST.Type(elementType))).withFields(variables).withField(AST.FieldDecl(AST.Type("int"), this.stateName).makePrivate()).withField(AST.FieldDecl(AST.Type("boolean"), "$hasNext").makePrivate()).withField(AST.FieldDecl(AST.Type("boolean"), "$nextDefined").makePrivate()).withField(AST.FieldDecl(AST.Type(elementType), this.nextName).makePrivate()).withMethod(AST.ConstructorDecl(yielderName).withImplicitSuper().makePrivate());
            if (this.returnsIterable) {
                yielder.implementing(AST.Type(Iterable.class).withTypeArgument(AST.Type(elementType))).withMethod(((MethodDecl)AST.MethodDecl(AST.Type(Iterator.class).withTypeArgument(AST.Type(elementType)), "iterator").makePublic()).withStatement(AST.If(AST.Equal(AST.Name(this.stateName), AST.Number(0))).Then(AST.Block().withStatement(AST.Assign(AST.Name(this.stateName), AST.Number(1))).withStatement(AST.Return(AST.This()))).Else(AST.Return(AST.New(AST.Type(yielderName))))));
            }
            yielder.implementing(AST.Type(Closeable.class)).withMethod(((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("boolean"), "hasNext").makePublic()).withStatement(AST.If(AST.Not(AST.Name("$nextDefined"))).Then(AST.Block().withStatement(AST.Assign(AST.Name("$hasNext"), AST.Call("getNext"))).withStatement(AST.Assign(AST.Name("$nextDefined"), AST.True()))))).withStatement(AST.Return(AST.Name("$hasNext")))).withMethod(((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type(elementType), "next").makePublic()).withStatement(AST.If(AST.Not(AST.Call("hasNext"))).Then(AST.Block().withStatement(AST.Throw(AST.New(AST.Type(NoSuchElementException.class))))))).withStatement(AST.Assign(AST.Name("$nextDefined"), AST.False()))).withStatement(AST.Return(AST.Name(this.nextName)))).withMethod(((MethodDecl)AST.MethodDecl(AST.Type("void"), "remove").makePublic()).withStatement(AST.Throw(AST.New(AST.Type(UnsupportedOperationException.class))))).withMethod(((MethodDecl)AST.MethodDecl(AST.Type("void"), "close").makePublic()).withStatement(closeStatement));
            if (errorHandlerSwitch != null) {
                String caughtErrorName = this.errorName + "Caught";
                yielder.withMethod(((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("boolean"), "getNext").makePrivate()).withStatement(AST.LocalDecl(AST.Type(Throwable.class), this.errorName))).withStatement(AST.While(AST.True()).Do(AST.Block().withStatement(AST.Try(AST.Block().withStatement(stateSwitch)).Catch(AST.Arg(AST.Type(Throwable.class), caughtErrorName), AST.Block().withStatement(AST.Assign(AST.Name(this.errorName), AST.Name(caughtErrorName))))).withStatement(errorHandlerSwitch))));
            } else {
                yielder.withMethod(((MethodDecl)AST.MethodDecl(AST.Type("boolean"), "getNext").makePrivate()).withStatement(AST.While(AST.True()).Do(stateSwitch)));
            }
            return yielder;
        }

        public Switch getStateSwitch() {
            ArrayList<Case> switchCases = new ArrayList<Case>();
            for (Case label : this.cases) {
                if (label == null) continue;
                switchCases.add(label);
            }
            return AST.Switch(AST.Name(this.stateName)).withCases(switchCases).withCase(AST.Case().withStatement(AST.Return(AST.False())));
        }

        public Switch getErrorHandlerSwitch() {
            if (this.errorHandlers.isEmpty()) {
                return null;
            }
            ArrayList<Case> switchCases = new ArrayList<Case>();
            HashSet<Case> labels = new HashSet<Case>();
            for (ErrorHandler handler : this.errorHandlers) {
                Case lastCase = null;
                for (int i = handler.begin; i < handler.end; ++i) {
                    Case label = this.cases.get(i);
                    if (label == null || !labels.add(label)) continue;
                    lastCase = AST.Case(label.getPattern());
                    switchCases.add(lastCase);
                }
                if (lastCase == null) continue;
                lastCase.withStatements(handler.statements);
            }
            String unhandledErrorName = this.errorName + "Unhandled";
            switchCases.add(AST.Case().withStatement(this.setState(this.literal(this.getBreakLabel(this.root)))).withStatement(AST.LocalDecl(AST.Type(ConcurrentModificationException.class), unhandledErrorName).withInitialization(AST.New(AST.Type(ConcurrentModificationException.class)))).withStatement(AST.Call(AST.Name(unhandledErrorName), "initCause").withArgument(AST.Name(this.errorName))).withStatement(AST.Throw(AST.Name(unhandledErrorName))));
            return AST.Switch(AST.Name(this.stateName)).withCases(switchCases);
        }

        public Statement<?> getCloseStatement() {
            Statement statement = this.setState(this.literal(this.getBreakLabel(this.root)));
            if (this.breakCases.isEmpty()) {
                return statement;
            }
            Object prev = null;
            ArrayList<Case> switchCases = new ArrayList<Case>();
            for (Case breakCase : this.breakCases) {
                NumberLiteral literal = (NumberLiteral)breakCase.getPattern();
                Number value = literal.getNumber();
                if (prev != null && prev.equals(value)) continue;
                switchCases.add(breakCase);
                prev = value;
            }
            switchCases.add(AST.Case().withStatement(statement).withStatement(AST.Return()));
            return AST.Do(AST.Switch(AST.Name(this.stateName)).withCases(switchCases)).While(AST.Call("getNext"));
        }

        public boolean hasYields() {
            return !this.yields.isEmpty();
        }

        public void collect(METHOD_TYPE method, String state, String next, String errorName, boolean returnsIterable) {
            this.method = method;
            this.stateName = state;
            this.nextName = next;
            this.errorName = errorName;
            this.returnsIterable = returnsIterable;
            if (this.scan()) {
                this.prepareRefactor();
                this.refactor();
            }
        }

        public String yielderName(METHOD_TYPE method) {
            String[] parts = method.name().split("_");
            String[] newParts = new String[parts.length + 1];
            newParts[0] = "yielder";
            System.arraycopy(parts, 0, newParts, 1, parts.length);
            return Names.camelCase("$", newParts);
        }

        public abstract String elementType(METHOD_TYPE var1);

        public abstract boolean scan();

        public abstract void prepareRefactor();

        public void refactor() {
            this.current = this.root;
            Case begin = AST.Case();
            Case iteratorLabel = this.getIterationLabel(this.root);
            this.usedLabels.add(begin);
            this.usedLabels.add(iteratorLabel);
            this.usedLabels.add(this.getBreakLabel(this.root));
            this.addLabel(begin);
            this.addStatement(this.setState(this.literal(iteratorLabel)));
            this.addLabel(iteratorLabel);
            this.root.refactor();
            this.endCase();
            this.optimizeStates();
            this.synchronizeLiteralsAndLabels();
        }

        public Expression<?> getStateFromAssignment(Statement<?> statement) {
            Assignment assign;
            NameRef field;
            if (statement instanceof Assignment && (assign = (Assignment)statement).getLeft() instanceof NameRef && this.stateName.equals((field = (NameRef)assign.getLeft()).getName())) {
                return assign.getRight();
            }
            return null;
        }

        public Case getLabel(Expression<?> expression) {
            return expression == null ? null : this.labelLiterals.get(expression);
        }

        public void endCase() {
            Case lastCase;
            if (!this.cases.isEmpty() && (lastCase = this.cases.get(this.cases.size() - 1)).getStatements().isEmpty() && !this.statements.isEmpty()) {
                lastCase.withStatements(this.statements);
                this.statements.clear();
            }
        }

        public void addLabel(Case label) {
            this.endCase();
            label.withPattern(AST.Number(this.cases.size()));
            this.cases.add(label);
        }

        public void addStatement(Statement<?> statement) {
            this.statements.add(statement);
        }

        public Case getBreakLabel(Scope<AST_BASE_TYPE> scope) {
            Case label = scope.breakLabel;
            if (label == null) {
                scope.breakLabel = label = AST.Case();
            }
            return label;
        }

        public Case getIterationLabel(Scope<AST_BASE_TYPE> scope) {
            Case label = scope.iterationLabel;
            if (label == null) {
                scope.iterationLabel = label = AST.Case();
            }
            return label;
        }

        public Case getFinallyLabel(Scope<AST_BASE_TYPE> scope) {
            Case label = scope.finallyLabel;
            if (label == null) {
                scope.finallyLabel = label = AST.Case();
            }
            return label;
        }

        public Expression<?> literal(Case label) {
            NumberLiteral pattern = (NumberLiteral)label.getPattern();
            NumberLiteral literal = AST.Number(pattern == null ? Integer.valueOf(-1) : pattern.getNumber());
            this.labelLiterals.put(literal, label);
            return literal;
        }

        public Statement<?> setState(Expression<?> expression) {
            return AST.Assign(AST.Name(this.stateName), expression);
        }

        public void refactorStatement(Object statement) {
            if (statement == null) {
                return;
            }
            Scope<AST_BASE_TYPE> scope = this.allScopes.get(statement);
            if (scope != null) {
                Scope<AST_BASE_TYPE> previous = this.current;
                this.current = scope;
                scope.refactor();
                this.current = previous;
            } else {
                this.addStatement(AST.Stat(statement));
            }
        }

        public void optimizeStates() {
            this.optimizeStateChanges();
            this.optimizeSuccessiveStates();
        }

        public void optimizeStateChanges() {
            int count = this.cases.size();
            for (Map.Entry<NumberLiteral, Case> entry : this.labelLiterals.entrySet()) {
                Case label = entry.getValue();
                while (label.getPattern() != null) {
                    if (label.getStatements().isEmpty()) {
                        NumberLiteral literal = (NumberLiteral)label.getPattern();
                        int i = (Integer)literal.getNumber() + 1;
                        if (i >= count) break;
                        label = this.cases.get(i);
                        continue;
                    }
                    Case next = this.getLabel(this.getStateFromAssignment(label.getStatements().get(0)));
                    int numberOfStatements = label.getStatements().size();
                    if (next == null || numberOfStatements != 1 && (numberOfStatements <= 1 || !(label.getStatements().get(1) instanceof Continue))) break;
                    label = next;
                }
                entry.setValue(label);
                if (label.getPattern() == null) continue;
                this.usedLabels.add(label);
            }
        }

        public void optimizeSuccessiveStates() {
            Case previous = null;
            int id = 0;
            int iend = this.cases.size();
            for (int i = 0; i < iend; ++i) {
                Case label = this.cases.get(i);
                if (!this.usedLabels.contains(label) && previous != null) {
                    Statement<?> last = previous.getStatements().get(previous.getStatements().size() - 1);
                    if (!label.getStatements().isEmpty() && Is.noneOf(last, Continue.class, Return.class)) {
                        previous.withStatements(label.getStatements());
                    }
                    this.cases.set(i, null);
                    continue;
                }
                NumberLiteral literal = (NumberLiteral)label.getPattern();
                literal.setNumber(id++);
                if (previous == null) {
                    previous = label;
                    continue;
                }
                boolean found = false;
                boolean remove = false;
                List<Statement<?>> list = previous.getStatements();
                Iterator<Statement<?>> iter = list.iterator();
                while (iter.hasNext()) {
                    Statement<?> statement = iter.next();
                    if (remove || found && statement instanceof Continue) {
                        remove = true;
                        iter.remove();
                        continue;
                    }
                    found = this.getLabel(this.getStateFromAssignment(statement)) == label;
                }
                previous = label;
            }
        }

        public void synchronizeLiteralsAndLabels() {
            for (Map.Entry<NumberLiteral, Case> entry : this.labelLiterals.entrySet()) {
                Case label = entry.getValue();
                if (label == null) continue;
                NumberLiteral literal = (NumberLiteral)label.getPattern();
                entry.getKey().setNumber(literal.getNumber());
            }
        }

        public List<FieldDecl> getStateVariables() {
            return this.stateVariables;
        }
    }

}

