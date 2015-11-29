/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.Attribute;
import lombok.libs.org.objectweb.asm.ClassReader;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.commons.JSRInlinerAdapter;
import lombok.patcher.Hook;
import lombok.patcher.MethodLogistics;
import lombok.patcher.MethodTarget;
import lombok.patcher.TargetMatcher;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class PatchScript {
    public String getPatchScriptName() {
        return this.getClass().getSimpleName();
    }

    public abstract Collection<String> getClassesToReload();

    public static boolean classMatches(String className, Collection<String> classSpecs) {
        for (String classSpec : classSpecs) {
            if (!MethodTarget.typeMatches(className, classSpec)) continue;
            return true;
        }
        return false;
    }

    public abstract byte[] patch(String var1, byte[] var2);

    protected byte[] runASM(byte[] byteCode, boolean computeFrames) {
        byte[] fixedByteCode = this.fixJSRInlining(byteCode);
        ClassReader reader = new ClassReader(fixedByteCode);
        FixedClassWriter writer = new FixedClassWriter(reader, computeFrames ? 3 : 0);
        ClassVisitor visitor = this.createClassVisitor(writer, reader.getClassName());
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }

    protected byte[] fixJSRInlining(byte[] byteCode) {
        ClassReader reader = new ClassReader(byteCode);
        FixedClassWriter writer = new FixedClassWriter(reader, 0);
        ClassVisitor visitor = new ClassVisitor(262144, writer){

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return new JSRInlinerAdapter(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc, signature, exceptions);
            }
        };
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }

    protected ClassVisitor createClassVisitor(ClassWriter writer, String classSpec) {
        throw new IllegalStateException("If you're going to call runASM, then you need to implement createClassVisitor");
    }

    private static byte[] readStream(String resourceName) {
        byte[] r22;
        block7 : {
            InputStream wrapStream = PatchScript.class.getResourceAsStream(resourceName);
            try {
                int r22;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] b = new byte[65536];
                while ((r22 = wrapStream.read(b)) != -1) {
                    baos.write(b, 0, r22);
                }
                r22 = baos.toByteArray();
                if (Collections.singletonList(wrapStream).get(0) == null) break block7;
            }
            catch (Throwable var5_7) {
                try {
                    if (Collections.singletonList(wrapStream).get(0) != null) {
                        wrapStream.close();
                    }
                    throw var5_7;
                }
                catch (IOException e) {
                    throw new IllegalArgumentException("resource " + resourceName + " does not exist.", e);
                }
            }
            wrapStream.close();
        }
        return r22;
    }

    protected static void insertMethod(final Hook methodToInsert, final MethodVisitor target) {
        byte[] classData = PatchScript.readStream("/" + methodToInsert.getClassSpec() + ".class");
        ClassReader reader = new ClassReader(classData);
        NoopClassVisitor methodFinder = new NoopClassVisitor(){

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (name.equals(methodToInsert.getMethodName()) && desc.equals(methodToInsert.getMethodDescriptor())) {
                    return new InsertBodyOfMethodIntoAnotherVisitor(target);
                }
                return null;
            }
        };
        reader.accept(methodFinder, 0);
    }

    protected static void transplantMethod(final Hook methodToTransplant, final ClassVisitor target) {
        byte[] classData = PatchScript.readStream("/" + methodToTransplant.getClassSpec() + ".class");
        ClassReader reader = new ClassReader(classData);
        NoopClassVisitor methodFinder = new NoopClassVisitor(){

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (name.equals(methodToTransplant.getMethodName()) && desc.equals(methodToTransplant.getMethodDescriptor())) {
                    return target.visitMethod(access, name, desc, signature, exceptions);
                }
                return null;
            }
        };
        reader.accept(methodFinder, 0);
    }

    protected static class MethodPatcher
    extends ClassVisitor {
        private List<TargetMatcher> targets = new ArrayList<TargetMatcher>();
        private String ownClassSpec;
        private final MethodPatcherFactory factory;
        private List<Hook> transplants = new ArrayList<Hook>();

        public MethodPatcher(ClassVisitor cv, MethodPatcherFactory factory) {
            super(262144, cv);
            this.factory = factory;
        }

        public void addTargetMatcher(TargetMatcher t) {
            this.targets.add(t);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.ownClassSpec = name;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        public void addTransplant(Hook transplant) {
            if (transplant == null) {
                throw new NullPointerException("transplant");
            }
            this.transplants.add(transplant);
        }

        public void visitEnd() {
            for (Hook transplant : this.transplants) {
                PatchScript.transplantMethod(transplant, this.cv);
            }
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
            Iterator<Hook> it = this.transplants.iterator();
            while (it.hasNext()) {
                Hook h = it.next();
                if (!h.getMethodName().equals(name) || !h.getMethodDescriptor().equals(desc)) continue;
                it.remove();
            }
            for (TargetMatcher t : this.targets) {
                if (!t.matches(this.ownClassSpec, name, desc)) continue;
                return this.factory.createMethodVisitor(name, desc, visitor, new MethodLogistics(access, desc));
            }
            return visitor;
        }

        public String getOwnClassSpec() {
            return this.ownClassSpec;
        }
    }

    private static final class InsertBodyOfMethodIntoAnotherVisitor
    extends MethodVisitor {
        private InsertBodyOfMethodIntoAnotherVisitor(MethodVisitor mv) {
            super(262144, mv);
        }

        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            return null;
        }

        public void visitMaxs(int maxStack, int maxLocals) {
        }

        public void visitLineNumber(int line, Label start) {
        }

        public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        }

        public void visitEnd() {
        }

        public void visitCode() {
        }

        public void visitInsn(int opcode) {
            if (opcode == 177 || opcode == 176 || opcode == 172 || opcode == 175 || opcode == 174 || opcode == 173) {
                return;
            }
            super.visitInsn(opcode);
        }

        public void visitAttribute(Attribute attr) {
        }

        public AnnotationVisitor visitAnnotationDefault() {
            return null;
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return null;
        }
    }

    private static abstract class NoopClassVisitor
    extends ClassVisitor {
        public NoopClassVisitor() {
            super(262144);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        }

        public void visitAttribute(Attribute attr) {
        }

        public void visitEnd() {
        }

        public void visitOuterClass(String owner, String name, String desc) {
        }

        public void visitSource(String source, String debug) {
        }

        public void visitInnerClass(String name, String outerName, String innerName, int access) {
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return null;
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return null;
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return null;
        }
    }

    public static interface MethodPatcherFactory {
        public MethodVisitor createMethodVisitor(String var1, String var2, MethodVisitor var3, MethodLogistics var4);
    }

    private static class FixedClassWriter
    extends ClassWriter {
        FixedClassWriter(ClassReader classReader, int flags) {
            super(classReader, flags);
        }

        protected String getCommonSuperClass(String type1, String type2) {
            try {
                return super.getCommonSuperClass(type1, type2);
            }
            catch (Exception e) {
                return "java/lang/Object";
            }
        }
    }

}

