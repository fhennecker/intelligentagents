/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.parser.EndPosParser
 *  com.sun.tools.javac.parser.Lexer
 *  com.sun.tools.javac.parser.ParserFactory
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.java7;

import com.sun.tools.javac.parser.EndPosParser;
import com.sun.tools.javac.parser.Lexer;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import java.util.List;
import java.util.Map;
import lombok.javac.CommentInfo;
import lombok.javac.java7.CommentCollectingScanner;

class CommentCollectingParser
extends EndPosParser {
    private final Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap;
    private final Lexer lexer;

    protected CommentCollectingParser(ParserFactory fac, Lexer S, boolean keepDocComments, boolean keepLineMap, Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap) {
        super(fac, S, keepDocComments, keepLineMap);
        this.lexer = S;
        this.commentsMap = commentsMap;
    }

    public JCTree.JCCompilationUnit parseCompilationUnit() {
        JCTree.JCCompilationUnit result = super.parseCompilationUnit();
        if (this.lexer instanceof CommentCollectingScanner) {
            com.sun.tools.javac.util.List<CommentInfo> comments = ((CommentCollectingScanner)this.lexer).getComments();
            this.commentsMap.put(result, (List<CommentInfo>)comments);
        }
        return result;
    }
}

