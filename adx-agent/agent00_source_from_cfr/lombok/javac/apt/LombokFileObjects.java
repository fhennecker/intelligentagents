/*
 * Decompiled with CFR 0_110.
 */
package lombok.javac.apt;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import lombok.core.DiagnosticsReceiver;
import lombok.javac.apt.EmptyLombokFileObject;
import lombok.javac.apt.InterceptingJavaFileObject;
import lombok.javac.apt.Javac6BaseFileObjectWrapper;
import lombok.javac.apt.Javac7BaseFileObjectWrapper;
import lombok.javac.apt.LombokFileObject;

final class LombokFileObjects {
    private LombokFileObjects() {
    }

    static Compiler getCompiler(JavaFileManager jfm) {
        String jfmClassName;
        String string = jfmClassName = jfm != null ? jfm.getClass().getName() : "null";
        if (jfmClassName.equals("com.sun.tools.javac.util.DefaultFileManager")) {
            return Compiler.JAVAC6;
        }
        if (jfmClassName.equals("com.sun.tools.javac.file.JavacFileManager")) {
            return Compiler.JAVAC7;
        }
        try {
            if (Class.forName("com.sun.tools.javac.file.BaseFileObject") == null) {
                throw new NullPointerException();
            }
            return Compiler.JAVAC7;
        }
        catch (Exception e) {
            try {
                if (Class.forName("com.sun.tools.javac.util.BaseFileObject") == null) {
                    throw new NullPointerException();
                }
                return Compiler.JAVAC6;
            }
            catch (Exception e) {
                return null;
            }
        }
    }

    static JavaFileObject createEmpty(Compiler compiler, String name, JavaFileObject.Kind kind) {
        return compiler.wrap(new EmptyLombokFileObject(name, kind));
    }

    static JavaFileObject createIntercepting(Compiler compiler, JavaFileObject delegate, String fileName, DiagnosticsReceiver diagnostics) {
        return compiler.wrap(new InterceptingJavaFileObject(delegate, fileName, diagnostics, compiler.getDecoderMethod()));
    }

    static enum Compiler {
        JAVAC6{
            private Method decoderMethod = null;
            private final AtomicBoolean decoderIsSet = new AtomicBoolean();

            @Override
            public JavaFileObject wrap(LombokFileObject fileObject) {
                return new Javac6BaseFileObjectWrapper(fileObject);
            }

            @Override
            public Method getDecoderMethod() {
                AtomicBoolean atomicBoolean = this.decoderIsSet;
                synchronized (atomicBoolean) {
                    if (this.decoderIsSet.get()) {
                        return this.decoderMethod;
                    }
                    this.decoderMethod = .getDecoderMethod("com.sun.tools.javac.util.BaseFileObject");
                    this.decoderIsSet.set(true);
                    return this.decoderMethod;
                }
            }
        }
        ,
        JAVAC7{
            private Method decoderMethod = null;
            private final AtomicBoolean decoderIsSet = new AtomicBoolean();

            @Override
            public JavaFileObject wrap(LombokFileObject fileObject) {
                return new Javac7BaseFileObjectWrapper(fileObject);
            }

            @Override
            public Method getDecoderMethod() {
                AtomicBoolean atomicBoolean = this.decoderIsSet;
                synchronized (atomicBoolean) {
                    if (this.decoderIsSet.get()) {
                        return this.decoderMethod;
                    }
                    this.decoderMethod = Compiler.getDecoderMethod("com.sun.tools.javac.util.BaseFileObject");
                    this.decoderIsSet.set(true);
                    return this.decoderMethod;
                }
            }
        };
        

        private Compiler() {
        }

        static Method getDecoderMethod(String className) {
            Method m = null;
            try {
                m = Class.forName(className).getDeclaredMethod("getDecoder", Boolean.TYPE);
                m.setAccessible(true);
            }
            catch (NoSuchMethodException e) {
            }
            catch (ClassNotFoundException e) {
                // empty catch block
            }
            return m;
        }

        abstract JavaFileObject wrap(LombokFileObject var1);

        abstract Method getDecoderMethod();

    }

}

