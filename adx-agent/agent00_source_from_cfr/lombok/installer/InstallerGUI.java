/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import lombok.core.Version;
import lombok.installer.AppleNativeLook;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeFinder;
import lombok.installer.IdeLocation;
import lombok.installer.InstallException;
import lombok.installer.Installer;
import lombok.installer.UninstallException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class InstallerGUI {
    static final AtomicReference<Integer> exitMarker = new AtomicReference();
    private JFrame appWindow;
    private JComponent loadingExpl;
    private Component javacArea;
    private Component ideArea;
    private Component uninstallArea;
    private Component howIWorkArea;
    private Box uninstallBox;
    private JHyperLink uninstallButton;
    private JLabel uninstallPlaceholder;
    private JButton installButton;
    private List<IdeLocation> toUninstall;
    private final Set<String> installSpecificMessages = new LinkedHashSet<String>();
    private IdesList idesList;
    private static final String IDE_TITLE = "<html><font size=\"+1\"><b><i>IDEs</i></b></font></html>";
    private static final String IDE_EXPLANATION = "<html>Lombok can update your Eclipse and Netbeans to fully support all Lombok features.<br>Select IDE installations below and hit 'Install/Update'.</html>";
    private static final String IDE_LOADING_EXPLANATION = "Scanning your drives for IDE installations...";
    private static final String JAVAC_TITLE = "<html><font size=\"+1\"><b><i>Javac</i></b></font> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; (and tools that invoke javac such as <i>ant</i> and <i>maven</i>)</html>";
    private static final String JAVAC_EXPLANATION = "<html>Lombok works 'out of the box' with javac.<br>Just make sure the lombok.jar is in your classpath when you compile.";
    private static final String JAVAC_EXAMPLE = "<html>Example: <code>javac -cp lombok.jar MyCode.java</code></html>";
    private static final String UNINSTALL_TITLE = "<html><font size=\"+1\"><b><i>Uninstall</i></b></font></html>";
    private static final String UNINSTALL_EXPLANATION = "<html>Uninstall Lombok from the following IDE Installations?</html>";
    private static final String HOW_I_WORK_TITLE = "<html><font size=\"+1\"><b><i>What this installer does</i></b></font></html>";
    private static final String HOW_I_WORK_EXPLANATION = "<html><h2>Eclipse</h2><ol><li>First, I copy myself (lombok.jar) to your Eclipse install directory.</li><li>Then, I edit the <i>eclipse.ini</i> file to add the following two entries:<br><pre>-Xbootclasspath/a:lombok.jar<br>-javaagent:lombok.jar</pre></li></ol>On Mac OS X, eclipse.ini is hidden in<br><code>Eclipse.app/Contents/MacOS</code> so that's where I place the jar files.<p><h2>Netbeans</h2><ol><li>First, I copy myself (lombok.jar) to your Netbeans install directory.</li><lI>Then, I edit <i>etc%1$snetbeans.conf</i> to add the following argument to<br><b>netbeans_default_options</b>:<br><pre>-J-javaagent:lombok.jar</pre></li></ol>On Mac OS X, your netbeans directory is hidden in<br><code>NetBeans.app/Contents/Resources/NetBeans</code></html>";

    public InstallerGUI() {
        this.idesList = new IdesList();
        this.appWindow = new JFrame(String.format("Project Lombok v%s - Installer", Version.getVersion()));
        this.appWindow.setDefaultCloseOperation(3);
        this.appWindow.setResizable(false);
        this.appWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Installer.class.getResource("lombokIcon.png")));
        try {
            this.javacArea = this.buildJavacArea();
            this.ideArea = this.buildIdeArea();
            this.uninstallArea = this.buildUninstallArea();
            this.uninstallArea.setVisible(false);
            this.howIWorkArea = this.buildHowIWorkArea();
            this.howIWorkArea.setVisible(false);
            this.buildChrome(this.appWindow.getContentPane());
            this.appWindow.pack();
        }
        catch (Throwable t) {
            this.handleException(t);
        }
    }

    private void handleException(final Throwable t) {
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, "There was a problem during the installation process:\n" + t, "Uh Oh!", 0);
                t.printStackTrace();
                System.exit(1);
            }
        });
    }

    private Component buildHowIWorkArea() {
        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = 17;
        container.add((Component)new JLabel("<html><font size=\"+1\"><b><i>What this installer does</i></b></font></html>"), constraints);
        constraints.gridy = 1;
        constraints.insets = new Insets(8, 0, 0, 16);
        container.add((Component)new JLabel(String.format("<html><h2>Eclipse</h2><ol><li>First, I copy myself (lombok.jar) to your Eclipse install directory.</li><li>Then, I edit the <i>eclipse.ini</i> file to add the following two entries:<br><pre>-Xbootclasspath/a:lombok.jar<br>-javaagent:lombok.jar</pre></li></ol>On Mac OS X, eclipse.ini is hidden in<br><code>Eclipse.app/Contents/MacOS</code> so that's where I place the jar files.<p><h2>Netbeans</h2><ol><li>First, I copy myself (lombok.jar) to your Netbeans install directory.</li><lI>Then, I edit <i>etc%1$snetbeans.conf</i> to add the following argument to<br><b>netbeans_default_options</b>:<br><pre>-J-javaagent:lombok.jar</pre></li></ol>On Mac OS X, your netbeans directory is hidden in<br><code>NetBeans.app/Contents/Resources/NetBeans</code></html>", File.separator)), constraints);
        Box buttonBar = Box.createHorizontalBox();
        JButton backButton = new JButton("Okay - Good to know!");
        buttonBar.add(Box.createHorizontalGlue());
        buttonBar.add(backButton);
        backButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                InstallerGUI.this.howIWorkArea.setVisible(false);
                InstallerGUI.this.javacArea.setVisible(true);
                InstallerGUI.this.ideArea.setVisible(true);
                InstallerGUI.this.appWindow.pack();
            }
        });
        constraints.gridy = 2;
        container.add((Component)buttonBar, constraints);
        return container;
    }

    private Component buildUninstallArea() {
        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = 17;
        container.add((Component)new JLabel("<html><font size=\"+1\"><b><i>Uninstall</i></b></font></html>"), constraints);
        constraints.gridy = 1;
        constraints.insets = new Insets(8, 0, 0, 16);
        container.add((Component)new JLabel("<html>Uninstall Lombok from the following IDE Installations?</html>"), constraints);
        this.uninstallBox = Box.createVerticalBox();
        constraints.gridy = 2;
        constraints.fill = 2;
        container.add((Component)this.uninstallBox, constraints);
        constraints.fill = 2;
        constraints.gridy = 3;
        container.add((Component)new JLabel("Are you sure?"), constraints);
        Box buttonBar = Box.createHorizontalBox();
        JButton noButton = new JButton("No - Don't uninstall");
        buttonBar.add(noButton);
        buttonBar.add(Box.createHorizontalGlue());
        JButton yesButton = new JButton("Yes - uninstall Lombok");
        buttonBar.add(yesButton);
        noButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                InstallerGUI.this.uninstallArea.setVisible(false);
                InstallerGUI.this.javacArea.setVisible(true);
                InstallerGUI.this.ideArea.setVisible(true);
                InstallerGUI.this.appWindow.pack();
            }
        });
        yesButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                InstallerGUI.this.doUninstall();
            }
        });
        constraints.gridy = 4;
        container.add((Component)buttonBar, constraints);
        return container;
    }

    private Component buildJavacArea() {
        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = 17;
        constraints.insets = new Insets(8, 0, 0, 16);
        container.add((Component)new JLabel("<html><font size=\"+1\"><b><i>Javac</i></b></font> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; (and tools that invoke javac such as <i>ant</i> and <i>maven</i>)</html>"), constraints);
        constraints.gridy = 1;
        constraints.weightx = 1.0;
        constraints.fill = 2;
        container.add((Component)new JLabel("<html>Lombok works 'out of the box' with javac.<br>Just make sure the lombok.jar is in your classpath when you compile."), constraints);
        JLabel example = new JLabel("<html>Example: <code>javac -cp lombok.jar MyCode.java</code></html>");
        constraints.gridy = 2;
        container.add((Component)example, constraints);
        return container;
    }

    private Component buildIdeArea() {
        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = 17;
        constraints.insets = new Insets(8, 0, 0, 16);
        container.add((Component)new JLabel("<html><font size=\"+1\"><b><i>IDEs</i></b></font></html>"), constraints);
        constraints.gridy = 1;
        container.add((Component)new JLabel("<html>Lombok can update your Eclipse and Netbeans to fully support all Lombok features.<br>Select IDE installations below and hit 'Install/Update'.</html>"), constraints);
        constraints.gridy = 2;
        this.loadingExpl = Box.createHorizontalBox();
        this.loadingExpl.add(new JLabel(new ImageIcon(Installer.class.getResource("loading.gif"))));
        this.loadingExpl.add(new JLabel("Scanning your drives for IDE installations..."));
        container.add((Component)this.loadingExpl, constraints);
        constraints.weightx = 1.0;
        constraints.gridy = 3;
        constraints.fill = 2;
        this.idesList = new IdesList();
        JScrollPane idesListScroll = new JScrollPane(this.idesList);
        idesListScroll.setBackground(Color.WHITE);
        idesListScroll.getViewport().setBackground(Color.WHITE);
        container.add((Component)idesListScroll, constraints);
        Thread findIdesThread = new Thread(){

            public void run() {
                try {
                    final ArrayList<IdeLocation> locations = new ArrayList<IdeLocation>();
                    final ArrayList<CorruptedIdeLocationException> problems = new ArrayList<CorruptedIdeLocationException>();
                    Installer.autoDiscover(locations, problems);
                    SwingUtilities.invokeLater(new Runnable(){

                        public void run() {
                            for (IdeLocation location : locations) {
                                try {
                                    InstallerGUI.this.idesList.addLocation(location);
                                }
                                catch (Throwable t) {
                                    InstallerGUI.this.handleException(t);
                                }
                            }
                            for (CorruptedIdeLocationException problem : problems) {
                                problem.showDialog(InstallerGUI.this.appWindow);
                            }
                            InstallerGUI.this.loadingExpl.setVisible(false);
                            if (locations.size() + problems.size() == 0) {
                                JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, "I can't find any IDEs on your computer.\nIf you have IDEs installed on this computer, please use the 'Specify Location...' button to manually point out the \nlocation of your IDE installation to me. Thanks!", "Can't find IDE", 1);
                            }
                        }
                    });
                }
                catch (Throwable t) {
                    InstallerGUI.this.handleException(t);
                }
            }

        };
        findIdesThread.start();
        Box buttonBar = Box.createHorizontalBox();
        JButton specifyIdeLocationButton = new JButton("Specify location...");
        buttonBar.add(specifyIdeLocationButton);
        specifyIdeLocationButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent event) {
                JFileChooser chooser;
                final List<Pattern> exeNames = Installer.getIdeExecutableNames();
                String file = null;
                if (IdeFinder.getOS() == IdeFinder.OS.MAC_OS_X) {
                    chooser = new FileDialog(InstallerGUI.this.appWindow);
                    chooser.setMode(0);
                    chooser.setFilenameFilter(new FilenameFilter(){

                        public boolean accept(File dir, String fileName) {
                            for (Pattern exeName : exeNames) {
                                if (!exeName.matcher(fileName).matches()) continue;
                                return true;
                            }
                            return false;
                        }
                    });
                    chooser.setVisible(true);
                    if (chooser.getDirectory() != null && chooser.getFile() != null) {
                        file = new File(chooser.getDirectory(), chooser.getFile()).getAbsolutePath();
                    }
                } else {
                    chooser = new JFileChooser();
                    chooser.setAcceptAllFileFilterUsed(false);
                    chooser.setFileSelectionMode(2);
                    chooser.setFileFilter(new FileFilter(){

                        public boolean accept(File f) {
                            if (f.isDirectory()) {
                                return true;
                            }
                            for (Pattern exeName : exeNames) {
                                if (!exeName.matcher(f.getName()).matches()) continue;
                                return true;
                            }
                            return false;
                        }

                        public String getDescription() {
                            return "IDE Installation";
                        }
                    });
                    switch (chooser.showDialog(InstallerGUI.this.appWindow, "Select")) {
                        case 0: {
                            file = chooser.getSelectedFile().getAbsolutePath();
                        }
                    }
                }
                if (file != null) {
                    try {
                        IdeLocation loc = Installer.tryAllProviders(file);
                        if (loc != null) {
                            InstallerGUI.this.idesList.addLocation(loc);
                        } else {
                            JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, "I can't find any IDE that lombok supports at location: " + file, "No IDE found", 2);
                        }
                    }
                    catch (CorruptedIdeLocationException e) {
                        e.showDialog(InstallerGUI.this.appWindow);
                    }
                    catch (Throwable t) {
                        InstallerGUI.this.handleException(t);
                    }
                }
            }

        });
        buttonBar.add(Box.createHorizontalGlue());
        this.installButton = new JButton("Install / Update");
        buttonBar.add(this.installButton);
        this.installButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                ArrayList<IdeLocation> locationsToInstall = new ArrayList<IdeLocation>(InstallerGUI.this.idesList.getSelectedIdes());
                if (locationsToInstall.isEmpty()) {
                    JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, "You haven't selected any IDE installations!.", "No Selection", 2);
                    return;
                }
                InstallerGUI.this.install(locationsToInstall);
            }
        });
        constraints.gridy = 4;
        constraints.weightx = 0.0;
        container.add((Component)buttonBar, constraints);
        constraints.gridy = 5;
        constraints.fill = 0;
        JHyperLink showMe = new JHyperLink("Show me what this installer will do to my IDE installation.");
        container.add((Component)showMe, constraints);
        showMe.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                InstallerGUI.this.showWhatIDo();
            }
        });
        constraints.gridy = 6;
        this.uninstallButton = new JHyperLink("Uninstall lombok from selected IDE installations.");
        this.uninstallPlaceholder = new JLabel("<html>&nbsp;</html>");
        this.uninstallButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                ArrayList<IdeLocation> locationsToUninstall = new ArrayList<IdeLocation>();
                for (IdeLocation location : InstallerGUI.this.idesList.getSelectedIdes()) {
                    if (!location.hasLombok()) continue;
                    locationsToUninstall.add(location);
                }
                if (locationsToUninstall.isEmpty()) {
                    JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, "You haven't selected any IDE installations that have been lombok-enabled.", "No Selection", 2);
                    return;
                }
                InstallerGUI.this.uninstall(locationsToUninstall);
            }
        });
        container.add((Component)this.uninstallButton, constraints);
        this.uninstallPlaceholder.setVisible(false);
        container.add((Component)this.uninstallPlaceholder, constraints);
        return container;
    }

    private void showWhatIDo() {
        this.javacArea.setVisible(false);
        this.ideArea.setVisible(false);
        this.howIWorkArea.setVisible(true);
        this.appWindow.pack();
    }

    private void uninstall(List<IdeLocation> locations) {
        this.javacArea.setVisible(false);
        this.ideArea.setVisible(false);
        this.uninstallBox.removeAll();
        this.uninstallBox.add(Box.createRigidArea(new Dimension(1, 16)));
        for (IdeLocation location : locations) {
            JLabel label = new JLabel(location.getName());
            label.setFont(label.getFont().deriveFont(1));
            this.uninstallBox.add(label);
        }
        this.uninstallBox.add(Box.createRigidArea(new Dimension(1, 16)));
        this.toUninstall = locations;
        this.uninstallArea.setVisible(true);
        this.appWindow.pack();
    }

    private void install(final List<IdeLocation> toInstall) {
        JPanel spinner = new JPanel();
        spinner.setOpaque(true);
        spinner.setLayout(new FlowLayout());
        spinner.add(new JLabel(new ImageIcon(Installer.class.getResource("loading.gif"))));
        this.appWindow.setContentPane(spinner);
        final AtomicInteger successes = new AtomicInteger();
        final AtomicBoolean failure = new AtomicBoolean();
        new Thread(){

            public void run() {
                for (IdeLocation loc : toInstall) {
                    try {
                        InstallerGUI.this.installSpecificMessages.add(loc.install());
                        successes.incrementAndGet();
                        continue;
                    }
                    catch (InstallException e) {
                        if (e.isWarning()) {
                            try {
                                SwingUtilities.invokeAndWait(new Runnable(){

                                    public void run() {
                                        JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, e.getMessage(), "Install Problem", 2);
                                    }
                                });
                                continue;
                            }
                            catch (Exception e2) {
                                e2.printStackTrace();
                                throw new RuntimeException(e2);
                            }
                        }
                        failure.set(true);
                        try {
                            SwingUtilities.invokeAndWait(new Runnable(){

                                public void run() {
                                    JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, e.getMessage(), "Install Problem", 0);
                                }
                            });
                            continue;
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                            throw new RuntimeException(e2);
                        }
                    }
                }
                if (successes.get() > 0) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable(){

                            public void run() {
                                StringBuilder installSpecific = new StringBuilder();
                                for (String installSpecificMessage : InstallerGUI.this.installSpecificMessages) {
                                    installSpecific.append("<br>").append(installSpecificMessage);
                                }
                                JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, "<html>Lombok has been installed on the selected IDE installations.<br>Don't forget to add <code>lombok.jar</code> to your projects, and restart your IDE!" + installSpecific.toString() + "</html>", "Install successful", 1);
                                InstallerGUI.this.appWindow.setVisible(false);
                                AtomicReference<Integer> i$ = InstallerGUI.exitMarker;
                                synchronized (i$) {
                                    InstallerGUI.exitMarker.set(0);
                                    InstallerGUI.exitMarker.notifyAll();
                                }
                            }
                        });
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    SwingUtilities.invokeLater(new Runnable(){

                        public void run() {
                            AtomicReference<Integer> atomicReference = InstallerGUI.exitMarker;
                            synchronized (atomicReference) {
                                InstallerGUI.exitMarker.set(failure.get() ? 1 : 0);
                                InstallerGUI.exitMarker.notifyAll();
                            }
                        }
                    });
                }
            }

        }.start();
    }

    private void doUninstall() {
        JPanel spinner = new JPanel();
        spinner.setOpaque(true);
        spinner.setLayout(new FlowLayout());
        spinner.add(new JLabel(new ImageIcon(Installer.class.getResource("/lombok/installer/loading.gif"))));
        final Container originalContentPane = this.appWindow.getContentPane();
        this.appWindow.setContentPane(spinner);
        final AtomicInteger successes = new AtomicInteger();
        new Thread(new Runnable(){

            public void run() {
                for (IdeLocation loc : InstallerGUI.this.toUninstall) {
                    try {
                        loc.uninstall();
                        successes.incrementAndGet();
                        continue;
                    }
                    catch (UninstallException e) {
                        if (e.isWarning()) {
                            try {
                                SwingUtilities.invokeAndWait(new Runnable(){

                                    public void run() {
                                        JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, e.getMessage(), "Uninstall Problem", 2);
                                    }
                                });
                                continue;
                            }
                            catch (Exception e2) {
                                e2.printStackTrace();
                                throw new RuntimeException(e2);
                            }
                        }
                        try {
                            SwingUtilities.invokeAndWait(new Runnable(){

                                public void run() {
                                    JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, e.getMessage(), "Uninstall Problem", 0);
                                }
                            });
                            continue;
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                            throw new RuntimeException(e2);
                        }
                    }
                }
                SwingUtilities.invokeLater(new Runnable(){

                    public void run() {
                        if (successes.get() > 0) {
                            JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, "Lombok has been removed from the selected IDE installations.", "Uninstall successful", 1);
                            InstallerGUI.this.appWindow.setVisible(false);
                            System.exit(0);
                            return;
                        }
                        InstallerGUI.this.appWindow.setContentPane(originalContentPane);
                    }
                });
            }

        }).start();
    }

    void selectedLomboksChanged(List<IdeLocation> selectedIdes) {
        boolean uninstallAvailable = false;
        boolean installAvailable = false;
        for (IdeLocation loc : selectedIdes) {
            if (loc.hasLombok()) {
                uninstallAvailable = true;
            }
            installAvailable = true;
        }
        this.uninstallButton.setVisible(uninstallAvailable);
        this.uninstallPlaceholder.setVisible(!uninstallAvailable);
        this.installButton.setEnabled(installAvailable);
    }

    private void buildChrome(Container appWindowContainer) {
        JLabel leftGraphic = new JLabel(new ImageIcon(Installer.class.getResource("lombok.png")));
        GridBagConstraints constraints = new GridBagConstraints();
        appWindowContainer.setLayout(new GridBagLayout());
        constraints.gridheight = 3;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(8, 8, 8, 8);
        appWindowContainer.add((Component)leftGraphic, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);
        ++constraints.gridx;
        ++constraints.gridy;
        constraints.gridheight = 1;
        constraints.fill = 2;
        constraints.ipadx = 16;
        constraints.ipady = 14;
        appWindowContainer.add(this.javacArea, constraints);
        ++constraints.gridy;
        appWindowContainer.add(this.ideArea, constraints);
        appWindowContainer.add(this.uninstallArea, constraints);
        appWindowContainer.add(this.howIWorkArea, constraints);
        ++constraints.gridy;
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.fill = 2;
        constraints.anchor = 14;
        constraints.insets = new Insets(0, 16, 8, 8);
        Box buttonBar = Box.createHorizontalBox();
        JButton quitButton = new JButton("Quit Installer");
        quitButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                InstallerGUI.this.appWindow.setVisible(false);
                System.exit(0);
            }
        });
        final JHyperLink hyperlink = new JHyperLink(Installer.ABOUT_LOMBOK_URL.toString());
        hyperlink.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent event) {
                hyperlink.setForeground(new Color(85, 145, 90));
                try {
                    Object desktop = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
                    Class.forName("java.awt.Desktop").getMethod("browse", URI.class).invoke(desktop, Installer.ABOUT_LOMBOK_URL);
                }
                catch (Exception e) {
                    Runtime rt = Runtime.getRuntime();
                    try {
                        switch (IdeFinder.getOS()) {
                            case WINDOWS: {
                                String[] cmd = new String[]{"cmd.exe", "/C", "start", Installer.ABOUT_LOMBOK_URL.toString()};
                                rt.exec(cmd);
                            }
                            case MAC_OS_X: {
                                rt.exec("open " + Installer.ABOUT_LOMBOK_URL.toString());
                            }
                        }
                        rt.exec("firefox " + Installer.ABOUT_LOMBOK_URL.toString());
                    }
                    catch (Exception e2) {
                        JOptionPane.showMessageDialog(InstallerGUI.this.appWindow, "Well, this is embarrassing. I don't know how to open a webbrowser.\nI guess you'll have to open it. Browse to:\n" + Installer.ABOUT_LOMBOK_URL + " for more information about Lombok.", "I'm embarrassed", 1);
                    }
                }
            }
        });
        buttonBar.add(hyperlink);
        buttonBar.add(Box.createRigidArea(new Dimension(16, 1)));
        buttonBar.add(new JLabel("<html><font size=\"-1\">v" + Version.getVersion() + "</font></html>"));
        buttonBar.add(Box.createHorizontalGlue());
        buttonBar.add(quitButton);
        this.appWindow.add((Component)buttonBar, constraints);
    }

    public void show() {
        this.appWindow.setVisible(true);
        if (IdeFinder.getOS() == IdeFinder.OS.MAC_OS_X) {
            try {
                AppleNativeLook.go();
            }
            catch (Throwable ignore) {
                // empty catch block
            }
        }
    }

    private static class JHyperLink
    extends JButton {
        private static final long serialVersionUID = 1;

        public JHyperLink(String text) {
            this.setFont(this.getFont().deriveFont(Collections.singletonMap(TextAttribute.UNDERLINE, 1)));
            this.setText(text);
            this.setBorder(null);
            this.setContentAreaFilled(false);
            this.setForeground(Color.BLUE);
            this.setCursor(Cursor.getPredefinedCursor(12));
            this.setMargin(new Insets(0, 0, 0, 0));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class IdesList
    extends JPanel
    implements Scrollable {
        private static final long serialVersionUID = 1;
        List<IdeLocation> locations;

        IdesList() {
            this.locations = new ArrayList<IdeLocation>();
            this.setLayout(new BoxLayout(this, 1));
            this.setBackground(Color.WHITE);
        }

        List<IdeLocation> getSelectedIdes() {
            ArrayList<IdeLocation> list = new ArrayList<IdeLocation>();
            for (IdeLocation loc : this.locations) {
                if (!loc.selected) continue;
                list.add(loc);
            }
            return list;
        }

        void fireSelectionChange() {
            InstallerGUI.this.selectedLomboksChanged(this.getSelectedIdes());
        }

        void addLocation(final IdeLocation location) {
            if (this.locations.contains(location)) {
                return;
            }
            Box box = Box.createHorizontalBox();
            box.setBackground(Color.WHITE);
            final JCheckBox checkbox = new JCheckBox(location.getName());
            checkbox.setBackground(Color.WHITE);
            box.add(new JLabel(new ImageIcon(location.getIdeIcon())));
            box.add(checkbox);
            checkbox.setSelected(true);
            checkbox.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    location.selected = checkbox.isSelected();
                    IdesList.this.fireSelectionChange();
                }
            });
            if (location.hasLombok()) {
                box.add(new JLabel(new ImageIcon(Installer.class.getResource("lombokIcon.png"))));
            }
            box.add(Box.createHorizontalGlue());
            this.locations.add(location);
            this.add(box);
            this.getParent().doLayout();
            this.fireSelectionChange();
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(1, 100);
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 12;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 1;
        }

    }

}

