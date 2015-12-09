/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ASTVisitor
 *  org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 *  org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ArrayReference
 *  org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.AssertStatement
 *  org.eclipse.jdt.internal.compiler.ast.Assignment
 *  org.eclipse.jdt.internal.compiler.ast.BinaryExpression
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.BreakStatement
 *  org.eclipse.jdt.internal.compiler.ast.CaseStatement
 *  org.eclipse.jdt.internal.compiler.ast.CastExpression
 *  org.eclipse.jdt.internal.compiler.ast.CharLiteral
 *  org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess
 *  org.eclipse.jdt.internal.compiler.ast.Clinit
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.CompoundAssignment
 *  org.eclipse.jdt.internal.compiler.ast.ConditionalExpression
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ContinueStatement
 *  org.eclipse.jdt.internal.compiler.ast.DoStatement
 *  org.eclipse.jdt.internal.compiler.ast.DoubleLiteral
 *  org.eclipse.jdt.internal.compiler.ast.EmptyStatement
 *  org.eclipse.jdt.internal.compiler.ast.EqualExpression
 *  org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.ExtendedStringLiteral
 *  org.eclipse.jdt.internal.compiler.ast.FalseLiteral
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldReference
 *  org.eclipse.jdt.internal.compiler.ast.FloatLiteral
 *  org.eclipse.jdt.internal.compiler.ast.ForStatement
 *  org.eclipse.jdt.internal.compiler.ast.ForeachStatement
 *  org.eclipse.jdt.internal.compiler.ast.IfStatement
 *  org.eclipse.jdt.internal.compiler.ast.ImportReference
 *  org.eclipse.jdt.internal.compiler.ast.Initializer
 *  org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression
 *  org.eclipse.jdt.internal.compiler.ast.IntLiteral
 *  org.eclipse.jdt.internal.compiler.ast.Javadoc
 *  org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression
 *  org.eclipse.jdt.internal.compiler.ast.JavadocArrayQualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.JavadocArraySingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference
 *  org.eclipse.jdt.internal.compiler.ast.JavadocImplicitTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend
 *  org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.LabeledStatement
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
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.StringLiteral
 *  org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation
 *  org.eclipse.jdt.internal.compiler.ast.SuperReference
 *  org.eclipse.jdt.internal.compiler.ast.SwitchStatement
 *  org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.ThrowStatement
 *  org.eclipse.jdt.internal.compiler.ast.TrueLiteral
 *  org.eclipse.jdt.internal.compiler.ast.TryStatement
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.UnaryExpression
 *  org.eclipse.jdt.internal.compiler.ast.WhileStatement
 *  org.eclipse.jdt.internal.compiler.ast.Wildcard
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 */
package lombok.eclipse.handlers;

import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExtendedStringLiteral;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocArraySingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocImplicitTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend;
import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
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
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;

