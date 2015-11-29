/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCArrayTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCBinary
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCPrimitiveTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import lombok.ToString;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleToString
extends JavacAnnotationHandler<ToString> {
    private void checkForBogusFieldNames(JavacNode type, AnnotationValues<ToString> annotation) {
        Iterator i$;
        int i;
        if (annotation.isExplicit("exclude")) {
            i$ = JavacHandlerUtil.createListOfNonExistentFields(List.from((Object[])annotation.getInstance().exclude()), type, true, false).iterator();
            while (i$.hasNext()) {
                i = (Integer)i$.next();
                annotation.setWarning("exclude", "This field does not exist, or would have been excluded anyway.", i);
            }
        }
        if (annotation.isExplicit("of")) {
            i$ = JavacHandlerUtil.createListOfNonExistentFields(List.from((Object[])annotation.getInstance().of()), type, false, false).iterator();
            while (i$.hasNext()) {
                i = (Integer)i$.next();
                annotation.setWarning("of", "This field does not exist.", i);
            }
        }
    }

    @Override
    public void handle(AnnotationValues<ToString> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, ToString.class);
        ToString ann = annotation.getInstance();
        List excludes = List.from((Object[])ann.exclude());
        List includes = List.from((Object[])ann.of());
        JavacNode typeNode = (JavacNode)annotationNode.up();
        this.checkForBogusFieldNames(typeNode, annotation);
        Boolean callSuper = ann.callSuper();
        if (!annotation.isExplicit("callSuper")) {
            callSuper = null;
        }
        if (!annotation.isExplicit("exclude")) {
            excludes = null;
        }
        if (!annotation.isExplicit("of")) {
            includes = null;
        }
        if (excludes != null && includes != null) {
            excludes = null;
            annotation.setWarning("exclude", "exclude and of are mutually exclusive; the 'exclude' parameter will be ignored.");
        }
        JavacHandlerUtil.FieldAccess fieldAccess = ann.doNotUseGetters() ? JavacHandlerUtil.FieldAccess.PREFER_FIELD : JavacHandlerUtil.FieldAccess.GETTER;
        this.generateToString(typeNode, annotationNode, excludes, includes, ann.includeFieldNames(), callSuper, true, fieldAccess);
    }

    public void generateToStringForType(JavacNode typeNode, JavacNode errorNode, Boolean callSuper) {
        for (JavacNode child : typeNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !JavacHandlerUtil.annotationTypeMatches(ToString.class, child)) continue;
            return;
        }
        boolean includeFieldNames = true;
        try {
            includeFieldNames = (Boolean)ToString.class.getMethod("includeFieldNames", new Class[0]).getDefaultValue();
        }
        catch (Exception ignore) {
            // empty catch block
        }
        this.generateToString(typeNode, errorNode, null, null, includeFieldNames, callSuper, false, JavacHandlerUtil.FieldAccess.GETTER);
    }

    public void generateToString(JavacNode typeNode, JavacNode source, List<String> excludes, List<String> includes, boolean includeFieldNames, Boolean callSuper, boolean whineIfExists, JavacHandlerUtil.FieldAccess fieldAccess) {
        boolean notAClass = true;
        if (typeNode.get() instanceof JCTree.JCClassDecl) {
            long flags = ((JCTree.JCClassDecl)typeNode.get()).mods.flags;
            boolean bl = notAClass = (flags & 8704) != 0;
        }
        if (callSuper == null) {
            try {
                callSuper = (boolean)((Boolean)ToString.class.getMethod("callSuper", new Class[0]).getDefaultValue());
            }
            catch (Exception ignore) {
                // empty catch block
            }
        }
        if (notAClass) {
            source.addError("@ToString is only supported on a class or enum.");
            return;
        }
        ListBuffer nodesForToString = ListBuffer.lb();
        if (includes != null) {
            for (JavacNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.FIELD) continue;
                JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)child.get();
                if (!includes.contains((Object)fieldDecl.name.toString())) continue;
                nodesForToString.append((Object)child);
            }
        } else {
            for (JavacNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.FIELD) continue;
                JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)child.get();
                if ((fieldDecl.mods.flags & 8) != 0 || excludes != null && excludes.contains((Object)fieldDecl.name.toString()) || fieldDecl.name.toString().startsWith("$")) continue;
                nodesForToString.append((Object)child);
            }
        }
        switch (JavacHandlerUtil.methodExists("toString", typeNode, 0)) {
            case NOT_EXISTS: {
                JCTree.JCMethodDecl method = this.createToString(typeNode, nodesForToString.toList(), includeFieldNames, callSuper, fieldAccess, (JCTree)source.get());
                JavacHandlerUtil.injectMethod(typeNode, method);
                break;
            }
            case EXISTS_BY_LOMBOK: {
                break;
            }
            default: {
                if (!whineIfExists) break;
                source.addWarning("Not generating toString(): A method with that name already exists");
            }
        }
    }

    private JCTree.JCMethodDecl createToString(JavacNode typeNode, List<JavacNode> fields, boolean includeFieldNames, boolean callSuper, JavacHandlerUtil.FieldAccess fieldAccess, JCTree source) {
        TreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCAnnotation overrideAnnotation = maker.Annotation((JCTree)JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Override"), List.nil());
        JCTree.JCModifiers mods = maker.Modifiers(1, List.of((Object)overrideAnnotation));
        JCTree.JCExpression returnType = JavacHandlerUtil.chainDots(typeNode, "java", "lang", "String");
        boolean first = true;
        String typeName = this.getTypeName(typeNode);
        String infix = ", ";
        String suffix = ")";
        String prefix = callSuper ? typeName + "(super=" : (fields.isEmpty() ? typeName + "()" : (includeFieldNames ? typeName + "(" + ((JCTree.JCVariableDecl)((JavacNode)fields.iterator().next()).get()).name.toString() + "=" : typeName + "("));
        JCTree.JCLiteral current = maker.Literal((Object)prefix);
        if (callSuper) {
            JCTree.JCMethodInvocation callToSuper = maker.Apply(List.nil(), (JCTree.JCExpression)maker.Select((JCTree.JCExpression)maker.Ident(typeNode.toName("super")), typeNode.toName("toString")), List.nil());
            current = maker.Binary(Javac.getCtcInt(JCTree.class, "PLUS"), (JCTree.JCExpression)current, (JCTree.JCExpression)callToSuper);
            first = false;
        }
        for (JavacNode fieldNode : fields) {
            JCTree.JCExpression expr;
            JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)fieldNode.get();
            JCTree.JCExpression fieldAccessor = JavacHandlerUtil.createFieldAccessor(maker, fieldNode, fieldAccess);
            if (JavacHandlerUtil.getFieldType(fieldNode, fieldAccess) instanceof JCTree.JCArrayTypeTree) {
                boolean multiDim = ((JCTree.JCArrayTypeTree)field.vartype).elemtype instanceof JCTree.JCArrayTypeTree;
                boolean primitiveArray = ((JCTree.JCArrayTypeTree)field.vartype).elemtype instanceof JCTree.JCPrimitiveTypeTree;
                boolean useDeepTS = multiDim || !primitiveArray;
                String[] arrstring = new String[4];
                arrstring[0] = "java";
                arrstring[1] = "util";
                arrstring[2] = "Arrays";
                arrstring[3] = useDeepTS ? "deepToString" : "toString";
                JCTree.JCExpression hcMethod = JavacHandlerUtil.chainDots(typeNode, arrstring);
                expr = maker.Apply(List.nil(), hcMethod, List.of((Object)fieldAccessor));
            } else {
                expr = fieldAccessor;
            }
            if (first) {
                current = maker.Binary(Javac.getCtcInt(JCTree.class, "PLUS"), (JCTree.JCExpression)current, expr);
                first = false;
                continue;
            }
            current = includeFieldNames ? maker.Binary(Javac.getCtcInt(JCTree.class, "PLUS"), (JCTree.JCExpression)current, (JCTree.JCExpression)maker.Literal((Object)(infix + fieldNode.getName() + "="))) : maker.Binary(Javac.getCtcInt(JCTree.class, "PLUS"), (JCTree.JCExpression)current, (JCTree.JCExpression)maker.Literal((Object)infix));
            current = maker.Binary(Javac.getCtcInt(JCTree.class, "PLUS"), (JCTree.JCExpression)current, expr);
        }
        if (!first) {
            current = maker.Binary(Javac.getCtcInt(JCTree.class, "PLUS"), (JCTree.JCExpression)current, (JCTree.JCExpression)maker.Literal((Object)suffix));
        }
        JCTree.JCReturn returnStatement = maker.Return((JCTree.JCExpression)current);
        JCTree.JCBlock body = maker.Block(0, List.of((Object)returnStatement));
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName("toString"), returnType, List.nil(), List.nil(), List.nil(), body, null), source);
    }

    private String getTypeName(JavacNode typeNode) {
        String typeName = ((JCTree.JCClassDecl)typeNode.get()).name.toString();
        JavacNode upType = (JavacNode)typeNode.up();
        while (upType.getKind() == AST.Kind.TYPE) {
            typeName = ((JCTree.JCClassDecl)upType.get()).name.toString() + "." + typeName;
            upType = (JavacNode)upType.up();
        }
        return typeName;
    }

}

