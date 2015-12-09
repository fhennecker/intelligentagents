/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Initializer
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.impl.ReferenceContext
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 *  org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding
 */
package lombok.eclipse.handlers.ast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import lombok.ast.AbstractMethodDecl;
import lombok.ast.ClassDecl;
import lombok.ast.ConstructorDecl;
import lombok.ast.EnumConstant;
import lombok.ast.FieldDecl;
import lombok.ast.IMethod;
import lombok.ast.ITypeEditor;
import lombok.ast.MethodDecl;
import lombok.ast.Modifier;
import lombok.ast.Node;
import lombok.core.util.Arrays;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.Eclipse;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.ast.EclipseASTMaker;
import lombok.eclipse.handlers.ast.EclipseMethod;
import lombok.eclipse.handlers.ast.EclipseType;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;

public final class EclipseTypeEditor
implements ITypeEditor<EclipseMethod, ASTNode, TypeDeclaration, AbstractMethodDeclaration> {
    private final EclipseType type;
    private final EclipseASTMaker builder;

    EclipseTypeEditor(EclipseType type, ASTNode source) {
        this.type = type;
        this.builder = new EclipseASTMaker(type.node(), source);
    }

    public TypeDeclaration get() {
        return this.type.get();
    }

    public EclipseNode node() {
        return this.type.node();
    }

    @Override
    public <T extends ASTNode> T build(Node<?> node) {
        return this.builder.build(node);
    }

    @Override
    public <T extends ASTNode> T build(Node<?> node, Class<T> extectedType) {
        return this.builder.build(node, extectedType);
    }

    @Override
    public <T extends ASTNode> List<T> build(List<? extends Node<?>> nodes) {
        return this.builder.build(nodes);
    }

    @Override
    public <T extends ASTNode> List<T> build(List<? extends Node<?>> nodes, Class<T> extectedType) {
        return this.builder.build(nodes, extectedType);
    }

    @Override
    public void injectInitializer(lombok.ast.Initializer initializer) {
        Initializer initializerBlock = (Initializer)this.builder.build(initializer);
        Eclipse.injectInitializer(this.node(), initializerBlock);
    }

    @Override
    public void injectField(FieldDecl fieldDecl) {
        FieldDeclaration field = (FieldDeclaration)this.builder.build(fieldDecl);
        EclipseHandlerUtil.injectField(this.node(), field);
    }

    @Override
    public void injectField(EnumConstant enumConstant) {
        FieldDeclaration field = (FieldDeclaration)this.builder.build(enumConstant);
        EclipseHandlerUtil.injectField(this.node(), field);
    }

    @Override
    public AbstractMethodDeclaration injectMethod(MethodDecl methodDecl) {
        return (MethodDeclaration)this.injectMethodImpl(methodDecl);
    }

    @Override
    public AbstractMethodDeclaration injectConstructor(ConstructorDecl constructorDecl) {
        return (ConstructorDeclaration)this.injectMethodImpl(constructorDecl);
    }

    private AbstractMethodDeclaration injectMethodImpl(AbstractMethodDecl<?> methodDecl) {
        AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.builder.build(methodDecl, MethodDeclaration.class);
        EclipseHandlerUtil.injectMethod(this.node(), method);
        TypeDeclaration type = this.get();
        if (type.scope != null && method.scope == null) {
            boolean aboutToBeResolved = false;
            for (StackTraceElement elem : Thread.currentThread().getStackTrace()) {
                if (!"org.eclipse.jdt.internal.compiler.lookup.ClassScope".equals(elem.getClassName()) || !"buildFieldsAndMethods".equals(elem.getMethodName())) continue;
                aboutToBeResolved = true;
                break;
            }
            if (!aboutToBeResolved) {
                MethodScope scope = new MethodScope(type.scope, (ReferenceContext)method, methodDecl.getModifiers().contains((Object)Modifier.STATIC));
                MethodBinding methodBinding = null;
                try {
                    methodBinding = (MethodBinding)Reflection.methodScopeCreateMethodMethod.invoke((Object)scope, new Object[]{method});
                }
                catch (Exception e) {
                    // empty catch block
                }
                if (methodBinding != null) {
                    SourceTypeBinding sourceType = type.scope.referenceContext.binding;
                    MethodBinding[] methods = sourceType.methods();
                    methods = Arrays.resize(methods, methods.length + 1);
                    methods[methods.length - 1] = methodBinding;
                    sourceType.setMethods(methods);
                    sourceType.resolveTypesFor(methodBinding);
                }
            }
        }
        return method;
    }

    @Override
    public void injectType(ClassDecl typeDecl) {
        TypeDeclaration type = (TypeDeclaration)this.builder.build(typeDecl);
        Eclipse.injectType(this.node(), type);
    }

    @Override
    public void removeMethod(EclipseMethod method) {
        TypeDeclaration type = this.get();
        ArrayList<AbstractMethodDeclaration> methods = new ArrayList<AbstractMethodDeclaration>();
        for (AbstractMethodDeclaration decl : type.methods) {
            if (decl.equals((Object)method.get())) continue;
            methods.add(decl);
        }
        type.methods = methods.toArray((T[])new AbstractMethodDeclaration[0]);
        this.node().removeChild(method.node());
    }

    @Override
    public void makeEnum() {
        this.get().modifiers |= 16384;
    }

    @Override
    public void makePrivate() {
        this.makePackagePrivate();
        this.get().modifiers |= 2;
    }

    @Override
    public void makePackagePrivate() {
        this.get().modifiers &= -8;
    }

    @Override
    public void makeProtected() {
        this.makePackagePrivate();
        this.get().modifiers |= 4;
    }

    @Override
    public void makePublic() {
        this.makePackagePrivate();
        this.get().modifiers |= 1;
    }

    @Override
    public void makeStatic() {
        this.get().modifiers |= 8;
    }

    @Override
    public void rebuild() {
        this.node().rebuild();
    }

    public String toString() {
        return this.get().toString();
    }

    private static final class Reflection {
        public static final Method methodScopeCreateMethodMethod;

        private Reflection() {
        }

        static {
            Method m = null;
            try {
                m = MethodScope.class.getDeclaredMethod("createMethod", AbstractMethodDeclaration.class);
                m.setAccessible(true);
            }
            catch (Exception e) {
                // empty catch block
            }
            methodScopeCreateMethodMethod = m;
        }
    }

}

