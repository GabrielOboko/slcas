package com.gabriel.slcas.controller;

import java.io.IOException;

import com.gabriel.slcas.model.AdminAction;
import com.gabriel.slcas.model.Book;
import com.gabriel.slcas.model.Journal;
import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.LibraryItem;
import com.gabriel.slcas.model.Magazine;
import com.gabriel.slcas.utils.FileHandler;
import com.gabriel.slcas.utils.IDGenerator;

/**
 * Main entry point for catalogue operations used by the GUI.
 * Handles item management, undo, and saving/loading the library.
 */
public class LibraryManager {

    private final LibraryDatabase db;

    public LibraryManager(LibraryDatabase db) {
        this.db = db;
    }

    public LibraryDatabase getDatabase() { return db; }

    // Returns the display details for any library item.
    public String describe(LibraryItem item) {
        return item.getDetails();
    }

    public Book addBook(String title, String author, int year, String isbn) {
        Book book = new Book(IDGenerator.nextBookId(), title, author, year, isbn);
        db.addItem(book);
        db.getUndoStack().push(new AdminAction(AdminAction.Type.ADD_ITEM, book, -1));
        return book;
    }

    public Magazine addMagazine(String title, String publisher, int year, String issn) {
        Magazine mag = new Magazine(IDGenerator.nextMagazineId(), title, publisher, year, issn);
        db.addItem(mag);
        db.getUndoStack().push(new AdminAction(AdminAction.Type.ADD_ITEM, mag, -1));
        return mag;
    }

    public Journal addJournal(String title, String publisher, int year, String issnDoi) {
        Journal journal = new Journal(IDGenerator.nextJournalId(), title, publisher, year, issnDoi);
        db.addItem(journal);
        db.getUndoStack().push(new AdminAction(AdminAction.Type.ADD_ITEM, journal, -1));
        return journal;
    }

    public boolean deleteItem(String itemId) {
        int index = db.getItems().indexOf(db.findItemById(itemId));
        LibraryItem item = db.findItemById(itemId);
        if (item == null) return false;
        db.getItems().remove(index);
        db.getUndoStack().push(new AdminAction(AdminAction.Type.DELETE_ITEM, item, index));
        return true;
    }

    // Reverses the most recent add or delete operation.
    public String undoLastAction() {
        if (db.getUndoStack().isEmpty()) {
            return "Nothing to undo.";
        }
        AdminAction action = db.getUndoStack().pop();
        if (action.getType() == AdminAction.Type.ADD_ITEM) {
            db.getItems().remove(action.getItem());
            return "Undo: removed newly added item " + action.getItem().getId();
        } else {
            int idx = Math.min(action.getIndexAtTimeOfDeletion(), db.getItems().size());
            db.getItems().add(idx, action.getItem());
            return "Undo: restored deleted item " + action.getItem().getId();
        }
    }

    public void save(String path) throws IOException {
        FileHandler.save(db, path);
    }

    public void load(String path) throws IOException {
        FileHandler.load(db, path);
    }
}
