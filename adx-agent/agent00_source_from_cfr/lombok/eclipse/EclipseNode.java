/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.Clinit
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Initializer
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 */
package lombok.eclipse;

import java.util.List;
import lombok.core.AST;
import lombok.core.LombokNode;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EclipseNode
extends LombokNode<EclipseAST, EclipseNode, ASTNode> {
    EclipseNode(EclipseAST ast, ASTNode node, List<EclipseNode> children, AST.Kind kind) {
        super(ast, node, children, kind);
    }

    public void traverse(EclipseASTVisitor visitor) {
        if (!this.isCompleteParse() && visitor.getClass().isAnnotationPresent(DeferUntilPostDiet.class)) {
            return;
        }
        block0 : switch (this.getKind()) {
            case COMPILATION_UNIT: {
                visitor.visitCompilationUnit(this, (CompilationUnitDeclaration)this.get());
                ((EclipseAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitCompilationUnit(this, (CompilationUnitDeclaration)this.get());
                break;
            }
            case TYPE: {
                visitor.visitType(this, (TypeDeclaration)this.get());
                ((EclipseAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitType(this, (TypeDeclaration)this.get());
                break;
            }
            case FIELD: {
                visitor.visitField(this, (FieldDeclaration)this.get());
                ((EclipseAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitField(this, (FieldDeclaration)this.get());
                break;
            }
            case INITIALIZER: {
                visitor.visitInitializer(this, (Initializer)this.get());
                ((EclipseAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitInitializer(this, (Initializer)this.get());
                break;
            }
            case METHOD: {
                if (this.get() instanceof Clinit) {
                    return;
                }
                visitor.visitMethod(this, (AbstractMethodDeclaration)this.get());
                ((EclipseAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitMethod(this, (AbstractMethodDeclaration)this.get());
                break;
            }
            case ARGUMENT: {
                AbstractMethodDeclaration method = (AbstractMethodDeclaration)((EclipseNode)this.up()).get();
                visitor.visitMethodArgument(this, (Argument)this.get(), method);
                ((EclipseAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitMethodArgument(this, (Argument)this.get(), method);
                break;
            }
            case LOCAL: {
                visitor.visitLocal(this, (LocalDeclaration)this.get());
                ((EclipseAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitLocal(this, (LocalDeclaration)this.get());
                break;
            }
            case ANNOTATION: {
                switch (((EclipseNode)this.up()).getKind()) {
                    case TYPE: {
                        visitor.visitAnnotationOnType((TypeDeclaration)((EclipseNode)this.up()).get(), this, (Annotation)this.get());
                        break block0;
                    }
                    case FIELD: {
                        visitor.visitAnnotationOnField((FieldDeclaration)((EclipseNode)this.up()).get(), this, (Annotation)this.get());
                        break block0;
                    }
                    case METHOD: {
                        visitor.visitAnnotationOnMethod((AbstractMethodDeclaration)((EclipseNode)this.up()).get(), this, (Annotation)this.get());
                        break block0;
                    }
                    case ARGUMENT: {
                        visitor.visitAnnotationOnMethodArgument((Argument)((EclipseNode)this.parent).get(), (AbstractMethodDeclaration)((EclipseNode)((EclipseNode)this.parent).directUp()).get(), this, (Annotation)this.get());
                        break block0;
                    }
                    case LOCAL: {
                        visitor.visitAnnotationOnLocal((LocalDeclaration)((EclipseNode)this.parent).get(), this, (Annotation)this.get());
                        break block0;
                    }
                }
                throw new AssertionError((Object)("Annotation not expected as child of a " + (Object)((Object)((EclipseNode)this.up()).getKind())));
            }
            case STATEMENT: {
                visitor.visitStatement(this, (Statement)this.get());
                ((EclipseAST)this.ast).traverseChildren(visitor, this);
                visitor.endVisitStatement(this, (Statement)this.get());
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected kind during node traversal: " + (Object)((Object)this.getKind())));
            }
        }
    }

    @Override
    protected boolean fieldContainsAnnotation(ASTNode field, ASTNode annotation) {
        if (!(field instanceof FieldDeclaration)) {
            return false;
        }
        FieldDeclaration f = (FieldDeclaration)field;
        if (f.annotations == null) {
            return false;
        }
        for (Annotation childAnnotation : f.annotations) {
            if (childAnnotation != annotation) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        Object n = this.node instanceof TypeDeclaration ? ((TypeDeclaration)this.node).name : (Object)(this.node instanceof FieldDeclaration ? ((FieldDeclaration)this.node).name : (Object)(this.node instanceof AbstractMethodDeclaration ? ((AbstractMethodDeclaration)this.node).selector : (this.node instanceof LocalDeclaration ? (Object)((LocalDeclaration)this.node).name : (Object)null)));
        return n == null ? null : new String((char[])n);
    }

    @Override
    public void addError(String message) {
        this.addError(message, ((ASTNode)this.get()).sourceStart, ((ASTNode)this.get()).sourceEnd);
    }

    public void addError(String message, int sourceStart, int sourceEnd) {
        EclipseAST eclipseAST = (EclipseAST)this.ast;
        eclipseAST.getClass();
        ((EclipseAST)this.ast).addProblem(eclipseAST.new EclipseAST.ParseProblem(false, message, sourceStart, sourceEnd));
    }

    @Override
    public void addWarning(String message) {
        this.addWarning(message, ((ASTNode)this.get()).sourceStart, ((ASTNode)this.get()).sourceEnd);
    }

    public void addWarning(String message, int sourceStart, int sourceEnd) {
        EclipseAST eclipseAST = (EclipseAST)this.ast;
        eclipseAST.getClass();
        ((EclipseAST)this.ast).addProblem(eclipseAST.new EclipseAST.ParseProblem(true, message, sourceStart, sourceEnd));
    }

    @Override
    protected boolean calculateIsStructurallySignificant(ASTNode parent) {
        if (this.node instanceof TypeDeclaration) {
            return true;
        }
        if (this.node instanceof AbstractMethodDeclaration) {
            return true;
        }
        if (this.node instanceof FieldDeclaration) {
            return true;
        }
        if (this.node instanceof LocalDeclaration) {
            return true;
        }
        if (this.node instanceof CompilationUnitDeclaration) {
            return true;
        }
        return false;
    }

    public boolean isCompleteParse() {
        return ((EclipseAST)this.ast).isCompleteParse();
    }

}

