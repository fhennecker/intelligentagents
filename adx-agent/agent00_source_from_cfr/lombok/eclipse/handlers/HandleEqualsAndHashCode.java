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
 *  org.eclipse.jdt.internal.compiler.ast.Assignment
 *  org.eclipse.jdt.internal.compiler.ast.BinaryExpression
 *  org.eclipse.jdt.internal.compiler.ast.CastExpression
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ConditionalExpression
 *  org.eclipse.jdt.internal.compiler.ast.EqualExpression
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FalseLiteral
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.IfStatement
 *  org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression
 *  org.eclipse.jdt.internal.compiler.ast.IntLiteral
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.NameReference
 *  org.eclipse.jdt.internal.compiler.ast.NullLiteral
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.SuperReference
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.TrueLiteral
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.ast.UnaryExpression
 *  org.eclipse.jdt.internal.compiler.ast.Wildcard
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.TypeConstants
 */
package lombok.eclipse.handlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HandleEqualsAndHashCode
extends EclipseAnnotationHandler<EqualsAndHashCode> {
    private final char[] PRIME = "PRIME".toCharArray();
    private final char[] RESULT = "result".toCharArray();
    private static final Set<String> BUILT_IN_TYPES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("byte", "short", "int", "long", "char", "boolean", "double", "float")));

    private void checkForBogusFieldNames(EclipseNode type, AnnotationValues<EqualsAndHashCode> annotation) {
        Iterator<Integer> i$;
        int i;
        if (annotation.isExplicit("exclude")) {
            i$ = EclipseHandlerUtil.createListOfNonExistentFields(Arrays.asList(annotation.getInstance().exclude()), type, true, true).iterator();
            while (i$.hasNext()) {
                i = i$.next();
                annotation.setWarning("exclude", "This field does not exist, or would have been excluded anyway.", i);
            }
        }
        if (annotation.isExplicit("of")) {
            i$ = EclipseHandlerUtil.createListOfNonExistentFields(Arrays.asList(annotation.getInstance().of()), type, false, false).iterator();
            while (i$.hasNext()) {
                i = i$.next();
                annotation.setWarning("of", "This field does not exist.", i);
            }
        }
    }

    public void generateEqualsAndHashCodeForType(EclipseNode typeNode, EclipseNode errorNode, Boolean callSuper) {
        for (EclipseNode child : typeNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(EqualsAndHashCode.class, child)) continue;
            return;
        }
        this.generateMethods(typeNode, errorNode, null, null, callSuper, false, EclipseHandlerUtil.FieldAccess.GETTER);
    }

    @Override
    public void handle(AnnotationValues<EqualsAndHashCode> annotation, Annotation ast, EclipseNode annotationNode) {
        EqualsAndHashCode ann = annotation.getInstance();
        List<String> excludes = Arrays.asList(ann.exclude());
        List<String> includes = Arrays.asList(ann.of());
        EclipseNode typeNode = (EclipseNode)annotationNode.up();
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
        EclipseHandlerUtil.FieldAccess fieldAccess = ann.doNotUseGetters() ? EclipseHandlerUtil.FieldAccess.PREFER_FIELD : EclipseHandlerUtil.FieldAccess.GETTER;
        this.generateMethods(typeNode, annotationNode, excludes, includes, callSuper, true, fieldAccess);
    }

    public void generateMethods(EclipseNode typeNode, EclipseNode errorNode, List<String> excludes, List<String> includes, Boolean callSuper, boolean whineIfExists, EclipseHandlerUtil.FieldAccess fieldAccess) {
        boolean notAClass;
        FieldDeclaration fieldDecl;
        boolean implicitCallSuper;
        assert (excludes == null || includes == null);
        TypeDeclaration typeDecl = null;
        if (typeNode.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)typeNode.get();
        }
        int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
        boolean bl = notAClass = (modifiers & 25088) != 0;
        if (typeDecl == null || notAClass) {
            errorNode.addError("@EqualsAndHashCode is only supported on a class.");
            return;
        }
        boolean bl2 = implicitCallSuper = callSuper == null;
        if (callSuper == null) {
            try {
                callSuper = (boolean)((Boolean)EqualsAndHashCode.class.getMethod("callSuper", new Class[0]).getDefaultValue());
            }
            catch (Exception ignore) {
                throw new InternalError("Lombok bug - this cannot happen - can't find callSuper field in EqualsAndHashCode annotation.");
            }
        }
        boolean isDirectDescendantOfObject = true;
        if (typeDecl.superclass != null) {
            String p = typeDecl.superclass.toString();
            boolean bl3 = isDirectDescendantOfObject = p.equals("Object") || p.equals("java.lang.Object");
        }
        if (isDirectDescendantOfObject && callSuper.booleanValue()) {
            errorNode.addError("Generating equals/hashCode with a supercall to java.lang.Object is pointless.");
            return;
        }
        if (!isDirectDescendantOfObject && !callSuper.booleanValue() && implicitCallSuper) {
            errorNode.addWarning("Generating equals/hashCode implementation but without a call to superclass, even though this class does not extend java.lang.Object. If this is intentional, add '@EqualsAndHashCode(callSuper=false)' to your type.");
        }
        ArrayList<EclipseNode> nodesForEquality = new ArrayList<EclipseNode>();
        if (includes != null) {
            for (EclipseNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.FIELD) continue;
                fieldDecl = (FieldDeclaration)child.get();
                if (!includes.contains(new String(fieldDecl.name))) continue;
                nodesForEquality.add(child);
            }
        } else {
            for (EclipseNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)child.get()) || (fieldDecl.modifiers & 128) != 0 || excludes != null && excludes.contains(new String(fieldDecl.name))) continue;
                nodesForEquality.add(child);
            }
        }
        boolean isFinal = (typeDecl.modifiers & 16) != 0;
        boolean needsCanEqual = !isDirectDescendantOfObject || !isFinal;
        ArrayList<EclipseHandlerUtil.MemberExistsResult> existsResults = new ArrayList<EclipseHandlerUtil.MemberExistsResult>();
        existsResults.add(EclipseHandlerUtil.methodExists("equals", typeNode, 1));
        existsResults.add(EclipseHandlerUtil.methodExists("hashCode", typeNode, 0));
        existsResults.add(EclipseHandlerUtil.methodExists("canEqual", typeNode, 1));
        switch ((EclipseHandlerUtil.MemberExistsResult)((Object)Collections.max(existsResults))) {
            case EXISTS_BY_LOMBOK: {
                return;
            }
            case EXISTS_BY_USER: {
                if (whineIfExists) {
                    Object[] arrobject = new Object[1];
                    arrobject[0] = needsCanEqual ? ", hashCode and canEquals" : " and hashCode";
                    String msg = String.format("Not generating equals%s: A method with one of those names already exists. (Either all or none of these methods will be generated).", arrobject);
                    errorNode.addWarning(msg);
                }
                return;
            }
        }
        MethodDeclaration equalsMethod = this.createEquals(typeNode, nodesForEquality, callSuper, (ASTNode)errorNode.get(), fieldAccess, needsCanEqual);
        equalsMethod.traverse((ASTVisitor)new SetGeneratedByVisitor((ASTNode)errorNode.get()), ((TypeDeclaration)typeNode.get()).scope);
        EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)equalsMethod);
        if (needsCanEqual) {
            MethodDeclaration canEqualMethod = this.createCanEqual(typeNode, (ASTNode)errorNode.get());
            canEqualMethod.traverse((ASTVisitor)new SetGeneratedByVisitor((ASTNode)errorNode.get()), ((TypeDeclaration)typeNode.get()).scope);
            EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)canEqualMethod);
        }
        MethodDeclaration hashCodeMethod = this.createHashCode(typeNode, nodesForEquality, callSuper, (ASTNode)errorNode.get(), fieldAccess);
        hashCodeMethod.traverse((ASTVisitor)new SetGeneratedByVisitor((ASTNode)errorNode.get()), ((TypeDeclaration)typeNode.get()).scope);
        EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)hashCodeMethod);
    }

    private MethodDeclaration createHashCode(EclipseNode type, Collection<EclipseNode> fields, boolean callSuper, ASTNode source, EclipseHandlerUtil.FieldAccess fieldAccess) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        MethodDeclaration method = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method, source);
        method.modifiers = EclipseHandlerUtil.toEclipseModifier(AccessLevel.PUBLIC);
        method.returnType = TypeReference.baseTypeReference((int)10, (int)0);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method.returnType, source);
        method.annotations = new Annotation[]{EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, source)};
        method.selector = "hashCode".toCharArray();
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 8388608;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        method.arguments = null;
        ArrayList<Object> statements = new ArrayList<Object>();
        boolean isEmpty = fields.isEmpty();
        if (!isEmpty || callSuper) {
            LocalDeclaration primeDecl = new LocalDeclaration(this.PRIME, pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)primeDecl, source);
            primeDecl.modifiers |= 16;
            primeDecl.type = TypeReference.baseTypeReference((int)10, (int)0);
            primeDecl.type.sourceStart = pS;
            primeDecl.type.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)primeDecl.type, source);
            primeDecl.initialization = EclipseHandlerUtil.makeIntLiteral("31".toCharArray(), source);
            statements.add((Object)primeDecl);
        }
        LocalDeclaration resultDecl = new LocalDeclaration(this.RESULT, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)resultDecl, source);
        resultDecl.initialization = EclipseHandlerUtil.makeIntLiteral("1".toCharArray(), source);
        resultDecl.type = TypeReference.baseTypeReference((int)10, (int)0);
        resultDecl.type.sourceStart = pS;
        resultDecl.type.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)resultDecl.type, source);
        statements.add((Object)resultDecl);
        if (callSuper) {
            MessageSend callToSuper = new MessageSend();
            EclipseHandlerUtil.setGeneratedBy((ASTNode)callToSuper, source);
            callToSuper.sourceStart = pS;
            callToSuper.sourceEnd = pE;
            callToSuper.receiver = new SuperReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)callToSuper.receiver, source);
            callToSuper.selector = "hashCode".toCharArray();
            statements.add((Object)this.createResultCalculation(source, (Expression)callToSuper));
        }
        for (EclipseNode field : fields) {
            TypeReference fType = EclipseHandlerUtil.getFieldType(field, fieldAccess);
            char[] dollarFieldName = ("$" + field.getName()).toCharArray();
            char[] token = fType.getLastToken();
            Expression fieldAccessor = EclipseHandlerUtil.createFieldAccessor(field, fieldAccess, source);
            if (fType.dimensions() == 0 && token != null) {
                SingleNameReference copy2;
                SingleNameReference copy1;
                if (Arrays.equals(TypeConstants.BOOLEAN, token)) {
                    IntLiteral int1231 = EclipseHandlerUtil.makeIntLiteral("1231".toCharArray(), source);
                    IntLiteral int1237 = EclipseHandlerUtil.makeIntLiteral("1237".toCharArray(), source);
                    ConditionalExpression int1231or1237 = new ConditionalExpression(fieldAccessor, (Expression)int1231, (Expression)int1237);
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)int1231or1237, source);
                    statements.add((Object)this.createResultCalculation(source, (Expression)int1231or1237));
                    continue;
                }
                if (Arrays.equals(TypeConstants.LONG, token)) {
                    statements.add((Object)this.createLocalDeclaration(source, dollarFieldName, TypeReference.baseTypeReference((int)7, (int)0), fieldAccessor));
                    copy1 = new SingleNameReference(dollarFieldName, p);
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)copy1, source);
                    copy2 = new SingleNameReference(dollarFieldName, p);
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)copy2, source);
                    statements.add((Object)this.createResultCalculation(source, this.longToIntForHashCode((Expression)copy1, (Expression)copy2, source)));
                    continue;
                }
                if (Arrays.equals(TypeConstants.FLOAT, token)) {
                    MessageSend floatToIntBits = new MessageSend();
                    floatToIntBits.sourceStart = pS;
                    floatToIntBits.sourceEnd = pE;
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)floatToIntBits, source);
                    floatToIntBits.receiver = this.generateQualifiedNameRef(source, TypeConstants.JAVA_LANG_FLOAT);
                    floatToIntBits.selector = "floatToIntBits".toCharArray();
                    floatToIntBits.arguments = new Expression[]{fieldAccessor};
                    statements.add((Object)this.createResultCalculation(source, (Expression)floatToIntBits));
                    continue;
                }
                if (Arrays.equals(TypeConstants.DOUBLE, token)) {
                    MessageSend doubleToLongBits = new MessageSend();
                    doubleToLongBits.sourceStart = pS;
                    doubleToLongBits.sourceEnd = pE;
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)doubleToLongBits, source);
                    doubleToLongBits.receiver = this.generateQualifiedNameRef(source, TypeConstants.JAVA_LANG_DOUBLE);
                    doubleToLongBits.selector = "doubleToLongBits".toCharArray();
                    doubleToLongBits.arguments = new Expression[]{fieldAccessor};
                    statements.add((Object)this.createLocalDeclaration(source, dollarFieldName, TypeReference.baseTypeReference((int)7, (int)0), (Expression)doubleToLongBits));
                    SingleNameReference copy12 = new SingleNameReference(dollarFieldName, p);
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)copy12, source);
                    SingleNameReference copy22 = new SingleNameReference(dollarFieldName, p);
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)copy22, source);
                    statements.add((Object)this.createResultCalculation(source, this.longToIntForHashCode((Expression)copy12, (Expression)copy22, source)));
                    continue;
                }
                if (BUILT_IN_TYPES.contains(new String(token))) {
                    statements.add((Object)this.createResultCalculation(source, fieldAccessor));
                    continue;
                }
                statements.add((Object)this.createLocalDeclaration(source, dollarFieldName, this.generateQualifiedTypeRef(source, TypeConstants.JAVA_LANG_OBJECT), fieldAccessor));
                copy1 = new SingleNameReference(dollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)copy1, source);
                copy2 = new SingleNameReference(dollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)copy2, source);
                MessageSend hashCodeCall = new MessageSend();
                hashCodeCall.sourceStart = pS;
                hashCodeCall.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy((ASTNode)hashCodeCall, source);
                hashCodeCall.receiver = copy1;
                hashCodeCall.selector = "hashCode".toCharArray();
                NullLiteral nullLiteral = new NullLiteral(pS, pE);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)nullLiteral, source);
                EqualExpression objIsNull = new EqualExpression((Expression)copy2, (Expression)nullLiteral, 18);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)objIsNull, source);
                IntLiteral int0 = EclipseHandlerUtil.makeIntLiteral("0".toCharArray(), source);
                ConditionalExpression nullOrHashCode = new ConditionalExpression((Expression)objIsNull, (Expression)int0, (Expression)hashCodeCall);
                nullOrHashCode.sourceStart = pS;
                nullOrHashCode.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy((ASTNode)nullOrHashCode, source);
                statements.add((Object)this.createResultCalculation(source, (Expression)nullOrHashCode));
                continue;
            }
            if (fType.dimensions() <= 0 || token == null) continue;
            MessageSend arraysHashCodeCall = new MessageSend();
            arraysHashCodeCall.sourceStart = pS;
            arraysHashCodeCall.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)arraysHashCodeCall, source);
            arraysHashCodeCall.receiver = this.generateQualifiedNameRef(source, TypeConstants.JAVA, TypeConstants.UTIL, "Arrays".toCharArray());
            arraysHashCodeCall.selector = fType.dimensions() > 1 || !BUILT_IN_TYPES.contains(new String(token)) ? "deepHashCode".toCharArray() : "hashCode".toCharArray();
            arraysHashCodeCall.arguments = new Expression[]{fieldAccessor};
            statements.add((Object)this.createResultCalculation(source, (Expression)arraysHashCodeCall));
        }
        SingleNameReference resultRef = new SingleNameReference(this.RESULT, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)resultRef, source);
        ReturnStatement returnStatement = new ReturnStatement((Expression)resultRef, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)returnStatement, source);
        statements.add((Object)returnStatement);
        method.statements = statements.toArray((T[])new Statement[statements.size()]);
        return method;
    }

    private LocalDeclaration createLocalDeclaration(ASTNode source, char[] dollarFieldName, TypeReference type, Expression initializer) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        LocalDeclaration tempVar = new LocalDeclaration(dollarFieldName, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)tempVar, source);
        tempVar.initialization = initializer;
        tempVar.type = type;
        tempVar.type.sourceStart = pS;
        tempVar.type.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)tempVar.type, source);
        tempVar.modifiers = 16;
        return tempVar;
    }

    private Expression createResultCalculation(ASTNode source, Expression ex) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        SingleNameReference resultRef = new SingleNameReference(this.RESULT, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)resultRef, source);
        SingleNameReference primeRef = new SingleNameReference(this.PRIME, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)primeRef, source);
        BinaryExpression multiplyByPrime = new BinaryExpression((Expression)resultRef, (Expression)primeRef, 15);
        multiplyByPrime.sourceStart = pS;
        multiplyByPrime.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)multiplyByPrime, source);
        BinaryExpression addItem = new BinaryExpression((Expression)multiplyByPrime, ex, 14);
        addItem.sourceStart = pS;
        addItem.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)addItem, source);
        resultRef = new SingleNameReference(this.RESULT, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)resultRef, source);
        Assignment assignment = new Assignment((Expression)resultRef, (Expression)addItem, pE);
        assignment.sourceStart = pS;
        assignment.sourceEnd = assignment.statementEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)assignment, source);
        return assignment;
    }

    private TypeReference createTypeReference(EclipseNode type, long p) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(type.getName());
        for (EclipseNode tNode = (EclipseNode)type.up(); tNode != null && tNode.getKind() == AST.Kind.TYPE; tNode = (EclipseNode)tNode.up()) {
            list.add(tNode.getName());
        }
        Collections.reverse(list);
        if (list.size() == 1) {
            return new SingleTypeReference(((String)list.get(0)).toCharArray(), p);
        }
        long[] ps = new long[list.size()];
        char[][] tokens = new char[list.size()][];
        for (int i = 0; i < list.size(); ++i) {
            ps[i] = p;
            tokens[i] = ((String)list.get(i)).toCharArray();
        }
        return new QualifiedTypeReference((char[][])tokens, ps);
    }

    private MethodDeclaration createEquals(EclipseNode type, Collection<EclipseNode> fields, boolean callSuper, ASTNode source, EclipseHandlerUtil.FieldAccess fieldAccess, boolean needsCanEqual) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        TypeDeclaration typeDecl = (TypeDeclaration)type.get();
        MethodDeclaration method = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method, source);
        method.modifiers = EclipseHandlerUtil.toEclipseModifier(AccessLevel.PUBLIC);
        method.returnType = TypeReference.baseTypeReference((int)5, (int)0);
        method.returnType.sourceStart = pS;
        method.returnType.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method.returnType, source);
        method.annotations = new Annotation[]{EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, source)};
        method.selector = "equals".toCharArray();
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 8388608;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        QualifiedTypeReference objectRef = new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, new long[]{p, p, p});
        EclipseHandlerUtil.setGeneratedBy((ASTNode)objectRef, source);
        method.arguments = new Argument[]{new Argument(new char[]{'o'}, 0, (TypeReference)objectRef, 16)};
        method.arguments[0].sourceStart = pS;
        method.arguments[0].sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method.arguments[0], source);
        ArrayList<Object> statements = new ArrayList<Object>();
        SingleNameReference oRef = new SingleNameReference(new char[]{'o'}, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)oRef, source);
        ThisReference thisRef = new ThisReference(pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)thisRef, source);
        EqualExpression otherEqualsThis = new EqualExpression((Expression)oRef, (Expression)thisRef, 18);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)otherEqualsThis, source);
        TrueLiteral trueLiteral = new TrueLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)trueLiteral, source);
        ReturnStatement returnTrue = new ReturnStatement((Expression)trueLiteral, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)returnTrue, source);
        IfStatement ifOtherEqualsThis = new IfStatement((Expression)otherEqualsThis, (Statement)returnTrue, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ifOtherEqualsThis, source);
        statements.add((Object)ifOtherEqualsThis);
        oRef = new SingleNameReference(new char[]{'o'}, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)oRef, source);
        TypeReference typeReference = this.createTypeReference(type, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)typeReference, source);
        InstanceOfExpression instanceOf = new InstanceOfExpression((Expression)oRef, typeReference);
        instanceOf.sourceStart = pS;
        instanceOf.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)instanceOf, source);
        UnaryExpression notInstanceOf = new UnaryExpression((Expression)instanceOf, 11);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)notInstanceOf, source);
        FalseLiteral falseLiteral = new FalseLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)falseLiteral, source);
        ReturnStatement returnFalse = new ReturnStatement((Expression)falseLiteral, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)returnFalse, source);
        IfStatement ifNotInstanceOf = new IfStatement((Expression)notInstanceOf, (Statement)returnFalse, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ifNotInstanceOf, source);
        statements.add((Object)ifNotInstanceOf);
        char[] otherName = "other".toCharArray();
        if (!fields.isEmpty() || needsCanEqual) {
            SingleTypeReference targetType;
            LocalDeclaration other = new LocalDeclaration(otherName, pS, pE);
            other.modifiers |= 16;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)other, source);
            char[] typeName = typeDecl.name;
            if (typeDecl.typeParameters == null || typeDecl.typeParameters.length == 0) {
                targetType = new SingleTypeReference(typeName, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)targetType, source);
                other.type = new SingleTypeReference(typeName, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)other.type, source);
            } else {
                TypeReference[] typeArgs = new TypeReference[typeDecl.typeParameters.length];
                for (int i = 0; i < typeArgs.length; ++i) {
                    typeArgs[i] = new Wildcard(0);
                    typeArgs[i].sourceStart = pS;
                    typeArgs[i].sourceEnd = pE;
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)typeArgs[i], source);
                }
                targetType = new ParameterizedSingleTypeReference(typeName, typeArgs, 0, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)targetType, source);
                other.type = new ParameterizedSingleTypeReference(typeName, EclipseHandlerUtil.copyTypes(typeArgs, source), 0, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)other.type, source);
            }
            SingleNameReference oRef2 = new SingleNameReference(new char[]{'o'}, p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)oRef2, source);
            other.annotations = EclipseHandlerUtil.createSuppressWarningsAll(source, null);
            other.initialization = EclipseHandlerUtil.makeCastExpression((Expression)oRef2, (TypeReference)targetType, source);
            statements.add((Object)other);
        }
        if (needsCanEqual) {
            MessageSend otherCanEqual = new MessageSend();
            otherCanEqual.sourceStart = pS;
            otherCanEqual.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)otherCanEqual, source);
            otherCanEqual.receiver = new SingleNameReference(otherName, p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)otherCanEqual.receiver, source);
            otherCanEqual.selector = "canEqual".toCharArray();
            ThisReference thisReference = new ThisReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)thisReference, source);
            CastExpression castThisRef = EclipseHandlerUtil.makeCastExpression((Expression)thisReference, this.generateQualifiedTypeRef(source, TypeConstants.JAVA_LANG_OBJECT), source);
            castThisRef.sourceStart = pS;
            castThisRef.sourceEnd = pE;
            otherCanEqual.arguments = new Expression[]{castThisRef};
            UnaryExpression notOtherCanEqual = new UnaryExpression((Expression)otherCanEqual, 11);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)notOtherCanEqual, source);
            FalseLiteral falseLiteral2 = new FalseLiteral(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)falseLiteral2, source);
            ReturnStatement returnFalse2 = new ReturnStatement((Expression)falseLiteral2, pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)returnFalse2, source);
            IfStatement ifNotCanEqual = new IfStatement((Expression)notOtherCanEqual, (Statement)returnFalse2, pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ifNotCanEqual, source);
            statements.add((Object)ifNotCanEqual);
        }
        if (callSuper) {
            MessageSend callToSuper = new MessageSend();
            callToSuper.sourceStart = pS;
            callToSuper.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)callToSuper, source);
            callToSuper.receiver = new SuperReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)callToSuper.receiver, source);
            callToSuper.selector = "equals".toCharArray();
            SingleNameReference oRef3 = new SingleNameReference(new char[]{'o'}, p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)oRef3, source);
            callToSuper.arguments = new Expression[]{oRef3};
            UnaryExpression superNotEqual = new UnaryExpression((Expression)callToSuper, 11);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)superNotEqual, source);
            falseLiteral = new FalseLiteral(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)falseLiteral, source);
            returnFalse = new ReturnStatement((Expression)falseLiteral, pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)returnFalse, source);
            IfStatement ifSuperEquals = new IfStatement((Expression)superNotEqual, (Statement)returnFalse, pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ifSuperEquals, source);
            statements.add((Object)ifSuperEquals);
        }
        for (EclipseNode field : fields) {
            TypeReference fType = EclipseHandlerUtil.getFieldType(field, fieldAccess);
            char[] token = fType.getLastToken();
            Expression thisFieldAccessor = EclipseHandlerUtil.createFieldAccessor(field, fieldAccess, source);
            Expression otherFieldAccessor = EclipseHandlerUtil.createFieldAccessor(field, fieldAccess, source, otherName);
            if (fType.dimensions() == 0 && token != null) {
                if (Arrays.equals(TypeConstants.FLOAT, token)) {
                    statements.add((Object)this.generateCompareFloatOrDouble(thisFieldAccessor, otherFieldAccessor, "Float".toCharArray(), source));
                    continue;
                }
                if (Arrays.equals(TypeConstants.DOUBLE, token)) {
                    statements.add((Object)this.generateCompareFloatOrDouble(thisFieldAccessor, otherFieldAccessor, "Double".toCharArray(), source));
                    continue;
                }
                if (BUILT_IN_TYPES.contains(new String(token))) {
                    EqualExpression fieldsNotEqual = new EqualExpression(thisFieldAccessor, otherFieldAccessor, 29);
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldsNotEqual, source);
                    FalseLiteral falseLiteral3 = new FalseLiteral(pS, pE);
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)falseLiteral3, source);
                    ReturnStatement returnStatement = new ReturnStatement((Expression)falseLiteral3, pS, pE);
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)returnStatement, source);
                    IfStatement ifStatement = new IfStatement((Expression)fieldsNotEqual, (Statement)returnStatement, pS, pE);
                    EclipseHandlerUtil.setGeneratedBy((ASTNode)ifStatement, source);
                    statements.add((Object)ifStatement);
                    continue;
                }
                char[] thisDollarFieldName = ("this$" + field.getName()).toCharArray();
                char[] otherDollarFieldName = ("other$" + field.getName()).toCharArray();
                statements.add((Object)this.createLocalDeclaration(source, thisDollarFieldName, this.generateQualifiedTypeRef(source, TypeConstants.JAVA_LANG_OBJECT), thisFieldAccessor));
                statements.add((Object)this.createLocalDeclaration(source, otherDollarFieldName, this.generateQualifiedTypeRef(source, TypeConstants.JAVA_LANG_OBJECT), otherFieldAccessor));
                SingleNameReference this1 = new SingleNameReference(thisDollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)this1, source);
                SingleNameReference this2 = new SingleNameReference(thisDollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)this2, source);
                SingleNameReference other1 = new SingleNameReference(otherDollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)other1, source);
                SingleNameReference other2 = new SingleNameReference(otherDollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)other2, source);
                NullLiteral nullLiteral = new NullLiteral(pS, pE);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)nullLiteral, source);
                EqualExpression fieldIsNull = new EqualExpression((Expression)this1, (Expression)nullLiteral, 18);
                nullLiteral = new NullLiteral(pS, pE);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)nullLiteral, source);
                EqualExpression otherFieldIsntNull = new EqualExpression((Expression)other1, (Expression)nullLiteral, 29);
                MessageSend equalsCall = new MessageSend();
                equalsCall.sourceStart = pS;
                equalsCall.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy((ASTNode)equalsCall, source);
                equalsCall.receiver = this2;
                equalsCall.selector = "equals".toCharArray();
                equalsCall.arguments = new Expression[]{other2};
                UnaryExpression fieldsNotEqual = new UnaryExpression((Expression)equalsCall, 11);
                fieldsNotEqual.sourceStart = pS;
                fieldsNotEqual.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldsNotEqual, source);
                ConditionalExpression fullEquals = new ConditionalExpression((Expression)fieldIsNull, (Expression)otherFieldIsntNull, (Expression)fieldsNotEqual);
                fullEquals.sourceStart = pS;
                fullEquals.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy((ASTNode)fullEquals, source);
                FalseLiteral falseLiteral4 = new FalseLiteral(pS, pE);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)falseLiteral4, source);
                ReturnStatement returnStatement = new ReturnStatement((Expression)falseLiteral4, pS, pE);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)returnStatement, source);
                IfStatement ifStatement = new IfStatement((Expression)fullEquals, (Statement)returnStatement, pS, pE);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)ifStatement, source);
                statements.add((Object)ifStatement);
                continue;
            }
            if (fType.dimensions() <= 0 || token == null) continue;
            MessageSend arraysEqualCall = new MessageSend();
            arraysEqualCall.sourceStart = pS;
            arraysEqualCall.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)arraysEqualCall, source);
            arraysEqualCall.receiver = this.generateQualifiedNameRef(source, TypeConstants.JAVA, TypeConstants.UTIL, "Arrays".toCharArray());
            arraysEqualCall.selector = fType.dimensions() > 1 || !BUILT_IN_TYPES.contains(new String(token)) ? "deepEquals".toCharArray() : "equals".toCharArray();
            arraysEqualCall.arguments = new Expression[]{thisFieldAccessor, otherFieldAccessor};
            UnaryExpression arraysNotEqual = new UnaryExpression((Expression)arraysEqualCall, 11);
            arraysNotEqual.sourceStart = pS;
            arraysNotEqual.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy((ASTNode)arraysNotEqual, source);
            FalseLiteral falseLiteral5 = new FalseLiteral(pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)falseLiteral5, source);
            ReturnStatement returnStatement = new ReturnStatement((Expression)falseLiteral5, pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)returnStatement, source);
            IfStatement ifStatement = new IfStatement((Expression)arraysNotEqual, (Statement)returnStatement, pS, pE);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)ifStatement, source);
            statements.add((Object)ifStatement);
        }
        TrueLiteral trueLiteral2 = new TrueLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)trueLiteral2, source);
        ReturnStatement returnStatement = new ReturnStatement((Expression)trueLiteral2, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)returnStatement, source);
        statements.add((Object)returnStatement);
        method.statements = statements.toArray((T[])new Statement[statements.size()]);
        return method;
    }

    private MethodDeclaration createCanEqual(EclipseNode type, ASTNode source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        char[] otherName = "other".toCharArray();
        MethodDeclaration method = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method, source);
        method.modifiers = EclipseHandlerUtil.toEclipseModifier(AccessLevel.PUBLIC);
        method.returnType = TypeReference.baseTypeReference((int)5, (int)0);
        method.returnType.sourceStart = pS;
        method.returnType.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method.returnType, source);
        method.selector = "canEqual".toCharArray();
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 8388608;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        QualifiedTypeReference objectRef = new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, new long[]{p, p, p});
        EclipseHandlerUtil.setGeneratedBy((ASTNode)objectRef, source);
        method.arguments = new Argument[]{new Argument(otherName, 0, (TypeReference)objectRef, 16)};
        method.arguments[0].sourceStart = pS;
        method.arguments[0].sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)method.arguments[0], source);
        SingleNameReference otherRef = new SingleNameReference(otherName, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)otherRef, source);
        TypeReference typeReference = this.createTypeReference(type, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)typeReference, source);
        InstanceOfExpression instanceOf = new InstanceOfExpression((Expression)otherRef, typeReference);
        instanceOf.sourceStart = pS;
        instanceOf.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)instanceOf, source);
        ReturnStatement returnStatement = new ReturnStatement((Expression)instanceOf, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)returnStatement, source);
        method.statements = new Statement[]{returnStatement};
        return method;
    }

    private IfStatement generateCompareFloatOrDouble(Expression thisRef, Expression otherRef, char[] floatOrDouble, ASTNode source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        MessageSend floatCompare = new MessageSend();
        floatCompare.sourceStart = pS;
        floatCompare.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)floatCompare, source);
        floatCompare.receiver = this.generateQualifiedNameRef(source, TypeConstants.JAVA, TypeConstants.LANG, floatOrDouble);
        floatCompare.selector = "compare".toCharArray();
        floatCompare.arguments = new Expression[]{thisRef, otherRef};
        IntLiteral int0 = EclipseHandlerUtil.makeIntLiteral("0".toCharArray(), source);
        EqualExpression ifFloatCompareIsNot0 = new EqualExpression((Expression)floatCompare, (Expression)int0, 29);
        ifFloatCompareIsNot0.sourceStart = pS;
        ifFloatCompareIsNot0.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ifFloatCompareIsNot0, source);
        FalseLiteral falseLiteral = new FalseLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)falseLiteral, source);
        ReturnStatement returnFalse = new ReturnStatement((Expression)falseLiteral, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)returnFalse, source);
        IfStatement ifStatement = new IfStatement((Expression)ifFloatCompareIsNot0, (Statement)returnFalse, pS, pE);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ifStatement, source);
        return ifStatement;
    }

    private Expression longToIntForHashCode(Expression ref1, Expression ref2, ASTNode source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        IntLiteral int32 = EclipseHandlerUtil.makeIntLiteral("32".toCharArray(), source);
        BinaryExpression higherBits = new BinaryExpression(ref1, (Expression)int32, 19);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)higherBits, source);
        BinaryExpression xorParts = new BinaryExpression(ref2, (Expression)higherBits, 8);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)xorParts, source);
        TypeReference intRef = TypeReference.baseTypeReference((int)10, (int)0);
        intRef.sourceStart = pS;
        intRef.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)intRef, source);
        CastExpression expr = EclipseHandlerUtil.makeCastExpression((Expression)xorParts, intRef, source);
        expr.sourceStart = pS;
        expr.sourceEnd = pE;
        return expr;
    }

    private /* varargs */ NameReference generateQualifiedNameRef(ASTNode source, char[] ... varNames) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        SingleNameReference ref = varNames.length > 1 ? new QualifiedNameReference(varNames, new long[varNames.length], pS, pE) : new SingleNameReference(varNames[0], p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ref, source);
        return ref;
    }

    private /* varargs */ TypeReference generateQualifiedTypeRef(ASTNode source, char[] ... varNames) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        long[] poss = Eclipse.poss(source, varNames.length);
        QualifiedTypeReference ref = varNames.length > 1 ? new QualifiedTypeReference(varNames, poss) : new SingleTypeReference(varNames[0], p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ref, source);
        return ref;
    }

}

