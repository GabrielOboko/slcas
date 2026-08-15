package com.gabriel.slcas.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.gabriel.slcas.controller.SearchEngine;
import com.gabriel.slcas.controller.SortEngine;
import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.LibraryItem;

public class SearchSortPanel extends JPanel {

    private final LibraryDatabase db;
    private final Consumer<String> statusSink;
    private final Runnable onCatalogueChanged;

    private final JTextField queryField = new JTextField(18);
    private final JComboBox<SearchEngine.Field> searchFieldCombo = new JComboBox<>(SearchEngine.Field.values());
    private final JComboBox<SearchEngine.Algorithm> searchAlgoCombo = new JComboBox<>(SearchEngine.Algorithm.values());

    private final JComboBox<SortEngine.Field> sortFieldCombo = new JComboBox<>(SortEngine.Field.values());
    private final JComboBox<SortEngine.Algorithm> sortAlgoCombo = new JComboBox<>(SortEngine.Algorithm.values());

    private final ItemTableModel resultsModel;
    private final JTable resultsTable;

    public SearchSortPanel(LibraryDatabase db, Consumer<String> statusSink, Runnable onCatalogueChanged) {
        this.db = db;
        this.statusSink = statusSink;
        this.onCatalogueChanged = onCatalogueChanged;

        setLayout(new BorderLayout(10, 10));
        UITheme.stylePanel(this);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchRow.setOpaque(false);
        searchRow.setBorder(BorderFactory.createTitledBorder("Search"));
        searchRow.add(new JLabel("Query:"));
        queryField.setToolTipText("Type a title or author, then press Enter or click Search (Alt+E)");
        searchRow.add(queryField);
        searchRow.add(new JLabel("Field:"));
        searchRow.add(searchFieldCombo);
        searchRow.add(new JLabel("Algorithm:"));
        searchAlgoCombo.setToolTipText("Binary/Recursive require the catalogue to be sorted on the same field first");
        searchRow.add(searchAlgoCombo);
        JButton searchBtn = UITheme.primaryButton("Search", 'E');
        searchRow.add(searchBtn);
        controls.add(searchRow);

        JPanel sortRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        sortRow.setOpaque(false);
        sortRow.setBorder(BorderFactory.createTitledBorder("Sort the master catalogue"));
        sortRow.add(new JLabel("Sort by:"));
        sortRow.add(sortFieldCombo);
        sortRow.add(new JLabel("Algorithm:"));
        sortRow.add(sortAlgoCombo);
        JButton sortBtn = UITheme.secondaryButton("Sort", 'O');
        sortRow.add(sortBtn);
        controls.add(sortRow);

        resultsModel = new ItemTableModel(new ArrayList<>());
        resultsTable = new JTable(resultsModel);
        resultsTable.setFont(UITheme.tableFont());
        resultsTable.setRowHeight(24);
        resultsTable.setDefaultRenderer(Object.class, new CategoryRenderer());
        resultsTable.getTableHeader().setBackground(UITheme.NAVY);
        resultsTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane resultsScroll = new JScrollPane(resultsTable);
        resultsScroll.setBorder(BorderFactory.createTitledBorder("Results"));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UITheme.heading("Search & Sort"), BorderLayout.NORTH);
        top.add(controls, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        add(resultsScroll, BorderLayout.CENTER);

        searchBtn.addActionListener(this::doSearch);
        queryField.addActionListener(this::doSearch); // Enter key shortcut
        sortBtn.addActionListener(e -> doSort());

        resultsModel.setItems(db.getItems());
    }

    private void doSearch(ActionEvent e) {
        String query = queryField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type something to search for.", "Empty query", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SearchEngine.Field field = (SearchEngine.Field) searchFieldCombo.getSelectedItem();
        SearchEngine.Algorithm algo = (SearchEngine.Algorithm) searchAlgoCombo.getSelectedItem();
        List<LibraryItem> results = new ArrayList<>();

        switch (algo) {
            case LINEAR -> results = SearchEngine.linearSearch(db.getItems(), query, field);
            case BINARY -> {
                List<LibraryItem> sorted = new ArrayList<>(db.getItems());
                SortEngine.sort(sorted, SortEngine.Algorithm.MERGE,
                        field == SearchEngine.Field.AUTHOR ? SortEngine.Field.AUTHOR : SortEngine.Field.TITLE);
                LibraryItem exact = SearchEngine.binarySearch(sorted, query, field);
                if (exact != null) results.add(exact);
            }
            case RECURSIVE -> {
                LibraryItem match = SearchEngine.recursiveSearch(db.getItems(), query, field);
                if (match != null) results.add(match);
            }
        }
        resultsModel.setItems(results);
        statusSink.accept("Search (" + algo + " by " + field + ") found " + results.size() + " result(s).");
    }

    private void doSort() {
        SortEngine.Field field = (SortEngine.Field) sortFieldCombo.getSelectedItem();
        SortEngine.Algorithm algo = (SortEngine.Algorithm) sortAlgoCombo.getSelectedItem();
        SortEngine.sort(db.getItems(), algo, field);
        resultsModel.setItems(db.getItems());
        statusSink.accept("Catalogue sorted by " + field + " using " + algo + " sort.");
        onCatalogueChanged.run();
    }
}
