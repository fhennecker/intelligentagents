/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.ArrayAccessTree
 *  com.sun.source.tree.AssignmentTree
 *  com.sun.source.tree.BinaryTree
 *  com.sun.source.tree.CompoundAssignmentTree
 *  com.sun.source.tree.ConditionalExpressionTree
 *  com.sun.source.tree.DoWhileLoopTree
 *  com.sun.source.tree.EnhancedForLoopTree
 *  com.sun.source.tree.ForLoopTree
 *  com.sun.source.tree.IfTree
 *  com.sun.source.tree.InstanceOfTree
 *  com.sun.source.tree.MemberSelectTree
 *  com.sun.source.tree.MethodInvocationTree
 *  com.sun.source.tree.NewArrayTree
 *  com.sun.source.tree.NewClassTree
 *  com.sun.source.tree.ReturnTree
 *  com.sun.source.tree.SwitchTree
 *  com.sun.source.tree.SynchronizedTree
 *  com.sun.source.tree.ThrowTree
 *  com.sun.source.tree.TypeCastTree
 *  com.sun.source.tree.UnaryTree
 *  com.sun.source.tree.VariableTree
 *  com.sun.source.tree.WhileLoopTree
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCArrayAccess
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCAssignOp
 *  com.sun.tools.javac.tree.JCTree$JCBinary
 *  com.sun.tools.javac.tree.JCTree$JCConditional
 *  com.sun.tools.javac.tree.JCTree$JCDoWhileLoop
 *  com.sun.tools.javac.tree.JCTree$JCEnhancedForLoop
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCForLoop
 *  com.sun.tools.javac.tree.JCTree$JCIf
 *  com.sun.tools.javac.tree.JCTree$JCInstanceOf
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCNewArray
 *  com.sun.tools.javac.tree.JCTree$JCNewClass
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCSwitch
 *  com.sun.tools.javac.tree.JCTree$JCSynchronized
 *  com.sun.tools.javac.tree.JCTree$JCThrow
 *  com.sun.tools.javac.tree.JCTree$JCTypeCast
 *  com.sun.tools.javac.tree.JCTree$JCUnary
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.JCTree$JCWhileLoop
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.handlers.replace;

import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.ast.Statement;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.replace.ReplaceVisitor;

