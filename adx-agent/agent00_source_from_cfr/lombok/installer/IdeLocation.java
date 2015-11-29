/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import lombok.installer.IdeFinder;
import lombok.installer.InstallException;
import lombok.installer.UninstallException;
import lombok.patcher.inject.LiveInjector;

public abstract class IdeLocation {
    boolean selected = true;
    private static final String LEGAL_PATH_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-_/";

    public abstract String install() throws InstallException;

    public abstract void uninstall() throws UninstallException;

    public abstract String getName();

    public abstract boolean hasLombok();

    public abstract URL getIdeIcon();

    public static File findOurJar() {
        return new File(LiveInjector.findPathJar(IdeFinder.class));
    }

    public String toString() {
        return this.getName();
    }

    public static String canonical(File p) {
        try {
            return p.getCanonicalPath();
        }
        catch (IOException e) {
            String x = p.getAbsolutePath();
            return x == null ? p.getPath() : x;
        }
    }

    public static String escapePath(String path) {
        StringBuilder out = new StringBuilder();
        for (char c : path.toCharArray()) {
            if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-_/".indexOf(c) == -1) {
                out.append('\\');
            }
            out.append(c);
        }
        return out.toString();
    }
}

