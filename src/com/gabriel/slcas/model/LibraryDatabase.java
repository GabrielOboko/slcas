package com.gabriel.slcas.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * Stores the application's library data, including catalogue items,
 * users, reservations, and undo history.
 */
public class LibraryDatabase {

    public static final int MFU_CACHE_SIZE = 5;

    private final ArrayList<LibraryItem> items = new ArrayList<>();
    private final ArrayList<UserAccount> users = new ArrayList<>();
    private final Map<String, Queue<String>> reservationQueues = new HashMap<>(); // Maps each item to its reservation queue
    private final Stack<AdminAction> undoStack = new Stack<>();
    private final LibraryItem[] mfuCache = new LibraryItem[MFU_CACHE_SIZE];
    private final Map<String, Integer> accessCounts = new HashMap<>();

    // Items 
    public ArrayList<LibraryItem> getItems() { return items; }

    public void addItem(LibraryItem item) {
        items.add(item);
    }

    public boolean removeItemById(String id) {
        return items.removeIf(i -> i.getId().equals(id));
    }

    public LibraryItem findItemById(String id) {
        for (LibraryItem item : items) {
            if (item.getId().equals(id)) return item;
        }
        return null;
    }

    // Users 
    public ArrayList<UserAccount> getUsers() { return users; }

    public void addUser(UserAccount user) { users.add(user); }

    public UserAccount findUserById(String id) {
        for (UserAccount u : users) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    // Reservation queues 
    public Queue<String> getReservationQueue(String itemId) {
        return reservationQueues.computeIfAbsent(itemId, k -> new LinkedList<>());
    }

    public Map<String, Queue<String>> getAllReservationQueues() { return reservationQueues; }

    // Undo history
    public Stack<AdminAction> getUndoStack() { return undoStack; }

    // Frequently accessed items
    public LibraryItem[] getMfuCache() { return mfuCache; }

    public void recordAccess(String itemId) {
        accessCounts.merge(itemId, 1, Integer::sum);
        recomputeMfuCache();
    }

    private void recomputeMfuCache() {
        // Refresh the most frequently accessed items cache.
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(accessCounts.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());
        for (int i = 0; i < MFU_CACHE_SIZE; i++) {
            if (i < entries.size()) {
                mfuCache[i] = findItemById(entries.get(i).getKey());
            } else {
                mfuCache[i] = null;
            }
        }
    }

    public Map<String, Integer> getAccessCounts() { return accessCounts; }
}
