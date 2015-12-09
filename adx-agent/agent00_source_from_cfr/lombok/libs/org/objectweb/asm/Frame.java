/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

import lombok.libs.org.objectweb.asm.ClassWriter;
import lombok.libs.org.objectweb.asm.Item;
import lombok.libs.org.objectweb.asm.Label;
import lombok.libs.org.objectweb.asm.Type;

final class Frame {
    static final int[] a;
    Label b;
    int[] c;
    int[] d;
    private int[] e;
    private int[] f;
    private int g;
    private int h;
    private int[] i;

    Frame() {
    }

    private int a(int n) {
        if (this.e == null || n >= this.e.length) {
            return 33554432 | n;
        }
        int n2 = this.e[n];
        if (n2 == 0) {
            n2 = this.e[n] = 33554432 | n;
        }
        return n2;
    }

    private void a(int n, int n2) {
        int n3;
        if (this.e == null) {
            this.e = new int[10];
        }
        if (n >= (n3 = this.e.length)) {
            int[] arrn = new int[Math.max(n + 1, 2 * n3)];
            System.arraycopy(this.e, 0, arrn, 0, n3);
            this.e = arrn;
        }
        this.e[n] = n2;
    }

    private void b(int n) {
        int n2;
        if (this.f == null) {
            this.f = new int[10];
        }
        if (this.g >= (n2 = this.f.length)) {
            int[] arrn = new int[Math.max(this.g + 1, 2 * n2)];
            System.arraycopy(this.f, 0, arrn, 0, n2);
            this.f = arrn;
        }
        this.f[this.g++] = n;
        int n3 = this.b.f + this.g;
        if (n3 > this.b.g) {
            this.b.g = n3;
        }
    }

    private void a(ClassWriter classWriter, String string) {
        int n = Frame.b(classWriter, string);
        if (n != 0) {
            this.b(n);
            if (n == 16777220 || n == 16777219) {
                this.b(16777216);
            }
        }
    }

    private static int b(ClassWriter classWriter, String string) {
        int n;
        int n2 = string.charAt(0) == '(' ? string.indexOf(41) + 1 : 0;
        switch (string.charAt(n2)) {
            case 'V': {
                return 0;
            }
            case 'B': 
            case 'C': 
            case 'I': 
            case 'S': 
            case 'Z': {
                return 16777217;
            }
            case 'F': {
                return 16777218;
            }
            case 'J': {
                return 16777220;
            }
            case 'D': {
                return 16777219;
            }
            case 'L': {
                String string2 = string.substring(n2 + 1, string.length() - 1);
                return 24117248 | classWriter.c(string2);
            }
        }
        int n3 = n2 + 1;
        while (string.charAt(n3) == '[') {
            ++n3;
        }
        switch (string.charAt(n3)) {
            case 'Z': {
                n = 16777225;
                break;
            }
            case 'C': {
                n = 16777227;
                break;
            }
            case 'B': {
                n = 16777226;
                break;
            }
            case 'S': {
                n = 16777228;
                break;
            }
            case 'I': {
                n = 16777217;
                break;
            }
            case 'F': {
                n = 16777218;
                break;
            }
            case 'J': {
                n = 16777220;
                break;
            }
            case 'D': {
                n = 16777219;
                break;
            }
            default: {
                String string3 = string.substring(n3 + 1, string.length() - 1);
                n = 24117248 | classWriter.c(string3);
            }
        }
        return n3 - n2 << 28 | n;
    }

    private int a() {
        if (this.g > 0) {
            return this.f[--this.g];
        }
        return 50331648 | - --this.b.f;
    }

    private void c(int n) {
        if (this.g >= n) {
            this.g -= n;
        } else {
            this.b.f -= n - this.g;
            this.g = 0;
        }
    }

    private void a(String string) {
        char c = string.charAt(0);
        if (c == '(') {
            this.c((Type.getArgumentsAndReturnSizes(string) >> 2) - 1);
        } else if (c == 'J' || c == 'D') {
            this.c(2);
        } else {
            this.c(1);
        }
    }

    private void d(int n) {
        int n2;
        if (this.i == null) {
            this.i = new int[2];
        }
        if (this.h >= (n2 = this.i.length)) {
            int[] arrn = new int[Math.max(this.h + 1, 2 * n2)];
            System.arraycopy(this.i, 0, arrn, 0, n2);
            this.i = arrn;
        }
        this.i[this.h++] = n;
    }