public final class SetGeneratedByVisitor
extends ASTVisitor {
    private static final long INT_TO_LONG_MASK = 0xFFFFFFFFL;
    private final ASTNode source;
    private final int newSourceStart;
    private final int newSourceEnd;

    public SetGeneratedByVisitor(ASTNode source) {
        this.source = source;
        this.newSourceStart = this.source.sourceStart;
        this.newSourceEnd = this.source.sourceEnd;
    }

    private void applyOffset(JavadocAllocationExpression node) {
        this.applyOffsetExpression((Expression)node);
        node.memberStart = this.newSourceStart;
        node.tagSourceEnd = this.newSourceEnd;
        node.tagSourceStart = this.newSourceStart;
    }

    private void applyOffset(JavadocMessageSend node) {
        this.applyOffsetMessageSend((MessageSend)node);
        node.tagSourceEnd = this.newSourceEnd;
        node.tagSourceStart = this.newSourceStart;
    }

    private void applyOffset(JavadocSingleNameReference node) {
        this.applyOffsetExpression((Expression)node);
        node.tagSourceEnd = this.newSourceEnd;
        node.tagSourceStart = this.newSourceStart;
    }

    private void applyOffset(JavadocSingleTypeReference node) {
        this.applyOffsetExpression((Expression)node);
        node.tagSourceEnd = this.newSourceEnd;
        node.tagSourceStart = this.newSourceStart;
    }

    private void applyOffset(JavadocFieldReference node) {
        this.applyOffsetFieldReference((FieldReference)node);
        node.tagSourceEnd = this.newSourceEnd;
        node.tagSourceStart = this.newSourceStart;
    }

    private void applyOffset(JavadocArrayQualifiedTypeReference node) {
        this.applyOffsetQualifiedTypeReference((QualifiedTypeReference)node);
        node.tagSourceEnd = this.newSourceEnd;
        node.tagSourceStart = this.newSourceStart;
    }

    private void applyOffset(JavadocQualifiedTypeReference node) {
        this.applyOffsetQualifiedTypeReference((QualifiedTypeReference)node);
        node.tagSourceEnd = this.newSourceEnd;
        node.tagSourceStart = this.newSourceStart;
    }

    private void applyOffset(Annotation node) {
        this.applyOffsetExpression((Expression)node);
        node.declarationSourceEnd = this.newSourceEnd;
    }

    private void applyOffset(ArrayTypeReference node) {
        this.applyOffsetExpression((Expression)node);
        node.originalSourceEnd = this.newSourceEnd;
    }

    private void applyOffset(AbstractMethodDeclaration node) {
        this.applyOffsetASTNode((ASTNode)node);
        node.bodyEnd = this.newSourceEnd;
        node.bodyStart = this.newSourceStart;
        node.declarationSourceEnd = this.newSourceEnd;
        node.declarationSourceStart = this.newSourceStart;
        node.modifiersSourceStart = this.newSourceStart;
    }

    private void applyOffset(Javadoc node) {
        this.applyOffsetASTNode((ASTNode)node);
        node.valuePositions = this.newSourceStart;
        for (int i = 0; i < node.inheritedPositions.length; ++i) {
            node.inheritedPositions[i] = this.recalcSourcePosition(node.inheritedPositions[i]);
        }
    }

    private void applyOffset(Initializer node) {
        this.applyOffsetFieldDeclaration((FieldDeclaration)node);
        node.bodyStart = this.newSourceStart;
        node.bodyEnd = this.newSourceEnd;
    }

    private void applyOffset(TypeDeclaration node) {
        this.applyOffsetASTNode((ASTNode)node);
        node.bodyEnd = this.newSourceEnd;
        node.bodyStart = this.newSourceStart;
        node.declarationSourceEnd = this.newSourceEnd;
        node.declarationSourceStart = this.newSourceStart;
        node.modifiersSourceStart = this.newSourceStart;
    }

    private void applyOffset(ImportReference node) {
        this.applyOffsetASTNode((ASTNode)node);
        node.declarationEnd = this.newSourceEnd;
        node.declarationSourceEnd = this.newSourceEnd;
        node.declarationSourceStart = this.newSourceStart;
        for (int i = 0; i < node.sourcePositions.length; ++i) {
            node.sourcePositions[i] = this.recalcSourcePosition(node.sourcePositions[i]);
        }
    }

    private void applyOffsetASTNode(ASTNode node) {
        node.sourceEnd = this.newSourceEnd;
        node.sourceStart = this.newSourceStart;
    }

    private void applyOffsetExpression(Expression node) {
        this.applyOffsetASTNode((ASTNode)node);
        node.statementEnd = this.newSourceEnd;
    }

    private void applyOffsetVariable(AbstractVariableDeclaration node) {
        this.applyOffsetASTNode((ASTNode)node);
        node.declarationEnd = this.newSourceEnd;
        node.declarationSourceEnd = this.newSourceEnd;
        node.declarationSourceStart = this.newSourceStart;
        node.modifiersSourceStart = this.newSourceStart;
    }

    private void applyOffsetFieldDeclaration(FieldDeclaration node) {
        this.applyOffsetVariable((AbstractVariableDeclaration)node);
        node.endPart1Position = this.newSourceEnd;
        node.endPart2Position = this.newSourceEnd;
    }

    private void applyOffsetFieldReference(FieldReference node) {
        this.applyOffsetExpression((Expression)node);
        node.nameSourcePosition = this.recalcSourcePosition(node.nameSourcePosition);
    }

    private void applyOffsetMessageSend(MessageSend node) {
        this.applyOffsetExpression((Expression)node);
        node.nameSourcePosition = this.recalcSourcePosition(node.nameSourcePosition);
    }

    private void applyOffsetQualifiedNameReference(QualifiedNameReference node) {
        this.applyOffsetExpression((Expression)node);
        for (int i = 0; i < node.sourcePositions.length; ++i) {
            node.sourcePositions[i] = this.recalcSourcePosition(node.sourcePositions[i]);
        }
    }

    private void applyOffsetQualifiedTypeReference(QualifiedTypeReference node) {
        this.applyOffsetExpression((Expression)node);
        for (int i = 0; i < node.sourcePositions.length; ++i) {
            node.sourcePositions[i] = this.recalcSourcePosition(node.sourcePositions[i]);
        }
    }

    private long recalcSourcePosition(long sourcePosition) {
        return (long)this.newSourceStart << 32 | (long)this.newSourceEnd & 0xFFFFFFFFL;
    }

    public boolean visit(AllocationExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(AND_AND_Expression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(AnnotationMethodDeclaration node, ClassScope classScope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((AbstractMethodDeclaration)node);
        return super.visit(node, classScope);
    }

    public boolean visit(Argument node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetVariable((AbstractVariableDeclaration)node);
        return super.visit(node, scope);
    }

    public boolean visit(Argument node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetVariable((AbstractVariableDeclaration)node);
        return super.visit(node, scope);
    }

    public boolean visit(ArrayAllocationExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ArrayInitializer node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ArrayQualifiedTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetQualifiedTypeReference((QualifiedTypeReference)node);
        return super.visit(node, scope);
    }

    public boolean visit(ArrayQualifiedTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetQualifiedTypeReference((QualifiedTypeReference)node);
        return super.visit(node, scope);
    }

    public boolean visit(ArrayReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ArrayTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(ArrayTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(AssertStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(Assignment node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(BinaryExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(Block node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(BreakStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(CaseStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(CastExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(CharLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ClassLiteralAccess node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(Clinit node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((AbstractMethodDeclaration)node);
        return super.visit(node, scope);
    }

    public boolean visit(CompilationUnitDeclaration node, CompilationUnitScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(CompoundAssignment node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ConditionalExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ConstructorDeclaration node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((AbstractMethodDeclaration)node);
        return super.visit(node, scope);
    }

    public boolean visit(ContinueStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(DoStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(DoubleLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(EmptyStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(EqualExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ExplicitConstructorCall node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(ExtendedStringLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(FalseLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(FieldDeclaration node, MethodScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetFieldDeclaration(node);
        return super.visit(node, scope);
    }

    public boolean visit(FieldReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetFieldReference(node);
        return super.visit(node, scope);
    }

    public boolean visit(FieldReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetFieldReference(node);
        return super.visit(node, scope);
    }

    public boolean visit(FloatLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ForeachStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(ForStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(IfStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(ImportReference node, CompilationUnitScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(Initializer node, MethodScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(InstanceOfExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(IntLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(Javadoc node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(Javadoc node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocAllocationExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocAllocationExpression node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArgumentExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArgumentExpression node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArrayQualifiedTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArrayQualifiedTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArraySingleTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((ArrayTypeReference)node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArraySingleTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((ArrayTypeReference)node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocFieldReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocFieldReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocImplicitTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocImplicitTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocMessageSend node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocMessageSend node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocQualifiedTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocQualifiedTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocReturnStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocReturnStatement node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocSingleNameReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocSingleNameReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocSingleTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(JavadocSingleTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(LabeledStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(LocalDeclaration node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetVariable((AbstractVariableDeclaration)node);
        return super.visit(node, scope);
    }

    public boolean visit(LongLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(MarkerAnnotation node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((Annotation)node);
        return super.visit(node, scope);
    }

    public boolean visit(MemberValuePair node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(MessageSend node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetMessageSend(node);
        return super.visit(node, scope);
    }

    public boolean visit(MethodDeclaration node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((AbstractMethodDeclaration)node);
        return super.visit(node, scope);
    }

    public boolean visit(StringLiteralConcatenation node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(NormalAnnotation node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((Annotation)node);
        return super.visit(node, scope);
    }

    public boolean visit(NullLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(OR_OR_Expression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ParameterizedQualifiedTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetQualifiedTypeReference((QualifiedTypeReference)node);
        return super.visit(node, scope);
    }

    public boolean visit(ParameterizedQualifiedTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetQualifiedTypeReference((QualifiedTypeReference)node);
        return super.visit(node, scope);
    }

    public boolean visit(ParameterizedSingleTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((ArrayTypeReference)node);
        return super.visit(node, scope);
    }

    public boolean visit(ParameterizedSingleTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((ArrayTypeReference)node);
        return super.visit(node, scope);
    }

    public boolean visit(PostfixExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(PrefixExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedAllocationExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedNameReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetQualifiedNameReference(node);
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedNameReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedSuperReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedSuperReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedThisReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedThisReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetQualifiedTypeReference(node);
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetQualifiedTypeReference(node);
        return super.visit(node, scope);
    }

    public boolean visit(ReturnStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(SingleMemberAnnotation node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset((Annotation)node);
        return super.visit(node, scope);
    }

    public boolean visit(SingleNameReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(SingleNameReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(SingleTypeReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(SingleTypeReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(StringLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(SuperReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(SwitchStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        node.blockStart = this.newSourceStart;
        return super.visit(node, scope);
    }

    public boolean visit(SynchronizedStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(ThisReference node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(ThisReference node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        return super.visit(node, scope);
    }

    public boolean visit(ThrowStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(TrueLiteral node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(TryStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(TypeDeclaration node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(TypeDeclaration node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(TypeDeclaration node, CompilationUnitScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffset(node);
        return super.visit(node, scope);
    }

    public boolean visit(TypeParameter node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetVariable((AbstractVariableDeclaration)node);
        return super.visit(node, scope);
    }

    public boolean visit(TypeParameter node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetVariable((AbstractVariableDeclaration)node);
        return super.visit(node, scope);
    }

    public boolean visit(UnaryExpression node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(WhileStatement node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetASTNode((ASTNode)node);
        return super.visit(node, scope);
    }

    public boolean visit(Wildcard node, BlockScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }

    public boolean visit(Wildcard node, ClassScope scope) {
        EclipseHandlerUtil.setGeneratedBy((ASTNode)node, this.source);
        this.applyOffsetExpression((Expression)node);
        return super.visit(node, scope);
    }
}

