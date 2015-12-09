/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.Name;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.AbstractTypeVisitor6;

public class FindTypeVarScanner
extends AbstractTypeVisitor6<Void, Void> {
    private Set<String> typeVariables = new HashSet<String>();

    public Set<String> getTypeVariables() {
        return this.typeVariables;
    }

    private Void subVisit(TypeMirror mirror) {
        if (mirror == null) {
            return null;
        }
        return (Void)mirror.accept(this, null);
    }

    @Override
    public Void visitPrimitive(PrimitiveType t, Void p) {
        return null;
    }

    @Override
    public Void visitNull(NullType t, Void p) {
        return null;
    }

    @Override
    public Void visitNoType(NoType t, Void p) {
        return null;
    }

    @Override
    public Void visitUnknown(TypeMirror t, Void p) {
        return null;
    }

    @Override
    public Void visitError(ErrorType t, Void p) {
        return null;
    }

    @Override
    public Void visitArray(ArrayType t, Void p) {
        return this.subVisit(t.getComponentType());
    }

    @Override
    public Void visitDeclared(DeclaredType t, Void p) {
        for (TypeMirror subT : t.getTypeArguments()) {
            this.subVisit(subT);
        }
        return null;
    }

    @Override
    public Void visitTypeVariable(TypeVariable t, Void p) {
        Name name = null;
        try {
            name = ((Type)t).tsym.name;
        }
        catch (NullPointerException e) {
            // empty catch block
        }
        if (name != null) {
            this.typeVariables.add(name.toString());
        }
        this.subVisit(t.getLowerBound());
        this.subVisit(t.getUpperBound());
        return null;
    }

    @Override
    public Void visitWildcard(WildcardType t, Void p) {
        this.subVisit(t.getSuperBound());
        this.subVisit(t.getExtendsBound());
        return null;
    }

    @Override
    public Void visitExecutable(ExecutableType t, Void p) {
        this.subVisit(t.getReturnType());
        for (TypeMirror subT22 : t.getParameterTypes()) {
            this.subVisit(subT22);
        }
        for (TypeMirror subT : t.getThrownTypes()) {
            this.subVisit(subT);
        }
        for (TypeVariable subT2 : t.getTypeVariables()) {
            this.subVisit(subT2);
        }
        return null;
    }
}

