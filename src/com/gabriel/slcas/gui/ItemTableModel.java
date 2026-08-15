package com.gabriel.slcas.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.gabriel.slcas.model.LibraryItem;

public class ItemTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"ID", "Title", "Author / Publisher", "Year", "Type", "Identifier", "Status"};

    private List<LibraryItem> items;

    public ItemTableModel(List<LibraryItem> items) {
        this.items = items;
    }

    public void setItems(List<LibraryItem> items) {
        this.items = items;
        fireTableDataChanged();
    }

    public LibraryItem getItemAt(int row) {
        return items.get(row);
    }

    @Override public int getRowCount() { return items.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int c) { return COLUMNS[c]; }
    @Override public boolean isCellEditable(int r, int c) { return false; }

    @Override
    public Object getValueAt(int row, int col) {
        LibraryItem item = items.get(row);
        switch (col) {
            case 0: return item.getId();
            case 1: return item.getTitle();
            case 2: return item.getAuthor();
            case 3: return item.getYear();
            case 4: return item.getCategory().getLabel();
            case 5: return item.getIdentifier();
            case 6: return item.getStatus().name();
            default: return "";
        }
    }
}
