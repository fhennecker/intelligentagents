/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer.eclipse;

import java.util.Arrays;
import java.util.List;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeLocation;
import lombok.installer.eclipse.EclipseFinder;
import lombok.installer.eclipse.STSLocationProvider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class STSFinder
extends EclipseFinder {
    @Override
    protected IdeLocation createLocation(String guess) throws CorruptedIdeLocationException {
        return new STSLocationProvider().create0(guess);
    }

    @Override
    protected String getDirName() {
        return "sts";
    }

    @Override
    protected String getMacExecutableName() {
        return "STS.app";
    }

    @Override
    protected String getUnixExecutableName() {
        return "STS";
    }

    @Override
    protected String getWindowsExecutableName() {
        return "STS.exe";
    }

    @Override
    protected List<String> getSourceDirsOnWindows() {
        return Arrays.asList("\\", "\\springsource", "\\Program Files", "\\Program Files\\springsource", System.getProperty("user.home", "."), System.getProperty("user.home", ".") + "\\springsource");
    }

    @Override
    protected List<String> getSourceDirsOnMac() {
        return Arrays.asList("/Applications", "/Applications/springsource", System.getProperty("user.home", "."), System.getProperty("user.home", ".") + "/springsource");
    }

    @Override
    protected List<String> getSourceDirsOnUnix() {
        return Arrays.asList(System.getProperty("user.home", "."), System.getProperty("user.home", ".") + "/springsource");
    }
}

