/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.source.tree.Tree
 *  com.sun.source.tree.Tree$Kind
 *  com.sun.tools.javac.code.BoundKind
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCArrayAccess
 *  com.sun.tools.javac.tree.JCTree$JCArrayTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCAssert
 *  com.sun.tools.javac.tree.JCTree$JCAssign
 *  com.sun.tools.javac.tree.JCTree$JCAssignOp
 *  com.sun.tools.javac.tree.JCTree$JCBinary
 *  com.sun.tools.javac.tree.JCTree$JCBlock
 *  com.sun.tools.javac.tree.JCTree$JCBreak
 *  com.sun.tools.javac.tree.JCTree$JCCase
 *  com.sun.tools.javac.tree.JCTree$JCCatch
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.tree.JCTree$JCConditional
 *  com.sun.tools.javac.tree.JCTree$JCContinue
 *  com.sun.tools.javac.tree.JCTree$JCDoWhileLoop
 *  com.sun.tools.javac.tree.JCTree$JCEnhancedForLoop
 *  com.sun.tools.javac.tree.JCTree$JCErroneous
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCExpressionStatement
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCForLoop
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCIf
 *  com.sun.tools.javac.tree.JCTree$JCImport
 *  com.sun.tools.javac.tree.JCTree$JCInstanceOf
 *  com.sun.tools.javac.tree.JCTree$JCLabeledStatement
 *  com.sun.tools.javac.tree.JCTree$JCLiteral
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCNewArray
 *  com.sun.tools.javac.tree.JCTree$JCNewClass
 *  com.sun.tools.javac.tree.JCTree$JCParens
 *  com.sun.tools.javac.tree.JCTree$JCPrimitiveTypeTree
 *  com.sun.tools.javac.tree.JCTree$JCReturn
 *  com.sun.tools.javac.tree.JCTree$JCSkip
 *  com.sun.tools.javac.tree.JCTree$JCStatement
 *  com.sun.tools.javac.tree.JCTree$JCSwitch
 *  com.sun.tools.javac.tree.JCTree$JCSynchronized
 *  com.sun.tools.javac.tree.JCTree$JCThrow
 *  com.sun.tools.javac.tree.JCTree$JCTry
 *  com.sun.tools.javac.tree.JCTree$JCTypeApply
 *  com.sun.tools.javac.tree.JCTree$JCTypeCast
 *  com.sun.tools.javac.tree.JCTree$JCTypeParameter
 *  com.sun.tools.javac.tree.JCTree$JCUnary
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.JCTree$JCWhileLoop
 *  com.sun.tools.javac.tree.JCTree$JCWildcard
 *  com.sun.tools.javac.tree.JCTree$LetExpr
 *  com.sun.tools.javac.tree.JCTree$TypeBoundKind
 *  com.sun.tools.javac.tree.JCTree$Visitor
 *  com.sun.tools.javac.tree.TreeInfo
 *  com.sun.tools.javac.tree.TreeScanner
 *  com.sun.tools.javac.util.Convert
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 *  com.sun.tools.javac.util.Name$Table
 */
package lombok.delombok;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Convert;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import lombok.javac.CommentInfo;
import lombok.javac.Javac;

