package com.libconnect.util;

import com.libconnect.model.Book;
import com.libconnect.model.Librarian;
import com.libconnect.model.Member;
import com.libconnect.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DataStore (singleton storage)")
class DataStoreTest {

    /** Seeded in {@link DataStore} constructor via {@code seedData()}. */
    private static final int SEEDED_BOOK_COUNT = 8;
    private static final int SEEDED_USER_COUNT = 3;
    private static final int SEEDED_MEMBER_COUNT = 2;

    private DataStore store;

    @BeforeEach
    void freshStore() {
        DataStore.resetForTests();
        store = DataStore.getInstance();
    }

    @Nested
    @DisplayName("Singleton")
    class SingletonTests {

        @Test
        @DisplayName("getInstance() returns the same instance until reset")
        void getInstance_isSingletonPerLifecycle() {
            assertSame(DataStore.getInstance(), DataStore.getInstance());
        }

        @Test
        @DisplayName("resetForTests() allows a new instance with a fresh seed")
        void reset_yieldsNewInstance() {
            DataStore first = DataStore.getInstance();
            DataStore.resetForTests();
            DataStore second = DataStore.getInstance();
            assertAll(
                    () -> assertNotSame(first, second),
                    () -> assertEquals(SEEDED_BOOK_COUNT, second.getAllBooks().size()),
                    () -> assertEquals(SEEDED_USER_COUNT, second.getAllUsers().size()));
        }
    }

    @Nested
    @DisplayName("Seeded data")
    class SeedDataTests {

        @Test
        @DisplayName("default catalog and user list sizes match seedData()")
        void seededCounts() {
            assertAll(
                    () -> assertEquals(SEEDED_BOOK_COUNT, store.getAllBooks().size()),
                    () -> assertEquals(SEEDED_USER_COUNT, store.getAllUsers().size()),
                    () -> assertEquals(SEEDED_MEMBER_COUNT, store.getAllMembers().size()));
        }
    }

    @Nested
    @DisplayName("Book operations")
    class BookOperationTests {

        @Test
        @DisplayName("getBookById returns the book when it exists")
        void getBookById_found() {
            Book b = store.getBookById("BK-001");
            assertAll(
                    () -> assertNotNull(b),
                    () -> assertEquals("Clean Code", b.getTitle()));
        }

        @Test
        @DisplayName("getBookById returns null when id is unknown")
        void getBookById_missing() {
            assertNull(store.getBookById("NO-ID"));
        }

        @Test
        @DisplayName("getBookByTitle is case-insensitive on title")
        void getBookByTitle_caseInsensitive() {
            Book a = store.getBookByTitle("CLEAN code");
            Book b = store.getBookByTitle("clean code");
            assertAll(
                    () -> assertNotNull(a),
                    () -> assertNotNull(b),
                    () -> assertEquals("BK-001", a.getBookId()),
                    () -> assertEquals(a.getBookId(), b.getBookId()));
        }

        @Test
        @DisplayName("addBook then lookup by id")
        void addBook_persists() {
            store.addBook(new Book("BK-T-1", "Temp", "T. Author", "Test", 1));
            assertAll(
                    () -> assertEquals(SEEDED_BOOK_COUNT + 1, store.getAllBooks().size()),
                    () -> assertNotNull(store.getBookById("BK-T-1")));
        }

        @Test
        @DisplayName("removeBook returns false when id does not exist")
        void removeBook_missing() {
            assertFalse(store.removeBook("id-not-there"));
        }

        @Test
        @DisplayName("removeBook returns true and removes the book by id")
        void removeBook_success() {
            assertNotNull(store.getBookById("BK-008"));
            assertTrue(store.removeBook("BK-008"));
            assertAll(
                    () -> assertNull(store.getBookById("BK-008")),
                    () -> assertEquals(SEEDED_BOOK_COUNT - 1, store.getAllBooks().size()));
        }

        @Test
        @DisplayName("searchBooks matches substrings in title or author (case-insensitive)")
        void searchBooks_byKeyword() {
            List<Book> martin = store.searchBooks("martin");
            assertAll(
                    () -> assertFalse(martin.isEmpty()),
                    () -> assertTrue(
                            martin.stream().anyMatch(b -> b.getTitle().toLowerCase().contains("code"))));
        }

