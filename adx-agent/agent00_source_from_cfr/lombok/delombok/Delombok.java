/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.comp.Todo
 *  com.sun.tools.javac.main.JavaCompiler
 *  com.sun.tools.javac.main.OptionName
 *  com.sun.tools.javac.tree.JCTree
 *  com.sun.tools.javac.tree.JCTree$JCCompilationUnit
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.List
 *  com.sun.tools.javac.util.ListBuffer
 */
package lombok.delombok;

import com.sun.tools.javac.comp.Todo;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.main.OptionName;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import lombok.delombok.DelombokResult;
import lombok.delombok.UnicodeEscapeWriter;
import lombok.javac.CommentCatcher;
import lombok.javac.CommentInfo;
import lombok.javac.LombokOptions;
import lombok.javac.apt.Processor;
import lombok.libs.com.zwitserloot.cmdreader.CmdReader;
import lombok.libs.com.zwitserloot.cmdreader.Description;
import lombok.libs.com.zwitserloot.cmdreader.Excludes;
import lombok.libs.com.zwitserloot.cmdreader.InvalidCommandLineException;
import lombok.libs.com.zwitserloot.cmdreader.Mandatory;
import lombok.libs.com.zwitserloot.cmdreader.Sequential;
import lombok.libs.com.zwitserloot.cmdreader.Shorthand;

public class Delombok {
    private Charset charset = Charset.defaultCharset();
    private Context context = new Context();
    private Writer presetWriter;
    private PrintStream feedback = System.err;
    private boolean verbose;
    private boolean noCopy;
    private boolean force = false;
    private String classpath;
    private String sourcepath;
    private LinkedHashMap<File, File> fileToBase = new LinkedHashMap();
    private java.util.List<File> filesToParse = new ArrayList<File>();
    private File output = null;

    public void setWriter(Writer writer) {
        this.presetWriter = writer;
    }

    public static void main(String[] rawArgs) {
        block22 : {
            CmdArgs args;
            CmdReader reader = CmdReader.of(CmdArgs.class);
            try {
                args = (CmdArgs)reader.make(rawArgs);
            }
            catch (InvalidCommandLineException e) {
                System.err.println("ERROR: " + e.getMessage());
                System.err.println(reader.generateCommandLineHelp("delombok"));
                System.exit(1);
                return;
            }
            if (args.help || args.input.isEmpty()) {
                if (!args.help) {
                    System.err.println("ERROR: no files or directories to delombok specified.");
                }
                System.err.println(reader.generateCommandLineHelp("delombok"));
                System.exit(args.help ? 0 : 1);
                return;
            }
            Delombok delombok = new Delombok();
            if (args.quiet) {
                delombok.setFeedback(new PrintStream(new OutputStream(){

                    @Override
                    public void write(int b) throws IOException {
                    }
                }));
            }
            if (args.encoding != null) {
                try {
                    delombok.setCharset(args.encoding);
                }
                catch (UnsupportedCharsetException e) {
                    System.err.println("ERROR: Not a known charset: " + args.encoding);
                    System.exit(1);
                    return;
                }
            }
            if (args.verbose) {
                delombok.setVerbose(true);
            }
            if (args.nocopy) {
                delombok.setNoCopy(true);
            }
            if (args.print) {
                delombok.setOutputToStandardOut();
            } else {
                delombok.setOutput(new File(args.target));
            }
            if (args.classpath != null) {
                delombok.setClasspath(args.classpath);
            }
            if (args.sourcepath != null) {
                delombok.setSourcepath(args.sourcepath);
            }
            try {
                for (String in : args.input) {
                    File f = new File(in).getAbsoluteFile();
                    if (f.isFile()) {
                        delombok.addFile(f.getParentFile(), f.getName());
                        continue;
                    }
                    if (f.isDirectory()) {
                        delombok.addDirectory(f);
                        continue;
                    }
                    if (!f.exists()) {
                        if (args.quiet) continue;
                        System.err.println("WARNING: does not exist - skipping: " + f);
                        continue;
                    }
                    if (args.quiet) continue;
                    System.err.println("WARNING: not a standard file or directory - skipping: " + f);
                }
                delombok.delombok();
            }
            catch (Exception e) {
                if (args.quiet) break block22;
                String msg = e.getMessage();
                if (msg != null && msg.startsWith("DELOMBOK: ")) {
                    System.err.println(msg.substring("DELOMBOK: ".length()));
                } else {
                    e.printStackTrace();
                }
                System.exit(1);
                return;
            }
        }
    }

    public void setCharset(String charsetName) throws UnsupportedCharsetException {
        if (charsetName == null) {
            this.charset = Charset.defaultCharset();
            return;
        }
        this.charset = Charset.forName(charsetName);
    }

    public void setDiagnosticsListener(DiagnosticListener<JavaFileObject> diagnostics) {
        if (diagnostics != null) {
            this.context.put((Class)DiagnosticListener.class, diagnostics);
        }
    }

