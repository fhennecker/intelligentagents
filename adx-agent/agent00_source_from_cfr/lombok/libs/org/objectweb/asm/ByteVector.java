/*
 * Decompiled with CFR 0_110.
 */
package lombok.libs.org.objectweb.asm;

public class ByteVector {
    byte[] a;
    int b;

    public ByteVector() {
        this.a = new byte[64];
    }

    public ByteVector(int n) {
        this.a = new byte[n];
    }

    public ByteVector putByte(int n) {
        int n2 = this.b;
        if (n2 + 1 > this.a.length) {
            this.a(1);
        }
        this.a[n2++] = (byte)n;
        this.b = n2;
        return this;
    }

    ByteVector a(int n, int n2) {
        int n3 = this.b;
        if (n3 + 2 > this.a.length) {
            this.a(2);
        }
        byte[] arrby = this.a;
        arrby[n3++] = (byte)n;
        arrby[n3++] = (byte)n2;
        this.b = n3;
        return this;
    }

    public ByteVector putShort(int n) {
        int n2 = this.b;
        if (n2 + 2 > this.a.length) {
            this.a(2);
        }
        byte[] arrby = this.a;
        arrby[n2++] = (byte)(n >>> 8);
        arrby[n2++] = (byte)n;
        this.b = n2;
        return this;
    }

    ByteVector b(int n, int n2) {
        int n3 = this.b;
        if (n3 + 3 > this.a.length) {
            this.a(3);
        }
        byte[] arrby = this.a;
        arrby[n3++] = (byte)n;
        arrby[n3++] = (byte)(n2 >>> 8);
        arrby[n3++] = (byte)n2;
        this.b = n3;
        return this;
    }

    public ByteVector putInt(int n) {
        int n2 = this.b;
        if (n2 + 4 > this.a.length) {
            this.a(4);
        }
        byte[] arrby = this.a;
        arrby[n2++] = (byte)(n >>> 24);
        arrby[n2++] = (byte)(n >>> 16);
        arrby[n2++] = (byte)(n >>> 8);
        arrby[n2++] = (byte)n;
        this.b = n2;
        return this;
    }

    public ByteVector putLong(long l) {
        int n = this.b;
        if (n + 8 > this.a.length) {
            this.a(8);
        }
        byte[] arrby = this.a;
        int n2 = (int)(l >>> 32);
        arrby[n++] = (byte)(n2 >>> 24);
        arrby[n++] = (byte)(n2 >>> 16);
        arrby[n++] = (byte)(n2 >>> 8);
        arrby[n++] = (byte)n2;
        n2 = (int)l;
        arrby[n++] = (byte)(n2 >>> 24);
        arrby[n++] = (byte)(n2 >>> 16);
        arrby[n++] = (byte)(n2 >>> 8);
        arrby[n++] = (byte)n2;
        this.b = n;
        return this;
    }

    public ByteVector putUTF8(String string) {
        int n = this.b;
        int n2 = string.length();
        if (n + 2 + n2 > this.a.length) {
            this.a(2 + n2);
        }
        byte[] arrby = this.a;
        arrby[n++] = (byte)(n2 >>> 8);
        arrby[n++] = (byte)n2;
        for (int i = 0; i < n2; ++i) {
            int n3;
            char c = string.charAt(i);
            if (c >= '\u0001' && c <= '') {
                arrby[n++] = (byte)c;
                continue;
            }
            int n4 = i;
            for (n3 = i; n3 < n2; ++n3) {
                c = string.charAt(n3);
                if (c >= '\u0001' && c <= '') {
                    ++n4;
                    continue;
                }
                if (c > '\u07ff') {
                    n4 += 3;
                    continue;
                }
                n4 += 2;
            }
            arrby[this.b] = (byte)(n4 >>> 8);
            arrby[this.b + 1] = (byte)n4;
            if (this.b + 2 + n4 > arrby.length) {
                this.b = n;
                this.a(2 + n4);
                arrby = this.a;
            }
            for (n3 = i; n3 < n2; ++n3) {
                c = string.charAt(n3);
                if (c >= '\u0001' && c <= '') {
                    arrby[n++] = (byte)c;
                    continue;
                }
                if (c > '\u07ff') {
                    arrby[n++] = (byte)(224 | c >> 12 & 15);
                    arrby[n++] = (byte)(128 | c >> 6 & 63);
                    arrby[n++] = (byte)(128 | c & 63);
                    continue;
                }
                arrby[n++] = (byte)(192 | c >> 6 & 31);
                arrby[n++] = (byte)(128 | c & 63);
            }
            break;
        }
        this.b = n;
        return this;
    }

    public ByteVector putByteArray(byte[] arrby, int n, int n2) {
        if (this.b + n2 > this.a.length) {
            this.a(n2);
        }
        if (arrby != null) {
            System.arraycopy(arrby, n, this.a, this.b, n2);
        }
        this.b += n2;
        return this;
    }

    private void a(int n) {
        int n2 = 2 * this.a.length;
        int n3 = this.b + n;
        byte[] arrby = new byte[n2 > n3 ? n2 : n3];
        System.arraycopy(this.a, 0, arrby, 0, this.b);
        this.a = arrby;
    }
}

