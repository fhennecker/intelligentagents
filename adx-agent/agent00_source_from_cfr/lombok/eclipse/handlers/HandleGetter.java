/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.CompilationResult
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Assignment
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.EqualExpression
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.IfStatement
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.NullLiteral
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 */
package lombok.eclipse.handlers;

import java.util.ArrayList;
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
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.agent.PatchDelegate;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HandleGetter
extends EclipseAnnotationHandler<Getter> {
    private static final Annotation[] EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];
    private static final char[][] AR = Eclipse.fromQualifiedName("java.util.concurrent.atomic.AtomicReference");
    private static final TypeReference[][] AR_PARAMS = new TypeReference[5][];
    private static final Map<String, char[][]> TYPE_MAP;
    private static char[] valueName;
    private static char[] actualValueName;

    public boolean generateGetterForType(EclipseNode typeNode, EclipseNode pos, AccessLevel level, boolean checkForTypeLevelGetter) {
        boolean notAClass;
        if (checkForTypeLevelGetter && typeNode != null) {
            for (EclipseNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(Getter.class, child)) continue;
                return true;
            }
        }
        TypeDeclaration typeDecl = null;
        if (typeNode.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)typeNode.get();
        }
        int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
        boolean bl = notAClass = (modifiers & 8704) != 0;
        if (typeDecl == null || notAClass) {
            pos.addError("@Getter is only supported on a class, an enum, or a field.");
            return false;
        }
        for (EclipseNode field : typeNode.down()) {
            if (!this.fieldQualifiesForGetterGeneration(field)) continue;
            this.generateGetterForField(field, (ASTNode)pos.get(), level, false);
        }
        return true;
    }

    public boolean fieldQualifiesForGetterGeneration(EclipseNode field) {
        if (field.getKind() != AST.Kind.FIELD) {
            return false;
        }
        FieldDeclaration fieldDecl = (FieldDeclaration)field.get();
        return EclipseHandlerUtil.filterField(fieldDecl);
    }

    public void generateGetterForField(EclipseNode fieldNode, ASTNode pos, AccessLevel level, boolean lazy) {
        for (EclipseNode child : fieldNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(Getter.class, child)) continue;
            return;
        }
        this.createGetterForField(level, fieldNode, fieldNode, pos, false, lazy);
    }

    @Override
    public void handle(AnnotationValues<Getter> annotation, Annotation ast, EclipseNode annotationNode) {
        EclipseNode node = (EclipseNode)annotationNode.up();
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
                this.createGetterForFields(level, annotationNode.upFromAnnotationToFields(), annotationNode, (ASTNode)annotationNode.get(), true, lazy);
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

    private void createGetterForFields(AccessLevel level, Collection<EclipseNode> fieldNodes, EclipseNode errorNode, ASTNode source, boolean whineIfExists, boolean lazy) {
        for (EclipseNode fieldNode : fieldNodes) {
            this.createGetterForField(level, fieldNode, errorNode, source, whineIfExists, lazy);
        }
    }

    private void createGetterForField(AccessLevel level, EclipseNode fieldNode, EclipseNode errorNode, ASTNode source, boolean whineIfExists, boolean lazy) {
        TypeReference fieldType;
        String getterName;
        Annotation[] copiedAnnotations;
        boolean isBoolean;
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            errorNode.addError("@Getter is only supported on a class or a field.");
            return;
        }
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        if (lazy) {
            if ((field.modifiers & 2) == 0 || (field.modifiers & 16) == 0) {
                errorNode.addError("'lazy' requires the field to be private and final.");
                return;
            }
            if (field.initialization == null) {
                errorNode.addError("'lazy' requires field initialization.");
                return;
            }
        }
        if ((getterName = EclipseHandlerUtil.toGetterName(fieldNode, isBoolean = Eclipse.nameEquals((fieldType = EclipseHandlerUtil.copyType(field.type, source)).getTypeName(), "boolean") && fieldType.dimensions() == 0)) == null) {
            errorNode.addWarning("Not generating getter for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        int modifier = EclipseHandlerUtil.toEclipseModifier(level) | field.modifiers & 8;
        for (String altName : EclipseHandlerUtil.toAllGetterNames(fieldNode, isBoolean)) {
            switch (EclipseHandlerUtil.methodExists(altName, fieldNode, false, 0)) {
                case EXISTS_BY_LOMBOK: {
                    return;
                }
                case EXISTS_BY_USER: {
                    if (whineIfExists) {
                        String altNameExpl = "";
                        if (!altName.equals(getterName)) {
                            altNameExpl = String.format(" (%s)", altName);
                        }
                        errorNode.addWarning(String.format("Not generating %s(): A method with that name already exists%s", getterName, altNameExpl));
                    }
                    return;
                }
            }
        }
        MethodDeclaration method = this.generateGetter((TypeDeclaration)((EclipseNode)fieldNode.up()).get(), fieldNode, getterName, modifier, source, lazy);
        Annotation[] deprecated = null;
        if (EclipseHandlerUtil.isFieldDeprecated(fieldNode)) {
            deprecated = new Annotation[]{EclipseHandlerUtil.generateDeprecatedAnnotation(source)};
        }
        if ((copiedAnnotations = EclipseHandlerUtil.copyAnnotations(source, Eclipse.findAnnotations(field, TransformationsUtil.NON_NULL_PATTERN), Eclipse.findAnnotations(field, TransformationsUtil.NULLABLE_PATTERN), HandleGetter.findDelegatesAndMarkAsHandled(fieldNode), deprecated)).length != 0) {
            method.annotations = copiedAnnotations;
        }
        EclipseHandlerUtil.injectMethod((EclipseNode)fieldNode.up(), (AbstractMethodDeclaration)method);
    }

    private static Annotation[] findDelegatesAndMarkAsHandled(EclipseNode fieldNode) {
        ArrayList<Annotation> delegates = new ArrayList<Annotation>();
        for (EclipseNode child : fieldNode.down()) {
            if (!EclipseHandlerUtil.annotationTypeMatches(Delegate.class, child)) continue;
            Annotation delegate = (Annotation)child.get();
            PatchDelegate.markHandled(delegate);
            delegates.add(delegate);
        }
        return delegates.toArray((T[])EMPTY_ANNOTATIONS_ARRAY);
    }

    private MethodDeclaration generateGetter(TypeDeclaration parent, EclipseNode fieldNode, String name, int modifier, ASTNode source, boolean lazy) {
        TypeReference returnType = EclipseHandlerUtil.copyType(((FieldDeclaration)fieldNode.get()).type, source);
        Statement[] statements = lazy ? this.createLazyGetterBody(source, fieldNode) : this.createSimpleGetterBody(source, fieldNode);
        MethodDeclaration method = new MethodDeclaration(parent.compilationResult);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method, source);
        method.modifiers = modifier;
        method.returnType = returnType;
        method.annotations = null;
        method.arguments = null;
        method.selector = name.toCharArray();
        method.binding = null;
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 8388608;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        method.statements = statements;
        EclipseHandlerUtil.registerCreatedLazyGetter((FieldDeclaration)fieldNode.get(), method.selector, returnType);
        return method;
    }

    private Statement[] createSimpleGetterBody(ASTNode source, EclipseNode fieldNode) {
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        Expression fieldRef = EclipseHandlerUtil.createFieldAccessor(fieldNode, EclipseHandlerUtil.FieldAccess.ALWAYS_FIELD, source);
        ReturnStatement returnStatement = new ReturnStatement(fieldRef, field.sourceStart, field.sourceEnd);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)returnStatement, source);
        return new Statement[]{returnStatement};
    }

    private Statement[] createLazyGetterBody(ASTNode source, EclipseNode fieldNode) {
        char[][] newType;
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        TypeReference componentType = EclipseHandlerUtil.copyType(field.type, source);
        if (field.type instanceof SingleTypeReference && !(field.type instanceof ArrayTypeReference) && (newType = TYPE_MAP.get(new String(((SingleTypeReference)field.type).token))) != null) {
            componentType = new QualifiedTypeReference(newType, Eclipse.poss(source, 3));
            EclipseHandlerUtil.setGeneratedBy((ASTNode)componentType, source);
        }
        Statement[] statements = new Statement[3];
        LocalDeclaration valueDecl = new LocalDeclaration(valueName, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)valueDecl, source);
        TypeReference[][] typeParams = (TypeReference[][])AR_PARAMS.clone();
        typeParams[4] = new TypeReference[]{EclipseHandlerUtil.copyType(componentType, source)};
        valueDecl.type = new ParameterizedQualifiedTypeReference(AR, typeParams, 0, Eclipse.poss(source, 5));
        valueDecl.type.sourceStart = pS;
        valueDecl.type.sourceEnd = valueDecl.type.statementEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)valueDecl.type, source);
        MessageSend getter = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)getter, source);
        getter.sourceStart = pS;
        getter.statementEnd = getter.sourceEnd = pE;
        getter.selector = new char[]{'g', 'e', 't'};
        getter.receiver = EclipseHandlerUtil.createFieldAccessor(fieldNode, EclipseHandlerUtil.FieldAccess.ALWAYS_FIELD, source);
        valueDecl.initialization = getter;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)valueDecl.initialization, source);
        statements[0] = valueDecl;
        EqualExpression cond = new EqualExpression((Expression)new SingleNameReference(valueName, p), (Expression)new NullLiteral(pS, pE), 18);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)cond.left, source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)cond.right, source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)cond, source);
        Block then = new Block(0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)then, source);
        Expression lock = EclipseHandlerUtil.createFieldAccessor(fieldNode, EclipseHandlerUtil.FieldAccess.ALWAYS_FIELD, source);
        Block inner = new Block(0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)inner, source);
        inner.statements = new Statement[2];
        MessageSend getter2 = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)getter2, source);
        getter2.sourceStart = pS;
        getter2.sourceEnd = getter2.statementEnd = pE;
        getter2.selector = new char[]{'g', 'e', 't'};
        getter2.receiver = EclipseHandlerUtil.createFieldAccessor(fieldNode, EclipseHandlerUtil.FieldAccess.ALWAYS_FIELD, source);
        Assignment assign = new Assignment((Expression)new SingleNameReference(valueName, p), (Expression)getter2, pE);
        assign.sourceStart = pS;
        assign.statementEnd = assign.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)assign, source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)assign.lhs, source);
        inner.statements[0] = assign;
        EqualExpression innerCond = new EqualExpression((Expression)new SingleNameReference(valueName, p), (Expression)new NullLiteral(pS, pE), 18);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)innerCond.left, source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)innerCond.right, source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)innerCond, source);
        Block innerThen = new Block(0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)innerThen, source);
        innerThen.statements = new Statement[3];
        LocalDeclaration actualValueDecl = new LocalDeclaration(actualValueName, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)actualValueDecl, source);
        actualValueDecl.type = EclipseHandlerUtil.copyType(field.type, source);
        actualValueDecl.type.sourceStart = pS;
        actualValueDecl.type.sourceEnd = actualValueDecl.type.statementEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)actualValueDecl.type, source);
        actualValueDecl.initialization = field.initialization;
        actualValueDecl.modifiers = 16;
        innerThen.statements[0] = actualValueDecl;
        AllocationExpression create = new AllocationExpression();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)create, source);
        create.sourceStart = pS;
        create.sourceEnd = create.statementEnd = pE;
        TypeReference[][] typeParams2 = (TypeReference[][])AR_PARAMS.clone();
        typeParams2[4] = new TypeReference[]{EclipseHandlerUtil.copyType(componentType, source)};
        create.type = new ParameterizedQualifiedTypeReference(AR, typeParams2, 0, Eclipse.poss(source, 5));
        create.type.sourceStart = pS;
        create.type.sourceEnd = create.type.statementEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)create.type, source);
        create.arguments = new Expression[]{new SingleNameReference(actualValueName, p)};
        EclipseHandlerUtil.setGeneratedBy((ASTNode)create.arguments[0], source);
        Assignment innerAssign = new Assignment((Expression)new SingleNameReference(valueName, p), (Expression)create, pE);
        innerAssign.sourceStart = pS;
        innerAssign.statementEnd = innerAssign.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)innerAssign, source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)innerAssign.lhs, source);
        innerThen.statements[1] = innerAssign;
        MessageSend setter = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)setter, source);
        setter.sourceStart = pS;
        setter.sourceEnd = setter.statementEnd = pE;
        setter.receiver = EclipseHandlerUtil.createFieldAccessor(fieldNode, EclipseHandlerUtil.FieldAccess.ALWAYS_FIELD, source);
        setter.selector = new char[]{'s', 'e', 't'};
        setter.arguments = new Expression[]{new SingleNameReference(valueName, p)};
        EclipseHandlerUtil.setGeneratedBy((ASTNode)setter.arguments[0], source);
        innerThen.statements[2] = setter;
        IfStatement innerIf = new IfStatement((Expression)innerCond, (Statement)innerThen, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)innerIf, source);
        inner.statements[1] = innerIf;
        SynchronizedStatement sync = new SynchronizedStatement(lock, inner, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)sync, source);
        then.statements = new Statement[]{sync};
        IfStatement ifStatement = new IfStatement((Expression)cond, (Statement)then, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ifStatement, source);
        statements[1] = ifStatement;
        MessageSend getter3 = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)getter3, source);
        getter3.sourceStart = pS;
        getter3.sourceEnd = getter3.statementEnd = pE;
        getter3.selector = new char[]{'g', 'e', 't'};
        getter3.receiver = new SingleNameReference(valueName, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)getter3.receiver, source);
        statements[2] = new ReturnStatement((Expression)getter3, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)statements[2], source);
        LocalDeclaration first = (LocalDeclaration)statements[0];
        TypeReference innerType = EclipseHandlerUtil.copyType(first.type, source);
        TypeReference[][] typeParams3 = (TypeReference[][])AR_PARAMS.clone();
        typeParams3[4] = new TypeReference[]{EclipseHandlerUtil.copyType(innerType, source)};
        ParameterizedQualifiedTypeReference type = new ParameterizedQualifiedTypeReference(AR, typeParams3, 0, Eclipse.poss(source, 5));
        type.sourceStart = -1;
        type.sourceEnd = -2;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)type, source);
        field.type = type;
        AllocationExpression init = new AllocationExpression();
        init.sourceStart = field.initialization.sourceStart;
        init.sourceEnd = init.statementEnd = field.initialization.sourceEnd;
        init.type = EclipseHandlerUtil.copyType((TypeReference)type, source);
        field.initialization = init;
        return statements;
    }

    static {
        HashMap<String, char[][]> m = new HashMap<String, char[][]>();
        m.put("int", Eclipse.fromQualifiedName("java.lang.Integer"));
        m.put("double", Eclipse.fromQualifiedName("java.lang.Double"));
        m.put("float", Eclipse.fromQualifiedName("java.lang.Float"));
        m.put("short", Eclipse.fromQualifiedName("java.lang.Short"));
        m.put("byte", Eclipse.fromQualifiedName("java.lang.Byte"));
        m.put("long", Eclipse.fromQualifiedName("java.lang.Long"));
        m.put("boolean", Eclipse.fromQualifiedName("java.lang.Boolean"));
        m.put("char", Eclipse.fromQualifiedName("java.lang.Character"));
        TYPE_MAP = Collections.unmodifiableMap(m);
        valueName = "value".toCharArray();
        actualValueName = "actualValue".toCharArray();
    }

}

