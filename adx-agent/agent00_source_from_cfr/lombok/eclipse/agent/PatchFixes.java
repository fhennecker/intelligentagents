/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.CoreException
 *  org.eclipse.jdt.core.IAnnotatable
 *  org.eclipse.jdt.core.IAnnotation
 *  org.eclipse.jdt.core.IMethod
 *  org.eclipse.jdt.core.ISourceRange
 *  org.eclipse.jdt.core.IType
 *  org.eclipse.jdt.core.JavaModelException
 *  org.eclipse.jdt.core.dom.ASTNode
 *  org.eclipse.jdt.core.dom.AbstractTypeDeclaration
 *  org.eclipse.jdt.core.dom.Annotation
 *  org.eclipse.jdt.core.dom.CompilationUnit
 *  org.eclipse.jdt.core.dom.Expression
 *  org.eclipse.jdt.core.dom.ITypeBinding
 *  org.eclipse.jdt.core.dom.MethodDeclaration
 *  org.eclipse.jdt.core.dom.Name
 *  org.eclipse.jdt.core.dom.NormalAnnotation
 *  org.eclipse.jdt.core.dom.QualifiedName
 *  org.eclipse.jdt.core.dom.SimpleName
 *  org.eclipse.jdt.core.dom.SingleMemberAnnotation
 *  org.eclipse.jdt.core.dom.Type
 *  org.eclipse.jdt.core.dom.rewrite.ListRewrite
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.core.dom.rewrite.NodeRewriteEvent
 *  org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent
 *  org.eclipse.jdt.internal.core.dom.rewrite.TokenScanner
 *  org.eclipse.jdt.internal.corext.refactoring.structure.ASTNodeSearchUtil
 */
