/*
 * Decompiled with CFR 0_110.
 */
package lombok.javac.apt;

import java.io.IOException;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import lombok.core.DiagnosticsReceiver;
import lombok.javac.apt.LombokFileObjects;

final class InterceptingJavaFileManager
extends ForwardingJavaFileManager<JavaFileManager> {
    private final DiagnosticsReceiver diagnostics;
    private final LombokFileObjects.Compiler compiler;

    InterceptingJavaFileManager(JavaFileManager original, DiagnosticsReceiver diagnostics) {
        super(original);
        this.compiler = LombokFileObjects.getCompiler(original);
        this.diagnostics = diagnostics;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (className.startsWith("lombok.dummy.ForceNewRound")) {
            String name = className.replace(".", "/") + kind.extension;
            return LombokFileObjects.createEmpty(this.compiler, name, kind);
        }
        JavaFileObject fileObject = this.fileManager.getJavaFileForOutput(location, className, kind, sibling);
        if (kind != JavaFileObject.Kind.CLASS) {
            return fileObject;
        }
        return LombokFileObjects.createIntercepting(this.compiler, fileObject, className, this.diagnostics);
    }
}

