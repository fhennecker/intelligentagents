/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.BoundKind
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$ClassSymbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Symtab
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.Type$ArrayType
 *  com.sun.tools.javac.code.Type$CapturedType
 *  com.sun.tools.javac.code.Type$ClassType
 *  com.sun.tools.javac.code.Type$WildcardType
 *  com.sun.tools.javac.code.TypeTags
 *  com.sun.tools.javac.code.Types
 *  com.sun.tools.javac.comp.Attr
 *  com.sun.tools.javac.comp.AttrContext
 *  com.sun.tools.javac.comp.Enter
 *  com.sun.tools.javac.comp.Env
 *  com.sun.tools.javac.comp.MemberEnter
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCArrayTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCPrimitiveTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.JCTree$JCWildcard
 *  com.sun.tools.javac.tree.JCTree$TypeBoundKind
 *  com.sun.tools.javac.tree.JCTree$Visitor
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Log
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac;

import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javax.lang.model.type.TypeKind;
import javax.tools.DiagnosticListener;
import lombok.javac.Javac;
import lombok.javac.JavacAST;
import lombok.javac.JavacNode;
import lombok.javac.TreeMirrorMaker;

public class JavacResolution {
    private final Attr attr;
    private final LogDisabler logDisabler;

    public JavacResolution(Context context) {
        this.attr = Attr.instance((Context)context);
        this.logDisabler = new LogDisabler(context);
    }

    public Map<JCTree, JCTree> resolveMethodMember(JavacNode node) {
        ArrayDeque stack = new ArrayDeque();
        for (JavacNode n = node; n != null; n = (JavacNode)n.up()) {
            stack.push(n.get());
        }
        this.logDisabler.disableLoggers();
        try {
            EnvFinder finder = new EnvFinder(node.getContext());
            while (!stack.isEmpty()) {
                ((JCTree)stack.pop()).accept((JCTree.Visitor)finder);
            }
            TreeMirrorMaker mirrorMaker = new TreeMirrorMaker(node.getTreeMaker());
            JCTree copy = mirrorMaker.copy(finder.copyAt());
            this.attrib(copy, finder.get());
            Map<JCTree, JCTree> map = mirrorMaker.getOriginalToCopyMap();
            return map;
        }
        finally {
            this.logDisabler.enableLoggers();
        }
    }

    public void resolveClassMember(JavacNode node) {
        ArrayDeque stack = new ArrayDeque();
        for (JavacNode n = node; n != null; n = (JavacNode)n.up()) {
            stack.push(n.get());
        }
        this.logDisabler.disableLoggers();
        try {
            EnvFinder finder = new EnvFinder(node.getContext());
            while (!stack.isEmpty()) {
                ((JCTree)stack.pop()).accept((JCTree.Visitor)finder);
            }
            this.attrib((JCTree)node.get(), finder.get());
        }
        finally {
            this.logDisabler.enableLoggers();
        }
    }

    private void attrib(JCTree tree, Env<AttrContext> env) {
        if (tree instanceof JCTree.JCBlock) {
            this.attr.attribStat(tree, env);
        } else if (tree instanceof JCTree.JCMethodDecl) {
            this.attr.attribStat((JCTree)((JCTree.JCMethodDecl)tree).body, env);
        } else if (tree instanceof JCTree.JCVariableDecl) {
            this.attr.attribStat(tree, env);
        } else {
            throw new IllegalStateException("Called with something that isn't a block, method decl, or variable decl");
        }
    }

    public static Type ifTypeIsIterableToComponent(Type type, JavacAST ast) {
        Types types = Types.instance((Context)ast.getContext());
        Symtab syms = Symtab.instance((Context)ast.getContext());
        Type boundType = types.upperBound(type);
        Type elemTypeIfArray = types.elemtype(boundType);
        if (elemTypeIfArray != null) {
            return elemTypeIfArray;
        }
        Type base = types.asSuper(boundType, (Symbol)syms.iterableType.tsym);
        if (base == null) {
            return syms.objectType;
        }
        List iterableParams = base.allparams();
        return iterableParams.isEmpty() ? syms.objectType : types.upperBound((Type)iterableParams.head);
    }

    public static JCTree.JCExpression typeToJCTree(Type type, JavacAST ast, boolean allowVoid) throws TypeNotConvertibleException {
        return JavacResolution.typeToJCTree(type, ast, false, allowVoid);
    }

    public static JCTree.JCExpression createJavaLangObject(JavacAST ast) {
        TreeMaker maker = ast.getTreeMaker();
        JCTree.JCIdent out = maker.Ident(ast.toName("java"));
        out = maker.Select((JCTree.JCExpression)out, ast.toName("lang"));
        out = maker.Select((JCTree.JCExpression)out, ast.toName("Object"));
        return out;
    }

