/*
 * Decompiled with CFR 0_110.
 */
package lombok.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.core.DiagnosticsReceiver;
import lombok.core.LombokApp;
import lombok.core.PostCompiler;
import lombok.libs.com.zwitserloot.cmdreader.CmdReader;
import lombok.libs.com.zwitserloot.cmdreader.Description;
import lombok.libs.com.zwitserloot.cmdreader.InvalidCommandLineException;
import lombok.libs.com.zwitserloot.cmdreader.Mandatory;
import lombok.libs.com.zwitserloot.cmdreader.Sequential;
import lombok.libs.com.zwitserloot.cmdreader.Shorthand;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PostCompilerApp
extends LombokApp {
    @Override
    public List<String> getAppAliases() {
        return Arrays.asList("post", "postcompile");
    }

    @Override
    public String getAppDescription() {
        return "Runs registered post compiler handlers to against existing class files, modifying them in the process.";
    }

    @Override
    public String getAppName() {
        return "post-compile";
    }

    @Override
    public int runApp(List<String> raw) throws Exception {
        CmdArgs args;
        CmdReader reader = CmdReader.of(CmdArgs.class);
        try {
            args = (CmdArgs)reader.make(raw.toArray(new String[0]));
            if (args.help) {
                System.out.println(reader.generateCommandLineHelp("java -jar lombok.jar post-compile"));
                return 0;
            }
        }
        catch (InvalidCommandLineException e) {
            System.err.println(e.getMessage());
            System.err.println(reader.generateCommandLineHelp("java -jar lombok.jar post-compile"));
            return 1;
        }
        int filesVisited = 0;
        int filesTouched = 0;
        for (File file : PostCompilerApp.cmdArgsToFiles(args.classFiles)) {
            byte[] clone;
            byte[] original;
            byte[] transformed;
            if (!file.exists() || !file.isFile()) {
                System.out.printf("Cannot find file '%s'\n", file);
                continue;
            }
            ++filesVisited;
            if (args.verbose) {
                System.out.println("Processing " + file.getAbsolutePath());
            }
            if ((clone = (byte[])(original = PostCompilerApp.readFile(file)).clone()) == (transformed = PostCompiler.applyTransformations(clone, file.toString(), DiagnosticsReceiver.CONSOLE)) || Arrays.equals(clone, transformed)) continue;
            ++filesTouched;
            if (args.verbose) {
                System.out.println("Rewriting " + file.getAbsolutePath());
            }
            PostCompilerApp.writeFile(file, transformed);
        }
        if (args.verbose) {
            System.out.printf("Total files visited: %d total files changed: %d\n", filesVisited, filesTouched);
        }
        return filesVisited == 0 ? 1 : 0;
    }

    static List<File> cmdArgsToFiles(List<String> fileNames) {
        ArrayList<File> filesToProcess = new ArrayList<File>();
        for (String f : fileNames) {
            PostCompilerApp.addFiles(filesToProcess, f);
        }
        return filesToProcess;
    }

    static void addFiles(List<File> filesToProcess, String f) {
        File file = new File(f);
        if (file.isDirectory()) {
            PostCompilerApp.addRecursively(filesToProcess, file);
        } else {
            filesToProcess.add(file);
        }
    }

    static void addRecursively(List<File> filesToProcess, File file) {
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                PostCompilerApp.addRecursively(filesToProcess, f);
                continue;
            }
            if (!f.getName().endsWith(".class")) continue;
            filesToProcess.add(f);
        }
    }

    static byte[] readFile(File file) throws IOException {
        int read;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file);
        while ((read = fileInputStream.read(buffer)) != -1) {
            bytes.write(buffer, 0, read);
        }
        fileInputStream.close();
        return bytes.toByteArray();
    }

    static void writeFile(File file, byte[] transformed) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(transformed);
        out.close();
    }

    public static class CmdArgs {
        @Sequential
        @Mandatory
        @Description(value="paths to class files to be converted. If a directory is named, all files (recursively) in that directory will be converted.")
        private List<String> classFiles = new ArrayList<String>();
        @Shorthand(value={"v"})
        @Description(value="Prints lots of status information as the post compiler runs")
        boolean verbose = false;
        @Shorthand(value={"h", "?"})
        @Description(value="Shows this help text")
        boolean help = false;
    }

}

