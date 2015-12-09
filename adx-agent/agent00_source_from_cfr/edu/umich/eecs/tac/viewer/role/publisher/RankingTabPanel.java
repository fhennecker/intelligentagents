/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.AgentSupport;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.publisher.RankingPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

public class RankingTabPanel
extends SimulationTabPanel {
    private Map<Query, RankingPanel> rankingPanels;
    private AgentSupport agentSupport = new AgentSupport();

    public RankingTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        simulationPanel.addViewListener(new CatalogListener(this, null));
        simulationPanel.addViewListener(this.agentSupport);
        simulationPanel.addTickListener(new DayListener());
        this.initialize();
    }

    private void initialize() {
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.rankingPanels = new HashMap<Query, RankingPanel>();
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.removeAll();
        this.rankingPanels.clear();
        for (Product product : retailCatalog) {
            Query f0 = new Query();
            Query f1Manufacturer = new Query(product.getManufacturer(), null);
            Query f1Component = new Query(null, product.getComponent());
            Query f2 = new Query(product.getManufacturer(), product.getComponent());
            this.addRankingPanel(f0);
            this.addRankingPanel(f1Manufacturer);
            this.addRankingPanel(f1Component);
            this.addRankingPanel(f2);
        }
        int panelCount = this.rankingPanels.size();
        int sideCount = (int)Math.ceil(Math.sqrt(panelCount));
        this.setLayout(new GridLayout(sideCount, sideCount));
        for (Query query : this.rankingPanels.keySet()) {
            this.add(this.rankingPanels.get(query));
        }
    }

    private void addRankingPanel(Query query) {
        if (!this.rankingPanels.containsKey(query)) {
            this.rankingPanels.put(query, new RankingPanel(query, this));
        }
    }

    protected void tick(long serverTime) {
    }

    protected void simulationTick(long serverTime, int simulationDate) {
        this.setBorder(BorderFactory.createTitledBorder(String.format("Auction Results for Day %s", simulationDate - 1)));
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

    private class CatalogListener
    extends ViewAdaptor {
        final /* synthetic */ RankingTabPanel this$0;

        private CatalogListener(RankingTabPanel rankingTabPanel) {
            this.this$0 = rankingTabPanel;
        }

        @Override
        public void dataUpdated(int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    Class valueType = value.getClass();
                    if (valueType == RetailCatalog.class) {
                        CatalogListener.this.this$0.handleRetailCatalog((RetailCatalog)value);
                    }
                }
            });
        }

        /* synthetic */ CatalogListener(RankingTabPanel rankingTabPanel, CatalogListener catalogListener) {
            CatalogListener catalogListener2;
            catalogListener2(rankingTabPanel);
        }

    }

    protected class DayListener
    implements TickListener {
        protected DayListener() {
        }

        @Override
        public void tick(long serverTime) {
            RankingTabPanel.this.tick(serverTime);
        }

        @Override
        public void simulationTick(long serverTime, int simulationDate) {
            RankingTabPanel.this.simulationTick(serverTime, simulationDate);
        }
    }

}

