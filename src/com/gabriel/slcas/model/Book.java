package com.gabriel.slcas.model;

public class Book extends LibraryItem {

    public Book(String id, String title, String author, int year, String isbn) {
        super(id, title, author, year, isbn, Category.BOOK);
    }

    @Override
    public String getDetails() {
        return String.format("[BOOK] \"%s\" by %s (%d) - ISBN %s", getTitle(), getAuthor(), getYear(), getIdentifier());
    }

    @Override
    public String getIdentifierLabel() {
        return "ISBN";
    }
}
