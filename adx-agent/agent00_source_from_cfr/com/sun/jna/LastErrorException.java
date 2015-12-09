/*
 * Decompiled with CFR 0_110.
 */
package com.sun.jna;

import com.sun.jna.Platform;

public class LastErrorException
extends RuntimeException {
    public int errorCode;

    private static String formatMessage(int code) {
        return Platform.isWindows() ? "GetLastError() returned " + code : "errno was " + code;
    }

    private static String parseMessage(String m) {
        try {
            return LastErrorException.formatMessage(Integer.parseInt(m));
        }
        catch (NumberFormatException e) {
            return m;
        }
    }

    public LastErrorException(String msg) {
        super(LastErrorException.parseMessage(msg));
        try {
            this.errorCode = Integer.parseInt(msg);
        }
        catch (NumberFormatException e) {
            this.errorCode = -1;
        }
    }

    public LastErrorException(int code) {
        super(LastErrorException.formatMessage(code));
        this.errorCode = code;
    }
}

