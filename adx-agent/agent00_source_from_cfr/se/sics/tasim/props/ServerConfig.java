/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.props;

import java.util.Enumeration;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.props.SimpleContent;

public class ServerConfig
extends SimpleContent {
    private static final long serialVersionUID = 5568859194058033441L;
    private static final int INTEGER = 0;
    private static final int FLOAT = 1;
    private static final int STRING = 2;

    public ServerConfig() {
    }

    public ServerConfig(ConfigManager config) {
        Enumeration enumeration = config.names();
        while (enumeration.hasMoreElements()) {
            String name = (String)enumeration.nextElement();
            String value = config.getProperty(name);
            int type = this.checkType(value);
            if (type == 0) {
                try {
                    long v = Long.parseLong(value);
                    if (v <= Integer.MAX_VALUE && v >= Integer.MIN_VALUE) {
                        this.setAttribute(name, (int)v);
                        continue;
                    }
                    this.setAttribute(name, v);
                }
                catch (Exception e) {
                    this.setAttribute(name, value);
                }
                continue;
            }
            if (type == 1) {
                try {
                    this.setAttribute(name, Float.parseFloat(value));
                }
                catch (Exception e) {
                    this.setAttribute(name, value);
                }
                continue;
            }
            this.setAttribute(name, value);
        }
    }

    private int checkType(String value) {
        int type = 0;
        int i = 0;
        int n = value.length();
        while (i < n) {
            char c = value.charAt(i);
            if (c == '.') {
                if (type != 0) {
                    return 2;
                }
                type = 1;
            } else if (c < '0' || c > '9') {
                return 2;
            }
            ++i;
        }
        return type;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer().append(this.getTransportName());
        return this.params(buf).toString();
    }

    @Override
    public String getTransportName() {
        return "serverConfig";
    }
}

