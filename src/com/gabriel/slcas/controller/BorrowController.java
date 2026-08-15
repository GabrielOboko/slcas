package com.gabriel.slcas.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.LibraryItem;

// Manages the borrow / return / reservation queue / overdue workflow. 
public class BorrowController {

    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double OVERDUE_RATE_PER_DAY = 50.0; // Daily fine (NGN)

    private final LibraryDatabase db;

    public BorrowController(LibraryDatabase db) {
        this.db = db;
    }

    // Returns a status message describing what happened. 
    public String borrow(String itemId, String userId) {
        LibraryItem item = db.findItemById(itemId);
        UserAccount user = db.findUserById(userId);
        if (item == null || user == null) return "Item or user not found.";

        db.recordAccess(itemId);

        if (!item.isAvailable()) {
            Queue<String> queue = db.getReservationQueue(itemId);
            if (!queue.contains(userId) && !userId.equals(item.getBorrowedByUserId())) {
                queue.offer(userId);
                return user.getName() + " was added to the reservation queue for \"" + item.getTitle() + "\" (position " + queue.size() + ").";
            }
            return "\"" + item.getTitle() + "\" is currently unavailable.";
        }

        LocalDate today = LocalDate.now();
        LocalDate due = today.plusDays(LOAN_PERIOD_DAYS);
        item.borrowItem(userId, today, due);
        user.logBorrow(item.getId(), item.getTitle(), fmt(today), fmt(due));
        return user.getName() + " successfully borrowed \"" + item.getTitle() + "\" — due " + fmt(due) + ".";
    }

    public String returnItem(String itemId, String userId) {
        LibraryItem item = db.findItemById(itemId);
        UserAccount user = db.findUserById(userId);
        if (item == null || user == null) return "Item or user not found.";
        if (!userId.equals(item.getBorrowedByUserId())) {
            return user.getName() + " does not currently hold \"" + item.getTitle() + "\".";
        }
        item.returnItem();
        user.logReturn(itemId, item.getTitle(), fmt(LocalDate.now()));

        Queue<String> queue = db.getReservationQueue(itemId);
        String next = queue.poll();
        if (next != null) {
            UserAccount nextUser = db.findUserById(next);
            LocalDate today = LocalDate.now();
            LocalDate due = today.plusDays(LOAN_PERIOD_DAYS);
            item.borrowItem(next, today, due);
            if (nextUser != null) {
                nextUser.logBorrow(item.getId(), item.getTitle(), fmt(today), fmt(due));
                return "\"" + item.getTitle() + "\" returned and auto-issued to next in queue: " + nextUser.getName() + ".";
            }
        }
        return "\"" + item.getTitle() + "\" returned successfully and is now available.";
    }

    /** Returns all currently borrowed items whose due date has passed. */
    public List<LibraryItem> findOverdueItems() {
        List<LibraryItem> overdue = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (LibraryItem item : db.getItems()) {
            if (item.getDueDate() != null && item.getDueDate().isBefore(today)) {
                overdue.add(item);
            }
        }
        return overdue;
    }

    public long daysOverdue(LibraryItem item) {
        if (item.getDueDate() == null) return 0;
        long days = java.time.temporal.ChronoUnit.DAYS.between(item.getDueDate(), LocalDate.now());
        return Math.max(days, 0);
    }

    public double overdueCharge(LibraryItem item) {
        return com.gabriel.slcas.utils.RecursiveUtils.recursiveOverdueCharge(daysOverdue(item), OVERDUE_RATE_PER_DAY);
    }

    private static String fmt(LocalDate d) {
        return d.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    }
}
