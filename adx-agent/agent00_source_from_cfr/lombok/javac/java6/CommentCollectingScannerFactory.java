/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.parser.Scanner
 *  com.sun.tools.javac.parser.Scanner$Factory
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.Context$Factory
 *  com.sun.tools.javac.util.Context$Key
 */
package lombok.javac.java6;

import com.sun.tools.javac.parser.Scanner;
import com.sun.tools.javac.util.Context;
import java.nio.CharBuffer;
import lombok.javac.java6.CommentCollectingScanner;

public class CommentCollectingScannerFactory
extends Scanner.Factory {
    public static void preRegister(final Context context) {
        if (context.get(scannerFactoryKey) == null) {
            context.put(scannerFactoryKey, (Context.Factory)new Context.Factory<Scanner.Factory>(){

                public Scanner.Factory make() {
                    return new CommentCollectingScannerFactory(context);
                }

                public Scanner.Factory make(Context c) {
                    return new CommentCollectingScannerFactory(c);
                }
            });
        }
    }

    protected CommentCollectingScannerFactory(Context context) {
        super(context);
    }

    public Scanner newScanner(CharSequence input) {
        if (input instanceof CharBuffer) {
            return new CommentCollectingScanner(this, (CharBuffer)input);
        }
        char[] array = input.toString().toCharArray();
        return this.newScanner(array, array.length);
    }

    public Scanner newScanner(char[] input, int inputLength) {
        return new CommentCollectingScanner(this, input, inputLength);
    }

}

