/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.core.compiler.CharOperation
 *  org.eclipse.jdt.internal.compiler.CompilationResult
 *  org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 *  org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ArrayReference
 *  org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Assignment
 *  org.eclipse.jdt.internal.compiler.ast.BinaryExpression
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.BreakStatement
 *  org.eclipse.jdt.internal.compiler.ast.CaseStatement
 *  org.eclipse.jdt.internal.compiler.ast.CastExpression
 *  org.eclipse.jdt.internal.compiler.ast.CharLiteral
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ContinueStatement
 *  org.eclipse.jdt.internal.compiler.ast.DoStatement
 *  org.eclipse.jdt.internal.compiler.ast.DoubleLiteral
 *  org.eclipse.jdt.internal.compiler.ast.EmptyStatement
 *  org.eclipse.jdt.internal.compiler.ast.EqualExpression
 *  org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FalseLiteral
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldReference
 *  org.eclipse.jdt.internal.compiler.ast.FloatLiteral
 *  org.eclipse.jdt.internal.compiler.ast.ForeachStatement
 *  org.eclipse.jdt.internal.compiler.ast.IfStatement
 *  org.eclipse.jdt.internal.compiler.ast.Initializer
 *  org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression
 *  org.eclipse.jdt.internal.compiler.ast.IntLiteral
 *  org.eclipse.jdt.internal.compiler.ast.Javadoc
 *  org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.LongLiteral
 *  org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.MemberValuePair
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.NormalAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.NullLiteral
 *  org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.PostfixExpression
 *  org.eclipse.jdt.internal.compiler.ast.PrefixExpression
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.StringLiteral
 *  org.eclipse.jdt.internal.compiler.ast.SwitchStatement
 *  org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.ThrowStatement
 *  org.eclipse.jdt.internal.compiler.ast.TrueLiteral
 *  org.eclipse.jdt.internal.compiler.ast.TryStatement
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.ast.UnaryExpression
 *  org.eclipse.jdt.internal.compiler.ast.WhileStatement
 *  org.eclipse.jdt.internal.compiler.ast.Wildcard
 *  org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding
 */
