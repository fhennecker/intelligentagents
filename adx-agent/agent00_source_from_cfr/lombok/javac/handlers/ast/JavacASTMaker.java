/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.BoundKind
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$MethodSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.TypeTags
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCArrayAccess
 *  com.sun.tools.javac.tree.JCTree$JCArrayTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCBinary
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCBreak
 *  com.sun.tools.javac.tree.JCTree$JCCase
 *  com.sun.tools.javac.tree.JCTree$JCCatch
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCContinue
 *  com.sun.tools.javac.tree.JCTree$JCDoWhileLoop
 *  com.sun.tools.javac.tree.JCTree$JCEnhancedForLoop
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
 *  com.sun.tools.javac.tree.JCTree$JCNewArray
 *  com.sun.tools.javac.tree.JCTree$JCNewClass
 *  com.sun.tools.javac.tree.JCTree$JCPrimitiveTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCSwitch
 *  com.sun.tools.javac.tree.JCTree$JCSynchronized
 *  com.sun.tools.javac.tree.JCTree$JCThrow
 *  com.sun.tools.javac.tree.JCTree$JCTry
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCTypeCast
 *  com.sun.tools.javac.tree.JCTree$JCTypeParameter
 *  com.sun.tools.javac.tree.JCTree$JCUnary
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.JCTree$JCWhileLoop
 *  com.sun.tools.javac.tree.JCTree$JCWildcard
 *  com.sun.tools.javac.tree.JCTree$TypeBoundKind
 *  com.sun.tools.javac.tree.TreeCopier
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers.ast;

import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeCopier;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.beans.ConstructorProperties;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
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
import lombok.core.util.As;
import lombok.core.util.Cast;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;

