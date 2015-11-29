/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeFinder;
import lombok.installer.IdeLocation;
import lombok.installer.IdeLocationProvider;
import lombok.installer.eclipse.EclipseLocation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EclipseLocationProvider
implements IdeLocationProvider {
    @Override
    public IdeLocation create(String path) throws CorruptedIdeLocationException {
        return this.create0(path);
    }

    protected List<String> getEclipseExecutableNames() {
        return Arrays.asList("eclipse.app", "eclipse.exe", "eclipse");
    }

    protected String getIniName() {
        return "eclipse.ini";
    }

    protected IdeLocation makeLocation(String name, File ini) throws CorruptedIdeLocationException {
        return new EclipseLocation(name, ini);
    }

    protected String getMacAppName() {
        return "Eclipse.app";
    }

    protected String getUnixAppName() {
        return "eclipse";
    }

    protected IdeLocation create0(String path) throws CorruptedIdeLocationException {
        if (path == null) {
            throw new NullPointerException("path");
        }
        File p = new File(path);
        if (!p.exists()) {
            return null;
        }
        if (p.isDirectory()) {
            for (String possibleExeName : this.getEclipseExecutableNames()) {
                File f = new File(p, possibleExeName);
                if (!f.exists()) continue;
                return this.findEclipseIniFromExe(f, 0);
            }
            File f = new File(p, this.getIniName());
            if (f.exists()) {
                return new EclipseLocation(IdeLocation.canonical(p), f);
            }
        }
        if (p.isFile() && p.getName().equalsIgnoreCase(this.getIniName())) {
            return new EclipseLocation(IdeLocation.canonical(p.getParentFile()), p);
        }
        if (this.getEclipseExecutableNames().contains(p.getName().toLowerCase())) {
            return this.findEclipseIniFromExe(p, 0);
        }
        return null;
    }

    private IdeLocation findEclipseIniFromExe(File exePath, int loopCounter) throws CorruptedIdeLocationException {
        block15 : {
            File ini = new File(exePath.getParentFile(), this.getIniName());
            if (ini.isFile()) {
                return this.makeLocation(IdeLocation.canonical(exePath), ini);
            }
            ini = new File(exePath.getParentFile(), this.getMacAppName() + "/Contents/MacOS/" + this.getIniName());
            if (ini.isFile()) {
                return this.makeLocation(IdeLocation.canonical(exePath), ini);
            }
            if (loopCounter < 50) {
                try {
                    String oPath = exePath.getAbsolutePath();
                    String nPath = exePath.getCanonicalPath();
                    if (oPath.equals(nPath)) break block15;
                    try {
                        IdeLocation loc = this.findEclipseIniFromExe(new File(nPath), loopCounter + 1);
                        if (loc != null) {
                            return loc;
                        }
                    }
                    catch (CorruptedIdeLocationException ignore) {
                    }
                }
                catch (IOException ignore) {
                    // empty catch block
                }
            }
        }
        String path = exePath.getAbsolutePath();
        try {
            path = exePath.getCanonicalPath();
        }
        catch (IOException ignore) {
            // empty catch block
        }
        if (path.equals("/usr/bin/" + this.getUnixAppName()) || path.equals("/bin/" + this.getUnixAppName()) || path.equals("/usr/local/bin/" + this.getUnixAppName())) {
            File ini = new File("/usr/lib/" + this.getUnixAppName() + "/" + this.getIniName());
            if (ini.isFile()) {
                return this.makeLocation(path, ini);
            }
            ini = new File("/usr/local/lib/" + this.getUnixAppName() + "/" + this.getIniName());
            if (ini.isFile()) {
                return this.makeLocation(path, ini);
            }
            ini = new File("/usr/local/etc/" + this.getUnixAppName() + "/" + this.getIniName());
            if (ini.isFile()) {
                return this.makeLocation(path, ini);
            }
            ini = new File("/etc/" + this.getIniName());
            if (ini.isFile()) {
                return this.makeLocation(path, ini);
            }
        }
        return null;
    }

    @Override
    public Pattern getLocationSelectors(IdeFinder.OS os) {
        switch (os) {
            case MAC_OS_X: {
                return Pattern.compile("^(eclipse|eclipse\\.ini|eclipse\\.app)$", 2);
            }
            case WINDOWS: {
                return Pattern.compile("^(eclipse\\.exe|eclipse\\.ini)$", 2);
            }
        }
        return Pattern.compile("^(eclipse|eclipse\\.ini)$", 2);
    }

}

