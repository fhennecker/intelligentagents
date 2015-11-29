/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.CompilationResult
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 *  org.eclipse.jdt.internal.compiler.ast.Assignment
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.FieldReference
 *  org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.ReturnStatement
 *  org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation
 *  org.eclipse.jdt.internal.compiler.ast.SingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.StringLiteral
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeParameter
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 */
package lombok.eclipse.handlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.TransformationsUtil;
import lombok.eclipse.DeferUntilBuildFieldsAndMethods;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HandleConstructor {
    private static final char[][] JAVA_BEANS_CONSTRUCTORPROPERTIES = new char[][]{"java".toCharArray(), "beans".toCharArray(), "ConstructorProperties".toCharArray()};

    private void handle(EclipseNode annotationNode, Class<? extends java.lang.annotation.Annotation> annotationType, ConstructorData data) {
        boolean notAClass;
        EclipseNode typeNode = (EclipseNode)annotationNode.up();
        TypeDeclaration typeDecl = null;
        if (typeNode.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)typeNode.get();
        }
        int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
        boolean bl = notAClass = (modifiers & 8704) != 0;
        if (typeDecl == null || notAClass) {
            annotationNode.addError(String.format("%s is only supported on a class or an enum.", annotationType.getSimpleName()));
            return;
        }
        if (data.accessLevel == AccessLevel.NONE) {
            return;
        }
        new HandleConstructor().generateConstructor(typeNode, (ASTNode)annotationNode.get(), data);
    }

    public static boolean constructorOrConstructorAnnotationExists(EclipseNode typeNode) {
        boolean constructorExists;
        boolean bl = constructorExists = EclipseHandlerUtil.constructorExists(typeNode) == EclipseHandlerUtil.MemberExistsResult.EXISTS_BY_USER;
        if (!constructorExists) {
            for (EclipseNode child : typeNode.down()) {
                if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(NoArgsConstructor.class, child) && !EclipseHandlerUtil.annotationTypeMatches(AllArgsConstructor.class, child) && !EclipseHandlerUtil.annotationTypeMatches(RequiredArgsConstructor.class, child)) continue;
                constructorExists = true;
                break;
            }
        }
        return constructorExists;
    }

    public void generateConstructor(EclipseNode typeNode, ASTNode source, ConstructorData data) {
        List<SuperConstructor> superConstructors = data.callSuper ? this.getSuperConstructors(typeNode, source) : Collections.singletonList(SuperConstructor.implicit());
        for (SuperConstructor superConstructor : superConstructors) {
            ConstructorDeclaration constr = this.createConstructor(typeNode, source, data, superConstructor);
            EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)constr);
            if (data.staticConstructorRequired()) {
                MethodDeclaration staticConstr = this.createStaticConstructor(typeNode, source, data, superConstructor);
                EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)staticConstr);
            }
            typeNode.rebuild();
        }
    }

    private static Annotation[] createConstructorProperties(ASTNode source, Annotation[] originalAnnotationArray, List<Argument> params) {
        if (params.isEmpty()) {
            return originalAnnotationArray;
        }
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        long[] poss = new long[3];
        Arrays.fill(poss, p);
        QualifiedTypeReference constructorPropertiesType = new QualifiedTypeReference(JAVA_BEANS_CONSTRUCTORPROPERTIES, poss);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)constructorPropertiesType, source);
        SingleMemberAnnotation ann = new SingleMemberAnnotation((TypeReference)constructorPropertiesType, pS);
        ann.declarationSourceEnd = pE;
        ArrayInitializer fieldNames = new ArrayInitializer();
        fieldNames.sourceStart = pS;
        fieldNames.sourceEnd = pE;
        fieldNames.expressions = new Expression[params.size()];
        int ctr = 0;
        for (Argument param : params) {
            fieldNames.expressions[ctr] = new StringLiteral(param.name, pS, pE, 0);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldNames.expressions[ctr], source);
            ++ctr;
        }
        ann.memberValue = fieldNames;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ann, source);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)ann.memberValue, source);
        if (originalAnnotationArray == null) {
            return new Annotation[]{ann};
        }
        Annotation[] newAnnotationArray = Arrays.copyOf(originalAnnotationArray, originalAnnotationArray.length + 1);
        newAnnotationArray[originalAnnotationArray.length] = ann;
        return newAnnotationArray;
    }

    private ConstructorDeclaration createConstructor(EclipseNode typeNode, ASTNode source, ConstructorData data, SuperConstructor superConstructor) {
        long p = (long)source.sourceStart << 32 | (long)source.sourceEnd;
        boolean isEnum = (((TypeDeclaration)typeNode.get()).modifiers & 16384) != 0;
        AccessLevel level = isEnum | data.staticConstructorRequired() ? AccessLevel.PRIVATE : data.accessLevel;
        ConstructorDeclaration constructor = new ConstructorDeclaration(((CompilationUnitDeclaration)((EclipseNode)typeNode.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)constructor, source);
        constructor.modifiers = EclipseHandlerUtil.toEclipseModifier(level);
        constructor.selector = ((TypeDeclaration)typeNode.get()).name;
        constructor.thrownExceptions = null;
        constructor.typeParameters = null;
        constructor.bits |= 8388608;
        constructor.declarationSourceStart = constructor.sourceStart = source.sourceStart;
        constructor.bodyStart = constructor.sourceStart;
        constructor.declarationSourceEnd = constructor.sourceEnd = source.sourceEnd;
        constructor.bodyEnd = constructor.sourceEnd;
        constructor.arguments = null;
        ArrayList<Argument> params = new ArrayList<Argument>();
        ArrayList<Assignment> assigns = new ArrayList<Assignment>();
        ArrayList<Object> nullChecks = new ArrayList<Object>();
        if (superConstructor.isImplicit) {
            constructor.constructorCall = new ExplicitConstructorCall(1);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)constructor.constructorCall, source);
        } else {
            constructor.constructorCall = new ExplicitConstructorCall(2);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)constructor.constructorCall, source);
            constructor.constructorCall.arguments = superConstructor.getArgs(source).toArray((T[])new Expression[0]);
            params.addAll(superConstructor.params);
        }
        List<EclipseNode> fields = data.fieldProvider.findFields(typeNode);
        for (EclipseNode fieldNode : fields) {
            Statement nullCheck;
            Annotation[] copiedAnnotations;
            FieldDeclaration field = (FieldDeclaration)fieldNode.get();
            FieldReference thisX = new FieldReference(field.name, p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)thisX, source);
            thisX.receiver = new ThisReference((int)(p >> 32), (int)p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)thisX.receiver, source);
            SingleNameReference assignmentNameRef = new SingleNameReference(field.name, p);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)assignmentNameRef, source);
            Assignment assignment = new Assignment((Expression)thisX, (Expression)assignmentNameRef, (int)p);
            assignment.sourceStart = (int)(p >> 32);
            assignment.sourceEnd = assignment.statementEnd = (int)(p >> 32);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)assignment, source);
            assigns.add(assignment);
            long fieldPos = (long)field.sourceStart << 32 | (long)field.sourceEnd;
            Argument parameter = new Argument(field.name, fieldPos, EclipseHandlerUtil.copyType(field.type, source), 16);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)parameter, source);
            Annotation[] nonNulls = Eclipse.findAnnotations(field, TransformationsUtil.NON_NULL_PATTERN);
            Annotation[] nullables = Eclipse.findAnnotations(field, TransformationsUtil.NULLABLE_PATTERN);
            if (nonNulls.length != 0 && (nullCheck = EclipseHandlerUtil.generateNullCheck((AbstractVariableDeclaration)field, source)) != null) {
                nullChecks.add((Object)nullCheck);
            }
            if ((copiedAnnotations = EclipseHandlerUtil.copyAnnotations(source, nonNulls, nullables)).length != 0) {
                parameter.annotations = copiedAnnotations;
            }
            params.add(parameter);
        }
        nullChecks.addAll(assigns);
        constructor.statements = nullChecks.isEmpty() ? null : nullChecks.toArray((T[])new Statement[nullChecks.size()]);
        Argument[] arrargument = constructor.arguments = params.isEmpty() ? null : params.toArray((T[])new Argument[params.size()]);
        if (!data.suppressConstructorProperties && level != AccessLevel.PRIVATE && !this.isLocalType(typeNode)) {
            constructor.annotations = HandleConstructor.createConstructorProperties(source, constructor.annotations, params);
        }
        return constructor;
    }

    private boolean isLocalType(EclipseNode type) {
        EclipseNode typeNode;
        for (typeNode = (EclipseNode)type.up(); typeNode != null && !(typeNode.get() instanceof TypeDeclaration); typeNode = (EclipseNode)typeNode.up()) {
        }
        return typeNode != null;
    }

    private MethodDeclaration createStaticConstructor(EclipseNode typeNode, ASTNode source, ConstructorData data, SuperConstructor superConstructor) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        MethodDeclaration constructor = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)typeNode.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)constructor, source);
        constructor.modifiers = 8 | EclipseHandlerUtil.toEclipseModifier(data.accessLevel);
        TypeDeclaration typeDecl = (TypeDeclaration)typeNode.get();
        if (typeDecl.typeParameters != null && typeDecl.typeParameters.length > 0) {
            TypeReference[] refs = new TypeReference[typeDecl.typeParameters.length];
            int idx = 0;
            for (TypeParameter param : typeDecl.typeParameters) {
                SingleTypeReference typeRef = new SingleTypeReference(param.name, (long)param.sourceStart << 32 | (long)param.sourceEnd);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)typeRef, source);
                refs[idx++] = typeRef;
            }
            constructor.returnType = new ParameterizedSingleTypeReference(typeDecl.name, refs, 0, p);
        } else {
            constructor.returnType = new SingleTypeReference(((TypeDeclaration)typeNode.get()).name, p);
        }
        EclipseHandlerUtil.setGeneratedBy((ASTNode)constructor.returnType, source);
        constructor.annotations = null;
        constructor.selector = data.staticName.toCharArray();
        constructor.thrownExceptions = null;
        constructor.typeParameters = EclipseHandlerUtil.copyTypeParams(((TypeDeclaration)typeNode.get()).typeParameters, source);
        constructor.bits |= 8388608;
        constructor.declarationSourceStart = constructor.sourceStart = source.sourceStart;
        constructor.bodyStart = constructor.sourceStart;
        constructor.declarationSourceEnd = constructor.sourceEnd = source.sourceEnd;
        constructor.bodyEnd = constructor.sourceEnd;
        ArrayList<Argument> params = new ArrayList<Argument>();
        ArrayList<Object> args = new ArrayList<Object>();
        if (!superConstructor.isImplicit) {
            params.addAll(superConstructor.params);
            args.addAll(superConstructor.getArgs(source));
        }
        AllocationExpression statement = new AllocationExpression();
        statement.sourceStart = pS;
        statement.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy((ASTNode)statement, source);
        statement.type = EclipseHandlerUtil.copyType(constructor.returnType, source);
        List<EclipseNode> fields = data.fieldProvider.findFields(typeNode);
        for (EclipseNode fieldNode : fields) {
            FieldDeclaration field = (FieldDeclaration)fieldNode.get();
            long fieldPos = (long)field.sourceStart << 32 | (long)field.sourceEnd;
            SingleNameReference nameRef = new SingleNameReference(field.name, fieldPos);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)nameRef, source);
            args.add((Object)nameRef);
            Argument parameter = new Argument(field.name, fieldPos, EclipseHandlerUtil.copyType(field.type, source), 16);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)parameter, source);
            Annotation[] copiedAnnotations = EclipseHandlerUtil.copyAnnotations(source, Eclipse.findAnnotations(field, TransformationsUtil.NON_NULL_PATTERN), Eclipse.findAnnotations(field, TransformationsUtil.NULLABLE_PATTERN));
            if (copiedAnnotations.length != 0) {
                parameter.annotations = copiedAnnotations;
            }
            params.add(parameter);
        }
        statement.arguments = args.isEmpty() ? null : args.toArray((T[])new Expression[args.size()]);
        constructor.arguments = params.isEmpty() ? null : params.toArray((T[])new Argument[params.size()]);
        constructor.statements = new Statement[]{new ReturnStatement((Expression)statement, (int)(p >> 32), (int)p)};
        EclipseHandlerUtil.setGeneratedBy((ASTNode)constructor.statements[0], source);
        return constructor;
    }

    public List<SuperConstructor> getSuperConstructors(EclipseNode typeNode, ASTNode source) {
        ArrayList<SuperConstructor> superConstructors = new ArrayList<SuperConstructor>();
        TypeDeclaration typeDecl = (TypeDeclaration)typeNode.get();
        if (typeDecl.superclass != null) {
            MethodBinding[] availableMethods;
            ReferenceBinding rb;
            TypeBinding binding = typeDecl.superclass.resolveType((BlockScope)typeDecl.initializerScope);
            this.ensureAllClassScopeMethodWereBuild(binding);
            if (binding instanceof ReferenceBinding && (availableMethods = (rb = (ReferenceBinding)binding).availableMethods()) != null) {
                for (MethodBinding mb : availableMethods) {
                    if (!mb.isConstructor() || mb.isSynthetic() || !mb.isPublic() && !mb.isProtected()) continue;
                    ArrayList<Argument> params = new ArrayList<Argument>();
                    int argCounter = 0;
                    if (mb.parameters != null) {
                        for (TypeBinding argtype : mb.parameters) {
                            String name = "arg" + argCounter++;
                            TypeReference varType = EclipseHandlerUtil.makeType(argtype, source, false);
                            long pos = (long)source.sourceStart << 32 | (long)source.sourceEnd;
                            Argument param = new Argument(name.toCharArray(), pos, varType, 16);
                            EclipseHandlerUtil.setGeneratedBy((ASTNode)param, source);
                            params.add(param);
                        }
                    }
                    superConstructors.add(new SuperConstructor(params));
                }
            }
        }
        if (superConstructors.isEmpty()) {
            superConstructors.add(SuperConstructor.implicit());
        }
        return superConstructors;
    }

    private void ensureAllClassScopeMethodWereBuild(TypeBinding binding) {
        ClassScope cs;
        if (binding instanceof SourceTypeBinding && (cs = ((SourceTypeBinding)binding).scope) != null) {
            try {
                Reflection.classScopeBuildFieldsAndMethodsMethod.invoke((Object)cs, new Object[0]);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    public static enum FieldProvider {
        REQUIRED{

            @Override
            public List<EclipseNode> findFields(EclipseNode typeNode) {
                ArrayList<EclipseNode> fields = new ArrayList<EclipseNode>();
                for (EclipseNode child : typeNode.down()) {
                    boolean isNonNull;
                    FieldDeclaration fieldDecl;
                    if (child.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)child.get())) continue;
                    boolean isFinal = (fieldDecl.modifiers & 16) != 0;
                    boolean bl = isNonNull = Eclipse.findAnnotations(fieldDecl, TransformationsUtil.NON_NULL_PATTERN).length != 0;
                    if (!isFinal && !isNonNull || fieldDecl.initialization != null) continue;
                    fields.add(child);
                }
                return fields;
            }
        }
        ,
        ALL{

            @Override
            public List<EclipseNode> findFields(EclipseNode typeNode) {
                ArrayList<EclipseNode> fields = new ArrayList<EclipseNode>();
                for (EclipseNode child : typeNode.down()) {
                    FieldDeclaration fieldDecl;
                    boolean isFinal;
                    if (child.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)child.get())) continue;
                    boolean bl = isFinal = (fieldDecl.modifiers & 16) != 0;
                    if (isFinal && fieldDecl.initialization != null) continue;
                    fields.add(child);
                }
                return fields;
            }
        }
        ,
        NO{

            @Override
            public List<EclipseNode> findFields(EclipseNode typeNode) {
                return Collections.emptyList();
            }
        };
        

        private FieldProvider() {
        }

        public abstract List<EclipseNode> findFields(EclipseNode var1);

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SuperConstructor {
        final List<Argument> params;
        boolean isImplicit;

        static SuperConstructor implicit() {
            SuperConstructor superConstructor = new SuperConstructor(Collections.<Argument>emptyList());
            superConstructor.isImplicit = true;
            return superConstructor;
        }

        SuperConstructor(List<Argument> params) {
            this.params = params;
        }

        public List<Expression> getArgs(ASTNode source) {
            ArrayList<Expression> args = new ArrayList<Expression>();
            for (Argument param : this.params) {
                long fieldPos = (long)param.sourceStart << 32 | (long)param.sourceEnd;
                SingleNameReference nameRef = new SingleNameReference(param.name, fieldPos);
                EclipseHandlerUtil.setGeneratedBy((ASTNode)nameRef, source);
                args.add((Expression)nameRef);
            }
            return args;
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
            catch (Exception e) {
                // empty catch block
            }
            classScopeBuildFieldsAndMethodsMethod = m;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @DeferUntilBuildFieldsAndMethods
    public static class HandleAllArgsConstructor
    extends EclipseAnnotationHandler<AllArgsConstructor> {
        @Override
        public void handle(AnnotationValues<AllArgsConstructor> annotation, Annotation ast, EclipseNode annotationNode) {
            AllArgsConstructor instance = annotation.getInstance();
            ConstructorData data = new ConstructorData().fieldProvider(FieldProvider.ALL).accessLevel(instance.access()).staticName(instance.staticName()).callSuper(instance.callSuper()).suppressConstructorProperties(instance.suppressConstructorProperties());
            new HandleConstructor().handle(annotationNode, AllArgsConstructor.class, data);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @DeferUntilBuildFieldsAndMethods
    public static class HandleRequiredArgsConstructor
    extends EclipseAnnotationHandler<RequiredArgsConstructor> {
        @Override
        public void handle(AnnotationValues<RequiredArgsConstructor> annotation, Annotation ast, EclipseNode annotationNode) {
            RequiredArgsConstructor instance = annotation.getInstance();
            ConstructorData data = new ConstructorData().fieldProvider(FieldProvider.REQUIRED).accessLevel(instance.access()).staticName(instance.staticName()).callSuper(instance.callSuper()).suppressConstructorProperties(instance.suppressConstructorProperties());
            new HandleConstructor().handle(annotationNode, RequiredArgsConstructor.class, data);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @DeferUntilBuildFieldsAndMethods
    public static class HandleNoArgsConstructor
    extends EclipseAnnotationHandler<NoArgsConstructor> {
        @Override
        public void handle(AnnotationValues<NoArgsConstructor> annotation, Annotation ast, EclipseNode annotationNode) {
            NoArgsConstructor instance = annotation.getInstance();
            ConstructorData data = new ConstructorData().fieldProvider(FieldProvider.NO).accessLevel(instance.access()).staticName(instance.staticName()).callSuper(instance.callSuper());
            new HandleConstructor().handle(annotationNode, NoArgsConstructor.class, data);
        }
    }

}

