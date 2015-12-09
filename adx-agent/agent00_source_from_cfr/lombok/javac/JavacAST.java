/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Symtab
 *  com.sun.tools.javac.model.JavacElements
 *  com.sun.tools.javac.model.JavacTypes
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCCatch
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCImport
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.JCDiagnostic
 *  com.sun.tools.javac.util.JCDiagnostic$DiagnosticPosition
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Log
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import lombok.core.AST;
import lombok.core.LombokNode;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacNode;

public class JavacAST
extends AST<JavacAST, JavacNode, JCTree> {
    private final Messager messager;
    private final JavacElements elements;
    private final TreeMaker treeMaker;
    private final Symtab symtab;
    private final JavacTypes javacTypes;
    private final Log log;
    private final Context context;

    public JavacAST(Messager messager, Context context, JCTree.JCCompilationUnit top) {
        super(JavacAST.sourceName(top), JavacAST.packageDeclaration(top), JavacAST.imports(top));
        this.setTop(this.buildCompilationUnit(top));
        this.context = context;
        this.messager = messager;
        this.log = Log.instance((Context)context);
        this.elements = JavacElements.instance((Context)context);
        this.treeMaker = TreeMaker.instance((Context)context);
        this.symtab = Symtab.instance((Context)context);
        this.javacTypes = JavacTypes.instance((Context)context);
        this.clearChanged();
    }

    private static String sourceName(JCTree.JCCompilationUnit cu) {
        return cu.sourcefile == null ? null : cu.sourcefile.toString();
    }

    private static String packageDeclaration(JCTree.JCCompilationUnit cu) {
        return cu.pid instanceof JCTree.JCFieldAccess || cu.pid instanceof JCTree.JCIdent ? cu.pid.toString() : null;
    }

    private static Collection<String> imports(JCTree.JCCompilationUnit cu) {
        ArrayList<String> imports = new ArrayList<String>();
        for (JCTree def : cu.defs) {
            if (!(def instanceof JCTree.JCImport)) continue;
            imports.add(((JCTree.JCImport)def).qualid.toString());
        }
        return imports;
    }

    public Context getContext() {
        return this.context;
    }

    public void traverse(JavacASTVisitor visitor) {
        ((JavacNode)this.top()).traverse(visitor);
    }

    void traverseChildren(JavacASTVisitor visitor, JavacNode node) {
        for (JavacNode child : new ArrayList(node.down())) {
            child.traverse(visitor);
        }
    }

    public Name toName(String name) {
        return this.elements.getName((CharSequence)name);
    }

    public TreeMaker getTreeMaker() {
        this.treeMaker.at(-1);
        return this.treeMaker;
    }

    public Symtab getSymbolTable() {
        return this.symtab;
    }

    public JavacTypes getTypesUtil() {
        return this.javacTypes;
    }

    @Override
    protected JavacNode buildTree(JCTree node, AST.Kind kind) {
        switch (kind) {
            case COMPILATION_UNIT: {
                return this.buildCompilationUnit((JCTree.JCCompilationUnit)node);
            }
            case TYPE: {
                return this.buildType((JCTree.JCClassDecl)node);
            }
            case FIELD: {
                return this.buildField((JCTree.JCVariableDecl)node);
            }
            case INITIALIZER: {
                return this.buildInitializer((JCTree.JCBlock)node);
            }
            case METHOD: {
                return this.buildMethod((JCTree.JCMethodDecl)node);
            }
            case ARGUMENT: {
                return this.buildLocalVar((JCTree.JCVariableDecl)node, kind);
            }
            case LOCAL: {
                return this.buildLocalVar((JCTree.JCVariableDecl)node, kind);
            }
            case STATEMENT: {
                return this.buildStatementOrExpression(node);
            }
            case ANNOTATION: {
                return this.buildAnnotation((JCTree.JCAnnotation)node, false);
            }
        }
        throw new AssertionError((Object)("Did not expect: " + (Object)((Object)kind)));
    }

    private JavacNode buildCompilationUnit(JCTree.JCCompilationUnit top) {
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree s : top.defs) {
            if (!(s instanceof JCTree.JCClassDecl)) continue;
            JavacAST.addIfNotNull(childNodes, this.buildType((JCTree.JCClassDecl)s));
        }
        return new JavacNode(this, (JCTree)top, childNodes, AST.Kind.COMPILATION_UNIT);
    }

    private JavacNode buildType(JCTree.JCClassDecl type) {
        if (this.setAndGetAsHandled(type)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCAnnotation annotation : type.mods.annotations) {
            JavacAST.addIfNotNull(childNodes, this.buildAnnotation(annotation, false));
        }
        for (JCTree def : type.defs) {
            if (def instanceof JCTree.JCMethodDecl) {
                JavacAST.addIfNotNull(childNodes, this.buildMethod((JCTree.JCMethodDecl)def));
                continue;
            }
            if (def instanceof JCTree.JCClassDecl) {
                JavacAST.addIfNotNull(childNodes, this.buildType((JCTree.JCClassDecl)def));
                continue;
            }
            if (def instanceof JCTree.JCVariableDecl) {
                JavacAST.addIfNotNull(childNodes, this.buildField((JCTree.JCVariableDecl)def));
                continue;
            }
            if (!(def instanceof JCTree.JCBlock)) continue;
            JavacAST.addIfNotNull(childNodes, this.buildInitializer((JCTree.JCBlock)def));
        }
        return this.putInMap(new JavacNode(this, (JCTree)type, childNodes, AST.Kind.TYPE));
    }

    private JavacNode buildField(JCTree.JCVariableDecl field) {
        if (this.setAndGetAsHandled(field)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCAnnotation annotation : field.mods.annotations) {
            JavacAST.addIfNotNull(childNodes, this.buildAnnotation(annotation, true));
        }
        JavacAST.addIfNotNull(childNodes, this.buildExpression(field.init));
        return this.putInMap(new JavacNode(this, (JCTree)field, childNodes, AST.Kind.FIELD));
    }

    private JavacNode buildLocalVar(JCTree.JCVariableDecl local, AST.Kind kind) {
        if (this.setAndGetAsHandled(local)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCAnnotation annotation : local.mods.annotations) {
            JavacAST.addIfNotNull(childNodes, this.buildAnnotation(annotation, true));
        }
        JavacAST.addIfNotNull(childNodes, this.buildExpression(local.init));
        return this.putInMap(new JavacNode(this, (JCTree)local, childNodes, kind));
    }

    private JavacNode buildInitializer(JCTree.JCBlock initializer) {
        if (this.setAndGetAsHandled(initializer)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCStatement statement : initializer.stats) {
            JavacAST.addIfNotNull(childNodes, this.buildStatement(statement));
        }
        return this.putInMap(new JavacNode(this, (JCTree)initializer, childNodes, AST.Kind.INITIALIZER));
    }

    private JavacNode buildMethod(JCTree.JCMethodDecl method) {
        if (this.setAndGetAsHandled(method)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCAnnotation annotation : method.mods.annotations) {
            JavacAST.addIfNotNull(childNodes, this.buildAnnotation(annotation, false));
        }
        for (JCTree.JCVariableDecl param : method.params) {
            JavacAST.addIfNotNull(childNodes, this.buildLocalVar(param, AST.Kind.ARGUMENT));
        }
        if (method.body != null && method.body.stats != null) {
            for (JCTree.JCStatement statement : method.body.stats) {
                JavacAST.addIfNotNull(childNodes, this.buildStatement(statement));
            }
        }
        return this.putInMap(new JavacNode(this, (JCTree)method, childNodes, AST.Kind.METHOD));
    }

    private JavacNode buildAnnotation(JCTree.JCAnnotation annotation, boolean varDecl) {
        boolean handled = this.setAndGetAsHandled(annotation);
        if (!varDecl && handled) {
            return null;
        }
        return this.putInMap(new JavacNode(this, (JCTree)annotation, null, AST.Kind.ANNOTATION));
    }

    private JavacNode buildExpression(JCTree.JCExpression expression) {
        return this.buildStatementOrExpression((JCTree)expression);
    }

    private JavacNode buildStatement(JCTree.JCStatement statement) {
        return this.buildStatementOrExpression((JCTree)statement);
    }

    private JavacNode buildStatementOrExpression(JCTree statement) {
        if (statement == null) {
            return null;
        }
        if (statement instanceof JCTree.JCAnnotation) {
            return null;
        }
        if (statement instanceof JCTree.JCClassDecl) {
            return this.buildType((JCTree.JCClassDecl)statement);
        }
        if (statement instanceof JCTree.JCVariableDecl) {
            return this.buildLocalVar((JCTree.JCVariableDecl)statement, AST.Kind.LOCAL);
        }
        if (this.setAndGetAsHandled(statement)) {
            return null;
        }
        return this.drill(statement);
    }

    private JavacNode drill(JCTree statement) {
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (AST.FieldAccess fa : this.fieldsOf(statement.getClass())) {
            childNodes.addAll(this.buildWithField(JavacNode.class, statement, fa));
        }
        return this.putInMap(new JavacNode(this, statement, childNodes, AST.Kind.STATEMENT));
    }

    @Override
    protected Collection<Class<? extends JCTree>> getStatementTypes() {
        ArrayList<Class<? extends JCTree>> collection = new ArrayList<Class<? extends JCTree>>(3);
        collection.add(JCTree.JCStatement.class);
        collection.add(JCTree.JCExpression.class);
        collection.add(JCTree.JCCatch.class);
        return collection;
    }

    private static void addIfNotNull(Collection<JavacNode> nodes, JavacNode node) {
        if (node != null) {
            nodes.add(node);
        }
    }

    void printMessage(Diagnostic.Kind kind, String message, JavacNode node, JCDiagnostic.DiagnosticPosition pos) {
        JavaFileObject oldSource = null;
        JavaFileObject newSource = null;
        JCTree astObject = node == null ? null : (JCTree)node.get();
        JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)this.top()).get();
        newSource = top.sourcefile;
        if (newSource != null) {
            oldSource = this.log.useSource(newSource);
            if (pos == null) {
                pos = astObject.pos();
            }
        }
        try {
            switch (kind) {
                case ERROR: {
                    this.increaseErrorCount(this.messager);
                    boolean prev = this.log.multipleErrors;
                    this.log.multipleErrors = true;
                    try {
                        this.log.error(pos, "proc.messager", new Object[]{message});
                    }
                    finally {
                        this.log.multipleErrors = prev;
                    }
                }
                default: {
                    this.log.warning(pos, "proc.messager", new Object[]{message});
                }
            }
        }
        finally {
            if (oldSource != null) {
                this.log.useSource(oldSource);
            }
        }
    }

    @Override
    protected void setElementInASTCollection(Field field, Object refField, java.util.List<Collection<?>> chain, Collection<?> collection, int idx, JCTree newN) throws IllegalAccessException {
        List list = this.setElementInConsList(chain, collection, ((java.util.List)collection).get(idx), (Object)newN);
        field.set(refField, (Object)list);
    }

    private List<?> setElementInConsList(java.util.List<Collection<?>> chain, Collection<?> current, Object oldO, Object newO) {
        List oldL = (List)current;
        List newL = this.replaceInConsList(oldL, oldO, newO);
        if (chain.isEmpty()) {
            return newL;
        }
        ArrayList reducedChain = new ArrayList(chain);
        Collection newCurrent = reducedChain.remove(reducedChain.size() - 1);
        return this.setElementInConsList(reducedChain, newCurrent, (Object)oldL, (Object)newL);
    }

    private List<?> replaceInConsList(List<?> oldL, Object oldO, Object newO) {
        boolean repl = false;
        Object[] a = oldL.toArray();
        for (int i = 0; i < a.length; ++i) {
            if (a[i] != oldO) continue;
            a[i] = newO;
            repl = true;
        }
        if (repl) {
            return List.from((Object[])a);
        }
        return oldL;
    }

    private void increaseErrorCount(Messager m) {
        try {
            Field f = m.getClass().getDeclaredField("errorCount");
            f.setAccessible(true);
            if (f.getType() == Integer.TYPE) {
                int val2 = ((Number)f.get(m)).intValue();
                f.set(m, val2 + 1);
            }
        }
        catch (Throwable t) {
            // empty catch block
        }
    }

}

