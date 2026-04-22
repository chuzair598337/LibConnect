package com.libconnect.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link User} behaviour using a concrete test double.
 * {@link User} is abstract; {@link TestUser} supplies a minimal
 * {@link #viewDashboard()} only.
 */
@DisplayName("User (abstract) — core behaviour")
class UserTest {

    private static final String ID = "USR-TEST-01";
    private static final String NAME = "Test User";
    private static final String EMAIL = "test@libconnect.com";
    private static final String PASSWORD = "secret123";

    private TestUser user;

    @BeforeEach
    void setUp() {
        user = new TestUser(ID, NAME, EMAIL, PASSWORD);
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("stores all field values on construction")
        void constructor_initializesAllFields() {
            assertAll(
                    () -> assertEquals(ID, user.getUserId()),
                    () -> assertEquals(NAME, user.getUsername()),
                    () -> assertEquals(EMAIL, user.getEmail()),
                    () -> assertEquals(PASSWORD, user.getPassword()));
        }
    }

    @Nested
    @DisplayName("Getters and setters")
    class GetterSetterTests {

        @Test
        @DisplayName("setters update and getters reflect the new values")
        void settersAndGetters_roundTrip() {
            user.setUserId("USR-2");
            user.setUsername("Another");
            user.setEmail("other@ex.com");
            user.setPassword("p2");

            assertAll(
                    () -> assertEquals("USR-2", user.getUserId()),
                    () -> assertEquals("Another", user.getUsername()),
                    () -> assertEquals("other@ex.com", user.getEmail()),
                    () -> assertEquals("p2", user.getPassword()));
        }
    }

    @Nested
    @DisplayName("login(String, String)")
    class LoginTests {

        @Test
        @DisplayName("returns true when email and password both match (case-sensitive)")
        void login_success_whenCredentialsMatch() {
            assertTrue(user.login(EMAIL, PASSWORD));
        }

        @Test
        @DisplayName("returns false when email is wrong")
        void login_failsWhenEmailDoesNotMatch() {
            assertFalse(user.login("wrong@libconnect.com", PASSWORD));
        }

        @Test
        @DisplayName("returns false when password is wrong")
        void login_failsWhenPasswordDoesNotMatch() {
            assertFalse(user.login(EMAIL, "wrongpass"));
        }

        @Test
        @DisplayName("email comparison is case-sensitive")
        void login_failsWhenEmailCaseDiffers() {
            assertFalse(user.login(EMAIL.toUpperCase(), PASSWORD));
        }

        @Test
        @DisplayName("returns false when email argument is null")
        void login_failsWhenEmailArgIsNull() {
            assertFalse(user.login(null, PASSWORD));
        }

        @Test
        @DisplayName("returns false when password argument is null")
        void login_failsWhenPasswordArgIsNull() {
            assertFalse(user.login(EMAIL, null));
        }

        @Test
        @DisplayName("returns false when stored email or password is null")
        void login_failsWhenUserCredentialsUnset() {
            user.setEmail(null);
            user.setPassword(PASSWORD);
            assertFalse(user.login("anything", PASSWORD));

            user.setEmail(EMAIL);
            user.setPassword(null);
            assertFalse(user.login(EMAIL, "x"));
        }
    }

    @Nested
    @DisplayName("toString()")
    class ToStringTests {

        @Test
        @DisplayName("includes userId, username, and email; never exposes the password")
        void toString_containsExpectedFieldsAndOmitsPassword() {
            String s = user.toString();
            assertAll(
                    () -> assertNotNull(s),
                    () -> assertTrue(s.contains(ID)),
                    () -> assertTrue(s.contains(NAME)),
                    () -> assertTrue(s.contains(EMAIL)),
                    () -> assertFalse(s.contains(PASSWORD)));
        }
    }

    @Nested
    @DisplayName("equals / hashCode")
    class EqualityTests {

        @Test
        @DisplayName("User does not implement equals; distinct instances are not equal by value")
        void noCustomEquals_usesObjectIdentity() {
            TestUser other = new TestUser(ID, NAME, EMAIL, PASSWORD);
            assertNotNull(other);
            assertNotSame(user, other);
            assertNotEquals(user, other);
        }
    }

    /**
     * Minimal concrete {@link User} for tests; GUI is not involved.
     */
    private static final class TestUser extends User {

        TestUser(String userId, String username, String email, String password) {
            super(userId, username, email, password);
        }

        @Override
        public void viewDashboard() {
            // no UI in unit tests
        }
    }
}
