/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.Context$Key
 *  com.sun.tools.javac.util.Options
 */
package lombok.javac;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Options;
import java.util.HashSet;
import java.util.Set;

public class LombokOptions
extends Options {
    private boolean deleteLombokAnnotations = true;
    private final Set<JCTree.JCCompilationUnit> changed = new HashSet<JCTree.JCCompilationUnit>();

    public static LombokOptions replaceWithDelombokOptions(Context context) {
        Options options = Options.instance((Context)context);
        context.put(optionsKey, (Object)null);
        LombokOptions result = new LombokOptions(context);
        result.putAll(options);
        return result;
    }

    public boolean isChanged(JCTree.JCCompilationUnit ast) {
        return this.changed.contains((Object)ast);
    }

    public static void markChanged(Context context, JCTree.JCCompilationUnit ast) {
        Options options = (Options)context.get(Options.optionsKey);
        if (options instanceof LombokOptions) {
            ((LombokOptions)options).changed.add(ast);
        }
    }

    public static boolean shouldDeleteLombokAnnotations(Context context) {
        Options options = (Options)context.get(Options.optionsKey);
        return options instanceof LombokOptions && ((LombokOptions)options).deleteLombokAnnotations;
    }

    private LombokOptions(Context context) {
        super(context);
    }
}

