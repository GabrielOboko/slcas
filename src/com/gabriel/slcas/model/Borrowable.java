package com.gabriel.slcas.model;

import java.time.LocalDate;

/**
 * Defines the basic borrowing and return operations for library items.
 */
public interface Borrowable {

    // Borrows the item for the specified user. 
    boolean borrowItem(String userId, LocalDate borrowDate, LocalDate dueDate);

    // Returns the item if it is currently on loan. 
    boolean returnItem();

    boolean isAvailable();
}
