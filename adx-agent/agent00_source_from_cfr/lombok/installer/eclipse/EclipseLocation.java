/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer.eclipse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeFinder;
import lombok.installer.IdeLocation;
import lombok.installer.InstallException;
import lombok.installer.Installer;
import lombok.installer.UninstallException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EclipseLocation
extends IdeLocation {
    private final String name;
    private final File eclipseIniPath;
    private volatile boolean hasLombok;
    private static final String OS_NEWLINE = IdeFinder.getOS().getLineEnding();
    private final Pattern JAVA_AGENT_LINE_MATCHER = Pattern.compile("^\\-javaagent\\:.*lombok.*\\.jar$", 2);
    private final Pattern BOOTCLASSPATH_LINE_MATCHER = Pattern.compile("^\\-Xbootclasspath\\/a\\:(.*lombok.*\\.jar.*)$", 2);

    protected String getTypeName() {
        return "eclipse";
    }

    protected String getIniFileName() {
        return "eclipse.ini";
    }

    EclipseLocation(String nameOfLocation, File pathToEclipseIni) throws CorruptedIdeLocationException {
        this.name = nameOfLocation;
        this.eclipseIniPath = pathToEclipseIni;
        try {
            this.hasLombok = this.checkForLombok(this.eclipseIniPath);
        }
        catch (IOException e) {
            throw new CorruptedIdeLocationException("I can't read the configuration file of the " + this.getTypeName() + " installed at " + this.name + "\n" + "You may need to run this installer with root privileges if you want to modify that " + this.getTypeName() + ".", this.getTypeName(), e);
        }
    }

    public int hashCode() {
        return this.eclipseIniPath.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof EclipseLocation)) {
            return false;
        }
        return ((EclipseLocation)o).eclipseIniPath.equals(this.eclipseIniPath);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasLombok() {
        return this.hasLombok;
    }

    private boolean checkForLombok(File iniFile) throws IOException {
        if (!iniFile.exists()) {
            return false;
        }
        FileInputStream fis = new FileInputStream(iniFile);
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            while ((line = br.readLine()) != null) {
                if (!this.JAVA_AGENT_LINE_MATCHER.matcher(line.trim()).matches()) continue;
                br.close();
                boolean bl = true;
                return bl;
            }
            br.close();
            boolean bl = false;
            return bl;
        }
        finally {
            fis.close();
        }
    }

    private List<File> getUninstallDirs() {
        ArrayList<File> result = new ArrayList<File>();
        File x = new File(this.name);
        if (!x.isDirectory()) {
            x = x.getParentFile();
        }
        if (x.isDirectory()) {
            result.add(x);
        }
        result.add(this.eclipseIniPath.getParentFile());
        return result;
    }

    @Override
    public void uninstall() throws UninstallException {
        ArrayList<File> lombokJarsForWhichCantDeleteSelf = new ArrayList<File>();
        StringBuilder newContents = new StringBuilder();
        if (this.eclipseIniPath.exists()) {
            try {
                FileInputStream fis = new FileInputStream(this.eclipseIniPath);
                try {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    while ((line = br.readLine()) != null) {
                        if (this.JAVA_AGENT_LINE_MATCHER.matcher(line).matches()) continue;
                        Matcher m = this.BOOTCLASSPATH_LINE_MATCHER.matcher(line);
                        if (m.matches()) {
                            StringBuilder elemBuilder = new StringBuilder();
                            elemBuilder.append("-Xbootclasspath/a:");
                            boolean first = true;
                            for (String elem : m.group(1).split(Pattern.quote(File.pathSeparator))) {
                                if (elem.toLowerCase().endsWith("lombok.jar") || elem.toLowerCase().endsWith("lombok.eclipse.agent.jar")) continue;
                                if (first) {
                                    first = false;
                                } else {
                                    elemBuilder.append(File.pathSeparator);
                                }
                                elemBuilder.append(elem);
                            }
                            if (first) continue;
                            newContents.append(elemBuilder.toString()).append(OS_NEWLINE);
                            continue;
                        }
                        newContents.append(line).append(OS_NEWLINE);
                    }
                    br.close();
                }
                finally {
                    fis.close();
                }
                FileOutputStream fos = new FileOutputStream(this.eclipseIniPath);
                try {
                    fos.write(newContents.toString().getBytes());
                }
                finally {
                    fos.close();
                }
            }
            catch (IOException e) {
                throw new UninstallException("Cannot uninstall lombok from " + this.name + EclipseLocation.generateWriteErrorMessage(), e);
            }
        }
        for (File dir : this.getUninstallDirs()) {
            File agentJar;
            File lombokJar = new File(dir, "lombok.jar");
            if (lombokJar.exists() && !lombokJar.delete()) {
                if (IdeFinder.getOS() == IdeFinder.OS.WINDOWS && Installer.isSelf(lombokJar.getAbsolutePath())) {
                    lombokJarsForWhichCantDeleteSelf.add(lombokJar);
                } else {
                    throw new UninstallException("Can't delete " + lombokJar.getAbsolutePath() + EclipseLocation.generateWriteErrorMessage(), null);
                }
            }
            if (!(agentJar = new File(dir, "lombok.eclipse.agent.jar")).exists()) continue;
            agentJar.delete();
        }
        if (!lombokJarsForWhichCantDeleteSelf.isEmpty()) {
            throw new UninstallException(true, String.format("lombok.jar cannot delete itself on windows.\nHowever, lombok has been uncoupled from your %s.\nYou can safely delete this jar file. You can find it at:\n%s", this.getTypeName(), ((File)lombokJarsForWhichCantDeleteSelf.get(0)).getAbsolutePath()), null);
        }
    }

    private static String generateWriteErrorMessage() {
        String osSpecificError;
        switch (IdeFinder.getOS()) {
            default: {
                osSpecificError = ":\nStart terminal, go to the directory with lombok.jar, and run: sudo java -jar lombok.jar";
                break;
            }
            case WINDOWS: {
                osSpecificError = ":\nStart a new cmd (dos box) with admin privileges, go to the directory with lombok.jar, and run: java -jar lombok.jar";
            }
        }
        return ", probably because this installer does not have the access rights.\nTry re-running the installer with administrative privileges" + osSpecificError;
    }

    @Override
    public String install() throws InstallException {
        boolean installSucceeded;
        StringBuilder newContents;
        boolean fullPathRequired;
        File lombokJar;
        fullPathRequired = IdeFinder.getOS() == IdeFinder.OS.UNIX || System.getProperty("lombok.installer.fullpath") != null;
        installSucceeded = false;
        newContents = new StringBuilder();
        lombokJar = new File(this.eclipseIniPath.getParentFile(), "lombok.jar");
        if (!Installer.isSelf(lombokJar.getAbsolutePath())) {
            File ourJar = EclipseLocation.findOurJar();
            byte[] b = new byte[524288];
            boolean readSucceeded = true;
            try {
                FileOutputStream out = new FileOutputStream(lombokJar);
                try {
                    readSucceeded = false;
                    FileInputStream in = new FileInputStream(ourJar);
                    try {
                        int r;
                        while ((r = in.read(b)) != -1) {
                            if (r > 0) {
                                readSucceeded = true;
                            }
                            out.write(b, 0, r);
                        }
                    }
                    finally {
                        in.close();
                    }
                }
                finally {
                    out.close();
                }
            }
            catch (IOException e) {
                try {
                    lombokJar.delete();
                }
                catch (Throwable ignore) {
                    // empty catch block
                }
                if (!readSucceeded) {
                    throw new InstallException("I can't read my own jar file. I think you've found a bug in this installer!\nI suggest you restart it and use the 'what do I do' link, to manually install lombok. Also, tell us about this at:\nhttp://groups.google.com/group/project-lombok - Thanks!", e);
                }
                throw new InstallException("I can't write to your " + this.getTypeName() + " directory at " + this.name + EclipseLocation.generateWriteErrorMessage(), e);
            }
        }
        new File(lombokJar.getParentFile(), "lombok.eclipse.agent.jar").delete();
        try {
            FileInputStream fis = new FileInputStream(this.eclipseIniPath);
            try {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                while ((line = br.readLine()) != null) {
                    if (this.JAVA_AGENT_LINE_MATCHER.matcher(line).matches()) continue;
                    Matcher m = this.BOOTCLASSPATH_LINE_MATCHER.matcher(line);
                    if (m.matches()) {
                        StringBuilder elemBuilder = new StringBuilder();
                        elemBuilder.append("-Xbootclasspath/a:");
                        boolean first = true;
                        for (String elem : m.group(1).split(Pattern.quote(File.pathSeparator))) {
                            if (elem.toLowerCase().endsWith("lombok.jar") || elem.toLowerCase().endsWith("lombok.eclipse.agent.jar")) continue;
                            if (first) {
                                first = false;
                            } else {
                                elemBuilder.append(File.pathSeparator);
                            }
                            elemBuilder.append(elem);
                        }
                        if (first) continue;
                        newContents.append(elemBuilder.toString()).append(OS_NEWLINE);
                        continue;
                    }
                    newContents.append(line).append(OS_NEWLINE);
                }
                br.close();
            }
            finally {
                fis.close();
            }
            String fullPathToLombok = fullPathRequired ? lombokJar.getParentFile().getCanonicalPath() + File.separator : "";
            newContents.append(String.format("-javaagent:%s", EclipseLocation.escapePath(fullPathToLombok + "lombok.jar"))).append(OS_NEWLINE);
            newContents.append(String.format("-Xbootclasspath/a:%s", EclipseLocation.escapePath(fullPathToLombok + "lombok.jar"))).append(OS_NEWLINE);
            FileOutputStream fos = new FileOutputStream(this.eclipseIniPath);
            try {
                fos.write(newContents.toString().getBytes());
            }
            finally {
                fos.close();
            }
            installSucceeded = true;
        }
        catch (IOException e) {
            throw new InstallException("Cannot install lombok at " + this.name + EclipseLocation.generateWriteErrorMessage(), e);
        }
        finally {
            if (!installSucceeded) {
                try {
                    lombokJar.delete();
                }
                catch (Throwable ignore) {}
            }
        }
        if (!installSucceeded) {
            throw new InstallException("I can't find the " + this.getIniFileName() + " file. Is this a real " + this.getTypeName() + " installation?", null);
        }
        return "If you start " + this.getTypeName() + " with a custom -vm parameter, you'll need to add:<br>" + "<code>-vmargs -Xbootclasspath/a:lombok.jar -javaagent:lombok.jar</code><br>" + "as parameter as well.";
    }

    @Override
    public URL getIdeIcon() {
        return EclipseLocation.class.getResource("eclipse.png");
    }

}

