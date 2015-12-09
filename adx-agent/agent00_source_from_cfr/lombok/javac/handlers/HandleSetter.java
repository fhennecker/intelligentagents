/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.TypeTags
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCTypeParameter
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.JCDiagnostic
 *  com.sun.tools.javac.util.JCDiagnostic$DiagnosticPosition
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.Collection;
import java.util.regex.Pattern;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.TransformationsUtil;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleSetter
extends JavacAnnotationHandler<Setter> {
    public void generateSetterForType(JavacNode typeNode, JavacNode errorNode, AccessLevel level, boolean checkForTypeLevelSetter) {
        boolean notAClass;
        if (checkForTypeLevelSetter && typeNode != null) {
            for (JavacNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.ANNOTATION || !JavacHandlerUtil.annotationTypeMatches(Setter.class, child)) continue;
                return;
            }
        }
        JCTree.JCClassDecl typeDecl = null;
        if (typeNode.get() instanceof JCTree.JCClassDecl) {
            typeDecl = (JCTree.JCClassDecl)typeNode.get();
        }
        long modifiers = typeDecl == null ? 0 : typeDecl.mods.flags;
        boolean bl = notAClass = (modifiers & 25088) != 0;
        if (typeDecl == null || notAClass) {
            errorNode.addError("@Setter is only supported on a class or a field.");
            return;
        }
        for (JavacNode field : typeNode.down()) {
            if (field.getKind() != AST.Kind.FIELD) continue;
            JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
            if (fieldDecl.name.toString().startsWith("$") || (fieldDecl.mods.flags & 8) != 0 || (fieldDecl.mods.flags & 16) != 0) continue;
            this.generateSetterForField(field, (JCDiagnostic.DiagnosticPosition)errorNode.get(), level);
        }
    }

    public void generateSetterForField(JavacNode fieldNode, JCDiagnostic.DiagnosticPosition pos, AccessLevel level) {
        for (JavacNode child : fieldNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !JavacHandlerUtil.annotationTypeMatches(Setter.class, child)) continue;
            return;
        }
        this.createSetterForField(level, fieldNode, fieldNode, false);
    }

    @Override
    public void handle(AnnotationValues<Setter> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        Collection<JavacNode> fields = annotationNode.upFromAnnotationToFields();
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Setter.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode node = (JavacNode)annotationNode.up();
        AccessLevel level = annotation.getInstance().value();
        if (level == AccessLevel.NONE || node == null) {
            return;
        }
        switch (node.getKind()) {
            case FIELD: {
                this.createSetterForFields(level, fields, annotationNode, true);
                break;
            }
            case TYPE: {
                this.generateSetterForType(node, annotationNode, level, false);
            }
        }
    }

    private void createSetterForFields(AccessLevel level, Collection<JavacNode> fieldNodes, JavacNode errorNode, boolean whineIfExists) {
        for (JavacNode fieldNode : fieldNodes) {
            this.createSetterForField(level, fieldNode, errorNode, whineIfExists);
        }
    }

    private void createSetterForField(AccessLevel level, JavacNode fieldNode, JavacNode source, boolean whineIfExists) {
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            fieldNode.addError("@Setter is only supported on a class or a field.");
            return;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)fieldNode.get();
        String methodName = JavacHandlerUtil.toSetterName(fieldNode);
        if (methodName == null) {
            source.addWarning("Not generating setter for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        for (String altName : JavacHandlerUtil.toAllSetterNames(fieldNode)) {
            switch (JavacHandlerUtil.methodExists(altName, fieldNode, false, 1)) {
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
        JCTree.JCMethodDecl createdSetter = this.createSetter(access, fieldNode, fieldNode.getTreeMaker(), (JCTree)source.get());
        JavacHandlerUtil.injectMethod((JavacNode)fieldNode.up(), createdSetter);
    }

    private JCTree.JCMethodDecl createSetter(long access, JavacNode field, TreeMaker treeMaker, JCTree source) {
        String setterName = JavacHandlerUtil.toSetterName(field);
        boolean returnThis = JavacHandlerUtil.shouldReturnThis(field);
        if (setterName == null) {
            return null;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
        JCTree.JCExpression fieldRef = JavacHandlerUtil.createFieldAccessor(treeMaker, field, JavacHandlerUtil.FieldAccess.ALWAYS_FIELD);
        JCTree.JCAssign assign = treeMaker.Assign(fieldRef, (JCTree.JCExpression)treeMaker.Ident(fieldDecl.name));
        ListBuffer statements = ListBuffer.lb();
        List<JCTree.JCAnnotation> nonNulls = JavacHandlerUtil.findAnnotations(field, TransformationsUtil.NON_NULL_PATTERN);
        List<JCTree.JCAnnotation> nullables = JavacHandlerUtil.findAnnotations(field, TransformationsUtil.NULLABLE_PATTERN);
        Name methodName = field.toName(setterName);
        List annsOnParam = nonNulls.appendList(nullables);
        JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(16, annsOnParam), fieldDecl.name, fieldDecl.vartype, null);
        if (nonNulls.isEmpty()) {
            statements.append((Object)treeMaker.Exec((JCTree.JCExpression)assign));
        } else {
            JCTree.JCStatement nullCheck = JavacHandlerUtil.generateNullCheck(treeMaker, field);
            if (nullCheck != null) {
                statements.append((Object)nullCheck);
            }
            statements.append((Object)treeMaker.Exec((JCTree.JCExpression)assign));
        }
        JCTree.JCExpression methodType = null;
        if (returnThis) {
            JavacNode typeNode;
            for (typeNode = field; typeNode != null && typeNode.getKind() != AST.Kind.TYPE; typeNode = (JavacNode)typeNode.up()) {
            }
            if (typeNode != null && typeNode.get() instanceof JCTree.JCClassDecl) {
                JCTree.JCClassDecl type = (JCTree.JCClassDecl)typeNode.get();
                ListBuffer typeArgs = ListBuffer.lb();
                if (!type.typarams.isEmpty()) {
                    for (JCTree.JCTypeParameter tp : type.typarams) {
                        typeArgs.append((Object)treeMaker.Ident(tp.name));
                    }
                    methodType = treeMaker.TypeApply((JCTree.JCExpression)treeMaker.Ident(type.name), typeArgs.toList());
                } else {
                    methodType = treeMaker.Ident(type.name);
                }
            }
        }
        if (methodType == null) {
            methodType = treeMaker.Type((Type)new JCNoType(Javac.getCtcInt(TypeTags.class, "VOID")));
            returnThis = false;
        }
        if (returnThis) {
            JCTree.JCReturn returnStatement = treeMaker.Return((JCTree.JCExpression)treeMaker.Ident(field.toName("this")));
            statements.append((Object)returnStatement);
        }
        JCTree.JCBlock methodBody = treeMaker.Block(0, statements.toList());
        List methodGenericParams = List.nil();
        List parameters = List.of((Object)param);
        List throwsClauses = List.nil();
        JCTree.JCExpression annotationMethodDefaultValue = null;
        List annsOnMethod = List.nil();
        if (JavacHandlerUtil.isFieldDeprecated(field)) {
            annsOnMethod = annsOnMethod.prepend((Object)treeMaker.Annotation((JCTree)JavacHandlerUtil.chainDots(field, "java", "lang", "Deprecated"), List.nil()));
        }
        return JavacHandlerUtil.recursiveSetGeneratedBy(treeMaker.MethodDef(treeMaker.Modifiers(access, annsOnMethod), methodName, methodType, methodGenericParams, parameters, throwsClauses, methodBody, annotationMethodDefaultValue), source);
    }

    private static class JCNoType
    extends Type
    implements NoType {
        public JCNoType(int tag) {
            super(tag, null);
        }

        @Override
        public TypeKind getKind() {
            if (this.tag == Javac.getCtcInt(TypeTags.class, "VOID")) {
                return TypeKind.VOID;
            }
            if (this.tag == Javac.getCtcInt(TypeTags.class, "NONE")) {
                return TypeKind.NONE;
            }
            throw new AssertionError((Object)("Unexpected tag: " + this.tag));
        }

        @Override
        public <R, P> R accept(TypeVisitor<R, P> v, P p) {
            return v.visitNoType(this, p);
        }
    }

}

