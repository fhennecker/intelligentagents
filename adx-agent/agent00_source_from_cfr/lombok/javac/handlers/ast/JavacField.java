/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.handlers.ast;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.ast.AST;
import lombok.ast.Annotation;
import lombok.ast.Expression;
import lombok.ast.IField;
import lombok.ast.IFieldEditor;
import lombok.ast.TypeRef;
import lombok.core.LombokNode;
import lombok.core.util.As;
import lombok.javac.Javac;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacASTUtil;
import lombok.javac.handlers.ast.JavacFieldEditor;

public final class JavacField
implements IField<JavacNode, JCTree, JCTree.JCVariableDecl> {
    private final JavacNode fieldNode;
    private final JavacFieldEditor editor;

    private JavacField(JavacNode fieldNode, JCTree source) {
        if (!(fieldNode.get() instanceof JCTree.JCVariableDecl)) {
            throw new IllegalArgumentException();
        }
        this.fieldNode = fieldNode;
        this.editor = new JavacFieldEditor(this, source);
    }

    public JavacFieldEditor editor() {
        return this.editor;
    }

    @Override
    public boolean isPrivate() {
        return (this.get().mods.flags & 2) != 0;
    }

    @Override
    public boolean isFinal() {
        return (this.get().mods.flags & 16) != 0;
    }

    @Override
    public boolean isStatic() {
        return (this.get().mods.flags & 8) != 0;
    }

    @Override
    public boolean isInitialized() {
        return this.get().init != null;
    }

    @Override
    public boolean isPrimitive() {
        return Javac.isPrimitive(this.get().vartype);
    }

    @Override
    public boolean hasJavaDoc() {
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit)((JavacNode)this.fieldNode.top()).get();
        return compilationUnit.docComments.get((Object)this.get()) != null;
    }

    @Override
    public JCTree.JCVariableDecl get() {
        return (JCTree.JCVariableDecl)this.fieldNode.get();
    }

    @Override
    public JavacNode node() {
        return this.fieldNode;
    }

    @Override
    public TypeRef type() {
        return AST.Type((Object)this.get().vartype);
    }

    @Override
    public TypeRef boxedType() {
        return JavacASTUtil.boxedType(this.get().vartype);
    }

    @Override
    public boolean isOfType(String typeName) {
        JCTree.JCExpression variableType = this.get().vartype;
        if (variableType == null) {
            return false;
        }
        String type = variableType instanceof JCTree.JCTypeApply ? ((JCTree.JCTypeApply)variableType).clazz.toString() : variableType.toString();
        return type.endsWith(typeName);
    }

    @Override
    public String name() {
        return this.node().getName();
    }

    @Override
    public Expression<?> initialization() {
        return this.get().init == null ? null : AST.Expr((Object)this.get().init);
    }

    @Override
    public java.util.List<TypeRef> typeArguments() {
        ArrayList<TypeRef> typeArguments = new ArrayList<TypeRef>();
        JCTree.JCExpression type = this.get().vartype;
        if (type instanceof JCTree.JCTypeApply) {
            JCTree.JCTypeApply typeRef = (JCTree.JCTypeApply)type;
            for (JCTree.JCExpression typeArgument : typeRef.arguments) {
                typeArguments.add(AST.Type(As.string((Object)typeArgument)));
            }
        }
        return typeArguments;
    }

    @Override
    public java.util.List<Annotation> annotations() {
        return this.annotations(null);
    }

    @Override
    public java.util.List<Annotation> annotations(Pattern namePattern) {
        ArrayList<Annotation> result = new ArrayList<Annotation>();
        for (JCTree.JCAnnotation annotation : this.get().mods.annotations) {
            String suspect;
            String name = annotation.annotationType.toString();
            int idx = name.lastIndexOf(".");
            String string = suspect = idx == -1 ? name : name.substring(idx + 1);
            if (namePattern != null && !namePattern.matcher(suspect).matches()) continue;
            Annotation ann = AST.Annotation(AST.Type((Object)annotation.annotationType));
            for (JCTree.JCExpression arg : annotation.args) {
                if (arg instanceof JCTree.JCAssign) {
                    JCTree.JCAssign assign = (JCTree.JCAssign)arg;
                    ann.withValue(assign.lhs.toString(), AST.Expr((Object)assign.rhs));
                    continue;
                }
                ann.withValue(AST.Expr((Object)arg));
            }
            result.add(ann);
        }
        return result;
    }

    public String toString() {
        return this.get().toString();
    }

    public static JavacField fieldOf(JavacNode node, JCTree source) {
        JavacNode fieldNode;
        for (fieldNode = node; fieldNode != null && !(fieldNode.get() instanceof JCTree.JCVariableDecl); fieldNode = (JavacNode)fieldNode.up()) {
        }
        return fieldNode == null ? null : new JavacField(fieldNode, source);
    }
}

