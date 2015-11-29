/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.AST;
import lombok.ast.Argument;
import lombok.ast.Call;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.Foreach;
import lombok.ast.IMethod;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.If;
import lombok.ast.MethodDecl;
import lombok.ast.Statement;
import lombok.ast.TypeRef;
import lombok.core.util.Names;

public abstract class ListenerSupportHandler<TYPE_TYPE extends IType<? extends IMethod<?, ?, ?, ?>, ?, ?, ?, ?, ?>> {
    public void addListenerField(TYPE_TYPE type, Object interfaze) {
        String interfaceName = Names.interfaceName(this.name(interfaze));
        type.editor().injectField(((FieldDecl)AST.FieldDecl(AST.Type("java.util.List").withTypeArgument(AST.Type(this.type(interfaze))), "$registered" + interfaceName).makePrivate().makeFinal()).withInitialization(AST.New(AST.Type("java.util.concurrent.CopyOnWriteArrayList").withTypeArgument(AST.Type(this.type(interfaze))))));
    }

    public void addAddListenerMethod(TYPE_TYPE type, Object interfaze) {
        String interfaceName = Names.interfaceName(this.name(interfaze));
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("void"), "add" + interfaceName).makePublic()).withArgument(AST.Arg(AST.Type(this.type(interfaze)), "l"))).withStatement(AST.If(AST.Not(AST.Call(AST.Name("$registered" + interfaceName), "contains").withArgument(AST.Name("l")))).Then(AST.Call(AST.Name("$registered" + interfaceName), "add").withArgument(AST.Name("l")))));
    }

    public void addRemoveListenerMethod(TYPE_TYPE type, Object interfaze) {
        String interfaceName = Names.interfaceName(this.name(interfaze));
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("void"), "remove" + interfaceName).makePublic()).withArgument(AST.Arg(AST.Type(this.type(interfaze)), "l"))).withStatement(AST.Call(AST.Name("$registered" + interfaceName), "remove").withArgument(AST.Name("l"))));
    }

    public void addFireListenerMethod(TYPE_TYPE type, Object interfaze, Object method) {
        ArrayList args = new ArrayList();
        ArrayList<Argument> params = new ArrayList<Argument>();
        this.createParamsAndArgs(method, params, args);
        String interfaceName = Names.interfaceName(this.name(interfaze));
        String methodName = this.name(method);
        type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type("void"), Names.camelCase("fire", methodName)).makeProtected()).withArguments(params)).withStatement(AST.Foreach(AST.LocalDecl(AST.Type(this.type(interfaze)), "l")).In(AST.Name("$registered" + interfaceName)).Do(AST.Call(AST.Name("l"), methodName).withArguments(args))));
    }

    protected abstract void createParamsAndArgs(Object var1, List<Argument> var2, List<Expression<?>> var3);

    protected abstract String name(Object var1);

    protected abstract Object type(Object var1);
}

