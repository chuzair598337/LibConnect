package com.libconnect.model;

import com.libconnect.util.DataStore;

public class Guest extends User {

    // ── Constructor ──────────────────────────────────────────────
    public Guest() {
        super("GUEST-000", "Guest", "guest@libconnect.com", "");
    }

    // ── Overridden Methods ───────────────────────────────────────
    @Override
    public void viewDashboard() {
        System.out.println("==============================");
        System.out.println("  GUEST DASHBOARD");
        System.out.println("  You are browsing as a Guest.");
        System.out.println("  Login to access full features.");
        System.out.println("==============================");
    }

    // ── Core Methods ─────────────────────────────────────────────

    /**
     * Allows the guest to view details of a book by title (read-only).
     */
    public void viewBookDetails(String bookTitle) {
        Book book = DataStore.getInstance().getBookByTitle(bookTitle);
        if (book == null) {
            System.out.println("Book not found: " + bookTitle);
            return;
        }
        book.displayBookDetails();
    }
}