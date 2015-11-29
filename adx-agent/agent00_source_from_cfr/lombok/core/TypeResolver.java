/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import lombok.core.AST;
import lombok.core.LombokNode;
import lombok.core.TypeLibrary;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TypeResolver {
    private Collection<String> imports;

    public TypeResolver(String packageString, Collection<String> importStrings) {
        this.imports = TypeResolver.makeImportList(packageString, importStrings);
    }

    private static Collection<String> makeImportList(String packageString, Collection<String> importStrings) {
        HashSet<String> imports = new HashSet<String>();
        if (packageString != null) {
            imports.add(packageString + ".*");
        }
        imports.addAll(importStrings == null ? Collections.emptySet() : importStrings);
        imports.add("java.lang.*");
        return imports;
    }

    public boolean typeMatches(LombokNode<?, ?, ?> context, String fqn, String typeRef) {
        return !this.findTypeMatches(context, TypeLibrary.createLibraryForSingleType(fqn), typeRef).isEmpty();
    }

    public Collection<String> findTypeMatches(LombokNode<?, ?, ?> context, TypeLibrary library, String typeRef) {
        Collection<String> potentialMatches = library.findCompatible(typeRef);
        if (potentialMatches.isEmpty()) {
            return Collections.emptyList();
        }
        int idx = typeRef.indexOf(46);
        if (idx > -1) {
            return potentialMatches;
        }
        if (this.nameConflictInImportList(typeRef, potentialMatches)) {
            return Collections.emptyList();
        }
        if ((potentialMatches = this.eliminateImpossibleMatches(potentialMatches, library)).isEmpty()) {
            return Collections.emptyList();
        }
        LombokNode n = context;
        while (n != null) {
            if (n.getKind() == AST.Kind.TYPE && typeRef.equals(n.getName())) {
                return Collections.emptyList();
            }
            if (n.getKind() == AST.Kind.STATEMENT || n.getKind() == AST.Kind.LOCAL) {
                Object newN = n.directUp();
                if (newN == null) break;
                if (newN.getKind() == AST.Kind.STATEMENT || newN.getKind() == AST.Kind.INITIALIZER || newN.getKind() == AST.Kind.METHOD) {
                    for (LombokNode child : newN.down()) {
                        if (child.getKind() == AST.Kind.TYPE && typeRef.equals(child.getName())) {
                            return Collections.emptyList();
                        }
                        if (child != n) continue;
                        break;
                    }
                }
                n = newN;
                continue;
            }
            if (n.getKind() == AST.Kind.TYPE || n.getKind() == AST.Kind.COMPILATION_UNIT) {
                for (LombokNode child : n.down()) {
                    if (child.getKind() != AST.Kind.TYPE || !typeRef.equals(child.getName())) continue;
                    return Collections.emptyList();
                }
            }
            n = n.directUp();
        }
        return potentialMatches;
    }

    private Collection<String> eliminateImpossibleMatches(Collection<String> potentialMatches, TypeLibrary library) {
        HashSet<String> results = new HashSet<String>();
        for (String importedType : this.imports) {
            HashSet<String> reduced = new HashSet<String>(library.findCompatible(importedType));
            reduced.retainAll(potentialMatches);
            results.addAll(reduced);
        }
        return results;
    }

    private boolean nameConflictInImportList(String simpleName, Collection<String> potentialMatches) {
        for (String importedType : this.imports) {
            if (!TypeResolver.toSimpleName(importedType).equals(simpleName) || potentialMatches.contains(importedType)) continue;
            return true;
        }
        return false;
    }

    private static String toSimpleName(String typeName) {
        int idx = typeName.lastIndexOf(46);
        return idx == -1 ? typeName : typeName.substring(idx + 1);
    }
}

