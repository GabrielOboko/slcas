package com.gabriel.slcas.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates sequential IDs for library items and users.
 */
public final class IDGenerator {

    private static final AtomicInteger bookSeq = new AtomicInteger(0);
    private static final AtomicInteger magazineSeq = new AtomicInteger(0);
    private static final AtomicInteger journalSeq = new AtomicInteger(0);
    private static final AtomicInteger userSeq = new AtomicInteger(0);

    private IDGenerator() { }

    public static String nextBookId() { return String.format("BK-%03d", bookSeq.incrementAndGet()); }
    public static String nextMagazineId() { return String.format("MG-%03d", magazineSeq.incrementAndGet()); }
    public static String nextJournalId() { return String.format("JR-%03d", journalSeq.incrementAndGet()); }
    public static String nextUserId() { return String.format("USR-%03d", userSeq.incrementAndGet()); }

    /**
    * Updates the counters after loading saved data to prevent duplicate IDs.
    */
    public static void fastForward(String id) {
        if (id == null || id.length() < 4) return;
        try {
            int n = Integer.parseInt(id.substring(id.lastIndexOf('-') + 1));
            if (id.startsWith("BK-")) bumpTo(bookSeq, n);
            else if (id.startsWith("MG-")) bumpTo(magazineSeq, n);
            else if (id.startsWith("JR-")) bumpTo(journalSeq, n);
            else if (id.startsWith("USR-")) bumpTo(userSeq, n);
        } catch (NumberFormatException ignored) { }
    }

    private static void bumpTo(AtomicInteger counter, int n) {
        counter.updateAndGet(cur -> Math.max(cur, n));
    }
}
