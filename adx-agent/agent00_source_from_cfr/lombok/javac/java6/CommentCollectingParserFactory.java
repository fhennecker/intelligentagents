/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.main.JavaCompiler
 *  com.sun.tools.javac.parser.Lexer
 *  com.sun.tools.javac.parser.Parser
 *  com.sun.tools.javac.parser.Parser$Factory
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.Context$Key
 *  com.sun.tools.javac.util.List
 */
package lombok.javac.java6;

import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.parser.Lexer;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import java.lang.reflect.Field;
import java.util.Map;
import lombok.javac.CommentInfo;
import lombok.javac.java6.CommentCollectingParser;

public class CommentCollectingParserFactory
extends Parser.Factory {
    private final Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap;

    static Context.Key<Parser.Factory> key() {
        return parserFactoryKey;
    }

    protected CommentCollectingParserFactory(Context context, Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap) {
        super(context);
        this.commentsMap = commentsMap;
    }

    public Parser newParser(Lexer S, boolean keepDocComments, boolean genEndPos) {
        CommentCollectingParser x = new CommentCollectingParser(this, S, keepDocComments, this.commentsMap);
        return (Parser)x;
    }

    public static void setInCompiler(JavaCompiler compiler, Context context, Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap) {
        context.put(CommentCollectingParserFactory.key(), (Object)null);
        try {
            Field field = JavaCompiler.class.getDeclaredField("parserFactory");
            field.setAccessible(true);
            field.set((Object)compiler, (Object)new CommentCollectingParserFactory(context, commentsMap));
        }
        catch (Exception e) {
            throw new IllegalStateException("Could not set comment sensitive parser in the compiler", e);
        }
    }
}

