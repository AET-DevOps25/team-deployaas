package com.aet.studyassistant.auth_service.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final String TEST_NAME = "Test User";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD_HASH = "hashed_password";
    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_COURSE_ID = "course123";

    @Test
    void defaultConstructorCreatesValidUser() {
        // Arrange & Act
        User user = new User();

        // Assert
        assertNull(user.getUuid());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPasswordHash());
    }

    @Test
    void parameterizedConstructorCreatesCorrectUser() {
        // Arrange & Act
        User user = new User(TEST_USER_ID, TEST_NAME, TEST_EMAIL, TEST_PASSWORD_HASH);

        // Assert
        assertEquals(TEST_USER_ID, user.getUuid());
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_PASSWORD_HASH, user.getPasswordHash());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        User user = new User();

        // Act
        user.setUuid(TEST_USER_ID);
        user.setName(TEST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setPasswordHash(TEST_PASSWORD_HASH);

        // Assert
        assertEquals(TEST_USER_ID, user.getUuid());
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_PASSWORD_HASH, user.getPasswordHash());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        User user = new User(TEST_USER_ID, TEST_NAME, TEST_EMAIL, TEST_PASSWORD_HASH);

        // Act & Assert
        assertEquals(TEST_USER_ID, user.getUuid());
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_PASSWORD_HASH, user.getPasswordHash());
    }

    @Test
    void userHandlesNullName() {
        // Arrange & Act
        User user = new User(TEST_USER_ID, null, TEST_EMAIL, TEST_PASSWORD_HASH);

        // Assert
        assertEquals(TEST_USER_ID, user.getUuid());
        assertNull(user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_PASSWORD_HASH, user.getPasswordHash());
    }

    @Test
    void userHandlesNullEmail() {
        // Arrange & Act
        User user = new User(TEST_USER_ID, TEST_NAME, null, TEST_PASSWORD_HASH);

        // Assert
        assertEquals(TEST_USER_ID, user.getUuid());
        assertEquals(TEST_NAME, user.getName());
        assertNull(user.getEmail());
        assertEquals(TEST_PASSWORD_HASH, user.getPasswordHash());
    }

    @Test
    void userHandlesNullPasswordHash() {
        // Arrange & Act
        User user = new User(TEST_USER_ID, TEST_NAME, TEST_EMAIL, null);

        // Assert
        assertEquals(TEST_USER_ID, user.getUuid());
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertNull(user.getPasswordHash());
    }

    @Test
    void userHandlesNullUuid() {
        // Arrange & Act
        User user = new User(null, TEST_NAME, TEST_EMAIL, TEST_PASSWORD_HASH);

        // Assert
        assertNull(user.getUuid());
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_PASSWORD_HASH, user.getPasswordHash());
    }

    @Test
    void userHandlesEmptyName() {
        // Arrange & Act
        User user = new User(TEST_USER_ID, "", TEST_EMAIL, TEST_PASSWORD_HASH);

        // Assert
        assertEquals(TEST_USER_ID, user.getUuid());
        assertEquals("", user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_PASSWORD_HASH, user.getPasswordHash());
    }

    @Test
    void userHandlesEmptyEmail() {
        // Arrange & Act
        User user = new User(TEST_USER_ID, TEST_NAME, "", TEST_PASSWORD_HASH);

        // Assert
        assertEquals(TEST_USER_ID, user.getUuid());
        assertEquals(TEST_NAME, user.getName());
        assertEquals("", user.getEmail());
        assertEquals(TEST_PASSWORD_HASH, user.getPasswordHash());
    }

    @Test
    void userHandlesEmptyPasswordHash() {
        // Arrange & Act
        User user = new User(TEST_USER_ID, TEST_NAME, TEST_EMAIL, "");

        // Assert
        assertEquals(TEST_USER_ID, user.getUuid());
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals("", user.getPasswordHash());
    }

    @Test
    void userHandlesAllNullParameters() {
        // Arrange & Act
        User user = new User(null, null, null, null);

        // Assert
        assertNull(user.getUuid());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPasswordHash());
    }

    @Test
    void enrollMethodExecutesWithoutException() {
        // Arrange
        User user = new User(TEST_USER_ID, TEST_NAME, TEST_EMAIL, TEST_PASSWORD_HASH);

        // Act & Assert - Should not throw any exception
        assertDoesNotThrow(() -> user.enroll(TEST_COURSE_ID));
    }

    @Test
    void enrollMethodHandlesNullCourseId() {
        // Arrange
        User user = new User(TEST_USER_ID, TEST_NAME, TEST_EMAIL, TEST_PASSWORD_HASH);

        // Act & Assert - Should not throw any exception
        assertDoesNotThrow(() -> user.enroll(null));
    }

    @Test
    void enrollMethodHandlesEmptyCourseId() {
        // Arrange
        User user = new User(TEST_USER_ID, TEST_NAME, TEST_EMAIL, TEST_PASSWORD_HASH);

        // Act & Assert - Should not throw any exception
        assertDoesNotThrow(() -> user.enroll(""));
    }

    @Test
    void userWithValidEmailFormat() {
        // Arrange
        String validEmail = "user@domain.com";

        // Act
        User user = new User(TEST_USER_ID, TEST_NAME, validEmail, TEST_PASSWORD_HASH);

        // Assert
        assertEquals(validEmail, user.getEmail());
    }

    @Test
    void userWithLongName() {
        // Arrange
        String longName = "This is a very long name that exceeds normal length expectations for testing purposes";

        // Act
        User user = new User(TEST_USER_ID, longName, TEST_EMAIL, TEST_PASSWORD_HASH);

        // Assert
        assertEquals(longName, user.getName());
    }

    @Test
    void userWithSpecialCharactersInName() {
        // Arrange
        String specialName = "José María O'Connor-Smith";

        // Act
        User user = new User(TEST_USER_ID, specialName, TEST_EMAIL, TEST_PASSWORD_HASH);

        // Assert
        assertEquals(specialName, user.getName());
    }

    @Test
    void userWithDifferentCaseEmail() {
        // Arrange
        String mixedCaseEmail = "Test.User@Example.COM";

        // Act
        User user = new User(TEST_USER_ID, TEST_NAME, mixedCaseEmail, TEST_PASSWORD_HASH);

        // Assert
        assertEquals(mixedCaseEmail, user.getEmail());
    }
}
