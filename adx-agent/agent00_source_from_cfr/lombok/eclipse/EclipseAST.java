/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.CompilationResult
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ImportReference
 *  org.eclipse.jdt.internal.compiler.ast.Initializer
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 */
package lombok.eclipse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Lombok;
import lombok.core.AST;
import lombok.core.LombokNode;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseAstProblemView;
import lombok.eclipse.EclipseNode;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EclipseAST
extends AST<EclipseAST, EclipseNode, ASTNode> {
    private final List<ParseProblem> queuedProblems = new ArrayList<ParseProblem>();
    private final CompilationUnitDeclaration compilationUnitDeclaration;
    private boolean completeParse;

    public EclipseAST(CompilationUnitDeclaration ast) {
        super(EclipseAST.toFileName(ast), EclipseAST.packageDeclaration(ast), EclipseAST.imports(ast));
        this.compilationUnitDeclaration = ast;
        this.setTop(this.buildCompilationUnit(ast));
        this.completeParse = EclipseAST.isComplete(ast);
        this.clearChanged();
    }

    private static String packageDeclaration(CompilationUnitDeclaration cud) {
        ImportReference pkg = cud.currentPackage;
        return pkg == null ? null : Eclipse.toQualifiedName(pkg.getImportName());
    }

    private static Collection<String> imports(CompilationUnitDeclaration cud) {
        ArrayList<String> imports = new ArrayList<String>();
        if (cud.imports == null) {
            return imports;
        }
        for (ImportReference imp : cud.imports) {
            if (imp == null) continue;
            String qualifiedName = Eclipse.toQualifiedName(imp.getImportName());
            if ((imp.bits & 131072) != 0) {
                qualifiedName = qualifiedName + ".*";
            }
            imports.add(qualifiedName);
        }
        return imports;
    }

    public void traverse(EclipseASTVisitor visitor) {
        ((EclipseNode)this.top()).traverse(visitor);
    }

    void traverseChildren(EclipseASTVisitor visitor, EclipseNode node) {
        for (EclipseNode child : node.down()) {
            child.traverse(visitor);
        }
    }

    public boolean isCompleteParse() {
        return this.completeParse;
    }

    private void propagateProblems() {
        if (this.queuedProblems.isEmpty()) {
            return;
        }
        CompilationUnitDeclaration cud = (CompilationUnitDeclaration)((EclipseNode)this.top()).get();
        if (cud.compilationResult == null) {
            return;
        }
        for (ParseProblem problem : this.queuedProblems) {
            problem.addToCompilationResult();
        }
        this.queuedProblems.clear();
    }

    void addProblem(ParseProblem problem) {
        this.queuedProblems.add(problem);
        this.propagateProblems();
    }

    public static void addProblemToCompilationResult(CompilationUnitDeclaration ast, boolean isWarning, String message, int sourceStart, int sourceEnd) {
        block6 : {
            if (ast.compilationResult == null) {
                return;
            }
            try {
                EcjReflectionCheck.addProblemToCompilationResult.invoke(null, new Object[]{ast, isWarning, message, sourceStart, sourceEnd});
            }
            catch (NoClassDefFoundError e) {
            }
            catch (IllegalAccessException e) {
                throw Lombok.sneakyThrow(e);
            }
            catch (InvocationTargetException e) {
                throw Lombok.sneakyThrow(e);
            }
            catch (NullPointerException e) {
                if ("false".equals(System.getProperty("lombok.debug.reflection", "false"))) break block6;
                e.initCause(EcjReflectionCheck.problem);
                throw e;
            }
        }
    }

    private static String toFileName(CompilationUnitDeclaration ast) {
        return ast.compilationResult.fileName == null ? null : new String(ast.compilationResult.fileName);
    }

    public void rebuild(boolean force) {
        this.propagateProblems();
        if (this.completeParse && !force) {
            return;
        }
        boolean changed = this.isChanged();
        boolean newCompleteParse = EclipseAST.isComplete(this.compilationUnitDeclaration);
        if (!newCompleteParse && !force) {
            return;
        }
        ((EclipseNode)this.top()).rebuild();
        this.completeParse = newCompleteParse;
        if (!changed) {
            this.clearChanged();
        }
    }

    private static boolean isComplete(CompilationUnitDeclaration unit) {
        return (unit.bits & 16) != 0;
    }

    @Override
    protected EclipseNode buildTree(ASTNode node, AST.Kind kind) {
        switch (kind) {
            case COMPILATION_UNIT: {
                return this.buildCompilationUnit((CompilationUnitDeclaration)node);
            }
            case TYPE: {
                return this.buildType((TypeDeclaration)node);
            }
            case FIELD: {
                return this.buildField((FieldDeclaration)node);
            }
            case INITIALIZER: {
                return this.buildInitializer((Initializer)node);
            }
            case METHOD: {
                return this.buildMethod((AbstractMethodDeclaration)node);
            }
            case ARGUMENT: {
                return this.buildLocal((LocalDeclaration)((Argument)node), kind);
            }
            case LOCAL: {
                return this.buildLocal((LocalDeclaration)node, kind);
            }
            case STATEMENT: {
                return this.buildStatement((Statement)node);
            }
            case ANNOTATION: {
                return this.buildAnnotation((Annotation)node, false);
            }
        }
        throw new AssertionError((Object)("Did not expect to arrive here: " + (Object)((Object)kind)));
    }

    private EclipseNode buildCompilationUnit(CompilationUnitDeclaration top) {
        if (this.setAndGetAsHandled(top)) {
            return null;
        }
        List<EclipseNode> children = this.buildTypes(top.types);
        return this.putInMap(new EclipseNode(this, (ASTNode)top, children, AST.Kind.COMPILATION_UNIT));
    }

    private void addIfNotNull(Collection<EclipseNode> collection, EclipseNode n) {
        if (n != null) {
            collection.add(n);
        }
    }

    private List<EclipseNode> buildTypes(TypeDeclaration[] children) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            for (TypeDeclaration type : children) {
                this.addIfNotNull(childNodes, this.buildType(type));
            }
        }
        return childNodes;
    }

    private EclipseNode buildType(TypeDeclaration type) {
        if (this.setAndGetAsHandled(type)) {
            return null;
        }
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        childNodes.addAll(this.buildFields(type.fields));
        childNodes.addAll(this.buildTypes(type.memberTypes));
        childNodes.addAll(this.buildMethods(type.methods));
        childNodes.addAll(this.buildAnnotations(type.annotations, false));
        return this.putInMap(new EclipseNode(this, (ASTNode)type, childNodes, AST.Kind.TYPE));
    }

    private Collection<EclipseNode> buildFields(FieldDeclaration[] children) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            for (FieldDeclaration child : children) {
                this.addIfNotNull(childNodes, this.buildField(child));
            }
        }
        return childNodes;
    }

    private static <T> List<T> singleton(T item) {
        ArrayList<T> list = new ArrayList<T>();
        if (item != null) {
            list.add(item);
        }
        return list;
    }

    private EclipseNode buildField(FieldDeclaration field) {
        if (field instanceof Initializer) {
            return this.buildInitializer((Initializer)field);
        }
        if (this.setAndGetAsHandled(field)) {
            return null;
        }
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        this.addIfNotNull(childNodes, this.buildStatement((Statement)field.initialization));
        childNodes.addAll(this.buildAnnotations(field.annotations, true));
        return this.putInMap(new EclipseNode(this, (ASTNode)field, childNodes, AST.Kind.FIELD));
    }

    private EclipseNode buildInitializer(Initializer initializer) {
        if (this.setAndGetAsHandled(initializer)) {
            return null;
        }
        return this.putInMap(new EclipseNode(this, (ASTNode)initializer, EclipseAST.singleton(this.buildStatement((Statement)initializer.block)), AST.Kind.INITIALIZER));
    }

    private Collection<EclipseNode> buildMethods(AbstractMethodDeclaration[] children) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            for (AbstractMethodDeclaration method : children) {
                this.addIfNotNull(childNodes, this.buildMethod(method));
            }
        }
        return childNodes;
    }

    private EclipseNode buildMethod(AbstractMethodDeclaration method) {
        if (this.setAndGetAsHandled(method)) {
            return null;
        }
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        childNodes.addAll(this.buildArguments(method.arguments));
        if (method instanceof ConstructorDeclaration) {
            ConstructorDeclaration constructor = (ConstructorDeclaration)method;
            this.addIfNotNull(childNodes, this.buildStatement((Statement)constructor.constructorCall));
        }
        childNodes.addAll(this.buildStatements(method.statements));
        childNodes.addAll(this.buildAnnotations(method.annotations, false));
        return this.putInMap(new EclipseNode(this, (ASTNode)method, childNodes, AST.Kind.METHOD));
    }

    private Collection<EclipseNode> buildArguments(Argument[] children) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            for (Argument local : children) {
                this.addIfNotNull(childNodes, this.buildLocal((LocalDeclaration)local, AST.Kind.ARGUMENT));
            }
        }
        return childNodes;
    }

    private EclipseNode buildLocal(LocalDeclaration local, AST.Kind kind) {
        if (this.setAndGetAsHandled(local)) {
            return null;
        }
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        this.addIfNotNull(childNodes, this.buildStatement((Statement)local.initialization));
        childNodes.addAll(this.buildAnnotations(local.annotations, true));
        return this.putInMap(new EclipseNode(this, (ASTNode)local, childNodes, kind));
    }

    private Collection<EclipseNode> buildAnnotations(Annotation[] annotations, boolean varDecl) {
        ArrayList<EclipseNode> elements = new ArrayList<EclipseNode>();
        if (annotations != null) {
            for (Annotation an : annotations) {
                this.addIfNotNull(elements, this.buildAnnotation(an, varDecl));
            }
        }
        return elements;
    }

    private EclipseNode buildAnnotation(Annotation annotation, boolean field) {
        if (annotation == null) {
            return null;
        }
        boolean handled = this.setAndGetAsHandled(annotation);
        if (!field && handled) {
            return null;
        }
        return this.putInMap(new EclipseNode(this, (ASTNode)annotation, null, AST.Kind.ANNOTATION));
    }

    private Collection<EclipseNode> buildStatements(Statement[] children) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            for (Statement child : children) {
                this.addIfNotNull(childNodes, this.buildStatement(child));
            }
        }
        return childNodes;
    }

    private EclipseNode buildStatement(Statement child) {
        if (child == null) {
            return null;
        }
        if (child instanceof TypeDeclaration) {
            return this.buildType((TypeDeclaration)child);
        }
        if (child instanceof LocalDeclaration) {
            return this.buildLocal((LocalDeclaration)child, AST.Kind.LOCAL);
        }
        if (this.setAndGetAsHandled(child)) {
            return null;
        }
        return this.drill(child);
    }

    private EclipseNode drill(Statement statement) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        for (AST.FieldAccess fa : this.fieldsOf(statement.getClass())) {
            childNodes.addAll(this.buildWithField(EclipseNode.class, statement, fa));
        }
        return this.putInMap(new EclipseNode(this, (ASTNode)statement, childNodes, AST.Kind.STATEMENT));
    }

    @Override
    protected Collection<Class<? extends ASTNode>> getStatementTypes() {
        return Collections.singleton(Statement.class);
    }

    private static class EcjReflectionCheck {
        private static final String CUD_TYPE = "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration";
        public static Method addProblemToCompilationResult;
        public static final Throwable problem;

        private EcjReflectionCheck() {
        }

        static {
            Throwable problem_ = null;
            Method m = null;
            try {
                m = EclipseAstProblemView.class.getMethod("addProblemToCompilationResult", Class.forName("org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration"), Boolean.TYPE, String.class, Integer.TYPE, Integer.TYPE);
            }
            catch (Throwable t) {
                problem_ = t;
            }
            addProblemToCompilationResult = m;
            problem = problem_;
        }
    }

    class ParseProblem {
        final boolean isWarning;
        final String message;
        final int sourceStart;
        final int sourceEnd;

        ParseProblem(boolean isWarning, String message, int sourceStart, int sourceEnd) {
            this.isWarning = isWarning;
            this.message = message;
            this.sourceStart = sourceStart;
            this.sourceEnd = sourceEnd;
        }

        void addToCompilationResult() {
            EclipseAST.addProblemToCompilationResult((CompilationUnitDeclaration)((EclipseNode)EclipseAST.this.top()).get(), this.isWarning, this.message, this.sourceStart, this.sourceEnd);
        }
    }

}

