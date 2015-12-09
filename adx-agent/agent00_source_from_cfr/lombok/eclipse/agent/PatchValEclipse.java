/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.core.dom.AST
 *  org.eclipse.jdt.core.dom.ASTNode
 *  org.eclipse.jdt.core.dom.Annotation
 *  org.eclipse.jdt.core.dom.IExtendedModifier
 *  org.eclipse.jdt.core.dom.MarkerAnnotation
 *  org.eclipse.jdt.core.dom.Modifier
 *  org.eclipse.jdt.core.dom.Modifier$ModifierKeyword
 *  org.eclipse.jdt.core.dom.Name
 *  org.eclipse.jdt.core.dom.QualifiedName
 *  org.eclipse.jdt.core.dom.SimpleName
 *  org.eclipse.jdt.core.dom.SingleVariableDeclaration
 *  org.eclipse.jdt.core.dom.VariableDeclarationStatement
 *  org.eclipse.jdt.internal.compiler.ast.ASTNode
 *  org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.Annotation
 *  org.eclipse.jdt.internal.compiler.ast.Expression
 *  org.eclipse.jdt.internal.compiler.ast.ForeachStatement
 *  org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 *  org.eclipse.jdt.internal.compiler.ast.SingleTypeReference
 *  org.eclipse.jdt.internal.compiler.ast.TypeReference
 *  org.eclipse.jdt.internal.compiler.parser.Parser
 */