package lombok.eclipse.handlers.ast;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
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
import lombok.ast.InstanceOf;
import lombok.ast.JavaDoc;
import lombok.ast.LocalDecl;
import lombok.ast.MethodDecl;
import lombok.ast.Modifier;
import lombok.ast.NameRef;
import lombok.ast.New;
import lombok.ast.NewArray;
import lombok.ast.Node;
import lombok.ast.NumberLiteral;
import lombok.ast.Return;
import lombok.ast.ReturnDefault;
import lombok.ast.Statement;
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
import lombok.core.util.Each;
import lombok.core.util.Is;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.Eclipse;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public final class EclipseASTMaker
implements ASTVisitor<ASTNode, Void> {
    private static final Map<String, Integer> UNARY_OPERATORS = new HashMap<String, Integer>();
    private static final Map<String, Integer> BINARY_OPERATORS;
    private final EclipseNode sourceNode;
    private final ASTNode source;

    public <T extends ASTNode> T build(Node<?> node) {
        return this.build(node, null);
    }

    public <T extends ASTNode> T build(Node<?> node, Class<T> extectedType) {
        if (node == null) {
            return null;
        }
        return (T)((ASTNode)Cast.uncheckedCast(node.accept(this, null)));
    }

    public <T extends ASTNode> List<T> build(List<? extends Node<?>> nodes) {
        return this.build(nodes, null);
    }

    public <T extends ASTNode> List<T> build(List<? extends Node<?>> nodes, Class<T> extectedType) {
        if (nodes == null) {
            return null;
        }
        ArrayList<T> list = new ArrayList<T>();
        for (Node node : nodes) {
            list.add(this.build(node, extectedType));
        }
        return list;
    }

    private ASTNode posHintOf(Node<?> node) {
        ASTNode posHint = (ASTNode)node.posHint();
        return posHint == null ? this.source : posHint;
    }

    private int modifiersFor(Set<Modifier> modifiers) {
        int mods = 0;
        mods |= modifiers.contains((Object)Modifier.FINAL) ? 16 : 0;
        mods |= modifiers.contains((Object)Modifier.PRIVATE) ? 2 : 0;
        mods |= modifiers.contains((Object)Modifier.PROTECTED) ? 4 : 0;
        mods |= modifiers.contains((Object)Modifier.PUBLIC) ? 1 : 0;
        mods |= modifiers.contains((Object)Modifier.STATIC) ? 8 : 0;
        mods |= modifiers.contains((Object)Modifier.TRANSIENT) ? 128 : 0;
        return mods |= modifiers.contains((Object)Modifier.VOLATILE) ? 64 : 0;
    }

    private org.eclipse.jdt.internal.compiler.ast.Statement getEmptyStatement(Node<?> node) {
        EmptyStatement emptyStatement = new EmptyStatement(0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)emptyStatement, this.source, this.posHintOf(node));
        return emptyStatement;
    }

    private static <ELEMENT_TYPE> ELEMENT_TYPE[] toArray(List<?> list, ELEMENT_TYPE[] array) {
        if (list != null && !list.isEmpty()) {
            return list.toArray(array);
        }
        return null;
    }

    @Override
    public ASTNode visitAnnotation(Annotation node, Void p) {
        MarkerAnnotation ann;
        if (node.getValues().isEmpty()) {
            ann = new MarkerAnnotation((TypeReference)this.build(node.getType(), TypeReference.class), 0);
        } else if (node.getValues().containsKey("value") && node.getValues().size() == 1) {
            ann = new SingleMemberAnnotation((TypeReference)this.build(node.getType(), TypeReference.class), 0);
            ((SingleMemberAnnotation)ann).memberValue = (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getValues().get("value"));
        } else {
            ann = new NormalAnnotation((TypeReference)this.build(node.getType(), TypeReference.class), 0);
            ArrayList<MemberValuePair> valuePairs = new ArrayList<MemberValuePair>();
            for (Map.Entry entry : node.getValues().entrySet()) {
                MemberValuePair valuePair = new MemberValuePair(entry.getKey().toCharArray(), 0, 0, (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(entry.getValue(), org.eclipse.jdt.internal.compiler.ast.Expression.class));
                Eclipse.setGeneratedByAndCopyPos((ASTNode)valuePair, this.source, this.posHintOf(node));
                valuePairs.add(valuePair);
            }
            ((NormalAnnotation)ann).memberValuePairs = valuePairs.toArray((T[])new MemberValuePair[0]);
        }
        Eclipse.setGeneratedByAndCopyPos((ASTNode)ann, this.source, this.posHintOf(node));
        return ann;
    }

    @Override
    public ASTNode visitArgument(Argument node, Void p) {
        org.eclipse.jdt.internal.compiler.ast.Argument argument = new org.eclipse.jdt.internal.compiler.ast.Argument(node.getName().toCharArray(), 0, null, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)argument, this.source, this.posHintOf(node));
        argument.modifiers = this.modifiersFor(node.getModifiers());
        argument.annotations = EclipseASTMaker.toArray(this.build(node.getAnnotations()), new org.eclipse.jdt.internal.compiler.ast.Annotation[0]);
        argument.bits |= 8388608;
        argument.type = (TypeReference)this.build(node.getType());
        return argument;
    }

    @Override
    public ASTNode visitArrayRef(ArrayRef node, Void p) {
        ArrayReference arrayReference = new ArrayReference((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getIndexed(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getIndex(), org.eclipse.jdt.internal.compiler.ast.Expression.class));
        Eclipse.setGeneratedByAndCopyPos((ASTNode)arrayReference, this.source, this.posHintOf(node));
        return arrayReference;
    }

    @Override
    public ASTNode visitAssignment(Assignment node, Void p) {
        org.eclipse.jdt.internal.compiler.ast.Assignment assignment = new org.eclipse.jdt.internal.compiler.ast.Assignment((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getLeft(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getRight(), org.eclipse.jdt.internal.compiler.ast.Expression.class), 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)assignment, this.source, this.posHintOf(node));
        return assignment;
    }

    @Override
    public ASTNode visitBinary(Binary node, Void p) {
        String operator = node.getOperator();
        if (!BINARY_OPERATORS.containsKey(operator)) {
            throw new IllegalStateException(String.format("Unknown binary operator '%s'", operator));
        }
        int opCode = BINARY_OPERATORS.get(operator);
        OR_OR_Expression binaryExpression = "||".equals(operator) ? new OR_OR_Expression((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getLeft(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getRight(), org.eclipse.jdt.internal.compiler.ast.Expression.class), opCode) : ("&&".equals(operator) ? new AND_AND_Expression((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getLeft(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getRight(), org.eclipse.jdt.internal.compiler.ast.Expression.class), opCode) : (Is.oneOf(operator, "==", "!=") ? new EqualExpression((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getLeft(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getRight(), org.eclipse.jdt.internal.compiler.ast.Expression.class), opCode) : new BinaryExpression((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getLeft(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getRight(), org.eclipse.jdt.internal.compiler.ast.Expression.class), opCode)));
        Eclipse.setGeneratedByAndCopyPos((ASTNode)binaryExpression, this.source, this.posHintOf(node));
        return binaryExpression;
    }

    @Override
    public ASTNode visitBlock(Block node, Void p) {
        org.eclipse.jdt.internal.compiler.ast.Block block = new org.eclipse.jdt.internal.compiler.ast.Block(0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)block, this.source, this.posHintOf(node));
        block.statements = EclipseASTMaker.toArray(this.build(node.getStatements()), new org.eclipse.jdt.internal.compiler.ast.Statement[0]);
        return block;
    }

    @Override
    public ASTNode visitBooleanLiteral(BooleanLiteral node, Void p) {
        TrueLiteral literal = node.isTrue() ? new TrueLiteral(0, 0) : new FalseLiteral(0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)literal, this.source, this.posHintOf(node));
        return literal;
    }

    @Override
    public ASTNode visitBreak(Break node, Void p) {
        BreakStatement breakStatement = new BreakStatement((char[])(node.getLabel() == null ? null : (Object)node.getLabel().toCharArray()), 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)breakStatement, this.source, this.posHintOf(node));
        return breakStatement;
    }

    @Override
    public ASTNode visitCall(Call node, Void p) {
        MessageSend messageSend = new MessageSend();
        Eclipse.setGeneratedByAndCopyPos((ASTNode)messageSend, this.source, this.posHintOf(node));
        messageSend.receiver = node.getReceiver() == null ? (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(AST.This().implicit()) : (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getReceiver());
        messageSend.selector = node.getName().toCharArray();
        messageSend.typeArguments = EclipseASTMaker.toArray(this.build(node.getTypeArgs()), new TypeReference[0]);
        messageSend.arguments = EclipseASTMaker.toArray(this.build(node.getArgs()), new org.eclipse.jdt.internal.compiler.ast.Expression[0]);
        return messageSend;
    }

    @Override
    public ASTNode visitCast(lombok.ast.Cast node, Void p) {
        CastExpression castExpression = this.createCastExpression((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getExpression(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getType(), TypeReference.class));
        Eclipse.setGeneratedByAndCopyPos((ASTNode)castExpression, this.source, this.posHintOf(node));
        return castExpression;
    }

    private CastExpression createCastExpression(org.eclipse.jdt.internal.compiler.ast.Expression expression, org.eclipse.jdt.internal.compiler.ast.Expression typeRef) {
        try {
            return Reflection.castExpressionConstructor.newInstance(new Object[]{expression, typeRef});
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ASTNode visitCase(Case node, Void p) {
        throw new IllegalStateException("");
    }

    @Override
    public ASTNode visitCharLiteral(CharLiteral node, Void p) {
        org.eclipse.jdt.internal.compiler.ast.CharLiteral literal = new org.eclipse.jdt.internal.compiler.ast.CharLiteral(node.getCharacter().toCharArray(), 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)literal, this.source, this.posHintOf(node));
        return literal;
    }

    @Override
    public ASTNode visitClassDecl(ClassDecl node, Void p) {
        TypeDeclaration typeDeclaration = new TypeDeclaration(((CompilationUnitDeclaration)((EclipseNode)this.sourceNode.top()).get()).compilationResult);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)typeDeclaration, this.source, this.posHintOf(node));
        typeDeclaration.modifiers = this.modifiersFor(node.getModifiers());
        if (node.isInterface()) {
            typeDeclaration.modifiers |= 512;
        }
        typeDeclaration.bits |= 8388608;
        if (node.isLocal()) {
            typeDeclaration.bits |= 256;
        }
        if (node.isAnonymous()) {
            typeDeclaration.bits |= 512;
        }
        typeDeclaration.name = Is.empty(node.getName()) ? CharOperation.NO_CHAR : node.getName().toCharArray();
        typeDeclaration.annotations = EclipseASTMaker.toArray(this.build(node.getAnnotations()), new org.eclipse.jdt.internal.compiler.ast.Annotation[0]);
        typeDeclaration.typeParameters = EclipseASTMaker.toArray(this.build(node.getTypeParameters()), new TypeParameter[0]);
        typeDeclaration.fields = EclipseASTMaker.toArray(this.build(node.getFields()), new FieldDeclaration[0]);
        typeDeclaration.methods = EclipseASTMaker.toArray(this.build(node.getMethods()), new AbstractMethodDeclaration[0]);
        typeDeclaration.memberTypes = EclipseASTMaker.toArray(this.build(node.getMemberTypes()), new TypeDeclaration[0]);
        typeDeclaration.superInterfaces = EclipseASTMaker.toArray(this.build(node.getSuperInterfaces()), new TypeReference[0]);
        typeDeclaration.superclass = (TypeReference)this.build(node.getSuperclass());
        for (FieldDeclaration field : Each.elementIn(typeDeclaration.fields)) {
            if (!EclipseASTMaker.isEnumConstant(field) && (field.modifiers & 8) == 0) continue;
            typeDeclaration.addClinit();
            break;
        }
        return typeDeclaration;
    }

    private static boolean isEnumConstant(FieldDeclaration field) {
        return field.initialization instanceof AllocationExpression && ((AllocationExpression)field.initialization).enumConstant == field;
    }

    @Override
    public ASTNode visitConstructorDecl(ConstructorDecl node, Void p) {
        ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration(((CompilationUnitDeclaration)((EclipseNode)this.sourceNode.top()).get()).compilationResult);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)constructorDeclaration, this.source, this.posHintOf(node));
        constructorDeclaration.modifiers = this.modifiersFor(node.getModifiers());
        constructorDeclaration.annotations = EclipseASTMaker.toArray(this.build(node.getAnnotations()), new org.eclipse.jdt.internal.compiler.ast.Annotation[0]);
        if (node.implicitSuper()) {
            constructorDeclaration.constructorCall = new ExplicitConstructorCall(1);
        }
        constructorDeclaration.selector = node.getName().toCharArray();
        constructorDeclaration.thrownExceptions = EclipseASTMaker.toArray(this.build(node.getThrownExceptions()), new TypeReference[0]);
        constructorDeclaration.typeParameters = EclipseASTMaker.toArray(this.build(node.getTypeParameters()), new TypeParameter[0]);
        constructorDeclaration.bits |= 8388608;
        constructorDeclaration.arguments = EclipseASTMaker.toArray(this.build(node.getArguments()), new org.eclipse.jdt.internal.compiler.ast.Argument[0]);
        if (!node.getStatements().isEmpty()) {
            constructorDeclaration.statements = EclipseASTMaker.toArray(this.build(node.getStatements()), new org.eclipse.jdt.internal.compiler.ast.Statement[0]);
        }
        constructorDeclaration.javadoc = (Javadoc)this.build(node.getJavaDoc());
        return constructorDeclaration;
    }

    @Override
    public ASTNode visitContinue(Continue node, Void p) {
        ContinueStatement continueStatement = new ContinueStatement((char[])(node.getLabel() == null ? null : (Object)node.getLabel().toCharArray()), 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)continueStatement, this.source, this.posHintOf(node));
        return continueStatement;
    }

    @Override
    public ASTNode visitDoWhile(DoWhile node, Void p) {
        DoStatement doStatement = new DoStatement((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getCondition(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Statement)this.build(node.getAction(), org.eclipse.jdt.internal.compiler.ast.Statement.class), 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)doStatement, this.source, this.posHintOf(node));
        return doStatement;
    }

    @Override
    public ASTNode visitDefaultValue(DefaultValue node, Void p) {
        Expression returnValue = AST.Null();
        TypeReference type = (TypeReference)this.build(node.getType());
        if (type instanceof SingleTypeReference) {
            String name = As.string(type.getLastToken());
            if ("int".equals(name)) {
                returnValue = AST.Number(0);
            } else if ("byte".equals(name)) {
                returnValue = AST.Number(0);
            } else if ("short".equals(name)) {
                returnValue = AST.Number(0);
            } else if ("char".equals(name)) {
                returnValue = AST.Char("");
            } else if ("long".equals(name)) {
                returnValue = AST.Number(0);
            } else if ("float".equals(name)) {
                returnValue = AST.Number(Float.valueOf(0.0f));
            } else if ("double".equals(name)) {
                returnValue = AST.Number(0.0);
            } else if ("boolean".equals(name)) {
                returnValue = AST.False();
            } else if ("void".equals(name)) {
                returnValue = null;
            }
        }
        return this.build(returnValue);
    }

    @Override
    public ASTNode visitEnumConstant(EnumConstant node, Void p) {
        AllocationExpression allocationExpression = new AllocationExpression();
        Eclipse.setGeneratedByAndCopyPos((ASTNode)allocationExpression, this.source, this.posHintOf(node));
        allocationExpression.arguments = EclipseASTMaker.toArray(this.build(node.getArgs()), new org.eclipse.jdt.internal.compiler.ast.Expression[0]);
        allocationExpression.enumConstant = new FieldDeclaration(node.getName().toCharArray(), 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)allocationExpression.enumConstant, this.source, this.posHintOf(node));
        allocationExpression.enumConstant.initialization = allocationExpression;
        allocationExpression.enumConstant.javadoc = (Javadoc)this.build(node.getJavaDoc());
        return allocationExpression.enumConstant;
    }

    @Override
    public ASTNode visitFieldDecl(FieldDecl node, Void p) {
        FieldDeclaration fieldDeclaration = new FieldDeclaration(node.getName().toCharArray(), 0, 0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldDeclaration, this.source);
        fieldDeclaration.modifiers = this.modifiersFor(node.getModifiers());
        fieldDeclaration.annotations = EclipseASTMaker.toArray(this.build(node.getAnnotations()), new org.eclipse.jdt.internal.compiler.ast.Annotation[0]);
        fieldDeclaration.bits |= 8388608;
        fieldDeclaration.type = (TypeReference)this.build(node.getType());
        if (node.getInitialization() != null) {
            fieldDeclaration.initialization = (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getInitialization());
        }
        fieldDeclaration.javadoc = (Javadoc)this.build(node.getJavaDoc());
        return fieldDeclaration;
    }

    @Override
    public ASTNode visitFieldRef(FieldRef node, Void p) {
        FieldReference fieldRef = new FieldReference(node.getName().toCharArray(), 0);
        fieldRef.receiver = (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getReceiver());
        Eclipse.setGeneratedByAndCopyPos((ASTNode)fieldRef, this.source, this.posHintOf(node));
        return fieldRef;
    }

    @Override
    public ASTNode visitForeach(Foreach node, Void p) {
        ForeachStatement forEach = new ForeachStatement((LocalDeclaration)this.build(node.getElementVariable(), LocalDeclaration.class), 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)forEach, this.source, this.posHintOf(node));
        forEach.collection = (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getCollection());
        forEach.action = (org.eclipse.jdt.internal.compiler.ast.Statement)this.build(node.getAction());
        return forEach;
    }

    @Override
    public ASTNode visitIf(If node, Void p) {
        org.eclipse.jdt.internal.compiler.ast.Statement thenStatement = node.getThenStatement() == null ? this.getEmptyStatement(node) : (org.eclipse.jdt.internal.compiler.ast.Statement)this.build(node.getThenStatement(), org.eclipse.jdt.internal.compiler.ast.Statement.class);
        IfStatement ifStatement = new IfStatement((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getCondition(), org.eclipse.jdt.internal.compiler.ast.Expression.class), thenStatement, 0, 0);
        if (node.getElseStatement() != null) {
            ifStatement.elseStatement = (org.eclipse.jdt.internal.compiler.ast.Statement)this.build(node.getElseStatement());
        }
        Eclipse.setGeneratedByAndCopyPos((ASTNode)ifStatement, this.source, this.posHintOf(node));
        return ifStatement;
    }

    @Override
    public ASTNode visitInitializer(lombok.ast.Initializer node, Void p) {
        org.eclipse.jdt.internal.compiler.ast.Block block = new org.eclipse.jdt.internal.compiler.ast.Block(0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)block, this.source, this.posHintOf(node));
        block.statements = EclipseASTMaker.toArray(this.build(node.getStatements()), new org.eclipse.jdt.internal.compiler.ast.Statement[0]);
        Initializer initializer = new Initializer(block, this.modifiersFor(node.getModifiers()));
        initializer.bits |= 8388608;
        Eclipse.setGeneratedByAndCopyPos((ASTNode)initializer, this.source, this.posHintOf(node));
        return initializer;
    }

    @Override
    public ASTNode visitInstanceOf(InstanceOf node, Void p) {
        InstanceOfExpression instanceOfExpression = new InstanceOfExpression((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getExpression(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (TypeReference)this.build(node.getType(), TypeReference.class));
        Eclipse.setGeneratedByAndCopyPos((ASTNode)instanceOfExpression, this.source, this.posHintOf(node));
        return instanceOfExpression;
    }

    @Override
    public ASTNode visitJavaDoc(JavaDoc node, Void p) {
        Javadoc javadoc = new Javadoc(0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)javadoc, this.source, this.posHintOf(node));
        ArrayList<JavadocSingleNameReference> argumentReferences = new ArrayList<JavadocSingleNameReference>();
        for (Map.Entry<String, String> argumentReference : node.getArgumentReferences().entrySet()) {
            JavadocSingleNameReference ref = new JavadocSingleNameReference(argumentReference.getKey().toCharArray(), 0, 0, 0);
            Eclipse.setGeneratedByAndCopyPos((ASTNode)ref, this.source, this.posHintOf(node));
            argumentReferences.add(ref);
        }
        javadoc.paramReferences = EclipseASTMaker.toArray(argumentReferences, new JavadocSingleNameReference[0]);
        ArrayList<JavadocSingleTypeReference> paramTypeReferences = new ArrayList<JavadocSingleTypeReference>();
        for (Map.Entry<String, String> paramTypeReference : node.getParamTypeReferences().entrySet()) {
            JavadocSingleTypeReference ref = new JavadocSingleTypeReference(paramTypeReference.getKey().toCharArray(), 0, 0, 0);
            Eclipse.setGeneratedByAndCopyPos((ASTNode)ref, this.source, this.posHintOf(node));
            paramTypeReferences.add(ref);
        }
        javadoc.paramTypeParameters = EclipseASTMaker.toArray(paramTypeReferences, new JavadocSingleTypeReference[0]);
        ArrayList<TypeReference> exceptionReferences = new ArrayList<TypeReference>();
        for (Map.Entry<TypeRef, String> exceptionReference : node.getExceptionReferences().entrySet()) {
            TypeReference ref = (TypeReference)this.build(exceptionReference.getKey());
            Eclipse.setGeneratedByAndCopyPos((ASTNode)ref, this.source, this.posHintOf(node));
            exceptionReferences.add(ref);
        }
        javadoc.exceptionReferences = EclipseASTMaker.toArray(exceptionReferences, new TypeReference[0]);
        if (node.getReturnMessage() != null) {
            javadoc.returnStatement = new JavadocReturnStatement(0, 0);
        }
        return javadoc;
    }

    @Override
    public ASTNode visitLocalDecl(LocalDecl node, Void p) {
        LocalDeclaration localDeclaration = new LocalDeclaration(node.getName().toCharArray(), 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)localDeclaration, this.source, this.posHintOf(node));
        localDeclaration.modifiers = this.modifiersFor(node.getModifiers());
        localDeclaration.annotations = EclipseASTMaker.toArray(this.build(node.getAnnotations()), new org.eclipse.jdt.internal.compiler.ast.Annotation[0]);
        localDeclaration.bits |= 8388608;
        localDeclaration.type = (TypeReference)this.build(node.getType());
        if (node.getInitialization() != null) {
            localDeclaration.initialization = (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getInitialization());
        }
        return localDeclaration;
    }

    @Override
    public ASTNode visitMethodDecl(MethodDecl node, Void p) {
        MethodDeclaration methodDeclaration = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)this.sourceNode.top()).get()).compilationResult);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)methodDeclaration, this.source, this.posHintOf(node));
        methodDeclaration.modifiers = this.modifiersFor(node.getModifiers());
        methodDeclaration.returnType = (TypeReference)this.build(node.getReturnType(), TypeReference.class);
        methodDeclaration.annotations = EclipseASTMaker.toArray(this.build(node.getAnnotations()), new org.eclipse.jdt.internal.compiler.ast.Annotation[0]);
        methodDeclaration.selector = node.getName().toCharArray();
        methodDeclaration.thrownExceptions = EclipseASTMaker.toArray(this.build(node.getThrownExceptions()), new TypeReference[0]);
        methodDeclaration.typeParameters = EclipseASTMaker.toArray(this.build(node.getTypeParameters()), new TypeParameter[0]);
        methodDeclaration.bits |= 8388608;
        methodDeclaration.arguments = EclipseASTMaker.toArray(this.build(node.getArguments()), new org.eclipse.jdt.internal.compiler.ast.Argument[0]);
        if (node.isImplementing()) {
            methodDeclaration.modifiers |= 536870912;
        }
        if (node.noBody() || (methodDeclaration.modifiers & 1024) != 0) {
            methodDeclaration.modifiers |= 16777216;
        } else {
            methodDeclaration.statements = EclipseASTMaker.toArray(this.build(node.getStatements()), new org.eclipse.jdt.internal.compiler.ast.Statement[0]);
        }
        methodDeclaration.javadoc = (Javadoc)this.build(node.getJavaDoc());
        return methodDeclaration;
    }

    @Override
    public ASTNode visitNameRef(NameRef node, Void p) {
        SingleNameReference nameReference;
        if (node.getName().contains(".")) {
            char[][] nameTokens = lombok.eclipse.Eclipse.fromQualifiedName(node.getName());
            nameReference = new QualifiedNameReference(nameTokens, lombok.eclipse.Eclipse.poss(this.posHintOf(node), nameTokens.length), 0, 0);
        } else {
            nameReference = new SingleNameReference(node.getName().toCharArray(), 0);
        }
        Eclipse.setGeneratedByAndCopyPos((ASTNode)nameReference, this.source, this.posHintOf(node));
        return nameReference;
    }

    @Override
    public ASTNode visitNew(New node, Void p) {
        AllocationExpression allocationExpression = node.getAnonymousType() != null ? new QualifiedAllocationExpression((TypeDeclaration)this.build(node.getAnonymousType(), TypeDeclaration.class)) : new AllocationExpression();
        Eclipse.setGeneratedByAndCopyPos((ASTNode)allocationExpression, this.source, this.posHintOf(node));
        allocationExpression.bits |= 8388608;
        allocationExpression.type = (TypeReference)this.build(node.getType());
        allocationExpression.typeArguments = EclipseASTMaker.toArray(this.build(node.getTypeArgs()), new TypeReference[0]);
        allocationExpression.arguments = EclipseASTMaker.toArray(this.build(node.getArgs()), new org.eclipse.jdt.internal.compiler.ast.Expression[0]);
        return allocationExpression;
    }

    @Override
    public ASTNode visitNewArray(NewArray node, Void p) {
        ArrayAllocationExpression allocationExpression = new ArrayAllocationExpression();
        Eclipse.setGeneratedByAndCopyPos((ASTNode)allocationExpression, this.source, this.posHintOf(node));
        allocationExpression.bits |= 8388608;
        allocationExpression.type = (TypeReference)this.build(node.getType());
        ArrayList<T> dims = new ArrayList<T>();
        dims.addAll(this.build(node.getDimensionExpressions(), org.eclipse.jdt.internal.compiler.ast.Expression.class));
        allocationExpression.dimensions = EclipseASTMaker.toArray(dims, new org.eclipse.jdt.internal.compiler.ast.Expression[0]);
        List<T> initializerExpressions = this.build(node.getInitializerExpressions(), org.eclipse.jdt.internal.compiler.ast.Expression.class);
        if (!initializerExpressions.isEmpty()) {
            ArrayInitializer initializer = new ArrayInitializer();
            Eclipse.setGeneratedByAndCopyPos((ASTNode)initializer, this.source, this.posHintOf(node));
            initializer.bits |= 8388608;
            initializer.expressions = initializerExpressions.isEmpty() ? null : EclipseASTMaker.toArray(initializerExpressions, new org.eclipse.jdt.internal.compiler.ast.Expression[0]);
            allocationExpression.initializer = initializer;
        }
        return allocationExpression;
    }

    @Override
    public ASTNode visitNullLiteral(lombok.ast.NullLiteral node, Void p) {
        NullLiteral literal = new NullLiteral(0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)literal, this.source, this.posHintOf(node));
        return literal;
    }

    @Override
    public ASTNode visitNumberLiteral(NumberLiteral node, Void p) {
        Number number = node.getNumber();
        IntLiteral literal = number instanceof Integer ? this.createIntLiteral(Integer.toString(number.intValue()).toCharArray()) : (number instanceof Long ? this.createLongLiteral((Long.toString(number.longValue()) + "L").toCharArray()) : (number instanceof Float ? new FloatLiteral((Float.toString(number.floatValue()) + "f").toCharArray(), 0, 0) : new DoubleLiteral((Double.toString(number.doubleValue()) + "d").toCharArray(), 0, 0)));
        Eclipse.setGeneratedByAndCopyPos((ASTNode)literal, this.source, this.posHintOf(node));
        return literal;
    }

    private IntLiteral createIntLiteral(char[] token) {
        IntLiteral result;
        try {
            result = Reflection.intLiteralConstructor != null ? Reflection.intLiteralConstructor.newInstance(token, 0, 0) : (IntLiteral)Reflection.intLiteralFactoryMethod.invoke(null, token, 0, 0);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    private LongLiteral createLongLiteral(char[] token) {
        LongLiteral result;
        try {
            result = Reflection.longLiteralConstructor != null ? Reflection.longLiteralConstructor.newInstance(token, 0, 0) : (LongLiteral)Reflection.longLiteralFactoryMethod.invoke(null, token, 0, 0);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    @Override
    public ASTNode visitReturn(Return node, Void p) {
        ReturnStatement returnStatement = new ReturnStatement(node.getExpression() == null ? null : (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getExpression(), org.eclipse.jdt.internal.compiler.ast.Expression.class), 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)returnStatement, this.source, this.posHintOf(node));
        return returnStatement;
    }

    @Override
    public ASTNode visitReturnDefault(ReturnDefault node, Void p) {
        TypeRef returnType = ((MethodDecl)node.upTo(MethodDecl.class)).getReturnType();
        if (returnType == null) {
            returnType = AST.Type(Eclipse.methodNodeOf(this.sourceNode).getName());
        }
        return this.build(AST.Return(AST.DefaultValue(returnType)));
    }

    @Override
    public ASTNode visitStringLiteral(lombok.ast.StringLiteral node, Void p) {
        StringLiteral stringLiteral = new StringLiteral(node.getString().toCharArray(), 0, 0, 1);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)stringLiteral, this.source, this.posHintOf(node));
        return stringLiteral;
    }

    @Override
    public ASTNode visitSwitch(Switch node, Void p) {
        SwitchStatement switchStatement = new SwitchStatement();
        switchStatement.expression = (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getExpression());
        ArrayList<Object> caseStatements = new ArrayList<Object>();
        for (Case caze : node.getCases()) {
            CaseStatement caseStatement = new CaseStatement(caze.getPattern() == null ? null : (org.eclipse.jdt.internal.compiler.ast.Expression)this.build(caze.getPattern(), org.eclipse.jdt.internal.compiler.ast.Expression.class), 0, 0);
            Eclipse.setGeneratedByAndCopyPos((ASTNode)caseStatement, this.source, this.posHintOf(node));
            caseStatements.add((Object)caseStatement);
            caseStatements.addAll(this.build(caze.getStatements(), org.eclipse.jdt.internal.compiler.ast.Statement.class));
        }
        switchStatement.statements = caseStatements.toArray((T[])new org.eclipse.jdt.internal.compiler.ast.Statement[caseStatements.size()]);
        return switchStatement;
    }

    @Override
    public ASTNode visitSynchronized(Synchronized node, Void p) {
        org.eclipse.jdt.internal.compiler.ast.Block block = new org.eclipse.jdt.internal.compiler.ast.Block(0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)block, this.source, this.posHintOf(node));
        block.statements = EclipseASTMaker.toArray(this.build(node.getStatements()), new org.eclipse.jdt.internal.compiler.ast.Statement[0]);
        SynchronizedStatement synchronizedStatemenet = new SynchronizedStatement((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getLock(), org.eclipse.jdt.internal.compiler.ast.Expression.class), block, 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)synchronizedStatemenet, this.source, this.posHintOf(node));
        return synchronizedStatemenet;
    }

    @Override
    public ASTNode visitThis(This node, Void p) {
        ThisReference thisReference;
        if (node.getType() != null) {
            thisReference = new QualifiedThisReference((TypeReference)this.build(node.getType(), TypeReference.class), 0, 0);
        } else {
            thisReference = new ThisReference(0, 0);
            if (node.isImplicit()) {
                thisReference.bits |= 4;
            }
        }
        Eclipse.setGeneratedByAndCopyPos((ASTNode)thisReference, this.source, this.posHintOf(node));
        return thisReference;
    }

    @Override
    public ASTNode visitThrow(Throw node, Void p) {
        ThrowStatement throwStatement = new ThrowStatement((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getExpression(), org.eclipse.jdt.internal.compiler.ast.Expression.class), 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)throwStatement, this.source, this.posHintOf(node));
        return throwStatement;
    }

    @Override
    public ASTNode visitTry(Try node, Void p) {
        TryStatement tryStatement = new TryStatement();
        Eclipse.setGeneratedByAndCopyPos((ASTNode)tryStatement, this.source, this.posHintOf(node));
        tryStatement.tryBlock = (org.eclipse.jdt.internal.compiler.ast.Block)this.build(node.getTryBlock());
        tryStatement.catchArguments = EclipseASTMaker.toArray(this.build(node.getCatchArguments()), new org.eclipse.jdt.internal.compiler.ast.Argument[0]);
        tryStatement.catchBlocks = EclipseASTMaker.toArray(this.build(node.getCatchBlocks()), new org.eclipse.jdt.internal.compiler.ast.Block[0]);
        if (node.getFinallyBlock() != null) {
            tryStatement.finallyBlock = (org.eclipse.jdt.internal.compiler.ast.Block)this.build(node.getFinallyBlock());
        }
        return tryStatement;
    }

    @Override
    public ASTNode visitTypeParam(TypeParam node, Void p) {
        TypeParameter typeParameter = new TypeParameter();
        typeParameter.name = node.getName().toCharArray();
        ArrayList<TypeRef> bounds = new ArrayList<TypeRef>(node.getBounds());
        if (!bounds.isEmpty()) {
            typeParameter.type = (TypeReference)this.build(bounds.get(0));
            bounds.remove(0);
            typeParameter.bounds = EclipseASTMaker.toArray(this.build(bounds), new TypeReference[0]);
        }
        Eclipse.setGeneratedByAndCopyPos((ASTNode)typeParameter, this.source, this.posHintOf(node));
        return typeParameter;
    }

    @Override
    public ASTNode visitTypeRef(TypeRef node, Void p) {
        SingleTypeReference typeReference;
        Object[] paramTypes = this.build(node.getTypeArgs()).toArray((T[])new TypeReference[0]);
        if (node.getTypeName().equals("void")) {
            typeReference = new SingleTypeReference(TypeBinding.VOID.simpleName, 0);
        } else if (node.getTypeName().contains(".")) {
            char[][] typeNameTokens = lombok.eclipse.Eclipse.fromQualifiedName(node.getTypeName());
            long[] poss = new long[typeNameTokens.length];
            Arrays.fill(poss, 0);
            if (Is.notEmpty(paramTypes)) {
                TypeReference[][] typeArguments = new TypeReference[typeNameTokens.length][];
                typeArguments[typeNameTokens.length - 1] = paramTypes;
                typeReference = new ParameterizedQualifiedTypeReference(typeNameTokens, (TypeReference[][])typeArguments, 0, poss);
            } else {
                typeReference = node.getDims() > 0 ? new ArrayQualifiedTypeReference(typeNameTokens, node.getDims(), poss) : new QualifiedTypeReference(typeNameTokens, poss);
            }
        } else {
            char[] typeNameToken = node.getTypeName().toCharArray();
            typeReference = Is.notEmpty(paramTypes) ? new ParameterizedSingleTypeReference(typeNameToken, (TypeReference[])paramTypes, 0, 0) : (node.getDims() > 0 ? new ArrayTypeReference(typeNameToken, node.getDims(), 0) : new SingleTypeReference(typeNameToken, 0));
        }
        Eclipse.setGeneratedByAndCopyPos((ASTNode)typeReference, this.source, this.posHintOf(node));
        if (node.isSuperType()) {
            typeReference.bits |= 16;
        }
        return typeReference;
    }

    @Override
    public ASTNode visitUnary(Unary node, Void p) {
        String operator = node.getOperator();
        if (!UNARY_OPERATORS.containsKey(operator)) {
            throw new IllegalStateException(String.format("Unknown unary operator '%s'", operator));
        }
        int opCode = UNARY_OPERATORS.get(operator);
        PrefixExpression unaryExpression = Is.oneOf(operator, "++X", "--X") ? new PrefixExpression((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getExpression(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Expression)IntLiteral.One, opCode, 0) : (Is.oneOf(operator, "X++", "X--") ? new PostfixExpression((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getExpression(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Expression)IntLiteral.One, opCode, 0) : new UnaryExpression((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getExpression(), org.eclipse.jdt.internal.compiler.ast.Expression.class), opCode));
        Eclipse.setGeneratedByAndCopyPos((ASTNode)unaryExpression, this.source, this.posHintOf(node));
        return unaryExpression;
    }

    @Override
    public ASTNode visitWhile(While node, Void p) {
        WhileStatement whileStatement = new WhileStatement((org.eclipse.jdt.internal.compiler.ast.Expression)this.build(node.getCondition(), org.eclipse.jdt.internal.compiler.ast.Expression.class), (org.eclipse.jdt.internal.compiler.ast.Statement)this.build(node.getAction(), org.eclipse.jdt.internal.compiler.ast.Statement.class), 0, 0);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)whileStatement, this.source, this.posHintOf(node));
        return whileStatement;
    }

    @Override
    public ASTNode visitWildcard(lombok.ast.Wildcard node, Void p) {
        int kind = 0;
        if (node.getBound() != null) {
            switch (node.getBound()) {
                case SUPER: {
                    kind = 2;
                    break;
                }
                default: {
                    kind = 1;
                }
            }
        }
        Wildcard wildcard = new Wildcard(kind);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)wildcard, this.source, this.posHintOf(node));
        wildcard.bound = (TypeReference)this.build(node.getType());
        return wildcard;
    }

    @Override
    public ASTNode visitWrappedExpression(WrappedExpression node, Void p) {
        org.eclipse.jdt.internal.compiler.ast.Expression expression = (org.eclipse.jdt.internal.compiler.ast.Expression)node.getWrappedObject();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)expression, this.source);
        return expression;
    }

    @Override
    public ASTNode visitWrappedMethodDecl(WrappedMethodDecl node, Void p) {
        MethodDeclaration methodDeclaration = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)this.sourceNode.top()).get()).compilationResult);
        Eclipse.setGeneratedByAndCopyPos((ASTNode)methodDeclaration, this.source, this.posHintOf(node));
        MethodBinding abstractMethod = (MethodBinding)node.getWrappedObject();
        if (node.getReturnType() == null) {
            node.withReturnType(AST.Type((Object)abstractMethod.returnType));
        }
        if (node.getThrownExceptions().isEmpty()) {
            for (ReferenceBinding thrownException : Each.elementIn(abstractMethod.thrownExceptions)) {
                node.withThrownException(AST.Type((Object)thrownException));
            }
        }
        if (node.getArguments().isEmpty() && Is.notEmpty(abstractMethod.parameters)) {
            for (int i = 0; i < abstractMethod.parameters.length; ++i) {
                node.withArgument(AST.Arg(AST.Type((Object)abstractMethod.parameters[i]), "arg" + i));
            }
        }
        if (node.getTypeParameters().isEmpty()) {
            for (TypeVariableBinding binding : Each.elementIn(abstractMethod.typeVariables)) {
                ReferenceBinding super1 = binding.superclass;
                ReferenceBinding[] super2 = binding.superInterfaces;
                TypeParam typeParameter = AST.TypeParam(As.string(binding.sourceName));
                if (super2 == null) {
                    super2 = new ReferenceBinding[]{};
                }
                if (super1 != null || super2.length > 0) {
                    if (super1 != null) {
                        typeParameter.withBound(AST.Type((Object)super1));
                    }
                    for (ReferenceBinding bound : super2) {
                        typeParameter.withBound(AST.Type((Object)bound).makeSuperType());
                    }
                }
                node.withTypeParameter(typeParameter);
            }
        }
        methodDeclaration.modifiers = abstractMethod.getAccessFlags() & -1025;
        methodDeclaration.returnType = (TypeReference)this.build(node.getReturnType(), TypeReference.class);
        methodDeclaration.annotations = EclipseASTMaker.toArray(this.build(node.getAnnotations()), new org.eclipse.jdt.internal.compiler.ast.Annotation[0]);
        methodDeclaration.selector = abstractMethod.selector;
        methodDeclaration.thrownExceptions = EclipseASTMaker.toArray(this.build(node.getThrownExceptions()), new TypeReference[0]);
        methodDeclaration.typeParameters = EclipseASTMaker.toArray(this.build(node.getTypeParameters()), new TypeParameter[0]);
        methodDeclaration.bits |= 8388608;
        methodDeclaration.arguments = EclipseASTMaker.toArray(this.build(node.getArguments()), new org.eclipse.jdt.internal.compiler.ast.Argument[0]);
        if (node.isImplementing()) {
            methodDeclaration.modifiers |= 536870912;
        }
        if (node.noBody()) {
            methodDeclaration.modifiers |= 16777216;
        } else {
            methodDeclaration.statements = EclipseASTMaker.toArray(this.build(node.getStatements()), new org.eclipse.jdt.internal.compiler.ast.Statement[0]);
        }
        return methodDeclaration;
    }

    @Override
    public ASTNode visitWrappedStatement(WrappedStatement node, Void p) {
        org.eclipse.jdt.internal.compiler.ast.Statement statement = (org.eclipse.jdt.internal.compiler.ast.Statement)node.getWrappedObject();
        Eclipse.setGeneratedByAndCopyPos((ASTNode)statement, this.source, this.posHintOf(node));
        return statement;
    }

    @Override
    public ASTNode visitWrappedTypeRef(WrappedTypeRef node, Void p) {
        TypeReference typeReference = null;
        if (node.getWrappedObject() instanceof TypeBinding) {
            typeReference = EclipseHandlerUtil.makeType((TypeBinding)node.getWrappedObject(), this.source, false);
        } else if (node.getWrappedObject() instanceof TypeReference) {
            typeReference = EclipseHandlerUtil.copyType((TypeReference)node.getWrappedObject(), this.source);
        }
        if (node.getDims() > 0) {
            typeReference = typeReference.copyDims(node.getDims());
        }
        if (node.isSuperType()) {
            typeReference.bits |= 16;
        }
        EclipseHandlerUtil.setGeneratedBy((ASTNode)typeReference, this.source);
        return typeReference;
    }

    @ConstructorProperties(value={"sourceNode", "source"})
    public EclipseASTMaker(EclipseNode sourceNode, ASTNode source) {
        this.sourceNode = sourceNode;
        this.source = source;
    }

    static {
        UNARY_OPERATORS.put("+", 14);
        UNARY_OPERATORS.put("-", 13);
        UNARY_OPERATORS.put("!", 11);
        UNARY_OPERATORS.put("~", 12);
        UNARY_OPERATORS.put("++X", 14);
        UNARY_OPERATORS.put("--X", 13);
        UNARY_OPERATORS.put("X++", 14);
        UNARY_OPERATORS.put("X--", 13);
        BINARY_OPERATORS = new HashMap<String, Integer>();
        BINARY_OPERATORS.put("||", 1);
        BINARY_OPERATORS.put("&&", 0);
        BINARY_OPERATORS.put("==", 18);
        BINARY_OPERATORS.put("!=", 29);
        BINARY_OPERATORS.put("<", 4);
        BINARY_OPERATORS.put(">", 6);
        BINARY_OPERATORS.put("<=", 5);
        BINARY_OPERATORS.put(">=", 7);
        BINARY_OPERATORS.put("|", 3);
        BINARY_OPERATORS.put("^", 8);
        BINARY_OPERATORS.put("&", 2);
        BINARY_OPERATORS.put("<<", 10);
        BINARY_OPERATORS.put(">>", 17);
        BINARY_OPERATORS.put(">>>", 19);
        BINARY_OPERATORS.put("+", 14);
        BINARY_OPERATORS.put("-", 13);
        BINARY_OPERATORS.put("*", 15);
        BINARY_OPERATORS.put("/", 9);
        BINARY_OPERATORS.put("%", 16);
    }

    private static final class Reflection {
        public static final Constructor<CastExpression> castExpressionConstructor;
        public static final Constructor<IntLiteral> intLiteralConstructor;
        public static final Constructor<LongLiteral> longLiteralConstructor;
        public static final Method intLiteralFactoryMethod;
        public static final Method longLiteralFactoryMethod;

        private Reflection() {
        }

        static {
            Class[] parameterTypes = new Class[]{char[].class, Integer.TYPE, Integer.TYPE};
            Constructor intLiteralConstructor_ = null;
            Constructor longLiteralConstructor_ = null;
            Method intLiteralFactoryMethod_ = null;
            Method longLiteralFactoryMethod_ = null;
            try {
                intLiteralConstructor_ = IntLiteral.class.getConstructor(parameterTypes);
                longLiteralConstructor_ = LongLiteral.class.getConstructor(parameterTypes);
            }
            catch (Exception ignore) {
                // empty catch block
            }
            try {
                intLiteralFactoryMethod_ = IntLiteral.class.getMethod("buildIntLiteral", parameterTypes);
                longLiteralFactoryMethod_ = LongLiteral.class.getMethod("buildLongLiteral", parameterTypes);
            }
            catch (Exception ignore) {
                // empty catch block
            }
            castExpressionConstructor = (Constructor)Cast.uncheckedCast(CastExpression.class.getConstructors()[0]);
            intLiteralConstructor = intLiteralConstructor_;
            longLiteralConstructor = longLiteralConstructor_;
            intLiteralFactoryMethod = intLiteralFactoryMethod_;
            longLiteralFactoryMethod = longLiteralFactoryMethod_;
        }
    }

}

