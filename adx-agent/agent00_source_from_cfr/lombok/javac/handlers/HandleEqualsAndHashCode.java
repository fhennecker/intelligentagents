/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.BoundKind
 *  com.sun.tools.javac.code.TypeTags
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCArrayTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCBinary
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCConditional
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCIf
 *  com.sun.tools.javac.tree.JCTree$JCInstanceOf
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCPrimitiveTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCTypeCast
 *  com.sun.tools.javac.tree.JCTree$JCUnary
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.JCTree$JCWildcard
 *  com.sun.tools.javac.tree.JCTree$TypeBoundKind
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.lang.model.type.TypeKind;
import lombok.EqualsAndHashCode;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleEqualsAndHashCode
extends JavacAnnotationHandler<EqualsAndHashCode> {
    private static final String RESULT_NAME = "result";
    private static final String PRIME_NAME = "PRIME";

    private void checkForBogusFieldNames(JavacNode type, AnnotationValues<EqualsAndHashCode> annotation) {
        Iterator i$;
        int i;
        if (annotation.isExplicit("exclude")) {
            i$ = JavacHandlerUtil.createListOfNonExistentFields(List.from((Object[])annotation.getInstance().exclude()), type, true, true).iterator();
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
    public void handle(AnnotationValues<EqualsAndHashCode> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, EqualsAndHashCode.class);
        EqualsAndHashCode ann = annotation.getInstance();
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
        this.generateMethods(typeNode, annotationNode, excludes, includes, callSuper, true, fieldAccess);
    }

    public void generateEqualsAndHashCodeForType(JavacNode typeNode, JavacNode source, Boolean callSuper) {
        for (JavacNode child : typeNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !JavacHandlerUtil.annotationTypeMatches(EqualsAndHashCode.class, child)) continue;
            return;
        }
        this.generateMethods(typeNode, source, null, null, callSuper, false, JavacHandlerUtil.FieldAccess.GETTER);
    }

    public void generateMethods(JavacNode typeNode, JavacNode source, List<String> excludes, List<String> includes, Boolean callSuper, boolean whineIfExists, JavacHandlerUtil.FieldAccess fieldAccess) {
        JCTree.JCVariableDecl fieldDecl;
        JCTree extending;
        boolean implicitCallSuper;
        boolean notAClass = true;
        if (typeNode.get() instanceof JCTree.JCClassDecl) {
            long flags = ((JCTree.JCClassDecl)typeNode.get()).mods.flags;
            boolean bl = notAClass = (flags & 25088) != 0;
        }
        if (notAClass) {
            source.addError("@EqualsAndHashCode is only supported on a class.");
            return;
        }
        boolean isDirectDescendantOfObject = true;
        boolean bl = implicitCallSuper = callSuper == null;
        if (callSuper == null) {
            try {
                callSuper = (boolean)((Boolean)EqualsAndHashCode.class.getMethod("callSuper", new Class[0]).getDefaultValue());
            }
            catch (Exception ignore) {
                throw new InternalError("Lombok bug - this cannot happen - can't find callSuper field in EqualsAndHashCode annotation.");
            }
        }
        if ((extending = ((JCTree.JCClassDecl)typeNode.get()).getExtendsClause()) != null) {
            String p = extending.toString();
            boolean bl2 = isDirectDescendantOfObject = p.equals("Object") || p.equals("java.lang.Object");
        }
        if (isDirectDescendantOfObject && callSuper.booleanValue()) {
            source.addError("Generating equals/hashCode with a supercall to java.lang.Object is pointless.");
            return;
        }
        if (!isDirectDescendantOfObject && !callSuper.booleanValue() && implicitCallSuper) {
            source.addWarning("Generating equals/hashCode implementation but without a call to superclass, even though this class does not extend java.lang.Object. If this is intentional, add '@EqualsAndHashCode(callSuper=false)' to your type.");
        }
        ListBuffer nodesForEquality = ListBuffer.lb();
        if (includes != null) {
            for (JavacNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.FIELD) continue;
                fieldDecl = (JCTree.JCVariableDecl)child.get();
                if (!includes.contains((Object)fieldDecl.name.toString())) continue;
                nodesForEquality.append((Object)child);
            }
        } else {
            for (JavacNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.FIELD) continue;
                fieldDecl = (JCTree.JCVariableDecl)child.get();
                if ((fieldDecl.mods.flags & 8) != 0 || (fieldDecl.mods.flags & 128) != 0 || excludes != null && excludes.contains((Object)fieldDecl.name.toString()) || fieldDecl.name.toString().startsWith("$")) continue;
                nodesForEquality.append((Object)child);
            }
        }
        boolean isFinal = (((JCTree.JCClassDecl)typeNode.get()).mods.flags & 16) != 0;
        boolean needsCanEqual = !isFinal || !isDirectDescendantOfObject;
        ArrayList<JavacHandlerUtil.MemberExistsResult> existsResults = new ArrayList<JavacHandlerUtil.MemberExistsResult>();
        existsResults.add(JavacHandlerUtil.methodExists("equals", typeNode, 1));
        existsResults.add(JavacHandlerUtil.methodExists("hashCode", typeNode, 0));
        existsResults.add(JavacHandlerUtil.methodExists("canEqual", typeNode, 1));
        switch ((JavacHandlerUtil.MemberExistsResult)((Object)Collections.max(existsResults))) {
            case EXISTS_BY_LOMBOK: {
                return;
            }
            case EXISTS_BY_USER: {
                if (whineIfExists) {
                    Object[] arrobject = new Object[1];
                    arrobject[0] = needsCanEqual ? ", hashCode and canEquals" : " and hashCode";
                    String msg = String.format("Not generating equals%s: A method with one of those names already exists. (Either all or none of these methods will be generated).", arrobject);
                    source.addWarning(msg);
                }
                return;
            }
        }
        JCTree.JCMethodDecl equalsMethod = this.createEquals(typeNode, nodesForEquality.toList(), callSuper, fieldAccess, needsCanEqual, (JCTree)source.get());
        JavacHandlerUtil.injectMethod(typeNode, equalsMethod);
        if (needsCanEqual) {
            JCTree.JCMethodDecl canEqualMethod = this.createCanEqual(typeNode, (JCTree)source.get());
            JavacHandlerUtil.injectMethod(typeNode, canEqualMethod);
        }
        JCTree.JCMethodDecl hashCodeMethod = this.createHashCode(typeNode, nodesForEquality.toList(), callSuper, fieldAccess, (JCTree)source.get());
        JavacHandlerUtil.injectMethod(typeNode, hashCodeMethod);
    }

    private JCTree.JCMethodDecl createHashCode(JavacNode typeNode, List<JavacNode> fields, boolean callSuper, JavacHandlerUtil.FieldAccess fieldAccess, JCTree source) {
        TreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCAnnotation overrideAnnotation = maker.Annotation((JCTree)JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Override"), List.nil());
        JCTree.JCModifiers mods = maker.Modifiers(1, List.of((Object)overrideAnnotation));
        JCTree.JCPrimitiveTypeTree returnType = maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "INT"));
        ListBuffer statements = ListBuffer.lb();
        Name primeName = typeNode.toName("PRIME");
        Name resultName = typeNode.toName("result");
        if (!fields.isEmpty() || callSuper) {
            statements.append((Object)maker.VarDef(maker.Modifiers(16), primeName, (JCTree.JCExpression)maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "INT")), (JCTree.JCExpression)maker.Literal((Object)31)));
        }
        statements.append((Object)maker.VarDef(maker.Modifiers(0), resultName, (JCTree.JCExpression)maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "INT")), (JCTree.JCExpression)maker.Literal((Object)1)));
        if (callSuper) {
            JCTree.JCMethodInvocation callToSuper = maker.Apply(List.nil(), (JCTree.JCExpression)maker.Select((JCTree.JCExpression)maker.Ident(typeNode.toName("super")), typeNode.toName("hashCode")), List.nil());
            statements.append((Object)this.createResultCalculation(typeNode, (JCTree.JCExpression)callToSuper));
        }
        Name dollar = typeNode.toName("$");
        block6 : for (JavacNode fieldNode : fields) {
            Name dollarFieldName;
            JCTree.JCExpression fType = JavacHandlerUtil.getFieldType(fieldNode, fieldAccess);
            JCTree.JCExpression fieldAccessor = JavacHandlerUtil.createFieldAccessor(maker, fieldNode, fieldAccess);
            if (fType instanceof JCTree.JCPrimitiveTypeTree) {
                switch (((JCTree.JCPrimitiveTypeTree)fType).getPrimitiveTypeKind()) {
                    case BOOLEAN: {
                        statements.append((Object)this.createResultCalculation(typeNode, (JCTree.JCExpression)maker.Conditional(fieldAccessor, (JCTree.JCExpression)maker.Literal((Object)1231), (JCTree.JCExpression)maker.Literal((Object)1237))));
                        continue block6;
                    }
                    case LONG: {
                        dollarFieldName = dollar.append(((JCTree.JCVariableDecl)fieldNode.get()).name);
                        statements.append((Object)maker.VarDef(maker.Modifiers(16), dollarFieldName, (JCTree.JCExpression)maker.TypeIdent(5), fieldAccessor));
                        statements.append((Object)this.createResultCalculation(typeNode, this.longToIntForHashCode(maker, (JCTree.JCExpression)maker.Ident(dollarFieldName), (JCTree.JCExpression)maker.Ident(dollarFieldName))));
                        continue block6;
                    }
                    case FLOAT: {
                        statements.append((Object)this.createResultCalculation(typeNode, (JCTree.JCExpression)maker.Apply(List.nil(), JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Float", "floatToIntBits"), List.of((Object)fieldAccessor))));
                        continue block6;
                    }
                    case DOUBLE: {
                        dollarFieldName = dollar.append(((JCTree.JCVariableDecl)fieldNode.get()).name);
                        JCTree.JCMethodInvocation init = maker.Apply(List.nil(), JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Double", "doubleToLongBits"), List.of((Object)fieldAccessor));
                        statements.append((Object)maker.VarDef(maker.Modifiers(16), dollarFieldName, (JCTree.JCExpression)maker.TypeIdent(5), (JCTree.JCExpression)init));
                        statements.append((Object)this.createResultCalculation(typeNode, this.longToIntForHashCode(maker, (JCTree.JCExpression)maker.Ident(dollarFieldName), (JCTree.JCExpression)maker.Ident(dollarFieldName))));
                        continue block6;
                    }
                }
                statements.append((Object)this.createResultCalculation(typeNode, fieldAccessor));
                continue;
            }
            if (fType instanceof JCTree.JCArrayTypeTree) {
                boolean multiDim = ((JCTree.JCArrayTypeTree)fType).elemtype instanceof JCTree.JCArrayTypeTree;
                boolean primitiveArray = ((JCTree.JCArrayTypeTree)fType).elemtype instanceof JCTree.JCPrimitiveTypeTree;
                boolean useDeepHC = multiDim || !primitiveArray;
                String[] arrstring = new String[4];
                arrstring[0] = "java";
                arrstring[1] = "util";
                arrstring[2] = "Arrays";
                arrstring[3] = useDeepHC ? "deepHashCode" : "hashCode";
                JCTree.JCExpression hcMethod = JavacHandlerUtil.chainDots(typeNode, arrstring);
                statements.append((Object)this.createResultCalculation(typeNode, (JCTree.JCExpression)maker.Apply(List.nil(), hcMethod, List.of((Object)fieldAccessor))));
                continue;
            }
            dollarFieldName = dollar.append(((JCTree.JCVariableDecl)fieldNode.get()).name);
            statements.append((Object)maker.VarDef(maker.Modifiers(16), dollarFieldName, JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Object"), fieldAccessor));
            JCTree.JCMethodInvocation hcCall = maker.Apply(List.nil(), (JCTree.JCExpression)maker.Select((JCTree.JCExpression)maker.Ident(dollarFieldName), typeNode.toName("hashCode")), List.nil());
            JCTree.JCBinary thisEqualsNull = maker.Binary(Javac.getCtcInt(JCTree.class, "EQ"), (JCTree.JCExpression)maker.Ident(dollarFieldName), (JCTree.JCExpression)maker.Literal(Javac.getCtcInt(TypeTags.class, "BOT"), (Object)null));
            statements.append((Object)this.createResultCalculation(typeNode, (JCTree.JCExpression)maker.Conditional((JCTree.JCExpression)thisEqualsNull, (JCTree.JCExpression)maker.Literal((Object)0), (JCTree.JCExpression)hcCall)));
        }
        statements.append((Object)maker.Return((JCTree.JCExpression)maker.Ident(resultName)));
        JCTree.JCBlock body = maker.Block(0, statements.toList());
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName("hashCode"), (JCTree.JCExpression)returnType, List.nil(), List.nil(), List.nil(), body, null), source);
    }

    private JCTree.JCExpressionStatement createResultCalculation(JavacNode typeNode, JCTree.JCExpression expr) {
        TreeMaker maker = typeNode.getTreeMaker();
        Name resultName = typeNode.toName("result");
        JCTree.JCBinary mult = maker.Binary(Javac.getCtcInt(JCTree.class, "MUL"), (JCTree.JCExpression)maker.Ident(resultName), (JCTree.JCExpression)maker.Ident(typeNode.toName("PRIME")));
        JCTree.JCBinary add = maker.Binary(Javac.getCtcInt(JCTree.class, "PLUS"), (JCTree.JCExpression)mult, expr);
        return maker.Exec((JCTree.JCExpression)maker.Assign((JCTree.JCExpression)maker.Ident(resultName), (JCTree.JCExpression)add));
    }

    private JCTree.JCExpression longToIntForHashCode(TreeMaker maker, JCTree.JCExpression ref1, JCTree.JCExpression ref2) {
        JCTree.JCBinary shift = maker.Binary(Javac.getCtcInt(JCTree.class, "USR"), ref1, (JCTree.JCExpression)maker.Literal((Object)32));
        JCTree.JCBinary xorBits = maker.Binary(Javac.getCtcInt(JCTree.class, "BITXOR"), (JCTree.JCExpression)shift, ref2);
        return maker.TypeCast((JCTree)maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "INT")), (JCTree.JCExpression)xorBits);
    }

    private JCTree.JCExpression createTypeReference(JavacNode type) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(type.getName());
        for (JavacNode tNode = (JavacNode)type.up(); tNode != null && tNode.getKind() == AST.Kind.TYPE; tNode = (JavacNode)tNode.up()) {
            list.add(tNode.getName());
        }
        Collections.reverse(list);
        TreeMaker maker = type.getTreeMaker();
        JCTree.JCIdent chain = maker.Ident(type.toName((String)list.get(0)));
        for (int i = 1; i < list.size(); ++i) {
            chain = maker.Select((JCTree.JCExpression)chain, type.toName((String)list.get(i)));
        }
        return chain;
    }

    private JCTree.JCMethodDecl createEquals(JavacNode typeNode, List<JavacNode> fields, boolean callSuper, JavacHandlerUtil.FieldAccess fieldAccess, boolean needsCanEqual, JCTree source) {
        TreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCClassDecl type = (JCTree.JCClassDecl)typeNode.get();
        Name oName = typeNode.toName("o");
        Name otherName = typeNode.toName("other");
        Name thisName = typeNode.toName("this");
        JCTree.JCAnnotation overrideAnnotation = maker.Annotation((JCTree)JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Override"), List.nil());
        JCTree.JCModifiers mods = maker.Modifiers(1, List.of((Object)overrideAnnotation));
        JCTree.JCExpression objectType = JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Object");
        JCTree.JCPrimitiveTypeTree returnType = maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "BOOLEAN"));
        ListBuffer statements = ListBuffer.lb();
        List params = List.of((Object)maker.VarDef(maker.Modifiers(16), oName, objectType, null));
        statements.append((Object)maker.If((JCTree.JCExpression)maker.Binary(Javac.getCtcInt(JCTree.class, "EQ"), (JCTree.JCExpression)maker.Ident(oName), (JCTree.JCExpression)maker.Ident(thisName)), this.returnBool(maker, true), null));
        JCTree.JCUnary notInstanceOf = maker.Unary(Javac.getCtcInt(JCTree.class, "NOT"), (JCTree.JCExpression)maker.TypeTest((JCTree.JCExpression)maker.Ident(oName), (JCTree)this.createTypeReference(typeNode)));
        statements.append((Object)maker.If((JCTree.JCExpression)notInstanceOf, this.returnBool(maker, false), null));
        if (!fields.isEmpty() || needsCanEqual) {
            JCTree.JCIdent selfType1;
            JCTree.JCIdent selfType2;
            ListBuffer wildcards1 = ListBuffer.lb();
            ListBuffer wildcards2 = ListBuffer.lb();
            for (int i = 0; i < type.typarams.length(); ++i) {
                wildcards1.append((Object)maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null));
                wildcards2.append((Object)maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null));
            }
            if (type.typarams.isEmpty()) {
                selfType1 = maker.Ident(type.name);
                selfType2 = maker.Ident(type.name);
            } else {
                selfType1 = maker.TypeApply((JCTree.JCExpression)maker.Ident(type.name), wildcards1.toList());
                selfType2 = maker.TypeApply((JCTree.JCExpression)maker.Ident(type.name), wildcards2.toList());
            }
            statements.append((Object)maker.VarDef(maker.Modifiers(16), otherName, (JCTree.JCExpression)selfType1, (JCTree.JCExpression)maker.TypeCast((JCTree)selfType2, (JCTree.JCExpression)maker.Ident(oName))));
        }
        if (needsCanEqual) {
            List exprNil = List.nil();
            JCTree.JCIdent thisRef = maker.Ident(thisName);
            JCTree.JCTypeCast castThisRef = maker.TypeCast((JCTree)JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Object"), (JCTree.JCExpression)thisRef);
            JCTree.JCMethodInvocation equalityCheck = maker.Apply(exprNil, (JCTree.JCExpression)maker.Select((JCTree.JCExpression)maker.Ident(otherName), typeNode.toName("canEqual")), List.of((Object)castThisRef));
            statements.append((Object)maker.If((JCTree.JCExpression)maker.Unary(Javac.getCtcInt(JCTree.class, "NOT"), (JCTree.JCExpression)equalityCheck), this.returnBool(maker, false), null));
        }
        if (callSuper) {
            JCTree.JCMethodInvocation callToSuper = maker.Apply(List.nil(), (JCTree.JCExpression)maker.Select((JCTree.JCExpression)maker.Ident(typeNode.toName("super")), typeNode.toName("equals")), List.of((Object)maker.Ident(oName)));
            JCTree.JCUnary superNotEqual = maker.Unary(Javac.getCtcInt(JCTree.class, "NOT"), (JCTree.JCExpression)callToSuper);
            statements.append((Object)maker.If((JCTree.JCExpression)superNotEqual, this.returnBool(maker, false), null));
        }
        Name thisDollar = typeNode.toName("this$");
        Name otherDollar = typeNode.toName("other$");
        block5 : for (JavacNode fieldNode : fields) {
            JCTree.JCExpression fType = JavacHandlerUtil.getFieldType(fieldNode, fieldAccess);
            JCTree.JCExpression thisFieldAccessor = JavacHandlerUtil.createFieldAccessor(maker, fieldNode, fieldAccess);
            JCTree.JCExpression otherFieldAccessor = JavacHandlerUtil.createFieldAccessor(maker, fieldNode, fieldAccess, (JCTree.JCExpression)maker.Ident(otherName));
            if (fType instanceof JCTree.JCPrimitiveTypeTree) {
                switch (((JCTree.JCPrimitiveTypeTree)fType).getPrimitiveTypeKind()) {
                    case FLOAT: {
                        statements.append((Object)this.generateCompareFloatOrDouble(thisFieldAccessor, otherFieldAccessor, maker, typeNode, false));
                        continue block5;
                    }
                    case DOUBLE: {
                        statements.append((Object)this.generateCompareFloatOrDouble(thisFieldAccessor, otherFieldAccessor, maker, typeNode, true));
                        continue block5;
                    }
                }
                statements.append((Object)maker.If((JCTree.JCExpression)maker.Binary(Javac.getCtcInt(JCTree.class, "NE"), thisFieldAccessor, otherFieldAccessor), this.returnBool(maker, false), null));
                continue;
            }
            if (fType instanceof JCTree.JCArrayTypeTree) {
                boolean multiDim = ((JCTree.JCArrayTypeTree)fType).elemtype instanceof JCTree.JCArrayTypeTree;
                boolean primitiveArray = ((JCTree.JCArrayTypeTree)fType).elemtype instanceof JCTree.JCPrimitiveTypeTree;
                boolean useDeepEquals = multiDim || !primitiveArray;
                String[] arrstring = new String[4];
                arrstring[0] = "java";
                arrstring[1] = "util";
                arrstring[2] = "Arrays";
                arrstring[3] = useDeepEquals ? "deepEquals" : "equals";
                JCTree.JCExpression eqMethod = JavacHandlerUtil.chainDots(typeNode, arrstring);
                List args = List.of((Object)thisFieldAccessor, (Object)otherFieldAccessor);
                statements.append((Object)maker.If((JCTree.JCExpression)maker.Unary(Javac.getCtcInt(JCTree.class, "NOT"), (JCTree.JCExpression)maker.Apply(List.nil(), eqMethod, args)), this.returnBool(maker, false), null));
                continue;
            }
            Name fieldName = ((JCTree.JCVariableDecl)fieldNode.get()).name;
            Name thisDollarFieldName = thisDollar.append(fieldName);
            Name otherDollarFieldName = otherDollar.append(fieldName);
            statements.append((Object)maker.VarDef(maker.Modifiers(16), thisDollarFieldName, JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Object"), thisFieldAccessor));
            statements.append((Object)maker.VarDef(maker.Modifiers(16), otherDollarFieldName, JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Object"), otherFieldAccessor));
            JCTree.JCBinary thisEqualsNull = maker.Binary(Javac.getCtcInt(JCTree.class, "EQ"), (JCTree.JCExpression)maker.Ident(thisDollarFieldName), (JCTree.JCExpression)maker.Literal(Javac.getCtcInt(TypeTags.class, "BOT"), (Object)null));
            JCTree.JCBinary otherNotEqualsNull = maker.Binary(Javac.getCtcInt(JCTree.class, "NE"), (JCTree.JCExpression)maker.Ident(otherDollarFieldName), (JCTree.JCExpression)maker.Literal(Javac.getCtcInt(TypeTags.class, "BOT"), (Object)null));
            JCTree.JCMethodInvocation thisEqualsThat = maker.Apply(List.nil(), (JCTree.JCExpression)maker.Select((JCTree.JCExpression)maker.Ident(thisDollarFieldName), typeNode.toName("equals")), List.of((Object)maker.Ident(otherDollarFieldName)));
            JCTree.JCConditional fieldsAreNotEqual = maker.Conditional((JCTree.JCExpression)thisEqualsNull, (JCTree.JCExpression)otherNotEqualsNull, (JCTree.JCExpression)maker.Unary(Javac.getCtcInt(JCTree.class, "NOT"), (JCTree.JCExpression)thisEqualsThat));
            statements.append((Object)maker.If((JCTree.JCExpression)fieldsAreNotEqual, this.returnBool(maker, false), null));
        }
        statements.append((Object)this.returnBool(maker, true));
        JCTree.JCBlock body = maker.Block(0, statements.toList());
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName("equals"), (JCTree.JCExpression)returnType, List.nil(), params, List.nil(), body, null), source);
    }

    private JCTree.JCMethodDecl createCanEqual(JavacNode typeNode, JCTree source) {
        TreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCModifiers mods = maker.Modifiers(1, List.nil());
        JCTree.JCPrimitiveTypeTree returnType = maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "BOOLEAN"));
        Name canEqualName = typeNode.toName("canEqual");
        JCTree.JCExpression objectType = JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Object");
        Name otherName = typeNode.toName("other");
        List params = List.of((Object)maker.VarDef(maker.Modifiers(16), otherName, objectType, null));
        JCTree.JCBlock body = maker.Block(0, List.of((Object)maker.Return((JCTree.JCExpression)maker.TypeTest((JCTree.JCExpression)maker.Ident(otherName), (JCTree)this.createTypeReference(typeNode)))));
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, canEqualName, (JCTree.JCExpression)returnType, List.nil(), params, List.nil(), body, null), source);
    }

    private JCTree.JCStatement generateCompareFloatOrDouble(JCTree.JCExpression thisDotField, JCTree.JCExpression otherDotField, TreeMaker maker, JavacNode node, boolean isDouble) {
        String[] arrstring = new String[3];
        arrstring[0] = "java";
        arrstring[1] = "lang";
        arrstring[2] = isDouble ? "Double" : "Float";
        JCTree.JCExpression clazz = JavacHandlerUtil.chainDots(node, arrstring);
        List args = List.of((Object)thisDotField, (Object)otherDotField);
        JCTree.JCBinary compareCallEquals0 = maker.Binary(Javac.getCtcInt(JCTree.class, "NE"), (JCTree.JCExpression)maker.Apply(List.nil(), (JCTree.JCExpression)maker.Select(clazz, node.toName("compare")), args), (JCTree.JCExpression)maker.Literal((Object)0));
        return maker.If((JCTree.JCExpression)compareCallEquals0, this.returnBool(maker, false), null);
    }

    private JCTree.JCStatement returnBool(TreeMaker maker, boolean bool) {
        return maker.Return((JCTree.JCExpression)maker.Literal(Javac.getCtcInt(TypeTags.class, "BOOLEAN"), (Object)(bool ? 1 : 0)));
    }

}

