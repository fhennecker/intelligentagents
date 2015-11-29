/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.adnet;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.auction.ResultsPageModel;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserMainTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserQueryTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.CampaignGrpahsTabPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;
import tau.tac.adx.agents.CampaignData;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.InitialCampaignMessage;

public class AdNetInfoTabPanel
extends SimulationTabPanel {
    private final int agent;
    private final String advertiser;
    private final TACAASimulationPanel simulationPanel;
    private JTabbedPane tabbedPane;
    private Map<Query, AdvertiserQueryTabPanel> advertiserQueryTabPanels;
    private final Map<Query, ResultsPageModel> models;
    private final Color legendColor;
    private int day;
    private CampaignData pendingCampaign;

    public AdNetInfoTabPanel(int agent, String advertiser, Map<Query, ResultsPageModel> models, TACAASimulationPanel simulationPanel, Color legendColor) {
        super(simulationPanel);
        this.agent = agent;
        this.advertiser = advertiser;
        this.simulationPanel = simulationPanel;
        this.models = models;
        this.legendColor = legendColor;
        simulationPanel.addViewListener(new CatalogListener(this, null));
        simulationPanel.addViewListener(new DataUpdateListener(this, null));
        simulationPanel.addTickListener(new DayListener());
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.advertiserQueryTabPanels = new HashMap<Query, AdvertiserQueryTabPanel>();
        this.tabbedPane = new JTabbedPane(4);
        this.tabbedPane.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.tabbedPane.add("Main", new AdvertiserMainTabPanel(this.simulationPanel, this.agent, this.advertiser, this.legendColor));
    }

    private void handleCampaign(CampaignOpportunityMessage value) {
        this.advertiserQueryTabPanels.clear();
        Query f0 = new Query();
        for (Query query : this.advertiserQueryTabPanels.keySet()) {
            this.tabbedPane.add(String.format("(%s,%s)", query.getManufacturer(), query.getComponent()), this.advertiserQueryTabPanels.get(query));
        }
        this.add(this.tabbedPane);
    }

    protected void updateCampaigns(AdNetworkDailyNotification campaignMessage) {
        if (this.pendingCampaign.getId() == campaignMessage.getCampaignId() && campaignMessage.getCostMillis() != 0) {
            CampaignGrpahsTabPanel campaignGrpahsTabPanel = new CampaignGrpahsTabPanel(this.simulationPanel, this.agent, this.advertiser, this.legendColor, campaignMessage.getCampaignId(), this.pendingCampaign.getReachImps());
            this.tabbedPane.add("Day " + (this.day + 1), campaignGrpahsTabPanel);
            this.tabbedPane.repaint();
            this.tabbedPane.revalidate();
        }
    }

    protected void updateCampaigns(InitialCampaignMessage campaignMessage) {
        this.tabbedPane.add("Day 0", new CampaignGrpahsTabPanel(this.simulationPanel, this.agent, this.advertiser, this.legendColor, campaignMessage.getId(), campaignMessage.getReachImps()));
        this.tabbedPane.repaint();
        this.tabbedPane.revalidate();
    }

    protected void handleCampaignOpportunityMessage(CampaignOpportunityMessage com) {
        this.pendingCampaign = new CampaignData(com);
    }

    static /* synthetic */ void access$0(AdNetInfoTabPanel adNetInfoTabPanel, int n) {
        adNetInfoTabPanel.day = n;
    }

    private class CatalogListener
    extends ViewAdaptor {
        final /* synthetic */ AdNetInfoTabPanel this$0;

        private CatalogListener(AdNetInfoTabPanel adNetInfoTabPanel) {
            this.this$0 = adNetInfoTabPanel;
        }

        @Override
        public void dataUpdated(int type, Transportable value) {
            if (value instanceof CampaignOpportunityMessage) {
                this.this$0.handleCampaign((CampaignOpportunityMessage)value);
            }
        }

        /* synthetic */ CatalogListener(AdNetInfoTabPanel adNetInfoTabPanel, CatalogListener catalogListener) {
            CatalogListener catalogListener2;
            catalogListener2(adNetInfoTabPanel);
        }
    }

    private class DataUpdateListener
    extends ViewAdaptor {
        final /* synthetic */ AdNetInfoTabPanel this$0;

        private DataUpdateListener(AdNetInfoTabPanel adNetInfoTabPanel) {
            this.this$0 = adNetInfoTabPanel;
        }

        @Override
        public void dataUpdated(final int agentId, final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (agentId == DataUpdateListener.this.this$0.agent) {
                        switch (type) {
                            case 406: {
                                DataUpdateListener.this.this$0.updateCampaigns((AdNetworkDailyNotification)value);
                                break;
                            }
                            case 403: {
                                if (DataUpdateListener.this.this$0.agent != agentId) break;
                                DataUpdateListener.this.this$0.updateCampaigns((InitialCampaignMessage)value);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void dataUpdated(final int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    switch (type) {
                        case 404: {
                            DataUpdateListener.this.this$0.handleCampaignOpportunityMessage((CampaignOpportunityMessage)value);
                        }
                    }
                }
            });
        }

        /* synthetic */ DataUpdateListener(AdNetInfoTabPanel adNetInfoTabPanel, DataUpdateListener dataUpdateListener) {
            DataUpdateListener dataUpdateListener2;
            dataUpdateListener2(adNetInfoTabPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            AdNetInfoTabPanel.access$0(AdNetInfoTabPanel.this, simulationDate);
        }
    }

}

