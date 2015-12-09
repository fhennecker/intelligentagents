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
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Name;
import java.io.PrintStream;
import lombok.javac.JavacNode;

public interface JavacASTVisitor {
    public void visitCompilationUnit(JavacNode var1, JCTree.JCCompilationUnit var2);

    public void endVisitCompilationUnit(JavacNode var1, JCTree.JCCompilationUnit var2);

    public void visitType(JavacNode var1, JCTree.JCClassDecl var2);

    public void visitAnnotationOnType(JCTree.JCClassDecl var1, JavacNode var2, JCTree.JCAnnotation var3);

    public void endVisitType(JavacNode var1, JCTree.JCClassDecl var2);

    public void visitField(JavacNode var1, JCTree.JCVariableDecl var2);

    public void visitAnnotationOnField(JCTree.JCVariableDecl var1, JavacNode var2, JCTree.JCAnnotation var3);

    public void endVisitField(JavacNode var1, JCTree.JCVariableDecl var2);

    public void visitInitializer(JavacNode var1, JCTree.JCBlock var2);

    public void endVisitInitializer(JavacNode var1, JCTree.JCBlock var2);

    public void visitMethod(JavacNode var1, JCTree.JCMethodDecl var2);

    public void visitAnnotationOnMethod(JCTree.JCMethodDecl var1, JavacNode var2, JCTree.JCAnnotation var3);

    public void endVisitMethod(JavacNode var1, JCTree.JCMethodDecl var2);

    public void visitMethodArgument(JavacNode var1, JCTree.JCVariableDecl var2, JCTree.JCMethodDecl var3);

    public void visitAnnotationOnMethodArgument(JCTree.JCVariableDecl var1, JCTree.JCMethodDecl var2, JavacNode var3, JCTree.JCAnnotation var4);

    public void endVisitMethodArgument(JavacNode var1, JCTree.JCVariableDecl var2, JCTree.JCMethodDecl var3);

    public void visitLocal(JavacNode var1, JCTree.JCVariableDecl var2);

    public void visitAnnotationOnLocal(JCTree.JCVariableDecl var1, JavacNode var2, JCTree.JCAnnotation var3);

    public void endVisitLocal(JavacNode var1, JCTree.JCVariableDecl var2);

    public void visitStatement(JavacNode var1, JCTree var2);

    public void endVisitStatement(JavacNode var1, JCTree var2);

