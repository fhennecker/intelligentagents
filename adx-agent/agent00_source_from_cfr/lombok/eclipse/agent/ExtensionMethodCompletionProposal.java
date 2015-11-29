/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.core.compiler.CharOperation
 *  org.eclipse.jdt.internal.codeassist.CompletionEngine
 *  org.eclipse.jdt.internal.codeassist.InternalCompletionProposal
 *  org.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberAccess
 *  org.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedNameReference
 *  org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.lookup.MethodBinding
 *  org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 *  org.eclipse.jdt.internal.compiler.lookup.TypeBinding
 */
package lombok.eclipse.agent;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.internal.codeassist.InternalCompletionProposal;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberAccess;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedNameReference;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ExtensionMethodCompletionProposal
extends InternalCompletionProposal {
    public ExtensionMethodCompletionProposal(int replacementOffset) {
        super(6, replacementOffset - 1);
    }

    public void setMethodBinding(MethodBinding method, ASTNode node) {
        MethodBinding original = method.original();
        TypeBinding[] parameters = Arrays.copyOf(method.parameters, method.parameters.length);
        method.parameters = Arrays.copyOfRange(method.parameters, 1, method.parameters.length);
        TypeBinding[] originalParameters = null;
        if (original != method) {
            originalParameters = Arrays.copyOf(method.original().parameters, method.original().parameters.length);
            method.original().parameters = Arrays.copyOfRange(method.original().parameters, 1, method.original().parameters.length);
        }
        int length = method.parameters == null ? 0 : method.parameters.length;
        char[][] parameterPackageNames = new char[length][];
        char[][] parameterTypeNames = new char[length][];
        for (int i = 0; i < length; ++i) {
            TypeBinding type = method.original().parameters[i];
            parameterPackageNames[i] = type.qualifiedPackageName();
            parameterTypeNames[i] = type.qualifiedSourceName();
        }
        char[] completion = CharOperation.concat((char[])method.selector, (char[])new char[]{'(', ')'});
        this.setDeclarationSignature(CompletionEngine.getSignature((TypeBinding)method.declaringClass));
        this.setSignature(CompletionEngine.getSignature((MethodBinding)method));
        if (original != method) {
            this.setOriginalSignature(CompletionEngine.getSignature((MethodBinding)original));
        }
        this.setDeclarationPackageName(method.declaringClass.qualifiedPackageName());
        this.setDeclarationTypeName(method.declaringClass.qualifiedSourceName());
        this.setParameterPackageNames((char[][])parameterPackageNames);
        this.setParameterTypeNames((char[][])parameterTypeNames);
        this.setPackageName(method.returnType.qualifiedPackageName());
        this.setTypeName(method.returnType.qualifiedSourceName());
        this.setName(method.selector);
        this.setCompletion(completion);
        this.setFlags(method.modifiers & -9);
        int index = node.sourceEnd + 1;
        if (node instanceof CompletionOnQualifiedNameReference) {
            index -= ((CompletionOnQualifiedNameReference)node).completionIdentifier.length;
        }
        if (node instanceof CompletionOnMemberAccess) {
            index -= ((CompletionOnMemberAccess)node).token.length;
        }
        if (node instanceof CompletionOnSingleNameReference) {
            index -= ((CompletionOnSingleNameReference)node).token.length;
        }
        this.setReplaceRange(index, index);
        this.setTokenRange(index, index);
        this.setRelevance(100);
        method.parameters = parameters;
        if (original != method) {
            method.original().parameters = originalParameters;
        }
    }
}

