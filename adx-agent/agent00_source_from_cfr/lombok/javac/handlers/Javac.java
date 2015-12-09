/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCImport
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.lang.annotation.Annotation;
import java.util.Collection;
import lombok.core.AST;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

public final class Javac {
    public static void injectType(JavacNode typeNode, JCTree.JCClassDecl type) {
        JCTree.JCClassDecl typeDecl = (JCTree.JCClassDecl)typeNode.get();
        Javac.addSuppressWarningsAll(type.mods, typeNode, type.pos);
        typeDecl.defs = typeDecl.defs.append((Object)type);
        typeNode.add(type, AST.Kind.TYPE);
    }

    public static void injectInitializer(JavacNode typeNode, JCTree.JCBlock initializerBlock) {
        JCTree.JCClassDecl typeDecl = (JCTree.JCClassDecl)typeNode.get();
        typeDecl.defs = typeDecl.defs.append((Object)initializerBlock);
        typeNode.add(initializerBlock, AST.Kind.INITIALIZER);
    }

    public static void addSuppressWarningsAll(JCTree.JCModifiers mods, JavacNode node, int pos) {
        TreeMaker maker = node.getTreeMaker();
        JCTree.JCExpression suppressWarningsType = JavacHandlerUtil.chainDotsString(node, "java.lang.SuppressWarnings").setPos(pos);
        JCTree.JCExpression allLiteral = maker.Literal((Object)"all").setPos(pos);
        ListBuffer newAnnotations = ListBuffer.lb();
        for (JCTree.JCAnnotation annotation : mods.annotations) {
            if (annotation.annotationType.toString().endsWith("SuppressWarnings")) continue;
            newAnnotations.append((Object)annotation);
        }
        newAnnotations.append((Object)((JCTree.JCAnnotation)maker.Annotation((JCTree)suppressWarningsType, List.of((Object)allLiteral)).setPos(pos)));
        mods.annotations = newAnnotations.toList();
    }

    public static boolean isMethodCallValid(JavacNode node, String methodName, Class<?> clazz, String method) {
        Collection<String> importedStatements = node.getImportStatements();
        boolean wasImported = methodName.equals(clazz.getName() + "." + method);
        wasImported |= methodName.equals(clazz.getSimpleName() + "." + method) && importedStatements.contains(clazz.getName());
        return wasImported |= methodName.equals(method) && importedStatements.contains(clazz.getName() + "." + method);
    }

    public static <T> List<T> remove(List<T> list, T elementToRemove) {
        ListBuffer newList = ListBuffer.lb();
        for (Object element : list) {
            if (elementToRemove == element) continue;
            newList.append(element);
        }
        return newList.toList();
    }

    public static void markInterfaceAsProcessed(JavacNode typeNode, Class<?> interfazeType) {
        if (JavacHandlerUtil.inNetbeansEditor(typeNode)) {
            return;
        }
        JCTree.JCClassDecl typeDecl = null;
        if (typeNode.get() instanceof JCTree.JCClassDecl) {
            typeDecl = (JCTree.JCClassDecl)typeNode.get();
        }
        if (typeDecl != null) {
            ListBuffer newImplementing = ListBuffer.lb();
            for (JCTree.JCExpression exp : typeDecl.implementing) {
                if (exp.toString().equals(interfazeType.getName()) || exp.toString().equals(interfazeType.getSimpleName())) continue;
                newImplementing.append((Object)exp);
            }
            typeDecl.implementing = newImplementing.toList();
        }
    }

    public static void deleteMethodCallImports(JavacNode node, String methodName, Class<?> clazz, String method) {
        if (methodName.equals(method)) {
            Javac.deleteImport(node, clazz.getName() + "." + method, true);
        } else if (methodName.equals(clazz.getSimpleName() + "." + method)) {
            Javac.deleteImport(node, clazz);
        }
    }

    public static void deleteImport(JavacNode node, Class<?> clazz) {
        Javac.deleteImport(node, clazz.getName());
    }

    public static void deleteImport(JavacNode node, String name) {
        Javac.deleteImport(node, name, false);
    }

    public static void deleteImport(JavacNode node, String name, boolean deleteStatic) {
        if (JavacHandlerUtil.inNetbeansEditor(node)) {
            return;
        }
        if (!node.shouldDeleteLombokAnnotations()) {
            return;
        }
        String adjustedName = name.replace("$", ".");
        JCTree.JCCompilationUnit unit = (JCTree.JCCompilationUnit)((JavacNode)node.top()).get();
        ListBuffer newDefs = ListBuffer.lb();
        for (JCTree def : unit.defs) {
            boolean delete = false;
            if (def instanceof JCTree.JCImport) {
                JCTree.JCImport imp0rt = (JCTree.JCImport)def;
                boolean bl = delete = (deleteStatic || !imp0rt.isStatic()) && imp0rt.qualid.toString().equals(adjustedName);
            }
            if (delete) continue;
            newDefs.append((Object)def);
        }
        unit.defs = newDefs.toList();
    }

    public static JavacNode methodNodeOf(JavacNode node) {
        JavacNode typeNode;
        if (node == null) {
            throw new IllegalArgumentException();
        }
        for (typeNode = node; typeNode != null && !(typeNode.get() instanceof JCTree.JCMethodDecl); typeNode = (JavacNode)typeNode.up()) {
        }
        return typeNode;
    }

    public static JavacNode typeNodeOf(JavacNode node) {
        JavacNode typeNode;
        if (node == null) {
            throw new IllegalArgumentException();
        }
        for (typeNode = node; typeNode != null && !(typeNode.get() instanceof JCTree.JCClassDecl); typeNode = (JavacNode)typeNode.up()) {
        }
        return typeNode;
    }

    public static JCTree.JCClassDecl typeDeclFiltering(JavacNode typeNode, long filterFlags) {
        JCTree.JCClassDecl typeDecl = null;
        if (typeNode != null && typeNode.get() instanceof JCTree.JCClassDecl) {
            typeDecl = (JCTree.JCClassDecl)typeNode.get();
        }
        if (typeDecl != null && (typeDecl.mods.flags & filterFlags) != 0) {
            typeDecl = null;
        }
        return typeDecl;
    }

    public static JCTree.JCAnnotation getAnnotation(Class<? extends Annotation> expectedType, JCTree.JCModifiers mods) {
        return Javac.getAnnotation(expectedType.getName(), mods);
    }

    public static JCTree.JCAnnotation getAnnotation(String typeName, JCTree.JCModifiers mods) {
        for (JCTree.JCAnnotation ann : mods.annotations) {
            if (!Javac.matchesType(ann, typeName)) continue;
            return ann;
        }
        return null;
    }

    public static boolean matchesType(JCTree.JCAnnotation ann, String typeName) {
        return typeName.replace("$", ".").endsWith(ann.annotationType.toString());
    }

    private Javac() {
    }
}

