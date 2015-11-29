/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.libs.org.objectweb.asm.AnnotationVisitor;
import lombok.libs.org.objectweb.asm.Attribute;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.FieldVisitor;
import lombok.libs.org.objectweb.asm.MethodVisitor;
import lombok.libs.org.objectweb.asm.tree.AnnotationNode;
import lombok.libs.org.objectweb.asm.tree.FieldNode;
import lombok.libs.org.objectweb.asm.tree.InnerClassNode;
import lombok.libs.org.objectweb.asm.tree.MethodNode;

public class ClassNode
extends ClassVisitor {
    public int version;
    public int access;
    public String name;
    public String signature;
    public String superName;
    public List interfaces = new ArrayList();
    public String sourceFile;
    public String sourceDebug;
    public String outerClass;
    public String outerMethod;
    public String outerMethodDesc;
    public List visibleAnnotations;
    public List invisibleAnnotations;
    public List attrs;
    public List innerClasses = new ArrayList();
    public List fields = new ArrayList();
    public List methods = new ArrayList();

    public ClassNode() {
        this(262144);
    }

    public ClassNode(int n) {
        super(n);
    }

    public void visit(int n, int n2, String string, String string2, String string3, String[] arrstring) {
        this.version = n;
        this.access = n2;
        this.name = string;
        this.signature = string2;
        this.superName = string3;
        if (arrstring != null) {
            this.interfaces.addAll(Arrays.asList(arrstring));
        }
    }

    public void visitSource(String string, String string2) {
        this.sourceFile = string;
        this.sourceDebug = string2;
    }

    public void visitOuterClass(String string, String string2, String string3) {
        this.outerClass = string;
        this.outerMethod = string2;
        this.outerMethodDesc = string3;
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        AnnotationNode annotationNode = new AnnotationNode(string);
        if (bl) {
            if (this.visibleAnnotations == null) {
                this.visibleAnnotations = new ArrayList(1);
            }
            this.visibleAnnotations.add(annotationNode);
        } else {
            if (this.invisibleAnnotations == null) {
                this.invisibleAnnotations = new ArrayList(1);
            }
            this.invisibleAnnotations.add(annotationNode);
        }
        return annotationNode;
    }

    public void visitAttribute(Attribute attribute) {
        if (this.attrs == null) {
            this.attrs = new ArrayList(1);
        }
        this.attrs.add(attribute);
    }

    public void visitInnerClass(String string, String string2, String string3, int n) {
        InnerClassNode innerClassNode = new InnerClassNode(string, string2, string3, n);
        this.innerClasses.add(innerClassNode);
    }

    public FieldVisitor visitField(int n, String string, String string2, String string3, Object object) {
        FieldNode fieldNode = new FieldNode(n, string, string2, string3, object);
        this.fields.add(fieldNode);
        return fieldNode;
    }

    public MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] arrstring) {
        MethodNode methodNode = new MethodNode(n, string, string2, string3, arrstring);
        this.methods.add(methodNode);
        return methodNode;
    }

    public void visitEnd() {
    }

    public void check(int n) {
    }

    public void accept(ClassVisitor classVisitor) {
        AnnotationNode annotationNode;
        int n;
        String[] arrstring = new String[this.interfaces.size()];
        this.interfaces.toArray(arrstring);
        classVisitor.visit(this.version, this.access, this.name, this.signature, this.superName, arrstring);
        if (this.sourceFile != null || this.sourceDebug != null) {
            classVisitor.visitSource(this.sourceFile, this.sourceDebug);
        }
        if (this.outerClass != null) {
            classVisitor.visitOuterClass(this.outerClass, this.outerMethod, this.outerMethodDesc);
        }
        int n2 = this.visibleAnnotations == null ? 0 : this.visibleAnnotations.size();
        for (n = 0; n < n2; ++n) {
            annotationNode = (AnnotationNode)this.visibleAnnotations.get(n);
            annotationNode.accept(classVisitor.visitAnnotation(annotationNode.desc, true));
        }
        n2 = this.invisibleAnnotations == null ? 0 : this.invisibleAnnotations.size();
        for (n = 0; n < n2; ++n) {
            annotationNode = (AnnotationNode)this.invisibleAnnotations.get(n);
            annotationNode.accept(classVisitor.visitAnnotation(annotationNode.desc, false));
        }
        n2 = this.attrs == null ? 0 : this.attrs.size();
        for (n = 0; n < n2; ++n) {
            classVisitor.visitAttribute((Attribute)this.attrs.get(n));
        }
        for (n = 0; n < this.innerClasses.size(); ++n) {
            ((InnerClassNode)this.innerClasses.get(n)).accept(classVisitor);
        }
        for (n = 0; n < this.fields.size(); ++n) {
            ((FieldNode)this.fields.get(n)).accept(classVisitor);
        }
        for (n = 0; n < this.methods.size(); ++n) {
            ((MethodNode)this.methods.get(n)).accept(classVisitor);
        }
        classVisitor.visitEnd();
    }
}

