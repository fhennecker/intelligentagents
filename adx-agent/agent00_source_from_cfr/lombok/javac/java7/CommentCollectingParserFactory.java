/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.main.JavaCompiler
 *  com.sun.tools.javac.parser.Lexer
 *  com.sun.tools.javac.parser.Parser
 *  com.sun.tools.javac.parser.ParserFactory
 *  com.sun.tools.javac.parser.Scanner
 *  com.sun.tools.javac.parser.ScannerFactory
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.Context$Key
 */
package lombok.javac.java7;

import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.parser.Lexer;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.parser.Scanner;
import com.sun.tools.javac.parser.ScannerFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import lombok.javac.CommentInfo;
import lombok.javac.java7.CommentCollectingParser;

public class CommentCollectingParserFactory
extends ParserFactory {
    private final Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap;
    private final Context context;

    static Context.Key<ParserFactory> key() {
        return parserFactoryKey;
    }

    protected CommentCollectingParserFactory(Context context, Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap) {
        super(context);
        this.context = context;
        this.commentsMap = commentsMap;
    }

    public Parser newParser(CharSequence input, boolean keepDocComments, boolean keepEndPos, boolean keepLineMap) {
        ScannerFactory scannerFactory = ScannerFactory.instance((Context)this.context);
        Scanner lexer = scannerFactory.newScanner(input, keepDocComments);
        CommentCollectingParser x = new CommentCollectingParser(this, (Lexer)lexer, keepDocComments, keepLineMap, this.commentsMap);
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

