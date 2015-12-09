/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TypeLibrary {
    private final Map<String, List<String>> keyToFqnMap;
    private final String singletonValue;
    private final List<String> singletonKeys;

    public TypeLibrary() {
        this.keyToFqnMap = new HashMap<String, List<String>>();
        this.singletonKeys = null;
        this.singletonValue = null;
    }

    private TypeLibrary(String fqnSingleton) {
        this.keyToFqnMap = null;
        this.singletonValue = fqnSingleton;
        int idx = fqnSingleton.lastIndexOf(46);
        this.singletonKeys = idx == -1 ? Collections.singletonList(fqnSingleton) : Arrays.asList(fqnSingleton, fqnSingleton.substring(idx + 1), fqnSingleton.substring(0, idx) + ".*");
    }

    public static TypeLibrary createLibraryForSingleType(String fqnSingleton) {
        return new TypeLibrary(fqnSingleton);
    }

    public void addType(String fullyQualifiedTypeName) {
        if (this.keyToFqnMap == null) {
            throw new IllegalStateException("SingleType library");
        }
        int idx = fullyQualifiedTypeName.lastIndexOf(46);
        if (idx == -1) {
            throw new IllegalArgumentException("Only fully qualified types are allowed (and stuff in the default package is not palatable to us either!)");
        }
        fullyQualifiedTypeName = fullyQualifiedTypeName.replace("$", ".");
        String simpleName = fullyQualifiedTypeName.substring(idx + 1);
        String packageName = fullyQualifiedTypeName.substring(0, idx);
        if (this.keyToFqnMap.put(fullyQualifiedTypeName, Collections.singletonList(fullyQualifiedTypeName)) != null) {
            return;
        }
        this.addToMap(simpleName, fullyQualifiedTypeName);
        this.addToMap(packageName + ".*", fullyQualifiedTypeName);
    }

    private TypeLibrary addToMap(String keyName, String fullyQualifiedTypeName) {
        List<String> list = this.keyToFqnMap.get(keyName);
        if (list == null) {
            list = new ArrayList<String>();
            this.keyToFqnMap.put(keyName, list);
        }
        list.add(fullyQualifiedTypeName);
        return this;
    }

    public Collection<String> findCompatible(String typeReference) {
        if (this.singletonKeys != null) {
            return this.singletonKeys.contains(typeReference) ? Collections.singletonList(this.singletonValue) : Collections.emptyList();
        }
        List<String> result = this.keyToFqnMap.get(typeReference);
        return result == null ? Collections.emptyList() : Collections.unmodifiableList(result);
    }
}

