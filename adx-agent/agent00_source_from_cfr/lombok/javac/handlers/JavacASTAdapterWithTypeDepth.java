/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import java.beans.ConstructorProperties;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacNode;

public class JavacASTAdapterWithTypeDepth
extends JavacASTAdapter {
    private final int maxTypeDepth;
    private int typeDepth;

    @Override
    public void visitType(JavacNode typeNode, JCTree.JCClassDecl type) {
        ++this.typeDepth;
    }

    @Override
    public void endVisitType(JavacNode typeNode, JCTree.JCClassDecl type) {
        --this.typeDepth;
    }

    public boolean isOfInterest() {
        return this.typeDepth <= this.maxTypeDepth;
    }

    @ConstructorProperties(value={"maxTypeDepth"})
    public JavacASTAdapterWithTypeDepth(int maxTypeDepth) {
        this.maxTypeDepth = maxTypeDepth;
    }
}

