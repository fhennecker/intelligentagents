/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm.commons;

import lombok.libs.org.objectweb.asm.Label;

public interface TableSwitchGenerator {
    public void generateCase(int var1, Label var2);

    public void generateDefault();
}

