/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher.scripts;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.libs.org.objectweb.asm.ClassVisitor;
import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.patcher.PatchScript;
import lombok.patcher.TargetMatcher;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class MethodLevelPatchScript
extends PatchScript {
    private final Set<String> affectedClasses;
    private final Collection<TargetMatcher> matchers;

    public MethodLevelPatchScript(Collection<TargetMatcher> matchers) {
        this.matchers = matchers;
        HashSet<String> affected = new HashSet<String>();
        for (TargetMatcher t : matchers) {
            affected.addAll(t.getAffectedClasses());
        }
        this.affectedClasses = Collections.unmodifiableSet(affected);
    }

    @Override
    public Collection<String> getClassesToReload() {
        return this.affectedClasses;
    }

    @Override
    public byte[] patch(String className, byte[] byteCode) {
        if (!MethodLevelPatchScript.classMatches(className, this.affectedClasses)) {
            return null;
        }
        return this.runASM(byteCode, true);
    }

    @Override
    protected final ClassVisitor createClassVisitor(ClassWriter writer, String classSpec) {
        PatchScript.MethodPatcher patcher = this.createPatcher(writer, classSpec);
        for (TargetMatcher matcher : this.matchers) {
            patcher.addTargetMatcher(matcher);
        }
        return patcher;
    }

    protected abstract PatchScript.MethodPatcher createPatcher(ClassWriter var1, String var2);
}

