/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import lombok.Lombok;
import lombok.core.AnnotationValues;
import lombok.core.PrintAST;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;

public class HandlePrintAST
extends JavacAnnotationHandler<PrintAST> {
    @Override
    public void handle(AnnotationValues<PrintAST> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        PrintStream stream = System.out;
        String fileName = annotation.getInstance().outfile();
        if (fileName.length() > 0) {
            try {
                stream = new PrintStream(new File(fileName));
            }
            catch (FileNotFoundException e) {
                Lombok.sneakyThrow(e);
            }
        }
        ((JavacNode)annotationNode.up()).traverse(new JavacASTVisitor.Printer(annotation.getInstance().printContent(), stream));
    }
}

