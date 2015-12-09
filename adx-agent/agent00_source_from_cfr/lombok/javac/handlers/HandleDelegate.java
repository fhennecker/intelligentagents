/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.Type$ClassType
 *  com.sun.tools.javac.code.Type$TypeVar
 *  com.sun.tools.javac.code.Types
 *  com.sun.tools.javac.model.JavacTypes
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCTypeParameter
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import lombok.Delegate;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.javac.FindTypeVarScanner;
import lombok.javac.JavacAST;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacResolution;
import lombok.javac.ResolutionBased;
import lombok.javac.handlers.JavacHandlerUtil;

@ResolutionBased
public class HandleDelegate
extends JavacAnnotationHandler<Delegate> {
    private static final java.util.List<String> METHODS_IN_OBJECT = Collections.unmodifiableList(Arrays.asList("hashCode()", "canEqual(java.lang.Object)", "equals(java.lang.Object)", "wait()", "wait(long)", "wait(long, int)", "notify()", "notifyAll()", "toString()", "getClass()", "clone()", "finalize()"));

    @Override
    public void handle(AnnotationValues<Delegate> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        Type delegateType;
        Type type;
        DelegateReceiver delegateReceiver;
        Type.ClassType ct;
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Delegate.class);
        Name delegateName = annotationNode.toName(((JavacNode)annotationNode.up()).getName());
        JavacResolution reso = new JavacResolution(annotationNode.getContext());
        if (((JavacNode)annotationNode.up()).getKind() == AST.Kind.FIELD) {
            delegateReceiver = DelegateReceiver.FIELD;
            delegateType = ((JCTree)((JavacNode)annotationNode.up()).get()).type;
            if (delegateType == null) {
                reso.resolveClassMember((JavacNode)annotationNode.up());
            }
            delegateType = ((JCTree)((JavacNode)annotationNode.up()).get()).type;
        } else if (((JavacNode)annotationNode.up()).getKind() == AST.Kind.METHOD) {
            if (!(((JavacNode)annotationNode.up()).get() instanceof JCTree.JCMethodDecl)) {
                annotationNode.addError("@Delegate is legal only on no-argument methods.");
                return;
            }
            JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl)((JavacNode)annotationNode.up()).get();
            if (!methodDecl.params.isEmpty()) {
                annotationNode.addError("@Delegate is legal only on no-argument methods.");
                return;
            }
            delegateReceiver = DelegateReceiver.METHOD;
            delegateType = methodDecl.restype.type;
            if (delegateType == null) {
                reso.resolveClassMember((JavacNode)annotationNode.up());
            }
            delegateType = methodDecl.restype.type;
        } else {
            return;
        }
        java.util.List<Object> delegateTypes = annotation.getActualExpressions("types");
        java.util.List<Object> excludeTypes = annotation.getActualExpressions("excludes");
        ArrayList<Type> toDelegate = new ArrayList<Type>();
        ArrayList<Type> toExclude = new ArrayList<Type>();
        if (delegateTypes.isEmpty()) {
            if (delegateType != null) {
                toDelegate.add(delegateType);
            }
        } else {
            for (Object dt : delegateTypes) {
                if (!(dt instanceof JCTree.JCFieldAccess) || !((JCTree.JCFieldAccess)dt).name.toString().equals("class")) continue;
                type = ((JCTree.JCFieldAccess)dt).selected.type;
                if (type == null) {
                    reso.resolveClassMember(annotationNode);
                }
                if ((type = ((JCTree.JCFieldAccess)dt).selected.type) == null) continue;
                toDelegate.add(type);
            }
        }
        for (Object et : excludeTypes) {
            if (!(et instanceof JCTree.JCFieldAccess) || !((JCTree.JCFieldAccess)et).name.toString().equals("class")) continue;
            type = ((JCTree.JCFieldAccess)et).selected.type;
            if (type == null) {
                reso.resolveClassMember(annotationNode);
            }
            if ((type = ((JCTree.JCFieldAccess)et).selected.type) == null) continue;
            toExclude.add(type);
        }
        ArrayList<MethodSig> signaturesToDelegate = new ArrayList<MethodSig>();
        ArrayList<MethodSig> signaturesToExclude = new ArrayList<MethodSig>();
        HashSet<String> banList = new HashSet<String>();
        banList.addAll(METHODS_IN_OBJECT);
        for (Type t2 : toExclude) {
            if (t2 instanceof Type.ClassType) {
                ct = (Type.ClassType)t2;
                this.addMethodBindings(signaturesToExclude, ct, annotationNode.getTypesUtil(), banList);
                continue;
            }
            annotationNode.addError("@Delegate can only use concrete class types, not wildcards, arrays, type variables, or primitives.");
            return;
        }
        for (MethodSig sig2 : signaturesToExclude) {
            banList.add(HandleDelegate.printSig(sig2.type, sig2.name, annotationNode.getTypesUtil()));
        }
        for (Type t2 : toDelegate) {
            if (t2 instanceof Type.ClassType) {
                ct = (Type.ClassType)t2;
                this.addMethodBindings(signaturesToDelegate, ct, annotationNode.getTypesUtil(), banList);
                continue;
            }
            annotationNode.addError("@Delegate can only use concrete class types, not wildcards, arrays, type variables, or primitives.");
            return;
        }
        for (MethodSig sig2 : signaturesToDelegate) {
            this.generateAndAdd(sig2, annotationNode, delegateName, delegateReceiver);
        }
    }

    private void generateAndAdd(MethodSig sig, JavacNode annotation, Name delegateName, DelegateReceiver delegateReceiver) {
        ArrayList<JCTree.JCMethodDecl> toAdd = new ArrayList<JCTree.JCMethodDecl>();
        try {
            toAdd.add(this.createDelegateMethod(sig, annotation, delegateName, delegateReceiver));
        }
        catch (JavacResolution.TypeNotConvertibleException e) {
            annotation.addError("Can't create delegate method for " + (Object)sig.name + ": " + e.getMessage());
            return;
        }
        catch (CantMakeDelegates e) {
            annotation.addError("There's a conflict in the names of type parameters. Fix it by renaming the following type parameters of your class: " + e.conflicted);
            return;
        }
        for (JCTree.JCMethodDecl method : toAdd) {
            JavacHandlerUtil.injectMethod((JavacNode)((JavacNode)annotation.up()).up(), method);
        }
    }

    private void checkConflictOfTypeVarNames(MethodSig sig, JavacNode annotation) throws CantMakeDelegates {
        if (sig.elem.getTypeParameters().isEmpty()) {
            return;
        }
        HashSet<String> usedInOurType = new HashSet<String>();
        for (JavacNode enclosingType = annotation; enclosingType != null; enclosingType = (JavacNode)enclosingType.up()) {
            List typarams;
            if (enclosingType.getKind() != AST.Kind.TYPE || (typarams = ((JCTree.JCClassDecl)enclosingType.get()).typarams) == null) continue;
            for (JCTree.JCTypeParameter param : typarams) {
                if (param.name == null) continue;
                usedInOurType.add(param.name.toString());
            }
        }
        HashSet<String> usedInMethodSig = new HashSet<String>();
        for (TypeParameterElement param : sig.elem.getTypeParameters()) {
            usedInMethodSig.add(param.getSimpleName().toString());
        }
        usedInMethodSig.retainAll(usedInOurType);
        if (usedInMethodSig.isEmpty()) {
            return;
        }
        FindTypeVarScanner scanner = new FindTypeVarScanner();
        sig.elem.asType().accept(scanner, null);
        HashSet<String> names = new HashSet<String>(scanner.getTypeVariables());
        names.removeAll(usedInMethodSig);
        if (!names.isEmpty()) {
            CantMakeDelegates cmd = new CantMakeDelegates();
            cmd.conflicted = usedInMethodSig;
            throw cmd;
        }
    }

    private JCTree.JCMethodDecl createDelegateMethod(MethodSig sig, JavacNode annotation, Name delegateName, DelegateReceiver delegateReceiver) throws JavacResolution.TypeNotConvertibleException, CantMakeDelegates {
        this.checkConflictOfTypeVarNames(sig, annotation);
        TreeMaker maker = annotation.getTreeMaker();
        List annotations = sig.isDeprecated ? List.of((Object)maker.Annotation((JCTree)JavacHandlerUtil.chainDots(annotation, "java", "lang", "Deprecated"), List.nil())) : List.nil();
        JCTree.JCModifiers mods = maker.Modifiers(1, annotations);
        JCTree.JCExpression returnType = JavacResolution.typeToJCTree((Type)sig.type.getReturnType(), (JavacAST)annotation.getAst(), true);
        boolean useReturn = sig.type.getReturnType().getKind() != TypeKind.VOID;
        ListBuffer params = sig.type.getParameterTypes().isEmpty() ? null : new ListBuffer();
        ListBuffer args = sig.type.getParameterTypes().isEmpty() ? null : new ListBuffer();
        ListBuffer thrown = sig.type.getThrownTypes().isEmpty() ? null : new ListBuffer();
        ListBuffer typeParams = sig.type.getTypeVariables().isEmpty() ? null : new ListBuffer();
        ListBuffer typeArgs = sig.type.getTypeVariables().isEmpty() ? null : new ListBuffer();
        Types types = Types.instance((Context)annotation.getContext());
        for (TypeVariable param : sig.type.getTypeVariables()) {
            Name name = ((Type.TypeVar)param).tsym.name;
            ListBuffer bounds = types.getBounds((Type.TypeVar)param).isEmpty() ? null : new ListBuffer();
            for (Type type : types.getBounds((Type.TypeVar)param)) {
                bounds.append((Object)JavacResolution.typeToJCTree(type, (JavacAST)annotation.getAst(), true));
            }
            typeParams.append((Object)maker.TypeParameter(name, bounds.toList()));
            typeArgs.append((Object)maker.Ident(name));
        }
        for (TypeMirror ex : sig.type.getThrownTypes()) {
            thrown.append((Object)JavacResolution.typeToJCTree((Type)ex, (JavacAST)annotation.getAst(), true));
        }
        int idx = 0;
        for (TypeMirror param2 : sig.type.getParameterTypes()) {
            JCTree.JCModifiers paramMods = maker.Modifiers(16);
            String[] paramNames = sig.getParameterNames();
            Name name = annotation.toName(paramNames[idx++]);
            params.append((Object)maker.VarDef(paramMods, name, JavacResolution.typeToJCTree((Type)param2, (JavacAST)annotation.getAst(), true), null));
            args.append((Object)maker.Ident(name));
        }
        JCTree.JCMethodInvocation delegateCall = maker.Apply(HandleDelegate.toList(typeArgs), (JCTree.JCExpression)maker.Select(delegateReceiver.get(annotation, delegateName), sig.name), HandleDelegate.toList(args));
        JCTree.JCReturn body = useReturn ? maker.Return((JCTree.JCExpression)delegateCall) : maker.Exec((JCTree.JCExpression)delegateCall);
        JCTree.JCBlock bodyBlock = maker.Block(0, List.of((Object)body));
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, sig.name, returnType, HandleDelegate.toList(typeParams), HandleDelegate.toList(params), HandleDelegate.toList(thrown), bodyBlock, null), (JCTree)annotation.get());
    }

    private static <T> List<T> toList(ListBuffer<T> collection) {
        return collection == null ? List.nil() : collection.toList();
    }

    private void addMethodBindings(java.util.List<MethodSig> signatures, Type.ClassType ct, JavacTypes types, Set<String> banList) {
        Symbol.TypeSymbol tsym = ct.asElement();
        if (tsym == null) {
            return;
        }
        for (Symbol member : tsym.getEnclosedElements()) {
            ExecutableType methodType;
            String sig;
            ExecutableElement exElem;
            if (member.getKind() != ElementKind.METHOD || member.isStatic() || member.isConstructor() || !(exElem = (ExecutableElement)member).getModifiers().contains((Object)Modifier.PUBLIC) || !banList.add(sig = HandleDelegate.printSig(methodType = (ExecutableType)types.asMemberOf((DeclaredType)ct, (Element)member), member.name, types))) continue;
            boolean isDeprecated = (member.flags() & 131072) != 0;
            signatures.add(new MethodSig(member.name, methodType, isDeprecated, exElem));
        }
        if (ct.supertype_field instanceof Type.ClassType) {
            this.addMethodBindings(signatures, (Type.ClassType)ct.supertype_field, types, banList);
        }
        if (ct.interfaces_field != null) {
            for (Type iface : ct.interfaces_field) {
                if (!(iface instanceof Type.ClassType)) continue;
                this.addMethodBindings(signatures, (Type.ClassType)iface, types, banList);
            }
        }
    }

    private static String printSig(ExecutableType method, Name name, JavacTypes types) {
        StringBuilder sb = new StringBuilder();
        sb.append(name.toString()).append("(");
        boolean first = true;
        for (TypeMirror param : method.getParameterTypes()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(HandleDelegate.typeBindingToSignature(param, types));
        }
        return sb.append(")").toString();
    }

    private static String typeBindingToSignature(TypeMirror binding, JavacTypes types) {
        binding = types.erasure(binding);
        return binding.toString();
    }

    private static enum DelegateReceiver {
        METHOD{

            @Override
            public JCTree.JCExpression get(JavacNode node, Name name) {
                List nilExprs = List.nil();
                TreeMaker maker = node.getTreeMaker();
                return maker.Apply(nilExprs, (JCTree.JCExpression)maker.Select((JCTree.JCExpression)maker.Ident(node.toName("this")), name), nilExprs);
            }
        }
        ,
        FIELD{

            @Override
            public JCTree.JCExpression get(JavacNode node, Name name) {
                TreeMaker maker = node.getTreeMaker();
                return maker.Select((JCTree.JCExpression)maker.Ident(node.toName("this")), name);
            }
        };
        

        private DelegateReceiver() {
        }

        public abstract JCTree.JCExpression get(JavacNode var1, Name var2);

    }

    private static class MethodSig {
        final Name name;
        final ExecutableType type;
        final boolean isDeprecated;
        final ExecutableElement elem;

        MethodSig(Name name, ExecutableType type, boolean isDeprecated, ExecutableElement elem) {
            this.name = name;
            this.type = type;
            this.isDeprecated = isDeprecated;
            this.elem = elem;
        }

        String[] getParameterNames() {
            java.util.List<? extends VariableElement> paramList = this.elem.getParameters();
            String[] paramNames = new String[paramList.size()];
            for (int i = 0; i < paramNames.length; ++i) {
                paramNames[i] = paramList.get(i).getSimpleName().toString();
            }
            return paramNames;
        }

        public String toString() {
            return (this.isDeprecated ? "@Deprecated " : "") + (Object)this.name + " " + this.type;
        }
    }

    private static class CantMakeDelegates
    extends Exception {
        Set<String> conflicted;

        private CantMakeDelegates() {
        }
    }

}

