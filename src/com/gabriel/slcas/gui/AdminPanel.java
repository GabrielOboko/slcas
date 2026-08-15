package com.gabriel.slcas.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import com.gabriel.slcas.controller.BorrowController;
import com.gabriel.slcas.controller.LibraryManager;
import com.gabriel.slcas.controller.ReportGenerator;
import com.gabriel.slcas.model.LibraryItem;

public class AdminPanel extends JPanel {

    private final LibraryManager manager;
    private final ReportGenerator reportGenerator;
    private final Consumer<String> statusSink;
    private final Runnable onCatalogueChanged;

    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Book", "Magazine", "Journal"});
    private final CardLayout identifierCards = new CardLayout();
    private final JPanel identifierCardPanel = new JPanel(identifierCards);
    private final JTextField isbnField = new JTextField();
    private final JTextField issnField = new JTextField();
    private final JTextField issnDoiField = new JTextField();
    private final JTextField titleField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JTextField yearField = new JTextField();

    private ItemTableModel adminTableModel;
    private JTable adminTable;

    private final JTextArea reportArea = new JTextArea();
    private final JLabel overdueTimerLabel = new JLabel("Overdue watchdog: idle");

    public AdminPanel(LibraryManager manager, BorrowController borrowController,
                       Consumer<String> statusSink, Runnable onCatalogueChanged) {
        this.manager = manager;
        this.reportGenerator = new ReportGenerator(manager.getDatabase(), borrowController);
        this.statusSink = statusSink;
        this.onCatalogueChanged = onCatalogueChanged;

        setLayout(new BorderLayout(12, 12));
        UITheme.stylePanel(this);
        add(UITheme.heading("Admin Console"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 14, 0));
        body.setOpaque(false);
        body.add(buildAddAndListPanel());
        body.add(buildReportsAndFilePanel());
        add(body, BorderLayout.CENTER);

        adminTableModel.setItems(manager.getDatabase().getItems());
    }

    // Left column
    private JPanel buildAddAndListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // Add Item form 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(null, "Add New Item", TitledBorder.LEFT, TitledBorder.TOP, UITheme.boldLabelFont()));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridy = 0; form.add(new JLabel("Type:"), gc);
        gc.gridx = 1; form.add(typeCombo, gc);

        gc.gridx = 0; gc.gridy = 1; form.add(new JLabel("Title:"), gc);
        gc.gridx = 1; titleField.setColumns(18); form.add(titleField, gc);

        gc.gridx = 0; gc.gridy = 2; form.add(new JLabel("Author / Publisher:"), gc);
        gc.gridx = 1; form.add(authorField, gc);

        gc.gridx = 0; gc.gridy = 3; form.add(new JLabel("Year:"), gc);
        gc.gridx = 1; form.add(yearField, gc);

        gc.gridx = 0; gc.gridy = 4; form.add(new JLabel("Identifier:"), gc);
        identifierCardPanel.add(isbnField, "Book");
        identifierCardPanel.add(issnField, "Magazine");
        identifierCardPanel.add(issnDoiField, "Journal");
        gc.gridx = 1; form.add(identifierCardPanel, gc);
        typeCombo.addActionListener(e -> identifierCards.show(identifierCardPanel, (String) typeCombo.getSelectedItem()));
        typeCombo.setToolTipText("Choosing a type swaps the identifier field (ISBN / ISSN / ISSN-DOI)");

        JButton addBtn = UITheme.primaryButton("Add Item", 'A');
        addBtn.setToolTipText("Validate and add this item to the catalogue (Alt+A)");
        addBtn.addActionListener(e -> addItem());
        gc.gridx = 1; gc.gridy = 5; gc.anchor = GridBagConstraints.EAST; form.add(addBtn, gc);

        panel.add(form, BorderLayout.NORTH);

