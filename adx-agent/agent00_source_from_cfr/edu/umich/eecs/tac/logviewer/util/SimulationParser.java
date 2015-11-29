/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.util;

import com.botbox.util.ArrayUtils;
import edu.umich.eecs.tac.Parser;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.PublisherInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.props.SlotInfo;
import edu.umich.eecs.tac.props.UserPopulationState;
import java.awt.Color;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.ServerConfig;
import se.sics.tasim.props.StartInfo;

public class SimulationParser
extends Parser {
    private LogHandler logHandler;
    private ParserMonitor[] monitors;
    private int numberOfDays;
    private int simulationID;
    private String simulationType;
    private long startTime;
    private int secondsPerDay;
    private double squashingParameter;
    private String serverName;
    private String serverVersion;
    private ParticipantInfo[] participants;
    private int[] newParticipantIndex;
    private int publisherCount;
    private int advertiserCount;
    private int usersCount;
    private Advertiser[] advertisers;
    private Set<Query> querySpace;
    private int currentDay;
    private RetailCatalog retailCatalog;
    private ServerConfig serverConfig;
    private SlotInfo slotInfo;
    private UserPopulationState[] ups;
    private boolean errorParsing = false;
    private boolean isParserWarningsEnabled = true;

    public SimulationParser(LogHandler logHandler, LogReader lr) {
        super(lr);
        this.logHandler = logHandler;
        this.simulationID = lr.getSimulationID();
        this.simulationType = lr.getSimulationType();
        this.startTime = lr.getStartTime();
        this.serverName = lr.getServerName();
        this.serverVersion = lr.getServerVersion();
        this.isParserWarningsEnabled = logHandler.getConfig().getPropertyAsBoolean("visualizer.parserWarnings", true);
        this.participants = lr.getParticipants();
        this.newParticipantIndex = new int[this.participants.length];
        int i = 0;
        int n = this.participants.length;
        while (i < n) {
            switch (this.participants[i].getRole()) {
                case 0: {
                    ++this.publisherCount;
                    break;
                }
                case 1: {
                    ++this.advertiserCount;
                    break;
                }
                case 2: {
                    ++this.usersCount;
                }
            }
            ++i;
        }
    }

    @Override
    protected void parseStarted() {
        if (this.monitors != null) {
            int i = 0;
            int n = this.monitors.length;
            while (i < n) {
                this.monitors[i].parseStarted();
                ++i;
            }
        }
    }

    @Override
    protected void parseStopped() {
        System.err.println();
        if (this.monitors != null) {
            int i = 0;
            int n = this.monitors.length;
            while (i < n) {
                this.monitors[i].parseStopped();
                ++i;
            }
        }
    }

    @Override
    protected void message(int sender, int receiver, Transportable content) {
        if (content instanceof BankStatus) {
            this.handleMessage(sender, receiver, (BankStatus)content);
        } else if (content instanceof PublisherInfo) {
            this.handleMessage(sender, receiver, (PublisherInfo)content);
        } else if (content instanceof AdvertiserInfo) {
            this.handleMessage(sender, receiver, (AdvertiserInfo)content);
        } else if (content instanceof BidBundle) {
            this.handleMessage(sender, receiver, (BidBundle)content);
        } else if (content instanceof QueryReport) {
            this.handleMessage(sender, receiver, (QueryReport)content);
        } else if (content instanceof SalesReport) {
            this.handleMessage(sender, receiver, (SalesReport)content);
        } else if (content instanceof SlotInfo) {
            this.handleMessage(sender, receiver, (SlotInfo)content);
        }
        if (this.monitors != null) {
            int i = 0;
            int n = this.monitors.length;
            while (i < n) {
                this.monitors[i].message(sender, receiver, content);
                ++i;
            }
        }
    }

    @Override
    protected void dataUpdated(int type, Transportable content) {
        if (content instanceof StartInfo) {
            this.handleData((StartInfo)content);
        } else if (content instanceof RetailCatalog) {
            this.handleData((RetailCatalog)content);
        }
        if (this.monitors != null) {
            int i = 0;
            int n = this.monitors.length;
            while (i < n) {
                this.monitors[i].dataUpdated(type, content);
                ++i;
            }
        }
    }

    @Override
    protected void dataUpdated(int sender, int type, Transportable content) {
        if (content instanceof UserPopulationState) {
            this.handleData((UserPopulationState)content);
        }
    }

    @Override
    protected void data(Transportable object) {
        if (object instanceof ServerConfig) {
            this.handleData((ServerConfig)object);
        }
        if (this.monitors != null) {
            int i = 0;
            int n = this.monitors.length;
            while (i < n) {
                this.monitors[i].data(object);
                ++i;
            }
        }
    }

    private void handleMessage(int sender, int receiver, BankStatus content) {
        if (this.participants[receiver].getRole() == 1) {
            int index = this.newParticipantIndex[receiver];
            this.advertisers[index].setAccountBalance(this.currentDay - 1, content.getAccountBalance());
        }
    }

    private void handleMessage(int sender, int receiver, PublisherInfo content) {
        this.squashingParameter = content.getSquashingParameter();
    }

    private void handleMessage(int sender, int receiver, AdvertiserInfo content) {
        if (this.participants[receiver].getRole() == 1) {
            int index = this.newParticipantIndex[receiver];
            this.advertisers[index].setManufacturerSpecialty(content.getManufacturerSpecialty());
            this.advertisers[index].setComponentSpecialty(content.getComponentSpecialty());
            this.advertisers[index].setDistributionCapacity(content.getDistributionCapacity());
            this.advertisers[index].setDistributionWindow(content.getDistributionWindow());
        }
    }

    private void handleMessage(int sender, int receiver, BidBundle content) {
        if (this.participants[sender].getRole() == 1) {
            int index = this.newParticipantIndex[sender];
            this.advertisers[index].setBidBundle(content, this.currentDay);
        }
    }

    private void handleMessage(int sender, int receiver, QueryReport content) {
        if (this.participants[receiver].getRole() == 1) {
            int index = this.newParticipantIndex[receiver];
            this.advertisers[index].setQueryReport(content, this.currentDay);
        }
    }

    private void handleMessage(int sender, int receiver, SalesReport content) {
        if (this.participants[receiver].getRole() == 1) {
            int index = this.newParticipantIndex[receiver];
            this.advertisers[index].setSalesReport(content, this.currentDay);
        }
    }

    private void handleMessage(int sender, int receiver, SlotInfo content) {
        if (this.slotInfo == null) {
            this.slotInfo = content;
        }
    }

    private void handleData(RetailCatalog content) {
        if (this.retailCatalog == null) {
            this.retailCatalog = content;
        }
        if (this.querySpace == null) {
            this.generatePossibleQueries(content);
        }
    }

    private void generatePossibleQueries(RetailCatalog retailCatalog) {
        if (retailCatalog != null && this.querySpace == null) {
            this.querySpace = new HashSet<Query>();
            for (Product product : retailCatalog) {
                Query f0 = new Query();
                Query f1_manufacturer = new Query(product.getManufacturer(), null);
                Query f1_component = new Query(null, product.getComponent());
                Query f2 = new Query(product.getManufacturer(), product.getComponent());
                this.querySpace.add(f0);
                this.querySpace.add(f1_manufacturer);
                this.querySpace.add(f1_component);
                this.querySpace.add(f2);
            }
        }
    }

    private void handleData(ServerConfig content) {
        this.serverConfig = content;
        this.secondsPerDay = content.getAttributeAsInt("game.secondsPerDay", -1);
    }

    private void handleData(StartInfo startInfo) {
        this.numberOfDays = startInfo.getNumberOfDays();
        this.initActors();
    }

    private void handleData(UserPopulationState userPopulationState) {
        if (this.ups == null) {
            this.ups = new UserPopulationState[this.numberOfDays + 1];
        }
        this.ups[this.currentDay] = userPopulationState;
    }

    @Override
    protected void nextDay(int date, long serverTime) {
        int n;
        int i;
        this.currentDay = date;
        int done = (int)(10.0 * (double)(this.currentDay + 1) / (double)this.numberOfDays);
        int notDone = 10 - done;
        if (this.monitors != null) {
            i = 0;
            n = this.monitors.length;
            while (i < n) {
                this.monitors[i].nextDay(date, serverTime);
                ++i;
            }
        }
        System.err.print("Parsing game " + this.simulationID + ": [");
        i = 0;
        n = done;
        while (i < n) {
            System.err.print("*");
            ++i;
        }
        i = 0;
        n = notDone;
        while (i < n) {
            System.err.print("-");
            ++i;
        }
        System.err.print("]");
        System.err.print('\r');
    }

    @Override
    public void unhandledNode(String nodeName) {
        if (this.monitors != null) {
            int i = 0;
            int n = this.monitors.length;
            while (i < n) {
                this.monitors[i].unhandledNode(nodeName);
                ++i;
            }
        }
    }

    public void addMonitor(ParserMonitor monitor) {
        this.monitors = (ParserMonitor[])ArrayUtils.add(ParserMonitor.class, this.monitors, monitor);
    }

    public void removeMonitor(ParserMonitor monitor) {
        this.monitors = (ParserMonitor[])ArrayUtils.remove(this.monitors, monitor);
    }

    public ParserMonitor[] getMonitors() {
        return this.monitors;
    }

    public int getCurrentDay() {
        return this.currentDay;
    }

    public boolean errorParsing() {
        return this.errorParsing;
    }

    public int getSecondsPerDay() {
        return this.secondsPerDay;
    }

    public int getNumberOfDays() {
        return this.numberOfDays;
    }

    public int getSimulationID() {
        return this.simulationID;
    }

    public String getSimulationType() {
        return this.simulationType;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public String getServer() {
        return String.valueOf(this.serverName) + " (version " + this.serverVersion + ')';
    }

    public double getSquashingParameter() {
        return this.squashingParameter;
    }

    public RetailCatalog getRetailCatalog() {
        return this.retailCatalog;
    }

    public SlotInfo getSlotInfo() {
        return this.slotInfo;
    }

    public UserPopulationState[] getUserPopulationState() {
        return this.ups;
    }

    public Set<Query> getQuerySpace() {
        return this.querySpace;
    }

    public Advertiser[] getAdvertisers() {
        return this.advertisers;
    }

    private void warn(String message) {
        if (this.isParserWarningsEnabled) {
            int timeunit = this.currentDay;
            this.logHandler.warn("Parse: [Day " + (timeunit < 10 ? " " : "") + timeunit + "] " + message);
        }
    }

    private void initActors() {
        Color[] c_array = new Color[]{Color.BLUE, Color.CYAN, Color.GREEN, new Color(75, 0, 130), Color.RED, Color.MAGENTA, Color.ORANGE, Color.PINK};
        this.advertisers = new Advertiser[this.advertiserCount];
        int i = 0;
        int n = this.participants.length;
        while (i < n) {
            switch (this.participants[i].getRole()) {
                case 1: {
                    this.advertisers[this.newParticipantIndex[i]] = new Advertiser(this.participants[i].getIndex(), this.participants[i].getAddress(), this.participants[i].getName(), this.numberOfDays, c_array[this.newParticipantIndex[i]]);
                    break;
                }
                case 0: {
                    break;
                }
            }
            ++i;
        }
    }

    protected static class CommMessageKey
    implements Comparable {
        int id;
        int sender;
        int day;

        CommMessageKey(int id, int sender, int day) {
            this.id = id;
            this.sender = sender;
            this.day = day;
        }

        public int compareTo(Object o) {
            CommMessageKey cm = (CommMessageKey)o;
            if (cm.day < this.day) {
                return -1;
            }
            if (cm.day > this.day) {
                return 1;
            }
            if (cm.sender < this.sender) {
                return -1;
            }
            if (cm.sender > this.sender) {
                return 1;
            }
            if (cm.id < this.id) {
                return -1;
            }
            if (cm.id > this.id) {
                return 1;
            }
            return 0;
        }
    }

}