    public static class Printer
    implements JavacASTVisitor {
        private final PrintStream out;
        private final boolean printContent;
        private int disablePrinting = 0;
        private int indent = 0;

        public Printer(boolean printContent) {
            this(printContent, System.out);
        }

        public Printer(boolean printContent, PrintStream out) {
            this.printContent = printContent;
            this.out = out;
        }

        private /* varargs */ void forcePrint(String text, Object ... params) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.indent; ++i) {
                sb.append("  ");
            }
            this.out.printf(sb.append(text).append('\n').toString(), params);
            this.out.flush();
        }

        private /* varargs */ void print(String text, Object ... params) {
            if (this.disablePrinting == 0) {
                this.forcePrint(text, params);
            }
        }

        @Override
        public void visitCompilationUnit(JavacNode LombokNode2, JCTree.JCCompilationUnit unit) {
            this.out.println("---------------------------------------------------------");
            this.print("<CU %s>", LombokNode2.getFileName());
            ++this.indent;
        }

        @Override
        public void endVisitCompilationUnit(JavacNode node, JCTree.JCCompilationUnit unit) {
            --this.indent;
            this.print("</CUD>", new Object[0]);
        }

        @Override
        public void visitType(JavacNode node, JCTree.JCClassDecl type) {
            this.print("<TYPE %s>", new Object[]{type.name});
            ++this.indent;
            if (this.printContent) {
                this.print("%s", new Object[]{type});
                ++this.disablePrinting;
            }
        }

        @Override
        public void visitAnnotationOnType(JCTree.JCClassDecl type, JavacNode node, JCTree.JCAnnotation annotation) {
            this.forcePrint("<ANNOTATION: %s />", new Object[]{annotation});
        }

        @Override
        public void endVisitType(JavacNode node, JCTree.JCClassDecl type) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</TYPE %s>", new Object[]{type.name});
        }

        @Override
        public void visitInitializer(JavacNode node, JCTree.JCBlock initializer) {
            Object[] arrobject = new Object[1];
            arrobject[0] = initializer.isStatic() ? "static" : "instance";
            this.print("<%s INITIALIZER>", arrobject);
            ++this.indent;
            if (this.printContent) {
                this.print("%s", new Object[]{initializer});
                ++this.disablePrinting;
            }
        }

        @Override
        public void endVisitInitializer(JavacNode node, JCTree.JCBlock initializer) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            Object[] arrobject = new Object[1];
            arrobject[0] = initializer.isStatic() ? "static" : "instance";
            this.print("</%s INITIALIZER>", arrobject);
        }

        @Override
        public void visitField(JavacNode node, JCTree.JCVariableDecl field) {
            this.print("<FIELD %s %s>", new Object[]{field.vartype, field.name});
            ++this.indent;
            if (this.printContent) {
                if (field.init != null) {
                    this.print("%s", new Object[]{field.init});
                }
                ++this.disablePrinting;
            }
        }

        @Override
        public void visitAnnotationOnField(JCTree.JCVariableDecl field, JavacNode node, JCTree.JCAnnotation annotation) {
            this.forcePrint("<ANNOTATION: %s />", new Object[]{annotation});
        }

        @Override
        public void endVisitField(JavacNode node, JCTree.JCVariableDecl field) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</FIELD %s %s>", new Object[]{field.vartype, field.name});
        }

        @Override
        public void visitMethod(JavacNode node, JCTree.JCMethodDecl method) {
            String type = method.name.contentEquals((CharSequence)"<init>") ? ((method.mods.flags & 0x1000000000L) != 0 ? "DEFAULTCONSTRUCTOR" : "CONSTRUCTOR") : "METHOD";
            this.print("<%s %s> returns: %s", new Object[]{type, method.name, method.restype});
            ++this.indent;
            if (this.printContent) {
                if (method.body == null) {
                    this.print("(ABSTRACT)", new Object[0]);
                } else {
                    this.print("%s", new Object[]{method.body});
                }
                ++this.disablePrinting;
            }
        }

        @Override
        public void visitAnnotationOnMethod(JCTree.JCMethodDecl method, JavacNode node, JCTree.JCAnnotation annotation) {
            this.forcePrint("<ANNOTATION: %s />", new Object[]{annotation});
        }

        @Override
        public void endVisitMethod(JavacNode node, JCTree.JCMethodDecl method) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</%s %s>", new Object[]{"XMETHOD", method.name});
        }

        @Override
        public void visitMethodArgument(JavacNode node, JCTree.JCVariableDecl arg, JCTree.JCMethodDecl method) {
            this.print("<METHODARG %s %s>", new Object[]{arg.vartype, arg.name});
            ++this.indent;
        }

        @Override
        public void visitAnnotationOnMethodArgument(JCTree.JCVariableDecl arg, JCTree.JCMethodDecl method, JavacNode nodeAnnotation, JCTree.JCAnnotation annotation) {
            this.forcePrint("<ANNOTATION: %s />", new Object[]{annotation});
        }

        @Override
        public void endVisitMethodArgument(JavacNode node, JCTree.JCVariableDecl arg, JCTree.JCMethodDecl method) {
            --this.indent;
            this.print("</METHODARG %s %s>", new Object[]{arg.vartype, arg.name});
        }

        @Override
        public void visitLocal(JavacNode node, JCTree.JCVariableDecl local) {
            this.print("<LOCAL %s %s>", new Object[]{local.vartype, local.name});
            ++this.indent;
        }

        @Override
        public void visitAnnotationOnLocal(JCTree.JCVariableDecl local, JavacNode node, JCTree.JCAnnotation annotation) {
            this.print("<ANNOTATION: %s />", new Object[]{annotation});
        }

        @Override
        public void endVisitLocal(JavacNode node, JCTree.JCVariableDecl local) {
            --this.indent;
            this.print("</LOCAL %s %s>", new Object[]{local.vartype, local.name});
        }

        @Override
        public void visitStatement(JavacNode node, JCTree statement) {
            this.print("<%s>", statement.getClass());
            ++this.indent;
            this.print("%s", new Object[]{statement});
        }

        @Override
        public void endVisitStatement(JavacNode node, JCTree statement) {
            --this.indent;
            this.print("</%s>", statement.getClass());
        }
    }

}

