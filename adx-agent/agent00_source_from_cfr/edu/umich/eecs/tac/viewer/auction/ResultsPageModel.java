/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.auction;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.auction.ResultsItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import se.sics.isl.transport.Transportable;

public class ResultsPageModel
extends AbstractListModel
implements ViewListener {
    private Query query;
    private List<ResultsItem> results;
    private Map<Integer, ResultsItem> items;
    private Map<Integer, String> names;

    public ResultsPageModel(Query query, TACAASimulationPanel simulationPanel) {
        this.query = query;
        this.results = new ArrayList<ResultsItem>();
        this.names = new HashMap<Integer, String>();
        this.items = new HashMap<Integer, ResultsItem>();
        simulationPanel.addViewListener(this);
    }

    @Override
    public int getSize() {
        return this.results.size();
    }

    @Override
    public Object getElementAt(int index) {
        return this.results.get(index);
    }

    public Query getQuery() {
        return this.query;
    }

    @Override
    public void dataUpdated(int agent, int type, int value) {
    }

    @Override
    public void dataUpdated(int agent, int type, long value) {
    }

    @Override
    public void dataUpdated(int agent, int type, float value) {
    }

    @Override
    public void dataUpdated(int agent, int type, double value) {
    }

    @Override
    public void dataUpdated(int agent, int type, String value) {
    }

    @Override
    public void dataUpdated(final int agent, int type, Transportable value) {
        if (type == 304 && value.getClass().equals(QueryReport.class)) {
            final QueryReport queryReport = (QueryReport)value;
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    String advertiser;
                    ResultsItem item = (ResultsItem)ResultsPageModel.this.items.get(agent);
                    if (item != null) {
                        ResultsPageModel.this.results.remove(item);
                    }
                    Ad ad = queryReport.getAd(ResultsPageModel.this.query);
                    double position = queryReport.getPosition(ResultsPageModel.this.query);
                    if (ad != null && !Double.isNaN(position) && (advertiser = (String)ResultsPageModel.this.names.get(agent)) != null) {
                        item = new ResultsItem(advertiser, ad, position);
                        ResultsPageModel.this.results.add(item);
                        ResultsPageModel.this.items.put(agent, item);
                    }
                    Collections.sort(ResultsPageModel.this.results);
                    ResultsPageModel.this.fireContentsChanged(this, 0, ResultsPageModel.this.getSize());
                }
            });
        }
    }

    @Override
    public void dataUpdated(int type, Transportable value) {
    }

    @Override
    public void participant(int agent, int role, String name, int participantID) {
        if (role == 1) {
            this.names.put(agent, name);
        }
    }

}

