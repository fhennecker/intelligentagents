/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.jfree.data.xy.XYSeries
 *  org.jfree.data.xy.XYSeriesCollection
 */
package edu.umich.eecs.tac.logviewer.gui.advertiser;

import edu.umich.eecs.tac.logviewer.TACAAVisualizerConstants;
import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.props.Query;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class AdvertiserRevCostPanel
extends JPanel {
    private XYSeriesCollection seriescollection;
    private XYSeries revSeries;
    private XYSeries costSeries;
    private int currentDay;
    private Set<Query> queries;
    private boolean showBorder;
    private Advertiser advertiser;

    public AdvertiserRevCostPanel(Advertiser advertiser, PositiveBoundedRangeModel dm, GameInfo gameInfo, boolean showBorder) {
        this.advertiser = advertiser;
        this.setBackground(TACAAVisualizerConstants.CHART_BACKGROUND);
        this.revSeries = new XYSeries((Comparable)((Object)"Revenue"));
        this.costSeries = new XYSeries((Comparable)((Object)"Cost"));
        this.seriescollection = new XYSeriesCollection();
        this.showBorder = showBorder;
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(1, 1));
        if (this.showBorder) {
            this.setBorder(BorderFactory.createTitledBorder("Revenue and Cost"));
        }
        this.queries = new HashSet<Query>();
        this.seriescollection.addSeries(this.revSeries);
        this.seriescollection.addSeries(this.costSeries);
    }
}

