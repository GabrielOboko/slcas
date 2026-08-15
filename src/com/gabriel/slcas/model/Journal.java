package com.gabriel.slcas.model;

public class Journal extends LibraryItem {

    public Journal(String id, String title, String publisher, int year, String issnOrDoi) {
        super(id, title, publisher, year, issnOrDoi, Category.JOURNAL);
    }

    @Override
    public String getDetails() {
        return String.format("[JOURNAL] \"%s\" - %s (%d) - ISSN/DOI %s", getTitle(), getAuthor(), getYear(), getIdentifier());
    }

    @Override
    public String getIdentifierLabel() {
        return "ISSN/DOI";
    }
}
