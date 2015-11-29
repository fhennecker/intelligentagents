/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.inject.Guice
 *  com.google.inject.Injector
 *  com.google.inject.Module
 */
package tau.tac.adx.sim;

import com.botbox.util.ArrayUtils;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.sim.Bank;
import edu.umich.eecs.tac.sim.BankStatusSender;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.aw.TimeListener;
import se.sics.tasim.is.EventWriter;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.props.ServerConfig;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.sim.LogWriter;
import se.sics.tasim.sim.Simulation;
import se.sics.tasim.sim.SimulationAgent;
import tau.tac.adx.AdxManager;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.DefaultAdxUsers;
import tau.tac.adx.agents.DemandAgent;
import tau.tac.adx.auction.manager.AdxBidManager;
import tau.tac.adx.auction.tracker.AdxBidTracker;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.report.adn.AdNetworkReportSender;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.InitialCampaignMessage;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportSender;
import tau.tac.adx.sim.AdxAgentRepository;
import tau.tac.adx.sim.AdxAuctioneer;
import tau.tac.adx.sim.AdxUsers;
import tau.tac.adx.sim.Publisher;
import tau.tac.adx.sim.TACAdxConstants;
import tau.tac.adx.sim.config.AdxConfigurationParser;
import tau.tac.adx.users.AdxUser;
import tau.tac.adx.util.AdxModule;

