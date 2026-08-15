package com.gabriel.slcas.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Queue;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import com.gabriel.slcas.controller.BorrowController;
import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.LibraryItem;
import com.gabriel.slcas.model.UserAccount;

public class BorrowReturnPanel extends JPanel {

    private final LibraryDatabase db;
    private final BorrowController borrowController;
    private final UserAccount currentUser;
    private final Consumer<String> statusSink;
    private final Runnable onCatalogueChanged;

    private final DefaultListModel<LibraryItem> listModel = new DefaultListModel<>();
    private final JList<LibraryItem> itemList = new JList<>(listModel);
    private final JTextArea detailsArea = new JTextArea();
    private final JTextArea queueArea = new JTextArea();
    private final JTextArea historyArea = new JTextArea();

    public BorrowReturnPanel(LibraryDatabase db, BorrowController borrowController, UserAccount currentUser,
                              Consumer<String> statusSink, Runnable onCatalogueChanged) {
        this.db = db;
        this.borrowController = borrowController;
        this.currentUser = currentUser;
        this.statusSink = statusSink;
        this.onCatalogueChanged = onCatalogueChanged;

        setLayout(new BorderLayout(12, 12));
        UITheme.stylePanel(this);

        JLabel heading = UITheme.heading("Borrow / Return  —  signed in as " + currentUser.getName());
        add(heading, BorderLayout.NORTH);

        itemList.setFont(UITheme.tableFont());
        itemList.setFixedCellHeight(24);
        itemList.setToolTipText("Select an item to borrow or return");
        itemList.addListSelectionListener(e -> updateDetails());
        JScrollPane listScroll = new JScrollPane(itemList);
        listScroll.setPreferredSize(new Dimension(380, 300));
        listScroll.setBorder(BorderFactory.createTitledBorder("Catalogue"));

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        center.setOpaque(false);

        JPanel detailsPanel = new JPanel(new BorderLayout(6, 6));
        detailsPanel.setOpaque(false);
        detailsArea.setEditable(false);
        detailsArea.setFont(UITheme.monoFont());
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Item Details"));

        JPanel queuePanel = new JPanel(new BorderLayout(6, 6));
        queuePanel.setOpaque(false);
        queueArea.setEditable(false);
        queueArea.setFont(UITheme.monoFont());
        queuePanel.add(new JScrollPane(queueArea), BorderLayout.CENTER);
        queuePanel.setBorder(BorderFactory.createTitledBorder("Reservation Queue"));

        center.add(detailsPanel);
        center.add(queuePanel);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setOpaque(false);
        left.add(listScroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        buttons.setOpaque(false);
        JButton borrowBtn = UITheme.primaryButton("Borrow", 'B');
        borrowBtn.setToolTipText("Borrow the selected item (Alt+B)");
        borrowBtn.addActionListener(e -> doBorrow());
        JButton returnBtn = UITheme.secondaryButton("Return", 'T');
        returnBtn.setToolTipText("Return the selected item (Alt+T)");
        returnBtn.addActionListener(e -> doReturn());
        buttons.add(borrowBtn);
        buttons.add(returnBtn);
        left.add(buttons, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, center);
        split.setResizeWeight(0.35);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        historyArea.setEditable(false);
        historyArea.setFont(UITheme.monoFont());
        historyArea.setRows(5);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(BorderFactory.createTitledBorder("My Borrowing History"));
        add(historyScroll, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        LibraryItem selected = itemList.getSelectedValue();
        listModel.clear();
        for (LibraryItem item : db.getItems()) listModel.addElement(item);
        if (selected != null) itemList.setSelectedValue(selected, true);
        updateDetails();
        historyArea.setText(String.join("\n", currentUser.getBorrowingHistory()));
        historyArea.setCaretPosition(0);
    }

    private void updateDetails() {
        LibraryItem item = itemList.getSelectedValue();
        if (item == null) {
            detailsArea.setText("");
            queueArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(item.getDetails()).append("\n\n");
        sb.append("Status   : ").append(item.getStatus()).append('\n');
        if (item.getBorrowedByUserId() != null) {
            UserAccount holder = db.findUserById(item.getBorrowedByUserId());
            sb.append("Held by  : ").append(holder != null ? holder.getName() : item.getBorrowedByUserId()).append('\n');
            sb.append("Due      : ").append(item.getDueDate()).append('\n');
        }
        detailsArea.setText(sb.toString());

        Queue<String> queue = db.getReservationQueue(item.getId());
        if (queue.isEmpty()) {
            queueArea.setText("(no reservations)");
        } else {
            StringBuilder q = new StringBuilder();
            int pos = 1;
            for (String uid : queue) {
                UserAccount u = db.findUserById(uid);
                q.append(pos++).append(". ").append(u != null ? u.getName() : uid).append('\n');
            }
            queueArea.setText(q.toString());
        }
    }

    private void doBorrow() {
        LibraryItem item = itemList.getSelectedValue();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select an item first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String msg = borrowController.borrow(item.getId(), currentUser.getId());
        statusSink.accept(msg);
        JOptionPane.showMessageDialog(this, msg, "Borrow", JOptionPane.INFORMATION_MESSAGE);
        refresh();
        onCatalogueChanged.run();
    }

    private void doReturn() {
        LibraryItem item = itemList.getSelectedValue();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select an item first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String msg = borrowController.returnItem(item.getId(), currentUser.getId());
        statusSink.accept(msg);
        JOptionPane.showMessageDialog(this, msg, "Return", JOptionPane.INFORMATION_MESSAGE);
        refresh();
        onCatalogueChanged.run();
    }
}
