/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.Tree
 *  com.sun.source.tree.Tree$Kind
 *  com.sun.tools.javac.code.BoundKind
 *  com.sun.tools.javac.code.Scope
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$ClassSymbol
 *  com.sun.tools.javac.code.Symbol$MethodSymbol
 *  com.sun.tools.javac.code.TypeTags
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCArrayTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCBinary
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCIf
 *  com.sun.tools.javac.tree.JCTree$JCImport
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCNewArray
 *  com.sun.tools.javac.tree.JCTree$JCNewClass
 *  com.sun.tools.javac.tree.JCTree$JCPrimitiveTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCThrow
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.JCTree$JCWildcard
 *  com.sun.tools.javac.tree.JCTree$TypeBoundKind
 *  com.sun.tools.javac.tree.JCTree$Visitor
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.tree.TreeScanner
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.JCDiagnostic
 *  com.sun.tools.javac.util.JCDiagnostic$DiagnosticPosition
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Name
 *  com.sun.tools.javac.util.Options
 */
package lombok.javac.handlers;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Options;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.core.TransformationsUtil;
import lombok.core.TypeResolver;
import lombok.experimental.Accessors;
import lombok.javac.Javac;
import lombok.javac.JavacNode;
import lombok.javac.handlers.HandleGetter;

public class JavacHandlerUtil {
    private static Map<JCTree, WeakReference<JCTree>> generatedNodes = new WeakHashMap<JCTree, WeakReference<JCTree>>();

    private JavacHandlerUtil() {
    }

    public static boolean inNetbeansEditor(JavacNode node) {
        Options options = Options.instance((Context)node.getContext());
        return options.keySet().contains("ide") && !options.keySet().contains("backgroundCompilation");
    }

    public static JCTree getGeneratedBy(JCTree node) {
        Map<JCTree, WeakReference<JCTree>> map = generatedNodes;
        synchronized (map) {
            WeakReference<JCTree> ref = generatedNodes.get((Object)node);
            return ref == null ? null : ref.get();
        }
    }

    public static boolean isGenerated(JCTree node) {
        return JavacHandlerUtil.getGeneratedBy(node) != null;
    }

    public static <T extends JCTree> T recursiveSetGeneratedBy(T node, JCTree source) {
        JavacHandlerUtil.setGeneratedBy(node, source);
        node.accept((JCTree.Visitor)new MarkingScanner(source));
        return node;
    }

    public static <T extends JCTree> T setGeneratedBy(T node, JCTree source) {
        Map<JCTree, WeakReference<JCTree>> map = generatedNodes;
        synchronized (map) {
            if (source == null) {
                generatedNodes.remove(node);
            } else {
                generatedNodes.put((JCTree)node, new WeakReference<JCTree>(source));
            }
        }
        return node;
    }

    public static boolean annotationTypeMatches(Class<? extends Annotation> type, JavacNode node) {
        if (node.getKind() != AST.Kind.ANNOTATION) {
            return false;
        }
        return JavacHandlerUtil.typeMatches(type, node, ((JCTree.JCAnnotation)node.get()).annotationType);
    }

    public static boolean filterField(JCTree.JCVariableDecl fieldDecl) {
        long fieldFlags = fieldDecl.mods.flags;
        if ((fieldFlags & 16384) != 0) {
            return false;
        }
        if (fieldDecl.name.toString().startsWith("$")) {
            return false;
        }
        if ((fieldFlags & 8) != 0) {
            return false;
        }
        return true;
    }

    public static boolean typeMatches(Class<?> type, JavacNode node, JCTree typeNode) {
        String typeName = typeNode.toString();
        TypeResolver resolver = new TypeResolver(node.getPackageDeclaration(), node.getImportStatements());
        return resolver.typeMatches(node, type.getName(), typeName);
    }

    public static boolean isFieldDeprecated(JavacNode field) {
        JCTree.JCVariableDecl fieldNode = (JCTree.JCVariableDecl)field.get();
        if ((fieldNode.mods.flags & 131072) != 0) {
            return true;
        }
        for (JavacNode child : field.down()) {
            if (!JavacHandlerUtil.annotationTypeMatches(Deprecated.class, child)) continue;
            return true;
        }
        return false;
    }

