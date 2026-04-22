package com.libconnect.model;

import com.libconnect.util.DataStore;

public class Librarian extends User {

    // ── Attributes ──────────────────────────────────────────────
    private final String[] librarianPrivileges = {
            "Add Book",
            "Remove Book",
            "Manage Members"
    };

    // ── Constructor ──────────────────────────────────────────────
    public Librarian(String userId, String username, String email, String password) {
        super(userId, username, email, password);
    }

    // ── Overridden Methods ───────────────────────────────────────
    @Override
    public void viewDashboard() {
        System.out.println("==============================");
        System.out.println("  LIBRARIAN DASHBOARD");
        System.out.println("  Welcome, " + getUsername());
        System.out.println("  Privileges:");
        for (String privilege : librarianPrivileges) {
            System.out.println("    - " + privilege);
        }
        System.out.println("==============================");
    }

    // ── Core Methods ─────────────────────────────────────────────

    /**
     * Adds a new book to the centralized DataStore.
     */
    public void addBook(String bookId, String title, String author, String genre, int copies) {
        Book newBook = new Book(bookId, title, author, genre, copies);
        DataStore.getInstance().addBook(newBook);
        System.out.println("Book added successfully: " + title);
    }

    /**
     * Removes a book from the DataStore by its ID.
     */
    public void removeBook(String bookId) {
        boolean removed = DataStore.getInstance().removeBook(bookId);
        if (removed) {
            System.out.println("Book removed successfully. ID: " + bookId);
        } else {
            System.out.println("Book not found with ID: " + bookId);
        }
    }

    /**
     * Manages a member account — supports actions like "ban" or "update".
     */
    public void manageMembers(String memberId, String action) {
        User user = DataStore.getInstance().getUserById(memberId);
        if (user == null) {
            System.out.println("Member not found with ID: " + memberId);
            return;
        }
        switch (action.toLowerCase()) {
            case "ban" -> System.out.println("Member " + user.getUsername() + " has been banned.");
            case "update" -> System.out.println("Member " + user.getUsername() + " details updated.");
            default -> System.out.println("Unknown action: " + action);
        }
    }

    // ── Getters ──────────────────────────────────────────────────
    public String[] getLibrarianPrivileges() {
        return librarianPrivileges;
    }
}