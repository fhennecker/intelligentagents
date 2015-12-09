/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Location
 *  org.apache.tools.ant.Project
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.types.FileSet
 *  org.apache.tools.ant.types.Path
 *  org.apache.tools.ant.types.Reference
 *  org.apache.tools.ant.types.ResourceCollection
 *  org.apache.tools.ant.types.resources.FileResource
 */
package lombok.delombok.ant;

import java.io.File;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import lombok.delombok.Delombok;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;

public class DelombokTask
extends Task {
    private File fromDir;
    private File toDir;
    private Path classpath;
    private Path sourcepath;
    private boolean verbose;
    private String encoding;
    private Path path;

    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    public void setSourcepath(Path sourcepath) {
        if (this.sourcepath == null) {
            this.sourcepath = sourcepath;
        } else {
            this.sourcepath.append(sourcepath);
        }
    }

    public Path createSourcepath() {
        if (this.sourcepath == null) {
            this.sourcepath = new Path(this.getProject());
        }
        return this.sourcepath.createPath();
    }

    public void setSourcepathRef(Reference r) {
        this.createSourcepath().setRefid(r);
    }

    public void setFrom(File dir) {
        this.fromDir = dir;
    }

    public void setTo(File dir) {
        this.toDir = dir;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void addFileset(FileSet set) {
        if (this.path == null) {
            this.path = new Path(this.getProject());
        }
        this.path.add((ResourceCollection)set);
    }

    public void execute() throws BuildException {
        if (this.fromDir == null && this.path == null) {
            throw new BuildException("Either 'from' attribute, or nested <fileset> tags are required.");
        }
        if (this.fromDir != null && this.path != null) {
            throw new BuildException("You can't specify both 'from' attribute and nested filesets. You need one or the other.");
        }
        if (this.toDir == null) {
            throw new BuildException("The to attribute is required.");
        }
        Delombok delombok = new Delombok();
        if (this.verbose) {
            delombok.setVerbose(true);
        }
        try {
            if (this.encoding != null) {
                delombok.setCharset(this.encoding);
            }
        }
        catch (UnsupportedCharsetException e) {
            throw new BuildException("Unknown charset: " + this.encoding, this.getLocation());
        }
        if (this.classpath != null) {
            delombok.setClasspath(this.classpath.toString());
        }
        if (this.sourcepath != null) {
            delombok.setSourcepath(this.sourcepath.toString());
        }
        delombok.setOutput(this.toDir);
        try {
            if (this.fromDir != null) {
                delombok.addDirectory(this.fromDir);
            } else {
                for (FileResource fileResource : this.path) {
                    File baseDir = fileResource.getBaseDir();
                    if (baseDir == null) {
                        File file = fileResource.getFile();
                        delombok.addFile(file.getParentFile(), file.getName());
                        continue;
                    }
                    delombok.addFile(baseDir, fileResource.getName());
                }
            }
            delombok.delombok();
        }
        catch (IOException e) {
            throw new BuildException("I/O problem during delombok", (Throwable)e, this.getLocation());
        }
    }
}

