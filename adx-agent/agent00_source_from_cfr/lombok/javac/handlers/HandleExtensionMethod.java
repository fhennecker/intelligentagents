/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.MethodInvocationTree
 *  com.sun.source.tree.TreeVisitor
 *  com.sun.source.util.TreeScanner
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$ClassSymbol
 *  com.sun.tools.javac.code.Symbol$MethodSymbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.Type$ClassType
 *  com.sun.tools.javac.code.Type$ErrorType
 *  com.sun.tools.javac.code.Type$ForAll
 *  com.sun.tools.javac.code.Type$MethodType
 *  com.sun.tools.javac.code.Types
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import javax.lang.model.element.ElementKind;
import lombok.ExtensionMethod;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAST;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.ResolutionBased;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacResolver;

@ResolutionBased
public class HandleExtensionMethod
extends JavacAnnotationHandler<ExtensionMethod> {
    @Override
    public void handle(AnnotationValues<ExtensionMethod> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, ExtensionMethod.class);
        JavacNode typeNode = (JavacNode)annotationNode.up();
        boolean isClassOrEnum = JavacHandlerUtil.isClassOrEnum(typeNode);
        if (!isClassOrEnum) {
            annotationNode.addError("@ExtensionMethod can only be used on a class or an enum");
            return;
        }
        boolean suppressBaseMethods = annotation.getInstance().suppressBaseMethods();
        java.util.List<Object> extensionProviders = annotation.getActualExpressions("value");
        if (extensionProviders.isEmpty()) {
            annotationNode.addError(String.format("@%s has no effect since no extension types were specified.", ExtensionMethod.class.getName()));
            return;
        }
        java.util.List<Extension> extensions = this.getExtensions(annotationNode, extensionProviders);
        if (extensions.isEmpty()) {
            return;
        }
        new ExtensionMethodReplaceVisitor(annotationNode, extensions, suppressBaseMethods).replace();
        annotationNode.rebuild();
    }

    private java.util.List<Extension> getExtensions(JavacNode typeNode, java.util.List<Object> extensionProviders) {
        ArrayList<Extension> extensions = new ArrayList<Extension>();
        for (Object extensionProvider : extensionProviders) {
            Type providerType;
            if (!(extensionProvider instanceof JCTree.JCFieldAccess)) continue;
            JCTree.JCFieldAccess provider = (JCTree.JCFieldAccess)extensionProvider;
            if (!"class".equals(provider.name.toString()) || (providerType = JavacResolver.CLASS.resolveMember(typeNode, provider.selected)) == null || (providerType.tsym.flags() & 8704) != 0) continue;
            extensions.add(this.getExtension(typeNode, (Type.ClassType)providerType));
        }
        return extensions;
    }

    private Extension getExtension(JavacNode typeNode, Type.ClassType extensionMethodProviderType) {
        ArrayList<Symbol.MethodSymbol> extensionMethods = new ArrayList<Symbol.MethodSymbol>();
        Symbol.TypeSymbol tsym = extensionMethodProviderType.asElement();
        if (tsym != null) {
            for (Symbol member : tsym.getEnclosedElements()) {
                Symbol.MethodSymbol method;
                if (member.getKind() != ElementKind.METHOD || ((method = (Symbol.MethodSymbol)member).flags() & 9) == 0 || method.params().isEmpty()) continue;
                extensionMethods.add(method);
            }
        }
        return new Extension(extensionMethods, tsym);
    }

    private static class ExtensionMethodReplaceVisitor
    extends TreeScanner<Void, Void> {
        final JavacNode annotationNode;
        final java.util.List<Extension> extensions;
        final boolean suppressBaseMethods;

        public ExtensionMethodReplaceVisitor(JavacNode annotationNode, java.util.List<Extension> extensions, boolean suppressBaseMethods) {
            this.annotationNode = annotationNode;
            this.extensions = extensions;
            this.suppressBaseMethods = suppressBaseMethods;
        }

        public void replace() {
            ((JCTree)((JavacNode)this.annotationNode.up()).get()).accept((TreeVisitor)this, (Object)null);
        }

        public Void visitMethodInvocation(MethodInvocationTree tree, Void p) {
            this.handleMethodCall((JCTree.JCMethodInvocation)tree);
            return (Void)super.visitMethodInvocation(tree, (Object)p);
        }

        private void handleMethodCall(JCTree.JCMethodInvocation methodCall) {
            JavacNode methodCallNode = (JavacNode)((JavacAST)this.annotationNode.getAst()).get(methodCall);
            JavacNode surroundingType = JavacHandlerUtil.upToTypeNode(methodCallNode);
            Symbol.ClassSymbol surroundingTypeSymbol = ((JCTree.JCClassDecl)surroundingType.get()).sym;
            JCTree.JCExpression receiver = this.receiverOf(methodCall);
            String methodName = this.methodNameOf(methodCall);
            if ("this".equals(methodName) || "super".equals(methodName)) {
                return;
            }
            Type resolvedMethodCall = JavacResolver.CLASS_AND_METHOD.resolveMember(methodCallNode, (JCTree.JCExpression)methodCall);
            if (resolvedMethodCall == null) {
                return;
            }
            if (!this.suppressBaseMethods && !(resolvedMethodCall instanceof Type.ErrorType)) {
                return;
            }
            Type receiverType = JavacResolver.CLASS_AND_METHOD.resolveMember(methodCallNode, receiver);
            if (receiverType == null) {
                return;
            }
            if (receiverType.tsym.toString().endsWith(receiver.toString())) {
                return;
            }
            Types types = Types.instance((Context)this.annotationNode.getContext());
            for (Extension extension : this.extensions) {
                Symbol.TypeSymbol extensionProvider = extension.extensionProvider;
                if (surroundingTypeSymbol == extensionProvider) continue;
                for (Symbol.MethodSymbol extensionMethod : extension.extensionMethods) {
                    Type firstArgType;
                    Type extensionMethodType;
                    if (!methodName.equals(extensionMethod.name.toString()) || !Type.MethodType.class.isInstance((Object)(extensionMethodType = extensionMethod.type)) && !Type.ForAll.class.isInstance((Object)extensionMethodType) || !types.isAssignable(receiverType, firstArgType = types.erasure((Type)extensionMethodType.asMethodType().argtypes.get(0)))) continue;
                    methodCall.args = methodCall.args.prepend((Object)receiver);
                    methodCall.meth = JavacHandlerUtil.chainDotsString(this.annotationNode, extensionProvider.toString() + "." + methodName);
                    return;
                }
            }
        }

        private String methodNameOf(JCTree.JCMethodInvocation methodCall) {
            if (methodCall.meth instanceof JCTree.JCIdent) {
                return ((JCTree.JCIdent)methodCall.meth).name.toString();
            }
            return ((JCTree.JCFieldAccess)methodCall.meth).name.toString();
        }

        private JCTree.JCExpression receiverOf(JCTree.JCMethodInvocation methodCall) {
            if (methodCall.meth instanceof JCTree.JCIdent) {
                return this.annotationNode.getTreeMaker().Ident(this.annotationNode.toName("this"));
            }
            return ((JCTree.JCFieldAccess)methodCall.meth).selected;
        }
    }

    private static class Extension {
        final java.util.List<Symbol.MethodSymbol> extensionMethods;
        final Symbol.TypeSymbol extensionProvider;

        public Extension(java.util.List<Symbol.MethodSymbol> extensionMethods, Symbol.TypeSymbol extensionProvider) {
            this.extensionMethods = extensionMethods;
            this.extensionProvider = extensionProvider;
        }
    }

}

