package com.gabriel.slcas.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.gabriel.slcas.model.Category;
import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.LibraryItem;

public class ViewItemsPanel extends JPanel {

    private final LibraryDatabase db;
    private final ItemTableModel model;
    private final JTable table;
    private final JComboBox<String> filterCombo;
    private final JLabel countLabel = new JLabel();

    public ViewItemsPanel(LibraryDatabase db) {
        this.db = db;
        setLayout(new BorderLayout(0, 10));
        UITheme.stylePanel(this);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(UITheme.heading("Catalogue"));
        JLabel filterLabel = new JLabel("  Filter:");
        filterLabel.setFont(UITheme.labelFont());
        left.add(filterLabel);
        filterCombo = new JComboBox<>(new String[]{"All", "Book", "Magazine", "Journal"});
        filterCombo.setToolTipText("Show only items of the selected category");
        filterCombo.addActionListener(e -> refresh());
        left.add(filterCombo);
        JButton refreshBtn = UITheme.secondaryButton("Refresh", 'R');
        refreshBtn.addActionListener(e -> refresh());
        left.add(refreshBtn);
        top.add(left, BorderLayout.WEST);
        countLabel.setFont(UITheme.labelFont());
        countLabel.setForeground(UITheme.TEXT_MUTED);
        top.add(countLabel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        model = new ItemTableModel(db.getItems());
        table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(UITheme.tableFont());
        table.setDefaultRenderer(Object.class, new CategoryRenderer());
        table.getTableHeader().setFont(UITheme.boldLabelFont());
        table.getTableHeader().setBackground(UITheme.NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setToolTipText("Double-click a row for full item details");
        table.setSelectionBackground(UITheme.NAVY_LIGHT);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                LibraryItem item = model.getItemAt(row);
                table.setToolTipText(item.getDetails());
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        String filter = (String) filterCombo.getSelectedItem();
        List<LibraryItem> filtered;
        if (filter == null || "All".equals(filter)) {
            filtered = db.getItems();
        } else {
            Category cat = Category.valueOf(filter.toUpperCase());
            filtered = db.getItems().stream().filter(i -> i.getCategory() == cat).collect(Collectors.toList());
        }
        model.setItems(filtered);
        long available = filtered.stream().filter(LibraryItem::isAvailable).count();
        countLabel.setText(filtered.size() + " item(s) shown  |  " + available + " available");
    }

    public JTable getTable() { return table; }
    public ItemTableModel getModel() { return model; }
}
