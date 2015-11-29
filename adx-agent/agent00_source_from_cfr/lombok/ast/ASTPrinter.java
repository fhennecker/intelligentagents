/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.io.PrintStream;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.ast.AST;
import lombok.ast.ASTVisitor;
import lombok.ast.AbstractMethodDecl;
import lombok.ast.Annotation;
import lombok.ast.Argument;
import lombok.ast.ArrayRef;
import lombok.ast.Assignment;
import lombok.ast.Binary;
import lombok.ast.Block;
import lombok.ast.BooleanLiteral;
import lombok.ast.Break;
import lombok.ast.Call;
import lombok.ast.Case;
import lombok.ast.Cast;
import lombok.ast.CharLiteral;
import lombok.ast.ClassDecl;
import lombok.ast.ConstructorDecl;
import lombok.ast.Continue;
import lombok.ast.DefaultValue;
import lombok.ast.DoWhile;
import lombok.ast.EnumConstant;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.FieldRef;
import lombok.ast.Foreach;
import lombok.ast.If;
import lombok.ast.Initializer;
import lombok.ast.InstanceOf;
import lombok.ast.JavaDoc;
import lombok.ast.LocalDecl;
import lombok.ast.MethodDecl;
import lombok.ast.Modifier;
import lombok.ast.NameRef;
import lombok.ast.New;
import lombok.ast.NewArray;
import lombok.ast.Node;
import lombok.ast.NullLiteral;
import lombok.ast.NumberLiteral;
import lombok.ast.Return;
import lombok.ast.ReturnDefault;
import lombok.ast.Statement;
import lombok.ast.StringLiteral;
import lombok.ast.Switch;
import lombok.ast.Synchronized;
import lombok.ast.This;
import lombok.ast.Throw;
import lombok.ast.Try;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;
import lombok.ast.Unary;
import lombok.ast.While;
import lombok.ast.Wildcard;
import lombok.ast.WrappedExpression;
import lombok.ast.WrappedMethodDecl;
import lombok.ast.WrappedStatement;
import lombok.ast.WrappedTypeRef;

