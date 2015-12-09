/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Map;
import lombok.libs.org.objectweb.asm.commons.Remapper;

public class SimpleRemapper
extends Remapper {
    private final Map mapping;

    public SimpleRemapper(Map map) {
        this.mapping = map;
    }

    public SimpleRemapper(String string, String string2) {
        this.mapping = Collections.singletonMap(string, string2);
    }

    public String mapMethodName(String string, String string2, String string3) {
        String string4 = this.map(string + '.' + string2 + string3);
        return string4 == null ? string2 : string4;
    }

    public String mapFieldName(String string, String string2, String string3) {
        String string4 = this.map(string + '.' + string2);
        return string4 == null ? string2 : string4;
    }

    public String map(String string) {
        return (String)this.mapping.get(string);
    }
}

