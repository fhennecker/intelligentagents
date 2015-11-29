/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Scope
 *  com.sun.tools.javac.code.Scope$Entry
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$ClassSymbol
 *  com.sun.tools.javac.code.Symbol$MethodSymbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Symbol$VarSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.Type$MethodType
 *  com.sun.tools.javac.code.Types
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCMethodDecl
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.AutoGenMethodStub;
import lombok.ast.AST;
import lombok.ast.Expression;
import lombok.ast.MethodDecl;
import lombok.ast.New;
import lombok.ast.Statement;
import lombok.core.AnnotationValues;
import lombok.core.util.ErrorMessages;
import lombok.javac.JavacAST;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.ResolutionBased;
import lombok.javac.handlers.Javac;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacType;
import lombok.javac.handlers.ast.JavacTypeEditor;

@ResolutionBased
public class HandleAutoGenMethodStub
extends JavacAnnotationHandler<AutoGenMethodStub> {
    @Override
    public void handle(AnnotationValues<AutoGenMethodStub> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, AutoGenMethodStub.class);
        JavacType type = JavacType.typeOf(annotationNode, (JCTree)source);
        if (type.isInterface() || type.isAnnotation()) {
            annotationNode.addError(ErrorMessages.canBeUsedOnClassAndEnumOnly(AutoGenMethodStub.class));
            return;
        }
        AutoGenMethodStub autoGenMethodStub = annotation.getInstance();
        Statement statement = autoGenMethodStub.throwException() ? AST.Throw(AST.New(AST.Type(UnsupportedOperationException.class)).withArgument(AST.String("This method is not implemented yet."))) : AST.ReturnDefault();
        for (Symbol.MethodSymbol methodSymbol : UndefiniedMethods.of(type.node())) {
            type.editor().injectMethod((MethodDecl)AST.MethodDecl((Object)methodSymbol).implementing().withStatement(statement));
        }
        type.editor().rebuild();
    }

    private static class UndefiniedMethods
    implements Iterator<Symbol.MethodSymbol>,
    Iterable<Symbol.MethodSymbol> {
        private final Set<String> handledMethods = new HashSet<String>();
        private final JavacNode typeNode;
        private final Symbol.ClassSymbol classSymbol;
        private final Types types;
        private boolean hasNext;
        private boolean nextDefined;
        private Symbol.MethodSymbol next;

        @Override
        public Iterator<Symbol.MethodSymbol> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            if (!this.nextDefined) {
                this.hasNext = this.getNext();
                this.nextDefined = true;
            }
            return this.hasNext;
        }

        @Override
        public Symbol.MethodSymbol next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.nextDefined = false;
            return this.next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private boolean getNext() {
            Symbol.MethodSymbol firstUndefinedMethod = this.getFirstUndefinedMethod(this.classSymbol);
            if (firstUndefinedMethod != null) {
                this.next = this.createMethodStubFor(firstUndefinedMethod);
                this.handledMethods.add(firstUndefinedMethod.toString());
                return true;
            }
            return false;
        }

        public static UndefiniedMethods of(JavacNode node) {
            JavacNode typeNode = Javac.typeNodeOf(node);
            return new UndefiniedMethods(typeNode, ((JCTree.JCClassDecl)typeNode.get()).sym, Types.instance((Context)((JavacAST)typeNode.getAst()).getContext()));
        }

        private Symbol.MethodSymbol createMethodStubFor(Symbol.MethodSymbol methodSym) {
            Type.MethodType type = (Type.MethodType)methodSym.type;
            Name name = methodSym.name;
            Symbol.MethodSymbol methodStubSym = new Symbol.MethodSymbol(methodSym.flags() & -1025, name, this.types.memberType(this.classSymbol.type, (Symbol)methodSym), (Symbol)this.classSymbol);
            ListBuffer paramSyms = new ListBuffer();
            int i = 1;
            if (type.argtypes != null) {
                for (Type argType : type.argtypes) {
                    paramSyms.append((Object)new Symbol.VarSymbol(0x200000000L, this.typeNode.toName("arg" + i++), argType, (Symbol)methodStubSym));
                }
            }
            methodStubSym.params = paramSyms.toList();
            return methodStubSym;
        }

        private Symbol.MethodSymbol getFirstUndefinedMethod(Symbol.ClassSymbol c) {
            Symbol.MethodSymbol undef = null;
            if (c == this.classSymbol || (c.flags() & 1536) != 0) {
                Scope s = c.members();
                Scope.Entry e = s.elems;
                while (undef == null && e != null) {
                    Symbol.MethodSymbol absmeth;
                    Symbol.MethodSymbol implmeth;
                    if (!(e.sym.kind != 16 || (e.sym.flags() & 2098176) != 1024 || (implmeth = (absmeth = (Symbol.MethodSymbol)e.sym).implementation((Symbol.TypeSymbol)this.classSymbol, this.types, true)) != null && implmeth != absmeth || this.handledMethods.contains(absmeth.toString()))) {
                        undef = absmeth;
                    }
                    e = e.sibling;
                }
                if (undef == null) {
                    Type st = this.types.supertype(c.type);
                    if (st.tag == 10) {
                        undef = this.getFirstUndefinedMethod((Symbol.ClassSymbol)st.tsym);
                    }
                }
                List l = this.types.interfaces(c.type);
                while (undef == null && l.nonEmpty()) {
                    undef = this.getFirstUndefinedMethod((Symbol.ClassSymbol)((Type)l.head).tsym);
                    l = l.tail;
                }
            }
            return undef;
        }

        private UndefiniedMethods(JavacNode typeNode, Symbol.ClassSymbol classSymbol, Types types) {
            this.typeNode = typeNode;
            this.classSymbol = classSymbol;
            this.types = types;
        }
    }

}

