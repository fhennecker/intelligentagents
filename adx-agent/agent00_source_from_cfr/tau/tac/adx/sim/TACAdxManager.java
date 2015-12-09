/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.sim;

import java.util.Hashtable;
import java.util.logging.Logger;
import se.sics.isl.util.ConfigManager;
import se.sics.tasim.is.InfoConnection;
import se.sics.tasim.is.SimulationInfo;
import se.sics.tasim.is.common.Competition;
import se.sics.tasim.is.common.InfoConnectionImpl;
import se.sics.tasim.is.common.SimServer;
import se.sics.tasim.sim.Admin;
import se.sics.tasim.sim.Simulation;
import se.sics.tasim.sim.SimulationManager;
import tau.tac.adx.sim.TACAdxConstants;
import tau.tac.adx.sim.TACAdxSimulation;

public class TACAdxManager
extends SimulationManager {
    private static final Logger log = Logger.getLogger(TACAdxManager.class.getName());
    private static final int DEFAULT_SIM_LENGTH = 600;
    static final int NUMBER_OF_ADVERTISERS = 8;
    private static Hashtable<String, Config> configTable = new Hashtable();

    @Override
    protected void init() {
        int i = 0;
        int n = TACAdxConstants.SUPPORTED_TYPES.length;
        while (i < n) {
            this.init(TACAdxConstants.SUPPORTED_TYPES[i]);
            ++i;
        }
    }

    private void init(String type) {
        configTable.put(type, new Config(type));
        this.registerType(type);
    }

    protected boolean isSupportedSimulationType(String type) {
        if (configTable.get(type) != null) {
            return true;
        }
        return false;
    }

    protected ConfigManager getSimulationConfig(Config simConfig) {
        if (simConfig.simulationConfig == null) {
            ConfigManager config;
            simConfig.simulationConfig = config = this.loadSimulationConfig(simConfig.type);
            simConfig.simulationLength = config.getPropertyAsInt("game.length", 600) * 1000;
            simConfig.numberOfAdvertisers = config.getPropertyAsInt("game.numberOfAdvertisers", 8);
        }
        return simConfig.simulationConfig;
    }

    @Override
    public SimulationInfo createSimulationInfo(String type, String params) {
        Config simConfig = configTable.get(type);
        if (simConfig != null) {
            this.getSimulationConfig(simConfig);
            return this.createSimulationInfo(type, params, simConfig.simulationLength);
        }
        return null;
    }

    @Override
    public boolean join(int agentID, int role, SimulationInfo info) {
        Config simConfig = configTable.get(info.getType());
        if (simConfig == null) {
            return false;
        }
        this.getSimulationConfig(simConfig);
        if (role == 5 && !info.isFull() && info.getParticipantCount() < simConfig.numberOfAdvertisers) {
            info.addParticipant(agentID, 5);
            if (info.getParticipantCount() >= simConfig.numberOfAdvertisers) {
                info.setFull();
            }
            return true;
        }
        return false;
    }

    @Override
    public String getSimulationRoleName(String type, int simRole) {
        return configTable.get(type) != null ? TACAdxSimulation.getSimulationRoleName(simRole) : null;
    }

    @Override
    public int getSimulationRoleID(String type, String simRole) {
        return configTable.get(type) != null ? (simRole == null ? 5 : TACAdxSimulation.getSimulationRole(simRole)) : 0;
    }

    @Override
    public int getSimulationLength(String type, String params) {
        Config simConfig = configTable.get(type);
        if (simConfig == null) {
            return 600;
        }
        this.getSimulationConfig(simConfig);
        return simConfig.simulationLength;
    }

    @Override
    public Simulation createSimulation(SimulationInfo info) {
        Config simConfig = configTable.get(info.getType());
        if (simConfig == null) {
            throw new IllegalArgumentException("simulation type " + info.getType() + " not supported");
        }
        ConfigManager config = this.getSimulationConfig(simConfig);
        Competition competition = this.findContainingCompetition(info.getSimulationID());
        return new TACAdxSimulation(config, competition);
    }

    private Competition findContainingCompetition(int simulationId) {
        Competition competition = null;
        Competition[] competitions = this.findCompetitions();
        int i = competitions.length - 1;
        while (i >= 0) {
            Competition c = competitions[i];
            if (c != null && c.containsSimulation(simulationId)) {
                competition = c;
                break;
            }
            --i;
        }
        return competition;
    }

    private SimServer findSimServer() {
        SimServer simServer = null;
        InfoConnection infoConnection = this.findInfoConnection();
        if (infoConnection instanceof InfoConnectionImpl) {
            InfoConnectionImpl infoConnectionImpl = (InfoConnectionImpl)infoConnection;
            simServer = infoConnectionImpl.getSimServer();
        }
        return simServer;
    }

    private InfoConnection findInfoConnection() {
        Admin admin = this.getAdmin();
        InfoConnection infoConnection = null;
        if (admin != null) {
            infoConnection = admin.getInfoConnection();
        }
        return infoConnection;
    }

    private Competition[] findCompetitions() {
        SimServer simServer = this.findSimServer();
        Competition[] competitions = null;
        if (simServer != null) {
            competitions = simServer.getCompetitions();
        }
        return competitions == null ? new Competition[]{} : competitions;
    }

    private static class Config {
        public final String type;
        public ConfigManager simulationConfig;
        public int simulationLength = 600000;
        public int numberOfAdvertisers = 8;

        public Config(String type) {
            this.type = type;
        }
    }

}

