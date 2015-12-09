/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.util;

import java.io.PrintStream;
import se.sics.isl.util.ConfigManager;

public class ArgumentManager
extends ConfigManager {
    private static final int OPTION = 0;
    private static final int ARG_NAME = 1;
    private static final int DESCRIPTION = 2;
    private static final int TYPE = 3;
    private static final int PARTS = 4;
    private String programName;
    private String[] originalArguments;
    private Object[] descriptions;
    private int descriptionCount = 0;
    private int columnWidth = 72;
    private String[] arguments;
    private int argLen;

    public ArgumentManager(String programName, String[] args) {
        this.programName = programName;
        this.originalArguments = args;
    }

    public ArgumentManager(ConfigManager parent, String programName, String[] args) {
        super(parent);
        this.programName = programName;
        this.originalArguments = args;
    }

    public void addOption(String option, String argName, String desc) {
        this.addOption(option, argName, desc, String.class);
    }

    public void addOption(String option, String desc) {
        this.addOption(option, null, desc, Boolean.class);
    }

    public void addHelp(String option, String desc) {
        this.addOption(option, null, desc, null);
    }

    public void addHelp(String option) {
        this.addOption(option, null, null, null);
    }

    private void addOption(String option, String argName, String desc, Class type) {
        int index = this.descriptionCount * 4;
        if (this.descriptions == null) {
            this.descriptions = new Object[40];
        } else if (index == this.descriptions.length) {
            Object[] tmp = new Object[index + 40];
            System.arraycopy(this.descriptions, 0, tmp, 0, index);
            this.descriptions = tmp;
        }
        this.descriptions[index + 0] = option;
        this.descriptions[index + 1] = argName;
        this.descriptions[index + 2] = desc;
        this.descriptions[index + 3] = type;
        ++this.descriptionCount;
    }

    public void validateArguments() {
        if (this.originalArguments == null || this.descriptionCount == 0) {
            return;
        }
        String[] arguments = new String[this.originalArguments.length];
        int argLen = 0;
        int i = 0;
        int n = this.originalArguments.length;
        while (i < n) {
            String a = this.originalArguments[i];
            if (a.length() > 1 && a.charAt(0) == '-') {
                int index = this.keyValuesIndexOf(this.descriptions, 4, 0, this.descriptionCount * 4, a = a.substring(1));
                if (index < 0) {
                    System.err.println("illegal argument '" + a + '\'');
                    this.usage(1);
                    return;
                }
                if (argLen + 2 >= arguments.length) {
                    String[] tmp = new String[argLen + 4];
                    System.arraycopy(arguments, 0, tmp, 0, arguments.length);
                    arguments = tmp;
                }
                arguments[argLen++] = a;
                Object argumentType = this.descriptions[index + 3];
                if (argumentType == Boolean.class) {
                    arguments[argLen++] = "true";
                } else {
                    if (argumentType == null) {
                        this.usage(0);
                        return;
                    }
                    if (++i >= n) {
                        System.err.println("argument '" + a + "' needs a value");
                        this.usage(1);
                        return;
                    }
                    arguments[argLen++] = this.originalArguments[i];
                }
            } else {
                System.err.println("illegal argument '" + a + '\'');
                this.usage(1);
                return;
            }
            ++i;
        }
        if (argLen > 0) {
            this.arguments = arguments;
            this.argLen = argLen;
        } else {
            this.arguments = null;
            this.argLen = 0;
        }
    }

    public void finishArguments() {
        this.originalArguments = null;
        this.descriptionCount = 0;
        this.descriptions = null;
        this.programName = null;
    }

    public void usage(int error) {
        if (this.descriptionCount > 0 && this.programName != null) {
            int len = 0;
            int splitLen = this.columnWidth / 2;
            int i = 0;
            int n = this.descriptionCount * 4;
            while (i < n) {
                String argName;
                String option = (String)this.descriptions[i + 0];
                int w = option.length() + ((argName = (String)this.descriptions[i + 1]) == null ? 0 : argName.length() + 3);
                if (w > len && w < splitLen) {
                    len = w;
                }
                i += 4;
            }
            len += 4;
            System.out.println("Usage: " + this.programName + " [-options]");
            System.out.println("where options include:");
            i = 0;
            n = this.descriptionCount * 4;
            while (i < n) {
                String desc = (String)this.descriptions[i + 2];
                if (desc != null && desc.length() > 0) {
                    int w;
                    String option = (String)this.descriptions[i + 0];
                    String argName = (String)this.descriptions[i + 1];
                    if (argName == null) {
                        System.out.print("  -" + option);
                        w = option.length() + 3;
                    } else {
                        System.out.print("  -" + option + " <" + argName + '>');
                        w = option.length() + argName.length() + 3 + 3;
                    }
                    if (w > splitLen) {
                        System.out.println();
                        w = 0;
                    }
                    int j = w;
                    while (j < len) {
                        System.out.print(' ');
                        ++j;
                    }
                    System.out.println(desc);
                }
                i += 4;
            }
        }
        System.exit(error);
    }

    public boolean hasArgument(String name) {
        if (this.keyValuesIndexOf(this.arguments, 2, 0, this.argLen, name) >= 0) {
            return true;
        }
        return false;
    }

    public String getArgument(String name) {
        return this.getArgument(name, null);
    }

    public String getArgument(String name, String defaultValue) {
        if (this.argLen == 0) {
            return defaultValue;
        }
        int index = this.keyValuesIndexOf(this.arguments, 2, 0, this.argLen, name);
        return index < 0 ? defaultValue : this.arguments[index + 1];
    }

    public int getArgumentAsInt(String name, int defaultValue) {
        String value = this.getArgument(name, null);
        return value != null ? this.parseInt(name, value, defaultValue) : defaultValue;
    }

    public long getArgumentAsLong(String name, long defaultValue) {
        String value = this.getArgument(name, null);
        return value != null ? this.parseLong(name, value, defaultValue) : defaultValue;
    }

    public float getArgumentAsFloat(String name, float defaultValue) {
        String value = this.getArgument(name, null);
        return value != null ? this.parseFloat(name, value, defaultValue) : defaultValue;
    }

    public double getArgumentAsDouble(String name, double defaultValue) {
        String value = this.getArgument(name, null);
        return value != null ? this.parseDouble(name, value, defaultValue) : defaultValue;
    }

    public boolean getArgumentAsBoolean(String name, boolean defaultValue) {
        String value = this.getArgument(name, null);
        return value != null ? this.parseBoolean(name, value, defaultValue) : defaultValue;
    }

    public void removeArgument(String name) {
        int index;
        if (this.argLen > 0 && (index = this.keyValuesIndexOf(this.arguments, 2, 0, this.argLen, name)) >= 0) {
            String oldValue = this.arguments[index + 1];
            this.argLen -= 2;
            this.arguments[index] = this.arguments[this.argLen];
            this.arguments[index + 1] = this.arguments[this.argLen + 1];
        }
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        String value = this.getArgument(name, null);
        return value == null ? super.getProperty(name, defaultValue) : value;
    }

    @Override
    public void setProperty(String name, String value) {
        this.removeArgument(name);
        super.setProperty(name, value);
    }

    private int keyValuesIndexOf(Object[] array, int nth, int start, int end, Object key) {
        int i = start;
        while (i < end) {
            if (key.equals(array[i])) {
                return i;
            }
            i += nth;
        }
        return -1;
    }
}