public abstract class ExpressionReplaceVisitor
extends ReplaceVisitor<JCTree.JCExpression> {
    protected ExpressionReplaceVisitor(JavacMethod method, Statement<?> replacement) {
        super(method, replacement);
    }

    public Void visitArrayAccess(ArrayAccessTree tree, Void p) {
        JCTree.JCArrayAccess arrayAccess = (JCTree.JCArrayAccess)tree;
        arrayAccess.index = this.replace(arrayAccess.index);
        arrayAccess.indexed = this.replace(arrayAccess.indexed);
        return (Void)super.visitArrayAccess(tree, (Object)p);
    }

    public Void visitAssignment(AssignmentTree tree, Void p) {
        JCTree.JCAssign assign = (JCTree.JCAssign)tree;
        assign.lhs = this.replace(assign.lhs);
        assign.rhs = this.replace(assign.rhs);
        return (Void)super.visitAssignment(tree, (Object)p);
    }

    public Void visitBinary(BinaryTree tree, Void p) {
        JCTree.JCBinary assign = (JCTree.JCBinary)tree;
        assign.lhs = this.replace(assign.lhs);
        assign.rhs = this.replace(assign.rhs);
        return (Void)super.visitBinary(tree, (Object)p);
    }

    public Void visitCompoundAssignment(CompoundAssignmentTree tree, Void p) {
        JCTree.JCAssignOp assignOp = (JCTree.JCAssignOp)tree;
        assignOp.lhs = this.replace(assignOp.lhs);
        assignOp.rhs = this.replace(assignOp.rhs);
        return (Void)super.visitCompoundAssignment(tree, (Object)p);
    }

    public Void visitConditionalExpression(ConditionalExpressionTree tree, Void p) {
        JCTree.JCConditional conditional = (JCTree.JCConditional)tree;
        conditional.cond = this.replace(conditional.cond);
        conditional.truepart = this.replace(conditional.truepart);
        conditional.falsepart = this.replace(conditional.falsepart);
        return (Void)super.visitConditionalExpression(tree, (Object)p);
    }

    public Void visitDoWhileLoop(DoWhileLoopTree tree, Void p) {
        JCTree.JCDoWhileLoop doWhileLoop = (JCTree.JCDoWhileLoop)tree;
        doWhileLoop.cond = this.replace(doWhileLoop.cond);
        return (Void)super.visitDoWhileLoop(tree, (Object)p);
    }

    public Void visitEnhancedForLoop(EnhancedForLoopTree tree, Void p) {
        JCTree.JCEnhancedForLoop enhancedForLoop = (JCTree.JCEnhancedForLoop)tree;
        enhancedForLoop.expr = this.replace(enhancedForLoop.expr);
        return (Void)super.visitEnhancedForLoop(tree, (Object)p);
    }

    public Void visitForLoop(ForLoopTree tree, Void p) {
        JCTree.JCForLoop forLoop = (JCTree.JCForLoop)tree;
        forLoop.cond = this.replace(forLoop.cond);
        return (Void)super.visitForLoop(tree, (Object)p);
    }

    public Void visitIf(IfTree tree, Void p) {
        JCTree.JCIf ifStatement = (JCTree.JCIf)tree;
        ifStatement.cond = this.replace(ifStatement.cond);
        return (Void)super.visitIf(tree, (Object)p);
    }

    public Void visitInstanceOf(InstanceOfTree tree, Void p) {
        JCTree.JCInstanceOf instanceOfExpression = (JCTree.JCInstanceOf)tree;
        instanceOfExpression.expr = this.replace(instanceOfExpression.expr);
        return (Void)super.visitInstanceOf(tree, (Object)p);
    }

    public Void visitMethodInvocation(MethodInvocationTree tree, Void p) {
        JCTree.JCMethodInvocation methodInvocation = (JCTree.JCMethodInvocation)tree;
        methodInvocation.args = this.replace(methodInvocation.args);
        return (Void)super.visitMethodInvocation(tree, (Object)p);
    }

    public Void visitMemberSelect(MemberSelectTree tree, Void p) {
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess)tree;
        fieldAccess.selected = this.replace(fieldAccess.selected);
        return (Void)super.visitMemberSelect(tree, (Object)p);
    }

    public Void visitNewClass(NewClassTree tree, Void p) {
        JCTree.JCNewClass newClass = (JCTree.JCNewClass)tree;
        newClass.args = this.replace(newClass.args);
        return (Void)super.visitNewClass(tree, (Object)p);
    }

    public Void visitNewArray(NewArrayTree tree, Void p) {
        JCTree.JCNewArray newArray = (JCTree.JCNewArray)tree;
        newArray.elems = this.replace(newArray.elems);
        newArray.dims = this.replace(newArray.dims);
        return (Void)super.visitNewArray(tree, (Object)p);
    }

    public Void visitReturn(ReturnTree tree, Void p) {
        JCTree.JCReturn returnStatement = (JCTree.JCReturn)tree;
        returnStatement.expr = this.replace(returnStatement.expr);
        return (Void)super.visitReturn(tree, (Object)p);
    }

    public Void visitSwitch(SwitchTree tree, Void p) {
        JCTree.JCSwitch switchStatement = (JCTree.JCSwitch)tree;
        switchStatement.selector = this.replace(switchStatement.selector);
        return (Void)super.visitSwitch(tree, (Object)p);
    }

    public Void visitSynchronized(SynchronizedTree tree, Void p) {
        JCTree.JCSynchronized synchronizedStatement = (JCTree.JCSynchronized)tree;
        synchronizedStatement.lock = this.replace(synchronizedStatement.lock);
        return (Void)super.visitSynchronized(tree, (Object)p);
    }

    public Void visitThrow(ThrowTree tree, Void p) {
        JCTree.JCThrow throwStatement = (JCTree.JCThrow)tree;
        throwStatement.expr = this.replace(throwStatement.expr);
        return (Void)super.visitThrow(tree, (Object)p);
    }

    public Void visitTypeCast(TypeCastTree tree, Void p) {
        JCTree.JCTypeCast typeCast = (JCTree.JCTypeCast)tree;
        typeCast.expr = this.replace(typeCast.expr);
        return (Void)super.visitTypeCast(tree, (Object)p);
    }

    public Void visitUnary(UnaryTree tree, Void p) {
        JCTree.JCUnary unary = (JCTree.JCUnary)tree;
        unary.arg = this.replace(unary.arg);
        return (Void)super.visitUnary(tree, (Object)p);
    }

    public Void visitVariable(VariableTree tree, Void p) {
        JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl)tree;
        variableDecl.init = this.replace(variableDecl.init);
        return (Void)super.visitVariable(tree, (Object)p);
    }

    public Void visitWhileLoop(WhileLoopTree tree, Void p) {
        JCTree.JCWhileLoop whileLoop = (JCTree.JCWhileLoop)tree;
        whileLoop.cond = this.replace(whileLoop.cond);
        return (Void)super.visitWhileLoop(tree, (Object)p);
    }
}

