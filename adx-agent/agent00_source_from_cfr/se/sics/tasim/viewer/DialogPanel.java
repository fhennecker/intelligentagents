/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import se.sics.isl.gui.WindowBorder;
import se.sics.tasim.viewer.ViewerPanel;

public class DialogPanel
extends JPanel
implements MouseListener,
MouseMotionListener {
    private ViewerPanel viewerPanel;
    private WindowBorder windowBorder;
    private Point mousePoint = new Point();
    private int deltaX;
    private int deltaY;
    private int minX;
    private int maxX;
    private int maxY;
    private boolean isPressed;

    public DialogPanel(ViewerPanel viewerPanel, LayoutManager layout) {
        super(layout);
        this.viewerPanel = viewerPanel;
        this.windowBorder = new WindowBorder();
        this.setBorder(this.windowBorder);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == this && this.windowBorder.isInCloseButton(this, mouseEvent.getX(), mouseEvent.getY())) {
            this.viewerPanel.closeDialog();
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        int x;
        int y;
        if (SwingUtilities.isLeftMouseButton(mouseEvent) && this.windowBorder.isInTitle(this, x = mouseEvent.getX(), y = mouseEvent.getY())) {
            this.mousePoint.setLocation(x, y);
            SwingUtilities.convertPointToScreen(this.mousePoint, (Component)mouseEvent.getSource());
            this.deltaX = this.getX() - this.mousePoint.x;
            this.deltaY = this.getY() - this.mousePoint.y;
            Container parent = this.getParent();
            if (parent != null) {
                this.minX = 20 - this.getWidth();
                this.maxX = parent.getWidth() - 20;
                this.maxY = parent.getHeight() - this.windowBorder.getTitleHeight();
            } else {
                this.minX = 0;
                this.maxY = Integer.MAX_VALUE;
                this.maxX = Integer.MAX_VALUE;
            }
            this.isPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        this.isPressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.isPressed && SwingUtilities.isLeftMouseButton(e)) {
            this.mousePoint.setLocation(e.getX(), e.getY());
            SwingUtilities.convertPointToScreen(this.mousePoint, (Component)e.getSource());
            int newX = this.deltaX + this.mousePoint.x;
            int newY = this.deltaY + this.mousePoint.y;
            if (newX < this.minX) {
                newX = this.minX;
            } else if (newX > this.maxX) {
                newX = this.maxX;
            }
            if (newY < 0) {
                newY = 0;
            } else if (newY > this.maxY) {
                newY = this.maxY;
            }
            this.setLocation(newX, newY);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}

