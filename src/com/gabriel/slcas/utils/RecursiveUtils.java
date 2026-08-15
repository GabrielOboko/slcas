package com.gabriel.slcas.utils;

import java.util.List;

import com.gabriel.slcas.model.Category;
import com.gabriel.slcas.model.LibraryItem;

/**
 * Utility methods that use recursion for searching and calculations.
 */
public final class RecursiveUtils {

    private RecursiveUtils() { }

    /**
    * Performs a recursive linear search by title or author. */
    public static LibraryItem recursiveLinearSearch(List<LibraryItem> items, String query, String field, int index) {
        if (index >= items.size()) return null;
        LibraryItem item = items.get(index);
        String haystack = "author".equalsIgnoreCase(field) ? item.getAuthor() : item.getTitle();
        if (haystack != null && haystack.toLowerCase().contains(query.toLowerCase())) {
            return item;
        }
        return recursiveLinearSearch(items, query, field, index + 1);
    }

    /**
    * Performs a recursive binary search on a sorted list. */
    public static LibraryItem recursiveBinarySearch(List<LibraryItem> sortedItems, String query, String field, int low, int high) {
        if (low > high) return null;
        int mid = (low + high) / 2;
        LibraryItem item = sortedItems.get(mid);
        String key = "author".equalsIgnoreCase(field) ? item.getAuthor() : item.getTitle();
        int cmp = key.compareToIgnoreCase(query);
        if (cmp == 0) return item;
        if (cmp > 0) return recursiveBinarySearch(sortedItems, query, field, low, mid - 1);
        return recursiveBinarySearch(sortedItems, query, field, mid + 1, high);
    }

    /**
    * Counts the number of items in a category using recursion. */
    public static int recursiveCountByCategory(List<LibraryItem> items, Category category, int index) {
        if (index >= items.size()) return 0;
        int rest = recursiveCountByCategory(items, category, index + 1);
        return (items.get(index).getCategory() == category ? 1 : 0) + rest;
    }

    /**
    * Calculates the overdue charge based on the daily rate. */
    public static double recursiveOverdueCharge(long daysOverdue, double ratePerDay) {
        if (daysOverdue <= 0) return 0.0;
        return ratePerDay + recursiveOverdueCharge(daysOverdue - 1, ratePerDay);
    }
}
