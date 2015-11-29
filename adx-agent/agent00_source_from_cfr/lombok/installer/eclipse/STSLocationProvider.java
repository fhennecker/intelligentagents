/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer.eclipse;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeFinder;
import lombok.installer.IdeLocation;
import lombok.installer.eclipse.EclipseLocationProvider;
import lombok.installer.eclipse.STSLocation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class STSLocationProvider
extends EclipseLocationProvider {
    @Override
    protected List<String> getEclipseExecutableNames() {
        return Arrays.asList("sts.app", "sts.exe", "stsc.exe", "sts");
    }

    @Override
    protected String getIniName() {
        return "STS.ini";
    }

    @Override
    protected IdeLocation makeLocation(String name, File ini) throws CorruptedIdeLocationException {
        return new STSLocation(name, ini);
    }

    @Override
    protected String getMacAppName() {
        return "STS.app";
    }

    @Override
    protected String getUnixAppName() {
        return "STS";
    }

    @Override
    public Pattern getLocationSelectors(IdeFinder.OS os) {
        switch (os) {
            case MAC_OS_X: {
                return Pattern.compile("^(sts|sts\\.ini|sts\\.app)$", 2);
            }
            case WINDOWS: {
                return Pattern.compile("^(stsc?\\.exe|sts\\.ini)$", 2);
            }
        }
        return Pattern.compile("^(sts|sts\\.ini)$", 2);
    }

}

