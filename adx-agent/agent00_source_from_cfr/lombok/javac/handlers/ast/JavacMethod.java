/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCTypeParameter
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers.ast;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.ast.Annotation;
import lombok.ast.Argument;
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
import lombok.core.util.Is;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.ast.JavacASTUtil;
import lombok.javac.handlers.ast.JavacMethodEditor;
import lombok.javac.handlers.ast.JavacResolver;
import lombok.javac.handlers.ast.JavacType;

public final class JavacMethod
implements IMethod<JavacType, JavacNode, JCTree, JCTree.JCMethodDecl> {
    private final JavacNode methodNode;
    private final JCTree source;
    private final JavacMethodEditor editor;

    private JavacMethod(JavacNode methodNode, JCTree source) {
        if (!(methodNode.get() instanceof JCTree.JCMethodDecl)) {
            throw new IllegalArgumentException();
        }
        this.methodNode = methodNode;
        this.source = source;
        this.editor = new JavacMethodEditor(this, source);
    }

    public JavacMethodEditor editor() {
        return this.editor;
    }

    @Override
    public TypeRef returns() {
        return this.isConstructor() ? null : lombok.ast.AST.Type((Object)this.returnType());
    }

    @Override
    public TypeRef boxedReturns() {
        return JavacASTUtil.boxedType(this.returnType());
    }

    @Override
    public boolean returns(Class<?> clazz) {
        return this.returns(clazz.getSimpleName());
    }

    @Override
    public boolean returns(String typeName) {
        JCTree.JCExpression returnType = this.returnType();
        if (returnType == null) {
            return false;
        }
        String type = returnType instanceof JCTree.JCTypeApply ? ((JCTree.JCTypeApply)returnType).clazz.toString() : returnType.toString();
        return type.endsWith(typeName);
    }

    private JCTree.JCExpression returnType() {
        return this.isConstructor() ? null : this.get().restype;
    }

    @Override
    public AccessLevel accessLevel() {
        if ((this.get().mods.flags & 1) != 0) {
            return AccessLevel.PUBLIC;
        }
        if ((this.get().mods.flags & 4) != 0) {
            return AccessLevel.PROTECTED;
        }
        if ((this.get().mods.flags & 2) != 0) {
            return AccessLevel.PRIVATE;
        }
        return AccessLevel.PACKAGE;
    }

    @Override
    public boolean isSynchronized() {
        return (this.get().mods.flags & 32) != 0;
    }

    @Override
    public boolean isStatic() {
        return (this.get().mods.flags & 8) != 0;
    }

    @Override
    public boolean isConstructor() {
        return "<init>".equals(this.methodNode.getName());
    }

    @Override
    public boolean isAbstract() {
        return (this.get().mods.flags & 1024) != 0;
    }

    @Override
    public boolean isEmpty() {
        return this.get().body == null || this.get().body.stats.isEmpty();
    }

    @Override
    public JCTree.JCMethodDecl get() {
        return (JCTree.JCMethodDecl)this.methodNode.get();
    }

    @Override
    public JavacNode node() {
        return this.methodNode;
    }

    @Override
    public JavacNode getAnnotation(Class<? extends java.lang.annotation.Annotation> expectedType) {
        return this.getAnnotation(expectedType.getName());
    }

    @Override
    public JavacNode getAnnotation(String typeName) {
        JavacNode annotationNode = null;
        for (JavacNode child : this.node().down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !Javac.matchesType((JCTree.JCAnnotation)child.get(), typeName)) continue;
            annotationNode = child;
        }
        return annotationNode;
    }

    @Override
    public boolean hasNonFinalArgument() {
        for (JCTree.JCVariableDecl param : this.get().params) {
            if (param.mods != null && (param.mods.flags & 16) != 0) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasArguments() {
        return !this.get().params.isEmpty();
    }

    @Override
    public String name() {
        return this.node().getName();
    }

    @Override
    public JavacType surroundingType() {
        return JavacType.typeOf(this.node(), this.source);
    }

    @Override
    public java.util.List<Statement<?>> statements() {
        ArrayList methodStatements = new ArrayList();
        for (JCTree.JCStatement statement : this.get().body.stats) {
            if (this.isConstructorCall(statement)) continue;
            methodStatements.add(lombok.ast.AST.Stat((Object)statement));
        }
        return methodStatements;
    }

    private boolean isConstructorCall(JCTree.JCStatement supect) {
        if (!(supect instanceof JCTree.JCExpressionStatement)) {
            return false;
        }
        JCTree.JCExpression supectExpression = ((JCTree.JCExpressionStatement)supect).expr;
        if (!(supectExpression instanceof JCTree.JCMethodInvocation)) {
            return false;
        }
        return Is.oneOf(((JCTree.JCMethodInvocation)supectExpression).meth.toString(), "super", "this");
    }

    @Override
    public java.util.List<Annotation> annotations() {
        return this.annotations(this.get().mods);
    }

    private java.util.List<Annotation> annotations(JCTree.JCModifiers mods) {
        ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        for (JCTree.JCAnnotation annotation : mods.annotations) {
            Type type = JavacResolver.METHOD.resolveMember(this.node(), (JCTree.JCExpression)annotation);
            if (type.toString().startsWith("lombok.")) continue;
            Annotation ann = lombok.ast.AST.Annotation(lombok.ast.AST.Type((Object)annotation.annotationType));
            for (JCTree.JCExpression arg : annotation.args) {
                if (arg instanceof JCTree.JCAssign) {
                    JCTree.JCAssign assign = (JCTree.JCAssign)arg;
                    ann.withValue(assign.lhs.toString(), lombok.ast.AST.Expr((Object)assign.rhs));
                    continue;
                }
                ann.withValue(lombok.ast.AST.Expr((Object)arg));
            }
            annotations.add(ann);
        }
        return annotations;
    }

    @Override
    public /* varargs */ java.util.List<Argument> arguments(IMethod.ArgumentStyle ... style) {
        java.util.List<IMethod.ArgumentStyle> styles = As.list(style);
        ArrayList<Argument> methodArguments = new ArrayList<Argument>();
        for (JCTree.JCVariableDecl param : this.get().params) {
            TypeRef argType = styles.contains((Object)IMethod.ArgumentStyle.BOXED_TYPES) ? JavacASTUtil.boxedType(param.vartype) : lombok.ast.AST.Type((Object)param.vartype);
            Argument arg = lombok.ast.AST.Arg(argType, As.string((Object)param.name));
            if (styles.contains((Object)IMethod.ArgumentStyle.INCLUDE_ANNOTATIONS)) {
                arg.withAnnotations(this.annotations(param.mods));
            }
            methodArguments.add(arg);
        }
        return methodArguments;
    }

    @Override
    public java.util.List<TypeParam> typeParameters() {
        ArrayList<TypeParam> typeParameters = new ArrayList<TypeParam>();
        if (this.isConstructor()) {
            return typeParameters;
        }
        for (JCTree.JCTypeParameter typaram : this.get().typarams) {
            TypeParam typeParam = lombok.ast.AST.TypeParam(As.string((Object)typaram.name));
            for (JCTree.JCExpression expr : typaram.bounds) {
                typeParam.withBound(lombok.ast.AST.Type((Object)expr));
            }
            typeParameters.add(typeParam);
        }
        return typeParameters;
    }

    @Override
    public java.util.List<TypeRef> thrownExceptions() {
        ArrayList<TypeRef> thrownExceptions = new ArrayList<TypeRef>();
        for (JCTree.JCExpression thrownException : this.get().thrown) {
            thrownExceptions.add(lombok.ast.AST.Type((Object)thrownException));
        }
        return thrownExceptions;
    }

    public String toString() {
        return this.get().toString();
    }

    public static JavacMethod methodOf(JavacNode node, JCTree source) {
        JavacNode methodNode = Javac.methodNodeOf(node);
        return methodNode == null ? null : new JavacMethod(methodNode, source);
    }
}