        //Item list
        adminTableModel = new ItemTableModel(manager.getDatabase().getItems());
        adminTable = new JTable(adminTableModel);
        adminTable.setFont(UITheme.tableFont());
        adminTable.setRowHeight(24);
        adminTable.setDefaultRenderer(Object.class, new CategoryRenderer());
        adminTable.getTableHeader().setBackground(UITheme.NAVY);
        adminTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane tableScroll = new JScrollPane(adminTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Catalogue (select a row to delete)"));
        panel.add(tableScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        actions.setOpaque(false);
        JButton deleteBtn = UITheme.accentButton("Delete Selected", 'D');
        deleteBtn.setToolTipText("Delete the selected catalogue item (Alt+D)");
        deleteBtn.addActionListener(e -> deleteSelected());
        JButton undoBtn = UITheme.secondaryButton("Undo Last Action", 'U');
        undoBtn.setToolTipText("Undo the last add/delete (Alt+U)");
        undoBtn.addActionListener(e -> {
            String msg = manager.undoLastAction();
            statusSink.accept(msg);
            refreshAll();
        });
        actions.add(deleteBtn);
        actions.add(undoBtn);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    // Right panel
    private JPanel buildReportsAndFilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        reportArea.setEditable(false);
        reportArea.setFont(UITheme.monoFont());
        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setBorder(BorderFactory.createTitledBorder("Reports"));
        panel.add(reportScroll, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JButton reportBtn = UITheme.primaryButton("Generate Reports", 'G');
        reportBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        reportBtn.setToolTipText("Most-borrowed items, overdue users, category distribution (Alt+G)");
        reportBtn.addActionListener(e -> reportArea.setText(reportGenerator.fullReport()));
        controls.add(reportBtn);
        controls.add(Box.createVerticalStrut(10));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        filePanel.setOpaque(false);
        JButton saveBtn = UITheme.secondaryButton("Save As…", 'V');
        saveBtn.setToolTipText("Export the catalogue and users to a JSON file (Alt+V)");
        saveBtn.addActionListener(e -> saveAs());
        JButton loadBtn = UITheme.secondaryButton("Load…", 'L');
        loadBtn.setToolTipText("Import a previously saved JSON file (Alt+L)");
        loadBtn.addActionListener(e -> loadFrom());
        filePanel.add(saveBtn);
        filePanel.add(loadBtn);
        controls.add(filePanel);

        controls.add(Box.createVerticalStrut(10));
        overdueTimerLabel.setFont(UITheme.labelFont());
        overdueTimerLabel.setForeground(UITheme.TEXT_MUTED);
        overdueTimerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controls.add(overdueTimerLabel);

        panel.add(controls, BorderLayout.SOUTH);

        reportArea.setText(reportGenerator.fullReport());
        startOverdueWatchdog();
        return panel;
    }

    private void startOverdueWatchdog() {
        // Checks for overdue items every minute and shows a reminder when needed.
        Timer timer = new Timer(60_000, e -> {
            var overdue = new BorrowController(manager.getDatabase()).findOverdueItems();
            overdueTimerLabel.setText("Overdue watchdog: last check " + java.time.LocalTime.now().withNano(0)
                    + "  |  " + overdue.size() + " overdue");
            if (!overdue.isEmpty()) {
                StringBuilder sb = new StringBuilder("The following items are overdue:\n");
                for (LibraryItem item : overdue) sb.append(" • ").append(item.getTitle()).append('\n');
                JOptionPane.showMessageDialog(this, sb.toString(), "Overdue Reminder", JOptionPane.WARNING_MESSAGE);
            }
        });
        timer.setInitialDelay(2000);
        timer.start();
    }

    private void addItem() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String yearText = yearField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String identifier = switch (type) {
            case "Book" -> isbnField.getText().trim();
            case "Magazine" -> issnField.getText().trim();
            default -> issnDoiField.getText().trim();
        };

        if (title.isEmpty() || author.isEmpty() || yearText.isEmpty() || identifier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year must be a whole number, e.g. 2024.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (type) {
            case "Book" -> manager.addBook(title, author, year, identifier);
            case "Magazine" -> manager.addMagazine(title, author, year, identifier);
            default -> manager.addJournal(title, author, year, identifier);
        }
        titleField.setText(""); authorField.setText(""); yearField.setText("");
        isbnField.setText(""); issnField.setText(""); issnDoiField.setText("");
        statusSink.accept("Added new " + type.toLowerCase() + ": \"" + title + "\".");
        refreshAll();
    }

    private void deleteSelected() {
        int row = adminTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LibraryItem item = adminTableModel.getItemAt(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete \"" + item.getTitle() + "\"? This can be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.deleteItem(item.getId());
            statusSink.accept("Deleted \"" + item.getTitle() + "\" (undoable).");
            refreshAll();
        }
    }

    private void saveAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("slcas_library.json"));
        chooser.setDialogTitle("Save library data as JSON");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                manager.save(chooser.getSelectedFile().getAbsolutePath());
                statusSink.accept("Saved library data to " + chooser.getSelectedFile().getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not save file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadFrom() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load library data from JSON");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                manager.load(chooser.getSelectedFile().getAbsolutePath());
                statusSink.accept("Loaded library data from " + chooser.getSelectedFile().getName());
                refreshAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not load file: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshAll() {
        adminTableModel.setItems(manager.getDatabase().getItems());
        reportArea.setText(reportGenerator.fullReport());
        onCatalogueChanged.run();
    }
}
