/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.gui;

import javax.swing.AbstractListModel;

public class MessageModel
extends AbstractListModel {
    public static final int NONE = 0;
    public static final int WARNING = 1;
    private String[] messages;
    private int[] messageFlag;
    private int messagePos = 0;
    private int size = 0;

    public MessageModel(int messageCount) {
        this.messages = new String[messageCount];
        this.messageFlag = new int[messageCount];
    }

    public void addMessage(String message) {
        this.addMessage(message, 0);
    }

    public void addMessage(String message, int flag) {
        if (this.size < this.messages.length) {
            int index = this.size++;
            this.messageFlag[index] = flag;
            this.messages[index] = message;
            this.fireIntervalAdded(this, index, index);
        } else {
            this.messages[this.messagePos] = message;
            this.messageFlag[this.messagePos] = flag;
            this.messagePos = (this.messagePos + 1) % this.size;
            this.fireContentsChanged(this, 0, this.size);
        }
    }

    public void clear() {
        if (this.size > 0) {
            int oldSize = this.size;
            this.fireIntervalRemoved(this, 0, this.size - 1);
            this.size = 0;
            int i = 0;
            while (i < oldSize) {
                this.messages[i] = null;
                ++i;
            }
        }
    }

    @Override
    public Object getElementAt(int index) {
        if (index >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.valueOf(index) + " >= " + this.size);
        }
        index = (this.messagePos + index) % this.size;
        return this.messages[index];
    }

    public int getFlagAt(int index) {
        if (index >= this.size) {
            throw new ArrayIndexOutOfBoundsException(String.valueOf(index) + " >= " + this.size);
        }
        index = (this.messagePos + index) % this.size;
        return this.messageFlag[index];
    }

    @Override
    public int getSize() {
        return this.size;
    }
}

