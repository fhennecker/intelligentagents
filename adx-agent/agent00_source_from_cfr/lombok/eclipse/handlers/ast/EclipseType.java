/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.MemberValuePair
 *  org.eclipse.jdt.internal.compiler.ast.NormalAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 */
package lombok.eclipse.handlers.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import lombok.core.util.Each;
import lombok.core.util.Is;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.Eclipse;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.ast.EclipseField;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.ast.EclipseTypeEditor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public final class EclipseType
implements IType<EclipseMethod, EclipseField, EclipseNode, ASTNode, TypeDeclaration, AbstractMethodDeclaration> {
    private final EclipseNode typeNode;
    private final ASTNode source;
    private final EclipseTypeEditor editor;

    private EclipseType(EclipseNode typeNode, ASTNode source) {
        if (!(typeNode.get() instanceof TypeDeclaration)) {
            throw new IllegalArgumentException();
        }
        this.typeNode = typeNode;
        this.source = source;
        this.editor = new EclipseTypeEditor(this, source);
    }

    public EclipseTypeEditor editor() {
        return this.editor;
    }

    @Override
    public boolean isInterface() {
        return (this.get().modifiers & 512) != 0;
    }

    @Override
    public boolean isEnum() {
        return (this.get().modifiers & 16384) != 0;
    }

    @Override
    public boolean isAnnotation() {
        return (this.get().modifiers & 8192) != 0;
    }

    @Override
    public boolean isClass() {
        return !this.isInterface() && !this.isEnum() && !this.isAnnotation();
    }

    @Override
    public boolean hasSuperClass() {
        return this.get().superclass != null;
    }

    @Override
    public <T extends IType<?, ?, ?, ?, ?, ?>> T memberType(String typeName) {
        for (EclipseNode child : this.node().down()) {
            if (child.getKind() != AST.Kind.TYPE || !child.getName().equals(typeName)) continue;
            return (T)((IType)Cast.uncheckedCast(EclipseType.typeOf(child, this.source)));
        }
        throw new IllegalArgumentException();
    }

    @Override
    public <T extends IType<?, ?, ?, ?, ?, ?>> T surroundingType() {
        EclipseNode parent = (EclipseNode)this.node().directUp();
        if (parent == null) {
            return null;
        }
        return (T)((IType)Cast.uncheckedCast(EclipseType.typeOf(parent, this.source)));
    }

    @Override
    public List<EclipseMethod> methods() {
        ArrayList<EclipseMethod> methods = new ArrayList<EclipseMethod>();
        for (EclipseNode child : this.node().down()) {
            if (child.getKind() != AST.Kind.METHOD) continue;
            methods.add(EclipseMethod.methodOf(child, this.source));
        }
        return methods;
    }

    @Override
    public List<EclipseField> fields() {
        ArrayList<EclipseField> fields = new ArrayList<EclipseField>();
        for (EclipseNode child : this.node().down()) {
            if (child.getKind() != AST.Kind.FIELD) continue;
            fields.add(EclipseField.fieldOf(child, this.source));
        }
        return fields;
    }

    @Override
    public boolean hasMultiArgumentConstructor() {
        for (AbstractMethodDeclaration def : Each.elementIn(this.get().methods)) {
            if (!(def instanceof ConstructorDeclaration) || !Is.notEmpty(def.arguments)) continue;
            return true;
        }
        return false;
    }

    @Override
    public TypeDeclaration get() {
        return (TypeDeclaration)this.typeNode.get();
    }

    @Override
    public EclipseNode node() {
        return this.typeNode;
    }

    @Override
    public <A extends java.lang.annotation.Annotation> AnnotationValues<A> getAnnotationValue(Class<A> expectedType) {
        LombokNode node = this.getAnnotation(expectedType);
        return node == null ? null : EclipseHandlerUtil.createAnnotation(expectedType, (EclipseNode)node);
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
    public String name() {
        return this.node().getName();
    }

    @Override
    public List<TypeRef> typeArguments() {
        ArrayList<TypeRef> typeArguments = new ArrayList<TypeRef>();
        for (TypeParameter typaram : Each.elementIn(this.get().typeParameters)) {
            typeArguments.add(lombok.ast.AST.Type(As.string(typaram.name)));
        }
        return typeArguments;
    }

    @Override
    public List<TypeParam> typeParameters() {
        ArrayList<TypeParam> typeParameters = new ArrayList<TypeParam>();
        for (TypeParameter typaram : Each.elementIn(this.get().typeParameters)) {
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
    public boolean hasField(String fieldName) {
        return EclipseHandlerUtil.fieldExists(fieldName, this.typeNode) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS;
    }

    @Override
    public /* varargs */ boolean hasMethod(String methodName, TypeRef ... argumentTypes) {
        return EclipseHandlerUtil.methodExists(methodName, this.typeNode, false, argumentTypes == null ? 0 : argumentTypes.length) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS;
    }

    @Override
    public /* varargs */ boolean hasMethodIncludingSupertypes(String methodName, TypeRef ... argumentTypes) {
        return this.hasMethod((TypeBinding)this.get().binding, methodName, this.editor().build(As.list(argumentTypes)));
    }

    private boolean hasMethod(TypeBinding binding, String methodName, List<ASTNode> argumentTypes) {
        if (binding instanceof ReferenceBinding) {
            ReferenceBinding rb = (ReferenceBinding)binding;
            MethodBinding[] availableMethods = rb.availableMethods();
            for (MethodBinding method : Each.elementIn(availableMethods)) {
                if (method.isAbstract() || !method.isPublic() || !methodName.equals(As.string(method.selector)) || argumentTypes.size() != As.list(method.parameters).size()) continue;
                return true;
            }
            ReferenceBinding superclass = rb.superclass();
            Eclipse.ensureAllClassScopeMethodWereBuild((TypeBinding)superclass);
            return this.hasMethod((TypeBinding)superclass, methodName, argumentTypes);
        }
        return false;
    }

    public String toString() {
        return this.get().toString();
    }

    public static EclipseType typeOf(EclipseNode node, ASTNode source) {
        EclipseNode typeNode = Eclipse.typeNodeOf(node);
        return typeNode == null ? null : new EclipseType(typeNode, source);
    }
}