public class PrettyCommentsPrinter
extends JCTree.Visitor {
    private static final Method GET_TAG_METHOD;
    private static final Field TAG_FIELD;
    private static final int PARENS;
    private static final int IMPORT;
    private static final int VARDEF;
    private static final int SELECT;
    private static final Map<Integer, String> OPERATORS;
    private List<CommentInfo> comments;
    private final JCTree.JCCompilationUnit cu;
    private boolean onNewLine = true;
    private boolean aligned = false;
    private boolean inParams = false;
    private boolean needsSpace = false;
    private boolean needsNewLine = false;
    private boolean needsAlign = false;
    Writer out;
    int lmargin = 0;
    Name enclClassName;
    Map<JCTree, String> docComments = null;
    String lineSep = System.getProperty("line.separator");
    int prec;

    static int getTag(JCTree tree) {
        if (GET_TAG_METHOD != null) {
            try {
                return (Integer)GET_TAG_METHOD.invoke((Object)tree, new Object[0]);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }
        try {
            return TAG_FIELD.getInt((Object)tree);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public PrettyCommentsPrinter(Writer out, JCTree.JCCompilationUnit cu, List<CommentInfo> comments) {
        this.out = out;
        this.comments = comments;
        this.cu = cu;
    }

    private int endPos(JCTree tree) {
        return tree.getEndPosition(this.cu.endPositions);
    }

    private void consumeComments(int till) throws IOException {
        boolean prevNewLine = this.onNewLine;
        boolean found = false;
        CommentInfo head = (CommentInfo)this.comments.head;
        while (this.comments.nonEmpty() && head.pos < till) {
            this.printComment(head);
            this.comments = this.comments.tail;
            head = (CommentInfo)this.comments.head;
        }
        if (!this.onNewLine && prevNewLine) {
            this.println();
        }
    }

    private void consumeTrailingComments(int from) throws IOException {
        boolean prevNewLine = this.onNewLine;
        CommentInfo head = (CommentInfo)this.comments.head;
        boolean stop = false;
        while (this.comments.nonEmpty() && head.prevEndPos == from && !stop && head.start != CommentInfo.StartConnection.ON_NEXT_LINE && head.start != CommentInfo.StartConnection.START_OF_LINE) {
            from = head.endPos;
            this.printComment(head);
            stop = head.end == CommentInfo.EndConnection.ON_NEXT_LINE;
            this.comments = this.comments.tail;
            head = (CommentInfo)this.comments.head;
        }
        if (!this.onNewLine && prevNewLine) {
            this.println();
        }
    }

    private void printComment(CommentInfo comment) throws IOException {
        this.prepareComment(comment.start);
        this.print(comment.content);
        switch (comment.end) {
            case ON_NEXT_LINE: {
                if (this.aligned) break;
                this.needsNewLine = true;
                this.needsAlign = true;
                break;
            }
            case AFTER_COMMENT: {
                this.needsSpace = true;
                break;
            }
        }
    }

    private void prepareComment(CommentInfo.StartConnection start) throws IOException {
        switch (start) {
            case DIRECT_AFTER_PREVIOUS: {
                this.needsSpace = false;
                break;
            }
            case AFTER_PREVIOUS: {
                this.needsSpace = true;
                break;
            }
            case START_OF_LINE: {
                this.needsNewLine = true;
                this.needsAlign = false;
                break;
            }
            case ON_NEXT_LINE: {
                if (this.aligned) break;
                this.needsNewLine = true;
                this.needsAlign = true;
            }
        }
    }

    void align() throws IOException {
        this.onNewLine = false;
        this.aligned = true;
        this.needsAlign = false;
        for (int i = 0; i < this.lmargin; ++i) {
            this.out.write("\t");
        }
    }

    void indent() {
        ++this.lmargin;
    }

    void undent() {
        --this.lmargin;
    }

    void open(int contextPrec, int ownPrec) throws IOException {
        if (ownPrec < contextPrec) {
            this.out.write("(");
        }
    }

    void close(int contextPrec, int ownPrec) throws IOException {
        if (ownPrec < contextPrec) {
            this.out.write(")");
        }
    }

    public void print(Object s) throws IOException {
        boolean align = this.needsAlign;
        if (this.needsNewLine && !this.onNewLine) {
            this.println();
        }
        if (align && !this.aligned) {
            this.align();
        }
        if (this.needsSpace && !this.onNewLine && !this.aligned) {
            this.out.write(32);
        }
        this.needsSpace = false;
        this.out.write(Convert.escapeUnicode((String)s.toString()));
        this.onNewLine = false;
        this.aligned = false;
    }

    public void println() throws IOException {
        this.onNewLine = true;
        this.aligned = false;
        this.needsNewLine = false;
        this.out.write(this.lineSep);
    }

    public void printExpr(JCTree tree, int prec) throws IOException {
        block6 : {
            int prevPrec = this.prec;
            try {
                this.prec = prec;
                if (tree == null) {
                    this.print("/*missing*/");
                    break block6;
                }
                this.consumeComments(tree.pos);
                tree.accept((JCTree.Visitor)this);
                int endPos = this.endPos(tree);
                this.consumeTrailingComments(endPos);
            }
            catch (UncheckedIOException ex) {
                IOException e = new IOException(ex.getMessage());
                e.initCause(ex);
                throw e;
            }
            finally {
                this.prec = prevPrec;
            }
        }
    }

    public void printExpr(JCTree tree) throws IOException {
        this.printExpr(tree, 0);
    }

    public void printStat(JCTree tree) throws IOException {
        if (this.isEmptyStat(tree)) {
            this.printEmptyStat();
        } else {
            this.printExpr(tree, -1);
        }
    }

    public void printEmptyStat() throws IOException {
        this.print(";");
    }

    public boolean isEmptyStat(JCTree tree) {
        if (!(tree instanceof JCTree.JCBlock)) {
            return false;
        }
        JCTree.JCBlock block = (JCTree.JCBlock)tree;
        return -1 == block.pos && block.stats.isEmpty();
    }

    public <T extends JCTree> void printExprs(List<T> trees, String sep) throws IOException {
        if (trees.nonEmpty()) {
            this.printExpr((JCTree)trees.head);
            List l = trees.tail;
            while (l.nonEmpty()) {
                this.print(sep);
                this.printExpr((JCTree)l.head);
                l = l.tail;
            }
        }
    }

    public <T extends JCTree> void printExprs(List<T> trees) throws IOException {
        this.printExprs(trees, ", ");
    }

    public void printStats(List<? extends JCTree> trees) throws IOException {
        List l = trees;
        while (l.nonEmpty()) {
            this.align();
            this.printStat((JCTree)l.head);
            this.println();
            l = l.tail;
        }
    }

    public void printFlags(long flags) throws IOException {
        if ((flags & 4096) != 0) {
            this.print("/*synthetic*/ ");
        }
        this.print(TreeInfo.flagNames((long)flags));
        if ((flags & 4095) != 0) {
            this.print(" ");
        }
        if ((flags & 8192) != 0) {
            this.print("@");
        }
    }

    public void printAnnotations(List<JCTree.JCAnnotation> trees) throws IOException {
        List l = trees;
        while (l.nonEmpty()) {
            this.printStat((JCTree)l.head);
            if (this.inParams) {
                this.print(" ");
            } else {
                this.println();
                this.align();
            }
            l = l.tail;
        }
    }

    public void printDocComment(JCTree tree) throws IOException {
        String dc;
        if (this.docComments != null && (dc = this.docComments.get((Object)tree)) != null) {
            this.print("/**");
            this.println();
            int pos = 0;
            int endpos = PrettyCommentsPrinter.lineEndPos(dc, pos);
            while (pos < dc.length()) {
                this.align();
                this.print(" *");
                if (pos < dc.length() && dc.charAt(pos) > ' ') {
                    this.print(" ");
                }
                this.print(dc.substring(pos, endpos));
                this.println();
                pos = endpos + 1;
                endpos = PrettyCommentsPrinter.lineEndPos(dc, pos);
            }
            this.align();
            this.print(" */");
            this.println();
            this.align();
        }
    }

    static int lineEndPos(String s, int start) {
        int pos = s.indexOf(10, start);
        if (pos < 0) {
            pos = s.length();
        }
        return pos;
    }

    public void printTypeParameters(List<JCTree.JCTypeParameter> trees) throws IOException {
        if (trees.nonEmpty()) {
            this.print("<");
            this.printExprs(trees);
            this.print(">");
        }
    }

    public void printBlock(List<? extends JCTree> stats, JCTree container) throws IOException {
        this.print("{");
        this.println();
        this.indent();
        this.printStats(stats);
        this.consumeComments(this.endPos(container));
        this.undent();
        this.align();
        this.print("}");
    }

    public void printEnumBody(List<JCTree> stats) throws IOException {
        this.print("{");
        this.println();
        this.indent();
        boolean first = true;
        List l = stats;
        while (l.nonEmpty()) {
            if (this.isEnumerator((JCTree)l.head)) {
                if (!first) {
                    this.print(",");
                    this.println();
                }
                this.align();
                this.printStat((JCTree)l.head);
                first = false;
            }
            l = l.tail;
        }
        this.print(";");
        this.println();
        l = stats;
        while (l.nonEmpty()) {
            if (!this.isEnumerator((JCTree)l.head)) {
                this.align();
                this.printStat((JCTree)l.head);
                this.println();
            }
            l = l.tail;
        }
        this.undent();
        this.align();
        this.print("}");
    }

    public void printEnumMember(JCTree.JCVariableDecl tree) throws IOException {
        this.printAnnotations(tree.mods.annotations);
        this.print((Object)tree.name);
        if (tree.init instanceof JCTree.JCNewClass) {
            JCTree.JCNewClass constructor = (JCTree.JCNewClass)tree.init;
            if (constructor.args != null && constructor.args.nonEmpty()) {
                this.print("(");
                this.printExprs(constructor.args);
                this.print(")");
            }
            if (constructor.def != null && constructor.def.defs != null) {
                this.print(" ");
                this.printBlock(constructor.def.defs, (JCTree)constructor.def);
            }
        }
    }

    boolean isEnumerator(JCTree t) {
        return PrettyCommentsPrinter.getTag(t) == VARDEF && (((JCTree.JCVariableDecl)t).mods.flags & 16384) != 0;
    }

    public void printUnit(JCTree.JCCompilationUnit tree, JCTree.JCClassDecl cdef) throws IOException {
        this.docComments = tree.docComments;
        this.printDocComment((JCTree)tree);
        if (tree.pid != null) {
            this.consumeComments(tree.pos);
            this.print("package ");
            this.printExpr((JCTree)tree.pid);
            this.print(";");
            this.println();
        }
        boolean firstImport = true;
        List l = tree.defs;
        while (l.nonEmpty() && (cdef == null || PrettyCommentsPrinter.getTag((JCTree)l.head) == IMPORT)) {
            if (PrettyCommentsPrinter.getTag((JCTree)l.head) == IMPORT) {
                JCTree.JCImport imp = (JCTree.JCImport)l.head;
                Name name = TreeInfo.name((JCTree)imp.qualid);
                if (name == name.table.fromChars(new char[]{'*'}, 0, 1) || cdef == null || this.isUsed(TreeInfo.symbol((JCTree)imp.qualid), (JCTree)cdef)) {
                    if (firstImport) {
                        firstImport = false;
                        this.println();
                    }
                    this.printStat((JCTree)imp);
                }
            } else {
                this.printStat((JCTree)l.head);
            }
            l = l.tail;
        }
        if (cdef != null) {
            this.printStat((JCTree)cdef);
            this.println();
        }
    }

    boolean isUsed(Symbol t, JCTree cdef) {
        class UsedVisitor
        extends TreeScanner {
            boolean result;
            final /* synthetic */ Symbol val$t;

            UsedVisitor() {
                this.val$t = var2_2;
                this.result = false;
            }

            public void scan(JCTree tree) {
                if (tree != null && !this.result) {
                    tree.accept((JCTree.Visitor)this);
                }
            }

            public void visitIdent(JCTree.JCIdent tree) {
                if (tree.sym == this.val$t) {
                    this.result = true;
                }
            }
        }
        UsedVisitor v = new UsedVisitor(this, t);
        v.scan(cdef);
        return v.result;
    }

    public void visitTopLevel(JCTree.JCCompilationUnit tree) {
        try {
            this.printUnit(tree, null);
            this.consumeComments(Integer.MAX_VALUE);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitImport(JCTree.JCImport tree) {
        try {
            this.print("import ");
            if (tree.staticImport) {
                this.print("static ");
            }
            this.printExpr(tree.qualid);
            this.print(";");
            this.println();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitClassDef(JCTree.JCClassDecl tree) {
        try {
            this.consumeComments(tree.pos);
            this.println();
            this.align();
            this.printDocComment((JCTree)tree);
            this.printAnnotations(tree.mods.annotations);
            this.printFlags(tree.mods.flags & -513);
            Name enclClassNamePrev = this.enclClassName;
            this.enclClassName = tree.name;
            if ((tree.mods.flags & 512) != 0) {
                this.print("interface " + (Object)tree.name);
                this.printTypeParameters(tree.typarams);
                if (tree.implementing.nonEmpty()) {
                    this.print(" extends ");
                    this.printExprs(tree.implementing);
                }
            } else {
                if ((tree.mods.flags & 16384) != 0) {
                    this.print("enum " + (Object)tree.name);
                } else {
                    this.print("class " + (Object)tree.name);
                }
                this.printTypeParameters(tree.typarams);
                if (tree.getExtendsClause() != null) {
                    this.print(" extends ");
                    this.printExpr(tree.getExtendsClause());
                }
                if (tree.implementing.nonEmpty()) {
                    this.print(" implements ");
                    this.printExprs(tree.implementing);
                }
            }
            this.print(" ");
            if ((tree.mods.flags & 512) != 0) {
                this.removeImplicitModifiersForInterfaceMembers(tree.defs);
            }
            if ((tree.mods.flags & 16384) != 0) {
                this.printEnumBody(tree.defs);
            } else {
                this.printBlock(tree.defs, (JCTree)tree);
            }
            this.enclClassName = enclClassNamePrev;
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void removeImplicitModifiersForInterfaceMembers(List<JCTree> defs) {
        for (JCTree def : defs) {
            if (def instanceof JCTree.JCVariableDecl) {
                ((JCTree.JCVariableDecl)def).mods.flags &= -26;
            }
            if (def instanceof JCTree.JCMethodDecl) {
                ((JCTree.JCMethodDecl)def).mods.flags &= -1026;
            }
            if (!(def instanceof JCTree.JCClassDecl)) continue;
            ((JCTree.JCClassDecl)def).mods.flags &= -10;
        }
    }

    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        try {
            boolean isGeneratedConstructor;
            boolean isConstructor;
            boolean bl = isConstructor = tree.name == tree.name.table.fromChars("<init>".toCharArray(), 0, 6);
            if (isConstructor && this.enclClassName == null) {
                return;
            }
            boolean bl2 = isGeneratedConstructor = isConstructor && (tree.mods.flags & 0x1000000000L) != 0;
            if (isGeneratedConstructor) {
                return;
            }
            this.println();
            this.align();
            this.printDocComment((JCTree)tree);
            this.printExpr((JCTree)tree.mods);
            this.printTypeParameters(tree.typarams);
            if (tree.typarams != null && tree.typarams.length() > 0) {
                this.print(" ");
            }
            if (tree.name == tree.name.table.fromChars("<init>".toCharArray(), 0, 6)) {
                this.print((Object)(this.enclClassName != null ? this.enclClassName : tree.name));
            } else {
                this.printExpr((JCTree)tree.restype);
                this.print(" " + (Object)tree.name);
            }
            this.print("(");
            this.inParams = true;
            this.printExprs(tree.params);
            this.inParams = false;
            this.print(")");
            if (tree.thrown.nonEmpty()) {
                this.print(" throws ");
                this.printExprs(tree.thrown);
            }
            if (tree.defaultValue != null) {
                this.print(" default ");
                this.print((Object)tree.defaultValue);
            }
            if (tree.body != null) {
                this.print(" ");
                this.printBlock(tree.body.stats, (JCTree)tree.body);
            } else {
                this.print(";");
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitVarDef(JCTree.JCVariableDecl tree) {
        try {
            if (this.docComments != null && this.docComments.get((Object)tree) != null) {
                this.println();
                this.align();
            }
            this.printDocComment((JCTree)tree);
            if ((tree.mods.flags & 16384) != 0) {
                this.printEnumMember(tree);
            } else {
                this.printExpr((JCTree)tree.mods);
                if ((tree.mods.flags & 0x400000000L) != 0) {
                    this.printExpr((JCTree)((JCTree.JCArrayTypeTree)tree.vartype).elemtype);
                    this.print("... " + (Object)tree.name);
                } else {
                    this.printExpr((JCTree)tree.vartype);
                    this.print(" " + (Object)tree.name);
                }
                if (tree.init != null) {
                    this.print(" = ");
                    this.printExpr((JCTree)tree.init);
                }
                if (this.prec == -1) {
                    this.print(";");
                }
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitSkip(JCTree.JCSkip tree) {
        try {
            this.print(";");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitBlock(JCTree.JCBlock tree) {
        try {
            this.consumeComments(tree.pos);
            this.printFlags(tree.flags);
            this.printBlock(tree.stats, (JCTree)tree);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitDoLoop(JCTree.JCDoWhileLoop tree) {
        try {
            this.print("do ");
            this.printStat((JCTree)tree.body);
            this.align();
            this.print(" while ");
            if (PrettyCommentsPrinter.getTag((JCTree)tree.cond) == PARENS) {
                this.printExpr((JCTree)tree.cond);
            } else {
                this.print("(");
                this.printExpr((JCTree)tree.cond);
                this.print(")");
            }
            this.print(";");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitWhileLoop(JCTree.JCWhileLoop tree) {
        try {
            this.print("while ");
            if (PrettyCommentsPrinter.getTag((JCTree)tree.cond) == PARENS) {
                this.printExpr((JCTree)tree.cond);
            } else {
                this.print("(");
                this.printExpr((JCTree)tree.cond);
                this.print(")");
            }
            this.print(" ");
            this.printStat((JCTree)tree.body);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitForLoop(JCTree.JCForLoop tree) {
        try {
            this.print("for (");
            if (tree.init.nonEmpty()) {
                if (PrettyCommentsPrinter.getTag((JCTree)tree.init.head) == VARDEF) {
                    this.printExpr((JCTree)tree.init.head);
                    List l = tree.init.tail;
                    while (l.nonEmpty()) {
                        JCTree.JCVariableDecl vdef = (JCTree.JCVariableDecl)l.head;
                        this.print(", " + (Object)vdef.name + " = ");
                        this.printExpr((JCTree)vdef.init);
                        l = l.tail;
                    }
                } else {
                    this.printExprs(tree.init);
                }
            }
            this.print("; ");
            if (tree.cond != null) {
                this.printExpr((JCTree)tree.cond);
            }
            this.print("; ");
            this.printExprs(tree.step);
            this.print(") ");
            this.printStat((JCTree)tree.body);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitForeachLoop(JCTree.JCEnhancedForLoop tree) {
        try {
            this.print("for (");
            this.printExpr((JCTree)tree.var);
            this.print(" : ");
            this.printExpr((JCTree)tree.expr);
            this.print(") ");
            this.printStat((JCTree)tree.body);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitLabelled(JCTree.JCLabeledStatement tree) {
        try {
            this.print((Object)tree.label + ": ");
            this.printStat((JCTree)tree.body);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitSwitch(JCTree.JCSwitch tree) {
        try {
            this.print("switch ");
            if (PrettyCommentsPrinter.getTag((JCTree)tree.selector) == PARENS) {
                this.printExpr((JCTree)tree.selector);
            } else {
                this.print("(");
                this.printExpr((JCTree)tree.selector);
                this.print(")");
            }
            this.print(" {");
            this.println();
            this.printStats(tree.cases);
            this.align();
            this.print("}");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitCase(JCTree.JCCase tree) {
        try {
            if (tree.pat == null) {
                this.print("default");
            } else {
                this.print("case ");
                this.printExpr((JCTree)tree.pat);
            }
            this.print(": ");
            this.println();
            this.indent();
            this.printStats(tree.stats);
            this.undent();
            this.align();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitSynchronized(JCTree.JCSynchronized tree) {
        try {
            this.print("synchronized ");
            if (PrettyCommentsPrinter.getTag((JCTree)tree.lock) == PARENS) {
                this.printExpr((JCTree)tree.lock);
            } else {
                this.print("(");
                this.printExpr((JCTree)tree.lock);
                this.print(")");
            }
            this.print(" ");
            this.printStat((JCTree)tree.body);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTry(JCTree.JCTry tree) {
        try {
            this.print("try ");
            this.printStat((JCTree)tree.body);
            List l = tree.catchers;
            while (l.nonEmpty()) {
                this.printStat((JCTree)l.head);
                l = l.tail;
            }
            if (tree.finalizer != null) {
                this.print(" finally ");
                this.printStat((JCTree)tree.finalizer);
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitCatch(JCTree.JCCatch tree) {
        try {
            this.print(" catch (");
            this.printExpr((JCTree)tree.param);
            this.print(") ");
            this.printStat((JCTree)tree.body);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitConditional(JCTree.JCConditional tree) {
        try {
            this.open(this.prec, 3);
            this.printExpr((JCTree)tree.cond, 3);
            this.print(" ? ");
            this.printExpr((JCTree)tree.truepart, 3);
            this.print(" : ");
            this.printExpr((JCTree)tree.falsepart, 3);
            this.close(this.prec, 3);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitIf(JCTree.JCIf tree) {
        try {
            this.print("if ");
            if (PrettyCommentsPrinter.getTag((JCTree)tree.cond) == PARENS) {
                this.printExpr((JCTree)tree.cond);
            } else {
                this.print("(");
                this.printExpr((JCTree)tree.cond);
                this.print(")");
            }
            this.print(" ");
            this.printStat((JCTree)tree.thenpart);
            if (tree.elsepart != null) {
                this.print(" else ");
                this.printStat((JCTree)tree.elsepart);
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isNoArgsSuperCall(JCTree.JCExpression expr) {
        if (!(expr instanceof JCTree.JCMethodInvocation)) {
            return false;
        }
        JCTree.JCMethodInvocation tree = (JCTree.JCMethodInvocation)expr;
        if (!tree.typeargs.isEmpty() || !tree.args.isEmpty()) {
            return false;
        }
        if (!(tree.meth instanceof JCTree.JCIdent)) {
            return false;
        }
        return ((JCTree.JCIdent)tree.meth).name.toString().equals("super");
    }

    public void visitExec(JCTree.JCExpressionStatement tree) {
        if (this.isNoArgsSuperCall(tree.expr)) {
            return;
        }
        try {
            this.printExpr((JCTree)tree.expr);
            if (this.prec == -1) {
                this.print(";");
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitBreak(JCTree.JCBreak tree) {
        try {
            this.print("break");
            if (tree.label != null) {
                this.print(" " + (Object)tree.label);
            }
            this.print(";");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitContinue(JCTree.JCContinue tree) {
        try {
            this.print("continue");
            if (tree.label != null) {
                this.print(" " + (Object)tree.label);
            }
            this.print(";");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitReturn(JCTree.JCReturn tree) {
        try {
            this.print("return");
            if (tree.expr != null) {
                this.print(" ");
                this.printExpr((JCTree)tree.expr);
            }
            this.print(";");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitThrow(JCTree.JCThrow tree) {
        try {
            this.print("throw ");
            this.printExpr((JCTree)tree.expr);
            this.print(";");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitAssert(JCTree.JCAssert tree) {
        try {
            this.print("assert ");
            this.printExpr((JCTree)tree.cond);
            if (tree.detail != null) {
                this.print(" : ");
                this.printExpr((JCTree)tree.detail);
            }
            this.print(";");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitApply(JCTree.JCMethodInvocation tree) {
        try {
            if (!tree.typeargs.isEmpty()) {
                if (PrettyCommentsPrinter.getTag((JCTree)tree.meth) == SELECT) {
                    JCTree.JCFieldAccess left = (JCTree.JCFieldAccess)tree.meth;
                    this.printExpr((JCTree)left.selected);
                    this.print(".<");
                    this.printExprs(tree.typeargs);
                    this.print(">" + (Object)left.name);
                } else {
                    this.print("<");
                    this.printExprs(tree.typeargs);
                    this.print(">");
                    this.printExpr((JCTree)tree.meth);
                }
            } else {
                this.printExpr((JCTree)tree.meth);
            }
            this.print("(");
            this.printExprs(tree.args);
            this.print(")");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitNewClass(JCTree.JCNewClass tree) {
        try {
            if (tree.encl != null) {
                this.printExpr((JCTree)tree.encl);
                this.print(".");
            }
            this.print("new ");
            if (!tree.typeargs.isEmpty()) {
                this.print("<");
                this.printExprs(tree.typeargs);
                this.print(">");
            }
            this.printExpr((JCTree)tree.clazz);
            this.print("(");
            this.printExprs(tree.args);
            this.print(")");
            if (tree.def != null) {
                Name enclClassNamePrev = this.enclClassName;
                Name name = tree.def.name != null ? tree.def.name : (this.enclClassName = tree.type != null && tree.type.tsym.name != tree.type.tsym.name.table.fromChars(new char[0], 0, 0) ? tree.type.tsym.name : null);
                if ((tree.def.mods.flags & 16384) != 0) {
                    this.print("/*enum*/");
                }
                this.printBlock(tree.def.defs, (JCTree)tree.def);
                this.enclClassName = enclClassNamePrev;
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitNewArray(JCTree.JCNewArray tree) {
        try {
            if (tree.elemtype != null) {
                this.print("new ");
                JCTree.JCExpression elem = tree.elemtype;
                if (elem instanceof JCTree.JCArrayTypeTree) {
                    this.printBaseElementType((JCTree.JCArrayTypeTree)elem);
                } else {
                    this.printExpr((JCTree)elem);
                }
                List l = tree.dims;
                while (l.nonEmpty()) {
                    this.print("[");
                    this.printExpr((JCTree)l.head);
                    this.print("]");
                    l = l.tail;
                }
                if (elem instanceof JCTree.JCArrayTypeTree) {
                    this.printBrackets((JCTree.JCArrayTypeTree)elem);
                }
            }
            if (tree.elems != null) {
                if (tree.elemtype != null) {
                    this.print("[]");
                }
                this.print("{");
                this.printExprs(tree.elems);
                this.print("}");
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitParens(JCTree.JCParens tree) {
        try {
            this.print("(");
            this.printExpr((JCTree)tree.expr);
            this.print(")");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitAssign(JCTree.JCAssign tree) {
        try {
            this.open(this.prec, 1);
            this.printExpr((JCTree)tree.lhs, 2);
            this.print(" = ");
            this.printExpr((JCTree)tree.rhs, 1);
            this.close(this.prec, 1);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String operatorName(int tag) {
        String result = OPERATORS.get(tag);
        if (result == null) {
            throw new Error();
        }
        return result;
    }

    public void visitAssignop(JCTree.JCAssignOp tree) {
        try {
            this.open(this.prec, 2);
            this.printExpr((JCTree)tree.lhs, 3);
            this.print(" " + this.operatorName(PrettyCommentsPrinter.getTag((JCTree)tree) - 17) + "= ");
            this.printExpr((JCTree)tree.rhs, 2);
            this.close(this.prec, 2);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitUnary(JCTree.JCUnary tree) {
        try {
            int ownprec = TreeInfo.opPrec((int)PrettyCommentsPrinter.getTag((JCTree)tree));
            String opname = this.operatorName(PrettyCommentsPrinter.getTag((JCTree)tree));
            this.open(this.prec, ownprec);
            if (PrettyCommentsPrinter.getTag((JCTree)tree) <= 51) {
                this.print(opname);
                this.printExpr((JCTree)tree.arg, ownprec);
            } else {
                this.printExpr((JCTree)tree.arg, ownprec);
                this.print(opname);
            }
            this.close(this.prec, ownprec);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitBinary(JCTree.JCBinary tree) {
        try {
            int ownprec = TreeInfo.opPrec((int)PrettyCommentsPrinter.getTag((JCTree)tree));
            String opname = this.operatorName(PrettyCommentsPrinter.getTag((JCTree)tree));
            this.open(this.prec, ownprec);
            this.printExpr((JCTree)tree.lhs, ownprec);
            this.print(" " + opname + " ");
            this.printExpr((JCTree)tree.rhs, ownprec + 1);
            this.close(this.prec, ownprec);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeCast(JCTree.JCTypeCast tree) {
        try {
            this.open(this.prec, 14);
            this.print("(");
            this.printExpr(tree.clazz);
            this.print(")");
            this.printExpr((JCTree)tree.expr, 14);
            this.close(this.prec, 14);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeTest(JCTree.JCInstanceOf tree) {
        try {
            this.open(this.prec, 10);
            this.printExpr((JCTree)tree.expr, 10);
            this.print(" instanceof ");
            this.printExpr(tree.clazz, 11);
            this.close(this.prec, 10);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitIndexed(JCTree.JCArrayAccess tree) {
        try {
            this.printExpr((JCTree)tree.indexed, 15);
            this.print("[");
            this.printExpr((JCTree)tree.index);
            this.print("]");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitSelect(JCTree.JCFieldAccess tree) {
        try {
            this.printExpr((JCTree)tree.selected, 15);
            this.print("." + (Object)tree.name);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitIdent(JCTree.JCIdent tree) {
        try {
            this.print((Object)tree.name);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitLiteral(JCTree.JCLiteral tree) {
        block11 : {
            try {
                switch (tree.typetag) {
                    case 4: {
                        this.print(tree.value.toString());
                        break block11;
                    }
                    case 5: {
                        this.print(tree.value + "L");
                        break block11;
                    }
                    case 6: {
                        this.print(tree.value + "F");
                        break block11;
                    }
                    case 7: {
                        this.print(tree.value.toString());
                        break block11;
                    }
                    case 2: {
                        this.print("'" + Convert.quote((String)String.valueOf((char)((Number)tree.value).intValue())) + "'");
                        break block11;
                    }
                    case 8: {
                        this.print(((Number)tree.value).intValue() == 1 ? "true" : "false");
                        break block11;
                    }
                    case 17: {
                        this.print("null");
                        break block11;
                    }
                }
                this.print("\"" + Convert.quote((String)tree.value.toString()) + "\"");
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public void visitTypeIdent(JCTree.JCPrimitiveTypeTree tree) {
        block13 : {
            try {
                switch (tree.typetag) {
                    case 1: {
                        this.print("byte");
                        break block13;
                    }
                    case 2: {
                        this.print("char");
                        break block13;
                    }
                    case 3: {
                        this.print("short");
                        break block13;
                    }
                    case 4: {
                        this.print("int");
                        break block13;
                    }
                    case 5: {
                        this.print("long");
                        break block13;
                    }
                    case 6: {
                        this.print("float");
                        break block13;
                    }
                    case 7: {
                        this.print("double");
                        break block13;
                    }
                    case 8: {
                        this.print("boolean");
                        break block13;
                    }
                    case 9: {
                        this.print("void");
                        break block13;
                    }
                }
                this.print("error");
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public void visitTypeArray(JCTree.JCArrayTypeTree tree) {
        try {
            this.printBaseElementType(tree);
            this.printBrackets(tree);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void printBaseElementType(JCTree.JCArrayTypeTree tree) throws IOException {
        JCTree.JCExpression elem = tree.elemtype;
        while (elem instanceof JCTree.JCWildcard) {
            elem = ((JCTree.JCWildcard)elem).inner;
        }
        if (elem instanceof JCTree.JCArrayTypeTree) {
            this.printBaseElementType((JCTree.JCArrayTypeTree)elem);
        } else {
            this.printExpr((JCTree)elem);
        }
    }

    private void printBrackets(JCTree.JCArrayTypeTree tree) throws IOException {
        do {
            JCTree.JCExpression elem = tree.elemtype;
            this.print("[]");
            if (!(elem instanceof JCTree.JCArrayTypeTree)) break;
            tree = (JCTree.JCArrayTypeTree)elem;
        } while (true);
    }

    public void visitTypeApply(JCTree.JCTypeApply tree) {
        try {
            this.printExpr((JCTree)tree.clazz);
            this.print("<");
            this.printExprs(tree.arguments);
            this.print(">");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTypeParameter(JCTree.JCTypeParameter tree) {
        try {
            this.print((Object)tree.name);
            if (tree.bounds.nonEmpty()) {
                this.print(" extends ");
                this.printExprs(tree.bounds, " & ");
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitWildcard(JCTree.JCWildcard tree) {
        try {
            Object kind = tree.getClass().getField("kind").get((Object)tree);
            this.print(kind);
            if (kind != null && kind.getClass().getSimpleName().equals("TypeBoundKind")) {
                kind = kind.getClass().getField("kind").get(kind);
            }
            if (tree.getKind() != Tree.Kind.UNBOUNDED_WILDCARD) {
                this.printExpr(tree.inner);
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void visitTypeBoundKind(JCTree.TypeBoundKind tree) {
        try {
            this.print(String.valueOf((Object)tree.kind));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitErroneous(JCTree.JCErroneous tree) {
        try {
            this.print("(ERROR)");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitLetExpr(JCTree.LetExpr tree) {
        try {
            this.print("(let " + (Object)tree.defs + " in " + (Object)tree.expr + ")");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitModifiers(JCTree.JCModifiers mods) {
        try {
            this.printAnnotations(mods.annotations);
            this.printFlags(mods.flags);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitAnnotation(JCTree.JCAnnotation tree) {
        try {
            this.print("@");
            this.printExpr(tree.annotationType);
            if (tree.args.nonEmpty()) {
                JCTree.JCExpression lhs;
                this.print("(");
                if (tree.args.length() == 1 && tree.args.get(0) instanceof JCTree.JCAssign && (lhs = ((JCTree.JCAssign)tree.args.get((int)0)).lhs) instanceof JCTree.JCIdent && ((JCTree.JCIdent)lhs).name.toString().equals("value")) {
                    tree.args = List.of((Object)((JCTree.JCAssign)tree.args.get((int)0)).rhs);
                }
                this.printExprs(tree.args);
                this.print(")");
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void visitTree(JCTree tree) {
        try {
            if ("JCTypeUnion".equals(tree.getClass().getSimpleName())) {
                this.print(tree.toString());
                return;
            }
            this.print("(UNKNOWN: " + (Object)tree + ")");
            this.println();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static {
        PARENS = Javac.getCtcInt(JCTree.class, "PARENS");
        IMPORT = Javac.getCtcInt(JCTree.class, "IMPORT");
        VARDEF = Javac.getCtcInt(JCTree.class, "VARDEF");
        SELECT = Javac.getCtcInt(JCTree.class, "SELECT");
        Method m = null;
        Field f = null;
        try {
            m = JCTree.class.getDeclaredMethod("getTag", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            try {
                f = JCTree.class.getDeclaredField("tag");
            }
            catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            }
        }
        GET_TAG_METHOD = m;
        TAG_FIELD = f;
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(Javac.getCtcInt(JCTree.class, "POS"), "+");
        map.put(Javac.getCtcInt(JCTree.class, "NEG"), "-");
        map.put(Javac.getCtcInt(JCTree.class, "NOT"), "!");
        map.put(Javac.getCtcInt(JCTree.class, "COMPL"), "~");
        map.put(Javac.getCtcInt(JCTree.class, "PREINC"), "++");
        map.put(Javac.getCtcInt(JCTree.class, "PREDEC"), "--");
        map.put(Javac.getCtcInt(JCTree.class, "POSTINC"), "++");
        map.put(Javac.getCtcInt(JCTree.class, "POSTDEC"), "--");
        map.put(Javac.getCtcInt(JCTree.class, "NULLCHK"), "<*nullchk*>");
        map.put(Javac.getCtcInt(JCTree.class, "OR"), "||");
        map.put(Javac.getCtcInt(JCTree.class, "AND"), "&&");
        map.put(Javac.getCtcInt(JCTree.class, "EQ"), "==");
        map.put(Javac.getCtcInt(JCTree.class, "NE"), "!=");
        map.put(Javac.getCtcInt(JCTree.class, "LT"), "<");
        map.put(Javac.getCtcInt(JCTree.class, "GT"), ">");
        map.put(Javac.getCtcInt(JCTree.class, "LE"), "<=");
        map.put(Javac.getCtcInt(JCTree.class, "GE"), ">=");
        map.put(Javac.getCtcInt(JCTree.class, "BITOR"), "|");
        map.put(Javac.getCtcInt(JCTree.class, "BITXOR"), "^");
        map.put(Javac.getCtcInt(JCTree.class, "BITAND"), "&");
        map.put(Javac.getCtcInt(JCTree.class, "SL"), "<<");
        map.put(Javac.getCtcInt(JCTree.class, "SR"), ">>");
        map.put(Javac.getCtcInt(JCTree.class, "USR"), ">>>");
        map.put(Javac.getCtcInt(JCTree.class, "PLUS"), "+");
        map.put(Javac.getCtcInt(JCTree.class, "MINUS"), "-");
        map.put(Javac.getCtcInt(JCTree.class, "MUL"), "*");
        map.put(Javac.getCtcInt(JCTree.class, "DIV"), "/");
        map.put(Javac.getCtcInt(JCTree.class, "MOD"), "%");
        OPERATORS = map;
    }

    private static class UncheckedIOException
    extends Error {
        static final long serialVersionUID = -4032692679158424751L;

        UncheckedIOException(IOException e) {
            super(e.getMessage(), e);
        }
    }

}

