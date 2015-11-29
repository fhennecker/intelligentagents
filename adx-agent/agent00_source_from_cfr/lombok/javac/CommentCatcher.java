/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.main.JavaCompiler
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.List
 */
package lombok.javac;

import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.javac.CommentInfo;

public class CommentCatcher {
    private final JavaCompiler compiler;
    private final Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap;

    public static CommentCatcher create(Context context) {
        CommentCatcher.registerCommentsCollectingScannerFactory(context);
        JavaCompiler compiler = new JavaCompiler(context);
        WeakHashMap<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap = new WeakHashMap<JCTree.JCCompilationUnit, List<CommentInfo>>();
        CommentCatcher.setInCompiler(compiler, context, commentsMap);
        compiler.keepComments = true;
        compiler.genEndPos = true;
        return new CommentCatcher(compiler, commentsMap);
    }

    private CommentCatcher(JavaCompiler compiler, Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap) {
        this.compiler = compiler;
        this.commentsMap = commentsMap;
    }

    public JavaCompiler getCompiler() {
        return this.compiler;
    }

    public List<CommentInfo> getComments(JCTree.JCCompilationUnit ast) {
        List<CommentInfo> list = this.commentsMap.get((Object)ast);
        return list == null ? List.nil() : list;
    }

    private static void registerCommentsCollectingScannerFactory(Context context) {
        try {
            if (JavaCompiler.version().startsWith("1.6")) {
                Class.forName("lombok.javac.java6.CommentCollectingScannerFactory").getMethod("preRegister", Context.class).invoke(null, new Object[]{context});
            } else {
                Class.forName("lombok.javac.java7.CommentCollectingScannerFactory").getMethod("preRegister", Context.class).invoke(null, new Object[]{context});
            }
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }

    private static void setInCompiler(JavaCompiler compiler, Context context, Map<JCTree.JCCompilationUnit, List<CommentInfo>> commentsMap) {
        try {
            if (JavaCompiler.version().startsWith("1.6")) {
                Class parserFactory = Class.forName("lombok.javac.java6.CommentCollectingParserFactory");
                parserFactory.getMethod("setInCompiler", JavaCompiler.class, Context.class, Map.class).invoke(null, new Object[]{compiler, context, commentsMap});
            } else {
                Class parserFactory = Class.forName("lombok.javac.java7.CommentCollectingParserFactory");
                parserFactory.getMethod("setInCompiler", JavaCompiler.class, Context.class, Map.class).invoke(null, new Object[]{compiler, context, commentsMap});
            }
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
}

