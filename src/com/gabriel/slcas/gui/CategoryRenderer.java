package com.gabriel.slcas.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.gabriel.slcas.model.Category;

/**
 * Custom table renderer that colors rows by item category
 * and highlights borrowed items in the status column.
 */
public class CategoryRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String typeLabel = (String) table.getModel().getValueAt(row, 4);
        Color tint;
        if (Category.BOOK.getLabel().equals(typeLabel)) tint = UITheme.BOOK_TINT;
        else if (Category.MAGAZINE.getLabel().equals(typeLabel)) tint = UITheme.MAGAZINE_TINT;
        else tint = UITheme.JOURNAL_TINT;

        if (!isSelected) {
            c.setBackground(tint);
            c.setForeground(UITheme.TEXT_DARK);
        } else {
            c.setBackground(UITheme.NAVY_LIGHT);
            c.setForeground(Color.WHITE);
        }

        setFont(UITheme.tableFont());

        if (column == 6) {
            String status = String.valueOf(value);
            setFont(UITheme.boldLabelFont());
            if (!isSelected) {
                setBackground("BORROWED".equals(status) ? UITheme.BORROWED_TINT : UITheme.AVAILABLE_TINT);
                setForeground("BORROWED".equals(status) ? UITheme.ACCENT_RED.darker() : new Color(0x1E7A3C));
            }
        }
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return c;
    }
}