public final class ASTPrinter
implements ASTVisitor<State, State> {
    private void writeModifiers(Set<Modifier> modifiers, State state) {
        if (modifiers.contains((Object)Modifier.PUBLIC)) {
            state.print("public ");
        }
        if (modifiers.contains((Object)Modifier.PRIVATE)) {
            state.print("private ");
        }
        if (modifiers.contains((Object)Modifier.PROTECTED)) {
            state.print("protected ");
        }
        if (modifiers.contains((Object)Modifier.STATIC)) {
            state.print("static ");
        }
        if (modifiers.contains((Object)Modifier.FINAL)) {
            state.print("final ");
        }
        if (modifiers.contains((Object)Modifier.VOLATILE)) {
            state.print("volatile ");
        }
        if (modifiers.contains((Object)Modifier.TRANSIENT)) {
            state.print("transient ");
        }
    }

    @Override
    public State visitAnnotation(Annotation node, State state) {
        state.print("@").print(node.getType(), this);
        if (!node.getValues().isEmpty()) {
            if (node.getValues().containsKey("value") && node.getValues().size() == 1) {
                node.getValues().get("value").accept(this, (State)state);
            } else {
                Set entries = node.getValues().entrySet();
                int i = 0;
                int iend = entries.size() - 1;
                for (Map.Entry entry : entries) {
                    state.print(entry.getKey()).print(" = ").print(entry.getValue(), this);
                    if (i == iend) break;
                    state.print(", ");
                    ++i;
                }
            }
        }
        return state;
    }

    @Override
    public State visitArgument(Argument node, State state) {
        for (Annotation annotation : node.getAnnotations()) {
            state.print(annotation, this).print(" ");
        }
        this.writeModifiers(node.getModifiers(), state);
        return state.print(node.getType(), this).print(" ").print(node.getName());
    }

    @Override
    public State visitArrayRef(ArrayRef node, State state) {
        return state.print(node.getIndexed(), this).print("[").print(node.getIndex(), this).print("]");
    }

    @Override
    public State visitAssignment(Assignment node, State state) {
        return state.print(node.getLeft(), this).print(" = ").print(node.getRight(), this);
    }

    @Override
    public State visitBinary(Binary node, State state) {
        return state.print(node.getLeft(), this).print(" ").print(node.getOperator()).print(" ").print(node.getRight(), this);
    }

    @Override
    public State visitBlock(Block node, State state) {
        state.print("{\n");
        State indentedState = state.indent();
        for (Statement statement : node.getStatements()) {
            indentedState.printIndent().print(statement, this).print(";\n");
        }
        return state.printIndent().print("}");
    }

    @Override
    public State visitBooleanLiteral(BooleanLiteral node, State state) {
        return state.print(node.isTrue() ? "true" : "false");
    }

    @Override
    public State visitBreak(Break node, State state) {
        state.print("break");
        if (node.getLabel() != null) {
            state.print(" ").print(node.getLabel());
        }
        return state;
    }

    @Override
    public State visitCall(Call node, State state) {
        int iend;
        int i;
        if (node.getReceiver() != null) {
            state.print(node.getReceiver(), this).print(".");
        }
        if (!node.getTypeArgs().isEmpty()) {
            state.print("<");
            iend = node.getTypeArgs().size() - 1;
            for (i = 0; i <= iend; ++i) {
                state.print(node.getTypeArgs().get(i), this);
                if (i == iend) break;
                state.print(", ");
            }
            state.print(">");
        }
        state.print(node.getName()).print("(");
        iend = node.getArgs().size() - 1;
        for (i = 0; i <= iend; ++i) {
            state.print(node.getArgs().get(i), this);
            if (i == iend) break;
            state.print(", ");
        }
        return state.print(")");
    }

    @Override
    public State visitCast(Cast node, State state) {
        return state.print("(").print(node.getType(), this).print(") ").print(node.getExpression(), this);
    }

    @Override
    public State visitCase(Case node, State state) {
        state.printIndent();
        if (node.getPattern() == null) {
            state.print("default:\n");
        } else {
            state.print("case ").print(node.getPattern(), this).print(":\n");
        }
        State indentedState = state.indent();
        for (Statement statement : node.getStatements()) {
            indentedState.printIndent().print(statement, this).print(";\n");
        }
        return state;
    }

    @Override
    public State visitCharLiteral(CharLiteral node, State state) {
        return state.print("'").print(node.getCharacter()).print("'");
    }

    @Override
    public State visitClassDecl(ClassDecl node, State state) {
        if (!node.isAnonymous()) {
            for (Annotation annotation : node.getAnnotations()) {
                state.print(annotation, this).print(" ");
            }
            this.writeModifiers(node.getModifiers(), state);
            if (node.isInterface()) {
                state.print("interface ");
            } else {
                state.print("class ");
            }
            state.print(node.getName());
            if (!node.getTypeParameters().isEmpty()) {
                state.print("<");
                int iend = node.getTypeParameters().size() - 1;
                for (int i = 0; i <= iend; ++i) {
                    state.print(node.getTypeParameters().get(i), this);
                    if (i == iend) break;
                    state.print(", ");
                }
                state.print(">");
            }
            if (node.getSuperclass() != null) {
                state.print(" extends ").print(node.getSuperclass(), this);
            }
            if (!node.getSuperInterfaces().isEmpty()) {
                state.print(" implements ");
                int iend = node.getSuperInterfaces().size() - 1;
                for (int i = 0; i <= iend; ++i) {
                    state.print(node.getSuperInterfaces().get(i), this);
                    if (i == iend) break;
                    state.print(", ");
                }
            }
            state.print(" ");
        }
        state.print("{\n");
        State indentedState = state.indent();
        for (FieldDecl field : node.getFields()) {
            indentedState.printIndent().print(field, this).print(";\n");
        }
        for (AbstractMethodDecl method : node.getMethods()) {
            indentedState.print("\n").printIndent().print(method, this).print("\n");
        }
        for (ClassDecl memberType : node.getMemberTypes()) {
            indentedState.print("\n").printIndent().print(memberType, this).print("\n");
        }
        return state.printIndent().print("}");
    }

    @Override
    public State visitConstructorDecl(ConstructorDecl node, State state) {
        int i;
        for (Annotation annotation : node.getAnnotations()) {
            state.print(annotation, this).print(" ");
        }
        this.writeModifiers(node.getModifiers(), state);
        if (!node.getTypeParameters().isEmpty()) {
            state.print("<");
            int iend = node.getTypeParameters().size() - 1;
            for (int i2 = 0; i2 <= iend; ++i2) {
                state.print(node.getTypeParameters().get(i2), this);
                if (i2 == iend) break;
                state.print(", ");
            }
            state.print("> ");
        }
        state.print(node.getName()).print("(");
        int iend = node.getArguments().size() - 1;
        for (i = 0; i <= iend; ++i) {
            state.print(node.getArguments().get(i), this);
            if (i == iend) break;
            state.print(", ");
        }
        state.print(")");
        if (!node.getThrownExceptions().isEmpty()) {
            state.print(" throws ");
            iend = node.getThrownExceptions().size() - 1;
            for (i = 0; i <= iend; ++i) {
                state.print(node.getThrownExceptions().get(i), this);
                if (i == iend) break;
                state.print(", ");
            }
        }
        state.print(" {\n");
        State indentedState = state.indent();
        if (node.implicitSuper()) {
            indentedState.printIndent().print("super();\n");
        }
        for (Statement statement : node.getStatements()) {
            indentedState.printIndent().print(statement, this).print(";\n");
        }
        return state.printIndent().print("}");
    }

    @Override
    public State visitContinue(Continue node, State state) {
        state.print("continue");
        if (node.getLabel() != null) {
            state.print(" ").print(node.getLabel());
        }
        return state;
    }

    @Override
    public State visitDefaultValue(DefaultValue node, State state) {
        return state.print("defaultValue");
    }

    @Override
    public State visitDoWhile(DoWhile node, State state) {
        return state.print("do ").print(node.getAction(), this).print("\nwhile ( ").print(node.getCondition(), this).print(")");
    }

    @Override
    public State visitEnumConstant(EnumConstant node, State state) {
        state.print(node.getName()).print("(");
        int iend = node.getArgs().size() - 1;
        for (int i = 0; i <= iend; ++i) {
            state.print(node.getArgs().get(i), this);
            if (i == iend) break;
            state.print(", ");
        }
        return state.print(")");
    }

    @Override
    public State visitFieldDecl(FieldDecl node, State state) {
        for (Annotation annotation : node.getAnnotations()) {
            state.print(annotation, this).print(" ");
        }
        this.writeModifiers(node.getModifiers(), state);
        state.print(node.getType(), this).print(" ").print(node.getName());
        if (node.getInitialization() != null) {
            state.print(" = ").print(node.getInitialization(), this);
        }
        return state;
    }

    @Override
    public State visitFieldRef(FieldRef node, State state) {
        return state.print(node.getReceiver(), this).print(".").print(node.getName());
    }

    @Override
    public State visitForeach(Foreach node, State state) {
        return state.print("for (").print(node.getElementVariable(), this).print(" : ").print(node.getCollection(), this).print(") ").print(node.getAction(), this);
    }

    @Override
    public State visitIf(If node, State state) {
        state.print("if (").print(node.getCondition(), this).print(") ").print(node.getThenStatement(), this);
        if (node.getElseStatement() != null) {
            state.print("\n").printIndent().print("else ").print(node.getElseStatement(), this);
        }
        return state;
    }

    @Override
    public State visitInitializer(Initializer node, State state) {
        state.print("{\n");
        State indentedState = state.indent();
        for (Statement statement : node.getStatements()) {
            indentedState.printIndent().print(statement, this).print(";\n");
        }
        return state.printIndent().print("}\n");
    }

    @Override
    public State visitInstanceOf(InstanceOf node, State state) {
        return state.print(node.getExpression(), this).print(" instanceof ").print(node.getType(), this);
    }

    @Override
    public State visitJavaDoc(JavaDoc node, State state) {
        state.print("/**\n");
        if (node.getMessage() != null) {
            state.printIndent().print(" * ").print(node.getMessage()).print("\n");
        }
        for (Map.Entry<String, String> argumentReference : node.getArgumentReferences().entrySet()) {
            state.printIndent().print(" * @param ").print(argumentReference.getKey()).print(" ").print(argumentReference.getValue()).print("\n");
        }
        for (Map.Entry<String, String> paramTypeReference : node.getParamTypeReferences().entrySet()) {
            state.printIndent().print(" * @param ").print(paramTypeReference.getKey()).print(" ").print(paramTypeReference.getValue()).print("\n");
        }
        for (Map.Entry exceptionReference : node.getExceptionReferences().entrySet()) {
            state.printIndent().print(" * @throws ").print(((TypeRef)exceptionReference.getKey()).getTypeName()).print(" ").print((CharSequence)exceptionReference.getValue()).print("\n");
        }
        if (node.getReturnMessage() != null) {
            state.printIndent().print(" * @return ").print(node.getReturnMessage()).print("\n");
        }
        state.printIndent().print(" */\n");
        return null;
    }

    @Override
    public State visitLocalDecl(LocalDecl node, State state) {
        for (Annotation annotation : node.getAnnotations()) {
            state.print(annotation, this).print(" ");
        }
        this.writeModifiers(node.getModifiers(), state);
        state.print(node.getType(), this).print(" ").print(node.getName());
        if (node.getInitialization() != null) {
            state.print(" = ").print(node.getInitialization(), this);
        }
        return state;
    }

    @Override
    public State visitMethodDecl(MethodDecl node, State state) {
        int i;
        for (Annotation annotation : node.getAnnotations()) {
            state.print(annotation, this).print(" ");
        }
        this.writeModifiers(node.getModifiers(), state);
        if (!node.getTypeParameters().isEmpty()) {
            state.print("<");
            int iend = node.getTypeParameters().size() - 1;
            for (int i2 = 0; i2 <= iend; ++i2) {
                state.print(node.getTypeParameters().get(i2), this);
                if (i2 == iend) break;
                state.print(", ");
            }
            state.print("> ");
        }
        state.print(node.getReturnType(), this).print(" ").print(node.getName()).print("(");
        int iend = node.getArguments().size() - 1;
        for (i = 0; i <= iend; ++i) {
            state.print(node.getArguments().get(i), this);
            if (i == iend) break;
            state.print(", ");
        }
        state.print(")");
        if (!node.getThrownExceptions().isEmpty()) {
            state.print(" throws ");
            iend = node.getThrownExceptions().size() - 1;
            for (i = 0; i <= iend; ++i) {
                state.print(node.getThrownExceptions().get(i), this);
                if (i == iend) break;
                state.print(", ");
            }
        }
        if (node.noBody()) {
            state.print(";\n");
        } else {
            state.print(" {\n");
            State indentedState = state.indent();
            for (Statement statement : node.getStatements()) {
                indentedState.printIndent().print(statement, this).print(";\n");
            }
            state.printIndent().print("}");
        }
        return state;
    }

    @Override
    public State visitNameRef(NameRef node, State state) {
        return state.print(node.getName());
    }

    @Override
    public State visitNew(New node, State state) {
        int iend;
        int i;
        state.print("new ").print(node.getType(), this);
        if (!node.getTypeArgs().isEmpty()) {
            state.print("<");
            iend = node.getTypeArgs().size() - 1;
            for (i = 0; i <= iend; ++i) {
                state.print(node.getTypeArgs().get(i), this);
                if (i == iend) break;
                state.print(", ");
            }
            state.print(">");
        }
        state.print("(");
        iend = node.getArgs().size() - 1;
        for (i = 0; i <= iend; ++i) {
            state.print(node.getArgs().get(i), this);
            if (i == iend) break;
            state.print(", ");
        }
        state.print(")");
        if (node.getAnonymousType() != null) {
            state.print(node.getAnonymousType(), this);
        }
        return state;
    }

    @Override
    public State visitNewArray(NewArray node, State state) {
        return state;
    }

    @Override
    public State visitNullLiteral(NullLiteral node, State state) {
        return state.print("null");
    }

    @Override
    public State visitNumberLiteral(NumberLiteral node, State state) {
        Number number = node.getNumber();
        if (number instanceof Integer) {
            return state.print(number.intValue());
        }
        if (number instanceof Long) {
            return state.print(number.longValue()).print("L");
        }
        if (number instanceof Float) {
            return state.print(Float.valueOf(number.floatValue())).print("f");
        }
        return state.print(number.doubleValue()).print("d");
    }

    @Override
    public State visitReturn(Return node, State state) {
        state.print("return");
        if (node.getExpression() != null) {
            state.print(" ").print(node.getExpression(), this);
        }
        return state;
    }

    @Override
    public State visitReturnDefault(ReturnDefault node, State state) {
        return state.print(AST.Return(AST.DefaultValue(null)));
    }

    @Override
    public State visitStringLiteral(StringLiteral node, State state) {
        return state.print("\"").print(node.getString()).print("\"");
    }

    @Override
    public State visitSwitch(Switch node, State state) {
        state.print("switch (").print(node.getExpression(), this).print(") {\n");
        for (Case caze : node.getCases()) {
            state.print(caze, this);
        }
        return state.printIndent().print("}");
    }

    @Override
    public State visitSynchronized(Synchronized node, State state) {
        state.print("synchronized (").print(node.getLock(), this).print(") {\n");
        State indentedState = state.indent();
        for (Statement statement : node.getStatements()) {
            indentedState.printIndent().print(statement, this).print(";\n");
        }
        return state.printIndent().print("}\n");
    }

    @Override
    public State visitThis(This node, State state) {
        if (!node.isImplicit()) {
            if (node.getType() != null) {
                state.print(node.getType(), this).print(".");
            }
            state.print("this");
        }
        return state;
    }

    @Override
    public State visitThrow(Throw node, State state) {
        return state.print("throw ").print(node.getExpression(), this);
    }

    @Override
    public State visitTry(Try node, State state) {
        state.print("try ").print(node.getTryBlock(), this);
        Iterator<Argument> iter = node.getCatchArguments().iterator();
        for (Block catchBlock : node.getCatchBlocks()) {
            Argument catchArgument = iter.next();
            state.print("catch (").print(catchArgument, this).print(") ").print(catchBlock, this);
        }
        return state;
    }

    @Override
    public State visitTypeParam(TypeParam node, State state) {
        state.print(node.getName());
        if (!node.getBounds().isEmpty()) {
            state.print(" extends ");
            int iend = node.getBounds().size() - 1;
            for (int i = 0; i <= iend; ++i) {
                state.print(node.getBounds().get(i), this);
                if (i == iend) break;
                state.print(" & ");
            }
        }
        return state;
    }

    @Override
    public State visitTypeRef(TypeRef node, State state) {
        int iend;
        int i;
        state.print(node.getTypeName());
        if (!node.getTypeArgs().isEmpty()) {
            state.print("<");
            iend = node.getTypeArgs().size() - 1;
            for (i = 0; i <= iend; ++i) {
                state.print(node.getTypeArgs().get(i), this);
                if (i == iend) break;
                state.print(", ");
            }
            state.print(">");
        }
        iend = node.getDims();
        for (i = 0; i < iend; ++i) {
            state.print("[]");
        }
        return state;
    }

    @Override
    public State visitUnary(Unary node, State state) {
        return state.print(node.getOperator()).print(node.getExpression(), this);
    }

    @Override
    public State visitWhile(While node, State state) {
        return state.print("while (").print(node.getCondition(), this).print(") ").print(node.getAction(), this);
    }

    @Override
    public State visitWildcard(Wildcard node, State state) {
        state.print("?");
        if (node.getBound() != null) {
            state.print(" ").print(node.getBound().name().toLowerCase()).print(" ").print(node.getType(), this);
        }
        return state;
    }

    @Override
    public State visitWrappedExpression(WrappedExpression node, State state) {
        return state.print(node.getWrappedObject());
    }

    @Override
    public State visitWrappedMethodDecl(WrappedMethodDecl node, State state) {
        return state.print(node.getWrappedObject());
    }

    @Override
    public State visitWrappedStatement(WrappedStatement node, State state) {
        return state.print(node.getWrappedObject());
    }

    @Override
    public State visitWrappedTypeRef(WrappedTypeRef node, State state) {
        return state.print(node.getWrappedObject());
    }

    public static class State {
        private final PrintStream out;
        private final String indent;
        private final int depth;

        public State(PrintStream out) {
            this(out, "  ", 0);
        }

        public State(PrintStream out, String indent) {
            this(out, indent, 0);
        }

        public State indent() {
            return new State(this.out, this.indent, this.depth + 1);
        }

        public State printIndent() {
            for (int i = 0; i < this.depth; ++i) {
                this.out.append(this.indent);
            }
            return this;
        }

        public State print(CharSequence csq) {
            this.out.append(csq);
            return this;
        }

        public State print(Object o) {
            return this.print(o.toString());
        }

        public State print(Node<?> node, ASTPrinter printer) {
            return (State)node.accept(printer, (State)this);
        }

        public State(PrintStream out, String indent, int depth) {
            this.out = out;
            this.indent = indent;
            this.depth = depth;
        }
    }

}