package lombok.eclipse.agent;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import lombok.core.DiagnosticsReceiver;
import lombok.core.PostCompiler;
import lombok.core.Version;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.core.dom.rewrite.NodeRewriteEvent;
import org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent;
import org.eclipse.jdt.internal.core.dom.rewrite.TokenScanner;
import org.eclipse.jdt.internal.corext.refactoring.structure.ASTNodeSearchUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PatchFixes {
    public static final int ALREADY_PROCESSED_FLAG = 8388608;

    public static String addLombokNotesToEclipseAboutDialog(String origReturnValue, String key) {
        if ("aboutText".equals(key)) {
            return origReturnValue + "\n\nLombok " + Version.getFullVersion() + " is installed. http://projectlombok.org/";
        }
        return origReturnValue;
    }

    public static boolean isGenerated(ASTNode node) {
        boolean result = false;
        try {
            result = (Boolean)node.getClass().getField("$isGenerated").get((Object)node);
            if (!result && node.getParent() != null && node.getParent() instanceof QualifiedName) {
                result = PatchFixes.isGenerated(node.getParent());
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        return result;
    }

    public static boolean isListRewriteOnGeneratedNode(ListRewrite rewrite) {
        return PatchFixes.isGenerated(rewrite.getParent());
    }

    public static boolean returnFalse(Object object) {
        return false;
    }

    public static boolean returnTrue(Object object) {
        return true;
    }

    public static List removeGeneratedNodes(List list) {
        try {
            ArrayList realNodes = new ArrayList(list.size());
            for (Object node : list) {
                if (PatchFixes.isGenerated((ASTNode)node)) continue;
                realNodes.add(node);
            }
            return realNodes;
        }
        catch (Exception e) {
            return list;
        }
    }

    public static String getRealMethodDeclarationSource(String original, Object processor, MethodDeclaration declaration) throws Exception {
        if (!PatchFixes.isGenerated((ASTNode)declaration)) {
            return original;
        }
        ArrayList<org.eclipse.jdt.core.dom.Annotation> annotations = new ArrayList<org.eclipse.jdt.core.dom.Annotation>();
        for (Object modifier : declaration.modifiers()) {
            String qualifiedAnnotationName;
            org.eclipse.jdt.core.dom.Annotation annotation;
            if (!(modifier instanceof org.eclipse.jdt.core.dom.Annotation) || "java.lang.Override".equals(qualifiedAnnotationName = (annotation = (org.eclipse.jdt.core.dom.Annotation)modifier).resolveTypeBinding().getQualifiedName()) || "java.lang.SuppressWarnings".equals(qualifiedAnnotationName)) continue;
            annotations.add(annotation);
        }
        StringBuilder signature = new StringBuilder();
        PatchFixes.addAnnotations(annotations, signature);
        if (((Boolean)processor.getClass().getDeclaredField("fPublic").get(processor)).booleanValue()) {
            signature.append("public ");
        }
        if (((Boolean)processor.getClass().getDeclaredField("fAbstract").get(processor)).booleanValue()) {
            signature.append("abstract ");
        }
        signature.append(declaration.getReturnType2().toString()).append(" ").append(declaration.getName().getFullyQualifiedName()).append("(");
        boolean first = true;
        for (Object parameter : declaration.parameters()) {
            if (!first) {
                signature.append(", ");
            }
            first = false;
            signature.append(parameter);
        }
        signature.append(");");
        return signature.toString();
    }

    public static void addAnnotations(List<org.eclipse.jdt.core.dom.Annotation> annotations, StringBuilder signature) {
        for (org.eclipse.jdt.core.dom.Annotation annotation : annotations) {
            ArrayList<String> values = new ArrayList<String>();
            if (annotation.isSingleMemberAnnotation()) {
                SingleMemberAnnotation smAnn = (SingleMemberAnnotation)annotation;
                values.add(smAnn.getValue().toString());
            } else if (annotation.isNormalAnnotation()) {
                NormalAnnotation normalAnn = (NormalAnnotation)annotation;
                for (Object value : normalAnn.values()) {
                    values.add(value.toString());
                }
            }
            signature.append("@").append(annotation.resolveTypeBinding().getQualifiedName());
            if (!values.isEmpty()) {
                signature.append("(");
                boolean first = true;
                for (String string : values) {
                    if (!first) {
                        signature.append(", ");
                    }
                    first = false;
                    signature.append('\"').append(string).append('\"');
                }
                signature.append(")");
            }
            signature.append(" ");
        }
    }

    public static MethodDeclaration getRealMethodDeclarationNode(IMethod sourceMethod, CompilationUnit cuUnit) throws JavaModelException {
        MethodDeclaration methodDeclarationNode = ASTNodeSearchUtil.getMethodDeclarationNode((IMethod)sourceMethod, (CompilationUnit)cuUnit);
        if (PatchFixes.isGenerated((ASTNode)methodDeclarationNode)) {
            Stack<IType> typeStack = new Stack<IType>();
            for (IType declaringType = sourceMethod.getDeclaringType(); declaringType != null; declaringType = declaringType.getDeclaringType()) {
                typeStack.push(declaringType);
            }
            IType rootType = (IType)typeStack.pop();
            AbstractTypeDeclaration typeDeclaration = PatchFixes.findTypeDeclaration(rootType, cuUnit.types());
            while (!typeStack.isEmpty() && typeDeclaration != null) {
                typeDeclaration = PatchFixes.findTypeDeclaration((IType)typeStack.pop(), typeDeclaration.bodyDeclarations());
            }
            if (typeStack.isEmpty() && typeDeclaration != null) {
                String methodName = sourceMethod.getElementName();
                for (Object declaration : typeDeclaration.bodyDeclarations()) {
                    MethodDeclaration methodDeclaration;
                    if (!(declaration instanceof MethodDeclaration) || !(methodDeclaration = (MethodDeclaration)declaration).getName().toString().equals(methodName)) continue;
                    return methodDeclaration;
                }
            }
        }
        return methodDeclarationNode;
    }

    public static AbstractTypeDeclaration findTypeDeclaration(IType searchType, List<?> nodes) {
        for (Object object : nodes) {
            AbstractTypeDeclaration typeDeclaration;
            if (!(object instanceof AbstractTypeDeclaration) || !(typeDeclaration = (AbstractTypeDeclaration)object).getName().toString().equals(searchType.getElementName())) continue;
            return typeDeclaration;
        }
        return null;
    }

    public static int getSourceEndFixed(int sourceEnd, org.eclipse.jdt.internal.compiler.ast.ASTNode node) throws Exception {
        org.eclipse.jdt.internal.compiler.ast.ASTNode object;
        if (sourceEnd == -1 && (object = (org.eclipse.jdt.internal.compiler.ast.ASTNode)node.getClass().getField("$generatedBy").get((Object)node)) != null) {
            return object.sourceEnd;
        }
        return sourceEnd;
    }

    public static int fixRetrieveStartingCatchPosition(int original, int start) {
        return original == -1 ? start : original;
    }

    public static int fixRetrieveIdentifierEndPosition(int original, int end) {
        return original == -1 ? end : original;
    }

    public static int fixRetrieveEllipsisStartPosition(int original, int end) {
        return original == -1 ? end : original;
    }

    public static int fixRetrieveRightBraceOrSemiColonPosition(int original, int end) {
        return original == -1 ? end : original;
    }

    public static boolean checkBit24(Object node) throws Exception {
        int bits = (Integer)node.getClass().getField("bits").get(node);
        return (bits & 8388608) != 0;
    }

    public static boolean skipRewritingGeneratedNodes(ASTNode node) throws Exception {
        return (Boolean)node.getClass().getField("$isGenerated").get((Object)node);
    }

    public static void setIsGeneratedFlag(ASTNode domNode, org.eclipse.jdt.internal.compiler.ast.ASTNode internalNode) throws Exception {
        boolean isGenerated;
        if (internalNode == null || domNode == null) {
            return;
        }
        boolean bl = isGenerated = internalNode.getClass().getField("$generatedBy").get((Object)internalNode) != null;
        if (isGenerated) {
            domNode.getClass().getField("$isGenerated").set((Object)domNode, true);
            domNode.setFlags(domNode.getFlags() & -3);
        }
    }

    public static void setIsGeneratedFlagForName(Name name, Object internalNode) throws Exception {
        if (internalNode instanceof org.eclipse.jdt.internal.compiler.ast.ASTNode && internalNode.getClass().getField("$generatedBy").get(internalNode) != null) {
            name.getClass().getField("$isGenerated").set((Object)name, true);
        }
    }

    public static RewriteEvent[] listRewriteHandleGeneratedMethods(RewriteEvent parent) {
        RewriteEvent[] children = parent.getChildren();
        ArrayList<Object> newChildren = new ArrayList<Object>();
        ArrayList<NodeRewriteEvent> modifiedChildren = new ArrayList<NodeRewriteEvent>();
        for (int i = 0; i < children.length; ++i) {
            RewriteEvent child = children[i];
            boolean isGenerated = PatchFixes.isGenerated((ASTNode)child.getOriginalValue());
            if (isGenerated) {
                if (child.getChangeKind() != 4 && child.getChangeKind() != 2 || !(child.getOriginalValue() instanceof MethodDeclaration) || child.getNewValue() == null) continue;
                modifiedChildren.add(new NodeRewriteEvent((Object)null, child.getNewValue()));
                continue;
            }
            newChildren.add((Object)child);
        }
        newChildren.addAll(modifiedChildren);
        return newChildren.toArray((T[])new RewriteEvent[newChildren.size()]);
    }

    public static int getTokenEndOffsetFixed(TokenScanner scanner, int token, int startOffset, Object domNode) throws CoreException {
        boolean isGenerated = false;
        try {
            isGenerated = (Boolean)domNode.getClass().getField("$isGenerated").get(domNode);
        }
        catch (Exception e) {
            // empty catch block
        }
        if (isGenerated) {
            return -1;
        }
        return scanner.getTokenEndOffset(token, startOffset);
    }

    public static IMethod[] removeGeneratedMethods(IMethod[] methods) throws Exception {
        ArrayList<IMethod> result = new ArrayList<IMethod>();
        for (IMethod m : methods) {
            if (m.getNameRange().getLength() <= 0 || m.getNameRange().equals((Object)m.getSourceRange())) continue;
            result.add(m);
        }
        return result.size() == methods.length ? methods : result.toArray((T[])new IMethod[result.size()]);
    }

    public static SimpleName[] removeGeneratedSimpleNames(SimpleName[] in) throws Exception {
        Field f = SimpleName.class.getField("$isGenerated");
        int count = 0;
        for (int i = 0; i < in.length; ++i) {
            if (in[i] != null && ((Boolean)f.get((Object)in[i])).booleanValue()) continue;
            ++count;
        }
        if (count == in.length) {
            return in;
        }
        SimpleName[] newSimpleNames = new SimpleName[count];
        count = 0;
        for (int i2 = 0; i2 < in.length; ++i2) {
            if (in[i2] != null && ((Boolean)f.get((Object)in[i2])).booleanValue()) continue;
            newSimpleNames[count++] = in[i2];
        }
        return newSimpleNames;
    }

    public static byte[] runPostCompiler(byte[] bytes, String fileName) {
        byte[] transformed = PostCompiler.applyTransformations(bytes, fileName, DiagnosticsReceiver.CONSOLE);
        return transformed == null ? bytes : transformed;
    }

    public static OutputStream runPostCompiler(OutputStream out) throws IOException {
        return PostCompiler.wrapOutputStream(out, "TEST", DiagnosticsReceiver.CONSOLE);
    }

    public static BufferedOutputStream runPostCompiler(BufferedOutputStream out, String path, String name) throws IOException {
        String fileName = path + "/" + name;
        return new BufferedOutputStream(PostCompiler.wrapOutputStream(out, fileName, DiagnosticsReceiver.CONSOLE));
    }

    public static Annotation[] convertAnnotations(Annotation[] out, IAnnotatable annotatable) {
        IAnnotation[] in;
        try {
            in = annotatable.getAnnotations();
        }
        catch (Exception e) {
            return out;
        }
        if (out == null) {
            return null;
        }
        int toWrite = 0;
        for (int idx = 0; idx < out.length; ++idx) {
            String oName = new String(out[idx].type.getLastToken());
            boolean found = false;
            for (IAnnotation i : in) {
                String name = i.getElementName();
                int li = name.lastIndexOf(46);
                if (li > -1) {
                    name = name.substring(li + 1);
                }
                if (!name.equals(oName)) continue;
                found = true;
                break;
            }
            if (!found) {
                out[idx] = null;
                continue;
            }
            ++toWrite;
        }
        Annotation[] replace = out;
        if (toWrite < out.length) {
            replace = new Annotation[toWrite];
            int idx2 = 0;
            for (int i = 0; i < out.length; ++i) {
                if (out[i] == null) continue;
                replace[idx2++] = out[i];
            }
        }
        return replace;
    }
}

