/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.mortbay.http.HttpContext
 *  org.mortbay.http.HttpHandler
 *  org.mortbay.http.HttpListener
 *  org.mortbay.http.HttpServer
 *  org.mortbay.http.NCSARequestLog
 *  org.mortbay.http.RequestLog
 *  org.mortbay.http.SocketListener
 *  org.mortbay.http.UserRealm
 *  org.mortbay.http.handler.NotFoundHandler
 *  org.mortbay.http.handler.ResourceHandler
 *  org.mortbay.http.handler.SecurityHandler
 *  org.mortbay.util.InetAddrPort
 */
package se.sics.tasim.is.common;

import com.botbox.html.HtmlWriter;
import com.botbox.util.ArrayUtils;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpListener;
import org.mortbay.http.HttpServer;
import org.mortbay.http.NCSARequestLog;
import org.mortbay.http.RequestLog;
import org.mortbay.http.SocketListener;
import org.mortbay.http.UserRealm;
import org.mortbay.http.handler.NotFoundHandler;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.http.handler.SecurityHandler;
import org.mortbay.util.InetAddrPort;
import se.sics.isl.db.DBField;
import se.sics.isl.db.DBMatcher;
import se.sics.isl.db.DBObject;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.db.Database;
import se.sics.isl.inet.InetServer;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.is.AgentInfo;
import se.sics.tasim.is.AgentLookup;
import se.sics.tasim.is.common.AgentRealm;
import se.sics.tasim.is.common.BlockingViewerServer;
import se.sics.tasim.is.common.DatabaseUtils;
import se.sics.tasim.is.common.HttpPage;
import se.sics.tasim.is.common.InfoConnectionImpl;
import se.sics.tasim.is.common.InfoManager;
import se.sics.tasim.is.common.PageHandler;
import se.sics.tasim.is.common.RedirectPage;
import se.sics.tasim.is.common.RegistrationNotificationPage;
import se.sics.tasim.is.common.RegistrationPage;
import se.sics.tasim.is.common.SimServer;
import se.sics.tasim.is.common.SimulationArchiver;
import se.sics.tasim.is.common.StaticPage;

public class InfoServer {
    private static final int ADMIN_USER_ID = 0;
    private static final int MAX_USER_NAME_LENGTH = 20;
    private static final int MAX_USER_PASSWORD_LENGTH = 20;
    private static final int MAX_USER_EMAIL_LENGTH = 80;
    public static final String CONF = "is.";
    public static final boolean ALLOW_SIM_TYPE = false;
    private static final Logger log = Logger.getLogger(InfoServer.class.getName());
    private final ConfigManager config;
    private final String defaultSimulationType;
    private final AgentLookup agentLookup = new AgentLookup();
    private final AgentRealm agentRealm;
    private Database userDatabase;
    private DBTable userTable;
    private Database database;
    private String basePath;
    private String infoServerName;
    private String version;
    private String serverType;
    private HttpServer httpServer;
    private HttpContext httpContext;
    private ResourceHandler httpResourceHandler;
    private SocketListener httpSocketListener;
    private PageHandler pageHandler;
    private String httpHost;
    private int httpPort;
    private StaticPage menuPage;
    private StaticPage statusPage;
    private RedirectPage redirectPage;
    private BlockingViewerServer viewerServer;
    private int timeDiff = 0;
    private static SimpleDateFormat dFormat = null;
    private static Date date = null;
    private SimServer[] servers;
    private boolean storeResults = false;
    private Hashtable managerTable = new Hashtable();
    private SimulationArchiver simulationArchiver;
    private Timer timer = new Timer();
    private String registrationURL = null;

