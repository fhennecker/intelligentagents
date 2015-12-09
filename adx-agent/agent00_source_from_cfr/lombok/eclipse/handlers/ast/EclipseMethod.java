/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.MemberValuePair
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.NormalAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse.handlers.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.ast.Annotation;
import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.IMethodEditor;
import lombok.ast.IType;
import lombok.ast.Statement;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;
import lombok.core.AST;
import lombok.core.LombokNode;
import lombok.core.util.As;
import lombok.core.util.Each;
import lombok.core.util.Is;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.Eclipse;
import lombok.eclipse.handlers.ast.EclipseASTUtil;
import lombok.eclipse.handlers.ast.EclipseMethodEditor;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public final class EclipseMethod
implements IMethod<EclipseType, EclipseNode, ASTNode, AbstractMethodDeclaration> {
    private final EclipseNode methodNode;
    private final ASTNode source;
    private final EclipseMethodEditor editor;

    private EclipseMethod(EclipseNode methodNode, ASTNode source) {
        if (!(methodNode.get() instanceof AbstractMethodDeclaration)) {
            throw new IllegalArgumentException();
        }
        this.methodNode = methodNode;
        this.source = source;
        this.editor = new EclipseMethodEditor(this, source);
    }

    public EclipseMethodEditor editor() {
        return this.editor;
    }

    @Override
    public TypeRef returns() {
        return this.isConstructor() ? null : lombok.ast.AST.Type((Object)this.returnType());
    }

    @Override
    public TypeRef boxedReturns() {
        return EclipseASTUtil.boxedType(this.returnType());
    }

    @Override
    public boolean returns(Class<?> clazz) {
        return this.returns(clazz.getSimpleName());
    }

    @Override
    public boolean returns(String typeName) {
        TypeReference returnType = this.returnType();
        if (returnType == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (char[] elem : returnType.getTypeName()) {
            if (first) {
                first = false;
            } else {
                sb.append('.');
            }
            sb.append(elem);
        }
        String type = sb.toString();
        return type.endsWith(typeName);
    }

    private TypeReference returnType() {
        if (this.isConstructor()) {
            return null;
        }
        MethodDeclaration methodDecl = (MethodDeclaration)this.get();
        return methodDecl.returnType;
    }

    @Override
    public AccessLevel accessLevel() {
        if ((this.get().modifiers & 1) != 0) {
            return AccessLevel.PUBLIC;
        }
        if ((this.get().modifiers & 4) != 0) {
            return AccessLevel.PROTECTED;
        }
        if ((this.get().modifiers & 2) != 0) {
            return AccessLevel.PRIVATE;
        }
        return AccessLevel.PACKAGE;
    }

    @Override
    public boolean isSynchronized() {
        return !this.isConstructor() && (this.get().modifiers & 32) != 0;
    }

    @Override
    public boolean isStatic() {
        return !this.isConstructor() && (this.get().modifiers & 8) != 0;
    }

    @Override
    public boolean isConstructor() {
        return this.get() instanceof ConstructorDeclaration;
    }

    @Override
    public boolean isAbstract() {
        return this.get().isAbstract();
    }

    @Override
    public boolean isEmpty() {
        if (this.isConstructor() && ((ConstructorDeclaration)this.get()).constructorCall != null) {
            return false;
        }
        return Is.empty(this.get().statements);
    }

    @Override
    public AbstractMethodDeclaration get() {
        return (AbstractMethodDeclaration)this.methodNode.get();
    }

    @Override
    public EclipseNode node() {
        return this.methodNode;
    }

    @Override
    public EclipseNode getAnnotation(Class<? extends java.lang.annotation.Annotation> expectedType) {
        return this.getAnnotation(expectedType.getName());
    }

    @Override
    public EclipseNode getAnnotation(String typeName) {
        EclipseNode annotationNode = null;
        for (EclipseNode child : this.node().down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !Eclipse.matchesType((org.eclipse.jdt.internal.compiler.ast.Annotation)child.get(), typeName)) continue;
            annotationNode = child;
        }
        return annotationNode;
    }

    @Override
    public boolean hasNonFinalArgument() {
        for (Argument arg : Each.elementIn(this.get().arguments)) {
            if ((arg.modifiers & 16) != 0) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasArguments() {
        return Is.notEmpty(this.get().arguments);
    }

    @Override
    public String name() {
        return this.node().getName();
    }

    @Override
    public EclipseType surroundingType() {
        return EclipseType.typeOf(this.node(), this.source);
    }

    @Override
    public List<Statement<?>> statements() {
        ArrayList methodStatements = new ArrayList();
        for (org.eclipse.jdt.internal.compiler.ast.Statement statement : Each.elementIn(this.get().statements)) {
            methodStatements.add(lombok.ast.AST.Stat((Object)statement));
        }
        return methodStatements;
    }

    @Override
    public List<Annotation> annotations() {
        return this.annotations(this.get().annotations);
    }

    private List<Annotation> annotations(org.eclipse.jdt.internal.compiler.ast.Annotation[] anns) {
        ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        for (org.eclipse.jdt.internal.compiler.ast.Annotation annotation : Each.elementIn(anns)) {
            Annotation ann = (Annotation)lombok.ast.AST.Annotation(lombok.ast.AST.Type((Object)annotation.type)).posHint((Object)annotation);
            if (annotation instanceof SingleMemberAnnotation) {
                ann.withValue(lombok.ast.AST.Expr((Object)((SingleMemberAnnotation)annotation).memberValue));
            } else if (annotation instanceof NormalAnnotation) {
                for (MemberValuePair pair : Each.elementIn(((NormalAnnotation)annotation).memberValuePairs)) {
                    ann.withValue(As.string(pair.name), lombok.ast.AST.Expr((Object)pair.value)).posHint((Object)pair);
                }
            }
            annotations.add(ann);
        }
        return annotations;
    }

    @Override
    public /* varargs */ List<lombok.ast.Argument> arguments(IMethod.ArgumentStyle ... style) {
        List<IMethod.ArgumentStyle> styles = As.list(style);
        ArrayList<lombok.ast.Argument> methodArguments = new ArrayList<lombok.ast.Argument>();
        for (Argument argument : Each.elementIn(this.get().arguments)) {
            TypeRef argType = styles.contains((Object)IMethod.ArgumentStyle.BOXED_TYPES) ? (TypeRef)EclipseASTUtil.boxedType(argument.type).posHint((Object)argument.type) : lombok.ast.AST.Type((Object)argument.type);
            lombok.ast.Argument arg = (lombok.ast.Argument)lombok.ast.AST.Arg(argType, As.string(argument.name)).posHint((Object)argument);
            if (styles.contains((Object)IMethod.ArgumentStyle.INCLUDE_ANNOTATIONS)) {
                arg.withAnnotations(this.annotations(argument.annotations));
            }
            methodArguments.add(arg);
        }
        return methodArguments;
    }

    @Override
    public List<TypeParam> typeParameters() {
        ArrayList<TypeParam> typeParameters = new ArrayList<TypeParam>();
        if (this.isConstructor()) {
            return typeParameters;
        }
        MethodDeclaration methodDecl = (MethodDeclaration)this.get();
        for (TypeParameter typaram : Each.elementIn(methodDecl.typeParameters)) {
            TypeParam typeParameter = (TypeParam)lombok.ast.AST.TypeParam(As.string(typaram.name)).posHint((Object)typaram);
            if (typaram.type != null) {
                typeParameter.withBound(lombok.ast.AST.Type((Object)typaram.type));
            }
            for (TypeReference bound : Each.elementIn(typaram.bounds)) {
                typeParameter.withBound(lombok.ast.AST.Type((Object)bound));
            }
            typeParameters.add(typeParameter);
        }
        return typeParameters;
    }

    @Override
    public List<TypeRef> thrownExceptions() {
        ArrayList<TypeRef> thrownExceptions = new ArrayList<TypeRef>();
        for (TypeReference thrownException : Each.elementIn(this.get().thrownExceptions)) {
            thrownExceptions.add(lombok.ast.AST.Type((Object)thrownException));
        }
        return thrownExceptions;
    }

    public String toString() {
        return this.get().toString();
    }

    public static EclipseMethod methodOf(EclipseNode node, ASTNode source) {
        EclipseNode methodNode = Eclipse.methodNodeOf(node);
        return methodNode == null ? null : new EclipseMethod(methodNode, source);
    }
}

