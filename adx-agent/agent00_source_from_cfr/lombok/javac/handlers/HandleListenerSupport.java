/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.code.Symbol
 *  com.sun.tools.javac.code.Symbol$MethodSymbol
 *  com.sun.tools.javac.code.Symbol$TypeSymbol
 *  com.sun.tools.javac.code.Type
 *  com.sun.tools.javac.code.Type$ClassType
 *  com.sun.tools.javac.code.Type$MethodType
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import javax.lang.model.element.ElementKind;
import lombok.ListenerSupport;
import lombok.ast.AST;
import lombok.ast.Argument;
import lombok.ast.Expression;
import lombok.core.AnnotationValues;
import lombok.core.handlers.ListenerSupportHandler;
import lombok.core.util.As;
import lombok.core.util.ErrorMessages;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.ResolutionBased;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.ast.JavacResolver;
import lombok.javac.handlers.ast.JavacType;
import lombok.javac.handlers.ast.JavacTypeEditor;

@ResolutionBased
public class HandleListenerSupport
extends JavacAnnotationHandler<ListenerSupport> {
    private final JavacListenerSupportHandler handler = new JavacListenerSupportHandler();

    @Override
    public void handle(AnnotationValues<ListenerSupport> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, ListenerSupport.class);
        JavacType type = JavacType.typeOf(annotationNode, (JCTree)source);
        if (type.isAnnotation() || type.isInterface()) {
            annotationNode.addError(ErrorMessages.canBeUsedOnClassAndEnumOnly(ListenerSupport.class));
            return;
        }
        java.util.List<Object> listenerInterfaces = annotation.getActualExpressions("value");
        if (listenerInterfaces.isEmpty()) {
            annotationNode.addError(String.format("@%s has no effect since no interface types were specified.", ListenerSupport.class.getName()));
            return;
        }
        java.util.List<Symbol.TypeSymbol> resolvedInterfaces = this.resolveInterfaces(annotationNode, ListenerSupport.class, listenerInterfaces);
        for (Symbol.TypeSymbol interfaze : resolvedInterfaces) {
            this.handler.addListenerField(type, (Object)interfaze);
            this.handler.addAddListenerMethod(type, (Object)interfaze);
            this.handler.addRemoveListenerMethod(type, (Object)interfaze);
            this.addFireListenerMethods(type, interfaze);
        }
        type.editor().rebuild();
    }

    private java.util.List<Symbol.TypeSymbol> resolveInterfaces(JavacNode annotationNode, Class<? extends Annotation> annotationType, java.util.List<Object> listenerInterfaces) {
        ArrayList<Symbol.TypeSymbol> resolvedInterfaces = new ArrayList<Symbol.TypeSymbol>();
        for (Object listenerInterface : listenerInterfaces) {
            Type interfaceType;
            if (!(listenerInterface instanceof JCTree.JCFieldAccess)) continue;
            JCTree.JCFieldAccess interfaze = (JCTree.JCFieldAccess)listenerInterface;
            if (!"class".equals(As.string((Object)interfaze.name)) || (interfaceType = JavacResolver.CLASS.resolveMember(annotationNode, interfaze.selected)) == null) continue;
            if (interfaceType.isInterface()) {
                Symbol.TypeSymbol interfaceSymbol = interfaceType.asElement();
                if (interfaceSymbol == null) continue;
                resolvedInterfaces.add(interfaceSymbol);
                continue;
            }
            annotationNode.addWarning(String.format("@%s works only with interfaces. %s was skipped", annotationType.getName(), listenerInterface));
        }
        return resolvedInterfaces;
    }

    private void addFireListenerMethods(JavacType type, Symbol.TypeSymbol interfaze) {
        this.addAllFireListenerMethods(type, interfaze, interfaze);
    }

    private void addAllFireListenerMethods(JavacType type, Symbol.TypeSymbol interfaze, Symbol.TypeSymbol superInterfaze) {
        for (Symbol member : superInterfaze.getEnclosedElements()) {
            if (member.getKind() != ElementKind.METHOD) continue;
            this.handler.addFireListenerMethod(type, (Object)interfaze, (Object)((Symbol.MethodSymbol)member));
        }
        Type.ClassType superInterfazeType = (Type.ClassType)superInterfaze.type;
        if (superInterfazeType.interfaces_field != null) {
            for (Type iface : superInterfazeType.interfaces_field) {
                this.addAllFireListenerMethods(type, interfaze, iface.asElement());
            }
        }
    }

    private static class JavacListenerSupportHandler
    extends ListenerSupportHandler<JavacType> {
        private JavacListenerSupportHandler() {
        }

        @Override
        protected void createParamsAndArgs(Object method, java.util.List<Argument> params, java.util.List<Expression<?>> args) {
            Type.MethodType mtype = (Type.MethodType)this.type(method);
            if (mtype.argtypes.isEmpty()) {
                return;
            }
            int argCounter = 0;
            for (Type parameter : mtype.getParameterTypes()) {
                String arg = "arg" + argCounter++;
                params.add(AST.Arg(AST.Type((Object)parameter), arg));
                args.add(AST.Name(arg));
            }
        }

        @Override
        protected String name(Object object) {
            return As.string((Object)((Symbol)object).name);
        }

        @Override
        protected Object type(Object object) {
            return ((Symbol)object).type;
        }
    }

}