    public InfoServer(ConfigManager config) throws IllegalConfigurationException, IOException {
        this.config = config;
        this.infoServerName = config.getProperty("is.server.name", config.getProperty("server.name"));
        if (this.infoServerName == null) {
            this.infoServerName = this.generateServerName();
        }
        this.serverType = config.getProperty("is.server.type", config.getProperty("server.type", "TAC SIM Server"));
        this.version = config.getProperty("is.server.version", config.getProperty("server.version", "0.0.1"));
        this.defaultSimulationType = config.getProperty("is.simulation.defaultType", config.getProperty("simulation.defaultType", "tac13adx"));
        this.setTimeZone(config.getPropertyAsInt("timeZone", 0));
        this.registrationURL = config.getProperty("is.registration.url");
        String[] names = config.getPropertyAsArray("is.manager.names");
        InfoManager[] managers = (InfoManager[])config.createInstances("is.manager", InfoManager.class, names);
        if (managers == null || managers.length == 0) {
            throw new IllegalConfigurationException("no info managers in configuration");
        }
        int i = 0;
        int n = names.length;
        while (i < n) {
            managers[i].init(this, names[i]);
            ++i;
        }
        this.basePath = config.getProperty("is.resultDirectory", "./public_html/");
        if (this.basePath.length() > 0) {
            File baseFile;
            if (!this.basePath.endsWith(File.separator)) {
                this.basePath = String.valueOf(this.basePath) + File.separator;
            }
            if (!(baseFile = new File(this.basePath)).exists() && !baseFile.mkdirs()) {
                throw new IllegalConfigurationException("simulation result directory '" + this.basePath + "' does not exist or is not " + "a directory");
            }
        }
        this.database = DatabaseUtils.createDatabase(config, "is.");
        this.userDatabase = DatabaseUtils.createUserDatabase(config, "is.", this.database);
        this.storeResults = config.getPropertyAsBoolean("is.database.results", false);
        this.userTable = this.userDatabase.getTable("users");
        if (this.userTable == null) {
            this.userTable = this.userDatabase.createTable("users");
            this.userTable.createField("id", 0, 32, 21);
            this.userTable.createField("parent", 0, 32, 0, new Integer(-1));
            this.userTable.createField("name", 4, 20, 0);
            this.userTable.createField("password", 4, 20, 0);
            this.userTable.createField("email", 4, 80, 0);
            this.createUser("admin", config.getProperty("admin.password", "secret_password"), null);
            this.userTable.flush();
        } else {
            if (!this.userTable.hasField("parent")) {
                this.userTable.createField("parent", 0, 32, 0, new Integer(-1));
                this.userTable.flush();
            }
            DBResult res = this.userTable.select();
            while (res.next()) {
                String name = res.getString("name");
                String password = res.getString("password");
                int userID = res.getInt("id");
                int parentID = res.getInt("parent");
                if (name == null) continue;
                this.agentLookup.setUser(name, password, userID, parentID);
            }
            res.close();
            String adminPassword = config.getProperty("admin.password");
            if (adminPassword != null && !this.agentLookup.validateAgent(0, adminPassword)) {
                this.agentLookup.setUser("admin", adminPassword, 0);
                DBMatcher matcher = new DBMatcher();
                DBObject object = new DBObject();
                matcher.setInt("id", 0);
                object.setString("password", adminPassword);
                this.userTable.update(matcher, object);
                this.userTable.flush();
            }
        }
        this.startViewerServer();
        this.httpHost = config.getProperty("is.http.host", config.getProperty("server.host"));
        this.httpPort = config.getPropertyAsInt("is.http.port", 8080);
        this.httpServer = new HttpServer();
        if (this.httpHost != null) {
            InetAddrPort addr = new InetAddrPort(this.httpHost, this.httpPort);
            this.httpSocketListener = new SocketListener(addr);
        } else {
            this.httpSocketListener = new SocketListener();
            this.httpSocketListener.setPort(this.httpPort);
        }
        this.httpSocketListener.setMaxThreads(30);
        this.httpServer.addListener((HttpListener)this.httpSocketListener);
        this.httpContext = this.httpServer.getContext("/");
        this.httpContext.setResourceBase(this.basePath);
        String accesslog = config.getProperty("is.http.accesslog");
        if (accesslog != null) {
            NCSARequestLog rLog = new NCSARequestLog(accesslog);
            rLog.setRetainDays(0);
            rLog.setAppend(true);
            rLog.setBuffered(false);
            this.httpServer.setRequestLog((RequestLog)rLog);
        }
        this.httpResourceHandler = new ResourceHandler();
        this.httpResourceHandler.setDirAllowed(false);
        this.httpResourceHandler.setAllowedMethods(new String[]{"GET", "HEAD"});
        this.httpResourceHandler.setAcceptRanges(true);
        String adminName = this.agentLookup.getAgentName(0);
        String adminPassword = this.agentLookup.getAgentPassword(0);
        this.agentRealm = new AgentRealm(this, this.serverType);
        if (adminName != null && adminPassword != null) {
            this.agentRealm.setAdminUser(adminName, adminPassword);
        }
        this.httpContext.addHandler((HttpHandler)new SecurityHandler());
        this.httpContext.setRealm((UserRealm)this.agentRealm);
        this.pageHandler = new PageHandler();
        this.httpContext.addHandler((HttpHandler)this.pageHandler);
        this.httpContext.addHandler((HttpHandler)this.httpResourceHandler);
        this.httpContext.addHandler((HttpHandler)new NotFoundHandler());
        String page = "<html><head><title>" + this.serverType + " " + this.infoServerName + "</title></head>\r\n" + "<FRAMESET BORDER=0 ROWS='105,*'>\r\n" + "<FRAME SRC='/top/'>" + "<FRAMESET BORDER=0 COLS='155,*'>\r\n" + "<FRAME SRC='/menu/'>\r\n" + "<FRAME SRC='/status/' NAME='content'>\r\n" + "</FRAMESET></FRAMESET>\r\n" + "</html>\r\n";
        this.pageHandler.addPage("/", new StaticPage("/", page));
        page = "<html><body style='margin-bottom: -25'>\r\n<table border=0 width='100%'><tr><td><img src='http://www.sics.se/tac/images/logo.gif'></td><td valign=top align=right><font face=arial><b>Trading Agent Competition</b></font><br><font face=arial size='-1' color='#900000'>" + this.serverType + " " + this.version + "</font></td></tr></table><hr>" + "</body></html>\r\n";
        this.pageHandler.addPage("/top/", new StaticPage("/top/", page));
        page = this.getMenuData();
        this.menuPage = new StaticPage("/menu/", page);
        this.pageHandler.addPage("/menu/", this.menuPage);
        page = this.getStatusData();
        this.statusPage = new StaticPage("/status/", page);
        this.pageHandler.addPage("/status/", this.statusPage);
        this.redirectPage = new RedirectPage();
        this.pageHandler.addPage("/admin/*", this.redirectPage);
        this.pageHandler.addPage("/games/*", this.redirectPage);
        this.pageHandler.addPage("/history/*", this.redirectPage);
        if (this.registrationURL == null || !config.getPropertyAsBoolean("is.registration.disabled", false)) {
            String notification = config.getProperty("is.registration.notification");
            String password = config.getProperty("is.registration.password");
            boolean isRemoteRegistrationEnabled = config.getPropertyAsBoolean("is.registration.remote", false);
            this.pageHandler.addPage("/register/", new RegistrationPage(this, notification, password, isRemoteRegistrationEnabled));
        }
        this.pageHandler.addPage("/notify/", new RegistrationNotificationPage(this));
        try {
            this.httpServer.start();
        }
        catch (Exception e) {
            throw (IOException)new IOException("could not start HTTP server").initCause(e);
        }
    }

