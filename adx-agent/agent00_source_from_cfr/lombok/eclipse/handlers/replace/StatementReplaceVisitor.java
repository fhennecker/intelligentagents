/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.DoStatement
 *  org.eclipse.jdt.internal.compiler.ast.ForStatement
 *  org.eclipse.jdt.internal.compiler.ast.ForeachStatement
 *  org.eclipse.jdt.internal.compiler.ast.IfStatement
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.SwitchStatement
 *  org.eclipse.jdt.internal.compiler.ast.WhileStatement
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 */
package lombok.eclipse.handlers.replace;

import lombok.ast.Statement;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.replace.ReplaceVisitor;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;

public abstract class StatementReplaceVisitor
extends ReplaceVisitor<org.eclipse.jdt.internal.compiler.ast.Statement> {
    protected StatementReplaceVisitor(EclipseMethod method, Statement<?> replacement) {
        super(method, replacement);
    }

    public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
        this.replace(constructorDeclaration.statements);
        return true;
    }

    public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
        this.replace(methodDeclaration.statements);
        return true;
    }

    public boolean visit(Block block, BlockScope scope) {
        this.replace(block.statements);
        return true;
    }

    public boolean visit(DoStatement doStatement, BlockScope scope) {
        doStatement.action = this.replace(doStatement.action);
        return true;
    }

    public boolean visit(ForeachStatement forStatement, BlockScope scope) {
        forStatement.action = this.replace(forStatement.action);
        return true;
    }

    public boolean visit(ForStatement forStatement, BlockScope scope) {
        forStatement.action = this.replace(forStatement.action);
        return true;
    }

    public boolean visit(IfStatement ifStatement, BlockScope scope) {
        ifStatement.thenStatement = this.replace(ifStatement.thenStatement);
        ifStatement.elseStatement = this.replace(ifStatement.elseStatement);
        return true;
    }

    public boolean visit(SwitchStatement switchStatement, BlockScope scope) {
        this.replace(switchStatement.statements);
        return true;
    }

    public boolean visit(WhileStatement whileStatement, BlockScope scope) {
        whileStatement.action = this.replace(whileStatement.action);
        return true;
    }
}

