/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 */
package lombok.eclipse.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import lombok.Lombok;
import lombok.core.AnnotationValues;
import lombok.core.PrintAST;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@DeferUntilPostDiet
public class HandlePrintAST
extends EclipseAnnotationHandler<PrintAST> {
    @Override
    public void handle(AnnotationValues<PrintAST> annotation, Annotation ast, EclipseNode annotationNode) {
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
        ((EclipseNode)annotationNode.up()).traverse(new EclipseASTVisitor.Printer(annotation.getInstance().printContent(), stream, annotation.getInstance().printPositions()));
    }
}

