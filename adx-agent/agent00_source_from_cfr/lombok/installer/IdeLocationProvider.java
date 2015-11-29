/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer;

import java.util.regex.Pattern;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeFinder;
import lombok.installer.IdeLocation;

public interface IdeLocationProvider {
    public IdeLocation create(String var1) throws CorruptedIdeLocationException;

    public Pattern getLocationSelectors(IdeFinder.OS var1);
}

