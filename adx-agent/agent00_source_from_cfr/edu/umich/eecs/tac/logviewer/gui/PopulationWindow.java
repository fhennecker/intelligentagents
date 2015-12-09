/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.ProductPopulationPanel;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.RetailCatalog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PopulationWindow
extends JFrame {
    GameInfo gameInfo;

    public PopulationWindow(GameInfo gameInfo) {
        super("Users per product population");
        this.setDefaultCloseOperation(2);
        this.gameInfo = gameInfo;
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add((Component)this.createPopulationPane(), "Center");
        this.pack();
    }

    public JPanel createPopulationPane() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblConstraints = new GridBagConstraints();
        gblConstraints.fill = 1;
        JPanel pane = new JPanel();
        pane.setLayout(gbl);
        Product[] products = this.gameInfo.getRetailCatalog().keys().toArray(new Product[0]);
        gblConstraints.weightx = 1.0;
        gblConstraints.weighty = 1.0;
        gblConstraints.gridwidth = 1;
        int i = 0;
        while (i < 3) {
            int j = 0;
            while (j < 3) {
                gblConstraints.gridx = i;
                gblConstraints.gridy = j;
                ProductPopulationPanel current = new ProductPopulationPanel(this.gameInfo, products[i * 3 + j]);
                gbl.setConstraints(current.getMainPane(), gblConstraints);
                pane.add(current.getMainPane());
                ++j;
            }
            ++i;
        }
        return pane;
    }
}

