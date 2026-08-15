package com.gabriel.slcas.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import com.gabriel.slcas.controller.BorrowController;
import com.gabriel.slcas.controller.LibraryManager;
import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.UserAccount;

/**
 * Main application window for the Smart Library Circulation & Automation System.
 */
public class MainWindow extends JFrame {

    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_DASHBOARD = "DASHBOARD";

    private final LibraryDatabase db;
    private final LibraryManager manager;
    private final BorrowController borrowController;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardHost = new JPanel();
    private final StatusBar statusBar = new StatusBar();

    private ViewItemsPanel viewItemsPanel;
    private BorrowReturnPanel borrowReturnPanel;
    private AdminPanel adminPanel;
    private SearchSortPanel searchSortPanel;

    public MainWindow(LibraryDatabase db) {
        super("Gabriel's SLCAS — Smart Library Circulation & Automation System");
        this.db = db;
        this.manager = new LibraryManager(db);
        this.borrowController = new BorrowController(db);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());

        cardHost.setLayout(cardLayout);
        cardHost.add(buildLoginCard(), CARD_LOGIN);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(cardHost, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        cardLayout.show(cardHost, CARD_LOGIN);
    }

    private JPanel buildLoginCard() {
        return new LoginPanel(db, this::onLogin);
    }

    private void onLogin(UserAccount user) {
        JPanel dashboard = buildDashboard(user);
        cardHost.add(dashboard, CARD_DASHBOARD);
        cardLayout.show(cardHost, CARD_DASHBOARD);
        statusBar.setMessage("Signed in as " + user.getName() + (user.isAdmin() ? " (Administrator)" : " (Member)"));
    }

    private JPanel buildDashboard(UserAccount user) {
        JPanel root = new JPanel(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.boldLabelFont());

        viewItemsPanel = new ViewItemsPanel(db);
        borrowReturnPanel = new BorrowReturnPanel(db, borrowController, user, statusBar::setMessage, this::refreshAll);
        searchSortPanel = new SearchSortPanel(db, statusBar::setMessage, this::refreshAll);

        tabs.addTab("View Items", viewItemsPanel);
        tabs.setMnemonicAt(0, KeyEvent.VK_1);
        tabs.addTab("Borrow / Return", borrowReturnPanel);
        tabs.setMnemonicAt(1, KeyEvent.VK_2);

        if (user.isAdmin()) {
            adminPanel = new AdminPanel(manager, borrowController, statusBar::setMessage, this::refreshAll);
            tabs.addTab("Admin", adminPanel);
            tabs.setMnemonicAt(2, KeyEvent.VK_3);
        }

        tabs.addTab("Search & Sort", searchSortPanel);
        tabs.setMnemonicAt(tabs.getTabCount() - 1, KeyEvent.VK_4);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.NAVY_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        JLabel title = new JLabel("Gabriel's SLCAS");
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel who = new JLabel(user.getName() + (user.isAdmin() ? "  ·  Administrator" : "  ·  Member") + "   ");
        who.setFont(UITheme.labelFont());
        who.setForeground(UITheme.ACCENT_GOLD);
        header.add(title, BorderLayout.WEST);
        header.add(who, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        return root;
    }

    private void refreshAll() {
        if (viewItemsPanel != null) viewItemsPanel.refresh();
        if (borrowReturnPanel != null) borrowReturnPanel.refresh();
        if (adminPanel != null) adminPanel.refreshAll();
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        JMenuItem saveItem = new JMenuItem("Save As…", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("slcas_library.json"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    manager.save(chooser.getSelectedFile().getAbsolutePath());
                    statusBar.setMessage("Saved to " + chooser.getSelectedFile().getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JMenuItem loadItem = new JMenuItem("Load…", KeyEvent.VK_L);
        loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        loadItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    manager.load(chooser.getSelectedFile().getAbsolutePath());
                    statusBar.setMessage("Loaded " + chooser.getSelectedFile().getName());
                    refreshAll();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        JMenuItem aboutItem = new JMenuItem("About Gabriel's SLCAS", KeyEvent.VK_A);
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Gabriel's SLCAS\nSmart Library Circulation & Automation System\n\n" +
                "\nby Gabriel Chukwuebuka Oboko",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        bar.add(fileMenu);
        bar.add(helpMenu);
        return bar;
    }
}
