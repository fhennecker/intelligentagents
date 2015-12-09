/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.adnet;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.auction.AverageRankingPanel;
import edu.umich.eecs.tac.viewer.auction.ResultsPageModel;
import edu.umich.eecs.tac.viewer.role.AgentSupport;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserQueryCountPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserQueryInfoPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserQueryPositionPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserQueryValuePanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;

public class AdNetQueryTabPanel
extends JPanel {
    private final int agent;
    private final String advertiser;
    private final Query query;
    private final ResultsPageModel resultsPageModel;
    private final TACAASimulationPanel simulationPanel;
    private final Color legendColor;
    private final AgentSupport agentSupport;

    public AdNetQueryTabPanel(int agent, String advertiser, Query query, ResultsPageModel resultsPageModel, TACAASimulationPanel simulationPanel, Color legendColor) {
        this.agent = agent;
        this.advertiser = advertiser;
        this.query = query;
        this.simulationPanel = simulationPanel;
        this.legendColor = legendColor;
        this.resultsPageModel = resultsPageModel;
        this.agentSupport = new AgentSupport();
        simulationPanel.addViewListener(this.agentSupport);
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 2));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 4.0;
        c.fill = 1;
        AverageRankingPanel averageRankingPanel = new AverageRankingPanel(this.resultsPageModel);
        leftPanel.add((Component)averageRankingPanel, c);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = 1;
        AdvertiserQueryInfoPanel queryInfoPanel = new AdvertiserQueryInfoPanel(this.agent, this.advertiser, this.query, this.simulationPanel);
        leftPanel.add((Component)queryInfoPanel, c);
        this.add(leftPanel);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = 1;
        AdvertiserQueryPositionPanel positionPanel = new AdvertiserQueryPositionPanel(this.agent, this.advertiser, this.query, this.simulationPanel, this.legendColor);
        rightPanel.add((Component)positionPanel, c);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 3.0;
        c.fill = 1;
        AdvertiserQueryCountPanel queryCountPanel = new AdvertiserQueryCountPanel(this.agent, this.advertiser, this.query, this.simulationPanel, this.legendColor);
        rightPanel.add((Component)queryCountPanel, c);
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        AdvertiserQueryValuePanel queryValuePanel = new AdvertiserQueryValuePanel(this.agent, this.advertiser, this.query, this.simulationPanel);
        rightPanel.add((Component)queryValuePanel, c);
        this.add(rightPanel);
    }

    public int getAgentCount() {
        return this.agentSupport.size();
    }

    public int getAgent(int index) {
        return this.agentSupport.agent(index);
    }

    public int getRole(int index) {
        return this.agentSupport.role(index);
    }

    public int getParticipant(int index) {
        return this.agentSupport.participant(index);
    }

    public int indexOfAgent(int agent) {
        return this.agentSupport.indexOfAgent(agent);
    }

    public String getAgentName(int index) {
        return this.agentSupport.name(index);
    }
}