        @Test
        @DisplayName("getBooksByGenre matches genre case-insensitively")
        void getBooksByGenre() {
            List<Book> fiction = store.getBooksByGenre("fiction");
            assertEquals(2, fiction.size());
        }

        @Test
        @DisplayName("getAllBooks returns the live catalog (same size after addBook)")
        void getAllBooks_reflectsAdditions() {
            int n = store.getAllBooks().size();
            store.addBook(new Book("BK-Z", "Z", "Z", "Z", 0));
            assertEquals(n + 1, store.getAllBooks().size());
        }
    }

    @Nested
    @DisplayName("User operations")
    class UserOperationTests {

        @Test
        @DisplayName("getUserById returns the user when it exists")
        void getUserById_found() {
            User u = store.getUserById("LIB-001");
            assertAll(
                    () -> assertNotNull(u),
                    () -> assertTrue(u instanceof Librarian));
        }

        @Test
        @DisplayName("getUserById returns null when id is unknown")
        void getUserById_missing() {
            assertNull(store.getUserById("X"));
        }

        @Test
        @DisplayName("getUserByEmail is case-insensitive on email")
        void getUserByEmail_caseInsensitive() {
            User u1 = store.getUserByEmail("ALICE@LIBCONNECT.COM");
            User u2 = store.getUserByEmail("alice@libconnect.com");
            assertAll(
                    () -> assertNotNull(u1),
                    () -> assertNotNull(u2),
                    () -> assertEquals("MEM-001", u1.getUserId()),
                    () -> assertSame(u1, u2));
        }

        @Test
        @DisplayName("authenticate returns the matching user for valid seeded credentials")
        void authenticate_success() {
            User u = store.authenticate("admin@libconnect.com", "admin123");
            assertAll(
                    () -> assertNotNull(u),
                    () -> assertEquals("LIB-001", u.getUserId()));
        }

        @Test
        @DisplayName("authenticate returns null for wrong password")
        void authenticate_wrongPassword() {
            assertNull(store.authenticate("admin@libconnect.com", "wrong"));
        }

        @Test
        @DisplayName("getAllMembers returns only Member instances from seed")
        void getAllMembers_seededLibrarianExcluded() {
            List<Member> members = store.getAllMembers();
            assertAll(
                    () -> assertEquals(SEEDED_MEMBER_COUNT, members.size()),
                    () -> assertTrue(
                            members.stream()
                                    .map(Member::getUserId)
                                    .allMatch(id -> id.startsWith("MEM-"))));
        }

        @Test
        @DisplayName("addUser and removeUser update the registry")
        void addUser_removeUser() {
            Member m = new Member("MEM-999", "Zed", "zed@t.com", "p");
            store.addUser(m);
            assertAll(
                    () -> assertEquals(SEEDED_USER_COUNT + 1, store.getAllUsers().size()),
                    () -> assertEquals(SEEDED_MEMBER_COUNT + 1, store.getAllMembers().size()),
                    () -> assertNotNull(store.getUserById("MEM-999")));

            assertTrue(store.removeUser("MEM-999"));
            assertAll(
                    () -> assertNull(store.getUserById("MEM-999")),
                    () -> assertEquals(SEEDED_USER_COUNT, store.getAllUsers().size()),
                    () -> assertFalse(store.removeUser("MEM-999")));
        }
    }

    @Nested
    @DisplayName("printAllData")
    class PrintAllDataTests {

        @Test
        @DisplayName("writes user and book sections to System.out")
        void printAllData_outputsHeaderLines() {
            PrintStream orig = System.out;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
                store.printAllData();
            } finally {
                System.setOut(orig);
            }
            String out = buf.toString(StandardCharsets.UTF_8);
            assertAll(
                    () -> assertTrue(out.contains("USERS (")),
                    () -> assertTrue(out.contains("BOOKS (")),
                    () -> assertTrue(out.contains("══")));
        }
    }
}
