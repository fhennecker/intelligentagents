/*
 * Decompiled with CFR 0_110.
 */
package lombok.javac.apt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import lombok.javac.apt.LombokFileObject;

class EmptyLombokFileObject
implements LombokFileObject {
    private final String name;
    private final JavaFileObject.Kind kind;

    public EmptyLombokFileObject(String name, JavaFileObject.Kind kind) {
        this.name = name;
        this.kind = kind;
    }

    @Override
    public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind) {
        String baseName = simpleName + kind.extension;
        return kind.equals((Object)this.getKind()) && (baseName.equals(this.toUri().getPath()) || this.toUri().getPath().endsWith("/" + baseName));
    }

    @Override
    public URI toUri() {
        return URI.create(this.name);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return "";
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return new StringReader("");
    }

    @Override
    public Writer openWriter() throws IOException {
        return new OutputStreamWriter(this.openOutputStream());
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return new ByteArrayOutputStream();
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public JavaFileObject.Kind getKind() {
        return this.kind;
    }

    @Override
    public String getName() {
        return this.toUri().getPath();
    }

    @Override
    public NestingKind getNestingKind() {
        return null;
    }

    @Override
    public Modifier getAccessLevel() {
        return null;
    }

    @Override
    public CharsetDecoder getDecoder(boolean ignoreEncodingErrors) {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        CodingErrorAction action = ignoreEncodingErrors ? CodingErrorAction.REPLACE : CodingErrorAction.REPORT;
        return decoder.onMalformedInput(action).onUnmappableCharacter(action);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof EmptyLombokFileObject)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        EmptyLombokFileObject other = (EmptyLombokFileObject)obj;
        return this.name.equals(other.name) && this.kind.equals((Object)other.kind);
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.kind.hashCode();
    }
}

