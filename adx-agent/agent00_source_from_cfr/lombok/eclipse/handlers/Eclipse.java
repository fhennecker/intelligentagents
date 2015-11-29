/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ImportReference
 *  org.eclipse.jdt.internal.compiler.ast.Initializer
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression
 *  org.eclipse.jdt.internal.compiler.ast.ThisReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.ClassScope
 *  org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 */
package lombok.eclipse.handlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import lombok.core.AST;
import lombok.core.util.Each;
import lombok.core.util.Is;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public final class Eclipse {
    public static void setGeneratedByAndCopyPos(ASTNode target, ASTNode source, ASTNode position) {
        EclipseHandlerUtil.setGeneratedBy(target, source);
        Eclipse.copyPosTo(target, position);
    }

    public static void injectType(EclipseNode typeNode, TypeDeclaration type) {
        type.annotations = EclipseHandlerUtil.createSuppressWarningsAll((ASTNode)type, type.annotations);
        TypeDeclaration parent = (TypeDeclaration)typeNode.get();
        if (parent.memberTypes == null) {
            parent.memberTypes = new TypeDeclaration[]{type};
        } else {
            TypeDeclaration[] newArray = new TypeDeclaration[parent.memberTypes.length + 1];
            System.arraycopy(parent.memberTypes, 0, newArray, 0, parent.memberTypes.length);
            newArray[parent.memberTypes.length] = type;
            parent.memberTypes = newArray;
        }
        typeNode.add(type, AST.Kind.TYPE);
    }

    public static void injectInitializer(EclipseNode typeNode, Initializer initializerBlock) {
        TypeDeclaration parent = (TypeDeclaration)typeNode.get();
        if (parent.fields == null) {
            parent.fields = new FieldDeclaration[]{initializerBlock};
        } else {
            FieldDeclaration[] newArray = new FieldDeclaration[parent.fields.length + 1];
            System.arraycopy(parent.fields, 0, newArray, 0, parent.fields.length);
            newArray[parent.fields.length] = initializerBlock;
            parent.fields = newArray;
        }
        typeNode.add(initializerBlock, AST.Kind.INITIALIZER);
    }

    public static void copyPosTo(ASTNode target, ASTNode source) {
        if (source == null) {
            return;
        }
        if (source instanceof AbstractMethodDeclaration) {
            target.sourceStart = ((AbstractMethodDeclaration)source).bodyStart;
            target.sourceEnd = ((AbstractMethodDeclaration)source).bodyEnd;
        } else if (source instanceof TypeDeclaration) {
            target.sourceStart = ((TypeDeclaration)source).bodyStart;
            target.sourceEnd = ((TypeDeclaration)source).bodyEnd;
        } else {
            target.sourceStart = source.sourceStart;
            target.sourceEnd = source.sourceEnd;
        }
        if (target instanceof AbstractMethodDeclaration) {
            ((AbstractMethodDeclaration)target).bodyStart = target.sourceStart;
            ((AbstractMethodDeclaration)target).bodyEnd = target.sourceEnd;
            if (source instanceof AbstractMethodDeclaration) {
                ((AbstractMethodDeclaration)target).declarationSourceStart = ((AbstractMethodDeclaration)source).declarationSourceStart;
                ((AbstractMethodDeclaration)target).declarationSourceEnd = ((AbstractMethodDeclaration)source).declarationSourceEnd;
            } else {
                ((AbstractMethodDeclaration)target).declarationSourceStart = target.sourceStart;
                ((AbstractMethodDeclaration)target).declarationSourceEnd = target.sourceEnd;
            }
        } else if (target instanceof TypeDeclaration) {
            ((TypeDeclaration)target).bodyStart = target.sourceStart;
            ((TypeDeclaration)target).bodyEnd = target.sourceEnd;
            if (source instanceof TypeDeclaration) {
                ((TypeDeclaration)target).declarationSourceStart = ((TypeDeclaration)source).declarationSourceStart;
                ((TypeDeclaration)target).declarationSourceEnd = ((TypeDeclaration)source).declarationSourceEnd;
            } else {
                ((TypeDeclaration)target).declarationSourceStart = target.sourceStart;
                ((TypeDeclaration)target).declarationSourceEnd = target.sourceEnd;
            }
        } else if (target instanceof Initializer) {
            ((Initializer)target).declarationSourceStart = target.sourceStart;
            ((Initializer)target).declarationSourceEnd = target.sourceEnd;
        } else if (target instanceof FieldDeclaration) {
            target.sourceStart = 0;
            target.sourceEnd = 0;
            ((AbstractVariableDeclaration)target).declarationSourceEnd = -1;
        }
        if (target instanceof Expression) {
            ((Expression)target).statementEnd = target.sourceEnd;
        }
        if (target instanceof QualifiedAllocationExpression && ((QualifiedAllocationExpression)target).anonymousType != null) {
            ((QualifiedAllocationExpression)target).anonymousType.bodyEnd = target.sourceEnd + 2;
            ((QualifiedAllocationExpression)target).anonymousType.sourceEnd = 0;
            target.sourceStart -= 4;
        }
        if (target instanceof Annotation) {
            ((Annotation)target).declarationSourceEnd = target.sourceEnd;
        }
    }

    public static String getMethodName(MessageSend methodCall) {
        String methodName = methodCall.receiver instanceof ThisReference ? "" : (Object)methodCall.receiver + ".";
        methodName = methodName + new String(methodCall.selector);
        return methodName;
    }

    public static boolean isMethodCallValid(EclipseNode node, String methodName, Class<?> clazz, String method) {
        Collection<String> importedStatements = node.getImportStatements();
        boolean wasImported = methodName.equals(clazz.getName() + "." + method);
        wasImported |= methodName.equals(clazz.getSimpleName() + "." + method) && importedStatements.contains(clazz.getName());
        return wasImported |= methodName.equals(method) && importedStatements.contains(clazz.getName() + "." + method);
    }

    public static void deleteMethodCallImports(EclipseNode node, String methodName, Class<?> clazz, String method) {
        if (methodName.equals(method)) {
            Eclipse.deleteImport(node, clazz.getName() + "." + method, true);
        } else if (methodName.equals(clazz.getSimpleName() + "." + method)) {
            Eclipse.deleteImport(node, clazz.getName(), false);
        }
    }

    public static void deleteImport(EclipseNode node, String name) {
        Eclipse.deleteImport(node, name, false);
    }

    public static void deleteImport(EclipseNode node, String name, boolean deleteStatic) {
        CompilationUnitDeclaration unit = (CompilationUnitDeclaration)((EclipseNode)node.top()).get();
        ArrayList<ImportReference> newImports = new ArrayList<ImportReference>();
        for (ImportReference imp0rt : Each.elementIn(unit.imports)) {
            boolean delete = (deleteStatic || !imp0rt.isStatic()) && imp0rt.toString().equals(name);
            if (delete) continue;
            newImports.add(imp0rt);
        }
        unit.imports = newImports.toArray((T[])new ImportReference[newImports.size()]);
    }

    public static EclipseNode methodNodeOf(EclipseNode node) {
        EclipseNode typeNode;
        if (node == null) {
            throw new IllegalArgumentException();
        }
        for (typeNode = node; typeNode != null && !(typeNode.get() instanceof AbstractMethodDeclaration); typeNode = (EclipseNode)typeNode.up()) {
        }
        return typeNode;
    }

    public static EclipseNode typeNodeOf(EclipseNode node) {
        EclipseNode typeNode;
        if (node == null) {
            throw new IllegalArgumentException();
        }
        for (typeNode = node; typeNode != null && !(typeNode.get() instanceof TypeDeclaration); typeNode = (EclipseNode)typeNode.up()) {
        }
        return typeNode;
    }

    public static TypeDeclaration typeDeclFiltering(EclipseNode typeNode, long filterFlags) {
        TypeDeclaration typeDecl = null;
        if (typeNode != null && typeNode.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)typeNode.get();
        }
        if (typeDecl != null && ((long)typeDecl.modifiers & filterFlags) != 0) {
            typeDecl = null;
        }
        return typeDecl;
    }

    public static boolean hasAnnotations(TypeDeclaration decl) {
        return decl != null && Is.notEmpty(decl.annotations);
    }

    public static Annotation getAnnotation(Class<? extends java.lang.annotation.Annotation> expectedType, Annotation[] annotations) {
        return Eclipse.getAnnotation(expectedType.getName(), annotations);
    }

    public static Annotation getAnnotation(String typeName, Annotation[] annotations) {
        for (Annotation ann : Each.elementIn(annotations)) {
            if (!Eclipse.matchesType(ann, typeName)) continue;
            return ann;
        }
        return null;
    }

    public static boolean matchesType(Annotation ann, String typeName) {
        return typeName.replace("$", ".").endsWith(ann.type.toString());
    }

    public static void ensureAllClassScopeMethodWereBuild(TypeBinding binding) {
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

    private Eclipse() {
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

}

