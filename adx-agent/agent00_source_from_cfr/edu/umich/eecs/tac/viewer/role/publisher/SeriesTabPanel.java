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
import edu.umich.eecs.tac.viewer.role.publisher.LegendPanel;
import edu.umich.eecs.tac.viewer.role.publisher.SeriesPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import se.sics.isl.transport.Transportable;

public class SeriesTabPanel
extends SimulationTabPanel {
    private Map<Query, SeriesPanel> seriesPanels;
    private AgentSupport agentSupport = new AgentSupport();

    public SeriesTabPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);
        simulationPanel.addViewListener(new BidBundleListener(this, null));
        simulationPanel.addViewListener(this.agentSupport);
        this.initialize();
    }

    private void initialize() {
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.seriesPanels = new HashMap<Query, SeriesPanel>();
    }

    private void handleRetailCatalog(RetailCatalog retailCatalog) {
        this.removeAll();
        this.seriesPanels.clear();
        for (Product product : retailCatalog) {
            Query f0 = new Query();
            Query f1Manufacturer = new Query(product.getManufacturer(), null);
            Query f1Component = new Query(null, product.getComponent());
            Query f2 = new Query(product.getManufacturer(), product.getComponent());
            this.addSeriesPanel(f0);
            this.addSeriesPanel(f1Manufacturer);
            this.addSeriesPanel(f1Component);
            this.addSeriesPanel(f2);
        }
        int panelCount = this.seriesPanels.size();
        Math.ceil(Math.sqrt(panelCount));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.ipady = 200;
        int index = 0;
        for (Query query : this.seriesPanels.keySet()) {
            SeriesPanel temp = this.seriesPanels.get(query);
            c.gridx = index / 4;
            c.gridy = index % 4;
            this.add((Component)temp, c);
            ++index;
        }
        LegendPanel legendPanel = new LegendPanel(this, TACAAViewerConstants.LEGEND_COLORS);
        c.fill = 0;
        c.gridx = 0;
        c.gridy = 4;
        c.ipadx = 500;
        c.ipady = 0;
        c.gridwidth = 4;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.insets = new Insets(5, 0, 0, 0);
        legendPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        c.anchor = 20;
        this.add((Component)legendPanel, c);
    }

    private void addSeriesPanel(Query query) {
        if (!this.seriesPanels.containsKey(query)) {
            this.seriesPanels.put(query, new SeriesPanel(query, this));
        }
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

    private class BidBundleListener
    extends ViewAdaptor {
        final /* synthetic */ SeriesTabPanel this$0;

        private BidBundleListener(SeriesTabPanel seriesTabPanel) {
            this.this$0 = seriesTabPanel;
        }

        @Override
        public void dataUpdated(int type, final Transportable value) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    Class valueType = value.getClass();
                    if (valueType == RetailCatalog.class) {
                        BidBundleListener.this.this$0.handleRetailCatalog((RetailCatalog)value);
                    }
                }
            });
        }

        /* synthetic */ BidBundleListener(SeriesTabPanel seriesTabPanel, BidBundleListener bidBundleListener) {
            BidBundleListener bidBundleListener2;
            bidBundleListener2(seriesTabPanel);
        }

    }

}

