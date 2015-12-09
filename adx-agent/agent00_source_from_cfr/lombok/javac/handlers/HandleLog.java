/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCAnnotation
 *  com.sun.tools.javac.tree.JCTree$JCClassDecl
 *  com.sun.tools.javac.tree.JCTree$JCExpression
 *  com.sun.tools.javac.tree.JCTree$JCFieldAccess
 *  com.sun.tools.javac.tree.JCTree$JCIdent
 *  com.sun.tools.javac.tree.JCTree$JCMethodInvocation
 *  com.sun.tools.javac.tree.JCTree$JCModifiers
 *  com.sun.tools.javac.tree.JCTree$JCVariableDecl
 *  com.sun.tools.javac.tree.TreeMaker
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.Name
 */
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.lang.annotation.Annotation;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleLog {
    private HandleLog() {
        throw new UnsupportedOperationException();
    }

    public static void processAnnotation(LoggingFramework framework, AnnotationValues<?> annotation, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, framework.getAnnotationClass());
        JavacNode typeNode = (JavacNode)annotationNode.up();
        switch (typeNode.getKind()) {
            case TYPE: {
                if ((((JCTree.JCClassDecl)typeNode.get()).mods.flags & 512) != 0) {
                    annotationNode.addError("@Log is legal only on classes and enums.");
                    return;
                }
                if (JavacHandlerUtil.fieldExists("log", typeNode) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
                    annotationNode.addWarning("Field 'log' already exists.");
                    return;
                }
                JCTree.JCFieldAccess loggingType = HandleLog.selfType(typeNode);
                HandleLog.createField(framework, typeNode, loggingType, (JCTree)annotationNode.get());
                break;
            }
            default: {
                annotationNode.addError("@Log is legal only on types.");
            }
        }
    }

    private static JCTree.JCFieldAccess selfType(JavacNode typeNode) {
        TreeMaker maker = typeNode.getTreeMaker();
        Name name = ((JCTree.JCClassDecl)typeNode.get()).name;
        return maker.Select((JCTree.JCExpression)maker.Ident(name), typeNode.toName("class"));
    }

    private static boolean createField(LoggingFramework framework, JavacNode typeNode, JCTree.JCFieldAccess loggingType, JCTree source) {
        TreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCExpression loggerType = JavacHandlerUtil.chainDotsString(typeNode, framework.getLoggerTypeName());
        JCTree.JCExpression factoryMethod = JavacHandlerUtil.chainDotsString(typeNode, framework.getLoggerFactoryMethodName());
        JCTree.JCExpression loggerName = framework.createFactoryParameter(typeNode, loggingType);
        JCTree.JCMethodInvocation factoryMethodCall = maker.Apply(List.nil(), factoryMethod, List.of((Object)loggerName));
        JCTree.JCVariableDecl fieldDecl = JavacHandlerUtil.recursiveSetGeneratedBy(maker.VarDef(maker.Modifiers(26), typeNode.toName("log"), loggerType, (JCTree.JCExpression)factoryMethodCall), source);
        JavacHandlerUtil.injectField(typeNode, fieldDecl);
        return true;
    }

    static enum LoggingFramework {
        COMMONS(CommonsLog.class, "org.apache.commons.logging.Log", "org.apache.commons.logging.LogFactory.getLog"),
        JUL((Class)Log.class, "java.util.logging.Logger", "java.util.logging.Logger.getLogger"){

            @Override
            public JCTree.JCExpression createFactoryParameter(JavacNode typeNode, JCTree.JCFieldAccess loggingType) {
                TreeMaker maker = typeNode.getTreeMaker();
                JCTree.JCFieldAccess method = maker.Select((JCTree.JCExpression)loggingType, typeNode.toName("getName"));
                return maker.Apply(List.nil(), (JCTree.JCExpression)method, List.nil());
            }
        }
        ,
        LOG4J(Log4j.class, "org.apache.log4j.Logger", "org.apache.log4j.Logger.getLogger"),
        SLF4J(Slf4j.class, "org.slf4j.Logger", "org.slf4j.LoggerFactory.getLogger");
        
        private final Class<? extends Annotation> annotationClass;
        private final String loggerTypeName;
        private final String loggerFactoryName;

        private LoggingFramework(Class<? extends Annotation> annotationClass, String loggerTypeName, String loggerFactoryName) {
            this.annotationClass = annotationClass;
            this.loggerTypeName = loggerTypeName;
            this.loggerFactoryName = loggerFactoryName;
        }

        final Class<? extends Annotation> getAnnotationClass() {
            return this.annotationClass;
        }

        final String getLoggerTypeName() {
            return this.loggerTypeName;
        }

        final String getLoggerFactoryMethodName() {
            return this.loggerFactoryName;
        }

        JCTree.JCExpression createFactoryParameter(JavacNode typeNode, JCTree.JCFieldAccess loggingType) {
            return loggingType;
        }

    }

    public static class HandleSlf4jLog
    extends JavacAnnotationHandler<Slf4j> {
        @Override
        public void handle(AnnotationValues<Slf4j> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandleLog.processAnnotation(LoggingFramework.SLF4J, annotation, annotationNode);
        }
    }

    public static class HandleLog4jLog
    extends JavacAnnotationHandler<Log4j> {
        @Override
        public void handle(AnnotationValues<Log4j> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandleLog.processAnnotation(LoggingFramework.LOG4J, annotation, annotationNode);
        }
    }

    public static class HandleJulLog
    extends JavacAnnotationHandler<Log> {
        @Override
        public void handle(AnnotationValues<Log> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandleLog.processAnnotation(LoggingFramework.JUL, annotation, annotationNode);
        }
    }

    public static class HandleCommonsLog
    extends JavacAnnotationHandler<CommonsLog> {
        @Override
        public void handle(AnnotationValues<CommonsLog> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandleLog.processAnnotation(LoggingFramework.COMMONS, annotation, annotationNode);
        }
    }

}

