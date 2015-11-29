/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogFormatter
extends Formatter {
    private static final String EOL = System.getProperty("line.separator", "\r\n");
    private Hashtable aliasTable;
    private int aliasLevel = 0;
    private SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
    private Date date = new Date(0);
    private boolean isUTC = false;
    private long timeDiff = 0;
    private boolean isShowingThreads = false;
    private Thread lastThread = null;
    private String lastThreadName = null;

    @Override
    public synchronized String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        this.date.setTime(record.getMillis() + this.timeDiff);
        sb.append(this.dFormat.format(this.date));
        if (this.isShowingThreads) {
            Thread currentThread = Thread.currentThread();
            if (currentThread != this.lastThread) {
                this.lastThread = currentThread;
                this.lastThreadName = currentThread.getName();
            }
            sb.append(" [").append(this.lastThreadName).append(']');
        }
        sb.append(' ').append(record.getLevel()).append(' ').append(this.getAliasFor(record.getLoggerName())).append('|').append(record.getMessage()).append(EOL);
        if (record.getThrown() != null) {
            try {
                StringWriter out = new StringWriter();
                PrintWriter pout = new PrintWriter(out);
                record.getThrown().printStackTrace(pout);
                pout.close();
                sb.append(out.toString());
            }
            catch (Exception out) {
                // empty catch block
            }
        }
        return sb.toString();
    }

    public synchronized void setLogTime(long currentTime) {
        this.timeDiff = currentTime - System.currentTimeMillis();
        if (!this.isUTC) {
            this.isUTC = true;
            this.dFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
        }
    }

    public boolean isShowingThreads() {
        return this.isShowingThreads;
    }

    public synchronized void setShowingThreads(boolean isShowingThreads) {
        this.isShowingThreads = isShowingThreads;
        if (!isShowingThreads) {
            this.lastThread = null;
            this.lastThreadName = null;
        }
    }

    private String getAliasFor(String name) {
        Hashtable aliases = this.aliasTable;
        if (aliases == null) {
            return name;
        }
        String value = (String)aliases.get(name);
        if (value != null) {
            return value;
        }
        int level = this.aliasLevel;
        if (level > 0) {
            String a;
            if (level == 1) {
                int index = name.lastIndexOf(46);
                a = index >= 0 && index < name.length() - 1 ? name.substring(index + 1) : name;
            } else {
                int index = name.length() - 2;
                while (index >= 0) {
                    if (name.charAt(index) == '.' && --level == 0) break;
                    --index;
                }
                a = index >= 0 ? name.substring(index + 1) : name;
            }
            aliases.put(name, a);
            return a;
        }
        this.aliasTable = null;
        return name;
    }

    public synchronized void setAliasLevel(int aliasLevel) {
        if (this.aliasLevel != aliasLevel) {
            this.aliasLevel = aliasLevel;
            this.aliasTable = aliasLevel > 0 ? new Hashtable() : null;
        }
    }

    public static void separator(Logger log, Level level, String title) {
        LogFormatter.separator(log, level, title, title);
    }

    public static void separator(Logger log, Level level, String title, String message) {
        log.log(level, String.valueOf(title) + EOL + "************************************************************" + EOL + "* " + message + EOL + "************************************************************" + EOL + EOL);
    }

    public static void setFormatterForAllHandlers(Formatter formatter) {
        Handler[] logHandlers = Logger.getLogger("").getHandlers();
        if (logHandlers != null) {
            int i = 0;
            int n = logHandlers.length;
            while (i < n) {
                logHandlers[i].setFormatter(formatter);
                ++i;
            }
        }
    }

    public static void setConsoleLevel(Level level) {
        Handler[] logHandlers = Logger.getLogger("").getHandlers();
        if (logHandlers != null) {
            int i = 0;
            int n = logHandlers.length;
            while (i < n) {
                if (logHandlers[i] instanceof ConsoleHandler) {
                    logHandlers[i].setLevel(level);
                }
                ++i;
            }
        }
    }

    public static Level getLogLevel(int level) {
        if (level <= 0) {
            return Level.ALL;
        }
        switch (level) {
            case 1: {
                return Level.FINEST;
            }
            case 2: {
                return Level.FINER;
            }
            case 3: {
                return Level.FINE;
            }
            case 4: {
                return Level.WARNING;
            }
            case 5: {
                return Level.SEVERE;
            }
        }
        return Level.OFF;
    }

    public static void setFileLevel(Level level) {
        Handler[] logHandlers = Logger.getLogger("").getHandlers();
        if (logHandlers != null) {
            int i = 0;
            int n = logHandlers.length;
            while (i < n) {
                if (logHandlers[i] instanceof FileHandler) {
                    logHandlers[i].setLevel(level);
                }
                ++i;
            }
        }
    }

    public static void setLevelForAllHandlers(Level level) {
        Handler[] logHandlers = Logger.getLogger("").getHandlers();
        if (logHandlers != null) {
            int i = 0;
            int n = logHandlers.length;
            while (i < n) {
                logHandlers[i].setLevel(level);
                ++i;
            }
        }
    }
}

