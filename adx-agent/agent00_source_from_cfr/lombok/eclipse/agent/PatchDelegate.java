/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ASTVisitor
 *  org.eclipse.jdt.internal.compiler.CompilationResult
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 *  org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldReference
 *  org.eclipse.jdt.internal.compiler.ast.MemberValuePair
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.ArrayBinding
 *  org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.Binding
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 *  org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeConstants
 *  org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding
 *  org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.WildcardBinding
 */
package lombok.eclipse.agent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import lombok.core.AST;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.TransformEclipseAST;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PatchDelegate {
    private static ThreadLocal<List<ClassScopeEntry>> visited = new ThreadLocal<List<ClassScopeEntry>>(){

        @Override
        protected List<ClassScopeEntry> initialValue() {
            return new ArrayList<ClassScopeEntry>();
        }
    };
    private static Map<ASTNode, Object> alreadyApplied = new WeakHashMap<ASTNode, Object>();
    private static final Object MARKER = new Object();
    private static final String LEGALITY_OF_DELEGATE = "@Delegate is legal only on instance fields or no-argument instance methods.";
    private static final List<String> METHODS_IN_OBJECT = Collections.unmodifiableList(Arrays.asList("hashCode()", "canEqual(java.lang.Object)", "equals(java.lang.Object)", "wait()", "wait(long)", "wait(long, int)", "notify()", "notifyAll()", "toString()", "getClass()", "clone()", "finalize()"));

    private static String nameOfScope(ClassScope scope) {
        TypeDeclaration decl = scope.referenceContext;
        if (decl == null) {
            return "(unknown)";
        }
        if (decl.name == null || decl.name.length == 0) {
            return "(unknown)";
        }
        return new String(decl.name);
    }

    private static boolean hasDelegateMarkedFieldsOrMethods(TypeDeclaration decl) {
        if (decl.fields != null) {
            for (FieldDeclaration field : decl.fields) {
                if (field.annotations == null) continue;
                for (Annotation ann : field.annotations) {
                    if (!PatchDelegate.isDelegate(ann, decl)) continue;
                    return true;
                }
            }
        }
        if (decl.methods != null) {
            for (FieldDeclaration method : decl.methods) {
                if (method.annotations == null) continue;
                for (Annotation ann : method.annotations) {
                    if (!PatchDelegate.isDelegate(ann, decl)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean handleDelegateForType(ClassScope scope) {
        block14 : {
            if (TransformEclipseAST.disableLombok) {
                return false;
            }
            if (!PatchDelegate.hasDelegateMarkedFieldsOrMethods(scope.referenceContext)) {
                return false;
            }
            List<ClassScopeEntry> stack = visited.get();
            StringBuilder corrupted = null;
            for (ClassScopeEntry entry : stack) {
                if (corrupted != null) {
                    corrupted.append(" -> ").append(PatchDelegate.nameOfScope(entry.scope));
                    continue;
                }
                if (entry.scope != scope) continue;
                corrupted = new StringBuilder().append(PatchDelegate.nameOfScope(scope));
            }
            if (corrupted != null) {
                boolean found = false;
                String path = corrupted.toString();
                for (ClassScopeEntry entry2 : stack) {
                    if (!found && entry2.scope == scope) {
                        found = true;
                    }
                    if (!found) continue;
                    entry2.corruptedPath = path;
                }
            } else {
                ClassScopeEntry entry3 = new ClassScopeEntry(scope);
                stack.add(entry3);
                try {
                    TypeDeclaration decl = scope.referenceContext;
                    if (decl == null) break block14;
                    CompilationUnitDeclaration cud = scope.compilationUnitScope().referenceContext;
                    EclipseAST eclipseAst = TransformEclipseAST.getAST(cud, true);
                    ArrayList<BindingTuple> methodsToDelegate = new ArrayList<BindingTuple>();
                    PatchDelegate.fillMethodBindingsForFields(cud, scope, methodsToDelegate);
                    if (entry3.corruptedPath != null) {
                        ((EclipseNode)eclipseAst.get(scope.referenceContext)).addError("No @Delegate methods created because there's a loop: " + entry3.corruptedPath);
                    } else {
                        PatchDelegate.generateDelegateMethods((EclipseNode)eclipseAst.get(decl), methodsToDelegate, DelegateReceiver.FIELD);
                    }
                    methodsToDelegate.clear();
                    PatchDelegate.fillMethodBindingsForMethods(cud, scope, methodsToDelegate);
                    if (entry3.corruptedPath != null) {
                        ((EclipseNode)eclipseAst.get(scope.referenceContext)).addError("No @Delegate methods created because there's a loop: " + entry3.corruptedPath);
                        break block14;
                    }
                    PatchDelegate.generateDelegateMethods((EclipseNode)eclipseAst.get(decl), methodsToDelegate, DelegateReceiver.METHOD);
                }
                finally {
                    stack.remove(stack.size() - 1);
                }
            }
        }
        return false;
    }

    private static String containsDuplicates(List<BindingTuple> tuples) {
        HashSet<String> sigs = new HashSet<String>();
        for (BindingTuple tuple : tuples) {
            if (sigs.add(PatchDelegate.printSig(tuple.parameterized))) continue;
            return PatchDelegate.printSig(tuple.parameterized);
        }
        return null;
    }

    public static void markHandled(Annotation annotation) {
        alreadyApplied.put((ASTNode)annotation, MARKER);
    }

    private static void fillMethodBindingsForFields(CompilationUnitDeclaration cud, ClassScope scope, List<BindingTuple> methodsToDelegate) {
        TypeDeclaration decl = scope.referenceContext;
        if (decl == null) {
            return;
        }
        if (decl.fields != null) {
            block0 : for (FieldDeclaration field : decl.fields) {
                if (field.annotations == null) continue;
                for (Annotation ann : field.annotations) {
                    if (!PatchDelegate.isDelegate(ann, decl) || alreadyApplied.put((ASTNode)ann, MARKER) == MARKER) continue;
                    if ((field.modifiers & 8) != 0) {
                        EclipseAST eclipseAst = TransformEclipseAST.getAST(cud, true);
                        ((EclipseNode)eclipseAst.get(ann)).addError("@Delegate is legal only on instance fields or no-argument instance methods.");
                        continue block0;
                    }
                    List<ClassLiteralAccess> rawTypes = PatchDelegate.rawTypes(ann, "types");
                    List<ClassLiteralAccess> excludedRawTypes = PatchDelegate.rawTypes(ann, "excludes");
                    ArrayList<BindingTuple> methodsToExclude = new ArrayList<BindingTuple>();
                    for (ClassLiteralAccess cla : excludedRawTypes) {
                        PatchDelegate.addAllMethodBindings(methodsToExclude, cla.type.resolveType((BlockScope)decl.initializerScope), new HashSet<String>(), field.name, (ASTNode)ann);
                    }
                    HashSet<String> banList = new HashSet<String>();
                    for (BindingTuple excluded : methodsToExclude) {
                        banList.add(PatchDelegate.printSig(excluded.parameterized));
                    }
                    ArrayList<BindingTuple> methodsToDelegateForThisAnn = new ArrayList<BindingTuple>();
                    if (rawTypes.isEmpty()) {
                        PatchDelegate.addAllMethodBindings(methodsToDelegateForThisAnn, field.type.resolveType((BlockScope)decl.initializerScope), banList, field.name, (ASTNode)ann);
                    } else {
                        for (ClassLiteralAccess cla2 : rawTypes) {
                            PatchDelegate.addAllMethodBindings(methodsToDelegateForThisAnn, cla2.type.resolveType((BlockScope)decl.initializerScope), banList, field.name, (ASTNode)ann);
                        }
                    }
                    String dupe = PatchDelegate.containsDuplicates(methodsToDelegateForThisAnn);
                    if (dupe != null) {
                        EclipseAST eclipseAst = TransformEclipseAST.getAST(cud, true);
                        ((EclipseNode)eclipseAst.get(ann)).addError("The method '" + dupe + "' is being delegated by more than one specified type.");
                        continue;
                    }
                    methodsToDelegate.addAll(methodsToDelegateForThisAnn);
                }
            }
        }
    }

    private static void fillMethodBindingsForMethods(CompilationUnitDeclaration cud, ClassScope scope, List<BindingTuple> methodsToDelegate) {
        TypeDeclaration decl = scope.referenceContext;
        if (decl == null) {
            return;
        }
        if (decl.methods != null) {
            block0 : for (AbstractMethodDeclaration methodDecl : decl.methods) {
                if (methodDecl.annotations == null) continue;
                for (Annotation ann : methodDecl.annotations) {
                    EclipseAST eclipseAst;
                    if (!PatchDelegate.isDelegate(ann, decl) || alreadyApplied.put((ASTNode)ann, MARKER) == MARKER) continue;
                    if (!(methodDecl instanceof MethodDeclaration)) {
                        eclipseAst = TransformEclipseAST.getAST(cud, true);
                        ((EclipseNode)eclipseAst.get(ann)).addError("@Delegate is legal only on instance fields or no-argument instance methods.");
                        continue block0;
                    }
                    if (methodDecl.arguments != null) {
                        eclipseAst = TransformEclipseAST.getAST(cud, true);
                        ((EclipseNode)eclipseAst.get(ann)).addError("@Delegate is legal only on instance fields or no-argument instance methods.");
                        continue block0;
                    }
                    if ((methodDecl.modifiers & 8) != 0) {
                        eclipseAst = TransformEclipseAST.getAST(cud, true);
                        ((EclipseNode)eclipseAst.get(ann)).addError("@Delegate is legal only on instance fields or no-argument instance methods.");
                        continue block0;
                    }
                    MethodDeclaration method = (MethodDeclaration)methodDecl;
                    List<ClassLiteralAccess> rawTypes = PatchDelegate.rawTypes(ann, "types");
                    List<ClassLiteralAccess> excludedRawTypes = PatchDelegate.rawTypes(ann, "excludes");
                    ArrayList<BindingTuple> methodsToExclude = new ArrayList<BindingTuple>();
                    for (ClassLiteralAccess cla : excludedRawTypes) {
                        PatchDelegate.addAllMethodBindings(methodsToExclude, cla.type.resolveType((BlockScope)decl.initializerScope), new HashSet<String>(), method.selector, (ASTNode)ann);
                    }
                    HashSet<String> banList = new HashSet<String>();
                    for (BindingTuple excluded : methodsToExclude) {
                        banList.add(PatchDelegate.printSig(excluded.parameterized));
                    }
                    ArrayList<BindingTuple> methodsToDelegateForThisAnn = new ArrayList<BindingTuple>();
                    if (rawTypes.isEmpty()) {
                        if (method.returnType == null) continue;
                        PatchDelegate.addAllMethodBindings(methodsToDelegateForThisAnn, method.returnType.resolveType((BlockScope)decl.initializerScope), banList, method.selector, (ASTNode)ann);
                    } else {
                        for (ClassLiteralAccess cla2 : rawTypes) {
                            PatchDelegate.addAllMethodBindings(methodsToDelegateForThisAnn, cla2.type.resolveType((BlockScope)decl.initializerScope), banList, method.selector, (ASTNode)ann);
                        }
                    }
                    String dupe = PatchDelegate.containsDuplicates(methodsToDelegateForThisAnn);
                    if (dupe != null) {
                        EclipseAST eclipseAst2 = TransformEclipseAST.getAST(cud, true);
                        ((EclipseNode)eclipseAst2.get(ann)).addError("The method '" + dupe + "' is being delegated by more than one specified type.");
                        continue;
                    }
                    methodsToDelegate.addAll(methodsToDelegateForThisAnn);
                }
            }
        }
    }

    private static boolean isDelegate(Annotation ann, TypeDeclaration decl) {
        if (ann.type == null) {
            return false;
        }
        TypeBinding tb = ann.type.resolveType((BlockScope)decl.initializerScope);
        if (tb == null) {
            return false;
        }
        if (!PatchDelegate.charArrayEquals("lombok", tb.qualifiedPackageName())) {
            return false;
        }
        if (!PatchDelegate.charArrayEquals("Delegate", tb.qualifiedSourceName())) {
            return false;
        }
        return true;
    }

    private static List<ClassLiteralAccess> rawTypes(Annotation ann, String name) {
        ArrayList<ClassLiteralAccess> rawTypes = new ArrayList<ClassLiteralAccess>();
        for (MemberValuePair pair : ann.memberValuePairs()) {
            if (!PatchDelegate.charArrayEquals(name, pair.name)) continue;
            if (pair.value instanceof ArrayInitializer) {
                for (Expression expr : ((ArrayInitializer)pair.value).expressions) {
                    if (!(expr instanceof ClassLiteralAccess)) continue;
                    rawTypes.add((ClassLiteralAccess)expr);
                }
            }
            if (!(pair.value instanceof ClassLiteralAccess)) continue;
            rawTypes.add((ClassLiteralAccess)pair.value);
        }
        return rawTypes;
    }

    private static void generateDelegateMethods(EclipseNode typeNode, List<BindingTuple> methods, DelegateReceiver delegateReceiver) {
        CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)typeNode.top()).get();
        for (BindingTuple pair : methods) {
            EclipseNode annNode;
            MethodDeclaration method = PatchDelegate.createDelegateMethod(pair.fieldName, typeNode, pair, top.compilationResult, annNode = (EclipseNode)((EclipseAST)typeNode.getAst()).get(pair.responsible), delegateReceiver);
            if (method == null) continue;
            SetGeneratedByVisitor visitor = new SetGeneratedByVisitor((ASTNode)annNode.get());
            method.traverse((ASTVisitor)visitor, ((TypeDeclaration)typeNode.get()).scope);
            EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)method);
        }
    }

    public static void checkConflictOfTypeVarNames(BindingTuple binding, EclipseNode typeNode) throws CantMakeDelegates {
        TypeVariableBinding[] typeVars = binding.parameterized.typeVariables();
        if (typeVars == null || typeVars.length == 0) {
            return;
        }
        HashSet<String> usedInOurType = new HashSet<String>();
        for (EclipseNode enclosingType = typeNode; enclosingType != null; enclosingType = (EclipseNode)enclosingType.up()) {
            TypeParameter[] typeParameters;
            if (enclosingType.getKind() != AST.Kind.TYPE || (typeParameters = ((TypeDeclaration)enclosingType.get()).typeParameters) == null) continue;
            for (TypeVariableBinding param : typeParameters) {
                if (param.name == null) continue;
                usedInOurType.add(new String(param.name));
            }
        }
        HashSet<String> usedInMethodSig = new HashSet<String>();
        for (TypeVariableBinding var : typeVars) {
            char[] sourceName = var.sourceName();
            if (sourceName == null) continue;
            usedInMethodSig.add(new String(sourceName));
        }
        usedInMethodSig.retainAll(usedInOurType);
        if (usedInMethodSig.isEmpty()) {
            return;
        }
        TypeVarFinder finder = new TypeVarFinder();
        finder.visitRaw((Binding)binding.base);
        HashSet<String> names = new HashSet<String>(finder.getTypeVariables());
        names.removeAll(usedInMethodSig);
        if (!names.isEmpty()) {
            CantMakeDelegates cmd = new CantMakeDelegates();
            cmd.conflicted = usedInMethodSig;
            throw cmd;
        }
    }

    private static MethodDeclaration createDelegateMethod(char[] name, EclipseNode typeNode, BindingTuple pair, CompilationResult compilationResult, EclipseNode annNode, DelegateReceiver delegateReceiver) {
        int i;
        MessageSend body;
        boolean isVarargs = (pair.base.modifiers & 128) != 0;
        try {
            PatchDelegate.checkConflictOfTypeVarNames(pair, typeNode);
        }
        catch (CantMakeDelegates e) {
            annNode.addError("There's a conflict in the names of type parameters. Fix it by renaming the following type parameters of your class: " + e.conflicted);
            return null;
        }
        ASTNode source = (ASTNode)annNode.get();
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        MethodBinding binding = pair.parameterized;
        MethodDeclaration method = new MethodDeclaration(compilationResult);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method, source);
        method.sourceStart = pS;
        method.sourceEnd = pE;
        method.modifiers = 1;
        method.returnType = EclipseHandlerUtil.makeType(binding.returnType, source, false);
        boolean isDeprecated = binding.isDeprecated();
        method.selector = binding.selector;
        if (binding.thrownExceptions != null && binding.thrownExceptions.length > 0) {
            method.thrownExceptions = new TypeReference[binding.thrownExceptions.length];
            for (int i2 = 0; i2 < method.thrownExceptions.length; ++i2) {
                method.thrownExceptions[i2] = EclipseHandlerUtil.makeType((TypeBinding)binding.thrownExceptions[i2], source, false);
            }
        }
        MessageSend call = new MessageSend();
        call.sourceStart = pS;
        call.sourceEnd = pE;
        call.nameSourcePosition = Eclipse.pos(source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)call, source);
        call.receiver = delegateReceiver.get(source, name);
        call.selector = binding.selector;
        if (binding.typeVariables != null && binding.typeVariables.length > 0) {
            method.typeParameters = new TypeParameter[binding.typeVariables.length];
            call.typeArguments = new TypeReference[binding.typeVariables.length];
            for (i = 0; i < method.typeParameters.length; ++i) {
                int j;
                method.typeParameters[i] = new TypeParameter();
                method.typeParameters[i].sourceStart = pS;
                method.typeParameters[i].sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy((ASTNode)method.typeParameters[i], source);
                method.typeParameters[i].name = binding.typeVariables[i].sourceName;
                call.typeArguments[i] = new SingleTypeReference(binding.typeVariables[i].sourceName, Eclipse.pos(source));
                EclipseHandlerUtil.setGeneratedBy((ASTNode)call.typeArguments[i], source);
                ReferenceBinding super1 = binding.typeVariables[i].superclass;
                ReferenceBinding[] super2 = binding.typeVariables[i].superInterfaces;
                if (super2 == null) {
                    super2 = new ReferenceBinding[]{};
                }
                if (super1 == null && super2.length <= 0) continue;
                int offset = super1 == null ? 0 : 1;
                method.typeParameters[i].bounds = new TypeReference[super2.length + offset - 1];
                method.typeParameters[i].type = super1 != null ? EclipseHandlerUtil.makeType((TypeBinding)super1, source, false) : EclipseHandlerUtil.makeType((TypeBinding)super2[0], source, false);
                int ctr = 0;
                int n = j = super1 == null ? 1 : 0;
                while (j < super2.length) {
                    method.typeParameters[i].bounds[ctr] = EclipseHandlerUtil.makeType((TypeBinding)super2[j], source, false);
                    method.typeParameters[i].bounds[ctr++].bits |= 16;
                    ++j;
                }
            }
        }
        if (isDeprecated) {
            method.annotations = new Annotation[]{EclipseHandlerUtil.generateDeprecatedAnnotation(source)};
        }
        method.bits |= 8388608;
        if (binding.parameters != null && binding.parameters.length > 0) {
            method.arguments = new Argument[binding.parameters.length];
            call.arguments = new Expression[method.arguments.length];
            for (i = 0; i < method.arguments.length; ++i) {
                AbstractMethodDeclaration sourceElem = pair.base.sourceMethod();
                char[] argName = sourceElem == null ? ("arg" + i).toCharArray() : sourceElem.arguments[i].name;
                method.arguments[i] = new Argument(argName, Eclipse.pos(source), EclipseHandlerUtil.makeType(binding.parameters[i], source, false), 16);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)method.arguments[i], source);
                call.arguments[i] = new SingleNameReference(argName, Eclipse.pos(source));
                EclipseHandlerUtil.setGeneratedBy((ASTNode)call.arguments[i], source);
            }
            if (isVarargs) {
                method.arguments[method.arguments.length - 1].type.bits |= 16384;
            }
        }
        if (method.returnType instanceof SingleTypeReference && ((SingleTypeReference)method.returnType).token == TypeConstants.VOID) {
            body = call;
        } else {
            body = new ReturnStatement((Expression)call, source.sourceStart, source.sourceEnd);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)body, source);
        }
        method.statements = new Statement[]{body};
        return method;
    }

    private static void addAllMethodBindings(List<BindingTuple> list, TypeBinding binding, Set<String> banList, char[] fieldName, ASTNode responsible) {
        banList.addAll(METHODS_IN_OBJECT);
        PatchDelegate.addAllMethodBindings0(list, binding, banList, fieldName, responsible);
    }

    private static void addAllMethodBindings0(List<BindingTuple> list, TypeBinding binding, Set<String> banList, char[] fieldName, ASTNode responsible) {
        ClassScope cs;
        if (binding == null) {
            return;
        }
        TypeBinding inner = binding instanceof ParameterizedTypeBinding ? ((ParameterizedTypeBinding)binding).genericType() : binding;
        if (inner instanceof SourceTypeBinding && (cs = ((SourceTypeBinding)inner).scope) != null) {
            try {
                Reflection.classScopeBuildFieldsAndMethodsMethod.invoke((Object)cs, new Object[0]);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (binding instanceof ReferenceBinding) {
            MethodBinding[] parameterizedSigs;
            ReferenceBinding rb = (ReferenceBinding)binding;
            MethodBinding[] baseSigs = parameterizedSigs = rb.availableMethods();
            if (binding instanceof ParameterizedTypeBinding && (baseSigs = ((ParameterizedTypeBinding)binding).genericType().availableMethods()).length != parameterizedSigs.length) {
                baseSigs = parameterizedSigs;
            }
            for (int i = 0; i < parameterizedSigs.length; ++i) {
                MethodBinding mb = parameterizedSigs[i];
                String sig = PatchDelegate.printSig(mb);
                if (mb.isStatic() || mb.isBridge() || mb.isConstructor() || mb.isDefaultAbstract() || !mb.isPublic() || mb.isSynthetic() || !banList.add(sig)) continue;
                BindingTuple pair = new BindingTuple(mb, baseSigs[i], fieldName, responsible);
                list.add(pair);
            }
            PatchDelegate.addAllMethodBindings0(list, (TypeBinding)rb.superclass(), banList, fieldName, responsible);
            ReferenceBinding[] interfaces = rb.superInterfaces();
            if (interfaces != null) {
                for (ReferenceBinding iface : interfaces) {
                    PatchDelegate.addAllMethodBindings0(list, (TypeBinding)iface, banList, fieldName, responsible);
                }
            }
        }
    }

    private static String printSig(MethodBinding binding) {
        StringBuilder signature = new StringBuilder();
        signature.append(binding.selector);
        signature.append("(");
        boolean first = true;
        if (binding.parameters != null) {
            for (TypeBinding param : binding.parameters) {
                if (!first) {
                    signature.append(", ");
                }
                first = false;
                signature.append(PatchDelegate.typeBindingToSignature(param));
            }
        }
        signature.append(")");
        return signature.toString();
    }

    private static String typeBindingToSignature(TypeBinding binding) {
        if ((binding = binding.erasure()) != null && binding.isBaseType()) {
            return new String(binding.sourceName());
        }
        if (binding instanceof ReferenceBinding) {
            String pkg = binding.qualifiedPackageName() == null ? "" : new String(binding.qualifiedPackageName());
            String qsn = binding.qualifiedSourceName() == null ? "" : new String(binding.qualifiedSourceName());
            return pkg.isEmpty() ? qsn : pkg + "." + qsn;
        }
        if (binding instanceof ArrayBinding) {
            StringBuilder out = new StringBuilder();
            out.append(PatchDelegate.typeBindingToSignature(binding.leafComponentType()));
            for (int i = 0; i < binding.dimensions(); ++i) {
                out.append("[]");
            }
            return out.toString();
        }
        return "";
    }

    private static boolean charArrayEquals(String s, char[] c) {
        if (s == null) {
            return c == null;
        }
        if (c == null) {
            return false;
        }
        if (s.length() != c.length) {
            return false;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == c[i]) continue;
            return false;
        }
        return true;
    }

    private static enum DelegateReceiver {
        METHOD{

            public Expression get(ASTNode source, char[] name) {
                MessageSend call = new MessageSend();
                call.sourceStart = source.sourceStart;
                call.sourceEnd = source.sourceEnd;
                call.nameSourcePosition = Eclipse.pos(source);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)call, source);
                call.selector = name;
                call.receiver = new ThisReference(source.sourceStart, source.sourceEnd);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)call.receiver, source);
                return call;
            }
        }
        ,
        FIELD{

            public Expression get(ASTNode source, char[] name) {
                FieldReference fieldRef = new FieldReference(name, Eclipse.pos(source));
                EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldRef, source);
                fieldRef.receiver = new ThisReference(source.sourceStart, source.sourceEnd);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldRef.receiver, source);
                return fieldRef;
            }
        };
        

        private DelegateReceiver() {
        }

        public abstract Expression get(ASTNode var1, char[] var2);

    }

    private static final class BindingTuple {
        final MethodBinding parameterized;
        final MethodBinding base;
        final char[] fieldName;
        final ASTNode responsible;

        BindingTuple(MethodBinding parameterized, MethodBinding base, char[] fieldName, ASTNode responsible) {
            this.parameterized = parameterized;
            this.base = base;
            this.fieldName = fieldName;
            this.responsible = responsible;
        }

        public String toString() {
            Object[] arrobject = new Object[3];
            arrobject[0] = this.parameterized == null ? "(null)" : PatchDelegate.printSig(this.parameterized);
            arrobject[1] = this.base == null ? "(null)" : PatchDelegate.printSig(this.base);
            arrobject[2] = new String(this.fieldName);
            return String.format("{param: %s, base: %s, fieldName: %s}", arrobject);
        }
    }

    private static final class Reflection {
        public static final Method classScopeBuildFieldsAndMethodsMethod;

        private Reflection() {
        }

        static {
            Method m = null;
            try {
                m = ClassScope.class.getDeclaredMethod("buildFieldsAndMethods", new Class[0]);
                m.setAccessible(true);
            }
            catch (Throwable t) {
                // empty catch block
            }
            classScopeBuildFieldsAndMethodsMethod = m;
        }
    }

    public static abstract class EclipseTypeBindingScanner {
        public void visitRaw(Binding binding) {
            if (binding == null) {
                return;
            }
            if (binding instanceof MethodBinding) {
                this.visitMethod((MethodBinding)binding);
            }
            if (binding instanceof BaseTypeBinding) {
                this.visitBase((BaseTypeBinding)binding);
            }
            if (binding instanceof ArrayBinding) {
                this.visitArray((ArrayBinding)binding);
            }
            if (binding instanceof UnresolvedReferenceBinding) {
                this.visitUnresolved((UnresolvedReferenceBinding)binding);
            }
            if (binding instanceof WildcardBinding) {
                this.visitWildcard((WildcardBinding)binding);
            }
            if (binding instanceof TypeVariableBinding) {
                this.visitTypeVariable((TypeVariableBinding)binding);
            }
            if (binding instanceof ParameterizedTypeBinding) {
                this.visitParameterized((ParameterizedTypeBinding)binding);
            }
            if (binding instanceof ReferenceBinding) {
                this.visitReference((ReferenceBinding)binding);
            }
        }

        public void visitReference(ReferenceBinding binding) {
        }

        public void visitParameterized(ParameterizedTypeBinding binding) {
            this.visitRaw((Binding)binding.genericType());
            TypeVariableBinding[] typeVars = binding.typeVariables();
            if (typeVars != null) {
                for (TypeVariableBinding child : typeVars) {
                    this.visitRaw((Binding)child);
                }
            }
        }

        public void visitTypeVariable(TypeVariableBinding binding) {
            this.visitRaw((Binding)binding.superclass);
            ReferenceBinding[] supers = binding.superInterfaces();
            if (supers != null) {
                for (ReferenceBinding child : supers) {
                    this.visitRaw((Binding)child);
                }
            }
        }

        public void visitWildcard(WildcardBinding binding) {
            this.visitRaw((Binding)binding.bound);
        }

        public void visitUnresolved(UnresolvedReferenceBinding binding) {
        }

        public void visitArray(ArrayBinding binding) {
            this.visitRaw((Binding)binding.leafComponentType());
        }

        public void visitBase(BaseTypeBinding binding) {
        }

        public void visitMethod(MethodBinding binding) {
            TypeVariableBinding[] typeVars;
            if (binding.parameters != null) {
                for (TypeBinding child : binding.parameters) {
                    this.visitRaw((Binding)child);
                }
            }
            this.visitRaw((Binding)binding.returnType);
            if (binding.thrownExceptions != null) {
                for (TypeBinding child : binding.thrownExceptions) {
                    this.visitRaw((Binding)child);
                }
            }
            if ((typeVars = binding.typeVariables()) != null) {
                TypeVariableBinding[] arr$ = typeVars;
                int len$ = arr$.length;
                boolean i$ = false;
                while (++i$ < len$) {
                    TypeVariableBinding child = arr$[i$];
                    this.visitRaw((Binding)child.superclass);
                    ReferenceBinding[] supers = child.superInterfaces();
                    if (supers != null) {
                        for (ReferenceBinding child2 : supers) {
                            this.visitRaw((Binding)child2);
                        }
                    }
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeVarFinder
    extends EclipseTypeBindingScanner {
        private Set<String> typeVars = new HashSet<String>();

        public Set<String> getTypeVariables() {
            return this.typeVars;
        }

        @Override
        public void visitTypeVariable(TypeVariableBinding binding) {
            if (binding.sourceName != null) {
                this.typeVars.add(new String(binding.sourceName));
            }
            super.visitTypeVariable(binding);
        }
    }

    public static class CantMakeDelegates
    extends Exception {
        public Set<String> conflicted;
    }

    private static class ClassScopeEntry {
        final ClassScope scope;
        String corruptedPath;

        ClassScopeEntry(ClassScope scope) {
            this.scope = scope;
        }
    }

}

