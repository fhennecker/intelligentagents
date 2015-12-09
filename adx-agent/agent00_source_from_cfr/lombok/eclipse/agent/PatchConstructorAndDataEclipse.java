/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ISourceElementRequestor
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.parser.Parser
 *  org.eclipse.jdt.internal.compiler.parser.Scanner
 *  org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor
 *  org.eclipse.jdt.internal.core.JavaElement
 *  org.eclipse.jdt.internal.core.SourceFieldElementInfo
 */
package lombok.eclipse.agent;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Stack;
import org.eclipse.jdt.internal.compiler.ISourceElementRequestor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.SourceFieldElementInfo;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PatchConstructorAndDataEclipse {
    public static void onSourceElementRequestor_exitField(ISourceElementRequestor requestor, int initializationStart, int declarationEnd, int declarationSourceEnd, FieldDeclaration fieldDeclaration, TypeDeclaration typeDeclaration) throws Exception {
        if (requestor instanceof CompilationUnitStructureRequestor) {
            boolean isAnnotatedWithConstructorOrData = false;
            if (typeDeclaration.annotations != null) {
                for (Annotation annotation : typeDeclaration.annotations) {
                    String type;
                    TypeReference annotationType = annotation.type;
                    if (annotationType == null || !(type = new String(annotationType.getLastToken())).equals("Data") && !type.equals("AllArgsConstructor") && !type.equals("NoArgsConstructor") && !type.equals("RequiredArgsConstructor")) continue;
                    isAnnotatedWithConstructorOrData = true;
                    break;
                }
            }
            if (isAnnotatedWithConstructorOrData) {
                if (fieldDeclaration.initialization != null) {
                    initializationStart = fieldDeclaration.initialization.sourceStart;
                }
                if (initializationStart != -1) {
                    CompilationUnitStructureRequestor cusRequestor = (CompilationUnitStructureRequestor)requestor;
                    JavaElement handle = (JavaElement)PatchConstructorAndDataEclipse.getHandleStack(cusRequestor).peek();
                    requestor.exitField(initializationStart, declarationEnd, declarationSourceEnd);
                    SourceFieldElementInfo info = (SourceFieldElementInfo)PatchConstructorAndDataEclipse.getNewElements(cusRequestor).get((Object)handle);
                    int length = declarationEnd - initializationStart;
                    if (length > 0) {
                        char[] initializer = new char[length];
                        System.arraycopy(PatchConstructorAndDataEclipse.getParser((CompilationUnitStructureRequestor)cusRequestor).scanner.source, initializationStart, initializer, 0, length);
                        PatchConstructorAndDataEclipse.setInitializationSource(info, initializer);
                    }
                    return;
                }
            }
        }
        requestor.exitField(initializationStart, declarationEnd, declarationSourceEnd);
    }

    private static Stack<?> getHandleStack(CompilationUnitStructureRequestor requestor) throws Exception {
        return (Stack)handleStackField.get((Object)requestor);
    }

    private static Map<?, ?> getNewElements(CompilationUnitStructureRequestor requestor) throws Exception {
        return (Map)newElementsField.get((Object)requestor);
    }

    private static Parser getParser(CompilationUnitStructureRequestor requestor) throws Exception {
        return (Parser)parserField.get((Object)requestor);
    }

    private static void setInitializationSource(SourceFieldElementInfo info, char[] initializer) throws Exception {
        initializationSourceField.set((Object)info, initializer);
    }

    private static final class Reflection {
        private static final Field handleStackField;
        private static final Field newElementsField;
        private static final Field parserField;
        private static final Field initializationSourceField;

        private Reflection() {
        }

        static {
            Field a = null;
            Field b = null;
            Field c = null;
            Field d = null;
            try {
                a = CompilationUnitStructureRequestor.class.getDeclaredField("handleStack");
                a.setAccessible(true);
                b = CompilationUnitStructureRequestor.class.getDeclaredField("newElements");
                b.setAccessible(true);
                c = CompilationUnitStructureRequestor.class.getDeclaredField("parser");
                c.setAccessible(true);
                d = SourceFieldElementInfo.class.getDeclaredField("initializationSource");
                d.setAccessible(true);
            }
            catch (Throwable t) {
                // empty catch block
            }
            handleStackField = a;
            newElementsField = b;
            parserField = c;
            initializationSourceField = d;
        }
    }

}

