package com.libconnect.model;

public class Book {

    // ── Attributes ──────────────────────────────────────────────
    private String bookId;
    private String title;
    private String author;
    private String genre;
    private int copiesAvailable;

    // ── Constructor ──────────────────────────────────────────────
    public Book(String bookId, String title, String author, String genre, int copiesAvailable) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.copiesAvailable = copiesAvailable;
    }

    // ── Core Methods ─────────────────────────────────────────────

    /**
     * Prints all details of the book to the console.
     */
    public void displayBookDetails() {
        System.out.println("==============================");
        System.out.println("Book ID   : " + bookId);
        System.out.println("Title     : " + title);
        System.out.println("Author    : " + author);
        System.out.println("Genre     : " + genre);
        System.out.println("Copies    : " + copiesAvailable);
        System.out.println("==============================");
    }

    /**
     * Updates available copies.
     * Pass +1 when a book is returned, -1 when borrowed.
     */
    public void updateCopies(int change) {
        int updated = this.copiesAvailable + change;
        if (updated < 0) {
            System.out.println("Error: Not enough copies available for \"" + title + "\".");
        } else {
            this.copiesAvailable = updated;
            System.out.println("Copies updated. Now available: " + this.copiesAvailable);
        }
    }

    /**
     * Checks if at least one copy is available to borrow.
     */
    public boolean isAvailable() {
        return copiesAvailable > 0;
    }

    // ── Getters & Setters ────────────────────────────────────────
    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setCopiesAvailable(int copies) {
        this.copiesAvailable = copies;
    }

    // ── toString ─────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", copies=" + copiesAvailable +
                '}';
    }
}