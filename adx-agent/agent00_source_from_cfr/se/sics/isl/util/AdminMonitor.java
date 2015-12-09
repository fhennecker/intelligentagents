/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.util;

import com.botbox.util.ThreadPool;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import se.sics.isl.util.AMonitor;

public class AdminMonitor
implements ActionListener,
AMonitor {
    private static final Logger log = Logger.getLogger(AdminMonitor.class.getName());
    private static final String THREAD_NAME = "Threads";
    private static final String STAT_NAME = "System";
    private static final String GC_NAME = "GC";
    private static AdminMonitor defaultAdminMonitor = new AdminMonitor();
    private long startTime = System.currentTimeMillis();
    private String title;
    private JFrame window;
    private JTextArea statusText;
    private JPanel buttonPanel;
    private Hashtable monitors = new Hashtable();

    public static AdminMonitor getDefault() {
        return defaultAdminMonitor;
    }

    public AdminMonitor() {
        this.monitors.put("Threads", this);
        this.monitors.put("System", this);
        this.monitors.put("GC", this);
    }

    public void setTitle(String title) {
        this.title = title;
        if (this.window != null) {
            this.window.setTitle(title == null ? "Admin Monitor" : "Admin Monitor: " + title);
        }
    }

    public void setBounds(int x, int y, int width, int height) {
        if (this.window == null) {
            this.createWindow();
        }
        this.window.setBounds(x, y, width, height);
    }

    public void setBounds(String bounds) {
        try {
            StringTokenizer tok = new StringTokenizer(bounds, ", \t");
            int x = Integer.parseInt(tok.nextToken());
            int y = Integer.parseInt(tok.nextToken());
            if (this.window == null) {
                this.createWindow();
            }
            if (tok.hasMoreTokens()) {
                this.window.setBounds(x, y, Integer.parseInt(tok.nextToken()), Integer.parseInt(tok.nextToken()));
            } else {
                this.window.setLocation(x, y);
            }
        }
        catch (Exception e) {
            log.log(Level.WARNING, "could not set boundary", e);
        }
    }

    public void start() {
        if (this.window == null) {
            this.createWindow();
        }
        this.window.setVisible(true);
    }

    private void createWindow() {
        String title = this.title;
        this.window = new JFrame(title == null ? "Admin Monitor" : "Admin Monitor: " + title);
        this.window.setDefaultCloseOperation(0);
        JPanel panel = new JPanel(new BorderLayout());
        this.statusText = new JTextArea(5, 40);
        panel.add((Component)new JScrollPane(this.statusText), "Center");
        this.statusText.setEditable(false);
        this.statusText.setFocusable(false);
        this.statusText.setRequestFocusEnabled(false);
        this.statusText.setTabSize(12);
        this.buttonPanel = new JPanel();
        Hashtable hashtable = this.monitors;
        synchronized (hashtable) {
            Enumeration mons = this.monitors.keys();
            while (mons.hasMoreElements()) {
                String name = (String)mons.nextElement();
                JButton button = new JButton(name);
                button.addActionListener(this);
                this.buttonPanel.add(button);
            }
        }
        panel.add((Component)this.buttonPanel, "South");
        this.window.getContentPane().add(panel);
        this.window.pack();
    }

    public void addMonitor(String name, AMonitor monitor) {
        Hashtable hashtable = this.monitors;
        synchronized (hashtable) {
            if (this.monitors.get(name) != null) {
                log.log(Level.WARNING, "monitor '" + name + "' already registered", new IllegalArgumentException("monitor already registered"));
                return;
            }
            this.monitors.put(name, monitor);
            if (this.buttonPanel != null) {
                final JButton button = new JButton(name);
                button.addActionListener(this);
                SwingUtilities.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        AdminMonitor.this.buttonPanel.add(button);
                        AdminMonitor.this.buttonPanel.revalidate();
                    }
                });
            }
        }
    }

    @Override
    public String getStatus(String name) {
        if (name == "Threads") {
            Enumeration pools = ThreadPool.getThreadPools();
            StringBuffer sb = new StringBuffer().append("--- ThreadPools ---");
            while (pools.hasMoreElements()) {
                ThreadPool pool = (ThreadPool)pools.nextElement();
                sb.append('\n');
                pool.getThreadStatus(sb);
            }
            return sb.toString();
        }
        if (name == "System") {
            long memory = Runtime.getRuntime().totalMemory();
            long free = Runtime.getRuntime().freeMemory();
            return "--- System ---\nSystem Running:\t" + this.getSystemTime() + "\nTotal Memory:\t" + this.formatMemory(memory) + "\nAvailable Memory:\t" + this.formatMemory(free) + "\nUsed Memory:\t" + this.formatMemory(memory - free) + "\nActive Threads:\t" + this.getThreadCount() + "\nJava Version:\t" + System.getProperty("java.version", "");
        }
        if (name == "GC") {
            long memory = Runtime.getRuntime().totalMemory();
            long free = Runtime.getRuntime().freeMemory();
            System.gc();
            long memory2 = Runtime.getRuntime().totalMemory();
            long free2 = Runtime.getRuntime().freeMemory();
            return "--- Memory ---\nBefore GC:\n  Total Memory:\t" + this.formatMemory(memory) + "\n  Available Memory:\t" + this.formatMemory(free) + "\n  Used Memory:\t" + this.formatMemory(memory - free) + "\nAfter GC:" + "\n  Total Memory:\t" + this.formatMemory(memory2) + "\n  Available Memory:\t" + this.formatMemory(free2) + "\n  Used Memory:\t" + this.formatMemory(memory2 - free2);
        }
        return null;
    }

    private String formatMemory(long value) {
        boolean isNegative;
        boolean bl = isNegative = value < 0;
        if (isNegative) {
            value = - value;
        }
        char[] buffer = new char[26];
        int index = buffer.length - 1;
        if (value == 0) {
            buffer[index--] = 48;
        } else {
            int count = 0;
            while (value > 0 && index >= 0) {
                if (count % 3 == 0 && count > 0 && index > 0) {
                    buffer[index--] = 32;
                }
                buffer[index--] = (char)(48 + value % 10);
                value /= 10;
                ++count;
            }
        }
        if (isNegative && index >= 0) {
            buffer[index--] = 45;
        }
        return new String(buffer, index + 1, buffer.length - index - 1);
    }

    private String getSystemTime() {
        int h;
        long time = (System.currentTimeMillis() - this.startTime) / 1000;
        StringBuffer sb = new StringBuffer();
        int days = (int)(time / 86400);
        if (days > 0) {
            sb.append(days).append(" day");
            if (days > 1) {
                sb.append('s');
            }
            sb.append(' ');
            time %= 86400;
        }
        if ((h = (int)(time / 3600)) > 0) {
            sb.append(h).append(" hour");
            if (h > 1) {
                sb.append('s');
            }
            sb.append(' ');
            time %= 3600;
        }
        int min = (int)(time / 60);
        int seconds = (int)(time % 60);
        sb.append(min).append(" min ").append(seconds).append(" sec");
        return sb.toString();
    }

    private String getThreadCount() {
        try {
            ThreadGroup parent;
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            while ((parent = group.getParent()) != null) {
                group = parent;
            }
            return Integer.toString(group.activeCount());
        }
        catch (SecurityException e) {
            return "no access";
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String name;
        Object source = event.getSource();
        if (source instanceof JButton && (name = event.getActionCommand()) != null) {
            String text;
            AMonitor monitor = (AMonitor)this.monitors.get(name);
            if (monitor != null && (text = monitor.getStatus(name)) != null) {
                this.statusText.setText(text);
            } else {
                this.statusText.setText("No status for monitor\n" + name);
            }
        }
    }

}

