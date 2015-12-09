/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.NameReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope
 *  org.eclipse.jdt.internal.compiler.lookup.InvocationSite
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 *  org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 *  org.eclipse.jdt.internal.compiler.problem.ProblemReporter
 */
package lombok.eclipse.agent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.ExtensionMethod;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.TransformEclipseAST;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PatchExtensionMethod {
    private static final Map<MessageSend, PostponedError> ERRORS = new WeakHashMap<MessageSend, PostponedError>();

    public static EclipseNode getTypeNode(TypeDeclaration decl) {
        CompilationUnitDeclaration cud = decl.scope.compilationUnitScope().referenceContext;
        EclipseAST astNode = TransformEclipseAST.getAST(cud, false);
        EclipseNode node = (EclipseNode)astNode.get(decl);
        if (node == null) {
            astNode = TransformEclipseAST.getAST(cud, true);
            node = (EclipseNode)astNode.get(decl);
        }
        return node;
    }

    public static Annotation getAnnotation(Class<? extends java.lang.annotation.Annotation> expectedType, EclipseNode node) {
        TypeDeclaration decl = (TypeDeclaration)node.get();
        if (decl.annotations != null) {
            for (Annotation ann : decl.annotations) {
                if (!EclipseHandlerUtil.typeMatches(expectedType, node, ann.type)) continue;
                return ann;
            }
        }
        return null;
    }

    static EclipseNode upToType(EclipseNode typeNode) {
        EclipseNode node = typeNode;
        while ((node = (EclipseNode)node.up()) != null && node.getKind() != AST.Kind.TYPE) {
        }
        return node;
    }

    static List<Extension> getApplicableExtensionMethods(EclipseNode typeNode, Annotation ann, TypeBinding receiverType) {
        ArrayList<Extension> extensions = new ArrayList<Extension>();
        if (typeNode != null && ann != null && receiverType != null) {
            MethodScope blockScope = ((TypeDeclaration)typeNode.get()).initializerScope;
            EclipseNode annotationNode = (EclipseNode)typeNode.getNodeFor(ann);
            AnnotationValues annotation = EclipseHandlerUtil.createAnnotation(ExtensionMethod.class, annotationNode);
            boolean suppressBaseMethods = false;
            try {
                suppressBaseMethods = ((ExtensionMethod)annotation.getInstance()).suppressBaseMethods();
            }
            catch (AnnotationValues.AnnotationValueDecodeFail fail) {
                fail.owner.setError(fail.getMessage(), fail.idx);
            }
            for (Object extensionMethodProvider : annotation.getActualExpressions("value")) {
                TypeBinding binding;
                if (!(extensionMethodProvider instanceof ClassLiteralAccess) || (binding = ((ClassLiteralAccess)extensionMethodProvider).type.resolveType((BlockScope)blockScope)) == null || !binding.isClass() && !binding.isEnum()) continue;
                Extension e = new Extension();
                e.extensionMethods = PatchExtensionMethod.getApplicableExtensionMethodsDefinedInProvider(typeNode, (ReferenceBinding)binding, receiverType);
                e.suppressBaseMethods = suppressBaseMethods;
                extensions.add(e);
            }
        }
        return extensions;
    }

    private static List<MethodBinding> getApplicableExtensionMethodsDefinedInProvider(EclipseNode typeNode, ReferenceBinding extensionMethodProviderBinding, TypeBinding receiverType) {
        ArrayList<MethodBinding> extensionMethods = new ArrayList<MethodBinding>();
        CompilationUnitScope cuScope = ((CompilationUnitDeclaration)((EclipseNode)typeNode.top()).get()).scope;
        for (MethodBinding method : extensionMethodProviderBinding.methods()) {
            TypeBinding firstArgType;
            if (!method.isStatic() || !method.isPublic() || method.parameters == null || method.parameters.length == 0 || receiverType.isProvablyDistinct(firstArgType = method.parameters[0]) && !receiverType.isCompatibleWith(firstArgType.erasure())) continue;
            TypeBinding[] argumentTypes = Arrays.copyOfRange(method.parameters, 1, method.parameters.length);
            if (receiverType instanceof ReferenceBinding && ((ReferenceBinding)receiverType).getExactMethod(method.selector, argumentTypes, cuScope) != null) continue;
            extensionMethods.add(method);
        }
        return extensionMethods;
    }

    public static void errorNoMethodFor(ProblemReporter problemReporter, MessageSend messageSend, TypeBinding recType, TypeBinding[] params) {
        ERRORS.put(messageSend, new PostponedNoMethodError(problemReporter, messageSend, recType, params));
    }

    public static void invalidMethod(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method) {
        ERRORS.put(messageSend, new PostponedInvalidMethodError(problemReporter, messageSend, method));
    }

    public static TypeBinding resolveType(TypeBinding resolvedType, MessageSend methodCall, BlockScope scope) {
        ArrayList<Extension> extensions = new ArrayList<Extension>();
        TypeDeclaration decl = scope.classScope().referenceContext;
        EclipseNode owningType = null;
        EclipseNode typeNode = PatchExtensionMethod.getTypeNode(decl);
        while (typeNode != null) {
            Annotation ann = PatchExtensionMethod.getAnnotation(ExtensionMethod.class, typeNode);
            if (ann != null) {
                extensions.addAll(0, PatchExtensionMethod.getApplicableExtensionMethods(typeNode, ann, methodCall.receiver.resolvedType));
                if (owningType == null) {
                    owningType = typeNode;
                }
            }
            typeNode = PatchExtensionMethod.upToType(typeNode);
        }
        for (Extension extension : extensions) {
            if (!extension.suppressBaseMethods && !(methodCall.binding instanceof ProblemMethodBinding)) continue;
            for (MethodBinding extensionMethod : extension.extensionMethods) {
                if (!Arrays.equals(methodCall.selector, extensionMethod.selector)) continue;
                ERRORS.remove((Object)methodCall);
                if (methodCall.receiver instanceof ThisReference) {
                    methodCall.receiver.bits &= -5;
                }
                ArrayList<Expression> arguments = new ArrayList<Expression>();
                arguments.add(methodCall.receiver);
                if (methodCall.arguments != null) {
                    arguments.addAll(Arrays.asList(methodCall.arguments));
                }
                ArrayList<TypeBinding> argumentTypes = new ArrayList<TypeBinding>();
                for (Expression argument : arguments) {
                    argumentTypes.add(argument.resolvedType);
                }
                MethodBinding fixedBinding = scope.getMethod((TypeBinding)extensionMethod.declaringClass, methodCall.selector, argumentTypes.toArray((T[])new TypeBinding[0]), (InvocationSite)methodCall);
                if (fixedBinding instanceof ProblemMethodBinding) {
                    if (fixedBinding.declaringClass != null) {
                        scope.problemReporter().invalidMethod(methodCall, fixedBinding);
                    }
                } else {
                    int iend = arguments.size();
                    for (int i = 0; i < iend; ++i) {
                        int id;
                        Expression arg = (Expression)arguments.get(i);
                        if (fixedBinding.parameters[i].isArrayType() != arg.resolvedType.isArrayType()) break;
                        if (arg.resolvedType.isArrayType() && arg instanceof MessageSend) {
                            ((MessageSend)arg).valueCast = arg.resolvedType;
                        }
                        if (!fixedBinding.parameters[i].isBaseType() && arg.resolvedType.isBaseType()) {
                            id = arg.resolvedType.id;
                            arg.implicitConversion = 512 | id + (id << 4);
                            continue;
                        }
                        if (!fixedBinding.parameters[i].isBaseType() || arg.resolvedType.isBaseType()) continue;
                        id = fixedBinding.parameters[i].id;
                        arg.implicitConversion = 1024 | id + (id << 4);
                    }
                    methodCall.arguments = arguments.toArray((T[])new Expression[0]);
                    methodCall.receiver = PatchExtensionMethod.createNameRef((TypeBinding)extensionMethod.declaringClass, (ASTNode)methodCall);
                    methodCall.actualReceiverType = extensionMethod.declaringClass;
                    methodCall.binding = fixedBinding;
                    methodCall.resolvedType = methodCall.binding.returnType;
                }
                return methodCall.resolvedType;
            }
        }
        PostponedError error = ERRORS.get((Object)methodCall);
        if (error != null) {
            error.fire();
        }
        ERRORS.remove((Object)methodCall);
        return resolvedType;
    }

    private static NameReference createNameRef(TypeBinding typeBinding, ASTNode source) {
        long p = (long)source.sourceStart << 32 | (long)source.sourceEnd;
        char[] pkg = typeBinding.qualifiedPackageName();
        char[] basename = typeBinding.qualifiedSourceName();
        StringBuilder sb = new StringBuilder();
        if (pkg != null) {
            sb.append(pkg);
        }
        if (sb.length() > 0) {
            sb.append(".");
        }
        sb.append(basename);
        String tName = sb.toString();
        if (tName.indexOf(46) == -1) {
            return new SingleNameReference(basename, p);
        }
        String[] in = tName.split("\\.");
        char[][] sources = new char[in.length][];
        for (int i = 0; i < in.length; ++i) {
            sources[i] = in[i].toCharArray();
        }
        long[] poss = new long[in.length];
        Arrays.fill(poss, p);
        return new QualifiedNameReference((char[][])sources, poss, source.sourceStart, source.sourceEnd);
    }

    private static interface PostponedError {
        public void fire();
    }

    private static class PostponedInvalidMethodError
    implements PostponedError {
        private final ProblemReporter problemReporter;
        private final WeakReference<MessageSend> messageSendRef;
        private final MethodBinding method;

        PostponedInvalidMethodError(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method) {
            this.problemReporter = problemReporter;
            this.messageSendRef = new WeakReference<MessageSend>(messageSend);
            this.method = method;
        }

        public void fire() {
            MessageSend messageSend = this.messageSendRef.get();
            if (messageSend != null) {
                this.problemReporter.invalidMethod(messageSend, this.method);
            }
        }
    }

    private static class PostponedNoMethodError
    implements PostponedError {
        private final ProblemReporter problemReporter;
        private final WeakReference<MessageSend> messageSendRef;
        private final TypeBinding recType;
        private final TypeBinding[] params;

        PostponedNoMethodError(ProblemReporter problemReporter, MessageSend messageSend, TypeBinding recType, TypeBinding[] params) {
            this.problemReporter = problemReporter;
            this.messageSendRef = new WeakReference<MessageSend>(messageSend);
            this.recType = recType;
            this.params = params;
        }

        public void fire() {
            MessageSend messageSend = this.messageSendRef.get();
            if (messageSend != null) {
                this.problemReporter.errorNoMethodFor(messageSend, this.recType, this.params);
            }
        }
    }

    static class Extension {
        List<MethodBinding> extensionMethods;
        boolean suppressBaseMethods;

        Extension() {
        }
    }

}

