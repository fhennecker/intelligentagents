/*
 * Decompiled with CFR 0_110.
 */
package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;

public class AggregateTranslator
extends CharSequenceTranslator {
    private final CharSequenceTranslator[] translators;

    public /* varargs */ AggregateTranslator(CharSequenceTranslator ... translators) {
        this.translators = ArrayUtils.clone(translators);
    }

    public int translate(CharSequence input, int index, Writer out) throws IOException {
        for (CharSequenceTranslator translator : this.translators) {
            int consumed = translator.translate(input, index, out);
            if (consumed == 0) continue;
            return consumed;
        }
        return 0;
    }
}