    public void setForceProcess(boolean force) {
        this.force = force;
    }

    public void setFeedback(PrintStream feedback) {
        this.feedback = feedback;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setNoCopy(boolean noCopy) {
        this.noCopy = noCopy;
    }

    public void setOutput(File dir) {
        if (dir.isFile() || !dir.isDirectory() && dir.getName().endsWith(".java")) {
            throw new IllegalArgumentException("DELOMBOK: delombok will only write to a directory. If you want to delombok a single file, use -p to output to standard output, then redirect this to a file:\ndelombok MyJavaFile.java -p >MyJavaFileDelombok.java");
        }
        this.output = dir;
    }

    public void setOutputToStandardOut() {
        this.output = null;
    }

    public void addDirectory(File base) throws IOException {
        this.addDirectory0(false, base, "", 0);
    }

    public void addDirectory1(boolean copy, File base, String name) throws IOException {
        File f = new File(base, name);
        if (f.isFile()) {
            String extension = Delombok.getExtension(f);
            if (extension.equals("java")) {
                this.addFile(base, name);
            } else if (extension.equals("class")) {
                this.skipClass(name);
            } else {
                this.copy(copy, base, name);
            }
        } else if (!f.exists()) {
            this.feedback.printf("Skipping %s because it does not exist.\n", Delombok.canonical(f));
        } else if (!f.isDirectory()) {
            this.feedback.printf("Skipping %s because it is a special file type.\n", Delombok.canonical(f));
        }
    }

    private void addDirectory0(boolean inHiddenDir, File base, String suffix, int loop) throws IOException {
        File dir;
        File file = dir = suffix.isEmpty() ? base : new File(base, suffix);
        if (dir.isDirectory()) {
            boolean thisDirIsHidden;
            boolean bl = thisDirIsHidden = !inHiddenDir && new File(Delombok.canonical(dir)).getName().startsWith(".");
            if (loop >= 100) {
                this.feedback.printf("Over 100 subdirectories? I'm guessing there's a loop in your directory structure. Skipping: %s\n", suffix);
            } else {
                File[] list = dir.listFiles();
                if (list.length > 0) {
                    if (thisDirIsHidden && !this.noCopy && this.output != null) {
                        this.feedback.printf("Only processing java files (not copying non-java files) in %s because it's a hidden directory.\n", Delombok.canonical(dir));
                    }
                    for (File f : list) {
                        this.addDirectory0(inHiddenDir || thisDirIsHidden, base, suffix + (suffix.isEmpty() ? "" : File.separator) + f.getName(), loop + 1);
                    }
                } else if (!(thisDirIsHidden || this.noCopy || inHiddenDir || this.output == null || suffix.isEmpty())) {
                    File emptyDir = new File(this.output, suffix);
                    emptyDir.mkdirs();
                    if (this.verbose) {
                        this.feedback.printf("Creating empty directory: %s\n", Delombok.canonical(emptyDir));
                    }
                }
            }
        } else {
            this.addDirectory1(!inHiddenDir && !this.noCopy, base, suffix);
        }
    }

    private void skipClass(String fileName) {
        if (this.verbose) {
            this.feedback.printf("Skipping class file: %s\n", fileName);
        }
    }

    private void copy(boolean copy, File base, String fileName) throws IOException {
        if (this.output == null) {
            this.feedback.printf("Skipping resource file: %s\n", fileName);
            return;
        }
        if (!copy) {
            if (this.verbose) {
                this.feedback.printf("Skipping resource file: %s\n", fileName);
            }
            return;
        }
        if (this.verbose) {
            this.feedback.printf("Copying resource file: %s\n", fileName);
        }
        byte[] b = new byte[65536];
        File inFile = new File(base, fileName);
        FileInputStream in = new FileInputStream(inFile);
        try {
            File outFile = new File(this.output, fileName);
            outFile.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(outFile);
            try {
                int r;
                while ((r = in.read(b)) != -1) {
                    out.write(b, 0, r);
                }
            }
            finally {
                out.close();
            }
        }
        finally {
            in.close();
        }
    }

    public void addFile(File base, String fileName) throws IOException {
        if (this.output != null && Delombok.canonical(base).equals(Delombok.canonical(this.output))) {
            throw new IOException("DELOMBOK: Output file and input file refer to the same filesystem location. Specify a separate path for output.");
        }
        File f = new File(base, fileName);
        this.filesToParse.add(f);
        this.fileToBase.put(f, base);
    }

    private static <T> List<T> toJavacList(java.util.List<T> list) {
        List out = List.nil();
        ListIterator<T> li = list.listIterator(list.size());
        while (li.hasPrevious()) {
            out = out.prepend(li.previous());
        }
        return out;
    }

    public boolean delombok() throws IOException {
        LombokOptions options = LombokOptions.replaceWithDelombokOptions(this.context);
        options.put(OptionName.ENCODING, this.charset.name());
        if (this.classpath != null) {
            options.put(OptionName.CLASSPATH, this.classpath);
        }
        if (this.sourcepath != null) {
            options.put(OptionName.SOURCEPATH, this.sourcepath);
        }
        options.put("compilePolicy", "attr");
        CommentCatcher catcher = CommentCatcher.create(this.context);
        JavaCompiler compiler = catcher.getCompiler();
        ArrayList<JCTree.JCCompilationUnit> roots = new ArrayList<JCTree.JCCompilationUnit>();
        IdentityHashMap<JCTree.JCCompilationUnit, File> baseMap = new IdentityHashMap<JCTree.JCCompilationUnit, File>();
        compiler.initProcessAnnotations(Collections.singleton(new Processor()));
        for (File fileToParse : this.filesToParse) {
            JCTree.JCCompilationUnit unit = compiler.parse(fileToParse.getAbsolutePath());
            baseMap.put(unit, this.fileToBase.get(fileToParse));
            roots.add(unit);
        }
        if (compiler.errorCount() > 0) {
            return false;
        }
        JavaCompiler delegate = compiler.processAnnotations(compiler.enterTrees(Delombok.toJavacList(roots)));
        delegate.flow(delegate.attribute((ListBuffer)delegate.todo));
        for (JCTree.JCCompilationUnit unit : roots) {
            DelombokResult result = new DelombokResult((java.util.List<CommentInfo>)catcher.getComments(unit), unit, this.force || options.isChanged(unit));
            if (this.verbose) {
                Object[] arrobject = new Object[2];
                arrobject[0] = unit.sourcefile.getName();
                arrobject[1] = result.isChanged() ? "delomboked" : "unchanged";
                this.feedback.printf("File: %s [%s]\n", arrobject);
            }
            Writer rawWriter = this.presetWriter != null ? this.presetWriter : (this.output == null ? this.createStandardOutWriter() : this.createFileWriter(this.output, (File)baseMap.get((Object)unit), unit.sourcefile.toUri()));
            BufferedWriter writer = new BufferedWriter(rawWriter);
            try {
                result.print(writer);
                continue;
            }
            finally {
                if (this.output != null) {
                    writer.close();
                    continue;
                }
                writer.flush();
                continue;
            }
        }
        delegate.close();
        return true;
    }

    private static String canonical(File dir) {
        try {
            return dir.getCanonicalPath();
        }
        catch (Exception e) {
            return dir.getAbsolutePath();
        }
    }

    private static String getExtension(File dir) {
        String name = dir.getName();
        int idx = name.lastIndexOf(46);
        return idx == -1 ? "" : name.substring(idx + 1);
    }

    private Writer createFileWriter(File outBase, File inBase, URI file) throws IOException {
        URI base = inBase.toURI();
        URI relative = base.relativize(base.resolve(file));
        File outFile = relative.isAbsolute() ? new File(outBase, new File(relative).getName()) : new File(outBase, relative.getPath());
        outFile.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(outFile);
        return this.createUnicodeEscapeWriter(out);
    }

    private Writer createStandardOutWriter() {
        return this.createUnicodeEscapeWriter(System.out);
    }

    private Writer createUnicodeEscapeWriter(OutputStream out) {
        return new UnicodeEscapeWriter(new OutputStreamWriter(out, this.charset), this.charset);
    }

    private static class CmdArgs {
        @Shorthand(value={"v"})
        @Description(value="Print the name of each file as it is being delombok-ed.")
        @Excludes(value={"quiet"})
        private boolean verbose;
        @Shorthand(value={"q"})
        @Description(value="No warnings or errors will be emitted to standard error")
        @Excludes(value={"verbose"})
        private boolean quiet;
        @Shorthand(value={"e"})
        @Description(value="Sets the encoding of your source files. Defaults to the system default charset. Example: \"UTF-8\"")
        private String encoding;
        @Shorthand(value={"p"})
        @Description(value="Print delombok-ed code to standard output instead of saving it in target directory")
        private boolean print;
        @Shorthand(value={"d"})
        @Description(value="Directory to save delomboked files to")
        @Mandatory(onlyIfNot={"print", "help"})
        private String target;
        @Shorthand(value={"c"})
        @Description(value="Classpath (analogous to javac -cp option)")
        private String classpath;
        @Shorthand(value={"s"})
        @Description(value="Sourcepath (analogous to javac -sourcepath option)")
        private String sourcepath;
        @Description(value="Files to delombok. Provide either a file, or a directory. If you use a directory, all files in it (recursive) are delombok-ed")
        @Sequential
        private java.util.List<String> input = new ArrayList<String>();
        @Description(value="Lombok will only delombok source files. Without this option, non-java, non-class files are copied to the target directory.")
        @Shorthand(value={"n"})
        private boolean nocopy;
        private boolean help;

        private CmdArgs() {
        }
    }

}

