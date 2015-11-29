/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.viewer.GraphicUtils;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewAdaptor;
import edu.umich.eecs.tac.viewer.ViewListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import se.sics.isl.transport.Transportable;

public class AdvertiserPropertiesPanel
extends JPanel {
    private int agent;
    private JLabel manufacturerLabel;
    private JLabel componentLabel;

    public AdvertiserPropertiesPanel(int agent, String name, TACAASimulationPanel simulationPanel) {
        this.agent = agent;
        simulationPanel.addViewListener(new AdvertiserInfoListener(this, null));
        this.initialize();
    }

    private void initialize() {
        this.setLayout(new GridLayout(2, 1));
        this.setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        this.setBorder(BorderFactory.createTitledBorder("Specialty Information"));
        this.manufacturerLabel = new JLabel(new ImageIcon());
        this.manufacturerLabel.setBorder(BorderFactory.createTitledBorder("Manufacturer"));
        this.add(this.manufacturerLabel);
        this.componentLabel = new JLabel(new ImageIcon());
        this.componentLabel.setBorder(BorderFactory.createTitledBorder("Component"));
        this.add(this.componentLabel);
    }

    private class AdvertiserInfoListener
    extends ViewAdaptor {
        final /* synthetic */ AdvertiserPropertiesPanel this$0;

        private AdvertiserInfoListener(AdvertiserPropertiesPanel advertiserPropertiesPanel) {
            this.this$0 = advertiserPropertiesPanel;
        }

        @Override
        public void dataUpdated(int agent, int type, Transportable value) {
            if (agent == this.this$0.agent && type == 307 && value.getClass() == AdvertiserInfo.class) {
                AdvertiserInfo info = (AdvertiserInfo)value;
                String component = info.getComponentSpecialty();
                String manufacturer = info.getManufacturerSpecialty();
                ImageIcon icon = GraphicUtils.iconForComponent(component);
                if (icon != null) {
                    this.this$0.componentLabel.setIcon(icon);
                }
                if ((icon = GraphicUtils.iconForManufacturer(manufacturer)) != null) {
                    this.this$0.manufacturerLabel.setIcon(icon);
                }
            }
        }

        /* synthetic */ AdvertiserInfoListener(AdvertiserPropertiesPanel advertiserPropertiesPanel, AdvertiserInfoListener advertiserInfoListener) {
            AdvertiserInfoListener advertiserInfoListener2;
            advertiserInfoListener2(advertiserPropertiesPanel);
        }
    }

}

