/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import se.sics.tasim.viewer.ViewerPanel;

public class ChatPanel
extends JPanel
implements ActionListener {
    private JTextArea chatArea;
    private JTextField chatMessage;
    private JButton sendButton;
    private JButton clearButton;
    private String agentName;
    private ViewerPanel mainPanel;
    private SimpleDateFormat dateFormat;
    private Date date;

    public ChatPanel(ViewerPanel mainPanel) {
        super(new BorderLayout());
        this.agentName = mainPanel.getUserName();
        this.mainPanel = mainPanel;
        this.chatArea = new JTextArea(6, 40);
        this.chatArea.setBackground(Color.white);
        this.chatArea.setEditable(false);
        this.chatArea.setLineWrap(true);
        this.add((Component)new JScrollPane(this.chatArea, 20, 31), "Center");
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.white);
        this.chatMessage = new JTextField("");
        panel.add((Component)this.chatMessage, "Center");
        JPanel panel2 = new JPanel(new FlowLayout(1, 0, 0));
        this.sendButton = new JButton("Send");
        panel2.add(this.sendButton);
        this.clearButton = new JButton("Clear");
        panel2.add(this.clearButton);
        panel.add((Component)panel2, "East");
        JLabel chatLabel = new JLabel(String.valueOf(this.agentName) + ':');
        chatLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        panel.add((Component)chatLabel, "West");
        this.add((Component)panel, "South");
        this.sendButton.addActionListener(this);
        this.chatMessage.addActionListener(this);
        this.clearButton.addActionListener(this);
    }

    void setStatusLabel(JLabel label) {
        this.add((Component)label, "North");
    }

    public void addChatMessage(final long time, final String serverName, final String userName, final String message) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                ChatPanel.this.doAddMessage(time, serverName, userName, message);
            }
        });
    }

    private void doAddMessage(long time, String serverName, String userName, String message) {
        if (this.dateFormat == null) {
            this.dateFormat = new SimpleDateFormat("d MMM HH:mm");
            this.dateFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
            this.date = new Date(time);
        } else {
            this.date.setTime(time);
        }
        message = String.valueOf(this.dateFormat.format(this.date)) + ' ' + userName + "> " + message;
        String text = this.chatArea.getText();
        int len = text.length();
        if (text.length() > 0) {
            int index;
            len = (text = String.valueOf(text) + '\n' + message).length();
            if (len > 5120 && (index = text.indexOf(10)) > 0 && ++index < len) {
                text = text.substring(index);
                len = text.length();
            }
        } else {
            text = message;
        }
        this.chatArea.setText(text);
        this.chatArea.setCaretPosition(len);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == this.chatMessage || source == this.sendButton) {
            String message = this.chatMessage.getText().trim();
            this.chatMessage.setText("");
            if (message.length() > 0) {
                this.mainPanel.sendChatMessage(message);
            }
        } else if (source == this.clearButton) {
            this.chatArea.setText("");
            this.chatMessage.setText("");
        }
    }

}

