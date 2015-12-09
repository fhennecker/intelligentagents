/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.handlers;

import java.beans.ConstructorProperties;
import java.util.List;
import lombok.Singleton;
import lombok.ast.AST;
import lombok.ast.ClassDecl;
import lombok.ast.EnumConstant;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.IMethod;
import lombok.ast.IMethodEditor;
import lombok.ast.IType;
import lombok.ast.ITypeEditor;
import lombok.ast.MethodDecl;
import lombok.ast.Statement;
import lombok.ast.TypeRef;
import lombok.core.DiagnosticsReceiver;
import lombok.core.util.ErrorMessages;

public final class SingletonHandler<TYPE_TYPE extends IType<METHOD_TYPE, ?, ?, ?, ?, ?>, METHOD_TYPE extends IMethod<TYPE_TYPE, ?, ?, ?>> {
    private final TYPE_TYPE type;
    private final DiagnosticsReceiver diagnosticsReceiver;

    public void handle(Singleton.Style style) {
        if (this.type.isAnnotation() || this.type.isInterface() || this.type.isEnum()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnClassOnly(Singleton.class));
            return;
        }
        if (this.type.hasSuperClass()) {
            this.diagnosticsReceiver.addError(ErrorMessages.canBeUsedOnConcreteClassOnly(Singleton.class));
            return;
        }
        if (this.type.hasMultiArgumentConstructor()) {
            this.diagnosticsReceiver.addError(ErrorMessages.requiresDefaultOrNoArgumentConstructor(Singleton.class));
            return;
        }
        if (this.type.hasMethod("getInstance", new TypeRef[0])) {
            return;
        }
        String typeName = this.type.name();
        if (this.type.surroundingType() != null) {
            this.type.editor().makeStatic();
        }
        switch (style) {
            case HOLDER: {
                String holderName = typeName + "Holder";
                this.replaceConstructorVisibility();
                this.type.editor().injectType(AST.ClassDecl(holderName).makePrivate().makeStatic().withField(((FieldDecl)AST.FieldDecl(AST.Type(typeName), "INSTANCE").makePrivate().makeFinal()).makeStatic().withInitialization(AST.New(AST.Type(typeName)))));
                this.type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type(typeName), "getInstance").makePublic()).makeStatic()).withStatement(AST.Return(AST.Name(holderName + ".INSTANCE"))));
                break;
            }
            default: {
                this.type.editor().makeEnum();
                this.replaceConstructorVisibility();
                this.type.editor().injectField(AST.EnumConstant("INSTANCE"));
                this.type.editor().injectMethod((MethodDecl)((MethodDecl)((MethodDecl)AST.MethodDecl(AST.Type(typeName), "getInstance").makePublic()).makeStatic()).withStatement(AST.Return(AST.Name("INSTANCE"))));
            }
        }
        this.type.editor().rebuild();
    }

    private void replaceConstructorVisibility() {
        for (IMethod method : this.type.methods()) {
            if (!method.isConstructor()) continue;
            method.editor().makePackagePrivate();
        }
    }

    @ConstructorProperties(value={"type", "diagnosticsReceiver"})
    public SingletonHandler(TYPE_TYPE type, DiagnosticsReceiver diagnosticsReceiver) {
        this.type = type;
        this.diagnosticsReceiver = diagnosticsReceiver;
    }

}

