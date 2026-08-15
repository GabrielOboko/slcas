package com.gabriel.slcas.utils;

import com.gabriel.slcas.controller.LibraryManager;
import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.Role;
import com.gabriel.slcas.model.UserAccount;

/**
 * Adds some default users and library resources when the application starts for the first time. This makes it easier to test the system
 * without having to enter everything manually.
 */
public final class SeedData {

    private SeedData() { }

    public static void populate(LibraryDatabase db) {
        LibraryManager mgr = new LibraryManager(db);

        // Members
        String[] members = {"Iris", "Zuri", "Chancellor", "Papi", "Amarachi", "Uduak-Obong", "Anjolaoluwa"};
        for (String name : members) {
            db.addUser(new UserAccount(IDGenerator.nextUserId(), name, Role.MEMBER));
        }
        // Admins
        db.addUser(new UserAccount(IDGenerator.nextUserId(), "Gabriel Oboko", Role.ADMIN));
        db.addUser(new UserAccount(IDGenerator.nextUserId(), "Zuri King", Role.ADMIN));

        // Books
        mgr.addBook("Cryptography and Network Security", "William Stallings", 2017, "978-0134444284");
        mgr.addBook("Computer Security: Principles and Practice", "William Stallings and Lawrie Brown", 2018, "978-0134794105");
        mgr.addBook("Practical Malware Analysis", "Michael Sikorski and Andrew Honig", 2012, "978-1593272906");
        mgr.addBook("Black Hat Python", "Justin Seitz and Tim Arnold", 2021, "978-1718501126");
        mgr.addBook("Digital Forensics and Incident Response", "Jason Luttgens, Matthew Pepe and Kevin Mandia", 2014, "978-0071798686");
        mgr.addBook("The Essential Physics of Medical Imaging", "Jerrold T. Bushberg et al.", 2020, "978-1975103224");
        mgr.addBook("Introduction to Radiological Physics and Radiation Dosimetry", "Frank Herbert Attix", 2004, "978-3527406111");
        mgr.addBook("Mixing Secrets for the Small Studio", "Mike Senior", 2018, "978-1138556379");
        mgr.addBook("Modern Recording Techniques", "David Miles Huber and Robert E. Runstein", 2017, "978-1138954373");
        mgr.addBook("Mastering Audio", "Bob Katz", 2015, "978-0240818962");
        mgr.addBook("The Art of Game Design", "Jesse Schell", 2019, "978-1138632059");
        mgr.addBook("Game Engine Architecture", "Jason Gregory", 2018, "978-1138035454");
        mgr.addBook("Basketball on Paper", "Dean Oliver", 2004, "978-1574886887");
        mgr.addBook("Color and Light", "James Gurney", 2010, "978-0740797712");
        mgr.addBook("Framed Ink", "Marcos Mateu-Mestre", 2010, "978-1933492957");

        // Journals
        mgr.addJournal("Medical Physics", "American Association of Physicists in Medicine", 2026, "ISSN 0094-2405");
        mgr.addJournal("Physics in Medicine & Biology", "Institute of Physics Publishing", 2026, "ISSN 0031-9155");
        mgr.addJournal("Journal of Cybersecurity", "Oxford University Press", 2026, "ISSN 2057-2085");
        mgr.addJournal("African Journal of Information and Communication Technology", "AJICT Editorial Board", 2026, "ISSN 2006-1781");
        mgr.addJournal("Journal of Sports Analytics", "IOS Press", 2026, "ISSN 2215-020X");
        mgr.addJournal("Leonardo", "MIT Press", 2026, "ISSN 0024-094X");

        // Magazines
        mgr.addMagazine("Sound On Sound", "Sound On Sound Ltd", 2026, "ISSN 0951-6816");
        mgr.addMagazine("PC Gamer", "Future plc", 2026, "ISSN 1080-4471");
        mgr.addMagazine("EDGE", "Future plc", 2026, "ISSN 1350-1593");
        mgr.addMagazine("SLAM", "SLAM Media Inc.", 2026, "ISSN 1093-9673");
        mgr.addMagazine("ImagineFX", "Future plc", 2026, "ISSN 1752-0763");
        mgr.addMagazine("Techpoint Africa Magazine", "Techpoint Africa", 2026, "N/A");
        mgr.addMagazine("BusinessDay Tech", "BusinessDay Media Ltd", 2026, "N/A");
        mgr.addMagazine("NBA Africa Review", "NBA Africa", 2026, "N/A");

        // The sample data adds lots of items, which also fills the undo stack.
        // Clear it here so the admin only sees actions performed during actual use.
        db.getUndoStack().clear();
    }
}
