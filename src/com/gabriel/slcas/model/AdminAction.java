package com.gabriel.slcas.model;

// Stores information needed to undo an admin action (add or delete item)
public class AdminAction {

    public enum Type { ADD_ITEM, DELETE_ITEM }

    private final Type type;
    private final LibraryItem item;
    private final int indexAtTimeOfDeletion; // Used when restoring a deleted item

    public AdminAction(Type type, LibraryItem item, int indexAtTimeOfDeletion) {
        this.type = type;
        this.item = item;
        this.indexAtTimeOfDeletion = indexAtTimeOfDeletion;
    }

    public Type getType() { return type; }
    public LibraryItem getItem() { return item; }
    public int getIndexAtTimeOfDeletion() { return indexAtTimeOfDeletion; }
}
