/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.Native;

public final class Platform {
    public static final int UNSPECIFIED = -1;
    public static final int MAC = 0;
    public static final int LINUX = 1;
    public static final int WINDOWS = 2;
    public static final int SOLARIS = 3;
    public static final int FREEBSD = 4;
    public static final int OPENBSD = 5;
    public static final int WINDOWSCE = 6;
    private static final int osType;

    private Platform() {
    }

    public static final int getOSType() {
        return osType;
    }

    public static final boolean isMac() {
        return osType == 0;
    }

    public static final boolean isLinux() {
        return osType == 1;
    }

    public static final boolean isWindowsCE() {
        return osType == 6;
    }

    public static final boolean isWindows() {
        return osType == 2 || osType == 6;
    }

    public static final boolean isSolaris() {
        return osType == 3;
    }

    public static final boolean isFreeBSD() {
        return osType == 4;
    }

    public static final boolean isOpenBSD() {
        return osType == 5;
    }

    public static final boolean isX11() {
        return !Platform.isWindows() && !Platform.isMac();
    }

    public static final boolean deleteNativeLibraryAfterVMExit() {
        return osType == 2;
    }

    public static final boolean hasRuntimeExec() {
        if (Platform.isWindowsCE() && "J9".equals(System.getProperty("java.vm.name"))) {
            return false;
        }
        return true;
    }

    public static final boolean is64Bit() {
        String model = System.getProperty("sun.arch.data.model");
        if (model != null) {
            return "64".equals(model);
        }
        String arch = System.getProperty("os.arch").toLowerCase();
        if ("x86_64".equals(arch) || "ppc64".equals(arch) || "sparcv9".equals(arch) || "amd64".equals(arch)) {
            return true;
        }
        return Native.POINTER_SIZE == 8;
    }

    static {
        String osName = System.getProperty("os.name");
        osType = osName.startsWith("Linux") ? 1 : (osName.startsWith("Mac") || osName.startsWith("Darwin") ? 0 : (osName.startsWith("Windows CE") ? 6 : (osName.startsWith("Windows") ? 2 : (osName.startsWith("Solaris") || osName.startsWith("SunOS") ? 3 : (osName.startsWith("FreeBSD") ? 4 : (osName.startsWith("OpenBSD") ? 5 : -1))))));
    }
}

