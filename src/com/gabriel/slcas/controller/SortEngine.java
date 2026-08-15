package com.gabriel.slcas.controller;

import java.util.Comparator;
import java.util.List;

import com.gabriel.slcas.model.LibraryItem;

// Provides sorting algorithms for library items.
public final class SortEngine {

    public enum Algorithm { SELECTION, INSERTION, MERGE, QUICK }
    public enum Field { TITLE, AUTHOR, YEAR }

    private SortEngine() { }

    public static Comparator<LibraryItem> comparatorFor(Field field) {
        switch (field) {
            case AUTHOR: return Comparator.comparing(LibraryItem::getAuthor, String.CASE_INSENSITIVE_ORDER);
            case YEAR:   return Comparator.comparingInt(LibraryItem::getYear);
            default:     return Comparator.comparing(LibraryItem::getTitle, String.CASE_INSENSITIVE_ORDER);
        }
    }

    public static void sort(List<LibraryItem> list, Algorithm algorithm, Field field) {
        Comparator<LibraryItem> cmp = comparatorFor(field);
        switch (algorithm) {
            case SELECTION: selectionSort(list, cmp); break;
            case INSERTION: insertionSort(list, cmp); break;
            case MERGE:     mergeSort(list, 0, list.size() - 1, cmp); break;
            case QUICK:     quickSort(list, 0, list.size() - 1, cmp); break;
        }
    }

    // Selection sort
    public static void selectionSort(List<LibraryItem> list, Comparator<LibraryItem> cmp) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (cmp.compare(list.get(j), list.get(minIdx)) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                swap(list, i, minIdx);
            }
        }
    }

    // Insertion sort
    public static void insertionSort(List<LibraryItem> list, Comparator<LibraryItem> cmp) {
        int n = list.size();
        for (int i = 1; i < n; i++) {
            LibraryItem key = list.get(i);
            int j = i - 1;
            while (j >= 0 && cmp.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    // Merge sort 
    public static void mergeSort(List<LibraryItem> list, int left, int right, Comparator<LibraryItem> cmp) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSort(list, left, mid, cmp);
        mergeSort(list, mid + 1, right, cmp);
        merge(list, left, mid, right, cmp);
    }

    private static void merge(List<LibraryItem> list, int left, int mid, int right, Comparator<LibraryItem> cmp) {
        List<LibraryItem> leftPart = new java.util.ArrayList<>(list.subList(left, mid + 1));
        List<LibraryItem> rightPart = new java.util.ArrayList<>(list.subList(mid + 1, right + 1));
        int i = 0, j = 0, k = left;
        while (i < leftPart.size() && j < rightPart.size()) {
            if (cmp.compare(leftPart.get(i), rightPart.get(j)) <= 0) {
                list.set(k++, leftPart.get(i++));
            } else {
                list.set(k++, rightPart.get(j++));
            }
        }
        while (i < leftPart.size()) list.set(k++, leftPart.get(i++));
        while (j < rightPart.size()) list.set(k++, rightPart.get(j++));
    }

    // Quick sort 
    public static void quickSort(List<LibraryItem> list, int low, int high, Comparator<LibraryItem> cmp) {
        if (low >= high) return;
        int pivotIndex = partition(list, low, high, cmp);
        quickSort(list, low, pivotIndex - 1, cmp);
        quickSort(list, pivotIndex + 1, high, cmp);
    }

    private static int partition(List<LibraryItem> list, int low, int high, Comparator<LibraryItem> cmp) {
        LibraryItem pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (cmp.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static void swap(List<LibraryItem> list, int a, int b) {
        LibraryItem tmp = list.get(a);
        list.set(a, list.get(b));
        list.set(b, tmp);
    }
}
