/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression
 *  org.eclipse.jdt.internal.compiler.ast.AllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 *  org.eclipse.jdt.internal.compiler.ast.ArrayReference
 *  org.eclipse.jdt.internal.compiler.ast.Assignment
 *  org.eclipse.jdt.internal.compiler.ast.BinaryExpression
 *  org.eclipse.jdt.internal.compiler.ast.CastExpression
 *  org.eclipse.jdt.internal.compiler.ast.CompoundAssignment
 *  org.eclipse.jdt.internal.compiler.ast.ConditionalExpression
 *  org.eclipse.jdt.internal.compiler.ast.DoStatement
 *  org.eclipse.jdt.internal.compiler.ast.EqualExpression
 *  org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.ForStatement
 *  org.eclipse.jdt.internal.compiler.ast.ForeachStatement
 *  org.eclipse.jdt.internal.compiler.ast.IfStatement
 *  org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression
 *  org.eclipse.jdt.internal.compiler.ast.PostfixExpression
 *  org.eclipse.jdt.internal.compiler.ast.PrefixExpression
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SwitchStatement
 *  org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement
 *  org.eclipse.jdt.internal.compiler.ast.ThrowStatement
 *  org.eclipse.jdt.internal.compiler.ast.UnaryExpression
 *  org.eclipse.jdt.internal.compiler.ast.WhileStatement
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 */
package lombok.eclipse.handlers.replace;

import lombok.ast.Statement;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.replace.ReplaceVisitor;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public abstract class ExpressionReplaceVisitor
extends ReplaceVisitor<Expression> {
    protected ExpressionReplaceVisitor(EclipseMethod method, Statement<?> replacement) {
        super(method, replacement);
    }

    public boolean visit(AllocationExpression allocationExpression, BlockScope scope) {
        this.replace(allocationExpression.arguments);
        return true;
    }

    public boolean visit(AND_AND_Expression and_and_Expression, BlockScope scope) {
        return this.visit((BinaryExpression)and_and_Expression, scope);
    }

    public boolean visit(ArrayAllocationExpression arrayAllocationExpression, BlockScope scope) {
        this.replace(arrayAllocationExpression.dimensions);
        return true;
    }

    public boolean visit(ArrayInitializer arrayInitializer, BlockScope scope) {
        this.replace(arrayInitializer.expressions);
        return true;
    }

    public boolean visit(ArrayReference arrayReference, BlockScope scope) {
        arrayReference.receiver = this.replace(arrayReference.receiver);
        arrayReference.position = this.replace(arrayReference.position);
        return true;
    }

    public boolean visit(Assignment assignment, BlockScope scope) {
        assignment.lhs = this.replace(assignment.lhs);
        assignment.expression = this.replace(assignment.expression);
        return true;
    }

    public boolean visit(BinaryExpression binaryExpression, BlockScope scope) {
        binaryExpression.left = this.replace(binaryExpression.left);
        binaryExpression.right = this.replace(binaryExpression.right);
        return true;
    }

    public boolean visit(CastExpression castExpression, BlockScope scope) {
        castExpression.expression = this.replace(castExpression.expression);
        return true;
    }

    public boolean visit(CompoundAssignment compoundAssignment, BlockScope scope) {
        compoundAssignment.lhs = this.replace(compoundAssignment.lhs);
        compoundAssignment.expression = this.replace(compoundAssignment.expression);
        return true;
    }

    public boolean visit(ConditionalExpression conditionalExpression, BlockScope scope) {
        conditionalExpression.condition = this.replace(conditionalExpression.condition);
        conditionalExpression.valueIfTrue = this.replace(conditionalExpression.valueIfTrue);
        conditionalExpression.valueIfFalse = this.replace(conditionalExpression.valueIfFalse);
        return true;
    }

    public boolean visit(DoStatement doStatement, BlockScope scope) {
        doStatement.condition = this.replace(doStatement.condition);
        return true;
    }

    public boolean visit(EqualExpression equalExpression, BlockScope scope) {
        return this.visit((BinaryExpression)equalExpression, scope);
    }

    public boolean visit(ExplicitConstructorCall explicitConstructor, BlockScope scope) {
        this.replace(explicitConstructor.arguments);
        return true;
    }

    public boolean visit(ForeachStatement forStatement, BlockScope scope) {
        forStatement.collection = this.replace(forStatement.collection);
        return true;
    }

    public boolean visit(ForStatement forStatement, BlockScope scope) {
        forStatement.condition = this.replace(forStatement.condition);
        return true;
    }

    public boolean visit(IfStatement ifStatement, BlockScope scope) {
        ifStatement.condition = this.replace(ifStatement.condition);
        return true;
    }

    public boolean visit(InstanceOfExpression instanceOfExpression, BlockScope scope) {
        instanceOfExpression.expression = this.replace(instanceOfExpression.expression);
        return true;
    }

    public boolean visit(LocalDeclaration localDeclaration, BlockScope scope) {
        localDeclaration.initialization = this.replace(localDeclaration.initialization);
        return true;
    }

    public boolean visit(MessageSend messageSend, BlockScope scope) {
        messageSend.receiver = this.replace(messageSend.receiver);
        this.replace(messageSend.arguments);
        return true;
    }

    public boolean visit(OR_OR_Expression or_or_Expression, BlockScope scope) {
        return this.visit((BinaryExpression)or_or_Expression, scope);
    }

    public boolean visit(PostfixExpression postfixExpression, BlockScope scope) {
        return this.visit((CompoundAssignment)postfixExpression, scope);
    }

    public boolean visit(PrefixExpression prefixExpression, BlockScope scope) {
        return this.visit((CompoundAssignment)prefixExpression, scope);
    }

    public boolean visit(QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope) {
        return this.visit((AllocationExpression)qualifiedAllocationExpression, scope);
    }

    public boolean visit(ReturnStatement returnStatement, BlockScope scope) {
        returnStatement.expression = this.replace(returnStatement.expression);
        return true;
    }

    public boolean visit(SwitchStatement switchStatement, BlockScope scope) {
        switchStatement.expression = this.replace(switchStatement.expression);
        return true;
    }

    public boolean visit(SynchronizedStatement synchronizedStatement, BlockScope scope) {
        synchronizedStatement.expression = this.replace(synchronizedStatement.expression);
        return true;
    }

    public boolean visit(ThrowStatement throwStatement, BlockScope scope) {
        throwStatement.exception = this.replace(throwStatement.exception);
        return true;
    }

    public boolean visit(UnaryExpression unaryExpression, BlockScope scope) {
        unaryExpression.expression = this.replace(unaryExpression.expression);
        return true;
    }

    public boolean visit(WhileStatement whileStatement, BlockScope scope) {
        whileStatement.condition = this.replace(whileStatement.condition);
        return true;
    }
}

