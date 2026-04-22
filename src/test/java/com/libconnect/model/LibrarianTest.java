package com.libconnect.model;

import com.libconnect.util.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Librarian")
class LibrarianTest {

    private static final String LIB_ID = "LIB-TEST-1";
    private static final String NAME = "Dana";
    private static final String EMAIL = "dana@libconnect.com";
    private static final String PASSWORD = "pw123";

    private Librarian librarian;

    @BeforeEach
    void resetStore() {
        DataStore.resetForTests();
        librarian = new Librarian(LIB_ID, NAME, EMAIL, PASSWORD);
    }

    @Nested
    @DisplayName("Inheritance and privileges")
    class CoreTests {

        @Test
        @DisplayName("constructor passes User fields to superclass")
        void inheritsUserData() {
            assertAll(
                    () -> assertEquals(LIB_ID, librarian.getUserId()),
                    () -> assertEquals(NAME, librarian.getUsername()),
                    () -> assertEquals(EMAIL, librarian.getEmail()),
                    () -> assertEquals(PASSWORD, librarian.getPassword()));
        }

        @Test
        @DisplayName("getLibrarianPrivileges returns the fixed role list")
        void privilegesMatchExpected() {
            assertArrayEquals(
                    new String[] { "Add Book", "Remove Book", "Manage Members" },
                    librarian.getLibrarianPrivileges());
        }
    }

    @Nested
    @DisplayName("DataStore integration")
    class DataStoreTests {

        @Test
        @DisplayName("addBook adds a book that can be looked up by id")
        void addBook_persists() {
            librarian.addBook("BK-900", "Unit Test Book", "Tester", "QA", 2);
            Book added = DataStore.getInstance().getBookById("BK-900");
            assertAll(
                    () -> assertNotNull(added),
                    () -> assertEquals("Unit Test Book", added.getTitle()),
                    () -> assertEquals(2, added.getCopiesAvailable()));
        }

        @Test
        @DisplayName("removeBook removes an existing seeded book by id")
        void removeBook_existingId() {
            assertNotNull(DataStore.getInstance().getBookById("BK-001"));
            librarian.removeBook("BK-001");
            assertNull(DataStore.getInstance().getBookById("BK-001"));
        }
    }

    @Nested
    @DisplayName("manageMembers")
    class ManageMembersTests {

        @Test
        @DisplayName("ban action prints a message for an existing member id")
        void banAction() {
            assertNotNull(DataStore.getInstance().getUserById("MEM-001"));
            PrintStream orig = System.out;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
                librarian.manageMembers("MEM-001", "ban");
            } finally {
                System.setOut(orig);
            }
            String out = buf.toString(StandardCharsets.UTF_8);
            assertTrue(out.toLowerCase().contains("banned"), out);
        }

        @Test
        @DisplayName("update action is recognized (case handled by switch on lowercased value)")
        void updateAction() {
            PrintStream orig = System.out;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
                librarian.manageMembers("MEM-001", "UpDaTe");
            } finally {
                System.setOut(orig);
            }
            assertTrue(
                    buf.toString(StandardCharsets.UTF_8).toLowerCase().contains("updated"));
        }

        @Test
        @DisplayName("unknown user id is reported and does not throw")
        void memberMissing() {
            PrintStream orig = System.out;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
                librarian.manageMembers("NO-SUCH", "ban");
            } finally {
                System.setOut(orig);
            }
            assertTrue(
                    buf.toString(StandardCharsets.UTF_8).contains("not found")
                            || buf.toString(StandardCharsets.UTF_8)
                                    .contains("Member not found"));
        }

        @Test
        @DisplayName("unknown action string produces unknown-action output")
        void unknownAction() {
            PrintStream orig = System.out;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
                librarian.manageMembers("MEM-001", "nope");
            } finally {
                System.setOut(orig);
            }
            assertTrue(buf.toString(StandardCharsets.UTF_8).toLowerCase().contains("unknown"));
        }
    }
}
