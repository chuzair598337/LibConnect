package com.libconnect.model;

import com.libconnect.util.DataStore;
import java.util.ArrayList;
import java.util.List;

public class Member extends User {

    // ── Attributes ──────────────────────────────────────────────
    private List<String> borrowedBooks; // stores titles of borrowed books

    // ── Constructor ──────────────────────────────────────────────
    public Member(String userId, String username, String email, String password) {
        super(userId, username, email, password);
        this.borrowedBooks = new ArrayList<>();
    }

    // ── Overridden Methods ───────────────────────────────────────
    @Override
    public void viewDashboard() {
        System.out.println("==============================");
        System.out.println("  MEMBER DASHBOARD");
        System.out.println("  Welcome, " + getUsername());
        System.out.println("  Borrowed Books: " + borrowedBooks.size());
        System.out.println("==============================");
    }

    // ── Core Methods ─────────────────────────────────────────────

    /**
     * Borrows a book by title if available in the DataStore.
     */
    public void borrowBook(String bookTitle) {
        Book book = DataStore.getInstance().getBookByTitle(bookTitle);
        if (book == null) {
            System.out.println("Book not found: " + bookTitle);
            return;
        }
        if (!book.isAvailable()) {
            System.out.println("No copies available for: " + bookTitle);
            return;
        }
        book.updateCopies(-1);
        borrowedBooks.add(bookTitle);
        System.out.println(getUsername() + " successfully borrowed: " + bookTitle);
    }

    /**
     * Returns a previously borrowed book back to the library.
     */
    public void returnBook(String bookTitle) {
        if (!borrowedBooks.contains(bookTitle)) {
            System.out.println("You have not borrowed: " + bookTitle);
            return;
        }
        Book book = DataStore.getInstance().getBookByTitle(bookTitle);
        if (book != null) {
            book.updateCopies(+1);
        }
        borrowedBooks.remove(bookTitle);
        System.out.println(getUsername() + " successfully returned: " + bookTitle);
    }

    /**
     * Displays details of a specific book by title.
     */
    public void viewBookDetails(String bookTitle) {
        Book book = DataStore.getInstance().getBookByTitle(bookTitle);
        if (book == null) {
            System.out.println("Book not found: " + bookTitle);
            return;
        }
        book.displayBookDetails();
    }

    // ── Getters ──────────────────────────────────────────────────
    public List<String> getBorrowedBooks() {
        return borrowedBooks;
    }
}