/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.parser.EndPosParser
 *  com.sun.tools.javac.parser.Lexer
 *  com.sun.tools.javac.parser.Parser
 *  com.sun.tools.javac.parser.Parser$Factory
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.java6;

import com.sun.tools.javac.parser.EndPosParser;
import com.sun.tools.javac.parser.Lexer;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.Map;
import lombok.javac.CommentInfo;
import lombok.javac.java6.CommentCollectingScanner;

class CommentCollectingParser
extends EndPosParser {
    private final Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap;
    private final Lexer lexer;

    protected CommentCollectingParser(Parser.Factory fac, Lexer S, boolean keepDocComments, Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap) {
        super(fac, S, keepDocComments);
        this.lexer = S;
        this.commentsMap = commentsMap;
    }

    public JCTree.JCCompilationUnit compilationUnit() {
        JCTree.JCCompilationUnit result = super.compilationUnit();
        if (this.lexer instanceof CommentCollectingScanner) {
            List<CommentInfo> comments = ((CommentCollectingScanner)this.lexer).getComments();
            this.commentsMap.put(result, comments);
        }
        return result;
    }
}