    private static JCTree.JCExpression typeToJCTree(Type type, JavacAST ast, boolean allowCompound, boolean allowVoid) throws TypeNotConvertibleException {
        int dims = 0;
        Type type0 = type;
        while (type0 instanceof Type.ArrayType) {
            ++dims;
            type0 = ((Type.ArrayType)type0).elemtype;
        }
        JCTree.JCExpression result = JavacResolution.typeToJCTree0(type0, ast, allowCompound, allowVoid);
        while (dims > 0) {
            result = ast.getTreeMaker().TypeArray(result);
            --dims;
        }
        return result;
    }

    private static JCTree.JCExpression typeToJCTree0(Type type, JavacAST ast, boolean allowCompound, boolean allowVoid) throws TypeNotConvertibleException {
        String qName;
        TreeMaker maker = ast.getTreeMaker();
        if (type.tag == Javac.getCtcInt(TypeTags.class, "BOT")) {
            return JavacResolution.createJavaLangObject(ast);
        }
        if (type.tag == Javac.getCtcInt(TypeTags.class, "VOID")) {
            return allowVoid ? JavacResolution.primitiveToJCTree(type.getKind(), maker) : JavacResolution.createJavaLangObject(ast);
        }
        if (type.isPrimitive()) {
            return JavacResolution.primitiveToJCTree(type.getKind(), maker);
        }
        if (type.isErroneous()) {
            throw new TypeNotConvertibleException("Type cannot be resolved");
        }
        Symbol.TypeSymbol symbol = type.asElement();
        List generics = type.getTypeArguments();
        JCTree.JCExpression replacement = null;
        if (symbol == null) {
            throw new TypeNotConvertibleException("Null or compound type");
        }
        if (symbol.name.length() == 0) {
            if (type instanceof Type.ClassType) {
                List ifaces = ((Type.ClassType)type).interfaces_field;
                Type supertype = ((Type.ClassType)type).supertype_field;
                if (ifaces != null && ifaces.length() == 1) {
                    return JavacResolution.typeToJCTree((Type)ifaces.get(0), ast, allowCompound, allowVoid);
                }
                if (supertype != null) {
                    return JavacResolution.typeToJCTree(supertype, ast, allowCompound, allowVoid);
                }
            }
            throw new TypeNotConvertibleException("Anonymous inner class");
        }
        if (type instanceof Type.CapturedType || type instanceof Type.WildcardType) {
            Type lower;
            Type upper;
            if (type instanceof Type.WildcardType) {
                upper = ((Type.WildcardType)type).getExtendsBound();
                lower = ((Type.WildcardType)type).getSuperBound();
            } else {
                lower = type.getLowerBound();
                upper = type.getUpperBound();
            }
            if (allowCompound) {
                if (lower == null || lower.tag == Javac.getCtcInt(TypeTags.class, "BOT")) {
                    if (upper == null || upper.toString().equals("java.lang.Object")) {
                        return maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
                    }
                    if (upper.getTypeArguments().contains((Object)type)) {
                        return maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
                    }
                    return maker.Wildcard(maker.TypeBoundKind(BoundKind.EXTENDS), (JCTree)JavacResolution.typeToJCTree(upper, ast, false, false));
                }
                return maker.Wildcard(maker.TypeBoundKind(BoundKind.SUPER), (JCTree)JavacResolution.typeToJCTree(lower, ast, false, false));
            }
            if (upper != null) {
                if (upper.getTypeArguments().contains((Object)type)) {
                    return maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
                }
                return JavacResolution.typeToJCTree(upper, ast, allowCompound, allowVoid);
            }
            return JavacResolution.createJavaLangObject(ast);
        }
        if (symbol.isLocal()) {
            qName = symbol.getSimpleName().toString();
        } else if (symbol.type != null && symbol.type.getEnclosingType() != null && symbol.type.getEnclosingType().tag == 10) {
            replacement = JavacResolution.typeToJCTree0(type.getEnclosingType(), ast, false, false);
            qName = symbol.getSimpleName().toString();
        } else {
            qName = symbol.getQualifiedName().toString();
        }
        if (qName.isEmpty()) {
            throw new TypeNotConvertibleException("unknown type");
        }
        if (qName.startsWith("<")) {
            throw new TypeNotConvertibleException(qName);
        }
        String[] baseNames = qName.split("\\.");
        int i = 0;
        if (replacement == null) {
            replacement = maker.Ident(ast.toName(baseNames[0]));
            i = 1;
        }
        while (i < baseNames.length) {
            replacement = maker.Select(replacement, ast.toName(baseNames[i]));
            ++i;
        }
        return JavacResolution.genericsToJCTreeNodes(generics, ast, replacement);
    }

