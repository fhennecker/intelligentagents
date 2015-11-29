/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$MethodSymbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.Type$MethodType
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCNewArray
 *  com.sun.tools.javac.tree.JCTree$JCNewClass
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
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
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.TransformationsUtil;
import lombok.javac.JavacAST;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacResolution;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleConstructor {
    private void handle(JavacNode annotationNode, Class<? extends Annotation> annotationType, ConstructorData data) {
        boolean notAClass;
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, annotationType);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode typeNode = (JavacNode)annotationNode.up();
        JCTree.JCClassDecl typeDecl = null;
        if (typeNode.get() instanceof JCTree.JCClassDecl) {
            typeDecl = (JCTree.JCClassDecl)typeNode.get();
        }
        long modifiers = typeDecl == null ? 0 : typeDecl.mods.flags;
        boolean bl = notAClass = (modifiers & 8704) != 0;
        if (typeDecl == null || notAClass) {
            annotationNode.addError(String.format("%s is only supported on a class or an enum.", annotationType.getSimpleName()));
            return;
        }
        if (data.accessLevel == AccessLevel.NONE) {
            return;
        }
        this.generateConstructor(typeNode, (JCTree)annotationNode.get(), data);
    }

    public static boolean constructorOrConstructorAnnotationExists(JavacNode typeNode) {
        boolean constructorExists;
        boolean bl = constructorExists = JavacHandlerUtil.constructorExists(typeNode) == JavacHandlerUtil.MemberExistsResult.EXISTS_BY_USER;
        if (!constructorExists) {
            for (JavacNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.ANNOTATION || !JavacHandlerUtil.annotationTypeMatches(NoArgsConstructor.class, child) && !JavacHandlerUtil.annotationTypeMatches(AllArgsConstructor.class, child) && !JavacHandlerUtil.annotationTypeMatches(RequiredArgsConstructor.class, child)) continue;
                constructorExists = true;
                break;
            }
        }
        return constructorExists;
    }

    public void generateConstructor(JavacNode typeNode, JCTree source, ConstructorData data) {
        List<SuperConstructor> superConstructors = data.callSuper ? this.getSuperConstructors(typeNode) : List.of((Object)SuperConstructor.implicit());
        for (SuperConstructor superConstructor : superConstructors) {
            JCTree.JCMethodDecl constr = this.createConstructor(typeNode, source, data, superConstructor);
            JavacHandlerUtil.injectMethod(typeNode, constr);
            if (data.staticConstructorRequired()) {
                JCTree.JCMethodDecl staticConstr = this.createStaticConstructor(typeNode, source, data, superConstructor);
                JavacHandlerUtil.injectMethod(typeNode, staticConstr);
            }
            typeNode.rebuild();
        }
    }

    private void addConstructorProperties(JCTree.JCModifiers mods, JavacNode node, List<JCTree.JCVariableDecl> params) {
        if (params.isEmpty()) {
            return;
        }
        TreeMaker maker = node.getTreeMaker();
        JCTree.JCExpression constructorPropertiesType = JavacHandlerUtil.chainDotsString(node, "java.beans.ConstructorProperties");
        ListBuffer fieldNames = ListBuffer.lb();
        for (JCTree.JCVariableDecl param : params) {
            fieldNames.append((Object)maker.Literal((Object)param.name.toString()));
        }
        JCTree.JCNewArray fieldNamesArray = maker.NewArray(null, List.nil(), fieldNames.toList());
        JCTree.JCAnnotation annotation = maker.Annotation((JCTree)constructorPropertiesType, List.of((Object)fieldNamesArray));
        mods.annotations = mods.annotations.append((Object)annotation);
    }

    private JCTree.JCMethodDecl createConstructor(JavacNode typeNode, JCTree source, ConstructorData data, SuperConstructor superConstructor) {
        TreeMaker maker = typeNode.getTreeMaker();
        boolean isEnum = (((JCTree.JCClassDecl)typeNode.get()).mods.flags & 16384) != 0;
        AccessLevel level = isEnum | data.staticConstructorRequired() ? AccessLevel.PRIVATE : data.accessLevel;
        ListBuffer statements = ListBuffer.lb();
        ListBuffer assigns = ListBuffer.lb();
        ListBuffer params = ListBuffer.lb();
        if (!superConstructor.isImplicit) {
            params.appendList(superConstructor.params);
            statements.append((Object)maker.Exec((JCTree.JCExpression)maker.Apply(List.nil(), (JCTree.JCExpression)maker.Ident(typeNode.toName("super")), superConstructor.getArgs(typeNode))));
        }
        List<JavacNode> fields = data.fieldProvider.findFields(typeNode);
        for (JavacNode fieldNode : fields) {
            JCTree.JCStatement nullCheck;
            JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)fieldNode.get();
            List<JCTree.JCAnnotation> nonNulls = JavacHandlerUtil.findAnnotations(fieldNode, TransformationsUtil.NON_NULL_PATTERN);
            List<JCTree.JCAnnotation> nullables = JavacHandlerUtil.findAnnotations(fieldNode, TransformationsUtil.NULLABLE_PATTERN);
            JCTree.JCVariableDecl param = maker.VarDef(maker.Modifiers(16, nonNulls.appendList(nullables)), field.name, field.vartype, null);
            params.append((Object)param);
            JCTree.JCFieldAccess thisX = maker.Select((JCTree.JCExpression)maker.Ident(fieldNode.toName("this")), field.name);
            JCTree.JCAssign assign = maker.Assign((JCTree.JCExpression)thisX, (JCTree.JCExpression)maker.Ident(field.name));
            assigns.append((Object)maker.Exec((JCTree.JCExpression)assign));
            if (nonNulls.isEmpty() || (nullCheck = JavacHandlerUtil.generateNullCheck(maker, fieldNode)) == null) continue;
            statements.append((Object)nullCheck);
        }
        JCTree.JCModifiers mods = maker.Modifiers((long)JavacHandlerUtil.toJavacModifier(level), List.nil());
        if (!data.suppressConstructorProperties && level != AccessLevel.PRIVATE && !this.isLocalType(typeNode)) {
            this.addConstructorProperties(mods, typeNode, params.toList());
        }
        JCTree.JCBlock body = maker.Block(0, statements.appendList(assigns).toList());
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName("<init>"), null, List.nil(), params.toList(), List.nil(), body, null), source);
    }

    private boolean isLocalType(JavacNode type) {
        JavacNode typeNode;
        for (typeNode = (JavacNode)type.up(); typeNode != null && !(typeNode.get() instanceof JCTree.JCClassDecl); typeNode = (JavacNode)typeNode.up()) {
        }
        return typeNode != null;
    }

    private JCTree.JCMethodDecl createStaticConstructor(JavacNode typeNode, JCTree source, ConstructorData data, SuperConstructor superConstructor) {
        JCTree.JCIdent returnType;
        JCTree.JCIdent constructorType;
        TreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCClassDecl type = (JCTree.JCClassDecl)typeNode.get();
        JCTree.JCModifiers mods = maker.Modifiers((long)(8 | JavacHandlerUtil.toJavacModifier(data.accessLevel)));
        ListBuffer typeParams = ListBuffer.lb();
        ListBuffer params = ListBuffer.lb();
        ListBuffer typeArgs1 = ListBuffer.lb();
        ListBuffer typeArgs2 = ListBuffer.lb();
        ListBuffer args = ListBuffer.lb();
        if (!superConstructor.isImplicit) {
            params.appendList(superConstructor.params);
            args.appendList(superConstructor.getArgs(typeNode));
        }
        if (!type.typarams.isEmpty()) {
            for (JCTree.JCTypeParameter param : type.typarams) {
                typeArgs1.append((Object)maker.Ident(param.name));
                typeArgs2.append((Object)maker.Ident(param.name));
                typeParams.append((Object)maker.TypeParameter(param.name, param.bounds));
            }
            returnType = maker.TypeApply((JCTree.JCExpression)maker.Ident(type.name), typeArgs1.toList());
            constructorType = maker.TypeApply((JCTree.JCExpression)maker.Ident(type.name), typeArgs2.toList());
        } else {
            returnType = maker.Ident(type.name);
            constructorType = maker.Ident(type.name);
        }
        List<JavacNode> fields = data.fieldProvider.findFields(typeNode);
        for (JavacNode fieldNode : fields) {
            JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)fieldNode.get();
            JCTree.JCExpression pType = JavacHandlerUtil.cloneType(maker, field.vartype, source);
            List<JCTree.JCAnnotation> nonNulls = JavacHandlerUtil.findAnnotations(fieldNode, TransformationsUtil.NON_NULL_PATTERN);
            List<JCTree.JCAnnotation> nullables = JavacHandlerUtil.findAnnotations(fieldNode, TransformationsUtil.NULLABLE_PATTERN);
            JCTree.JCVariableDecl param = maker.VarDef(maker.Modifiers(16, nonNulls.appendList(nullables)), field.name, pType, null);
            params.append((Object)param);
            args.append((Object)maker.Ident(field.name));
        }
        JCTree.JCReturn returnStatement = maker.Return((JCTree.JCExpression)maker.NewClass(null, List.nil(), (JCTree.JCExpression)constructorType, args.toList(), null));
        JCTree.JCBlock body = maker.Block(0, List.of((Object)returnStatement));
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName(data.staticName), (JCTree.JCExpression)returnType, typeParams.toList(), params.toList(), List.nil(), body, null), source);
    }

    public List<SuperConstructor> getSuperConstructors(JavacNode typeNode) {
        ListBuffer superConstructors = ListBuffer.lb();
        JCTree.JCClassDecl typeDecl = (JCTree.JCClassDecl)typeNode.get();
        if (typeDecl.extending != null) {
            Type type = typeDecl.extending.type;
            if (type == null) {
                try {
                    JCTree.JCExpression resolvedExpression = (JCTree.JCExpression)new JavacResolution(typeNode.getContext()).resolveMethodMember(typeNode).get((Object)typeDecl.extending);
                    if (resolvedExpression != null) {
                        type = resolvedExpression.type;
                    }
                }
                catch (Exception ignore) {
                    // empty catch block
                }
            }
            TreeMaker maker = typeNode.getTreeMaker();
            Symbol.TypeSymbol typeSymbol = type.asElement();
            if (typeSymbol != null) {
                for (Symbol member : typeSymbol.getEnclosedElements()) {
                    if (member.getKind() != ElementKind.CONSTRUCTOR || !member.getModifiers().contains((Object)Modifier.PUBLIC) && !member.getModifiers().contains((Object)Modifier.PROTECTED)) continue;
                    try {
                        Symbol.MethodSymbol superConstructor = (Symbol.MethodSymbol)member;
                        Type.MethodType superConstructorType = superConstructor.type.asMethodType();
                        ListBuffer params = ListBuffer.lb();
                        int argCounter = 0;
                        if (superConstructorType.argtypes != null) {
                            for (Type argtype : superConstructorType.argtypes) {
                                JCTree.JCModifiers paramMods = maker.Modifiers(16);
                                Name name = typeNode.toName("arg" + argCounter++);
                                JCTree.JCExpression varType = JavacResolution.typeToJCTree(argtype, (JavacAST)typeNode.getAst(), true);
                                JCTree.JCVariableDecl varDef = maker.VarDef(paramMods, name, varType, null);
                                params.append((Object)varDef);
                            }
                        }
                        superConstructors.append((Object)new SuperConstructor(params.toList()));
                    }
                    catch (JavacResolution.TypeNotConvertibleException e) {
                        typeNode.addError("Can't create super constructor call: " + e.getMessage());
                    }
                }
            }
        }
        if (superConstructors.isEmpty()) {
            superConstructors.append((Object)SuperConstructor.implicit());
        }
        return superConstructors.toList();
    }

    public static enum FieldProvider {
        REQUIRED{

            @Override
            public List<JavacNode> findFields(JavacNode typeNode) {
                ListBuffer fields = ListBuffer.lb();
                for (JavacNode child : typeNode.down()) {
                    boolean isNonNull;
                    JCTree.JCVariableDecl fieldDecl;
                    if (child.getKind() != AST.Kind.FIELD || !JavacHandlerUtil.filterField(fieldDecl = (JCTree.JCVariableDecl)child.get())) continue;
                    boolean isFinal = (fieldDecl.mods.flags & 16) != 0;
                    boolean bl = isNonNull = !JavacHandlerUtil.findAnnotations(child, TransformationsUtil.NON_NULL_PATTERN).isEmpty();
                    if (!isFinal && !isNonNull || fieldDecl.init != null) continue;
                    fields.append((Object)child);
                }
                return fields.toList();
            }
        }
        ,
        ALL{

            @Override
            public List<JavacNode> findFields(JavacNode typeNode) {
                ListBuffer fields = ListBuffer.lb();
                for (JavacNode child : typeNode.down()) {
                    JCTree.JCVariableDecl fieldDecl;
                    boolean isFinal;
                    if (child.getKind() != AST.Kind.FIELD || !JavacHandlerUtil.filterField(fieldDecl = (JCTree.JCVariableDecl)child.get())) continue;
                    boolean bl = isFinal = (fieldDecl.mods.flags & 16) != 0;
                    if (isFinal && fieldDecl.init != null) continue;
                    fields.append((Object)child);
                }
                return fields.toList();
            }
        }
        ,
        NO{

            @Override
            public List<JavacNode> findFields(JavacNode typeNode) {
                return List.nil();
            }
        };
        

        private FieldProvider() {
        }

        public abstract List<JavacNode> findFields(JavacNode var1);

    }

    public static class SuperConstructor {
        final List<JCTree.JCVariableDecl> params;
        boolean isImplicit;

        static SuperConstructor implicit() {
            SuperConstructor superConstructor = new SuperConstructor(List.nil());
            superConstructor.isImplicit = true;
            return superConstructor;
        }

        SuperConstructor(List<JCTree.JCVariableDecl> params) {
            this.params = params;
        }

        public List<JCTree.JCExpression> getArgs(JavacNode typeNode) {
            TreeMaker maker = typeNode.getTreeMaker();
            ListBuffer args = ListBuffer.lb();
            for (JCTree.JCVariableDecl param : this.params) {
                args.append((Object)maker.Ident(param.name));
            }
            return args.toList();
        }
    }

    public static class ConstructorData {
        FieldProvider fieldProvider;
        AccessLevel accessLevel;
        String staticName;
        boolean callSuper;
        boolean suppressConstructorProperties;

        public ConstructorData fieldProvider(FieldProvider provider) {
            this.fieldProvider = provider;
            return this;
        }

        public ConstructorData accessLevel(AccessLevel accessLevel) {
            this.accessLevel = accessLevel;
            return this;
        }

        public ConstructorData staticName(String name) {
            this.staticName = name;
            return this;
        }

        public ConstructorData callSuper(boolean b) {
            this.callSuper = b;
            return this;
        }

        public ConstructorData suppressConstructorProperties(boolean b) {
            this.suppressConstructorProperties = b;
            return this;
        }

        public boolean staticConstructorRequired() {
            return this.staticName != null && !this.staticName.equals("");
        }
    }

    public static class HandleAllArgsConstructor
    extends JavacAnnotationHandler<AllArgsConstructor> {
        @Override
        public void handle(AnnotationValues<AllArgsConstructor> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            AllArgsConstructor instance = annotation.getInstance();
            ConstructorData data = new ConstructorData().fieldProvider(FieldProvider.ALL).accessLevel(instance.access()).staticName(instance.staticName()).callSuper(instance.callSuper()).suppressConstructorProperties(instance.suppressConstructorProperties());
            new HandleConstructor().handle(annotationNode, AllArgsConstructor.class, data);
        }
    }

    public static class HandleRequiredArgsConstructor
    extends JavacAnnotationHandler<RequiredArgsConstructor> {
        @Override
        public void handle(AnnotationValues<RequiredArgsConstructor> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            RequiredArgsConstructor instance = annotation.getInstance();
            ConstructorData data = new ConstructorData().fieldProvider(FieldProvider.REQUIRED).accessLevel(instance.access()).staticName(instance.staticName()).callSuper(instance.callSuper()).suppressConstructorProperties(instance.suppressConstructorProperties());
            new HandleConstructor().handle(annotationNode, RequiredArgsConstructor.class, data);
        }
    }

    public static class HandleNoArgsConstructor
    extends JavacAnnotationHandler<NoArgsConstructor> {
        @Override
        public void handle(AnnotationValues<NoArgsConstructor> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            NoArgsConstructor instance = annotation.getInstance();
            ConstructorData data = new ConstructorData().fieldProvider(FieldProvider.NO).accessLevel(instance.access()).staticName(instance.staticName()).callSuper(instance.callSuper());
            new HandleConstructor().handle(annotationNode, NoArgsConstructor.class, data);
        }
    }

}

