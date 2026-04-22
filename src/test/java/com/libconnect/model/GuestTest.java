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

@DisplayName("Guest")
class GuestTest {

    private static final String SEEDED_TITLE = "1984";

    private Guest guest;

    @BeforeEach
    void resetStore() {
        DataStore.resetForTests();
        guest = new Guest();
    }

    @Nested
    @DisplayName("default identity")
    class IdentityTests {

        @Test
        @DisplayName("default constructor sets fixed guest profile")
        void defaultValues() {
            assertAll(
                    () -> assertEquals("GUEST-000", guest.getUserId()),
                    () -> assertEquals("Guest", guest.getUsername()),
                    () -> assertEquals("guest@libconnect.com", guest.getEmail()),
                    () -> assertEquals("", guest.getPassword()));
        }

        @Test
        @DisplayName("login accepts guest email and empty password")
        void loginWithGuestCredentials() {
            assertTrue(guest.login("guest@libconnect.com", ""));
        }
    }

    @Nested
    @DisplayName("viewBookDetails")
    class ViewDetailsTests {

        @Test
        @DisplayName("loads a seeded book by title and does not throw")
        void existingBook() {
            assertNotNull(DataStore.getInstance().getBookByTitle(SEEDED_TITLE));
            guest.viewBookDetails(SEEDED_TITLE);
        }
    }
}
