/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WindowsDriveInfo {
    public List<String> getLogicalDrives() {
        int flags = this.getLogicalDrives0();
        ArrayList<String> letters = new ArrayList<String>();
        for (int i = 0; i < 26; ++i) {
            if ((flags & 1 << i) == 0) continue;
            letters.add(Character.toString((char)(65 + i)));
        }
        return letters;
    }

    private native int getLogicalDrives0();

    public boolean isFixedDisk(String letter) {
        if (letter.length() != 1) {
            throw new IllegalArgumentException("Supply 1 letter, not: " + letter);
        }
        char drive = Character.toUpperCase(letter.charAt(0));
        if (drive < 'A' || drive > 'Z') {
            throw new IllegalArgumentException("A drive is indicated by a letter, so A-Z inclusive. Not " + drive);
        }
        return (long)this.getDriveType("" + drive + ":\\") == 3;
    }

    private native int getDriveType(String var1);

    public static void main(String[] args) {
        System.loadLibrary("WindowsDriveInfo");
        WindowsDriveInfo info = new WindowsDriveInfo();
        for (String letter : info.getLogicalDrives()) {
            Object[] arrobject = new Object[2];
            arrobject[0] = letter;
            arrobject[1] = info.isFixedDisk(letter) ? "Fixed Disk" : "Not Fixed Disk";
            System.out.printf("Drive %s: - %s\n", arrobject);
        }
    }
}

