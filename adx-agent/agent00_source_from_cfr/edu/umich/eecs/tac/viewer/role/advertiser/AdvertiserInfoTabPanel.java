/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.auction.ResultsPageModel;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserMainTabPanel;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserQueryTabPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JTabbedPane;
import se.sics.isl.transport.Transportable;
import tau.tac.adx.props.PublisherCatalog;

public class AdvertiserInfoTabPanel
extends SimulationTabPanel {
    private final int agent;
    private final String advertiser;
    private final TACAASimulationPanel simulationPanel;
    private JTabbedPane tabbedPane;
    private Map<Query, AdvertiserQueryTabPanel> advertiserQueryTabPanels;
    private final Map<Query, ResultsPageModel> models;
    private final Color legendColor;

    public AdvertiserInfoTabPanel(int agent, String advertiser, Map<Query, ResultsPageModel> models, TACAASimulationPanel simulationPanel, Color legendColor) {
        super(simulationPanel);
        this.agent = agent;
        this.advertiser = advertiser;
        this.simulationPanel = simulationPanel;
        this.models = models;
        this.legendColor = legendColor;
        simulationPanel.addViewListener(new CatalogListener(this, null));
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

    private void handleRetailCatalog(PublisherCatalog retailCatalog) {
        this.advertiserQueryTabPanels.clear();
        for (Query query : this.advertiserQueryTabPanels.keySet()) {
            this.tabbedPane.add(String.format("(%s,%s)", query.getManufacturer(), query.getComponent()), this.advertiserQueryTabPanels.get(query));
        }
        this.add(this.tabbedPane);
    }

    private void createAdvertiserQueryTabPanels(Query query) {
        ResultsPageModel model = this.models.get(query);
        if (model == null) {
            model = new ResultsPageModel(query, this.simulationPanel);
            this.models.put(query, model);
        }
        if (!this.advertiserQueryTabPanels.containsKey(query)) {
            this.advertiserQueryTabPanels.put(query, new AdvertiserQueryTabPanel(this.agent, this.advertiser, query, model, this.simulationPanel, this.legendColor));
        }
    }

    private class CatalogListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserInfoTabPanel this$0;

        private CatalogListener(AdvertiserInfoTabPanel advertiserInfoTabPanel) {
            this.this$0 = advertiserInfoTabPanel;
        }

        @Override
        public void dataUpdated(int type, Transportable value) {
            Class valueType = value.getClass();
            if (valueType == PublisherCatalog.class) {
                this.this$0.handleRetailCatalog((PublisherCatalog)value);
            }
        }

        /* synthetic */ CatalogListener(AdvertiserInfoTabPanel advertiserInfoTabPanel, CatalogListener catalogListener) {
            CatalogListener catalogListener2;
            catalogListener2(advertiserInfoTabPanel);
        }
    }

}

