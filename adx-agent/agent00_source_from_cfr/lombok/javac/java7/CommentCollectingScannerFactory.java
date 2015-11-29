/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.sun.tools.javac.parser.Scanner
 *  com.sun.tools.javac.parser.ScannerFactory
 *  com.sun.tools.javac.util.Context
 *  com.sun.tools.javac.util.Context$Factory
 *  com.sun.tools.javac.util.Context$Key
 */
package lombok.javac.java7;

import com.sun.tools.javac.parser.Scanner;
import com.sun.tools.javac.parser.ScannerFactory;
import com.sun.tools.javac.util.Context;
import java.nio.CharBuffer;
import lombok.javac.java7.CommentCollectingScanner;

public class CommentCollectingScannerFactory
extends ScannerFactory {
    public static void preRegister(final Context context) {
        if (context.get(scannerFactoryKey) == null) {
            context.put(scannerFactoryKey, (Context.Factory)new Context.Factory<ScannerFactory>(){

                public ScannerFactory make() {
                    return new CommentCollectingScannerFactory(context);
                }

                public ScannerFactory make(Context c) {
                    return new CommentCollectingScannerFactory(c);
                }
            });
        }
    }

    protected CommentCollectingScannerFactory(Context context) {
        super(context);
    }

    public Scanner newScanner(CharSequence input, boolean keepDocComments) {
        if (input instanceof CharBuffer) {
            return new CommentCollectingScanner(this, (CharBuffer)input);
        }
        char[] array = input.toString().toCharArray();
        return this.newScanner(array, array.length, keepDocComments);
    }

    public Scanner newScanner(char[] input, int inputLength, boolean keepDocComments) {
        return new CommentCollectingScanner(this, input, inputLength);
    }

}

