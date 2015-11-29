/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import lombok.core.DiagnosticsReceiver;
import lombok.core.PostCompilerTransformation;
import lombok.core.SpiLoadUtil;

public final class PostCompiler {
    private static List<PostCompilerTransformation> transformations;

    private PostCompiler() {
    }

    public static byte[] applyTransformations(byte[] original, String fileName, DiagnosticsReceiver diagnostics) {
        PostCompiler.init(diagnostics);
        byte[] previous = original;
        for (PostCompilerTransformation transformation : transformations) {
            try {
                byte[] next = transformation.applyTransformations(previous, fileName, diagnostics);
                if (next == null) continue;
                previous = next;
            }
            catch (Exception e) {
                diagnostics.addWarning(String.format("Error during the transformation of '%s'; post-compiler '%s' caused an exception: %s", fileName, transformation.getClass().getName(), e.getMessage()));
            }
        }
        return previous;
    }

    private static synchronized void init(DiagnosticsReceiver diagnostics) {
        if (transformations != null) {
            return;
        }
        try {
            transformations = SpiLoadUtil.readAllFromIterator(SpiLoadUtil.findServices(PostCompilerTransformation.class, PostCompilerTransformation.class.getClassLoader()));
        }
        catch (IOException e) {
            transformations = Collections.emptyList();
            diagnostics.addWarning("Could not load post-compile transformers: " + e.getMessage());
        }
    }

    public static OutputStream wrapOutputStream(final OutputStream originalStream, final String fileName, final DiagnosticsReceiver diagnostics) throws IOException {
        return new ByteArrayOutputStream(){

            public void close() throws IOException {
                byte[] original = this.toByteArray();
                Object copy = null;
                try {
                    copy = PostCompiler.applyTransformations(original, fileName, diagnostics);
                }
                catch (Exception e) {
                    diagnostics.addWarning(String.format("Error during the transformation of '%s'; no post-compilation has been applied", fileName));
                }
                if (copy == null) {
                    copy = original;
                }
                originalStream.write((byte[])copy);
                originalStream.close();
            }
        };
    }

}

