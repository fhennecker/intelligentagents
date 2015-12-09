/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.TypeTags
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCBinary
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCIf
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCNewClass
 *  com.sun.tools.javac.tree.JCTree$JCPrimitiveTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCSynchronized
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.JCDiagnostic
 *  com.sun.tools.javac.util.JCDiagnostic$DiagnosticPosition
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Delegate;
import lombok.Getter;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.TransformationsUtil;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleGetter
extends JavacAnnotationHandler<Getter> {
    private static final String AR = "java.util.concurrent.atomic.AtomicReference";
    private static final List<JCTree.JCExpression> NIL_EXPRESSION = List.nil();
    private static final Map<Integer, String> TYPE_MAP;

    public void generateGetterForType(JavacNode typeNode, JavacNode errorNode, AccessLevel level, boolean checkForTypeLevelGetter) {
        boolean notAClass;
        if (checkForTypeLevelGetter && typeNode != null) {
            for (JavacNode child : typeNode.down()) {
                if (!JavacHandlerUtil.annotationTypeMatches(Getter.class, child)) continue;
                return;
            }
        }
        JCTree.JCClassDecl typeDecl = null;
        if (typeNode.get() instanceof JCTree.JCClassDecl) {
            typeDecl = (JCTree.JCClassDecl)typeNode.get();
        }
        long modifiers = typeDecl == null ? 0 : typeDecl.mods.flags;
        boolean bl = notAClass = (modifiers & 8704) != 0;
        if (typeDecl == null || notAClass) {
            errorNode.addError("@Getter is only supported on a class, an enum, or a field.");
            return;
        }
        for (JavacNode field : typeNode.down()) {
            if (!this.fieldQualifiesForGetterGeneration(field)) continue;
            this.generateGetterForField(field, (JCDiagnostic.DiagnosticPosition)errorNode.get(), level, false);
        }
    }

    public boolean fieldQualifiesForGetterGeneration(JavacNode field) {
        if (field.getKind() != AST.Kind.FIELD) {
            return false;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
        if (fieldDecl.name.toString().startsWith("$")) {
            return false;
        }
        if ((fieldDecl.mods.flags & 8) != 0) {
            return false;
        }
        return true;
    }

    public void generateGetterForField(JavacNode fieldNode, JCDiagnostic.DiagnosticPosition pos, AccessLevel level, boolean lazy) {
        for (JavacNode child : fieldNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !JavacHandlerUtil.annotationTypeMatches(Getter.class, child)) continue;
            return;
        }
        this.createGetterForField(level, fieldNode, fieldNode, false, lazy);
    }

    @Override
    public void handle(AnnotationValues<Getter> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        Collection<JavacNode> fields = annotationNode.upFromAnnotationToFields();
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Getter.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode node = (JavacNode)annotationNode.up();
        Getter annotationInstance = annotation.getInstance();
        AccessLevel level = annotationInstance.value();
        boolean lazy = annotationInstance.lazy();
        if (level == AccessLevel.NONE) {
            if (lazy) {
                annotationNode.addWarning("'lazy' does not work with AccessLevel.NONE.");
            }
            return;
        }
        if (node == null) {
            return;
        }
        switch (node.getKind()) {
            case FIELD: {
                this.createGetterForFields(level, fields, annotationNode, true, lazy);
                break;
            }
            case TYPE: {
                if (lazy) {
                    annotationNode.addError("'lazy' is not supported for @Getter on a type.");
                }
                this.generateGetterForType(node, annotationNode, level, false);
            }
        }
    }

    private void createGetterForFields(AccessLevel level, Collection<JavacNode> fieldNodes, JavacNode errorNode, boolean whineIfExists, boolean lazy) {
        for (JavacNode fieldNode : fieldNodes) {
            this.createGetterForField(level, fieldNode, errorNode, whineIfExists, lazy);
        }
    }

    private void createGetterForField(AccessLevel level, JavacNode fieldNode, JavacNode source, boolean whineIfExists, boolean lazy) {
        String methodName;
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            source.addError("@Getter is only supported on a class or a field.");
            return;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)fieldNode.get();
        if (lazy) {
            if ((fieldDecl.mods.flags & 2) == 0 || (fieldDecl.mods.flags & 16) == 0) {
                source.addError("'lazy' requires the field to be private and final.");
                return;
            }
            if (fieldDecl.init == null) {
                source.addError("'lazy' requires field initialization.");
                return;
            }
        }
        if ((methodName = JavacHandlerUtil.toGetterName(fieldNode)) == null) {
            source.addWarning("Not generating getter for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        for (String altName : JavacHandlerUtil.toAllGetterNames(fieldNode)) {
            switch (JavacHandlerUtil.methodExists(altName, fieldNode, false, 0)) {
                case EXISTS_BY_LOMBOK: {
                    return;
                }
                case EXISTS_BY_USER: {
                    if (whineIfExists) {
                        String altNameExpl = "";
                        if (!altName.equals(methodName)) {
                            altNameExpl = String.format(" (%s)", altName);
                        }
                        source.addWarning(String.format("Not generating %s(): A method with that name already exists%s", methodName, altNameExpl));
                    }
                    return;
                }
            }
        }
        long access = (long)JavacHandlerUtil.toJavacModifier(level) | fieldDecl.mods.flags & 8;
        JavacHandlerUtil.injectMethod((JavacNode)fieldNode.up(), this.createGetter(access, fieldNode, fieldNode.getTreeMaker(), lazy, (JCTree)source.get()));
    }

    private JCTree.JCMethodDecl createGetter(long access, JavacNode field, TreeMaker treeMaker, boolean lazy, JCTree source) {
        List<JCTree.JCStatement> statements;
        JCTree.JCVariableDecl fieldNode = (JCTree.JCVariableDecl)field.get();
        JCTree.JCExpression methodType = this.copyType(treeMaker, fieldNode);
        Name methodName = field.toName(JavacHandlerUtil.toGetterName(field));
        JCTree.JCExpression toClearOfMarkers = null;
        if (lazy) {
            toClearOfMarkers = fieldNode.init;
            statements = JavacHandlerUtil.inNetbeansEditor(field) ? this.createSimpleGetterBody(treeMaker, field) : this.createLazyGetterBody(treeMaker, field, source);
        } else {
            statements = this.createSimpleGetterBody(treeMaker, field);
        }
        JCTree.JCBlock methodBody = treeMaker.Block(0, statements);
        List methodGenericParams = List.nil();
        List parameters = List.nil();
        List throwsClauses = List.nil();
        JCTree.JCExpression annotationMethodDefaultValue = null;
        List<JCTree.JCAnnotation> nonNulls = JavacHandlerUtil.findAnnotations(field, TransformationsUtil.NON_NULL_PATTERN);
        List<JCTree.JCAnnotation> nullables = JavacHandlerUtil.findAnnotations(field, TransformationsUtil.NULLABLE_PATTERN);
        List<JCTree.JCAnnotation> delegates = HandleGetter.findDelegatesAndRemoveFromField(field);
        List annsOnMethod = nonNulls.appendList(nullables);
        if (JavacHandlerUtil.isFieldDeprecated(field)) {
            annsOnMethod = annsOnMethod.prepend((Object)treeMaker.Annotation((JCTree)JavacHandlerUtil.chainDots(field, "java", "lang", "Deprecated"), List.nil()));
        }
        JCTree.JCMethodDecl decl = JavacHandlerUtil.recursiveSetGeneratedBy(treeMaker.MethodDef(treeMaker.Modifiers(access, annsOnMethod), methodName, methodType, methodGenericParams, parameters, throwsClauses, methodBody, annotationMethodDefaultValue), source);
        if (toClearOfMarkers != null) {
            JavacHandlerUtil.recursiveSetGeneratedBy(toClearOfMarkers, null);
        }
        decl.mods.annotations = decl.mods.annotations.appendList(delegates);
        return decl;
    }

    private static List<JCTree.JCAnnotation> findDelegatesAndRemoveFromField(JavacNode field) {
        JCTree.JCVariableDecl fieldNode = (JCTree.JCVariableDecl)field.get();
        List delegates = List.nil();
        for (JCTree.JCAnnotation annotation : fieldNode.mods.annotations) {
            if (!JavacHandlerUtil.typeMatches(Delegate.class, field, annotation.annotationType)) continue;
            delegates = delegates.append((Object)annotation);
        }
        if (!delegates.isEmpty()) {
            ListBuffer withoutDelegates = ListBuffer.lb();
            for (JCTree.JCAnnotation annotation2 : fieldNode.mods.annotations) {
                if (delegates.contains((Object)annotation2)) continue;
                withoutDelegates.append((Object)annotation2);
            }
            fieldNode.mods.annotations = withoutDelegates.toList();
            field.rebuild();
        }
        return delegates;
    }

    private List<JCTree.JCStatement> createSimpleGetterBody(TreeMaker treeMaker, JavacNode field) {
        return List.of((Object)treeMaker.Return(JavacHandlerUtil.createFieldAccessor(treeMaker, field, JavacHandlerUtil.FieldAccess.ALWAYS_FIELD)));
    }

    private List<JCTree.JCStatement> createLazyGetterBody(TreeMaker maker, JavacNode fieldNode, JCTree source) {
        String boxed;
        ListBuffer statements = ListBuffer.lb();
        JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)fieldNode.get();
        JCTree.JCExpression copyOfRawFieldType = this.copyType(maker, field);
        field.type = null;
        if (field.vartype instanceof JCTree.JCPrimitiveTypeTree && (boxed = TYPE_MAP.get(((JCTree.JCPrimitiveTypeTree)field.vartype).typetag)) != null) {
            field.vartype = JavacHandlerUtil.chainDotsString(fieldNode, boxed);
        }
        Name valueName = fieldNode.toName("value");
        Name actualValueName = fieldNode.toName("actualValue");
        JCTree.JCTypeApply valueVarType = maker.TypeApply(JavacHandlerUtil.chainDotsString(fieldNode, "java.util.concurrent.atomic.AtomicReference"), List.of((Object)this.copyType(maker, field)));
        statements.append((Object)maker.VarDef(maker.Modifiers(0), valueName, (JCTree.JCExpression)valueVarType, (JCTree.JCExpression)this.callGet(fieldNode, JavacHandlerUtil.createFieldAccessor(maker, fieldNode, JavacHandlerUtil.FieldAccess.ALWAYS_FIELD))));
        ListBuffer synchronizedStatements = ListBuffer.lb();
        JCTree.JCExpressionStatement newAssign = maker.Exec((JCTree.JCExpression)maker.Assign((JCTree.JCExpression)maker.Ident(valueName), (JCTree.JCExpression)this.callGet(fieldNode, JavacHandlerUtil.createFieldAccessor(maker, fieldNode, JavacHandlerUtil.FieldAccess.ALWAYS_FIELD))));
        synchronizedStatements.append((Object)newAssign);
        ListBuffer innerIfStatements = ListBuffer.lb();
        innerIfStatements.append((Object)maker.VarDef(maker.Modifiers(16), actualValueName, copyOfRawFieldType, field.init));
        JCTree.JCTypeApply valueVarType2 = maker.TypeApply(JavacHandlerUtil.chainDotsString(fieldNode, "java.util.concurrent.atomic.AtomicReference"), List.of((Object)this.copyType(maker, field)));
        JCTree.JCNewClass newInstance = maker.NewClass(null, NIL_EXPRESSION, (JCTree.JCExpression)valueVarType2, List.of((Object)maker.Ident(actualValueName)), null);
        JCTree.JCExpressionStatement statement = maker.Exec((JCTree.JCExpression)maker.Assign((JCTree.JCExpression)maker.Ident(valueName), (JCTree.JCExpression)newInstance));
        innerIfStatements.append((Object)statement);
        JCTree.JCStatement statement2 = this.callSet(fieldNode, JavacHandlerUtil.createFieldAccessor(maker, fieldNode, JavacHandlerUtil.FieldAccess.ALWAYS_FIELD), (JCTree.JCExpression)maker.Ident(valueName));
        innerIfStatements.append((Object)statement2);
        JCTree.JCBinary isNull = maker.Binary(Javac.getCtcInt(JCTree.class, "EQ"), (JCTree.JCExpression)maker.Ident(valueName), (JCTree.JCExpression)maker.Literal(Javac.getCtcInt(TypeTags.class, "BOT"), (Object)null));
        JCTree.JCIf ifStatement = maker.If((JCTree.JCExpression)isNull, (JCTree.JCStatement)maker.Block(0, innerIfStatements.toList()), null);
        synchronizedStatements.append((Object)ifStatement);
        JCTree.JCSynchronized synchronizedStatement = maker.Synchronized(JavacHandlerUtil.createFieldAccessor(maker, fieldNode, JavacHandlerUtil.FieldAccess.ALWAYS_FIELD), maker.Block(0, synchronizedStatements.toList()));
        JCTree.JCBinary isNull2 = maker.Binary(Javac.getCtcInt(JCTree.class, "EQ"), (JCTree.JCExpression)maker.Ident(valueName), (JCTree.JCExpression)maker.Literal(Javac.getCtcInt(TypeTags.class, "BOT"), (Object)null));
        JCTree.JCIf ifStatement2 = maker.If((JCTree.JCExpression)isNull2, (JCTree.JCStatement)maker.Block(0, List.of((Object)synchronizedStatement)), null);
        statements.append((Object)ifStatement2);
        statements.append((Object)maker.Return((JCTree.JCExpression)this.callGet(fieldNode, (JCTree.JCExpression)maker.Ident(valueName))));
        field.vartype = (JCTree.JCExpression)JavacHandlerUtil.recursiveSetGeneratedBy(maker.TypeApply(JavacHandlerUtil.chainDotsString(fieldNode, "java.util.concurrent.atomic.AtomicReference"), List.of((Object)maker.TypeApply(JavacHandlerUtil.chainDotsString(fieldNode, "java.util.concurrent.atomic.AtomicReference"), List.of((Object)this.copyType(maker, field))))), source);
        field.init = (JCTree.JCExpression)JavacHandlerUtil.recursiveSetGeneratedBy(maker.NewClass(null, NIL_EXPRESSION, this.copyType(maker, field), NIL_EXPRESSION, null), source);
        return statements.toList();
    }

    private JCTree.JCMethodInvocation callGet(JavacNode source, JCTree.JCExpression receiver) {
        TreeMaker maker = source.getTreeMaker();
        return maker.Apply(NIL_EXPRESSION, (JCTree.JCExpression)maker.Select(receiver, source.toName("get")), NIL_EXPRESSION);
    }

    private JCTree.JCStatement callSet(JavacNode source, JCTree.JCExpression receiver, JCTree.JCExpression value) {
        TreeMaker maker = source.getTreeMaker();
        return maker.Exec((JCTree.JCExpression)maker.Apply(NIL_EXPRESSION, (JCTree.JCExpression)maker.Select(receiver, source.toName("set")), List.of((Object)value)));
    }

    private JCTree.JCExpression copyType(TreeMaker treeMaker, JCTree.JCVariableDecl fieldNode) {
        return fieldNode.type != null ? treeMaker.Type(fieldNode.type) : fieldNode.vartype;
    }

    static {
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        m.put(Javac.getCtcInt(TypeTags.class, "INT"), "java.lang.Integer");
        m.put(Javac.getCtcInt(TypeTags.class, "DOUBLE"), "java.lang.Double");
        m.put(Javac.getCtcInt(TypeTags.class, "FLOAT"), "java.lang.Float");
        m.put(Javac.getCtcInt(TypeTags.class, "SHORT"), "java.lang.Short");
        m.put(Javac.getCtcInt(TypeTags.class, "BYTE"), "java.lang.Byte");
        m.put(Javac.getCtcInt(TypeTags.class, "LONG"), "java.lang.Long");
        m.put(Javac.getCtcInt(TypeTags.class, "BOOLEAN"), "java.lang.Boolean");
        m.put(Javac.getCtcInt(TypeTags.class, "CHAR"), "java.lang.Character");
        TYPE_MAP = Collections.unmodifiableMap(m);
    }

}

