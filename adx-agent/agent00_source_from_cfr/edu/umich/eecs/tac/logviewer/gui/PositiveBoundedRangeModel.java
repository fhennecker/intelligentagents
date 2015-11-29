/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.logviewer.gui;

import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class PositiveBoundedRangeModel {
    protected ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    protected int last = 0;
    protected int current = 0;

    public int getLast() {
        return this.last;
    }

    public void setLast(int newLast) {
        this.setDayProperties(this.current, newLast);
    }

    public int getCurrent() {
        return this.current;
    }

    public void setCurrent(int newCurrent) {
        this.setDayProperties(newCurrent, this.last);
    }

    public void changeCurrent(int change) {
        this.setDayProperties(this.current + change, this.last);
    }

    public void setDayProperties(int newCurrent, int newLast) {
        if (newCurrent > newLast || newCurrent < 0 || newLast < 0 || newCurrent == this.current && newLast == this.last) {
            return;
        }
        this.current = newCurrent;
        this.last = newLast;
        this.fireStateChanged();
    }

    public void addChangeListener(ChangeListener l) {
        this.listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        this.listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged() {
        Object[] listeners = this.listenerList.getListenerList();
        int i = listeners.length - 2;
        while (i >= 0) {
            if (listeners[i] == ChangeListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i + 1]).stateChanged(this.changeEvent);
            }
            i -= 2;
        }
    }
}