public class TACAdxSimulation
extends Simulation
implements AdxAgentRepository,
BankStatusSender,
AdxPublisherReportSender,
AdNetworkReportSender {
    private Bank bank;
    private final String timeUnitName = "Day";
    private int currentTimeUnit = 0;
    private int secondsPerDay = 10;
    private int numberOfDays = 60;
    private int numberOfAdvertisers = 8;
    private int pingInterval = 0;
    private int nextPingRequest = 0;
    private int nextPingReport = 0;
    private PublisherCatalog publisherCatalog;
    private final String[] advertiserAddresses = new String[this.numberOfAdvertisers];
    private final String[] adxAdvertiserAddresses = new String[this.numberOfAdvertisers];
    private Map<String, AdvertiserInfo> advertiserInfoMap;
    private List<AdxUser> userPopulation;
    private Map<Device, Integer> deviceDistributionMap;
    private Map<AdType, Integer> adTypeDistributionMap;
    private final Random random;
    private Competition competition;
    private final Injector injector = Guice.createInjector((Module[])new Module[]{new AdxModule()});
    private final Runnable afterTickTarget;
    public static EventBus eventBus = new EventBus("AdX");
    private boolean recoverAgents;
    private final AdxAuctioneer auctioneer;
    private static final Logger log = Logger.getLogger(TACAdxSimulation.class.getName());
    private DefaultAdxUsers adxAgent;
    private DemandAgent demandAgent;

    public String[] getAdxAdvertiserAddresses() {
        return this.adxAdvertiserAddresses;
    }

    public TACAdxSimulation(ConfigManager config, Competition competition) {
        super(config);
        this.afterTickTarget = new Runnable(){

            @Override
            public void run() {
                TACAdxSimulation.this.handleAfterTick();
            }
        };
        this.recoverAgents = false;
        this.auctioneer = (AdxAuctioneer)this.injector.getInstance((Class)AdxAuctioneer.class);
        this.setCompetition(competition);
        this.random = new Random();
        AdxManager.getInstance().setSimulation(this);
    }

    public TACAdxSimulation(ConfigManager config) {
        super(config);
        this.afterTickTarget = new ;
        this.recoverAgents = false;
        this.auctioneer = (AdxAuctioneer)this.injector.getInstance((Class)AdxAuctioneer.class);
        this.random = new Random();
        AdxManager.getInstance().setSimulation(this);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Map<String, AdvertiserInfo> getAdvertiserInfo() {
        return this.getAdvertiserInfoMap();
    }

    @Override
    protected void setupSimulation() throws IllegalConfigurationException {
        ConfigManager config = this.getConfig();
        SimulationInfo info = this.getSimulationInfo();
        AdxConfigurationParser adxConfigurationParser = new AdxConfigurationParser(config);
        int seconds = info.getParameter("secondsPerDay", 0);
        int n = this.secondsPerDay = seconds <= 1 ? config.getPropertyAsInt("game.secondsPerDay", this.secondsPerDay) : seconds;
        if (this.secondsPerDay < 1) {
            this.secondsPerDay = 1;
        }
        this.numberOfDays = info.getSimulationLength() / (this.secondsPerDay * 1000);
        int pingIntervalSeconds = config.getPropertyAsInt("ping.interval", 0);
        if (pingIntervalSeconds > 0) {
            this.pingInterval = pingIntervalSeconds / this.secondsPerDay;
            if (this.pingInterval <= 1) {
                this.pingInterval = 1;
            }
            this.nextPingRequest = this.pingInterval;
            this.nextPingReport = this.pingInterval + 1;
        } else {
            this.pingInterval = 0;
        }
        this.numberOfAdvertisers = config.getPropertyAsInt("game.numberOfAdvertisers", 8);
        log.info("TACAA Simulation " + info.getSimulationID() + " is setting up...");
        this.setBank(new Bank(this, this.getSimulationInfo(), this.numberOfAdvertisers));
        this.createBuiltinAgents("adxusers", 3, DefaultAdxUsers.class);
        log.info("Created Adx Agent");
        this.createBuiltinAgents("demand", 4, DemandAgent.class);
        log.info("Created Demand Agent");
        this.publisherCatalog = adxConfigurationParser.createPublisherCatalog();
        this.userPopulation = adxConfigurationParser.createUserPopulation();
        this.deviceDistributionMap = adxConfigurationParser.createDeviceDistributionMap();
        this.adTypeDistributionMap = adxConfigurationParser.createAdTypeDistributionMap();
        this.validateConfiguration();
        int i = 0;
        int n2 = info.getParticipantCount();
        while (i < n2) {
            this.createExternalAgent("adv" + (i + 1), 5, info.getParticipantID(i));
            ++i;
        }
        if (info.getParticipantCount() < this.numberOfAdvertisers) {
            this.createDummies("dummy.adnetwork", 5, this.numberOfAdvertisers - info.getParticipantCount());
        }
        adxConfigurationParser.initializeAdvertisers(this);
        AdxManager.getInstance().setup();
        SimulationAgent adxSimulationAgent = this.getAgents(3)[0];
        this.adxAgent = (DefaultAdxUsers)adxSimulationAgent.getAgent();
        this.adxAgent.simulationSetup(this, adxSimulationAgent.getIndex());
        this.addTimeListener(this.adxAgent);
        SimulationAgent demandSimulationAgent = this.getAgents(4)[0];
        this.demandAgent = (DemandAgent)demandSimulationAgent.getAgent();
        this.demandAgent.simulationSetup(this, demandSimulationAgent.getIndex());
        this.addTimeListener(this.demandAgent);
    }

    @Override
    protected String getTimeUnitName() {
        return "Day";
    }

    @Override
    protected int getTimeUnitCount() {
        return this.numberOfDays;
    }

    @Override
    protected void startSimulation() {
        LogWriter logWriter = this.getLogWriter();
        ConfigManager config = this.getConfig();
        ServerConfig serverConfig = new ServerConfig(config);
        logWriter.write(serverConfig);
        SimulationInfo simInfo = this.getSimulationInfo();
        StartInfo startInfo = this.createStartInfo(simInfo);
        startInfo.lock();
        logWriter.dataUpdated(0, startInfo);
        this.sendToRole(5, startInfo);
        this.recoverAgents = true;
        this.sendToRole(5, this.publisherCatalog);
        SimulationAgent[] arrsimulationAgent = this.getPublishers();
        int n = arrsimulationAgent.length;
        int n2 = 0;
        while (n2 < n) {
            SimulationAgent publisher = arrsimulationAgent[n2];
            Publisher publisherAgent = (Publisher)publisher.getAgent();
            publisherAgent.sendPublisherInfoToAll();
            ++n2;
        }
        for (Map.Entry<String, AdvertiserInfo> entry : this.getAdvertiserInfoMap().entrySet()) {
            this.sendMessage(entry.getKey(), entry.getValue());
            this.getEventWriter().dataUpdated(this.agentIndex(entry.getKey()), 307, entry.getValue());
        }
        this.startTickTimer(simInfo.getStartTime(), this.secondsPerDay * 1000);
        logWriter.commit();
    }

    private StartInfo createStartInfo(SimulationInfo info) {
        return new StartInfo(info.getSimulationID(), info.getStartTime(), info.getSimulationLength(), this.secondsPerDay);
    }

    @Override
    protected void prepareStopSimulation() {
        this.recoverAgents = false;
        this.getBank().sendBankStatusToAll();
        SimulationAgent[] arrsimulationAgent = this.getAgents(3);
        int n = arrsimulationAgent.length;
        int n2 = 0;
        while (n2 < n) {
            SimulationAgent agent = arrsimulationAgent[n2];
            if (agent.getAgent() instanceof AdxUsers) {
                AdxUsers adxUsers = (AdxUsers)agent.getAgent();
                adxUsers.sendReportsToAll();
            }
            ++n2;
        }
        int millisConsumed = (int)(this.getServerTime() - this.getSimulationInfo().getEndTime());
        SimulationStatus status = new SimulationStatus(this.numberOfDays, millisConsumed, true);
        this.sendToRole(5, status);
    }

    @Override
    protected void completeStopSimulation() {
        LogWriter writer = this.getLogWriter();
        writer.commit();
    }

    @Override
    protected void preNextTimeUnit(int timeUnit) {
        this.auctioneer.applyBidUpdates();
        this.adxAgent.preNextTimeUnit(timeUnit);
        this.demandAgent.preNextTimeUnit(timeUnit);
    }

    @Override
    protected void nextTimeUnitStarted(int timeUnit) {
        this.currentTimeUnit = timeUnit;
        LogWriter writer = this.getLogWriter();
        writer.nextTimeUnit(timeUnit, this.getServerTime());
        if (timeUnit >= this.numberOfDays) {
            this.requestStopSimulation();
        } else {
            this.getBank().sendBankStatusToAll();
        }
    }

    @Override
    protected void nextTimeUnitFinished(int timeUnit) {
        if (timeUnit < this.numberOfDays) {
            int millisConsumed = (int)(this.getServerTime() - this.getSimulationInfo().getStartTime() - (long)(timeUnit * this.secondsPerDay * 1000));
            SimulationStatus status = new SimulationStatus(timeUnit, millisConsumed);
            this.sendToRole(5, status);
        }
        this.invokeLater(this.afterTickTarget);
    }

    private void handleAfterTick() {
        if (this.pingInterval > 0 && this.currentTimeUnit < this.numberOfDays) {
            SimulationAgent[] advertisers;
            if (this.currentTimeUnit >= this.nextPingRequest) {
                this.nextPingRequest += this.pingInterval;
                advertisers = this.getAgents(5);
                if (advertisers != null) {
                    int i = 0;
                    int n = advertisers.length;
                    while (i < n) {
                        advertisers[i].requestPing();
                        ++i;
                    }
                }
            }
            if (this.currentTimeUnit >= this.nextPingReport) {
                this.nextPingReport += this.pingInterval;
                advertisers = this.getAgents(5);
                if (advertisers != null) {
                    EventWriter writer;
                    EventWriter n = writer = this.getEventWriter();
                    synchronized (n) {
                        int i = 0;
                        int n2 = advertisers.length;
                        while (i < n2) {
                            SimulationAgent sa = advertisers[i];
                            if (sa.getPingCount() > 0) {
                                int index = sa.getIndex();
                                writer.dataUpdated(index, 64, sa.getAverageResponseTime());
                                writer.dataUpdated(index, 65, sa.getLastResponseTime());
                            }
                            ++i;
                        }
                    }
                }
            }
        }
        System.gc();
        System.gc();
    }

    private void validateConfiguration() throws IllegalConfigurationException {
    }

    @Override
    protected int getAgentRecoverMode(SimulationAgent agent) {
        if (!this.recoverAgents) {
            return 0;
        }
        if (agent.hasAgentBeenActive()) {
            return 2;
        }
        this.recoverAgent(agent);
        return 0;
    }

    @Override
    protected void recoverAgent(SimulationAgent agent) {
        if (this.recoverAgents) {
            log.warning("recovering agent " + agent.getName());
            String agentAddress = agent.getAddress();
            StartInfo info = this.createStartInfo(this.getSimulationInfo());
            info.lock();
            this.sendMessage(new Message(agentAddress, info));
            this.sendMessage(new Message(agentAddress, this.publisherCatalog));
            this.sendMessage(new Message(agentAddress, this.getAdvertiserInfoMap().get(agentAddress)));
            SimulationAgent[] arrsimulationAgent = this.getPublishers();
            int n = arrsimulationAgent.length;
            int n2 = 0;
            while (n2 < n) {
                SimulationAgent publisher = arrsimulationAgent[n2];
                Publisher publisherAgent = (Publisher)publisher.getAgent();
                publisherAgent.sendPublisherInfo(agentAddress);
                ++n2;
            }
        }
    }

    @Override
    protected void messageReceived(Message message) {
        log.warning("received (ignoring) " + message);
    }

    public static String getSimulationRoleName(int simRole) {
        return simRole >= 0 && simRole < TACAdxConstants.ROLE_NAME.length ? TACAdxConstants.ROLE_NAME[simRole] : null;
    }

    public static int getSimulationRole(String role) {
        return ArrayUtils.indexOf(TACAdxConstants.ROLE_NAME, role);
    }

    @Override
    protected boolean validateMessage(SimulationAgent receiverAgent, Message message) {
        int type;
        int senderIndex;
        String sender = message.getSender();
        SimulationAgent senderAgent = this.getAgent(sender);
        if (senderAgent == null) {
            senderIndex = 0;
        } else {
            if (senderAgent.getRole() == receiverAgent.getRole()) {
                return false;
            }
            senderIndex = senderAgent.getIndex();
        }
        int receiverIndex = receiverAgent.getIndex();
        Transportable content = message.getContent();
        Class contentType = content.getClass();
        if (this.logContentType(contentType)) {
            LogWriter writer = this.getLogWriter();
            writer.message(senderIndex, receiverIndex, content, this.getServerTime());
            writer.commit();
        }
        if ((type = this.getContentType(contentType)) != 0) {
            this.getEventWriter().interaction(senderIndex, receiverIndex, type);
        }
        return true;
    }

    @Override
    protected boolean validateMessageToRole(SimulationAgent sender, int role, Transportable content) {
        if (content instanceof AdxBidBundle) {
            return true;
        }
        return true;
    }

    @Override
    protected boolean validateMessageToRole(int role, Transportable content) {
        this.logToRole(0, role, content);
        return true;
    }

    private void logToRole(int senderIndex, int role, Transportable content) {
        int type;
        Class contentType = content.getClass();
        if (this.logContentType(contentType)) {
            LogWriter writer = this.getLogWriter();
            writer.messageToRole(senderIndex, role, content, this.getServerTime());
            writer.commit();
        }
        if ((type = this.getContentType(contentType)) != 0) {
            this.getEventWriter().interactionWithRole(senderIndex, role, type);
        }
    }

    private boolean logContentType(Class type) {
        if (type != StartInfo.class) {
            return true;
        }
        return false;
    }

    private int getContentType(Class type) {
        return 0;
    }

    @Override
    public final int getNumberOfAdvertisers() {
        return this.numberOfAdvertisers;
    }

    @Override
    public final SimulationAgent[] getPublishers() {
        return new SimulationAgent[0];
    }

    @Override
    public final String[] getAdvertiserAddresses() {
        return this.advertiserAddresses;
    }

    public final String getAgentName(String agentAddress) {
        SimulationAgent agent = this.getAgent(agentAddress);
        return agent != null ? agent.getName() : agentAddress;
    }

    public final void transaction(String source, String recipient, double amount) {
    }

    @Override
    public void sendBankStatus(String agentName, BankStatus status) {
        this.sendMessage(agentName, status);
        EventWriter eventWriter = this.getEventWriter();
        eventWriter.dataUpdated(this.getAgent(agentName).getIndex(), 413, status.getAccountBalance());
    }

    public final void sendInitialCampaign(String agentName, InitialCampaignMessage initialCampaignMessage) {
        this.sendMessage(agentName, initialCampaignMessage);
        this.getEventWriter().dataUpdated(this.agentIndex(agentName), 403, initialCampaignMessage);
    }

    public final void sendCampaignOpportunity(CampaignOpportunityMessage campaignOpportunityMessage) {
        this.sendToRole(5, campaignOpportunityMessage);
        this.getEventWriter().dataUpdated(404, campaignOpportunityMessage);
    }

    public final void sendCampaignReport(String agentName, CampaignReport campaignReport) {
        this.sendMessage(agentName, campaignReport);
        this.getEventWriter().dataUpdated(this.agentIndex(agentName), 405, campaignReport);
    }

    public final void sendDemandDailyNotification(String agentName, AdNetworkDailyNotification dailyNotification) {
        this.sendMessage(agentName, dailyNotification);
        this.getEventWriter().dataUpdated(this.agentIndex(agentName), 406, dailyNotification);
    }

    public void broadcastImpressions(String advertiser, int impressions) {
        this.getEventWriter().dataUpdated(this.agentIndex(advertiser), 301, impressions);
    }

    @Override
    public void broadcastPublisherReport(AdxPublisherReport report) {
        this.sendToRole(5, report);
        this.getEventWriter().dataUpdated(400, report);
    }

    public void sendCampaignAuctionReport(CampaignAuctionReport campaignAuctionReport) {
        this.getEventWriter().dataUpdated(414, campaignAuctionReport);
    }

    public void broadcastClicks(String advertiser, int clicks) {
        this.getEventWriter().dataUpdated(this.agentIndex(advertiser), 302, clicks);
    }

    @Override
    public PublisherCatalog getPublisherCatalog() {
        return this.publisherCatalog;
    }

    @Override
    public List<AdxUser> getUserPopulation() {
        return this.userPopulation;
    }

    @Override
    public Map<Device, Integer> getDeviceDistributionMap() {
        return this.deviceDistributionMap;
    }

    @Override
    public Map<AdType, Integer> getAdTypeDistributionMap() {
        return this.adTypeDistributionMap;
    }

    @Override
    public void broadcastAdNetowrkReport(String bidder, AdNetworkReport report) {
        this.sendMessage(bidder, report);
        this.getEventWriter().dataUpdated(this.agentIndex(bidder), 401, report);
        int value = 0;
        for (AdNetworkKey adNetworkKey : report) {
            value += report.getAdNetworkReportEntry(adNetworkKey).getWinCount();
        }
        this.getEventWriter().dataUpdated(this.agentIndex(bidder), 407, value);
        this.broadcastADXExpense(bidder, report.getDailyCost());
    }

    public void broadcastAdNetworkQualityRating(String adNet, double rating) {
        this.getEventWriter().dataUpdated(this.agentIndex(adNet), 408, rating);
    }

    public void broadcastAdNetworkRevenue(String adNet, double revenue) {
        this.getBank().deposit(adNet, revenue);
        this.getEventWriter().dataUpdated(this.agentIndex(adNet), 409, revenue);
    }

    public void broadcastUCSWin(String adNet, double expense) {
        this.getEventWriter().dataUpdated(this.agentIndex(adNet), 411, expense);
        this.broadcastAdNetworkExpense(adNet, expense);
    }

    public void broadcastADXExpense(String adNet, double expense) {
        this.getEventWriter().dataUpdated(this.agentIndex(adNet), 412, expense);
        this.broadcastAdNetworkExpense(adNet, expense);
    }

    public void broadcastAdNetworkExpense(String adNet, double expense) {
        this.getBank().withdraw(adNet, expense);
        this.getEventWriter().dataUpdated(this.agentIndex(adNet), 410, expense);
    }

    @Override
    public AdxAuctioneer getAuctioneer() {
        return this.auctioneer;
    }

    public Map<String, AdvertiserInfo> getAdvertiserInfoMap() {
        return this.advertiserInfoMap;
    }

    public void setAdvertiserInfoMap(Map<String, AdvertiserInfo> advertiserInfoMap) {
        this.advertiserInfoMap = advertiserInfoMap;
    }

    public Competition getCompetition() {
        return this.competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Bank getBank() {
        return this.bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    @Override
    public AdxBidManager getAdxBidManager() {
        return (AdxBidManager)this.injector.getInstance((Class)AdxBidManager.class);
    }

    @Override
    public AdxBidTracker getAdxBidTracker() {
        return (AdxBidTracker)this.injector.getInstance((Class)AdxBidTracker.class);
    }

}