public final class JavacASTMaker
implements ASTVisitor<JCTree, Void> {
    private static final Map<String, Integer> UNARY_OPERATORS = new HashMap<String, Integer>();
    private static final Map<String, Integer> BINARY_OPERATORS;
    private static final Map<String, Integer> TYPES;
    private final JavacNode sourceNode;
    private final JCTree source;

    public <T extends JCTree> T build(Node<?> node) {
        return this.build(node, null);
    }

    public <T extends JCTree> T build(Node<?> node, Class<T> extectedType) {
        if (node == null) {
            return null;
        }
        JCTree tree = (JCTree)node.accept(this, null);
        if (JCTree.JCStatement.class == extectedType && tree instanceof JCTree.JCExpression) {
            tree = this.M(node).Exec((JCTree.JCExpression)tree);
        }
        return (T)((JCTree)Cast.uncheckedCast((Object)tree));
    }

    public <T extends JCTree> List<T> build(java.util.List<? extends Node<?>> nodes) {
        return this.build(nodes, null);
    }

    public <T extends JCTree> List<T> build(java.util.List<? extends Node<?>> nodes, Class<T> extectedType) {
        if (nodes == null) {
            return null;
        }
        ListBuffer list = ListBuffer.lb();
        for (Node node : nodes) {
            list.append(this.build(node, extectedType));
        }
        return list.toList();
    }

    private TreeMaker M(Node<?> node) {
        JCTree posHint;
        int pos = node.upTo(EnumConstant.class) != null || node.upTo(FieldDecl.class) != null ? -1 : ((posHint = (JCTree)node.posHint()) == null ? this.source.pos : posHint.pos);
        return this.sourceNode.getTreeMaker().at(pos);
    }

    private Name name(String name) {
        return this.sourceNode.toName(name);
    }

    private JCTree.JCExpression chainDots(Node<?> node, String name) {
        String[] elements = name.split("\\.");
        JCTree.JCIdent e = this.M(node).Ident(this.name(elements[0]));
        int iend = elements.length;
        for (int i = 1; i < iend; ++i) {
            e = this.M(node).Select((JCTree.JCExpression)e, this.name(elements[i]));
        }
        return e;
    }

    private JCTree.JCExpression fixLeadingDot(Node<?> node, JCTree.JCExpression expr) {
        if (expr instanceof JCTree.JCFieldAccess) {
            JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess)expr;
            JCTree.JCExpression selExpr = fieldAccess.selected;
            if (selExpr instanceof JCTree.JCIdent) {
                if ("".equals(selExpr.toString())) {
                    return this.M(node).Ident(fieldAccess.name);
                }
            } else if (selExpr instanceof JCTree.JCFieldAccess) {
                fieldAccess.selected = this.fixLeadingDot(node, selExpr);
            }
        }
        return expr;
    }

    private long flagsFor(Set<Modifier> modifiers) {
        long flags = 0;
        flags |= modifiers.contains((Object)Modifier.FINAL) ? 16 : 0;
        flags |= modifiers.contains((Object)Modifier.PRIVATE) ? 2 : 0;
        flags |= modifiers.contains((Object)Modifier.PROTECTED) ? 4 : 0;
        flags |= modifiers.contains((Object)Modifier.PUBLIC) ? 1 : 0;
        flags |= modifiers.contains((Object)Modifier.STATIC) ? 8 : 0;
        flags |= modifiers.contains((Object)Modifier.TRANSIENT) ? 128 : 0;
        return flags |= modifiers.contains((Object)Modifier.VOLATILE) ? 64 : 0;
    }

    private JCTree withJavaDoc(JCTree target, JavaDoc javaDoc) {
        JCTree.JCLiteral javadocExp = (JCTree.JCLiteral)this.build(javaDoc, JCTree.JCLiteral.class);
        if (javadocExp != null) {
            JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit)((JavacNode)this.sourceNode.top()).get();
            compilationUnit.docComments.put(target, As.string(javadocExp.getValue()));
        }
        return target;
    }

    @Override
    public JCTree visitAnnotation(Annotation node, Void p) {
        ListBuffer args = ListBuffer.lb();
        for (Map.Entry entry : node.getValues().entrySet()) {
            args.append(this.build(AST.Assign(AST.Name(entry.getKey()), entry.getValue()), JCTree.JCExpression.class));
        }
        JCTree.JCAnnotation annotation = JavacHandlerUtil.setGeneratedBy(this.M(node).Annotation(this.build(node.getType()), args.toList()), this.source);
        return annotation;
    }

    @Override
    public JCTree visitArgument(Argument node, Void p) {
        JCTree.JCModifiers mods = JavacHandlerUtil.setGeneratedBy(this.M(node).Modifiers(this.flagsFor(node.getModifiers()), this.build(node.getAnnotations(), JCTree.JCAnnotation.class)), this.source);
        JCTree.JCVariableDecl argument = JavacHandlerUtil.setGeneratedBy(this.M(node).VarDef(mods, this.name(node.getName()), (JCTree.JCExpression)this.build(node.getType(), JCTree.JCExpression.class), null), this.source);
        return argument;
    }

    @Override
    public JCTree visitArrayRef(ArrayRef node, Void p) {
        JCTree.JCArrayAccess arrayAccess = JavacHandlerUtil.setGeneratedBy(this.M(node).Indexed((JCTree.JCExpression)this.build(node.getIndexed(), JCTree.JCExpression.class), (JCTree.JCExpression)this.build(node.getIndex(), JCTree.JCExpression.class)), this.source);
        return arrayAccess;
    }

    @Override
    public JCTree visitAssignment(Assignment node, Void p) {
        JCTree.JCAssign assignment = JavacHandlerUtil.setGeneratedBy(this.M(node).Assign((JCTree.JCExpression)this.build(node.getLeft(), JCTree.JCExpression.class), (JCTree.JCExpression)this.build(node.getRight(), JCTree.JCExpression.class)), this.source);
        return assignment;
    }

    @Override
    public JCTree visitBinary(Binary node, Void p) {
        String operator = node.getOperator();
        if (!BINARY_OPERATORS.containsKey(operator)) {
            throw new IllegalStateException(String.format("Unknown binary operator '%s'", operator));
        }
        int opCode = BINARY_OPERATORS.get(operator);
        JCTree.JCBinary binary = JavacHandlerUtil.setGeneratedBy(this.M(node).Binary(opCode, (JCTree.JCExpression)this.build(node.getLeft(), JCTree.JCExpression.class), (JCTree.JCExpression)this.build(node.getRight(), JCTree.JCExpression.class)), this.source);
        return binary;
    }

    @Override
    public JCTree visitBlock(Block node, Void p) {
        JCTree.JCBlock block = JavacHandlerUtil.setGeneratedBy(this.M(node).Block(0, this.build(node.getStatements(), JCTree.JCStatement.class)), this.source);
        return block;
    }

    @Override
    public JCTree visitBooleanLiteral(BooleanLiteral node, Void p) {
        JCTree.JCLiteral literal = JavacHandlerUtil.setGeneratedBy(this.M(node).Literal(TYPES.get("boolean").intValue(), (Object)(node.isTrue() ? 1 : 0)), this.source);
        return literal;
    }

    @Override
    public JCTree visitBreak(Break node, Void p) {
        JCTree.JCBreak breakStatement = JavacHandlerUtil.setGeneratedBy(this.M(node).Break(node.getLabel() == null ? null : this.name(node.getLabel())), this.source);
        return breakStatement;
    }

    @Override
    public JCTree visitCall(Call node, Void p) {
        JCTree.JCIdent fn = node.getReceiver() == null ? this.M(node).Ident(this.name(node.getName())) : this.M(node).Select((JCTree.JCExpression)this.build(node.getReceiver(), JCTree.JCExpression.class), this.name(node.getName()));
        JCTree.JCMethodInvocation methodInvocation = JavacHandlerUtil.setGeneratedBy(this.M(node).Apply(this.build(node.getTypeArgs(), JCTree.JCExpression.class), (JCTree.JCExpression)fn, this.build(node.getArgs(), JCTree.JCExpression.class)), this.source);
        return methodInvocation;
    }

    @Override
    public JCTree visitCase(Case node, Void p) {
        JCTree.JCCase caze = JavacHandlerUtil.setGeneratedBy(this.M(node).Case((JCTree.JCExpression)this.build(node.getPattern(), JCTree.JCExpression.class), this.build(node.getStatements(), JCTree.JCStatement.class)), this.source);
        return caze;
    }

    @Override
    public JCTree visitCast(lombok.ast.Cast node, Void p) {
        JCTree.JCTypeCast cast = JavacHandlerUtil.setGeneratedBy(this.M(node).TypeCast(this.build(node.getType()), (JCTree.JCExpression)this.build(node.getExpression(), JCTree.JCExpression.class)), this.source);
        return cast;
    }

    @Override
    public JCTree visitCharLiteral(CharLiteral node, Void p) {
        JCTree.JCLiteral literal = JavacHandlerUtil.setGeneratedBy(this.M(node).Literal((Object)Character.valueOf(node.getCharacter().charAt(0))), this.source);
        return literal;
    }

    @Override
    public JCTree visitClassDecl(ClassDecl node, Void p) {
        JCTree.JCModifiers mods = JavacHandlerUtil.setGeneratedBy(this.M(node).Modifiers(this.flagsFor(node.getModifiers()), this.build(node.getAnnotations(), JCTree.JCAnnotation.class)), this.source);
        if (node.isInterface()) {
            mods.flags |= 512;
        }
        ListBuffer defs = ListBuffer.lb();
        defs.appendList(this.build(node.getFields()));
        defs.appendList(this.build(node.getMethods()));
        defs.appendList(this.build(node.getMemberTypes()));
        List<T> typarams = this.build(node.getTypeParameters());
        JCTree.JCExpression extending = (JCTree.JCExpression)this.build(node.getSuperclass());
        List<T> implementing = this.build(node.getSuperInterfaces());
        JCTree.JCClassDecl classDecl = JavacHandlerUtil.setGeneratedBy(this.createClassDef(node, mods, this.name(node.getName()), typarams, extending, implementing, defs.toList()), this.source);
        return classDecl;
    }

    private JCTree.JCClassDecl createClassDef(Node<?> node, JCTree.JCModifiers mods, Name name, List<JCTree.JCTypeParameter> typarams, JCTree.JCExpression extending, List<JCTree.JCExpression> implementing, List<JCTree> defs) {
        try {
            Method classDefMethod = null;
            for (Method method : TreeMaker.class.getMethods()) {
                if (!"ClassDef".equals(method.getName())) continue;
                classDefMethod = method;
                break;
            }
            if (classDefMethod == null) {
                throw new IllegalStateException();
            }
            return (JCTree.JCClassDecl)classDefMethod.invoke((Object)this.M(node), new Object[]{mods, name, typarams, extending, implementing, defs});
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public JCTree visitConstructorDecl(ConstructorDecl node, Void p) {
        JCTree.JCModifiers mods = JavacHandlerUtil.setGeneratedBy(this.M(node).Modifiers(this.flagsFor(node.getModifiers()), this.build(node.getAnnotations(), JCTree.JCAnnotation.class)), this.source);
        List statements = this.build(node.getStatements(), JCTree.JCStatement.class);
        if (node.implicitSuper()) {
            statements = statements.prepend(this.build(AST.Call("super"), JCTree.JCStatement.class));
        }
        List<T> typarams = this.build(node.getTypeParameters());
        List<T> params = this.build(node.getArguments());
        List<T> thrown = this.build(node.getThrownExceptions());
        JCTree.JCBlock body = JavacHandlerUtil.setGeneratedBy(this.M(node).Block(0, statements), this.source);
        JCTree.JCMethodDecl constructor = JavacHandlerUtil.setGeneratedBy(this.M(node).MethodDef(mods, this.name("<init>"), null, typarams, params, thrown, body, null), this.source);
        return this.withJavaDoc((JCTree)constructor, node.getJavaDoc());
    }

    @Override
    public JCTree visitContinue(Continue node, Void p) {
        JCTree.JCContinue continueStatement = JavacHandlerUtil.setGeneratedBy(this.M(node).Continue(node.getLabel() == null ? null : this.name(node.getLabel())), this.source);
        return continueStatement;
    }

    @Override
    public JCTree visitDefaultValue(DefaultValue node, Void p) {
        Expression defaultValue = AST.Null();
        JCTree.JCExpression type = (JCTree.JCExpression)this.build(node.getType());
        if (type instanceof JCTree.JCPrimitiveTypeTree) {
            JCTree.JCPrimitiveTypeTree primitiveType = (JCTree.JCPrimitiveTypeTree)type;
            defaultValue = primitiveType.typetag == TYPES.get("void") ? null : AST.Expr((Object)this.M(node).Literal(primitiveType.typetag, (Object)0));
        }
        return this.build(defaultValue);
    }

    @Override
    public JCTree visitDoWhile(DoWhile node, Void p) {
        JCTree.JCDoWhileLoop doStatement = JavacHandlerUtil.setGeneratedBy(this.M(node).DoLoop((JCTree.JCStatement)this.build(node.getAction(), JCTree.JCStatement.class), (JCTree.JCExpression)this.build(node.getCondition(), JCTree.JCExpression.class)), this.source);
        return doStatement;
    }

    @Override
    public JCTree visitEnumConstant(EnumConstant node, Void p) {
        JCTree.JCModifiers mods = JavacHandlerUtil.setGeneratedBy(this.M(node).Modifiers(16409, this.build(node.getAnnotations(), JCTree.JCAnnotation.class)), this.source);
        ClassDecl enumClassDecl = (ClassDecl)node.upTo(ClassDecl.class);
        JCTree.JCExpression varType = enumClassDecl == null ? (JCTree.JCExpression)this.build(AST.Type(Javac.typeNodeOf(this.sourceNode).getName())) : this.chainDots(node, enumClassDecl.getName());
        List nilExp = List.nil();
        List<T> args = this.build(node.getArgs());
        JCTree.JCNewClass init = JavacHandlerUtil.setGeneratedBy(this.M(node).NewClass(null, nilExp, varType, args, null), this.source);
        JCTree.JCVariableDecl enumContant = JavacHandlerUtil.setGeneratedBy(this.M(node).VarDef(mods, this.name(node.getName()), varType, (JCTree.JCExpression)init), this.source);
        return this.withJavaDoc((JCTree)enumContant, node.getJavaDoc());
    }

    @Override
    public JCTree visitFieldDecl(FieldDecl node, Void p) {
        JCTree.JCModifiers mods = JavacHandlerUtil.setGeneratedBy(this.M(node).Modifiers(this.flagsFor(node.getModifiers()), this.build(node.getAnnotations(), JCTree.JCAnnotation.class)), this.source);
        JCTree.JCExpression vartype = (JCTree.JCExpression)this.build(node.getType());
        JCTree.JCExpression init = (JCTree.JCExpression)this.build(node.getInitialization());
        JCTree.JCVariableDecl field = JavacHandlerUtil.setGeneratedBy(this.M(node).VarDef(mods, this.name(node.getName()), vartype, init), this.source);
        return this.withJavaDoc((JCTree)field, node.getJavaDoc());
    }

    @Override
    public JCTree visitFieldRef(FieldRef node, Void p) {
        Name fieldName = this.name(node.getName());
        if (node.getReceiver() == null) {
            return JavacHandlerUtil.setGeneratedBy(this.M(node).Ident(fieldName), this.source);
        }
        return JavacHandlerUtil.setGeneratedBy(this.M(node).Select((JCTree.JCExpression)this.build(node.getReceiver(), JCTree.JCExpression.class), fieldName), this.source);
    }

    @Override
    public JCTree visitForeach(Foreach node, Void p) {
        JCTree.JCVariableDecl var = (JCTree.JCVariableDecl)this.build(node.getElementVariable());
        JCTree.JCExpression expr = (JCTree.JCExpression)this.build(node.getCollection());
        JCTree.JCStatement body = (JCTree.JCStatement)this.build(node.getAction(), JCTree.JCStatement.class);
        JCTree.JCEnhancedForLoop foreach = JavacHandlerUtil.setGeneratedBy(this.M(node).ForeachLoop(var, expr, body), this.source);
        return foreach;
    }

    @Override
    public JCTree visitIf(If node, Void p) {
        JCTree.JCExpression cond = (JCTree.JCExpression)this.build(node.getCondition());
        JCTree.JCStatement thenpart = (JCTree.JCStatement)this.build(node.getThenStatement(), JCTree.JCStatement.class);
        JCTree.JCStatement elsepart = (JCTree.JCStatement)this.build(node.getElseStatement(), JCTree.JCStatement.class);
        JCTree.JCIf ifStatement = JavacHandlerUtil.setGeneratedBy(this.M(node).If(cond, thenpart, elsepart), this.source);
        return ifStatement;
    }

    @Override
    public JCTree visitInitializer(Initializer node, Void p) {
        JCTree.JCBlock block = JavacHandlerUtil.setGeneratedBy(this.M(node).Block(this.flagsFor(node.getModifiers()), this.build(node.getStatements(), JCTree.JCStatement.class)), this.source);
        return block;
    }

    @Override
    public JCTree visitInstanceOf(InstanceOf node, Void p) {
        JCTree.JCInstanceOf instanceOf = JavacHandlerUtil.setGeneratedBy(this.M(node).TypeTest((JCTree.JCExpression)this.build(node.getExpression(), JCTree.JCExpression.class), this.build(node.getType())), this.source);
        return instanceOf;
    }

    @Override
    public JCTree visitJavaDoc(JavaDoc node, Void p) {
        StringBuilder javadoc = new StringBuilder();
        if (node.getMessage() != null) {
            javadoc.append(node.getMessage()).append("\n");
        }
        for (Map.Entry<String, String> argumentReference : node.getArgumentReferences().entrySet()) {
            javadoc.append("@param ").append(argumentReference.getKey()).append(" ").append(argumentReference.getValue()).append("\n");
        }
        for (Map.Entry<String, String> paramTypeReference : node.getParamTypeReferences().entrySet()) {
            javadoc.append("@param <").append(paramTypeReference.getKey()).append("> ").append(paramTypeReference.getValue()).append("\n");
        }
        for (Map.Entry exceptionReference : node.getExceptionReferences().entrySet()) {
            javadoc.append("@throws ").append(((TypeRef)exceptionReference.getKey()).getTypeName()).append(" ").append((String)exceptionReference.getValue()).append("\n");
        }
        if (node.getReturnMessage() != null) {
            javadoc.append("@return ").append(node.getReturnMessage()).append("\n");
        }
        return this.M(node).Literal((Object)javadoc.toString());
    }

    @Override
    public JCTree visitLocalDecl(LocalDecl node, Void p) {
        JCTree.JCModifiers mods = JavacHandlerUtil.setGeneratedBy(this.M(node).Modifiers(this.flagsFor(node.getModifiers()), this.build(node.getAnnotations(), JCTree.JCAnnotation.class)), this.source);
        JCTree.JCExpression vartype = (JCTree.JCExpression)this.build(node.getType());
        JCTree.JCExpression init = (JCTree.JCExpression)this.build(node.getInitialization());
        JCTree.JCVariableDecl local = JavacHandlerUtil.setGeneratedBy(this.M(node).VarDef(mods, this.name(node.getName()), vartype, init), this.source);
        return local;
    }

    @Override
    public JCTree visitMethodDecl(MethodDecl node, Void p) {
        JCTree.JCModifiers mods = JavacHandlerUtil.setGeneratedBy(this.M(node).Modifiers(this.flagsFor(node.getModifiers()), this.build(node.getAnnotations(), JCTree.JCAnnotation.class)), this.source);
        JCTree.JCExpression restype = (JCTree.JCExpression)this.build(node.getReturnType());
        List<T> typarams = this.build(node.getTypeParameters());
        List<T> params = this.build(node.getArguments());
        List<T> thrown = this.build(node.getThrownExceptions());
        JCTree.JCBlock body = null;
        if (!node.noBody() && (mods.flags & 1024) == 0) {
            body = JavacHandlerUtil.setGeneratedBy(this.M(node).Block(0, this.build(node.getStatements(), JCTree.JCStatement.class)), this.source);
        }
        JCTree.JCMethodDecl method = JavacHandlerUtil.setGeneratedBy(this.M(node).MethodDef(mods, this.name(node.getName()), restype, typarams, params, thrown, body, null), this.source);
        return this.withJavaDoc((JCTree)method, node.getJavaDoc());
    }

    @Override
    public JCTree visitNameRef(NameRef node, Void p) {
        return JavacHandlerUtil.setGeneratedBy(this.chainDots(node, node.getName()), this.source);
    }

    @Override
    public JCTree visitNew(New node, Void p) {
        List<T> typeargs = this.build(node.getTypeArgs());
        JCTree.JCExpression clazz = (JCTree.JCExpression)this.build(node.getType());
        List<T> args = this.build(node.getArgs());
        JCTree.JCClassDecl def = (JCTree.JCClassDecl)this.build(node.getAnonymousType());
        JCTree.JCNewClass newClass = JavacHandlerUtil.setGeneratedBy(this.M(node).NewClass(null, typeargs, clazz, args, def), this.source);
        return newClass;
    }

    @Override
    public JCTree visitNewArray(NewArray node, Void p) {
        ListBuffer dims = ListBuffer.lb();
        dims.appendList(this.build(node.getDimensionExpressions(), JCTree.JCExpression.class));
        JCTree.JCExpression elemtype = (JCTree.JCExpression)this.build(node.getType());
        List<T> initializerExpressions = this.build(node.getInitializerExpressions(), JCTree.JCExpression.class);
        JCTree.JCNewArray newClass = JavacHandlerUtil.setGeneratedBy(this.M(node).NewArray(elemtype, dims.toList(), initializerExpressions.isEmpty() ? null : initializerExpressions), this.source);
        return newClass;
    }

    @Override
    public JCTree visitNullLiteral(NullLiteral node, Void p) {
        JCTree.JCLiteral literal = JavacHandlerUtil.setGeneratedBy(this.M(node).Literal(TYPES.get("null").intValue(), (Object)null), this.source);
        return literal;
    }

    @Override
    public JCTree visitNumberLiteral(NumberLiteral node, Void p) {
        JCTree.JCLiteral literal = JavacHandlerUtil.setGeneratedBy(this.M(node).Literal((Object)node.getNumber()), this.source);
        return literal;
    }

    @Override
    public JCTree visitReturn(Return node, Void p) {
        JCTree.JCReturn returnStatement = JavacHandlerUtil.setGeneratedBy(this.M(node).Return((JCTree.JCExpression)this.build(node.getExpression(), JCTree.JCExpression.class)), this.source);
        return returnStatement;
    }

    @Override
    public JCTree visitReturnDefault(ReturnDefault node, Void p) {
        TypeRef returnType = ((MethodDecl)node.upTo(MethodDecl.class)).getReturnType();
        if (returnType == null) {
            returnType = AST.Type(Javac.methodNodeOf(this.sourceNode).getName());
        }
        return this.build(AST.Return(AST.DefaultValue(returnType)));
    }

    @Override
    public JCTree visitStringLiteral(StringLiteral node, Void p) {
        JCTree.JCLiteral literal = JavacHandlerUtil.setGeneratedBy(this.M(node).Literal((Object)node.getString()), this.source);
        return literal;
    }

    @Override
    public JCTree visitSwitch(Switch node, Void p) {
        JCTree.JCSwitch switchStatement = JavacHandlerUtil.setGeneratedBy(this.M(node).Switch((JCTree.JCExpression)this.build(node.getExpression(), JCTree.JCExpression.class), this.build(node.getCases(), JCTree.JCCase.class)), this.source);
        return switchStatement;
    }

    @Override
    public JCTree visitSynchronized(Synchronized node, Void p) {
        JCTree.JCBlock block = JavacHandlerUtil.setGeneratedBy(this.M(node).Block(0, this.build(node.getStatements(), JCTree.JCStatement.class)), this.source);
        JCTree.JCSynchronized synchronizedStatemenet = JavacHandlerUtil.setGeneratedBy(this.M(node).Synchronized((JCTree.JCExpression)this.build(node.getLock(), JCTree.JCExpression.class), block), this.source);
        return synchronizedStatemenet;
    }

    @Override
    public JCTree visitThis(This node, Void p) {
        Name thisName = this.name("this");
        if (node.getType() == null) {
            return JavacHandlerUtil.setGeneratedBy(this.M(node).Ident(thisName), this.source);
        }
        return JavacHandlerUtil.setGeneratedBy(this.M(node).Select((JCTree.JCExpression)this.build(node.getType(), JCTree.JCExpression.class), thisName), this.source);
    }

    @Override
    public JCTree visitThrow(Throw node, Void p) {
        JCTree.JCThrow throwStatement = JavacHandlerUtil.setGeneratedBy(this.M(node).Throw(this.build(node.getExpression(), JCTree.JCExpression.class)), this.source);
        return throwStatement;
    }

    @Override
    public JCTree visitTry(Try node, Void p) {
        ListBuffer catchers = ListBuffer.lb();
        Iterator<Argument> iter = node.getCatchArguments().iterator();
        for (Block catchBlock : node.getCatchBlocks()) {
            Argument catchArgument = iter.next();
            catchers.append((Object)this.M(node).Catch((JCTree.JCVariableDecl)this.build(catchArgument, JCTree.JCVariableDecl.class), (JCTree.JCBlock)this.build(catchBlock, JCTree.JCBlock.class)));
        }
        JCTree.JCTry tryStatement = JavacHandlerUtil.setGeneratedBy(this.M(node).Try((JCTree.JCBlock)this.build(node.getTryBlock(), JCTree.JCBlock.class), catchers.toList(), (JCTree.JCBlock)this.build(node.getFinallyBlock(), JCTree.JCBlock.class)), this.source);
        return tryStatement;
    }

    @Override
    public JCTree visitTypeParam(TypeParam node, Void p) {
        JCTree.JCTypeParameter typeParam = JavacHandlerUtil.setGeneratedBy(this.M(node).TypeParameter(this.name(node.getName()), this.build(node.getBounds(), JCTree.JCExpression.class)), this.source);
        return typeParam;
    }

    @Override
    public JCTree visitTypeRef(TypeRef node, Void p) {
        JCTree.JCExpression typeRef;
        String typeName = node.getTypeName();
        if (TYPES.containsKey(typeName)) {
            typeRef = this.M(node).TypeIdent(TYPES.get(typeName).intValue());
            typeRef = JavacHandlerUtil.setGeneratedBy(typeRef, this.source);
            if ("void".equals(typeName)) {
                return typeRef;
            }
        } else {
            typeRef = this.chainDots(node, node.getTypeName());
            typeRef = JavacHandlerUtil.setGeneratedBy(typeRef, this.source);
            if (!node.getTypeArgs().isEmpty()) {
                typeRef = this.M(node).TypeApply(typeRef, this.build(node.getTypeArgs(), JCTree.JCExpression.class));
                typeRef = JavacHandlerUtil.setGeneratedBy(typeRef, this.source);
            }
        }
        for (int i = 0; i < node.getDims(); ++i) {
            typeRef = (JCTree.JCExpression)JavacHandlerUtil.setGeneratedBy(this.M(node).TypeArray(typeRef), this.source);
        }
        return typeRef;
    }

    @Override
    public JCTree visitUnary(Unary node, Void p) {
        String operator = node.getOperator();
        if (!UNARY_OPERATORS.containsKey(operator)) {
            throw new IllegalStateException(String.format("Unknown unary operator '%s'", operator));
        }
        int opCode = UNARY_OPERATORS.get(operator);
        JCTree.JCUnary unary = JavacHandlerUtil.setGeneratedBy(this.M(node).Unary(opCode, (JCTree.JCExpression)this.build(node.getExpression(), JCTree.JCExpression.class)), this.source);
        return unary;
    }

    @Override
    public JCTree visitWhile(While node, Void p) {
        JCTree.JCWhileLoop whileLoop = JavacHandlerUtil.setGeneratedBy(this.M(node).WhileLoop((JCTree.JCExpression)this.build(node.getCondition(), JCTree.JCExpression.class), (JCTree.JCStatement)this.build(node.getAction(), JCTree.JCStatement.class)), this.source);
        return whileLoop;
    }

    @Override
    public JCTree visitWildcard(Wildcard node, Void p) {
        BoundKind boundKind = BoundKind.UNBOUND;
        if (node.getBound() != null) {
            switch (node.getBound()) {
                case SUPER: {
                    boundKind = BoundKind.SUPER;
                    break;
                }
                default: {
                    boundKind = BoundKind.EXTENDS;
                }
            }
        }
        JCTree.TypeBoundKind kind = JavacHandlerUtil.setGeneratedBy(this.M(node).TypeBoundKind(boundKind), this.source);
        JCTree.JCWildcard wildcard = JavacHandlerUtil.setGeneratedBy(this.M(node).Wildcard(kind, this.build(node.getType(), JCTree.JCExpression.class)), this.source);
        return wildcard;
    }

    @Override
    public JCTree visitWrappedExpression(WrappedExpression node, Void p) {
        JCTree.JCExpression expression = (JCTree.JCExpression)new TreeCopier(this.M(node)).copy((JCTree)((JCTree.JCExpression)node.getWrappedObject()));
        return expression;
    }

    @Override
    public JCTree visitWrappedMethodDecl(WrappedMethodDecl node, Void p) {
        Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol)node.getWrappedObject();
        Type mtype = methodSymbol.type;
        if (node.getReturnType() == null) {
            node.withReturnType(AST.Type((Object)this.fixLeadingDot(node, this.M(node).Type(mtype.getReturnType()))));
        }
        if (node.getThrownExceptions().isEmpty()) {
            for (JCTree.JCExpression expr : this.M(node).Types(mtype.getThrownTypes())) {
                node.withThrownException(AST.Type((Object)this.fixLeadingDot(node, expr)));
            }
        }
        if (node.getArguments().isEmpty()) {
            for (JCTree.JCVariableDecl param : this.M(node).Params(mtype.getParameterTypes(), (Symbol)methodSymbol)) {
                node.withArgument(AST.Arg(AST.Type((Object)this.fixLeadingDot(node, param.vartype)), As.string((Object)param.name)));
            }
        }
        if (node.getTypeParameters().isEmpty()) {
            for (JCTree.JCTypeParameter typaram : this.M(node).TypeParams(mtype.getTypeArguments())) {
                TypeParam typeParam = AST.TypeParam(As.string((Object)typaram.name));
                for (JCTree.JCExpression expr : typaram.bounds) {
                    typeParam.withBound(AST.Type((Object)this.fixLeadingDot(node, expr)));
                }
                node.withTypeParameter(typeParam);
            }
        }
        JCTree.JCModifiers mods = this.M(node).Modifiers(methodSymbol.flags() & -1025, this.build(node.getAnnotations(), JCTree.JCAnnotation.class));
        JCTree.JCExpression restype = (JCTree.JCExpression)this.build(node.getReturnType());
        Name name = methodSymbol.name;
        List<T> thrown = this.build(node.getThrownExceptions(), JCTree.JCExpression.class);
        List<T> typarams = this.build(node.getTypeParameters(), JCTree.JCTypeParameter.class);
        List<T> params = this.build(node.getArguments(), JCTree.JCVariableDecl.class);
        JCTree.JCBlock body = null;
        if (!node.noBody()) {
            body = this.M(node).Block(0, this.build(node.getStatements(), JCTree.JCStatement.class));
        }
        JCTree.JCMethodDecl method = this.M(node).MethodDef(mods, name, restype, typarams, params, thrown, body, null);
        return method;
    }

    @Override
    public JCTree visitWrappedStatement(WrappedStatement node, Void p) {
        JCTree.JCStatement statement = (JCTree.JCStatement)new TreeCopier(this.M(node)).copy((JCTree)((JCTree.JCStatement)node.getWrappedObject()));
        return statement;
    }

    @Override
    public JCTree visitWrappedTypeRef(WrappedTypeRef node, Void p) {
        JCTree.JCExpression typeRef = null;
        if (node.getWrappedObject() instanceof Type) {
            typeRef = this.fixLeadingDot(node, this.M(node).Type((Type)node.getWrappedObject()));
        } else if (node.getWrappedObject() instanceof JCTree.JCExpression) {
            typeRef = (JCTree.JCExpression)new TreeCopier(this.M(node)).copy((JCTree)((JCTree.JCExpression)node.getWrappedObject()));
        }
        for (int i = 0; i < node.getDims(); ++i) {
            typeRef = (JCTree.JCExpression)JavacHandlerUtil.setGeneratedBy(this.M(node).TypeArray(typeRef), this.source);
        }
        return typeRef;
    }

    @ConstructorProperties(value={"sourceNode", "source"})
    public JavacASTMaker(JavacNode sourceNode, JCTree source) {
        this.sourceNode = sourceNode;
        this.source = source;
    }

    static {
        UNARY_OPERATORS.put("+", lombok.javac.Javac.getCtcInt(JCTree.class, "POS"));
        UNARY_OPERATORS.put("-", lombok.javac.Javac.getCtcInt(JCTree.class, "NEG"));
        UNARY_OPERATORS.put("!", lombok.javac.Javac.getCtcInt(JCTree.class, "NOT"));
        UNARY_OPERATORS.put("~", lombok.javac.Javac.getCtcInt(JCTree.class, "COMPL"));
        UNARY_OPERATORS.put("++X", lombok.javac.Javac.getCtcInt(JCTree.class, "PREINC"));
        UNARY_OPERATORS.put("--X", lombok.javac.Javac.getCtcInt(JCTree.class, "PREDEC"));
        UNARY_OPERATORS.put("X++", lombok.javac.Javac.getCtcInt(JCTree.class, "POSTINC"));
        UNARY_OPERATORS.put("X--", lombok.javac.Javac.getCtcInt(JCTree.class, "POSTDEC"));
        BINARY_OPERATORS = new HashMap<String, Integer>();
        BINARY_OPERATORS.put("||", lombok.javac.Javac.getCtcInt(JCTree.class, "OR"));
        BINARY_OPERATORS.put("&&", lombok.javac.Javac.getCtcInt(JCTree.class, "AND"));
        BINARY_OPERATORS.put("==", lombok.javac.Javac.getCtcInt(JCTree.class, "EQ"));
        BINARY_OPERATORS.put("!=", lombok.javac.Javac.getCtcInt(JCTree.class, "NE"));
        BINARY_OPERATORS.put("<", lombok.javac.Javac.getCtcInt(JCTree.class, "LT"));
        BINARY_OPERATORS.put(">", lombok.javac.Javac.getCtcInt(JCTree.class, "GT"));
        BINARY_OPERATORS.put("<=", lombok.javac.Javac.getCtcInt(JCTree.class, "LE"));
        BINARY_OPERATORS.put(">=", lombok.javac.Javac.getCtcInt(JCTree.class, "GE"));
        BINARY_OPERATORS.put("|", lombok.javac.Javac.getCtcInt(JCTree.class, "BITOR"));
        BINARY_OPERATORS.put("^", lombok.javac.Javac.getCtcInt(JCTree.class, "BITXOR"));
        BINARY_OPERATORS.put("&", lombok.javac.Javac.getCtcInt(JCTree.class, "BITAND"));
        BINARY_OPERATORS.put("<<", lombok.javac.Javac.getCtcInt(JCTree.class, "SL"));
        BINARY_OPERATORS.put(">>", lombok.javac.Javac.getCtcInt(JCTree.class, "SR"));
        BINARY_OPERATORS.put(">>>", lombok.javac.Javac.getCtcInt(JCTree.class, "USR"));
        BINARY_OPERATORS.put("+", lombok.javac.Javac.getCtcInt(JCTree.class, "PLUS"));
        BINARY_OPERATORS.put("-", lombok.javac.Javac.getCtcInt(JCTree.class, "MINUS"));
        BINARY_OPERATORS.put("*", lombok.javac.Javac.getCtcInt(JCTree.class, "MUL"));
        BINARY_OPERATORS.put("/", lombok.javac.Javac.getCtcInt(JCTree.class, "DIV"));
        BINARY_OPERATORS.put("%", lombok.javac.Javac.getCtcInt(JCTree.class, "MOD"));
        TYPES = new HashMap<String, Integer>();
        TYPES.put("none", lombok.javac.Javac.getCtcInt(TypeTags.class, "NONE"));
        TYPES.put("null", lombok.javac.Javac.getCtcInt(TypeTags.class, "BOT"));
        TYPES.put("void", lombok.javac.Javac.getCtcInt(TypeTags.class, "VOID"));
        TYPES.put("int", lombok.javac.Javac.getCtcInt(TypeTags.class, "INT"));
        TYPES.put("long", lombok.javac.Javac.getCtcInt(TypeTags.class, "LONG"));
        TYPES.put("short", lombok.javac.Javac.getCtcInt(TypeTags.class, "SHORT"));
        TYPES.put("boolean", lombok.javac.Javac.getCtcInt(TypeTags.class, "BOOLEAN"));
        TYPES.put("byte", lombok.javac.Javac.getCtcInt(TypeTags.class, "BYTE"));
        TYPES.put("char", lombok.javac.Javac.getCtcInt(TypeTags.class, "CHAR"));
        TYPES.put("float", lombok.javac.Javac.getCtcInt(TypeTags.class, "FLOAT"));
        TYPES.put("double", lombok.javac.Javac.getCtcInt(TypeTags.class, "DOUBLE"));
    }

}

