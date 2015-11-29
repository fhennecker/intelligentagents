/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import lombok.ast.AST;
import lombok.ast.Argument;
import lombok.ast.Call;
import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.MethodDecl;
import lombok.ast.Statement;
import lombok.ast.TypeRef;

public final class EntrypointHandler<TYPE_TYPE extends IType<METHOD_TYPE, ?, ?, ?, ?, ?>, METHOD_TYPE extends IMethod<TYPE_TYPE, ?, ?, ?>> {
    public boolean entrypointExists(String methodName, TYPE_TYPE type) {
        for (IMethod method : type.methods()) {
            if (!method.isStatic() || !method.returns("void") || !method.name().equals(methodName)) continue;
            return true;
        }
        return false;
    }

    public void createEntrypoint(TYPE_TYPE type, String name, String methodName, Parameters params, Arguments args) {
        if (this.entrypointExists(name, type)) {
            return;
        }
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("void"), name).makePublic()).makeStatic()).withArguments(params.get(name))).withThrownException(AST.Type("java.lang.Throwable"))).withStatement(AST.Call(AST.New(AST.Type(type.name())), methodName).withArguments(args.get(name))));
    }

    public static enum Parameters {
        APPLICATION{

            @Override
            public List<Argument> get(String name) {
                ArrayList<Argument> params = new ArrayList<Argument>();
                params.add(AST.Arg(AST.Type(String.class).withDimensions(1), "args"));
                return params;
            }
        }
        ,
        JVM_AGENT{

            @Override
            public List<Argument> get(String name) {
                ArrayList<Argument> params = new ArrayList<Argument>();
                params.add(AST.Arg(AST.Type(String.class), "params"));
                params.add(AST.Arg(AST.Type(Instrumentation.class), "instrumentation"));
                return params;
            }
        };
        

        private Parameters() {
        }

        public abstract List<Argument> get(String var1);

    }

    public static enum Arguments {
        APPLICATION{

            @Override
            public List<Expression<?>> get(String name) {
                ArrayList args = new ArrayList();
                args.add(AST.Name("args"));
                return args;
            }
        }
        ,
        JVM_AGENT{

            @Override
            public List<Expression<?>> get(String name) {
                ArrayList args = new ArrayList();
                args.add("agentmain".equals(name) ? AST.True() : AST.False());
                args.add(AST.Name("params"));
                args.add(AST.Name("instrumentation"));
                return args;
            }
        };
        

        private Arguments() {
        }

        public abstract List<Expression<?>> get(String var1);

    }

}

