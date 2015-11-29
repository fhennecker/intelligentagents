/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.BlockTree
 *  com.sun.source.tree.CaseTree
 *  com.sun.source.tree.DoWhileLoopTree
 *  com.sun.source.tree.EnhancedForLoopTree
 *  com.sun.source.tree.ForLoopTree
 *  com.sun.source.tree.IfTree
 *  com.sun.source.tree.WhileLoopTree
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCCase
 *  com.sun.tools.javac.tree.JCTree$JCDoWhileLoop
 *  com.sun.tools.javac.tree.JCTree$JCEnhancedForLoop
 *  com.sun.tools.javac.tree.JCTree$JCForLoop
 *  com.sun.tools.javac.tree.JCTree$JCIf
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCWhileLoop
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.handlers.replace;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.ast.Statement;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.replace.ReplaceVisitor;

public abstract class StatementReplaceVisitor
extends ReplaceVisitor<JCTree.JCStatement> {
    protected StatementReplaceVisitor(JavacMethod method, Statement<?> replacement) {
        super(method, replacement);
    }

    public Void visitBlock(BlockTree tree, Void p) {
        JCTree.JCBlock block = (JCTree.JCBlock)tree;
        block.stats = this.replace(block.stats);
        return (Void)super.visitBlock(tree, (Object)p);
    }

    public Void visitCase(CaseTree tree, Void p) {
        JCTree.JCCase caseTree = (JCTree.JCCase)tree;
        caseTree.stats = this.replace(caseTree.stats);
        return (Void)super.visitCase(tree, (Object)p);
    }

    public Void visitDoWhileLoop(DoWhileLoopTree tree, Void p) {
        JCTree.JCDoWhileLoop doWhileLoop = (JCTree.JCDoWhileLoop)tree;
        doWhileLoop.body = this.replace(doWhileLoop.body);
        return (Void)super.visitDoWhileLoop(tree, (Object)p);
    }

    public Void visitEnhancedForLoop(EnhancedForLoopTree tree, Void p) {
        JCTree.JCEnhancedForLoop enhancedForLoop = (JCTree.JCEnhancedForLoop)tree;
        enhancedForLoop.body = this.replace(enhancedForLoop.body);
        return (Void)super.visitEnhancedForLoop(tree, (Object)p);
    }

    public Void visitForLoop(ForLoopTree tree, Void p) {
        JCTree.JCForLoop forLoop = (JCTree.JCForLoop)tree;
        forLoop.body = this.replace(forLoop.body);
        return (Void)super.visitForLoop(tree, (Object)p);
    }

    public Void visitIf(IfTree tree, Void p) {
        JCTree.JCIf ifTree = (JCTree.JCIf)tree;
        ifTree.thenpart = this.replace(ifTree.thenpart);
        ifTree.elsepart = this.replace(ifTree.elsepart);
        return (Void)super.visitIf(tree, (Object)p);
    }

    public Void visitWhileLoop(WhileLoopTree tree, Void p) {
        JCTree.JCWhileLoop whileLoop = (JCTree.JCWhileLoop)tree;
        whileLoop.body = this.replace(whileLoop.body);
        return (Void)super.visitWhileLoop(tree, (Object)p);
    }
}

