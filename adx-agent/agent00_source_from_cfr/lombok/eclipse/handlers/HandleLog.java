/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ASTVisitor
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.MessageSend
 *  org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.lookup.MethodScope
 */
package lombok.eclipse.handlers;

import java.util.Arrays;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HandleLog {
    private HandleLog() {
        throw new UnsupportedOperationException();
    }

    public static void processAnnotation(LoggingFramework framework, AnnotationValues<? extends java.lang.annotation.Annotation> annotation, Annotation source, EclipseNode annotationNode) {
        EclipseNode owner = (EclipseNode)annotationNode.up();
        switch (owner.getKind()) {
            case TYPE: {
                boolean notAClass;
                TypeDeclaration typeDecl = null;
                if (owner.get() instanceof TypeDeclaration) {
                    typeDecl = (TypeDeclaration)owner.get();
                }
                int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
                boolean bl = notAClass = (modifiers & 8704) != 0;
                if (typeDecl == null || notAClass) {
                    annotationNode.addError(framework.getAnnotationAsString() + " is legal only on classes and enums.");
                    return;
                }
                if (EclipseHandlerUtil.fieldExists("log", owner) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
                    annotationNode.addWarning("Field 'log' already exists.");
                    return;
                }
                ClassLiteralAccess loggingType = HandleLog.selfType(owner, source);
                FieldDeclaration fieldDeclaration = HandleLog.createField(framework, source, loggingType);
                fieldDeclaration.traverse((ASTVisitor)new SetGeneratedByVisitor((ASTNode)source), typeDecl.staticInitializerScope);
                EclipseHandlerUtil.injectField(owner, fieldDeclaration);
                owner.rebuild();
                break;
            }
        }
    }

    private static ClassLiteralAccess selfType(EclipseNode type, Annotation source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        TypeDeclaration typeDeclaration = (TypeDeclaration)type.get();
        SingleTypeReference typeReference = new SingleTypeReference(typeDeclaration.name, p);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)typeReference, (ASTNode)source);
        ClassLiteralAccess result = new ClassLiteralAccess(source.sourceEnd, (TypeReference)typeReference);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)result, (ASTNode)source);
        return result;
    }

    private static FieldDeclaration createField(LoggingFramework framework, Annotation source, ClassLiteralAccess loggingType) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        FieldDeclaration fieldDecl = new FieldDeclaration("log".toCharArray(), 0, -1);
        EclipseHandlerUtil.setGeneratedBy((ASTNode)fieldDecl, (ASTNode)source);
        fieldDecl.declarationSourceEnd = -1;
        fieldDecl.modifiers = 26;
        fieldDecl.type = HandleLog.createTypeReference(framework.getLoggerTypeName(), source);
        MessageSend factoryMethodCall = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy((ASTNode)factoryMethodCall, (ASTNode)source);
        factoryMethodCall.receiver = EclipseHandlerUtil.createNameReference(framework.getLoggerFactoryTypeName(), source);
        factoryMethodCall.selector = framework.getLoggerFactoryMethodName().toCharArray();
        Expression parameter = framework.createFactoryParameter(loggingType, source);
        factoryMethodCall.arguments = new Expression[]{parameter};
        factoryMethodCall.nameSourcePosition = p;
        factoryMethodCall.sourceStart = pS;
        factoryMethodCall.sourceEnd = factoryMethodCall.statementEnd = pE;
        fieldDecl.initialization = factoryMethodCall;
        return fieldDecl;
    }

    private static TypeReference createTypeReference(String typeName, Annotation source) {
        QualifiedTypeReference typeReference;
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        if (typeName.contains(".")) {
            char[][] typeNameTokens = Eclipse.fromQualifiedName(typeName);
            long[] pos = new long[typeNameTokens.length];
            Arrays.fill(pos, p);
            typeReference = new QualifiedTypeReference(typeNameTokens, pos);
        } else {
            typeReference = null;
        }
        EclipseHandlerUtil.setGeneratedBy((ASTNode)typeReference, (ASTNode)source);
        return typeReference;
    }

    static enum LoggingFramework {
        COMMONS("org.apache.commons.logging.Log", "org.apache.commons.logging.LogFactory", "getLog", "@CommonsLog"),
        JUL("java.util.logging.Logger", "java.util.logging.Logger", "getLogger", "@Log"){

            public Expression createFactoryParameter(ClassLiteralAccess type, Annotation source) {
                int pS = source.sourceStart;
                int pE = source.sourceEnd;
                long p = (long)pS << 32 | (long)pE;
                MessageSend factoryParameterCall = new MessageSend();
                EclipseHandlerUtil.setGeneratedBy((ASTNode)factoryParameterCall, (ASTNode)source);
                factoryParameterCall.receiver = super.createFactoryParameter(type, source);
                factoryParameterCall.selector = "getName".toCharArray();
                factoryParameterCall.nameSourcePosition = p;
                factoryParameterCall.sourceStart = pS;
                factoryParameterCall.sourceEnd = factoryParameterCall.statementEnd = pE;
                return factoryParameterCall;
            }
        }
        ,
        LOG4J("org.apache.log4j.Logger", "org.apache.log4j.Logger", "getLogger", "@Log4j"),
        SLF4J("org.slf4j.Logger", "org.slf4j.LoggerFactory", "getLogger", "@Slf4j");
        
        private final String loggerTypeName;
        private final String loggerFactoryTypeName;
        private final String loggerFactoryMethodName;
        private final String annotationAsString;

        private LoggingFramework(String loggerTypeName, String loggerFactoryTypeName, String loggerFactoryMethodName, String annotationAsString) {
            this.loggerTypeName = loggerTypeName;
            this.loggerFactoryTypeName = loggerFactoryTypeName;
            this.loggerFactoryMethodName = loggerFactoryMethodName;
            this.annotationAsString = annotationAsString;
        }

        final String getAnnotationAsString() {
            return this.annotationAsString;
        }

        final String getLoggerTypeName() {
            return this.loggerTypeName;
        }

        final String getLoggerFactoryTypeName() {
            return this.loggerFactoryTypeName;
        }

        final String getLoggerFactoryMethodName() {
            return this.loggerFactoryMethodName;
        }

        Expression createFactoryParameter(ClassLiteralAccess loggingType, Annotation source) {
            TypeReference copy = EclipseHandlerUtil.copyType(loggingType.type, (ASTNode)source);
            ClassLiteralAccess result = new ClassLiteralAccess(source.sourceEnd, copy);
            EclipseHandlerUtil.setGeneratedBy((ASTNode)result, (ASTNode)source);
            return result;
        }

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class HandleSlf4jLog
    extends EclipseAnnotationHandler<Slf4j> {
        @Override
        public void handle(AnnotationValues<Slf4j> annotation, Annotation source, EclipseNode annotationNode) {
            HandleLog.processAnnotation(LoggingFramework.SLF4J, annotation, source, annotationNode);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class HandleLog4jLog
    extends EclipseAnnotationHandler<Log4j> {
        @Override
        public void handle(AnnotationValues<Log4j> annotation, Annotation source, EclipseNode annotationNode) {
            HandleLog.processAnnotation(LoggingFramework.LOG4J, annotation, source, annotationNode);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class HandleJulLog
    extends EclipseAnnotationHandler<Log> {
        @Override
        public void handle(AnnotationValues<Log> annotation, Annotation source, EclipseNode annotationNode) {
            HandleLog.processAnnotation(LoggingFramework.JUL, annotation, source, annotationNode);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class HandleCommonsLog
    extends EclipseAnnotationHandler<CommonsLog> {
        @Override
        public void handle(AnnotationValues<CommonsLog> annotation, Annotation source, EclipseNode annotationNode) {
            HandleLog.processAnnotation(LoggingFramework.COMMONS, annotation, source, annotationNode);
        }
    }

}

