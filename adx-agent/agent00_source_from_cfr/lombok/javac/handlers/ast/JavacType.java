/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$ClassSymbol
 *  com.sun.tools.javac.code.Symbol$MethodSymbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.Type$MethodType
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCTypeParameter
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers.ast;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import java.util.Collection;
import lombok.ast.Annotation;
import lombok.ast.Expression;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.Node;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.util.As;
import lombok.core.util.Cast;
import lombok.javac.JavacNode;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacField;
import lombok.javac.handlers.ast.JavacMethod;
import lombok.javac.handlers.ast.JavacTypeEditor;

public final class JavacType
implements IType<JavacMethod, JavacField, JavacNode, JCTree, JCTree.JCClassDecl, JCTree.JCMethodDecl> {
    private final JavacNode typeNode;
    private final JCTree source;
    private final JavacTypeEditor editor;

    private JavacType(JavacNode typeNode, JCTree source) {
        if (!(typeNode.get() instanceof JCTree.JCClassDecl)) {
            throw new IllegalArgumentException();
        }
        this.typeNode = typeNode;
        this.source = source;
        this.editor = new JavacTypeEditor(this, source);
    }

    public JavacTypeEditor editor() {
        return this.editor;
    }

    @Override
    public boolean isInterface() {
        return (this.get().mods.flags & 512) != 0;
    }

    @Override
    public boolean isEnum() {
        return (this.get().mods.flags & 16384) != 0;
    }

    @Override
    public boolean isAnnotation() {
        return (this.get().mods.flags & 8192) != 0;
    }

    @Override
    public boolean isClass() {
        return !this.isInterface() && !this.isEnum() && !this.isAnnotation();
    }

    @Override
    public boolean hasSuperClass() {
        return this.get().getExtendsClause() != null;
    }

    @Override
    public <A extends java.lang.annotation.Annotation> AnnotationValues<A> getAnnotationValue(Class<A> expectedType) {
        LombokNode node = this.getAnnotation(expectedType);
        return node == null ? null : JavacHandlerUtil.createAnnotation(expectedType, (JavacNode)node);
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
    public <T extends IType<?, ?, ?, ?, ?, ?>> T memberType(String typeName) {
        for (JavacNode child : this.node().down()) {
            if (child.getKind() != AST.Kind.TYPE || !child.getName().equals(typeName)) continue;
            return (T)((IType)Cast.uncheckedCast(JavacType.typeOf(child, this.source)));
        }
        throw new IllegalArgumentException();
    }

    @Override
    public <T extends IType<?, ?, ?, ?, ?, ?>> T surroundingType() {
        JavacNode parent = (JavacNode)this.node().directUp();
        if (parent == null) {
            return null;
        }
        return (T)((IType)Cast.uncheckedCast(JavacType.typeOf(parent, this.source)));
    }

    @Override
    public java.util.List<JavacMethod> methods() {
        ArrayList<JavacMethod> methods = new ArrayList<JavacMethod>();
        for (JavacNode child : this.node().down()) {
            if (child.getKind() != AST.Kind.METHOD) continue;
            methods.add(JavacMethod.methodOf(child, this.source));
        }
        return methods;
    }

    @Override
    public java.util.List<JavacField> fields() {
        ArrayList<JavacField> fields = new ArrayList<JavacField>();
        for (JavacNode child : this.node().down()) {
            if (child.getKind() != AST.Kind.FIELD) continue;
            fields.add(JavacField.fieldOf(child, this.source));
        }
        return fields;
    }

    @Override
    public boolean hasMultiArgumentConstructor() {
        for (JCTree def : this.get().defs) {
            if (!(def instanceof JCTree.JCMethodDecl) || ((JCTree.JCMethodDecl)def).params.isEmpty()) continue;
            return true;
        }
        return false;
    }

    @Override
    public JCTree.JCClassDecl get() {
        return (JCTree.JCClassDecl)this.typeNode.get();
    }

    @Override
    public JavacNode node() {
        return this.typeNode;
    }

    @Override
    public String name() {
        return this.node().getName();
    }

    @Override
    public java.util.List<TypeRef> typeArguments() {
        ArrayList<TypeRef> typeArguments = new ArrayList<TypeRef>();
        for (JCTree.JCTypeParameter typaram : this.get().typarams) {
            typeArguments.add(lombok.ast.AST.Type(As.string((Object)typaram.name)));
        }
        return typeArguments;
    }

    @Override
    public java.util.List<TypeParam> typeParameters() {
        ArrayList<TypeParam> typeParameters = new ArrayList<TypeParam>();
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
    public java.util.List<Annotation> annotations() {
        return this.annotations(this.get().mods);
    }

    private java.util.List<Annotation> annotations(JCTree.JCModifiers mods) {
        ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        for (JCTree.JCAnnotation annotation : mods.annotations) {
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
    public boolean hasField(String fieldName) {
        return JavacHandlerUtil.fieldExists(fieldName, this.typeNode) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS;
    }

    @Override
    public /* varargs */ boolean hasMethod(String methodName, TypeRef ... argumentTypes) {
        return JavacHandlerUtil.methodExists(methodName, this.typeNode, false, argumentTypes == null ? 0 : argumentTypes.length) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS;
    }

    @Override
    public /* varargs */ boolean hasMethodIncludingSupertypes(String methodName, TypeRef ... argumentTypes) {
        return this.hasMethod((Symbol.TypeSymbol)this.get().sym, methodName, this.editor().build(As.list(argumentTypes)));
    }

    private boolean hasMethod(Symbol.TypeSymbol type, String methodName, java.util.List<JCTree> argumentTypes) {
        if (type == null) {
            return false;
        }
        for (Symbol enclosedElement : type.getEnclosedElements()) {
            if (!(enclosedElement instanceof Symbol.MethodSymbol) || (enclosedElement.flags() & 1024) != 0 || (enclosedElement.flags() & 1) == 0) continue;
            Symbol.MethodSymbol method = (Symbol.MethodSymbol)enclosedElement;
            if (!methodName.equals(As.string((Object)method.name))) continue;
            Type.MethodType methodType = (Type.MethodType)method.type;
            if (argumentTypes.size() != methodType.argtypes.size()) continue;
            return true;
        }
        Type supertype = ((Symbol.ClassSymbol)type).getSuperclass();
        return this.hasMethod(supertype.tsym, methodName, argumentTypes);
    }

    public String toString() {
        return this.get().toString();
    }

    public static JavacType typeOf(JavacNode node, JCTree source) {
        JavacNode typeNode = Javac.typeNodeOf(node);
        return typeNode == null ? null : new JavacType(typeNode, source);
    }
}