    private String generateServerName() {
        return InetServer.getLocalHostName();
    }

    public ConfigManager getConfig() {
        return this.config;
    }

    public String getDefaultSimulationType() {
        return this.defaultSimulationType;
    }

    public PageHandler getPageHandler() {
        return this.pageHandler;
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    public int getHttpPort() {
        return this.httpPort;
    }

    public long getServerTimeSeconds() {
        return (System.currentTimeMillis() + (long)this.timeDiff) / 1000;
    }

    public long getServerTimeMillis() {
        return System.currentTimeMillis() + (long)this.timeDiff;
    }

    public static synchronized String getServerTimeAsString(long serverTime) {
        if (dFormat == null) {
            dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
            date = new Date(0);
        }
        date.setTime(serverTime);
        return dFormat.format(date);
    }

    public int getTimeZone() {
        return this.timeDiff / 3600000;
    }

    public void setTimeZone(int hoursFromUTC) {
        this.timeDiff = hoursFromUTC * 3600000;
    }

    void schedule(TimerTask task, long delay, long period) {
        this.timer.schedule(task, delay, period);
    }

    void schedule(TimerTask task, long delay) {
        this.timer.schedule(task, delay);
    }

    public SimServer getSimServer(String serverName) {
        SimServer[] servers = this.servers;
        int index = SimServer.indexOf(servers, serverName);
        return index >= 0 ? servers[index] : null;
    }

    public synchronized void addInfoConnection(InfoConnectionImpl connection) {
        String serverName = connection.getServerName();
        int index = SimServer.indexOf(this.servers, serverName);
        if (index >= 0) {
            this.servers[index].setInfoConnection(connection);
        } else {
            String path = String.valueOf(this.basePath) + serverName + File.separatorChar;
            Database serverDatabase = DatabaseUtils.createChildDatabase(this.config, "is.", serverName, this.database);
            SimServer server = new SimServer(this, serverDatabase, connection, path, this.storeResults);
            boolean firstServer = this.servers == null;
            this.servers = (SimServer[])ArrayUtils.add(SimServer.class, this.servers, server);
            this.menuPage.setPage(this.getMenuData());
            this.statusPage.setPage(this.getStatusData());
            if (firstServer) {
                this.redirectPage.setRedirectPath("/" + serverName, true);
            }
        }
    }

    public SimulationArchiver getSimulationArchiver() {
        if (this.simulationArchiver == null) {
            InfoServer infoServer = this;
            synchronized (infoServer) {
                if (this.simulationArchiver == null) {
                    this.simulationArchiver = new SimulationArchiver();
                }
            }
        }
        return this.simulationArchiver;
    }

    public InfoManager getInfoManager(String type) {
        return (InfoManager)this.managerTable.get(type);
    }

    public void serverMessageChanged(SimServer simServer) {
        this.statusPage.setPage(this.getStatusData());
    }

    public synchronized void addInfoManager(String type, InfoManager manager) {
        this.managerTable.put(type, manager);
    }

    private void startViewerServer() throws IOException {
        this.viewerServer = new BlockingViewerServer(this);
        this.viewerServer.start();
    }

    void serverClosed(BlockingViewerServer viewerServer) {
        if (this.viewerServer == viewerServer) {
            log.severe("VIEWER SERVER CLOSED!!!");
            try {
                this.startViewerServer();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not restart viewer", e);
            }
        }
    }

    public synchronized boolean updateUser(int id) {
        DBMatcher matcher = new DBMatcher();
        matcher.setInt("id", id);
        matcher.setLimit(1);
        return this.updateUser(matcher);
    }

    public synchronized boolean updateUser(String userName) {
        DBMatcher matcher = new DBMatcher();
        matcher.setString("name", userName);
        matcher.setLimit(1);
        return this.updateUser(matcher);
    }

    private synchronized boolean updateUser(DBMatcher matcher) {
        DBResult res = this.userTable.select(matcher);
        if (res.next()) {
            String name = res.getString("name");
            String password = res.getString("password");
            int userID = res.getInt("id");
            int parentID = res.getInt("parent");
            if (name != null) {
                this.agentLookup.setUser(name, password, userID, parentID);
                SimServer[] servers = this.servers;
                if (servers != null) {
                    int i = 0;
                    int n = servers.length;
                    while (i < n) {
                        servers[i].setUser(name, password, userID);
                        ++i;
                    }
                }
                this.agentRealm.updateUser(name);
                res.close();
                return true;
            }
        }
        res.close();
        return false;
    }

    public void validateUserInfo(String name, String password, String email) {
        if (name == null || (name = name.trim()).length() < 2) {
            throw new IllegalArgumentException("name must be at least 2 characters");
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException("too long name (max 20 characters)");
        }
        int i = 0;
        int n = name.length();
        while (i < n) {
            char c = name.charAt(i);
            if (c <= ' ') {
                throw new IllegalArgumentException("name may only contain characters or digits");
            }
            ++i;
        }
        if (name.equalsIgnoreCase("dummy") || name.equalsIgnoreCase("dummy-")) {
            throw new IllegalArgumentException("user '" + name + "' already exists");
        }
        if (password == null || (password = password.trim()).length() < 4) {
            throw new IllegalArgumentException("password must be at least 4 characters");
        }
        if (password.length() > 20) {
            throw new IllegalArgumentException("too long password (max 20 characters)");
        }
        if (email != null && email.length() > 80) {
            throw new IllegalArgumentException("too long email (max 80 characters)");
        }
        if (this.agentLookup.getAgentID(name) >= 0) {
            throw new IllegalArgumentException("user '" + name + "' already exists");
        }
        AgentInfo[] agents = this.agentLookup.getAgentInfos();
        if (agents != null) {
            int length = name.length();
            int i2 = 0;
            int n2 = agents.length;
            while (i2 < n2) {
                char c;
                AgentInfo agent = agents[i2];
                String u = agent.getName();
                if (u.length() == length + 1 && u.startsWith(name) && (c = u.charAt(length)) >= '0' && c <= '9') {
                    throw new IllegalArgumentException("user '" + name + "' already exists");
                }
                ++i2;
            }
        }
    }

    public synchronized int createUser(String name, String password, String email) {
        this.validateUserInfo(name, password, email);
        DBObject object = new DBObject();
        int id = this.userTable.getObjectCount();
        int userID = id * 11;
        object.setInt("id", userID);
        object.setString("name", name);
        object.setString("password", password);
        object.setString("email", email == null ? "" : email);
        this.userTable.insert(object);
        this.userTable.flush();
        this.agentLookup.setUser(name, password, userID);
        SimServer[] servers = this.servers;
        if (servers != null) {
            int i = 0;
            int n = servers.length;
            while (i < n) {
                servers[i].setUser(name, password, userID);
                ++i;
            }
        }
        return userID;
    }

    public synchronized int claimUser(String name, String password, String email) {
        boolean updatePassword;
        int userID = this.agentLookup.getAgentID(name);
        if (userID < 0 && this.updateUser(name)) {
            userID = this.agentLookup.getAgentID(name);
        }
        if (userID < 0 || userID % 11 != 0) {
            return this.createUser(name, password, email);
        }
        String pwd = this.agentLookup.getAgentPassword(userID);
        if (pwd == null) {
            throw new IllegalStateException("could not find password for agent " + name);
        }
        boolean bl = updatePassword = !pwd.equals(password);
        if (updatePassword || email != null) {
            DBMatcher matcher = new DBMatcher();
            DBObject object = new DBObject();
            matcher.setInt("id", userID);
            if (updatePassword) {
                object.setString("password", password);
            }
            if (email != null) {
                object.setString("email", email);
            }
            this.userTable.update(matcher, object);
            this.userTable.flush();
            if (updatePassword) {
                this.agentLookup.setUser(name, password, userID);
                SimServer[] servers = this.servers;
                if (servers != null) {
                    int i = 0;
                    int n = servers.length;
                    while (i < n) {
                        servers[i].setUser(name, password, userID);
                        ++i;
                    }
                }
                this.agentRealm.updateUser(name);
            }
        }
        return userID;
    }

    public String getUserName(int userID) {
        return this.agentLookup.getAgentName(userID);
    }

    public AgentInfo[] getAgentInfos() {
        return this.agentLookup.getAgentInfos();
    }

    public int getUserID(String name) {
        int userID = this.agentLookup.getAgentID(name);
        if (userID < 0 && this.updateUser(name)) {
            userID = this.agentLookup.getAgentID(name);
        }
        return userID;
    }

    public String getUserPassword(String name) {
        int userID = this.agentLookup.getAgentID(name);
        if (userID < 0 && this.updateUser(name)) {
            userID = this.agentLookup.getAgentID(name);
        }
        return userID >= 0 && userID % 11 == 0 ? this.agentLookup.getAgentPassword(userID) : null;
    }

    public boolean isAdministrator(int userID) {
        if (userID == 0) {
            return true;
        }
        return false;
    }

    private String getStatusData() {
        HtmlWriter page = new HtmlWriter();
        SimServer[] servers = this.servers;
        page.pageStart("Server Status");
        if (servers == null) {
            page.text("<em>No servers are running at this time.</em>");
        } else {
            int i = 0;
            int n = servers.length;
            while (i < n) {
                String message = servers[i].getServerMessage();
                page.h2("Server " + servers[i].getServerName() + " is " + (servers[i].isConnected() ? "running." : "offline."));
                if (message != null) {
                    page.text(message).p();
                }
                ++i;
            }
        }
        page.close();
        return page.toString();
    }

    private String getMenuData() {
        HtmlWriter page = new HtmlWriter();
        page.pageStart("Menu").attr("style", "margin-right: -25").table().attr("border", 0).attr("width", "100%");
        this.title(page, "Menu", false);
        this.link(page, "/status/", "Status");
        this.link(page, this.registrationURL != null ? this.registrationURL : "/register/", "Register new user");
        SimServer[] servers = this.servers;
        if (servers != null) {
            int i = 0;
            int n = servers.length;
            while (i < n) {
                String name = servers[i].getServerName();
                this.title(page, "Server " + name, true);
                this.link(page, String.valueOf('/') + name + "/games/", "Coming games (watch, create)");
                this.link(page, String.valueOf('/') + name + "/history/", "Game History");
                ++i;
            }
        }
        page.close();
        return page.toString();
    }

    private void title(HtmlWriter page, String title, boolean whitespace) {
        if (whitespace) {
            page.tr().td("&nbsp;");
        }
        page.tr().td().attr("bgcolor='#202080'").tag("font", "face='Arial,Helvetica,sans-serif' color=white").tag('b').text(title).tagEnd('b').tagEnd("font");
    }

    private void link(HtmlWriter page, String url, String text) {
        page.tr().td().tag('a').attr("href", url).attr("target=content").tag("font", "face='Arial,Helvetica,sans-serif'").text(text).tagEnd("font").tagEnd('a');
    }

    public String getVersion() {
        return this.version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    public String getServerType() {
        return this.serverType;
    }

    void setServerType(String serverType) {
        this.serverType = serverType;
    }
}