    public static <A extends Annotation> AnnotationValues<A> createAnnotation(Class<A> type, final JavacNode node) {
        HashMap<String, AnnotationValues.AnnotationValue> values = new HashMap<String, AnnotationValues.AnnotationValue>();
        JCTree.JCAnnotation anno = (JCTree.JCAnnotation)node.get();
        List arguments = anno.getArguments();
        for (Method m : type.getDeclaredMethods()) {
            if (!Modifier.isPublic(m.getModifiers())) continue;
            String name = m.getName();
            ArrayList<String> raws = new ArrayList<String>();
            ArrayList<Object> guesses = new ArrayList<Object>();
            ArrayList<JCTree.JCExpression> expressions = new ArrayList<JCTree.JCExpression>();
            final ArrayList<JCDiagnostic.DiagnosticPosition> positions = new ArrayList<JCDiagnostic.DiagnosticPosition>();
            boolean isExplicit = false;
            for (JCTree.JCExpression arg : arguments) {
                String mName;
                JCTree.JCExpression rhs;
                if (arg instanceof JCTree.JCAssign) {
                    JCTree.JCAssign assign = (JCTree.JCAssign)arg;
                    mName = assign.lhs.toString();
                    rhs = assign.rhs;
                } else {
                    rhs = arg;
                    mName = "value";
                }
                if (!mName.equals(name)) continue;
                isExplicit = true;
                if (rhs instanceof JCTree.JCNewArray) {
                    List elems = ((JCTree.JCNewArray)rhs).elems;
                    for (JCTree.JCExpression inner : elems) {
                        raws.add(inner.toString());
                        expressions.add(inner);
                        guesses.add(Javac.calculateGuess(inner));
                        positions.add(inner.pos());
                    }
                    continue;
                }
                raws.add(rhs.toString());
                expressions.add(rhs);
                guesses.add(Javac.calculateGuess(rhs));
                positions.add(rhs.pos());
            }
            values.put(name, ()new AnnotationValues.AnnotationValue(node, raws, expressions, guesses, isExplicit){

                @Override
                public void setError(String message, int valueIdx) {
                    if (valueIdx < 0) {
                        node.addError(message);
                    } else {
                        node.addError(message, (JCDiagnostic.DiagnosticPosition)positions.get(valueIdx));
                    }
                }

                @Override
                public void setWarning(String message, int valueIdx) {
                    if (valueIdx < 0) {
                        node.addWarning(message);
                    } else {
                        node.addWarning(message, (JCDiagnostic.DiagnosticPosition)positions.get(valueIdx));
                    }
                }
            });
        }
        return new AnnotationValues<A>(type, values, node);
    }

