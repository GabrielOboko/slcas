package com.gabriel.slcas.model;

import java.time.LocalDate;

/**
 * Base class for all library items.
 * It contains the common properties and borrowing behaviour shared by
 * books, magazines, and journals.
 */
public abstract class LibraryItem implements Borrowable {

    private final String id;
    private String title;
    private String author;
    private int year;
    private final String identifier;
    private final Category category;

    private ItemStatus status = ItemStatus.AVAILABLE;
    private String borrowedByUserId = null;
    private LocalDate borrowDate = null;
    private LocalDate dueDate = null;
    private int accessCount = 0;

    protected LibraryItem(String id, String title, String author, int year,
                           String identifier, Category category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.identifier = identifier;
        this.category = category;
    }

    // Returns a formatted description of the item. 
    public abstract String getDetails();

    // Returns the label used for this item's identifier. 
    public abstract String getIdentifierLabel();

    @Override
    public boolean borrowItem(String userId, LocalDate borrowDate, LocalDate dueDate) {
        if (status != ItemStatus.AVAILABLE) {
            return false;
        }
        this.status = ItemStatus.BORROWED;
        this.borrowedByUserId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        return true;
    }

    @Override
    public boolean returnItem() {
        if (status != ItemStatus.BORROWED) {
            return false;
        }
        this.status = ItemStatus.AVAILABLE;
        this.borrowedByUserId = null;
        this.borrowDate = null;
        this.dueDate = null;
        return true;
    }

    @Override
    public boolean isAvailable() {
        return status == ItemStatus.AVAILABLE;
    }

    public void registerAccess() {
        accessCount++;
    }

    // getters / setters 
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getIdentifier() { return identifier; }
    public Category getCategory() { return category; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public String getBorrowedByUserId() { return borrowedByUserId; }
    public void setBorrowedByUserId(String borrowedByUserId) { this.borrowedByUserId = borrowedByUserId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public int getAccessCount() { return accessCount; }

    @Override
    public String toString() {
        return getDetails();
    }
}
