/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.gui.PositiveRangeDiagram;
import edu.umich.eecs.tac.logviewer.gui.advertiser.AdvertiserWindow;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

public class AdvertiserPanel {
    JPanel mainPane;
    JPanel diagramPane;
    JLabel name;
    JLabel manufacturer;
    JLabel component;
    JLabel capacity;
    PositiveRangeDiagram accountDiagram;
    Advertiser advertiser;
    PositiveBoundedRangeModel dayModel;
    AdvertiserWindow advertiserWindow;
    GameInfo gameInfo;
    ParserMonitor[] monitors;

    public AdvertiserPanel(GameInfo gameInfo, Advertiser advertiser, PositiveBoundedRangeModel dayModel, ParserMonitor[] monitors) {
        this.dayModel = dayModel;
        this.advertiser = advertiser;
        this.gameInfo = gameInfo;
        this.monitors = monitors;
        this.mainPane = new JPanel();
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createBevelBorder(0));
        this.mainPane.addMouseListener(new MouseInputAdapter(){

            @Override
            public void mouseClicked(MouseEvent me) {
                AdvertiserPanel.this.openAgentWindow();
            }
        });
        this.name = new JLabel(advertiser.getName());
        this.name.setForeground(advertiser.getColor());
        this.manufacturer = new JLabel("Manufacturer: " + advertiser.getManufacturerSpecialty());
        this.component = new JLabel("Component: " + advertiser.getComponentSpecialty());
        this.capacity = new JLabel("Capacity: " + advertiser.getDistributionCapacity());
        this.mainPane.add(this.name);
        this.mainPane.add(this.manufacturer);
        this.mainPane.add(this.component);
        this.mainPane.add(this.capacity);
    }

    protected void openAgentWindow() {
        if (this.advertiserWindow == null) {
            this.advertiserWindow = new AdvertiserWindow(this.gameInfo, this.advertiser, this.dayModel, this.monitors);
            this.advertiserWindow.setLocationRelativeTo(this.mainPane);
            this.advertiserWindow.setVisible(true);
        } else if (this.advertiserWindow.isVisible()) {
            this.advertiserWindow.toFront();
        } else {
            this.advertiserWindow.setVisible(true);
        }
        int state = this.advertiserWindow.getExtendedState();
        if ((state & 1) != 0) {
            this.advertiserWindow.setExtendedState(state & -2);
        }
    }

    public Component getMainPane() {
        return this.mainPane;
    }

}