    public static void deleteAnnotationIfNeccessary(JavacNode annotation, Class<? extends Annotation> annotationType) {
        if (JavacHandlerUtil.inNetbeansEditor(annotation)) {
            return;
        }
        if (!annotation.shouldDeleteLombokAnnotations()) {
            return;
        }
        JavacNode parentNode = (JavacNode)annotation.directUp();
        switch (parentNode.getKind()) {
            case FIELD: 
            case ARGUMENT: 
            case LOCAL: {
                JCTree.JCVariableDecl variable = (JCTree.JCVariableDecl)parentNode.get();
                variable.mods.annotations = JavacHandlerUtil.filterList(variable.mods.annotations, (JCTree)annotation.get());
                break;
            }
            case METHOD: {
                JCTree.JCMethodDecl method = (JCTree.JCMethodDecl)parentNode.get();
                method.mods.annotations = JavacHandlerUtil.filterList(method.mods.annotations, (JCTree)annotation.get());
                break;
            }
            case TYPE: {
                try {
                    JCTree.JCClassDecl type = (JCTree.JCClassDecl)parentNode.get();
                    type.mods.annotations = JavacHandlerUtil.filterList(type.mods.annotations, (JCTree)annotation.get());
                }
                catch (ClassCastException e) {}
                break;
            }
            default: {
                return;
            }
        }
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotation, annotationType.getName());
    }

    public static void deleteImportFromCompilationUnit(JavacNode node, String name) {
        if (JavacHandlerUtil.inNetbeansEditor(node)) {
            return;
        }
        if (!node.shouldDeleteLombokAnnotations()) {
            return;
        }
        ListBuffer newDefs = ListBuffer.lb();
        JCTree.JCCompilationUnit unit = (JCTree.JCCompilationUnit)((JavacNode)node.top()).get();
        for (JCTree def : unit.defs) {
            boolean delete = false;
            if (def instanceof JCTree.JCImport) {
                JCTree.JCImport imp0rt = (JCTree.JCImport)def;
                boolean bl = delete = !imp0rt.staticImport && imp0rt.qualid.toString().equals(name);
            }
            if (delete) continue;
            newDefs.append((Object)def);
        }
        unit.defs = newDefs.toList();
    }

    private static List<JCTree.JCAnnotation> filterList(List<JCTree.JCAnnotation> annotations, JCTree jcTree) {
        ListBuffer newAnnotations = ListBuffer.lb();
        for (JCTree.JCAnnotation ann : annotations) {
            if (jcTree == ann) continue;
            newAnnotations.append((Object)ann);
        }
        return newAnnotations.toList();
    }

    public static java.util.List<String> toAllGetterNames(JavacNode field) {
        String fieldName = field.getName();
        boolean isBoolean = JavacHandlerUtil.isBoolean(field);
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(field);
        return TransformationsUtil.toAllGetterNames(accessors, fieldName, isBoolean);
    }

    public static String toGetterName(JavacNode field) {
        String fieldName = field.getName();
        boolean isBoolean = JavacHandlerUtil.isBoolean(field);
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(field);
        return TransformationsUtil.toGetterName(accessors, fieldName, isBoolean);
    }

    public static java.util.List<String> toAllSetterNames(JavacNode field) {
        String fieldName = field.getName();
        boolean isBoolean = JavacHandlerUtil.isBoolean(field);
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(field);
        return TransformationsUtil.toAllSetterNames(accessors, fieldName, isBoolean);
    }

    public static String toSetterName(JavacNode field) {
        String fieldName = field.getName();
        boolean isBoolean = JavacHandlerUtil.isBoolean(field);
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(field);
        return TransformationsUtil.toSetterName(accessors, fieldName, isBoolean);
    }

    public static boolean shouldReturnThis(JavacNode field) {
        if ((((JCTree.JCVariableDecl)field.get()).mods.flags & 8) != 0) {
            return false;
        }
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(field);
        boolean forced = accessors.getActualExpression("chain") != null;
        Accessors instance = accessors.getInstance();
        return instance.chain() || instance.fluent() && !forced;
    }

    private static boolean isBoolean(JavacNode field) {
        JCTree.JCExpression varType = ((JCTree.JCVariableDecl)field.get()).vartype;
        return varType != null && varType.toString().equals("boolean");
    }

    public static AnnotationValues<Accessors> getAccessorsForField(JavacNode field) {
        for (JavacNode node : field.down()) {
            if (!JavacHandlerUtil.annotationTypeMatches(Accessors.class, node)) continue;
            return JavacHandlerUtil.createAnnotation(Accessors.class, node);
        }
        for (JavacNode current = (JavacNode)field.up(); current != null; current = (JavacNode)current.up()) {
            for (JavacNode node2 : current.down()) {
                if (!JavacHandlerUtil.annotationTypeMatches(Accessors.class, node2)) continue;
                return JavacHandlerUtil.createAnnotation(Accessors.class, node2);
            }
        }
        return AnnotationValues.of(Accessors.class, field);
    }

    public static MemberExistsResult fieldExists(String fieldName, JavacNode node) {
        if ((node = JavacHandlerUtil.upToTypeNode(node)) != null && node.get() instanceof JCTree.JCClassDecl) {
            for (JCTree def : ((JCTree.JCClassDecl)node.get()).defs) {
                if (!(def instanceof JCTree.JCVariableDecl) || !((JCTree.JCVariableDecl)def).name.contentEquals((CharSequence)fieldName)) continue;
                return JavacHandlerUtil.getGeneratedBy(def) == null ? MemberExistsResult.EXISTS_BY_USER : MemberExistsResult.EXISTS_BY_LOMBOK;
            }
        }
        return MemberExistsResult.NOT_EXISTS;
    }

    public static MemberExistsResult methodExists(String methodName, JavacNode node, int params) {
        return JavacHandlerUtil.methodExists(methodName, node, true, params);
    }

    public static MemberExistsResult methodExists(String methodName, JavacNode node, boolean caseSensitive, int params) {
        if ((node = JavacHandlerUtil.upToTypeNode(node)) != null && node.get() instanceof JCTree.JCClassDecl) {
            for (JCTree def : ((JCTree.JCClassDecl)node.get()).defs) {
                if (!(def instanceof JCTree.JCMethodDecl)) continue;
                JCTree.JCMethodDecl md = (JCTree.JCMethodDecl)def;
                String name = md.name.toString();
                boolean matches = caseSensitive ? name.equals(methodName) : name.equalsIgnoreCase(methodName);
                if (!matches) continue;
                if (params > -1) {
                    List ps = md.params;
                    int minArgs = 0;
                    int maxArgs = 0;
                    if (ps != null && ps.length() > 0) {
                        minArgs = ps.length();
                        if ((((JCTree.JCVariableDecl)ps.last()).mods.flags & 0x400000000L) != 0) {
                            maxArgs = Integer.MAX_VALUE;
                            --minArgs;
                        } else {
                            maxArgs = minArgs;
                        }
                    }
                    if (params < minArgs || params > maxArgs) continue;
                }
                return JavacHandlerUtil.getGeneratedBy(def) == null ? MemberExistsResult.EXISTS_BY_USER : MemberExistsResult.EXISTS_BY_LOMBOK;
            }
        }
        return MemberExistsResult.NOT_EXISTS;
    }

    public static MemberExistsResult constructorExists(JavacNode node) {
        if ((node = JavacHandlerUtil.upToTypeNode(node)) != null && node.get() instanceof JCTree.JCClassDecl) {
            for (JCTree def : ((JCTree.JCClassDecl)node.get()).defs) {
                if (!(def instanceof JCTree.JCMethodDecl) || !((JCTree.JCMethodDecl)def).name.contentEquals((CharSequence)"<init>") || (((JCTree.JCMethodDecl)def).mods.flags & 0x1000000000L) != 0) continue;
                return JavacHandlerUtil.getGeneratedBy(def) == null ? MemberExistsResult.EXISTS_BY_USER : MemberExistsResult.EXISTS_BY_LOMBOK;
            }
        }
        return MemberExistsResult.NOT_EXISTS;
    }

    public static int toJavacModifier(AccessLevel accessLevel) {
        switch (accessLevel) {
            case MODULE: 
            case PACKAGE: {
                return 0;
            }
            default: {
                return 1;
            }
            case NONE: 
            case PRIVATE: {
                return 2;
            }
            case PROTECTED: 
        }
        return 4;
    }

    private static GetterMethod findGetter(JavacNode field) {
        JavacNode containingType;
        JCTree.JCVariableDecl decl = (JCTree.JCVariableDecl)field.get();
        JavacNode typeNode = (JavacNode)field.up();
        for (String potentialGetterName : JavacHandlerUtil.toAllGetterNames(field)) {
            for (JavacNode potentialGetter : typeNode.down()) {
                if (potentialGetter.getKind() != AST.Kind.METHOD) continue;
                JCTree.JCMethodDecl method = (JCTree.JCMethodDecl)potentialGetter.get();
                if (!method.name.toString().equalsIgnoreCase(potentialGetterName) || (method.mods.flags & 8) != 0 || method.params != null && method.params.size() > 0) continue;
                return new GetterMethod(method.name, method.restype);
            }
        }
        boolean hasGetterAnnotation = false;
        for (JavacNode child : field.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !JavacHandlerUtil.annotationTypeMatches(Getter.class, child)) continue;
            AnnotationValues<A> ann = JavacHandlerUtil.createAnnotation(Getter.class, child);
            if (((Getter)ann.getInstance()).value() == AccessLevel.NONE) {
                return null;
            }
            hasGetterAnnotation = true;
        }
        if (!hasGetterAnnotation && new HandleGetter().fieldQualifiesForGetterGeneration(field) && (containingType = (JavacNode)field.up()) != null) {
            for (JavacNode child2 : containingType.down()) {
                if (child2.getKind() == AST.Kind.ANNOTATION && JavacHandlerUtil.annotationTypeMatches(Data.class, child2)) {
                    hasGetterAnnotation = true;
                }
                if (child2.getKind() != AST.Kind.ANNOTATION || !JavacHandlerUtil.annotationTypeMatches(Getter.class, child2)) continue;
                AnnotationValues<A> ann = JavacHandlerUtil.createAnnotation(Getter.class, child2);
                if (((Getter)ann.getInstance()).value() == AccessLevel.NONE) {
                    return null;
                }
                hasGetterAnnotation = true;
            }
        }
        if (hasGetterAnnotation) {
            String getterName = JavacHandlerUtil.toGetterName(field);
            if (getterName == null) {
                return null;
            }
            return new GetterMethod(field.toName(getterName), decl.vartype);
        }
        return null;
    }

    static boolean lookForGetter(JavacNode field, FieldAccess fieldAccess) {
        if (fieldAccess == FieldAccess.GETTER) {
            return true;
        }
        if (fieldAccess == FieldAccess.ALWAYS_FIELD) {
            return false;
        }
        for (JavacNode child : field.down()) {
            AnnotationValues<A> ann;
            if (child.getKind() != AST.Kind.ANNOTATION || !JavacHandlerUtil.annotationTypeMatches(Getter.class, child) || !((Getter)(ann = JavacHandlerUtil.createAnnotation(Getter.class, child)).getInstance()).lazy()) continue;
            return true;
        }
        return false;
    }

    static JCTree.JCExpression getFieldType(JavacNode field, FieldAccess fieldAccess) {
        GetterMethod getter;
        boolean lookForGetter = JavacHandlerUtil.lookForGetter(field, fieldAccess);
        GetterMethod getterMethod = getter = lookForGetter ? JavacHandlerUtil.findGetter(field) : null;
        if (getter == null) {
            return ((JCTree.JCVariableDecl)field.get()).vartype;
        }
        return getter.type;
    }

    static JCTree.JCExpression createFieldAccessor(TreeMaker maker, JavacNode field, FieldAccess fieldAccess) {
        return JavacHandlerUtil.createFieldAccessor(maker, field, fieldAccess, null);
    }

    static JCTree.JCExpression createFieldAccessor(TreeMaker maker, JavacNode field, FieldAccess fieldAccess, JCTree.JCExpression receiver) {
        boolean lookForGetter = JavacHandlerUtil.lookForGetter(field, fieldAccess);
        GetterMethod getter = lookForGetter ? JavacHandlerUtil.findGetter(field) : null;
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
        if (getter == null) {
            if (receiver == null) {
                if ((fieldDecl.mods.flags & 8) == 0) {
                    receiver = maker.Ident(field.toName("this"));
                } else {
                    JavacNode containerNode = (JavacNode)field.up();
                    if (containerNode != null && containerNode.get() instanceof JCTree.JCClassDecl) {
                        JCTree.JCClassDecl container = (JCTree.JCClassDecl)((JavacNode)field.up()).get();
                        receiver = maker.Ident(container.name);
                    }
                }
            }
            return receiver == null ? maker.Ident(fieldDecl.name) : maker.Select(receiver, fieldDecl.name);
        }
        if (receiver == null) {
            receiver = maker.Ident(field.toName("this"));
        }
        JCTree.JCMethodInvocation call = maker.Apply(List.nil(), (JCTree.JCExpression)maker.Select(receiver, getter.name), List.nil());
        return call;
    }

    public static void injectFieldSuppressWarnings(JavacNode typeNode, JCTree.JCVariableDecl field) {
        JavacHandlerUtil.injectField(typeNode, field, true);
    }

    public static void injectField(JavacNode typeNode, JCTree.JCVariableDecl field) {
        JavacHandlerUtil.injectField(typeNode, field, false);
    }

    private static void injectField(JavacNode typeNode, JCTree.JCVariableDecl field, boolean addSuppressWarnings) {
        JCTree.JCVariableDecl f;
        JCTree.JCClassDecl type = (JCTree.JCClassDecl)typeNode.get();
        if (addSuppressWarnings) {
            JavacHandlerUtil.addSuppressWarningsAll(field.mods, typeNode, field.pos, JavacHandlerUtil.getGeneratedBy((JCTree)field));
        }
        List insertAfter = null;
        List insertBefore = type.defs;
        while (insertBefore.tail != null && insertBefore.head instanceof JCTree.JCVariableDecl && (JavacHandlerUtil.isEnumConstant(f = (JCTree.JCVariableDecl)insertBefore.head) || JavacHandlerUtil.isGenerated((JCTree)f))) {
            insertAfter = insertBefore;
            insertBefore = insertBefore.tail;
        }
        List fieldEntry = List.of((Object)field);
        fieldEntry.tail = insertBefore;
        if (insertAfter == null) {
            type.defs = fieldEntry;
        } else {
            insertAfter.tail = fieldEntry;
        }
        typeNode.add(field, AST.Kind.FIELD);
    }

    private static boolean isEnumConstant(JCTree.JCVariableDecl field) {
        return (field.mods.flags & 16384) != 0;
    }

    public static void injectMethod(JavacNode typeNode, JCTree.JCMethodDecl method) {
        JCTree.JCClassDecl type = (JCTree.JCClassDecl)typeNode.get();
        if (method.getName().contentEquals((CharSequence)"<init>")) {
            int idx = 0;
            for (JCTree def : type.defs) {
                if (def instanceof JCTree.JCMethodDecl && (((JCTree.JCMethodDecl)def).mods.flags & 0x1000000000L) != 0) {
                    JavacNode tossMe = (JavacNode)typeNode.getNodeFor(def);
                    if (tossMe != null) {
                        ((JavacNode)tossMe.up()).removeChild(tossMe);
                    }
                    type.defs = JavacHandlerUtil.addAllButOne(type.defs, idx);
                    if (type.sym == null || type.sym.members_field == null) break;
                    type.sym.members_field.remove((Symbol)((JCTree.JCMethodDecl)def).sym);
                    break;
                }
                ++idx;
            }
        }
        JavacHandlerUtil.addSuppressWarningsAll(method.mods, typeNode, method.pos, JavacHandlerUtil.getGeneratedBy((JCTree)method));
        type.defs = type.defs.append((Object)method);
        typeNode.add(method, AST.Kind.METHOD);
    }

    private static void addSuppressWarningsAll(JCTree.JCModifiers mods, JavacNode node, int pos, JCTree source) {
        TreeMaker maker = node.getTreeMaker();
        JCTree.JCExpression suppressWarningsType = JavacHandlerUtil.chainDots(node, "java", "lang", "SuppressWarnings");
        JCTree.JCLiteral allLiteral = maker.Literal((Object)"all");
        suppressWarningsType.pos = pos;
        allLiteral.pos = pos;
        JCTree.JCAnnotation annotation = JavacHandlerUtil.recursiveSetGeneratedBy(maker.Annotation((JCTree)suppressWarningsType, List.of((Object)allLiteral)), source);
        annotation.pos = pos;
        mods.annotations = mods.annotations.append((Object)annotation);
    }

    private static List<JCTree> addAllButOne(List<JCTree> defs, int idx) {
        ListBuffer out = ListBuffer.lb();
        int i = 0;
        for (JCTree def : defs) {
            if (i++ == idx) continue;
            out.append((Object)def);
        }
        return out.toList();
    }

    public static /* varargs */ JCTree.JCExpression chainDots(JavacNode node, String ... elems) {
        return JavacHandlerUtil.chainDots(node, -1, elems);
    }

    public static /* varargs */ JCTree.JCExpression chainDots(JavacNode node, int pos, String ... elems) {
        assert (elems != null);
        assert (elems.length > 0);
        TreeMaker maker = node.getTreeMaker();
        if (pos != -1) {
            maker = maker.at(pos);
        }
        JCTree.JCIdent e = maker.Ident(node.toName(elems[0]));
        for (int i = 1; i < elems.length; ++i) {
            e = maker.Select((JCTree.JCExpression)e, node.toName(elems[i]));
        }
        return e;
    }

    public static JCTree.JCExpression chainDotsString(JavacNode node, String elems) {
        return JavacHandlerUtil.chainDots(node, elems.split("\\."));
    }

    public static List<JCTree.JCAnnotation> findAnnotations(JavacNode fieldNode, Pattern namePattern) {
        ListBuffer result = ListBuffer.lb();
        for (JavacNode child : fieldNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION) continue;
            JCTree.JCAnnotation annotation = (JCTree.JCAnnotation)child.get();
            String name = annotation.annotationType.toString();
            int idx = name.lastIndexOf(".");
            String suspect = idx == -1 ? name : name.substring(idx + 1);
            if (!namePattern.matcher(suspect).matches()) continue;
            result.append((Object)annotation);
        }
        return result.toList();
    }

    public static JCTree.JCStatement generateNullCheck(TreeMaker treeMaker, JavacNode variable) {
        JCTree.JCVariableDecl varDecl = (JCTree.JCVariableDecl)variable.get();
        if (Javac.isPrimitive(varDecl.vartype)) {
            return null;
        }
        Name fieldName = varDecl.name;
        JCTree.JCExpression npe = JavacHandlerUtil.chainDots(variable, "java", "lang", "NullPointerException");
        JCTree.JCNewClass exception = treeMaker.NewClass(null, List.nil(), npe, List.of((Object)treeMaker.Literal((Object)fieldName.toString())), null);
        JCTree.JCThrow throwStatement = treeMaker.Throw((JCTree)exception);
        return treeMaker.If((JCTree.JCExpression)treeMaker.Binary(Javac.getCtcInt(JCTree.class, "EQ"), (JCTree.JCExpression)treeMaker.Ident(fieldName), (JCTree.JCExpression)treeMaker.Literal(Javac.getCtcInt(TypeTags.class, "BOT"), (Object)null)), (JCTree.JCStatement)throwStatement, null);
    }

    public static List<Integer> createListOfNonExistentFields(List<String> list, JavacNode type, boolean excludeStandard, boolean excludeTransient) {
        boolean[] matched = new boolean[list.size()];
        for (JavacNode child : type.down()) {
            int idx;
            if (list.isEmpty()) break;
            if (child.getKind() != AST.Kind.FIELD) continue;
            JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)child.get();
            if (excludeStandard && ((field.mods.flags & 8) != 0 || field.name.toString().startsWith("$")) || excludeTransient && (field.mods.flags & 128) != 0 || (idx = list.indexOf((Object)child.getName())) <= -1) continue;
            matched[idx] = true;
        }
        ListBuffer problematic = ListBuffer.lb();
        for (int i = 0; i < list.size(); ++i) {
            if (matched[i]) continue;
            problematic.append((Object)i);
        }
        return problematic.toList();
    }

    static List<JCTree.JCExpression> getAndRemoveAnnotationParameter(JCTree.JCAnnotation ast, String parameterName) {
        ListBuffer params = ListBuffer.lb();
        List result = List.nil();
        for (JCTree.JCExpression param : ast.args) {
            if (param instanceof JCTree.JCAssign) {
                JCTree.JCAssign assign = (JCTree.JCAssign)param;
                if (assign.lhs instanceof JCTree.JCIdent) {
                    JCTree.JCIdent ident = (JCTree.JCIdent)assign.lhs;
                    if (parameterName.equals(ident.name.toString())) {
                        if (assign.rhs instanceof JCTree.JCNewArray) {
                            result = ((JCTree.JCNewArray)assign.rhs).elems;
                            continue;
                        }
                        result = result.append((Object)assign.rhs);
                        continue;
                    }
                }
            }
            params.append((Object)param);
        }
        ast.args = params.toList();
        return result;
    }

    static List<JCTree.JCAnnotation> copyAnnotations(List<JCTree.JCExpression> in) {
        ListBuffer out = ListBuffer.lb();
        for (JCTree.JCExpression expr : in) {
            if (!(expr instanceof JCTree.JCAnnotation)) continue;
            out.append((Object)((JCTree.JCAnnotation)expr.clone()));
        }
        return out.toList();
    }

    static boolean isClass(JavacNode typeNode) {
        return JavacHandlerUtil.isClassAndDoesNotHaveFlags(typeNode, 25088);
    }

    static boolean isClassOrEnum(JavacNode typeNode) {
        return JavacHandlerUtil.isClassAndDoesNotHaveFlags(typeNode, 8704);
    }

    private static boolean isClassAndDoesNotHaveFlags(JavacNode typeNode, int flags) {
        JCTree.JCClassDecl typeDecl = null;
        if (!(typeNode.get() instanceof JCTree.JCClassDecl)) {
            return false;
        }
        typeDecl = (JCTree.JCClassDecl)typeNode.get();
        long typeDeclflags = typeDecl == null ? 0 : typeDecl.mods.flags;
        return (typeDeclflags & (long)flags) == 0;
    }

    public static JavacNode upToTypeNode(JavacNode node) {
        if (node == null) {
            throw new NullPointerException("node");
        }
        while (node != null && !(node.get() instanceof JCTree.JCClassDecl)) {
            node = (JavacNode)node.up();
        }
        return node;
    }

    public static JCTree.JCExpression cloneType(TreeMaker maker, JCTree.JCExpression in, JCTree source) {
        JCTree.JCExpression out = JavacHandlerUtil.cloneType0(maker, (JCTree)in);
        if (out != null) {
            JavacHandlerUtil.recursiveSetGeneratedBy(out, source);
        }
        return out;
    }

    private static JCTree.JCExpression cloneType0(TreeMaker maker, JCTree in) {
        if (in == null) {
            return null;
        }
        if (in instanceof JCTree.JCPrimitiveTypeTree) {
            return (JCTree.JCExpression)in;
        }
        if (in instanceof JCTree.JCIdent) {
            return maker.Ident(((JCTree.JCIdent)in).name);
        }
        if (in instanceof JCTree.JCFieldAccess) {
            JCTree.JCFieldAccess fa = (JCTree.JCFieldAccess)in;
            return maker.Select(JavacHandlerUtil.cloneType0(maker, (JCTree)fa.selected), fa.name);
        }
        if (in instanceof JCTree.JCArrayTypeTree) {
            JCTree.JCArrayTypeTree att = (JCTree.JCArrayTypeTree)in;
            return maker.TypeArray(JavacHandlerUtil.cloneType0(maker, (JCTree)att.elemtype));
        }
        if (in instanceof JCTree.JCTypeApply) {
            JCTree.JCTypeApply ta = (JCTree.JCTypeApply)in;
            ListBuffer lb = ListBuffer.lb();
            for (JCTree.JCExpression typeArg : ta.arguments) {
                lb.append((Object)JavacHandlerUtil.cloneType0(maker, (JCTree)typeArg));
            }
            return maker.TypeApply(JavacHandlerUtil.cloneType0(maker, (JCTree)ta.clazz), lb.toList());
        }
        if (in instanceof JCTree.JCWildcard) {
            JCTree.TypeBoundKind newKind;
            JCTree.JCWildcard w = (JCTree.JCWildcard)in;
            JCTree.JCExpression newInner = JavacHandlerUtil.cloneType0(maker, w.inner);
            switch (w.getKind()) {
                case SUPER_WILDCARD: {
                    newKind = maker.TypeBoundKind(BoundKind.SUPER);
                    break;
                }
                case EXTENDS_WILDCARD: {
                    newKind = maker.TypeBoundKind(BoundKind.EXTENDS);
                    break;
                }
                default: {
                    newKind = maker.TypeBoundKind(BoundKind.UNBOUND);
                }
            }
            return maker.Wildcard(newKind, (JCTree)newInner);
        }
        return (JCTree.JCExpression)in;
    }

    public static enum FieldAccess {
        GETTER,
        PREFER_FIELD,
        ALWAYS_FIELD;
        

        private FieldAccess() {
        }
    }

    private static class GetterMethod {
        private final Name name;
        private final JCTree.JCExpression type;

        GetterMethod(Name name, JCTree.JCExpression type) {
            this.name = name;
            this.type = type;
        }
    }

    public static enum MemberExistsResult {
        NOT_EXISTS,
        EXISTS_BY_LOMBOK,
        EXISTS_BY_USER;
        

        private MemberExistsResult() {
        }
    }

    private static class MarkingScanner
    extends TreeScanner {
        private final JCTree source;

        MarkingScanner(JCTree source) {
            this.source = source;
        }

        public void scan(JCTree tree) {
            JavacHandlerUtil.setGeneratedBy(tree, this.source);
            super.scan(tree);
        }
    }

}

