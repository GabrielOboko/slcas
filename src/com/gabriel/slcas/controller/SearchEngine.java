package com.gabriel.slcas.controller;

import java.util.ArrayList;
import java.util.List;

import com.gabriel.slcas.model.LibraryItem;
import com.gabriel.slcas.utils.RecursiveUtils;

// Provides linear, binary, and recursive search methods for library items.
public final class SearchEngine {

    public enum Algorithm { LINEAR, BINARY, RECURSIVE }
    public enum Field { TITLE, AUTHOR }

    private SearchEngine() { }

    // Finds all case-insensitive matches for the given query.
    public static List<LibraryItem> linearSearch(List<LibraryItem> items, String query, Field field) {
        List<LibraryItem> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (LibraryItem item : items) {
            String haystack = field == Field.AUTHOR ? item.getAuthor() : item.getTitle();
            if (haystack != null && haystack.toLowerCase().contains(q)) {
                results.add(item);
            }
        }
        return results;
    }

    // Searches a sorted list for an exact match.
    public static LibraryItem binarySearch(List<LibraryItem> sortedItems, String query, Field field) {
        int low = 0, high = sortedItems.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            LibraryItem item = sortedItems.get(mid);
            String key = field == Field.AUTHOR ? item.getAuthor() : item.getTitle();
            int cmp = key.compareToIgnoreCase(query);
            if (cmp == 0) return item;
            if (cmp > 0) high = mid - 1; else low = mid + 1;
        }
        return null;
    }

    public static LibraryItem recursiveSearch(List<LibraryItem> items, String query, Field field) {
        return RecursiveUtils.recursiveLinearSearch(items, query, field.name(), 0);
    }
}