package lombok.eclipse.agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import lombok.Lombok;
import lombok.eclipse.agent.PatchVal;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.parser.Parser;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PatchValEclipse {
    private static final Field FIELD_NAME_INDEX;

    public static void copyInitializationOfForEachIterable(Parser parser) {
        org.eclipse.jdt.internal.compiler.ast.ASTNode[] astStack;
        int astPtr;
        try {
            astStack = (org.eclipse.jdt.internal.compiler.ast.ASTNode[])astStackField.get((Object)parser);
            astPtr = (Integer)astPtrField.get((Object)parser);
        }
        catch (Exception e) {
            return;
        }
        ForeachStatement foreachDecl = (ForeachStatement)astStack[astPtr];
        Expression init = foreachDecl.collection;
        if (init == null) {
            return;
        }
        if (foreachDecl.elementVariable == null || !PatchVal.couldBeVal(foreachDecl.elementVariable.type)) {
            return;
        }
        try {
            if (iterableCopyField != null) {
                iterableCopyField.set((Object)foreachDecl.elementVariable, (Object)init);
            }
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    public static void copyInitializationOfLocalDeclaration(Parser parser) {
        org.eclipse.jdt.internal.compiler.ast.ASTNode[] astStack;
        int astPtr;
        try {
            astStack = (org.eclipse.jdt.internal.compiler.ast.ASTNode[])astStackField.get((Object)parser);
            astPtr = (Integer)astPtrField.get((Object)parser);
        }
        catch (Exception e) {
            return;
        }
        AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration)astStack[astPtr];
        if (!(variableDecl instanceof LocalDeclaration)) {
            return;
        }
        Expression init = variableDecl.initialization;
        if (init == null) {
            return;
        }
        if (!PatchVal.couldBeVal(variableDecl.type)) {
            return;
        }
        try {
            if (initCopyField != null) {
                initCopyField.set((Object)variableDecl, (Object)init);
            }
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    public static void addFinalAndValAnnotationToSingleVariableDeclaration(Object converter, SingleVariableDeclaration out, LocalDeclaration in) {
        List modifiers = out.modifiers();
        PatchValEclipse.addFinalAndValAnnotationToModifierList(converter, modifiers, out.getAST(), in);
    }

    public static void addFinalAndValAnnotationToVariableDeclarationStatement(Object converter, VariableDeclarationStatement out, LocalDeclaration in) {
        List modifiers = out.modifiers();
        PatchValEclipse.addFinalAndValAnnotationToModifierList(converter, modifiers, out.getAST(), in);
    }

    public static void addFinalAndValAnnotationToModifierList(Object converter, List<IExtendedModifier> modifiers, AST ast, LocalDeclaration in) {
        if ((in.modifiers & 16) == 0) {
            return;
        }
        if (in.annotations == null) {
            return;
        }
        boolean found = false;
        Annotation valAnnotation = null;
        for (Annotation ann : in.annotations) {
            if (!PatchVal.couldBeVal(ann.type)) continue;
            found = true;
            valAnnotation = ann;
            break;
        }
        if (!found) {
            return;
        }
        if (modifiers == null) {
            return;
        }
        boolean finalIsPresent = false;
        boolean valIsPresent = false;
        for (IExtendedModifier present : modifiers) {
            Name typeName;
            String fullyQualifiedName;
            if (present instanceof Modifier) {
                Modifier.ModifierKeyword keyword = ((Modifier)present).getKeyword();
                if (keyword == null) continue;
                if (keyword.toFlagValue() == 16) {
                    finalIsPresent = true;
                }
            }
            if (!(present instanceof org.eclipse.jdt.core.dom.Annotation) || (typeName = ((org.eclipse.jdt.core.dom.Annotation)present).getTypeName()) == null || !"val".equals(fullyQualifiedName = typeName.getFullyQualifiedName()) && !"lombok.val".equals(fullyQualifiedName)) continue;
            valIsPresent = true;
        }
        if (!finalIsPresent) {
            modifiers.add((IExtendedModifier)PatchValEclipse.createModifier(ast, Modifier.ModifierKeyword.FINAL_KEYWORD, valAnnotation.sourceStart, valAnnotation.sourceEnd));
        }
        if (!valIsPresent) {
            MarkerAnnotation newAnnotation = PatchValEclipse.createValAnnotation(ast, valAnnotation, valAnnotation.sourceStart, valAnnotation.sourceEnd);
            try {
                astConverterRecordNodes.invoke(converter, new Object[]{newAnnotation, valAnnotation});
                astConverterRecordNodes.invoke(converter, new Object[]{newAnnotation.getTypeName(), valAnnotation.type});
            }
            catch (IllegalAccessException e) {
                Lombok.sneakyThrow(e);
            }
            catch (InvocationTargetException e) {
                Lombok.sneakyThrow(e.getCause());
            }
            modifiers.add((IExtendedModifier)newAnnotation);
        }
    }

    public static Modifier createModifier(AST ast, Modifier.ModifierKeyword keyword, int start, int end) {
        Modifier modifier = null;
        try {
            modifier = (Modifier)modifierConstructor.newInstance(new Object[]{ast});
        }
        catch (InstantiationException e) {
            Lombok.sneakyThrow(e);
        }
        catch (IllegalAccessException e) {
            Lombok.sneakyThrow(e);
        }
        catch (InvocationTargetException e) {
            Lombok.sneakyThrow(e);
        }
        if (modifier != null) {
            modifier.setKeyword(keyword);
            modifier.setSourceRange(start, end - start + 1);
        }
        return modifier;
    }

    public static MarkerAnnotation createValAnnotation(AST ast, Annotation original, int start, int end) {
        MarkerAnnotation out = null;
        try {
            out = (MarkerAnnotation)markerAnnotationConstructor.newInstance(new Object[]{ast});
        }
        catch (InstantiationException e) {
            Lombok.sneakyThrow(e);
        }
        catch (IllegalAccessException e) {
            Lombok.sneakyThrow(e);
        }
        catch (InvocationTargetException e) {
            Lombok.sneakyThrow(e);
        }
        if (out != null) {
            SimpleName valName = ast.newSimpleName("val");
            valName.setSourceRange(start, end - start + 1);
            if (original.type instanceof SingleTypeReference) {
                out.setTypeName((Name)valName);
                PatchValEclipse.setIndex((Name)valName, 1);
            } else {
                SimpleName lombokName = ast.newSimpleName("lombok");
                lombokName.setSourceRange(start, end - start + 1);
                PatchValEclipse.setIndex((Name)lombokName, 1);
                PatchValEclipse.setIndex((Name)valName, 2);
                QualifiedName fullName = ast.newQualifiedName((Name)lombokName, valName);
                PatchValEclipse.setIndex((Name)fullName, 1);
                fullName.setSourceRange(start, end - start + 1);
                out.setTypeName((Name)fullName);
            }
            out.setSourceRange(start, end - start + 1);
        }
        return out;
    }

    private static void setIndex(Name name, int index) {
        try {
            if (FIELD_NAME_INDEX != null) {
                FIELD_NAME_INDEX.set((Object)name, index);
            }
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    static {
        Field f = null;
        try {
            f = Name.class.getDeclaredField("index");
            f.setAccessible(true);
        }
        catch (Throwable t) {
            // empty catch block
        }
        FIELD_NAME_INDEX = f;
    }

    public static final class Reflection {
        private static final Field initCopyField;
        private static final Field iterableCopyField;
        private static final Field astStackField;
        private static final Field astPtrField;
        private static final Constructor<Modifier> modifierConstructor;
        private static final Constructor<MarkerAnnotation> markerAnnotationConstructor;
        private static final Method astConverterRecordNodes;

        static {
            Field a = null;
            Field b = null;
            Field c = null;
            Field d = null;
            Constructor f = null;
            Constructor g = null;
            Method h = null;
            try {
                a = LocalDeclaration.class.getDeclaredField("$initCopy");
                b = LocalDeclaration.class.getDeclaredField("$iterableCopy");
            }
            catch (Throwable t) {
                // empty catch block
            }
            try {
                c = Parser.class.getDeclaredField("astStack");
                c.setAccessible(true);
                d = Parser.class.getDeclaredField("astPtr");
                d.setAccessible(true);
                f = Modifier.class.getDeclaredConstructor(AST.class);
                f.setAccessible(true);
                g = MarkerAnnotation.class.getDeclaredConstructor(AST.class);
                g.setAccessible(true);
                Class z = Class.forName("org.eclipse.jdt.core.dom.ASTConverter");
                h = z.getDeclaredMethod("recordNodes", ASTNode.class, org.eclipse.jdt.internal.compiler.ast.ASTNode.class);
                h.setAccessible(true);
            }
            catch (Throwable t) {
                // empty catch block
            }
            initCopyField = a;
            iterableCopyField = b;
            astStackField = c;
            astPtrField = d;
            modifierConstructor = f;
            markerAnnotationConstructor = g;
            astConverterRecordNodes = h;
        }
    }

}

