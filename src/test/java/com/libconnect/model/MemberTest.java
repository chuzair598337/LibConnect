package com.libconnect.model;

import com.libconnect.util.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Member")
class MemberTest {

    private static final String MEM_ID = "MEM-TEST-1";
    private static final String NAME = "Erin";
    private static final String EMAIL = "erin@libconnect.com";
    private static final String PASSWORD = "pw123";

    /** Seeded book title in DataStore (see seedData) */
    private static final String SEEDED_TITLE = "Clean Code";

    private Member member;

    @BeforeEach
    void resetStore() {
        DataStore.resetForTests();
        member = new Member(MEM_ID, NAME, EMAIL, PASSWORD);
    }

    @Nested
    @DisplayName("User inheritance")
    class UserFieldTests {

        @Test
        @DisplayName("constructor initializes user fields and empty borrowed list")
        void newMember_hasEmptyList() {
            assertAll(
                    () -> assertEquals(MEM_ID, member.getUserId()),
                    () -> assertEquals(EMAIL, member.getEmail()),
                    () -> assertNotNull(member.getBorrowedBooks()),
                    () -> assertTrue(member.getBorrowedBooks().isEmpty()));
        }
    }

    @Nested
    @DisplayName("borrowBook")
    class BorrowTests {

        @Test
        @DisplayName("borrows a seeded book: decrements store copies and records title")
        void borrowSeededTitle() {
            Book before = DataStore.getInstance().getBookByTitle(SEEDED_TITLE);
            assertNotNull(before);
            int copiesStart = before.getCopiesAvailable();
            assertTrue(copiesStart > 0);

            member.borrowBook(SEEDED_TITLE);

            assertAll(
                    () -> assertEquals(1, member.getBorrowedBooks().size()),
                    () -> assertTrue(member.getBorrowedBooks().contains(SEEDED_TITLE)),
                    () -> assertEquals(
                            copiesStart - 1,
                            DataStore.getInstance().getBookByTitle(SEEDED_TITLE)
                                    .getCopiesAvailable()));
        }

        @Test
        @DisplayName("no-op when book title is not in catalog")
        void bookNotFound() {
            int booksInStore = DataStore.getInstance().getAllBooks().size();
            member.borrowBook("Nonexistent Title 999");
            assertAll(
                    () -> assertTrue(member.getBorrowedBooks().isEmpty()),
                    () -> assertEquals(
                            booksInStore, DataStore.getInstance().getAllBooks().size()));
        }
    }

    @Nested
    @DisplayName("returnBook")
    class ReturnTests {

        @Test
        @DisplayName("returns a borrowed book: increments copies and removes from list")
        void returnAfterBorrow() {
            member.borrowBook(SEEDED_TITLE);
            Book b = DataStore.getInstance().getBookByTitle(SEEDED_TITLE);
            int afterBorrow = b.getCopiesAvailable();

            member.returnBook(SEEDED_TITLE);

            assertAll(
                    () -> assertTrue(member.getBorrowedBooks().isEmpty()),
                    () -> assertEquals(
                            afterBorrow + 1,
                            DataStore.getInstance().getBookByTitle(SEEDED_TITLE)
                                    .getCopiesAvailable()));
        }

        @Test
        @DisplayName("no-op if title was not borrowed by this member")
        void notBorrowed() {
            member.returnBook("Missing Title");
            assertTrue(member.getBorrowedBooks().isEmpty());
        }
    }

    @Nested
    @DisplayName("viewBookDetails")
    class ViewDetailsTests {

        @Test
        @DisplayName("resolves a seeded title and does not throw")
        void existingBook() {
            member.viewBookDetails(SEEDED_TITLE);
            assertNotNull(DataStore.getInstance().getBookByTitle(SEEDED_TITLE));
        }
    }
}
