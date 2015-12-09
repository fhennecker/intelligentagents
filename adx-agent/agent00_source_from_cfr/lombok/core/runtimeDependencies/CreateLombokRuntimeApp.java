/*
 * Decompiled with CFR 0_110.
 */
package lombok.core.runtimeDependencies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import lombok.core.LombokApp;
import lombok.core.SpiLoadUtil;
import lombok.core.runtimeDependencies.RuntimeDependencyInfo;
import lombok.libs.com.zwitserloot.cmdreader.CmdReader;
import lombok.libs.com.zwitserloot.cmdreader.Description;
import lombok.libs.com.zwitserloot.cmdreader.InvalidCommandLineException;
import lombok.libs.com.zwitserloot.cmdreader.Mandatory;
import lombok.libs.com.zwitserloot.cmdreader.Requires;
import lombok.libs.com.zwitserloot.cmdreader.Shorthand;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CreateLombokRuntimeApp
extends LombokApp {
    private List<RuntimeDependencyInfo> infoObjects;

    @Override
    public String getAppName() {
        return "createRuntime";
    }

    @Override
    public String getAppDescription() {
        return "Creates a small lombok-runtime.jar with the runtime\ndependencies of all lombok transformations that have them,\nand prints the names of each lombok transformation that\nrequires the lombok-runtime.jar at runtime.";
    }

    @Override
    public List<String> getAppAliases() {
        return Arrays.asList("runtime");
    }

    @Override
    public int runApp(List<String> rawArgs) throws Exception {
        CmdReader<CmdArgs> reader = CmdReader.of(CmdArgs.class);
        try {
            CmdArgs args = (CmdArgs)reader.make(rawArgs.toArray(new String[0]));
        }
        catch (InvalidCommandLineException e) {
            this.printHelp(reader, e.getMessage(), System.err);
            return 1;
        }
        if (args.help) {
            this.printHelp(reader, null, System.out);
            return 0;
        }
        this.initializeInfoObjects();
        if (args.print) {
            this.printRuntimeDependents();
        }
        int errCode = 0;
        if (args.create) {
            File out = new File("./lombok-runtime.jar");
            if (args.output != null && (out = new File(args.output)).isDirectory()) {
                out = new File(out, "lombok-runtime.jar");
            }
            try {
                errCode = this.writeRuntimeJar(out);
            }
            catch (Exception e) {
                System.err.println("ERROR: Creating " + CreateLombokRuntimeApp.canonical(out) + " failed: ");
                e.printStackTrace();
                return 1;
            }
        }
        return errCode;
    }

    private void printRuntimeDependents() {
        ArrayList<String> descriptions = new ArrayList<String>();
        for (RuntimeDependencyInfo info : this.infoObjects) {
            descriptions.addAll(info.getRuntimeDependentsDescriptions());
        }
        if (descriptions.isEmpty()) {
            System.out.println("Not printing dependents: No lombok transformations currently have any runtime dependencies!");
        } else {
            System.out.println("Using any of these lombok features means your app will need lombok-runtime.jar:");
            for (String desc : descriptions) {
                System.out.println(desc);
            }
        }
    }

    private int writeRuntimeJar(File outFile) throws Exception {
        LinkedHashMap deps = new LinkedHashMap();
        for (RuntimeDependencyInfo info : this.infoObjects) {
            List<String> depNames = info.getRuntimeDependencies();
            if (depNames == null) continue;
            for (String depName : depNames) {
                if (deps.containsKey(depName)) continue;
                deps.put(depName, info.getClass());
            }
        }
        if (deps.isEmpty()) {
            System.out.println("Not generating lombok-runtime.jar: No lombok transformations currently have any runtime dependencies!");
            return 1;
        }
        FileOutputStream out = new FileOutputStream(outFile);
        boolean success = false;
        try {
            JarOutputStream jar = new JarOutputStream(out);
            deps.put("LICENSE", CreateLombokRuntimeApp.class);
            deps.put("AUTHORS", CreateLombokRuntimeApp.class);
            for (Map.Entry dep : deps.entrySet()) {
                InputStream in = ((Class)dep.getValue()).getResourceAsStream("/" + (String)dep.getKey());
                try {
                    if (in == null) {
                        throw new Fail(String.format("Dependency %s contributed by %s cannot be found", dep.getKey(), dep.getValue()));
                    }
                    this.writeIntoJar(jar, (String)dep.getKey(), in);
                    continue;
                }
                finally {
                    if (in == null) continue;
                    in.close();
                    continue;
                }
            }
            jar.close();
            out.close();
            System.out.println("Successfully created: " + CreateLombokRuntimeApp.canonical(outFile));
            return 0;
        }
        catch (Throwable t) {
            try {
                out.close();
            }
            catch (Throwable ignore) {
                // empty catch block
            }
            if (!success) {
                outFile.delete();
            }
            if (t instanceof Fail) {
                System.err.println(t.getMessage());
                return 1;
            }
            if (t instanceof Exception) {
                throw (Exception)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new Exception(t);
        }
    }

    private void writeIntoJar(JarOutputStream jar, String depName, InputStream in) throws IOException {
        int r;
        jar.putNextEntry(new ZipEntry(depName));
        byte[] b = new byte[65536];
        while ((r = in.read(b)) != -1) {
            jar.write(b, 0, r);
        }
        jar.closeEntry();
        in.close();
    }

    private void initializeInfoObjects() throws IOException {
        this.infoObjects = SpiLoadUtil.readAllFromIterator(SpiLoadUtil.findServices(RuntimeDependencyInfo.class));
    }

    private static String canonical(File out) {
        try {
            return out.getCanonicalPath();
        }
        catch (Exception e) {
            return out.getAbsolutePath();
        }
    }

    private void printHelp(CmdReader<CmdArgs> reader, String message, PrintStream out) {
        if (message != null) {
            out.println(message);
            out.println("----------------------------");
        }
        out.println(reader.generateCommandLineHelp("java -jar lombok.jar createRuntime"));
    }

    private static class Fail
    extends Exception {
        Fail(String message) {
            super(message);
        }
    }

    private static class CmdArgs {
        @Shorthand(value={"p"})
        @Description(value="Prints those lombok transformations that require lombok-runtime.jar.")
        @Mandatory(onlyIfNot={"create"})
        boolean print;
        @Shorthand(value={"c"})
        @Description(value="Creates the lombok-runtime.jar.")
        @Mandatory(onlyIfNot={"print"})
        boolean create;
        @Shorthand(value={"o"})
        @Description(value="Where to write the lombok-runtime.jar. Defaults to the current working directory.")
        @Requires(value={"create"})
        String output;
        @Description(value="Shows this help text")
        boolean help;

        private CmdArgs() {
        }
    }

}