    private static JCTree.JCExpression genericsToJCTreeNodes(List<Type> generics, JavacAST ast, JCTree.JCExpression rawTypeNode) throws TypeNotConvertibleException {
        if (generics != null && !generics.isEmpty()) {
            ListBuffer args = ListBuffer.lb();
            for (Type t : generics) {
                args.append((Object)JavacResolution.typeToJCTree(t, ast, true, false));
            }
            return ast.getTreeMaker().TypeApply(rawTypeNode, args.toList());
        }
        return rawTypeNode;
    }

    private static JCTree.JCExpression primitiveToJCTree(TypeKind kind, TreeMaker maker) throws TypeNotConvertibleException {
        switch (kind) {
            case BYTE: {
                return maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "BYTE"));
            }
            case CHAR: {
                return maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "CHAR"));
            }
            case SHORT: {
                return maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "SHORT"));
            }
            case INT: {
                return maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "INT"));
            }
            case LONG: {
                return maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "LONG"));
            }
            case FLOAT: {
                return maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "FLOAT"));
            }
            case DOUBLE: {
                return maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "DOUBLE"));
            }
            case BOOLEAN: {
                return maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "BOOLEAN"));
            }
            case VOID: {
                return maker.TypeIdent(Javac.getCtcInt(TypeTags.class, "VOID"));
            }
        }
        throw new TypeNotConvertibleException("Nulltype");
    }

    public static class TypeNotConvertibleException
    extends Exception {
        public TypeNotConvertibleException(String msg) {
            super(msg);
        }
    }

    private static final class EnvFinder
    extends JCTree.Visitor {
        private Env<AttrContext> env = null;
        private Enter enter;
        private MemberEnter memberEnter;
        private JCTree copyAt = null;

        EnvFinder(Context context) {
            this.enter = Enter.instance((Context)context);
            this.memberEnter = MemberEnter.instance((Context)context);
        }

        Env<AttrContext> get() {
            return this.env;
        }

        JCTree copyAt() {
            return this.copyAt;
        }

        public void visitTopLevel(JCTree.JCCompilationUnit tree) {
            if (this.copyAt != null) {
                return;
            }
            this.env = this.enter.getTopLevelEnv(tree);
        }

        public void visitClassDef(JCTree.JCClassDecl tree) {
            if (this.copyAt != null) {
                return;
            }
            this.env = this.enter.getClassEnv((Symbol.TypeSymbol)tree.sym);
        }

        public void visitMethodDef(JCTree.JCMethodDecl tree) {
            if (this.copyAt != null) {
                return;
            }
            this.env = this.memberEnter.getMethodEnv(tree, this.env);
            this.copyAt = tree;
        }

        public void visitVarDef(JCTree.JCVariableDecl tree) {
            if (this.copyAt != null) {
                return;
            }
            this.env = this.memberEnter.getInitEnv(tree, this.env);
            this.copyAt = tree;
        }

        public void visitBlock(JCTree.JCBlock tree) {
            if (this.copyAt != null) {
                return;
            }
            this.copyAt = tree;
        }

        public void visitTree(JCTree that) {
        }
    }

    private static final class LogDisabler {
        private final Log log;
        private static final Field errWriterField;
        private static final Field warnWriterField;
        private static final Field noticeWriterField;
        private static final Field dumpOnErrorField;
        private static final Field promptOnErrorField;
        private static final Field diagnosticListenerField;
        private static final Field deferDiagnosticsField;
        private static final Field deferredDiagnosticsField;
        private PrintWriter errWriter;
        private PrintWriter warnWriter;
        private PrintWriter noticeWriter;
        private Boolean dumpOnError;
        private Boolean promptOnError;
        private DiagnosticListener<?> contextDiagnosticListener;
        private DiagnosticListener<?> logDiagnosticListener;
        private final Context context;
        private static final boolean dontBother;
        private static final ThreadLocal<Queue<?>> queueCache;

        LogDisabler(Context context) {
            this.log = Log.instance((Context)context);
            this.context = context;
        }

        boolean disableLoggers() {
            this.contextDiagnosticListener = (DiagnosticListener)this.context.get((Class)DiagnosticListener.class);
            this.context.put((Class)DiagnosticListener.class, (Object)null);
            if (dontBother) {
                return false;
            }
            boolean dontBotherInstance = false;
            PrintWriter dummyWriter = new PrintWriter(new OutputStream(){

                @Override
                public void write(int b) throws IOException {
                }
            });
            if (deferDiagnosticsField != null) {
                try {
                    if (Boolean.TRUE.equals(deferDiagnosticsField.get((Object)this.log))) {
                        queueCache.set((Queue)deferredDiagnosticsField.get((Object)this.log));
                        LinkedList empty = new LinkedList();
                        deferredDiagnosticsField.set((Object)this.log, empty);
                    }
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            if (!dontBotherInstance) {
                try {
                    this.errWriter = (PrintWriter)errWriterField.get((Object)this.log);
                    errWriterField.set((Object)this.log, dummyWriter);
                }
                catch (Exception e) {
                    dontBotherInstance = true;
                }
            }
            if (!dontBotherInstance) {
                try {
                    this.warnWriter = (PrintWriter)warnWriterField.get((Object)this.log);
                    warnWriterField.set((Object)this.log, dummyWriter);
                }
                catch (Exception e) {
                    dontBotherInstance = true;
                }
            }
            if (!dontBotherInstance) {
                try {
                    this.noticeWriter = (PrintWriter)noticeWriterField.get((Object)this.log);
                    noticeWriterField.set((Object)this.log, dummyWriter);
                }
                catch (Exception e) {
                    dontBotherInstance = true;
                }
            }
            if (!dontBotherInstance) {
                try {
                    this.dumpOnError = (Boolean)dumpOnErrorField.get((Object)this.log);
                    dumpOnErrorField.set((Object)this.log, false);
                }
                catch (Exception e) {
                    dontBotherInstance = true;
                }
            }
            if (!dontBotherInstance) {
                try {
                    this.promptOnError = (Boolean)promptOnErrorField.get((Object)this.log);
                    promptOnErrorField.set((Object)this.log, false);
                }
                catch (Exception e) {
                    dontBotherInstance = true;
                }
            }
            if (!dontBotherInstance) {
                try {
                    this.logDiagnosticListener = (DiagnosticListener)diagnosticListenerField.get((Object)this.log);
                    diagnosticListenerField.set((Object)this.log, null);
                }
                catch (Exception e) {
                    dontBotherInstance = true;
                }
            }
            if (dontBotherInstance) {
                this.enableLoggers();
            }
            return !dontBotherInstance;
        }

        void enableLoggers() {
            if (this.contextDiagnosticListener != null) {
                this.context.put((Class)DiagnosticListener.class, this.contextDiagnosticListener);
                this.contextDiagnosticListener = null;
            }
            if (this.errWriter != null) {
                try {
                    errWriterField.set((Object)this.log, this.errWriter);
                    this.errWriter = null;
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            if (this.warnWriter != null) {
                try {
                    warnWriterField.set((Object)this.log, this.warnWriter);
                    this.warnWriter = null;
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            if (this.noticeWriter != null) {
                try {
                    noticeWriterField.set((Object)this.log, this.noticeWriter);
                    this.noticeWriter = null;
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            if (this.dumpOnError != null) {
                try {
                    dumpOnErrorField.set((Object)this.log, this.dumpOnError);
                    this.dumpOnError = null;
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            if (this.promptOnError != null) {
                try {
                    promptOnErrorField.set((Object)this.log, this.promptOnError);
                    this.promptOnError = null;
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            if (this.logDiagnosticListener != null) {
                try {
                    diagnosticListenerField.set((Object)this.log, this.logDiagnosticListener);
                    this.logDiagnosticListener = null;
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            if (deferDiagnosticsField != null && queueCache.get() != null) {
                try {
                    deferredDiagnosticsField.set((Object)this.log, queueCache.get());
                    queueCache.set(null);
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
        }

        static {
            boolean z;
            queueCache = new ThreadLocal();
            Field a = null;
            Field b = null;
            Field c = null;
            Field d = null;
            Field e = null;
            Field f = null;
            Field g = null;
            Field h = null;
            try {
                a = Log.class.getDeclaredField("errWriter");
                b = Log.class.getDeclaredField("warnWriter");
                c = Log.class.getDeclaredField("noticeWriter");
                d = Log.class.getDeclaredField("dumpOnError");
                e = Log.class.getDeclaredField("promptOnError");
                f = Log.class.getDeclaredField("diagListener");
                z = false;
                a.setAccessible(true);
                b.setAccessible(true);
                c.setAccessible(true);
                d.setAccessible(true);
                e.setAccessible(true);
                f.setAccessible(true);
            }
            catch (Throwable x) {
                z = true;
            }
            try {
                g = Log.class.getDeclaredField("deferDiagnostics");
                h = Log.class.getDeclaredField("deferredDiagnostics");
                g.setAccessible(true);
                h.setAccessible(true);
            }
            catch (Throwable x) {
                // empty catch block
            }
            errWriterField = a;
            warnWriterField = b;
            noticeWriterField = c;
            dumpOnErrorField = d;
            promptOnErrorField = e;
            diagnosticListenerField = f;
            deferDiagnosticsField = g;
            deferredDiagnosticsField = h;
            dontBother = z;
        }

    }

}

