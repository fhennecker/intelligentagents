/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx;

import com.botbox.util.ArrayUtils;
import edu.umich.eecs.tac.Parser;
import edu.umich.eecs.tac.Participant;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SlotInfo;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.ServerConfig;

public class TACAdxSimulationInfo
extends Parser {
    private static final Logger log = Logger.getLogger(TACAdxSimulationInfo.class.getName());
    private int simID;
    private int uniqueID;
    private String simType;
    private String simParams;
    private long startTime;
    private int simLength;
    private String serverName;
    private String serverVersion;
    private ServerConfig serverConfig;
    private SlotInfo slotInfo;
    private RetailCatalog retailCatalog;
    private Participant[] participants;
    private Hashtable participantTable;
    private int[] agentRoles;
    private Participant[][] agentsPerRole;
    private int agentRoleNumber;
    private int currentDate = 0;
    private boolean isParsingExtended = false;

    public TACAdxSimulationInfo(LogReader logReader) throws IOException, ParseException {
        super(logReader);
        ParticipantInfo[] infos = logReader.getParticipants();
        this.participants = new Participant[infos == null ? 0 : infos.length];
        this.participantTable = new Hashtable();
        int i = 0;
        int n = this.participants.length;
        while (i < n) {
            ParticipantInfo info = infos[i];
            if (info != null) {
                this.participants[i] = new Participant(info);
                this.participantTable.put(info.getAddress(), this.participants[i]);
            }
            ++i;
        }
        this.simID = logReader.getSimulationID();
        this.uniqueID = logReader.getUniqueID();
        this.simType = logReader.getSimulationType();
        this.simParams = logReader.getSimulationParams();
        this.startTime = logReader.getStartTime();
        this.simLength = logReader.getSimulationLength();
        this.serverName = logReader.getServerName();
        this.serverVersion = logReader.getServerVersion();
        this.start();
        Participant[] advertisers = this.getParticipantsByRole(1);
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getServerVersion() {
        return this.serverVersion;
    }

    public int getUniqueID() {
        return this.uniqueID;
    }

    public int getSimulationID() {
        return this.simID;
    }

    public String getSimulationType() {
        return this.simType;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int getSimulationLength() {
        return this.simLength;
    }

    public ServerConfig getServerConfig() {
        return this.serverConfig;
    }

    public Participant getParticipant(int agentIndex) {
        Participant p;
        if (agentIndex >= this.participants.length || (p = this.participants[agentIndex]) == null) {
            throw new IllegalArgumentException("no participant " + agentIndex);
        }
        return p;
    }

    public Participant getParticipant(String address) {
        Participant p = (Participant)this.participantTable.get(address);
        if (p == null) {
            throw new IllegalArgumentException("no participant " + address);
        }
        return p;
    }

    public int getParticipantCount() {
        return this.participants.length;
    }

    public Participant[] getParticipants() {
        return this.participants;
    }

    public Participant[] getParticipantsByRole(int role) {
        int index = ArrayUtils.indexOf(this.agentRoles, 0, this.agentRoleNumber, role);
        if (index < 0) {
            if (this.agentRoles == null) {
                this.agentRoles = new int[5];
                this.agentsPerRole = new Participant[5][];
            } else if (this.agentRoleNumber == this.agentRoles.length) {
                this.agentRoles = ArrayUtils.setSize(this.agentRoles, this.agentRoleNumber + 5);
                this.agentsPerRole = (Participant[][])ArrayUtils.setSize((Object[])this.agentsPerRole, this.agentRoleNumber + 5);
            }
            ArrayList<Participant> list = new ArrayList<Participant>();
            int i = 0;
            int n = this.participants.length;
            while (i < n) {
                Participant a = this.participants[i];
                if (a != null && a.getInfo().getRole() == role) {
                    list.add(a);
                }
                ++i;
            }
            index = this.agentRoleNumber;
            this.agentsPerRole[this.agentRoleNumber] = list.size() > 0 ? list.toArray(new Participant[list.size()]) : null;
            this.agentRoles[this.agentRoleNumber++] = role;
        }
        return this.agentsPerRole[index];
    }

    @Override
    protected void messageToRole(int sender, int role, Transportable content) {
        int i = 0;
        int n = this.participants.length;
        while (i < n) {
            if (this.participants[i].getInfo().getRole() == role) {
                this.participants[i].messageReceived(this.currentDate, sender, content);
            }
            ++i;
        }
        if (sender != 0) {
            this.getParticipant(sender).messageSentToRole(this.currentDate, role, content);
        }
    }

    @Override
    protected void message(int sender, int receiver, Transportable content) {
        if (receiver != 0) {
            this.getParticipant(receiver).messageReceived(this.currentDate, sender, content);
            if (sender != 0) {
                this.getParticipant(sender).messageSent(this.currentDate, receiver, content);
            }
        }
    }

    @Override
    protected void data(Transportable object) {
        if (object instanceof ServerConfig) {
            this.serverConfig = (ServerConfig)object;
        }
    }

    @Override
    protected void dataUpdated(int agentIndex, int type, int value) {
        switch (type) {
            case 407: {
                this.impressions(agentIndex, value);
            }
        }
    }

    @Override
    protected void dataUpdated(int agentIndex, int type, long value) {
    }

    @Override
    protected void dataUpdated(int agentIndex, int type, double value) {
        switch (type) {
            case 409: {
                this.revenue(agentIndex, value);
                break;
            }
            case 412: {
                this.adxCost(agentIndex, value);
                break;
            }
            case 411: {
                this.ucsCost(agentIndex, value);
                break;
            }
            case 408: {
                this.qualityRating(agentIndex, value);
                break;
            }
            case 413: {
                Participant p = this.getParticipant(agentIndex);
                p.setResult(value);
            }
        }
    }

    @Override
    protected void dataUpdated(int agent, int type, float value) {
    }

    @Override
    protected void dataUpdated(int agent, int type, String value) {
    }

    @Override
    protected void dataUpdated(int agent, int type, Transportable content) {
    }

    @Override
    protected void dataUpdated(int type, Transportable object) {
        if (object instanceof SlotInfo) {
            this.slotInfo = (SlotInfo)object;
        } else if (object instanceof RetailCatalog) {
            this.retailCatalog = (RetailCatalog)object;
        }
    }

    private void qualityRating(int agentIndex, double value) {
        Participant p = this.getParticipant(agentIndex);
        p.setQualityRating(value);
    }

    private void ucsCost(int agentIndex, double value) {
        Participant p = this.getParticipant(agentIndex);
        p.addUCSCost(value);
    }

    private void adxCost(int agentIndex, double value) {
        Participant p = this.getParticipant(agentIndex);
        p.addADXCost(value);
    }

    private void revenue(int agentIndex, double value) {
        Participant p = this.getParticipant(agentIndex);
        p.addRevenue(value);
    }

    protected void impressions(int agent, long amount) {
        Participant agentInfo = this.getParticipant(agent);
        agentInfo.addImpressions(amount);
    }

    @Override
    protected void nextDay(int date, long serverTime) {
        this.currentDate = date;
    }

    @Override
    protected void unhandledNode(String nodeName) {
        log.warning("ignoring unhandled node '" + nodeName + '\'');
    }
}

