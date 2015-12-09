/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Symtab
 *  com.sun.tools.javac.model.JavacTypes
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.JCDiagnostic
 *  com.sun.tools.javac.util.JCDiagnostic$DiagnosticPosition
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import javax.tools.Diagnostic;
import lombok.core.AST;
import lombok.core.LombokNode;
import lombok.javac.JavacAST;
import lombok.javac.JavacASTVisitor;
import lombok.javac.LombokOptions;

public class JavacNode
extends LombokNode<JavacAST, JavacNode, JCTree> {
    public JavacNode(JavacAST ast, JCTree node, java.util.List<JavacNode> children, AST.Kind kind) {
        super(ast, node, children, kind);
    }

    public void traverse(JavacASTVisitor visitor) {
        block0 : switch (this.getKind()) {
            case COMPILATION_UNIT: {
                visitor.visitCompilationUnit(this, (JCTree.JCCompilationUnit)this.get());
                ((JavacAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitCompilationUnit(this, (JCTree.JCCompilationUnit)this.get());
                break;
            }
            case TYPE: {
                visitor.visitType(this, (JCTree.JCClassDecl)this.get());
                ((JavacAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitType(this, (JCTree.JCClassDecl)this.get());
                break;
            }
            case FIELD: {
                visitor.visitField(this, (JCTree.JCVariableDecl)this.get());
                ((JavacAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitField(this, (JCTree.JCVariableDecl)this.get());
                break;
            }
            case METHOD: {
                visitor.visitMethod(this, (JCTree.JCMethodDecl)this.get());
                ((JavacAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitMethod(this, (JCTree.JCMethodDecl)this.get());
                break;
            }
            case INITIALIZER: {
                visitor.visitInitializer(this, (JCTree.JCBlock)this.get());
                ((JavacAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitInitializer(this, (JCTree.JCBlock)this.get());
                break;
            }
            case ARGUMENT: {
                JCTree.JCMethodDecl parentMethod = (JCTree.JCMethodDecl)((JavacNode)this.up()).get();
                visitor.visitMethodArgument(this, (JCTree.JCVariableDecl)this.get(), parentMethod);
                ((JavacAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitMethodArgument(this, (JCTree.JCVariableDecl)this.get(), parentMethod);
                break;
            }
            case LOCAL: {
                visitor.visitLocal(this, (JCTree.JCVariableDecl)this.get());
                ((JavacAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitLocal(this, (JCTree.JCVariableDecl)this.get());
                break;
            }
            case STATEMENT: {
                visitor.visitStatement(this, (JCTree)this.get());
                ((JavacAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitStatement(this, (JCTree)this.get());
                break;
            }
            case ANNOTATION: {
                switch (((JavacNode)this.up()).getKind()) {
                    case TYPE: {
                        visitor.visitAnnotationOnType((JCTree.JCClassDecl)((JavacNode)this.up()).get(), this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                    case FIELD: {
                        visitor.visitAnnotationOnField((JCTree.JCVariableDecl)((JavacNode)this.up()).get(), this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                    case METHOD: {
                        visitor.visitAnnotationOnMethod((JCTree.JCMethodDecl)((JavacNode)this.up()).get(), this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                    case ARGUMENT: {
                        JCTree.JCVariableDecl argument = (JCTree.JCVariableDecl)((JavacNode)this.up()).get();
                        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl)((JavacNode)((JavacNode)this.up()).up()).get();
                        visitor.visitAnnotationOnMethodArgument(argument, method, this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                    case LOCAL: {
                        visitor.visitAnnotationOnLocal((JCTree.JCVariableDecl)((JavacNode)this.up()).get(), this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                }
                throw new AssertionError((Object)("Annotion not expected as child of a " + (Object)((Object)((JavacNode)this.up()).getKind())));
            }
            default: {
                throw new AssertionError((Object)("Unexpected kind during node traversal: " + (Object)((Object)this.getKind())));
            }
        }
    }

    @Override
    public String getName() {
        Name n = this.node instanceof JCTree.JCClassDecl ? ((JCTree.JCClassDecl)this.node).name : (this.node instanceof JCTree.JCMethodDecl ? ((JCTree.JCMethodDecl)this.node).name : (this.node instanceof JCTree.JCVariableDecl ? ((JCTree.JCVariableDecl)this.node).name : null));
        return n == null ? null : n.toString();
    }

    @Override
    protected boolean calculateIsStructurallySignificant(JCTree parent) {
        if (this.node instanceof JCTree.JCClassDecl) {
            return true;
        }
        if (this.node instanceof JCTree.JCMethodDecl) {
            return true;
        }
        if (this.node instanceof JCTree.JCVariableDecl) {
            return true;
        }
        if (this.node instanceof JCTree.JCCompilationUnit) {
            return true;
        }
        if (this.node instanceof JCTree.JCBlock) {
            return parent instanceof JCTree.JCClassDecl;
        }
        return false;
    }

    @Override
    protected boolean fieldContainsAnnotation(JCTree field, JCTree annotation) {
        if (!(field instanceof JCTree.JCVariableDecl)) {
            return false;
        }
        JCTree.JCVariableDecl f = (JCTree.JCVariableDecl)field;
        if (f.mods.annotations == null) {
            return false;
        }
        for (JCTree.JCAnnotation childAnnotation : f.mods.annotations) {
            if (childAnnotation != annotation) continue;
            return true;
        }
        return false;
    }

    public TreeMaker getTreeMaker() {
        return ((JavacAST)this.ast).getTreeMaker();
    }

    public Symtab getSymbolTable() {
        return ((JavacAST)this.ast).getSymbolTable();
    }

    public JavacTypes getTypesUtil() {
        return ((JavacAST)this.ast).getTypesUtil();
    }

    public Context getContext() {
        return ((JavacAST)this.ast).getContext();
    }

    public boolean shouldDeleteLombokAnnotations() {
        return LombokOptions.shouldDeleteLombokAnnotations(((JavacAST)this.ast).getContext());
    }

    public Name toName(String name) {
        return ((JavacAST)this.ast).toName(name);
    }

    @Override
    public void addError(String message) {
        ((JavacAST)this.ast).printMessage(Diagnostic.Kind.ERROR, message, this, null);
    }

    public void addError(String message, JCDiagnostic.DiagnosticPosition pos) {
        ((JavacAST)this.ast).printMessage(Diagnostic.Kind.ERROR, message, null, pos);
    }

    @Override
    public void addWarning(String message) {
        ((JavacAST)this.ast).printMessage(Diagnostic.Kind.WARNING, message, this, null);
    }

    public void addWarning(String message, JCDiagnostic.DiagnosticPosition pos) {
        ((JavacAST)this.ast).printMessage(Diagnostic.Kind.WARNING, message, null, pos);
    }

}

