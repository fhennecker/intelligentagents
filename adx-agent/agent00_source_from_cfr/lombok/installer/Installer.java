/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import lombok.Lombok;
import lombok.core.LombokApp;
import lombok.core.SpiLoadUtil;
import lombok.core.Version;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeFinder;
import lombok.installer.IdeLocation;
import lombok.installer.IdeLocationProvider;
import lombok.installer.InstallException;
import lombok.installer.InstallerGUI;
import lombok.installer.UninstallException;
import lombok.libs.com.zwitserloot.cmdreader.CmdReader;
import lombok.libs.com.zwitserloot.cmdreader.Description;
import lombok.libs.com.zwitserloot.cmdreader.InvalidCommandLineException;
import lombok.libs.com.zwitserloot.cmdreader.Sequential;
import lombok.libs.com.zwitserloot.cmdreader.Shorthand;
import lombok.patcher.inject.LiveInjector;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Installer {
    static final URI ABOUT_LOMBOK_URL = URI.create("http://projectlombok.org");
    static final List<IdeLocationProvider> locationProviders;

    static List<Pattern> getIdeExecutableNames() {
        IdeFinder.OS os = IdeFinder.getOS();
        ArrayList<Pattern> list = new ArrayList<Pattern>();
        for (IdeLocationProvider provider : locationProviders) {
            Pattern p = provider.getLocationSelectors(os);
            if (p == null) continue;
            list.add(p);
        }
        return list;
    }

    static IdeLocation tryAllProviders(String location) throws CorruptedIdeLocationException {
        for (IdeLocationProvider provider : locationProviders) {
            IdeLocation loc = provider.create(location);
            if (loc == null) continue;
            return loc;
        }
        return null;
    }

    static void autoDiscover(List<IdeLocation> locations, List<CorruptedIdeLocationException> problems) {
        try {
            for (IdeFinder finder : SpiLoadUtil.findServices(IdeFinder.class)) {
                finder.findIdes(locations, problems);
            }
        }
        catch (IOException e) {
            throw Lombok.sneakyThrow(e);
        }
    }

    public static boolean isSelf(String jar) {
        String self = LiveInjector.findPathJar(Installer.class);
        if (self == null) {
            return false;
        }
        File a = new File(jar).getAbsoluteFile();
        File b = new File(self).getAbsoluteFile();
        try {
            a = a.getCanonicalFile();
        }
        catch (IOException ignore) {
            // empty catch block
        }
        try {
            b = b.getCanonicalFile();
        }
        catch (IOException ignore) {
            // empty catch block
        }
        return a.equals(b);
    }

    private static int guiInstaller() {
        if (IdeFinder.getOS() == IdeFinder.OS.MAC_OS_X) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Lombok Installer");
            System.setProperty("com.apple.macos.use-file-dialog-packages", "true");
        }
        try {
            SwingUtilities.invokeLater(new Runnable(){

                public void run() {
                    try {
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        }
                        catch (Exception ignore) {
                            // empty catch block
                        }
                        new InstallerGUI().show();
                    }
                    catch (HeadlessException e) {
                        Installer.printHeadlessInfo();
                    }
                }
            });
            AtomicReference<Integer> atomicReference = InstallerGUI.exitMarker;
            synchronized (atomicReference) {
                while (!Thread.interrupted() && InstallerGUI.exitMarker.get() == null) {
                    try {
                        InstallerGUI.exitMarker.wait();
                        continue;
                    }
                    catch (InterruptedException e) {
                        return 1;
                    }
                }
                // MONITOREXIT [0, 1, 3] lbl16 : MonitorExitStatement: MONITOREXIT : var0
                Integer errCode = InstallerGUI.exitMarker.get();
                return errCode == null ? 1 : errCode;
            }
        }
        catch (HeadlessException e) {
            Installer.printHeadlessInfo();
            return 1;
        }
    }

    public static int cliInstaller(boolean uninstall, List<String> rawArgs) {
        CmdReader<CmdArgs> reader = CmdReader.of(CmdArgs.class);
        try {
            CmdArgs args = (CmdArgs)reader.make(rawArgs.toArray(new String[0]));
        }
        catch (InvalidCommandLineException e) {
            System.err.println(e.getMessage());
            System.err.println("--------------------------");
            System.err.println(Installer.generateCliHelp(uninstall, reader));
            return 1;
        }
        if (args.help) {
            System.out.println(Installer.generateCliHelp(uninstall, reader));
            return 0;
        }
        if (args.path.isEmpty()) {
            System.err.println("ERROR: Nothing to do!");
            System.err.println("--------------------------");
            System.err.println(Installer.generateCliHelp(uninstall, reader));
            return 1;
        }
        ArrayList<IdeLocation> locations = new ArrayList<IdeLocation>();
        ArrayList<CorruptedIdeLocationException> problems = new ArrayList<CorruptedIdeLocationException>();
        if (args.path.contains("auto")) {
            Installer.autoDiscover(locations, problems);
        }
        for (String rawPath : args.path) {
            if (rawPath.equals("auto")) continue;
            try {
                IdeLocation loc = Installer.tryAllProviders(rawPath);
                if (loc != null) {
                    locations.add(loc);
                    continue;
                }
                problems.add(new CorruptedIdeLocationException("Can't find any IDE at: " + rawPath, null, null));
            }
            catch (CorruptedIdeLocationException e) {
                problems.add(e);
            }
        }
        int validLocations = locations.size();
        for (IdeLocation loc : locations) {
            try {
                if (uninstall) {
                    loc.uninstall();
                } else {
                    loc.install();
                }
                Object[] arrobject = new Object[3];
                arrobject[0] = uninstall ? "uninstalled" : "installed";
                arrobject[1] = uninstall ? "from" : "to";
                arrobject[2] = loc.getName();
                System.out.printf("Lombok %s %s: %s\n", arrobject);
            }
            catch (InstallException e) {
                if (e.isWarning()) {
                    System.err.printf("Warning while installing at %s:\n", loc.getName());
                } else {
                    System.err.printf("Installation at %s failed:\n", loc.getName());
                    --validLocations;
                }
                System.err.println(e.getMessage());
            }
            catch (UninstallException e) {
                if (e.isWarning()) {
                    System.err.printf("Warning while uninstalling at %s:\n", loc.getName());
                } else {
                    System.err.printf("Uninstall at %s failed:\n", loc.getName());
                    --validLocations;
                }
                System.err.println(e.getMessage());
            }
        }
        for (CorruptedIdeLocationException problem : problems) {
            System.err.println("WARNING: " + problem.getMessage());
        }
        if (validLocations == 0) {
            System.err.println("WARNING: Zero valid locations found; so nothing was done!");
        }
        return 0;
    }

    private static String generateCliHelp(boolean uninstall, CmdReader<CmdArgs> reader) {
        return reader.generateCommandLineHelp("java -jar lombok.jar " + (uninstall ? "uninstall" : "install"));
    }

    private static void printHeadlessInfo() {
        System.out.printf("About lombok v%s\nLombok makes java better by providing very spicy additions to the Java programming language,such as using @Getter to automatically generate a getter method for any field.\n\nBrowse to %s for more information. To install lombok on Eclipse, re-run this jar file on a graphical computer system - this message is being shown because your terminal is not graphics capable.\nAlternatively, use the command line installer (java -jar lombok.jar install --help).\nIf you are just using 'javac' or a tool that calls on javac, no installation is neccessary; just make sure lombok.jar is in the classpath when you compile. Example:\n\n   java -cp lombok.jar MyCode.java\n", Version.getVersion(), ABOUT_LOMBOK_URL);
    }

    static {
        ArrayList<IdeLocationProvider> list = new ArrayList<IdeLocationProvider>();
        try {
            for (IdeLocationProvider provider : SpiLoadUtil.findServices(IdeLocationProvider.class)) {
                list.add(provider);
            }
        }
        catch (IOException e) {
            throw Lombok.sneakyThrow(e);
        }
        locationProviders = Collections.unmodifiableList(list);
    }

    private static class CmdArgs {
        @Description(value="Specify paths to a location to install/uninstall. Use 'auto' to apply to all automatically discoverable installations.")
        @Sequential
        List<String> path = new ArrayList<String>();
        @Shorthand(value={"h", "?"})
        @Description(value="Shows this help text")
        boolean help;

        private CmdArgs() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class CommandLineUninstallerApp
    extends LombokApp {
        @Override
        public String getAppName() {
            return "uninstall";
        }

        @Override
        public String getAppDescription() {
            return "Runs the 'handsfree' command line scriptable uninstaller.";
        }

        @Override
        public int runApp(List<String> args) throws Exception {
            return Installer.cliInstaller(true, args);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class CommandLineInstallerApp
    extends LombokApp {
        @Override
        public String getAppName() {
            return "install";
        }

        @Override
        public String getAppDescription() {
            return "Runs the 'handsfree' command line scriptable installer.";
        }

        @Override
        public int runApp(List<String> args) throws Exception {
            return Installer.cliInstaller(false, args);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class GraphicalInstallerApp
    extends LombokApp {
        @Override
        public String getAppName() {
            return "installer";
        }

        @Override
        public String getAppDescription() {
            return "Runs the graphical installer tool (default).";
        }

        @Override
        public List<String> getAppAliases() {
            return Arrays.asList("");
        }

        @Override
        public int runApp(List<String> args) throws Exception {
            return Installer.guiInstaller();
        }
    }

}

