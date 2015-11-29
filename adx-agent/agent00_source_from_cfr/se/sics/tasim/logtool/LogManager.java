/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.logtool;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.isl.util.LogFormatter;
import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.LogSession;
import se.sics.tasim.logtool.ValueSet;

public class LogManager {
    private static final String CONF = "manager.";
    public static final String VERSION = "0.4.1 beta";
    private static final String USER_AGENT;
    private static final Logger log;
    private ConfigManager config;
    private File gameDirectory;
    private Hashtable handlerTable = new Hashtable();
    private boolean isSession = false;

    static {
        String os;
        try {
            os = System.getProperty("os.name");
        }
        catch (Exception e) {
            os = null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("SIMLogManager/0.4.1 beta");
        if (os != null) {
            sb.append(" (");
            sb.append(os);
            sb.append(')');
        }
        USER_AGENT = sb.toString();
        log = Logger.getLogger(LogManager.class.getName());
    }

    public LogManager(ConfigManager config) throws IOException, IllegalConfigurationException {
        this.config = config;
        this.gameDirectory = new File(config.getProperty("game.directory", "games"));
        if (!this.gameDirectory.exists() && !this.gameDirectory.mkdirs() || !this.gameDirectory.isDirectory()) {
            throw new IllegalConfigurationException("could not create directory '" + this.gameDirectory.getAbsolutePath() + '\'');
        }
        this.setLogging(config);
        log.info("TAC SIM Log Tool 0.4.1 beta");
        String dataFileName = config.getProperty("file");
        if (dataFileName != null) {
            this.processSingleFile(dataFileName);
        } else {
            ValueSet games = new ValueSet(config.getProperty("games", ""));
            ValueSet excludes = new ValueSet(config.getProperty("excludes", ""));
            if (games.hasValues()) {
                new LogSession(this, config.getProperty("server"), games, excludes).start();
            } else {
                System.err.println("nothing to do");
                System.exit(0);
            }
        }
    }

    private void processSingleFile(String dataFileName) {
        boolean showXML = this.config.getPropertyAsBoolean("xml", false);
        if (showXML) {
            this.generateXML(dataFileName);
        } else {
            File path = new File(dataFileName);
            if (path.isFile()) {
                this.parseFile(dataFileName);
            } else {
                File[] arrfile = path.listFiles();
                int n = arrfile.length;
                int n2 = 0;
                while (n2 < n) {
                    File file = arrfile[n2];
                    this.parseFile(file.getAbsolutePath());
                    ++n2;
                }
            }
        }
    }

    private void parseFile(String dataFileName) {
        try {
            this.sessionStarted();
            this.processDataFile(dataFileName);
        }
        finally {
            this.sessionEnded();
        }
    }

    private void generateXML(String filename) {
        try {
            LogReader.generateXML(this.getDataStream(filename));
        }
        catch (FileNotFoundException e) {
            log.severe("could not find the game log file '" + filename + '\'');
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not process the game log file '" + filename + '\'', e);
        }
    }

    private LogHandler setupLogHandler(String handlerName) throws IllegalConfigurationException {
        try {
            LogHandler handler = (LogHandler)Class.forName(handlerName).newInstance();
            handler.init(this);
            if (this.isSession) {
                handler.sessionStarted();
            }
            return handler;
        }
        catch (Exception e) {
            throw (IllegalConfigurationException)new IllegalConfigurationException("could not create log handler " + handlerName).initCause(e);
        }
    }

    private LogHandler getLogHandler(String type) throws IllegalConfigurationException {
        LogHandler handler = (LogHandler)this.handlerTable.get(type);
        if (handler != null) {
            return handler;
        }
        String handlerName = this.config.getProperty("handler." + type, this.config.getProperty("handler", "edu.umich.eecs.tac.logviewer.Visualizer"));
        handler = this.setupLogHandler(handlerName);
        this.handlerTable.put(type, handler);
        return handler;
    }

    void sessionStarted() {
        if (!this.isSession) {
            this.isSession = true;
            Enumeration e = this.handlerTable.elements();
            while (e.hasMoreElements()) {
                LogHandler handler = (LogHandler)e.nextElement();
                handler.sessionStarted();
            }
        }
    }

    void sessionEnded() {
        if (this.isSession) {
            this.isSession = false;
            Enumeration e = this.handlerTable.elements();
            while (e.hasMoreElements()) {
                LogHandler handler = (LogHandler)e.nextElement();
                handler.sessionEnded();
            }
        }
    }

    void processDataFile(String filename) {
        LogReader reader = null;
        try {
            reader = new LogReader(this.getDataStream(filename));
            System.out.println("Processing game " + reader.getSimulationID() + " (" + filename + ')');
            String simType = reader.getSimulationType();
            LogHandler handler = this.getLogHandler(simType);
            try {
                handler.start(reader);
            }
            finally {
                reader.close();
            }
        }
        catch (FileNotFoundException e) {
            log.severe("could not find the game log file '" + filename + '\'');
        }
        catch (EOFException e) {
            if (reader == null || !reader.isCancelled()) {
                log.log(Level.SEVERE, "could not process the game log file '" + filename + '\'', e);
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "could not process the game log file '" + filename + '\'', e);
        }
    }

    private InputStream getDataStream(String filename) throws IOException {
        File fp = new File(filename);
        if (!fp.exists()) {
            String string = filename = filename.endsWith(".gz") ? filename.substring(0, filename.length() - 3) : String.valueOf(filename) + ".gz";
        }
        if (filename.endsWith(".gz")) {
            return new GZIPInputStream(new FileInputStream(filename));
        }
        return new FileInputStream(filename);
    }

    public ConfigManager getConfig() {
        return this.config;
    }

    public File getGameDirectory() {
        return this.gameDirectory;
    }

    File getTempDirectory(String name) throws IOException {
        File fp = new File(this.gameDirectory, name);
        if (!fp.exists() && !fp.mkdirs() || !fp.isDirectory()) {
            throw new IOException("could not create directory '" + fp.getAbsolutePath() + '\'');
        }
        return fp;
    }

    void warn(String message) {
        System.out.println(message);
    }

    private void setLogging(ConfigManager config) throws IOException {
        int consoleLevel = config.getPropertyAsInt("log.consoleLevel", 0);
        int fileLevel = config.getPropertyAsInt("log.fileLevel", 6);
        Level consoleLogLevel = LogFormatter.getLogLevel(consoleLevel);
        Level fileLogLevel = LogFormatter.getLogLevel(fileLevel);
        Level logLevel = consoleLogLevel.intValue() < fileLogLevel.intValue() ? consoleLogLevel : fileLogLevel;
        boolean showThreads = config.getPropertyAsBoolean("log.threads", false);
        Logger root = Logger.getLogger("");
        Logger.getLogger("se.sics").setLevel(logLevel);
        LogFormatter formatter = new LogFormatter();
        formatter.setAliasLevel(2);
        LogFormatter.setFormatterForAllHandlers(formatter);
        formatter.setShowingThreads(showThreads);
        LogFormatter.setConsoleLevel(consoleLogLevel);
        if (fileLogLevel != Level.OFF) {
            FileHandler handler = new FileHandler("logtool.log", true);
            handler.setFormatter(formatter);
            root.addHandler(handler);
            handler.setLevel(fileLogLevel);
        }
    }
}

