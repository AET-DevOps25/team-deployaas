package com.aet.studyassistant.auth_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthRequestTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_NAME = "Test User";

    @Test
    void defaultConstructorCreatesEmptyObject() {
        // Arrange & Act
        AuthRequest authRequest = new AuthRequest();

        // Assert
        assertNull(authRequest.getEmail());
        assertNull(authRequest.getPassword());
        assertNull(authRequest.getName());
    }

    @Test
    void parameterizedConstructorCreatesCorrectObject() {
        // Arrange & Act
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);

        // Assert
        assertEquals(TEST_EMAIL, authRequest.getEmail());
        assertEquals(TEST_PASSWORD, authRequest.getPassword());
        assertEquals(TEST_NAME, authRequest.getName());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();

        // Act
        authRequest.setEmail(TEST_EMAIL);
        authRequest.setPassword(TEST_PASSWORD);
        authRequest.setName(TEST_NAME);

        // Assert
        assertEquals(TEST_EMAIL, authRequest.getEmail());
        assertEquals(TEST_PASSWORD, authRequest.getPassword());
        assertEquals(TEST_NAME, authRequest.getName());
    }

    @Test
    void gettersReturnCorrectValues() {
        // Arrange
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);

        // Act & Assert
        assertEquals(TEST_EMAIL, authRequest.getEmail());
        assertEquals(TEST_PASSWORD, authRequest.getPassword());
        assertEquals(TEST_NAME, authRequest.getName());
    }

    @Test
    void authRequestHandlesNullEmail() {
        // Arrange & Act
        AuthRequest authRequest = new AuthRequest(null, TEST_PASSWORD, TEST_NAME);

        // Assert
        assertNull(authRequest.getEmail());
        assertEquals(TEST_PASSWORD, authRequest.getPassword());
        assertEquals(TEST_NAME, authRequest.getName());
    }

    @Test
    void authRequestHandlesNullPassword() {
        // Arrange & Act
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, null, TEST_NAME);

        // Assert
        assertEquals(TEST_EMAIL, authRequest.getEmail());
        assertNull(authRequest.getPassword());
        assertEquals(TEST_NAME, authRequest.getName());
    }

    @Test
    void authRequestHandlesNullName() {
        // Arrange & Act
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, null);

        // Assert
        assertEquals(TEST_EMAIL, authRequest.getEmail());
        assertEquals(TEST_PASSWORD, authRequest.getPassword());
        assertNull(authRequest.getName());
    }

    @Test
    void authRequestHandlesEmptyStrings() {
        // Arrange & Act
        AuthRequest authRequest = new AuthRequest("", "", "");

        // Assert
        assertEquals("", authRequest.getEmail());
        assertEquals("", authRequest.getPassword());
        assertEquals("", authRequest.getName());
    }

    @Test
    void authRequestHandlesAllNullValues() {
        // Arrange & Act
        AuthRequest authRequest = new AuthRequest(null, null, null);

        // Assert
        assertNull(authRequest.getEmail());
        assertNull(authRequest.getPassword());
        assertNull(authRequest.getName());
    }

    @Test
    void authRequestWithValidEmailFormat() {
        // Arrange
        String validEmail = "user@domain.com";

        // Act
        AuthRequest authRequest = new AuthRequest(validEmail, TEST_PASSWORD, TEST_NAME);

        // Assert
        assertEquals(validEmail, authRequest.getEmail());
    }

    @Test
    void authRequestWithLongPassword() {
        // Arrange
        String longPassword = "ThisIsAVeryLongPasswordWithSpecialCharacters!@#$%^&*()123456789";

        // Act
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, longPassword, TEST_NAME);

        // Assert
        assertEquals(longPassword, authRequest.getPassword());
    }

    @Test
    void authRequestWithSpecialCharactersInName() {
        // Arrange
        String specialName = "José María O'Connor-Smith";

        // Act
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, specialName);

        // Assert
        assertEquals(specialName, authRequest.getName());
    }

    @Test
    void authRequestForLoginWithoutName() {
        // Arrange & Act - For login, name is typically not required
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, null);

        // Assert
        assertEquals(TEST_EMAIL, authRequest.getEmail());
        assertEquals(TEST_PASSWORD, authRequest.getPassword());
        assertNull(authRequest.getName());
    }

    @Test
    void authRequestForRegistrationWithAllFields() {
        // Arrange & Act - For registration, all fields are typically required
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);

        // Assert
        assertEquals(TEST_EMAIL, authRequest.getEmail());
        assertEquals(TEST_PASSWORD, authRequest.getPassword());
        assertEquals(TEST_NAME, authRequest.getName());
    }

    @Test
    void authRequestWithWhitespaceValues() {
        // Arrange
        String whitespaceEmail = "   test@example.com   ";
        String whitespacePassword = "   password123   ";
        String whitespaceName = "   Test User   ";

        // Act
        AuthRequest authRequest = new AuthRequest(whitespaceEmail, whitespacePassword, whitespaceName);

        // Assert
        assertEquals(whitespaceEmail, authRequest.getEmail());
        assertEquals(whitespacePassword, authRequest.getPassword());
        assertEquals(whitespaceName, authRequest.getName());
    }
}
