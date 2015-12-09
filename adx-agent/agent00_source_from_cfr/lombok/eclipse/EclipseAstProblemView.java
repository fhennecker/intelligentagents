/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.core.compiler.CategorizedProblem
 *  org.eclipse.jdt.internal.compiler.CompilationResult
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 *  org.eclipse.jdt.internal.compiler.impl.ReferenceContext
 *  org.eclipse.jdt.internal.compiler.problem.DefaultProblem
 *  org.eclipse.jdt.internal.compiler.util.Util
 */
package lombok.eclipse;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.util.Util;

public class EclipseAstProblemView {
    public static void addProblemToCompilationResult(CompilationUnitDeclaration ast, boolean isWarning, String message, int sourceStart, int sourceEnd) {
        int n;
        if (ast.compilationResult == null) {
            return;
        }
        char[] fileNameArray = ast.getFileName();
        if (fileNameArray == null) {
            fileNameArray = "(unknown).java".toCharArray();
        }
        int lineNumber = 0;
        int columnNumber = 1;
        CompilationResult result = ast.compilationResult;
        Object lineEnds = null;
        if (sourceStart >= 0) {
            int[] arrn = result.getLineSeparatorPositions();
            lineEnds = arrn;
            n = Util.getLineNumber((int)sourceStart, (int[])arrn, (int)0, (int)(lineEnds.length - 1));
        } else {
            n = 0;
        }
        lineNumber = n;
        columnNumber = sourceStart >= 0 ? Util.searchColumnNumber((int[])result.getLineSeparatorPositions(), (int)lineNumber, (int)sourceStart) : 0;
        LombokProblem ecProblem = new LombokProblem(fileNameArray, message, 0, new String[0], isWarning ? 0 : 1, sourceStart, sourceEnd, lineNumber, columnNumber);
        ast.compilationResult.record((CategorizedProblem)ecProblem, null);
    }

    private static class LombokProblem
    extends DefaultProblem {
        private static final String MARKER_ID = "org.eclipse.jdt.apt.pluggable.core.compileProblem";

        public LombokProblem(char[] originatingFileName, String message, int id, String[] stringArguments, int severity, int startPosition, int endPosition, int line, int column) {
            super(originatingFileName, message, id, stringArguments, severity, startPosition, endPosition, line, column);
        }

        public int getCategoryID() {
            return 0;
        }

        public String getMarkerType() {
            return "org.eclipse.jdt.apt.pluggable.core.compileProblem";
        }
    }

}

