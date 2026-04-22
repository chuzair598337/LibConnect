package com.libconnect.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Book")
class BookTest {

    private static final String ID = "BK-TST";
    private static final String TITLE = "Test Title";
    private static final String AUTHOR = "Test Author";
    private static final String GENRE = "Test Genre";

    @Nested
    @DisplayName("Constructor and accessors")
    class ConstructionTests {

        @Test
        @DisplayName("constructor sets all properties")
        void constructor_storesAllFields() {
            Book b = new Book(ID, TITLE, AUTHOR, GENRE, 3);
            assertAll(
                    () -> assertEquals(ID, b.getBookId()),
                    () -> assertEquals(TITLE, b.getTitle()),
                    () -> assertEquals(AUTHOR, b.getAuthor()),
                    () -> assertEquals(GENRE, b.getGenre()),
                    () -> assertEquals(3, b.getCopiesAvailable()));
        }

        @Test
        @DisplayName("setters update all mutable fields")
        void setters_roundTrip() {
            Book b = new Book("x", "x", "x", "x", 1);
            b.setBookId("BK-2");
            b.setTitle("T2");
            b.setAuthor("A2");
            b.setGenre("G2");
            b.setCopiesAvailable(9);
            assertAll(
                    () -> assertEquals("BK-2", b.getBookId()),
                    () -> assertEquals("T2", b.getTitle()),
                    () -> assertEquals("A2", b.getAuthor()),
                    () -> assertEquals("G2", b.getGenre()),
                    () -> assertEquals(9, b.getCopiesAvailable()));
        }
    }

    @Nested
    @DisplayName("isAvailable()")
    class AvailabilityTests {

        @Test
        @DisplayName("true when copies > 0")
        void trueWhenPositiveCopies() {
            assertTrue(new Book(ID, TITLE, AUTHOR, GENRE, 1).isAvailable());
        }

        @Test
        @DisplayName("false when copies == 0")
        void falseWhenZero() {
            assertFalse(new Book(ID, TITLE, AUTHOR, GENRE, 0).isAvailable());
        }
    }

    @Nested
    @DisplayName("updateCopies(int)")
    class UpdateCopiesTests {

        @Test
        @DisplayName("increments available copies for positive change")
        void addCopies() {
            Book b = new Book(ID, TITLE, AUTHOR, GENRE, 2);
            b.updateCopies(2);
            assertEquals(4, b.getCopiesAvailable());
        }

        @Test
        @DisplayName("decrements available copies when result stays non-negative")
        void borrowDecrements() {
            Book b = new Book(ID, TITLE, AUTHOR, GENRE, 2);
            b.updateCopies(-1);
            assertEquals(1, b.getCopiesAvailable());
        }

        @Test
        @DisplayName("does not change copies when result would be negative (borrow denied)")
        void doesNotGoNegative() {
            Book b = new Book(ID, TITLE, AUTHOR, GENRE, 0);
            b.updateCopies(-1);
            assertEquals(0, b.getCopiesAvailable());
        }
    }

    @Nested
    @DisplayName("toString()")
    class ToStringTests {

        @Test
        @DisplayName("includes id, title, author, genre, and copy count")
        void includesKeyFields() {
            Book b = new Book(ID, TITLE, AUTHOR, GENRE, 4);
            String s = b.toString();
            assertAll(
                    () -> assertNotNull(s),
                    () -> assertTrue(s.contains(ID)),
                    () -> assertTrue(s.contains(TITLE)),
                    () -> assertTrue(s.contains(AUTHOR)),
                    () -> assertTrue(s.contains(GENRE)),
                    () -> assertTrue(s.contains("4")));
        }
    }

    @Nested
    @DisplayName("displayBookDetails()")
    class DisplayDetailsTests {

        @Test
        @DisplayName("writes book attributes to System.out")
        void printsExpectedContent() {
            Book b = new Book(ID, TITLE, AUTHOR, GENRE, 2);
            PrintStream orig = System.out;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
                b.displayBookDetails();
            } finally {
                System.setOut(orig);
            }
            String out = buf.toString(StandardCharsets.UTF_8);
            assertAll(
                    () -> assertTrue(out.contains("Book ID")),
                    () -> assertTrue(out.contains(ID)),
                    () -> assertTrue(out.contains(TITLE)),
                    () -> assertTrue(out.contains(AUTHOR)),
                    () -> assertTrue(out.contains(GENRE)));
        }
    }
}
