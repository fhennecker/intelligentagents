/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.Binding
 *  org.eclipse.jdt.internal.compiler.lookup.BlockScope
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 */
package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.ListenerSupport;
import lombok.ast.AST;
import lombok.ast.Argument;
import lombok.ast.Expression;
import lombok.core.AnnotationValues;
import lombok.core.handlers.ListenerSupportHandler;
import lombok.core.util.As;
import lombok.core.util.Each;
import lombok.core.util.ErrorMessages;
import lombok.eclipse.DeferUntilBuildFieldsAndMethods;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.Eclipse;
import lombok.eclipse.handlers.ast.EclipseType;
import lombok.eclipse.handlers.ast.EclipseTypeEditor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

@DeferUntilBuildFieldsAndMethods
public class HandleListenerSupport
extends EclipseAnnotationHandler<ListenerSupport> {
    private final EclipseListenerSupportHandler handler = new EclipseListenerSupportHandler();

    @Override
    public void handle(AnnotationValues<ListenerSupport> annotation, Annotation source, EclipseNode annotationNode) {
        EclipseType type = EclipseType.typeOf(annotationNode, (ASTNode)source);
        if (type.isAnnotation() || type.isInterface()) {
            annotationNode.addError(ErrorMessages.canBeUsedOnClassAndEnumOnly(ListenerSupport.class));
            return;
        }
        List<Object> listenerInterfaces = annotation.getActualExpressions("value");
        if (listenerInterfaces.isEmpty()) {
            annotationNode.addError(String.format("@%s has no effect since no interface types were specified.", ListenerSupport.class.getName()));
            return;
        }
        for (Object listenerInterface : listenerInterfaces) {
            TypeBinding binding;
            if (!(listenerInterface instanceof ClassLiteralAccess) || (binding = ((ClassLiteralAccess)listenerInterface).type.resolveType((BlockScope)type.get().initializerScope)) == null) continue;
            if (!binding.isInterface()) {
                annotationNode.addWarning(String.format("@%s works only with interfaces. %s was skipped", ListenerSupport.class.getName(), As.string(binding.readableName())));
                continue;
            }
            this.handler.addListenerField(type, (Object)binding);
            this.handler.addAddListenerMethod(type, (Object)binding);
            this.handler.addRemoveListenerMethod(type, (Object)binding);
            this.addFireListenerMethods(type, binding);
        }
        type.editor().rebuild();
    }

    private void addFireListenerMethods(EclipseType type, TypeBinding interfaze) {
        List<MethodBinding> methods = this.getInterfaceMethods(interfaze);
        for (MethodBinding method : methods) {
            this.handler.addFireListenerMethod(type, (Object)interfaze, (Object)method);
        }
    }

    private List<MethodBinding> getInterfaceMethods(TypeBinding binding) {
        ArrayList<MethodBinding> methods = new ArrayList<MethodBinding>();
        this.getInterfaceMethods(binding, methods, new HashSet<String>());
        return methods;
    }

    private void getInterfaceMethods(TypeBinding binding, List<MethodBinding> methods, Set<String> banList) {
        if (binding == null) {
            return;
        }
        Eclipse.ensureAllClassScopeMethodWereBuild(binding);
        if (binding instanceof ReferenceBinding) {
            ReferenceBinding rb = (ReferenceBinding)binding;
            MethodBinding[] availableMethods = rb.availableMethods();
            for (MethodBinding mb : Each.elementIn(availableMethods)) {
                String sig = As.string(mb.readableName());
                if (!banList.add(sig)) continue;
                methods.add(mb);
            }
            ReferenceBinding[] interfaces = rb.superInterfaces();
            for (ReferenceBinding iface : Each.elementIn(interfaces)) {
                this.getInterfaceMethods((TypeBinding)iface, methods, banList);
            }
        }
    }

    private static class EclipseListenerSupportHandler
    extends ListenerSupportHandler<EclipseType> {
        private EclipseListenerSupportHandler() {
        }

        @Override
        protected void createParamsAndArgs(Object method, List<Argument> params, List<Expression<?>> args) {
            MethodBinding methodBinding = (MethodBinding)method;
            int argCounter = 0;
            for (TypeBinding parameter : Each.elementIn(methodBinding.parameters)) {
                String arg = "arg" + argCounter++;
                params.add(AST.Arg(AST.Type((Object)parameter), arg));
                args.add(AST.Name(arg));
            }
        }

        @Override
        protected String name(Object object) {
            if (object instanceof MethodBinding) {
                return As.string(((MethodBinding)object).selector);
            }
            return As.string(((Binding)object).shortReadableName());
        }

        @Override
        protected Object type(Object object) {
            return object;
        }
    }

}

