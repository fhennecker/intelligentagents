/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Argument
 *  org.eclipse.jdt.internal.compiler.ast.Block
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Initializer
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Statement
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 */
package lombok.eclipse;

import java.io.PrintStream;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public interface EclipseASTVisitor {
    public void visitCompilationUnit(EclipseNode var1, CompilationUnitDeclaration var2);

    public void endVisitCompilationUnit(EclipseNode var1, CompilationUnitDeclaration var2);

    public void visitType(EclipseNode var1, TypeDeclaration var2);

    public void visitAnnotationOnType(TypeDeclaration var1, EclipseNode var2, Annotation var3);

    public void endVisitType(EclipseNode var1, TypeDeclaration var2);

    public void visitField(EclipseNode var1, FieldDeclaration var2);

    public void visitAnnotationOnField(FieldDeclaration var1, EclipseNode var2, Annotation var3);

    public void endVisitField(EclipseNode var1, FieldDeclaration var2);

    public void visitInitializer(EclipseNode var1, Initializer var2);

    public void endVisitInitializer(EclipseNode var1, Initializer var2);

    public void visitMethod(EclipseNode var1, AbstractMethodDeclaration var2);

    public void visitAnnotationOnMethod(AbstractMethodDeclaration var1, EclipseNode var2, Annotation var3);

    public void endVisitMethod(EclipseNode var1, AbstractMethodDeclaration var2);

    public void visitMethodArgument(EclipseNode var1, Argument var2, AbstractMethodDeclaration var3);

    public void visitAnnotationOnMethodArgument(Argument var1, AbstractMethodDeclaration var2, EclipseNode var3, Annotation var4);

    public void endVisitMethodArgument(EclipseNode var1, Argument var2, AbstractMethodDeclaration var3);

    public void visitLocal(EclipseNode var1, LocalDeclaration var2);

    public void visitAnnotationOnLocal(LocalDeclaration var1, EclipseNode var2, Annotation var3);

    public void endVisitLocal(EclipseNode var1, LocalDeclaration var2);

    public void visitStatement(EclipseNode var1, Statement var2);

    public void endVisitStatement(EclipseNode var1, Statement var2);

    public static class Printer
    implements EclipseASTVisitor {
        private final PrintStream out;
        private final boolean printContent;
        private int disablePrinting = 0;
        private int indent = 0;
        private boolean printClassNames = false;
        private final boolean printPositions;

        public Printer(boolean printContent) {
            this(printContent, System.out, false);
        }

        public Printer(boolean printContent, PrintStream out, boolean printPositions) {
            this.printContent = printContent;
            this.out = out;
            this.printPositions = printPositions;
        }

        private /* varargs */ void forcePrint(String text, Object ... params) {
            Object[] t;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.indent; ++i) {
                sb.append("  ");
            }
            sb.append(text);
            if (this.printClassNames && params.length > 0) {
                int i2;
                sb.append(" [");
                for (i2 = 0; i2 < params.length; ++i2) {
                    if (i2 > 0) {
                        sb.append(", ");
                    }
                    sb.append("%s");
                }
                sb.append("]");
                t = new Object[params.length + params.length];
                for (i2 = 0; i2 < params.length; ++i2) {
                    t[i2] = params[i2];
                    t[i2 + params.length] = params[i2] == null ? "NULL " : params[i2].getClass();
                }
            } else {
                t = params;
            }
            sb.append("\n");
            this.out.printf(sb.toString(), t);
            this.out.flush();
        }

        private /* varargs */ void print(String text, Object ... params) {
            if (this.disablePrinting == 0) {
                this.forcePrint(text, params);
            }
        }

        private String str(char[] c) {
            if (c == null) {
                return "(NULL)";
            }
            return new String(c);
        }

        private String str(TypeReference type) {
            if (type == null) {
                return "(NULL)";
            }
            char[][] c = type.getTypeName();
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (char[] d : c) {
                sb.append(first ? "" : ".").append(new String(d));
                first = false;
            }
            return sb.toString();
        }

        public void visitCompilationUnit(EclipseNode node, CompilationUnitDeclaration unit) {
            this.out.println("---------------------------------------------------------");
            this.out.println(node.isCompleteParse() ? "COMPLETE" : "incomplete");
            Object[] arrobject = new Object[3];
            arrobject[0] = node.getFileName();
            arrobject[1] = EclipseHandlerUtil.isGenerated((ASTNode)unit) ? " (GENERATED)" : "";
            arrobject[2] = this.position(node);
            this.print("<CUD %s%s%s>", arrobject);
            ++this.indent;
        }

        public void endVisitCompilationUnit(EclipseNode node, CompilationUnitDeclaration unit) {
            --this.indent;
            this.print("</CUD>", new Object[0]);
        }

        public void visitType(EclipseNode node, TypeDeclaration type) {
            Object[] arrobject = new Object[3];
            arrobject[0] = this.str(type.name);
            arrobject[1] = EclipseHandlerUtil.isGenerated((ASTNode)type) ? " (GENERATED)" : "";
            arrobject[2] = this.position(node);
            this.print("<TYPE %s%s%s>", arrobject);
            ++this.indent;
            if (this.printContent) {
                this.print("%s", new Object[]{type});
                ++this.disablePrinting;
            }
        }

        public void visitAnnotationOnType(TypeDeclaration type, EclipseNode node, Annotation annotation) {
            Object[] arrobject = new Object[3];
            arrobject[0] = EclipseHandlerUtil.isGenerated((ASTNode)annotation) ? " (GENERATED)" : "";
            arrobject[1] = annotation;
            arrobject[2] = this.position(node);
            this.forcePrint("<ANNOTATION%s: %s%s />", arrobject);
        }

        public void endVisitType(EclipseNode node, TypeDeclaration type) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</TYPE %s>", this.str(type.name));
        }

        public void visitInitializer(EclipseNode node, Initializer initializer) {
            Block block = initializer.block;
            boolean s = block != null && block.statements != null;
            Object[] arrobject = new Object[4];
            arrobject[0] = (initializer.modifiers & 8) != 0 ? "static" : "instance";
            arrobject[1] = s ? "filled" : "blank";
            arrobject[2] = EclipseHandlerUtil.isGenerated((ASTNode)initializer) ? " (GENERATED)" : "";
            arrobject[3] = this.position(node);
            this.print("<%s INITIALIZER: %s%s%s>", arrobject);
            ++this.indent;
            if (this.printContent) {
                if (initializer.block != null) {
                    this.print("%s", new Object[]{initializer.block});
                }
                ++this.disablePrinting;
            }
        }

        public void endVisitInitializer(EclipseNode node, Initializer initializer) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            Object[] arrobject = new Object[1];
            arrobject[0] = (initializer.modifiers & 8) != 0 ? "static" : "instance";
            this.print("</%s INITIALIZER>", arrobject);
        }

        public void visitField(EclipseNode node, FieldDeclaration field) {
            Object[] arrobject = new Object[5];
            arrobject[0] = EclipseHandlerUtil.isGenerated((ASTNode)field) ? " (GENERATED)" : "";
            arrobject[1] = this.str(field.type);
            arrobject[2] = this.str(field.name);
            arrobject[3] = field.initialization;
            arrobject[4] = this.position(node);
            this.print("<FIELD%s %s %s = %s%s>", arrobject);
            ++this.indent;
            if (this.printContent) {
                if (field.initialization != null) {
                    this.print("%s", new Object[]{field.initialization});
                }
                ++this.disablePrinting;
            }
        }

        public void visitAnnotationOnField(FieldDeclaration field, EclipseNode node, Annotation annotation) {
            Object[] arrobject = new Object[3];
            arrobject[0] = EclipseHandlerUtil.isGenerated((ASTNode)annotation) ? " (GENERATED)" : "";
            arrobject[1] = annotation;
            arrobject[2] = this.position(node);
            this.forcePrint("<ANNOTATION%s: %s%s />", arrobject);
        }

        public void endVisitField(EclipseNode node, FieldDeclaration field) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</FIELD %s %s>", this.str(field.type), this.str(field.name));
        }

        public void visitMethod(EclipseNode node, AbstractMethodDeclaration method) {
            String type = method instanceof ConstructorDeclaration ? "CONSTRUCTOR" : "METHOD";
            Object[] arrobject = new Object[5];
            arrobject[0] = type;
            arrobject[1] = this.str(method.selector);
            arrobject[2] = method.statements != null ? "filled" : "blank";
            arrobject[3] = EclipseHandlerUtil.isGenerated((ASTNode)method) ? " (GENERATED)" : "";
            arrobject[4] = this.position(node);
            this.print("<%s %s: %s%s%s>", arrobject);
            ++this.indent;
            if (this.printContent) {
                if (method.statements != null) {
                    this.print("%s", new Object[]{method});
                }
                ++this.disablePrinting;
            }
        }

        public void visitAnnotationOnMethod(AbstractMethodDeclaration method, EclipseNode node, Annotation annotation) {
            Object[] arrobject = new Object[3];
            arrobject[0] = EclipseHandlerUtil.isGenerated((ASTNode)method) ? " (GENERATED)" : "";
            arrobject[1] = annotation;
            arrobject[2] = this.position(node);
            this.forcePrint("<ANNOTATION%s: %s%s />", arrobject);
        }

        public void endVisitMethod(EclipseNode node, AbstractMethodDeclaration method) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            String type = method instanceof ConstructorDeclaration ? "CONSTRUCTOR" : "METHOD";
            --this.indent;
            this.print("</%s %s>", type, this.str(method.selector));
        }

        public void visitMethodArgument(EclipseNode node, Argument arg, AbstractMethodDeclaration method) {
            Object[] arrobject = new Object[5];
            arrobject[0] = EclipseHandlerUtil.isGenerated((ASTNode)arg) ? " (GENERATED)" : "";
            arrobject[1] = this.str(arg.type);
            arrobject[2] = this.str(arg.name);
            arrobject[3] = arg.initialization;
            arrobject[4] = this.position(node);
            this.print("<METHODARG%s %s %s = %s%s>", arrobject);
            ++this.indent;
        }

        public void visitAnnotationOnMethodArgument(Argument arg, AbstractMethodDeclaration method, EclipseNode node, Annotation annotation) {
            Object[] arrobject = new Object[3];
            arrobject[0] = EclipseHandlerUtil.isGenerated((ASTNode)annotation) ? " (GENERATED)" : "";
            arrobject[1] = annotation;
            arrobject[2] = this.position(node);
            this.print("<ANNOTATION%s: %s%s />", arrobject);
        }

        public void endVisitMethodArgument(EclipseNode node, Argument arg, AbstractMethodDeclaration method) {
            --this.indent;
            this.print("</METHODARG %s %s>", this.str(arg.type), this.str(arg.name));
        }

        public void visitLocal(EclipseNode node, LocalDeclaration local) {
            Object[] arrobject = new Object[5];
            arrobject[0] = EclipseHandlerUtil.isGenerated((ASTNode)local) ? " (GENERATED)" : "";
            arrobject[1] = this.str(local.type);
            arrobject[2] = this.str(local.name);
            arrobject[3] = local.initialization;
            arrobject[4] = this.position(node);
            this.print("<LOCAL%s %s %s = %s%s>", arrobject);
            ++this.indent;
        }

        public void visitAnnotationOnLocal(LocalDeclaration local, EclipseNode node, Annotation annotation) {
            Object[] arrobject = new Object[2];
            arrobject[0] = EclipseHandlerUtil.isGenerated((ASTNode)annotation) ? " (GENERATED)" : "";
            arrobject[1] = annotation;
            this.print("<ANNOTATION%s: %s />", arrobject);
        }

        public void endVisitLocal(EclipseNode node, LocalDeclaration local) {
            --this.indent;
            this.print("</LOCAL %s %s>", this.str(local.type), this.str(local.name));
        }

        public void visitStatement(EclipseNode node, Statement statement) {
            Object[] arrobject = new Object[3];
            arrobject[0] = statement.getClass();
            arrobject[1] = EclipseHandlerUtil.isGenerated((ASTNode)statement) ? " (GENERATED)" : "";
            arrobject[2] = this.position(node);
            this.print("<%s%s%s>", arrobject);
            ++this.indent;
            this.print("%s", new Object[]{statement});
        }

        public void endVisitStatement(EclipseNode node, Statement statement) {
            --this.indent;
            this.print("</%s>", statement.getClass());
        }

        String position(EclipseNode node) {
            if (!this.printPositions) {
                return "";
            }
            int start = ((ASTNode)node.get()).sourceStart();
            int end = ((ASTNode)node.get()).sourceEnd();
            return String.format(" [%d, %d]", start, end);
        }
    }

}

