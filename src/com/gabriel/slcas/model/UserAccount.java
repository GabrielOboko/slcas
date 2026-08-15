package com.gabriel.slcas.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a library user, including their role,
 * borrowing history, and currently borrowed items.
 */
public class UserAccount {

    private final String id;
    private final String name;
    private final Role role;
    private final List<String> borrowingHistory = new ArrayList<>();   // Borrowing activity log
    private final Set<String> currentlyBorrowedItemIds = new LinkedHashSet<>();

    public UserAccount(String id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public void logBorrow(String itemId, String title, String borrowDate, String dueDate) {
        currentlyBorrowedItemIds.add(itemId);
        borrowingHistory.add(String.format("BORROWED  %-8s %-45s on %s (due %s)", itemId, title, borrowDate, dueDate));
    }

    public void logReturn(String itemId, String title, String returnDate) {
        currentlyBorrowedItemIds.remove(itemId);
        borrowingHistory.add(String.format("RETURNED  %-8s %-45s on %s", itemId, title, returnDate));
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public boolean isAdmin() { return role == Role.ADMIN; }
    public List<String> getBorrowingHistory() { return borrowingHistory; }
    public Set<String> getCurrentlyBorrowedItemIds() { return currentlyBorrowedItemIds; }

    @Override
    public String toString() {
        return name + " (" + id + (isAdmin() ? " - ADMIN" : " - MEMBER") + ")";
    }
}
