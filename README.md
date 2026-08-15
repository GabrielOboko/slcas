# Gabriel's SLCAS
### Smart Library Circulation & Automation System
**COS 202 Project – MIVA Open University**

## Overview

Gabriel's SLCAS (Smart Library Circulation & Automation System) is a desktop library management application built with Java Swing for my COS 202 (Computer Programming II) course at MIVA Open University.

The aim of the project was to apply object-oriented programming concepts, implement common data structures and algorithms from scratch, and build a functional desktop application without relying on external libraries.

The system allows users to browse the library catalogue, search and sort resources, borrow and return items, reserve unavailable resources, and generate reports. Administrators can also manage the catalogue by adding, deleting, and restoring items.

Everything is built using core Java, making the project easy to run on any computer with a compatible JDK installed.

---

# Features

The application includes:

- User login for members and administrators
- Browse the library catalogue
- Borrow and return library items
- Reservation queue for unavailable items
- Automatic overdue detection
- Library reports and statistics
- Search using multiple search algorithms
- Sort using multiple sorting algorithms
- Undo support for administrator actions
- Save and load library data using JSON
- Automatic save when the application closes

---

# Technologies Used

- Java 17
- Java Swing
- Maven
- JSON (custom implementation)
- Object-Oriented Programming

No third-party libraries were used.

---

# Running the Project

### Option 1 — IntelliJ IDEA (Recommended)

1. Open the project in IntelliJ IDEA.
2. Allow Maven to load the project.
3. Run:

```
com.gabriel.slcas.App
```

---

### Option 2 — Maven

```bash
mvn package
java -jar target/gabriels-slcas.jar
```

---

### Option 3 — Using javac

```bash
./build_and_run.sh
```

---

## Requirements

- JDK 17 or newer

The first time the application runs, it automatically creates sample users and library resources.

After that, all changes are automatically saved to:

```
slcas_autosave.json
```

The data is loaded again the next time the application starts.

---

# Sample Accounts

### Members

- Iris
- Zuri
- Chancellor
- Papi
- Amarachi
- Uduak-Obong
- Anjolaoluwa

### Administrators

- Gabriel Oboko
- Zuri King

Administrator accounts have access to the Admin panel where items can be added, removed, restored, imported, exported, and reports generated.

---

# Project Structure

```
src/
└── com/
    └── gabriel/
        └── slcas/
            ├── App.java
            ├── model/
            ├── controller/
            ├── gui/
            └── utils/
```

---

# Course Requirements Covered

This project demonstrates the major concepts required for the COS 202 course.

| Requirement | Implementation |
|-------------|----------------|
| Abstract classes | `LibraryItem` |
| Inheritance | `Book`, `Magazine`, `Journal` |
| Interface | `Borrowable` |
| Encapsulation | `LibraryDatabase`, `UserAccount` |
| Polymorphism | Library item handling throughout the application |
| ArrayList | Library catalogue |
| Queue | Reservation system |
| Stack | Undo feature |
| Array | Most Frequently Used (MFU) cache |
| Linear Search | `SearchEngine` |
| Binary Search | `SearchEngine` |
| Recursive Search | `RecursiveUtils` |
| Selection Sort | `SortEngine` |
| Insertion Sort | `SortEngine` |
| Merge Sort | `SortEngine` |
| Quick Sort | `SortEngine` |
| Recursion | Search, sorting, category counting, overdue charge calculation |
| Event-driven programming | Swing event listeners and Timer |
| GUI | Java Swing |
| File handling | Custom JSON reader and writer |

---

# Application Highlights

Some of the features I particularly enjoyed implementing include:

- A custom colour-coded table renderer that visually distinguishes books, journals, and magazines.
- A reservation queue that automatically keeps track of users waiting for unavailable items.
- An undo feature that allows administrators to reverse catalogue changes.
- A background timer that checks for overdue items while the application is running.
- Multiple search and sorting algorithms that can be selected from the interface.
- A lightweight JSON parser written from scratch for saving and loading application data.

---

# Author

**Gabriel Chukwuebuka Oboko**

Smart Library Circulation & Automation System (SLCAS)
