/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BidBundle;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.publisher.RankingRenderer;
import edu.umich.eecs.tac.viewer.role.publisher.RankingTabPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import se.sics.isl.transport.Transportable;

public class RankingPanel
extends JPanel {
    private Query query;
    private Map<Integer, String> names;
    private Map<String, Color> colors;
    private MyTableModel model;

    public RankingPanel(Query query, RankingTabPanel rankingTabPanel) {
        super(new GridLayout(1, 0));
        this.query = query;
        this.names = new HashMap<Integer, String>();
        this.colors = new HashMap<String, Color>();
        this.initialize();
        rankingTabPanel.getSimulationPanel().addViewListener(new AuctionListener(this, null));
    }

    protected void initialize() {
        this.model = new MyTableModel(this, null);
        JTable table = new JTable(this.model);
        table.setDefaultRenderer(String.class, new RankingRenderer(Color.white, Color.black));
        table.setDefaultRenderer(Double.class, new RankingRenderer(Color.white, Color.black));
        table.setDefaultRenderer(Boolean.class, new RankingRenderer(Color.white, Color.black));
        table.setGridColor(Color.white);
        this.initColumnSizes(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Auction For (" + this.query.getManufacturer() + " , " + this.query.getComponent() + ")"));
        this.add(scrollPane);
    }

    public Query getQuery() {
        return this.query;
    }

    private void handleQueryReport(int agent, QueryReport queryReport) {
        String name = this.names.get(agent);
        if (name != null) {
            Ad ad = queryReport.getAd(this.query);
            double position = queryReport.getPosition(this.query);
            double promotedRatio = 0.0;
            if (queryReport.getImpressions(this.query) != 0) {
                promotedRatio = (double)queryReport.getPromotedImpressions(this.query) / (double)queryReport.getImpressions(this.query);
            }
            Color bkgndColor = promotedRatio > 0.5 ? Color.lightGray : Color.white;
            this.model.handleQueryReportItem(name, ad, position, bkgndColor);
        }
    }

    private void handleBidBundle(int agent, BidBundle bundle) {
        double bid;
        String name = this.names.get(agent);
        if (!(name == null || Double.NaN == (bid = bundle.getBid(this.query)) || Double.isNaN(Double.NaN) && Double.isNaN(bid))) {
            this.model.handleBidBundleItem(name, bid);
        }
    }

    private void initColumnSizes(JTable table) {
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        int i = 0;
        while (i < table.getColumnCount()) {
            TableColumn column = table.getColumnModel().getColumn(i);
            Component comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            int headerWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(headerWidth);
            ++i;
        }
    }

    private class AuctionListener
    extends ViewAdaptor {
        final /* synthetic */ RankingPanel this$0;

        private AuctionListener(RankingPanel rankingPanel) {
            this.this$0 = rankingPanel;
        }

        @Override
        public void dataUpdated(int agent, int type, Transportable value) {
            if (type == 304 && value.getClass().equals(QueryReport.class)) {
                this.this$0.handleQueryReport(agent, (QueryReport)value);
            } else if (type == 300 && value.getClass().equals(BidBundle.class)) {
                this.this$0.handleBidBundle(agent, (BidBundle)value);
            }
        }

        @Override
        public void participant(int agent, int role, String name, int participantID) {
            if (role == 1) {
                this.this$0.names.put(agent, name);
                int size = this.this$0.names.size();
                this.this$0.colors.put(name, TACAAViewerConstants.LEGEND_COLORS[size - 1]);
            }
        }

        /* synthetic */ AuctionListener(RankingPanel rankingPanel, AuctionListener auctionListener) {
            AuctionListener auctionListener2;
            auctionListener2(rankingPanel);
        }
    }

    public class MyTableModel
    extends AbstractTableModel {
        String[] columnNames;
        List<ResultsItem> data;
        Map<String, ResultsItem> map;
        final /* synthetic */ RankingPanel this$0;

        private MyTableModel(RankingPanel rankingPanel) {
            this.this$0 = rankingPanel;
            this.columnNames = new String[]{"Avg. Position", "    Advertiser    ", "  Bid ($)  ", "Targeted"};
            this.data = new ArrayList<ResultsItem>();
            this.map = new HashMap<String, ResultsItem>();
        }

        @Override
        public int getColumnCount() {
            return this.columnNames.length;
        }

        @Override
        public int getRowCount() {
            return this.data.size();
        }

        public Color getRowFgndColor(int row) {
            return this.data.get(row).getFgndColor();
        }

        public Color getRowBkgndColor(int row) {
            return this.data.get(row).getBkgndColor();
        }

        @Override
        public String getColumnName(int col) {
            return this.columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return this.data.get(row).getPosition();
            }
            if (col == 1) {
                return this.data.get(row).getAdvertiser();
            }
            if (col == 2) {
                return this.data.get(row).getBid();
            }
            if (col == 3) {
                return this.data.get(row).isTargeted();
            }
            return null;
        }

        public Class getColumnClass(int c) {
            return this.getValueAt(0, c).getClass();
        }

        public void handleQueryReportItem(final String name, final Ad ad, final double position, final Color bkgndColor) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    ResultsItem item = MyTableModel.this.map.get(name);
                    if (item != null) {
                        MyTableModel.this.data.remove(item);
                    } else {
                        item = new ResultsItem(name);
                        MyTableModel.this.map.put(name, item);
                    }
                    item.setAd(ad);
                    item.setPosition(position);
                    item.setFgndColor((Color)MyTableModel.this.this$0.colors.get(name));
                    item.setBkgndColor(bkgndColor);
                    if (!Double.isNaN(position)) {
                        MyTableModel.this.data.add(item);
                        Collections.sort(MyTableModel.this.data);
                    }
                    MyTableModel.this.fireTableDataChanged();
                }
            });
        }

        public void handleBidBundleItem(final String name, final double bid) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    ResultsItem item = MyTableModel.this.map.get(name);
                    if (item != null) {
                        MyTableModel.this.data.remove(item);
                    } else {
                        item = new ResultsItem(name);
                        MyTableModel.this.map.put(name, item);
                    }
                    item.setBid(bid);
                    if (!Double.isNaN(item.getPosition())) {
                        MyTableModel.this.data.add(item);
                        Collections.sort(MyTableModel.this.data);
                    }
                    MyTableModel.this.fireTableDataChanged();
                }
            });
        }

        /* synthetic */ MyTableModel(RankingPanel rankingPanel, MyTableModel myTableModel) {
            MyTableModel myTableModel2;
            myTableModel2(rankingPanel);
        }

    }

    private static class ResultsItem
    implements Comparable<ResultsItem> {
        private String advertiser;
        private Ad ad;
        private double position;
        private double bid;
        private Color fgndColor;
        private Color bkgndColor;

        public ResultsItem(String advertiser) {
            this.advertiser = advertiser;
            this.position = Double.NaN;
            this.bid = Double.NaN;
        }

        public void setAd(Ad ad) {
            this.ad = ad;
        }

        public void setPosition(double position) {
            this.position = position;
        }

        public void setBid(double bid) {
            this.bid = bid;
        }

        public void setBkgndColor(Color color) {
            this.bkgndColor = color;
        }

        public void setFgndColor(Color color) {
            this.fgndColor = color;
        }

        public double getBid() {
            return this.bid;
        }

        public String getAdvertiser() {
            return this.advertiser;
        }

        public Ad getAd() {
            return this.ad;
        }

        public double getPosition() {
            return this.position;
        }

        public Color getFgndColor() {
            return this.fgndColor;
        }

        public Color getBkgndColor() {
            return this.bkgndColor;
        }

        public boolean isTargeted() {
            if (this.getAd().getProduct() != null) {
                return true;
            }
            return false;
        }

        @Override
        public int compareTo(ResultsItem o) {
            return Double.compare(this.position, o.position);
        }
    }

}

