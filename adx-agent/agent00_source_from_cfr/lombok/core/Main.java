/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.core.LombokApp;
import lombok.core.SpiLoadUtil;
import lombok.core.Version;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Main {
    private static final Collection<?> HELP_SWITCHES = Collections.unmodifiableList(Arrays.asList("/?", "/h", "/help", "-h", "-help", "--help", "help", "h"));
    private final List<LombokApp> apps;
    private final List<String> args;

    public static void main(String[] args) throws IOException {
        int err = new Main(SpiLoadUtil.readAllFromIterator(SpiLoadUtil.findServices(LombokApp.class)), Arrays.asList(args)).go();
        System.exit(err);
    }

    public Main(List<LombokApp> apps, List<String> args) {
        this.apps = apps;
        this.args = args;
    }

    public int go() {
        String command;
        if (!this.args.isEmpty() && HELP_SWITCHES.contains(this.args.get(0))) {
            this.printHelp(null, System.out);
            return 0;
        }
        String string = command = this.args.isEmpty() ? "" : this.args.get(0).trim();
        if (command.startsWith("--")) {
            command = command.substring(2);
        } else if (command.startsWith("-")) {
            command = command.substring(1);
        }
        List<String> subArgs = this.args.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(this.args.subList(1, this.args.size()));
        for (LombokApp app : this.apps) {
            if (!app.getAppName().equals(command) && !app.getAppAliases().contains(command)) continue;
            try {
                return app.runApp(subArgs);
            }
            catch (Exception e) {
                e.printStackTrace();
                return 5;
            }
        }
        this.printHelp("Unknown command: " + command, System.err);
        return 1;
    }

    public void printHelp(String message, PrintStream out) {
        if (message != null) {
            out.println(message);
            out.println("------------------------------");
        }
        out.println("projectlombok.org " + Version.getFullVersion());
        out.println("Copyright (C) 2009-2012 The Project Lombok Authors.");
        out.println("Run 'lombok license' to see the lombok license agreement.");
        out.println();
        out.println("Run lombok without any parameters to start the graphical installer.");
        out.println("Other available commands:");
        for (LombokApp app : this.apps) {
            if (app.isDebugTool()) continue;
            String[] desc = app.getAppDescription().split("\n");
            for (int i = 0; i < desc.length; ++i) {
                Object[] arrobject = new Object[2];
                arrobject[0] = i == 0 ? app.getAppName() : "";
                arrobject[1] = desc[i];
                out.printf("  %15s    %s\n", arrobject);
            }
        }
        out.println();
        out.println("Run lombok commandName --help for more info on each command.");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class LicenseApp
    extends LombokApp {
        @Override
        public String getAppName() {
            return "license";
        }

        @Override
        public String getAppDescription() {
            return "prints license information.";
        }

        @Override
        public List<String> getAppAliases() {
            return Arrays.asList("licence", "copyright", "copyleft", "gpl");
        }

        @Override
        public int runApp(List<String> args) {
            int r;
            InputStream in = Main.class.getResourceAsStream("/LICENSE");
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] b = new byte[65536];
                while ((r = in.read(b)) != -1) {
                    out.write(b, 0, r);
                }
                System.out.println(new String(out.toByteArray()));
                r = 0;
            }
            catch (Throwable var6_7) {
                try {
                    in.close();
                    throw var6_7;
                }
                catch (Exception e) {
                    System.err.println("License file not found. Check http://projectlombok.org/LICENSE");
                    return 1;
                }
            }
            in.close();
            return r;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class VersionApp
    extends LombokApp {
        @Override
        public String getAppName() {
            return "version";
        }

        @Override
        public String getAppDescription() {
            return "prints lombok's version.";
        }

        @Override
        public List<String> getAppAliases() {
            return Arrays.asList("-version", "--version");
        }

        @Override
        public int runApp(List<String> args) {
            System.out.println(Version.getFullVersion());
            return 0;
        }
    }

}

