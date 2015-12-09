/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

import lombok.libs.org.objectweb.asm.Label;

class Handler {
    Label a;
    Label b;
    Label c;
    String d;
    int e;
    Handler f;

    Handler() {
    }

    static Handler a(Handler handler, Label label, Label label2) {
        int n;
        if (handler == null) {
            return null;
        }
        handler.f = Handler.a(handler.f, label, label2);
        int n2 = handler.a.c;
        int n3 = handler.b.c;
        int n4 = label.c;
        int n5 = n = label2 == null ? Integer.MAX_VALUE : label2.c;
        if (n4 < n3 && n > n2) {
            if (n4 <= n2) {
                if (n >= n3) {
                    handler = handler.f;
                } else {
                    handler.a = label2;
                }
            } else if (n >= n3) {
                handler.b = label;
            } else {
                Handler handler2 = new Handler();
                handler2.a = label2;
                handler2.b = handler.b;
                handler2.c = handler.c;
                handler2.d = handler.d;
                handler2.e = handler.e;
                handler2.f = handler.f;
                handler.b = label;
                handler.f = handler2;
            }
        }
        return handler;
    }
}

