/*
 * Decompiled with CFR 0_110.
 */
package lombok.delombok;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class UnicodeEscapeWriter
extends Writer {
    private final Writer writer;
    private CharsetEncoder encoder;

    public UnicodeEscapeWriter(Writer writer, Charset charset) {
        this.writer = writer;
        this.encoder = charset.newEncoder();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override
    public final void write(char[] cbuf, int off, int len) throws IOException {
        int start;
        int end = off + len;
        for (int index = start = off; index < end; ++index) {
            if (this.encoder.canEncode(cbuf[index])) continue;
            this.writer.write(cbuf, start, index - start);
            this.writeUnicodeEscape(cbuf[index]);
            start = index + 1;
        }
        if (start < end) {
            this.writer.write(cbuf, start, end - start);
        }
    }

    protected void writeUnicodeEscape(char c) throws IOException {
        this.writer.write("\\u" + Integer.toHexString(c));
    }
}

