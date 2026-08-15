package com.gabriel.slcas.model;

// Library item categories. 
public enum Category {
    BOOK("Book"),
    MAGAZINE("Magazine"),
    JOURNAL("Journal");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
