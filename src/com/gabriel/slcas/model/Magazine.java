package com.gabriel.slcas.model;

public class Magazine extends LibraryItem {

    public Magazine(String id, String title, String publisher, int year, String issn) {
        super(id, title, publisher, year, issn, Category.MAGAZINE);
    }

    @Override
    public String getDetails() {
        return String.format("[MAGAZINE] \"%s\" - %s (%d) - ISSN %s", getTitle(), getAuthor(), getYear(), getIdentifier());
    }

    @Override
    public String getIdentifierLabel() {
        return "ISSN";
    }
}
