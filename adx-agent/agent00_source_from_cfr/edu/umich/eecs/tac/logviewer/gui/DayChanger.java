/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DayChanger {
    protected final int SLIDER_DELAY = 150;
    JPanel mainPane;
    JPanel buttonPane;
    JSlider daySlider;
    JButton nextDayButton;
    JButton prevDayButton;
    JButton lastDayButton;
    JButton firstDayButton;
    JLabel dayLabel;
    ActionListener actionListeners = null;
    PositiveBoundedRangeModel dayModel;

    public DayChanger(PositiveBoundedRangeModel dm) {
        this.dayModel = dm;
        this.mainPane = new JPanel();
        this.mainPane.setLayout(new BoxLayout(this.mainPane, 1));
        this.mainPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Day Changer "));
        this.buttonPane = new JPanel();
        this.buttonPane.setLayout(new BoxLayout(this.buttonPane, 0));
        this.daySlider = new JSlider();
        this.daySlider.setMinimum(0);
        this.daySlider.setMaximum(this.dayModel.getLast());
        this.daySlider.setValue(this.dayModel.getCurrent());
        this.daySlider.addChangeListener(new ChangeListener(){
            int value;
            Timer timer;

            @Override
            public void stateChanged(ChangeEvent ce) {
                this.value = DayChanger.this.daySlider.getValue();
                if (!this.timer.isRunning()) {
                    this.timer.start();
                }
            }

            static /* synthetic */ DayChanger access$0( var0) {
                return var0.DayChanger.this;
            }

        });
        this.dayLabel = new JLabel(String.valueOf(this.dayModel.getCurrent()) + " / " + this.dayModel.getLast());
        this.dayModel.addChangeListener(new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent ce) {
                DayChanger.this.daySlider.setMaximum(DayChanger.this.dayModel.getLast());
                DayChanger.this.daySlider.setValue(DayChanger.this.dayModel.getCurrent());
                DayChanger.this.dayLabel.setText(String.valueOf(DayChanger.this.dayModel.getCurrent()) + " / " + DayChanger.this.dayModel.getLast());
            }
        });
        this.nextDayButton = new JButton(">");
        this.prevDayButton = new JButton("<");
        this.lastDayButton = new JButton(">|");
        this.firstDayButton = new JButton("|<");
        this.nextDayButton.setAlignmentX(0.0f);
        this.prevDayButton.setAlignmentX(0.0f);
        this.lastDayButton.setAlignmentX(1.0f);
        this.firstDayButton.setAlignmentX(1.0f);
        this.nextDayButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                DayChanger.this.dayModel.changeCurrent(1);
            }
        });
        this.prevDayButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                DayChanger.this.dayModel.changeCurrent(-1);
            }
        });
        this.firstDayButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                DayChanger.this.dayModel.setCurrent(0);
            }
        });
        this.lastDayButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                DayChanger.this.dayModel.setCurrent(DayChanger.this.dayModel.getLast());
            }
        });
        this.buttonPane.add(this.firstDayButton);
        this.buttonPane.add(this.prevDayButton);
        this.buttonPane.add(Box.createHorizontalGlue());
        this.buttonPane.add(this.dayLabel);
        this.buttonPane.add(Box.createHorizontalGlue());
        this.buttonPane.add(this.nextDayButton);
        this.buttonPane.add(this.lastDayButton);
        this.mainPane.add(this.buttonPane);
        this.mainPane.add(this.daySlider);
    }

    public JPanel getMainPane() {
        return this.mainPane;
    }

}

