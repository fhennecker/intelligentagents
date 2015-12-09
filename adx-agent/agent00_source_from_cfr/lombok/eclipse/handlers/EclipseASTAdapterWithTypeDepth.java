/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 */
package lombok.eclipse.handlers;

import java.beans.ConstructorProperties;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseNode;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

public class EclipseASTAdapterWithTypeDepth
extends EclipseASTAdapter {
    private final int maxTypeDepth;
    private int typeDepth;

    @Override
    public void visitType(EclipseNode typeNode, TypeDeclaration type) {
        ++this.typeDepth;
    }

    @Override
    public void endVisitType(EclipseNode typeNode, TypeDeclaration type) {
        --this.typeDepth;
    }

    public boolean isOfInterest() {
        return this.typeDepth <= this.maxTypeDepth;
    }

    @ConstructorProperties(value={"maxTypeDepth"})
    public EclipseASTAdapterWithTypeDepth(int maxTypeDepth) {
        this.maxTypeDepth = maxTypeDepth;
    }
}

