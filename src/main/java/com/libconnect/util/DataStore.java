package com.libconnect.util;

import com.libconnect.model.Book;
import com.libconnect.model.Librarian;
import com.libconnect.model.Member;
import com.libconnect.model.User;

import java.util.ArrayList;
import java.util.List;

public class DataStore {

    // ── Singleton Instance ───────────────────────────────────────
    private static DataStore instance;

    // ── Centralized Collections ──────────────────────────────────
    private final List<Book> books;
    private final List<User> users;

    // ── Private Constructor (Singleton) ──────────────────────────
    private DataStore() {
        books = new ArrayList<>();
        users = new ArrayList<>();
        seedData(); // pre-load sample data on startup
    }

    // ── Get Singleton Instance ───────────────────────────────────
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Resets the singleton so the next call to {@link #getInstance()} returns a
     * fresh store (including default {@link #seedData()}). Intended for unit
     * tests; do not use from application code.
     */
    public static void resetForTests() {
        synchronized (DataStore.class) {
            instance = null;
        }
    }

    // ════════════════════════════════════════════════════════════
    // BOOK OPERATIONS
    // ════════════════════════════════════════════════════════════

    /**
     * Adds a new book to the collection.
     */
    public void addBook(Book book) {
        books.add(book);
    }

    /**
     * Removes a book by its ID.
     * Returns true if removed, false if not found.
     */
    public boolean removeBook(String bookId) {
        return books.removeIf(b -> b.getBookId().equals(bookId));
    }

    /**
     * Finds a book by its exact title (case-insensitive).
     */
    public Book getBookByTitle(String title) {
        return books.stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a book by its unique ID.
     */
    public Book getBookById(String bookId) {
        return books.stream()
                .filter(b -> b.getBookId().equals(bookId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Searches books by title OR author keyword (case-insensitive).
     */
    public List<Book> searchBooks(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(book);
            }
        }
        return results;
    }

    /**
     * Filters books by genre (case-insensitive).
     */
    public List<Book> getBooksByGenre(String genre) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getGenre().equalsIgnoreCase(genre)) {
                results.add(book);
            }
        }
        return results;
    }

    /**
     * Returns the full book catalog.
     */
    public List<Book> getAllBooks() {
        return books;
    }

    // ════════════════════════════════════════════════════════════
    // USER OPERATIONS
    // ════════════════════════════════════════════════════════════

    /**
     * Registers a new user into the system.
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Removes a user by their ID.
     * Returns true if removed, false if not found.
     */
    public boolean removeUser(String userId) {
        return users.removeIf(u -> u.getUserId().equals(userId));
    }

    /**
     * Finds a user by their unique ID.
     */
    public User getUserById(String userId) {
        return users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a user by their email address.
     */
    public User getUserByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Authenticates a user by email and password.
     * Returns the matched User object, or null if credentials are wrong.
     */
    public User authenticate(String email, String password) {
        return users.stream()
                .filter(u -> u.login(email, password))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns all registered users.
     */
    public List<User> getAllUsers() {
        return users;
    }

    /**
     * Returns only Member-type users.
     */
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Member m) {
                members.add(m);
            }
        }
        return members;
    }

    // ════════════════════════════════════════════════════════════
    // SEED DATA — Pre-loaded sample records
    // ════════════════════════════════════════════════════════════

    /**
     * Populates the system with default users and books on startup.
     */
    private void seedData() {

        // ── Default Users ────────────────────────────────────────
        users.add(new Librarian(
                "LIB-001",
                "Admin Librarian",
                "admin@libconnect.com",
                "admin123"));

        users.add(new Member(
                "MEM-001",
                "Alice Johnson",
                "alice@libconnect.com",
                "alice123"));

        users.add(new Member(
                "MEM-002",
                "Bob Smith",
                "bob@libconnect.com",
                "bob123"));

        // ── Default Books ─────────────────────────────────────────
        books.add(new Book("BK-001", "Clean Code", "Robert C. Martin", "Technology", 5));
        books.add(new Book("BK-002", "The Pragmatic Programmer", "David Thomas", "Technology", 3));
        books.add(new Book("BK-003", "To Kill a Mockingbird", "Harper Lee", "Fiction", 4));
        books.add(new Book("BK-004", "1984", "George Orwell", "Dystopian", 6));
        books.add(new Book("BK-005", "The Great Gatsby", "F. Scott Fitzgerald", "Classic", 2));
        books.add(new Book("BK-006", "Sapiens", "Yuval Noah Harari", "History", 7));
        books.add(new Book("BK-007", "Atomic Habits", "James Clear", "Self-Help", 8));
        books.add(new Book("BK-008", "The Alchemist", "Paulo Coelho", "Fiction", 5));
    }

    // ── Debug Utility ────────────────────────────────────────────

    /**
     * Prints a full summary of all stored data to the console.
     */
    public void printAllData() {
        System.out.println("\n══ USERS (" + users.size() + ") ══");
        users.forEach(System.out::println);
        System.out.println("\n══ BOOKS (" + books.size() + ") ══");
        books.forEach(System.out::println);
    }
}