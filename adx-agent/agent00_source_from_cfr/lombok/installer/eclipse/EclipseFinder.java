/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer.eclipse;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeFinder;
import lombok.installer.IdeLocation;
import lombok.installer.eclipse.EclipseLocationProvider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EclipseFinder
extends IdeFinder {
    protected String getDirName() {
        return "eclipse";
    }

    protected String getWindowsExecutableName() {
        return "eclipse.exe";
    }

    protected String getUnixExecutableName() {
        return "eclipse";
    }

    protected String getMacExecutableName() {
        return "Eclipse.app";
    }

    protected IdeLocation createLocation(String guess) throws CorruptedIdeLocationException {
        return new EclipseLocationProvider().create0(guess);
    }

    protected List<String> getSourceDirsOnWindows() {
        return Arrays.asList("\\", "\\Program Files", "\\Program Files (x86)", System.getProperty("user.home", "."));
    }

    private List<String> getSourceDirsOnWindowsWithDriveLetters() {
        List<String> driveLetters = Arrays.asList("C");
        try {
            driveLetters = EclipseFinder.getDrivesOnWindows();
        }
        catch (Throwable ignore) {
            ignore.printStackTrace();
        }
        ArrayList<String> sourceDirs = new ArrayList<String>();
        for (String letter : driveLetters) {
            for (String possibleSource : this.getSourceDirsOnWindows()) {
                sourceDirs.add(letter + ":" + possibleSource);
            }
        }
        return sourceDirs;
    }

    protected List<String> getSourceDirsOnMac() {
        return Arrays.asList("/Applications", System.getProperty("user.home", "."));
    }

    protected List<String> getSourceDirsOnUnix() {
        return Arrays.asList(System.getProperty("user.home", "."));
    }

    private List<File> transformToFiles(List<String> fileNames) {
        ArrayList<File> files = new ArrayList<File>();
        for (String fileName : fileNames) {
            files.add(new File(fileName));
        }
        return files;
    }

    private List<File> getFlatSourceLocationsOnUnix() {
        ArrayList<File> dirs = new ArrayList<File>();
        dirs.add(new File("/usr/bin/"));
        dirs.add(new File("/usr/local/bin/"));
        dirs.add(new File(System.getProperty("user.home", "."), "bin/"));
        return dirs;
    }

    private List<File> getNestedSourceLocationOnUnix() {
        ArrayList<File> dirs = new ArrayList<File>();
        dirs.add(new File("/usr/local/share"));
        dirs.add(new File("/usr/local"));
        dirs.add(new File("/usr/share"));
        return dirs;
    }

    @Override
    public void findIdes(List<IdeLocation> locations, List<CorruptedIdeLocationException> problems) {
        switch (EclipseFinder.getOS()) {
            case WINDOWS: {
                new WindowsFinder().findEclipse(locations, problems);
                break;
            }
            case MAC_OS_X: {
                new MacFinder().findEclipse(locations, problems);
                break;
            }
            default: {
                new UnixFinder().findEclipse(locations, problems);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private abstract class DirectoryFinder {
        private final List<File> flatSourceDirs;
        private final List<File> nestedSourceDirs;

        DirectoryFinder(List<File> nestedSourceDirs, List<File> flatSourceDirs) {
            this.nestedSourceDirs = nestedSourceDirs;
            this.flatSourceDirs = flatSourceDirs;
        }

        public void findEclipse(List<IdeLocation> locations, List<CorruptedIdeLocationException> problems) {
            for (File dir2 : this.nestedSourceDirs) {
                this.recurseDirectory(locations, problems, dir2);
            }
            for (File dir2 : this.flatSourceDirs) {
                this.findEclipse(locations, problems, dir2);
            }
        }

        protected abstract String findEclipseOnPlatform(File var1);

        protected void recurseDirectory(List<IdeLocation> locations, List<CorruptedIdeLocationException> problems, File dir) {
            this.recurseDirectory0(locations, problems, dir, 0);
        }

        private void recurseDirectory0(List<IdeLocation> locations, List<CorruptedIdeLocationException> problems, File f, int loopCounter) {
            File[] listFiles = f.listFiles();
            if (listFiles == null) {
                return;
            }
            for (File dir : listFiles) {
                if (!dir.isDirectory()) continue;
                try {
                    if (!dir.getName().toLowerCase().contains(EclipseFinder.this.getDirName())) continue;
                    this.findEclipse(locations, problems, dir);
                    if (loopCounter >= 50) continue;
                    this.recurseDirectory0(locations, problems, dir, loopCounter + 1);
                    continue;
                }
                catch (Exception ignore) {
                    // empty catch block
                }
            }
        }

        private void findEclipse(List<IdeLocation> locations, List<CorruptedIdeLocationException> problems, File dir) {
            String eclipseLocation = this.findEclipseOnPlatform(dir);
            if (eclipseLocation != null) {
                try {
                    IdeLocation newLocation = EclipseFinder.this.createLocation(eclipseLocation);
                    if (newLocation != null) {
                        locations.add(newLocation);
                    }
                }
                catch (CorruptedIdeLocationException e) {
                    problems.add(e);
                }
            }
        }
    }

    private class MacFinder
    extends DirectoryFinder {
        MacFinder() {
            super(EclipseFinder.this.transformToFiles(EclipseFinder.this.getSourceDirsOnMac()), Collections.<File>emptyList());
        }

        protected String findEclipseOnPlatform(File dir) {
            if (dir.getName().toLowerCase().equals(EclipseFinder.this.getMacExecutableName().toLowerCase())) {
                return dir.getParent();
            }
            if (dir.getName().toLowerCase().contains(EclipseFinder.this.getDirName()) && new File(dir, EclipseFinder.this.getMacExecutableName()).exists()) {
                return dir.toString();
            }
            return null;
        }
    }

    private class WindowsFinder
    extends DirectoryFinder {
        WindowsFinder() {
            super(EclipseFinder.this.transformToFiles(EclipseFinder.this.getSourceDirsOnWindowsWithDriveLetters()), Collections.<File>emptyList());
        }

        protected String findEclipseOnPlatform(File dir) {
            File possible = new File(dir, EclipseFinder.this.getWindowsExecutableName());
            return possible.isFile() ? dir.getAbsolutePath() : null;
        }
    }

    private class UnixFinder
    extends DirectoryFinder {
        UnixFinder() {
            super(EclipseFinder.this.getNestedSourceLocationOnUnix(), EclipseFinder.this.getFlatSourceLocationsOnUnix());
        }

        protected String findEclipseOnPlatform(File dir) {
            File possible = new File(dir, EclipseFinder.this.getUnixExecutableName());
            return possible.exists() ? possible.getAbsolutePath() : null;
        }
    }

}

