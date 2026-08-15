package com.gabriel.slcas.controller;

import java.util.List;

import com.gabriel.slcas.model.Category;
import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.LibraryItem;
import com.gabriel.slcas.model.UserAccount;
import com.gabriel.slcas.utils.RecursiveUtils;

// Generates summary reports for the library administrators.
public class ReportGenerator {

    private final LibraryDatabase db;
    private final BorrowController borrowController;

    public ReportGenerator(LibraryDatabase db, BorrowController borrowController) {
        this.db = db;
        this.borrowController = borrowController;
    }

    // Generates a report of the most frequently borrowed items.
    public String mostBorrowedItemsReport() {
        StringBuilder sb = new StringBuilder("=== MOST FREQUENTLY BORROWED / ACCESSED ITEMS ===\n");
        LibraryItem[] cache = db.getMfuCache();
        boolean any = false;
        for (int i = 0; i < cache.length; i++) {
            if (cache[i] != null) {
                any = true;
                int count = db.getAccessCounts().getOrDefault(cache[i].getId(), 0);
                sb.append(String.format("%d. %-45s  accesses: %d%n", i + 1, cache[i].getTitle(), count));
            }
        }
        if (!any) sb.append("(no borrow activity yet)\n");
        return sb.toString();
    }

    // Generates a report of users with overdue library items.
    public String usersWithOverdueItemsReport() {
        StringBuilder sb = new StringBuilder("=== USERS WITH OVERDUE ITEMS ===\n");
        List<LibraryItem> overdue = borrowController.findOverdueItems();
        if (overdue.isEmpty()) {
            sb.append("(no overdue items — great job!)\n");
            return sb.toString();
        }
        for (LibraryItem item : overdue) {
            UserAccount user = db.findUserById(item.getBorrowedByUserId());
            String name = user != null ? user.getName() : item.getBorrowedByUserId();
            long days = borrowController.daysOverdue(item);
            double charge = borrowController.overdueCharge(item);
            sb.append(String.format("%-20s | %-40s | %2d day(s) overdue | charge: NGN %.2f%n",
                    name, item.getTitle(), days, charge));
        }
        return sb.toString();
    }

    // Summarizes the number of items in each category.
    public String categoryDistributionReport() {
        StringBuilder sb = new StringBuilder("=== CATEGORY DISTRIBUTION ===\n");
        List<LibraryItem> items = db.getItems();
        for (Category cat : Category.values()) {
            int count = RecursiveUtils.recursiveCountByCategory(items, cat, 0);
            sb.append(String.format("%-10s : %d item(s)%n", cat.getLabel(), count));
        }
        return sb.toString();
    }

    public String fullReport() {
        return mostBorrowedItemsReport() + "\n" + usersWithOverdueItemsReport() + "\n" + categoryDistributionReport();
    }
}