    private int a(ClassWriter classWriter, int n) {
        int n2;
        if (n == 16777222) {
            n2 = 24117248 | classWriter.c(classWriter.I);
        } else if ((n & -1048576) == 25165824) {
            String string = classWriter.H[n & 1048575].g;
            n2 = 24117248 | classWriter.c(string);
        } else {
            return n;
        }
        for (int i = 0; i < this.h; ++i) {
            int n3 = this.i[i];
            int n4 = n3 & -268435456;
            int n5 = n3 & 251658240;
            if (n5 == 33554432) {
                n3 = n4 + this.c[n3 & 8388607];
            } else if (n5 == 50331648) {
                n3 = n4 + this.d[this.d.length - (n3 & 8388607)];
            }
            if (n != n3) continue;
            return n2;
        }
        return n;
    }

    void a(ClassWriter classWriter, int n, Type[] arrtype, int n2) {
        this.c = new int[n2];
        this.d = new int[0];
        int n3 = 0;
        if ((n & 8) == 0) {
            this.c[n3++] = (n & 262144) == 0 ? 24117248 | classWriter.c(classWriter.I) : 16777222;
        }
        for (int i = 0; i < arrtype.length; ++i) {
            int n4 = Frame.b(classWriter, arrtype[i].getDescriptor());
            this.c[n3++] = n4;
            if (n4 != 16777220 && n4 != 16777219) continue;
            this.c[n3++] = 16777216;
        }
        while (n3 < n2) {
            this.c[n3++] = 16777216;
        }
    }

    void a(int n, int n2, ClassWriter classWriter, Item item) {
        block0 : switch (n) {
            case 0: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 145: 
            case 146: 
            case 147: 
            case 167: 
            case 177: {
                break;
            }
            case 1: {
                this.b(16777221);
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 16: 
            case 17: 
            case 21: {
                this.b(16777217);
                break;
            }
            case 9: 
            case 10: 
            case 22: {
                this.b(16777220);
                this.b(16777216);
                break;
            }
            case 11: 
            case 12: 
            case 13: 
            case 23: {
                this.b(16777218);
                break;
            }
            case 14: 
            case 15: 
            case 24: {
                this.b(16777219);
                this.b(16777216);
                break;
            }
            case 18: {
                switch (item.b) {
                    case 3: {
                        this.b(16777217);
                        break block0;
                    }
                    case 5: {
                        this.b(16777220);
                        this.b(16777216);
                        break block0;
                    }
                    case 4: {
                        this.b(16777218);
                        break block0;
                    }
                    case 6: {
                        this.b(16777219);
                        this.b(16777216);
                        break block0;
                    }
                    case 7: {
                        this.b(24117248 | classWriter.c("java/lang/Class"));
                        break block0;
                    }
                    case 8: {
                        this.b(24117248 | classWriter.c("java/lang/String"));
                        break block0;
                    }
                    case 16: {
                        this.b(24117248 | classWriter.c("java/lang/invoke/MethodType"));
                        break block0;
                    }
                }
                this.b(24117248 | classWriter.c("java/lang/invoke/MethodHandle"));
                break;
            }
            case 25: {
                this.b(this.a(n2));
                break;
            }
            case 46: 
            case 51: 
            case 52: 
            case 53: {
                this.c(2);
                this.b(16777217);
                break;
            }
            case 47: 
            case 143: {
                this.c(2);
                this.b(16777220);
                this.b(16777216);
                break;
            }
            case 48: {
                this.c(2);
                this.b(16777218);
                break;
            }
            case 49: 
            case 138: {
                this.c(2);
                this.b(16777219);
                this.b(16777216);
                break;
            }
            case 50: {
                this.c(1);
                int n3 = this.a();
                this.b(-268435456 + n3);
                break;
            }
            case 54: 
            case 56: 
            case 58: {
                int n4 = this.a();
                this.a(n2, n4);
                if (n2 <= 0) break;
                int n5 = this.a(n2 - 1);
                if (n5 == 16777220 || n5 == 16777219) {
                    this.a(n2 - 1, 16777216);
                    break;
                }
                if ((n5 & 251658240) == 16777216) break;
                this.a(n2 - 1, n5 | 8388608);
                break;
            }
            case 55: 
            case 57: {
                this.c(1);
                int n6 = this.a();
                this.a(n2, n6);
                this.a(n2 + 1, 16777216);
                if (n2 <= 0) break;
                int n7 = this.a(n2 - 1);
                if (n7 == 16777220 || n7 == 16777219) {
                    this.a(n2 - 1, 16777216);
                    break;
                }
                if ((n7 & 251658240) == 16777216) break;
                this.a(n2 - 1, n7 | 8388608);
                break;
            }
            case 79: 
            case 81: 
            case 83: 
            case 84: 
            case 85: 
            case 86: {
                this.c(3);
                break;
            }
            case 80: 
            case 82: {
                this.c(4);
                break;
            }
            case 87: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 170: 
            case 171: 
            case 172: 
            case 174: 
            case 176: 
            case 191: 
            case 194: 
            case 195: 
            case 198: 
            case 199: {
                this.c(1);
                break;
            }
            case 88: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 173: 
            case 175: {
                this.c(2);
                break;
            }
            case 89: {
                int n8 = this.a();
                this.b(n8);
                this.b(n8);
                break;
            }
            case 90: {
                int n9 = this.a();
                int n10 = this.a();
                this.b(n9);
                this.b(n10);
                this.b(n9);
                break;
            }
            case 91: {
                int n11 = this.a();
                int n12 = this.a();
                int n13 = this.a();
                this.b(n11);
                this.b(n13);
                this.b(n12);
                this.b(n11);
                break;
            }
            case 92: {
                int n14 = this.a();
                int n15 = this.a();
                this.b(n15);
                this.b(n14);
                this.b(n15);
                this.b(n14);
                break;
            }
            case 93: {
                int n16 = this.a();
                int n17 = this.a();
                int n18 = this.a();
                this.b(n17);
                this.b(n16);
                this.b(n18);
                this.b(n17);
                this.b(n16);
                break;
            }
            case 94: {
                int n19 = this.a();
                int n20 = this.a();
                int n21 = this.a();
                int n22 = this.a();
                this.b(n20);
                this.b(n19);
                this.b(n22);
                this.b(n21);
                this.b(n20);
                this.b(n19);
                break;
            }
            case 95: {
                int n23 = this.a();
                int n24 = this.a();
                this.b(n23);
                this.b(n24);
                break;
            }
            case 96: 
            case 100: 
            case 104: 
            case 108: 
            case 112: 
            case 120: 
            case 122: 
            case 124: 
            case 126: 
            case 128: 
            case 130: 
            case 136: 
            case 142: 
            case 149: 
            case 150: {
                this.c(2);
                this.b(16777217);
                break;
            }
            case 97: 
            case 101: 
            case 105: 
            case 109: 
            case 113: 
            case 127: 
            case 129: 
            case 131: {
                this.c(4);
                this.b(16777220);
                this.b(16777216);
                break;
            }
            case 98: 
            case 102: 
            case 106: 
            case 110: 
            case 114: 
            case 137: 
            case 144: {
                this.c(2);
                this.b(16777218);
                break;
            }
            case 99: 
            case 103: 
            case 107: 
            case 111: 
            case 115: {
                this.c(4);
                this.b(16777219);
                this.b(16777216);
                break;
            }
            case 121: 
            case 123: 
            case 125: {
                this.c(3);
                this.b(16777220);
                this.b(16777216);
                break;
            }
            case 132: {
                this.a(n2, 16777217);
                break;
            }
            case 133: 
            case 140: {
                this.c(1);
                this.b(16777220);
                this.b(16777216);
                break;
            }
            case 134: {
                this.c(1);
                this.b(16777218);
                break;
            }
            case 135: 
            case 141: {
                this.c(1);
                this.b(16777219);
                this.b(16777216);
                break;
            }
            case 139: 
            case 190: 
            case 193: {
                this.c(1);
                this.b(16777217);
                break;
            }
            case 148: 
            case 151: 
            case 152: {
                this.c(4);
                this.b(16777217);
                break;
            }
            case 168: 
            case 169: {
                throw new RuntimeException("JSR/RET are not supported with computeFrames option");
            }
            case 178: {
                this.a(classWriter, item.i);
                break;
            }
            case 179: {
                this.a(item.i);
                break;
            }
            case 180: {
                this.c(1);
                this.a(classWriter, item.i);
                break;
            }
            case 181: {
                this.a(item.i);
                this.a();
                break;
            }
            case 182: 
            case 183: 
            case 184: 
            case 185: {
                this.a(item.i);
                if (n != 184) {
                    int n25 = this.a();
                    if (n == 183 && item.h.charAt(0) == '<') {
                        this.d(n25);
                    }
                }
                this.a(classWriter, item.i);
                break;
            }
            case 186: {
                this.a(item.h);
                this.a(classWriter, item.h);
                break;
            }
            case 187: {
                this.b(25165824 | classWriter.a(item.g, n2));
                break;
            }
            case 188: {
                this.a();
                switch (n2) {
                    case 4: {
                        this.b(285212681);
                        break block0;
                    }
                    case 5: {
                        this.b(285212683);
                        break block0;
                    }
                    case 8: {
                        this.b(285212682);
                        break block0;
                    }
                    case 9: {
                        this.b(285212684);
                        break block0;
                    }
                    case 10: {
                        this.b(285212673);
                        break block0;
                    }
                    case 6: {
                        this.b(285212674);
                        break block0;
                    }
                    case 7: {
                        this.b(285212675);
                        break block0;
                    }
                }
                this.b(285212676);
                break;
            }
            case 189: {
                String string = item.g;
                this.a();
                if (string.charAt(0) == '[') {
                    this.a(classWriter, "" + '[' + string);
                    break;
                }
                this.b(292552704 | classWriter.c(string));
                break;
            }
            case 192: {
                String string = item.g;
                this.a();
                if (string.charAt(0) == '[') {
                    this.a(classWriter, string);
                    break;
                }
                this.b(24117248 | classWriter.c(string));
                break;
            }
            default: {
                this.c(n2);
                this.a(classWriter, item.g);
            }
        }
    }

    boolean a(ClassWriter classWriter, Frame frame, int n) {
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        boolean bl = false;
        int n7 = this.c.length;
        int n8 = this.d.length;
        if (frame.c == null) {
            frame.c = new int[n7];
            bl = true;
        }
        for (n5 = 0; n5 < n7; ++n5) {
            if (this.e != null && n5 < this.e.length) {
                n6 = this.e[n5];
                if (n6 == 0) {
                    n2 = this.c[n5];
                } else {
                    n4 = n6 & -268435456;
                    n3 = n6 & 251658240;
                    if (n3 == 16777216) {
                        n2 = n6;
                    } else {
                        n2 = n3 == 33554432 ? n4 + this.c[n6 & 8388607] : n4 + this.d[n8 - (n6 & 8388607)];
                        if ((n6 & 8388608) != 0 && (n2 == 16777220 || n2 == 16777219)) {
                            n2 = 16777216;
                        }
                    }
                }
            } else {
                n2 = this.c[n5];
            }
            if (this.i != null) {
                n2 = this.a(classWriter, n2);
            }
            bl |= Frame.a(classWriter, n2, frame.c, n5);
        }
        if (n > 0) {
            for (n5 = 0; n5 < n7; ++n5) {
                n2 = this.c[n5];
                bl |= Frame.a(classWriter, n2, frame.c, n5);
            }
            if (frame.d == null) {
                frame.d = new int[1];
                bl = true;
            }
            return bl |= Frame.a(classWriter, n, frame.d, 0);
        }
        int n9 = this.d.length + this.b.f;
        if (frame.d == null) {
            frame.d = new int[n9 + this.g];
            bl = true;
        }
        for (n5 = 0; n5 < n9; ++n5) {
            n2 = this.d[n5];
            if (this.i != null) {
                n2 = this.a(classWriter, n2);
            }
            bl |= Frame.a(classWriter, n2, frame.d, n5);
        }
        for (n5 = 0; n5 < this.g; ++n5) {
            n6 = this.f[n5];
            n4 = n6 & -268435456;
            n3 = n6 & 251658240;
            if (n3 == 16777216) {
                n2 = n6;
            } else {
                n2 = n3 == 33554432 ? n4 + this.c[n6 & 8388607] : n4 + this.d[n8 - (n6 & 8388607)];
                if ((n6 & 8388608) != 0 && (n2 == 16777220 || n2 == 16777219)) {
                    n2 = 16777216;
                }
            }
            if (this.i != null) {
                n2 = this.a(classWriter, n2);
            }
            bl |= Frame.a(classWriter, n2, frame.d, n9 + n5);
        }
        return bl;
    }

    private static boolean a(ClassWriter classWriter, int n, int[] arrn, int n2) {
        int n3;
        int n4 = arrn[n2];
        if (n4 == n) {
            return false;
        }
        if ((n & 268435455) == 16777221) {
            if (n4 == 16777221) {
                return false;
            }
            n = 16777221;
        }
        if (n4 == 0) {
            arrn[n2] = n;
            return true;
        }
        if ((n4 & 267386880) == 24117248 || (n4 & -268435456) != 0) {
            if (n == 16777221) {
                return false;
            }
            n3 = (n & -1048576) == (n4 & -1048576) ? ((n4 & 267386880) == 24117248 ? n & -268435456 | 24117248 | classWriter.a(n & 1048575, n4 & 1048575) : 24117248 | classWriter.c("java/lang/Object")) : ((n & 267386880) == 24117248 || (n & -268435456) != 0 ? 24117248 | classWriter.c("java/lang/Object") : 16777216);
        } else {
            n3 = n4 == 16777221 ? ((n & 267386880) == 24117248 || (n & -268435456) != 0 ? n : 16777216) : 16777216;
        }
        if (n4 != n3) {
            arrn[n2] = n3;
            return true;
        }
        return false;
    }

    static {
        int[] arrn = new int[202];
        String string = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = string.charAt(i) - 69;
        }
        a = arrn;
    }
}

